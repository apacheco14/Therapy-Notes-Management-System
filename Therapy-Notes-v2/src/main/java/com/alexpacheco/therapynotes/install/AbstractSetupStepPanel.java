package com.alexpacheco.therapynotes.install;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Abstract base class for setup wizard step panels. Provides consistent structure and common functionality for all setup steps.
 */
public abstract class AbstractSetupStepPanel extends JPanel
{
	private static final long serialVersionUID = 5003873539388008114L;
	protected final SetupConfiguration config;
	protected JLabel lblTitle;
	protected JLabel lblDescription;
	protected JPanel contentPanel;
	
	public AbstractSetupStepPanel( SetupConfiguration config )
	{
		this.config = config;
		setLayout( new BorderLayout( 0, 10 ) );
		setBorder( new EmptyBorder( 10, 0, 10, 0 ) );
		
		initializeHeader();
		initializeContent();
	}
	
	private void initializeHeader()
	{
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout( new BoxLayout( headerPanel, BoxLayout.Y_AXIS ) );
		
		lblTitle = new JLabel( getStepTitle() );
		lblTitle.setFont( lblTitle.getFont().deriveFont( Font.BOLD, 20f ) );
		lblTitle.setAlignmentX( Component.LEFT_ALIGNMENT );
		
		lblDescription = new JLabel( "<html><body style='width: 450px'>" + getStepDescription() + "</body></html>" );
		lblDescription.setFont( lblDescription.getFont().deriveFont( 13f ) );
		lblDescription.setForeground( new Color( 100, 100, 100 ) );
		lblDescription.setAlignmentX( Component.LEFT_ALIGNMENT );
		lblDescription.setBorder( new EmptyBorder( 8, 0, 0, 0 ) );
		
		headerPanel.add( lblTitle );
		headerPanel.add( lblDescription );
		
		add( headerPanel, BorderLayout.NORTH );
	}
	
	private void initializeContent()
	{
		contentPanel = new JPanel();
		contentPanel.setLayout( new BoxLayout( contentPanel, BoxLayout.Y_AXIS ) );
		contentPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
		
		buildContent( contentPanel );
		
		// Wrap in scroll pane for longer content
		JScrollPane scrollPane = new JScrollPane( contentPanel );
		scrollPane.setBorder( null );
		scrollPane.getVerticalScrollBar().setUnitIncrement( 16 );
		
		add( scrollPane, BorderLayout.CENTER );
	}
	
	/**
	 * @return The title displayed at the top of this step
	 */
	protected abstract String getStepTitle();
	
	/**
	 * @return The description text displayed below the title
	 */
	protected abstract String getStepDescription();
	
	/**
	 * Build the main content of this step.
	 * 
	 * @param container The panel to add components to
	 */
	protected abstract void buildContent( JPanel container );
	
	/**
	 * Validate the data entered in this step.
	 * 
	 * @return true if valid, false if validation failed (should show error message)
	 */
	public abstract boolean validateStep();
	
	/**
	 * Save the data from this step to the configuration.
	 */
	public abstract void saveStepData();
	
	/**
	 * Called before this step is displayed. Override to update content dynamically.
	 */
	public void prepareStep()
	{
		// Default implementation does nothing
	}
	
	// Utility methods for building consistent UI
	
	protected JPanel createFieldPanel( String labelText, JComponent field )
	{
		JPanel panel = new JPanel( new BorderLayout( 10, 5 ) );
		panel.setAlignmentX( Component.LEFT_ALIGNMENT );
		panel.setMaximumSize( new Dimension( Integer.MAX_VALUE, 60 ) );
		
		JLabel label = new JLabel( labelText );
		label.setFont( label.getFont().deriveFont( Font.BOLD, 12f ) );
		label.setPreferredSize( new Dimension( 140, 25 ) );
		
		panel.add( label, BorderLayout.WEST );
		panel.add( field, BorderLayout.CENTER );
		
		return panel;
	}
	
	protected JPanel createVerticalFieldPanel( String labelText, JComponent field )
	{
		JPanel panel = new JPanel();
		panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
		panel.setAlignmentX( Component.LEFT_ALIGNMENT );
		
		JLabel label = new JLabel( labelText );
		label.setFont( label.getFont().deriveFont( Font.BOLD, 12f ) );
		label.setAlignmentX( Component.LEFT_ALIGNMENT );
		
		field.setAlignmentX( Component.LEFT_ALIGNMENT );
		if( field instanceof JTextField )
		{
			field.setMaximumSize( new Dimension( Integer.MAX_VALUE, 30 ) );
		}
		
		panel.add( label );
		panel.add( Box.createVerticalStrut( 5 ) );
		panel.add( field );
		
		return panel;
	}
	
	protected JPanel createCheckboxPanel( JCheckBox checkbox, String description )
	{
		JPanel panel = new JPanel( new BorderLayout( 10, 0 ) );
		panel.setAlignmentX( Component.LEFT_ALIGNMENT );
		panel.setMaximumSize( new Dimension( Integer.MAX_VALUE, 50 ) );
		
		checkbox.setFont( checkbox.getFont().deriveFont( Font.BOLD, 12f ) );
		
		JLabel descLabel = new JLabel( "<html><body style='width: 350px; color: #666666'>" + description + "</body></html>" );
		descLabel.setFont( descLabel.getFont().deriveFont( 11f ) );
		descLabel.setBorder( new EmptyBorder( 0, 24, 0, 0 ) );
		
		panel.add( checkbox, BorderLayout.NORTH );
		panel.add( descLabel, BorderLayout.CENTER );
		
		return panel;
	}
	
	protected void showValidationError( String message )
	{
		JOptionPane.showMessageDialog( this, message, "Validation Error", JOptionPane.WARNING_MESSAGE );
	}
	
	protected Component createVerticalSpacer( int height )
	{
		return Box.createVerticalStrut( height );
	}
}