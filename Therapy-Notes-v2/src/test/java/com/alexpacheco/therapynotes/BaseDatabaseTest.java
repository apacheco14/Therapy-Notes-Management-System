package test.java.com.alexpacheco.therapynotes;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import main.java.com.alexpacheco.therapynotes.util.DbUtil;

public abstract class BaseDatabaseTest
{
	protected static Connection conn;
	protected static final String DB_URL = "jdbc:sqlite::memory:"; // Use in-memory for testing
	private static final String SCHEMA_SCRIPT_FILE = "src/main/resources/schema-config.sql";
	private static final String TRIGGER_SCRIPT_FILE = "src/main/resources/trigger-config.sql";
	private static final String OPTIONS_SCRIPT_FILE = "src/main/resources/populate-option-tables.sql";
	
	@BeforeAll
	static void init() throws SQLException
	{
		conn = DbUtil.getConnection( DB_URL );
		DbUtil.executeSqlScript( conn, new File( SCHEMA_SCRIPT_FILE ) );
		DbUtil.executeTriggerScript( conn, new File( TRIGGER_SCRIPT_FILE ) );
		DbUtil.executeSqlScript( conn, new File( OPTIONS_SCRIPT_FILE ) );
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