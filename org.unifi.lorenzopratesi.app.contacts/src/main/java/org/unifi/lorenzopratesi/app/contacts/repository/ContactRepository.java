package org.unifi.lorenzopratesi.app.contacts.repository;

import java.util.List;

import org.unifi.lorenzopratesi.app.contacts.model.Contact;

public interface ContactRepository {

	public List<Contact> findAll();

	public Contact findById(String id);

	public Contact findByName(String name);

	public void save(Contact contact);

	public void updatePhone(Contact contact, String phone);

	public void updateEmail(Contact contact, String email);

	public void delete(String id);


}
