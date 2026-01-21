package com.alexpacheco.therapynotes.model.entities.assessmentoptions;

public class SymptomAssessmentOption extends AssessmentOption
{
	protected SymptomAssessmentOption(String name, String description)
	{
		super(name, description);
	}
	
	protected SymptomAssessmentOption(int id, String name, String description)
	{
		super(id, name, description);
	}

	@Override
	public AssessmentOptionType getOptionType()
	{
		return AssessmentOptionType.SYMPTOMS;
	}
}
