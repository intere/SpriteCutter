package org.csdgn.swing;

import javax.swing.*;
import java.awt.*;

public class ThumbnailListCellRenderer extends JComponent implements ListCellRenderer<Icon> {
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
	public Component getListCellRendererComponent(JList<? extends Icon> list, Icon value, int index, boolean isSelected, boolean cellHasFocus) {
		
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

		label.setIcon(value);

		Dimension d = new Dimension(value.getIconWidth()+8,value.getIconHeight()+8);
		setMaximumSize(d);
		setPreferredSize(d);
		
		return this;
	}


}
