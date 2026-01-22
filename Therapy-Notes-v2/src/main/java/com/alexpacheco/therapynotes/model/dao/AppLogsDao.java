package com.alexpacheco.therapynotes.model.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.alexpacheco.therapynotes.model.entities.AppLog;
import com.alexpacheco.therapynotes.util.AppLogger;

/**
 * Data Access Object for reading file-based application logs. Parses log files and converts them to AppLog entities for display in the UI.
 */
public class AppLogsDao
{
	// Pattern to parse log lines: [ID] [TIMESTAMP] [SESSION] [LEVEL] [SOURCE] MESSAGE
	private static final Pattern LOG_PATTERN = Pattern.compile( "^\\[(\\d+)\\]\\s+" + // ID
			"\\[([^\\]]+)\\]\\s+" + // Timestamp
			"\\[([^\\]]+)\\]\\s+" + // Session ID
			"\\[([^\\]]+)\\]\\s+" + // Level
			"\\[([^\\]]+)\\]\\s+" + // Source
			"(.*)$" // Message
	);
	
	private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss.SSS" );
	
	private final String logDirectory;
	
	/**
	 * Constructor that uses the default log directory from AppLogger.
	 */
	public AppLogsDao()
	{
		this.logDirectory = AppLogger.getLogDirectoryPath();
	}
	
	/**
	 * Constructor with custom log directory (useful for testing).
	 */
	public AppLogsDao( String logDirectory )
	{
		this.logDirectory = logDirectory;
	}
	
	/**
	 * Get all logs from all log files.
	 */
	public List<AppLog> getAllLogs()
	{
		return getAllLogs( null, null, null, null );
	}
	
	/**
	 * Get logs filtered by various criteria.
	 * 
	 * @param startDate Filter logs from this date onwards (inclusive), null for no filter
	 * @param endDate   Filter logs up to this date (inclusive), null for no filter
	 * @param level     Filter by log level (INFO, WARNING, SEVERE), null for all levels
	 * @param sessionId Filter by session ID, null for all sessions
	 * @return List of AppLog entries matching the criteria
	 */
	public List<AppLog> getAllLogs( LocalDateTime startDate, LocalDateTime endDate, String level, String sessionId )
	{
		List<AppLog> logs = new ArrayList<>();
		
		try
		{
			// Get all log files sorted by modification time (newest first)
			List<File> logFiles = getLogFiles();
			
			for( File logFile : logFiles )
			{
				logs.addAll( parseLogFile( logFile, startDate, endDate, level, sessionId ) );
			}
			
			// Sort by timestamp descending (newest first)
			Collections.sort( logs, Collections.reverseOrder() );
		}
		catch( IOException e )
		{
			AppLogger.error( "Failed to read log files", e );
		}
		
		return logs;
	}
	
	/**
	 * Get logs for the current session only.
	 */
	public List<AppLog> getCurrentSessionLogs()
	{
		String currentSession = AppLogger.getSessionId();
		return getAllLogs( null, null, null, currentSession );
	}
	
	/**
	 * Get logs by level.
	 */
	public List<AppLog> getLogsByLevel( String level )
	{
		return getAllLogs( null, null, level, null );
	}
	
	/**
	 * Get logs for a specific date range.
	 */
	public List<AppLog> getLogsByDateRange( LocalDateTime startDate, LocalDateTime endDate )
	{
		return getAllLogs( startDate, endDate, null, null );
	}
	
	/**
	 * Get most recent N logs.
	 */
	public List<AppLog> getRecentLogs( int limit )
	{
		List<AppLog> allLogs = getAllLogs();
		return allLogs.stream().limit( limit ).collect( Collectors.toList() );
	}
	
	/**
	 * Get error logs only (WARNING and SEVERE).
	 */
	public List<AppLog> getErrorLogs()
	{
		return getAllLogs().stream().filter( log -> "WARNING".equals( log.getLevel() ) || "SEVERE".equals( log.getLevel() ) )
				.collect( Collectors.toList() );
	}
	
	/**
	 * Get audit logs only (messages starting with [AUDIT]).
	 */
	public List<AppLog> getAuditLogs()
	{
		return getAllLogs().stream().filter( log -> log.getMessage() != null && log.getMessage().startsWith( "[AUDIT]" ) )
				.collect( Collectors.toList() );
	}
	
	/**
	 * Search logs by message content.
	 */
	public List<AppLog> searchLogs( String searchTerm )
	{
		if( searchTerm == null || searchTerm.trim().isEmpty() )
		{
			return getAllLogs();
		}
		
		String lowerSearchTerm = searchTerm.toLowerCase();
		return getAllLogs().stream().filter( log ->
		{
			String message = log.getMessage() != null ? log.getMessage().toLowerCase() : "";
			String source = log.getSource() != null ? log.getSource().toLowerCase() : "";
			return message.contains( lowerSearchTerm ) || source.contains( lowerSearchTerm );
		} ).collect( Collectors.toList() );
	}
	
	/**
	 * Get all unique session IDs.
	 */
	public List<String> getAllSessionIds()
	{
		return getAllLogs().stream().map( AppLog::getSessionId ).distinct().sorted( Comparator.reverseOrder() ) // Newest first
				.collect( Collectors.toList() );
	}
	
	/**
	 * Get log statistics.
	 */
	public Map<String, Long> getLogStatistics()
	{
		List<AppLog> allLogs = getAllLogs();
		
		Map<String, Long> stats = new HashMap<>();
		stats.put( "TOTAL", (long) allLogs.size() );
		stats.put( "INFO", allLogs.stream().filter( l -> "INFO".equals( l.getLevel() ) ).count() );
		stats.put( "WARNING", allLogs.stream().filter( l -> "WARNING".equals( l.getLevel() ) ).count() );
		stats.put( "SEVERE", allLogs.stream().filter( l -> "SEVERE".equals( l.getLevel() ) ).count() );
		stats.put( "FINE", allLogs.stream().filter( l -> "FINE".equals( l.getLevel() ) ).count() );
		
		return stats;
	}
	
	// ==================== Private Helper Methods ====================
	
	/**
	 * Get all log files from the log directory, sorted by modification time (newest first).
	 */
	private List<File> getLogFiles() throws IOException
	{
		File logDir = new File( logDirectory );
		
		if( !logDir.exists() || !logDir.isDirectory() )
		{
			return Collections.emptyList();
		}
		
		File[] files = logDir.listFiles( ( dir, name ) -> name.startsWith( "TherapyNotes-" ) && name.endsWith( ".log" ) );
		
		if( files == null || files.length == 0 )
		{
			return Collections.emptyList();
		}
		
		// Sort by last modified time, newest first
		Arrays.sort( files, Comparator.comparing( File::lastModified ).reversed() );
		
		return Arrays.asList( files );
	}
	
	/**
	 * Parse a single log file and return matching AppLog entries.
	 */
	private List<AppLog> parseLogFile( File logFile, LocalDateTime startDate, LocalDateTime endDate, String levelFilter,
			String sessionFilter ) throws IOException
	{
		List<AppLog> logs = new ArrayList<>();
		
		try( BufferedReader reader = new BufferedReader( new FileReader( logFile ) ) )
		{
			String line;
			AppLog currentLog = null;
			StringBuilder multiLineMessage = null;
			
			while( ( line = reader.readLine() ) != null )
			{
				Matcher matcher = LOG_PATTERN.matcher( line );
				
				if( matcher.matches() )
				{
					// Save previous log if exists
					if( currentLog != null && multiLineMessage != null )
					{
						currentLog.setMessage( multiLineMessage.toString().trim() );
						if( matchesFilters( currentLog, startDate, endDate, levelFilter, sessionFilter ) )
						{
							logs.add( currentLog );
						}
					}
					
					// Parse new log entry
					currentLog = parseLogLine( matcher );
					multiLineMessage = new StringBuilder( currentLog.getMessage() );
					
				}
				else if( currentLog != null && !line.trim().isEmpty() )
				{
					// This is a continuation line (e.g., stack trace)
					multiLineMessage.append( "\n" ).append( line );
				}
			}
			
			// Don't forget the last log entry
			if( currentLog != null && multiLineMessage != null )
			{
				currentLog.setMessage( multiLineMessage.toString().trim() );
				if( matchesFilters( currentLog, startDate, endDate, levelFilter, sessionFilter ) )
				{
					logs.add( currentLog );
				}
			}
		}
		
		return logs;
	}
	
	/**
	 * Parse a single log line into an AppLog object.
	 */
	private AppLog parseLogLine( Matcher matcher )
	{
		AppLog log = new AppLog();
		
		try
		{
			// Group 1: ID
			log.setId( Integer.parseInt( matcher.group( 1 ) ) );
			
			// Group 2: Timestamp
			String timestampStr = matcher.group( 2 ).trim();
			try
			{
				log.setTimestamp( LocalDateTime.parse( timestampStr, TIMESTAMP_FORMATTER ) );
			}
			catch( DateTimeParseException e )
			{
				AppLogger.warning( "Failed to parse timestamp '" + timestampStr + "': " + e.getMessage() );
			}
			
			// Group 3: Session ID
			log.setSessionId( matcher.group( 3 ).trim() );
			
			// Group 4: Level
			log.setLevel( matcher.group( 4 ).trim() );
			
			// Group 5: Source
			log.setSource( matcher.group( 5 ).trim() );
			
			// Group 6: Message
			log.setMessage( matcher.group( 6 ).trim() );
			
		}
		catch( Exception e )
		{
			AppLogger.error( "Failed to parse log line: " + matcher.group( 0 ), e );
		}
		
		return log;
	}
	
	/**
	 * Check if a log entry matches the given filters.
	 */
	private boolean matchesFilters( AppLog log, LocalDateTime startDate, LocalDateTime endDate, String levelFilter, String sessionFilter )
	{
		// Date range filter
		if( startDate != null && log.getTimestamp().isBefore( startDate ) )
		{
			return false;
		}
		if( endDate != null && log.getTimestamp().isAfter( endDate ) )
		{
			return false;
		}
		
		// Level filter
		if( levelFilter != null && !levelFilter.trim().isEmpty() )
		{
			if( !levelFilter.equalsIgnoreCase( log.getLevel() ) )
			{
				return false;
			}
		}
		
		// Session filter
		if( sessionFilter != null && !sessionFilter.trim().isEmpty() )
		{
			if( !sessionFilter.equals( log.getSessionId() ) )
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Get the total number of log entries.
	 */
	public int getLogCount()
	{
		return getAllLogs().size();
	}
	
	/**
	 * Clear old log files (older than specified days). This is useful for maintenance.
	 */
	public void clearOldLogs( int daysToKeep )
	{
		try
		{
			File logDir = new File( logDirectory );
			if( !logDir.exists() )
			{
				return;
			}
			
			long cutoffTime = System.currentTimeMillis() - ( daysToKeep * 24L * 60 * 60 * 1000 );
			
			File[] files = logDir.listFiles( ( dir, name ) -> name.startsWith( "TherapyNotes-" ) && name.endsWith( ".log" ) );
			
			if( files != null )
			{
				for( File file : files )
				{
					if( file.lastModified() < cutoffTime )
					{
						if( file.delete() )
						{
							AppLogger.info( "Deleted old log file: " + file.getName() );
						}
					}
				}
			}
		}
		catch( Exception e )
		{
			AppLogger.error( "Failed to clear old logs", e );
		}
	}
	
	/**
	 * Export logs to a text file.
	 */
	public void exportLogs( File destination, LocalDateTime startDate, LocalDateTime endDate, String level, String sessionId )
			throws IOException
	{
		List<AppLog> logs = getAllLogs( startDate, endDate, level, sessionId );
		
		try( PrintWriter writer = new PrintWriter( new FileWriter( destination ) ) )
		{
			writer.println( "Therapy Notes - Log Export" );
			writer.println( "Generated: " + LocalDateTime.now().format( TIMESTAMP_FORMATTER ) );
			writer.println( "Total Entries: " + logs.size() );
			writer.println( "=".repeat( 100 ) );
			writer.println();
			
			for( AppLog log : logs )
			{
				writer.printf( "[%05d] [%s] [%s] [%-7s] [%s] %s%n", log.getId(), log.getTimestamp().format( TIMESTAMP_FORMATTER ),
						log.getSessionId(), log.getLevel(), log.getSource(), log.getMessage() );
				writer.println();
			}
		}
		
		AppLogger.audit( "Exported " + logs.size() + " log entries to " + destination.getName() );
	}
}