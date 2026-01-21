package com.alexpacheco.therapynotes;

import org.junit.jupiter.api.*;

import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName( "Therapy App DB Tests" )
@TestInstance( TestInstance.Lifecycle.PER_CLASS )
@TestMethodOrder( MethodOrderer.OrderAnnotation.class ) // Use @TestMethodOrder because database tests often rely on a clean state.
public class SchemaTest extends BaseDatabaseTest
{
	@Test
	@Order( 1 )
	@DisplayName( "1. Unique Constraint: Prevent Duplicate Client Codes" )
	void testUniqueConstraint() throws SQLException
	{
		try( Statement stmt = conn.createStatement() )
		{
			stmt.execute( "INSERT INTO clients (client_id, client_code) VALUES (100, 'UNIQUE_CODE')" );
			
			String duplicateSql = "INSERT INTO clients (client_id, client_code) VALUES (101, 'UNIQUE_CODE')";
			
			assertThrows( SQLException.class, () -> stmt.execute( duplicateSql ),
					"Should throw SQLException due to UNIQUE constraint violation on client_code." );
		}
	}
	
	@Test
	@Order( 2 )
	@DisplayName( "2. Boolean Logic: Verify BIT to Integer mapping" )
	void testBooleanMapping() throws SQLException
	{
		try( Statement stmt = conn.createStatement() )
		{
			// Insert with inactive = 1 (True)
			stmt.execute( "INSERT INTO clients (client_id, client_code, first_name, inactive) VALUES (200, 'IU2026', 'InactiveUser', 1)" );
			
			ResultSet rs = stmt.executeQuery( "SELECT inactive FROM clients WHERE client_id = 200" );
			rs.next();
			
			int inactiveValue = rs.getInt( "inactive" );
			assertEquals( 1, inactiveValue, "The 'BIT' equivalent should store and return 1." );
			
			// Verify we can query using standard boolean logic
			ResultSet rsFiltered = stmt.executeQuery( "SELECT client_code, first_name FROM clients WHERE inactive = 1" );
			assertTrue( rsFiltered.next() );
			assertEquals( "IU2026", rsFiltered.getString( "client_code" ) );
			assertEquals( "InactiveUser", rsFiltered.getString( "first_name" ) );
		}
	}
	
	@Test
	@Order( 3 )
	@DisplayName( "3. Trigger: Update 'update_date' on Clients" )
	void testUpdateDateTrigger() throws SQLException, InterruptedException
	{
		Statement stmt = conn.createStatement();
		
		// 1. Insert a client
		stmt.execute( "INSERT INTO clients (client_code, first_name, last_name) VALUES ('JD2026', 'Jane', 'Doe')" );
		
		// 2. Fetch the initial update_date
		ResultSet rs = stmt.executeQuery( "SELECT update_date FROM clients WHERE first_name = 'Jane'" );
		rs.next();
		String initialDate = rs.getString( "update_date" );
		
		// 3. Wait 1 second to ensure the timestamp will be different
		Thread.sleep( 1000 );
		
		// 4. Update the client
		stmt.execute( "UPDATE clients SET last_name = 'Smith' WHERE first_name = 'Jane'" );
		
		// 5. Fetch the new update_date
		ResultSet rs2 = stmt.executeQuery( "SELECT update_date FROM clients WHERE first_name = 'Jane'" );
		rs2.next();
		String updatedDate = rs2.getString( "update_date" );
		
		// 6. Assertions
		assertNotNull( initialDate );
		assertNotNull( updatedDate );
		assertNotEquals( initialDate, updatedDate, "The update_date should have changed via the trigger." );
	}
	
	@Test
	@Order( 4 )
	@DisplayName( "4. Trigger: Update 'update_date' on Contacts" )
	void testContactsUpdateTrigger() throws SQLException, InterruptedException
	{
		try( Statement stmt = conn.createStatement() )
		{
			stmt.execute( "INSERT INTO contacts (first_name) VALUES ('Emergency')" );
			
			ResultSet rs = stmt.executeQuery( "SELECT contact_id, update_date FROM contacts LIMIT 1" );
			rs.next();
			int id = rs.getInt( 1 );
			String initialDate = rs.getString( 2 );
			
			Thread.sleep( 1000 );
			
			stmt.execute( "UPDATE contacts SET last_name = 'Contact' WHERE contact_id = " + id );
			
			ResultSet rs2 = stmt.executeQuery( "SELECT update_date FROM contacts WHERE contact_id = " + id );
			rs2.next();
			assertNotEquals( initialDate, rs2.getString( 1 ), "Contacts update_date did not change." );
		}
	}
}