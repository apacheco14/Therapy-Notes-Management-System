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

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public abstract class Pnl_SearchBase extends JPanel
{
	private static final long serialVersionUID = 7666233389604741198L;
	protected JTable resultsTable;
	protected JScrollPane scrollPane;
	protected JLabel noResultsLabel;
	protected JPanel resultsPanel;
	protected CardLayout parentCardLayout;
	protected JPanel parentPanel;
	protected CardLayout resultsCardLayout;
	
	public Pnl_SearchBase(CardLayout cardLayout, JPanel mainPanel)
	{
		this.parentCardLayout = cardLayout;
		this.parentPanel = mainPanel;
		
		setLayout(new BorderLayout());
		
		JLabel titleLabel = new JLabel(getScreenTitle(), SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
		titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
		
		// Search criteria panel
		JPanel searchPanel = new JPanel(new GridBagLayout());
		searchPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50),
				BorderFactory.createTitledBorder("Search Criteria")));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);
		
		setupSearchCriteria(searchPanel, gbc);
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		JButton clearButton = new JButton("Clear Fields");
		JButton searchButton = new JButton("Search");
		
		clearButton.addActionListener(e -> clearFields());
		searchButton.addActionListener(e -> performSearch());
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 4;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weightx = 0.0;
		buttonPanel.add(clearButton);
		buttonPanel.add(searchButton);
		searchPanel.add(buttonPanel, gbc);
		
		// Results panel
		resultsCardLayout = new CardLayout();
		resultsPanel = new JPanel(resultsCardLayout);
		resultsPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 20, 50));
		
		resultsTable = new JTable(getTableModel());
		resultsTable.setRowHeight(30);
		resultsTable.getTableHeader().setReorderingAllowed(false);
		
		// Hide the ID column
		resultsTable.getColumnModel().getColumn(0).setMinWidth(0);
		resultsTable.getColumnModel().getColumn(0).setMaxWidth(0);
		resultsTable.getColumnModel().getColumn(0).setWidth(0);
		
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
					Integer id = getRowId(currentRow);
					doRowLevelAction(id);
					fireEditingStopped();
				});
				return button;
			}
		});
		
		setColumnWidths();
		
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
	
	protected abstract String getScreenTitle();
	
	protected abstract void setupSearchCriteria(JPanel searchPanel, GridBagConstraints gbc);
	
	public abstract void clearFields();
	
	public abstract void performSearch();
	
	protected abstract DefaultTableModel getTableModel();
	
	protected abstract String getRowLevelButtonTitle();
	
	protected abstract Integer getRowId(int currentRow);
	
	protected abstract void doRowLevelAction(Integer clientId);
	
	protected abstract void setColumnWidths();
}
