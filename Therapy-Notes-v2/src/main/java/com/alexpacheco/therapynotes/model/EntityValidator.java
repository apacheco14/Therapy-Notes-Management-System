package main.java.com.alexpacheco.therapynotes.model;

import java.util.List;

import main.java.com.alexpacheco.therapynotes.controller.errorhandling.exceptions.MissingRequiredElementException;
import main.java.com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import main.java.com.alexpacheco.therapynotes.model.entities.Client;
import main.java.com.alexpacheco.therapynotes.model.entities.Contact;
import main.java.com.alexpacheco.therapynotes.model.entities.Note;
import main.java.com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOption;
import main.java.com.alexpacheco.therapynotes.util.JavaUtils;
import main.java.com.alexpacheco.therapynotes.util.PreferencesUtil;

public class EntityValidator
{
	public static void validateClient( Client client ) throws TherapyAppException
	{
		if( client.getClientCode() == null || client.getClientCode().length() < 3 )
			throw new MissingRequiredElementException( "Client code must be at least 3 characters." );
		
		if( JavaUtils.isNullOrEmpty( client.getFirstName() ) && JavaUtils.isNullOrEmpty( client.getLastName() ) )
			throw new MissingRequiredElementException( "First or last name is required." );
		
		if( JavaUtils.isNullOrEmpty( client.getFirstName() ) && PreferencesUtil.isClientFirstNameRequired() )
			throw new MissingRequiredElementException( "First name is required." );
		
		if( JavaUtils.isNullOrEmpty( client.getLastName() ) && PreferencesUtil.isClientLastNameRequired() )
			throw new MissingRequiredElementException( "Last name is required." );
		
		if( client.getDateOfBirth() == null && PreferencesUtil.isClientDOBRequired() )
			throw new MissingRequiredElementException( "Date of birth is required." );
	}
	
	public static void validateNote( Note note ) throws TherapyAppException
	{
		if( JavaUtils.isNullOrEmpty( note.getDiagnosis() ) && PreferencesUtil.isNoteDiagnosisRequired() )
			throw new MissingRequiredElementException( "Diagnosis is required." );
		
		if( ( note.getSymptoms() == null || note.getSymptoms().isEmpty() ) && PreferencesUtil.isNoteSymptomsRequired() )
			throw new MissingRequiredElementException( "Symptom selection is required." );
		
		if( JavaUtils.isNullOrEmpty( note.getNarrative() ) && PreferencesUtil.isNoteNarrativeRequired() )
			throw new MissingRequiredElementException( "Narrative is required." );
		
		if( note.getAppearance() == null && PreferencesUtil.isNoteAppearanceRequired() )
			throw new MissingRequiredElementException( "Appearance selection is required." );
		
		if( note.getSpeech() == null && PreferencesUtil.isNoteSpeechRequired() )
			throw new MissingRequiredElementException( "Speech selection is required." );
		
		if( note.getAffect() == null && PreferencesUtil.isNoteAffectRequired() )
			throw new MissingRequiredElementException( "Affect selection is required." );
		
		if( note.getEyeContact() == null && PreferencesUtil.isNoteEyeContactRequired() )
			throw new MissingRequiredElementException( "Eye contact selection is required." );
		
		if( ( note.getReferrals() == null || note.getReferrals().isEmpty() ) && PreferencesUtil.isNoteReferralsRequired() )
			throw new MissingRequiredElementException( "Referral selection is required." );
		
		if( ( note.getCollateralContacts() == null || note.getCollateralContacts().isEmpty() )
				&& PreferencesUtil.isNoteCollateralContactsRequired() )
			throw new MissingRequiredElementException( "Collateral Contact selection is required." );
		
		if( note.getNextAppt() == null && PreferencesUtil.isNoteNextAppointmentRequired() )
			throw new MissingRequiredElementException( "Next appointment selection is required." );
	}
	
	public static void validateAssessmentOptions( List<AssessmentOption> options ) throws TherapyAppException
	{
		for( AssessmentOption option : options )
		{
			validateAssessmentOption( option );
		}
	}
	
	public static void validateAssessmentOption( AssessmentOption option ) throws TherapyAppException
	{
		if( option.getOptionType() == null )
			throw new MissingRequiredElementException( "Assessment option type is required." );
		
		if( JavaUtils.isNullOrEmpty( option.getName() ) )
			throw new MissingRequiredElementException( "Name is required." );
	}
	
	public static void validateContact( Contact contact ) throws TherapyAppException
	{
		if( JavaUtils.isNullOrEmpty( contact.getFirstName() ) && JavaUtils.isNullOrEmpty( contact.getLastName() ) )
			throw new MissingRequiredElementException( "First or last name is required." );
		
		if( JavaUtils.isNullOrEmpty( contact.getFirstName() ) && PreferencesUtil.isContactFirstNameRequired() )
			throw new MissingRequiredElementException( "First name is required." );
		
		if( JavaUtils.isNullOrEmpty( contact.getLastName() ) && PreferencesUtil.isContactLastNameRequired() )
			throw new MissingRequiredElementException( "Last name is required." );
	}
}
