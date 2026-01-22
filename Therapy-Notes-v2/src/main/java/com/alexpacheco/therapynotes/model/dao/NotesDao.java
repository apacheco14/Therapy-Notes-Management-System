package com.alexpacheco.therapynotes.model.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alexpacheco.therapynotes.controller.AppController;
import com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.entities.CollateralContact;
import com.alexpacheco.therapynotes.model.entities.Note;
import com.alexpacheco.therapynotes.model.entities.Referral;
import com.alexpacheco.therapynotes.model.entities.Symptom;
import com.alexpacheco.therapynotes.model.entities.assessmentoptions.AffectAssessmentOption;
import com.alexpacheco.therapynotes.model.entities.assessmentoptions.AppearanceAssessmentOption;
import com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOption;
import com.alexpacheco.therapynotes.model.entities.assessmentoptions.EyeContactAssessmentOption;
import com.alexpacheco.therapynotes.model.entities.assessmentoptions.NextApptAssessmentOption;
import com.alexpacheco.therapynotes.model.entities.assessmentoptions.SpeechAssessmentOption;
import com.alexpacheco.therapynotes.util.AppLogger;
import com.alexpacheco.therapynotes.util.DateFormatUtil;
import com.alexpacheco.therapynotes.util.DbUtil;
import com.alexpacheco.therapynotes.util.JavaUtils;

public class NotesDao
{
	/**
	 * Saves a complete session note. Uses a transaction to ensure all-or-nothing data integrity.
	 */
	public void createNewNote( Note note ) throws SQLException
	{
		String insertNoteSql = "INSERT INTO notes (client_id, appt_date_time, virtual_appt, appt_note, diagnosis, session_number, session_length, narrative, appearance_comment, speech_comment, affect_comment, eye_contact_comment, next_appt_comment, certified, appearance, speech, affect, eye_contact, next_appt, referral_comment, collateral_contact_comment)"
				+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String insertSymptomSql = "INSERT INTO symptoms (note_id, symptom_id) VALUES (?, ?)";
		String insertReferralSql = "INSERT INTO referrals (note_id, referral_id)  VALUES (?, ?)";
		String insertCollateralContactSql = "INSERT INTO collateral_contacts (note_id, collateral_contact_type_id)  VALUES (?, ?)";
		
		Connection conn = null;
		try
		{
			conn = DbUtil.getConnection();
			conn.setAutoCommit( false ); // Start Transaction
			
			int generatedNoteId;
			
			// 1. Insert the Note record
			try( PreparedStatement pstmt = conn.prepareStatement( insertNoteSql, Statement.RETURN_GENERATED_KEYS ) )
			{
				pstmt.setInt( 1, note.getClient().getClientId() );
				pstmt.setString( 2, DateFormatUtil.toSqliteString( note.getApptDateTime() ) );
				pstmt.setInt( 3, JavaUtils.convertBooleanToBit( note.isVirtualAppt() ) );
				pstmt.setString( 4, note.getApptComment() );
				pstmt.setString( 5, note.getDiagnosis() );
				pstmt.setInt( 6, note.getSessionNumber() );
				pstmt.setString( 7, note.getSessionLength() );
				pstmt.setString( 8, note.getNarrative() );
				pstmt.setString( 9, note.getAppearanceComment() );
				pstmt.setString( 10, note.getSpeechComment() );
				pstmt.setString( 11, note.getAffectComment() );
				pstmt.setString( 12, note.getEyeContactComment() );
				pstmt.setString( 13, note.getNextApptComment() );
				pstmt.setString( 14, DateFormatUtil.toSqliteString( note.getCertifiedDate() ) );
				pstmt.setInt( 15, getAssessmentOptionId( note.getAppearance() ) );
				pstmt.setInt( 16, getAssessmentOptionId( note.getSpeech() ) );
				pstmt.setInt( 17, getAssessmentOptionId( note.getAffect() ) );
				pstmt.setInt( 18, getAssessmentOptionId( note.getEyeContact() ) );
				pstmt.setInt( 19, getAssessmentOptionId( note.getNextAppt() ) );
				pstmt.setString( 20, note.getReferralComment() );
				pstmt.setString( 21, note.getCollateralContactComment() );
				pstmt.executeUpdate();
				
				// Get the newly created note_id
				try( ResultSet generatedKeys = pstmt.getGeneratedKeys() )
				{
					if( generatedKeys.next() )
					{
						generatedNoteId = generatedKeys.getInt( 1 );
					}
					else
					{
						throw new SQLException( "Creating note failed, no ID obtained." );
					}
				}
			}
			
			// 2. Insert the Symptoms (Junction Table)
			try( PreparedStatement pstmtSymptom = conn.prepareStatement( insertSymptomSql ) )
			{
				for( Symptom symptom : note.getSymptoms() )
				{
					pstmtSymptom.setInt( 1, generatedNoteId );
					pstmtSymptom.setInt( 2, symptom.getSymptomId() );
					pstmtSymptom.addBatch(); // Use batching for performance
				}
				pstmtSymptom.executeBatch();
			}
			
			// 3. Insert the Referrals (Junction Table)
			try( PreparedStatement pstmtReferral = conn.prepareStatement( insertReferralSql ) )
			{
				for( Referral referral : note.getReferrals() )
				{
					pstmtReferral.setInt( 1, generatedNoteId );
					pstmtReferral.setInt( 2, referral.getReferralTypeId() );
					pstmtReferral.addBatch(); // Use batching for performance
				}
				pstmtReferral.executeBatch();
			}
			
			// 4. Insert the Collateral Contacts (Junction Table)
			try( PreparedStatement pstmtCollateralContacts = conn.prepareStatement( insertCollateralContactSql ) )
			{
				for( CollateralContact collateralContact : note.getCollateralContacts() )
				{
					pstmtCollateralContacts.setInt( 1, generatedNoteId );
					pstmtCollateralContacts.setInt( 2, collateralContact.getCollateralContactTypeId() );
					pstmtCollateralContacts.addBatch(); // Use batching for performance
				}
				pstmtCollateralContacts.executeBatch();
			}
			
			conn.commit(); // Finalize everything
		}
		catch( SQLException e )
		{
			if( conn != null )
			{
				conn.rollback(); // Undo everything if any part fails
			}
			AppLogger.logDatabaseOperation( "INSERT", "notes", false );
			throw e;
		}
		finally
		{
			if( conn != null )
			{
				conn.setAutoCommit( true );
				conn.close();
			}
			AppLogger.logDatabaseOperation( "INSERT", "notes", true );
		}
	}
	
	/**
	 * Saves a complete session note. Uses a transaction to ensure all-or-nothing data integrity.
	 */
	public void updateExistingNote( Note note ) throws SQLException
	{
		// Update the main note record
		String updateNoteSql = "UPDATE notes SET client_id = ?, appt_date_time = ?, virtual_appt = ?, appt_note = ?, diagnosis = ?, session_number = ?, session_length = ?, narrative = ?, appearance_comment = ?, speech_comment = ?, affect_comment = ?, eye_contact_comment = ?, next_appt_comment = ?, certified = ?, appearance = ?, speech = ?, affect = ?, eye_contact = ?, next_appt = ?, referral_comment = ?, collateral_contact_comment = ? WHERE note_id = ?";
		
		// Delete existing related records before re-inserting
		String deleteSymptomsSql = "DELETE FROM symptoms WHERE note_id = ?";
		String deleteReferralsSql = "DELETE FROM referrals WHERE note_id = ?";
		String deleteCollateralContactsSql = "DELETE FROM collateral_contacts WHERE note_id = ?";
		
		// Insert statements remain the same for re-inserting updated data
		String insertSymptomSql = "INSERT INTO symptoms (note_id, symptom_id) VALUES (?, ?)";
		String insertReferralSql = "INSERT INTO referrals (note_id, referral_id) VALUES (?, ?)";
		String insertCollateralContactSql = "INSERT INTO collateral_contacts (note_id, collateral_contact_type_id) VALUES (?, ?)";
		
		Connection conn = null;
		try
		{
			conn = DbUtil.getConnection();
			conn.setAutoCommit( false ); // Start Transaction
			
			// 1. Update the Note record
			try( PreparedStatement pstmt = conn.prepareStatement( updateNoteSql ) )
			{
				pstmt.setInt( 1, note.getClient().getClientId() );
				pstmt.setString( 2, DateFormatUtil.toSqliteString( note.getApptDateTime() ) );
				pstmt.setInt( 3, JavaUtils.convertBooleanToBit( note.isVirtualAppt() ) );
				pstmt.setString( 4, note.getApptComment() );
				pstmt.setString( 5, note.getDiagnosis() );
				pstmt.setInt( 6, note.getSessionNumber() );
				pstmt.setString( 7, note.getSessionLength() );
				pstmt.setString( 8, note.getNarrative() );
				pstmt.setString( 9, note.getAppearanceComment() );
				pstmt.setString( 10, note.getSpeechComment() );
				pstmt.setString( 11, note.getAffectComment() );
				pstmt.setString( 12, note.getEyeContactComment() );
				pstmt.setString( 13, note.getNextApptComment() );
				pstmt.setString( 14, DateFormatUtil.toSqliteString( note.getCertifiedDate() ) );
				pstmt.setInt( 15, getAssessmentOptionId( note.getAppearance() ) );
				pstmt.setInt( 16, getAssessmentOptionId( note.getSpeech() ) );
				pstmt.setInt( 17, getAssessmentOptionId( note.getAffect() ) );
				pstmt.setInt( 18, getAssessmentOptionId( note.getEyeContact() ) );
				pstmt.setInt( 19, getAssessmentOptionId( note.getNextAppt() ) );
				pstmt.setString( 20, note.getReferralComment() );
				pstmt.setString( 21, note.getCollateralContactComment() );
				pstmt.setInt( 22, note.getNoteId() );
				pstmt.executeUpdate();
			}
			
			// 2. Delete records from junction tables
			try( PreparedStatement pstmtDeleteSymptoms = conn.prepareStatement( deleteSymptomsSql ) )
			{
				pstmtDeleteSymptoms.setInt( 1, note.getNoteId() );
				pstmtDeleteSymptoms.executeUpdate();
			}
			
			try( PreparedStatement pstmtDeleteReferrals = conn.prepareStatement( deleteReferralsSql ) )
			{
				pstmtDeleteReferrals.setInt( 1, note.getNoteId() );
				pstmtDeleteReferrals.executeUpdate();
			}
			
			try( PreparedStatement pstmtDeleteCollateralContacts = conn.prepareStatement( deleteCollateralContactsSql ) )
			{
				pstmtDeleteCollateralContacts.setInt( 1, note.getNoteId() );
				pstmtDeleteCollateralContacts.executeUpdate();
			}
			
			// 3. Insert the Symptoms (Junction Table)
			try( PreparedStatement pstmtSymptom = conn.prepareStatement( insertSymptomSql ) )
			{
				for( Symptom symptom : note.getSymptoms() )
				{
					pstmtSymptom.setInt( 1, note.getNoteId() );
					pstmtSymptom.setInt( 2, symptom.getSymptomId() );
					pstmtSymptom.addBatch(); // Use batching for performance
				}
				pstmtSymptom.executeBatch();
			}
			
			// 4. Insert the Referrals (Junction Table)
			try( PreparedStatement pstmtReferral = conn.prepareStatement( insertReferralSql ) )
			{
				for( Referral referral : note.getReferrals() )
				{
					pstmtReferral.setInt( 1, note.getNoteId() );
					pstmtReferral.setInt( 2, referral.getReferralTypeId() );
					pstmtReferral.addBatch(); // Use batching for performance
				}
				pstmtReferral.executeBatch();
			}
			
			// 5. Insert the Collateral Contacts (Junction Table)
			try( PreparedStatement pstmtCollateralContacts = conn.prepareStatement( insertCollateralContactSql ) )
			{
				for( CollateralContact collateralContact : note.getCollateralContacts() )
				{
					pstmtCollateralContacts.setInt( 1, note.getNoteId() );
					pstmtCollateralContacts.setInt( 2, collateralContact.getCollateralContactTypeId() );
					pstmtCollateralContacts.addBatch(); // Use batching for performance
				}
				pstmtCollateralContacts.executeBatch();
			}
			
			conn.commit(); // Finalize everything
		}
		catch( SQLException e )
		{
			if( conn != null )
			{
				conn.rollback(); // Undo everything if any part fails
			}
			AppLogger.logDatabaseOperation( "UPDATE", "notes", false );
			throw e;
		}
		finally
		{
			if( conn != null )
			{
				conn.setAutoCommit( true );
				conn.close();
				AppLogger.logDatabaseOperation( "UPDATE", "notes", true );
			}
		}
	}
	
	private Integer getAssessmentOptionId( AssessmentOption option )
	{
		if( option == null )
			return null;
		else
			return option.getId();
	}
	
	public Note getNote( int note_id ) throws SQLException, TherapyAppException
	{
		String sql = "SELECT * FROM notes WHERE note_id = ?";
		
		try( Connection conn = DbUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement( sql ) )
		{
			pstmt.setInt( 1, note_id );
			try( ResultSet rs = pstmt.executeQuery() )
			{
				while( rs.next() )
				{
					return _populateNote( rs );
				}
			}
		}
		
		AppLogger.logDatabaseOperation( "SELECT", "notes", true );
		return null;
	}
	
	public List<Note> searchNotes( Integer clientId, Date startDate, Date endDate ) throws SQLException, TherapyAppException
	{
		List<Note> notes = new ArrayList<>();
		
		String sql = "SELECT * FROM notes WHERE (? IS NULL OR client_id = ?) AND (? IS NULL OR DATE(appt_date_time) >= DATE(?))"
				+ " AND (? IS NULL OR DATE(appt_date_time) <= DATE(?)) ORDER BY appt_date_time DESC LIMIT 100";
		
		try( Connection conn = DbUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement( sql ) )
		{
			if( clientId == null )
			{
				pstmt.setNull( 1, Types.INTEGER );
				pstmt.setNull( 2, Types.INTEGER );
			}
			else
			{
				pstmt.setInt( 1, clientId );
				pstmt.setInt( 2, clientId );
			}
			
			if( startDate == null )
			{
				pstmt.setNull( 3, Types.VARCHAR );
				pstmt.setNull( 4, Types.VARCHAR );
			}
			else
			{
				String startDateStr = DateFormatUtil.toSqliteString( startDate );
				pstmt.setString( 3, startDateStr );
				pstmt.setString( 4, startDateStr );
			}
			
			if( endDate == null )
			{
				pstmt.setNull( 5, Types.VARCHAR );
				pstmt.setNull( 6, Types.VARCHAR );
			}
			else
			{
				String endDateStr = DateFormatUtil.toSqliteString( endDate );
				pstmt.setString( 5, endDateStr );
				pstmt.setString( 6, endDateStr );
			}
			
			try( ResultSet rs = pstmt.executeQuery() )
			{
				while( rs.next() )
				{
					notes.add( _populateNote( rs ) );
				}
			}
		}
		
		AppLogger.logDatabaseOperation( "SELECT", "notes", true );
		return notes;
	}
	
	public Integer getHighestUsedSessionNumberForClient( Integer clientId ) throws SQLException
	{
		if( clientId == null )
			return null;
		
		String sql = "SELECT MAX(session_number) FROM notes WHERE client_id = ?";
		
		try( Connection conn = DbUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement( sql ) )
		{
			pstmt.setInt( 1, clientId );
			try( ResultSet rs = pstmt.executeQuery() )
			{
				while( rs.next() )
				{
					AppLogger.logDatabaseOperation( "SELECT MAX(session_number)", "notes", true );
					return rs.getInt( 1 );
				}
			}
		}
		
		AppLogger.logDatabaseOperation( "SELECT MAX(session_number)", "notes", false );
		return null;
	}
	
	public String getLastUsedDiagnosisForClient( Integer clientId ) throws SQLException
	{
		if( clientId == null )
			return null;
		
		String sql = "SELECT diagnosis FROM notes WHERE client_id = ? ORDER BY insert_date DESC";
		
		try( Connection conn = DbUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement( sql ) )
		{
			pstmt.setInt( 1, clientId );
			try( ResultSet rs = pstmt.executeQuery() )
			{
				while( rs.next() )
				{
					return rs.getString( 1 );
				}
			}
		}
		
		AppLogger.logDatabaseOperation( "SELECT", "notes", true );
		return null;
	}
	
	/**
	 * Creates, populates, and returns a Note object based on query results of SELECT * FROM notes
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 * @throws TherapyAppException
	 */
	private Note _populateNote( ResultSet rs ) throws SQLException, TherapyAppException
	{
		Note n = new Note();
		n.setNoteId( rs.getInt( "note_id" ) );
		n.setClient( AppController.getClientById( rs.getInt( "client_id" ) ) );
		n.setApptDateTime( DateFormatUtil.toLocalDateTime( rs.getString( "appt_date_time" ) ) );
		n.setVirtualAppt( JavaUtils.convertBitToBoolean( rs.getInt( "virtual_appt" ) ) );
		n.setApptComment( rs.getString( "appt_note" ) );
		n.setDiagnosis( rs.getString( "diagnosis" ) );
		n.setSessionNumber( rs.getInt( "session_number" ) );
		n.setSessionLength( rs.getString( "session_length" ) );
		n.setNarrative( rs.getString( "narrative" ) );
		n.setAppearance( (AppearanceAssessmentOption) AppController.getAssessmentOptionById( rs.getInt( "appearance" ) ) );
		n.setAppearanceComment( rs.getString( "appearance_comment" ) );
		n.setSpeech( (SpeechAssessmentOption) AppController.getAssessmentOptionById( rs.getInt( "speech" ) ) );
		n.setSpeechComment( rs.getString( "speech_comment" ) );
		n.setAffect( (AffectAssessmentOption) AppController.getAssessmentOptionById( rs.getInt( "affect" ) ) );
		n.setAffectComment( rs.getString( "affect_comment" ) );
		n.setEyeContact( (EyeContactAssessmentOption) AppController.getAssessmentOptionById( rs.getInt( "eye_contact" ) ) );
		n.setEyeContactComment( rs.getString( "eye_contact_comment" ) );
		n.setNextAppt( (NextApptAssessmentOption) AppController.getAssessmentOptionById( rs.getInt( "next_appt" ) ) );
		n.setNextApptComment( rs.getString( "next_appt_comment" ) );
		n.setReferralComment( rs.getString( "referral_comment" ) );
		n.setCollateralContactComment( rs.getString( "collateral_contact_comment" ) );
		n.setCertifiedDate( DateFormatUtil.toLocalDateTime( rs.getString( "certified" ) ) );
		n.setInsertDate( DateFormatUtil.toLocalDateTime( rs.getString( "insert_date" ) ) );
		n.setUpdateDate( DateFormatUtil.toLocalDateTime( rs.getString( "update_date" ) ) );
		return n;
	}
}