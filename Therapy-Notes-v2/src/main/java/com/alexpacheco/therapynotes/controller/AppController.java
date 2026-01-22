package com.alexpacheco.therapynotes.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.alexpacheco.therapynotes.controller.enums.ConfigKey;
import com.alexpacheco.therapynotes.controller.enums.LogLevel;
import com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.install.SetupConfiguration;
import com.alexpacheco.therapynotes.install.SetupConfigurationManager;
import com.alexpacheco.therapynotes.model.api.AppLogApi;
import com.alexpacheco.therapynotes.model.api.AssessmentOptionApi;
import com.alexpacheco.therapynotes.model.api.ClientApi;
import com.alexpacheco.therapynotes.model.api.CollateralContactApi;
import com.alexpacheco.therapynotes.model.api.ContactApi;
import com.alexpacheco.therapynotes.model.api.NoteApi;
import com.alexpacheco.therapynotes.model.api.PreferenceApi;
import com.alexpacheco.therapynotes.model.api.ReferralApi;
import com.alexpacheco.therapynotes.model.api.SymptomApi;
import com.alexpacheco.therapynotes.model.entities.AppLog;
import com.alexpacheco.therapynotes.model.entities.Client;
import com.alexpacheco.therapynotes.model.entities.Contact;
import com.alexpacheco.therapynotes.model.entities.Note;
import com.alexpacheco.therapynotes.model.entities.Preference;
import com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOption;
import com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOptionFactory;
import com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOptionType;
import com.alexpacheco.therapynotes.util.AppLogger;
import com.alexpacheco.therapynotes.util.JavaUtils;
import com.alexpacheco.therapynotes.view.MainWindow;
import com.alexpacheco.therapynotes.view.dialogs.Dlg_PinEntry;

public class AppController
{
	private static MainWindow window;
	private static List<String> icd10Codes;
	private static NoteApi noteApi = new NoteApi();
	private static ClientApi clientApi = new ClientApi();
	private static ContactApi contactApi = new ContactApi();
	private static AssessmentOptionApi assessmentOptionApi = new AssessmentOptionApi();
	private static SymptomApi symptomApi = new SymptomApi();
	private static ReferralApi referralApi = new ReferralApi();
	private static CollateralContactApi collateralContactApi = new CollateralContactApi();
	private static AppLogApi appLogApi = new AppLogApi();
	private static PreferenceApi preferenceApi = new PreferenceApi();
	
	public static void launchMainWindow()
	{
		try
		{
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		}
		catch( Exception e )
		{
			AppLogger.error( "Error setting look and feel.", e );
			AppController.showBasicErrorPopup( "Error setting look and feel." );
		}
		
		if( PinManager.isPinEnabled() )
		{
			if( !Dlg_PinEntry.authenticate( null ) )
			{
				AppLogger.logShutdown();
				System.exit( 0 );
			}
		}
		
		AppLogger.logStartup();
		window = new MainWindow();
		window.setVisible( true );
	}
	
	public static void showBasicErrorPopup( String message )
	{
		_showErrorPopup( window, message, "Error" );
	}
	
	public static void showBasicErrorPopup( TherapyAppException exception, String message )
	{
		exception.printStackTrace();
		if( JavaUtils.isNullOrEmpty( message ) )
		{
			_showErrorPopup( window, exception.getMessage(), exception.getErrorCode().getName() );
		}
		else
		{
			_showErrorPopup( window, message + "\n" + exception.getMessage(), exception.getErrorCode().getName() );
		}
	}
	
	public static void showBasicErrorPopup( TherapyAppException exception )
	{
		exception.printStackTrace();
		_showErrorPopup( window, exception.getMessage(), exception.getErrorCode().getName() );
	}
	
	private static void _showErrorPopup( JFrame parent, String message, String title )
	{
		JOptionPane.showMessageDialog( parent, message, title == null ? "Error" : title, JOptionPane.ERROR_MESSAGE );
	}
	
	public static void showValidationErrorPopup( String message )
	{
		JOptionPane.showMessageDialog( window, message, "Validation Error", JOptionPane.ERROR_MESSAGE );
	}
	
	public static List<String> getClientList() throws TherapyAppException
	{
		ArrayList<String> clientList = new ArrayList<String>();
		List<Client> clients = clientApi.getAllClients( false );
		for( Client client : clients )
		{
			clientList.add( client.getClientCode() + " - " + client.getFirstName() + " " + client.getLastName() );
		}
		
		return clientList;
	}
	
	public static HashMap<String, Integer> getClientMap() throws TherapyAppException
	{
		HashMap<String, Integer> clientMap = new HashMap<String, Integer>();
		List<Client> clients = clientApi.getAllClients( false );
		for( Client client : clients )
		{
			clientMap.put( client.getDisplayName(), client.getClientId() );
		}
		
		return clientMap;
	}
	
	public static void createClient( Client client ) throws TherapyAppException
	{
		clientApi.createClient( client );
	}
	
	public static void updateClient( Client client ) throws TherapyAppException
	{
		clientApi.updateClient( client );
	}
	
	public static Client getClientById( Integer clientId ) throws TherapyAppException
	{
		return clientApi.getClient( clientId );
	}
	
	public static List<Client> searchClients( String firstName, String lastName, String clientCode, boolean showInactive )
			throws TherapyAppException
	{
		return clientApi.findClients( firstName, lastName, clientCode, showInactive );
	}
	
	public static List<Client> getAllClients() throws TherapyAppException
	{
		return clientApi.getAllClients( true );
	}
	
	public static List<Contact> getContactsForClient( Integer clientId ) throws TherapyAppException
	{
		return contactApi.getAllContactsLinkedToClient( clientId );
	}
	
	public static Contact getContactById( Integer contactId ) throws TherapyAppException
	{
		return contactApi.getContactById( contactId );
	}
	
	public static List<Contact> searchContacts( String firstName, String lastName, Integer linkedClientId ) throws TherapyAppException
	{
		return contactApi.searchContacts( firstName, lastName, linkedClientId );
	}
	
	public static void createContact( Contact contact ) throws TherapyAppException
	{
		contactApi.createContact( contact );
	}
	
	public static void updateContact( Contact contact ) throws TherapyAppException
	{
		contactApi.updateContact( contact );
	}
	
	public static void addSingleConfigOption( ConfigKey configKey, String name, String description ) throws TherapyAppException
	{
		switch( configKey )
		{
			case AFFECT:
				_callCreateAssessmentOptionsApi( name, description, AssessmentOptionType.AFFECT );
				break;
			case APPEARANCE:
				_callCreateAssessmentOptionsApi( name, description, AssessmentOptionType.APPEARANCE );
				break;
			case EYE_CONTACT:
				_callCreateAssessmentOptionsApi( name, description, AssessmentOptionType.EYE_CONTACT );
				break;
			case NEXT_APPOINTMENT:
				_callCreateAssessmentOptionsApi( name, description, AssessmentOptionType.NEXT_APPT );
				break;
			case SPEECH:
				_callCreateAssessmentOptionsApi( name, description, AssessmentOptionType.SPEECH );
				break;
			case SYMPTOMS:
				_callCreateAssessmentOptionsApi( name, description, AssessmentOptionType.SYMPTOMS );
				break;
			case COLLATERAL_CONTACT_TYPES:
				_callCreateAssessmentOptionsApi( name, description, AssessmentOptionType.COLL_CONTACTS );
				break;
			case REFERRAL_TYPES:
				_callCreateAssessmentOptionsApi( name, description, AssessmentOptionType.REFERRALS );
				break;
			default:
				break;
		}
	}
	
	private static void _callCreateAssessmentOptionsApi( String name, String description, AssessmentOptionType type )
			throws TherapyAppException
	{
		List<AssessmentOption> options = new ArrayList<>();
		options.add( AssessmentOptionFactory.createAssessmentOption( name, description, type ) );
		assessmentOptionApi.createAssessmentOptions( options );
	}
	
	public static List<AssessmentOption> getConfigOptions( ConfigKey configKey ) throws TherapyAppException
	{
		switch( configKey )
		{
			case AFFECT:
				return assessmentOptionApi.getOptions( AssessmentOptionType.AFFECT );
			case APPEARANCE:
				return assessmentOptionApi.getOptions( AssessmentOptionType.APPEARANCE );
			case EYE_CONTACT:
				return assessmentOptionApi.getOptions( AssessmentOptionType.EYE_CONTACT );
			case SPEECH:
				return assessmentOptionApi.getOptions( AssessmentOptionType.SPEECH );
			case NEXT_APPOINTMENT:
				return assessmentOptionApi.getOptions( AssessmentOptionType.NEXT_APPT );
			case SYMPTOMS:
				return assessmentOptionApi.getOptions( AssessmentOptionType.SYMPTOMS );
			case COLLATERAL_CONTACT_TYPES:
				return assessmentOptionApi.getOptions( AssessmentOptionType.COLL_CONTACTS );
			case REFERRAL_TYPES:
				return assessmentOptionApi.getOptions( AssessmentOptionType.REFERRALS );
			default:
				return Collections.emptyList();
		}
	}
	
	public static List<AssessmentOption> getAssessmentOptions( AssessmentOptionType type ) throws TherapyAppException
	{
		List<AssessmentOption> options = assessmentOptionApi.getOptions( type );
		JavaUtils.moveOtherToEnd( options );
		return options;
	}
	
	public static void saveNote( Note note ) throws TherapyAppException
	{
		if( note.getNoteId() == null )
			noteApi.createNewNote( note );
		else
			noteApi.updateExistingNote( note );
	}
	
	/**
	 * Search for notes for a client within a certain date range
	 * 
	 * @param clientId  Client ID filter (can be null for all clients)
	 * @param startDate Start date filter (can be null for no lower bound)
	 * @param endDate   End date filter (can be null for no upper bound)
	 * @return List of Note objects
	 */
	public static List<Note> searchNotes( Integer clientId, Date startDate, Date endDate ) throws TherapyAppException
	{
		List<Note> notes = noteApi.searchNotes( clientId, startDate, endDate );
		for( Note note : notes )
		{
			note.setSymptoms( symptomApi.getSelectedSymptomsForNote( note.getNoteId() ) );
			note.setReferrals( referralApi.getSelectedReferralsForNote( note.getNoteId() ) );
			note.setCollateralContacts( collateralContactApi.getSelectedCollateralContactsForNote( note.getNoteId() ) );
		}
		
		return notes;
	}
	
	public static List<Note> getNotesByClientId( int clientId ) throws TherapyAppException
	{
		List<Note> notes = noteApi.searchNotes( clientId, null, null );
		for( Note note : notes )
		{
			note.setSymptoms( symptomApi.getSelectedSymptomsForNote( note.getNoteId() ) );
			note.setReferrals( referralApi.getSelectedReferralsForNote( note.getNoteId() ) );
			note.setCollateralContacts( collateralContactApi.getSelectedCollateralContactsForNote( note.getNoteId() ) );
		}
		
		return notes;
	}
	
	public static Note getNote( int noteId ) throws TherapyAppException
	{
		Note note = noteApi.getNote( noteId );
		note.setSymptoms( symptomApi.getSelectedSymptomsForNote( noteId ) );
		note.setReferrals( referralApi.getSelectedReferralsForNote( noteId ) );
		note.setCollateralContacts( collateralContactApi.getSelectedCollateralContactsForNote( noteId ) );
		return note;
	}
	
	public static Integer getHighestUsedSessionNumberForClient( Integer clientId ) throws TherapyAppException
	{
		return noteApi.getHighestUsedSessionNumberForClient( clientId );
	}
	
	public static String getLastUsedDiagnosisForClient( Integer clientId ) throws TherapyAppException
	{
		return noteApi.getLastUsedDiagnosisForClient( clientId );
	}
	
	public static AssessmentOption getAssessmentOptionById( Integer assessmentOptionId ) throws TherapyAppException
	{
		return assessmentOptionApi.getOption( assessmentOptionId );
	}
	
	public static void returnHome( boolean skipValidation )
	{
		window.returnHome( skipValidation );
	}
	
	public static List<AppLog> getLogs( Date startDate, Date endDate, LogLevel logLevel, int maxResults ) throws TherapyAppException
	{
		return appLogApi.getLogs( startDate, endDate, logLevel, maxResults );
	}
	
	public static List<String> getIcd10Codes()
	{
		if( icd10Codes == null || icd10Codes.isEmpty() )
		{
			InputStream codesStream = AppController.class.getResourceAsStream( "/f_codes.txt" );
			BufferedReader reader = new BufferedReader( new InputStreamReader( codesStream ) );
			icd10Codes = reader.lines().filter( line -> !line.isBlank() ).collect( Collectors.toList() );
			AppLogger.info( "ICD 10 codes loaded" );
		}
		
		return icd10Codes;
	}
	
	public static void saveOptions( List<AssessmentOption> options ) throws TherapyAppException
	{
		assessmentOptionApi.editAssessmentOptions( options );
	}
	
	public static int savePrefernces( List<Preference> preferences ) throws TherapyAppException
	{
		return preferenceApi.savePreferences( preferences );
	}
	
	public static void resetAllToDefaults() throws TherapyAppException
	{
		preferenceApi.resetAllToDefaults();
	}
	
	public static String getConfiguredDbPath()
	{
		SetupConfiguration setupConfig = SetupConfigurationManager.loadConfiguration();
		return setupConfig.getDatabasePath();
	}
	
	/**
	 * Get practice name for display in title bar or reports.
	 */
	public static String getPracticeDisplayName()
	{
		String practiceName = SetupConfigurationManager.getValue( "practice.name" );
		String practitioner = SetupConfigurationManager.getValue( "practice.practitioner" );
		
		if( practiceName != null && practitioner != null )
		{
			return practiceName + " - " + practitioner;
		}
		else if( practiceName != null )
		{
			return practiceName;
		}
		else
		{
			return "Notes Management System";
		}
	}
	
	/**
	 * Get formatted practice header for documents/exports.
	 */
	public static String getPracticeHeader()
	{
		SetupConfiguration config = SetupConfigurationManager.loadConfiguration();
		
		StringBuilder header = new StringBuilder();
		header.append( config.getPracticeName() ).append( "\n" );
		header.append( config.getPractitionerName() );
		
		if( config.getLicenseNumber() != null && !config.getLicenseNumber().isEmpty() )
		{
			header.append( ", " ).append( config.getLicenseNumber() );
		}
		
		header.append( "\n" );
		
		if( config.getAddress() != null && !config.getAddress().isEmpty() )
		{
			header.append( config.getAddress() ).append( "\n" );
		}
		
		if( config.getPhone() != null && !config.getPhone().isEmpty() )
		{
			header.append( "Phone: " ).append( config.getPhone() );
		}
		
		if( config.getEmail() != null && !config.getEmail().isEmpty() )
		{
			if( config.getPhone() != null && !config.getPhone().isEmpty() )
			{
				header.append( " | " );
			}
			header.append( "Email: " ).append( config.getEmail() );
		}
		
		return header.toString();
	}
	
	public static void updateMenu()
	{
		SwingUtilities.invokeLater( () ->
		{
			window.updateMenu();
		} );
	}
	
	public static void openNote( Integer noteId )
	{
		try
		{
			Note note = AppController.getNote( noteId );
			if( note != null )
			{
				window.openNote( note );
			}
			else
			{
				JOptionPane.showMessageDialog( window, "Note not found.", "Error", JOptionPane.ERROR_MESSAGE );
			}
		}
		catch( TherapyAppException e )
		{
			AppController.showBasicErrorPopup( e, "Error loading note:" );
		}
	}
}
