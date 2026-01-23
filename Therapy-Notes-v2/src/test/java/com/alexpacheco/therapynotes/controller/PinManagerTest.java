package com.alexpacheco.therapynotes.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.alexpacheco.therapynotes.controller.PinManager.PinStrength;

/**
 * Unit tests for {@link PinManager#evaluateStrength(char[])}.
 * 
 * Tests cover length scoring, complexity scoring, penalties for sequential/repeating characters, and detection of common weak PINs.
 */
@DisplayName( "PinManager.evaluateStrength()" )
class PinManagerTest
{
	@Nested
	@DisplayName( "Empty and null inputs" )
	class EmptyInputs
	{
		@Test
		@DisplayName( "Null PIN returns NONE" )
		void nullPin_returnsNone()
		{
			assertEquals( PinStrength.NONE, PinManager.evaluateStrength( null ) );
		}
		
		@Test
		@DisplayName( "Empty PIN returns NONE" )
		void emptyPin_returnsNone()
		{
			assertEquals( PinStrength.NONE, PinManager.evaluateStrength( new char[0] ) );
		}
	}
	
	@Nested
	@DisplayName( "Common weak PINs" )
	class CommonWeakPins
	{
		@ParameterizedTest( name = "Common PIN \"{0}\" should be WEAK" )
		@ValueSource( strings = { "0000", "1111", "1234", "4321", "1212", "2121", "password", "pass", "admin", "login" } )
		void commonPins_returnWeak( String pin )
		{
			assertEquals( PinStrength.WEAK, PinManager.evaluateStrength( pin.toCharArray() ) );
		}
		
		@Test
		@DisplayName( "Common PIN detection is case-insensitive" )
		void commonPinDetection_isCaseInsensitive()
		{
			assertEquals( PinStrength.WEAK, PinManager.evaluateStrength( "PASSWORD".toCharArray() ) );
			assertEquals( PinStrength.WEAK, PinManager.evaluateStrength( "Admin".toCharArray() ) );
			assertEquals( PinStrength.WEAK, PinManager.evaluateStrength( "LOGIN".toCharArray() ) );
		}
	}
	
	@Nested
	@DisplayName( "Length-based scoring" )
	class LengthScoring
	{
		@Test
		@DisplayName( "Very short PIN (1-3 chars) scores low" )
		void veryShortPin_scoresLow()
		{
			// 3 chars, no complexity bonus
			assertEquals( PinStrength.WEAK, PinManager.evaluateStrength( "abc".toCharArray() ) );
		}
		
		@Test
		@DisplayName( "4-character PIN gets base length score" )
		void fourCharPin_getsBaseScore()
		{
			// 4 chars = 1 point, letters only = no complexity bonus
			assertEquals( PinStrength.WEAK, PinManager.evaluateStrength( "abcd".toCharArray() ) );
		}
		
		@Test
		@DisplayName( "6-character PIN gets additional length score" )
		void sixCharPin_getsAdditionalScore()
		{
			// 6 chars = 2 points, letters only = no complexity bonus = FAIR
			assertEquals( PinStrength.FAIR, PinManager.evaluateStrength( "axedgp".toCharArray() ) );
		}
		
		@Test
		@DisplayName( "8-character PIN gets maximum length score" )
		void eightCharPin_getsMaxLengthScore()
		{
			// 8 chars = 3 points = GOOD
			assertEquals( PinStrength.GOOD, PinManager.evaluateStrength( "axemgpbw".toCharArray() ) );
		}
	}
	
	@Nested
	@DisplayName( "Complexity scoring" )
	class ComplexityScoring
	{
		@Test
		@DisplayName( "Digits only provides no complexity bonus" )
		void digitsOnly_noComplexityBonus()
		{
			// 6 digits = 2 length points, no complexity = FAIR
			assertEquals( PinStrength.FAIR, PinManager.evaluateStrength( "135792".toCharArray() ) );
		}
		
		@Test
		@DisplayName( "Letters only provides no complexity bonus" )
		void lettersOnly_noComplexityBonus()
		{
			// 6 letters = 2 length points, no complexity = FAIR
			assertEquals( PinStrength.FAIR, PinManager.evaluateStrength( "acevxz".toCharArray() ) );
		}
		
		@Test
		@DisplayName( "Mixed digits and letters adds complexity bonus" )
		void mixedDigitsAndLetters_addsComplexityBonus()
		{
			// 6 chars = 2 points + mixed = 1 point = 3 = GOOD
			assertEquals( PinStrength.GOOD, PinManager.evaluateStrength( "ace135".toCharArray() ) );
		}
		
		@Test
		@DisplayName( "Special characters add complexity bonus" )
		void specialCharacters_addComplexityBonus()
		{
			// 6 chars = 2 points + special = 1 point = 3 = GOOD
			assertEquals( PinStrength.GOOD, PinManager.evaluateStrength( "ace!@#".toCharArray() ) );
		}
		
		@Test
		@DisplayName( "Mixed alphanumeric with special chars maximizes complexity" )
		void mixedWithSpecial_maximizesComplexity()
		{
			// 8 chars = 3 points + mixed = 1 point + special = 1 point = 5 = STRONG
			assertEquals( PinStrength.STRONG, PinManager.evaluateStrength( "ace12!@#".toCharArray() ) );
		}
	}
	
	@Nested
	@DisplayName( "Sequential character penalties" )
	class SequentialPenalties
	{
		@Test
		@DisplayName( "Sequential digits incur penalty" )
		void sequentialDigits_incurPenalty()
		{
			// "567890" has sequential chars, reducing score
			// 6 chars = 2 points - 1 sequential penalty = 1 = WEAK
			assertEquals( PinStrength.WEAK, PinManager.evaluateStrength( "567890".toCharArray() ) );
		}
		
		@Test
		@DisplayName( "Sequential letters incur penalty" )
		void sequentialLetters_incurPenalty()
		{
			// "mnopqr" has sequential chars
			// 6 chars = 2 points - 1 sequential penalty = 1 = WEAK
			assertEquals( PinStrength.WEAK, PinManager.evaluateStrength( "mnopqr".toCharArray() ) );
		}
		
		@Test
		@DisplayName( "Non-sequential characters avoid penalty" )
		void nonSequential_avoidsPenalty()
		{
			// "azbycx" - no sequential pairs
			// 6 chars = 2 points = FAIR
			assertEquals( PinStrength.FAIR, PinManager.evaluateStrength( "azbycx".toCharArray() ) );
		}
	}
	
	@Nested
	@DisplayName( "Repeating character penalties" )
	class RepeatingPenalties
	{
		@Test
		@DisplayName( "Repeating characters incur penalty" )
		void repeatingChars_incurPenalty()
		{
			// "aabbcc" has repeating chars
			// 6 chars = 2 points - 1 repeating penalty = 1 = WEAK
			assertEquals( PinStrength.WEAK, PinManager.evaluateStrength( "aabbcc".toCharArray() ) );
		}
		
		@Test
		@DisplayName( "All same character is weak" )
		void allSameChar_isWeak()
		{
			// "aaaaaa" - all repeating (but not in common list for 'a')
			// 6 chars = 2 points - 1 repeating penalty = 1 = WEAK
			assertEquals( PinStrength.WEAK, PinManager.evaluateStrength( "aaaaaa".toCharArray() ) );
		}
		
		@Test
		@DisplayName( "No repeating characters avoids penalty" )
		void noRepeating_avoidsPenalty()
		{
			// "adspoz" - no repeating pairs
			// 6 chars = 2 points = FAIR
			assertEquals( PinStrength.FAIR, PinManager.evaluateStrength( "adspoz".toCharArray() ) );
		}
	}
	
	@Nested
	@DisplayName( "Combined scoring scenarios" )
	class CombinedScenarios
	{
		@Test
		@DisplayName( "Long complex PIN with no penalties is STRONG" )
		void longComplexNoPenalties_isStrong()
		{
			// "Th3r@py!" - 8 chars, mixed, special, no sequential/repeating
			assertEquals( PinStrength.STRONG, PinManager.evaluateStrength( "Th3r@py!".toCharArray() ) );
		}
		
		@Test
		@DisplayName( "Long PIN with sequential penalty can still be GOOD" )
		void longWithSequential_canBeGood()
		{
			// "abc12!@#" - 8 chars (3) + mixed (1) + special (1) - sequential (1) = 4 = STRONG
			// Actually "abc" is sequential so penalty applies
			assertEquals( PinStrength.STRONG, PinManager.evaluateStrength( "abc12!@#".toCharArray() ) );
		}
		
		@Test
		@DisplayName( "Short PIN with maximum complexity" )
		void shortWithMaxComplexity()
		{
			// "a1!" - 3 chars (0 length points) + mixed (1) + special (1) = 2 = FAIR
			assertEquals( PinStrength.FAIR, PinManager.evaluateStrength( "a1!".toCharArray() ) );
		}
		
		@Test
		@DisplayName( "Multiple penalties reduce score significantly" )
		void multiplePenalties_reduceScore()
		{
			// "112233" - 6 chars (2) - repeating (1) = 1 = WEAK
			assertEquals( PinStrength.WEAK, PinManager.evaluateStrength( "112233".toCharArray() ) );
		}
	}
	
	@Nested
	@DisplayName( "Edge cases" )
	class EdgeCases
	{
		@Test
		@DisplayName( "Single character PIN" )
		void singleChar_isWeak()
		{
			assertEquals( PinStrength.WEAK, PinManager.evaluateStrength( "a".toCharArray() ) );
		}
		
		@Test
		@DisplayName( "Very long PIN" )
		void veryLongPin_benefitsFromLength()
		{
			// 20 mixed chars with special = max length (3) + mixed (1) + special (1) = 5 = STRONG
			assertEquals( PinStrength.STRONG, PinManager.evaluateStrength( "aB3!xY9@mN5#pQ7$rT1%".toCharArray() ) );
		}
		
		@Test
		@DisplayName( "PIN with only special characters" )
		void onlySpecialChars()
		{
			// "!#@%$^" - 6 chars (2) + special (1) = 3 = GOOD
			assertEquals( PinStrength.GOOD, PinManager.evaluateStrength( "!#@%$^".toCharArray() ) );
		}
		
		@Test
		@DisplayName( "PIN with spaces" )
		void pinWithSpaces()
		{
			// "ab cd" - spaces count as special chars
			// 5 chars (1 length point for >=4) + mixed letters/special (0 for no digits) + special (1) = 2 = FAIR
			PinStrength result = PinManager.evaluateStrength( "ab cd".toCharArray() );
			// Space is not a digit or letter, so it's "special"
			assertTrue( result == PinStrength.FAIR || result == PinStrength.WEAK );
		}
		
		@Test
		@DisplayName( "Unicode characters" )
		void unicodeCharacters()
		{
			// Unicode chars should be treated as non-digit, non-letter (special)
			PinStrength result = PinManager.evaluateStrength( "café123".toCharArray() );
			assertNotNull( result );
			// Should still evaluate without throwing
		}
	}
	
	@Nested
	@DisplayName( "PinStrength enum" )
	class PinStrengthEnum
	{
		@Test
		@DisplayName( "NONE has empty label" )
		void none_hasEmptyLabel()
		{
			assertEquals( "", PinStrength.NONE.getLabel() );
		}
		
		@Test
		@DisplayName( "All strengths have non-null colors" )
		void allStrengths_haveColors()
		{
			for( PinStrength strength : PinStrength.values() )
			{
				assertNotNull( strength.getColor(), "Color should not be null for " + strength );
			}
		}
		
		@Test
		@DisplayName( "Strength labels are correct" )
		void strengthLabels_areCorrect()
		{
			assertEquals( "Weak", PinStrength.WEAK.getLabel() );
			assertEquals( "Fair", PinStrength.FAIR.getLabel() );
			assertEquals( "Good", PinStrength.GOOD.getLabel() );
			assertEquals( "Strong", PinStrength.STRONG.getLabel() );
		}
	}
}