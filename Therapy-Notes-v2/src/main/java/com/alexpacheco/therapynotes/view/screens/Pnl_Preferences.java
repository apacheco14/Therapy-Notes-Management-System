package com.alexpacheco.therapynotes.view.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import com.alexpacheco.therapynotes.controller.AppController;
import com.alexpacheco.therapynotes.controller.enums.PreferenceKey;
import com.alexpacheco.therapynotes.controller.enums.PreferenceType;
import com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.entities.Preference;
import com.alexpacheco.therapynotes.util.AppLogger;
import com.alexpacheco.therapynotes.util.PreferencesUtil;

/**
 * Panel for editing user preferences. Organizes preferences into logical sections with appropriate input controls.
 */
public class Pnl_Preferences extends JPanel
{
	private static final long serialVersionUID = 1774644048730878775L;
	
	// Fonts
	private static final Font TITLE_FONT = new Font( "SansSerif", Font.BOLD, 24 );
	private static final Font SECTION_FONT = new Font( "SansSerif", Font.BOLD, 14 );
	private static final Font SUBSECTION_FONT = new Font( "SansSerif", Font.BOLD, 12 );
	
	// UI Components - Default Behavior
	private JCheckBox chkDefaultSessionFromPrevious;
	private JCheckBox chkDefaultDiagnosisFromPrevious;
	private JComboBox<String> cboDefaultAppointmentDate;
	private JCheckBox chkDefaultVirtual;
	
	// UI Components - Required Fields: Note
	private JCheckBox chkRequiredNoteDiagnosis;
	private JCheckBox chkRequiredNoteNarrative;
	private JCheckBox chkRequiredNoteAffect;
	private JCheckBox chkRequiredNoteAppearance;
	private JCheckBox chkRequiredNoteSpeech;
	private JCheckBox chkRequiredNoteEyeContact;
	private JCheckBox chkRequiredNoteNextAppointment;
	private JCheckBox chkRequiredNoteCollateralContacts;
	private JCheckBox chkRequiredNoteReferrals;
	private JCheckBox chkRequiredNoteSymptoms;
	
	// UI Components - Required Fields: Client
	private JCheckBox chkRequiredClientFirstName;
	private JCheckBox chkRequiredClientLastName;
	private JCheckBox chkRequiredClientDOB;
	
	// UI Components - Required Fields: Contact
	private JCheckBox chkRequiredContactFirstName;
	private JCheckBox chkRequiredContactLastName;
	
	// Buttons
	private JButton btnSave;
	private JButton btnCancel;
	private JButton btnResetToDefaults;
	
	// Track original values for cancel functionality
	private Map<PreferenceKey, Object> originalValues;
	
	public Pnl_Preferences()
	{
		this.originalValues = new HashMap<>();
		
		initComponents();
		layoutComponents();
		loadPreferences();
	}
	
	private void initComponents()
	{
		// Default Behavior components
		chkDefaultSessionFromPrevious = new JCheckBox( "Default session number from previous note" );
		chkDefaultDiagnosisFromPrevious = new JCheckBox( "Default diagnosis from previous note" );
		cboDefaultAppointmentDate = new JComboBox<>( new String[] { "Today", "None" } );
		chkDefaultVirtual = new JCheckBox( "Default to virtual/telehealth session" );
		
		// Required Fields: Note components
		chkRequiredNoteDiagnosis = new JCheckBox( "Diagnosis" );
		chkRequiredNoteNarrative = new JCheckBox( "Narrative" );
		chkRequiredNoteAffect = new JCheckBox( "Affect" );
		chkRequiredNoteAppearance = new JCheckBox( "Appearance" );
		chkRequiredNoteSpeech = new JCheckBox( "Speech" );
		chkRequiredNoteEyeContact = new JCheckBox( "Eye Contact" );
		chkRequiredNoteNextAppointment = new JCheckBox( "Next Appointment" );
		chkRequiredNoteCollateralContacts = new JCheckBox( "Collateral Contacts" );
		chkRequiredNoteReferrals = new JCheckBox( "Referrals" );
		chkRequiredNoteSymptoms = new JCheckBox( "Symptoms" );
		
		// Required Fields: Client components
		chkRequiredClientFirstName = new JCheckBox( "First Name" );
		chkRequiredClientLastName = new JCheckBox( "Last Name" );
		chkRequiredClientDOB = new JCheckBox( "Date of Birth" );
		
		// Required Fields: Contact components
		chkRequiredContactFirstName = new JCheckBox( "First Name" );
		chkRequiredContactLastName = new JCheckBox( "Last Name" );
		
		// Buttons
		btnSave = new JButton( "Save" );
		btnCancel = new JButton( "Cancel" );
		btnResetToDefaults = new JButton( "Reset to Defaults" );
		
		// Button actions
		btnSave.addActionListener( e -> savePreferences() );
		btnCancel.addActionListener( e -> AppController.returnHome( !hasUnsavedChanges() ) );
		btnResetToDefaults.addActionListener( e -> resetToDefaults() );
	}
	
	private void layoutComponents()
	{
		setLayout( new BorderLayout( 10, 10 ) );
		setBorder( BorderFactory.createEmptyBorder( 15, 20, 15, 20 ) );
		
		// Header panel with title
		JPanel headerPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 0 ) );
		JLabel lblTitle = new JLabel( "Preferences", SwingConstants.CENTER );
		lblTitle.setFont( TITLE_FONT );
		headerPanel.add( lblTitle );
		
		// Main content panel
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout( new BoxLayout( contentPanel, BoxLayout.Y_AXIS ) );
		
		contentPanel.add( Box.createVerticalStrut( 15 ) );
		contentPanel.add( createDefaultBehaviorSection() );
		contentPanel.add( Box.createVerticalStrut( 20 ) );
		contentPanel.add( createSeparator() );
		contentPanel.add( Box.createVerticalStrut( 20 ) );
		contentPanel.add( createRequiredFieldsSection() );
		contentPanel.add( Box.createVerticalGlue() );
		
		// Wrap content in a left-aligned panel
		JPanel contentWrapper = new JPanel( new BorderLayout() );
		contentWrapper.add( contentPanel, BorderLayout.NORTH );
		
		// Scroll pane
		JScrollPane scrollPane = new JScrollPane( contentWrapper );
		scrollPane.setBorder( null );
		scrollPane.getVerticalScrollBar().setUnitIncrement( 16 );
		
		// Button panel
		JPanel buttonPanel = createButtonPanel();
		
		add( headerPanel, BorderLayout.NORTH );
		add( scrollPane, BorderLayout.CENTER );
		add( buttonPanel, BorderLayout.SOUTH );
	}
	
	private JSeparator createSeparator()
	{
		JSeparator separator = new JSeparator();
		separator.setForeground( Color.LIGHT_GRAY );
		return separator;
	}
	
	private JLabel createSectionLabel( String text )
	{
		JLabel label = new JLabel( text );
		label.setFont( SECTION_FONT );
		return label;
	}
	
	private JLabel createSubsectionLabel( String text )
	{
		JLabel label = new JLabel( text );
		label.setFont( SUBSECTION_FONT );
		return label;
	}
	
	private JPanel createDefaultBehaviorSection()
	{
		JPanel panel = new JPanel( new GridBagLayout() );
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets( 4, 0, 4, 10 );
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		
		// Section header
		panel.add( createSectionLabel( "Default Behavior" ), gbc );
		
		gbc.gridy++;
		gbc.insets = new Insets( 8, 20, 4, 10 );
		panel.add( chkDefaultSessionFromPrevious, gbc );
		
		gbc.gridy++;
		gbc.insets = new Insets( 4, 20, 4, 10 );
		panel.add( chkDefaultDiagnosisFromPrevious, gbc );
		
		gbc.gridy++;
		gbc.gridwidth = 1;
		panel.add( new JLabel( "Default appointment date:" ), gbc );
		
		gbc.gridx = 1;
		panel.add( cboDefaultAppointmentDate, gbc );
		
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		panel.add( chkDefaultVirtual, gbc );
		
		// Push everything to the left
		gbc.gridx = 2;
		gbc.weightx = 1.0;
		panel.add( Box.createHorizontalGlue(), gbc );
		
		return panel;
	}
	
	private JPanel createRequiredFieldsSection()
	{
		JPanel panel = new JPanel( new GridBagLayout() );
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets( 4, 0, 4, 10 );
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 3;
		
		// Section header
		panel.add( createSectionLabel( "Required Fields" ), gbc );
		
		// Subsections in columns
		gbc.gridy++;
		gbc.gridwidth = 1;
		gbc.insets = new Insets( 12, 20, 4, 40 );
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.NONE;
		
		panel.add( createNoteRequiredFieldsPanel(), gbc );
		
		gbc.gridx = 1;
		panel.add( createClientRequiredFieldsPanel(), gbc );
		
		gbc.gridx = 2;
		panel.add( createContactRequiredFieldsPanel(), gbc );
		
		// Push everything to the left
		gbc.gridx = 3;
		gbc.weightx = 1.0;
		panel.add( Box.createHorizontalGlue(), gbc );
		
		return panel;
	}
	
	private JPanel createNoteRequiredFieldsPanel()
	{
		JPanel panel = new JPanel( new GridBagLayout() );
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets( 2, 0, 2, 0 );
		gbc.gridx = 0;
		gbc.gridy = 0;
		
		panel.add( createSubsectionLabel( "New/Edit Note" ), gbc );
		
		gbc.gridy++;
		gbc.insets = new Insets( 6, 15, 2, 0 );
		panel.add( chkRequiredNoteDiagnosis, gbc );
		
		gbc.gridy++;
		gbc.insets = new Insets( 2, 15, 2, 0 );
		panel.add( chkRequiredNoteNarrative, gbc );
		
		gbc.gridy++;
		panel.add( chkRequiredNoteAffect, gbc );
		
		gbc.gridy++;
		panel.add( chkRequiredNoteAppearance, gbc );
		
		gbc.gridy++;
		panel.add( chkRequiredNoteSpeech, gbc );
		
		gbc.gridy++;
		panel.add( chkRequiredNoteEyeContact, gbc );
		
		gbc.gridy++;
		panel.add( chkRequiredNoteNextAppointment, gbc );
		
		gbc.gridy++;
		panel.add( chkRequiredNoteCollateralContacts, gbc );
		
		gbc.gridy++;
		panel.add( chkRequiredNoteReferrals, gbc );
		
		gbc.gridy++;
		panel.add( chkRequiredNoteSymptoms, gbc );
		
		return panel;
	}
	
	private JPanel createClientRequiredFieldsPanel()
	{
		JPanel panel = new JPanel( new GridBagLayout() );
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets( 2, 0, 2, 0 );
		gbc.gridx = 0;
		gbc.gridy = 0;
		
		panel.add( createSubsectionLabel( "New/Edit Client" ), gbc );
		
		gbc.gridy++;
		gbc.insets = new Insets( 6, 15, 2, 0 );
		panel.add( chkRequiredClientFirstName, gbc );
		
		gbc.gridy++;
		gbc.insets = new Insets( 2, 15, 2, 0 );
		panel.add( chkRequiredClientLastName, gbc );
		
		gbc.gridy++;
		panel.add( chkRequiredClientDOB, gbc );
		
		return panel;
	}
	
	private JPanel createContactRequiredFieldsPanel()
	{
		JPanel panel = new JPanel( new GridBagLayout() );
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets( 2, 0, 2, 0 );
		gbc.gridx = 0;
		gbc.gridy = 0;
		
		panel.add( createSubsectionLabel( "New/Edit Contact" ), gbc );
		
		gbc.gridy++;
		gbc.insets = new Insets( 6, 15, 2, 0 );
		panel.add( chkRequiredContactFirstName, gbc );
		
		gbc.gridy++;
		gbc.insets = new Insets( 2, 15, 2, 0 );
		panel.add( chkRequiredContactLastName, gbc );
		
		return panel;
	}
	
	private JPanel createButtonPanel()
	{
		JPanel panel = new JPanel( new BorderLayout() );
		panel.setBorder( BorderFactory.createEmptyBorder( 10, 0, 0, 0 ) );
		
		// Reset button on the left
		JPanel leftPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
		leftPanel.add( btnResetToDefaults );
		
		// Save and Cancel on the right
		JPanel rightPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 0 ) );
		rightPanel.add( btnCancel );
		rightPanel.add( btnSave );
		
		panel.add( leftPanel, BorderLayout.WEST );
		panel.add( rightPanel, BorderLayout.EAST );
		
		return panel;
	}
	
	private void loadPreferences()
	{
		// Load Default Behavior
		chkDefaultSessionFromPrevious.setSelected( PreferencesUtil.isDefaultSessionFromPrevious() );
		chkDefaultDiagnosisFromPrevious.setSelected( PreferencesUtil.isDefaultDiagnosisFromPrevious() );
		cboDefaultAppointmentDate
				.setSelectedItem( "today".equalsIgnoreCase( PreferencesUtil.getDefaultAppointmentDate() ) ? "Today" : "None" );
		chkDefaultVirtual.setSelected( PreferencesUtil.isDefaultVirtual() );
		
		// Load Required Fields: Note
		chkRequiredNoteDiagnosis.setSelected( PreferencesUtil.isNoteDiagnosisRequired() );
		chkRequiredNoteNarrative.setSelected( PreferencesUtil.isNoteNarrativeRequired() );
		chkRequiredNoteAffect.setSelected( PreferencesUtil.isNoteAffectRequired() );
		chkRequiredNoteAppearance.setSelected( PreferencesUtil.isNoteAppearanceRequired() );
		chkRequiredNoteSpeech.setSelected( PreferencesUtil.isNoteSpeechRequired() );
		chkRequiredNoteEyeContact.setSelected( PreferencesUtil.isNoteEyeContactRequired() );
		chkRequiredNoteNextAppointment.setSelected( PreferencesUtil.isNoteNextAppointmentRequired() );
		chkRequiredNoteCollateralContacts.setSelected( PreferencesUtil.isNoteCollateralContactsRequired() );
		chkRequiredNoteReferrals.setSelected( PreferencesUtil.isNoteReferralsRequired() );
		chkRequiredNoteSymptoms.setSelected( PreferencesUtil.isNoteSymptomsRequired() );
		
		// Load Required Fields: Client
		chkRequiredClientFirstName.setSelected( PreferencesUtil.isClientFirstNameRequired() );
		chkRequiredClientLastName.setSelected( PreferencesUtil.isClientLastNameRequired() );
		chkRequiredClientDOB.setSelected( PreferencesUtil.isClientDOBRequired() );
		
		// Load Required Fields: Contact
		chkRequiredContactFirstName.setSelected( PreferencesUtil.isContactFirstNameRequired() );
		chkRequiredContactLastName.setSelected( PreferencesUtil.isContactLastNameRequired() );
		
		// Store original values
		storeOriginalValues();
	}
	
	private void storeOriginalValues()
	{
		originalValues.clear();
		
		// Default Behavior
		originalValues.put( PreferenceKey.DEFAULT_SESSION_FROM_PREVIOUS, chkDefaultSessionFromPrevious.isSelected() );
		originalValues.put( PreferenceKey.DEFAULT_DIAGNOSIS_FROM_PREVIOUS, chkDefaultDiagnosisFromPrevious.isSelected() );
		originalValues.put( PreferenceKey.DEFAULT_APPOINTMENT_DATE, cboDefaultAppointmentDate.getSelectedItem() );
		originalValues.put( PreferenceKey.DEFAULT_VIRTUAL, chkDefaultVirtual.isSelected() );
		
		// Required Fields: Note
		originalValues.put( PreferenceKey.REQUIRED_NOTE_DIAGNOSIS, chkRequiredNoteDiagnosis.isSelected() );
		originalValues.put( PreferenceKey.REQUIRED_NOTE_NARRATIVE, chkRequiredNoteNarrative.isSelected() );
		originalValues.put( PreferenceKey.REQUIRED_NOTE_AFFECT, chkRequiredNoteAffect.isSelected() );
		originalValues.put( PreferenceKey.REQUIRED_NOTE_APPEARANCE, chkRequiredNoteAppearance.isSelected() );
		originalValues.put( PreferenceKey.REQUIRED_NOTE_SPEECH, chkRequiredNoteSpeech.isSelected() );
		originalValues.put( PreferenceKey.REQUIRED_NOTE_EYE_CONTACT, chkRequiredNoteEyeContact.isSelected() );
		originalValues.put( PreferenceKey.REQUIRED_NOTE_NEXT_APPOINTMENT, chkRequiredNoteNextAppointment.isSelected() );
		originalValues.put( PreferenceKey.REQUIRED_NOTE_COLLATERAL_CONTACTS, chkRequiredNoteCollateralContacts.isSelected() );
		originalValues.put( PreferenceKey.REQUIRED_NOTE_REFERRALS, chkRequiredNoteReferrals.isSelected() );
		originalValues.put( PreferenceKey.REQUIRED_NOTE_SYMPTOMS, chkRequiredNoteSymptoms.isSelected() );
		
		// Required Fields: Client
		originalValues.put( PreferenceKey.REQUIRED_CLIENT_FIRST_NAME, chkRequiredClientFirstName.isSelected() );
		originalValues.put( PreferenceKey.REQUIRED_CLIENT_LAST_NAME, chkRequiredClientLastName.isSelected() );
		originalValues.put( PreferenceKey.REQUIRED_CLIENT_DOB, chkRequiredClientDOB.isSelected() );
		
		// Required Fields: Contact
		originalValues.put( PreferenceKey.REQUIRED_CONTACT_FIRST_NAME, chkRequiredContactFirstName.isSelected() );
		originalValues.put( PreferenceKey.REQUIRED_CONTACT_LAST_NAME, chkRequiredContactLastName.isSelected() );
	}
	
	private void savePreferences()
	{
		try
		{
			List<Preference> preferencesToSave = new ArrayList<>();
			
			// Default Behavior
			preferencesToSave.add( PreferencesUtil.createPreferenceFromKey( PreferenceKey.DEFAULT_SESSION_FROM_PREVIOUS,
					String.valueOf( chkDefaultSessionFromPrevious.isSelected() ) ) );
			
			preferencesToSave.add( PreferencesUtil.createPreferenceFromKey( PreferenceKey.DEFAULT_DIAGNOSIS_FROM_PREVIOUS,
					String.valueOf( chkDefaultDiagnosisFromPrevious.isSelected() ) ) );
			
			preferencesToSave.add( PreferencesUtil.createPreferenceFromKey( PreferenceKey.DEFAULT_APPOINTMENT_DATE,
					( (String) cboDefaultAppointmentDate.getSelectedItem() ).toLowerCase() ) );
			
			preferencesToSave.add( PreferencesUtil.createPreferenceFromKey( PreferenceKey.DEFAULT_VIRTUAL,
					String.valueOf( chkDefaultVirtual.isSelected() ) ) );
			
			// Required Fields: Note
			preferencesToSave.add( PreferencesUtil.createPreferenceFromKey( PreferenceKey.REQUIRED_NOTE_DIAGNOSIS,
					String.valueOf( chkRequiredNoteDiagnosis.isSelected() ) ) );
			
			preferencesToSave.add( PreferencesUtil.createPreferenceFromKey( PreferenceKey.REQUIRED_NOTE_NARRATIVE,
					String.valueOf( chkRequiredNoteNarrative.isSelected() ) ) );
			
			preferencesToSave.add( PreferencesUtil.createPreferenceFromKey( PreferenceKey.REQUIRED_NOTE_AFFECT,
					String.valueOf( chkRequiredNoteAffect.isSelected() ) ) );
			
			preferencesToSave.add( PreferencesUtil.createPreferenceFromKey( PreferenceKey.REQUIRED_NOTE_APPEARANCE,
					String.valueOf( chkRequiredNoteAppearance.isSelected() ) ) );
			
			preferencesToSave.add( PreferencesUtil.createPreferenceFromKey( PreferenceKey.REQUIRED_NOTE_SPEECH,
					String.valueOf( chkRequiredNoteSpeech.isSelected() ) ) );
			
			preferencesToSave.add( PreferencesUtil.createPreferenceFromKey( PreferenceKey.REQUIRED_NOTE_EYE_CONTACT,
					String.valueOf( chkRequiredNoteEyeContact.isSelected() ) ) );
			
			preferencesToSave.add( PreferencesUtil.createPreferenceFromKey( PreferenceKey.REQUIRED_NOTE_NEXT_APPOINTMENT,
					String.valueOf( chkRequiredNoteNextAppointment.isSelected() ) ) );
			
			preferencesToSave.add( PreferencesUtil.createPreferenceFromKey( PreferenceKey.REQUIRED_NOTE_COLLATERAL_CONTACTS,
					String.valueOf( chkRequiredNoteCollateralContacts.isSelected() ) ) );
			
			preferencesToSave.add( PreferencesUtil.createPreferenceFromKey( PreferenceKey.REQUIRED_NOTE_REFERRALS,
					String.valueOf( chkRequiredNoteReferrals.isSelected() ) ) );
			
			preferencesToSave.add( PreferencesUtil.createPreferenceFromKey( PreferenceKey.REQUIRED_NOTE_SYMPTOMS,
					String.valueOf( chkRequiredNoteSymptoms.isSelected() ) ) );
			
			// Required Fields: Client
			preferencesToSave.add( PreferencesUtil.createPreferenceFromKey( PreferenceKey.REQUIRED_CLIENT_FIRST_NAME,
					String.valueOf( chkRequiredClientFirstName.isSelected() ) ) );
			
			preferencesToSave.add( PreferencesUtil.createPreferenceFromKey( PreferenceKey.REQUIRED_CLIENT_LAST_NAME,
					String.valueOf( chkRequiredClientLastName.isSelected() ) ) );
			
			preferencesToSave.add( PreferencesUtil.createPreferenceFromKey( PreferenceKey.REQUIRED_CLIENT_DOB,
					String.valueOf( chkRequiredClientDOB.isSelected() ) ) );
			
			// Required Fields: Contact
			preferencesToSave.add( PreferencesUtil.createPreferenceFromKey( PreferenceKey.REQUIRED_CONTACT_FIRST_NAME,
					String.valueOf( chkRequiredContactFirstName.isSelected() ) ) );
			
			preferencesToSave.add( PreferencesUtil.createPreferenceFromKey( PreferenceKey.REQUIRED_CONTACT_LAST_NAME,
					String.valueOf( chkRequiredContactLastName.isSelected() ) ) );
			
			// Save all preferences
			AppController.savePrefernces( preferencesToSave );
			_logChanges( preferencesToSave );
			
			// Update original values
			storeOriginalValues();
			
			JOptionPane.showMessageDialog( this, "Preferences saved successfully.", "Preferences Saved", JOptionPane.INFORMATION_MESSAGE );
			
			AppController.returnHome( true );
		}
		catch( TherapyAppException e )
		{
			JOptionPane.showMessageDialog( this, "Error saving preferences: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
		}
	}
	
	private void _logChanges( List<Preference> preferencesToSave )
	{
		for( Preference pref : preferencesToSave )
		{
			PreferenceKey key = PreferenceKey.fromKey( pref.getPreferenceKey() );
			Object originalValue = originalValues.get( key );
			String newValueStr = pref.getPreferenceValue();
			
			if( _hasValueChanged( originalValue, newValueStr, pref.getPreferenceType() ) )
			{
				AppLogger.logPreferenceChange( pref.getPreferenceKey(), _formatValueForLog( newValueStr, pref.getPreferenceType() ) );
			}
		}
	}
	
	private boolean _hasValueChanged( Object originalValue, String newValueStr, PreferenceType type )
	{
		// Handle nulls
		if( originalValue == null && newValueStr == null )
		{
			return false;
		}
		if( originalValue == null || newValueStr == null )
		{
			return true;
		}
		
		// Compare based on type
		if( type == null )
		{
			return !originalValue.toString().equals( newValueStr );
		}
		
		switch( type )
		{
			case BOOLEAN:
				return !originalValue.equals( Boolean.parseBoolean( newValueStr ) );
			case INTEGER:
				try
				{
					return !originalValue.equals( Integer.parseInt( newValueStr ) );
				}
				catch( NumberFormatException e )
				{
					return true;
				}
			case DOUBLE:
				try
				{
					return !originalValue.equals( Double.parseDouble( newValueStr ) );
				}
				catch( NumberFormatException e )
				{
					return true;
				}
			case STRING:
			default:
				return !originalValue.toString().equals( newValueStr );
		}
	}
	
	private String _formatValueForLog( Object value, PreferenceType type )
	{
		if( value == null )
		{
			return "(not set)";
		}
		
		if( type == null )
		{
			return value.toString();
		}
		
		switch( type )
		{
			case BOOLEAN:
				if( value instanceof Boolean )
				{
					return (Boolean) value ? "enabled" : "disabled";
				}
				return Boolean.parseBoolean( value.toString() ) ? "enabled" : "disabled";
			case INTEGER:
			case DOUBLE:
			case STRING:
			default:
				return value.toString();
		}
	}
	
	private void resetToDefaults()
	{
		int result = JOptionPane.showConfirmDialog( this, "Are you sure you want to reset all preferences to their default values?",
				"Reset to Defaults", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE );
		
		if( result == JOptionPane.YES_OPTION )
		{
			try
			{
				AppController.resetAllToDefaults();
				loadPreferences();
				
				JOptionPane.showMessageDialog( this, "All preferences have been reset to their default values.", "Reset Complete",
						JOptionPane.INFORMATION_MESSAGE );
			}
			catch( TherapyAppException e )
			{
				JOptionPane.showMessageDialog( this, "Error resetting preferences: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
			}
		}
	}
	
	/**
	 * Check if there are unsaved changes
	 * 
	 * @return true if any preference has been modified
	 */
	public boolean hasUnsavedChanges()
	{
		if( originalValues.isEmpty() )
		{
			return false;
		}
		
		return !originalValues.get( PreferenceKey.DEFAULT_SESSION_FROM_PREVIOUS ).equals( chkDefaultSessionFromPrevious.isSelected() )
				|| !originalValues.get( PreferenceKey.DEFAULT_DIAGNOSIS_FROM_PREVIOUS )
						.equals( chkDefaultDiagnosisFromPrevious.isSelected() )
				|| !originalValues.get( PreferenceKey.DEFAULT_APPOINTMENT_DATE ).equals( cboDefaultAppointmentDate.getSelectedItem() )
				|| !originalValues.get( PreferenceKey.DEFAULT_VIRTUAL ).equals( chkDefaultVirtual.isSelected() )
				|| !originalValues.get( PreferenceKey.REQUIRED_NOTE_DIAGNOSIS ).equals( chkRequiredNoteDiagnosis.isSelected() )
				|| !originalValues.get( PreferenceKey.REQUIRED_NOTE_NARRATIVE ).equals( chkRequiredNoteNarrative.isSelected() )
				|| !originalValues.get( PreferenceKey.REQUIRED_NOTE_AFFECT ).equals( chkRequiredNoteAffect.isSelected() )
				|| !originalValues.get( PreferenceKey.REQUIRED_NOTE_APPEARANCE ).equals( chkRequiredNoteAppearance.isSelected() )
				|| !originalValues.get( PreferenceKey.REQUIRED_NOTE_SPEECH ).equals( chkRequiredNoteSpeech.isSelected() )
				|| !originalValues.get( PreferenceKey.REQUIRED_NOTE_EYE_CONTACT ).equals( chkRequiredNoteEyeContact.isSelected() )
				|| !originalValues.get( PreferenceKey.REQUIRED_NOTE_NEXT_APPOINTMENT ).equals( chkRequiredNoteNextAppointment.isSelected() )
				|| !originalValues.get( PreferenceKey.REQUIRED_NOTE_COLLATERAL_CONTACTS )
						.equals( chkRequiredNoteCollateralContacts.isSelected() )
				|| !originalValues.get( PreferenceKey.REQUIRED_NOTE_REFERRALS ).equals( chkRequiredNoteReferrals.isSelected() )
				|| !originalValues.get( PreferenceKey.REQUIRED_NOTE_SYMPTOMS ).equals( chkRequiredNoteSymptoms.isSelected() )
				|| !originalValues.get( PreferenceKey.REQUIRED_CLIENT_FIRST_NAME ).equals( chkRequiredClientFirstName.isSelected() )
				|| !originalValues.get( PreferenceKey.REQUIRED_CLIENT_LAST_NAME ).equals( chkRequiredClientLastName.isSelected() )
				|| !originalValues.get( PreferenceKey.REQUIRED_CLIENT_DOB ).equals( chkRequiredClientDOB.isSelected() )
				|| !originalValues.get( PreferenceKey.REQUIRED_CONTACT_FIRST_NAME ).equals( chkRequiredContactFirstName.isSelected() )
				|| !originalValues.get( PreferenceKey.REQUIRED_CONTACT_LAST_NAME ).equals( chkRequiredContactLastName.isSelected() );
	}
}