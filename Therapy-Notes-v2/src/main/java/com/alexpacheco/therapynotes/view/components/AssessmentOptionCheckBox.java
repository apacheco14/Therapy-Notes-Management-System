package com.alexpacheco.therapynotes.view.components;

import javax.swing.JCheckBox;

import com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOption;

/**
 * A checkbox component associated with an AssessmentOption. Typically used for multi-select
 * scenarios (e.g., selecting multiple symptoms).
 */
public class AssessmentOptionCheckBox extends AssessmentOptionComponent
{
	private static final long serialVersionUID = 4828965423136265500L;

	/**
	 * Creates a checkbox for the given assessment option
	 * 
	 * @param assessmentOption The assessment option (must have an ID)
	 */
	public AssessmentOptionCheckBox(AssessmentOption assessmentOption)
	{
		super(assessmentOption);
	}
	
	@Override
	protected void initializeButton()
	{
		button = new JCheckBox();
	}
	
	/**
	 * Get the underlying JCheckBox
	 * 
	 * @return The checkbox component
	 */
	public JCheckBox getCheckBox()
	{
		return (JCheckBox) button;
	}
}