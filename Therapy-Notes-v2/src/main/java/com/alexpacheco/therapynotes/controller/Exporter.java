package com.alexpacheco.therapynotes.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.alexpacheco.therapynotes.controller.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.api.AppLogApi;
import com.alexpacheco.therapynotes.model.entities.AppLog;
import com.alexpacheco.therapynotes.model.entities.Note;
import com.alexpacheco.therapynotes.util.AppLogger;
import com.alexpacheco.therapynotes.util.DateFormatUtil;
import com.alexpacheco.therapynotes.util.JavaUtils;
import com.alexpacheco.therapynotes.util.export.NoteDocxExporter;
import com.alexpacheco.therapynotes.util.export.NotePdfExporter;

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
		AppLogApi api = new AppLogApi();
		List<AppLog> appLogs = api.getRecentLogs( 1000 );
		try( FileWriter writer = new FileWriter( outputFile ); BufferedWriter bw = new BufferedWriter( writer ) )
		{
			// Write CSV header
			bw.write( "ID,Session ID,Timestamp,Log Level,Source,Message,Exception Stacktrace" );
			bw.newLine();
			
			// Write data rows
			for( AppLog log : appLogs )
			{
				StringBuilder row = new StringBuilder();
				
				row.append( log.getId() ).append( "," );
				row.append( escapeCsvValue( log.getSessionId() ) ).append( "," );
				row.append( escapeCsvValue( DateFormatUtil.toSqliteString( log.getTimestamp() ) ) ).append( "," );
				row.append( escapeCsvValue( log.getLevel() ) ).append( "," );
				row.append( escapeCsvValue( log.getSource() ) ).append( "," );
				row.append( escapeCsvValue( log.getMessage() ) ).append( "," );
				row.append( escapeCsvValue( "" ) );
				
				bw.write( row.toString() );
				bw.newLine();
			}
			
			AppLogger.info( "Exported logs to CSV: " + outputFile.getAbsolutePath() );
			return true;
			
		}
		catch( IOException e )
		{
			AppLogger.error( "Failed to export logs to CSV: " + e.getMessage(), e );
			
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
