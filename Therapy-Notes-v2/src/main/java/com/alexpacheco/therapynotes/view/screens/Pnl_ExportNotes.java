package com.alexpacheco.therapynotes.view.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;

import com.alexpacheco.therapynotes.controller.AppController;
import com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.entities.Note;
import com.alexpacheco.therapynotes.util.AppLogger;
import com.alexpacheco.therapynotes.util.DateFormatUtil;
import com.alexpacheco.therapynotes.util.JavaUtils;
import com.alexpacheco.therapynotes.util.export.NoteDocxExporter;
import com.alexpacheco.therapynotes.util.export.NotePdfExporter;
import com.alexpacheco.therapynotes.view.components.Cmb_ClientSelection;
import com.alexpacheco.therapynotes.view.dialogs.Dlg_ExportProgress;
import com.toedter.calendar.JDateChooser;

/**
 * Panel for bulk exporting therapy notes to PDF or DOCX format. Provides options for export type, note selection criteria, output folder,
 * and file naming convention.
 */
public class Pnl_ExportNotes extends JPanel
{
	private static final long serialVersionUID = 1766307158519686756L;
	
	// Export format selection
	private JRadioButton rbFormatPdf;
	private JRadioButton rbFormatDocx;
	
	// Note selection criteria
	private JRadioButton rbSelectAll;
	private JRadioButton rbSelectByClient;
	private JRadioButton rbSelectByDateRange;
	private Cmb_ClientSelection cmbClient;
	private JDateChooser startDateChooser;
	private JDateChooser endDateChooser;
	
	// Output folder
	private JTextField txtOutputFolder;
	private JButton btnBrowseFolder;
	
	// File naming convention
	private JTextField txtFileNamePattern;
	private JButton btnInsertNoteId;
	private JButton btnInsertApptDate;
	private JButton btnInsertClientCode;
	private JLabel lblPatternPreview;
	
	// Action buttons
	private JButton btnExport;
	private JButton btnCancel;
	
	// Selection modes
	private static final String SELECT_ALL = "ALL";
	private static final String SELECT_CLIENT = "CLIENT";
	private static final String SELECT_DATE_RANGE = "DATE_RANGE";
	
	/**
	 * Creates a new Pnl_BulkExport panel.
	 * 
	 * @param cardLayout The parent CardLayout for navigation
	 * @param mainPanel  The parent panel containing this panel
	 */
	public Pnl_ExportNotes()
	{
		initializeComponents();
		layoutComponents();
		attachListeners();
		updateComponentStates();
	}
	
	/**
	 * Initializes all UI components.
	 */
	private void initializeComponents()
	{
		// Export format
		rbFormatPdf = new JRadioButton( "PDF" );
		rbFormatDocx = new JRadioButton( "Word Document (DOCX)" );
		rbFormatPdf.setSelected( true );
		
		ButtonGroup formatGroup = new ButtonGroup();
		formatGroup.add( rbFormatPdf );
		formatGroup.add( rbFormatDocx );
		
		// Note selection
		rbSelectAll = new JRadioButton( "All Notes" );
		rbSelectByClient = new JRadioButton( "Notes for Specific Client" );
		rbSelectByDateRange = new JRadioButton( "Notes Within Date Range" );
		rbSelectAll.setSelected( true );
		
		ButtonGroup selectionGroup = new ButtonGroup();
		selectionGroup.add( rbSelectAll );
		selectionGroup.add( rbSelectByClient );
		selectionGroup.add( rbSelectByDateRange );
		
		// Client dropdown
		cmbClient = new Cmb_ClientSelection( false );
		cmbClient.setPreferredSize( new Dimension( 250, 25 ) );
		cmbClient.setEnabled( false );
		
		// Date choosers
		startDateChooser = new JDateChooser();
		startDateChooser.setDateFormatString( "MM/dd/yyyy" );
		startDateChooser.setPreferredSize( new Dimension( 150, 25 ) );
		startDateChooser.setEnabled( false );
		
		endDateChooser = new JDateChooser();
		endDateChooser.setDateFormatString( "MM/dd/yyyy" );
		endDateChooser.setPreferredSize( new Dimension( 150, 25 ) );
		endDateChooser.setEnabled( false );
		
		// Output folder
		txtOutputFolder = new JTextField( 30 );
		txtOutputFolder.setText( getDefaultExportFolder() );
		btnBrowseFolder = new JButton( "Browse..." );
		
		// File naming pattern
		txtFileNamePattern = new JTextField( 30 );
		txtFileNamePattern.setText( "{client_code}_{appt_date}_{note_id}" );
		
		btnInsertNoteId = new JButton( "+ Note ID" );
		btnInsertApptDate = new JButton( "+ Appt Date" );
		btnInsertClientCode = new JButton( "+ Client Code" );
		
		btnInsertNoteId.setToolTipText( "Insert {note_id} at cursor position" );
		btnInsertApptDate.setToolTipText( "Insert {appt_date} at cursor position" );
		btnInsertClientCode.setToolTipText( "Insert {client_code} at cursor position" );
		
		lblPatternPreview = new JLabel();
		lblPatternPreview.setFont( lblPatternPreview.getFont().deriveFont( Font.ITALIC ) );
		lblPatternPreview.setForeground( Color.GRAY );
		updatePatternPreview();
		
		// Action buttons
		btnExport = new JButton( "Export Notes" );
		btnCancel = new JButton( "Cancel" );
		
		btnExport.setPreferredSize( new Dimension( 120, 30 ) );
		btnCancel.setPreferredSize( new Dimension( 100, 30 ) );
	}
	
	public void setDefaultValues()
	{
		rbFormatPdf.setSelected( true );
		rbSelectAll.setSelected( true );
		cmbClient.setSelectedIndex( -1 );
		startDateChooser.setDate( null );
		endDateChooser.setDate( null );
		txtOutputFolder.setText( getDefaultExportFolder() );
		txtFileNamePattern.setText( "{client_code}_{appt_date}_{note_id}" );
		updatePatternPreview();
	}
	
	/**
	 * Lays out all components.
	 */
	private void layoutComponents()
	{
		setLayout( new BorderLayout() );
		
		// Title
		JLabel titleLabel = new JLabel( "Export Notes", SwingConstants.CENTER );
		titleLabel.setFont( new Font( "Arial", Font.BOLD, 24 ) );
		titleLabel.setBorder( BorderFactory.createEmptyBorder( 20, 0, 20, 0 ) );
		add( titleLabel, BorderLayout.NORTH );
		
		// Main content panel
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout( new BoxLayout( contentPanel, BoxLayout.Y_AXIS ) );
		contentPanel.setBorder( BorderFactory.createEmptyBorder( 10, 50, 10, 50 ) );
		
		// Export Format Section
		JPanel formatPanel = createTitledPanel( "Export Format" );
		formatPanel.setLayout( new FlowLayout( FlowLayout.LEFT, 20, 5 ) );
		formatPanel.add( rbFormatPdf );
		formatPanel.add( rbFormatDocx );
		contentPanel.add( formatPanel );
		contentPanel.add( Box.createVerticalStrut( 10 ) );
		
		// Note Selection Section
		JPanel selectionPanel = createTitledPanel( "Select Notes to Export" );
		selectionPanel.setLayout( new GridBagLayout() );
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets( 5, 10, 5, 10 );
		gbc.weightx = 0.0;
		
		// All notes option
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		selectionPanel.add( rbSelectAll, gbc );
		
		// By client option
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		selectionPanel.add( rbSelectByClient, gbc );
		
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		selectionPanel.add( cmbClient, gbc );
		
		// By date range option
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 0.0;
		selectionPanel.add( rbSelectByDateRange, gbc );
		
		JPanel dateRangePanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 5, 0 ) );
		dateRangePanel.add( new JLabel( "From:" ) );
		dateRangePanel.add( startDateChooser );
		dateRangePanel.add( Box.createHorizontalStrut( 10 ) );
		dateRangePanel.add( new JLabel( "To:" ) );
		dateRangePanel.add( endDateChooser );
		
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		selectionPanel.add( dateRangePanel, gbc );
		
		contentPanel.add( selectionPanel );
		contentPanel.add( Box.createVerticalStrut( 10 ) );
		
		// Output Folder Section
		JPanel folderPanel = createTitledPanel( "Output Folder" );
		folderPanel.setLayout( new GridBagLayout() );
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets( 5, 10, 5, 10 );
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		folderPanel.add( txtOutputFolder, gbc );
		
		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		folderPanel.add( btnBrowseFolder, gbc );
		
		contentPanel.add( folderPanel );
		contentPanel.add( Box.createVerticalStrut( 10 ) );
		
		// File Naming Section
		JPanel namingPanel = createTitledPanel( "File Naming Convention" );
		namingPanel.setLayout( new GridBagLayout() );
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets( 5, 10, 5, 10 );
		
		// Pattern input row
		gbc.gridx = 0;
		gbc.gridy = 0;
		namingPanel.add( new JLabel( "Pattern:" ), gbc );
		
		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		namingPanel.add( txtFileNamePattern, gbc );
		
		// Variable buttons row
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		
		JPanel variableButtonsPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 5, 0 ) );
		variableButtonsPanel.add( new JLabel( "Insert Variable:" ) );
		variableButtonsPanel.add( btnInsertNoteId );
		variableButtonsPanel.add( btnInsertApptDate );
		variableButtonsPanel.add( btnInsertClientCode );
		namingPanel.add( variableButtonsPanel, gbc );
		
		// Preview row
		gbc.gridy = 2;
		JPanel previewPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 5, 0 ) );
		previewPanel.add( new JLabel( "Preview:" ) );
		previewPanel.add( lblPatternPreview );
		namingPanel.add( previewPanel, gbc );
		
		// Help text
		gbc.gridy = 3;
		gbc.insets = new Insets( 10, 10, 5, 10 );
		JLabel helpLabel = new JLabel( "<html><small>Use {note_id}, {appt_date}, {client_code} as placeholders. "
				+ "Add any text between or around them. Do not include the file extension.<br>"
				+ "Example: \"Note {note_id} on {appt_date} for {client_code}\" → \"Note 42 on 2000-01-01 for JD123.pdf\"</small></html>" );
		helpLabel.setForeground( Color.GRAY );
		namingPanel.add( helpLabel, gbc );
		
		contentPanel.add( namingPanel );
		
		add( contentPanel, BorderLayout.CENTER );
		
		// Button panel
		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 10, 10 ) );
		buttonPanel.setBorder( BorderFactory.createEmptyBorder( 0, 50, 20, 50 ) );
		buttonPanel.add( btnCancel );
		buttonPanel.add( btnExport );
		add( buttonPanel, BorderLayout.SOUTH );
	}
	
	/**
	 * Creates a panel with a titled border.
	 */
	private JPanel createTitledPanel( String title )
	{
		JPanel panel = new JPanel();
		TitledBorder border = BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), title );
		border.setTitleFont( border.getTitleFont().deriveFont( Font.BOLD ) );
		panel.setBorder( border );
		return panel;
	}
	
	/**
	 * Attaches event listeners to components.
	 */
	private void attachListeners()
	{
		// Selection mode changes
		rbSelectAll.addActionListener( e -> updateComponentStates() );
		rbSelectByClient.addActionListener( e -> updateComponentStates() );
		rbSelectByDateRange.addActionListener( e -> updateComponentStates() );
		
		// Format changes update preview
		rbFormatPdf.addActionListener( e -> updatePatternPreview() );
		rbFormatDocx.addActionListener( e -> updatePatternPreview() );
		
		// Browse folder button
		btnBrowseFolder.addActionListener( e -> browseForFolder() );
		
		// Variable insert buttons
		btnInsertNoteId.addActionListener( e -> insertVariable( "{note_id}" ) );
		btnInsertApptDate.addActionListener( e -> insertVariable( "{appt_date}" ) );
		btnInsertClientCode.addActionListener( e -> insertVariable( "{client_code}" ) );
		
		// Pattern text field - update preview on change
		txtFileNamePattern.getDocument().addDocumentListener( new javax.swing.event.DocumentListener()
		{
			@Override
			public void insertUpdate( javax.swing.event.DocumentEvent e )
			{
				updatePatternPreview();
			}
			
			@Override
			public void removeUpdate( javax.swing.event.DocumentEvent e )
			{
				updatePatternPreview();
			}
			
			@Override
			public void changedUpdate( javax.swing.event.DocumentEvent e )
			{
				updatePatternPreview();
			}
		} );
		
		// Export button
		btnExport.addActionListener( e -> performExport() );
		
		// Cancel button
		btnCancel.addActionListener( e -> cancel() );
	}
	
	/**
	 * Updates component enabled states based on selection mode.
	 */
	private void updateComponentStates()
	{
		cmbClient.setEnabled( rbSelectByClient.isSelected() );
		startDateChooser.setEnabled( rbSelectByDateRange.isSelected() );
		endDateChooser.setEnabled( rbSelectByDateRange.isSelected() );
	}
	
	/**
	 * Returns the default export folder path.
	 */
	private String getDefaultExportFolder()
	{
		String userHome = System.getProperty( "user.home" );
		File documentsFolder = new File( userHome, "Documents" );
		File exportFolder = new File( documentsFolder, "ProgressNotes_Export" );
		return exportFolder.getAbsolutePath();
	}
	
	/**
	 * Opens a folder chooser dialog.
	 */
	private void browseForFolder()
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle( "Select Export Folder" );
		chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
		chooser.setAcceptAllFileFilterUsed( false );
		
		// Set current folder as starting point
		String currentPath = txtOutputFolder.getText();
		if( !currentPath.isEmpty() )
		{
			File currentFolder = new File( currentPath );
			if( currentFolder.exists() )
			{
				chooser.setCurrentDirectory( currentFolder );
			}
		}
		
		int result = chooser.showOpenDialog( this );
		if( result == JFileChooser.APPROVE_OPTION )
		{
			txtOutputFolder.setText( chooser.getSelectedFile().getAbsolutePath() );
		}
	}
	
	/**
	 * Inserts a variable placeholder at the cursor position in the pattern field.
	 */
	private void insertVariable( String variable )
	{
		int caretPos = txtFileNamePattern.getCaretPosition();
		String currentText = txtFileNamePattern.getText();
		
		String newText = currentText.substring( 0, caretPos ) + variable + currentText.substring( caretPos );
		txtFileNamePattern.setText( newText );
		txtFileNamePattern.setCaretPosition( caretPos + variable.length() );
		txtFileNamePattern.requestFocus();
	}
	
	/**
	 * Updates the pattern preview label with a sample filename.
	 */
	private void updatePatternPreview()
	{
		String pattern = txtFileNamePattern.getText();
		String extension = rbFormatPdf.isSelected() ? ".pdf" : ".docx";
		
		// Replace variables with sample values
		String preview = pattern.replace( "{note_id}", "42" ).replace( "{appt_date}", "2000-01-01" ).replace( "{client_code}", "JD123" );
		
		lblPatternPreview.setText( preview + extension );
	}
	
	private String getFileNameFromPattern( Note note )
	{
		String pattern = txtFileNamePattern.getText();
		String extension = rbFormatPdf.isSelected() ? ".pdf" : ".docx";
		
		// Replace variables with values from Note object
		String fileName = pattern.replace( "{note_id}", note.getNoteId().toString() )
				.replace( "{appt_date}", DateFormatUtil.toDateFileNameString( note.getApptDateTime() ) )
				.replace( "{client_code}", note.getClient().getClientCode() );
		fileName = JavaUtils.sanitizeFilename( fileName );
		return fileName + extension;
	}
	
	/**
	 * Performs the bulk export operation.
	 */
	private void performExport()
	{
		// Validate inputs
		if( !validateInputs() )
		{
			return;
		}
		
		try
		{
			// Gather notes to export
			List<Note> notesToExport;
			switch( getSelectionMode() )
			{
				case SELECT_DATE_RANGE:
					notesToExport = AppController.searchNotes( null, getStartDate(), getEndDate() );
					break;
				case SELECT_CLIENT:
					notesToExport = AppController.searchNotes( getSelectedClientId(), null, null );
					break;
				default:
					notesToExport = AppController.searchNotes( null, null, null );
					break;
			}
			
			if( notesToExport.isEmpty() )
			{
				JOptionPane.showMessageDialog( this, "No notes found matching the selected criteria.", "Export Warning",
						JOptionPane.WARNING_MESSAGE );
				return;
			}
			
			// Build the notes map
			HashMap<Note, String> notesMap = new HashMap<Note, String>();
			for( Note note : notesToExport )
			{
				String fileOutputPath = getOutputFolder() + "\\" + getFileNameFromPattern( note );
				notesMap.put( note, fileOutputPath );
			}
			
			// Get parent frame for dialog
			Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor( this );
			
			// Create progress dialog
			Dlg_ExportProgress progressDialog = new Dlg_ExportProgress( parentFrame );
			
			// Capture format selection for use in worker
			String selectedFormat = getSelectedFormat();
			
			// Create and execute SwingWorker
			SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>()
			{
				private Exception exportException = null;
				
				@Override
				protected Void doInBackground() throws Exception
				{
					try
					{
						List<Note> noteList = new ArrayList<>( notesMap.keySet() );
						int total = noteList.size();
						
						for( int i = 0; i < total; i++ )
						{
							Note note = noteList.get( i );
							String outputPath = notesMap.get( note );
							
							// Export single note based on format
							switch( selectedFormat )
							{
								case "DOCX":
									NoteDocxExporter.exportToDocx( note, outputPath );
									break;
								case "PDF":
									NotePdfExporter.exportToPdf( note, outputPath );
									break;
							}
							
							// Publish progress (1-based for display)
							publish( i + 1 );
						}
					}
					catch( Exception e )
					{
						exportException = e;
						AppLogger.error( e );
					}
					return null;
				}
				
				@Override
				protected void process( List<Integer> chunks )
				{
					// Get the most recent progress update
					int current = chunks.get( chunks.size() - 1 );
					int total = notesMap.size();
					progressDialog.setProgress( current, total );
				}
				
				@Override
				protected void done()
				{
					progressDialog.dispose();
					
					if( exportException != null )
					{
						AppLogger.logBulkExport( notesMap.size(), getSelectedFormat(), false );
						if( exportException instanceof TherapyAppException )
						{
							AppController.showBasicErrorPopup( (TherapyAppException) exportException,
									"Error occurred while exporting notes." );
						}
						else
						{
							AppController.showBasicErrorPopup( "Error occurred while exporting notes: " + exportException.getMessage() );
						}
					}
					else
					{
						JOptionPane.showMessageDialog( null, "Notes successfully exported to the following folder:\n" + getOutputFolder(),
								"Success", JOptionPane.INFORMATION_MESSAGE );
						AppLogger.logBulkExport( notesMap.size(), getSelectedFormat(), true );
						setDefaultValues();
						AppController.returnHome( true );
					}
				}
			};
			
			worker.execute();
			progressDialog.setVisible( true ); // Blocks until dialog is disposed
		}
		catch( TherapyAppException e )
		{
			AppLogger.error( "Error occurred while exporting notes.", e );
			AppController.showBasicErrorPopup( e, "Error occurred while exporting notes." );
		}
	}
	
	/**
	 * Validates all input fields.
	 */
	private boolean validateInputs()
	{
		// Validate client selection
		if( rbSelectByClient.isSelected() )
		{
			String selected = (String) cmbClient.getSelectedItem();
			if( JavaUtils.isNullOrEmpty( selected ) )
			{
				AppController.showBasicErrorPopup( "Please select a client." );
				return false;
			}
		}
		
		// Validate date range
		if( rbSelectByDateRange.isSelected() )
		{
			Date startDate = startDateChooser.getDate();
			Date endDate = endDateChooser.getDate();
			
			if( startDate == null || endDate == null )
			{
				AppController.showBasicErrorPopup( "Please select both start and end dates." );
				return false;
			}
			
			if( startDate.after( endDate ) )
			{
				AppController.showBasicErrorPopup( "Start date cannot be after end date." );
				return false;
			}
		}
		
		// Validate output folder
		if( !validateOutputFolder() )
		{
			return false;
		}
		
		if( !validateFileNamePattern() )
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Validates the output folder path. The path must either exist as a directory or be a valid path that can be created.
	 * 
	 * @return true if valid, false otherwise
	 */
	private boolean validateOutputFolder()
	{
		String outputFolder = txtOutputFolder.getText();
		
		if( JavaUtils.isNullOrEmpty( outputFolder ) )
		{
			AppController.showBasicErrorPopup( "Please specify an output folder." );
			return false;
		}
		
		File folder = new File( outputFolder );
		
		// Check if it exists and is a directory
		if( folder.exists() )
		{
			if( !folder.isDirectory() )
			{
				AppController.showBasicErrorPopup( "The specified path is a file, not a directory." );
				return false;
			}
			return true;
		}
		
		// Doesn't exist - check if parent path is valid and we could create it
		File parentFolder = folder.getParentFile();
		if( parentFolder == null || !parentFolder.exists() )
		{
			AppController.showBasicErrorPopup( "The specified output folder path is invalid." );
			return false;
		}
		
		return true;
	}
	
	/**
	 * Validates the file naming pattern. The pattern must be able to produce unique filenames, which requires either: - The {note_id}
	 * variable, OR - Both {appt_date} AND {client_code} variables
	 * 
	 * @return true if valid, false otherwise
	 */
	private boolean validateFileNamePattern()
	{
		String pattern = txtFileNamePattern.getText();
		
		if( JavaUtils.isNullOrEmpty( pattern ) )
		{
			AppController.showBasicErrorPopup( "Please specify a file naming pattern." );
			return false;
		}
		
		boolean hasNoteId = pattern.contains( "{note_id}" );
		boolean hasApptDate = pattern.contains( "{appt_date}" );
		boolean hasClientCode = pattern.contains( "{client_code}" );
		
		// Must have note_id OR (appt_date AND client_code) for uniqueness
		if( !hasNoteId && !( hasApptDate && hasClientCode ) )
		{
			AppController.showBasicErrorPopup(
					"File naming pattern must include {note_id} OR both {appt_date} and {client_code} to ensure unique file names." );
			return false;
		}
		
		return true;
	}
	
	/**
	 * Navigates back to the previous screen.
	 */
	private void cancel()
	{
		setDefaultValues();
		AppController.returnHome( true );
	}
	
	// ===== Public Getters for Export Options =====
	
	/**
	 * Returns the selected export format.
	 * 
	 * @return "PDF" or "DOCX"
	 */
	public String getSelectedFormat()
	{
		return rbFormatPdf.isSelected() ? "PDF" : "DOCX";
	}
	
	/**
	 * Returns the selection mode.
	 * 
	 * @return "ALL", "CLIENT", or "DATE_RANGE"
	 */
	public String getSelectionMode()
	{
		if( rbSelectByDateRange.isSelected() )
		{
			return SELECT_DATE_RANGE;
		}
		else if( rbSelectByClient.isSelected() )
		{
			return SELECT_CLIENT;
		}
		else
		{
			return SELECT_ALL;
		}
	}
	
	/**
	 * Returns the selected client ID (if selection mode is CLIENT).
	 * 
	 * @return The client ID, or null if none selected
	 */
	public Integer getSelectedClientId()
	{
		return cmbClient.getSelectedClientId();
	}
	
	/**
	 * Returns the start date (if selection mode is DATE_RANGE).
	 * 
	 * @return The start date, or null
	 */
	public Date getStartDate()
	{
		return startDateChooser.getDate();
	}
	
	/**
	 * Returns the end date (if selection mode is DATE_RANGE).
	 * 
	 * @return The end date, or null
	 */
	public Date getEndDate()
	{
		return endDateChooser.getDate();
	}
	
	/**
	 * Returns the output folder path.
	 * 
	 * @return The folder path
	 */
	public String getOutputFolder()
	{
		return txtOutputFolder.getText();
	}
	
	/**
	 * Returns the file naming pattern.
	 * 
	 * @return The pattern with variable placeholders
	 */
	public String getFileNamePattern()
	{
		return txtFileNamePattern.getText();
	}
}