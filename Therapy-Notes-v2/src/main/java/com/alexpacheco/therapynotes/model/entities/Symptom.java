package main.java.com.alexpacheco.therapynotes.model.entities;

import java.time.LocalDateTime;

public class Symptom
{
	private int noteId;
	private int symptomId;
	private String symptomName;
	private String symptomDescription;
	private LocalDateTime insertDate;
	
	public int getNoteId()
	{
		return noteId;
	}
	
	public void setNoteId(int noteId)
	{
		this.noteId = noteId;
	}
	
	public int getSymptomId()
	{
		return symptomId;
	}
	
	public void setSymptomId(int symptomId)
	{
		this.symptomId = symptomId;
	}
	
	public String getSymptomName()
	{
		return symptomName;
	}
	
	public void setSymptomName(String symptomName)
	{
		this.symptomName = symptomName;
	}
	
	public String getSymptomDescription()
	{
		return symptomDescription;
	}
	
	public void setSymptomDescription(String symptomDescription)
	{
		this.symptomDescription = symptomDescription;
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