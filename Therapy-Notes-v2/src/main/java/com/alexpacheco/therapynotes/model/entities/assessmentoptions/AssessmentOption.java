package com.alexpacheco.therapynotes.model.entities.assessmentoptions;

public abstract class AssessmentOption
{
	private Integer id = null;
	private String name;
	private String description;
	
	protected AssessmentOption(String name, String description)
	{
		this.name = name;
		this.description = description;
	}
	
	protected AssessmentOption(Integer id, String name, String description)
	{
		this.id = id;
		this.name = name;
		this.description = description;
	}
	
	public Integer getId()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String optionName)
	{
		this.name = optionName;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public void setDescription(String optionDescription)
	{
		this.description = optionDescription;
	}
	
	public abstract AssessmentOptionType getOptionType();
}
