package main.java.com.alexpacheco.therapynotes.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import main.java.com.alexpacheco.therapynotes.controller.enums.LogLevel;
import main.java.com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import main.java.com.alexpacheco.therapynotes.model.entities.Note;
import main.java.com.alexpacheco.therapynotes.util.DbUtil;
import main.java.com.alexpacheco.therapynotes.util.JavaUtils;
import main.java.com.alexpacheco.therapynotes.util.export.NoteDocxExporter;
import main.java.com.alexpacheco.therapynotes.util.export.NotePdfExporter;

public class Exporter
{
	private static final DateTimeFormatter FILE_DATE_FORMATTER = DateTimeFormatter.ofPattern( "yyyy-MM-dd" );
	
	/**
	 * Exports all log entries from the app_logs table to a CSV file.
	 * 
	 * @param outputFile the File object representing where to save the CSV
	 * @return true if export was successful, false otherwise
	 */
	public static boolean exportLogsToCSV( File outputFile )
	{
		String sql = "SELECT id, session_id, timestamp, level, source, message, exception_stacktrace FROM app_logs ORDER BY timestamp DESC";
		
		try( Connection conn = DbUtil.getConnection();
				PreparedStatement pstmt = conn.prepareStatement( sql );
				ResultSet rs = pstmt.executeQuery();
				FileWriter writer = new FileWriter( outputFile );
				BufferedWriter bw = new BufferedWriter( writer ) )
		{
			// Write CSV header
			bw.write( "ID,Session ID,Timestamp,Log Level,Source,Message,Exception Stacktrace" );
			bw.newLine();
			
			// Write data rows
			while( rs.next() )
			{
				StringBuilder row = new StringBuilder();
				
				row.append( rs.getInt( "id" ) ).append( "," );
				row.append( escapeCsvValue( rs.getString( "session_id" ) ) ).append( "," );
				row.append( escapeCsvValue( rs.getString( "timestamp" ) ) ).append( "," );
				row.append( escapeCsvValue( rs.getString( "level" ) ) ).append( "," );
				row.append( escapeCsvValue( rs.getString( "source" ) ) ).append( "," );
				row.append( escapeCsvValue( rs.getString( "message" ) ) ).append( "," );
				row.append( escapeCsvValue( rs.getString( "exception_stacktrace" ) ) );
				
				bw.write( row.toString() );
				bw.newLine();
			}
			
			AppController.logToDatabase( LogLevel.INFO, "AppController", "Exported logs to CSV: " + outputFile.getAbsolutePath() );
			return true;
			
		}
		catch( SQLException | IOException e )
		{
			System.err.println( "Failed to export logs to CSV: " + e.getMessage() );
			e.printStackTrace();
			
			try
			{
				AppController.logToDatabase( LogLevel.ERROR, "AppController", "Failed to export logs to CSV: " + e.getMessage() );
			}
			catch( Exception logEx )
			{
				// Ignore - can't log the logging error
			}
			
			return false;
		}
	}
	
	/**
	 * Escapes a CSV value by wrapping it in quotes if it contains commas, quotes, or newlines. Doubles any quotes inside the value.
	 * 
	 * @param value the string value to escape
	 * @return the escaped CSV value, or empty string if value is null
	 */
	private static String escapeCsvValue( String value )
	{
		if( value == null )
		{
			return "";
		}
		
		// If value contains comma, quote, or newline, wrap in quotes and escape internal quotes
		if( value.contains( "," ) || value.contains( "\"" ) || value.contains( "\n" ) || value.contains( "\r" ) )
		{
			return "\"" + value.replace( "\"", "\"\"" ) + "\"";
		}
		
		return value;
	}
	
	public static String exportToDocx( Note note ) throws TherapyAppException
	{
		File exportDir = new File( System.getProperty( "user.home" ), "ProgressNotes/Exports" );
		String filePath = NoteDocxExporter.exportToDocx( note, exportDir, _generateFilename( note, ".docx" ) );
		return filePath;
	}
	
	public static String exportToPdf( Note note ) throws TherapyAppException
	{
		File exportDir = new File( System.getProperty( "user.home" ), "ProgressNotes/Exports" );
		String filePath = NotePdfExporter.exportToPdf( note, exportDir, _generateFilename( note, ".pdf" ) );
		return filePath;
	}
	
	/**
	 * Generates a filename for the note document. Format: note_{clientCode}_{date}{extension}
	 */
	private static String _generateFilename( Note note, String extension )
	{
		StringBuilder sb = new StringBuilder( "note_" );
		
		if( note.getClient() != null && note.getClient().getClientCode() != null )
		{
			sb.append( JavaUtils.sanitizeFilename( note.getClient().getClientCode() ) );
		}
		else
		{
			sb.append( "unknown" );
		}
		
		sb.append( "_" );
		
		if( note.getApptDateTime() != null )
		{
			sb.append( note.getApptDateTime().format( FILE_DATE_FORMATTER ) );
		}
		else
		{
			sb.append( LocalDateTime.now().format( FILE_DATE_FORMATTER ) );
		}
		
		sb.append( JavaUtils.isNullOrEmpty( extension ) ? ".txt" : extension );
		
		return sb.toString();
	}
	
	public static void exportToDocx( Map<Note, String> notes ) throws TherapyAppException
	{
		for( Note note : notes.keySet() )
		{
			String outputPath = notes.get( note );
			NoteDocxExporter.exportToDocx( note, outputPath );
		}
	}
	
	public static void exportToPdf( Map<Note, String> notes ) throws TherapyAppException
	{
		for( Note note : notes.keySet() )
		{
			String outputPath = notes.get( note );
			NotePdfExporter.exportToPdf( note, outputPath );
		}
	}
}
