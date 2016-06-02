package org.csdgn.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

public class ThumbnailListCellRenderer extends JComponent implements ListCellRenderer {
	private static final long serialVersionUID = 7809286388638138365L;

	private JLabel label;
	public ThumbnailListCellRenderer() {
		setLayout(new BorderLayout());
		label = new JLabel();
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setOpaque(true);
		add(label);
		
	}
	
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		
		Color background;
		Color foreground;
		
//		if(isSelected) {
//			background = UIManager.getColor("List.selectionBackground");
//			foreground = UIManager.getColor("List.selectionForeground");
//			
//		} else {
			background = UIManager.getColor("List.background");
			foreground = UIManager.getColor("List.foreground");
//		}
		
		label.setBackground(background);
		label.setForeground(foreground);

		if(value instanceof Icon) {
			Icon ico = (Icon)value;
			label.setIcon(ico);
			
			Dimension d = new Dimension(ico.getIconWidth()+8,ico.getIconHeight()+8);
			setMaximumSize(d);
			setPreferredSize(d);
		}
		
		return this;
	}
}
