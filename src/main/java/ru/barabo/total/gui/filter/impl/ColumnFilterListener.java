package ru.barabo.total.gui.filter.impl;

import ru.barabo.total.gui.filter.FilterTable;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;


public class ColumnFilterListener implements TableColumnModelListener {

	private FilterTable listenerTable;
	
	ColumnFilterListener(FilterTable listenerTable) {
		this.listenerTable = listenerTable;
	}
	
	@Override
	public void columnAdded(TableColumnModelEvent e) {
		
		
	}

	@Override
	public void columnRemoved(TableColumnModelEvent e) {
		
	}

	@Override
	public void columnMoved(TableColumnModelEvent e) {
		listenerTable.updateWidthColumn();
	}

	@Override
	public void columnMarginChanged(ChangeEvent e) {
		listenerTable.updateWidthColumn();
		
	}

	@Override
	public void columnSelectionChanged(ListSelectionEvent e) {
		listenerTable.updateWidthColumn();
	}

}
