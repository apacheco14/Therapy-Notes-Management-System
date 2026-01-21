package com.alexpacheco.therapynotes.model.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.alexpacheco.therapynotes.BaseDatabaseTest;
import com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOption;
import com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOptionFactory;
import com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOptionType;

class AssessmentOptionDaoTest extends BaseDatabaseTest
{
	private AssessmentOptionsDao dao;
	
	@BeforeEach
	void setUp()
	{
		dao = new AssessmentOptionsDao();
	}
	
	@AfterEach
	void cleanUp() throws SQLException, InterruptedException
	{
		// Clean up test data after each test
		try( Statement stmt = conn.createStatement() )
		{
			stmt.execute( "DELETE FROM assessment_options WHERE name LIKE 'Test%'" );
		}
	}
	
	@Test
	void testCreateOptionsBatch_rollbackOnError() throws SQLException
	{
		// Arrange
		List<AssessmentOption> options = new ArrayList<>();
		options.add( AssessmentOptionFactory.createAssessmentOption( "Test Valid Option", "Valid", AssessmentOptionType.APPEARANCE ) );
		
		// Create an option that will fail (null type will cause factory to return null)
		AssessmentOption invalidOption = AssessmentOptionFactory.createAssessmentOption( "Test Invalid", "Invalid", null );
		options.add( invalidOption );
		
		// Act & Assert
		assertThrows( Exception.class, () -> dao.createOptionsBatch( options ) );
		
		// Verify rollback - no options should be inserted
		try( Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery( "SELECT COUNT(*) as count FROM assessment_options WHERE name = 'Test Valid Option'" ) )
		{
			assertTrue( rs.next() );
			assertEquals( 0, rs.getInt( "count" ), "Transaction should be rolled back, no options inserted" );
		}
	}
	
	@Test
	void testUpdateOption_notFound() throws SQLException
	{
		// Arrange - Create option with non-existent ID
		AssessmentOption option = AssessmentOptionFactory.createAssessmentOption( 99999, "Test Nonexistent", "Description",
				AssessmentOptionType.AFFECT );
		
		// Act & Assert
		SQLException exception = assertThrows( SQLException.class, () -> dao.updateOption( option ) );
		assertTrue( exception.getMessage().contains( "not found" ) );
	}
	
	@Test
	void testGetOptions_returnsAllOptions() throws SQLException
	{
		// Arrange - Insert test options
		dao.createOption(
				AssessmentOptionFactory.createAssessmentOption( "Test Symptom A", "Description A", AssessmentOptionType.SYMPTOMS ) );
		dao.createOption( AssessmentOptionFactory.createAssessmentOption( "Test Affect B", "Description B", AssessmentOptionType.AFFECT ) );
		
		// Act
		List<AssessmentOption> result = dao.getOptions();
		
		// Assert
		assertNotNull( result );
		assertTrue( result.size() >= 2, "Should have at least 2 test options plus any pre-populated options" );
		
		// Verify our test options are in the results
		boolean foundSymptom = result.stream().anyMatch( o -> "Test Symptom A".equals( o.getName() ) );
		boolean foundAffect = result.stream().anyMatch( o -> "Test Affect B".equals( o.getName() ) );
		assertTrue( foundSymptom, "Should contain Test Symptom A" );
		assertTrue( foundAffect, "Should contain Test Affect B" );
	}
	
	@Test
	void testGetOptionsByType_returnsFilteredOptions() throws SQLException
	{
		// Arrange
		dao.createOption( AssessmentOptionFactory.createAssessmentOption( "Test Speech 1", "Description 1", AssessmentOptionType.SPEECH ) );
		dao.createOption( AssessmentOptionFactory.createAssessmentOption( "Test Speech 2", "Description 2", AssessmentOptionType.SPEECH ) );
		dao.createOption( AssessmentOptionFactory.createAssessmentOption( "Test Affect 1", "Description 3", AssessmentOptionType.AFFECT ) );
		
		// Act
		List<AssessmentOption> speechOptions = dao.getOptions( AssessmentOptionType.SPEECH );
		
		// Assert
		assertNotNull( speechOptions );
		assertTrue( speechOptions.size() >= 2, "Should have at least 2 speech options" );
		
		// Verify all returned options are SPEECH type
		for( AssessmentOption option : speechOptions )
		{
			assertEquals( AssessmentOptionType.SPEECH, option.getOptionType() );
		}
		
		// Verify our test options are present
		boolean foundSpeech1 = speechOptions.stream().anyMatch( o -> "Test Speech 1".equals( o.getName() ) );
		boolean foundSpeech2 = speechOptions.stream().anyMatch( o -> "Test Speech 2".equals( o.getName() ) );
		assertTrue( foundSpeech1, "Should contain Test Speech 1" );
		assertTrue( foundSpeech2, "Should contain Test Speech 2" );
	}
	
	@Test
	void testGetOptions_returnsCorrectSubclasses() throws SQLException
	{
		// Arrange
		dao.createOption(
				AssessmentOptionFactory.createAssessmentOption( "Test Symptom", "Symptom description", AssessmentOptionType.SYMPTOMS ) );
		dao.createOption(
				AssessmentOptionFactory.createAssessmentOption( "Test Referral", "Referral description", AssessmentOptionType.REFERRALS ) );
		
		// Act
		List<AssessmentOption> allOptions = dao.getOptions();
		
		// Assert - Verify options are retrieved with correct types
		AssessmentOption symptomOption = allOptions.stream().filter( o -> "Test Symptom".equals( o.getName() ) ).findFirst().orElse( null );
		
		AssessmentOption referralOption = allOptions.stream().filter( o -> "Test Referral".equals( o.getName() ) ).findFirst()
				.orElse( null );
		
		assertNotNull( symptomOption );
		assertNotNull( referralOption );
		assertEquals( AssessmentOptionType.SYMPTOMS, symptomOption.getOptionType() );
		assertEquals( AssessmentOptionType.REFERRALS, referralOption.getOptionType() );
	}
}