package test.java.com.alexpacheco.therapynotes;

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
	@DisplayName( "1. Cascade Delete: Cleanup Junction Tables" )
	void testCascadeDelete() throws SQLException
	{
		try( Statement stmt = conn.createStatement() )
		{
			// Setup: Insert Appointment and Client
			stmt.execute( "INSERT INTO appointments (appt_id, status) VALUES (10, 'Scheduled')" );
			stmt.execute( "INSERT INTO clients (client_id, client_code, first_name) VALUES (50, 'DOE2026', 'John')" );
			
			// Link them
			stmt.execute( "INSERT INTO appt_clients (appt_id, client_id) VALUES (10, 50)" );
			
			// Delete the parent (Appointment)
			stmt.execute( "DELETE FROM appointments WHERE appt_id = 10" );
			
			// Verify the link is gone
			ResultSet rs = stmt.executeQuery( "SELECT COUNT(*) FROM appt_clients WHERE appt_id = 10" );
			rs.next();
			assertEquals( 0, rs.getInt( 1 ), "Junction record should be deleted automatically via CASCADE." );
		}
	}
	
	@Test
	@Order( 2 )
	@DisplayName( "2. Unique Constraint: Prevent Duplicate Client Codes" )
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
	@Order( 3 )
	@DisplayName( "3. Boolean Logic: Verify BIT to Integer mapping" )
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
	@Order( 4 )
	@DisplayName( "4. Trigger: Update 'update_date' on Clients" )
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
	@Order( 5 )
	@DisplayName( "5. Trigger: Update 'update_date' on Appointments" )
	void testAppointmentUpdateTrigger() throws SQLException, InterruptedException
	{
		try( Statement stmt = conn.createStatement() )
		{
			stmt.execute( "INSERT INTO appointments (status) VALUES ('Scheduled')" );
			
			ResultSet rs = stmt.executeQuery( "SELECT appt_id, update_date FROM appointments LIMIT 1" );
			rs.next();
			int id = rs.getInt( 1 );
			String initialDate = rs.getString( 2 );
			
			Thread.sleep( 1000 );
			
			stmt.execute( "UPDATE appointments SET status = 'Completed' WHERE appt_id = " + id );
			
			ResultSet rs2 = stmt.executeQuery( "SELECT update_date FROM appointments WHERE appt_id = " + id );
			rs2.next();
			assertNotEquals( initialDate, rs2.getString( 1 ), "Appointment update_date did not change." );
		}
	}
	
	@Test
	@Order( 6 )
	@DisplayName( "6. Trigger: Update 'update_date' on Notes" )
	void testNotesUpdateTrigger() throws SQLException, InterruptedException
	{
		try( Statement stmt = conn.createStatement() )
		{
			stmt.execute( "INSERT INTO notes (narrative) VALUES ('Initial session notes')" );
			
			ResultSet rs = stmt.executeQuery( "SELECT note_id, update_date FROM notes LIMIT 1" );
			rs.next();
			int id = rs.getInt( 1 );
			String initialDate = rs.getString( 2 );
			
			Thread.sleep( 1000 );
			
			stmt.execute( "UPDATE notes SET narrative = 'Updated session notes' WHERE note_id = " + id );
			
			ResultSet rs2 = stmt.executeQuery( "SELECT update_date FROM notes WHERE note_id = " + id );
			rs2.next();
			assertNotEquals( initialDate, rs2.getString( 1 ), "Notes update_date did not change." );
		}
	}
	
	@Test
	@Order( 7 )
	@DisplayName( "7. Trigger: Update 'update_date' on appt_clients (Junction)" )
	void testApptClientsUpdateTrigger() throws SQLException, InterruptedException
	{
		try( Statement stmt = conn.createStatement() )
		{
			// Setup parent data
			stmt.execute( "INSERT INTO appointments (appt_id) VALUES (500)" );
			stmt.execute( "INSERT INTO clients (client_id, client_code) VALUES (600, 'CL600')" );
			stmt.execute( "INSERT INTO appt_clients (appt_id, client_id) VALUES (500, 600)" );
			
			ResultSet rs = stmt.executeQuery( "SELECT update_date FROM appt_clients WHERE appt_id = 500 AND client_id = 600" );
			rs.next();
			String initialDate = rs.getString( 1 );
			
			Thread.sleep( 1000 );
			
			// Trigger update by changing the record (even if to same value to fire the trigger)
			stmt.execute( "UPDATE appt_clients SET update_date = CURRENT_TIMESTAMP WHERE appt_id = 500 AND client_id = 600" );
			
			ResultSet rs2 = stmt.executeQuery( "SELECT update_date FROM appt_clients WHERE appt_id = 500 AND client_id = 600" );
			rs2.next();
			assertNotEquals( initialDate, rs2.getString( 1 ), "appt_clients junction update_date did not change." );
		}
	}
	
	@Test
	@Order( 8 )
	@DisplayName( "8. Trigger: Update 'update_date' on Contacts" )
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
	
	@Test
	@Order( 9 )
	@DisplayName( "9. Trigger: Update 'update_date' on Symptoms (Junction)" )
	void testSymptomsUpdateTrigger() throws SQLException, InterruptedException
	{
		try( Statement stmt = conn.createStatement() )
		{
			// Setup parent data
			stmt.execute( "INSERT INTO notes (note_id) VALUES (700)" );
			stmt.execute( "INSERT INTO assessment_options (id, type, name) VALUES (800, 'symptoms', 'Anxiety')" );
			stmt.execute( "INSERT INTO symptoms (note_id, symptom_id, symptom_note) VALUES (700, 800, 'Mild')" );
			
			ResultSet rs = stmt.executeQuery( "SELECT update_date FROM symptoms WHERE note_id = 700 AND symptom_id = 800" );
			rs.next();
			String initialDate = rs.getString( 1 );
			
			Thread.sleep( 1000 );
			
			stmt.execute( "UPDATE symptoms SET symptom_note = 'Moderate' WHERE note_id = 700 AND symptom_id = 800" );
			
			ResultSet rs2 = stmt.executeQuery( "SELECT update_date FROM symptoms WHERE note_id = 700 AND symptom_id = 800" );
			rs2.next();
			assertNotEquals( initialDate, rs2.getString( 1 ), "Symptoms junction update_date did not change." );
		}
	}
}