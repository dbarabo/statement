package ru.barabo.total.db.impl;

import ru.barabo.total.db.DBStore;
import ru.barabo.total.db.DetailFieldItem;
import ru.barabo.total.db.Type;

import javax.swing.*;
import java.text.Format;
import java.util.EventListener;
import java.util.List;

public class DetailField extends Field implements DetailFieldItem {
	
	//final transient private static Logger logger = Logger.getLogger(DetailField.class.getName());
	

	private DelegateDetailField detailField;
	
	public DetailField(String label, boolean isGrid, Type clazz,
			String[] list, String column, int width, int index, boolean isReadOnly,
					   Integer[] maps, Format formatter, String groupLabel, int posX, int posY, int height) {
		
		super(label, isGrid, clazz, list, column, width, index, isReadOnly, maps, formatter);
		
		detailField = new DelegateDetailField(groupLabel, posX, posY, height);
	}

	public DetailField(String label, boolean isGrid, Type clazz,
			String[] list, String column, int width, int index, boolean isReadOnly,
					   Integer[] maps, Format formatter,
			String groupLabel, int posX, int posY, int height,
			EventListener listener) {

		super(label, isGrid, clazz, list, column, width, index, isReadOnly, maps, formatter);

		detailField = new DelegateDetailField(groupLabel, posX, posY, height, listener);
	}

	public void setStore(DBStore store) {
		detailField.setStore(store);
	}

	@Override
	public String getGroupLabel() {
		return detailField.getGroupLabel();
	}

	@Override
	public int getPosX() {

		return detailField.getPosX();
	}

	@Override
	public int getPosY() {
		
		return detailField.getPosY();
	}

	@Override
	public int getHeight() {
		
		return detailField.getHeight();
	}

	@Override
	public void setComponent(JComponent component) {
		
		detailField.setComponent(component);		
	}

	@Override
	public void setComponentValue(String value) {
		detailField.setComponentValue(value, this);
	}

	@Override
	public List<DetailFieldItem> getSubFields() {
		return detailField.getSubFields();
	}

	@Override
	public JComponent getComponent() {
		return detailField.getComponent();
	}

}
