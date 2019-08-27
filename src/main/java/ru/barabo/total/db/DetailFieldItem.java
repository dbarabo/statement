package ru.barabo.total.db;

import javax.swing.*;
import java.util.List;

public interface DetailFieldItem extends FieldItem {

	String getGroupLabel();
	
	int getPosX();
	
	int getPosY();
	
	int getHeight();

	void setComponent(JComponent component);
	
	void setComponentValue(String value);
	
	List<DetailFieldItem> getSubFields();

	JComponent getComponent();
}
