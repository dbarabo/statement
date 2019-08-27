package ru.barabo.total.gui.any;

import ru.barabo.statement.main.resources.ResourcesManager;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.List;

public class ButtonKarkas {

	private String ico;
	private String name;

	private ActionListener listener;
	private Integer groupIndex;
	
	private AbstractButton button;
	
	public ButtonKarkas(String ico, String name, 
			ActionListener listener, Integer groupIndex) {
		
		this.ico = ico;
		this.name = name;
		this.listener = listener;
		this.groupIndex = groupIndex;
	}

	public ButtonKarkas(String ico, String name,
						ActionListener listener) {

		this(ico, name, listener, null);
	}

	protected void setListener(ActionListener listener) {
		this.listener = listener;
	}

	public ButtonKarkas(String ico, String name) {
		this(ico, name, null, null);
	}
	
	public AbstractButton getButton() {
		return button;
	}

	void setButton(AbstractButton button) {
		this.button = button;
	}
	
	String getIco() {
		return ico;
	}

	public ImageIcon getImageIco() {
		return getIco() == null ? null : ResourcesManager.getIcon(getIco());
	}

	public String getName() {
		return name;
	}
	
	Integer getGroupIndex() {
		return groupIndex;
	}

	public ActionListener getListener() {
		return listener;
	}

	static public AbstractButton createButton(ButtonKarkas karkas, List<ButtonGroup> buttonGroupList) {
		if(karkas.getName() == null) return null;

		ImageIcon icon = ResourcesManager.getIcon(karkas.getIco());

		AbstractButton button;

		if(karkas.getGroupIndex() != null) {
			button = new JToggleButton(icon);
			if(buttonGroupList != null) {
                addGroup(buttonGroupList, button, karkas.getGroupIndex());
			}
		} else {
			button = new JButton(icon);
		}
		// show caption on
		button.setText(karkas.getName());

		button.setToolTipText(karkas.getName());
		button.addActionListener(karkas.getListener() );
		karkas.setButton(button);

		return button;
	}

    static private void addGroup(List<ButtonGroup> buttonGroupList, AbstractButton button, int index) {

        if(buttonGroupList.size() <= index) {
            buttonGroupList.add(new ButtonGroup());
        }

        buttonGroupList.get(index).add(button);
    }
}
