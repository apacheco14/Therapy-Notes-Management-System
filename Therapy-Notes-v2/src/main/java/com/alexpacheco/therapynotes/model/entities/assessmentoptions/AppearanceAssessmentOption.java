package main.java.com.alexpacheco.therapynotes.model.entities.assessmentoptions;

public class AppearanceAssessmentOption extends AssessmentOption
{
	protected AppearanceAssessmentOption(String name, String description)
	{
		super(name, description);
	}
	
	protected AppearanceAssessmentOption(int id, String name, String description)
	{
		super(id, name, description);
	}

	@Override
	public AssessmentOptionType getOptionType()
	{
		return AssessmentOptionType.APPEARANCE;
	}
}
