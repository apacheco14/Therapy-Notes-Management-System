package com.alexpacheco.therapynotes.controller.enums;

/**
 * Enum defining all user preference keys with their default values and metadata.
 * Use this enum throughout the application to reference preferences consistently.
 */
public enum PreferenceKey
{
	// Default Behavior
	DEFAULT_SESSION_FROM_PREVIOUS(
			"default.session_from_previous",
			"true",
			PreferenceType.BOOLEAN,
			"Default Session from Previous",
			"Automatically populate session number from the client's previous note",
			"Default Behavior"),
	
	DEFAULT_DIAGNOSIS_FROM_PREVIOUS(
			"default.diagnosis_from_previous",
			"true",
			PreferenceType.BOOLEAN,
			"Default Diagnosis from Previous",
			"Automatically populate diagnosis from the client's previous note",
			"Default Behavior"),
	
	DEFAULT_APPOINTMENT_DATE(
			"default.appointment_date",
			"today",
			PreferenceType.STRING,
			"Default Appointment Date",
			"Default value for appointment date when creating a new note",
			"Default Behavior"),
	
	DEFAULT_VIRTUAL(
			"default.virtual",
			"true",
			PreferenceType.BOOLEAN,
			"Default Virtual Session",
			"Default new notes to virtual/telehealth session type",
			"Default Behavior"),
	
	// Required Fields: Note
	REQUIRED_NOTE_DIAGNOSIS(
			"required.note.diagnosis",
			"true",
			PreferenceType.BOOLEAN,
			"Require Diagnosis",
			"Require diagnosis field when saving a note",
			"Required Fields: Note"),
	
	REQUIRED_NOTE_NARRATIVE(
			"required.note.narrative",
			"true",
			PreferenceType.BOOLEAN,
			"Require Narrative",
			"Require narrative field when saving a note",
			"Required Fields: Note"),
	
	REQUIRED_NOTE_AFFECT(
			"required.note.affect",
			"true",
			PreferenceType.BOOLEAN,
			"Require Affect",
			"Require affect field when saving a note",
			"Required Fields: Note"),
	
	REQUIRED_NOTE_APPEARANCE(
			"required.note.appearance",
			"true",
			PreferenceType.BOOLEAN,
			"Require Appearance",
			"Require appearance field when saving a note",
			"Required Fields: Note"),
	
	REQUIRED_NOTE_SPEECH(
			"required.note.speech",
			"true",
			PreferenceType.BOOLEAN,
			"Require Speech",
			"Require speech field when saving a note",
			"Required Fields: Note"),
	
	REQUIRED_NOTE_EYE_CONTACT(
			"required.note.eye_contact",
			"true",
			PreferenceType.BOOLEAN,
			"Require Eye Contact",
			"Require eye contact field when saving a note",
			"Required Fields: Note"),
	
	REQUIRED_NOTE_NEXT_APPOINTMENT(
			"required.note.next_appointment",
			"true",
			PreferenceType.BOOLEAN,
			"Require Next Appointment",
			"Require next appointment field when saving a note",
			"Required Fields: Note"),
	
	REQUIRED_NOTE_COLLATERAL_CONTACTS(
			"required.note.collateral_contacts",
			"false",
			PreferenceType.BOOLEAN,
			"Require Collateral Contacts",
			"Require collateral contacts field when saving a note",
			"Required Fields: Note"),
	
	REQUIRED_NOTE_REFERRALS(
			"required.note.referrals",
			"false",
			PreferenceType.BOOLEAN,
			"Require Referrals",
			"Require referrals field when saving a note",
			"Required Fields: Note"),
	
	REQUIRED_NOTE_SYMPTOMS(
			"required.note.symptoms",
			"false",
			PreferenceType.BOOLEAN,
			"Require Symptoms",
			"Require symptoms field when saving a note",
			"Required Fields: Note"),
	
	// Required Fields: Client
	REQUIRED_CLIENT_FIRST_NAME(
			"required.client.first_name",
			"false",
			PreferenceType.BOOLEAN,
			"Require Client First Name",
			"Require first name when saving a client",
			"Required Fields: Client"),
	
	REQUIRED_CLIENT_LAST_NAME(
			"required.client.last_name",
			"false",
			PreferenceType.BOOLEAN,
			"Require Client Last Name",
			"Require last name when saving a client",
			"Required Fields: Client"),
	
	REQUIRED_CLIENT_DOB(
			"required.client.date_of_birth",
			"false",
			PreferenceType.BOOLEAN,
			"Require Client Date of Birth",
			"Require date of birth when saving a client",
			"Required Fields: Client"),
	
	// Required Fields: Contact
	REQUIRED_CONTACT_FIRST_NAME(
			"required.contact.first_name",
			"false",
			PreferenceType.BOOLEAN,
			"Require Contact First Name",
			"Require first name when saving a contact",
			"Required Fields: Contact"),
	
	REQUIRED_CONTACT_LAST_NAME(
			"required.contact.last_name",
			"false",
			PreferenceType.BOOLEAN,
			"Require Contact Last Name",
			"Require last name when saving a contact",
			"Required Fields: Contact");
	
	private final String key;
	private final String defaultValue;
	private final PreferenceType type;
	private final String displayName;
	private final String description;
	private final String category;
	
	PreferenceKey(String key, String defaultValue, PreferenceType type, 
			String displayName, String description, String category)
	{
		this.key = key;
		this.defaultValue = defaultValue;
		this.type = type;
		this.displayName = displayName;
		this.description = description;
		this.category = category;
	}
	
	public String getKey()
	{
		return key;
	}
	
	public String getDefaultValue()
	{
		return defaultValue;
	}
	
	public PreferenceType getType()
	{
		return type;
	}
	
	public String getDisplayName()
	{
		return displayName;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public String getCategory()
	{
		return category;
	}
	
	/**
	 * Get the default value as a boolean
	 * 
	 * @return boolean default value
	 */
	public boolean getDefaultAsBoolean()
	{
		return Boolean.parseBoolean(defaultValue);
	}
	
	/**
	 * Get the default value as an integer
	 * 
	 * @return integer default value, or 0 if not parseable
	 */
	public int getDefaultAsInt()
	{
		try
		{
			return Integer.parseInt(defaultValue);
		}
		catch (NumberFormatException e)
		{
			return 0;
		}
	}
	
	/**
	 * Get the default value as a double
	 * 
	 * @return double default value, or 0.0 if not parseable
	 */
	public double getDefaultAsDouble()
	{
		try
		{
			return Double.parseDouble(defaultValue);
		}
		catch (NumberFormatException e)
		{
			return 0.0;
		}
	}
	
	/**
	 * Find a PreferenceKey by its key string
	 * 
	 * @param key The preference key string
	 * @return The matching PreferenceKey, or null if not found
	 */
	public static PreferenceKey fromKey(String key)
	{
		for (PreferenceKey pref : values())
		{
			if (pref.key.equals(key))
			{
				return pref;
			}
		}
		return null;
	}
	
	/**
	 * Get all preference keys in a specific category
	 * 
	 * @param category The category name
	 * @return Array of PreferenceKeys in that category
	 */
	public static PreferenceKey[] getByCategory(String category)
	{
		return java.util.Arrays.stream(values())
				.filter(p -> p.category.equals(category))
				.toArray(PreferenceKey[]::new);
	}
}