package com.alexpacheco.therapynotes.model.api;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.alexpacheco.therapynotes.controller.AppController;
import com.alexpacheco.therapynotes.controller.enums.ErrorCode;
import com.alexpacheco.therapynotes.controller.enums.LogLevel;
import com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.dao.AppLogsDao;
import com.alexpacheco.therapynotes.model.entities.AppLog;

public class AppLogApi
{
	private final AppLogsDao appLogsDao = new AppLogsDao();

	public List<AppLog> getLogs(Date startDate, Date endDate, LogLevel logLevel, int maxResults) throws TherapyAppException
	{
		try
		{
			return appLogsDao.getLogs(startDate, endDate, logLevel, maxResults);
		}
		catch (SQLException | TherapyAppException e)
		{
			AppController.logException("AppLogApi", e);
			throw new TherapyAppException("Error retrieving logs from database", ErrorCode.DB_ERROR);
		}
	}
	
}
