package com.alexpacheco.therapynotes.model.api;

import java.sql.SQLException;
import java.util.List;

import com.alexpacheco.therapynotes.controller.enums.ErrorCode;
import com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.dao.CollateralContactsDao;
import com.alexpacheco.therapynotes.model.entities.CollateralContact;
import com.alexpacheco.therapynotes.util.AppLogger;

public class CollateralContactApi
{
	private final CollateralContactsDao collateralContactsDao = new CollateralContactsDao();
	
	public List<CollateralContact> getSelectedCollateralContactsForNote( Integer noteId ) throws TherapyAppException
	{
		try
		{
			return collateralContactsDao.getSelectedCollateralContactsForNote( noteId );
		}
		catch( SQLException e )
		{
			AppLogger.error( e );
			throw new TherapyAppException( "Error occurred while retrieving collateral contacts for note.", ErrorCode.DB_ERROR );
		}
	}
}
