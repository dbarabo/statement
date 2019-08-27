package ru.barabo.total.gui.table;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class TotalRenderer extends JLabel implements TableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		if(isSelected) {
			setBackground(table.getSelectionBackground());
			setFont(table.getFont());
			this.setOpaque( true );
			setHorizontalAlignment(SwingConstants.LEFT);
		} else {
			setFont(table.getFont());
			setBackground(table.getBackground());
			setHorizontalAlignment(SwingConstants.LEFT);
			this.setOpaque( false );
		}
		
		this.setText(value == null ? "" : value.toString());
		
		return this;
	}
	

}
