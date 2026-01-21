package com.alexpacheco.therapynotes.view.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import com.alexpacheco.therapynotes.controller.AppController;
import com.alexpacheco.therapynotes.controller.Exporter;
import com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.entities.Client;
import com.alexpacheco.therapynotes.model.entities.CollateralContact;
import com.alexpacheco.therapynotes.model.entities.Note;
import com.alexpacheco.therapynotes.model.entities.Referral;
import com.alexpacheco.therapynotes.model.entities.Symptom;
import com.alexpacheco.therapynotes.model.entities.assessmentoptions.AffectAssessmentOption;
import com.alexpacheco.therapynotes.model.entities.assessmentoptions.AppearanceAssessmentOption;
import com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOption;
import com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOptionType;
import com.alexpacheco.therapynotes.model.entities.assessmentoptions.EyeContactAssessmentOption;
import com.alexpacheco.therapynotes.model.entities.assessmentoptions.NextApptAssessmentOption;
import com.alexpacheco.therapynotes.model.entities.assessmentoptions.SpeechAssessmentOption;
import com.alexpacheco.therapynotes.util.DateFormatUtil;
import com.alexpacheco.therapynotes.util.JavaUtils;
import com.alexpacheco.therapynotes.util.PreferencesUtil;
import com.alexpacheco.therapynotes.view.components.AssessmentOptionCheckBox;
import com.alexpacheco.therapynotes.view.components.AssessmentOptionRadioButton;
import com.alexpacheco.therapynotes.view.components.Cmb_ClientSelection;
import com.alexpacheco.therapynotes.view.components.Cmb_ICD10Diagnosis;
import com.toedter.calendar.JDateChooser;

/**
 * Panel for creating or editing a client progress note. Contains sections for session info, clinical symptoms, narrative, mental status,
 * and post-session administrative information.
 */
public class Pnl_NewEditNote extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	// Session Info components
	private Cmb_ClientSelection cmbClient;
	private JDateChooser dateAppointment;
	private Cmb_ICD10Diagnosis cmbDiagnosis;
	private JLabel lblDateOfBirth;
	private JTextField txtSessionNumber;
	private JTextField txtLengthOfSession;
	private JTextField txtAppointmentComment;
	private JCheckBox chkVirtual;
	
	// Clinical Symptoms components
	private List<AssessmentOptionCheckBox> symptomCheckboxes;
	
	// Narrative components
	private JTextArea txtNarrative;
	
	// Mental Status components
	private Map<AssessmentOptionType, List<AssessmentOptionRadioButton>> mentalStatusRadioButtons;
	private Map<AssessmentOptionType, ButtonGroup> mentalStatusButtonGroups;
	private Map<AssessmentOptionType, JTextField> mentalStatusNoteFields;
	
	// Administrative Section components
	private List<AssessmentOptionCheckBox> referralCheckboxes;
	private List<AssessmentOptionCheckBox> collateralContactCheckboxes;
	private List<AssessmentOptionRadioButton> nextAppointmentRadioButtons;
	private ButtonGroup nextAppointmentButtonGroup;
	private JTextField txtReferralsNotes;
	private JTextField txtCollateralContactsNotes;
	private JTextField txtNextAppointmentNotes;
	private JCheckBox chkCertification;
	private JLabel lblCertificationTimestamp;
	private LocalDateTime certificationTimestamp;
	
	// Header panel components
	private JPanel headerPanel;
	private JLabel lblHeaderClientName;
	private JLabel lblHeaderAppointmentDate;
	
	// Action buttons
	private JButton btnSave;
	private JButton btnExportDocx;
	private JButton btnExportPdf;
	private JButton btnCancel;
	
	// Date formatter for certification timestamp
	private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern( "MM/dd/yyyy hh:mm:ss a" );
	
	private Integer noteId;
	
	/**
	 * Constructs a new Pnl_NewEditNote panel for creating a new progress note.
	 */
	public Pnl_NewEditNote()
	{
		initComponents();
		layoutComponents();
		setupListeners();
	}
	
	/**
	 * Initializes all UI components.
	 */
	private void initComponents()
	{
		// Header panel
		lblHeaderClientName = new JLabel();
		lblHeaderClientName.setFont( lblHeaderClientName.getFont().deriveFont( Font.BOLD, 14f ) );
		lblHeaderAppointmentDate = new JLabel();
		lblHeaderAppointmentDate.setFont( lblHeaderAppointmentDate.getFont().deriveFont( Font.BOLD, 14f ) );
		
		// Session Info
		cmbClient = new Cmb_ClientSelection( false );
		dateAppointment = new JDateChooser();
		dateAppointment.setDateFormatString( "MM/dd/yyyy" );
		if( PreferencesUtil.isDefaultAppointmentDateToday() )
			dateAppointment.setDate( new java.util.Date() ); // Default to current date
		else
			dateAppointment.setDate( null );
		dateAppointment.setMinSelectableDate( DateFormatUtil.toDate( LocalDateTime.now().minusYears( 50 ) ) );
		dateAppointment.setMaxSelectableDate( DateFormatUtil.toDate( LocalDateTime.now().plusYears( 1 ) ) );
		cmbDiagnosis = new Cmb_ICD10Diagnosis();
		cmbDiagnosis.setPreferredSize( new Dimension( 300, 25 ) );
		lblDateOfBirth = new JLabel();
		txtSessionNumber = new JTextField( 5 );
		applyNumericFilter( txtSessionNumber );
		txtLengthOfSession = new JTextField( 10 );
		txtAppointmentComment = new JTextField( 30 );
		chkVirtual = new JCheckBox();
		
		// Clinical Symptoms
		symptomCheckboxes = new ArrayList<>();
		loadSymptomCheckboxes();
		
		// Narrative
		txtNarrative = new JTextArea( 10, 50 );
		txtNarrative.setLineWrap( true );
		txtNarrative.setWrapStyleWord( true );
		
		// Mental Status
		mentalStatusRadioButtons = new HashMap<>();
		mentalStatusButtonGroups = new HashMap<>();
		mentalStatusNoteFields = new HashMap<>();
		loadMentalStatusOptions();
		
		// Administrative Section
		referralCheckboxes = new ArrayList<>();
		collateralContactCheckboxes = new ArrayList<>();
		nextAppointmentRadioButtons = new ArrayList<>();
		nextAppointmentButtonGroup = new ButtonGroup();
		txtReferralsNotes = new JTextField( 30 );
		txtCollateralContactsNotes = new JTextField( 30 );
		txtNextAppointmentNotes = new JTextField( 30 );
		loadAdministrativeOptions();
		
		chkCertification = new JCheckBox();
		lblCertificationTimestamp = new JLabel();
		lblCertificationTimestamp.setFont( lblCertificationTimestamp.getFont().deriveFont( Font.ITALIC ) );
		lblCertificationTimestamp.setForeground( new Color( 0, 100, 0 ) );
		lblCertificationTimestamp.setVisible( false );
		
		// Action buttons
		btnSave = new JButton( "Save" );
		btnExportDocx = new JButton( "Export to DOCX" );
		btnExportPdf = new JButton( "Export to PDF" );
		btnCancel = new JButton( "Cancel" );
	}
	
	/**
	 * Loads symptom checkboxes from the database.
	 */
	private void loadSymptomCheckboxes()
	{
		symptomCheckboxes.clear();
		try
		{
			List<AssessmentOption> symptoms = AppController.getAssessmentOptions( AssessmentOptionType.SYMPTOMS );
			for( AssessmentOption symptom : symptoms )
			{
				symptomCheckboxes.add( new AssessmentOptionCheckBox( symptom ) );
			}
		}
		catch( TherapyAppException e )
		{
			AppController.showBasicErrorPopup( e, "Error loading symptoms:" );
		}
	}
	
	/**
	 * Loads mental status options (Appearance, Speech, Affect, Eye Contact).
	 */
	private void loadMentalStatusOptions()
	{
		AssessmentOptionType[] mentalStatusTypes = { AssessmentOptionType.APPEARANCE, AssessmentOptionType.SPEECH,
				AssessmentOptionType.AFFECT, AssessmentOptionType.EYE_CONTACT };
		
		for( AssessmentOptionType type : mentalStatusTypes )
		{
			List<AssessmentOptionRadioButton> radioButtons = new ArrayList<>();
			ButtonGroup buttonGroup = new ButtonGroup();
			
			try
			{
				List<AssessmentOption> options = AppController.getAssessmentOptions( type );
				for( AssessmentOption option : options )
				{
					AssessmentOptionRadioButton radioButton = new AssessmentOptionRadioButton( option );
					buttonGroup.add( radioButton.getRadioButton() );
					radioButton.enableDeselection( buttonGroup );
					radioButtons.add( radioButton );
				}
			}
			catch( TherapyAppException e )
			{
				AppController.showBasicErrorPopup( e, "Error loading " + type.getName() + " options:" );
			}
			
			mentalStatusRadioButtons.put( type, radioButtons );
			mentalStatusButtonGroups.put( type, buttonGroup );
			mentalStatusNoteFields.put( type, new JTextField( 30 ) );
		}
	}
	
	/**
	 * Loads administrative section options (Referrals, Collateral Contacts, Next Appointment).
	 */
	private void loadAdministrativeOptions()
	{
		// Referrals checkboxes
		try
		{
			List<AssessmentOption> referrals = AppController.getAssessmentOptions( AssessmentOptionType.REFERRALS );
			for( AssessmentOption referral : referrals )
			{
				referralCheckboxes.add( new AssessmentOptionCheckBox( referral ) );
			}
		}
		catch( TherapyAppException e )
		{
			AppController.showBasicErrorPopup( "Error loading referrals: " + e.getMessage() );
		}
		
		// Collateral Contacts checkboxes
		try
		{
			List<AssessmentOption> collateralContacts = AppController.getAssessmentOptions( AssessmentOptionType.COLL_CONTACTS );
			for( AssessmentOption contact : collateralContacts )
			{
				collateralContactCheckboxes.add( new AssessmentOptionCheckBox( contact ) );
			}
		}
		catch( TherapyAppException e )
		{
			AppController.showBasicErrorPopup( "Error loading collateral contacts: " + e.getMessage() );
		}
		
		// Next Appointment radio buttons
		try
		{
			List<AssessmentOption> nextApptOptions = AppController.getAssessmentOptions( AssessmentOptionType.NEXT_APPT );
			for( AssessmentOption option : nextApptOptions )
			{
				AssessmentOptionRadioButton radioButton = new AssessmentOptionRadioButton( option );
				nextAppointmentButtonGroup.add( radioButton.getRadioButton() );
				radioButton.enableDeselection( nextAppointmentButtonGroup );
				nextAppointmentRadioButtons.add( radioButton );
			}
		}
		catch( TherapyAppException e )
		{
			AppController.showBasicErrorPopup( "Error loading next appointment options: " + e.getMessage() );
		}
	}
	
	/**
	 * Applies a numeric-only filter to a text field.
	 */
	private void applyNumericFilter( JTextField textField )
	{
		( (AbstractDocument) textField.getDocument() ).setDocumentFilter( new DocumentFilter()
		{
			@Override
			public void insertString( FilterBypass fb, int offset, String string, AttributeSet attr ) throws BadLocationException
			{
				if( string != null && string.matches( "\\d*" ) )
				{
					super.insertString( fb, offset, string, attr );
				}
			}
			
			@Override
			public void replace( FilterBypass fb, int offset, int length, String text, AttributeSet attrs ) throws BadLocationException
			{
				if( text != null && text.matches( "\\d*" ) )
				{
					super.replace( fb, offset, length, text, attrs );
				}
			}
		} );
	}
	
	/**
	 * Lays out all components on the panel.
	 */
	private void layoutComponents()
	{
		setLayout( new BorderLayout() );
		
		// Header panel - always visible
		headerPanel = createHeaderPanel();
		add( headerPanel, BorderLayout.NORTH );
		
		JPanel mainContentPanel = new JPanel();
		mainContentPanel.setLayout( new BoxLayout( mainContentPanel, BoxLayout.Y_AXIS ) );
		mainContentPanel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
		
		// Add sections
		mainContentPanel.add( createSessionInfoSection() );
		mainContentPanel.add( createSymptomsSection() );
		mainContentPanel.add( createNarrativeSection() );
		mainContentPanel.add( createMentalStatusSection() );
		mainContentPanel.add( createAdministrativeSection() );
		
		// Wrap in scroll pane with both vertical and horizontal scrolling
		JScrollPane scrollPane = new JScrollPane( mainContentPanel );
		scrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED );
		scrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
		scrollPane.getVerticalScrollBar().setUnitIncrement( 16 );
		scrollPane.getHorizontalScrollBar().setUnitIncrement( 16 );
		
		add( scrollPane, BorderLayout.CENTER );
		add( createButtonPanel(), BorderLayout.SOUTH );
	}
	
	/**
	 * Creates the header panel with client name and appointment date.
	 */
	private JPanel createHeaderPanel()
	{
		JPanel panel = new JPanel( new FlowLayout( FlowLayout.LEFT, 20, 10 ) );
		panel.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createMatteBorder( 0, 0, 1, 0, Color.GRAY ),
				BorderFactory.createEmptyBorder( 5, 10, 5, 10 ) ) );
		panel.setBackground( new Color( 245, 245, 245 ) );
		
		panel.add( new JLabel( "Client:" ) );
		panel.add( lblHeaderClientName );
		panel.add( new JLabel( "    " ) ); // Spacer
		panel.add( new JLabel( "Appointment Date:" ) );
		panel.add( lblHeaderAppointmentDate );
		
		return panel;
	}
	
	/**
	 * Creates the Session Info section panel.
	 */
	private JPanel createSessionInfoSection()
	{
		JPanel panel = new JPanel( new GridBagLayout() );
		panel.setBorder( createSectionBorder( "Session Information" ) );
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets( 5, 5, 5, 5 );
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		// Column 1 - Client, Date of Birth, Diagnosis
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.0;
		panel.add( new JLabel( "Client:" ), gbc );
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		panel.add( cmbClient, gbc );
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0.0;
		panel.add( new JLabel( "Date of Birth:" ), gbc );
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		lblDateOfBirth.setPreferredSize( new Dimension( 100, 20 ) );
		panel.add( lblDateOfBirth, gbc );
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 0.0;
		panel.add( new JLabel( "Diagnosis:" ), gbc );
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		panel.add( cmbDiagnosis, gbc );
		
		// Column 2 - Appointment Date, Session Number, Length of Session
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.weightx = 0.0;
		panel.add( new JLabel( "Appointment Date:" ), gbc );
		gbc.gridx = 3;
		gbc.weightx = 1.0;
		dateAppointment.setPreferredSize( new Dimension( 150, 25 ) );
		panel.add( dateAppointment, gbc );
		
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.weightx = 0.0;
		panel.add( new JLabel( "Session Number:" ), gbc );
		gbc.gridx = 3;
		gbc.weightx = 1.0;
		panel.add( txtSessionNumber, gbc );
		
		gbc.gridx = 2;
		gbc.gridy = 2;
		gbc.weightx = 0.0;
		panel.add( new JLabel( "Length of Session:" ), gbc );
		gbc.gridx = 3;
		gbc.weightx = 1.0;
		panel.add( txtLengthOfSession, gbc );
		
		// Column 3 - Appointment Type, Virtual
		gbc.gridx = 4;
		gbc.gridy = 0;
		gbc.weightx = 0.0;
		panel.add( new JLabel( "Appointment Comment:" ), gbc );
		gbc.gridx = 5;
		gbc.weightx = 1.0;
		panel.add( txtAppointmentComment, gbc );
		
		gbc.gridx = 4;
		gbc.gridy = 1;
		gbc.weightx = 0.0;
		panel.add( new JLabel( "Virtual Appointment:" ), gbc );
		gbc.gridx = 5;
		gbc.weightx = 1.0;
		chkVirtual.setSelected( PreferencesUtil.isDefaultVirtual() );
		panel.add( chkVirtual, gbc );
		
		return panel;
	}
	
	/**
	 * Creates the Clinical Symptoms section panel.
	 */
	private JPanel createSymptomsSection()
	{
		JPanel panel = new JPanel( new BorderLayout() );
		panel.setBorder( createSectionBorder( "Clinical Symptoms" ) );
		
		// Header label
		JLabel headerLabel = new JLabel( "Clinical symptoms present in the past week that demonstrate medical necessity:" );
		headerLabel.setBorder( BorderFactory.createEmptyBorder( 0, 5, 10, 5 ) );
		panel.add( headerLabel, BorderLayout.NORTH );
		
		// Checkboxes in 4 columns
		JPanel checkboxPanel = new JPanel( new GridBagLayout() );
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets( 2, 5, 2, 15 );
		
		int columns = 4;
		int row = 0;
		int col = 0;
		
		for( AssessmentOptionCheckBox checkbox : symptomCheckboxes )
		{
			gbc.gridx = col;
			gbc.gridy = row;
			gbc.weightx = 1.0 / columns;
			checkboxPanel.add( checkbox, gbc );
			
			col++;
			if( col >= columns )
			{
				col = 0;
				row++;
			}
		}
		
		panel.add( checkboxPanel, BorderLayout.CENTER );
		
		return panel;
	}
	
	/**
	 * Creates the Narrative section panel.
	 */
	private JPanel createNarrativeSection()
	{
		JPanel panel = new JPanel( new BorderLayout() );
		panel.setBorder( createSectionBorder( "Narrative" ) );
		
		JScrollPane scrollPane = new JScrollPane( txtNarrative );
		scrollPane.setPreferredSize( new Dimension( 0, 200 ) );
		panel.add( scrollPane, BorderLayout.CENTER );
		
		return panel;
	}
	
	/**
	 * Creates the Mental Status section panel.
	 */
	private JPanel createMentalStatusSection()
	{
		JPanel panel = new JPanel();
		panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
		panel.setBorder( createSectionBorder( "Mental Status" ) );
		
		// Create rows for each mental status type
		panel.add( createMentalStatusRow( "Appearance", AssessmentOptionType.APPEARANCE ) );
		panel.add( createMentalStatusRow( "Speech", AssessmentOptionType.SPEECH ) );
		panel.add( createMentalStatusRow( "Affect", AssessmentOptionType.AFFECT ) );
		panel.add( createMentalStatusRow( "Eye Contact", AssessmentOptionType.EYE_CONTACT ) );
		
		return panel;
	}
	
	/**
	 * Creates a single row for the Mental Status section.
	 */
	private JPanel createMentalStatusRow( String labelText, AssessmentOptionType type )
	{
		JPanel rowPanel = new JPanel( new GridBagLayout() );
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets( 5, 5, 5, 5 );
		gbc.gridy = 0;
		
		// Label
		gbc.gridx = 0;
		gbc.weightx = 0.0;
		JLabel label = new JLabel( labelText + ":" );
		label.setPreferredSize( new Dimension( 80, 20 ) );
		label.setFont( label.getFont().deriveFont( Font.BOLD ) );
		rowPanel.add( label, gbc );
		
		// Radio buttons
		int col = 1;
		List<AssessmentOptionRadioButton> radioButtons = mentalStatusRadioButtons.get( type );
		if( radioButtons != null )
		{
			for( AssessmentOptionRadioButton radioButton : radioButtons )
			{
				gbc.gridx = col++;
				gbc.weightx = 0.0;
				rowPanel.add( radioButton, gbc );
			}
		}
		
		// Notes field
		gbc.gridx = col;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		rowPanel.add( mentalStatusNoteFields.get( type ), gbc );
		
		return rowPanel;
	}
	
	/**
	 * Creates the Post-Session Administrative section panel.
	 */
	private JPanel createAdministrativeSection()
	{
		JPanel panel = new JPanel();
		panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
		panel.setBorder( createSectionBorder( "Post-Session Administrative" ) );
		
		// Referrals row
		panel.add( createAdminCheckboxRow( "Referrals", referralCheckboxes, txtReferralsNotes ) );
		
		// Collateral Contacts row
		panel.add( createAdminCheckboxRow( "Collateral Contacts", collateralContactCheckboxes, txtCollateralContactsNotes ) );
		
		// Next Appointment row
		panel.add( createAdminRadioRow( "Next Appointment", nextAppointmentRadioButtons, txtNextAppointmentNotes ) );
		
		// Certification section
		panel.add( createCertificationPanel() );
		
		return panel;
	}
	
	/**
	 * Creates an administrative row with checkboxes.
	 */
	private JPanel createAdminCheckboxRow( String labelText, List<AssessmentOptionCheckBox> checkboxes, JTextField commentsField )
	{
		JPanel rowPanel = new JPanel( new GridBagLayout() );
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets( 5, 5, 5, 5 );
		gbc.gridy = 0;
		
		// Label
		gbc.gridx = 0;
		gbc.weightx = 0.0;
		JLabel label = new JLabel( labelText + ":" );
		label.setPreferredSize( new Dimension( 120, 20 ) );
		label.setFont( label.getFont().deriveFont( Font.BOLD ) );
		rowPanel.add( label, gbc );
		
		// Checkboxes
		int col = 1;
		for( AssessmentOptionCheckBox checkbox : checkboxes )
		{
			gbc.gridx = col++;
			gbc.weightx = 0.0;
			rowPanel.add( checkbox, gbc );
		}
		
		// Comments field
		gbc.gridx = col;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		rowPanel.add( commentsField, gbc );
		
		return rowPanel;
	}
	
	/**
	 * Creates an administrative row with radio buttons.
	 */
	private JPanel createAdminRadioRow( String labelText, List<AssessmentOptionRadioButton> radioButtons, JTextField commentsField )
	{
		JPanel rowPanel = new JPanel( new GridBagLayout() );
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets( 5, 5, 5, 5 );
		gbc.gridy = 0;
		
		// Label
		gbc.gridx = 0;
		gbc.weightx = 0.0;
		JLabel label = new JLabel( labelText + ":" );
		label.setPreferredSize( new Dimension( 120, 20 ) );
		label.setFont( label.getFont().deriveFont( Font.BOLD ) );
		rowPanel.add( label, gbc );
		
		// Radio buttons
		int col = 1;
		for( AssessmentOptionRadioButton radioButton : radioButtons )
		{
			gbc.gridx = col++;
			gbc.weightx = 0.0;
			rowPanel.add( radioButton, gbc );
		}
		
		// Comments field
		gbc.gridx = col;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		rowPanel.add( commentsField, gbc );
		
		return rowPanel;
	}
	
	/**
	 * Creates the certification panel with checkbox and timestamp.
	 */
	private JPanel createCertificationPanel()
	{
		JPanel panel = new JPanel( new FlowLayout( FlowLayout.LEFT, 10, 10 ) );
		panel.setBorder( BorderFactory.createEmptyBorder( 10, 0, 0, 0 ) );
		
		panel.add( chkCertification );
		panel.add( new JLabel( "I certify that this information is accurate to the best of my knowledge." ) );
		panel.add( lblCertificationTimestamp );
		
		return panel;
	}
	
	/**
	 * Creates the button panel with Save, Export, and Cancel buttons.
	 */
	private JPanel createButtonPanel()
	{
		JPanel panel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 10, 10 ) );
		panel.setBorder( BorderFactory.createEmptyBorder( 5, 10, 10, 10 ) );
		
		panel.add( btnCancel );
		panel.add( btnSave );
		panel.add( btnExportDocx );
		panel.add( btnExportPdf );
		
		return panel;
	}
	
	/**
	 * Creates a titled border for sections.
	 */
	private TitledBorder createSectionBorder( String title )
	{
		TitledBorder border = BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), title );
		border.setTitleFont( border.getTitleFont().deriveFont( Font.BOLD, 14f ) );
		return border;
	}
	
	/**
	 * Sets up event listeners for components.
	 */
	private void setupListeners()
	{
		// Client selection listener - updates date of birth and header and defaults a session number and
		// diagnosis
		cmbClient.addActionListener( e ->
		{
			String selectedClientName = (String) cmbClient.getSelectedItem();
			if( selectedClientName != null )
			{
				lblHeaderClientName.setText( selectedClientName );
				Integer clientId = cmbClient.getSelectedClientId();
				if( clientId != null )
				{
					try
					{
						Client client = AppController.getClientById( clientId );
						if( client != null && client.getDateOfBirth() != null )
						{
							lblDateOfBirth
									.setText( DateFormatUtil.toLocalDateTime( DateFormatUtil.toSqliteString( client.getDateOfBirth() ) )
											.format( DateTimeFormatter.ofPattern( "MM/dd/yyyy" ) ) );
						}
						else
						{
							lblDateOfBirth.setText( "" );
						}
						
						if( PreferencesUtil.isDefaultSessionFromPrevious() )
						{
							Integer sessionNumber = AppController.getHighestUsedSessionNumberForClient( clientId );
							String sessionNumberDefault = sessionNumber == null ? "" : String.valueOf( sessionNumber + 1 );
							txtSessionNumber.setText( sessionNumberDefault );
						}
						
						if( PreferencesUtil.isDefaultDiagnosisFromPrevious() )
							cmbDiagnosis.setDiagnosis( AppController.getLastUsedDiagnosisForClient( clientId ) );
					}
					catch( TherapyAppException ex )
					{
						AppController.showBasicErrorPopup( ex, "Error loading client data:" );
						lblDateOfBirth.setText( "" );
					}
				}
			}
			else
			{
				lblHeaderClientName.setText( "" );
				lblDateOfBirth.setText( "" );
			}
		} );
		
		// Appointment date listener - updates header
		dateAppointment.getDateEditor().addPropertyChangeListener( "date", evt ->
		{
			if( dateAppointment.getDate() != null )
			{
				lblHeaderAppointmentDate.setText( DateFormatUtil.toSimpleString( dateAppointment.getDate() ) );
			}
			else
			{
				lblHeaderAppointmentDate.setText( "" );
			}
		} );
		
		// Initialize header with default date
		if( dateAppointment.getDate() != null )
		{
			lblHeaderAppointmentDate.setText( DateFormatUtil.toSimpleString( dateAppointment.getDate() ) );
		}
		
		// Certification checkbox listener
		chkCertification.addActionListener( e ->
		{
			if( chkCertification.isSelected() )
			{
				certificationTimestamp = LocalDateTime.now();
				lblCertificationTimestamp.setText( "Certified on: " + certificationTimestamp.format( TIMESTAMP_FORMATTER ) );
				lblCertificationTimestamp.setVisible( true );
			}
			else
			{
				certificationTimestamp = null;
				lblCertificationTimestamp.setVisible( false );
			}
		} );
		
		// Save button listener
		btnSave.addActionListener( e -> saveNote() );
		
		// Export to DOCX button listener
		btnExportDocx.addActionListener( e -> exportToDocx() );
		
		// Export to PDF button listener
		btnExportPdf.addActionListener( e -> exportToPdf() );
		
		// Cancel button listener
		btnCancel.addActionListener( e -> cancel() );
	}
	
	/**
	 * Loads an existing note for editing.
	 * 
	 * @param note The note to load
	 */
	public void loadNote( Note note )
	{
		if( note == null )
		{
			return;
		}
		
		noteId = note.getNoteId();
		
		// Session Info
		if( note.getClient() != null )
		{
			selectClientById( note.getClient().getClientId() );
		}
		if( note.getApptDateTime() != null )
		{
			dateAppointment.setDate( DateFormatUtil.toDate( note.getApptDateTime() ) );
			lblHeaderAppointmentDate.setText( DateFormatUtil.toSimpleString( DateFormatUtil.toDate( note.getApptDateTime() ) ) );
		}
		cmbDiagnosis.setDiagnosis( note.getDiagnosis() );
		txtSessionNumber.setText( note.getSessionNumber() != null ? String.valueOf( note.getSessionNumber() ) : "" );
		txtLengthOfSession.setText( note.getSessionLength() );
		txtAppointmentComment.setText( note.getApptComment() );
		chkVirtual.setSelected( note.isVirtualAppt() );
		
		// Clinical Symptoms
		List<Symptom> symptoms = note.getSymptoms();
		if( symptoms != null )
		{
			List<Integer> selectedSymptomIds = symptoms.stream().map( Symptom::getSymptomId ).toList();
			for( AssessmentOptionCheckBox checkbox : symptomCheckboxes )
			{
				checkbox.setSelected( selectedSymptomIds.contains( checkbox.getAssessmentOptionId() ) );
			}
		}
		
		// Narrative
		txtNarrative.setText( note.getNarrative() );
		
		// Mental Status
		loadMentalStatusFromNote( note );
		
		// Administrative
		loadAdministrativeFromNote( note );
		
		// Certification
		if( note.getCertifiedDate() != null )
		{
			chkCertification.setSelected( true );
			certificationTimestamp = note.getCertifiedDate();
			lblCertificationTimestamp.setText( "Certified on: " + certificationTimestamp.format( TIMESTAMP_FORMATTER ) );
			lblCertificationTimestamp.setVisible( true );
		}
	}
	
	/**
	 * Selects a client in the dropdown by ID.
	 */
	private void selectClientById( Integer clientId )
	{
		if( clientId == null )
		{
			cmbClient.setSelectedIndex( -1 );
			lblHeaderClientName.setText( "" );
			return;
		}
		
		cmbClient.selectByClientId( clientId );
		lblHeaderClientName.setText( (String) cmbClient.getSelectedItem() );
	}
	
	/**
	 * Loads mental status selections from a note.
	 */
	private void loadMentalStatusFromNote( Note note )
	{
		// Appearance
		if( note.getAppearance() != null )
		{
			selectRadioButton( mentalStatusRadioButtons.get( AssessmentOptionType.APPEARANCE ), note.getAppearance().getId() );
		}
		mentalStatusNoteFields.get( AssessmentOptionType.APPEARANCE ).setText( note.getAppearanceComment() );
		
		// Speech
		if( note.getSpeech() != null )
		{
			selectRadioButton( mentalStatusRadioButtons.get( AssessmentOptionType.SPEECH ), note.getSpeech().getId() );
		}
		mentalStatusNoteFields.get( AssessmentOptionType.SPEECH ).setText( note.getSpeechComment() );
		
		// Affect
		if( note.getAffect() != null )
		{
			selectRadioButton( mentalStatusRadioButtons.get( AssessmentOptionType.AFFECT ), note.getAffect().getId() );
		}
		mentalStatusNoteFields.get( AssessmentOptionType.AFFECT ).setText( note.getAffectComment() );
		
		// Eye Contact
		if( note.getEyeContact() != null )
		{
			selectRadioButton( mentalStatusRadioButtons.get( AssessmentOptionType.EYE_CONTACT ), note.getEyeContact().getId() );
		}
		mentalStatusNoteFields.get( AssessmentOptionType.EYE_CONTACT ).setText( note.getEyeContactComment() );
	}
	
	/**
	 * Loads administrative selections from a note.
	 */
	private void loadAdministrativeFromNote( Note note )
	{
		// Referrals
		List<Referral> referrals = note.getReferrals();
		if( referrals != null )
		{
			List<Integer> referralTypeIds = referrals.stream().map( Referral::getReferralTypeId ).toList();
			for( AssessmentOptionCheckBox checkbox : referralCheckboxes )
			{
				checkbox.setSelected( referralTypeIds.contains( checkbox.getAssessmentOptionId() ) );
			}
			txtReferralsNotes.setText( note.getReferralComment() );
		}
		
		// Collateral Contacts
		List<CollateralContact> collateralContacts = note.getCollateralContacts();
		if( collateralContacts != null )
		{
			List<Integer> collateralContactTypeIds = collateralContacts.stream().map( CollateralContact::getCollateralContactTypeId )
					.toList();
			for( AssessmentOptionCheckBox checkbox : collateralContactCheckboxes )
			{
				checkbox.setSelected( collateralContactTypeIds.contains( checkbox.getAssessmentOptionId() ) );
			}
			txtCollateralContactsNotes.setText( note.getCollateralContactComment() );
		}
		
		// Next Appointment
		if( note.getNextAppt() != null )
		{
			selectRadioButton( nextAppointmentRadioButtons, note.getNextAppt().getId() );
		}
		txtNextAppointmentNotes.setText( note.getNextApptComment() );
	}
	
	/**
	 * Selects a radio button by option ID.
	 */
	private void selectRadioButton( List<AssessmentOptionRadioButton> radioButtons, Integer optionId )
	{
		if( radioButtons == null || optionId == null )
		{
			return;
		}
		
		for( AssessmentOptionRadioButton radioButton : radioButtons )
		{
			if( radioButton.getAssessmentOptionId().equals( optionId ) )
			{
				radioButton.setSelected( true );
				return;
			}
		}
	}
	
	/**
	 * Saves the note.
	 */
	private void saveNote()
	{
		Note note = collectNoteData();
		if( _isNoteValid( note ) )
		{
			try
			{
				AppController.saveNote( note );
				JOptionPane.showMessageDialog( this, "Note saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE );
				clearForm();
			}
			catch( TherapyAppException e )
			{
				AppController.showBasicErrorPopup( e, "Error saving note:" );
			}
		}
	}
	
	/**
	 * Exports the note to a DOCX file.
	 */
	private void exportToDocx()
	{
		Note note = collectNoteData();
		if( _isNoteValid( note ) )
		{
			try
			{
				AppController.saveNote( note );
				String outputPath = Exporter.exportToDocx( note );
				if( !JavaUtils.isNullOrEmpty( outputPath ) )
				{
					JOptionPane.showMessageDialog( this, "Note exported successfully!\n" + outputPath, "Export Success",
							JOptionPane.INFORMATION_MESSAGE );
				}
			}
			catch( TherapyAppException e )
			{
				AppController.showBasicErrorPopup( e, "Error exporting note to DOCX:" );
			}
		}
	}
	
	/**
	 * Exports the note to a PDF file.
	 */
	private void exportToPdf()
	{
		Note note = collectNoteData();
		if( _isNoteValid( note ) )
		{
			try
			{
				AppController.saveNote( note );
				String outputPath = Exporter.exportToPdf( note );
				if( !JavaUtils.isNullOrEmpty( outputPath ) )
				{
					JOptionPane.showMessageDialog( this, "Note exported successfully!\n" + outputPath, "Export Success",
							JOptionPane.INFORMATION_MESSAGE );
				}
			}
			catch( TherapyAppException e )
			{
				AppController.showBasicErrorPopup( e, "Error exporting note to PDF:" );
			}
		}
	}
	
	private boolean _isNoteValid( Note note )
	{
		Date chosenDate = dateAppointment.getDate();
		if( chosenDate != null && ( chosenDate.after( DateFormatUtil.toDate( LocalDateTime.now().plusYears( 1 ) ) )
				|| chosenDate.before( DateFormatUtil.toDate( LocalDateTime.now().minusYears( 50 ) ) ) ) )
		{
			JOptionPane.showMessageDialog( this, "Appointment date is invalid.", "Validation Error", JOptionPane.ERROR_MESSAGE );
			return false;
		}
		
		return true;
	}
	
	/**
	 * Collects all data from the form into a Note object.
	 * 
	 * @return The populated Note object
	 */
	public Note collectNoteData()
	{
		Note note = new Note();
		
		note.setNoteId( noteId );
		
		// Session Info
		Integer clientId = cmbClient.getSelectedClientId();
		if( clientId != null )
		{
			try
			{
				Client client = AppController.getClientById( clientId );
				note.setClient( client );
			}
			catch( TherapyAppException e )
			{
				AppController.showBasicErrorPopup( e, "Error loading client:" );
			}
		}
		
		if( dateAppointment.getDate() != null )
		{
			note.setApptDateTime( dateAppointment.getDate().toInstant().atZone( java.time.ZoneId.systemDefault() ).toLocalDateTime() );
		}
		
		note.setDiagnosis( cmbDiagnosis.getDiagnosis() );
		
		String sessionNumText = txtSessionNumber.getText();
		if( !sessionNumText.isEmpty() )
		{
			note.setSessionNumber( Integer.parseInt( sessionNumText ) );
		}
		
		note.setSessionLength( txtLengthOfSession.getText() );
		note.setApptComment( txtAppointmentComment.getText() );
		note.setVirtualAppt( chkVirtual.isSelected() );
		
		// Clinical Symptoms
		List<Symptom> symptoms = new ArrayList<>();
		for( AssessmentOptionCheckBox checkbox : symptomCheckboxes )
		{
			if( checkbox.isSelected() )
			{
				Symptom symptom = new Symptom();
				symptom.setSymptomId( checkbox.getAssessmentOptionId() );
				symptom.setSymptomName( checkbox.getAssessmentOptionName() );
				symptom.setSymptomDescription( checkbox.getAssessmentOption().getDescription() );
				symptoms.add( symptom );
			}
		}
		note.setSymptoms( symptoms );
		
		// Narrative
		note.setNarrative( txtNarrative.getText() );
		
		// Mental Status
		try
		{
			Integer appearanceId = getSelectedRadioButtonId( mentalStatusRadioButtons.get( AssessmentOptionType.APPEARANCE ) );
			if( appearanceId != null )
			{
				note.setAppearance( (AppearanceAssessmentOption) AppController.getAssessmentOptionById( appearanceId ) );
			}
			note.setAppearanceComment( mentalStatusNoteFields.get( AssessmentOptionType.APPEARANCE ).getText() );
			
			Integer speechId = getSelectedRadioButtonId( mentalStatusRadioButtons.get( AssessmentOptionType.SPEECH ) );
			if( speechId != null )
			{
				note.setSpeech( (SpeechAssessmentOption) AppController.getAssessmentOptionById( speechId ) );
			}
			note.setSpeechComment( mentalStatusNoteFields.get( AssessmentOptionType.SPEECH ).getText() );
			
			Integer affectId = getSelectedRadioButtonId( mentalStatusRadioButtons.get( AssessmentOptionType.AFFECT ) );
			if( affectId != null )
			{
				note.setAffect( (AffectAssessmentOption) AppController.getAssessmentOptionById( affectId ) );
			}
			note.setAffectComment( mentalStatusNoteFields.get( AssessmentOptionType.AFFECT ).getText() );
			
			Integer eyeContactId = getSelectedRadioButtonId( mentalStatusRadioButtons.get( AssessmentOptionType.EYE_CONTACT ) );
			if( eyeContactId != null )
			{
				note.setEyeContact( (EyeContactAssessmentOption) AppController.getAssessmentOptionById( eyeContactId ) );
			}
			note.setEyeContactComment( mentalStatusNoteFields.get( AssessmentOptionType.EYE_CONTACT ).getText() );
		}
		catch( TherapyAppException e )
		{
			AppController.showBasicErrorPopup( e, "Error loading mental status options:" );
		}
		
		// Administrative
		try
		{
			List<Referral> referrals = new ArrayList<>();
			for( AssessmentOptionCheckBox checkbox : referralCheckboxes )
			{
				if( checkbox.isSelected() )
				{
					Referral referral = new Referral();
					referral.setReferralTypeId( checkbox.getAssessmentOptionId() );
					referral.setReferralName( checkbox.getAssessmentOptionName() );
					referrals.add( referral );
				}
			}
			note.setReferrals( referrals );
			note.setReferralComment( txtReferralsNotes.getText() );
			
			List<CollateralContact> collateralContacts = new ArrayList<>();
			for( AssessmentOptionCheckBox checkbox : collateralContactCheckboxes )
			{
				if( checkbox.isSelected() )
				{
					CollateralContact collateralContact = new CollateralContact();
					collateralContact.setCollateralContactTypeId( checkbox.getAssessmentOptionId() );
					collateralContact.setCollateralContactName( checkbox.getAssessmentOptionName() );
					collateralContacts.add( collateralContact );
				}
			}
			note.setCollateralContacts( collateralContacts );
			note.setCollateralContactComment( txtCollateralContactsNotes.getText() );
			
			Integer nextApptId = getSelectedRadioButtonId( nextAppointmentRadioButtons );
			if( nextApptId != null )
			{
				note.setNextAppt( (NextApptAssessmentOption) AppController.getAssessmentOptionById( nextApptId ) );
			}
			note.setNextApptComment( txtNextAppointmentNotes.getText() );
		}
		catch( TherapyAppException e )
		{
			AppController.showBasicErrorPopup( e, "Error loading administrative options:" );
		}
		
		// Certification
		note.setCertifiedDate( certificationTimestamp );
		
		return note;
	}
	
	/**
	 * Gets the selected option ID from a list of radio buttons.
	 */
	private Integer getSelectedRadioButtonId( List<AssessmentOptionRadioButton> radioButtons )
	{
		if( radioButtons == null )
		{
			return null;
		}
		
		for( AssessmentOptionRadioButton radioButton : radioButtons )
		{
			if( radioButton.isSelected() )
			{
				return radioButton.getAssessmentOptionId();
			}
		}
		return null;
	}
	
	/**
	 * Clears all form fields for a new note.
	 */
	public void clearForm()
	{
		// Header
		lblHeaderClientName.setText( "" );
		lblHeaderAppointmentDate.setText( DateFormatUtil.toSimpleString( dateAppointment.getDate() ) );
		
		// Session Info
		cmbClient.setSelectedIndex( -1 );
		if( PreferencesUtil.isDefaultAppointmentDateToday() )
			dateAppointment.setDate( new java.util.Date() ); // Default to current date
		else
			dateAppointment.setDate( null );
		cmbDiagnosis.clear();
		lblDateOfBirth.setText( "" );
		txtSessionNumber.setText( "" );
		txtLengthOfSession.setText( "" );
		txtAppointmentComment.setText( "" );
		chkVirtual.setSelected( PreferencesUtil.isDefaultVirtual() );
		
		// Clinical Symptoms
		for( AssessmentOptionCheckBox checkbox : symptomCheckboxes )
		{
			checkbox.setSelected( false );
		}
		
		// Narrative
		txtNarrative.setText( "" );
		
		// Mental Status
		for( ButtonGroup group : mentalStatusButtonGroups.values() )
		{
			group.clearSelection();
		}
		for( JTextField field : mentalStatusNoteFields.values() )
		{
			field.setText( "" );
		}
		
		// Administrative
		for( AssessmentOptionCheckBox checkbox : referralCheckboxes )
		{
			checkbox.setSelected( false );
		}
		for( AssessmentOptionCheckBox checkbox : collateralContactCheckboxes )
		{
			checkbox.setSelected( false );
		}
		nextAppointmentButtonGroup.clearSelection();
		txtReferralsNotes.setText( "" );
		txtCollateralContactsNotes.setText( "" );
		txtNextAppointmentNotes.setText( "" );
		
		// Certification
		chkCertification.setSelected( false );
		certificationTimestamp = null;
		lblCertificationTimestamp.setVisible( false );
	}
	
	// Getters for Save and Cancel buttons to allow external action binding
	
	public JButton getSaveButton()
	{
		return btnSave;
	}
	
	public JButton getCancelButton()
	{
		return btnCancel;
	}
	
	public JButton getExportDocxButton()
	{
		return btnExportDocx;
	}
	
	public JButton getExportPdfButton()
	{
		return btnExportPdf;
	}
	
	/**
	 * Cancels the note editing and returns to the home screen.
	 */
	private void cancel()
	{
		int result = JOptionPane.showConfirmDialog( this, "Are you sure you want to cancel? Any unsaved changes will be lost.",
				"Cancel Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE );
		
		if( result == JOptionPane.YES_OPTION )
		{
			clearForm();
			AppController.returnHome( true );
		}
	}
}