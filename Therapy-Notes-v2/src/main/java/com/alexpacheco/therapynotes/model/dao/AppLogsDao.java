package main.java.com.alexpacheco.therapynotes.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import main.java.com.alexpacheco.therapynotes.controller.enums.LogLevel;
import main.java.com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import main.java.com.alexpacheco.therapynotes.model.entities.AppLog;
import main.java.com.alexpacheco.therapynotes.util.DateFormatUtil;
import main.java.com.alexpacheco.therapynotes.util.DbUtil;

public class AppLogsDao
{
	public List<AppLog> getLogs(Date startDate, Date endDate, LogLevel level, int maxResults) throws SQLException, TherapyAppException
	{
		List<AppLog> logs = new ArrayList<>();
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT id, session_id, level, source, message, timestamp ");
		sql.append("FROM app_logs WHERE 1=1 ");
		
		List<Object> params = new ArrayList<>();
		
		if (startDate != null)
		{
			sql.append("AND timestamp >= ? ");
			params.add(DateFormatUtil.toSqliteString(startDate));
		}
		
		if (endDate != null)
		{
			sql.append("AND timestamp <= ? ");
			params.add(DateFormatUtil.toSqliteString(endDate));
		}
		
		if (level != null)
		{
			sql.append("AND level = ? ");
			params.add(level.getDbCode());
		}
		
		sql.append("ORDER BY timestamp DESC ");
		sql.append("LIMIT ?");
		params.add(maxResults);
		
		try (Connection conn = DbUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql.toString()))
		{
			for (int i = 0; i < params.size(); i++)
			{
				stmt.setObject(i + 1, params.get(i));
			}
			
			try (ResultSet rs = stmt.executeQuery())
			{
				while (rs.next())
				{
					AppLog log = new AppLog();
					log.setId(rs.getInt("id"));
					log.setSessionId(rs.getString("session_id"));
					log.setLevel(rs.getString("level"));
					log.setSource(rs.getString("source"));
					log.setMessage(rs.getString("message"));
					
					String timestampStr = rs.getString("timestamp");
					if (timestampStr != null)
					{
						log.setTimestamp(DateFormatUtil.toLocalDateTime(timestampStr));
					}
					
					logs.add(log);
				}
			}
		}
		
		return logs;
	}
}
