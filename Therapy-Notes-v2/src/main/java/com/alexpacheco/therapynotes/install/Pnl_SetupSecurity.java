package com.alexpacheco.therapynotes.install;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.alexpacheco.therapynotes.controller.PinManager;

import java.awt.*;
import java.util.Arrays;

/**
 * Security setup step - configures PIN protection for application access.
 */
public class Pnl_SetupSecurity extends AbstractSetupStepPanel
{
	private static final long serialVersionUID = 8584755010484697012L;
	private JCheckBox chkEnablePin;
	private JPasswordField txtPin;
	private JPasswordField txtConfirmPin;
	private JTextField txtHint;
	private JCheckBox chkShowPin;
	private JPanel pinFieldsPanel;
	private JLabel lblStrength;
	private JProgressBar strengthBar;
	private JLabel lblMatch;
	
	public Pnl_SetupSecurity( SetupConfiguration config )
	{
		super( config );
	}
	
	@Override
	protected String getStepTitle()
	{
		return "Security Settings";
	}
	
	@Override
	protected String getStepDescription()
	{
		return "Protect your client data with a PIN. This adds a layer of security to prevent unauthorized access to the application.";
	}
	
	@Override
	protected void buildContent( JPanel container )
	{
		// Enable PIN checkbox
		chkEnablePin = new JCheckBox( "Enable PIN protection (recommended)" );
		chkEnablePin.setFont( chkEnablePin.getFont().deriveFont( Font.BOLD, 13f ) );
		chkEnablePin.setSelected( true );
		chkEnablePin.setAlignmentX( Component.LEFT_ALIGNMENT );
		
		container.add( chkEnablePin );
		container.add( createVerticalSpacer( 5 ) );
		
		// Security note
		JLabel securityNote = new JLabel( "<html><body style='width: 420px; color: #666666; font-size: 11px;'>"
				+ "A PIN prevents unauthorized access to the application. For HIPAA compliance, protecting access to client data is essential. Please note that this PIN only protects application access."
				+ "</body></html>" );
		securityNote.setAlignmentX( Component.LEFT_ALIGNMENT );
		securityNote.setBorder( new EmptyBorder( 0, 24, 0, 0 ) );
		container.add( securityNote );
		container.add( createVerticalSpacer( 20 ) );
		
		// Info panel
		JPanel infoPanel = new JPanel( new BorderLayout() );
		infoPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
		infoPanel.setBackground( new Color( 240, 248, 255 ) );
		infoPanel.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( new Color( 173, 216, 230 ), 1 ),
				new EmptyBorder( 12, 15, 12, 15 ) ) );
		infoPanel.setMaximumSize( new Dimension( Integer.MAX_VALUE, 100 ) );
		
		JLabel infoLabel = new JLabel(
				"<html><body style='width: 400px'>" + "<b>Tips for a strong PIN:</b><br>" + "• Use at least 6 characters<br>"
						+ "• Mix numbers and letters<br>" + "• Avoid obvious patterns (1234, 0000)" + "</body></html>" );
		infoLabel.setFont( infoLabel.getFont().deriveFont( 11f ) );
		infoPanel.add( infoLabel, BorderLayout.CENTER );
		
		container.add( infoPanel );
		container.add( createVerticalSpacer( 20 ) );
		
		// PIN fields panel
		pinFieldsPanel = new JPanel();
		pinFieldsPanel.setLayout( new BoxLayout( pinFieldsPanel, BoxLayout.Y_AXIS ) );
		pinFieldsPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
		pinFieldsPanel.setBorder( new EmptyBorder( 0, 25, 0, 0 ) );
		
		// PIN entry
		JPanel pinPanel = new JPanel();
		pinPanel.setLayout( new BoxLayout( pinPanel, BoxLayout.Y_AXIS ) );
		pinPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
		
		JLabel lblPin = new JLabel( "Enter PIN" );
		lblPin.setFont( lblPin.getFont().deriveFont( Font.BOLD, 12f ) );
		lblPin.setAlignmentX( Component.LEFT_ALIGNMENT );
		
		txtPin = new JPasswordField( 20 );
		txtPin.setMaximumSize( new Dimension( 250, 30 ) );
		txtPin.setAlignmentX( Component.LEFT_ALIGNMENT );
		txtPin.setToolTipText( "Enter a PIN (minimum 4 characters)" );
		
		pinPanel.add( lblPin );
		pinPanel.add( Box.createVerticalStrut( 5 ) );
		pinPanel.add( txtPin );
		
		pinFieldsPanel.add( pinPanel );
		pinFieldsPanel.add( Box.createVerticalStrut( 8 ) );
		
		// Strength indicator
		JPanel strengthPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
		strengthPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
		strengthPanel.setMaximumSize( new Dimension( Integer.MAX_VALUE, 25 ) );
		
		strengthBar = new JProgressBar( 0, 4 );
		strengthBar.setPreferredSize( new Dimension( 120, 8 ) );
		strengthBar.setBorderPainted( false );
		strengthBar.setStringPainted( false );
		
		lblStrength = new JLabel( "" );
		lblStrength.setFont( lblStrength.getFont().deriveFont( 11f ) );
		lblStrength.setBorder( new EmptyBorder( 0, 10, 0, 0 ) );
		
		strengthPanel.add( strengthBar );
		strengthPanel.add( lblStrength );
		
		pinFieldsPanel.add( strengthPanel );
		pinFieldsPanel.add( Box.createVerticalStrut( 15 ) );
		
		// Confirm PIN
		JPanel confirmPanel = new JPanel();
		confirmPanel.setLayout( new BoxLayout( confirmPanel, BoxLayout.Y_AXIS ) );
		confirmPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
		
		JLabel lblConfirm = new JLabel( "Confirm PIN" );
		lblConfirm.setFont( lblConfirm.getFont().deriveFont( Font.BOLD, 12f ) );
		lblConfirm.setAlignmentX( Component.LEFT_ALIGNMENT );
		
		txtConfirmPin = new JPasswordField( 20 );
		txtConfirmPin.setMaximumSize( new Dimension( 250, 30 ) );
		txtConfirmPin.setAlignmentX( Component.LEFT_ALIGNMENT );
		
		confirmPanel.add( lblConfirm );
		confirmPanel.add( Box.createVerticalStrut( 5 ) );
		confirmPanel.add( txtConfirmPin );
		
		pinFieldsPanel.add( confirmPanel );
		pinFieldsPanel.add( Box.createVerticalStrut( 5 ) );
		
		// Match indicator
		lblMatch = new JLabel( " " );
		lblMatch.setFont( lblMatch.getFont().deriveFont( 11f ) );
		lblMatch.setAlignmentX( Component.LEFT_ALIGNMENT );
		pinFieldsPanel.add( lblMatch );
		pinFieldsPanel.add( Box.createVerticalStrut( 10 ) );
		
		// Show PIN checkbox
		chkShowPin = new JCheckBox( "Show PIN" );
		chkShowPin.setAlignmentX( Component.LEFT_ALIGNMENT );
		chkShowPin.setFont( chkShowPin.getFont().deriveFont( 11f ) );
		pinFieldsPanel.add( chkShowPin );
		pinFieldsPanel.add( Box.createVerticalStrut( 20 ) );
		
		// Hint field
		JPanel hintPanel = new JPanel();
		hintPanel.setLayout( new BoxLayout( hintPanel, BoxLayout.Y_AXIS ) );
		hintPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
		
		JLabel lblHint = new JLabel( "PIN Hint (optional)" );
		lblHint.setFont( lblHint.getFont().deriveFont( Font.BOLD, 12f ) );
		lblHint.setAlignmentX( Component.LEFT_ALIGNMENT );
		
		txtHint = new JTextField( 30 );
		txtHint.setMaximumSize( new Dimension( 350, 30 ) );
		txtHint.setAlignmentX( Component.LEFT_ALIGNMENT );
		txtHint.setToolTipText( "A hint to help you remember your PIN (don't make it too obvious!)" );
		
		JLabel hintNote = new JLabel( "<html><body style='color: #888888; font-size: 10px;'>"
				+ "This hint will be shown after failed PIN attempts" + "</body></html>" );
		hintNote.setAlignmentX( Component.LEFT_ALIGNMENT );
		
		hintPanel.add( lblHint );
		hintPanel.add( Box.createVerticalStrut( 5 ) );
		hintPanel.add( txtHint );
		hintPanel.add( Box.createVerticalStrut( 3 ) );
		hintPanel.add( hintNote );
		
		pinFieldsPanel.add( hintPanel );
		
		container.add( pinFieldsPanel );
		
		// Setup event handlers
		setupEventHandlers();
		
		// Initial state
		updatePinFieldsEnabled( true );
	}
	
	private void setupEventHandlers()
	{
		// Toggle PIN fields enabled
		chkEnablePin.addActionListener( e ->
		{
			updatePinFieldsEnabled( chkEnablePin.isSelected() );
		} );
		
		// Show/hide PIN
		chkShowPin.addActionListener( e ->
		{
			char echoChar = chkShowPin.isSelected() ? (char) 0 : '•';
			txtPin.setEchoChar( echoChar );
			txtConfirmPin.setEchoChar( echoChar );
		} );
		
		// PIN strength and match checking
		DocumentListener pinListener = new DocumentListener()
		{
			@Override
			public void insertUpdate( DocumentEvent e )
			{
				updateStrengthAndMatch();
			}
			
			@Override
			public void removeUpdate( DocumentEvent e )
			{
				updateStrengthAndMatch();
			}
			
			@Override
			public void changedUpdate( DocumentEvent e )
			{
				updateStrengthAndMatch();
			}
		};
		
		txtPin.getDocument().addDocumentListener( pinListener );
		txtConfirmPin.getDocument().addDocumentListener( pinListener );
	}
	
	private void updatePinFieldsEnabled( boolean enabled )
	{
		txtPin.setEnabled( enabled );
		txtConfirmPin.setEnabled( enabled );
		txtHint.setEnabled( enabled );
		chkShowPin.setEnabled( enabled );
		strengthBar.setEnabled( enabled );
		
		if( !enabled )
		{
			txtPin.setText( "" );
			txtConfirmPin.setText( "" );
			txtHint.setText( "" );
			lblStrength.setText( "" );
			lblMatch.setText( " " );
			strengthBar.setValue( 0 );
		}
		
		// Visual dimming
		pinFieldsPanel.setEnabled( enabled );
		for( Component c : pinFieldsPanel.getComponents() )
		{
			setComponentTreeEnabled( c, enabled );
		}
	}
	
	private void setComponentTreeEnabled( Component component, boolean enabled )
	{
		component.setEnabled( enabled );
		if( component instanceof Container )
		{
			for( Component child : ( (Container) component ).getComponents() )
			{
				setComponentTreeEnabled( child, enabled );
			}
		}
	}
	
	private void updateStrengthAndMatch()
	{
		char[] pin = txtPin.getPassword();
		char[] confirm = txtConfirmPin.getPassword();
		
		// Update strength indicator
		if( pin.length > 0 )
		{
			PinManager.PinStrength strength = PinManager.evaluateStrength( pin );
			lblStrength.setText( strength.getLabel() );
			lblStrength.setForeground( strength.getColor() );
			strengthBar.setForeground( strength.getColor() );
			strengthBar.setValue( strength.ordinal() );
		}
		else
		{
			lblStrength.setText( "" );
			strengthBar.setValue( 0 );
		}
		
		// Update match indicator
		if( confirm.length > 0 )
		{
			if( Arrays.equals( pin, confirm ) )
			{
				lblMatch.setText( "✓ PINs match" );
				lblMatch.setForeground( new Color( 76, 175, 80 ) );
			}
			else
			{
				lblMatch.setText( "✗ PINs do not match" );
				lblMatch.setForeground( new Color( 244, 67, 54 ) );
			}
		}
		else
		{
			lblMatch.setText( " " );
		}
		
		// Clear sensitive data
		Arrays.fill( pin, '\0' );
		Arrays.fill( confirm, '\0' );
	}
	
	@Override
	public boolean validateStep()
	{
		if( !chkEnablePin.isSelected() )
		{
			// Confirm user wants to skip PIN
			int result = JOptionPane.showConfirmDialog( this,
					"Are you sure you want to skip PIN protection?\n\n" + "Without a PIN, anyone with access to your computer can open\n"
							+ "the application and view client records.",
					"Skip PIN Protection", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE );
			
			return result == JOptionPane.YES_OPTION;
		}
		
		char[] pin = txtPin.getPassword();
		char[] confirm = txtConfirmPin.getPassword();
		
		try
		{
			// Check minimum length
			if( pin.length < 4 )
			{
				showValidationError( "PIN must be at least 4 characters." );
				txtPin.requestFocus();
				return false;
			}
			
			// Check maximum length
			if( pin.length > 32 )
			{
				showValidationError( "PIN cannot exceed 32 characters." );
				txtPin.requestFocus();
				return false;
			}
			
			// Check match
			if( !Arrays.equals( pin, confirm ) )
			{
				showValidationError( "PINs do not match. Please re-enter." );
				txtConfirmPin.requestFocus();
				txtConfirmPin.selectAll();
				return false;
			}
			
			// Warn about weak PIN
			PinManager.PinStrength strength = PinManager.evaluateStrength( pin );
			if( strength == PinManager.PinStrength.WEAK )
			{
				int result = JOptionPane.showConfirmDialog( this,
						"Your PIN is weak and may be easy to guess.\n\n" + "Are you sure you want to use this PIN?", "Weak PIN",
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE );
				
				if( result != JOptionPane.YES_OPTION )
				{
					txtPin.requestFocus();
					txtPin.selectAll();
					return false;
				}
			}
			
			return true;
			
		}
		finally
		{
			// Clear sensitive data
			Arrays.fill( pin, '\0' );
			Arrays.fill( confirm, '\0' );
		}
	}
	
	@Override
	public void saveStepData()
	{
		config.setPinEnabled( chkEnablePin.isSelected() );
		
		if( chkEnablePin.isSelected() )
		{
			char[] pin = txtPin.getPassword();
			String hint = txtHint.getText().trim();
			
			try
			{
				// Store PIN using PinManager (handles hashing)
				PinManager.setupPin( pin, hint );
			}
			finally
			{
				// Clear sensitive data
				Arrays.fill( pin, '\0' );
				txtPin.setText( "" );
				txtConfirmPin.setText( "" );
			}
		}
	}
	
	@Override
	public void prepareStep()
	{
		// Clear fields when entering this step
		txtPin.setText( "" );
		txtConfirmPin.setText( "" );
		lblMatch.setText( " " );
		strengthBar.setValue( 0 );
		lblStrength.setText( "" );
	}
}