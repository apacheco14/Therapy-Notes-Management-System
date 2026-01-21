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




/*


Example 1: Creating Checkboxes for Symptoms (Multi-Select)

//Get symptom options from database
List<AssessmentOption> symptoms = AppController.getAssessmentOptions(AssessmentOptionType.SYMPTOMS);

//Create panel to hold checkboxes
JPanel symptomsPanel = new JPanel();
symptomsPanel.setLayout(new BoxLayout(symptomsPanel, BoxLayout.Y_AXIS));

//Create checkbox for each symptom
List<AssessmentOptionCheckBox> symptomCheckboxes = new ArrayList<>();
for (AssessmentOption symptom : symptoms)
{
 AssessmentOptionCheckBox checkbox = new AssessmentOptionCheckBox(symptom);
 symptomsPanel.add(checkbox);
 symptomCheckboxes.add(checkbox);
}

//Later, get selected symptoms
List<Integer> selectedSymptomIds = new ArrayList<>();
for (AssessmentOptionCheckBox checkbox : symptomCheckboxes)
{
 if (checkbox.isSelected())
 {
     selectedSymptomIds.add(checkbox.getAssessmentOptionId());
 }
}


Example 2: Creating Radio Buttons for Affect (Single-Select)

//Get affect options from database
List<AssessmentOption> affects = AppController.getAssessmentOptions(AssessmentOptionType.AFFECT);

//Create panel and button group
JPanel affectPanel = new JPanel();
affectPanel.setLayout(new BoxLayout(affectPanel, BoxLayout.Y_AXIS));
ButtonGroup affectGroup = new ButtonGroup();

//Create radio button for each affect
List<AssessmentOptionRadioButton> affectRadioButtons = new ArrayList<>();
for (AssessmentOption affect : affects)
{
 AssessmentOptionRadioButton radioButton = new AssessmentOptionRadioButton(affect);
 affectGroup.add(radioButton.getRadioButton()); // Add to ButtonGroup for mutual exclusion
 affectPanel.add(radioButton);
 affectRadioButtons.add(radioButton);
}

//Later, get selected affect
Integer selectedAffectId = null;
for (AssessmentOptionRadioButton radioButton : affectRadioButtons)
{
 if (radioButton.isSelected())
 {
     selectedAffectId = radioButton.getAssessmentOptionId();
     break; // Only one can be selected
 }
}


Example 3: Adding Action Listeners


AssessmentOptionCheckBox checkbox = new AssessmentOptionCheckBox(symptom);
checkbox.addActionListener(e -> {
    System.out.println("Checkbox changed: " + checkbox.getAssessmentOptionName());
    System.out.println("Selected: " + checkbox.isSelected());
    System.out.println("Option ID: " + checkbox.getAssessmentOptionId());
});



Example 4: Pre-selecting Based on Saved Data

//Load saved symptom IDs for a note
List<Integer> savedSymptomIds = AppController.getSymptomsForNote(noteId);

//Pre-select checkboxes
for (AssessmentOptionCheckBox checkbox : symptomCheckboxes)
{
 if (savedSymptomIds.contains(checkbox.getAssessmentOptionId()))
 {
     checkbox.setSelected(true);
 }
}

*/