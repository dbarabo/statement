package ru.barabo.total.xls.loader.ui;

import javax.swing.JTable;

import ru.barabo.total.xls.loader.data.IDataImportExcel;

public class TableImpExcel extends JTable {
	
	public TableImpExcel(IDataImportExcel data) {
		super();
        setModel( new TableImpExcelModel(data) );
	}
}
