package com.alexpacheco.therapynotes.model.api;

/*
 * Business Logic Layer (Services)
Avoid putting logic inside your DAOs or Controllers. Create a NoteApi.

Validation: Check if a note is "Certified" before allowing an update. (Once certified, clinical notes are usually locked).

Formatting: Convert database timestamps into user-friendly formats (e.g., "Jan 6, 2026").

Coordination: If saving a note should also trigger an email notification or update an appointment status, the Service handles that orchestration.
 */
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.alexpacheco.therapynotes.controller.AppController;
import com.alexpacheco.therapynotes.controller.enums.ErrorCode;
import com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.EntityValidator;
import com.alexpacheco.therapynotes.model.dao.NotesDao;
import com.alexpacheco.therapynotes.model.entities.Note;

public class NoteApi
{
	private final NotesDao notesDao = new NotesDao();
	
	public void createNewNote(Note note) throws TherapyAppException
	{
		EntityValidator.validateNote(note);
		
		try
		{
			notesDao.createNewNote(note);
		}
		catch (SQLException e)
		{
			AppController.logException("NoteApi", e);
			throw new TherapyAppException("An internal database error occurred.", ErrorCode.DB_ERROR);
		}
	}
	
	public void updateExistingNote(Note note) throws TherapyAppException
	{
		EntityValidator.validateNote(note);
		
		try
		{
			notesDao.updateExistingNote(note);
		}
		catch (SQLException e)
		{
			AppController.logException("NoteApi", e);
			throw new TherapyAppException("An internal database error occurred.", ErrorCode.DB_ERROR);
		}
	}
	
	public Note getNote(int noteId) throws TherapyAppException
	{
		try
		{
			return notesDao.getNote(noteId);
		}
		catch (SQLException e)
		{
			AppController.logException("NoteApi", e);
			throw new TherapyAppException("Error retrieving note details.", ErrorCode.DB_ERROR);
		}
	}
	
	public List<Note> searchNotes(Integer clientId, Date startDate, Date endDate) throws TherapyAppException
	{
		try
		{
			return notesDao.searchNotes(clientId, startDate, endDate);
		}
		catch (SQLException e)
		{
			AppController.logException("NoteApi", e);
			throw new TherapyAppException("An internal database error occurred.", ErrorCode.DB_ERROR);
		}
	}
	
	public Integer getHighestUsedSessionNumberForClient(Integer clientId) throws TherapyAppException
	{
		try
		{
			return notesDao.getHighestUsedSessionNumberForClient(clientId);
		}
		catch (SQLException e)
		{
			AppController.logException("NoteApi", e);
			throw new TherapyAppException("An internal database error occurred.", ErrorCode.DB_ERROR);
		}
	}
	
	public String getLastUsedDiagnosisForClient(Integer clientId) throws TherapyAppException
	{
		try
		{
			return notesDao.getLastUsedDiagnosisForClient(clientId);
		}
		catch (SQLException e)
		{
			AppController.logException("NoteApi", e);
			throw new TherapyAppException("An internal database error occurred.", ErrorCode.DB_ERROR);
		}
	}
}
