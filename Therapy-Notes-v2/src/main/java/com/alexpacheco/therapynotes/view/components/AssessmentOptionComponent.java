package com.alexpacheco.therapynotes.view.components;

import javax.swing.*;

import com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOption;
import com.alexpacheco.therapynotes.util.JavaUtils;

/**
 * Abstract base class for UI components associated with AssessmentOptions. Provides common behavior
 * for checkboxes and radio buttons that represent assessment options.
 */
public abstract class AssessmentOptionComponent extends JComponent
{
	private static final long serialVersionUID = -5696775174316286613L;
	protected AssessmentOption assessmentOption;
	protected AbstractButton button; // JCheckBox or JRadioButton
	
	/**
	 * Constructor that associates an AssessmentOption with this component
	 * 
	 * @param assessmentOption The assessment option (must have an ID)
	 */
	protected AssessmentOptionComponent(AssessmentOption assessmentOption)
	{
		if (assessmentOption == null)
		{
			throw new IllegalArgumentException("AssessmentOption cannot be null");
		}
		
		if (assessmentOption.getId() == null)
		{
			throw new IllegalArgumentException("AssessmentOption must have an ID");
		}
		
		this.assessmentOption = assessmentOption;
		initializeButton();
		setupComponent();
	}
	
	/**
	 * Template method for subclasses to create their specific button type
	 */
	protected abstract void initializeButton();
	
	/**
	 * Common setup for all assessment option components
	 */
	private void setupComponent()
	{
		setLayout(new java.awt.BorderLayout());
		
		// Set button text to the option name
		button.setText(assessmentOption.getName());
		
		// Set tooltip to show description if available
		if (!JavaUtils.isNullOrEmpty(assessmentOption.getDescription()))
		{
			button.setToolTipText(assessmentOption.getDescription());
		}
		
		// Store the assessment option ID in the button's action command
		button.setActionCommand(String.valueOf(assessmentOption.getId()));
		
		add(button, java.awt.BorderLayout.CENTER);
	}
	
	/**
	 * Get the associated AssessmentOption
	 * 
	 * @return The assessment option
	 */
	public AssessmentOption getAssessmentOption()
	{
		return assessmentOption;
	}
	
	/**
	 * Get the assessment option ID
	 * 
	 * @return The ID of the associated assessment option
	 */
	public Integer getAssessmentOptionId()
	{
		return assessmentOption.getId();
	}
	
	/**
	 * Get the assessment option name
	 * 
	 * @return The name of the associated assessment option
	 */
	public String getAssessmentOptionName()
	{
		return assessmentOption.getName();
	}
	
	/**
	 * Check if this component is selected
	 * 
	 * @return true if selected, false otherwise
	 */
	public boolean isSelected()
	{
		return button.isSelected();
	}
	
	/**
	 * Set the selected state of this component
	 * 
	 * @param selected true to select, false to deselect
	 */
	public void setSelected(boolean selected)
	{
		button.setSelected(selected);
	}
	
	/**
	 * Enable or disable this component
	 * 
	 * @param enabled true to enable, false to disable
	 */
	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		button.setEnabled(enabled);
	}
	
	/**
	 * Add an action listener to this component
	 * 
	 * @param listener The action listener to add
	 */
	public void addActionListener(java.awt.event.ActionListener listener)
	{
		button.addActionListener(listener);
	}
	
	/**
	 * Remove an action listener from this component
	 * 
	 * @param listener The action listener to remove
	 */
	public void removeActionListener(java.awt.event.ActionListener listener)
	{
		button.removeActionListener(listener);
	}
	
	/**
	 * Get the underlying button component
	 * 
	 * @return The JCheckBox or JRadioButton
	 */
	protected AbstractButton getButton()
	{
		return button;
	}
}