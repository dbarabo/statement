package ru.barabo.total.gui.filter.impl;

import ru.barabo.total.db.FilteredStore;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

public class FilterModelSimple extends AbstractTableModel {

	private FilteredStore store;
	private JTable mainTable;

	//final static transient private Logger logger = Logger.getLogger(FilterModelSimple.class.getName());
	
	FilterModelSimple(FilteredStore store, JTable mainTable) {
		this.store = store;
		this.mainTable = mainTable;
	}

	@Override
	public int getColumnCount() {
		return mainTable.getColumnCount();
	}

	@Override
	public int getRowCount() {
		return 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		return store.getFilterValue(columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	
		if("".equals(aValue)) aValue = null;

		store.setFilterValue(columnIndex, (String)aValue);
		//mainTable.repaint();
		((AbstractTableModel)mainTable.getModel()).fireTableDataChanged();

		// modelTable.requestFocus();
	}
}
