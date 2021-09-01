package org.unifi.lorenzopratesi.app.contacts.view;

import java.util.List;

import org.unifi.lorenzopratesi.app.contacts.model.Contact;

public interface ContactView {

	void listContacts(List<Contact> contacts);

	void contactAdded(Contact contact);

	void contactEdited(Contact contact);

	void contactRemoved(Contact contactToDelete);

	void showMessage(String message);

}
