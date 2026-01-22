package com.alexpacheco.therapynotes.model.api;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import com.alexpacheco.therapynotes.controller.enums.LogLevel;
import com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.dao.AppLogsDao;
import com.alexpacheco.therapynotes.model.entities.AppLog;

public class AppLogApi
{
	private final AppLogsDao appLogsDao = new AppLogsDao();
	
	public List<AppLog> getRecentLogs( int limit )
	{
		return appLogsDao.getRecentLogs( limit );
	}
	
	public List<AppLog> getLogs( Date startDate, Date endDate, LogLevel logLevel, int maxResults ) throws TherapyAppException
	{
		LocalDateTime startDateTime = null;
		if( startDate != null )
		{
			startDateTime = LocalDateTime.ofInstant( startDate.toInstant(), ZoneId.systemDefault() );
		}
		
		LocalDateTime endDateTime = null;
		if( endDate != null )
		{
			endDateTime = LocalDateTime.ofInstant( endDate.toInstant(), ZoneId.systemDefault() );
		}
		
		String logLevelCode = logLevel == null ? null : logLevel.getDbCode();
		
		List<AppLog> logs = appLogsDao.getAllLogs( startDateTime, endDateTime, logLevelCode, null );
		
		if( logs.size() > maxResults )
		{
			logs.subList( maxResults, logs.size() ).clear();
		}
		
		return logs;
	}
	
}
