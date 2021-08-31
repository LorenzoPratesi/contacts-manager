package org.unifi.lorenzopratesi.app.contacts.controller;

import org.unifi.lorenzopratesi.app.contacts.model.Contact;
import org.unifi.lorenzopratesi.app.contacts.repository.ContactRepository;
import org.unifi.lorenzopratesi.app.contacts.view.ContactView;

public class ContactController {

	private ContactView contactView;
	private ContactRepository contactRepository;

	public void allContacts() {
		contactView.listContacts(contactRepository.findAllContacts());
	}

	public void addContact(Contact contact) {
		Contact existingContact = contactRepository.findByName(contact.getName());
		
		contactRepository.addContact(contact);
		contactView.contactAdded(contact);
		contactView.showMessage("Product added", contact, "");
	}

}
