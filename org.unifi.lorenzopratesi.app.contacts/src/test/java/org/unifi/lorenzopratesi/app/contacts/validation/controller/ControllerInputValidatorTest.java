package org.unifi.lorenzopratesi.app.contacts.validation.controller;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.unifi.lorenzopratesi.app.contacts.validation.InputValidation;

@DisplayName("Tests for Controller Input Validator")
class ControllerInputValidatorTest {

	InputValidation validator;

	@BeforeEach
	void setup() {
		validator = new ControllerInputValidator();
	}

	@Nested
	@DisplayName("Phone Validation")
	class PhoneValidation {

		@ParameterizedTest
		@NullAndEmptySource
		@ValueSource(strings = { "  ", "\t", "\n" })
		@DisplayName("Phone validation should return false on null string and for all types of blank strings - testValidatePhoneShouldReturnFalseForAllTypesOfBlankStrings()")
		void testValidatePhoneShouldReturnFalseForAllTypesOfBlankStrings(String emptyPhone) {
			assertThat(validator.validatePhone(emptyPhone)).isFalse();
		}

		@Test
		@DisplayName("Phone validation should return false when string length is more than 15 - testValidatePhoneShouldReturnFalseOnImputGraterThan15Chars()")
		void testValidatePhoneShouldReturnFalseOnImputGraterThan15Chars() {
			String phoneGraterThan15 = "11111111111111111";
			String phoneLenght15 = "111111111111111";
			assertThat(phoneGraterThan15.length()).isGreaterThan(15);
			assertThat(phoneLenght15).hasSize(15);
			assertThat(validator.validatePhone(phoneGraterThan15)).isFalse();
			assertThat(validator.validatePhone(phoneLenght15)).isTrue();
		}

		@ParameterizedTest
		@ValueSource(strings = { "+10,5 9!34,34%561", "(+1)2234567891", "223-4567891", "111.111.111" })
		@DisplayName("Phone validation should return false when string does not contain only numbers or with a plus at the beginning - testValidatePhoneShoudReturnFalseOnSpecialCharacters()")
		void testValidatePhoneShoudReturnFalseOnSpecialCharacters(String invalidPhone) {
			assertThat(validator.validatePhone(invalidPhone)).isFalse();
		}

		@Test
		@DisplayName("Phone validation should return false when plus sign is misolaced - testValidatePhoneShoudReturnFalseWhenMisplacedPlusSign()")
		void testValidatePhoneShoudReturnFalseWhenMisplacedPlusSign() {
			String phone = "01234+67891";
			assertThat(validator.validatePhone(phone)).isFalse();
		}

		@ParameterizedTest
		@ValueSource(strings = { "+12234567891", "2234567891", "+00000000000000" })
		@DisplayName("Phone validation should return true on correct format number - testValidatePhoneShouldReturnTrueOnCorrectFormatNumber()")
		void testValidatePhoneShouldReturnTrueOnCorrectFormatNumber(String phone) {
			assertThat(validator.validatePhone(phone)).isTrue();
		}
	}

	@Nested
	@DisplayName("Email Validation")
	class EmailValidation {
		
		@ParameterizedTest
		@NullAndEmptySource
		@ValueSource(strings = { "  ", "\t", "\n" })
		@DisplayName("Email validation should return false on null string and for all types of blank strings - testValidatePhoneShouldReturnFalseForAllTypesOfBlankStrings()")
		void testValidateEmailShouldReturnFalseForAllTypesOfBlankStrings(String emptyPhone) {
			assertThat(validator.validatePhone(emptyPhone)).isFalse();
		}

		@ParameterizedTest
		@ValueSource(strings = { 
				"name@domain.ext",
				"name.surname@domain.ext",
				"name.surname.other@domain.ext",
				"name@sub.domain.ext",
				"name.surname@sub.domain.ext",
				"name.surmane.other@sub.domain.ext",
		})
		@DisplayName("Email validation should return true when string format is valid - testValidEmailAddresses()")
		void testValidEmailAddresses(String validEmail) {
			assertThat(validator.validateEmail(validEmail)).isTrue();
		}

		@ParameterizedTest
		@ValueSource(strings = { 
				"   name@domain.ext", 
				"name@domain.ext   ", 
				"   name@domain.ext   ",
				"name @ domain . ext", 
				"@", 
				".", 
				"name", 
				"name@", 
				"name@sub@domain.ext", 
				"@domain", 
				"@domain.ext",
				"name@!#$%&'*+-/=?^_`{|}~.ext", ";@domain.ext", "!#$%&'*+-/=?^_`{|}~@domain.ext" 
		})
		@DisplayName("Email validation should return false when string format is not valid - testInvalidEmailAddresses()")
		void testInvalidEmailAddresses(String invalidEmail) {
			assertThat(validator.validateEmail(invalidEmail)).isFalse();
		}
	}
}
