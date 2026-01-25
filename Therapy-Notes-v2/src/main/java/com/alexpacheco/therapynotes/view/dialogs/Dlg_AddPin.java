package com.alexpacheco.therapynotes.view.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.alexpacheco.therapynotes.controller.AppController;
import com.alexpacheco.therapynotes.controller.PinManager;
import com.alexpacheco.therapynotes.util.AppFonts;
import com.alexpacheco.therapynotes.util.AppLogger;

/**
 * Dialog for setting up a new PIN when none exists. (For use from Settings menu when PIN was skipped during setup)
 */
public class Dlg_AddPin extends JDialog
{
	private static final long serialVersionUID = -1435849152739681570L;
	private JPasswordField txtPin;
	private JPasswordField txtConfirmPin;
	private JTextField txtHint;
	private JLabel lblStrength;
	private JProgressBar strengthBar;
	private JLabel lblMatch;
	private JButton btnSetup;
	private JButton btnCancel;
	
	private boolean pinSetup = false;
	
	public Dlg_AddPin( Frame parent )
	{
		super( parent, "Add PIN", true );
		
		initializeComponents();
		layoutComponents();
		setupEventHandlers();
		
		setSize( 400, 420 );
		setResizable( false );
		setLocationRelativeTo( parent );
	}
	
	private void initializeComponents()
	{
		txtPin = new JPasswordField( 20 );
		txtConfirmPin = new JPasswordField( 20 );
		txtHint = new JTextField( 25 );
		
		strengthBar = new JProgressBar( 0, 4 );
		strengthBar.setPreferredSize( new Dimension( 100, 8 ) );
		strengthBar.setBorderPainted( false );
		
		lblStrength = new JLabel( "" );
		lblStrength.setFont( AppFonts.getSmallFont() );
		
		lblMatch = new JLabel( " " );
		lblMatch.setFont( AppFonts.getSmallFont() );
		
		btnSetup = new JButton( "Enable PIN" );
		btnCancel = new JButton( "Cancel" );
	}
	
	private void layoutComponents()
	{
		setLayout( new BorderLayout() );
		setBackground( AppController.getBackgroundColor() );
		
		// Info panel at top
		JPanel infoPanel = new JPanel( new BorderLayout() );
		infoPanel.setBackground( new Color( 232, 245, 233 ) );
		infoPanel.setBorder( new EmptyBorder( 15, 20, 15, 20 ) );
		
		JLabel infoLabel = new JLabel( "<html><body style='width: 320px'>" + "<b>Protect your client data</b><br>"
				+ "A PIN prevents unauthorized access when your computer is unattended." + "</body></html>" );
		infoPanel.add( infoLabel );
		
		add( infoPanel, BorderLayout.NORTH );
		
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout( new BoxLayout( contentPanel, BoxLayout.Y_AXIS ) );
		contentPanel.setBorder( new EmptyBorder( 20, 30, 10, 30 ) );
		contentPanel.setBackground( AppController.getBackgroundColor() );
		
		// PIN field
		contentPanel.add( createFieldPanel( "Enter PIN", txtPin ) );
		contentPanel.add( Box.createVerticalStrut( 5 ) );
		
		// Strength
		JPanel strengthPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
		strengthPanel.setBackground( AppController.getBackgroundColor() );
		strengthPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
		strengthPanel.add( strengthBar );
		strengthPanel.add( Box.createHorizontalStrut( 10 ) );
		strengthPanel.add( lblStrength );
		contentPanel.add( strengthPanel );
		contentPanel.add( Box.createVerticalStrut( 15 ) );
		
		// Confirm
		contentPanel.add( createFieldPanel( "Confirm PIN", txtConfirmPin ) );
		contentPanel.add( Box.createVerticalStrut( 5 ) );
		
		lblMatch.setAlignmentX( Component.LEFT_ALIGNMENT );
		contentPanel.add( lblMatch );
		contentPanel.add( Box.createVerticalStrut( 15 ) );
		
		// Hint
		contentPanel.add( createFieldPanel( "PIN Hint (optional)", txtHint ) );
		
		add( contentPanel, BorderLayout.CENTER );
		
		// Buttons
		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 10, 0 ) );
		buttonPanel.setBorder( new EmptyBorder( 15, 30, 20, 30 ) );
		buttonPanel.setBackground( AppController.getBackgroundColor() );
		buttonPanel.add( btnCancel );
		buttonPanel.add( btnSetup );
		
		add( buttonPanel, BorderLayout.SOUTH );
	}
	
	private JPanel createFieldPanel( String label, JComponent field )
	{
		JPanel panel = new JPanel();
		panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
		panel.setAlignmentX( Component.LEFT_ALIGNMENT );
		panel.setBackground( AppController.getBackgroundColor() );
		
		JLabel lbl = new JLabel( label );
		lbl.setFont( AppFonts.getLabelBoldFont() );
		lbl.setAlignmentX( Component.LEFT_ALIGNMENT );
		
		field.setAlignmentX( Component.LEFT_ALIGNMENT );
		field.setMaximumSize( new Dimension( Integer.MAX_VALUE, 30 ) );
		
		panel.add( lbl );
		panel.add( Box.createVerticalStrut( 5 ) );
		panel.add( field );
		
		return panel;
	}
	
	private void setupEventHandlers()
	{
		btnCancel.addActionListener( e -> dispose() );
		
		btnSetup.addActionListener( e -> attemptSetup() );
		
		javax.swing.event.DocumentListener pinListener = new javax.swing.event.DocumentListener()
		{
			@Override
			public void insertUpdate( javax.swing.event.DocumentEvent e )
			{
				updateUI();
			}
			
			@Override
			public void removeUpdate( javax.swing.event.DocumentEvent e )
			{
				updateUI();
			}
			
			@Override
			public void changedUpdate( javax.swing.event.DocumentEvent e )
			{
				updateUI();
			}
		};
		
		txtPin.getDocument().addDocumentListener( pinListener );
		txtConfirmPin.getDocument().addDocumentListener( pinListener );
	}
	
	private void updateUI()
	{
		char[] pin = txtPin.getPassword();
		char[] confirm = txtConfirmPin.getPassword();
		
		if( pin.length > 0 )
		{
			PinManager.PinStrength strength = PinManager.evaluateStrength( pin );
			lblStrength.setText( strength.getLabel() );
			lblStrength.setForeground( strength.getColor() );
			strengthBar.setValue( strength.ordinal() );
			strengthBar.setForeground( strength.getColor() );
		}
		else
		{
			lblStrength.setText( "" );
			strengthBar.setValue( 0 );
		}
		
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
		
		Arrays.fill( pin, '\0' );
		Arrays.fill( confirm, '\0' );
	}
	
	private void attemptSetup()
	{
		char[] pin = txtPin.getPassword();
		char[] confirm = txtConfirmPin.getPassword();
		
		try
		{
			if( pin.length < 4 )
			{
				AppController.showBasicErrorPopup( "PIN must be at least 4 characters." );
				txtPin.requestFocus();
				return;
			}
			
			if( !Arrays.equals( pin, confirm ) )
			{
				AppController.showBasicErrorPopup( "PINs do not match." );
				txtConfirmPin.requestFocus();
				return;
			}
			
			String hint = txtHint.getText().trim();
			PinManager.setupPin( pin, hint );
			
			JOptionPane.showMessageDialog( this,
					"PIN protection is now enabled.\n\n" + "You will be asked for this PIN each time you open the application.",
					"PIN Enabled", JOptionPane.INFORMATION_MESSAGE );
			
			pinSetup = true;
			dispose();
		}
		finally
		{
			Arrays.fill( pin, '\0' );
			Arrays.fill( confirm, '\0' );
			AppController.updateMenu();
		}
	}
	
	public boolean isPinSetup()
	{
		return pinSetup;
	}
	
	/**
	 * Show the setup PIN dialog.
	 * 
	 * @return true if PIN was set up
	 */
	public static boolean show( Frame parent )
	{
		Dlg_AddPin dialog = new Dlg_AddPin( parent );
		dialog.setVisible( true );
		AppLogger.logDialogOpened( "Add PIN" );
		return dialog.isPinSetup();
	}
}
