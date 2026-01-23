package com.alexpacheco.therapynotes.util.validators;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Unit tests for {@link EmailValidator} and {@link PhoneNumberValidator}.
 * 
 * Both validators treat null/empty as valid (optional field semantics).
 */
@DisplayName( "Validator" )
class ValidatorTest
{
	@Nested
	@DisplayName( "EmailValidator" )
	class EmailValidatorTest
	{
		@Nested
		@DisplayName( "Null and empty handling" )
		class NullAndEmptyHandling
		{
			@Test
			@DisplayName( "Null input returns true (optional field)" )
			void nullInput_returnsTrue()
			{
				assertTrue( EmailValidator.isValidEmailAddress( null ) );
			}
			
			@Test
			@DisplayName( "Empty string returns true (optional field)" )
			void emptyString_returnsTrue()
			{
				assertTrue( EmailValidator.isValidEmailAddress( "" ) );
			}
			
			@Test
			@DisplayName( "Whitespace-only string returns false" )
			void whitespaceOnly_returnsFalse()
			{
				assertFalse( EmailValidator.isValidEmailAddress( "   " ) );
			}
		}
		
		@Nested
		@DisplayName( "Valid email addresses" )
		class ValidEmails
		{
			@ParameterizedTest( name = "\"{0}\" should be valid" )
			@ValueSource( strings = { "user@example.com", "user@example.org", "user@example.net", "user@example.co", "user@example.info",
					"firstname.lastname@example.com", "user123@example.com", "user_name@example.com", "user-name@example.com",
					"user.name@example.com", "user@subdomain.example.com", "user@example-site.com", "user@123.com", "a@b.co",
					"test@mail.example.com" } )
			void validEmails_returnTrue( String email )
			{
				assertTrue( EmailValidator.isValidEmailAddress( email ), "Expected valid: " + email );
			}
		}
		
		@Nested
		@DisplayName( "Invalid email addresses" )
		class InvalidEmails
		{
			@ParameterizedTest( name = "\"{0}\" should be invalid" )
			@ValueSource( strings = { "plaintext", "missing@tld", "@missinglocal.com", "missing.domain@", "user@.com", "user@example.",
					"user@example.c", // TLD too short (min 2 chars)
					"user name@example.com", // Space in local part
					"user@exam ple.com", // Space in domain
					"user@@example.com", // Double @
					"@example.com", // Missing local part
					"user@", // Missing domain
					"@", // Just @
					".user@example.com", // Leading dot in local part
					"user.@example.com", // Trailing dot in local part
					"..user@example.com", // Multiple leading dots
					".@example.com", // Just a dot as local part
					"user@.example.com", // Leading dot in domain
					"user@example..com", // Consecutive dots in domain
					"-user@example.com", // Leading hyphen in local part
					"user-@example.com" // Trailing hyphen in local part
			} )
			void invalidEmails_returnFalse( String email )
			{
				assertFalse( EmailValidator.isValidEmailAddress( email ), "Expected invalid: " + email );
			}
		}
		
		@Nested
		@DisplayName( "Edge cases" )
		class EdgeCases
		{
			@Test
			@DisplayName( "Single character local part is valid" )
			void singleCharLocalPart_valid()
			{
				assertTrue( EmailValidator.isValidEmailAddress( "a@example.com" ) );
			}
			
			@Test
			@DisplayName( "Two character local part is valid" )
			void twoCharLocalPart_valid()
			{
				assertTrue( EmailValidator.isValidEmailAddress( "ab@example.com" ) );
			}
			
			@Test
			@DisplayName( "Very long valid email" )
			void veryLongEmail_valid()
			{
				String longLocal = "a".repeat( 50 );
				String email = longLocal + "@example.com";
				assertTrue( EmailValidator.isValidEmailAddress( email ) );
			}
			
			@Test
			@DisplayName( "Numeric local part" )
			void numericLocalPart_valid()
			{
				assertTrue( EmailValidator.isValidEmailAddress( "12345@example.com" ) );
			}
			
			@Test
			@DisplayName( "Numeric domain" )
			void numericDomain_valid()
			{
				assertTrue( EmailValidator.isValidEmailAddress( "user@123.com" ) );
			}
			
			@Test
			@DisplayName( "Dot in middle of local part is valid" )
			void dotInMiddle_valid()
			{
				assertTrue( EmailValidator.isValidEmailAddress( "first.last@example.com" ) );
				assertTrue( EmailValidator.isValidEmailAddress( "a.b.c@example.com" ) );
			}
			
			@Test
			@DisplayName( "Hyphen in middle of local part is valid" )
			void hyphenInMiddle_valid()
			{
				assertTrue( EmailValidator.isValidEmailAddress( "first-last@example.com" ) );
			}
			
			@Test
			@DisplayName( "Multiple subdomains" )
			void multipleSubdomains_valid()
			{
				assertTrue( EmailValidator.isValidEmailAddress( "user@a.b.c.example.com" ) );
			}
			
			@Test
			@DisplayName( "Two-character TLD" )
			void twoCharTld_valid()
			{
				assertTrue( EmailValidator.isValidEmailAddress( "user@example.co" ) );
			}
			
			@Test
			@DisplayName( "Long TLD" )
			void longTld_valid()
			{
				assertTrue( EmailValidator.isValidEmailAddress( "user@example.technology" ) );
			}
		}
	}
	
	@Nested
	@DisplayName( "PhoneNumberValidator" )
	class PhoneNumberValidatorTest
	{
		@Nested
		@DisplayName( "Null and empty handling" )
		class NullAndEmptyHandling
		{
			@Test
			@DisplayName( "Null input returns true (optional field)" )
			void nullInput_returnsTrue()
			{
				assertTrue( PhoneNumberValidator.isValidUSPhone( null ) );
			}
			
			@Test
			@DisplayName( "Empty string returns true (optional field)" )
			void emptyString_returnsTrue()
			{
				assertTrue( PhoneNumberValidator.isValidUSPhone( "" ) );
			}
		}
		
		@Nested
		@DisplayName( "Valid 10-digit numbers" )
		class Valid10DigitNumbers
		{
			@ParameterizedTest( name = "\"{0}\" should be valid" )
			@ValueSource( strings = { "5551234567", "555-123-4567", "555.123.4567", "555 123 4567", "(555) 123-4567", "(555)123-4567",
					"(555) 1234567", "555-1234567" } )
			void valid10Digit_returnTrue( String phone )
			{
				assertTrue( PhoneNumberValidator.isValidUSPhone( phone ), "Expected valid: " + phone );
			}
		}
		
		@Nested
		@DisplayName( "Valid 11-digit numbers with country code" )
		class Valid11DigitNumbers
		{
			@ParameterizedTest( name = "\"{0}\" should be valid" )
			@ValueSource( strings = { "15551234567", "1-555-123-4567", "1.555.123.4567", "1 555 123 4567", "+1 555 123 4567",
					"+1 (555) 123-4567", "1 (555) 123-4567", "+1-555-123-4567" } )
			void valid11DigitWithCountryCode_returnTrue( String phone )
			{
				assertTrue( PhoneNumberValidator.isValidUSPhone( phone ), "Expected valid: " + phone );
			}
		}
		
		@Nested
		@DisplayName( "Invalid phone numbers" )
		class InvalidPhoneNumbers
		{
			@ParameterizedTest( name = "\"{0}\" should be invalid" )
			@ValueSource( strings = { "555123456", // 9 digits - too short
					"55512345678", // 11 digits not starting with 1
					"123456789012", // 12 digits
					"1234567", // 7 digits
					"123456", // 6 digits
					"1", // 1 digit
					"abcdefghij" // No digits
			} )
			void invalidPhones_returnFalse( String phone )
			{
				assertFalse( PhoneNumberValidator.isValidUSPhone( phone ), "Expected invalid: " + phone );
			}
		}
		
		@Nested
		@DisplayName( "Formatting variations" )
		class FormattingVariations
		{
			@Test
			@DisplayName( "Handles extra spaces" )
			void extraSpaces_valid()
			{
				assertTrue( PhoneNumberValidator.isValidUSPhone( "  555  123  4567  " ) );
			}
			
			@Test
			@DisplayName( "Handles mixed delimiters" )
			void mixedDelimiters_valid()
			{
				assertTrue( PhoneNumberValidator.isValidUSPhone( "(555)-123.4567" ) );
			}
			
			@Test
			@DisplayName( "Handles letters mixed with digits (letters stripped)" )
			void lettersInPhone_strippedAndValidated()
			{
				// "555CALL123" -> strips to "555123" which is 6 digits = invalid
				assertFalse( PhoneNumberValidator.isValidUSPhone( "555CALL123" ) );
			}
			
			@Test
			@DisplayName( "Phone number spelled with letters that yields 10 digits" )
			void spelledNumber_10Digits_valid()
			{
				// "555-GET-FOOD" -> 5554383663 = 10 digits
				assertTrue( PhoneNumberValidator.isValidUSPhone( "555-438-3663" ) );
			}
		}
		
		@Nested
		@DisplayName( "Edge cases" )
		class EdgeCases
		{
			@Test
			@DisplayName( "All zeros is valid (10 digits)" )
			void allZeros_valid()
			{
				assertTrue( PhoneNumberValidator.isValidUSPhone( "0000000000" ) );
			}
			
			@Test
			@DisplayName( "All nines is valid (10 digits)" )
			void allNines_valid()
			{
				assertTrue( PhoneNumberValidator.isValidUSPhone( "9999999999" ) );
			}
			
			@Test
			@DisplayName( "Country code 1 with all zeros" )
			void countryCodeWithZeros_valid()
			{
				assertTrue( PhoneNumberValidator.isValidUSPhone( "10000000000" ) );
			}
			
			@Test
			@DisplayName( "Only non-digit characters returns false" )
			void onlyNonDigits_returnsFalse()
			{
				assertFalse( PhoneNumberValidator.isValidUSPhone( "---" ) );
				assertFalse( PhoneNumberValidator.isValidUSPhone( "()" ) );
				assertFalse( PhoneNumberValidator.isValidUSPhone( "abc" ) );
			}
			
			@Test
			@DisplayName( "Whitespace-only returns false" )
			void whitespaceOnly_returnsFalse()
			{
				assertFalse( PhoneNumberValidator.isValidUSPhone( "   " ) );
			}
			
			@Test
			@DisplayName( "Plus sign alone with 10 digits is valid" )
			void plusWith10Digits_valid()
			{
				// "+5551234567" -> strips to 5551234567 (10 digits)
				assertTrue( PhoneNumberValidator.isValidUSPhone( "+5551234567" ) );
			}
		}
		
		@Nested
		@DisplayName( "Country code validation" )
		class CountryCodeValidation
		{
			@Test
			@DisplayName( "11 digits starting with 1 is valid" )
			void elevenDigitsStartingWith1_valid()
			{
				assertTrue( PhoneNumberValidator.isValidUSPhone( "15551234567" ) );
			}
			
			@Test
			@DisplayName( "11 digits starting with 2 is invalid" )
			void elevenDigitsStartingWith2_invalid()
			{
				assertFalse( PhoneNumberValidator.isValidUSPhone( "25551234567" ) );
			}
			
			@Test
			@DisplayName( "11 digits starting with 0 is invalid" )
			void elevenDigitsStartingWith0_invalid()
			{
				assertFalse( PhoneNumberValidator.isValidUSPhone( "05551234567" ) );
			}
			
			@Test
			@DisplayName( "11 digits starting with 9 is invalid" )
			void elevenDigitsStartingWith9_invalid()
			{
				assertFalse( PhoneNumberValidator.isValidUSPhone( "95551234567" ) );
			}
		}
	}
}