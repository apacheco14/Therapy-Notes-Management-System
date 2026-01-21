package com.alexpacheco.therapynotes.model.api;

import java.sql.SQLException;
import java.util.List;

import com.alexpacheco.therapynotes.controller.AppController;
import com.alexpacheco.therapynotes.controller.enums.ErrorCode;
import com.alexpacheco.therapynotes.controller.errorhandling.exceptions.ResourceConflictException;
import com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.EntityValidator;
import com.alexpacheco.therapynotes.model.dao.ClientsDao;
import com.alexpacheco.therapynotes.model.entities.Client;

public class ClientApi
{
	private final ClientsDao clientDao = new ClientsDao();
	
	/**
	 * Creates a new client and validates the unique client_code.
	 */
	public void createClient(Client client) throws TherapyAppException
	{
		EntityValidator.validateClient(client);
		
		try
		{
			// Check if code already exists to provide a better error than a SQL crash
			if (clientDao.existsByCode(client.getClientCode()))
			{
				throw new ResourceConflictException("Client code '" + client.getClientCode() + "' is already in use.");
			}
			
			clientDao.saveNew(client);
		}
		catch (SQLException e)
		{
			AppController.logException("ClientApi", e);
			// SQLite specific error code for unique constraint is 19
			if (e.getErrorCode() == 19 || e.getMessage().contains("UNIQUE"))
			{
				throw new ResourceConflictException(
						"The client code '" + client.getClientCode() + "' is already assigned to another patient.");
			}
			
			// Fallback for other database errors
			throw new TherapyAppException("An internal database error occurred.", ErrorCode.DB_ERROR);
		}
	}
	
	/**
	 * Edits an existing client and validates the unique client_code.
	 */
	public void updateClient(Client client) throws TherapyAppException
	{
		EntityValidator.validateClient(client);
		
		try
		{
			clientDao.saveExisting(client);
		}
		catch (SQLException e)
		{
			AppController.logException("ClientApi", e);
			throw new TherapyAppException("An internal database error occurred.", ErrorCode.DB_ERROR);
		}
	}
	
	/**
	 * Searches for Clients based on any one or a combination of columns.
	 */
	public List<Client> findClients(String firstName, String lastName, String clientCode, boolean includeInactive)
			throws TherapyAppException
	{
		try
		{
			return clientDao.findClients(firstName, lastName, clientCode, includeInactive);
		}
		catch (SQLException e)
		{
			AppController.logException("ClientApi", e);
			throw new TherapyAppException("Error occurred while searching for clients.", ErrorCode.DB_ERROR);
		}
	}
	
	public List<Client> getAllClients(boolean includeInactive) throws TherapyAppException
	{
		try
		{
			return clientDao.getAllClients(includeInactive);
		}
		catch (SQLException e)
		{
			AppController.logException("ClientApi", e);
			throw new TherapyAppException("Error occurred while searching for clients.", ErrorCode.DB_ERROR);
		}
	}
	
	public Client getClient(int clientId) throws TherapyAppException
	{
		try
		{
			return clientDao.getClientById(clientId);
		}
		catch (SQLException e)
		{
			AppController.logException("ClientApi", e);
			throw new TherapyAppException("Error occurred while searching for clients.", ErrorCode.DB_ERROR);
		}
	}
}