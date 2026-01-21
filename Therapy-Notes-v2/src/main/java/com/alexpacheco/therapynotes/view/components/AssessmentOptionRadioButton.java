package com.alexpacheco.therapynotes.view.components;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOption;

/**
 * A radio button component associated with an AssessmentOption. Typically used for single-select
 * scenarios
 * 
 * Note: Multiple AssessmentOptionRadioButtons should be added to a ButtonGroup to ensure only one
 * can be selected at a time.
 */
public class AssessmentOptionRadioButton extends AssessmentOptionComponent
{
	private static final long serialVersionUID = 1918889001281755621L;
	
	/**
	 * Creates a radio button for the given assessment option
	 * 
	 * @param assessmentOption The assessment option (must have an ID)
	 */
	public AssessmentOptionRadioButton(AssessmentOption assessmentOption)
	{
		super(assessmentOption);
	}
	
	@Override
	protected void initializeButton()
	{
		button = new JRadioButton();
	}
	
	/**
	 * Get the underlying JRadioButton
	 * 
	 * @return The radio button component
	 */
	public JRadioButton getRadioButton()
	{
		return (JRadioButton) button;
	}
	
	/**
	 * Enables the ability to deselect this radio button by clicking on it when already selected.
	 * 
	 * @param buttonGroup The ButtonGroup this radio button belongs to
	 */
	public void enableDeselection(ButtonGroup buttonGroup)
	{
		getRadioButton().addMouseListener(new MouseAdapter()
		{
			private boolean wasSelected;
			
			@Override
			public void mousePressed(MouseEvent e)
			{
				wasSelected = isSelected();
			}
			
			@Override
			public void mouseReleased(MouseEvent e)
			{
				if (wasSelected)
				{
					buttonGroup.clearSelection();
				}
			}
		});
	}
}