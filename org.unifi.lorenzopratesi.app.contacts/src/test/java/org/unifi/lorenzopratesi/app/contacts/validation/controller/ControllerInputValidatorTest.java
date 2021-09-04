package org.unifi.lorenzopratesi.app.contacts.validation.controller;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.unifi.lorenzopratesi.app.contacts.validation.InputValidation;

class ControllerInputValidatorTest {

	InputValidation validator;

	@BeforeEach
	void setup() {
		validator = new ControllerInputValidator();
	}

	@Nested
	class PhoneValidation {

		@ParameterizedTest
		@NullAndEmptySource
		@ValueSource(strings = { "  ", "\t", "\n" })
		void testValidatePhoneShouldReturnFalseForAllTypesOfBlankStrings(String emptyPhone) {
			assertThat(validator.validatePhone(emptyPhone)).isFalse();
		}

		@Test
		void testValidatePhoneShouldReturnFalseOnImputGraterThan15Chars() {
			String phoneGraterThan15 = "11111111111111111";
			assertThat(phoneGraterThan15.length()).isGreaterThan(15);
			assertThat(validator.validatePhone(phoneGraterThan15)).isFalse();
		}

		@ParameterizedTest
		@ValueSource(strings = { "+10,5 9!34,34%561", "(+1)2234567891", "223-4567891", "111.111.111" })
		void testValidatePhoneShoudReturnFalseOnSpecialCharacters(String invalidPhone) {
			assertThat(validator.validatePhone(invalidPhone)).isFalse();
		}

		@Test
		void testValidatePhoneShoudReturnFalseWhenMisplacedPlusSign() {
			String phone = "01234+67891";
			assertThat(validator.validatePhone(phone)).isFalse();
		}

		@ParameterizedTest
		@ValueSource(strings = { "+12234567891", "2234567891" })
		void testValidatePhoneShouldReturnTrueOnCorrectFormatNumber(String phone) {
			assertThat(validator.validatePhone(phone)).isTrue();
		}
	}

	@Nested
	class EmailValidation {
		@ParameterizedTest
		@NullAndEmptySource
		@ValueSource(strings = { "  ", "\t", "\n" })
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
				"name@!#$%&'*+-/=?^_`{|}~.ext",
				";@domain.ext",
				"!#$%&'*+-/=?^_`{|}~@domain.ext"
		})
		void testInvalidEmailAddresses(String invalidEmail) {
			assertThat(validator.validateEmail(invalidEmail)).isFalse();
		}
	}

}
