package ru.barabo.statement.ui;


import ru.barabo.statement.data.IDataExtractExportXLS;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;

public class TableFNS extends JTable {
	
	private IDataExtractExportXLS data;
	private JTextField text;
	private JTextField address;
	
	public TableFNS(IDataExtractExportXLS data, JTextField text, JTextField address) {
		super();
		
		this.data = data;
		this.text = text;
		this.address = address;
		
        setModel( new TableFNSModel(data) );
 	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		super.valueChanged(e);
	    final int firstIndex = e.getFirstIndex();
	    final int lastIndex  = e.getLastIndex();
	    
	    if (firstIndex == -1 && lastIndex == -1) { 
		      repaint();
		} else {	    
			data.setFNSSelectedRow(this.getSelectedRow() == firstIndex ? firstIndex : lastIndex);
			text.setText(data.getFNSSelected(0));
			address.setText(data.getFNSSelected(1));
		}
	}
}