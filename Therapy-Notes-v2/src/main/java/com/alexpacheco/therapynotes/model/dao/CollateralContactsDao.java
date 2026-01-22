package com.alexpacheco.therapynotes.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.alexpacheco.therapynotes.controller.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.entities.CollateralContact;
import com.alexpacheco.therapynotes.util.AppLogger;
import com.alexpacheco.therapynotes.util.DateFormatUtil;
import com.alexpacheco.therapynotes.util.DbUtil;

public class CollateralContactsDao
{
	public List<CollateralContact> getSelectedCollateralContactsForNote( Integer noteId ) throws SQLException, TherapyAppException
	{
		List<CollateralContact> collateralContacts = new ArrayList<>();
		String sql = "SELECT c.note_id, c.collateral_contact_type_id, a.name, a.description, c.insert_date FROM collateral_contacts c JOIN assessment_options a ON c.collateral_contact_type_id = a.id WHERE c.note_id = ?";
		
		try( Connection conn = DbUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement( sql ) )
		{
			pstmt.setInt( 1, noteId );
			
			try( ResultSet rs = pstmt.executeQuery() )
			{
				while( rs.next() )
				{
					collateralContacts.add( _populateCollateralContact( rs ) );
				}
			}
		}
		
		AppLogger.logDatabaseOperation( "SELECT", "collateral_contacts JOIN assessment_options", true );
		return collateralContacts;
	}
	
	private CollateralContact _populateCollateralContact( ResultSet rs ) throws SQLException, TherapyAppException
	{
		CollateralContact collateralContact = new CollateralContact();
		collateralContact.setNoteId( rs.getInt( "note_id" ) );
		collateralContact.setCollateralContactTypeId( rs.getInt( "collateral_contact_type_id" ) );
		collateralContact.setCollateralContactName( rs.getString( "name" ) );
		collateralContact.setCollateralContactDescription( rs.getString( "description" ) );
		collateralContact.setInsertDate( DateFormatUtil.toLocalDateTime( rs.getString( "insert_date" ) ) );
		return collateralContact;
	}
}
