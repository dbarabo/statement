package ru.barabo.total.gui.table;

import ru.barabo.total.db.DBStore;
import ru.barabo.total.db.FieldItem;
import ru.barabo.total.db.impl.AbstractRowFields;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public class TableModelPlanExecData<E extends AbstractRowFields> extends AbstractTableModel {

	private DBStore<E> store;
	
	TableModelPlanExecData(DBStore<E> store) {
		this.store = store;
	}
	
	@Override
	public int getRowCount() {
		
		List<E> data = store.getData();
		
		return (data == null) ? 0 : data.size();
	}
	
	private int getViewColumnCount() {
		int count = 0;
		for (FieldItem item : store.getFields()) {
			if(item.isExistsGrid()) {
				count++;
			}
		}
		
		return count;
	}
	
	/**
	 * мапим на видимый столбец на все в списке
	 */
	private int mapViewColumnToAllColumn(int viewColumn) {
		int columnShow = 0;
		for (int index = 0; index < store.getFields().size(); index++) {
			
			final FieldItem item = store.getFields().get(index);
			
			if(item.isExistsGrid()) {
				if(columnShow == viewColumn) {
					return index;
				}
				columnShow++;
			}
		}
		
		return -1;
	}
	

	@Override
    public String getColumnName( int column ) {
		
		int colIndex = mapViewColumnToAllColumn(column);
		
		return store.getFields().get(colIndex).getLabel();
	}
	

	@Override
	public int getColumnCount() {
		
		return getViewColumnCount();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		List<E> data = store.getData();
		
		if(data == null || data.size() <= rowIndex) {
			return null;
		}
		
		int colIndex = mapViewColumnToAllColumn(columnIndex);
		
		E row = data.get(rowIndex);
		
		if(row == null || row.fieldItems().size() <= colIndex) {
			return null;
		}
		
		return row.fieldItems().get(colIndex).getValueField();
	}
	

}
