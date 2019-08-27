package ru.barabo.total.gui.detail;

import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import ru.barabo.total.db.DetailFieldItem;
import ru.barabo.total.db.FieldItem;

public class FactoryComponent {

	static private final String UNICAL_STRING = "\b";

	static public JComponent create(DetailFieldItem field) {

		JComponent comp;

		if (field.getListField() == null || field.getListField().length < 2) {
			comp = new JTextField();
			initValue((JTextField) comp, field);

			((JTextField) comp).setEditable(!field.isReadOnly());

		} else if (field.getListField().length == 2 &&
				"0".equals(field.getListField()[0]) &&
				"1".equals(field.getListField()[1])) {
			comp = new JCheckBox();
			initValue((JCheckBox) comp, field);
		} else {
            JComboBox<String> combo = new JComboBox<>(field.getListField());
            comp = combo;
			initValue(combo, field);
		}

		field.setComponent(comp);

		return comp;
	}

	static private void initValue(JTextField comp, DetailFieldItem field) {
		comp.setText(field.getValueField());
	}

	static private void initValue(JCheckBox comp, DetailFieldItem field) {
		boolean isNotEnabled = field.getValueField() == null ||
				"".equals(field.getValueField().trim()) ||
				"0".equals(field.getValueField().trim());

		comp.setSelected(!isNotEnabled);
	}

	private static void initValue(JComboBox<String> comp, FieldItem field) {
		initValue(comp, field, field.getValueField());
	}

	static public void initValue(JComboBox<String> comp, FieldItem field, String valueField) {

		setListItemsT(comp, Arrays.asList(field.getListField()), valueField);
	}
	
	static public void setListItemsT(JComboBox<String> comp, List<String> itemValues) {

		setListItemsT(comp, itemValues, UNICAL_STRING);
	}

	static public <T> void setListItemsT(JComboBox<T> comp, List<T> itemValues, T newValue) {
		ActionListener[] listeners = comp.getActionListeners();

		for (ActionListener listener : listeners) {
			comp.removeActionListener(listener);
		}

		comp.removeAllItems();

		for (T item : itemValues) {
			comp.addItem(item);
		}

		if (newValue == null || itemValues.indexOf(newValue) >= 0) {
			comp.setSelectedItem(newValue);
		}

		for (ActionListener listener : listeners) {
			comp.addActionListener(listener);
		}
	}
}
