package com.alexpacheco.therapynotes.model.entities;

import java.time.LocalDateTime;

public class Referral
{
	private int noteId;
	private int referralTypeId;
	private String referralName;
	private String referralDescription;
	private LocalDateTime insertDate;
	
	public int getNoteId()
	{
		return noteId;
	}
	
	public void setNoteId(int noteId)
	{
		this.noteId = noteId;
	}
	
	public int getReferralTypeId()
	{
		return referralTypeId;
	}
	
	public void setReferralTypeId(int referralTypeId)
	{
		this.referralTypeId = referralTypeId;
	}
	
	public String getReferralName()
	{
		return referralName;
	}
	
	public void setReferralName(String referralName)
	{
		this.referralName = referralName;
	}
	
	public String getReferralDescription()
	{
		return referralDescription;
	}
	
	public void setReferralDescription(String referralDescription)
	{
		this.referralDescription = referralDescription;
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
