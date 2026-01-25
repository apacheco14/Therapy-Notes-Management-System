package com.alexpacheco.therapynotes.view.screens;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import com.alexpacheco.therapynotes.controller.AppController;
import com.alexpacheco.therapynotes.controller.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.EntityValidator;
import com.alexpacheco.therapynotes.model.entities.Client;
import com.alexpacheco.therapynotes.util.AppFonts;
import com.alexpacheco.therapynotes.util.DateFormatUtil;
import com.alexpacheco.therapynotes.util.PreferencesUtil;
import com.alexpacheco.therapynotes.view.components.Txt_EmailAddress;
import com.alexpacheco.therapynotes.view.components.Txt_PhoneNumber;
import com.alexpacheco.therapynotes.view.components.ValidatedTextField;
import com.toedter.calendar.JDateChooser;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Date;

public class Pnl_NewEditClient extends Pnl_NewEditScreen
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
	private JLabel firstNameLabel;
	private JLabel lastNameLabel;
	private JLabel dateOfBirthLabel;
	
	public Pnl_NewEditClient()
	{
		super();
	}
	
	@Override
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
	
	@Override
	public void refreshLabelsText()
	{
		firstNameLabel.setText( "First Name:" + ( PreferencesUtil.isClientFirstNameRequired() ? " *" : "" ) );
		lastNameLabel.setText( "Last Name:" + ( PreferencesUtil.isClientLastNameRequired() ? " *" : "" ) );
		dateOfBirthLabel.setText( "Date of Birth:" + ( PreferencesUtil.isClientDOBRequired() ? " *" : "" ) );
		this.repaint();
		this.revalidate();
	}
	
	@Override
	protected void loadEntityData( Integer entityId )
	{
		try
		{
			Client client = AppController.getClientById( entityId );
			
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
	
	@Override
	protected void doNewSave( Object entity ) throws TherapyAppException
	{
		AppController.createClient( (Client) entity );
		JOptionPane.showMessageDialog( this, "Client created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE );
	}
	
	@Override
	protected void doEditSave( Object entity ) throws TherapyAppException
	{
		AppController.updateClient( (Client) entity );
		JOptionPane.showMessageDialog( this, "Client updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE );
	}
	
	@Override
	protected boolean isDataValid()
	{
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
	
	@Override
	protected boolean isEveryRequiredFieldFilled( Object entity )
	{
		try
		{
			EntityValidator.validateClient( (Client) entity );
		}
		catch( TherapyAppException e )
		{
			AppController.showValidationErrorPopup( e.getMessage() );
			return false;
		}
		
		return true;
	}
	
	@Override
	protected Object collectEntityData()
	{
		Client client = new Client();
		if( isEditMode )
		{
			client.setClientId( entityId );
		}
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
		return client;
	}
	
	@Override
	protected void showSaveError( TherapyAppException e )
	{
		AppController.showBasicErrorPopup( e, "Error saving client:" );
	}
	
	@Override
	protected void toggleTitleLabel()
	{
		if( isEditMode )
		{
			titleLabel.setText( "Edit Client" );
		}
		else
		{
			titleLabel.setText( "New Client" );
		}
	}
	
	@Override
	protected void initHeaderPanelComponents()
	{
		titleLabel = new JLabel( "New Client", SwingConstants.CENTER );
		titleLabel.setFont( AppFonts.getScreenTitleFont() );
		titleLabel.setForeground( AppController.getTitleColor() );
		titleLabel.setBorder( BorderFactory.createEmptyBorder( 20, 0, 20, 0 ) );
		headerPanel.add( titleLabel );
	}
	
	@Override
	protected void initMainPanelComponents()
	{
		mainContentPanel.setLayout( new GridBagLayout() );
		mainContentPanel.setBorder( BorderFactory.createEmptyBorder( 20, 50, 20, 50 ) );
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets( 5, 5, 5, 5 );
		
		firstNameField = new JTextField( 50 );
		lastNameField = new JTextField( 50 );
		clientCodeField = new JTextField( 10 );
		setupClientCodeField();
		dateOfBirthChooser = new JDateChooser();
		dateOfBirthChooser.setFont( AppFonts.getTextFieldFont() );
		dateOfBirthChooser.setDateFormatString( "MM/dd/yyyy" );
		dateOfBirthChooser.setPreferredSize( new Dimension( 150, 25 ) );
		dateOfBirthChooser.setMinSelectableDate( DateFormatUtil.toDate( LocalDateTime.now().minusYears( 100 ) ) );
		dateOfBirthChooser.setMaxSelectableDate( new java.util.Date() );
		inactiveCheckBox = new JCheckBox();
		inactiveCheckBox.setBackground( AppController.getBackgroundColor() );
		email1Field = new Txt_EmailAddress();
		email2Field = new Txt_EmailAddress();
		email3Field = new Txt_EmailAddress();
		phone1Field = new Txt_PhoneNumber();
		phone2Field = new Txt_PhoneNumber();
		phone3Field = new Txt_PhoneNumber();
		txtClientNotes = new JTextArea( 5, 50 );
		txtClientNotes.setLineWrap( true );
		txtClientNotes.setWrapStyleWord( true );
		txtClientNotes.setFont( AppFonts.getTextFieldFont() );
		
		firstNameLabel = new JLabel();
		lastNameLabel = new JLabel();
		dateOfBirthLabel = new JLabel();
		refreshLabelsText();
		
		// Column 0
		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0.0;
		
		gbc.gridy = 0;
		mainContentPanel.add( firstNameLabel, gbc );
		
		gbc.gridy = 1;
		mainContentPanel.add( new JLabel( "Client Code: *" ), gbc );
		
		gbc.gridy = 2;
		mainContentPanel.add( new JLabel( "Email 1:" ), gbc );
		
		gbc.gridy = 3;
		mainContentPanel.add( new JLabel( "Email 2:" ), gbc );
		
		gbc.gridy = 4;
		mainContentPanel.add( new JLabel( "Email 3:" ), gbc );
		
		gbc.gridy = 5;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		mainContentPanel.add( new JLabel( "Notes:" ), gbc );
		
		// Column 1
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		
		gbc.gridy = 0;
		mainContentPanel.add( firstNameField, gbc );
		
		gbc.gridy = 1;
		mainContentPanel.add( clientCodeField, gbc );
		
		gbc.gridy = 2;
		mainContentPanel.add( email1Field, gbc );
		
		gbc.gridy = 3;
		mainContentPanel.add( email2Field, gbc );
		
		gbc.gridy = 4;
		mainContentPanel.add( email3Field, gbc );
		
		// Column 2
		gbc.gridx = 2;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0.0;
		
		gbc.gridy = 0;
		mainContentPanel.add( lastNameLabel, gbc );
		
		gbc.gridy = 1;
		mainContentPanel.add( dateOfBirthLabel, gbc );
		
		gbc.gridy = 2;
		mainContentPanel.add( new JLabel( "Phone 1:" ), gbc );
		
		gbc.gridy = 3;
		mainContentPanel.add( new JLabel( "Phone 2:" ), gbc );
		
		gbc.gridy = 4;
		mainContentPanel.add( new JLabel( "Phone 3:" ), gbc );
		
		// Column 3
		gbc.gridx = 3;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		
		gbc.gridy = 0;
		mainContentPanel.add( lastNameField, gbc );
		
		gbc.gridy = 1;
		// Create a panel to hold both date of birth and inactive checkbox
		JPanel dobInactivePanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 10, 0 ) );
		dobInactivePanel.add( dateOfBirthChooser );
		dobInactivePanel.add( new JLabel( "Inactive:" ) );
		dobInactivePanel.add( inactiveCheckBox );
		dobInactivePanel.setBackground( AppController.getBackgroundColor() );
		mainContentPanel.add( dobInactivePanel, gbc );
		
		gbc.gridy = 2;
		mainContentPanel.add( phone1Field, gbc );
		
		gbc.gridy = 3;
		mainContentPanel.add( phone2Field, gbc );
		
		gbc.gridy = 4;
		mainContentPanel.add( phone3Field, gbc );
		
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridwidth = 3;
		gbc.gridy = 5;
		JScrollPane scrollPane = new JScrollPane( txtClientNotes );
		Dimension textAreaSize = txtClientNotes.getPreferredSize();
		scrollPane.setPreferredSize( textAreaSize );
		scrollPane.setMinimumSize( new Dimension( textAreaSize.width, textAreaSize.height + 5 ) );
		mainContentPanel.add( scrollPane, gbc );
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
	
	@Override
	protected void initFooterComponents()
	{
		
	}
	
	@Override
	protected void disableUneditableFields()
	{
		clientCodeField.setEnabled( false );
	}
	
	@Override
	protected void enableUneditableFields()
	{
		clientCodeField.setEnabled( true );
	}
}