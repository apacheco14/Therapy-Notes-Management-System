package com.alexpacheco.therapynotes.view.screens;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import javax.swing.SwingConstants;
import javax.swing.table.TableColumnModel;

import com.alexpacheco.therapynotes.controller.AppController;
import com.alexpacheco.therapynotes.controller.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.entities.Note;
import com.alexpacheco.therapynotes.util.DateFormatUtil;
import com.alexpacheco.therapynotes.view.components.Cmb_ClientSelection;
import com.alexpacheco.therapynotes.view.tablemodels.NoteSearchResultsTableModel;
import com.toedter.calendar.JDateChooser;

public class Pnl_SearchNote extends JPanel
{
	private static final long serialVersionUID = 1144413567069633820L;
	private Cmb_ClientSelection clientComboBox;
	private JDateChooser startDateChooser;
	private JDateChooser endDateChooser;
	private JTable resultsTable;
	private NoteSearchResultsTableModel tableModel;
	private JScrollPane scrollPane;
	private JLabel noResultsLabel;
	private JPanel resultsPanel;
	private CardLayout resultsCardLayout;
	private SimpleDateFormat dateFormat = new SimpleDateFormat( "MM/dd/yyyy" );
	
	public Pnl_SearchNote()
	{
		setLayout( new BorderLayout() );
		
		JLabel titleLabel = new JLabel( "Search Notes", SwingConstants.CENTER );
		titleLabel.setFont( new Font( "Arial", Font.BOLD, 24 ) );
		titleLabel.setBorder( BorderFactory.createEmptyBorder( 20, 0, 20, 0 ) );
		
		// Search criteria panel
		JPanel searchPanel = new JPanel( new GridBagLayout() );
		searchPanel.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createEmptyBorder( 10, 50, 10, 50 ),
				BorderFactory.createTitledBorder( "Search Criteria" ) ) );
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets( 5, 5, 5, 5 );
		
		clientComboBox = new Cmb_ClientSelection( true );
		
		startDateChooser = new JDateChooser();
		startDateChooser.setDateFormatString( "MM/dd/yyyy" );
		startDateChooser.setPreferredSize( new Dimension( 150, 25 ) );
		
		endDateChooser = new JDateChooser();
		endDateChooser.setDateFormatString( "MM/dd/yyyy" );
		endDateChooser.setPreferredSize( new Dimension( 150, 25 ) );
		
		// Row 0 - Client
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0.0;
		searchPanel.add( new JLabel( "Client:" ), gbc );
		
		gbc.gridx = 1;
		gbc.gridwidth = 3;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		searchPanel.add( clientComboBox, gbc );
		
		gbc.gridwidth = 1;
		
		// Row 1 - Date Range
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0.0;
		searchPanel.add( new JLabel( "Start Date:" ), gbc );
		
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 0.5;
		searchPanel.add( startDateChooser, gbc );
		
		gbc.gridx = 2;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0.0;
		searchPanel.add( new JLabel( "End Date:" ), gbc );
		
		gbc.gridx = 3;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 0.5;
		searchPanel.add( endDateChooser, gbc );
		
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
		tableModel = new NoteSearchResultsTableModel();
		
		resultsTable = new JTable( tableModel );
		resultsTable.setRowHeight( 30 );
		resultsTable.getTableHeader().setReorderingAllowed( false );
		resultsTable.setAutoCreateRowSorter( true );
		
		// Hide the Note ID column
		resultsTable.getColumnModel().getColumn( 0 ).setMinWidth( 0 );
		resultsTable.getColumnModel().getColumn( 0 ).setMaxWidth( 0 );
		resultsTable.getColumnModel().getColumn( 0 ).setWidth( 0 );
		
		// Add Open button to last column
		resultsTable.getColumn( "Open" ).setCellRenderer( ( table, value, isSelected, hasFocus, row, column ) ->
		{
			JButton button = new JButton( "Open" );
			return button;
		} );
		
		resultsTable.getColumn( "Open" ).setCellEditor( new DefaultCellEditor( new JCheckBox() )
		{
			private static final long serialVersionUID = 2362695094036944167L;
			private JButton button = new JButton( "Open" );
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
					Integer noteId = (Integer) tableModel.getNoteIdAt( currentRow );
					AppController.openNote( noteId );
					fireEditingStopped();
				} );
				return button;
			}
		} );
		
		// Set column widths
		TableColumnModel columnModel = resultsTable.getColumnModel();
		columnModel.getColumn( NoteSearchResultsTableModel.COL_CLIENT ).setPreferredWidth( 200 );
		columnModel.getColumn( NoteSearchResultsTableModel.COL_SESSION_NUM ).setPreferredWidth( 50 );
		columnModel.getColumn( NoteSearchResultsTableModel.COL_APPT_DATE ).setPreferredWidth( 120 );
		columnModel.getColumn( NoteSearchResultsTableModel.COL_APPT_COMMENT ).setPreferredWidth( 200 );
		columnModel.getColumn( NoteSearchResultsTableModel.COL_OPEN_BUTTON ).setPreferredWidth( 50 );
		
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
		clientComboBox.setSelectedIndex( 0 ); // Select blank option
		startDateChooser.setDate( null );
		endDateChooser.setDate( null );
		clearResults();
	}
	
	private void clearResults()
	{
		tableModel.setRowCount( 0 );
	}
	
	private void performSearch()
	{
		// Get selected client ID (if any)
		Integer clientId = clientComboBox.getSelectedClientId();
		
		// Get date range
		Date startDate = startDateChooser.getDate();
		Date endDate = endDateChooser.getDate();
		
		// Validate date range
		if( startDate != null && endDate != null && startDate.after( endDate ) )
		{
			JOptionPane.showMessageDialog( this, "Start Date must be before or equal to End Date.", "Validation Error",
					JOptionPane.ERROR_MESSAGE );
			return;
		}
		
		try
		{
			displayResults( AppController.searchNotes( clientId, startDate, endDate ) );
		}
		catch( TherapyAppException e )
		{
			AppController.showBasicErrorPopup( e, "Error searching notes:" );
		}
	}
	
	private void displayResults( List<Note> notes )
	{
		clearResults();
		
		if( notes == null || notes.isEmpty() )
		{
			resultsCardLayout.show( resultsPanel, "noResults" );
		}
		else
		{
			for( Note note : notes )
			{
				Integer noteId = note.getNoteId();
				String clientName = note.getClient().getDisplayName();
				Integer sessionNumber = note.getSessionNumber();
				Date apptDate = DateFormatUtil.toDate( note.getApptDateTime() );
				String apptNote = note.getApptComment();
				
				String formattedDate = apptDate != null ? dateFormat.format( apptDate ) : "";
				
				Object[] rowData = { noteId, // Hidden column
						clientName, sessionNumber, formattedDate, apptNote, "Open" };
				
				tableModel.addRow( rowData );
			}
			
			resultsCardLayout.show( resultsPanel, "table" );
		}
	}
}