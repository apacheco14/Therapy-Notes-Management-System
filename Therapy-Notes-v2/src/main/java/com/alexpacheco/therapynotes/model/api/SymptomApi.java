package com.alexpacheco.therapynotes.model.api;

import java.sql.SQLException;
import java.util.List;

import com.alexpacheco.therapynotes.controller.AppController;
import com.alexpacheco.therapynotes.controller.enums.ErrorCode;
import com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.dao.SymptomsDao;
import com.alexpacheco.therapynotes.model.entities.Symptom;

public class SymptomApi
{
	private final SymptomsDao symptomDao = new SymptomsDao();
	
	public List<Symptom> getSelectedSymptomsForNote(Integer noteId) throws TherapyAppException
	{
		try
		{
			return symptomDao.getSelectedSymptomsForNote(noteId);
		}
		catch (SQLException e)
		{
			AppController.logException("SymptomApi", e);
			throw new TherapyAppException("Error occurred while retrieving symptoms for note.", ErrorCode.DB_ERROR);
		}
	}
}
