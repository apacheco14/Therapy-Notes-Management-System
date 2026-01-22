package com.alexpacheco.therapynotes.model.api;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.alexpacheco.therapynotes.controller.enums.ErrorCode;
import com.alexpacheco.therapynotes.controller.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.EntityValidator;
import com.alexpacheco.therapynotes.model.dao.NotesDao;
import com.alexpacheco.therapynotes.model.entities.Note;
import com.alexpacheco.therapynotes.util.AppLogger;

public class NoteApi
{
	private final NotesDao notesDao = new NotesDao();
	
	public void createNewNote( Note note ) throws TherapyAppException
	{
		EntityValidator.validateNote( note );
		
		try
		{
			notesDao.createNewNote( note );
		}
		catch( SQLException e )
		{
			AppLogger.error( e );
			throw new TherapyAppException( "An internal database error occurred.", ErrorCode.DB_ERROR );
		}
	}
	
	public void updateExistingNote( Note note ) throws TherapyAppException
	{
		EntityValidator.validateNote( note );
		
		try
		{
			notesDao.updateExistingNote( note );
		}
		catch( SQLException e )
		{
			AppLogger.error( e );
			throw new TherapyAppException( "An internal database error occurred.", ErrorCode.DB_ERROR );
		}
	}
	
	public Note getNote( int noteId ) throws TherapyAppException
	{
		try
		{
			return notesDao.getNote( noteId );
		}
		catch( SQLException e )
		{
			AppLogger.error( e );
			throw new TherapyAppException( "Error retrieving note details.", ErrorCode.DB_ERROR );
		}
	}
	
	public List<Note> searchNotes( Integer clientId, Date startDate, Date endDate ) throws TherapyAppException
	{
		try
		{
			return notesDao.searchNotes( clientId, startDate, endDate );
		}
		catch( SQLException e )
		{
			AppLogger.error( e );
			throw new TherapyAppException( "An internal database error occurred.", ErrorCode.DB_ERROR );
		}
	}
	
	public Integer getHighestUsedSessionNumberForClient( Integer clientId ) throws TherapyAppException
	{
		try
		{
			return notesDao.getHighestUsedSessionNumberForClient( clientId );
		}
		catch( SQLException e )
		{
			AppLogger.error( e );
			throw new TherapyAppException( "An internal database error occurred.", ErrorCode.DB_ERROR );
		}
	}
	
	public String getLastUsedDiagnosisForClient( Integer clientId ) throws TherapyAppException
	{
		try
		{
			return notesDao.getLastUsedDiagnosisForClient( clientId );
		}
		catch( SQLException e )
		{
			AppLogger.error( e );
			throw new TherapyAppException( "An internal database error occurred.", ErrorCode.DB_ERROR );
		}
	}
}
