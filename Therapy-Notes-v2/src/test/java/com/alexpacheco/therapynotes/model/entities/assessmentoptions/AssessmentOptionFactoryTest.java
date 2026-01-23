package com.alexpacheco.therapynotes.model.entities.assessmentoptions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;

/**
 * Unit tests for {@link AssessmentOptionFactory}.
 */
@DisplayName( "AssessmentOptionFactory" )
class AssessmentOptionFactoryTest
{
	private static final int TEST_ID = 42;
	private static final String TEST_NAME = "Test Option";
	private static final String TEST_DESCRIPTION = "Test description";
	
	/**
	 * Provides mapping of AssessmentOptionType to expected concrete class.
	 */
	private static Stream<Arguments> typeToClassMapping()
	{
		return Stream.of( Arguments.of( AssessmentOptionType.SYMPTOMS, SymptomAssessmentOption.class ),
				Arguments.of( AssessmentOptionType.APPEARANCE, AppearanceAssessmentOption.class ),
				Arguments.of( AssessmentOptionType.SPEECH, SpeechAssessmentOption.class ),
				Arguments.of( AssessmentOptionType.AFFECT, AffectAssessmentOption.class ),
				Arguments.of( AssessmentOptionType.EYE_CONTACT, EyeContactAssessmentOption.class ),
				Arguments.of( AssessmentOptionType.REFERRALS, ReferralAssessmentOption.class ),
				Arguments.of( AssessmentOptionType.COLL_CONTACTS, CollateralContactAssessmentOption.class ),
				Arguments.of( AssessmentOptionType.NEXT_APPT, NextApptAssessmentOption.class ) );
	}
	
	@Nested
	@DisplayName( "createAssessmentOption(int, String, String, AssessmentOptionType)" )
	class CreateWithIdAndType
	{
		@Test
		@DisplayName( "Returns null when type is null" )
		void returnsNullForNullType()
		{
			AssessmentOption result = AssessmentOptionFactory.createAssessmentOption( TEST_ID, TEST_NAME, TEST_DESCRIPTION,
					(AssessmentOptionType) null );
			
			assertNull( result );
		}
		
		@ParameterizedTest( name = "{0} creates {1}" )
		@MethodSource( "com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOptionFactoryTest#typeToClassMapping" )
		@DisplayName( "Creates correct subclass for each type" )
		void createsCorrectSubclass( AssessmentOptionType type, Class<? extends AssessmentOption> expectedClass )
		{
			AssessmentOption result = AssessmentOptionFactory.createAssessmentOption( TEST_ID, TEST_NAME, TEST_DESCRIPTION, type );
			
			assertNotNull( result );
			assertInstanceOf( expectedClass, result );
		}
		
		@ParameterizedTest( name = "{0} sets properties correctly" )
		@EnumSource( AssessmentOptionType.class )
		@DisplayName( "Sets ID, name, and description correctly" )
		void setsPropertiesCorrectly( AssessmentOptionType type )
		{
			AssessmentOption result = AssessmentOptionFactory.createAssessmentOption( TEST_ID, TEST_NAME, TEST_DESCRIPTION, type );
			
			assertNotNull( result );
			assertEquals( TEST_ID, result.getId() );
			assertEquals( TEST_NAME, result.getName() );
			assertEquals( TEST_DESCRIPTION, result.getDescription() );
		}
		
		@Test
		@DisplayName( "Works with zero ID" )
		void worksWithZeroId()
		{
			AssessmentOption result = AssessmentOptionFactory.createAssessmentOption( 0, TEST_NAME, TEST_DESCRIPTION,
					AssessmentOptionType.SYMPTOMS );
			
			assertNotNull( result );
			assertEquals( 0, result.getId() );
		}
		
		@Test
		@DisplayName( "Works with negative ID" )
		void worksWithNegativeId()
		{
			AssessmentOption result = AssessmentOptionFactory.createAssessmentOption( -1, TEST_NAME, TEST_DESCRIPTION,
					AssessmentOptionType.SYMPTOMS );
			
			assertNotNull( result );
			assertEquals( -1, result.getId() );
		}
		
		@Test
		@DisplayName( "Works with null name" )
		void worksWithNullName()
		{
			AssessmentOption result = AssessmentOptionFactory.createAssessmentOption( TEST_ID, null, TEST_DESCRIPTION,
					AssessmentOptionType.SYMPTOMS );
			
			assertNotNull( result );
			assertNull( result.getName() );
		}
		
		@Test
		@DisplayName( "Works with null description" )
		void worksWithNullDescription()
		{
			AssessmentOption result = AssessmentOptionFactory.createAssessmentOption( TEST_ID, TEST_NAME, null,
					AssessmentOptionType.SYMPTOMS );
			
			assertNotNull( result );
			assertNull( result.getDescription() );
		}
		
		@Test
		@DisplayName( "Works with empty strings" )
		void worksWithEmptyStrings()
		{
			AssessmentOption result = AssessmentOptionFactory.createAssessmentOption( TEST_ID, "", "", AssessmentOptionType.SYMPTOMS );
			
			assertNotNull( result );
			assertEquals( "", result.getName() );
			assertEquals( "", result.getDescription() );
		}
	}
	
	@Nested
	@DisplayName( "createAssessmentOption(String, String, AssessmentOptionType)" )
	class CreateWithoutId
	{
		@Test
		@DisplayName( "Returns null when type is null" )
		void returnsNullForNullType()
		{
			AssessmentOption result = AssessmentOptionFactory.createAssessmentOption( TEST_NAME, TEST_DESCRIPTION,
					(AssessmentOptionType) null );
			
			assertNull( result );
		}
		
		@ParameterizedTest( name = "{0} creates {1}" )
		@MethodSource( "com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOptionFactoryTest#typeToClassMapping" )
		@DisplayName( "Creates correct subclass for each type" )
		void createsCorrectSubclass( AssessmentOptionType type, Class<? extends AssessmentOption> expectedClass )
		{
			AssessmentOption result = AssessmentOptionFactory.createAssessmentOption( TEST_NAME, TEST_DESCRIPTION, type );
			
			assertNotNull( result );
			assertInstanceOf( expectedClass, result );
		}
		
		@ParameterizedTest( name = "{0} sets name and description correctly" )
		@EnumSource( AssessmentOptionType.class )
		@DisplayName( "Sets name and description correctly" )
		void setsPropertiesCorrectly( AssessmentOptionType type )
		{
			AssessmentOption result = AssessmentOptionFactory.createAssessmentOption( TEST_NAME, TEST_DESCRIPTION, type );
			
			assertNotNull( result );
			assertEquals( TEST_NAME, result.getName() );
			assertEquals( TEST_DESCRIPTION, result.getDescription() );
		}
		
		@ParameterizedTest( name = "{0} has null ID" )
		@EnumSource( AssessmentOptionType.class )
		@DisplayName( "Created option has null ID" )
		void hasNullId( AssessmentOptionType type )
		{
			AssessmentOption result = AssessmentOptionFactory.createAssessmentOption( TEST_NAME, TEST_DESCRIPTION, type );
			
			assertNotNull( result );
			assertNull( result.getId() );
		}
		
		@Test
		@DisplayName( "Works with null name" )
		void worksWithNullName()
		{
			AssessmentOption result = AssessmentOptionFactory.createAssessmentOption( null, TEST_DESCRIPTION,
					AssessmentOptionType.APPEARANCE );
			
			assertNotNull( result );
			assertNull( result.getName() );
		}
		
		@Test
		@DisplayName( "Works with null description" )
		void worksWithNullDescription()
		{
			AssessmentOption result = AssessmentOptionFactory.createAssessmentOption( TEST_NAME, null, AssessmentOptionType.APPEARANCE );
			
			assertNotNull( result );
			assertNull( result.getDescription() );
		}
	}
	
	@Nested
	@DisplayName( "createAssessmentOption(int, String, String, String dbTypeKey)" )
	class CreateWithDbTypeKey
	{
		@Test
		@DisplayName( "Returns null when dbTypeKey is null" )
		void returnsNullForNullDbTypeKey()
		{
			AssessmentOption result = AssessmentOptionFactory.createAssessmentOption( TEST_ID, TEST_NAME, TEST_DESCRIPTION, (String) null );
			
			assertNull( result );
		}
		
		@Test
		@DisplayName( "Returns null for invalid dbTypeKey" )
		void returnsNullForInvalidDbTypeKey()
		{
			AssessmentOption result = AssessmentOptionFactory.createAssessmentOption( TEST_ID, TEST_NAME, TEST_DESCRIPTION, "INVALID_KEY" );
			
			assertNull( result );
		}
		
		@Test
		@DisplayName( "Returns null for empty dbTypeKey" )
		void returnsNullForEmptyDbTypeKey()
		{
			AssessmentOption result = AssessmentOptionFactory.createAssessmentOption( TEST_ID, TEST_NAME, TEST_DESCRIPTION, "" );
			
			assertNull( result );
		}
		
		@ParameterizedTest( name = "dbTypeKey ''{0}'' creates {1}" )
		@MethodSource( "dbTypeKeyToClassMapping" )
		@DisplayName( "Creates correct subclass for each dbTypeKey" )
		void createsCorrectSubclassFromDbTypeKey( String dbTypeKey, Class<? extends AssessmentOption> expectedClass )
		{
			AssessmentOption result = AssessmentOptionFactory.createAssessmentOption( TEST_ID, TEST_NAME, TEST_DESCRIPTION, dbTypeKey );
			
			assertNotNull( result, "Expected non-null result for dbTypeKey: " + dbTypeKey );
			assertInstanceOf( expectedClass, result );
		}
		
		@ParameterizedTest( name = "dbTypeKey ''{0}'' sets properties correctly" )
		@MethodSource( "validDbTypeKeys" )
		@DisplayName( "Sets ID, name, and description correctly" )
		void setsPropertiesCorrectly( String dbTypeKey )
		{
			AssessmentOption result = AssessmentOptionFactory.createAssessmentOption( TEST_ID, TEST_NAME, TEST_DESCRIPTION, dbTypeKey );
			
			// Skip if dbTypeKey is not recognized
			if( result != null )
			{
				assertEquals( TEST_ID, result.getId() );
				assertEquals( TEST_NAME, result.getName() );
				assertEquals( TEST_DESCRIPTION, result.getDescription() );
			}
		}
		
		/**
		 * Provides mapping of dbTypeKey strings to expected concrete classes. Adjust these keys to match your
		 * AssessmentOptionType.getByDbTypeKey() implementation.
		 */
		private static Stream<Arguments> dbTypeKeyToClassMapping()
		{
			return Stream.of( Arguments.of( AssessmentOptionType.SYMPTOMS.getDbTypeKey(), SymptomAssessmentOption.class ),
					Arguments.of( AssessmentOptionType.APPEARANCE.getDbTypeKey(), AppearanceAssessmentOption.class ),
					Arguments.of( AssessmentOptionType.SPEECH.getDbTypeKey(), SpeechAssessmentOption.class ),
					Arguments.of( AssessmentOptionType.AFFECT.getDbTypeKey(), AffectAssessmentOption.class ),
					Arguments.of( AssessmentOptionType.EYE_CONTACT.getDbTypeKey(), EyeContactAssessmentOption.class ),
					Arguments.of( AssessmentOptionType.REFERRALS.getDbTypeKey(), ReferralAssessmentOption.class ),
					Arguments.of( AssessmentOptionType.COLL_CONTACTS.getDbTypeKey(), CollateralContactAssessmentOption.class ),
					Arguments.of( AssessmentOptionType.NEXT_APPT.getDbTypeKey(), NextApptAssessmentOption.class ) );
		}
		
		/**
		 * Provides all valid dbTypeKey values.
		 */
		private static Stream<String> validDbTypeKeys()
		{
			return Stream.of( AssessmentOptionType.values() ).map( AssessmentOptionType::getDbTypeKey );
		}
	}
	
	@Nested
	@DisplayName( "Type coverage verification" )
	class TypeCoverage
	{
		@Test
		@DisplayName( "All AssessmentOptionType values are handled in factory (with ID)" )
		void allTypesHandledWithId()
		{
			for( AssessmentOptionType type : AssessmentOptionType.values() )
			{
				AssessmentOption result = AssessmentOptionFactory.createAssessmentOption( TEST_ID, TEST_NAME, TEST_DESCRIPTION, type );
				
				assertNotNull( result, "Factory should handle type: " + type );
			}
		}
		
		@Test
		@DisplayName( "All AssessmentOptionType values are handled in factory (without ID)" )
		void allTypesHandledWithoutId()
		{
			for( AssessmentOptionType type : AssessmentOptionType.values() )
			{
				AssessmentOption result = AssessmentOptionFactory.createAssessmentOption( TEST_NAME, TEST_DESCRIPTION, type );
				
				assertNotNull( result, "Factory should handle type: " + type );
			}
		}
		
		@Test
		@DisplayName( "All dbTypeKeys are handled in factory" )
		void allDbTypeKeysHandled()
		{
			for( AssessmentOptionType type : AssessmentOptionType.values() )
			{
				String dbTypeKey = type.getDbTypeKey();
				AssessmentOption result = AssessmentOptionFactory.createAssessmentOption( TEST_ID, TEST_NAME, TEST_DESCRIPTION, dbTypeKey );
				
				assertNotNull( result, "Factory should handle dbTypeKey: " + dbTypeKey );
			}
		}
	}
	
	@Nested
	@DisplayName( "Concrete type verification" )
	class ConcreteTypeVerification
	{
		@Test
		@DisplayName( "SYMPTOMS creates SymptomAssessmentOption" )
		void symptomsCreatesCorrectType()
		{
			AssessmentOption result = AssessmentOptionFactory.createAssessmentOption( TEST_ID, TEST_NAME, TEST_DESCRIPTION,
					AssessmentOptionType.SYMPTOMS );
			
			assertInstanceOf( SymptomAssessmentOption.class, result );
		}
		
		@Test
		@DisplayName( "APPEARANCE creates AppearanceAssessmentOption" )
		void appearanceCreatesCorrectType()
		{
			AssessmentOption result = AssessmentOptionFactory.createAssessmentOption( TEST_ID, TEST_NAME, TEST_DESCRIPTION,
					AssessmentOptionType.APPEARANCE );
			
			assertInstanceOf( AppearanceAssessmentOption.class, result );
		}
		
		@Test
		@DisplayName( "SPEECH creates SpeechAssessmentOption" )
		void speechCreatesCorrectType()
		{
			AssessmentOption result = AssessmentOptionFactory.createAssessmentOption( TEST_ID, TEST_NAME, TEST_DESCRIPTION,
					AssessmentOptionType.SPEECH );
			
			assertInstanceOf( SpeechAssessmentOption.class, result );
		}
		
		@Test
		@DisplayName( "AFFECT creates AffectAssessmentOption" )
		void affectCreatesCorrectType()
		{
			AssessmentOption result = AssessmentOptionFactory.createAssessmentOption( TEST_ID, TEST_NAME, TEST_DESCRIPTION,
					AssessmentOptionType.AFFECT );
			
			assertInstanceOf( AffectAssessmentOption.class, result );
		}
		
		@Test
		@DisplayName( "EYE_CONTACT creates EyeContactAssessmentOption" )
		void eyeContactCreatesCorrectType()
		{
			AssessmentOption result = AssessmentOptionFactory.createAssessmentOption( TEST_ID, TEST_NAME, TEST_DESCRIPTION,
					AssessmentOptionType.EYE_CONTACT );
			
			assertInstanceOf( EyeContactAssessmentOption.class, result );
		}
		
		@Test
		@DisplayName( "REFERRALS creates ReferralAssessmentOption" )
		void referralsCreatesCorrectType()
		{
			AssessmentOption result = AssessmentOptionFactory.createAssessmentOption( TEST_ID, TEST_NAME, TEST_DESCRIPTION,
					AssessmentOptionType.REFERRALS );
			
			assertInstanceOf( ReferralAssessmentOption.class, result );
		}
		
		@Test
		@DisplayName( "COLL_CONTACTS creates CollateralContactAssessmentOption" )
		void collContactsCreatesCorrectType()
		{
			AssessmentOption result = AssessmentOptionFactory.createAssessmentOption( TEST_ID, TEST_NAME, TEST_DESCRIPTION,
					AssessmentOptionType.COLL_CONTACTS );
			
			assertInstanceOf( CollateralContactAssessmentOption.class, result );
		}
		
		@Test
		@DisplayName( "NEXT_APPT creates NextApptAssessmentOption" )
		void nextApptCreatesCorrectType()
		{
			AssessmentOption result = AssessmentOptionFactory.createAssessmentOption( TEST_ID, TEST_NAME, TEST_DESCRIPTION,
					AssessmentOptionType.NEXT_APPT );
			
			assertInstanceOf( NextApptAssessmentOption.class, result );
		}
	}
}