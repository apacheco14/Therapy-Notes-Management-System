package main.java.com.alexpacheco.therapynotes.model.entities.assessmentoptions;

public class AffectAssessmentOption extends AssessmentOption
{
	protected AffectAssessmentOption(String name, String description)
	{
		super(name, description);
	}
	
	protected AffectAssessmentOption(int id, String name, String description)
	{
		super(id, name, description);
	}

	@Override
	public AssessmentOptionType getOptionType()
	{
		return AssessmentOptionType.AFFECT;
	}
}
