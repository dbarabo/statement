package ru.barabo.statement.data;

import ru.barabo.xls.Record;

import java.util.Date;
import java.util.List;

public interface IDataExtractExportXLS {

	/**
	 * запускает процедуру экспорта
	 * @return true если все хорошо
	 */
	List<String> startExport(String account, Date dateFrom, Date dateTo,
							 String path, String fnsName, String fnsAddress, String fnsRequest, boolean isTurn,
							 boolean isRur, boolean isOpened, boolean isShowRestEveryDay, Record clientId);
	
	/**
	 * @return ч-ло столбцов в таблице фНС
	 */
	int getFNSColumnCount();
	
	/**
	 * @return ч-ло строк в таблице фНС
	 */
	int getFNSRowCount();
	
	String getFNSColumnName(int columnIndex);
	
	Object getFNSValue(int rowIndex, int columnIndex);
	
	String getFNSSelected(int columnIndex);

	void setFNSSelectedRow(int row);

	String checkAccount(String account);
}
