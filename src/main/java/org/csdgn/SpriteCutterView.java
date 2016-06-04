package org.csdgn;

import java.awt.BorderLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.BoxLayout;
import javax.swing.border.BevelBorder;
import javax.swing.JCheckBox;
import javax.swing.JSplitPane;
import java.awt.Dimension;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.csdgn.io.UnsupportedFileTypeException;
import org.csdgn.swing.ArrayListModel;
import org.csdgn.swing.ThumbnailListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.JButton;
import javax.swing.JTextField;

/**
 * The thing you see when you start the program
 * @author Chase
 *
 */
public class SpriteCutterView extends JFrame {
	private static final long serialVersionUID = -3045641152075832443L;
	
	private JPanel contentPane;
	private SpriteCutterController controller;
	protected JTextField exportTarget;
	protected ArrayListModel previewModel = new ArrayListModel();
	protected JList previewList;
	private JButton btnExportBrowse;
	private JButton btnExportToFile;
	protected JFileChooser importChooser;
	protected JFileChooser exportChooser;
	protected JTextField importTarget;
	
	/**
	 * Create the frame.
	 */
	public SpriteCutterView() {
		setTitle("Chase's Sprite Cutter v" +SpriteCutter.VERSION);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationByPlatform(true);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 2));
		
		controller = new SpriteCutterController();
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerSize(0);
		splitPane.setOpaque(false);
		splitPane.setRequestFocusEnabled(false);
		splitPane.setBorder(null);
		splitPane.setEnabled(false);
		splitPane.setResizeWeight(0.5);
		contentPane.add(splitPane, BorderLayout.NORTH);
		
		JPanel maskOptionPanel = new JPanel();
		splitPane.setLeftComponent(maskOptionPanel);
		maskOptionPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Mask Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		maskOptionPanel.setLayout(new BoxLayout(maskOptionPanel, BoxLayout.Y_AXIS));
		
		JCheckBox chckbxMaskOutput = new JCheckBox("Mask Output");
		chckbxMaskOutput.setActionCommand("m");
		chckbxMaskOutput.addActionListener(controller);
		chckbxMaskOutput.setToolTipText("Alpha mask's the output sprites, useful for overlapping sprites.");
		maskOptionPanel.add(chckbxMaskOutput);
		
		JCheckBox chckbxClearSecondaryMask = new JCheckBox("Clear Secondary Mask");
		chckbxClearSecondaryMask.setActionCommand("s");
		chckbxClearSecondaryMask.addActionListener(controller);
		chckbxClearSecondaryMask.setToolTipText("Generally used if your sprites have a second mask on them aside from the background color.");
		maskOptionPanel.add(chckbxClearSecondaryMask);
		
		JCheckBox chckbxMultisampleBg = new JCheckBox("Multisample BG");
		chckbxMultisampleBg.setActionCommand("b");
		chckbxMultisampleBg.setToolTipText("Takes a sample from the 4 edges and corners of the image to determine background color.");
		maskOptionPanel.add(chckbxMultisampleBg);
		
		JPanel fillOptionPane = new JPanel();
		splitPane.setRightComponent(fillOptionPane);
		fillOptionPane.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Shape Fill Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		fillOptionPane.setLayout(new BoxLayout(fillOptionPane, BoxLayout.Y_AXIS));
		
		JCheckBox chckbxEightWaySearch = new JCheckBox("Eight Way Search");
		chckbxEightWaySearch.setActionCommand("8");
		chckbxEightWaySearch.addActionListener(controller);
		chckbxEightWaySearch.setToolTipText("Sets the system to use a diagonal search as well as North, South, East and West.");
		fillOptionPane.add(chckbxEightWaySearch);
		
		JCheckBox chckbxWideSearch = new JCheckBox("Wide Search");
		chckbxWideSearch.setToolTipText("Searches an extra sample outward in each sampled direction. If used with eight way find it will also look between the spokes.");
		chckbxWideSearch.setActionCommand("w");
		chckbxWideSearch.addActionListener(controller);
		fillOptionPane.add(chckbxWideSearch);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(2, 80));
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		previewList = new JList();
		previewList.setToolTipText("This is a preview pane, for previewing what the cut sprites will look like.");
		previewList.setModel(previewModel);
		previewList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		previewList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		previewList.setVisibleRowCount(-1);
		previewList.setCellRenderer(new ThumbnailListCellRenderer());
		
		//list.set
		scrollPane.setViewportView(previewList);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 2));
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Export", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new BorderLayout(4, 4));
		
		exportTarget = new JTextField();
		exportTarget.setToolTipText("This is the directory where you want to export the files");
		exportTarget.setEnabled(false);
		panel_1.add(exportTarget);
		exportTarget.setColumns(10);
		
		btnExportBrowse = new JButton("Browse");
		btnExportBrowse.setActionCommand("b");
		btnExportBrowse.addActionListener(SpriteCutter.controller);
		btnExportBrowse.setToolTipText("Use this to browse for a folder to save the images.");
		btnExportBrowse.setEnabled(false);
		panel_1.add(btnExportBrowse, BorderLayout.EAST);
		
		btnExportToFile = new JButton("Export To Files");
		btnExportToFile.setActionCommand("e");
		btnExportToFile.addActionListener(SpriteCutter.controller);
		btnExportToFile.setToolTipText("By clicking this you will export the given preview images to the given directory");
		btnExportToFile.setEnabled(false);
		panel_1.add(btnExportToFile, BorderLayout.SOUTH);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "Import", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(4, 4));
		
		JPanel dropPanel = new JPanel();
		dropPanel.setPreferredSize(new Dimension(10, 40));
		panel_2.add(dropPanel, BorderLayout.NORTH);
		dropPanel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		dropPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblDropFilesHere = new JLabel("Drag and Drop Image Here");
		lblDropFilesHere.setToolTipText("Output files will be placed in the origin folder of the dropped image.");
		lblDropFilesHere.setHorizontalAlignment(SwingConstants.CENTER);
		lblDropFilesHere.setRequestFocusEnabled(false);
		lblDropFilesHere.setFocusable(false);
		lblDropFilesHere.setTransferHandler(controller);
		
		dropPanel.add(lblDropFilesHere, BorderLayout.CENTER);
		
		JPanel panel_3 = new JPanel();
		panel_2.add(panel_3, BorderLayout.SOUTH);
		panel_3.setLayout(new BorderLayout(4, 4));
		
		importTarget = new JTextField();
		importTarget.setEditable(false);
		panel_3.add(importTarget);
		importTarget.setColumns(10);
		
		JButton btnImportBrowse = new JButton("Browse");
		btnImportBrowse.addActionListener(SpriteCutter.controller);
		btnImportBrowse.setToolTipText("Use this to browse for a file to open.");
		btnImportBrowse.setActionCommand("i");
		panel_3.add(btnImportBrowse, BorderLayout.EAST);
		
		pack();
		
		importChooser = new JFileChooser();
		importChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		importChooser.setMultiSelectionEnabled(false);
		
		exportChooser = new JFileChooser();
		exportChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	}
	
	protected void showImportFileChooser() {
		if(importChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				SpriteCutter.controller.importFile(importChooser.getSelectedFile());
			} catch (UnsupportedFileTypeException e) {
				SpriteCutter.view.showErrorMessage(e.getMessage());
			} catch (Exception e) {
				SpriteCutter.view.showErrorMessage(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	protected void showExportFolderChooser() {
		if(exportChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			SpriteCutter.controller.updateModelExportPath(exportChooser.getSelectedFile());
			SpriteCutter.controller.updateViewExportPath();
		}
	}
	
	protected void enableExport() {
		exportTarget.setEnabled(true);
		btnExportBrowse.setEnabled(true);
		btnExportToFile.setEnabled(true);
	}
	
	public void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
}
