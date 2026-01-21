package com.alexpacheco.therapynotes.view.screens;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.alexpacheco.therapynotes.controller.AppController;
import com.alexpacheco.therapynotes.controller.enums.Screens;
import com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.entities.Client;
import com.alexpacheco.therapynotes.model.entities.Contact;
import com.alexpacheco.therapynotes.util.DateFormatUtil;
import com.alexpacheco.therapynotes.util.JavaUtils;
import com.alexpacheco.therapynotes.view.tablemodels.ContactsTableModel;

import java.awt.*;
import java.util.List;

public class Pnl_ViewClientDetails extends JPanel
{
	private static final long serialVersionUID = -387942566164331127L;
	private JLabel clientCodeLabel;
	private JLabel firstNameLabel;
	private JLabel lastNameLabel;
	private JLabel dateOfBirthLabel;
	private JLabel email1Label;
	private JLabel email2Label;
	private JLabel email3Label;
	private JLabel phone1Label;
	private JLabel phone2Label;
	private JLabel phone3Label;
	private JTable contactsTable;
	private DefaultTableModel contactsTableModel;
	private JScrollPane contactsScrollPane;
	private JLabel noContactsLabel;
	private JPanel contactsDisplayPanel;
	private CardLayout contactsCardLayout;
	private CardLayout parentCardLayout;
	private JPanel parentPanel;
	
	public Pnl_ViewClientDetails( CardLayout cardLayout, JPanel mainPanel )
	{
		this.parentCardLayout = cardLayout;
		this.parentPanel = mainPanel;
		
		setLayout( new BorderLayout() );
		
		JLabel titleLabel = new JLabel( "Client Details", SwingConstants.CENTER );
		titleLabel.setFont( new Font( "Arial", Font.BOLD, 24 ) );
		titleLabel.setBorder( BorderFactory.createEmptyBorder( 20, 0, 20, 0 ) );
		
		// Details panel - 3 columns layout
		JPanel detailsPanel = new JPanel( new GridBagLayout() );
		detailsPanel.setBorder( BorderFactory.createEmptyBorder( 20, 50, 20, 50 ) );
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets( 10, 10, 10, 10 );
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		
		// Initialize labels
		clientCodeLabel = new JLabel();
		firstNameLabel = new JLabel();
		lastNameLabel = new JLabel();
		dateOfBirthLabel = new JLabel();
		email1Label = new JLabel();
		email2Label = new JLabel();
		email3Label = new JLabel();
		phone1Label = new JLabel();
		phone2Label = new JLabel();
		phone3Label = new JLabel();
		
		Font labelFont = new Font( "Arial", Font.PLAIN, 14 );
		Font valueFont = new Font( "Arial", Font.BOLD, 14 );
		
		clientCodeLabel.setFont( valueFont );
		firstNameLabel.setFont( valueFont );
		lastNameLabel.setFont( valueFont );
		dateOfBirthLabel.setFont( valueFont );
		email1Label.setFont( valueFont );
		email2Label.setFont( valueFont );
		email3Label.setFont( valueFont );
		phone1Label.setFont( valueFont );
		phone2Label.setFont( valueFont );
		phone3Label.setFont( valueFont );
		
		// Column 1: Client Code, First Name, Last Name, DOB
		JPanel column1 = new JPanel( new GridBagLayout() );
		GridBagConstraints col1gbc = new GridBagConstraints();
		col1gbc.fill = GridBagConstraints.HORIZONTAL;
		col1gbc.insets = new Insets( 5, 5, 5, 5 );
		col1gbc.anchor = GridBagConstraints.WEST;
		col1gbc.gridx = 0;
		col1gbc.weightx = 0.0;
		
		col1gbc.gridy = 0;
		JLabel clientCodeTitleLabel = new JLabel( "Client Code:" );
		clientCodeTitleLabel.setFont( labelFont );
		column1.add( clientCodeTitleLabel, col1gbc );
		col1gbc.gridx = 1;
		col1gbc.weightx = 1.0;
		column1.add( clientCodeLabel, col1gbc );
		
		col1gbc.gridx = 0;
		col1gbc.gridy = 1;
		col1gbc.weightx = 0.0;
		JLabel firstNameTitleLabel = new JLabel( "First Name:" );
		firstNameTitleLabel.setFont( labelFont );
		column1.add( firstNameTitleLabel, col1gbc );
		col1gbc.gridx = 1;
		col1gbc.weightx = 1.0;
		column1.add( firstNameLabel, col1gbc );
		
		col1gbc.gridx = 0;
		col1gbc.gridy = 2;
		col1gbc.weightx = 0.0;
		JLabel lastNameTitleLabel = new JLabel( "Last Name:" );
		lastNameTitleLabel.setFont( labelFont );
		column1.add( lastNameTitleLabel, col1gbc );
		col1gbc.gridx = 1;
		col1gbc.weightx = 1.0;
		column1.add( lastNameLabel, col1gbc );
		
		col1gbc.gridx = 0;
		col1gbc.gridy = 3;
		col1gbc.weightx = 0.0;
		JLabel dateOfBirthTitleLabel = new JLabel( "DOB:" );
		dateOfBirthTitleLabel.setFont( labelFont );
		column1.add( dateOfBirthTitleLabel, col1gbc );
		col1gbc.gridx = 1;
		col1gbc.weightx = 1.0;
		column1.add( dateOfBirthLabel, col1gbc );
		
		// Column 2: Emails
		JPanel column2 = new JPanel( new GridBagLayout() );
		GridBagConstraints col2gbc = new GridBagConstraints();
		col2gbc.fill = GridBagConstraints.HORIZONTAL;
		col2gbc.insets = new Insets( 5, 5, 5, 5 );
		col2gbc.anchor = GridBagConstraints.WEST;
		col2gbc.gridx = 0;
		col2gbc.weightx = 0.0;
		
		col2gbc.gridy = 0;
		JLabel email1TitleLabel = new JLabel( "Email 1:" );
		email1TitleLabel.setFont( labelFont );
		column2.add( email1TitleLabel, col2gbc );
		col2gbc.gridx = 1;
		col2gbc.weightx = 1.0;
		column2.add( email1Label, col2gbc );
		
		col2gbc.gridx = 0;
		col2gbc.gridy = 1;
		col2gbc.weightx = 0.0;
		JLabel email2TitleLabel = new JLabel( "Email 2:" );
		email2TitleLabel.setFont( labelFont );
		column2.add( email2TitleLabel, col2gbc );
		col2gbc.gridx = 1;
		col2gbc.weightx = 1.0;
		column2.add( email2Label, col2gbc );
		
		col2gbc.gridx = 0;
		col2gbc.gridy = 2;
		col2gbc.weightx = 0.0;
		JLabel email3TitleLabel = new JLabel( "Email 3:" );
		email3TitleLabel.setFont( labelFont );
		column2.add( email3TitleLabel, col2gbc );
		col2gbc.gridx = 1;
		col2gbc.weightx = 1.0;
		column2.add( email3Label, col2gbc );
		
		// Column 3: Phones
		JPanel column3 = new JPanel( new GridBagLayout() );
		GridBagConstraints col3gbc = new GridBagConstraints();
		col3gbc.fill = GridBagConstraints.HORIZONTAL;
		col3gbc.insets = new Insets( 5, 5, 5, 5 );
		col3gbc.anchor = GridBagConstraints.WEST;
		col3gbc.gridx = 0;
		col3gbc.weightx = 0.0;
		
		col3gbc.gridy = 0;
		JLabel phone1TitleLabel = new JLabel( "Phone 1:" );
		phone1TitleLabel.setFont( labelFont );
		column3.add( phone1TitleLabel, col3gbc );
		col3gbc.gridx = 1;
		col3gbc.weightx = 1.0;
		column3.add( phone1Label, col3gbc );
		
		col3gbc.gridx = 0;
		col3gbc.gridy = 1;
		col3gbc.weightx = 0.0;
		JLabel phone2TitleLabel = new JLabel( "Phone 2:" );
		phone2TitleLabel.setFont( labelFont );
		column3.add( phone2TitleLabel, col3gbc );
		col3gbc.gridx = 1;
		col3gbc.weightx = 1.0;
		column3.add( phone2Label, col3gbc );
		
		col3gbc.gridx = 0;
		col3gbc.gridy = 2;
		col3gbc.weightx = 0.0;
		JLabel phone3TitleLabel = new JLabel( "Phone 3:" );
		phone3TitleLabel.setFont( labelFont );
		column3.add( phone3TitleLabel, col3gbc );
		col3gbc.gridx = 1;
		col3gbc.weightx = 1.0;
		column3.add( phone3Label, col3gbc );
		
		// Add columns to details panel
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		detailsPanel.add( column1, gbc );
		
		gbc.gridx = 1;
		detailsPanel.add( column2, gbc );
		
		gbc.gridx = 2;
		detailsPanel.add( column3, gbc );
		
		// Wrap details panel in a container with scroll
		JPanel detailsContainer = new JPanel( new BorderLayout() );
		detailsContainer.add( detailsPanel, BorderLayout.NORTH );
		
		JScrollPane detailsScrollPane = new JScrollPane( detailsContainer );
		detailsScrollPane.setBorder( null );
		detailsScrollPane.getVerticalScrollBar().setUnitIncrement( 16 );
		
		// Contacts section
		JPanel contactsSection = new JPanel( new BorderLayout() );
		contactsSection.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createEmptyBorder( 10, 50, 10, 50 ),
				BorderFactory.createTitledBorder( "Associated Contacts" ) ) );
		
		// Contacts display panel with CardLayout
		contactsCardLayout = new CardLayout();
		contactsDisplayPanel = new JPanel( contactsCardLayout );
		
		// Contacts table
		contactsTableModel = new ContactsTableModel();
		
		contactsTable = new JTable( contactsTableModel );
		contactsTable.setRowHeight( 25 );
		contactsTable.getTableHeader().setReorderingAllowed( false );
		contactsTable.setCellSelectionEnabled( false );
		contactsTable.setAutoCreateRowSorter( true );
		
		contactsScrollPane = new JScrollPane( contactsTable );
		contactsScrollPane.setPreferredSize( new Dimension( 0, 150 ) );
		
		JPanel noContactsPanel = new JPanel( new BorderLayout() );
		noContactsLabel = new JLabel( "No contacts associated with this client", SwingConstants.CENTER );
		noContactsLabel.setFont( new Font( "Arial", Font.ITALIC, 14 ) );
		noContactsLabel.setForeground( Color.GRAY );
		noContactsPanel.add( noContactsLabel, BorderLayout.CENTER );
		
		contactsDisplayPanel.add( contactsScrollPane, "table" );
		contactsDisplayPanel.add( noContactsPanel, "noContacts" );
		
		contactsSection.add( contactsDisplayPanel, BorderLayout.CENTER );
		
		// Main content panel
		JPanel contentPanel = new JPanel( new BorderLayout() );
		contentPanel.add( detailsScrollPane, BorderLayout.NORTH );
		contentPanel.add( contactsSection, BorderLayout.CENTER );
		
		// Button panel
		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 10, 10 ) );
		buttonPanel.setBorder( BorderFactory.createEmptyBorder( 10, 0, 20, 0 ) );
		
		JButton backButton = new JButton( "Back" );
		backButton.addActionListener( e -> goBack() );
		
		buttonPanel.add( backButton );
		
		add( titleLabel, BorderLayout.NORTH );
		add( contentPanel, BorderLayout.CENTER );
		add( buttonPanel, BorderLayout.SOUTH );
	}
	
	/**
	 * Load and display client details
	 * 
	 * @param clientId The ID of the client to display
	 * @throws TherapyAppException
	 */
	public void loadClientDetails( Integer clientId ) throws TherapyAppException
	{
		Client client = AppController.getClientById( clientId );
		
		if( client != null )
		{
			displayClientDetails( client );
			loadContacts( clientId );
			parentCardLayout.show( parentPanel, Screens.CLIENT_DETAILS.getPanelName() );
		}
		else
		{
			AppController.showBasicErrorPopup( "Client not found." );
			goBack();
		}
	}
	
	private void displayClientDetails( Client client )
	{
		clientCodeLabel.setText( client.getClientCode() + ( client.isInactive() ? " (Inactive)" : "" ) );
		firstNameLabel.setText( JavaUtils.isNullOrEmpty( client.getFirstName() ) ? "N/A" : client.getFirstName() );
		lastNameLabel.setText( JavaUtils.isNullOrEmpty( client.getLastName() ) ? "N/A" : client.getLastName() );
		dateOfBirthLabel.setText( DateFormatUtil.toSimpleString( client.getDateOfBirth() ) );
		
		email1Label.setText( JavaUtils.isNullOrEmpty( client.getEmail1() ) ? "N/A" : client.getEmail1() );
		email2Label.setText( JavaUtils.isNullOrEmpty( client.getEmail2() ) ? "N/A" : client.getEmail2() );
		email3Label.setText( JavaUtils.isNullOrEmpty( client.getEmail3() ) ? "N/A" : client.getEmail3() );
		
		phone1Label.setText( JavaUtils.isNullOrEmpty( client.getPhone1() ) ? "N/A" : client.getPhone1() );
		phone2Label.setText( JavaUtils.isNullOrEmpty( client.getPhone2() ) ? "N/A" : client.getPhone2() );
		phone3Label.setText( JavaUtils.isNullOrEmpty( client.getPhone3() ) ? "N/A" : client.getPhone3() );
	}
	
	private void goBack()
	{
		parentCardLayout.show( parentPanel, Screens.SEARCH_CLIENTS_VIEW.getPanelName() );
	}
	
	private void loadContacts( Integer clientId )
	{
		try
		{
			List<Contact> contacts = AppController.getContactsForClient( clientId );
			displayContacts( contacts );
		}
		catch( TherapyAppException e )
		{
			AppController.showBasicErrorPopup( e, "Error loading contacts:" );
		}
	}
	
	private void displayContacts( List<Contact> contacts )
	{
		contactsTableModel.setRowCount( 0 );
		
		if( contacts == null || contacts.isEmpty() )
		{
			contactsCardLayout.show( contactsDisplayPanel, "noContacts" );
		}
		else
		{
			for( Contact contact : contacts )
			{
				String emergencyContact = contact.isEmergencyContact() ? "Yes" : "No";
				String email1 = JavaUtils.isNullOrEmpty( contact.getEmail1() ) ? "N/A" : contact.getEmail1();
				String email2 = JavaUtils.isNullOrEmpty( contact.getEmail2() ) ? "N/A" : contact.getEmail2();
				String email3 = JavaUtils.isNullOrEmpty( contact.getEmail3() ) ? "N/A" : contact.getEmail3();
				String phone1 = JavaUtils.isNullOrEmpty( contact.getPhone1() ) ? "N/A" : contact.getPhone1();
				String phone2 = JavaUtils.isNullOrEmpty( contact.getPhone2() ) ? "N/A" : contact.getPhone2();
				String phone3 = JavaUtils.isNullOrEmpty( contact.getPhone3() ) ? "N/A" : contact.getPhone3();
				
				Object[] rowData = { JavaUtils.isNullOrEmpty( contact.getFirstName() ) ? "N/A" : contact.getFirstName(),
						JavaUtils.isNullOrEmpty( contact.getLastName() ) ? "N/A" : contact.getLastName(), emergencyContact, email1, email2,
						email3, phone1, phone2, phone3 };
				
				contactsTableModel.addRow( rowData );
			}
			
			contactsCardLayout.show( contactsDisplayPanel, "table" );
		}
	}
}