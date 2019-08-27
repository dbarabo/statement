package ru.barabo.statement.ui;


import ru.barabo.statement.data.IDataExtractExportXLS;

import javax.swing.table.AbstractTableModel;

public class TableFNSModel extends AbstractTableModel {

	private IDataExtractExportXLS data;
	
	public TableFNSModel(IDataExtractExportXLS data) {
		this.data = data;
	}
	
	@Override
	public int getColumnCount() {
		return data.getFNSColumnCount();
	}

	@Override
	public int getRowCount() {
		return data.getFNSRowCount();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data.getFNSValue(rowIndex, columnIndex);
	}
	
	@Override
    public String getColumnName( int column ) {
	  	return data.getFNSColumnName(column);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
}
