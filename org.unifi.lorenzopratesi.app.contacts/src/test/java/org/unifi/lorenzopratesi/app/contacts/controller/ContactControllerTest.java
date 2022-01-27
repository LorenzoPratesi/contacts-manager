package org.unifi.lorenzopratesi.app.contacts.controller;

import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.unifi.lorenzopratesi.app.contacts.model.Contact;

import org.unifi.lorenzopratesi.app.contacts.repository.ContactRepository;
import org.unifi.lorenzopratesi.app.contacts.validation.InputValidation;
import org.unifi.lorenzopratesi.app.contacts.view.ContactView;

import static java.util.Arrays.asList;

@DisplayName("Tests for Contact Controller")
class ContactControllerTest {

	@Mock
	private ContactRepository contactRepository;

	@Mock
	private ContactView contactView;

	@Mock
	private InputValidation inputValidation;

	@InjectMocks
	private ContactController contactController;

	private AutoCloseable closeable;

	@BeforeEach
	void setup() {
		closeable = MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	void releaseMocks() throws Exception {
		closeable.close();
	}

	@Nested
	@DisplayName("Contact Controller Happy Cases")
	class HappyCases {

		@Test
		@DisplayName("All Contact list request - testFindAllContacts()")
		void testFindAllContacts() {
			Contact contact1 = new Contact("1", "testFirstName1", "testLastName1", "1111111111", "test1@email.com");
			Contact contact2 = new Contact("2", "testFirstName2", "testLastName2", "2222222222", "test2@email.com");
			List<Contact> contacts = asList(contact1, contact2);
			when(contactRepository.findAll()).thenReturn(contacts);
			contactController.allContacts();
			verify(contactView).showContacts(contacts);
		}

		@Test
		@DisplayName("Find contact by name request - testFindContactByName()")
		void testFindContactByName() {
			Contact existingContact = new Contact("1", "testFirstName", "testLastName", "1234567890", "test@email.com");
			when(contactRepository.findByName("testFirstName")).thenReturn(asList(existingContact));
			contactController.findByName("testFirstName");
			verify(contactView).showContacts(asList(existingContact));
		}

		@Test
		@DisplayName("New contact request when infos are valid  - testAddContactWhenContactInfosAreValid()")
		void testAddContactWhenContactInfosAreValid() {
			Contact newContact = new Contact("testFirstName", "testLastName", "1234567890", "test@email.com");

			when(inputValidation.validatePhone("1234567890")).thenReturn(true);
			when(inputValidation.validateEmail("test@email.com")).thenReturn(true);
			when(contactRepository.findById(newContact.getId())).thenReturn(null);

			contactController.newContact(newContact);
			InOrder inOrder = inOrder(contactRepository, contactView, contactView);
			inOrder.verify(contactRepository).save(newContact);
			inOrder.verify(contactView).contactAdded(newContact);
			inOrder.verify(contactView).showMessage("Contact added");
		}

		@Test
		@DisplayName("New contact request when infos are valid and contact with same id does not exist yet  - testAddContactWhenInfoAreValidAndContactWithSameIdDoesNotAlreadyExist()")
		void testAddContactWhenInfoAreValidAndContactWithSameIdDoesNotAlreadyExist() {
			Contact newContact = new Contact("1", "testFirstName", "testLastName", "1234567890", "test@email.com");

			when(contactRepository.findById(newContact.getId())).thenReturn(null);
			when(inputValidation.validatePhone("1234567890")).thenReturn(true);
			when(inputValidation.validateEmail("test@email.com")).thenReturn(true);
			contactController.newContact(newContact);

			InOrder inOrder = inOrder(contactRepository, contactView, contactView);
			inOrder.verify(contactRepository).save(newContact);
			inOrder.verify(contactView).contactAdded(newContact);
			inOrder.verify(contactView).showMessage("Contact added");
		}

		@Test
		@DisplayName("Delete contact request when contact exist - testDeleteContactWhenContactExist()")
		void testDeleteContactWhenContactExist() {
			Contact contactToDelete = new Contact("1", "testFirstName", "testLastName", "0000000000", "test@email.com");
			when(contactRepository.findById(contactToDelete.getId())).thenReturn(contactToDelete);
			contactController.deleteContact(contactToDelete);
			InOrder inOrder = inOrder(contactRepository, contactView);
			inOrder.verify(contactRepository).delete(contactToDelete.getId());
			inOrder.verify(contactView).contactRemoved(contactToDelete);
			inOrder.verify(contactView).showMessage("Contact deleted");
		}

		@Test
		@DisplayName("Update contact phone request when contact exist and phone is valid - testUpdateContactPhoneWhenContactExistAndPhoneIsValid()")
		void testUpdateContactPhoneWhenContactExistAndPhoneIsValid() {
			Contact contactToUpdate = new Contact("1", "testFirstName", "testLastName", "0000000000", "test@email.com");
			when(contactRepository.findById(contactToUpdate.getId())).thenReturn(contactToUpdate);
			when(inputValidation.validatePhone("1111111111")).thenReturn(true);

			contactController.updatePhone(contactToUpdate, "1111111111");

			Contact updatedContact = new Contact("1", "testFirstName", "testLastName", "1111111111", "test@email.com");
			InOrder inOrder = inOrder(contactRepository, contactView, contactView);
			inOrder.verify(contactRepository).updatePhone(contactToUpdate.getId(), "1111111111");
			inOrder.verify(contactView).contactEdited(updatedContact);
			inOrder.verify(contactView).showMessage("Contact phone changed");
		}

		@Test
		@DisplayName("Update contact email request when contact exist and email is valid - testUpdateContactEmailWhenContactExistAndEmailIsValid()")
		void testUpdateContactEmailWhenContactExistAndEmailIsValid() {
			Contact contactToUpdate = new Contact("1", "testFirstName", "testLastName", "0000000000", "test@email.com");
			when(contactRepository.findById(contactToUpdate.getId())).thenReturn(contactToUpdate);
			when(inputValidation.validateEmail("newtest@email.com")).thenReturn(true);

			contactController.updateEmail(contactToUpdate, "newtest@email.com");

			Contact updatedContact = new Contact("1", "testFirstName", "testLastName", "0000000000",
					"newtest@email.com");
			InOrder inOrder = inOrder(contactRepository, contactView, contactView);
			inOrder.verify(contactRepository).updateEmail(contactToUpdate.getId(), "newtest@email.com");
			inOrder.verify(contactView).contactEdited(updatedContact);
			inOrder.verify(contactView).showMessage("Contact email changed");
		}
	}

	@Nested
	@DisplayName("Contact Controller Exceptional Cases")
	class ExceptionalCases {

		@Test
		@DisplayName("New contact request when phone is not valid - testAddContactWhenPhoneIsNotValid()")
		void testAddContactWhenPhoneIsNotValid() {
			Contact newContactPhoneNotValid = new Contact("testFirstName", "testLastName", "telephoneNumNotValid",
					"test@email.com");

			when(inputValidation.validateEmail("test@email.com")).thenReturn(true);
			when(inputValidation.validatePhone("telephoneNumNotValid")).thenReturn(false);
			when(contactRepository.findById(newContactPhoneNotValid.getId())).thenReturn(null);
			contactController.newContact(newContactPhoneNotValid);

			InOrder inOrder = inOrder(contactRepository, contactView, contactView);
			inOrder.verify(contactView).showMessage(
					"Contact Phone is not valid: telephoneNumNotValid. Format must be similar to +10000000000.");
			verifyNoMoreInteractions(contactView, contactRepository);
		}

		@Test
		@DisplayName("New contact request when email is not valid - testAddContactWhenEmailIsNotValid()")
		void testAddContactWhenEmailIsNotValid() {
			Contact newContactEmailNotValid = new Contact("testFirstName", "testLastName", "1234567890",
					"emailNotValid");

			when(inputValidation.validateEmail("emailNotValid")).thenReturn(false);
			contactController.newContact(newContactEmailNotValid);
			verify(contactView)
					.showMessage("Contact Email is not valid: emailNotValid. Format must be similar to prefix@domain.");
			verifyNoInteractions(contactRepository);
			verifyNoMoreInteractions(contactView);
		}

		@Test
		@DisplayName("New contact request when contact infos are valid but contact already exists shoud not create new entry - testAddContactWhenInfoAreValidAndContactWithSameIdExistsShouldNotCreateNewEntry()")
		void testAddContactWhenInfoAreValidAndContactWithSameIdExistsShouldNotCreateNewEntry() {
			Contact existingContact = new Contact("1", "testFirstName", "testLastName", "1234567890", "test@email.com");
			Contact newContact = new Contact("1", "testFirstName", "testLastName", "1234567890", "test@email.com");

			when(inputValidation.validatePhone("1234567890")).thenReturn(true);
			when(inputValidation.validateEmail("test@email.com")).thenReturn(true);
			when(contactRepository.findById(newContact.getId())).thenReturn(existingContact);
			contactController.newContact(newContact);
			verify(contactView).showMessage("Contact with id 1 already exists.");
			verify(contactRepository, Mockito.times(0)).save(newContact);
		}

		@Test
		@DisplayName("Delete contact request when contact not exist - testDeleteContactWhenContactNotExist()")
		void testDeleteContactWhenContactNotExist() {
			Contact contactNotPresent = new Contact("1", "testFirstName", "testLastName", "0000000000",
					"test@email.com");
			when(contactRepository.findById(contactNotPresent.getId())).thenReturn(null);
			contactController.deleteContact(contactNotPresent);
			verify(contactView).showMessage(String.format("There is no contact with id %s, %s, %s",
					contactNotPresent.getId(), contactNotPresent.getFirstName(), contactNotPresent.getLastName()));
		}

		@Test
		@DisplayName("Update contact phone request when contact not exist - testUpdateContactPhoneWhenContactNotExist()")
		void testUpdateContactPhoneWhenContactNotExist() {
			Contact contactNotPresent = new Contact("1", "testFirstName", "testLastName", "0000000000",
					"test@email.com");
			when(contactRepository.findById(contactNotPresent.getId())).thenReturn(null);
			contactController.updatePhone(contactNotPresent, "1111111111");
			verify(contactView).showMessage(String.format("There is no contact with id %s, %s, %s",
					contactNotPresent.getId(), contactNotPresent.getFirstName(), contactNotPresent.getLastName()));
		}

		@Test
		@DisplayName("Update contact phone request when contact exist but new phone is not valid - testUpdateContactPhoneWhenContactExistButNewPhoneIsNotvalid()")
		void testUpdateContactPhoneWhenContactExistButNewPhoneIsNotvalid() {
			Contact contactToUpdate = new Contact("1", "testFirstName", "testLastName", "0000000000", "test@email.com");
			when(contactRepository.findById(contactToUpdate.getId())).thenReturn(contactToUpdate);
			when(inputValidation.validatePhone("telephoneNumNotValid")).thenReturn(false);
			contactController.updatePhone(contactToUpdate, "telephoneNumNotValid");

			verify(contactView).showMessage(
					"Contact Phone is not valid: telephoneNumNotValid. Format must be similar to +10000000000.");
		}

		@Test
		@DisplayName("Update contact email request when contact not exist - testUpdateContactEmailWhenContactNotExist()")
		void testUpdateContactEmailWhenContactNotExist() {
			Contact contactNotPresent = new Contact("1", "testFirstName", "testLastName", "0000000000",
					"test@email.com");
			when(contactRepository.findById(contactNotPresent.getId())).thenReturn(null);
			contactController.updateEmail(contactNotPresent, "newtest@email.com");
			verify(contactView).showMessage(String.format("There is no contact with id %s, %s, %s",
					contactNotPresent.getId(), contactNotPresent.getFirstName(), contactNotPresent.getLastName()));
		}

		@Test
		@DisplayName("Update contact email request when contact exist but new email is not valid - testUpdateContactEmailWhenContactExistButNewEmailIsNotvalid()")
		void testUpdateContactEmailWhenContactExistButNewEmailIsNotvalid() {
			Contact contactToUpdate = new Contact("1", "testFirstName", "testLastName", "0000000000", "test@email.com");
			when(contactRepository.findById(contactToUpdate.getId())).thenReturn(contactToUpdate);
			when(inputValidation.validatePhone("emailNumNotValid")).thenReturn(false);
			contactController.updateEmail(contactToUpdate, "emailNumNotValid");

			verify(contactView).showMessage(
					"Contact Email is not valid: emailNumNotValid. Format must be similar to prefix@domain.");
		}
	}

}
