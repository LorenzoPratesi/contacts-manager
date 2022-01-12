package org.unifi.lorenzopratesi.app.contacts.controller;

import org.unifi.lorenzopratesi.app.contacts.model.Contact;
import org.unifi.lorenzopratesi.app.contacts.repository.ContactRepository;
import org.unifi.lorenzopratesi.app.contacts.validation.InputValidation;
import org.unifi.lorenzopratesi.app.contacts.view.ContactView;

public class ContactController {

	private final ContactView contactView;
	private final ContactRepository contactRepository;
	private final InputValidation inputValidation;

	public ContactController(ContactRepository contactRepository, ContactView contactView,
			InputValidation inputValidation) {
		this.contactRepository = contactRepository;
		this.contactView = contactView;
		this.inputValidation = inputValidation;
	}

	public void allContacts() {
		contactView.showContacts(contactRepository.findAll());
	}
	

	public void findByName(String name) {
		contactView.showContacts(contactRepository.findByName(name));
	}

	public void newContact(Contact contact) {
		if (!isValidEmail(contact.getEmail())) {
			contactView.showMessage(emailNotValidMsg(contact.getEmail()));
			return;
		}

		if (!isValidPhone(contact.getPhone())) {
			contactView.showMessage(phoneNotValidMsg(contact.getPhone()));
			return;
		}

		if (exists(contact.getId())) {
			contactView.showMessage(contactExistsMsg(contact.getId()));
			return;
		}

		contactRepository.save(contact);
		contactView.contactAdded(contact);
		contactView.showMessage("Contact added");

	}

	public void deleteContact(Contact contact) {
		if (!exists(contact.getId())) {
			contactView.showMessage(contactNotExistsMsg(contact));
			return;
		}
		
		contactRepository.delete(contact.getId());
		contactView.contactRemoved(contact);
	}

	public void updatePhone(Contact contact, String phone) {
		if (!exists(contact.getId())) {
			contactView.showMessage(contactNotExistsMsg(contact));
			return;
		}

		if (!isValidPhone(phone)) {
			contactView.showMessage(phoneNotValidMsg(phone));
			return;
		}

		contactRepository.updatePhone(contact.getId(), phone);
		contact.setPhone(phone);
		contactView.contactEdited(contact);
		contactView.showMessage("Contact phone changed");
	}

	public void updateEmail(Contact contact, String email) {
		if (!exists(contact.getId())) {
			contactView.showMessage(contactNotExistsMsg(contact));
			return;
		}

		if (!isValidEmail(email)) {
			contactView.showMessage(emailNotValidMsg(email));
			return;
		}

		contactRepository.updateEmail(contact.getId(), email);
		contact.setEmail(email);
		contactView.contactEdited(contact);
		contactView.showMessage("Contact email changed");
	}

	private boolean exists(String id) {
		return contactRepository.findById(id) != null;
	}

	private boolean isValidPhone(String phone) {
		return inputValidation.validatePhone(phone);
	}

	private boolean isValidEmail(String email) {
		return inputValidation.validateEmail(email);
	}

	private String contactExistsMsg(String id) {
		return "Contact with id " + id + " already exists.";
	}

	private String contactNotExistsMsg(Contact contact) {
		return String.format("There is no contact with id %s, %s, %s", contact.getId(), contact.getFirstName(),
				contact.getLastName());
	}

	private String phoneNotValidMsg(String phone) {
		return "Contact Phone is not valid: " + phone + ". Format must be similar to +10000000000.";
	}

	private String emailNotValidMsg(String email) {
		return "Contact Email is not valid: " + email + ". Format must be similar to prefix@domain.";
	}

}
