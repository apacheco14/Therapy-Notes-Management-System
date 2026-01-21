package main.java.com.alexpacheco.therapynotes.view.dialogs;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import main.java.com.alexpacheco.therapynotes.controller.PinManager;
import java.awt.*;
import java.awt.event.*;

/**
 * PIN entry dialog shown on application startup when PIN protection is enabled. Features: - Clean, focused interface - Failed attempt
 * tracking with lockout display - Optional hint display after failed attempts - Auto-lock timeout reset
 */
public class Dlg_PinEntry extends JDialog
{
	private static final long serialVersionUID = -4609835405355960639L;
	private JPasswordField txtPin;
	private JButton btnUnlock;
	private JButton btnExit;
	private JLabel lblMessage;
	private JLabel lblHint;
	private JCheckBox chkShowPin;
	private JPanel lockoutPanel;
	private JLabel lblLockoutTime;
	private Timer lockoutTimer;
	
	private boolean authenticated = false;
	private int failedAttempts = 0;
	
	private static final int SHOW_HINT_AFTER_ATTEMPTS = 2;
	
	public Dlg_PinEntry( Frame parent )
	{
		super( parent, "Notes Management System", true );
		
		initializeComponents();
		layoutComponents();
		setupEventHandlers();
		
		setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
		setResizable( false );
		setSize( 400, 340 );
		setLocationRelativeTo( parent );
		
		// Check for existing lockout AFTER dialog is visible
		addWindowListener( new WindowAdapter()
		{
			@Override
			public void windowOpened( WindowEvent e )
			{
				if( PinManager.isLockedOut() )
				{
					showLockout();
				}
			}
		} );
	}
	
	private void initializeComponents()
	{
		txtPin = new JPasswordField( 20 );
		txtPin.setFont( new Font( Font.MONOSPACED, Font.PLAIN, 16 ) );
		txtPin.setHorizontalAlignment( JTextField.CENTER );
		
		btnUnlock = new JButton( "Unlock" );
		btnUnlock.setPreferredSize( new Dimension( 100, 35 ) );
		btnUnlock.setFont( btnUnlock.getFont().deriveFont( Font.BOLD ) );
		
		btnExit = new JButton( "Exit" );
		btnExit.setPreferredSize( new Dimension( 100, 35 ) );
		
		lblMessage = new JLabel( " " );
		lblMessage.setHorizontalAlignment( SwingConstants.CENTER );
		lblMessage.setFont( lblMessage.getFont().deriveFont( 12f ) );
		lblMessage.setVisible( false );
		
		lblHint = new JLabel( " " );
		lblHint.setHorizontalAlignment( SwingConstants.CENTER );
		lblHint.setFont( lblHint.getFont().deriveFont( Font.ITALIC, 11f ) );
		lblHint.setForeground( new Color( 100, 100, 100 ) );
		lblHint.setVisible( false );
		
		chkShowPin = new JCheckBox( "Show PIN" );
		chkShowPin.setFont( chkShowPin.getFont().deriveFont( 11f ) );
		
		// Lockout panel (hidden by default)
		lockoutPanel = new JPanel( new BorderLayout( 10, 10 ) );
		lockoutPanel.setBackground( new Color( 255, 235, 238 ) );
		lockoutPanel.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( new Color( 244, 67, 54 ), 1 ),
				new EmptyBorder( 15, 20, 15, 20 ) ) );
		
		JLabel lockoutIcon = new JLabel( "🔒" );
		lockoutIcon.setFont( lockoutIcon.getFont().deriveFont( 24f ) );
		
		lblLockoutTime = new JLabel( "Account locked. Please wait..." );
		lblLockoutTime.setFont( lblLockoutTime.getFont().deriveFont( 12f ) );
		
		lockoutPanel.add( lockoutIcon, BorderLayout.WEST );
		lockoutPanel.add( lblLockoutTime, BorderLayout.CENTER );
		lockoutPanel.setPreferredSize( new Dimension( 280, 60 ) );
		lockoutPanel.setVisible( false );
	}
	
	private void layoutComponents()
	{
		setLayout( new BorderLayout() );
		
		// --- Header Section ---
		JPanel headerPanel = new JPanel( new BorderLayout() );
		headerPanel.setBackground( new Color( 41, 98, 255 ) );
		headerPanel.setBorder( new EmptyBorder( 20, 20, 20, 20 ) );
		
		JLabel lblTitle = new JLabel( "Notes Management System" );
		lblTitle.setFont( lblTitle.getFont().deriveFont( Font.BOLD, 18f ) );
		lblTitle.setForeground( Color.WHITE );
		lblTitle.setHorizontalAlignment( SwingConstants.CENTER );
		
		JLabel lblSubtitle = new JLabel( "Enter your PIN to continue" );
		lblSubtitle.setFont( lblSubtitle.getFont().deriveFont( 12f ) );
		lblSubtitle.setForeground( new Color( 200, 220, 255 ) );
		lblSubtitle.setHorizontalAlignment( SwingConstants.CENTER );
		
		headerPanel.add( lblTitle, BorderLayout.CENTER );
		headerPanel.add( lblSubtitle, BorderLayout.SOUTH );
		add( headerPanel, BorderLayout.NORTH );
		
		// --- Content Section ---
		JPanel contentPanel = new JPanel( new GridBagLayout() );
		contentPanel.setBorder( new EmptyBorder( 15, 30, 15, 30 ) );
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Default constraints
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets( 4, 0, 4, 0 );
		gbc.weightx = 1.0;
		
		// PIN field
		txtPin.setAlignmentX( Component.CENTER_ALIGNMENT );
		txtPin.setMaximumSize( new Dimension( 200, 35 ) );
		gbc.gridy = 0;
		contentPanel.add( txtPin, gbc );
		
		// Show PIN checkbox
		gbc.gridy = 1;
		gbc.insets = new Insets( 0, 0, 5, 0 );
		contentPanel.add( chkShowPin, gbc );
		
		// Message label
		lblMessage.setAlignmentX( Component.CENTER_ALIGNMENT );
		gbc.gridy = 2;
		contentPanel.add( lblMessage, gbc );
		
		// Hint label
		lblHint.setAlignmentX( Component.CENTER_ALIGNMENT );
		gbc.gridy = 3;
		contentPanel.add( lblHint, gbc );
		
		// Lockout panel
		lockoutPanel.setAlignmentX( Component.CENTER_ALIGNMENT );
		lockoutPanel.setOpaque( true );
		gbc.gridy = 4;
		gbc.weighty = 1.0; // This allows the panel to occupy extra space
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.CENTER;
		contentPanel.add( lockoutPanel, gbc );
		
		add( contentPanel, BorderLayout.CENTER );
		
		// Buttons
		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 15, 0 ) );
		buttonPanel.setBorder( new EmptyBorder( 0, 0, 20, 0 ) );
		buttonPanel.add( btnExit );
		buttonPanel.add( btnUnlock );
		add( buttonPanel, BorderLayout.SOUTH );
		
		getRootPane().setDefaultButton( btnUnlock );
	}
	
	private void setupEventHandlers()
	{
		btnUnlock.addActionListener( e -> attemptUnlock() );
		
		btnExit.addActionListener( e ->
		{
			int result = JOptionPane.showConfirmDialog( this, "Exit without unlocking?", "Confirm Exit", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE );
			
			if( result == JOptionPane.YES_OPTION )
			{
				authenticated = false;
				dispose();
			}
		} );
		
		chkShowPin.addActionListener( e ->
		{
			txtPin.setEchoChar( chkShowPin.isSelected() ? (char) 0 : '•' );
		} );
		
		txtPin.addActionListener( e -> attemptUnlock() );
		
		// Window handlers
		addWindowListener( new WindowAdapter()
		{
			@Override
			public void windowOpened( WindowEvent e )
			{
				// Check for existing lockout after dialog is visible
				if( PinManager.isLockedOut() )
				{
					showLockout();
				}
			}
			
			@Override
			public void windowClosing( WindowEvent e )
			{
				btnExit.doClick();
			}
		} );
		
		// Clear PIN field when dialog gains or loses focus (security measure)
		addWindowFocusListener( new WindowFocusListener()
		{
			@Override
			public void windowGainedFocus( WindowEvent e )
			{
				txtPin.requestFocusInWindow();
			}
			
			@Override
			public void windowLostFocus( WindowEvent e )
			{
				
			}
		} );
	}
	
	private void attemptUnlock()
	{
		if( PinManager.isLockedOut() )
		{
			showLockout();
			return;
		}
		
		char[] pin = txtPin.getPassword();
		
		if( pin.length == 0 )
		{
			showMessage( "Please enter your PIN.", false );
			return;
		}
		
		// Disable UI during verification
		setInputEnabled( false );
		lblMessage.setText( "Verifying..." );
		lblMessage.setForeground( Color.GRAY );
		
		// Use SwingWorker for potentially slow PBKDF2 operation
		SwingWorker<PinManager.VerificationResult, Void> worker = new SwingWorker<PinManager.VerificationResult, Void>()
		{
			
			@Override
			protected PinManager.VerificationResult doInBackground()
			{
				return PinManager.verifyPin( pin );
			}
			
			@Override
			protected void done()
			{
				try
				{
					PinManager.VerificationResult result = get();
					handleVerificationResult( result );
				}
				catch( Exception e )
				{
					showMessage( "Verification error.", true );
					setInputEnabled( true );
				}
			}
		};
		
		worker.execute();
	}
	
	private void handleVerificationResult( PinManager.VerificationResult result )
	{
		if( result.isSuccess() )
		{
			authenticated = true;
			dispose();
		}
		else
		{
			failedAttempts++;
			
			if( result.isLockedOut() )
			{
				showLockout();
			}
			else
			{
				showMessage( result.getMessage(), true );
				
				// Show hint after a few failed attempts
				if( failedAttempts >= SHOW_HINT_AFTER_ATTEMPTS )
				{
					String hint = PinManager.getPinHint();
					if( hint != null && !hint.isEmpty() )
					{
						lblHint.setText( "Hint: " + hint );
						lblHint.setVisible( true );
					}
				}
				
				txtPin.setText( "" );
				txtPin.requestFocus();
				setInputEnabled( true );
			}
		}
	}
	
	private void showMessage( String message, boolean isError )
	{
		lblMessage.setText( message );
		lblMessage.setForeground( isError ? new Color( 244, 67, 54 ) : Color.GRAY );
		lblMessage.setVisible( true );
	}
	
	private void showLockout()
	{
		setInputEnabled( false );
		lockoutPanel.setVisible( true );
		lblMessage.setVisible( false );
		lblHint.setVisible( false );
		
		// Force layout update
		lockoutPanel.getParent().revalidate();
		lockoutPanel.getParent().repaint();
		
		lockoutTimer = new Timer( 1000, e ->
		{
			long minutes = PinManager.getLockoutMinutesRemaining();
			
			if( minutes <= 0 )
			{
				lockoutTimer.stop();
				lockoutPanel.setVisible( false );
				getContentPane().revalidate();
				getContentPane().repaint();
				setInputEnabled( true );
				txtPin.setText( "" );
				txtPin.requestFocus();
				showMessage( "You can try again now.", false );
			}
			else
			{
				lblLockoutTime.setText( "Account locked. Please wait " + minutes + " minute(s)." );
			}
		} );
		
		// Initial update
		long minutes = PinManager.getLockoutMinutesRemaining();
		lblLockoutTime.setText( "Account locked. Please wait " + minutes + " minute(s)." );
		
		lockoutTimer.start();
	}
	
	private void setInputEnabled( boolean enabled )
	{
		txtPin.setEnabled( enabled );
		btnUnlock.setEnabled( enabled );
		chkShowPin.setEnabled( enabled );
	}
	
	/**
	 * @return true if user successfully authenticated
	 */
	public boolean isAuthenticated()
	{
		return authenticated;
	}
	
	/**
	 * Show the PIN entry dialog and return whether authentication succeeded.
	 * 
	 * @param parent Parent frame (can be null)
	 * @return true if authenticated, false if cancelled or failed
	 */
	public static boolean authenticate( Frame parent )
	{
		if( !PinManager.isPinEnabled() )
		{
			return true; // No PIN required
		}
		
		Dlg_PinEntry dialog = new Dlg_PinEntry( parent );
		dialog.setVisible( true );
		
		return dialog.isAuthenticated();
	}
	
	@Override
	public void dispose()
	{
		// Clean up timer
		if( lockoutTimer != null )
		{
			lockoutTimer.stop();
		}
		
		// Clear sensitive data
		txtPin.setText( "" );
		
		super.dispose();
	}
}