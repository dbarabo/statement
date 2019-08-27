package ru.barabo.total.xls.loader.ui;

import ru.barabo.total.xls.loader.data.IDataImportExcel;

import javax.swing.table.AbstractTableModel;

public class TableImpExcelModel extends AbstractTableModel {
	
	private IDataImportExcel data;
	
	public TableImpExcelModel(IDataImportExcel data) {
		this.data = data;
	}
	
	@Override
	public int getColumnCount() {
		return data.getVarColumnCount();
	}

	@Override
	public int getRowCount() {
		return data.getVarCount();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data.getVarValue(rowIndex, columnIndex);
	}
	
	@Override
    public String getColumnName( int column ) {
	  	return data.getVarColumnName(column);
	}
}
