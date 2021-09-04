package org.unifi.lorenzopratesi.app.contacts.validation.controller;

import java.util.regex.Pattern;

import org.unifi.lorenzopratesi.app.contacts.validation.InputValidation;

public class ControllerInputValidator implements InputValidation {

	private static final int PHONE_MAX_LENGTH = 15;

	@Override
	public boolean validateEmail(String email) {
		if (email == null) {
			return false;
		}

		return Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9+_.-]+$", email);
	}

	@Override
	public boolean validatePhone(String phone) {
		if (phone == null || phone.length() > PHONE_MAX_LENGTH) {
			return false;
		}
		return Pattern.matches("^[+]\\d+$|^\\d+$", phone);
	}

}
