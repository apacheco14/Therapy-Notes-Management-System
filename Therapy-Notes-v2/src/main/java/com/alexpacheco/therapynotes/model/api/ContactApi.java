package com.alexpacheco.therapynotes.model.api;

import java.sql.SQLException;
import java.util.List;

import com.alexpacheco.therapynotes.controller.enums.ErrorCode;
import com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.EntityValidator;
import com.alexpacheco.therapynotes.model.dao.ContactsDao;
import com.alexpacheco.therapynotes.model.entities.Contact;
import com.alexpacheco.therapynotes.util.AppLogger;

public class ContactApi
{
	private final ContactsDao contactDao = new ContactsDao();
	
	/**
	 * Creates a new client and validates the unique client_code.
	 */
	public void createContact( Contact contact ) throws TherapyAppException
	{
		EntityValidator.validateContact( contact );
		
		try
		{
			contactDao.saveNew( contact );
		}
		catch( SQLException e )
		{
			AppLogger.error( e );
			throw new TherapyAppException( "An internal database error occurred.", ErrorCode.DB_ERROR );
		}
	}
	
	public List<Contact> getAllContactsLinkedToClient( int clientId ) throws TherapyAppException
	{
		try
		{
			return contactDao.getAllContactsLinkedToClient( clientId );
		}
		catch( SQLException e )
		{
			AppLogger.error( e );
			throw new TherapyAppException( "Error occurred while searching for contacts.", ErrorCode.DB_ERROR );
		}
	}
	
	public Contact getContactById( Integer contactId ) throws TherapyAppException
	{
		try
		{
			return contactDao.getContactById( contactId );
		}
		catch( SQLException e )
		{
			AppLogger.error( e );
			throw new TherapyAppException( "An internal database error occurred.", ErrorCode.DB_ERROR );
		}
	}
	
	public void updateContact( Contact contact ) throws TherapyAppException
	{
		EntityValidator.validateContact( contact );
		
		try
		{
			contactDao.saveExisting( contact );
		}
		catch( SQLException e )
		{
			AppLogger.error( e );
			throw new TherapyAppException( "An internal database error occurred.", ErrorCode.DB_ERROR );
		}
	}
	
	public List<Contact> searchContacts( String firstName, String lastName, Integer linkedClientId ) throws TherapyAppException
	{
		try
		{
			return contactDao.searchContacts( firstName, lastName, linkedClientId );
		}
		catch( SQLException e )
		{
			AppLogger.error( e );
			throw new TherapyAppException( "Error occurred while searching for contacts.", ErrorCode.DB_ERROR );
		}
	}
}
