package com.alexpacheco.therapynotes.model.entities.assessmentoptions;

public enum AssessmentOptionType
{
	SYMPTOMS("Symptoms", "symptoms"),
	APPEARANCE("AppearanceAssessmentOption", "appearance"),
	SPEECH("SpeechAssessmentOption", "speech"),
	AFFECT("AffectAssessmentOption", "affect"),
	EYE_CONTACT("Eye Contact", "eyeContact"),
	REFERRALS("Referrals", "referrals"),
	COLL_CONTACTS("Collateral Contacts", "collateralContacts"),
	NEXT_APPT("Next Appointment", "nextAppt");
	
	private final String name;
	private final String dbTypeKey;
	
	AssessmentOptionType(String name, String dbTypeKey)
	{
		this.name = name;
		this.dbTypeKey = dbTypeKey;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDbTypeKey()
	{
		return dbTypeKey;
	}
	
	public static AssessmentOptionType getByDbTypeKey(String code)
	{
		for (AssessmentOptionType type : AssessmentOptionType.values())
		{
			if (type.dbTypeKey.equals(code))
			{
				return type;
			}
		}
		throw new IllegalArgumentException("Unknown DB type key: " + code);
	}
}
