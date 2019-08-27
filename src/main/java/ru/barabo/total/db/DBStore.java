package ru.barabo.total.db;

import java.util.List;

public interface DBStore <E> {
	
	/**
	 * @return список филдов этого стора
	 */
	List<FieldItem> getFields();
	
	/**
	 * Устанавливает вид 
	 */
	void setViewType(int type);

	/**
	 * 
	 * @return все данные с учетом фильтрации и депенденси
	 */
	List<E> getData();
	
	/**
	 * @return текущий курсор данных
	 */
	E getRow();
	
	/**
	 * устанавливает тек. курсор данных
	 */
	void setRow(E row);
	
	/**
	 * добавляет слушателя для UI
	 */
	void addListenerStore(ListenerStore<E> listenerStore);

	/**
	 * удаляет текущий курсор
	 */
	void removeRow();

	int getTypeSelect();

	void searchTo(List<E> filterData);

    void updateAllData();
}
