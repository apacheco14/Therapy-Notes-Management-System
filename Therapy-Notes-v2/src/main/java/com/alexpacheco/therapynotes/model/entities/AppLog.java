package com.alexpacheco.therapynotes.model.entities;

import java.time.LocalDateTime;

/**
 * Entity class representing an application log entry from the app_logs table.
 */
public class AppLog
{
	private Integer id;
	private String sessionId;
	private String level;
	private String source;
	private String message;
	private LocalDateTime timestamp;
	
	public AppLog()
	{
	}
	
	public Integer getId()
	{
		return id;
	}
	
	public void setId(Integer id)
	{
		this.id = id;
	}
	
	public String getSessionId()
	{
		return sessionId;
	}
	
	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}
	
	public String getLevel()
	{
		return level;
	}
	
	public void setLevel(String level)
	{
		this.level = level;
	}
	
	public String getSource()
	{
		return source;
	}
	
	public void setSource(String source)
	{
		this.source = source;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	public LocalDateTime getTimestamp()
	{
		return timestamp;
	}
	
	public void setTimestamp(LocalDateTime timestamp)
	{
		this.timestamp = timestamp;
	}
	
	@Override
	public String toString()
	{
		return "AppLog [id=" + id + ", level=" + level + ", source=" + source + ", message=" + message + "]";
	}
}