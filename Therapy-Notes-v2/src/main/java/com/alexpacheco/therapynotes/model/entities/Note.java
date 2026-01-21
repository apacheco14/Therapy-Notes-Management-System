package main.java.com.alexpacheco.therapynotes.model.entities;

import java.time.LocalDateTime;
import java.util.List;

import main.java.com.alexpacheco.therapynotes.model.entities.assessmentoptions.AffectAssessmentOption;
import main.java.com.alexpacheco.therapynotes.model.entities.assessmentoptions.AppearanceAssessmentOption;
import main.java.com.alexpacheco.therapynotes.model.entities.assessmentoptions.EyeContactAssessmentOption;
import main.java.com.alexpacheco.therapynotes.model.entities.assessmentoptions.NextApptAssessmentOption;
import main.java.com.alexpacheco.therapynotes.model.entities.assessmentoptions.SpeechAssessmentOption;

public class Note
{
	private Integer noteId;
	private Client client;
	private LocalDateTime apptDateTime;
	private String diagnosis;
	private Integer sessionNumber;
	private String sessionLength;
	private Boolean virtualAppt;
	private String apptComment;
	private List<Symptom> symptoms;
	private List<Referral> referrals;
	private List<CollateralContact> collateralContacts;
	private String narrative;
	private AppearanceAssessmentOption appearance;
	private String appearanceComment;
	private SpeechAssessmentOption speech;
	private String speechComment;
	private AffectAssessmentOption affect;
	private String affectComment;
	private EyeContactAssessmentOption eyeContact;
	private String eyeContactComment;
	private NextApptAssessmentOption nextAppt;
	private String nextApptComment;
	private String referralComment;
	private String collateralContactComment;
	private LocalDateTime certifiedDate;
	private LocalDateTime insertDate;
	private LocalDateTime updateDate;
	
	public Integer getNoteId()
	{
		return noteId;
	}
	
	public void setNoteId(Integer noteId)
	{
		this.noteId = noteId;
	}
	
	public Client getClient()
	{
		return client;
	}
	
	public void setClient(Client client)
	{
		this.client = client;
	}
	
	public LocalDateTime getApptDateTime()
	{
		return apptDateTime;
	}
	
	public void setApptDateTime(LocalDateTime apptDateTime)
	{
		this.apptDateTime = apptDateTime;
	}
	
	public String getDiagnosis()
	{
		return diagnosis;
	}
	
	public void setDiagnosis(String diagnosis)
	{
		this.diagnosis = diagnosis;
	}
	
	public Integer getSessionNumber()
	{
		return sessionNumber;
	}
	
	public void setSessionNumber(Integer sessionNumber)
	{
		this.sessionNumber = sessionNumber;
	}
	
	public String getSessionLength()
	{
		return sessionLength;
	}
	
	public void setSessionLength(String sessionLength)
	{
		this.sessionLength = sessionLength;
	}
	
	public boolean isVirtualAppt()
	{
		return virtualAppt;
	}
	
	public void setVirtualAppt(boolean virtualAppt)
	{
		this.virtualAppt = virtualAppt;
	}
	
	public String getApptComment()
	{
		return apptComment;
	}
	
	public void setApptComment(String apptComment)
	{
		this.apptComment = apptComment;
	}
	
	public List<Symptom> getSymptoms()
	{
		return symptoms;
	}
	
	public void setSymptoms(List<Symptom> symptoms)
	{
		this.symptoms = symptoms;
	}
	
	public List<Referral> getReferrals()
	{
		return referrals;
	}
	
	public void setReferrals(List<Referral> referrals)
	{
		this.referrals = referrals;
	}
	
	public List<CollateralContact> getCollateralContacts()
	{
		return collateralContacts;
	}
	
	public void setCollateralContacts(List<CollateralContact> collateralContacts)
	{
		this.collateralContacts = collateralContacts;
	}
	
	public String getNarrative()
	{
		return narrative;
	}
	
	public void setNarrative(String narrative)
	{
		this.narrative = narrative;
	}
	
	public AppearanceAssessmentOption getAppearance()
	{
		return appearance;
	}
	
	public void setAppearance(AppearanceAssessmentOption appearance)
	{
		this.appearance = appearance;
	}
	
	public String getAppearanceComment()
	{
		return appearanceComment;
	}
	
	public void setAppearanceComment(String appearanceNotes)
	{
		this.appearanceComment = appearanceNotes;
	}
	
	public SpeechAssessmentOption getSpeech()
	{
		return speech;
	}
	
	public void setSpeech(SpeechAssessmentOption speech)
	{
		this.speech = speech;
	}
	
	public String getSpeechComment()
	{
		return speechComment;
	}
	
	public void setSpeechComment(String speechNotes)
	{
		this.speechComment = speechNotes;
	}
	
	public AffectAssessmentOption getAffect()
	{
		return affect;
	}
	
	public void setAffect(AffectAssessmentOption affect)
	{
		this.affect = affect;
	}
	
	public String getAffectComment()
	{
		return affectComment;
	}
	
	public void setAffectComment(String affectNotes)
	{
		this.affectComment = affectNotes;
	}
	
	public EyeContactAssessmentOption getEyeContact()
	{
		return eyeContact;
	}
	
	public void setEyeContact(EyeContactAssessmentOption eyeContact)
	{
		this.eyeContact = eyeContact;
	}
	
	public String getEyeContactComment()
	{
		return eyeContactComment;
	}
	
	public void setEyeContactComment(String eyeContactNotes)
	{
		this.eyeContactComment = eyeContactNotes;
	}
	
	public NextApptAssessmentOption getNextAppt()
	{
		return nextAppt;
	}
	
	public void setNextAppt(NextApptAssessmentOption nextAppt)
	{
		this.nextAppt = nextAppt;
	}
	
	public String getNextApptComment()
	{
		return nextApptComment;
	}
	
	public void setNextApptComment(String nextApptNotes)
	{
		this.nextApptComment = nextApptNotes;
	}
	
	public String getReferralComment()
	{
		return referralComment;
	}

	public void setReferralComment(String symptomComment)
	{
		this.referralComment = symptomComment;
	}

	public String getCollateralContactComment()
	{
		return collateralContactComment;
	}

	public void setCollateralContactComment(String collateralContactComment)
	{
		this.collateralContactComment = collateralContactComment;
	}

	public LocalDateTime getCertifiedDate()
	{
		return certifiedDate;
	}
	
	public void setCertifiedDate(LocalDateTime certifiedDate)
	{
		this.certifiedDate = certifiedDate;
	}
	
	public LocalDateTime getInsertDate()
	{
		return insertDate;
	}
	
	public void setInsertDate(LocalDateTime insertDate)
	{
		this.insertDate = insertDate;
	}
	
	public LocalDateTime getUpdateDate()
	{
		return updateDate;
	}
	
	public void setUpdateDate(LocalDateTime updateDate)
	{
		this.updateDate = updateDate;
	}
}