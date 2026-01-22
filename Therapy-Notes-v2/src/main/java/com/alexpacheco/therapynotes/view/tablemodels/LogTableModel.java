package com.alexpacheco.therapynotes.view.tablemodels;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import com.alexpacheco.therapynotes.model.entities.AppLog;
import com.alexpacheco.therapynotes.util.DateFormatUtil;

/**
 * Table model for displaying application logs.
 */
public class LogTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 1L;
	
	private static final String[] COLUMN_NAMES = { "ID", "Session ID", "Level", "Source", "Message", "Timestamp" };
	
	private List<AppLog> logs;
	private Set<Integer> expandedRows;
	
	public LogTableModel()
	{
		this.logs = new ArrayList<>();
		this.expandedRows = new HashSet<>();
	}
	
	@Override
	public int getRowCount()
	{
		return logs.size();
	}
	
	@Override
	public int getColumnCount()
	{
		return COLUMN_NAMES.length;
	}
	
	@Override
	public String getColumnName( int column )
	{
		return COLUMN_NAMES[column];
	}
	
	@Override
	public Class<?> getColumnClass( int columnIndex )
	{
		switch( columnIndex )
		{
			case 0:
				return Integer.class;
			default:
				return String.class;
		}
	}
	
	@Override
	public boolean isCellEditable( int rowIndex, int columnIndex )
	{
		return false;
	}
	
	@Override
	public Object getValueAt( int rowIndex, int columnIndex )
	{
		if( rowIndex < 0 || rowIndex >= logs.size() )
		{
			return null;
		}
		
		AppLog log = logs.get( rowIndex );
		
		switch( columnIndex )
		{
			case 0:
				return log.getId();
			case 1:
				return log.getSessionId();
			case 2:
				return log.getLevel();
			case 3:
				return log.getSource();
			case 4:
				return getMessageDisplay( log, rowIndex );
			case 5:
				return log.getTimestamp() != null ? DateFormatUtil.toSqliteString( log.getTimestamp() ) : "";
			default:
				return null;
		}
	}
	
	/**
	 * Gets the message display, showing full message when expanded or first line with indicator when collapsed.
	 */
	private String getMessageDisplay( AppLog log, int rowIndex )
	{
		String message = log.getMessage();
		
		if( hasStacktrace( message ) )
		{
			if( expandedRows.contains( rowIndex ) )
			{
				// Show full message when expanded
				return message;
			}
			else
			{
				// Show first line with indicator when collapsed
				String firstLine = getFirstLine( message );
				return firstLine + " [+]";
			}
		}
		
		return message;
	}
	
	/**
	 * Checks if the message contains a stacktrace.
	 */
	public boolean hasStacktrace( String message )
	{
		if( message == null || message.isEmpty() )
		{
			return false;
		}
		
		// Check for common stacktrace indicators
		return message.contains( "\n\tat " ) || message.contains( "\nCaused by:" ) || message.contains( "\n\t... " )
				|| ( message.contains( "Exception" ) && message.contains( "\n" ) );
	}
	
	/**
	 * Gets the first line of a message.
	 */
	private String getFirstLine( String message )
	{
		if( message == null )
		{
			return "";
		}
		
		int newlineIndex = message.indexOf( '\n' );
		if( newlineIndex > 0 )
		{
			return message.substring( 0, newlineIndex );
		}
		
		return message;
	}
	
	/**
	 * Sets whether a row is expanded.
	 */
	public void setRowExpanded( int rowIndex, boolean expanded )
	{
		if( expanded )
		{
			expandedRows.add( rowIndex );
		}
		else
		{
			expandedRows.remove( rowIndex );
		}
	}
	
	/**
	 * Checks if a row is expanded.
	 */
	public boolean isRowExpanded( int rowIndex )
	{
		return expandedRows.contains( rowIndex );
	}
	
	/**
	 * Adds a log entry to the table.
	 */
	public void addLog( AppLog log )
	{
		logs.add( log );
		fireTableRowsInserted( logs.size() - 1, logs.size() - 1 );
	}
	
	/**
	 * Clears all logs from the table.
	 */
	public void clearLogs()
	{
		int size = logs.size();
		if( size > 0 )
		{
			logs.clear();
			expandedRows.clear();
			fireTableRowsDeleted( 0, size - 1 );
		}
	}
	
	/**
	 * Gets the AppLog at the specified row index.
	 */
	public AppLog getLogAt( int rowIndex )
	{
		if( rowIndex >= 0 && rowIndex < logs.size() )
		{
			return logs.get( rowIndex );
		}
		return null;
	}
	
	/**
	 * Gets all logs in the model.
	 */
	public List<AppLog> getAllLogs()
	{
		return new ArrayList<>( logs );
	}
}