package test.java.com.alexpacheco.therapynotes.model.entities.assessmentoptions;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import main.java.com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOption;
import main.java.com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOptionFactory;
import main.java.com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOptionType;
import main.java.com.alexpacheco.therapynotes.model.entities.assessmentoptions.SymptomAssessmentOption;
import main.java.com.alexpacheco.therapynotes.model.entities.assessmentoptions.AppearanceAssessmentOption;
import main.java.com.alexpacheco.therapynotes.model.entities.assessmentoptions.SpeechAssessmentOption;
import main.java.com.alexpacheco.therapynotes.model.entities.assessmentoptions.AffectAssessmentOption;
import main.java.com.alexpacheco.therapynotes.model.entities.assessmentoptions.EyeContactAssessmentOption;
import main.java.com.alexpacheco.therapynotes.model.entities.assessmentoptions.ReferralAssessmentOption;
import main.java.com.alexpacheco.therapynotes.model.entities.assessmentoptions.CollateralContactAssessmentOption;
import main.java.com.alexpacheco.therapynotes.model.entities.assessmentoptions.NextApptAssessmentOption;

class AssessmentOptionFactoryTest
{
	@Test
	void testCreateAssessmentOption_withoutId_symptoms()
	{
		// Act
		AssessmentOption option = AssessmentOptionFactory.createAssessmentOption("Anxiety", "Feeling anxious",
				AssessmentOptionType.SYMPTOMS);
		
		// Assert
		assertNotNull(option);
		assertInstanceOf(SymptomAssessmentOption.class, option);
		assertNull(option.getId());
		assertEquals("Anxiety", option.getName());
		assertEquals("Feeling anxious", option.getDescription());
		assertEquals(AssessmentOptionType.SYMPTOMS, option.getOptionType());
	}
	
	@Test
	void testCreateAssessmentOption_withoutId_appearance()
	{
		// Act
		AssessmentOption option = AssessmentOptionFactory.createAssessmentOption("Well-groomed", "Clean appearance",
				AssessmentOptionType.APPEARANCE);
		
		// Assert
		assertNotNull(option);
		assertInstanceOf(AppearanceAssessmentOption.class, option);
		assertEquals(AssessmentOptionType.APPEARANCE, option.getOptionType());
	}
	
	@Test
	void testCreateAssessmentOption_withoutId_speech()
	{
		// Act
		AssessmentOption option = AssessmentOptionFactory.createAssessmentOption("Clear", "Clear speech", AssessmentOptionType.SPEECH);
		
		// Assert
		assertNotNull(option);
		assertInstanceOf(SpeechAssessmentOption.class, option);
		assertEquals(AssessmentOptionType.SPEECH, option.getOptionType());
	}
	
	@Test
	void testCreateAssessmentOption_withoutId_affect()
	{
		// Act
		AssessmentOption option = AssessmentOptionFactory.createAssessmentOption("Happy", "Cheerful mood", AssessmentOptionType.AFFECT);
		
		// Assert
		assertNotNull(option);
		assertInstanceOf(AffectAssessmentOption.class, option);
		assertEquals(AssessmentOptionType.AFFECT, option.getOptionType());
	}
	
	@Test
	void testCreateAssessmentOption_withoutId_eyeContact()
	{
		// Act
		AssessmentOption option = AssessmentOptionFactory.createAssessmentOption("Good", "Maintains eye contact",
				AssessmentOptionType.EYE_CONTACT);
		
		// Assert
		assertNotNull(option);
		assertInstanceOf(EyeContactAssessmentOption.class, option);
		assertEquals(AssessmentOptionType.EYE_CONTACT, option.getOptionType());
	}
	
	@Test
	void testCreateAssessmentOption_withoutId_referrals()
	{
		// Act
		AssessmentOption option = AssessmentOptionFactory.createAssessmentOption("Psychiatrist", "Referral to psychiatrist",
				AssessmentOptionType.REFERRALS);
		
		// Assert
		assertNotNull(option);
		assertInstanceOf(ReferralAssessmentOption.class, option);
		assertEquals(AssessmentOptionType.REFERRALS, option.getOptionType());
	}
	
	@Test
	void testCreateAssessmentOption_withoutId_collateralContacts()
	{
		// Act
		AssessmentOption option = AssessmentOptionFactory.createAssessmentOption("Family Member", "Contact with family",
				AssessmentOptionType.COLL_CONTACTS);
		
		// Assert
		assertNotNull(option);
		assertInstanceOf(CollateralContactAssessmentOption.class, option);
		assertEquals(AssessmentOptionType.COLL_CONTACTS, option.getOptionType());
	}
	
	@Test
	void testCreateAssessmentOption_withoutId_nextAppt()
	{
		// Act
		AssessmentOption option = AssessmentOptionFactory.createAssessmentOption("1 Week", "Next appointment in 1 week",
				AssessmentOptionType.NEXT_APPT);
		
		// Assert
		assertNotNull(option);
		assertInstanceOf(NextApptAssessmentOption.class, option);
		assertEquals(AssessmentOptionType.NEXT_APPT, option.getOptionType());
	}
	
	@Test
	void testCreateAssessmentOption_withoutId_nullType()
	{
		// Act
		AssessmentOption option = AssessmentOptionFactory.createAssessmentOption("Test", "Description", null);
		
		// Assert
		assertNull(option, "Factory should return null for null type");
	}
	
	@Test
	void testCreateAssessmentOption_withoutId_nullDescription()
	{
		// Act
		AssessmentOption option = AssessmentOptionFactory.createAssessmentOption("Anxiety", null, AssessmentOptionType.SYMPTOMS);
		
		// Assert
		assertNotNull(option);
		assertEquals("Anxiety", option.getName());
		assertNull(option.getDescription());
	}
	
	@Test
	void testCreateAssessmentOption_withId_symptoms()
	{
		// Act
		AssessmentOption option = AssessmentOptionFactory.createAssessmentOption(42, "Depression", "Feeling depressed",
				AssessmentOptionType.SYMPTOMS);
		
		// Assert
		assertNotNull(option);
		assertInstanceOf(SymptomAssessmentOption.class, option);
		assertEquals(42, option.getId());
		assertEquals("Depression", option.getName());
		assertEquals("Feeling depressed", option.getDescription());
		assertEquals(AssessmentOptionType.SYMPTOMS, option.getOptionType());
	}
	
	@Test
	void testCreateAssessmentOption_withId_appearance()
	{
		// Act
		AssessmentOption option = AssessmentOptionFactory.createAssessmentOption(10, "Disheveled", "Unkempt appearance",
				AssessmentOptionType.APPEARANCE);
		
		// Assert
		assertNotNull(option);
		assertInstanceOf(AppearanceAssessmentOption.class, option);
		assertEquals(10, option.getId());
		assertEquals(AssessmentOptionType.APPEARANCE, option.getOptionType());
	}
	
	@Test
	void testCreateAssessmentOption_withId_speech()
	{
		// Act
		AssessmentOption option = AssessmentOptionFactory.createAssessmentOption(15, "Slurred", "Slurred speech",
				AssessmentOptionType.SPEECH);
		
		// Assert
		assertNotNull(option);
		assertInstanceOf(SpeechAssessmentOption.class, option);
		assertEquals(15, option.getId());
		assertEquals(AssessmentOptionType.SPEECH, option.getOptionType());
	}
	
	@Test
	void testCreateAssessmentOption_withId_affect()
	{
		// Act
		AssessmentOption option = AssessmentOptionFactory.createAssessmentOption(20, "Sad", "Depressed mood", AssessmentOptionType.AFFECT);
		
		// Assert
		assertNotNull(option);
		assertInstanceOf(AffectAssessmentOption.class, option);
		assertEquals(20, option.getId());
		assertEquals(AssessmentOptionType.AFFECT, option.getOptionType());
	}
	
	@Test
	void testCreateAssessmentOption_withId_eyeContact()
	{
		// Act
		AssessmentOption option = AssessmentOptionFactory.createAssessmentOption(25, "Poor", "Avoids eye contact",
				AssessmentOptionType.EYE_CONTACT);
		
		// Assert
		assertNotNull(option);
		assertInstanceOf(EyeContactAssessmentOption.class, option);
		assertEquals(25, option.getId());
		assertEquals(AssessmentOptionType.EYE_CONTACT, option.getOptionType());
	}
	
	@Test
	void testCreateAssessmentOption_withId_referrals()
	{
		// Act
		AssessmentOption option = AssessmentOptionFactory.createAssessmentOption(30, "Psychologist", "Referral to psychologist",
				AssessmentOptionType.REFERRALS);
		
		// Assert
		assertNotNull(option);
		assertInstanceOf(ReferralAssessmentOption.class, option);
		assertEquals(30, option.getId());
		assertEquals(AssessmentOptionType.REFERRALS, option.getOptionType());
	}
	
	@Test
	void testCreateAssessmentOption_withId_collateralContacts()
	{
		// Act
		AssessmentOption option = AssessmentOptionFactory.createAssessmentOption(35, "School", "Contact with school",
				AssessmentOptionType.COLL_CONTACTS);
		
		// Assert
		assertNotNull(option);
		assertInstanceOf(CollateralContactAssessmentOption.class, option);
		assertEquals(35, option.getId());
		assertEquals(AssessmentOptionType.COLL_CONTACTS, option.getOptionType());
	}
	
	@Test
	void testCreateAssessmentOption_withId_nextAppt()
	{
		// Act
		AssessmentOption option = AssessmentOptionFactory.createAssessmentOption(40, "2 Weeks", "Next appointment in 2 weeks",
				AssessmentOptionType.NEXT_APPT);
		
		// Assert
		assertNotNull(option);
		assertInstanceOf(NextApptAssessmentOption.class, option);
		assertEquals(40, option.getId());
		assertEquals(AssessmentOptionType.NEXT_APPT, option.getOptionType());
	}
	
	@Test
	void testCreateAssessmentOption_withId_nullType()
	{
		// Act
		AssessmentOption option1 = AssessmentOptionFactory.createAssessmentOption(100, "Test1", "Description", (AssessmentOptionType) null);
		AssessmentOption option2 = AssessmentOptionFactory.createAssessmentOption(200, "Test2", "Description", (String) null);
		
		// Assert
		assertNull(option1, "Factory should return null for null type");
		assertNull(option2, "Factory should return null for null type");
	}
	
	@Test
	void testCreateAssessmentOption_byDbTypeKey_symptoms()
	{
		// Act
		AssessmentOption option = AssessmentOptionFactory.createAssessmentOption(50, "Anxiety", "Anxious feeling", "symptoms");
		
		// Assert
		assertNotNull(option);
		assertInstanceOf(SymptomAssessmentOption.class, option);
		assertEquals(50, option.getId());
		assertEquals("Anxiety", option.getName());
		assertEquals(AssessmentOptionType.SYMPTOMS, option.getOptionType());
	}
	
	@Test
	void testCreateAssessmentOption_byDbTypeKey_affect()
	{
		// Act
		AssessmentOption option = AssessmentOptionFactory.createAssessmentOption(60, "Happy", "Cheerful", "affect");
		
		// Assert
		assertNotNull(option);
		assertInstanceOf(AffectAssessmentOption.class, option);
		assertEquals(AssessmentOptionType.AFFECT, option.getOptionType());
	}
	
	@Test
	void testCreateAssessmentOption_byDbTypeKey_invalidKey()
	{
		assertThrows(IllegalArgumentException.class, () ->
		{
			AssessmentOptionFactory.createAssessmentOption(70, "Test", "Description", "invalid_key");
		});
	}
	
	@Test
	void testFactoryCreatesDistinctInstances()
	{
		// Act
		AssessmentOption option1 = AssessmentOptionFactory.createAssessmentOption("Anxiety", "Description", AssessmentOptionType.SYMPTOMS);
		AssessmentOption option2 = AssessmentOptionFactory.createAssessmentOption("Anxiety", "Description", AssessmentOptionType.SYMPTOMS);
		
		// Assert
		assertNotNull(option1);
		assertNotNull(option2);
		assertNotSame(option1, option2, "Factory should create distinct instances");
		assertEquals(option1.getName(), option2.getName());
		assertEquals(option1.getDescription(), option2.getDescription());
	}
}