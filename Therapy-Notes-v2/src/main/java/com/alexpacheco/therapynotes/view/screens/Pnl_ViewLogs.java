package com.alexpacheco.therapynotes.view.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import com.alexpacheco.therapynotes.controller.AppController;
import com.alexpacheco.therapynotes.controller.enums.LogLevel;
import com.alexpacheco.therapynotes.controller.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.entities.AppLog;
import com.alexpacheco.therapynotes.view.tablemodels.LogTableModel;
import com.toedter.calendar.JDateChooser;

/**
 * Panel for viewing application logs from the app_logs table. Supports filtering by date range and log level, with expandable rows for
 * stacktraces.
 */
public class Pnl_ViewLogs extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	// Filter components
	private JDateChooser startDateChooser;
	private JDateChooser endDateChooser;
	private JComboBox<String> logLevelComboBox;
	
	// Table components
	private JTable logsTable;
	private LogTableModel tableModel;
	private JScrollPane tableScrollPane;
	
	// Results panel
	private JPanel resultsPanel;
	private JLabel noResultsLabel;
	
	private static final int MAX_RESULTS = 1000;
	
	public Pnl_ViewLogs()
	{
		initComponents();
		layoutComponents();
	}
	
	private void initComponents()
	{
		// Date choosers
		startDateChooser = new JDateChooser();
		startDateChooser.setDateFormatString( "MM/dd/yyyy" );
		startDateChooser.setPreferredSize( new Dimension( 150, 25 ) );
		
		endDateChooser = new JDateChooser();
		endDateChooser.setDateFormatString( "MM/dd/yyyy" );
		endDateChooser.setPreferredSize( new Dimension( 150, 25 ) );
		
		// Log level dropdown
		logLevelComboBox = new JComboBox<>();
		logLevelComboBox.addItem( "" ); // All levels
		for( LogLevel level : LogLevel.values() )
		{
			logLevelComboBox.addItem( level.getDbCode() );
		}
		logLevelComboBox.setPreferredSize( new Dimension( 100, 25 ) );
		
		// Table setup
		tableModel = new LogTableModel();
		logsTable = new JTable( tableModel );
		logsTable.setRowHeight( 25 );
		logsTable.getTableHeader().setReorderingAllowed( false );
		logsTable.setAutoCreateRowSorter( true );
		
		// Set column widths
		logsTable.getColumnModel().getColumn( 0 ).setPreferredWidth( 50 ); // ID
		logsTable.getColumnModel().getColumn( 1 ).setPreferredWidth( 200 ); // Session ID
		logsTable.getColumnModel().getColumn( 2 ).setPreferredWidth( 60 ); // Level
		logsTable.getColumnModel().getColumn( 3 ).setPreferredWidth( 150 ); // Source
		logsTable.getColumnModel().getColumn( 4 ).setPreferredWidth( 400 ); // Message
		logsTable.getColumnModel().getColumn( 5 ).setPreferredWidth( 140 ); // Timestamp
		
		// Custom renderer for log level colors
		logsTable.getColumnModel().getColumn( 2 ).setCellRenderer( new LogLevelCellRenderer() );
		
		// Custom renderer for message column with multiline support
		logsTable.getColumnModel().getColumn( 4 ).setCellRenderer( new MessageCellRenderer() );
		
		// Row click listener for expanding/collapsing stacktrace
		logsTable.addMouseListener( new MouseAdapter()
		{
			@Override
			public void mouseClicked( MouseEvent e )
			{
				int row = logsTable.rowAtPoint( e.getPoint() );
				if( row >= 0 )
				{
					int modelRow = logsTable.convertRowIndexToModel( row );
					toggleRowExpansion( modelRow );
				}
			}
		} );
		
		tableScrollPane = new JScrollPane( logsTable );
		
		// No results label
		noResultsLabel = new JLabel( "No logs found", SwingConstants.CENTER );
		noResultsLabel.setFont( new Font( "Arial", Font.PLAIN, 16 ) );
		noResultsLabel.setForeground( Color.GRAY );
	}
	
	private void layoutComponents()
	{
		setLayout( new BorderLayout() );
		
		// Title
		JLabel titleLabel = new JLabel( "Application Logs", SwingConstants.CENTER );
		titleLabel.setFont( new Font( "Arial", Font.BOLD, 24 ) );
		titleLabel.setBorder( BorderFactory.createEmptyBorder( 20, 0, 10, 0 ) );
		
		// Filter panel
		JPanel filterPanel = createFilterPanel();
		
		// Header panel (title + filters)
		JPanel headerPanel = new JPanel( new BorderLayout() );
		headerPanel.add( titleLabel, BorderLayout.NORTH );
		headerPanel.add( filterPanel, BorderLayout.CENTER );
		
		// Results panel
		resultsPanel = new JPanel( new BorderLayout() );
		resultsPanel.setBorder( BorderFactory.createEmptyBorder( 10, 20, 20, 20 ) );
		resultsPanel.add( tableScrollPane, BorderLayout.CENTER );
		
		add( headerPanel, BorderLayout.NORTH );
		add( resultsPanel, BorderLayout.CENTER );
	}
	
	private JPanel createFilterPanel()
	{
		JPanel panel = new JPanel( new GridBagLayout() );
		panel.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createEmptyBorder( 10, 50, 10, 50 ),
				BorderFactory.createTitledBorder( "Filter Criteria" ) ) );
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets( 5, 5, 5, 5 );
		gbc.anchor = GridBagConstraints.WEST;
		
		// Row 0 - Date range and log level
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add( new JLabel( "Start Date:" ), gbc );
		
		gbc.gridx = 1;
		panel.add( startDateChooser, gbc );
		
		gbc.gridx = 2;
		panel.add( new JLabel( "End Date:" ), gbc );
		
		gbc.gridx = 3;
		panel.add( endDateChooser, gbc );
		
		gbc.gridx = 4;
		panel.add( new JLabel( "Log Level:" ), gbc );
		
		gbc.gridx = 5;
		panel.add( logLevelComboBox, gbc );
		
		// Row 1 - Buttons
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 6;
		gbc.anchor = GridBagConstraints.CENTER;
		
		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 10, 5 ) );
		JButton clearButton = new JButton( "Clear Filters" );
		JButton searchButton = new JButton( "Search" );
		
		clearButton.addActionListener( e -> clearFilters() );
		searchButton.addActionListener( e -> performSearch() );
		
		buttonPanel.add( clearButton );
		buttonPanel.add( searchButton );
		panel.add( buttonPanel, gbc );
		
		return panel;
	}
	
	private void clearFilters()
	{
		startDateChooser.setDate( null );
		endDateChooser.setDate( null );
		logLevelComboBox.setSelectedIndex( 0 );
		clearResults();
	}
	
	private void clearResults()
	{
		tableModel.clearLogs();
	}
	
	private void performSearch()
	{
		Date startDate = startDateChooser.getDate();
		Date endDate = endDateChooser.getDate();
		String selectedLevel = (String) logLevelComboBox.getSelectedItem();
		LogLevel logLevel = null;
		
		if( selectedLevel != null && !selectedLevel.isEmpty() )
		{
			for( LogLevel level : LogLevel.values() )
			{
				if( level.getDbCode().equals( selectedLevel ) )
				{
					logLevel = level;
					break;
				}
			}
		}
		
		try
		{
			List<AppLog> logs = AppController.getLogs( startDate, endDate, logLevel, MAX_RESULTS );
			displayResults( logs );
		}
		catch( TherapyAppException e )
		{
			AppController.showBasicErrorPopup( e, "Error loading logs:" );
		}
	}
	
	private void displayResults( List<AppLog> logs )
	{
		clearResults();
		
		if( logs == null || logs.isEmpty() )
		{
			resultsPanel.removeAll();
			resultsPanel.add( noResultsLabel, BorderLayout.CENTER );
			resultsPanel.revalidate();
			resultsPanel.repaint();
		}
		else
		{
			resultsPanel.removeAll();
			resultsPanel.add( tableScrollPane, BorderLayout.CENTER );
			
			for( AppLog log : logs )
			{
				tableModel.addLog( log );
			}
			
			resultsPanel.revalidate();
			resultsPanel.repaint();
		}
	}
	
	private void toggleRowExpansion( int modelRow )
	{
		AppLog log = tableModel.getLogAt( modelRow );
		if( log == null || !tableModel.hasStacktrace( log.getMessage() ) )
		{
			return; // No stacktrace to expand
		}
		
		if( tableModel.isRowExpanded( modelRow ) )
		{
			tableModel.setRowExpanded( modelRow, false );
			logsTable.setRowHeight( modelRow, 25 );
		}
		else
		{
			tableModel.setRowExpanded( modelRow, true );
			// Calculate height based on message lines (approximate)
			int lineCount = log.getMessage().split( "\n" ).length;
			int expandedHeight = Math.min( 25 + ( lineCount * 16 ), 300 ); // Cap at 300px
			logsTable.setRowHeight( modelRow, expandedHeight );
		}
		
		tableModel.fireTableRowsUpdated( modelRow, modelRow );
	}
	
	/**
	 * Custom cell renderer for log level column with color coding.
	 */
	private class LogLevelCellRenderer extends DefaultTableCellRenderer
	{
		private static final long serialVersionUID = 1L;
		
		@Override
		public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
				int column )
		{
			Component c = super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );
			
			if( !isSelected && value != null )
			{
				String level = value.toString();
				switch( level )
				{
					case "SEVERE":
						c.setBackground( new Color( 255, 200, 200 ) );
						c.setForeground( new Color( 139, 0, 0 ) );
						break;
					case "WARNING":
						c.setBackground( new Color( 255, 255, 200 ) );
						c.setForeground( new Color( 184, 134, 11 ) );
						break;
					case "INFO":
						c.setBackground( new Color( 200, 255, 200 ) );
						c.setForeground( new Color( 0, 100, 0 ) );
						break;
					case "FINE":
						c.setBackground( new Color( 220, 220, 220 ) );
						c.setForeground( Color.DARK_GRAY );
						break;
					default:
						c.setBackground( Color.WHITE );
						c.setForeground( Color.BLACK );
				}
			}
			else if( isSelected )
			{
				c.setBackground( table.getSelectionBackground() );
				c.setForeground( table.getSelectionForeground() );
			}
			
			return c;
		}
	}
	
	/**
	 * Custom cell renderer for message column with multiline support.
	 */
	private class MessageCellRenderer extends JTextArea implements javax.swing.table.TableCellRenderer
	{
		private static final long serialVersionUID = 1L;
		
		public MessageCellRenderer()
		{
			setLineWrap( true );
			setWrapStyleWord( true );
			setOpaque( true );
			setFont( logsTable.getFont() );
		}
		
		@Override
		public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
				int column )
		{
			if( isSelected )
			{
				setBackground( table.getSelectionBackground() );
				setForeground( table.getSelectionForeground() );
			}
			else
			{
				setBackground( table.getBackground() );
				setForeground( table.getForeground() );
			}
			
			setText( value != null ? value.toString() : "" );
			
			return this;
		}
	}
}