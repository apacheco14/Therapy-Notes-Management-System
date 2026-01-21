package com.alexpacheco.therapynotes.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.entities.Referral;
import com.alexpacheco.therapynotes.util.DateFormatUtil;
import com.alexpacheco.therapynotes.util.DbUtil;

public class ReferralsDao
{
	public List<Referral> getSelectedReferralsForNote(Integer noteId) throws SQLException, TherapyAppException
	{
		List<Referral> referrals = new ArrayList<>();
		String sql = "SELECT r.note_id, r.referral_id, a.name, a.description, r.insert_date FROM referrals r JOIN assessment_options a ON r.referral_id = a.id WHERE r.note_id = ?";
		
		try (Connection conn = DbUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql))
		{
			pstmt.setInt(1, noteId);
			
			try (ResultSet rs = pstmt.executeQuery())
			{
				while (rs.next())
				{
					referrals.add(_populateReferral(rs));
				}
			}
		}
		
		return referrals;
	}
	
	private Referral _populateReferral(ResultSet rs) throws SQLException, TherapyAppException
	{
		Referral referral = new Referral();
		referral.setNoteId(rs.getInt("note_id"));
		referral.setReferralTypeId(rs.getInt("referral_id"));
		referral.setReferralName(rs.getString("name"));
		referral.setReferralDescription(rs.getString("description"));
		referral.setInsertDate(DateFormatUtil.toLocalDateTime(rs.getString("insert_date")));
		return referral;
	}
}
