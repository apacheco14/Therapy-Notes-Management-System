package main.java.com.alexpacheco.therapynotes.model.entities.assessmentoptions;

public class NextApptAssessmentOption extends AssessmentOption
{
	protected NextApptAssessmentOption(String name, String description)
	{
		super(name, description);
	}
	
	protected NextApptAssessmentOption(int id, String name, String description)
	{
		super(id, name, description);
	}
	
	@Override
	public AssessmentOptionType getOptionType()
	{
		return AssessmentOptionType.NEXT_APPT;
	}
}