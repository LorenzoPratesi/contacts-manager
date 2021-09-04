package org.unifi.lorenzopratesi.app.contacts.controller;

import org.unifi.lorenzopratesi.app.contacts.model.Contact;
import org.unifi.lorenzopratesi.app.contacts.repository.ContactRepository;
import org.unifi.lorenzopratesi.app.contacts.validation.InputValidation;
import org.unifi.lorenzopratesi.app.contacts.view.ContactView;

public class ContactController {

	private ContactView contactView;
	private ContactRepository contactRepository;
	private InputValidation inputValidation;

	public ContactController(ContactRepository contactRepository, ContactView contactView,
			InputValidation inputValidation) {
		this.contactRepository = contactRepository;
		this.contactView = contactView;
		this.inputValidation = inputValidation;
	}

	public void allContacts() {
		contactView.listContacts(contactRepository.findAll());
	}

	public void newContact(Contact contact) {
		if (isValidEmail(contact.getEmail()) && isValidPhone(contact.getPhone()) && !exists(contact.getId())) {
			Contact newContact = new Contact(contact.getFirstName(), contact.getLastName(), contact.getPhone(),
					contact.getEmail());
			contactRepository.save(newContact);
			contactView.contactAdded(newContact);
			contactView.showMessage("Contact added");
		}
	}

	public void deleteContact(Contact contact) {
		if (contactRepository.findById(contact.getId()) == null) {
			contactView.showMessage(String.format("There is no guest with id %s", contact.getId()));
		} else {
			contactRepository.delete(contact.getId());
			contactView.contactRemoved(contact);
		}

	}

	private boolean exists(String id) {
		if (contactRepository.findById(id) != null) {
			contactView.showMessage(String.format("Contact with id %s already exists.", id));
			return true;
		}
		return false;
	}

	private boolean isValidPhone(String phone) {
		if (!inputValidation.validatePhone(phone)) {
			contactView.showMessage(
					String.format("Contact Phone is not valid: %s. Format must be similar to +10000000000.", phone));
			return false;
		}
		return true;
	}

	private boolean isValidEmail(String email) {
		if ((!inputValidation.validateEmail(email))) {
			contactView.showMessage(
					String.format("Contact Email is not valid: %s. Format must be similar to prefix@domain.", email));
			return false;
		}
		return true;
	}

}
