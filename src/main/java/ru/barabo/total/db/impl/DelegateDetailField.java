package ru.barabo.total.db.impl;

import ru.barabo.total.db.DBStore;
import ru.barabo.total.db.DetailFieldItem;
import ru.barabo.total.db.FieldItem;
import ru.barabo.total.gui.detail.FactoryComponent;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.EventListener;
import java.util.List;

public class DelegateDetailField {
	
	// final transient private static Logger logger = Logger.getLogger(DelegateDetailField.class.getName());

	private String groupLabel;
	
	private int posX;
	
	private int posY;
	
	private int height;
	
	private JComponent component;
	
	private EventListener listener;

	private DBStore store;

	DelegateDetailField(String groupLabel, int posX, int posY, int height) {
		
		this.groupLabel = groupLabel;
		this.posX = posX;
		this.posY = posY;
		this.height = height;
	}
	
	public void setStore(DBStore store) {
		this.store = store;
	}

	DelegateDetailField(String groupLabel, int posX, int posY, int height,
			EventListener listener) {

		this(groupLabel, posX, posY, height);

		this.listener = listener;
	}

	JComponent getComponent() {
		return component;
	}

	String getGroupLabel() {
		return groupLabel;
	}
	
	int getPosX() {
		return posX;
	}
	
	int getPosY() {
		return posY;
	}
		
	int getHeight() {
		return height;
	}

	void setComponent(JComponent component) {
		this.component = component;

		addListener();
	}

	private void addListener() {
		if (component instanceof JComboBox &&
				listener == null) {
			listener = getDefaultComboBoxSelected();
		}

		if (listener == null) {
			return;
		}

		if (component instanceof JTextField) {
			component.addKeyListener((KeyListener) listener);
		} else if (component instanceof JComboBox) {
			((JComboBox) component).addActionListener((ActionListener) listener);
		} else if (component instanceof JCheckBox) {
			((JCheckBox) component).addActionListener((ActionListener) listener);
		}
	}

	private ActionListener getDefaultComboBoxSelected() {
		return e -> {

			if (store == null) {
				return;
			}

			JComboBox combo = (JComboBox) e.getSource();

			AbstractRowFields row = (AbstractRowFields) store.getRow();

			if (row == null) {
				return;
			}

			FieldItem item = row.getFieldByLabel(combo.getName());

			if (item == null) {
				return;
			}

			item.setValueField((String) combo.getSelectedItem());
		};
	}
		
	void setComponentValue(String value, FieldItem field) {
		
		if(component instanceof JTextField) {
			((JTextField)component).setText(value);
		} else if(component instanceof JComboBox) {

            FactoryComponent.initValue((JComboBox<String>) component, field, value);

		} else if (component instanceof JCheckBox) {
			boolean isSelected = !(value == null || "".equals(value.trim()) || "0".equals(value
					.trim()));

			((JCheckBox) component).setSelected(isSelected);
		}
	}
	
	List<DetailFieldItem> getSubFields() {
		return null;
	}

}
