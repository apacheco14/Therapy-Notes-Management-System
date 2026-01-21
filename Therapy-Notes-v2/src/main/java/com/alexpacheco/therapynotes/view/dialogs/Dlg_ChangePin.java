package com.alexpacheco.therapynotes.view.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.alexpacheco.therapynotes.controller.AppController;
import com.alexpacheco.therapynotes.controller.PinManager;
import com.alexpacheco.therapynotes.controller.enums.LogLevel;

/**
 * Dialog for changing the application PIN.
 */
public class Dlg_ChangePin extends JDialog
{
	private static final long serialVersionUID = -2265611101143699679L;
	private JPasswordField txtCurrentPin;
	private JPasswordField txtNewPin;
	private JPasswordField txtConfirmPin;
	private JTextField txtHint;
	private JLabel lblStrength;
	private JProgressBar strengthBar;
	private JLabel lblMatch;
	private JButton btnChange;
	private JButton btnCancel;
	private JButton btnRemovePin;
	
	private boolean pinChanged = false;
	
	public Dlg_ChangePin( Frame parent )
	{
		super( parent, "Change PIN", true );
		
		initializeComponents();
		layoutComponents();
		setupEventHandlers();
		
		setSize( 400, 420 );
		setResizable( false );
		setLocationRelativeTo( parent );
	}
	
	private void initializeComponents()
	{
		txtCurrentPin = new JPasswordField( 20 );
		txtNewPin = new JPasswordField( 20 );
		txtConfirmPin = new JPasswordField( 20 );
		txtHint = new JTextField( 25 );
		
		// Load existing hint
		String existingHint = PinManager.getPinHint();
		if( existingHint != null && !existingHint.isEmpty() )
		{
			txtHint.setText( existingHint );
		}
		
		strengthBar = new JProgressBar( 0, 4 );
		strengthBar.setPreferredSize( new Dimension( 100, 8 ) );
		strengthBar.setBorderPainted( false );
		
		lblStrength = new JLabel( "" );
		lblStrength.setFont( lblStrength.getFont().deriveFont( 11f ) );
		
		lblMatch = new JLabel( " " );
		lblMatch.setFont( lblMatch.getFont().deriveFont( 11f ) );
		
		btnChange = new JButton( "Change PIN" );
		btnCancel = new JButton( "Cancel" );
		btnRemovePin = new JButton( "Remove PIN" );
		btnRemovePin.setForeground( new Color( 183, 28, 28 ) );
	}
	
	private void layoutComponents()
	{
		setLayout( new BorderLayout() );
		
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout( new BoxLayout( contentPanel, BoxLayout.Y_AXIS ) );
		contentPanel.setBorder( new EmptyBorder( 20, 30, 10, 30 ) );
		
		// Current PIN
		contentPanel.add( createFieldPanel( "Current PIN", txtCurrentPin ) );
		contentPanel.add( Box.createVerticalStrut( 20 ) );
		
		// Separator
		JSeparator sep = new JSeparator();
		sep.setAlignmentX( Component.LEFT_ALIGNMENT );
		sep.setMaximumSize( new Dimension( Integer.MAX_VALUE, 1 ) );
		contentPanel.add( sep );
		contentPanel.add( Box.createVerticalStrut( 20 ) );
		
		// New PIN
		contentPanel.add( createFieldPanel( "New PIN", txtNewPin ) );
		contentPanel.add( Box.createVerticalStrut( 5 ) );
		
		// Strength indicator
		JPanel strengthPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
		strengthPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
		strengthPanel.add( strengthBar );
		strengthPanel.add( Box.createHorizontalStrut( 10 ) );
		strengthPanel.add( lblStrength );
		contentPanel.add( strengthPanel );
		contentPanel.add( Box.createVerticalStrut( 15 ) );
		
		// Confirm new PIN
		contentPanel.add( createFieldPanel( "Confirm New PIN", txtConfirmPin ) );
		contentPanel.add( Box.createVerticalStrut( 5 ) );
		
		lblMatch.setAlignmentX( Component.LEFT_ALIGNMENT );
		contentPanel.add( lblMatch );
		contentPanel.add( Box.createVerticalStrut( 15 ) );
		
		// Hint
		contentPanel.add( createFieldPanel( "PIN Hint (optional)", txtHint ) );
		
		add( contentPanel, BorderLayout.CENTER );
		
		// Buttons
		JPanel buttonPanel = new JPanel( new BorderLayout() );
		buttonPanel.setBorder( new EmptyBorder( 15, 30, 20, 30 ) );
		
		JPanel leftButtons = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
		leftButtons.add( btnRemovePin );
		
		JPanel rightButtons = new JPanel( new FlowLayout( FlowLayout.RIGHT, 10, 0 ) );
		rightButtons.add( btnCancel );
		rightButtons.add( btnChange );
		
		buttonPanel.add( leftButtons, BorderLayout.WEST );
		buttonPanel.add( rightButtons, BorderLayout.EAST );
		
		add( buttonPanel, BorderLayout.SOUTH );
	}
	
	private JPanel createFieldPanel( String label, JComponent field )
	{
		JPanel panel = new JPanel();
		panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
		panel.setAlignmentX( Component.LEFT_ALIGNMENT );
		
		JLabel lbl = new JLabel( label );
		lbl.setFont( lbl.getFont().deriveFont( Font.BOLD, 12f ) );
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
		
		btnChange.addActionListener( e -> attemptChange() );
		
		btnRemovePin.addActionListener( e -> attemptRemove() );
		
		// Strength and match checking
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
		
		txtNewPin.getDocument().addDocumentListener( pinListener );
		txtConfirmPin.getDocument().addDocumentListener( pinListener );
	}
	
	private void updateUI()
	{
		char[] newPin = txtNewPin.getPassword();
		char[] confirm = txtConfirmPin.getPassword();
		
		// Strength
		if( newPin.length > 0 )
		{
			PinManager.PinStrength strength = PinManager.evaluateStrength( newPin );
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
		
		// Match
		if( confirm.length > 0 )
		{
			if( Arrays.equals( newPin, confirm ) )
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
		
		Arrays.fill( newPin, '\0' );
		Arrays.fill( confirm, '\0' );
	}
	
	private void attemptChange()
	{
		char[] currentPin = txtCurrentPin.getPassword();
		char[] newPin = txtNewPin.getPassword();
		char[] confirmPin = txtConfirmPin.getPassword();
		
		try
		{
			// Validate new PIN
			if( newPin.length < 4 )
			{
				showError( "New PIN must be at least 4 characters." );
				txtNewPin.requestFocus();
				return;
			}
			
			if( !Arrays.equals( newPin, confirmPin ) )
			{
				showError( "New PINs do not match." );
				txtConfirmPin.requestFocus();
				return;
			}
			
			// Attempt change
			String hint = txtHint.getText().trim();
			boolean success = PinManager.changePin( currentPin, newPin, hint );
			
			if( success )
			{
				JOptionPane.showMessageDialog( this, "PIN changed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE );
				pinChanged = true;
				dispose();
			}
			else
			{
				showError( "Current PIN is incorrect." );
				txtCurrentPin.requestFocus();
				txtCurrentPin.selectAll();
				AppController.logToDatabase( LogLevel.WARN, "Dlg_ChangePin", "PIN changed attempted but incorrect current PIN provided" );
			}
			
		}
		finally
		{
			Arrays.fill( currentPin, '\0' );
			Arrays.fill( newPin, '\0' );
			Arrays.fill( confirmPin, '\0' );
			AppController.logToDatabase( LogLevel.INFO, "Dlg_ChangePin", "PIN changed" );
			AppController.updateMenu();
		}
	}
	
	private void attemptRemove()
	{
		int result = JOptionPane.showConfirmDialog( this,
				"Are you sure you want to remove PIN protection?\n\n" + "Anyone with access to your computer will be able to open\n"
						+ "the application and view client records.",
				"Remove PIN Protection", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE );
		
		if( result != JOptionPane.YES_OPTION )
		{
			return;
		}
		
		char[] currentPin = txtCurrentPin.getPassword();
		
		try
		{
			boolean success = PinManager.removePin( currentPin );
			
			if( success )
			{
				JOptionPane.showMessageDialog( this, "PIN protection has been removed.", "PIN Removed", JOptionPane.INFORMATION_MESSAGE );
				pinChanged = true;
				dispose();
			}
			else
			{
				showError( "Current PIN is incorrect." );
				txtCurrentPin.requestFocus();
				txtCurrentPin.selectAll();
				AppController.logToDatabase( LogLevel.WARN, "Dlg_ChangePin", "PIN removal attempted but incorrect current PIN provided" );
			}
		}
		finally
		{
			Arrays.fill( currentPin, '\0' );
			AppController.logToDatabase( LogLevel.INFO, "Dlg_ChangePin", "PIN removed" );
			AppController.updateMenu();
		}
	}
	
	private void showError( String message )
	{
		JOptionPane.showMessageDialog( this, message, "Error", JOptionPane.ERROR_MESSAGE );
	}
	
	public boolean isPinChanged()
	{
		return pinChanged;
	}
	
	/**
	 * Show the change PIN dialog.
	 * 
	 * @return true if PIN was changed or removed
	 */
	public static boolean show( Frame parent )
	{
		Dlg_ChangePin dialog = new Dlg_ChangePin( parent );
		dialog.setVisible( true );
		return dialog.isPinChanged();
	}
}