package main.java.com.alexpacheco.therapynotes.install;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * First-run setup wizard dialog that guides users through initial application configuration. Manages navigation between setup steps and
 * collects configuration data.
 */
public class SetupWizardDialog extends JDialog
{
	private static final long serialVersionUID = -4327683999838092346L;
	private final SetupConfiguration config;
	private final List<AbstractSetupStepPanel> steps;
	private int currentStepIndex = 0;
	
	private JPanel stepContainer;
	private CardLayout cardLayout;
	private JButton btnBack;
	private JButton btnNext;
	private JButton btnCancel;
	private JPanel progressPanel;
	private boolean setupCompleted = false;
	
	public SetupWizardDialog( Frame parent )
	{
		super( parent, "Setup Wizard", true );
		this.config = new SetupConfiguration();
		this.steps = new ArrayList<>();
		
		initializeSteps();
		initializeComponents();
		layoutComponents();
		setupEventHandlers();
		
		setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
		setSize( 700, 600 );
		setMinimumSize( new Dimension( 700, 600 ) );
		setLocationRelativeTo( parent );
		
		updateNavigationState();
		updateProgressIndicator();
	}
	
	private void initializeSteps()
	{
		steps.add( new Pnl_SetupWelcome( config ) );
		steps.add( new Pnl_SetupPracticeInfo( config ) );
		steps.add( new Pnl_SetupSecurity( config ) );
		steps.add( new Pnl_SetupDatabaseLocation( config ) );
		steps.add( new Pnl_SetupComplete( config ) );
	}
	
	private void initializeComponents()
	{
		cardLayout = new CardLayout();
		stepContainer = new JPanel( cardLayout );
		stepContainer.setBorder( new EmptyBorder( 20, 30, 10, 30 ) );
		
		for( int i = 0; i < steps.size(); i++ )
		{
			stepContainer.add( steps.get( i ), "step" + i );
		}
		
		btnBack = new JButton( "← Back" );
		btnNext = new JButton( "Next →" );
		btnCancel = new JButton( "Cancel" );
		
		btnBack.setPreferredSize( new Dimension( 100, 32 ) );
		btnNext.setPreferredSize( new Dimension( 100, 32 ) );
		btnCancel.setPreferredSize( new Dimension( 100, 32 ) );
		
		progressPanel = createProgressPanel();
	}
	
	private JPanel createProgressPanel()
	{
		JPanel panel = new JPanel( new FlowLayout( FlowLayout.CENTER, 15, 10 ) );
		panel.setBackground( new Color( 245, 247, 250 ) );
		panel.setBorder( BorderFactory.createMatteBorder( 0, 0, 1, 0, new Color( 220, 220, 220 ) ) );
		return panel;
	}
	
	private void updateProgressIndicator()
	{
		progressPanel.removeAll();
		
		String[] stepNames = { "Welcome", "Practice", "Security", "Database", "Complete" };
		
		for( int i = 0; i < steps.size(); i++ )
		{
			JLabel stepLabel = new JLabel( stepNames[i] );
			stepLabel.setFont( stepLabel.getFont().deriveFont( 11f ) );
			
			if( i == currentStepIndex )
			{
				stepLabel.setForeground( new Color( 41, 98, 255 ) );
				stepLabel.setFont( stepLabel.getFont().deriveFont( Font.BOLD, 11f ) );
			}
			else if( i < currentStepIndex )
			{
				stepLabel.setForeground( new Color( 76, 175, 80 ) );
			}
			else
			{
				stepLabel.setForeground( Color.GRAY );
			}
			
			progressPanel.add( stepLabel );
			
			if( i < steps.size() - 1 )
			{
				JLabel separator = new JLabel( "›" );
				separator.setForeground( Color.LIGHT_GRAY );
				progressPanel.add( separator );
			}
		}
		
		progressPanel.revalidate();
		progressPanel.repaint();
	}
	
	private void layoutComponents()
	{
		setLayout( new BorderLayout() );
		
		add( progressPanel, BorderLayout.NORTH );
		add( stepContainer, BorderLayout.CENTER );
		
		JPanel buttonPanel = new JPanel( new BorderLayout() );
		buttonPanel.setBorder( new EmptyBorder( 15, 30, 20, 30 ) );
		
		JPanel leftButtons = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
		leftButtons.add( btnCancel );
		
		JPanel rightButtons = new JPanel( new FlowLayout( FlowLayout.RIGHT, 10, 0 ) );
		rightButtons.add( btnBack );
		rightButtons.add( btnNext );
		
		buttonPanel.add( leftButtons, BorderLayout.WEST );
		buttonPanel.add( rightButtons, BorderLayout.EAST );
		
		add( buttonPanel, BorderLayout.SOUTH );
	}
	
	private void setupEventHandlers()
	{
		btnBack.addActionListener( e -> navigateBack() );
		btnNext.addActionListener( e -> navigateNext() );
		btnCancel.addActionListener( e -> handleCancel() );
		
		addWindowListener( new WindowAdapter()
		{
			@Override
			public void windowClosing( WindowEvent e )
			{
				handleCancel();
			}
		} );
	}
	
	private void navigateBack()
	{
		if( currentStepIndex > 0 )
		{
			currentStepIndex--;
			cardLayout.show( stepContainer, "step" + currentStepIndex );
			updateNavigationState();
			updateProgressIndicator();
		}
	}
	
	private void navigateNext()
	{
		AbstractSetupStepPanel currentStep = steps.get( currentStepIndex );
		
		// Validate current step
		if( !currentStep.validateStep() )
		{
			return;
		}
		
		// Save current step data
		currentStep.saveStepData();
		
		if( currentStepIndex < steps.size() - 1 )
		{
			currentStepIndex++;
			
			// Prepare next step (allows dynamic content based on config)
			steps.get( currentStepIndex ).prepareStep();
			
			cardLayout.show( stepContainer, "step" + currentStepIndex );
			updateNavigationState();
			updateProgressIndicator();
		}
		else
		{
			// Final step - complete setup
			completeSetup();
		}
	}
	
	private void updateNavigationState()
	{
		btnBack.setEnabled( currentStepIndex > 0 );
		
		boolean isLastStep = currentStepIndex == steps.size() - 1;
		btnNext.setText( isLastStep ? "Finish ✓" : "Next →" );
		
		// Hide cancel on completion step
		btnCancel.setVisible( currentStepIndex < steps.size() - 1 );
	}
	
	private void handleCancel()
	{
		if( currentStepIndex == steps.size() - 1 )
		{
			// On completion step, just close
			dispose();
			return;
		}
		
		int result = JOptionPane.showConfirmDialog( this, "Are you sure you want to cancel setup?", "Cancel Setup",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );
		
		if( result == JOptionPane.YES_OPTION )
		{
			setupCompleted = false;
			dispose();
		}
	}
	
	private void completeSetup()
	{
		try
		{
			// Save configuration to persistent storage
			SetupConfigurationManager.saveConfiguration( config );
			
			// Mark first run as complete
			SetupConfigurationManager.markSetupComplete();
			
			setupCompleted = true;
			
			JOptionPane.showMessageDialog( this, "Setup complete! Your Notes Management System is ready to use.", "Setup Complete",
					JOptionPane.INFORMATION_MESSAGE );
			
			dispose();
			
		}
		catch( Exception e )
		{
			JOptionPane.showMessageDialog( this, "Failed to save configuration: " + e.getMessage(), "Setup Error",
					JOptionPane.ERROR_MESSAGE );
		}
	}
	
	public boolean isSetupCompleted()
	{
		return setupCompleted;
	}
	
	public SetupConfiguration getConfiguration()
	{
		return config;
	}
	
	/**
	 * Shows the setup wizard if this is a first run.
	 * 
	 * @param parent The parent frame
	 * @return true if setup completed successfully or was already done, false if cancelled
	 */
	public static boolean showIfFirstRun( Frame parent )
	{
		if( SetupConfigurationManager.isSetupComplete() )
		{
			return true;
		}
		
		SetupWizardDialog wizard = new SetupWizardDialog( parent );
		wizard.setVisible( true );
		
		return wizard.isSetupCompleted();
	}
}