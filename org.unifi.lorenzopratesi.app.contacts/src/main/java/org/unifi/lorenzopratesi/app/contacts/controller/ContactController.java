package org.unifi.lorenzopratesi.app.contacts.controller;

import org.unifi.lorenzopratesi.app.contacts.repository.ContactRepository;
import org.unifi.lorenzopratesi.app.contacts.view.ContactView;

public class ContactController {

	private ContactView contactView;
	private ContactRepository contactRepository;

	public void allContacts() {
		contactView.listContacts(contactRepository.findAllContacts());
	}

}
