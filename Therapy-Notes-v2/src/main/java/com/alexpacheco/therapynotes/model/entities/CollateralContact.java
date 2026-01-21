package main.java.com.alexpacheco.therapynotes.model.entities;

import java.time.LocalDateTime;

public class CollateralContact
{
	private int noteId;
	private int collateralContactTypeId;
	private String collateralContactName;
	private String collateralContactDescription;
	private LocalDateTime insertDate;
	
	public int getNoteId()
	{
		return noteId;
	}
	
	public void setNoteId(int noteId)
	{
		this.noteId = noteId;
	}
	
	public int getCollateralContactTypeId()
	{
		return collateralContactTypeId;
	}
	
	public void setCollateralContactTypeId(int collateralContactTypeId)
	{
		this.collateralContactTypeId = collateralContactTypeId;
	}
	
	public String getCollateralContactName()
	{
		return collateralContactName;
	}
	
	public void setCollateralContactName(String collateralContactName)
	{
		this.collateralContactName = collateralContactName;
	}
	
	public String getCollateralContactDescription()
	{
		return collateralContactDescription;
	}
	
	public void setCollateralContactDescription(String collateralContactDescription)
	{
		this.collateralContactDescription = collateralContactDescription;
	}
	
	public LocalDateTime getInsertDate()
	{
		return insertDate;
	}
	
	public void setInsertDate(LocalDateTime insertDate)
	{
		this.insertDate = insertDate;
	}
}
