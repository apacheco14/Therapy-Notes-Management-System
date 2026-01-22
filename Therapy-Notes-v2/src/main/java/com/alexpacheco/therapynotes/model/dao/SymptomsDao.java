package com.alexpacheco.therapynotes.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.entities.Symptom;
import com.alexpacheco.therapynotes.util.AppLogger;
import com.alexpacheco.therapynotes.util.DateFormatUtil;
import com.alexpacheco.therapynotes.util.DbUtil;

public class SymptomsDao
{
	public List<Symptom> getSelectedSymptomsForNote( Integer noteId ) throws SQLException, TherapyAppException
	{
		List<Symptom> symptoms = new ArrayList<>();
		String sql = "SELECT s.note_id, s.symptom_id, a.name, a.description, s.insert_date FROM symptoms s JOIN assessment_options a ON s.symptom_id = a.id WHERE s.note_id = ?";
		
		try( Connection conn = DbUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement( sql ) )
		{
			pstmt.setInt( 1, noteId );
			
			try( ResultSet rs = pstmt.executeQuery() )
			{
				while( rs.next() )
				{
					symptoms.add( _populateSymptom( rs ) );
				}
			}
		}
		
		AppLogger.logDatabaseOperation( "SELECT", "symptoms JOIN assessment_options", true );
		return symptoms;
	}
	
	private Symptom _populateSymptom( ResultSet rs ) throws SQLException, TherapyAppException
	{
		Symptom symptom = new Symptom();
		symptom.setNoteId( rs.getInt( "note_id" ) );
		symptom.setSymptomId( rs.getInt( "symptom_id" ) );
		symptom.setSymptomName( rs.getString( "name" ) );
		symptom.setSymptomDescription( rs.getString( "description" ) );
		symptom.setInsertDate( DateFormatUtil.toLocalDateTime( rs.getString( "insert_date" ) ) );
		return symptom;
	}
}
