package ru.barabo.total.xls.loader.data;

import ru.barabo.total.xls.loader.jexel.ExcelFileType;

import java.io.File;

public interface IDataImportExcel {
	
	/**
	 * @return ч-ло столбцов
	 */
	int getVarColumnCount(); 
	
	/**
	 * @return ч-ло переменных в списке за мес для формы
	 */
	int getVarCount();
	
	/**
	 * @param varIndex
	 * @param columnIndex
	 * @return название-описание-значение переменной 
	 */
	Object getVarValue(int varIndex, int columnIndex);
	
	/**
	 * @param columnIndex
	 * @return название столбца
	 */
	String getVarColumnName(int columnIndex);
	
	/**
	 * @param excelFileType
	 * смена типа формы ЦБ
	 */
	void changeTypeFileExcel(ExcelFileType excelFileType);
	
	ExcelFileType getTypeFileExcel();
	
	
	/**
	 * производит импорт
	 * @return true - если выполнился
	 */
	boolean importExcel();
	
	/**
	 * @param fileName
	 * Устанавливает имя импортируемого файла
	 */
	void setExcelFileName(File fileName);
	
	/**
	 * Удаляет все показатели с типом excelFileType
	 * @param excelFileType
	 */
	boolean clearAllRates();
	
	void changeMonth(int monthNumber);
	
	int getMonthIndex();
	
	String getExcelYear();
}
