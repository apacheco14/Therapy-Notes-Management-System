package com.alexpacheco.therapynotes.model.api;

import java.sql.SQLException;
import java.util.List;

import com.alexpacheco.therapynotes.controller.enums.ErrorCode;
import com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.dao.ReferralsDao;
import com.alexpacheco.therapynotes.model.entities.Referral;
import com.alexpacheco.therapynotes.util.AppLogger;

public class ReferralApi
{
	private final ReferralsDao referralsDao = new ReferralsDao();
	
	public List<Referral> getSelectedReferralsForNote( Integer noteId ) throws TherapyAppException
	{
		try
		{
			return referralsDao.getSelectedReferralsForNote( noteId );
		}
		catch( SQLException e )
		{
			AppLogger.error( e );
			throw new TherapyAppException( "Error occurred while retrieving referrals for note.", ErrorCode.DB_ERROR );
		}
	}
}
