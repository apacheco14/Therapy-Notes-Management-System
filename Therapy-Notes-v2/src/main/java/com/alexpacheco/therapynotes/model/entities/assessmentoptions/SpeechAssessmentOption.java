package main.java.com.alexpacheco.therapynotes.model.entities.assessmentoptions;

public class SpeechAssessmentOption extends AssessmentOption
{
	protected SpeechAssessmentOption(String name, String description)
	{
		super(name, description);
	}
	
	protected SpeechAssessmentOption(int id, String name, String description)
	{
		super(id, name, description);
	}

	@Override
	public AssessmentOptionType getOptionType()
	{
		return AssessmentOptionType.SPEECH;
	}
}
