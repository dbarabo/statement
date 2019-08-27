package ru.barabo.total.gui.any;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

public abstract class DefaultFieldEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

	//final static transient private Logger logger = Logger.getLogger(DefaultFieldEditor.class.getName());

	private JComponent field;
	
	protected abstract void setFieldValue(JComponent field, Object value);
	
	protected abstract int isEditClickCount();
	
	protected abstract Object getValueField(JComponent field);
	
////////////////////////////////////////////////////////////////
DefaultFieldEditor(JComponent field)	{
		this.field = field;
	}
	
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		setFieldValue(field, value);
		return field;
	}
	
	public Object getCellEditorValue() {

		return getValueField(field);
	}
	
	/**
     * Stops editing and
     * returns true to indicate that editing has stopped.
     * This method calls <code>fireEditingStopped</code>.
     *
     */
	/*
	 * public boolean stopCellEditing() { fireEditingStopped(); return true; }
	 */
	

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		//super.stopCellEditing();
		 fireEditingStopped();
	}

	@Override
	public boolean isCellEditable(EventObject e) {
		
		return true;
	}

	public JComponent getField() {
		return field;
	}
}
