package org.unifi.lorenzopratesi.app.contacts.repository;

import java.util.List;

import org.unifi.lorenzopratesi.app.contacts.model.Contact;

public interface ContactRepository {

	public List<Contact> findAll();

	public List<Contact> findByName(String name);

	public Contact findById(String id);

	public void save(Contact contact);

	public void updatePhone(String id, String phone);

	public void updateEmail(String id, String email);

	public void delete(String id);

}
