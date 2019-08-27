package ru.barabo.total.gui.table;

import org.apache.log4j.Logger;
import ru.barabo.total.db.DBStore;
import ru.barabo.total.db.FieldItem;
import ru.barabo.total.db.ListenerStore;
import ru.barabo.total.db.StateRefresh;
import ru.barabo.total.db.impl.AbstractFilterStore;
import ru.barabo.total.db.impl.AbstractRowFields;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.List;

public class TotalRowTable<E extends AbstractRowFields> extends JTable
implements ListenerStore<E> {
	
	final static transient private Logger logger = Logger.getLogger(TotalRowTable.class.getName());

	private DBStore<E> store;

	protected TableCellRenderer renderer;
	
	private boolean isMustFullRefresh;
	
	public TotalRowTable(DBStore<E> store) {
		super();
		
		this.store = store;
		
		setModel(getDefaultTableModel(store));
		
		renderer = new TotalRenderer();
		
		getSelectionModel().addListSelectionListener(this::selectListener);
		
		store.addListenerStore(this);
		
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	
		setColumnmSizes();
		
		this.setComponentPopupMenu(getPopupMenu() );
		
		isMustFullRefresh = false;
	}
	
	protected DBStore<E> getStore() {
		return store;
	}

	protected AbstractTableModel getDefaultTableModel(DBStore<E> store) {
		return new TableModelPlanExecData<>(store);
	}

	public void setMustFullRefresh() {
		this.isMustFullRefresh = true;
	}
	
	private void selectListener(ListSelectionEvent e) {
		if (e.getValueIsAdjusting ()) return;
		
        ListSelectionModel selModel = (ListSelectionModel)e.getSource();
        // Номер текущей строки таблицы
        if (selModel.isSelectionEmpty ())  return;
        	
        int index = selModel.getMinSelectionIndex ();
        	
        //logger.info(" e.getFirstIndex()=" +  index);
        	
        List<E> data = store.getData();
        	
        if(index < 0 || data == null || data.size() <= index) {
		  return;
		}

		//logger.debug(" data=" +  data);
		//logger.debug(" data.size()=" +  data.size());
			
		store.setRow(data.get(index));
	}
	
	private JPopupMenu getPopupMenu() {
		
		JPopupMenu popup = new JPopupMenu();
		
		JMenuItem copyRow = new JMenuItem("Копировать строку");
		
		copyRow.addActionListener(this::copyRow);
				
		popup.add(copyRow);
		
		
		JMenuItem copyTable = new JMenuItem("Копировать всю таблицу");
		
		copyTable.addActionListener(this::copyTable);
		
		popup.add(copyTable);
		
		return popup;	
	}
	
	private void copyTable(ActionEvent e) {
		
		//if(!(e.getSource() instanceof JTable)) return;
		
		List<E> data = store.getData();
		
		if(data == null) return;
		
		StringBuilder tableData = new StringBuilder();
		
		for (E row : data) {
			tableData.append(row.getRowString()).append("\n");
		}
			
		StringSelection selection = new StringSelection(tableData.toString());

		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
	}
	
	private void copyRow(ActionEvent e) {
		
		//if(!(e.getSource() instanceof JTable)) return;
		
		E row = store.getRow();
		
		if(row == null) return;
			
		StringSelection selection = new StringSelection(row.getRowString() );

		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
	}

	private void setColumnmSizes() {
		
		//getTableHeader().setResizingAllowed(false);
		//setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS/*AUTO_RESIZE_SUBSEQUENT_COLUMNS*/); 
		
		int visibleIndex = 0;
		for (FieldItem field : store.getFields()) {
			  if(field.isExistsGrid()) {
				  int width = field.getWidth();
				  
				  getColumnModel().getColumn(visibleIndex).setPreferredWidth(width);
				  //getColumnModel().getColumn(visibleIndex).setMaxWidth(width);
				  visibleIndex++;
			  }  
		}
	}

	@Override
	public void setCursor(E row) {
		
		if(store.getData() == null || store.getData().size() == 0) {
			((AbstractTableModel)this.getModel()).fireTableDataChanged();
			return;
		}
		
		int index = store.getData().indexOf(store.getRow());
		
		if(index < 0) {
			index = 0;
		}
		
		this.setRowSelectionInterval(index, index);
		this.scrollRectToVisible(this.getCellRect(index, 0, true));
	}


	@Override
	public void refreshData(List<E> allData, StateRefresh stateRefresh) {

//		logger.error("stateRefresh="+stateRefresh);
//		logger.error("isMustFullRefresh="+isMustFullRefresh);
//		logger.error("store="+ store);

		if(isMustFullRefresh || stateRefresh == StateRefresh.ALL) {
			isMustFullRefresh = false;
			((AbstractTableModel) this.getModel()).fireTableStructureChanged();
			setColumnmSizes();
			return;
		}
		
		if(store instanceof AbstractFilterStore) {
//			logger.error("repaint="+ store);
			this.repaint();
		} else {
//			logger.error("fireTableDataChanged="+ store);
			((AbstractTableModel)this.getModel()).fireTableDataChanged();
		}
	}
	
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		return renderer;
	}
}
