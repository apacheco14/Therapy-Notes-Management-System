package com.alexpacheco.therapynotes.view.screens;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.TableColumnModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import com.alexpacheco.therapynotes.controller.AppController;
import com.alexpacheco.therapynotes.controller.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.entities.Client;
import com.alexpacheco.therapynotes.view.tablemodels.ClientSearchResultsTableModel;

public abstract class Pnl_SearchClients extends JPanel
{
	private static final long serialVersionUID = 5155306875771239233L;
	protected JTextField firstNameField;
	protected JTextField lastNameField;
	protected JTextField clientCodeField;
	protected JCheckBox showInactiveCheckBox;
	protected JTable resultsTable;
	protected ClientSearchResultsTableModel tableModel;
	protected JScrollPane scrollPane;
	protected JLabel noResultsLabel;
	protected JPanel resultsPanel;
	protected CardLayout parentCardLayout;
	protected JPanel parentPanel;
	protected CardLayout resultsCardLayout;
	
	protected Pnl_SearchClients(CardLayout cardLayout, JPanel mainPanel)
	{
		this.parentCardLayout = cardLayout;
		this.parentPanel = mainPanel;
		
		setLayout(new BorderLayout());
		
		JLabel titleLabel = new JLabel("Search Clients", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
		titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
		
		// Search criteria panel
		JPanel searchPanel = new JPanel(new GridBagLayout());
		searchPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50),
				BorderFactory.createTitledBorder("Search Criteria")));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);
		
		firstNameField = new JTextField(20);
		lastNameField = new JTextField(20);
		clientCodeField = new JTextField(20);
		_setupClientCodeField();
		showInactiveCheckBox = new JCheckBox();
		
		// Row 0
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0.0;
		searchPanel.add(new JLabel("First Name:"), gbc);
		
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		searchPanel.add(firstNameField, gbc);
		
		gbc.gridx = 2;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0.0;
		searchPanel.add(new JLabel("Last Name:"), gbc);
		
		gbc.gridx = 3;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		searchPanel.add(lastNameField, gbc);
		
		// Row 1
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0.0;
		searchPanel.add(new JLabel("Client Code:"), gbc);
		
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		searchPanel.add(clientCodeField, gbc);
		
		gbc.gridx = 2;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0.0;
		searchPanel.add(new JLabel("Show Inactive:"), gbc);
		
		gbc.gridx = 3;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		searchPanel.add(showInactiveCheckBox, gbc);
		
		// Button row
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 4;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weightx = 0.0;
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		JButton clearButton = new JButton("Clear Fields");
		JButton searchButton = new JButton("Search");
		
		clearButton.addActionListener(e -> clearFields());
		searchButton.addActionListener(e -> _performSearch());
		
		buttonPanel.add(clearButton);
		buttonPanel.add(searchButton);
		searchPanel.add(buttonPanel, gbc);
		
		// Results panel
		resultsCardLayout = new CardLayout();
		resultsPanel = new JPanel(resultsCardLayout);
		resultsPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 20, 50));
		
		// Table setup
		tableModel = new ClientSearchResultsTableModel();
		
		resultsTable = new JTable(tableModel);
		resultsTable.setRowHeight(30);
		resultsTable.getTableHeader().setReorderingAllowed(false);
		
		// Hide the Client ID column
		TableColumnModel columnModel = resultsTable.getColumnModel();
		columnModel.getColumn(ClientSearchResultsTableModel.COL_ID).setMinWidth(0);
		columnModel.getColumn(ClientSearchResultsTableModel.COL_ID).setMaxWidth(0);
		columnModel.getColumn(ClientSearchResultsTableModel.COL_ID).setWidth(0);
		
		// Add button to last column
		resultsTable.getColumn("Action").setCellRenderer((table, value, isSelected, hasFocus, row, column) ->
		{
			JButton button = new JButton(getRowLevelButtonTitle());
			return button;
		});
		
		resultsTable.getColumn("Action").setCellEditor(new DefaultCellEditor(new JCheckBox())
		{
			private static final long serialVersionUID = 2025493518599780008L;
			private JButton button = new JButton(getRowLevelButtonTitle());
			private int currentRow;
			
			@Override
			public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
			{
				currentRow = row;
				
				// Remove all previous action listeners
				for (java.awt.event.ActionListener al : button.getActionListeners())
				{
					button.removeActionListener(al);
				}
				
				// Add new action listener for this row
				button.addActionListener(e ->
				{
					Integer clientId = (Integer) tableModel.getValueAt(currentRow, ClientSearchResultsTableModel.COL_ID);
					doRowLevelAction(clientId);
					fireEditingStopped();
				});
				return button;
			}
		});
		
		// Set column widths
		columnModel.getColumn(ClientSearchResultsTableModel.COL_FIRST_NAME).setPreferredWidth(150);
		columnModel.getColumn(ClientSearchResultsTableModel.COL_LAST_NAME).setPreferredWidth(150);
		columnModel.getColumn(ClientSearchResultsTableModel.COL_CLIENT_CODE).setPreferredWidth(120);
		columnModel.getColumn(ClientSearchResultsTableModel.COL_STATUS).setPreferredWidth(100);
		columnModel.getColumn(ClientSearchResultsTableModel.COL_BUTTON).setPreferredWidth(80);
		
		scrollPane = new JScrollPane(resultsTable);
		
		JPanel noResultsPanel = new JPanel(new BorderLayout());
		noResultsLabel = new JLabel("No results found", SwingConstants.CENTER);
		noResultsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
		noResultsLabel.setForeground(Color.GRAY);
		noResultsPanel.add(noResultsLabel, BorderLayout.CENTER);
		
		resultsPanel.add(scrollPane, "table");
		resultsPanel.add(noResultsPanel, "noResults");
		
		add(titleLabel, BorderLayout.NORTH);
		add(searchPanel, BorderLayout.NORTH);
		add(resultsPanel, BorderLayout.CENTER);
	}
	
	public void loadAllClients()
	{
		try
		{
			List<Client> clients = AppController.getAllClients();
			displayResults(clients);
		}
		catch (TherapyAppException e)
		{
			AppController.showBasicErrorPopup(e, "Error loading clients:");
		}
	}
	
	public void clearFields()
	{
		firstNameField.setText("");
		lastNameField.setText("");
		clientCodeField.setText("");
		showInactiveCheckBox.setSelected(false);
		_clearResults();
	}
	
	private void _setupClientCodeField()
	{
		((AbstractDocument) clientCodeField.getDocument()).setDocumentFilter(new DocumentFilter()
		{
			@Override
			public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException
			{
				String upperString = string.toUpperCase();
				super.insertString(fb, offset, upperString, attr);
			}
			
			@Override
			public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException
			{
				String upperText = text.toUpperCase();
				super.replace(fb, offset, length, upperText, attrs);
			}
		});
	}
	
	private void _clearResults()
	{
		tableModel.setRowCount(0);
	}
	
	private void _performSearch()
	{
		String firstName = firstNameField.getText().trim();
		String lastName = lastNameField.getText().trim();
		String clientCode = clientCodeField.getText().trim();
		boolean showInactive = showInactiveCheckBox.isSelected();
		
		try
		{
			List<Client> clients = AppController.searchClients(firstName, lastName, clientCode, showInactive);
			
			displayResults(clients);
		}
		catch (TherapyAppException e)
		{
			JOptionPane.showMessageDialog(this, "Error searching clients: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void displayResults(List<Client> clients)
	{
		_clearResults();
		
		if (clients == null || clients.isEmpty())
		{
			resultsCardLayout.show(resultsPanel, "noResults");
		}
		else
		{
			for (Client client : clients)
			{
				String status = client.isInactive() ? "Inactive" : "Active";
				
				Object[] rowData = { client.getClientId(), // Hidden column
						client.getClientCode(), client.getFirstName(), client.getLastName(), status, "Action" };
				
				tableModel.addRow(rowData);
			}
			
			resultsCardLayout.show(resultsPanel, "table");
		}
	}
	
	protected abstract String getRowLevelButtonTitle();
	
	protected abstract void doRowLevelAction(Integer clientId);
}
