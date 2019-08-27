package ru.barabo.total.gui.any;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class ShowMenuListener {

	private JPopupMenu popupMenu;

	private Map<String, ActionListener> listeners;

	private AbstractButton sourceButton;

	private String ico;

	private String caption;

	public ShowMenuListener(ButtonKarkas[] menuItems) {

		initPopup(menuItems);
	}

	public ActionListener getListener() {

		return this::showMenu;
	}

	public ButtonKarkas createButtonKarkas(int groupIndex) {

		return new ButtonKarkas(ico, caption, getListener(), groupIndex);
	}

	private void initPopup(ButtonKarkas[] menuItems) {
		popupMenu = new JPopupMenu();

		listeners = new HashMap<String, ActionListener>();

		ico = menuItems[0].getIco();

		caption = menuItems[0].getName();

		int index = 0;
		for (ButtonKarkas karkas : menuItems) {

			if (karkas.getName() == null) {
				popupMenu.addSeparator();
				continue;
			}
			JMenuItem item = new JMenuItem(karkas.getName(), karkas.getImageIco());

			String name = "item" + index;

			item.setName(name);

			listeners.put(name, karkas.getListener());

			item.addActionListener(this::replaceListener);
			popupMenu.add(item);

			index++;
		}
	}

	private void showMenu(ActionEvent e) {

		Component src = (Component) e.getSource();

		if (src instanceof AbstractButton) {
			sourceButton = (AbstractButton) src;
		}

		popupMenu.show(src, 1, src.getHeight() + 1);
	}

	private void replaceListener(ActionEvent e) {
		Component comp = (Component) e.getSource();

		if (comp == null) {
			return;
		}

		ActionListener listener = listeners.get(comp.getName());

		if (listener != null) {
			listener.actionPerformed(e);
		}

		changeMainMenu(comp, sourceButton);

		sourceButton = null;
	}

	private void changeMainMenu(Object src, AbstractButton mainMenu) {
		if (mainMenu == null || !(src instanceof AbstractButton)) {
			return;
		}

		AbstractButton btnSrc = (AbstractButton) src;

		mainMenu.setText(btnSrc.getText());
		mainMenu.setIcon(btnSrc.getIcon());
	}

}
