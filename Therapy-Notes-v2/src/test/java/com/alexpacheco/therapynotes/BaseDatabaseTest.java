package com.alexpacheco.therapynotes;

import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import com.alexpacheco.therapynotes.controller.DatabaseInitializer;
import com.alexpacheco.therapynotes.util.DbUtil;

public abstract class BaseDatabaseTest
{
	protected static Connection conn;
	protected static final String DB_URL = "jdbc:sqlite::memory:"; // Use in-memory for testing
	private static final String SCHEMA_SCRIPT_FILE = "/main/resources/schema-config.sql";
	private static final String TRIGGER_SCRIPT_FILE = "/main/resources/trigger-config.sql";
	private static final String OPTIONS_SCRIPT_FILE = "/main/resources/populate-option-tables.sql";
	
	@BeforeAll
	static void init() throws SQLException
	{
		conn = DbUtil.getConnection( DB_URL );
		DbUtil.executeSqlScript( conn, DatabaseInitializer.class.getResourceAsStream( SCHEMA_SCRIPT_FILE ) );
		DbUtil.executeTriggerScript( conn, DatabaseInitializer.class.getResourceAsStream( TRIGGER_SCRIPT_FILE ) );
		DbUtil.executeSqlScript( conn, DatabaseInitializer.class.getResourceAsStream( OPTIONS_SCRIPT_FILE ) );
		System.out.println( "BaseTest: Database environment initialized." );
	}
	
	@AfterAll
	static void close() throws SQLException
	{
		if( conn != null && !conn.isClosed() )
		{
			conn.close();
		}
	}
}