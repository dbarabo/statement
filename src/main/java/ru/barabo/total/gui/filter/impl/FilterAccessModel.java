package ru.barabo.total.gui.filter.impl;

import ru.barabo.total.db.DBStore;
import ru.barabo.total.db.FilteredStore;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

public class FilterAccessModel<P, C> extends AbstractTableModel {

	//final static transient private Logger logger = Logger.getLogger(FilterAccessModel.class.getName());

	private JTable accessTable;
	private JTable searchTable;

	private FilteredStore<C> srcFilterStore;
	private FilteredStore<P> toAccessStore;
	private int srcFilterFieldAssoc;

	private DBStore<C> searchAddStore;


	FilterAccessModel(FilteredStore<C> srcFilterStore, // allContent for all packetStore
        FilteredStore<P> toAccessStore, // packetStore
        int srcFilterFieldAssoc,
        JTable accessTable, //packet table
        JTable searchTable, // content table
        DBStore<C> searchAddStore) {

		this.accessTable = accessTable;
		this.searchTable = searchTable;
		this.srcFilterStore = srcFilterStore;
		this.toAccessStore = toAccessStore;
		this.srcFilterFieldAssoc = srcFilterFieldAssoc;

		this.searchAddStore = searchAddStore;
	}

	@Override
	public int getColumnCount() {
		return searchTable.getColumnCount();
	}

	@Override
	public int getRowCount() {
		return 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		return srcFilterStore.getFilterValue(columnIndex);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

		setFilterValue((String) aValue, columnIndex);

	}

	void setFilterValue(String value, int columnIndex) {
		if ("".equals(value)) {
			value = null;
		}

		srcFilterStore.setFilterValue(columnIndex, value);

		String allValues = srcFilterStore.getAllFieldValue(srcFilterFieldAssoc);

		toAccessStore.setFilterValue(-1, allValues);

		//((AbstractTableModel) accessTable.getModel()).fireTableDataChanged();
		accessTable.repaint();

        searchAddStore.searchTo(srcFilterStore.getData());
	}
}
