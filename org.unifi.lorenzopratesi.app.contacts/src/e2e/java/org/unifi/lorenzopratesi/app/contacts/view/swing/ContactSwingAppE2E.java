package org.unifi.lorenzopratesi.app.contacts.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.regex.Pattern;

import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unifi.lorenzopratesi.app.contacts.model.Contact;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.model.Filters;

@RunWith(GUITestRunner.class)
public class ContactSwingAppE2E extends AssertJSwingJUnitTestCase { // NOSONAR (doesn't match regular expression for testcase's name)

	private static final String MONGO_CLIENT_HOST = "localhost";
	private static final String DATABASE_NAME = "contacthouse";
	private static final String CONTACT_COLLECTION_NAME = "contact";
	private static final String CONTACT_FIXTURE_1_ID = "6089e6550aeb977d93b1a169";
	private static final String CONTACT_FIXTURE_2_ID = "6089e73443ba0b25d9862a14";
	private static final int INITIAL_NUM_OF_CONTACTS = 2;
	private FrameFixture window;
	private MongoClient mongoClient;
	private CodecRegistry pojoCodecRegistry;

	@Override
	protected void onSetUp() {
		int mongoPort = Integer.parseInt(System.getProperty("mongo.port", "27017"));
		mongoClient = new MongoClient(MONGO_CLIENT_HOST, mongoPort);
		pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));

		// Reset database, add starting guests and bookings to the database.
		mongoClient.getDatabase(DATABASE_NAME).drop();
		addTestContactToDatabase(
				new Contact(CONTACT_FIXTURE_1_ID, "testFirstName1", "testLastName1", "1111111111", "test1@email.com"));
		addTestContactToDatabase(
				new Contact(CONTACT_FIXTURE_2_ID, "testFirstName2", "testLastName2", "2222222222", "test2@email.com"));

		// Start the Swing application.
		application("org.unifi.lorenzopratesi.app.contacts.app.swing.ContactSwingApp")
				.withArgs("--mongo-host=" + MONGO_CLIENT_HOST, "--mongo-port=" + mongoPort,
						"--db-name=" + DATABASE_NAME, "--db-contact-collection=" + CONTACT_COLLECTION_NAME)
				.start();

		// Get a reference of its JFrame.
		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "Contact Manager".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(robot());
	}

	@Override
	protected void onTearDown() {
		mongoClient.close();
	}

	@Test
	@GUITest
	public void testOnStartAllContactsAreShown() {
		assertThat(window.list().contents())
				.anySatisfy(
						e -> assertThat(e).contains("testFirstName1", "testLastName1", "1111111111", "test1@email.com"))
				.anySatisfy(e -> assertThat(e).contains("testFirstName2", "testLastName2", "2222222222",
						"test2@email.com"));
	}

	@Test
	@GUITest
	public void testAddContactButtonSuccess() {
		window.textBox("firstNameTextBox").enterText("contact");
		window.textBox("lastNameTextBox").enterText("contact");
		window.textBox("emailTextBox").setText("contact@email.com");
		window.textBox("phoneTextBox").enterText("0000000000");
		window.button("addContactButton").click();
		assertThat(window.list().contents())
				.anySatisfy(e -> assertThat(e).contains("contact", "contact", "0000000000", "contact@email.com"));
	}

	@Test
	@GUITest
	public void testDeleteContactButtonSuccess() {
		window.list().selectItem(Pattern.compile(".*testFirstName1.*testLastName1.*"));
		window.button("deleteContactButton").click();
		assertThat(window.list().contents()).noneMatch(e -> e.contains("testFirstName1"));
	}

	@Test
	@GUITest
	public void testAddContactButtonErrorWhenEmailIsNotValid() {
		window.textBox("firstNameTextBox").enterText("test");
		window.textBox("lastNameTextBox").enterText("test");
		window.textBox("phoneTextBox").enterText("0000000000");
		window.textBox("emailTextBox").enterText("email");
		window.button("addContactButton").click();
		assertThat(window.list().contents()).hasSize(INITIAL_NUM_OF_CONTACTS);
		assertThat(window.label("messageLabel").text()).contains("email", "Format must be similar to prefix@domain.");
	}

	@Test
	@GUITest
	public void testDeleteContactButtonErrorWhenContactIsNotInTheDB() {
		window.list().selectItem(Pattern.compile(".*testFirstName1.*testLastName1.*"));
		removeTestContactFromDatabase(CONTACT_FIXTURE_1_ID);
		window.button("deleteContactButton").click();
		assertThat(window.label("messageLabel").text()).contains("testFirstName1", "testLastName1");
	}

	@Test
	@GUITest
	public void testEditContactButtonWhenPhoneIsSelectedContactIsSelectedAndFieldIsFilled() {
		String newContactPhone = "0000000000";

		window.list("contactsList").selectItem(Pattern.compile(".*testFirstName1.*testLastName1.*"));
		window.comboBox("comboBoxEditAttribute").selectItem("Phone");
		window.textBox("newAttributeTextBox").enterText(newContactPhone);
		window.button("editContactButton").click();
		
		Contact updatedContact = new Contact(CONTACT_FIXTURE_1_ID, "testFirstName1", "testLastName1", newContactPhone, "test1@email.com");
		String[] listProducts = window.list().contents();
		assertThat(listProducts).contains(updatedContact.toString());
	}
	
	@Test
	@GUITest
	public void testEditContactButtonWhenEmailIsSelectedContactIsSelectedAndFieldIsFilledWithValidEmail() {
		String newContactEmail = "newemail@gmail.com";

		window.list("contactsList").selectItem(Pattern.compile(".*testFirstName1.*testLastName1.*"));
		window.comboBox("comboBoxEditAttribute").selectItem("Email");
		window.textBox("newAttributeTextBox").enterText(newContactEmail);
		window.textBox("newAttributeTextBox").setText(newContactEmail);
		window.button("editContactButton").click();
		
		Contact updatedContact = new Contact(CONTACT_FIXTURE_1_ID, "testFirstName1", "testLastName1", "1111111111", newContactEmail);
		String[] listProducts = window.list().contents();
		assertThat(listProducts).contains(updatedContact.toString());
	}
	
	
	@Test
	@GUITest
	public void testSearchWhenContactIsMatched() {
		window.textBox("textFieldSearch").enterText("testFirstName1");
		
		assertThat(window.list().contents())
				.anySatisfy(e -> assertThat(e).contains("testFirstName1", "testLastName1", "1111111111", "test1@email.com"));
	}
	
	@Test
	@GUITest
	public void testSearchWhenContactIsNoMatched() {
		window.textBox("textFieldSearch").enterText("testFirstName3");
		assertThat(window.list().contents()).isEmpty();
	}
	
	private void addTestContactToDatabase(Contact guest) {
		mongoClient.getDatabase(DATABASE_NAME).withCodecRegistry(pojoCodecRegistry)
				.getCollection(CONTACT_COLLECTION_NAME, Contact.class).insertOne(guest);
	}

	private void removeTestContactFromDatabase(String id) {
		mongoClient.getDatabase(DATABASE_NAME).withCodecRegistry(pojoCodecRegistry)
				.getCollection(CONTACT_COLLECTION_NAME).deleteOne(Filters.eq("_id", new ObjectId(id)));
	}

}