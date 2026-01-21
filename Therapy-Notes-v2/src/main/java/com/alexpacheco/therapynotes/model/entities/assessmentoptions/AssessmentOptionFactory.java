package com.alexpacheco.therapynotes.model.entities.assessmentoptions;

public class AssessmentOptionFactory
{
	public static AssessmentOption createAssessmentOption(int id, String name, String description, String dbTypeKey)
	{
		if(dbTypeKey == null)
			return null;
		
		return createAssessmentOption(id, name, description, AssessmentOptionType.getByDbTypeKey(dbTypeKey));
	}
	
	public static AssessmentOption createAssessmentOption(String name, String description, AssessmentOptionType type)
	{
		if (type == null)
			return null;
		
		switch (type)
		{
			case SYMPTOMS:
				return new SymptomAssessmentOption(name, description);
			case APPEARANCE:
				return new AppearanceAssessmentOption(name, description);
			case SPEECH:
				return new SpeechAssessmentOption(name, description);
			case AFFECT:
				return new AffectAssessmentOption(name, description);
			case EYE_CONTACT:
				return new EyeContactAssessmentOption(name, description);
			case REFERRALS:
				return new ReferralAssessmentOption(name, description);
			case COLL_CONTACTS:
				return new CollateralContactAssessmentOption(name, description);
			case NEXT_APPT:
				return new NextApptAssessmentOption(name, description);
			default:
				return null;
		}
	}
	
	public static AssessmentOption createAssessmentOption(int id, String name, String description, AssessmentOptionType type)
	{
		if (type == null)
			return null;
		
		switch (type)
		{
			case SYMPTOMS:
				return new SymptomAssessmentOption(id, name, description);
			case APPEARANCE:
				return new AppearanceAssessmentOption(id, name, description);
			case SPEECH:
				return new SpeechAssessmentOption(id, name, description);
			case AFFECT:
				return new AffectAssessmentOption(id, name, description);
			case EYE_CONTACT:
				return new EyeContactAssessmentOption(id, name, description);
			case REFERRALS:
				return new ReferralAssessmentOption(id, name, description);
			case COLL_CONTACTS:
				return new CollateralContactAssessmentOption(id, name, description);
			case NEXT_APPT:
				return new NextApptAssessmentOption(id, name, description);
			default:
				return null;
		}
	}
}
