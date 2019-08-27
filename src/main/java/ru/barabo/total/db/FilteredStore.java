package ru.barabo.total.db;

public interface FilteredStore<E> extends DBStore<E> {

	void setFilterValue(int columnIndex, String value);
	
	String getFilterValue(int columnIndex);

	String getAllFieldValue(int fieldIndex);

    Integer getCountUnfilteredData();
}
