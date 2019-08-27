package ru.barabo.total.db;

import java.util.List;

/**
 * слушатель данных типа E
 * @author debara
 *
 * @param <E>
 */
public interface ListenerStore<E> {

	/**
	 * сообщает об изменении курсора E - тек. курсор
	 */
	void setCursor(E row);
	
	/**
	 * сообщает об изменениие всех данных
	 */
	void refreshData(List<E> allData, StateRefresh stateRefresh);
}
