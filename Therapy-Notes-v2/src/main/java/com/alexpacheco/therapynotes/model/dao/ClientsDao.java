package com.alexpacheco.therapynotes.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.entities.Client;
import com.alexpacheco.therapynotes.util.DateFormatUtil;
import com.alexpacheco.therapynotes.util.DbUtil;
import com.alexpacheco.therapynotes.util.JavaUtils;

public class ClientsDao
{
	/**
	 * Checks if a client_code already exists in the database. Used by the Service layer to prevent
	 * duplicate clinical records.
	 */
	public boolean existsByCode(String clientCode) throws SQLException
	{
		String sql = "SELECT COUNT(*) FROM clients WHERE UPPER(client_code) = ?";
		
		try (Connection conn = DbUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql))
		{
			pstmt.setString(1, clientCode.toUpperCase());
			try (ResultSet rs = pstmt.executeQuery())
			{
				if (rs.next())
				{
					return rs.getInt(1) > 0;
				}
			}
		}
		return false;
	}
	
	/**
	 * Saves a new client to the database.
	 */
	public void saveNew(Client client) throws SQLException
	{
		String sql = "INSERT INTO clients (first_name, last_name, client_code, inactive, email1, email2, email3, phone1, phone2, phone3, date_of_birth)"
				+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		_save(client, sql);
	}
	
	/**
	 * Saves an existing client to the database.
	 */
	public void saveExisting(Client client) throws SQLException
	{
		String sql = "UPDATE clients SET first_name = ?, last_name = ?, client_code = ?, inactive = ?, email1 = ?,"
				+ " email2 = ?, email3 = ?, phone1 = ?, phone2 = ?, phone3 = ?, date_of_birth = ? WHERE client_id = ?";
		
		_save(client, sql);
	}
	
	private void _save(Client client, String sql) throws SQLException
	{
		try (Connection conn = DbUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql))
		{
			pstmt.setString(1, client.getFirstName());
			pstmt.setString(2, client.getLastName());
			pstmt.setString(3, client.getClientCode());
			pstmt.setInt(4, JavaUtils.convertBooleanToBit(client.isInactive()));
			pstmt.setString(5, client.getEmail1());
			pstmt.setString(6, client.getEmail2());
			pstmt.setString(7, client.getEmail3());
			pstmt.setString(8, client.getPhone1());
			pstmt.setString(9, client.getPhone2());
			pstmt.setString(10, client.getPhone3());
			pstmt.setString(11, DateFormatUtil.toSqliteString(client.getDateOfBirth()));
			if (client.getClientId() != null)
				pstmt.setInt(12, client.getClientId());
			
			pstmt.executeUpdate();
		}
	}
	
	/**
	 * Searches for clients by partial name and/or code (e.g., "Smi" for "Smith")
	 * 
	 * @return A list of matching active clients.
	 * @throws SQLException, TherapyAppException 
	 */
	public List<Client> findClients(String firstName, String lastName, String clientCode, boolean includeInactive) throws SQLException, TherapyAppException
	{
		List<Client> clients = new ArrayList<>();
		String sql = "SELECT * FROM clients WHERE (? IS NULL OR UPPER(first_name) LIKE UPPER(?)) AND (? IS NULL OR UPPER(last_name) LIKE UPPER(?))"
				+ " AND (? IS NULL OR UPPER(client_code) LIKE UPPER(?)) AND (? IS NULL OR inactive <> ?)";
		
		try (Connection conn = DbUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql))
		{
			if (JavaUtils.isNullOrEmpty(firstName))
			{
				pstmt.setNull(1, Types.VARCHAR);
				pstmt.setNull(2, Types.VARCHAR);
			}
			else
			{
				pstmt.setString(1, firstName);
				pstmt.setString(2, "%" + firstName + "%");
			}
			
			if (JavaUtils.isNullOrEmpty(lastName))
			{
				pstmt.setNull(3, Types.VARCHAR);
				pstmt.setNull(4, Types.VARCHAR);
			}
			else
			{
				pstmt.setString(3, lastName);
				pstmt.setString(4, "%" + lastName + "%");
			}
			
			if (JavaUtils.isNullOrEmpty(clientCode))
			{
				pstmt.setNull(5, Types.VARCHAR);
				pstmt.setNull(6, Types.VARCHAR);
			}
			else
			{
				pstmt.setString(5, clientCode);
				pstmt.setString(6, "%" + clientCode + "%");
			}
			
			if(includeInactive)
			{
				pstmt.setNull(7, Types.INTEGER);
				pstmt.setNull(8, Types.INTEGER);
			}
			else
			{
				pstmt.setInt(7, 1);
				pstmt.setInt(8, 1);
			}
			
			try (ResultSet rs = pstmt.executeQuery())
			{
				while (rs.next())
				{
					clients.add(_populateClient(rs));
				}
			}
		}
		return clients;
	}
	
	public List<Client> getAllClients(boolean includeInactive) throws SQLException, TherapyAppException
	{
		List<Client> results = new ArrayList<>();
		String sql = "SELECT * FROM clients";
		
		if (!includeInactive)
			sql = sql + " WHERE inactive = 0";
		
		try (Connection conn = DbUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql))
		{
			try (ResultSet rs = pstmt.executeQuery())
			{
				while (rs.next())
				{
					Client c = _populateClient(rs);
					results.add(c);
				}
			}
		}
		return results;
	}
	
	public Client getClientById(int clientId) throws SQLException, TherapyAppException
	{
		String sql = "SELECT * FROM clients WHERE client_id = ?";
		
		try (Connection conn = DbUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql))
		{
			pstmt.setInt(1, clientId);
			
			try (ResultSet rs = pstmt.executeQuery())
			{
				while (rs.next())
				{
					return _populateClient(rs);
				}
			}
		}
		return null;
	}
	
	/**
	 * Creates, populates, and returns a Client object based on query results of SELECT * FROM clients
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 * @throws TherapyAppException
	 */
	private Client _populateClient(ResultSet rs) throws SQLException, TherapyAppException
	{
		Client c = new Client();
		c.setClientId(rs.getInt("client_id"));
		c.setFirstName(rs.getString("first_name"));
		c.setLastName(rs.getString("last_name"));
		c.setClientCode(rs.getString("client_code"));
		c.setEmail1(rs.getString("email1"));
		c.setEmail2(rs.getString("email2"));
		c.setEmail3(rs.getString("email3"));
		c.setPhone1(rs.getString("phone1"));
		c.setPhone2(rs.getString("phone2"));
		c.setPhone3(rs.getString("phone3"));
		c.setDateOfBirth(DateFormatUtil.toDate(DateFormatUtil.toLocalDateTime(rs.getString("date_of_birth"))));
		c.setInactive(JavaUtils.convertBitToBoolean(rs.getInt("inactive")));
		return c;
	}
}
