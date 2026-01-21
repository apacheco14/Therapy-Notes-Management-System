package main.java.com.alexpacheco.therapynotes.util;

import main.java.com.alexpacheco.therapynotes.controller.enums.PreferenceKey;
import main.java.com.alexpacheco.therapynotes.model.api.PreferenceApi;
import main.java.com.alexpacheco.therapynotes.model.entities.Preference;

/**
 * Utility class for accessing user preferences throughout the application. Provides static methods for retrieving preference values with
 * sensible defaults.
 */
public class PreferencesUtil
{
	private static PreferenceApi preferenceApi = new PreferenceApi();
	
	// ===========================================
	// Default Behavior Preferences
	// ===========================================
	
	/**
	 * Check if session number should default from previous note
	 * 
	 * @return true if enabled
	 */
	public static boolean isDefaultSessionFromPrevious()
	{
		return getBoolean( PreferenceKey.DEFAULT_SESSION_FROM_PREVIOUS );
	}
	
	/**
	 * Check if diagnosis should default from previous note
	 * 
	 * @return true if enabled
	 */
	public static boolean isDefaultDiagnosisFromPrevious()
	{
		return getBoolean( PreferenceKey.DEFAULT_DIAGNOSIS_FROM_PREVIOUS );
	}
	
	/**
	 * Get the default appointment date setting
	 * 
	 * @return "today" or "none"
	 */
	public static String getDefaultAppointmentDate()
	{
		return getString( PreferenceKey.DEFAULT_APPOINTMENT_DATE );
	}
	
	/**
	 * Check if appointment date should default to today
	 * 
	 * @return true if default is "today"
	 */
	public static boolean isDefaultAppointmentDateToday()
	{
		return "today".equalsIgnoreCase( getDefaultAppointmentDate() );
	}
	
	/**
	 * Check if sessions should default to virtual/telehealth
	 * 
	 * @return true if enabled
	 */
	public static boolean isDefaultVirtual()
	{
		return getBoolean( PreferenceKey.DEFAULT_VIRTUAL );
	}
	
	// ===========================================
	// Required Fields: Note
	// ===========================================
	
	/**
	 * Check if diagnosis is required for notes
	 * 
	 * @return true if required
	 */
	public static boolean isNoteDiagnosisRequired()
	{
		return getBoolean( PreferenceKey.REQUIRED_NOTE_DIAGNOSIS );
	}
	
	/**
	 * Check if narrative is required for notes
	 * 
	 * @return true if required
	 */
	public static boolean isNoteNarrativeRequired()
	{
		return getBoolean( PreferenceKey.REQUIRED_NOTE_NARRATIVE );
	}
	
	/**
	 * Check if affect is required for notes
	 * 
	 * @return true if required
	 */
	public static boolean isNoteAffectRequired()
	{
		return getBoolean( PreferenceKey.REQUIRED_NOTE_AFFECT );
	}
	
	/**
	 * Check if appearance is required for notes
	 * 
	 * @return true if required
	 */
	public static boolean isNoteAppearanceRequired()
	{
		return getBoolean( PreferenceKey.REQUIRED_NOTE_APPEARANCE );
	}
	
	/**
	 * Check if speech is required for notes
	 * 
	 * @return true if required
	 */
	public static boolean isNoteSpeechRequired()
	{
		return getBoolean( PreferenceKey.REQUIRED_NOTE_SPEECH );
	}
	
	/**
	 * Check if eye contact is required for notes
	 * 
	 * @return true if required
	 */
	public static boolean isNoteEyeContactRequired()
	{
		return getBoolean( PreferenceKey.REQUIRED_NOTE_EYE_CONTACT );
	}
	
	/**
	 * Check if next appointment is required for notes
	 * 
	 * @return true if required
	 */
	public static boolean isNoteNextAppointmentRequired()
	{
		return getBoolean( PreferenceKey.REQUIRED_NOTE_NEXT_APPOINTMENT );
	}
	
	/**
	 * Check if collateral contacts is required for notes
	 * 
	 * @return true if required
	 */
	public static boolean isNoteCollateralContactsRequired()
	{
		return getBoolean( PreferenceKey.REQUIRED_NOTE_COLLATERAL_CONTACTS );
	}
	
	/**
	 * Check if referrals is required for notes
	 * 
	 * @return true if required
	 */
	public static boolean isNoteReferralsRequired()
	{
		return getBoolean( PreferenceKey.REQUIRED_NOTE_REFERRALS );
	}
	
	/**
	 * Check if symptoms is required for notes
	 * 
	 * @return true if required
	 */
	public static boolean isNoteSymptomsRequired()
	{
		return getBoolean( PreferenceKey.REQUIRED_NOTE_SYMPTOMS );
	}
	
	// ===========================================
	// Required Fields: Client
	// ===========================================
	
	/**
	 * Check if first name is required for clients
	 * 
	 * @return true if required
	 */
	public static boolean isClientFirstNameRequired()
	{
		return getBoolean( PreferenceKey.REQUIRED_CLIENT_FIRST_NAME );
	}
	
	/**
	 * Check if last name is required for clients
	 * 
	 * @return true if required
	 */
	public static boolean isClientLastNameRequired()
	{
		return getBoolean( PreferenceKey.REQUIRED_CLIENT_LAST_NAME );
	}
	
	/**
	 * Check if date of birth is required for clients
	 * 
	 * @return true if required
	 */
	public static boolean isClientDOBRequired()
	{
		return getBoolean( PreferenceKey.REQUIRED_CLIENT_DOB );
	}
	
	// ===========================================
	// Required Fields: Contact
	// ===========================================
	
	/**
	 * Check if first name is required for contacts
	 * 
	 * @return true if required
	 */
	public static boolean isContactFirstNameRequired()
	{
		return getBoolean( PreferenceKey.REQUIRED_CONTACT_FIRST_NAME );
	}
	
	/**
	 * Check if last name is required for contacts
	 * 
	 * @return true if required
	 */
	public static boolean isContactLastNameRequired()
	{
		return getBoolean( PreferenceKey.REQUIRED_CONTACT_LAST_NAME );
	}
	
	// ===========================================
	// Generic Getters using PreferenceKey
	// ===========================================
	
	/**
	 * Get a boolean preference value using PreferenceKey
	 * 
	 * @param key The PreferenceKey enum value
	 * @return The preference value, or the default from the enum if not found
	 */
	public static boolean getBoolean( PreferenceKey key )
	{
		return preferenceApi.getBoolean( key.getKey(), key.getDefaultAsBoolean() );
	}
	
	/**
	 * Get a string preference value using PreferenceKey
	 * 
	 * @param key The PreferenceKey enum value
	 * @return The preference value, or the default from the enum if not found
	 */
	public static String getString( PreferenceKey key )
	{
		return preferenceApi.getValue( key.getKey(), key.getDefaultValue() );
	}
	
	/**
	 * Get an integer preference value using PreferenceKey
	 * 
	 * @param key The PreferenceKey enum value
	 * @return The preference value, or the default from the enum if not found
	 */
	public static int getInt( PreferenceKey key )
	{
		return preferenceApi.getInt( key.getKey(), key.getDefaultAsInt() );
	}
	
	/**
	 * Get a double preference value using PreferenceKey
	 * 
	 * @param key The PreferenceKey enum value
	 * @return The preference value, or the default from the enum if not found
	 */
	public static double getDouble( PreferenceKey key )
	{
		return preferenceApi.getDouble( key.getKey(), key.getDefaultAsDouble() );
	}
	
	/**
	 * Create a Preference entity from a PreferenceKey enum value
	 * 
	 * @param key   The PreferenceKey enum value
	 * @param value The current value to set
	 * @return A new Preference entity
	 */
	public static Preference createPreferenceFromKey( PreferenceKey key, String value )
	{
		return new Preference( key.getKey(), value, key.getType(), key.getDisplayName(), key.getDescription(), key.getDefaultValue(),
				key.getCategory() );
	}
}