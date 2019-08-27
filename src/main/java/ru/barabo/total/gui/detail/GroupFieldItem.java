package ru.barabo.total.gui.detail;


import javax.swing.JComponent;


import org.apache.log4j.Logger;
import ru.barabo.total.db.DetailFieldItem;
import ru.barabo.total.db.Type;

import java.util.ArrayList;
import java.util.List;

public class GroupFieldItem implements DetailFieldItem {
	
	final private int MAX_ROW = 10000;
	
	final transient private static Logger logger = Logger
			.getLogger(GroupFieldItem.class.getName());

	private String label;
	
	private int width;
	
	private int posX;
	
	private int posY;
	
	private int height;
	
	private List<DetailFieldItem> fields;
	
	
	public GroupFieldItem(String label, List<DetailFieldItem> subFileds) {
		this.label = label;
		
		addSubFields(subFileds);
		
		checkWidthHeight();
	}
	
	private void checkWidthHeight() {
		
		width = 0;
		
		height = 0;
		
		posX = 0;
		
		posY = MAX_ROW;
		
		for (DetailFieldItem field : fields) {
			if(field.getPosX() + field.getWidth() > width) {
				width = field.getPosX() + field.getWidth();
			}
			
			if(field.getPosX() == 0) {
				height += field.getHeight();
			}
			
			if(field.getPosY() < posY) {
				posY = field.getPosY();
			}
		}
	}
	
	private void addSubFields(List<DetailFieldItem> subFileds) {
		fields = new ArrayList<DetailFieldItem>();
		
		for (DetailFieldItem field : subFileds) {

			if(label.equals(field.getGroupLabel())) {
				fields.add(field);
			}
		}
	}
	
	@Override
	public List<DetailFieldItem> getSubFields() {

		return fields;
	}

	@Override
	public String getLabel() {
		
		return label;
	}
	
	@Override
	public int getWidth() {
		return width;
	}
	
	@Override
	public String getGroupLabel() {
		return label;
	}

	@Override
	public int getPosX() {
		return posX;
	}

	@Override
	public int getPosY() {
		return posY;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void setComponentValue(String value) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void setComponent(JComponent component) {
		// TODO Auto-generated method stub
	}

	@Override
	public JComponent getComponent() {
		// TODO Auto-generated method stub
		return null;
	}
		

	@Override
	public boolean isExistsGrid() {
		return false;
	}

	@Override
	public void setValueField(String value) {
	}

	@Override
	public void setValueFieldObject(Object value) {
	}

	@Override
	public String getColumn() {
		return null;
	}

	@Override
	public String[] getListField() {
		return null;
	}

	@Override
	public void setListField(String[] valueList) {

	}

	@Override
	public String getValueField() {
		return null;
	}

	@Override
	public Object getVal() {
		return null;
	}

	@Override
	public int getIndex() {
		return -1;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public Type getClazz() {
		return Type.LONG;
	}


}
