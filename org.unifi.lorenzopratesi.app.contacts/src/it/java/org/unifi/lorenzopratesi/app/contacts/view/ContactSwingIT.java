package org.unifi.lorenzopratesi.app.contacts.view;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.unifi.lorenzopratesi.app.contacts.controller.ContactController;
import org.unifi.lorenzopratesi.app.contacts.model.Contact;
import org.unifi.lorenzopratesi.app.contacts.repository.mongo.ContactMongoRepository;
import org.unifi.lorenzopratesi.app.contacts.validation.controller.ControllerInputValidator;
import org.unifi.lorenzopratesi.app.contacts.view.swing.ContactSwingView;

import com.mongodb.MongoClient;

@DisplayName("Integration Tests for Contact Swing View")
class ContactSwingIT {

	private static final String MONGO_CLIENT_HOST = "localhost";
	private static final String DATABASE_NAME = "contact-manager";
	private static final String COLLECTION_NAME = "contact";

	private ContactMongoRepository contactRepository;
	private ControllerInputValidator inputValidation;
	private ContactSwingView contactSwingView;
	private ContactController contactController;
	private FrameFixture window;

	@BeforeAll
	static void setUpOnce() {
		FailOnThreadViolationRepaintManager.install();
	}

	@BeforeEach
	void onSetUp() {
		// Set repositories and input validation.
		int mongoPort = Integer.parseInt(System.getProperty("mongo.port", "27017"));
		contactRepository = new ContactMongoRepository(new MongoClient(MONGO_CLIENT_HOST, mongoPort), DATABASE_NAME,
				COLLECTION_NAME);
		inputValidation = new ControllerInputValidator();

		// Clean the collections.
		contactRepository.findAll().forEach(c -> contactRepository.delete(c.getId()));

		// Set swing view.
		GuiActionRunner.execute(() -> {
			contactSwingView = new ContactSwingView();
			contactController = new ContactController(contactRepository, contactSwingView, inputValidation);
			contactSwingView.setContactController(contactController);
			return contactSwingView;
		});
		window = new FrameFixture(contactSwingView);
		window.show();
	}

	@AfterEach
	void tearDown() {
		window.cleanUp();
	}

	@Nested
	@DisplayName("Contact Tests")
	class ContactTests {

		@Test
		@DisplayName("All contact list request - testShowAllContacts()")
		void testShowAllContacts() {
			// Setup.
			Contact contact1 = new Contact("testFirstName1", "testLastName1", "1111111111", "test1@email.com");
			Contact contact2 = new Contact("testFirstName2", "testLastName2", "2222222222", "test2@email.com");
			contactRepository.save(contact1);
			contactRepository.save(contact2);

			// Execute.
			GuiActionRunner.execute(() -> contactController.allContacts());

			// Verify.
			assertThat(window.list().contents()).containsExactly(contact1.toString(), contact2.toString());
		}

		@Test
		@DisplayName("Add contact button success - testAddContactButtonSuccess()")
		void testAddContactButtonSuccess() {
			window.textBox("firstNameTextBox").enterText("test");
			window.textBox("lastNameTextBox").enterText("test");
			window.textBox("emailTextBox").setText("test@email.com");
			window.textBox("phoneTextBox").enterText("0000000000");
			window.button("addContactButton").click();
			Contact newContact = contactRepository.findAll().get(0);

			assertThat(window.list().contents()).containsExactly(newContact.toString());
		}

		@Test
		@DisplayName("Delete contact button success - testDeleteContactButtonSuccess()")
		void testDeleteContactButtonSuccess() {
			GuiActionRunner.execute(() -> contactController
					.newContact(new Contact("testFirstName", "testLastName", "1234567890", "test@email.com")));
			window.list().selectItem(0);
			window.button("deleteContactButton").click();
			assertThat(window.list().contents()).isEmpty();
		}

		@Test
		@DisplayName("Add contact button error when email is not Valid - testAddContactButtonErrorWhenEmailIsNotValid()")
		void testAddContactButtonErrorWhenEmailIsNotValid() {
			window.textBox("firstNameTextBox").enterText("test");
			window.textBox("lastNameTextBox").enterText("test");
			window.textBox("phoneTextBox").enterText("0000000000");
			window.textBox("emailTextBox").enterText("email");
			window.button("addContactButton").click();
			assertThat(window.list().contents()).isEmpty();
			window.label("messageLabel")
					.requireText("Contact Email is not valid: email. Format must be similar to prefix@domain.");
		}

		@Test
		@DisplayName("Delete contact button error when contact is not in the database - testDeleteContactButtonErrorWhenContactIsNotInTheDB()")
		void testDeleteContactButtonErrorWhenContactIsNotInTheDB() {
			// Setup.
			Contact contact = new Contact(new ObjectId().toString(), "testFirstName", "testLastName", "1111111111",
					"test@email.com");
			GuiActionRunner.execute(() -> contactSwingView.getListContactsModel().addElement(contact));

			// Execute.
			window.list().selectItem(0);
			window.button("deleteContactButton").click();

			// Verify.
			assertThat(window.list().contents()).containsExactly(contact.toString());
			window.label("messageLabel").requireText("There is no contact with id " + contact.getId() + ", "
					+ contact.getFirstName() + ", " + contact.getLastName());
		}

	}
}
