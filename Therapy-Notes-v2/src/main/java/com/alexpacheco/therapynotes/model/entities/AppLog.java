package com.alexpacheco.therapynotes.model.entities;

import java.time.LocalDateTime;

/**
 * Entity class representing an application log entry from the app_logs table.
 */
public class AppLog implements Comparable<AppLog>
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
	
	public void setId( Integer id )
	{
		this.id = id;
	}
	
	public String getSessionId()
	{
		return sessionId;
	}
	
	public void setSessionId( String sessionId )
	{
		this.sessionId = sessionId;
	}
	
	public String getLevel()
	{
		return level;
	}
	
	public void setLevel( String level )
	{
		this.level = level;
	}
	
	public String getSource()
	{
		return source;
	}
	
	public void setSource( String source )
	{
		this.source = source;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public void setMessage( String message )
	{
		this.message = message;
	}
	
	public LocalDateTime getTimestamp()
	{
		return timestamp;
	}
	
	public void setTimestamp( LocalDateTime timestamp )
	{
		this.timestamp = timestamp;
	}
	
	/**
	 * Compare logs primarily by timestamp, then by ID. Natural ordering is chronological (oldest first).
	 * 
	 * @param other the AppLog to compare to
	 * @return negative if this log is earlier, positive if later, 0 if equal
	 */
	@Override
	public int compareTo( AppLog other )
	{
		if( other == null )
		{
			return 1;
		}
		
		// Primary sort: by timestamp
		if( this.timestamp == null && other.timestamp == null )
		{
			// Both null, fall through to ID comparison
		}
		else if( this.timestamp == null )
		{
			return 1; // Null timestamps sort last
		}
		else if( other.timestamp == null )
		{
			return -1;
		}
		else
		{
			int timestampComparison = this.timestamp.compareTo( other.timestamp );
			if( timestampComparison != 0 )
			{
				return timestampComparison;
			}
		}
		
		// Secondary sort: by ID (if timestamps are equal or both null)
		if( this.id == null && other.id == null )
		{
			return 0;
		}
		else if( this.id == null )
		{
			return 1; // Null IDs sort last
		}
		else if( other.id == null )
		{
			return -1;
		}
		else
		{
			return this.id.compareTo( other.id );
		}
	}
	
	@Override
	public String toString()
	{
		return "AppLog [id=" + id + ", level=" + level + ", source=" + source + ", message=" + message + "]";
	}
}