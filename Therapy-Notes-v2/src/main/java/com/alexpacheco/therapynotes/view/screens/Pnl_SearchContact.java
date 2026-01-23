package com.alexpacheco.therapynotes.view.screens;

import javax.swing.*;
import javax.swing.table.TableColumnModel;

import com.alexpacheco.therapynotes.controller.AppController;
import com.alexpacheco.therapynotes.controller.enums.Screens;
import com.alexpacheco.therapynotes.controller.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.entities.Contact;
import com.alexpacheco.therapynotes.util.AppFonts;
import com.alexpacheco.therapynotes.view.components.Cmb_ClientSelection;
import com.alexpacheco.therapynotes.view.tablemodels.ContactSearchResultsTableModel;

import java.awt.*;
import java.util.List;

public class Pnl_SearchContact extends JPanel
{
	private static final long serialVersionUID = 5334371778026922433L;
	private JTextField firstNameField;
	private JTextField lastNameField;
	private Cmb_ClientSelection linkedClientComboBox;
	private JTable resultsTable;
	private ContactSearchResultsTableModel tableModel;
	private JScrollPane scrollPane;
	private JLabel noResultsLabel;
	private JPanel resultsPanel;
	private CardLayout resultsCardLayout;
	private CardLayout parentCardLayout;
	private JPanel parentPanel;
	private Pnl_NewEditContact editContactPanel;
	
	public Pnl_SearchContact( CardLayout cardLayout, JPanel mainPanel, Pnl_NewEditContact editContactPanel )
	{
		this.parentCardLayout = cardLayout;
		this.parentPanel = mainPanel;
		this.editContactPanel = editContactPanel;
		
		setLayout( new BorderLayout() );
		
		JLabel titleLabel = new JLabel( "Search Contacts", SwingConstants.CENTER );
		titleLabel.setFont( AppFonts.getScreenTitleFont() );
		titleLabel.setBorder( BorderFactory.createEmptyBorder( 20, 0, 20, 0 ) );
		
		// Search criteria panel
		JPanel searchPanel = new JPanel( new GridBagLayout() );
		searchPanel.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createEmptyBorder( 10, 50, 10, 50 ),
				BorderFactory.createTitledBorder( "Search Criteria" ) ) );
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets( 5, 5, 5, 5 );
		
		firstNameField = new JTextField( 20 );
		lastNameField = new JTextField( 20 );
		linkedClientComboBox = new Cmb_ClientSelection( true );
		
		// Row 0
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0.0;
		searchPanel.add( new JLabel( "First Name:" ), gbc );
		
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		searchPanel.add( firstNameField, gbc );
		
		gbc.gridx = 2;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0.0;
		searchPanel.add( new JLabel( "Last Name:" ), gbc );
		
		gbc.gridx = 3;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		searchPanel.add( lastNameField, gbc );
		
		// Row 1
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0.0;
		searchPanel.add( new JLabel( "Linked Client:" ), gbc );
		
		gbc.gridx = 1;
		gbc.gridwidth = 3;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		searchPanel.add( linkedClientComboBox, gbc );
		
		gbc.gridwidth = 1;
		
		// Button row
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 4;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weightx = 0.0;
		
		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 10, 10 ) );
		JButton clearButton = new JButton( "Clear Fields" );
		JButton searchButton = new JButton( "Search" );
		
		clearButton.addActionListener( e -> clearFields() );
		searchButton.addActionListener( e -> performSearch() );
		
		buttonPanel.add( clearButton );
		buttonPanel.add( searchButton );
		searchPanel.add( buttonPanel, gbc );
		
		// Results panel with CardLayout
		resultsCardLayout = new CardLayout();
		resultsPanel = new JPanel( resultsCardLayout );
		resultsPanel.setBorder( BorderFactory.createEmptyBorder( 10, 50, 20, 50 ) );
		
		// Table setup
		tableModel = new ContactSearchResultsTableModel();
		
		resultsTable = new JTable( tableModel );
		resultsTable.setRowHeight( 30 );
		resultsTable.getTableHeader().setReorderingAllowed( false );
		
		// Hide the Contact ID column
		resultsTable.getColumnModel().getColumn( 0 ).setMinWidth( 0 );
		resultsTable.getColumnModel().getColumn( 0 ).setMaxWidth( 0 );
		resultsTable.getColumnModel().getColumn( 0 ).setWidth( 0 );
		
		// Add Edit button to last column
		resultsTable.getColumn( "Edit" ).setCellRenderer( ( table, value, isSelected, hasFocus, row, column ) ->
		{
			JButton button = new JButton( "Edit" );
			return button;
		} );
		
		resultsTable.getColumn( "Edit" ).setCellEditor( new DefaultCellEditor( new JCheckBox() )
		{
			private static final long serialVersionUID = -8805306434602082580L;
			private JButton button = new JButton( "Edit" );
			private int currentRow;
			
			@Override
			public Component getTableCellEditorComponent( JTable table, Object value, boolean isSelected, int row, int column )
			{
				currentRow = row;
				
				// Remove all previous action listeners
				for( java.awt.event.ActionListener al : button.getActionListeners() )
				{
					button.removeActionListener( al );
				}
				
				// Add new action listener for this row
				button.addActionListener( e ->
				{
					Integer contactId = tableModel.getContactIdAt( currentRow );
					editContact( contactId );
					fireEditingStopped();
				} );
				return button;
			}
		} );
		
		// Set column widths
		TableColumnModel columnModel = resultsTable.getColumnModel();
		columnModel.getColumn( 1 ).setPreferredWidth( 120 );
		columnModel.getColumn( 2 ).setPreferredWidth( 120 );
		columnModel.getColumn( 3 ).setPreferredWidth( 150 );
		columnModel.getColumn( 4 ).setPreferredWidth( 120 );
		columnModel.getColumn( 5 ).setPreferredWidth( 150 );
		columnModel.getColumn( 6 ).setPreferredWidth( 120 );
		columnModel.getColumn( 7 ).setPreferredWidth( 80 );
		
		scrollPane = new JScrollPane( resultsTable );
		
		JPanel noResultsPanel = new JPanel( new BorderLayout() );
		noResultsLabel = new JLabel( "No results found", SwingConstants.CENTER );
		noResultsLabel.setFont( new Font( "Arial", Font.PLAIN, 16 ) );
		noResultsLabel.setForeground( Color.GRAY );
		noResultsPanel.add( noResultsLabel, BorderLayout.CENTER );
		
		resultsPanel.add( scrollPane, "table" );
		resultsPanel.add( noResultsPanel, "noResults" );
		
		add( titleLabel, BorderLayout.NORTH );
		add( searchPanel, BorderLayout.NORTH );
		add( resultsPanel, BorderLayout.CENTER );
	}
	
	public void clearFields()
	{
		firstNameField.setText( "" );
		lastNameField.setText( "" );
		linkedClientComboBox.setSelectedIndex( 0 ); // Select blank option
		clearResults();
	}
	
	private void clearResults()
	{
		tableModel.setRowCount( 0 );
	}
	
	private void performSearch()
	{
		try
		{
			List<Contact> contacts = AppController.searchContacts( firstNameField.getText().trim(), lastNameField.getText().trim(),
					linkedClientComboBox.getSelectedClientId() );
			displayResults( contacts );
		}
		catch( TherapyAppException e )
		{
			AppController.showBasicErrorPopup( e, "Error searching contacts:" );
		}
	}
	
	private void displayResults( List<Contact> contacts )
	{
		clearResults();
		
		if( contacts == null || contacts.isEmpty() )
		{
			resultsCardLayout.show( resultsPanel, "noResults" );
		}
		else
		{
			for( Contact contact : contacts )
			{
				String emergencyContact = contact.isEmergencyContact() ? "Yes" : "No";
				String email = contact.getEmail1() != null ? contact.getEmail1() : "";
				String phone = contact.getPhone1() != null ? contact.getPhone1() : "";
				String linkedClientName = linkedClientComboBox.getClientName( contact.getLinkedClientId() );
				
				Object[] rowData = { contact.getContactId(), // Hidden column
						contact.getFirstName(), contact.getLastName(), linkedClientName, emergencyContact, email, phone, "Edit" };
				
				tableModel.addRow( rowData );
			}
			
			resultsCardLayout.show( resultsPanel, "table" );
		}
	}
	
	private void editContact( Integer contactId )
	{
		editContactPanel.setEditMode( contactId );
		parentCardLayout.show( parentPanel, Screens.NEW_EDIT_CONTACT.getPanelName() );
	}
}