package com.alexpacheco.therapynotes.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.logging.*;

import com.alexpacheco.therapynotes.model.dao.AppLogsDao;
import com.alexpacheco.therapynotes.model.entities.AppLog;

/**
 * Centralized application logging utility. Logs to rotating files in AppData/Roaming/TherapyNotes/logs directory.
 */
public class AppLogger
{
	private static final Logger logger = Logger.getLogger( "TherapyNotes" );
	private static final String APP_NAME = "TherapyNotes";
	private static final String LOG_DIR_NAME = "logs";
	
	// Log file configuration
	private static final int MAX_LOG_FILE_SIZE = 5 * 1024 * 1024; // 5MB per file
	private static final int MAX_LOG_FILES = 5; // Keep 5 rotated files
	
	private static boolean initialized = false;
	private static String currentSessionId;
	private static long logSequence = 0; // Simulates DB auto-increment ID
	
	static
	{
		initializeLogger();
	}
	
	/**
	 * Initialize the logging system. Creates log directory and sets up file handlers with rotation.
	 */
	private static void initializeLogger()
	{
		try
		{
			// Generate unique session ID
			currentSessionId = generateSessionId();
			
			// Get log directory path
			String logDirPath = getLogDirectory();
			File logDir = new File( logDirPath );
			
			// Create logs directory if it doesn't exist
			if( !logDir.exists() )
			{
				logDir.mkdirs();
			}
			
			// Setup file handler with rotation
			String logFilePattern = logDirPath + File.separator + APP_NAME + "-%g.log";
			FileHandler fileHandler = new FileHandler( logFilePattern, MAX_LOG_FILE_SIZE, MAX_LOG_FILES, true // append mode
			);
			
			// Set custom formatter
			fileHandler.setFormatter( new DetailedLogFormatter() );
			
			// Configure logger
			logger.addHandler( fileHandler );
			logger.setLevel( Level.INFO );
			logger.setUseParentHandlers( false ); // Don't log to console
			
			initialized = true;
			
			initializeLogSequence();
			
			// Log successful initialization
			log( Level.INFO, "Application logging initialized - Session: " + currentSessionId );
		}
		catch( IOException e )
		{
			System.err.println( "Failed to initialize file logging: " + e.getMessage() );
			e.printStackTrace();
			// Fallback to console logging
			setupConsoleLogging();
		}
	}
	
	private static void initializeLogSequence()
	{
		try
		{
			AppLogsDao dao = new AppLogsDao();
			List<AppLog> recentLogs = dao.getRecentLogs( 1 );
			if( !recentLogs.isEmpty() )
			{
				logSequence = recentLogs.get( 0 ).getId();
			}
		}
		catch( Exception e )
		{
			// Fall back to 0 if we can't read logs
			logSequence = 0;
		}
	}
	
	/**
	 * Fallback to console logging if file logging fails.
	 */
	private static void setupConsoleLogging()
	{
		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setFormatter( new DetailedLogFormatter() );
		logger.addHandler( consoleHandler );
		logger.setLevel( Level.INFO );
		initialized = true;
	}
	
	/**
	 * Get the log directory path based on OS.
	 */
	private static String getLogDirectory()
	{
		String baseDir;
		String os = System.getProperty( "os.name" ).toLowerCase();
		
		if( os.contains( "win" ) )
		{
			// Windows: Use AppData/Roaming
			baseDir = System.getenv( "APPDATA" );
		}
		else if( os.contains( "mac" ) )
		{
			// macOS: Use Application Support
			baseDir = System.getProperty( "user.home" ) + "/Library/Application Support";
		}
		else
		{
			// Linux/Unix: Use .config
			baseDir = System.getProperty( "user.home" ) + "/.config";
		}
		
		return baseDir + File.separator + APP_NAME + File.separator + LOG_DIR_NAME;
	}
	
	/**
	 * Generate unique session ID for this application run.
	 */
	private static String generateSessionId()
	{
		if( JavaUtils.isNullOrEmpty( currentSessionId ) )
			currentSessionId = UUID.randomUUID().toString();
		return currentSessionId;
	}
	
	/**
	 * Core logging method with configurable stack depth for accurate source detection.
	 * 
	 * @param level      The log level
	 * @param message    The log message
	 * @param stackDepth The stack frame index to get the caller from
	 */
	private static void log( Level level, String message, int stackDepth )
	{
		if( !initialized )
			return;
		
		StackTraceElement caller = Thread.currentThread().getStackTrace()[stackDepth];
		
		LogRecord record = new LogRecord( level, message );
		record.setSourceClassName( caller.getClassName() );
		record.setSourceMethodName( caller.getMethodName() );
		record.setLoggerName( logger.getName() );
		
		logger.log( record );
	}
	
	/**
	 * Core logging method that captures source class/method automatically. Use this from info/warning/error etc. For convenience methods,
	 * use log with explicit depth.
	 */
	private static void log( Level level, String message )
	{
		// Depth 4: getStackTrace -> log(3) -> log(2) -> info/warning/error -> actual caller
		log( level, message, 4 );
	}
	
	/**
	 * Log with explicit source information (for special cases).
	 */
	@SuppressWarnings( "unused" )
	private static void logWithSource( Level level, String source, String message )
	{
		if( !initialized )
			return;
		
		LogRecord record = new LogRecord( level, message );
		record.setSourceClassName( source );
		record.setSourceMethodName( "" );
		record.setLoggerName( logger.getName() );
		
		logger.log( record );
	}
	
	// ==================== Public Logging Methods ====================
	
	/**
	 * Log informational message.
	 */
	public static void info( String message )
	{
		log( Level.INFO, message );
	}
	
	/**
	 * Log warning message.
	 */
	public static void warning( String message )
	{
		log( Level.WARNING, message );
	}
	
	/**
	 * Log error message.
	 */
	public static void error( String message )
	{
		log( Level.SEVERE, message );
	}
	
	/**
	 * Log exception.
	 */
	public static void error( Throwable throwable )
	{
		String fullMessage = throwable.getMessage() + "\n" + getStackTrace( throwable );
		log( Level.SEVERE, fullMessage );
	}
	
	/**
	 * Log error message with exception details.
	 */
	public static void error( String message, Throwable throwable )
	{
		String fullMessage = message + "\n" + getStackTrace( throwable );
		log( Level.SEVERE, fullMessage );
	}
	
	/**
	 * Log audit trail event (high-importance actions).
	 */
	public static void audit( String message )
	{
		// Audit logs get special prefix
		log( Level.INFO, "[AUDIT] " + message );
	}
	
	/**
	 * Log debug message (only logged if debug level is enabled).
	 */
	public static void debug( String message )
	{
		if( initialized && logger.isLoggable( Level.FINE ) )
		{
			log( Level.FINE, message );
		}
	}
	
	// ==================== Application Lifecycle Logging ====================
	
	/**
	 * Log application startup.
	 */
	public static void logStartup()
	{
		// Depth 3: getStackTrace -> log(3) -> logStartup -> actual caller
		log( Level.INFO, "Application Started", 3 );
		log( Level.INFO, "Java Version: " + System.getProperty( "java.version" ), 3 );
		log( Level.INFO, "OS: " + System.getProperty( "os.name" ) + " " + System.getProperty( "os.version" ), 3 );
		log( Level.INFO, "User: " + System.getProperty( "user.name" ), 3 );
	}
	
	/**
	 * Log application shutdown.
	 */
	public static void logShutdown()
	{
		log( Level.INFO, "Application Shutdown - Session: " + currentSessionId, 3 );
	}
	
	/**
	 * Log user login attempt.
	 */
	public static void logLogin( boolean success )
	{
		if( success )
		{
			log( Level.INFO, "[AUDIT] User authenticated successfully", 3 );
		}
		else
		{
			log( Level.WARNING, "Failed authentication attempt", 3 );
		}
	}
	
	/**
	 * Log user logout.
	 */
	public static void logLogout()
	{
		log( Level.INFO, "[AUDIT] User logged out", 3 );
	}
	
	// ==================== Database Operation Logging ====================
	
	/**
	 * Log database operation.
	 */
	public static void logDatabaseOperation( String operation, String table, boolean success )
	{
		if( success )
		{
			log( Level.INFO, String.format( "Database %s on %s - SUCCESS", operation, table ), 3 );
		}
		else
		{
			log( Level.WARNING, String.format( "Database %s on %s - FAILED", operation, table ), 3 );
		}
	}
	
	/**
	 * Log database error.
	 */
	public static void logDatabaseError( String operation, String table, Exception e )
	{
		log( Level.SEVERE, String.format( "Database %s on %s - ERROR: %s", operation, table, e.getMessage() ) + "\n" + getStackTrace( e ),
				3 );
	}
	
	// ==================== UI Navigation Logging ====================
	
	/**
	 * Log panel/view navigation.
	 */
	public static void logNavigation( String fromPanel, String toPanel )
	{
		log( Level.INFO, String.format( "Navigation: %s -> %s", fromPanel, toPanel ), 3 );
	}
	
	/**
	 * Log dialog opened.
	 */
	public static void logDialogOpened( String dialogName )
	{
		if( initialized && logger.isLoggable( Level.FINE ) )
		{
			log( Level.FINE, "Dialog opened: " + dialogName, 3 );
		}
	}
	
	// ==================== Export/Document Logging ====================
	
	/**
	 * Log document export.
	 */
	public static void logExport( String documentType, String destination, boolean success )
	{
		if( success )
		{
			log( Level.INFO, "[AUDIT] " + String.format( "Exported %s to %s", documentType, destination ), 3 );
		}
		else
		{
			log( Level.WARNING, String.format( "Failed to export %s to %s", documentType, destination ), 3 );
		}
	}
	
	/**
	 * Log bulk export operation.
	 */
	public static void logBulkExport( int count, String format, boolean success )
	{
		if( success )
		{
			log( Level.INFO, "[AUDIT] " + String.format( "Bulk export completed: %d documents as %s", count, format ), 3 );
		}
		else
		{
			log( Level.SEVERE, String.format( "Bulk export failed: %d documents as %s", count, format ), 3 );
		}
	}
	
	// ==================== Configuration Logging ====================
	
	/**
	 * Log configuration change.
	 */
	public static void logConfigChange( String setting, String oldValue, String newValue )
	{
		log( Level.INFO, "[AUDIT] " + String.format( "Config changed: %s from '%s' to '%s'", setting, oldValue, newValue ), 3 );
	}
	
	/**
	 * Log preference change.
	 */
	public static void logPreferenceChange( String preference, Object newValue )
	{
		log( Level.INFO, String.format( "Preference updated: %s = %s", preference, newValue ), 3 );
	}
	
	// ==================== Utility Methods ====================
	
	/**
	 * Get stack trace as string.
	 */
	private static String getStackTrace( Throwable throwable )
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter( sw );
		throwable.printStackTrace( pw );
		return sw.toString();
	}
	
	/**
	 * Enable debug logging (for development/troubleshooting).
	 */
	public static void enableDebugLogging()
	{
		logger.setLevel( Level.FINE );
		info( "Debug logging enabled" );
	}
	
	/**
	 * Disable debug logging.
	 */
	public static void disableDebugLogging()
	{
		info( "Debug logging disabled" );
		logger.setLevel( Level.INFO );
	}
	
	/**
	 * Get the current log directory path.
	 */
	public static String getLogDirectoryPath()
	{
		return getLogDirectory();
	}
	
	/**
	 * Get the current session ID.
	 */
	public static String getSessionId()
	{
		return currentSessionId;
	}
	
	// ==================== Custom Log Formatter ====================
	
	/**
	 * Custom formatter that matches DB table structure: [ID] [TIMESTAMP] [SESSION] [LEVEL] [SOURCE] MESSAGE
	 * 
	 * Example output: [00001] [2025-01-21 14:30:45.000] [50ece470-2288-43b2-bdeb-b1ce56a69252] [INFO] [ClientsDao.saveClient] Client saved
	 * successfully
	 */
	private static class DetailedLogFormatter extends Formatter
	{
		
		private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss.SSS" );
		
		@Override
		public String format( LogRecord record )
		{
			StringBuilder sb = new StringBuilder();
			
			// ID (sequential counter - simulates DB auto-increment)
			synchronized( AppLogger.class )
			{
				sb.append( String.format( "[%05d] ", ++logSequence ) );
			}
			
			// Timestamp
			LocalDateTime timestamp = LocalDateTime.ofInstant( record.getInstant(), java.time.ZoneId.systemDefault() );
			sb.append( "[" ).append( timestamp.format( dateFormatter ) ).append( "] " );
			
			// Session ID
			sb.append( "[" ).append( currentSessionId ).append( "] " );
			
			// Level
			sb.append( "[" ).append( String.format( "%-7s", record.getLevel().getName() ) ).append( "] " );
			
			// Source (Class.method)
			String source = getSource( record );
			sb.append( "[" ).append( String.format( "%-40s", source ) ).append( "] " );
			
			// Message
			sb.append( formatMessage( record ) );
			sb.append( System.lineSeparator() );
			
			return sb.toString();
		}
		
		/**
		 * Extract source information from LogRecord.
		 */
		private String getSource( LogRecord record )
		{
			String className = record.getSourceClassName();
			String methodName = record.getSourceMethodName();
			
			if( className == null )
			{
				return "Unknown";
			}
			
			// Get simple class name (without package)
			String simpleClassName = className;
			int lastDot = className.lastIndexOf( '.' );
			if( lastDot >= 0 && lastDot < className.length() - 1 )
			{
				simpleClassName = className.substring( lastDot + 1 );
			}
			
			if( methodName != null && !methodName.isEmpty() )
			{
				return simpleClassName + "." + methodName;
			}
			else
			{
				return simpleClassName;
			}
		}
	}
}