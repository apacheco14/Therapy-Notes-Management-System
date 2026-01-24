package com.alexpacheco.therapynotes.view.screens;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.alexpacheco.therapynotes.controller.AppController;
import com.alexpacheco.therapynotes.controller.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.EntityValidator;
import com.alexpacheco.therapynotes.model.entities.Contact;
import com.alexpacheco.therapynotes.util.AppFonts;
import com.alexpacheco.therapynotes.util.PreferencesUtil;
import com.alexpacheco.therapynotes.view.components.Cmb_ClientSelection;
import com.alexpacheco.therapynotes.view.components.Txt_EmailAddress;
import com.alexpacheco.therapynotes.view.components.Txt_PhoneNumber;
import com.alexpacheco.therapynotes.view.components.ValidatedTextField;

public class Pnl_NewEditContact extends JPanel
{
	private static final long serialVersionUID = -8227488932620896098L;
	private JTextField firstNameField;
	private JTextField lastNameField;
	private Cmb_ClientSelection linkedClientComboBox;
	private JCheckBox emergencyContactCheckBox;
	private ValidatedTextField email1Field;
	private ValidatedTextField email2Field;
	private ValidatedTextField email3Field;
	private ValidatedTextField phone1Field;
	private ValidatedTextField phone2Field;
	private ValidatedTextField phone3Field;
	private JLabel titleLabel;
	private JLabel firstNameLabel;
	private JLabel lastNameLabel;
	
	private boolean isEditMode = false;
	private Integer contactId = null;
	
	public Pnl_NewEditContact()
	{
		setLayout( new BorderLayout() );
		
		titleLabel = new JLabel( "New Contact", SwingConstants.CENTER );
		titleLabel.setFont( AppFonts.getScreenTitleFont() );
		titleLabel.setBorder( BorderFactory.createEmptyBorder( 20, 0, 20, 0 ) );
		
		JPanel formPanel = new JPanel( new GridBagLayout() );
		formPanel.setBorder( BorderFactory.createEmptyBorder( 20, 50, 20, 50 ) );
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets( 5, 5, 5, 5 );
		
		firstNameField = new JTextField( 50 );
		lastNameField = new JTextField( 50 );
		linkedClientComboBox = new Cmb_ClientSelection( false );
		emergencyContactCheckBox = new JCheckBox();
		email1Field = new Txt_EmailAddress();
		email2Field = new Txt_EmailAddress();
		email3Field = new Txt_EmailAddress();
		phone1Field = new Txt_PhoneNumber();
		phone2Field = new Txt_PhoneNumber();
		phone3Field = new Txt_PhoneNumber();
		
		firstNameLabel = new JLabel();
		lastNameLabel = new JLabel();
		refreshLabelsText();
		
		// Column 0
		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0.0;
		
		gbc.gridy = 0;
		formPanel.add( firstNameLabel, gbc );
		
		gbc.gridy = 1;
		formPanel.add( new JLabel( "Associated Client: *" ), gbc );
		
		gbc.gridy = 2;
		formPanel.add( new JLabel( "Email 1:" ), gbc );
		
		gbc.gridy = 3;
		formPanel.add( new JLabel( "Email 2:" ), gbc );
		
		gbc.gridy = 4;
		formPanel.add( new JLabel( "Email 3:" ), gbc );
		
		// Column 1
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		
		gbc.gridy = 0;
		formPanel.add( firstNameField, gbc );
		
		gbc.gridy = 1;
		formPanel.add( linkedClientComboBox, gbc );
		
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
		formPanel.add( lastNameLabel, gbc );
		
		gbc.gridy = 1;
		formPanel.add( new JLabel( "Emergency Contact:" ), gbc );
		
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
		formPanel.add( emergencyContactCheckBox, gbc );
		
		gbc.gridy = 2;
		formPanel.add( phone1Field, gbc );
		
		gbc.gridy = 3;
		formPanel.add( phone2Field, gbc );
		
		gbc.gridy = 4;
		formPanel.add( phone3Field, gbc );
		
		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 10, 10 ) );
		buttonPanel.setBorder( BorderFactory.createEmptyBorder( 10, 0, 20, 0 ) );
		
		JButton saveButton = new JButton( "Save" );
		JButton cancelButton = new JButton( "Cancel" );
		
		saveButton.addActionListener( e -> saveContact() );
		cancelButton.addActionListener( e -> cancel() );
		
		buttonPanel.add( cancelButton );
		buttonPanel.add( saveButton );
		
		add( titleLabel, BorderLayout.NORTH );
		add( formPanel, BorderLayout.CENTER );
		add( buttonPanel, BorderLayout.SOUTH );
		
		clearForm();
	}
	
	/**
	 * Set the panel to edit mode and load the contact data
	 * 
	 * @param contactId The ID of the contact to edit
	 */
	public void setEditMode( Integer contactId )
	{
		this.isEditMode = true;
		this.contactId = contactId;
		this.titleLabel.setText( "Edit Contact" );
		
		loadContactData( contactId );
	}
	
	/**
	 * Set the panel to create mode (default)
	 */
	public void setCreateMode()
	{
		this.isEditMode = false;
		this.contactId = null;
		this.titleLabel.setText( "New Contact" );
		clearForm();
	}
	
	private void loadContactData( Integer contactId )
	{
		try
		{
			Contact contact = AppController.getContactById( contactId );
			
			if( contact != null )
			{
				displayContactData( contact );
			}
			else
			{
				JOptionPane.showMessageDialog( this, "Contact not found.", "Error", JOptionPane.ERROR_MESSAGE );
				setCreateMode();
			}
		}
		catch( TherapyAppException e )
		{
			AppController.showBasicErrorPopup( e, "Error loading contact data:" );
			setCreateMode();
		}
	}
	
	private void displayContactData( Contact contact )
	{
		firstNameField.setText( contact.getFirstName() );
		lastNameField.setText( contact.getLastName() );
		
		// Set the associated client in dropdown
		Integer linkedClientId = contact.getLinkedClientId();
		if( linkedClientId != null )
		{
			linkedClientComboBox.selectByClientId( linkedClientId );
			linkedClientComboBox.setEnabled( false );
		}
		else
		{
			linkedClientComboBox.setSelectedIndex( -1 );
		}
		
		emergencyContactCheckBox.setSelected( contact.isEmergencyContact() );
		email1Field.setText( contact.getEmail1() );
		email2Field.setText( contact.getEmail2() );
		email3Field.setText( contact.getEmail3() );
		phone1Field.setText( contact.getPhone1() );
		phone2Field.setText( contact.getPhone2() );
		phone3Field.setText( contact.getPhone3() );
	}
	
	private void saveContact()
	{
		if( !_isContactDataValid() )
		{
			return;
		}
		
		Contact contact = _collectContactData();
		if( !_isEveryRequiredFieldFilled( contact ) )
		{
			return;
		}
		
		try
		{
			if( isEditMode )
			{
				AppController.updateContact( contact );
				JOptionPane.showMessageDialog( this, "Contact updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE );
			}
			else
			{
				AppController.createContact( contact );
				JOptionPane.showMessageDialog( this, "Contact saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE );
			}
			
			clearForm();
			setCreateMode();
		}
		catch( TherapyAppException e )
		{
			AppController.showBasicErrorPopup( e, "Error saving contact:" );
		}
	}
	
	private Contact _collectContactData()
	{
		Contact c = new Contact();
		if( isEditMode )
		{
			c.setContactId( contactId );
		}
		c.setFirstName( firstNameField.getText().trim() );
		c.setLastName( lastNameField.getText().trim() );
		c.setLinkedClientId( linkedClientComboBox.getSelectedClientId() );
		c.setEmergencyContact( emergencyContactCheckBox.isSelected() );
		c.setEmail1( email1Field.getText().trim() );
		c.setEmail2( email2Field.getText().trim() );
		c.setEmail3( email3Field.getText().trim() );
		c.setPhone1( phone1Field.getText().trim() );
		c.setPhone2( phone2Field.getText().trim() );
		c.setPhone3( phone3Field.getText().trim() );
		return c;
	}
	
	private boolean _isContactDataValid()
	{
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
		
		return true;
	}
	
	private boolean _isEveryRequiredFieldFilled( Contact contact )
	{
		try
		{
			EntityValidator.validateContact( contact );
		}
		catch( TherapyAppException e )
		{
			AppController.showValidationErrorPopup( e.getMessage() );
			return false;
		}
		
		return true;
	}
	
	private void cancel()
	{
		int result = JOptionPane.showConfirmDialog( this, "Are you sure you want to cancel? Any unsaved changes will be lost.",
				"Cancel Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );
		
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
		linkedClientComboBox.setSelectedIndex( -1 );
		emergencyContactCheckBox.setSelected( false );
		email1Field.setText( "" );
		email2Field.setText( "" );
		email3Field.setText( "" );
		phone1Field.setText( "" );
		phone2Field.setText( "" );
		phone3Field.setText( "" );
	}
	
	public void refreshLabelsText()
	{
		firstNameLabel.setText( "First Name:" + ( PreferencesUtil.isContactFirstNameRequired() ? " *" : "" ) );
		lastNameLabel.setText( "Last Name:" + ( PreferencesUtil.isContactLastNameRequired() ? " *" : "" ) );
		this.repaint();
		this.revalidate();
	}
}