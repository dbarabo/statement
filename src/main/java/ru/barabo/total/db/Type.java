package ru.barabo.total.db;

/**
 * типы филдов из DB
 * @author debara
 *
 */
public enum Type {

	LONG(Integer.class, java.sql.Types.INTEGER),
	STRING(String.class, java.sql.Types.VARCHAR),
	DECIMAL(Double.class, java.sql.Types.DECIMAL),
	DATE(java.sql.Date.class, java.sql.Types.TIMESTAMP);

	private Class clazz;

	private int sqlType;

	Type(Class clazz, int sqlType) {
		this.clazz = clazz;
		this.sqlType = sqlType;
	}

	public Class getClazz() {
		return clazz;
	}

	public int getSqlType() {
		return sqlType;
	}
}
