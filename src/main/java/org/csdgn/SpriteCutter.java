package org.csdgn;

import java.awt.EventQueue;
import java.util.Locale;

import javax.swing.ToolTipManager;
import javax.swing.UIManager;

public class SpriteCutter {
	public static SpriteCutterModel model = new SpriteCutterModel();
	public static SpriteCutterView view;
	public static SpriteCutterController controller = new SpriteCutterController();
	
	public static final String VERSION = "1.5";
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {}
		try {
			ToolTipManager.sharedInstance().setInitialDelay(500);
			ToolTipManager.sharedInstance().setDismissDelay(30000);
			ToolTipManager.sharedInstance().setReshowDelay(250);
		} catch(Exception e) {}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					view = new SpriteCutterView();
					view.setVisible(true);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
