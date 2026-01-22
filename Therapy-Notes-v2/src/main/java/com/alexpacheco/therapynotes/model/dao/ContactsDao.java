package com.alexpacheco.therapynotes.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.alexpacheco.therapynotes.model.entities.Contact;
import com.alexpacheco.therapynotes.util.AppLogger;
import com.alexpacheco.therapynotes.util.DbUtil;
import com.alexpacheco.therapynotes.util.JavaUtils;

public class ContactsDao
{
	/**
	 * Saves a new contact to the database.
	 * 
	 * @throws SQLException
	 */
	public void saveNew( Contact contact ) throws SQLException
	{
		String sql = "INSERT INTO contacts (first_name, last_name, linked_client, emergency_contact, email1, email2, email3, phone1, phone2, phone3)"
				+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		_save( contact, sql );
		AppLogger.logDatabaseOperation( "INSERT", "contacts", true );
	}
	
	public void saveExisting( Contact contact ) throws SQLException
	{
		String sql = "UPDATE contacts SET first_name = ?, last_name = ?, linked_client = ?, emergency_contact = ?, email1 = ?,"
				+ " email2 = ?, email3 = ?, phone1 = ?, phone2 = ?, phone3 = ? WHERE contact_id = ?";
		
		_save( contact, sql );
		AppLogger.logDatabaseOperation( "UPDATE", "contacts", true );
	}
	
	private void _save( Contact contact, String sql ) throws SQLException
	{
		try( Connection conn = DbUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement( sql ) )
		{
			pstmt.setString( 1, contact.getFirstName() );
			pstmt.setString( 2, contact.getLastName() );
			pstmt.setInt( 3, contact.getLinkedClientId() );
			pstmt.setInt( 4, JavaUtils.convertBooleanToBit( contact.isEmergencyContact() ) );
			pstmt.setString( 5, contact.getEmail1() );
			pstmt.setString( 6, contact.getEmail2() );
			pstmt.setString( 7, contact.getEmail3() );
			pstmt.setString( 8, contact.getPhone1() );
			pstmt.setString( 9, contact.getPhone2() );
			pstmt.setString( 10, contact.getPhone3() );
			if( contact.getContactId() != null )
				pstmt.setInt( 11, contact.getContactId() );
			
			pstmt.executeUpdate();
		}
	}
	
	public List<Contact> getAllContactsLinkedToClient( int clientId ) throws SQLException
	{
		List<Contact> results = new ArrayList<>();
		String sql = "SELECT * FROM contacts WHERE linked_client = ? ORDER BY emergency_contact DESC";
		
		try( Connection conn = DbUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement( sql ) )
		{
			pstmt.setInt( 1, clientId );
			
			try( ResultSet rs = pstmt.executeQuery() )
			{
				while( rs.next() )
				{
					Contact c = _populateContact( rs );
					results.add( c );
				}
			}
		}
		
		AppLogger.logDatabaseOperation( "SELECT", "contacts", true );
		return results;
	}
	
	public Contact getContactById( Integer contactId ) throws SQLException
	{
		String sql = "SELECT * FROM contacts WHERE contact_id = ?";
		
		try( Connection conn = DbUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement( sql ) )
		{
			pstmt.setInt( 1, contactId );
			
			try( ResultSet rs = pstmt.executeQuery() )
			{
				while( rs.next() )
				{
					return _populateContact( rs );
				}
			}
		}
		
		AppLogger.logDatabaseOperation( "SELECT", "contacts", true );
		return null;
	}
	
	public List<Contact> searchContacts( String firstName, String lastName, Integer linkedClientId ) throws SQLException
	{
		List<Contact> contacts = new ArrayList<>();
		
		String sql = "SELECT * FROM contacts WHERE (? IS NULL OR UPPER(first_name) LIKE UPPER(?)) AND (? IS NULL OR UPPER(last_name) LIKE UPPER(?))"
				+ " AND (? IS NULL OR linked_client = ?) ORDER BY last_name, first_name";
		
		try( Connection conn = DbUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement( sql ) )
		{
			if( JavaUtils.isNullOrEmpty( firstName ) )
			{
				pstmt.setNull( 1, Types.VARCHAR );
				pstmt.setNull( 2, Types.VARCHAR );
			}
			else
			{
				pstmt.setString( 1, firstName );
				pstmt.setString( 2, "%" + firstName + "%" );
			}
			
			if( lastName == null )
			{
				pstmt.setNull( 3, Types.VARCHAR );
				pstmt.setNull( 4, Types.VARCHAR );
			}
			else
			{
				pstmt.setString( 3, lastName );
				pstmt.setString( 4, "%" + lastName + "%" );
			}
			
			if( linkedClientId == null )
			{
				pstmt.setNull( 5, Types.INTEGER );
				pstmt.setNull( 6, Types.INTEGER );
			}
			else
			{
				pstmt.setInt( 5, linkedClientId );
				pstmt.setInt( 6, linkedClientId );
			}
			
			// Execute query
			try( ResultSet rs = pstmt.executeQuery() )
			{
				while( rs.next() )
				{
					contacts.add( _populateContact( rs ) );
				}
			}
		}
		
		AppLogger.logDatabaseOperation( "SELECT", "contacts", true );
		return contacts;
	}
	
	/**
	 * Creates, populates, and returns a Contact object based on query results of SELECT * FROM contacts
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private Contact _populateContact( ResultSet rs ) throws SQLException
	{
		Contact c = new Contact();
		c.setContactId( rs.getInt( "contact_id" ) );
		c.setFirstName( rs.getString( "first_name" ) );
		c.setLastName( rs.getString( "last_name" ) );
		c.setLinkedClientId( rs.getInt( "linked_client" ) );
		c.setEmail1( rs.getString( "email1" ) );
		c.setEmail2( rs.getString( "email2" ) );
		c.setEmail3( rs.getString( "email3" ) );
		c.setPhone1( rs.getString( "phone1" ) );
		c.setPhone2( rs.getString( "phone2" ) );
		c.setPhone3( rs.getString( "phone3" ) );
		c.setEmergencyContact( JavaUtils.convertBitToBoolean( rs.getInt( "emergency_contact" ) ) );
		return c;
	}
}
