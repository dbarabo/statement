package ru.barabo.total.db;

public interface FieldItem {

	/**
	 * название поля
	 */
	String getLabel();

	/**
	 * присутствует ли филд в сетке
	 */
	boolean isExistsGrid();

	/**
	 * установка значения филда
	 */
	void setValueField(String value);

	void setValueFieldObject(Object value);
	
	String getColumn();
	
	/**
	 * возврат списка
	 */
	String[] getListField();

	void setListField(String[] valueList);

	/**
	 * возврат значения фильтра для целых возвращает в виде min;max
	 */
	String getValueField();
	
	Object getVal();
	
	/**
	 * 
	 */
	int getWidth();
	
	
	int getIndex();
	
	boolean isReadOnly();
	
	Type getClazz();

}
