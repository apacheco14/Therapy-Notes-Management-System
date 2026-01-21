package main.java.com.alexpacheco.therapynotes.install;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import main.java.com.alexpacheco.therapynotes.util.JavaUtils;

import java.awt.*;

/**
 * Setup Complete step - shows summary of configuration and next steps.
 */
public class Pnl_SetupComplete extends AbstractSetupStepPanel
{
	private static final long serialVersionUID = -3591004024931441542L;
	private JPanel summaryPanel;
	
	public Pnl_SetupComplete( SetupConfiguration config )
	{
		super( config );
	}
	
	@Override
	protected String getStepTitle()
	{
		return "Setup Complete!";
	}
	
	@Override
	protected String getStepDescription()
	{
		return "Your notes management system is ready to use. Here's a summary of your configuration.";
	}
	
	@Override
	protected void buildContent( JPanel container )
	{
		// Success icon and message
		JPanel successPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
		successPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
		
		JLabel successIcon = new JLabel( "✓" );
		successIcon.setFont( successIcon.getFont().deriveFont( Font.BOLD, 32f ) );
		successIcon.setForeground( new Color( 76, 175, 80 ) );
		
		JLabel successText = new JLabel( "  All set!" );
		successText.setFont( successText.getFont().deriveFont( Font.BOLD, 18f ) );
		successText.setForeground( new Color( 76, 175, 80 ) );
		
		successPanel.add( successIcon );
		successPanel.add( successText );
		container.add( successPanel );
		container.add( createVerticalSpacer( 20 ) );
		
		// Configuration summary
		summaryPanel = new JPanel();
		summaryPanel.setLayout( new BoxLayout( summaryPanel, BoxLayout.Y_AXIS ) );
		summaryPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
		summaryPanel.setBackground( new Color( 250, 250, 250 ) );
		summaryPanel.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( new Color( 220, 220, 220 ), 1 ),
				new EmptyBorder( 15, 20, 15, 20 ) ) );
		summaryPanel.setMaximumSize( new Dimension( Integer.MAX_VALUE, 200 ) );
		
		JLabel summaryTitle = new JLabel( "Configuration Summary" );
		summaryTitle.setFont( summaryTitle.getFont().deriveFont( Font.BOLD, 13f ) );
		summaryTitle.setAlignmentX( Component.LEFT_ALIGNMENT );
		summaryPanel.add( summaryTitle );
		summaryPanel.add( Box.createVerticalStrut( 12 ) );
		
		// Summary items will be populated in prepareStep()
		container.add( summaryPanel );
		container.add( createVerticalSpacer( 25 ) );
		
		// Getting started section
		JLabel gettingStartedTitle = new JLabel( "Getting Started" );
		gettingStartedTitle.setFont( gettingStartedTitle.getFont().deriveFont( Font.BOLD, 14f ) );
		gettingStartedTitle.setAlignmentX( Component.LEFT_ALIGNMENT );
		container.add( gettingStartedTitle );
		container.add( createVerticalSpacer( 12 ) );
		
		String[] tips = { "Add your first client from the Clients menu", "Create session notes from the Notes menu",
				"View the Help documentation for detailed guidance on all features" };
		
		for( int i = 0; i < tips.length; i++ )
		{
			JPanel tipPanel = createTipItem( i + 1, tips[i] );
			container.add( tipPanel );
			container.add( createVerticalSpacer( 8 ) );
		}
	}
	
	private JPanel createTipItem( int number, String text )
	{
		JPanel panel = new JPanel( new BorderLayout( 10, 0 ) );
		panel.setAlignmentX( Component.LEFT_ALIGNMENT );
		panel.setMaximumSize( new Dimension( Integer.MAX_VALUE, 25 ) );
		
		JLabel numLabel = new JLabel( number + "." );
		numLabel.setFont( numLabel.getFont().deriveFont( Font.BOLD ) );
		numLabel.setForeground( new Color( 41, 98, 255 ) );
		numLabel.setPreferredSize( new Dimension( 20, 20 ) );
		
		JLabel textLabel = new JLabel( text );
		textLabel.setFont( textLabel.getFont().deriveFont( 12f ) );
		
		panel.add( numLabel, BorderLayout.WEST );
		panel.add( textLabel, BorderLayout.CENTER );
		
		return panel;
	}
	
	private JPanel createSummaryRow( String label, String value )
	{
		JPanel row = new JPanel( new BorderLayout( 10, 0 ) );
		row.setOpaque( false );
		row.setAlignmentX( Component.LEFT_ALIGNMENT );
		row.setMaximumSize( new Dimension( Integer.MAX_VALUE, 22 ) );
		
		JLabel lblKey = new JLabel( label + ":" );
		lblKey.setFont( lblKey.getFont().deriveFont( 11f ) );
		lblKey.setForeground( new Color( 100, 100, 100 ) );
		lblKey.setPreferredSize( new Dimension( 120, 18 ) );
		
		JLabel lblValue = new JLabel( value );
		lblValue.setFont( lblValue.getFont().deriveFont( 11f ) );
		
		row.add( lblKey, BorderLayout.WEST );
		row.add( lblValue, BorderLayout.CENTER );
		
		return row;
	}
	
	@Override
	public void prepareStep()
	{
		// Rebuild summary with current config values
		// Remove old summary items (keep title)
		while( summaryPanel.getComponentCount() > 2 )
		{
			summaryPanel.remove( 2 );
		}
		
		// Add current configuration summary
		if( !JavaUtils.isNullOrEmpty( config.getPracticeName() ) )
		{
			summaryPanel.add( createSummaryRow( "Practice", config.getPracticeName() ) );
			summaryPanel.add( Box.createVerticalStrut( 4 ) );
		}
		
		if( !JavaUtils.isNullOrEmpty( config.getPractitionerName() ) )
		{
			summaryPanel.add( createSummaryRow( "Practitioner", config.getPractitionerName() ) );
			summaryPanel.add( Box.createVerticalStrut( 4 ) );
		}
		
		if( !JavaUtils.isNullOrEmpty( config.getLicenseNumber() ) )
		{
			summaryPanel.add( createSummaryRow( "License", config.getLicenseNumber() ) );
			summaryPanel.add( Box.createVerticalStrut( 4 ) );
		}
		
		String dbPath = truncatePath( config.getDatabasePath(), 100 ); // Truncate long paths for display
		summaryPanel.add( createSummaryRow( "Database", dbPath ) );
		summaryPanel.add( Box.createVerticalStrut( 4 ) );
		
		summaryPanel.add( createSummaryRow( "PIN Enabled", config.isPinEnabled() ? "Yes" : "No" ) );
		summaryPanel.add( Box.createVerticalStrut( 4 ) );
		
		summaryPanel.revalidate();
		summaryPanel.repaint();
	}
	
	private String truncatePath( String path, int maxLength )
	{
		if( path == null || path.length() <= maxLength )
		{
			return path;
		}
		
		// Show beginning and end of path
		int half = ( maxLength - 3 ) / 2;
		return path.substring( 0, half ) + "..." + path.substring( path.length() - half );
	}
	
	@Override
	public boolean validateStep()
	{
		return true; // Complete step always valid
	}
	
	@Override
	public void saveStepData()
	{
		// No additional data to save
	}
}