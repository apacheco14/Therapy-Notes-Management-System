package com.alexpacheco.therapynotes.model.entities.assessmentoptions;

public class CollateralContactAssessmentOption extends AssessmentOption
{
	public CollateralContactAssessmentOption(String name, String description)
	{
		super(name, description);
	}
	
	public CollateralContactAssessmentOption(int id, String name, String description)
	{
		super(id, name, description);
	}
	
	@Override
	public AssessmentOptionType getOptionType()
	{
		return AssessmentOptionType.COLL_CONTACTS;
	}
}
