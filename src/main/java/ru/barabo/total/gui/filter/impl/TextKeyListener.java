package ru.barabo.total.gui.filter.impl;

import org.apache.log4j.Logger;
import ru.barabo.total.gui.filter.FilterTable;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class TextKeyListener implements KeyListener {
	
	final static transient private Logger logger = Logger
			.getLogger(TextKeyListener.class.getName());

	private FilterTable mainTable;
	
	public TextKeyListener(FilterTable mainTable) {
		this.mainTable = mainTable;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {
		
		//logger.info("TEXT  ====" + e.getKeyChar());

		String textFilter = e.getSource() instanceof JTextComponent ? ((JTextComponent)e.getSource()).getText() : null;

		mainTable.setFilterPress(textFilter);
		
	}

}
