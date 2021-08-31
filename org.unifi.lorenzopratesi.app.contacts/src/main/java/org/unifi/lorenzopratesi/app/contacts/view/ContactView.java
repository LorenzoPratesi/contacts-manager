package org.unifi.lorenzopratesi.app.contacts.view;

import java.util.List;

import org.unifi.lorenzopratesi.app.contacts.model.Contact;

public interface ContactView {

	void listContacts(List<Contact> contacts);

	void contactAdded(Contact contact);

	void showMessage(String message, Contact contact, String type);

}
