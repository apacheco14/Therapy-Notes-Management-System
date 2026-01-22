package com.alexpacheco.therapynotes.model.api;

import java.sql.SQLException;
import java.util.List;

import com.alexpacheco.therapynotes.controller.enums.ErrorCode;
import com.alexpacheco.therapynotes.controller.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.dao.SymptomsDao;
import com.alexpacheco.therapynotes.model.entities.Symptom;
import com.alexpacheco.therapynotes.util.AppLogger;

public class SymptomApi
{
	private final SymptomsDao symptomDao = new SymptomsDao();
	
	public List<Symptom> getSelectedSymptomsForNote( Integer noteId ) throws TherapyAppException
	{
		try
		{
			return symptomDao.getSelectedSymptomsForNote( noteId );
		}
		catch( SQLException e )
		{
			AppLogger.error( e );
			throw new TherapyAppException( "Error occurred while retrieving symptoms for note.", ErrorCode.DB_ERROR );
		}
	}
}
