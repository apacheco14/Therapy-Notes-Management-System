package com.alexpacheco.therapynotes.model.api;

import java.sql.SQLException;
import java.util.List;

import com.alexpacheco.therapynotes.controller.AppController;
import com.alexpacheco.therapynotes.controller.enums.ErrorCode;
import com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.EntityValidator;
import com.alexpacheco.therapynotes.model.dao.AssessmentOptionsDao;
import com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOption;
import com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOptionType;

public class AssessmentOptionApi
{
	private AssessmentOptionsDao dao = new AssessmentOptionsDao();
	
	/**
	 * Creates a new AssessmentOption
	 */
	public void createAssessmentOptions(List<AssessmentOption> options) throws TherapyAppException
	{
		EntityValidator.validateAssessmentOptions(options);
		
		try
		{
			if (options.size() == 1)
				dao.createOption(options.get(0));
			else
				dao.createOptionsBatch(options);
		}
		catch (SQLException e)
		{
			AppController.logException("AssessmentOptionApi", e);
			throw new TherapyAppException("An internal database error occurred.", ErrorCode.DB_ERROR);
		}
	}
	
	/**
	 * Edits existing AssessmentOption objects.
	 */
	public void editAssessmentOptions(List<AssessmentOption> options) throws TherapyAppException
	{
		EntityValidator.validateAssessmentOptions(options);
		try
		{
			if (options.size() == 1)
				dao.updateOption(options.get(0));
			else
				dao.updateOptionsBatch(options);
		}
		catch (SQLException e)
		{
			AppController.logException("AssessmentOptionApi", e);
			throw new TherapyAppException("An internal database error occurred.", ErrorCode.DB_ERROR);
		}
	}
	
	public List<AssessmentOption> getOptions(AssessmentOptionType type) throws TherapyAppException
	{
		try
		{
			if(type == null)
				return dao.getOptions();
			else
				return dao.getOptions(type);
		}
		catch (SQLException e)
		{
			AppController.logException("AssessmentOptionApi", e);
			throw new TherapyAppException("An internal database error occurred.", ErrorCode.DB_ERROR);
		}
	}

	public AssessmentOption getOption(Integer assessmentOptionId) throws TherapyAppException
	{
		try
		{
			if(assessmentOptionId == null)
				return null;
			else
				return dao.getOption(assessmentOptionId);
		}
		catch (SQLException e)
		{
			AppController.logException("AssessmentOptionApi", e);
			throw new TherapyAppException("An internal database error occurred.", ErrorCode.DB_ERROR);
		}
	}
}
