package org.csdgn;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.TransferHandler;

import org.csdgn.io.UnsupportedFileTypeException;
import org.csdgn.utils.Filename;

/**
 * Kinda the go between the view and the model
 * 
 * @author Chase
 * 
 */
public class SpriteCutterController extends TransferHandler implements
		ActionListener {
	private static final long serialVersionUID = 2534889150810141066L;

	public boolean fileLoaded = false;

	/*
	 * Linux requires we have our own datatype for correct drag and drop
	 * support. So since pretty much every other OS is a flavor of linux. We
	 * detect if are on windows (easier).
	 */
	private static final boolean isWindows = System.getProperty("os.name")
			.toLowerCase().contains("windows");
	private static DataFlavor linuxFileDataFlavor = null;
	static {
		try {
			linuxFileDataFlavor = new DataFlavor(
					"text/uri-list;class=java.lang.String");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public boolean canImport(TransferHandler.TransferSupport info) {
		if (isWindows) {
			return info.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
		}
		if (linuxFileDataFlavor == null) {
			return false;
		}
		return info.isDataFlavorSupported(linuxFileDataFlavor);
	}

	public boolean importData(TransferHandler.TransferSupport info) {
		if (!info.isDrop()) {
			return false;
		}

		try {
			Transferable tf = info.getTransferable();

			File targetFile = null;

			if (isWindows) {
				// WINDOWS METHOD
				@SuppressWarnings("unchecked")
				List<File> list = (List<File>) tf
						.getTransferData(DataFlavor.javaFileListFlavor);
				targetFile = list.get(0);
			} else {
				// NOT WINDOWS METHOD (joy)
				String data = (String) tf.getTransferData(linuxFileDataFlavor);
				for (StringTokenizer st = new StringTokenizer(data, "\r\n"); st
						.hasMoreTokens();) {
					String token = st.nextToken().trim();
					if (token.startsWith("#") || token.isEmpty()) {
						// comment line, by RFC 2483
						continue;
					}

					targetFile = new File(new URI(token));
					break;
				}
			}

			if (targetFile != null) {
				importFile(targetFile);
			}
		} catch (UnsupportedFileTypeException e) {
			SpriteCutter.view.showErrorMessage(e.getMessage());
		} catch (Exception e) {
			SpriteCutter.view.showErrorMessage(e.getMessage());
			e.printStackTrace();
		}

		return false;
	}

	public void importFile(File file) throws Exception {
		SpriteCutter.model.loadImage(file);
		generateImages();

		fileLoaded = true;

		SpriteCutter.view.importTarget.setText(file.getAbsolutePath());
		SpriteCutter.view.exportTarget
				.setText(SpriteCutter.model.targetFilename.getDirectory());
		SpriteCutter.view.enableExport();
	}

	private void generateImages() throws Exception {
		SpriteCutter.model.cutImage();
		SpriteCutter.model.generateImages();
		updatePreview();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		char cmd = e.getActionCommand().charAt(0);

		Object source = e.getSource();

		if (source instanceof JCheckBox) {
			JCheckBox box = (JCheckBox) e.getSource();
			switch (cmd) {
			case 'm': // mask output
				SpriteCutter.model.maskOutput = box.isSelected();
				break;
			case 's': // secondary mask
				SpriteCutter.model.secondaryMask = box.isSelected();
				break;
			case '8': // 8 way search
				SpriteCutter.model.eightWay = box.isSelected();
				break;
			case 'w': // wide/reach search
				SpriteCutter.model.wide = box.isSelected();
				break;
			case 'b': // multisample background
				SpriteCutter.model.multiSampleBG = box.isSelected();
				break;
			}
			// blaaah
			if (fileLoaded)
				try {
					generateImages();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
		} else {
			switch (cmd) {
			case 'e': // export to files
				try {
					File file = new File(SpriteCutter.view.exportTarget.getText());
					if (file.isFile())
						file = file.getParentFile();
					if (file.exists())
						updateModelExportPath(file);
					SpriteCutter.model.writeImages();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				break;
			case 'b': { // export browse
				File file = new File(SpriteCutter.view.exportTarget.getText());
				if (file.isFile())
					file = file.getParentFile();
				if (file.exists())
					SpriteCutter.view.exportChooser.setCurrentDirectory(file);
				SpriteCutter.view.showExportFolderChooser();
				break;
			}
			case 'i': { // import browse
				File file = new File(SpriteCutter.view.importTarget.getText());
				if (file.isFile())
					file = file.getParentFile();
				if (file.exists())
					SpriteCutter.view.importChooser.setCurrentDirectory(file);
				SpriteCutter.view.showImportFileChooser();
			}

			}
		}
	}

	public void updateViewExportPath() {
		SpriteCutter.view.exportTarget
				.setText(SpriteCutter.model.targetFilename.getDirectory());
	}

	public void updateModelExportPath(File path) {
		if (!path.exists())
			return;
		if (path.isFile())
			path = path.getParentFile();
		SpriteCutter.model.targetFilename = new Filename(path);
		SpriteCutter.model.targetFilename.setNameIsDirectory();
	}

	/**
	 * This method is a mess and I know it.
	 */
	public void updatePreview() {
		SpriteCutter.view.previewList.setVisible(false);
		SpriteCutter.view.previewModel.clear();
		for (int i = 0; i < SpriteCutter.model.images.length; ++i) {
			SpriteCutter.view.previewModel.add(new ImageIcon(
					SpriteCutter.model.images[i]));
		}
		SpriteCutter.view.previewList.setVisible(true);
	}
}
