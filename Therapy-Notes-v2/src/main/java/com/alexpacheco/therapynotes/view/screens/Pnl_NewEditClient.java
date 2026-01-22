package com.alexpacheco.therapynotes.view.screens;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import com.alexpacheco.therapynotes.controller.AppController;
import com.alexpacheco.therapynotes.controller.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.entities.Client;
import com.alexpacheco.therapynotes.util.DateFormatUtil;
import com.alexpacheco.therapynotes.util.JavaUtils;
import com.alexpacheco.therapynotes.view.components.Txt_EmailAddress;
import com.alexpacheco.therapynotes.view.components.Txt_PhoneNumber;
import com.alexpacheco.therapynotes.view.components.ValidatedTextField;
import com.toedter.calendar.JDateChooser;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Date;

public class Pnl_NewEditClient extends JPanel
{
	private static final long serialVersionUID = -4521789456123789456L;
	private JTextField firstNameField;
	private JTextField lastNameField;
	private JTextField clientCodeField;
	private JDateChooser dateOfBirthChooser;
	private JCheckBox inactiveCheckBox;
	private ValidatedTextField email1Field;
	private ValidatedTextField email2Field;
	private ValidatedTextField email3Field;
	private ValidatedTextField phone1Field;
	private ValidatedTextField phone2Field;
	private ValidatedTextField phone3Field;
	private JTextArea txtClientNotes;
	private JLabel titleLabel;
	
	private boolean isEditMode = false;
	private Integer clientId = null;
	
	public Pnl_NewEditClient()
	{
		setLayout( new BorderLayout() );
		
		titleLabel = new JLabel( "New Client", SwingConstants.CENTER );
		titleLabel.setFont( new Font( "Arial", Font.BOLD, 24 ) );
		titleLabel.setBorder( BorderFactory.createEmptyBorder( 20, 0, 20, 0 ) );
		
		JPanel formPanel = new JPanel( new GridBagLayout() );
		formPanel.setBorder( BorderFactory.createEmptyBorder( 20, 50, 20, 50 ) );
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets( 5, 5, 5, 5 );
		
		firstNameField = new JTextField( 50 );
		lastNameField = new JTextField( 50 );
		clientCodeField = new JTextField( 10 );
		setupClientCodeField();
		dateOfBirthChooser = new JDateChooser();
		dateOfBirthChooser.setDateFormatString( "MM/dd/yyyy" );
		dateOfBirthChooser.setPreferredSize( new Dimension( 150, 25 ) );
		dateOfBirthChooser.setMinSelectableDate( DateFormatUtil.toDate( LocalDateTime.now().minusYears( 100 ) ) );
		dateOfBirthChooser.setMaxSelectableDate( new java.util.Date() );
		inactiveCheckBox = new JCheckBox();
		email1Field = new Txt_EmailAddress();
		email2Field = new Txt_EmailAddress();
		email3Field = new Txt_EmailAddress();
		phone1Field = new Txt_PhoneNumber();
		phone2Field = new Txt_PhoneNumber();
		phone3Field = new Txt_PhoneNumber();
		txtClientNotes = new JTextArea( 5, 50 );
		txtClientNotes.setLineWrap( true );
		txtClientNotes.setWrapStyleWord( true );
		txtClientNotes.setFont( new Font( "Arial", Font.PLAIN, 12 ) );
		
		// Column 0
		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0.0;
		
		gbc.gridy = 0;
		formPanel.add( new JLabel( "First Name:" ), gbc );
		
		gbc.gridy = 1;
		formPanel.add( new JLabel( "Client Code:" ), gbc );
		
		gbc.gridy = 2;
		formPanel.add( new JLabel( "Email 1:" ), gbc );
		
		gbc.gridy = 3;
		formPanel.add( new JLabel( "Email 2:" ), gbc );
		
		gbc.gridy = 4;
		formPanel.add( new JLabel( "Email 3:" ), gbc );
		
		gbc.gridy = 5;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		formPanel.add( new JLabel( "Notes:" ), gbc );
		
		// Column 1
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		
		gbc.gridy = 0;
		formPanel.add( firstNameField, gbc );
		
		gbc.gridy = 1;
		formPanel.add( clientCodeField, gbc );
		
		gbc.gridy = 2;
		formPanel.add( email1Field, gbc );
		
		gbc.gridy = 3;
		formPanel.add( email2Field, gbc );
		
		gbc.gridy = 4;
		formPanel.add( email3Field, gbc );
		
		// Column 2
		gbc.gridx = 2;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0.0;
		
		gbc.gridy = 0;
		formPanel.add( new JLabel( "Last Name:" ), gbc );
		
		gbc.gridy = 1;
		formPanel.add( new JLabel( "Date of Birth:" ), gbc );
		
		gbc.gridy = 2;
		formPanel.add( new JLabel( "Phone 1:" ), gbc );
		
		gbc.gridy = 3;
		formPanel.add( new JLabel( "Phone 2:" ), gbc );
		
		gbc.gridy = 4;
		formPanel.add( new JLabel( "Phone 3:" ), gbc );
		
		// Column 3
		gbc.gridx = 3;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		
		gbc.gridy = 0;
		formPanel.add( lastNameField, gbc );
		
		gbc.gridy = 1;
		// Create a panel to hold both date of birth and inactive checkbox
		JPanel dobInactivePanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 10, 0 ) );
		dobInactivePanel.add( dateOfBirthChooser );
		dobInactivePanel.add( new JLabel( "Inactive:" ) );
		dobInactivePanel.add( inactiveCheckBox );
		formPanel.add( dobInactivePanel, gbc );
		
		gbc.gridy = 2;
		formPanel.add( phone1Field, gbc );
		
		gbc.gridy = 3;
		formPanel.add( phone2Field, gbc );
		
		gbc.gridy = 4;
		formPanel.add( phone3Field, gbc );
		
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridwidth = 3;
		gbc.gridy = 5;
		JScrollPane scrollPane = new JScrollPane( txtClientNotes );
		formPanel.add( scrollPane, gbc );
		
		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 10, 10 ) );
		buttonPanel.setBorder( BorderFactory.createEmptyBorder( 10, 0, 20, 0 ) );
		
		JButton saveButton = new JButton( "Save" );
		JButton cancelButton = new JButton( "Cancel" );
		
		saveButton.addActionListener( e -> saveClient() );
		cancelButton.addActionListener( e -> cancel() );
		
		buttonPanel.add( cancelButton );
		buttonPanel.add( saveButton );
		
		add( titleLabel, BorderLayout.NORTH );
		add( formPanel, BorderLayout.CENTER );
		add( buttonPanel, BorderLayout.SOUTH );
		
		clearForm();
	}
	
	/**
	 * Set the panel to edit mode and load the client data
	 * 
	 * @param clientId The ID of the client to edit
	 */
	public void setEditMode( Integer clientId )
	{
		this.isEditMode = true;
		this.clientId = clientId;
		this.titleLabel.setText( "Edit Client" );
		this.clientCodeField.setEnabled( false );
		
		loadClientData( clientId );
	}
	
	/**
	 * Set the panel to create mode (default)
	 */
	public void setCreateMode()
	{
		this.isEditMode = false;
		this.clientId = null;
		this.titleLabel.setText( "New Client" );
		this.clientCodeField.setEnabled( true );
		clearForm();
	}
	
	private void loadClientData( Integer clientId )
	{
		try
		{
			Client client = AppController.getClientById( clientId );
			
			if( client != null )
			{
				firstNameField.setText( client.getFirstName() );
				lastNameField.setText( client.getLastName() );
				clientCodeField.setText( client.getClientCode() );
				if( client.getDateOfBirth() != null )
				{
					dateOfBirthChooser.setDate( client.getDateOfBirth() );
				}
				else
				{
					dateOfBirthChooser.setDate( null );
				}
				inactiveCheckBox.setSelected( client.isInactive() );
				email1Field.setText( client.getEmail1() );
				email2Field.setText( client.getEmail2() );
				email3Field.setText( client.getEmail3() );
				phone1Field.setText( client.getPhone1() );
				phone2Field.setText( client.getPhone2() );
				phone3Field.setText( client.getPhone3() );
				txtClientNotes.setText( client.getClientNotes() );
			}
			else
			{
				JOptionPane.showMessageDialog( this, "Client not found.", "Error", JOptionPane.ERROR_MESSAGE );
				setCreateMode();
			}
		}
		catch( TherapyAppException e )
		{
			AppController.showBasicErrorPopup( e, "Error loading client data:" );
			setCreateMode();
		}
	}
	
	private void setupClientCodeField()
	{
		( (AbstractDocument) clientCodeField.getDocument() ).setDocumentFilter( new DocumentFilter()
		{
			@Override
			public void insertString( FilterBypass fb, int offset, String string, AttributeSet attr ) throws BadLocationException
			{
				String upperString = string.toUpperCase();
				if( fb.getDocument().getLength() + upperString.length() <= 10 )
				{
					super.insertString( fb, offset, upperString, attr );
				}
			}
			
			@Override
			public void replace( FilterBypass fb, int offset, int length, String text, AttributeSet attrs ) throws BadLocationException
			{
				String upperText = text.toUpperCase();
				int currentLength = fb.getDocument().getLength();
				int newLength = currentLength - length + upperText.length();
				
				if( newLength <= 10 )
				{
					super.replace( fb, offset, length, upperText, attrs );
				}
			}
		} );
	}
	
	private void saveClient()
	{
		if( !_isDataValid() )
		{
			return;
		}
		
		try
		{
			Client client = new Client();
			client.setFirstName( firstNameField.getText().trim() );
			client.setLastName( lastNameField.getText().trim() );
			client.setClientCode( clientCodeField.getText().trim() );
			if( dateOfBirthChooser.getDate() != null )
			{
				client.setDateOfBirth( dateOfBirthChooser.getDate() );
			}
			client.setInactive( inactiveCheckBox.isSelected() );
			client.setEmail1( email1Field.getText().trim() );
			client.setEmail2( email2Field.getText().trim() );
			client.setEmail3( email3Field.getText().trim() );
			client.setPhone1( phone1Field.getText().trim() );
			client.setPhone2( phone2Field.getText().trim() );
			client.setPhone3( phone3Field.getText().trim() );
			client.setClientNotes( txtClientNotes.getText() );
			
			if( isEditMode )
			{
				client.setClientId( clientId );
				AppController.updateClient( client );
				JOptionPane.showMessageDialog( this, "Client updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE );
				clearForm();
				setCreateMode();
				AppController.returnHome( true );
			}
			else
			{
				AppController.createClient( client );
				JOptionPane.showMessageDialog( this, "Client created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE );
				clearForm();
				setCreateMode();
			}
		}
		catch( TherapyAppException e )
		{
			AppController.showBasicErrorPopup( e, "Error saving client:" );
		}
	}
	
	private boolean _isDataValid()
	{
		if( JavaUtils.isNullOrEmpty( firstNameField.getText() ) && JavaUtils.isNullOrEmpty( lastNameField.getText() ) )
		{
			AppController.showValidationErrorPopup( "At least one of First Name or Last Name is required." );
			return false;
		}
		
		if( JavaUtils.isNullOrEmpty( clientCodeField.getText() ) )
		{
			AppController.showValidationErrorPopup( "Client Code is required." );
			return false;
		}
		
		Date chosenDate = dateOfBirthChooser.getDate();
		if( chosenDate != null && ( chosenDate.after( new java.util.Date() )
				|| chosenDate.before( DateFormatUtil.toDate( LocalDateTime.now().minusYears( 100 ) ) ) ) )
		{
			AppController.showValidationErrorPopup( "Date of birth is invalid." );
			return false;
		}
		
		if( !phone1Field.isInputValid() || !phone2Field.isInputValid() || !phone3Field.isInputValid() )
		{
			AppController.showValidationErrorPopup( "Phone number is invalid." );
			return false;
		}
		
		if( !email1Field.isInputValid() || !email2Field.isInputValid() || !email3Field.isInputValid() )
		{
			AppController.showValidationErrorPopup( "Email address is invalid." );
			return false;
		}
		
		if( chosenDate != null && chosenDate.after( DateFormatUtil.toDate( LocalDateTime.now().minusYears( 5 ) ) ) )
		{
			int result = JOptionPane.showConfirmDialog( this,
					"Are you sure the date of birth is correct?\n" + DateFormatUtil.toSimpleString( chosenDate ), "Validation Warning",
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE );
			
			if( result != JOptionPane.YES_OPTION )
			{
				return false;
			}
		}
		
		return true;
	}
	
	private void cancel()
	{
		int result = JOptionPane.showConfirmDialog( this, "Are you sure you want to cancel? Any unsaved changes will be lost.",
				"Cancel Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE );
		
		if( result == JOptionPane.YES_OPTION )
		{
			clearForm();
			setCreateMode();
			AppController.returnHome( true );
		}
	}
	
	public void clearForm()
	{
		firstNameField.setText( "" );
		lastNameField.setText( "" );
		clientCodeField.setText( "" );
		dateOfBirthChooser.setDate( null );
		inactiveCheckBox.setSelected( false );
		email1Field.setText( "" );
		email2Field.setText( "" );
		email3Field.setText( "" );
		phone1Field.setText( "" );
		phone2Field.setText( "" );
		phone3Field.setText( "" );
		txtClientNotes.setText( "" );
	}
}