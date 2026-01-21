package main.java.com.alexpacheco.therapynotes.model.entities.assessmentoptions;

public class EyeContactAssessmentOption extends AssessmentOption
{
	protected EyeContactAssessmentOption(String name, String description)
	{
		super(name, description);
	}
	
	protected EyeContactAssessmentOption(int id, String name, String description)
	{
		super(id, name, description);
	}

	@Override
	public AssessmentOptionType getOptionType()
	{
		return AssessmentOptionType.EYE_CONTACT;
	}
}
