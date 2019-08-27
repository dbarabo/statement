package ru.barabo.total.gui.any;

import org.apache.log4j.Logger;

import javax.swing.*;

public class DefFilterEditor extends DefaultFieldEditor {
	
	final static transient private Logger logger = Logger.getLogger(DefFilterEditor.class.getName());

	public DefFilterEditor() {
		super(new JTextField());

		((JTextField)getField()).addActionListener( this );
	}

	@Override
	protected Object getValueField(JComponent field) {
		return ((JTextField)field).getText();
	}

	@Override
	protected int isEditClickCount() {
		return 1;
	}

	@Override
	protected void setFieldValue(JComponent field, Object value) {
		if(value == null) {
			value = "";
		}

		((JTextField)field).setText( value.toString() );
	}

}
