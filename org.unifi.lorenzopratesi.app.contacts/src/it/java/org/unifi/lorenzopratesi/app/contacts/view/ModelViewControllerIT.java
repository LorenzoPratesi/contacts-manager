package org.unifi.lorenzopratesi.app.contacts.view;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.unifi.lorenzopratesi.app.contacts.controller.ContactController;
import org.unifi.lorenzopratesi.app.contacts.model.Contact;
import org.unifi.lorenzopratesi.app.contacts.repository.mongo.ContactMongoRepository;
import org.unifi.lorenzopratesi.app.contacts.validation.controller.ControllerInputValidator;
import org.unifi.lorenzopratesi.app.contacts.view.swing.ContactSwingView;

import com.mongodb.MongoClient;

class ModelViewControllerIT {

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

	@Test
	void testAddContact() {
		// use the UI to add a contact...
		window.textBox("firstNameTextBox").setText("test");
		window.textBox("lastNameTextBox").setText("test");
		window.textBox("emailTextBox").setText("test@email.com");
		window.textBox("phoneTextBox").enterText("0000000000");
		window.button("addContactButton").click();
		// ...verify that it has been added to the database
		Contact contactAdded = contactRepository.findAll().get(0);
		assertThat(contactAdded).isEqualTo(new Contact(contactAdded.getId(), "test", "test", "0000000000", "test@email.com"));
	}

	@Test
	void testDeleteContact() {
		// add a contact needed for tests
		Contact contact = new Contact(new ObjectId().toString(), "existingTest", "existingTest", "0000000000",
				"existingTest@email.com");
		contactRepository.save(contact);
		// use the controller's allContacts to make the contact
		// appear in the GUI list
		GuiActionRunner.execute(() -> contactController.allContacts());
		// ...select the existing contact
		window.list().selectItem(0);
		window.button("deleteContactButton").click();
		// verify that the contact has been deleted from the db
		assertThat(contactRepository.findById(contact.getId())).isNull();
	}

}
