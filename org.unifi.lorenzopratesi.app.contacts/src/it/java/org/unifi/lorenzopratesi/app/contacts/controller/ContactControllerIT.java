package org.unifi.lorenzopratesi.app.contacts.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.bson.types.ObjectId;

import static java.util.Arrays.asList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unifi.lorenzopratesi.app.contacts.model.Contact;
import org.unifi.lorenzopratesi.app.contacts.repository.ContactRepository;
import org.unifi.lorenzopratesi.app.contacts.repository.mongo.ContactMongoRepository;
import org.unifi.lorenzopratesi.app.contacts.validation.InputValidation;
import org.unifi.lorenzopratesi.app.contacts.validation.controller.ControllerInputValidator;
import org.unifi.lorenzopratesi.app.contacts.view.ContactView;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

class ContactControllerIT {

	@Mock
	private ContactView contactView;

	private ContactRepository contactRepository;

	private ContactController contactController;

	private InputValidation inputValidation;

	private AutoCloseable closeable;

	private static int mongoPort = Integer.parseInt(System.getProperty("mongo.port", "27017"));

	@BeforeEach
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		contactRepository = new ContactMongoRepository(new MongoClient(new ServerAddress("localhost", mongoPort)));
		inputValidation = new ControllerInputValidator();
		contactRepository.findAll().forEach(c -> contactRepository.delete(c.getId()));
		
		contactController = new ContactController(contactRepository, contactView, inputValidation);
	}

	@AfterEach
	public void releaseMocks() throws Exception {
		closeable.close();
	}

	@Test
	void testAllGuests() {
		Contact contact1 = new Contact("testFirstName1", "testLastName1", "1111111111", "test1@email.com");
		Contact contact2 = new Contact("testFirstName2", "testLastName2", "2222222222", "test2@email.com");
		contactRepository.save(contact1);
		contactRepository.save(contact2);
		contactController.allContacts();
		verify(contactView).listContacts(asList(contact1, contact2));
		verifyNoMoreInteractions(contactView);
	}
	
	@Test
	void testNewContact() {
		contactController.newContact(new Contact("testFirstName", "testLastName", "1234567890", "test@email.com"));
		Contact newContact = contactRepository.findAll().get(0);
		verify(contactView).contactAdded(newContact);
	}
	
	@Test
	void testDeleteGuest() {
		Contact contactToDelete = new Contact(new ObjectId().toString(), "testFirstName", "testLastName",
				"0000000000", "test@email.com");
		contactRepository.save(contactToDelete);
		contactController.deleteContact(contactToDelete);
		verify(contactView).contactRemoved(contactToDelete);
		verifyNoMoreInteractions(contactView);
	}


}
