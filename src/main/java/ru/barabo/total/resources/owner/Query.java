package ru.barabo.total.resources.owner;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;
import ru.barabo.total.db.impl.AbstractRowFields;

import java.util.stream.Collectors;

@Sources({ "${cfgtotal}/query.properties" })
public interface Query extends Config {

	@DefaultValue("{ call od.PTKB_PLASTIC_AUTO.setReportId(?, ?, ?, ?) }")
	String prepareBbr();

	@DefaultValue("{ call od.PTKB_PLASTIC_AUTO.getReportData(?, ?, ?, ?) }")
	String getBbrText();

	@DefaultValue("select classified.nextval from dual")
	String seqNextId();

	@DefaultValue("SELECT %s FROM %s")
	String selectAll(String columns, String table);

	@DefaultValue("SELECT %s FROM %s ORDER BY %s")
	String selectAllOrderBy(String columns, String table, String order);

	default String selectAll(AbstractRowFields row, String tableName, String order) {

		String listField = row.fieldItems().stream()
				.filter(f -> isDbColumn(f.getColumn(), f.getVal(), false))
				.map(f -> f.getColumn())
				.collect(Collectors.joining(", "));

		if ("".equals(listField)) {
			return null;
		}

		return (order != null) ? selectAllOrderBy(listField, tableName, order)
				: selectAll(listField, tableName);
	}

	default String selectAll(AbstractRowFields row, String tableName) {
		return selectAll(row, tableName, null);
	}

	default boolean isDbColumn(String columnName, Object value, boolean isInsert) {
		return columnName != null && Character.isLetter(columnName.charAt(0)) &&
				((!isInsert) || value != null);
	}

	@DefaultValue("INSERT INTO %s (%s) VALUES (%s)")
	String insertAll(String table, String columns, String questions);

	default String insert(AbstractRowFields row, String tableName) {

		String columns = row.fieldItems().stream()
				.filter(f -> isDbColumn(f.getColumn(), f.getVal(), true))
				.map(f -> f.getColumn())
				.collect(Collectors.joining(", "));

		String values = row.fieldItems().stream()
				.filter(f -> isDbColumn(f.getColumn(), f.getVal(), true))
				.map(f -> "?")
				.collect(Collectors.joining(", "));

		if (columns == null || "".equals(columns)) {
			return null;
		}

		return insertAll(tableName, columns, values);
	}

	@DefaultValue("delete from %s where id = ?")
	String deleteById(String tableName);

	@DefaultValue("UPDATE %s SET %s WHERE ID = ?")
	String updateById(String tableName, String columns);

	default String update(AbstractRowFields row, String tableName) {

		String columns = row.fieldItems().stream().skip(1)
				.filter(f -> isDbColumn(f.getColumn(), f.getVal(), false))
				.map(f -> f.getColumn())
				.collect(Collectors.joining(" = ?, ", "", " = ? "));

		return updateById(tableName, columns);
	}
}
