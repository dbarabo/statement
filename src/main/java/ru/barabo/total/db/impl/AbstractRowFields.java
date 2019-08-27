package ru.barabo.total.db.impl;

import org.apache.log4j.Logger;
import ru.barabo.total.db.FieldItem;

import java.util.List;

public abstract class AbstractRowFields {

	final static transient private Logger logger = Logger.getLogger(AbstractRowFields.class.getName());
	
	private List<FieldItem> fields;
	
	abstract protected List<FieldItem> createFields();

	static public <T extends AbstractRowFields> T create(Class<T> clazz) {

		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error("create", e);

			throw new NumberFormatException(e.getMessage());
		}
	}

	static public <T extends AbstractRowFields> T create(Object[] row, Class<T> rowClazz) {
		T rowField = create(rowClazz);

        //logger.error("rowField=" + rowField.fieldItems());

		for (int index = 0; index < Math.min(rowField.fieldItems().size(), row.length); index++) {
			rowField.fieldItems().get(index).setValueFieldObject(row[index]);
		}

		return rowField;
	}

	public AbstractRowFields() {
		fields = createFields();
	}
	
	public List<FieldItem> fieldItems() {
		return fields;
	}

	public FieldItem getFieldByLabel(String label) {

		for (FieldItem field : fieldItems()) {
			if (label.equals(field.getLabel())) {
				return field;
			}
		}

		return null;
	}

	public String getRowString() {
		StringBuilder result = new StringBuilder();
		
		for(FieldItem field : fields) {
			if(field.isExistsGrid()) {
				result.append(field.getValueField() == null ? "" : field.getValueField()).append("\t");
			}
		}
		
		return result.toString();
	}

    public Object[] getFields() {
		Object[] row = new Object[fields.size()];
		
		for(int index = 0; index < fields.size(); index++) {
			row[index] = fields.get(index).getVal();
		}
		return row;
	}
	
	private long getFieldDBCount() {
		
		return fields.stream().filter(field -> field.getColumn() != null).count();
		
	}
	
	public Object[] getFieldsDB() {
		
		Object[] row = new Object[(int)getFieldDBCount()];
		
		int index = 0;
		for(FieldItem field : fields) {
			if(field.getColumn() != null) {
				row[index] = field.getVal();
				index++;
			}
		}
		return row;
	}

    public Number getId() {
		return (Number)fields.get(0).getVal();
	}
	
	public void setId(Number id) {
		fields.get(0).setValueFieldObject(id);
	}
	
	public String getName() {

		return (String)fields.get(1).getVal();
	}

	public void setName(String name) {
		fields.get(1).setValueFieldObject(name);
	}
}
