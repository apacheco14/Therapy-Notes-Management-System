package main.java.com.alexpacheco.therapynotes.model.entities.assessmentoptions;

public class ReferralAssessmentOption extends AssessmentOption
{
	public ReferralAssessmentOption(String name, String description)
	{
		super(name, description);
	}
	
	public ReferralAssessmentOption(int id, String name, String description)
	{
		super(id, name, description);
	}
	
	@Override
	public AssessmentOptionType getOptionType()
	{
		return AssessmentOptionType.REFERRALS;
	}
}
