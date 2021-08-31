package org.unifi.lorenzopratesi.app.contacts.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unifi.lorenzopratesi.app.contacts.model.Contact;
import org.unifi.lorenzopratesi.app.contacts.repository.ContactRepository;
import org.unifi.lorenzopratesi.app.contacts.view.ContactView;

import static java.util.Arrays.asList;

class ContactControllerTest {
	
	@Mock
	private ContactRepository contactRepository;
	
	@Mock
	private ContactView contactView;
	
	@InjectMocks
	private ContactController contactController;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	void testFindAllContacts() {
		List<Contact> contacts = asList(new Contact());
		when(contactRepository.findAllContacts()).thenReturn(contacts);
		contactController.allContacts();
		verify(contactView).listContacts(contacts);
	}
	
	@Test
	void testAddContactWhenContactWithSameNameDoesNotAlreadyExist() {
		Contact newContact = new Contact("name", "surname", "000000000");
		when(contactRepository.findByName("name")).thenReturn(null);
		contactController.addContact(newContact);
		InOrder inOrder = inOrder(contactRepository, contactView, contactView);
		inOrder.verify(contactRepository).addContact(newContact);
		inOrder.verify(contactView).contactAdded(newContact);
		inOrder.verify(contactView).showMessage("Product added", newContact, "");
	}

}
