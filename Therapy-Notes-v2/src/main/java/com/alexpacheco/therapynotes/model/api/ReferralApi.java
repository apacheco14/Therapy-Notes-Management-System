package main.java.com.alexpacheco.therapynotes.model.api;

import java.sql.SQLException;
import java.util.List;

import main.java.com.alexpacheco.therapynotes.controller.AppController;
import main.java.com.alexpacheco.therapynotes.controller.enums.ErrorCode;
import main.java.com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import main.java.com.alexpacheco.therapynotes.model.dao.ReferralsDao;
import main.java.com.alexpacheco.therapynotes.model.entities.Referral;

public class ReferralApi
{
	private final ReferralsDao referralsDao = new ReferralsDao();
	
	public List<Referral> getSelectedReferralsForNote(Integer noteId) throws TherapyAppException
	{
		try
		{
			return referralsDao.getSelectedReferralsForNote(noteId);
		}
		catch (SQLException e)
		{
			AppController.logException("ReferralApi", e);
			throw new TherapyAppException("Error occurred while retrieving referrals for note.", ErrorCode.DB_ERROR);
		}
	}
}
