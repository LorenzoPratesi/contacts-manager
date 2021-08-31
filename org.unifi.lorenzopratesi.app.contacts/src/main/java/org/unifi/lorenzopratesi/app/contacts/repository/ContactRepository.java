package org.unifi.lorenzopratesi.app.contacts.repository;

import java.util.List;

import org.unifi.lorenzopratesi.app.contacts.model.Contact;

public interface ContactRepository {

	public List<Contact> findAllContacts();

}
