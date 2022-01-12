package org.unifi.lorenzopratesi.app.contacts.repository;

import java.util.List;

import org.unifi.lorenzopratesi.app.contacts.model.Contact;

public interface ContactRepository {

	List<Contact> findAll();

	List<Contact> findByName(String name);

	Contact findById(String id);

	void save(Contact contact);

	void updatePhone(String id, String phone);

	void updateEmail(String id, String email);

	void delete(String id);

}
