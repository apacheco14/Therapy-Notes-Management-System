package main.java.com.alexpacheco.therapynotes.install;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Welcome step - introduces the setup wizard and explains what will be configured.
 */
public class Pnl_SetupWelcome extends AbstractSetupStepPanel
{
	private static final long serialVersionUID = -3131162265054898452L;
	
	public Pnl_SetupWelcome( SetupConfiguration config )
	{
		super( config );
	}
	
	@Override
	protected String getStepTitle()
	{
		return "Welcome";
	}
	
	@Override
	protected String getStepDescription()
	{
		return "Let's set up your notes management system. This wizard will guide you through the initial configuration in just a few steps.";
	}
	
	@Override
	protected void buildContent( JPanel container )
	{
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout( new BoxLayout( infoPanel, BoxLayout.Y_AXIS ) );
		infoPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
		infoPanel.setBorder( new EmptyBorder( 20, 0, 20, 0 ) );
		
//		String[] steps = { "Practice Information – Your practice name and contact details",
//				"Database Location – Where your client data will be stored", "Backup Settings – Configure automatic backup preferences" };
		
		String[] steps = { "Practice Information – Your practice name and contact details",
				"Security – PIN enablement to prevent unauthorized application access",
				"Database Location – Where your client data will be stored" };
		
		JLabel setupLabel = new JLabel( "We'll configure:" );
		setupLabel.setFont( setupLabel.getFont().deriveFont( Font.BOLD, 13f ) );
		setupLabel.setAlignmentX( Component.LEFT_ALIGNMENT );
		infoPanel.add( setupLabel );
		infoPanel.add( Box.createVerticalStrut( 15 ) );
		
		for( int i = 0; i < steps.length; i++ )
		{
			JPanel stepItem = createStepItem( i + 1, steps[i] );
			infoPanel.add( stepItem );
			infoPanel.add( Box.createVerticalStrut( 12 ) );
		}
		
		container.add( infoPanel );
	}
	
	private JPanel createStepItem( int number, String text )
	{
		JPanel panel = new JPanel( new BorderLayout( 12, 0 ) );
		panel.setAlignmentX( Component.LEFT_ALIGNMENT );
		panel.setMaximumSize( new Dimension( Integer.MAX_VALUE, 30 ) );
		
		// Number circle
		JLabel numberLabel = new JLabel( String.valueOf( number ), SwingConstants.CENTER );
		numberLabel.setPreferredSize( new Dimension( 26, 26 ) );
		numberLabel.setOpaque( true );
		numberLabel.setBackground( new Color( 41, 98, 255 ) );
		numberLabel.setForeground( Color.WHITE );
		numberLabel.setFont( numberLabel.getFont().deriveFont( Font.BOLD, 12f ) );
		numberLabel.setBorder( BorderFactory.createEmptyBorder( 2, 0, 0, 0 ) );
		
		// Make it circular with custom painting
		JPanel circlePanel = new JPanel( new BorderLayout() )
		{
			private static final long serialVersionUID = 7825628874403860081L;
			
			@Override
			protected void paintComponent( Graphics g )
			{
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
				g2.setColor( new Color( 41, 98, 255 ) );
				g2.fillOval( 0, 0, 26, 26 );
				g2.dispose();
			}
		};
		circlePanel.setPreferredSize( new Dimension( 26, 26 ) );
		circlePanel.setOpaque( false );
		circlePanel.add( numberLabel );
		
		JLabel textLabel = new JLabel( text );
		textLabel.setFont( textLabel.getFont().deriveFont( 13f ) );
		
		panel.add( circlePanel, BorderLayout.WEST );
		panel.add( textLabel, BorderLayout.CENTER );
		
		return panel;
	}
	
	@Override
	public boolean validateStep()
	{
		return true; // Welcome step always valid
	}
	
	@Override
	public void saveStepData()
	{
		// No data to save
	}
}