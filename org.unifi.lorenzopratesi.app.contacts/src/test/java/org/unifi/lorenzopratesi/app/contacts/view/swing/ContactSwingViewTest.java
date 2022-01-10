package org.unifi.lorenzopratesi.app.contacts.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import javax.swing.DefaultListModel;

import static java.util.Arrays.*;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JListFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unifi.lorenzopratesi.app.contacts.controller.ContactController;
import org.unifi.lorenzopratesi.app.contacts.model.Contact;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContactSwingViewTest {

	private FrameFixture window;

	private ContactSwingView contactSwingView;

	@Mock
	private ContactController contactController;

	private AutoCloseable closeable;

	@BeforeEach
	protected void onSetUp() {
		closeable = MockitoAnnotations.openMocks(this);
		GuiActionRunner.execute(() -> {
			contactSwingView = new ContactSwingView();
			contactSwingView.setContactController(contactController);
			return contactSwingView;
		});
		window = new FrameFixture(contactSwingView);
		window.show();
	}

	@AfterEach
	protected void onTearDown() throws Exception {
		closeable.close();
		window.cleanUp();
	}

	@Nested
	@DisplayName("Inital State Tests")
	class InitalStateTests {

		@Test
		@DisplayName("Frame initial state - testFrameInitialState()")
		void testFrameInitialState() {
			window.requireTitle("Contact Manager");
		}

		@Test
		@DisplayName("Error Log initial state - testErrorLogInitialState()")
		void testInitialStateOfContactPane() {
			window.label("firstNameLabel").requireVisible().requireEnabled().requireText("First Name");
			window.textBox("firstNameTextBox").requireVisible().requireEnabled().requireEmpty();
			window.label("lastNameLabel").requireVisible().requireEnabled().requireText("Last Name");
			window.textBox("lastNameTextBox").requireVisible().requireEnabled().requireEmpty();
			window.label("phoneLabel").requireVisible().requireEnabled().requireText("Phone");
			window.textBox("phoneTextBox").requireVisible().requireEnabled().requireEmpty();
			window.label("emailLabel").requireVisible().requireEnabled().requireText("Email");
			window.textBox("emailTextBox").requireVisible().requireEnabled().requireEmpty();
			window.button("addContactButton").requireVisible().requireDisabled().requireText("Add Contact");
			window.list("contactsList").requireVisible().requireEnabled().requireNoSelection();
			window.button("deleteContactButton").requireVisible().requireDisabled().requireText("Delete Selected");
		}
	}

	@Nested
	@DisplayName("Content Pane Tests")
	class ContentPaneTests {

		@Test
		@DisplayName("Show Error should show the message in the error log  - testShowErrorShouldShowTheMessageInTheErrorLog()")
		void testShowErrorShouldShowTheMessageInTheErrorLog() {
			GuiActionRunner.execute(() -> contactSwingView.showMessage("Error message test"));
			window.label("messageLabel").requireText("Error message test");
		}

	}

	@Nested
	@DisplayName("Enabling Buttons Tests")
	class ContactEnablingButtonsTests {

		@Test
		@DisplayName("Add Contact Button should be enabled when contact info are not empty - testWhenContactNamesAndPhonesAreNotEmptyThenAddContactButtonShouldBeEnabled()")
		void testWhenContactOInfoAreNotEmptyThenAddContactButtonShouldBeEnabled() {
			window.textBox("firstNameTextBox").enterText("test");
			window.textBox("lastNameTextBox").enterText("test");
			window.textBox("phoneTextBox").enterText("test");
			window.textBox("emailTextBox").enterText("test");
			window.button("addContactButton").requireEnabled();
		}

		@Test
		@DisplayName("Add Contact Button should be disabled when contact infos are blank - testWhenSomeContactInfosAreBlankThenAddContactButtonShouldBeDisabled()")
		void testWhenSomeContactInfosAreBlankThenAddContactButtonShouldBeDisabled() {

			JTextComponentFixture firstNameTextBox = window.textBox("firstNameTextBox");
			JTextComponentFixture lastNameTextBox = window.textBox("lastNameTextBox");
			JTextComponentFixture phoneTextBox = window.textBox("phoneTextBox");
			JTextComponentFixture emailTextBox = window.textBox("emailTextBox");
			JButtonFixture addContactButton = window.button("addContactButton");

			firstNameTextBox.enterText(" ");
			lastNameTextBox.enterText("test");
			phoneTextBox.enterText("test");
			emailTextBox.enterText("test");
			addContactButton.requireDisabled();

			resetTextBoxFields(firstNameTextBox, lastNameTextBox, phoneTextBox, emailTextBox);

			firstNameTextBox.enterText("test");
			lastNameTextBox.enterText(" ");
			phoneTextBox.enterText("test");
			emailTextBox.enterText("test");
			addContactButton.requireDisabled();

			resetTextBoxFields(firstNameTextBox, lastNameTextBox, phoneTextBox, emailTextBox);

			firstNameTextBox.enterText("test");
			lastNameTextBox.enterText("test");
			phoneTextBox.enterText("test");
			emailTextBox.enterText(" ");
			addContactButton.requireDisabled();

			resetTextBoxFields(firstNameTextBox, lastNameTextBox, phoneTextBox, emailTextBox);

			firstNameTextBox.enterText("test");
			lastNameTextBox.enterText("test");
			phoneTextBox.enterText(" ");
			emailTextBox.enterText("test");
			addContactButton.requireDisabled();

			resetTextBoxFields(firstNameTextBox, lastNameTextBox, phoneTextBox, emailTextBox);
		}

		@Test
		@DisplayName("Delete Contact Button should be enabled only when a Contact is selected - testDeleteContactButtonShouldBeEnabledOnlyWhenAContactIsSelected()")
		void testDeleteContactButtonShouldBeEnabledOnlyWhenAContactIsSelected() {
			// Setup.
			Contact contact = new Contact("1", "testFirstName", "testLastName", "0000000000", "test@email.com");

			// Execute.
			GuiActionRunner.execute(() -> contactSwingView.getListContactsModel().addElement(contact));

			// Verify.
			JListFixture contactsList = window.list("contactsList");
			JButtonFixture deleteContactButton = window.button("deleteContactButton");
			contactsList.selectItem(0);
			deleteContactButton.requireEnabled();
			contactsList.clearSelection();
			deleteContactButton.requireDisabled();
		}

		@Test
		@DisplayName("Edit Contact Button should be enabled only when a Contact is selected - testDeleteContactButtonShouldBeEnabledOnlyWhenAContactIsSelected()")
		public void testEditContactButtonShouldBeEnabledWhenAContactIsSelectedAndFieldIsFilledWithString() {
			// Setup.
			Contact contact = new Contact("1", "testFirstName", "testLastName", "0000000000", "test@email.com");

			// Execute.
			GuiActionRunner.execute(() -> contactSwingView.getListContactsModel().addElement(contact));

			// Verify.
			JListFixture contactsList = window.list("contactsList");
			JButtonFixture editContactButton = window.button("editContactButton");
			contactsList.selectItem(0);

			window.textBox("newAttributeTextBox").enterText("testNewFirstName");
			editContactButton.requireEnabled();
		}

		private void resetTextBoxFields(JTextComponentFixture... textBoxFields) {
			stream(textBoxFields).forEach(f -> f.setText(""));
		}
	}

	@Nested
	@DisplayName("Contacts Methods tests")
	class ContactInterfaceMethodsTests {

		@Test
		@DisplayName("Show Contact should fill contact infos to the list - testShowContactsShouldAddContactDescriptionsToTheList()")
		void testShowContactsShouldAddContactDescriptionsToTheList() {
			// Setup.
			Contact contact1 = new Contact("1", "testFirstName1", "testLastName1", "0000000000", "test1@email.com");
			Contact contact2 = new Contact("2", "testFirstName2", "testLastName2", "1111111111", "test2@email.com");

			// Execute.
			GuiActionRunner.execute(() -> contactSwingView.showContacts(asList(contact1, contact2)));

			// Verify.
			String[] contactsListContent = window.list("contactsList").contents();
			assertThat(contactsListContent).containsExactly(contact1.toString(), contact2.toString());
		}

		@Test
		@DisplayName("Show Error Contact Not Found should show an error message - testShowErrorShouldShowInfoOnTheDedicatedErrorLabel()")
		void testShowErrorShouldShowInfoOnTheDedicatedErrorLabel() {
			Contact contact = new Contact("1", "testFirstName", "testLastName", "0000000000", "test@email.com");
			GuiActionRunner.execute(() -> contactSwingView.showMessage("Error message: " + contact));
			window.label("messageLabel").requireText("Error message: " + contact);
		}

		@Test
		@DisplayName("Contact Added should added contact to the list then clear the error log and Contact form - testContactAddedShouldAddedContactToTheListThenClearErrorLogAndContactForm()")
		void testContactAddedShouldAddedContactToTheListThenClearErrorLogAndContactForm() {
			// Setup.
			Contact contactToAdd = new Contact("1", "testFirstName", "testLastName", "0000000000", "test@email.com");

			// Execute.
			GuiActionRunner.execute(() -> contactSwingView.contactAdded(contactToAdd));

			// Verify.
			String[] contactsListContent = window.list("contactsList").contents();
			assertThat(contactsListContent).containsExactly(contactToAdd.toString());
			window.label("messageLabel").requireText(" ");
			window.textBox("firstNameTextBox").requireEmpty();
			window.textBox("lastNameTextBox").requireEmpty();
			window.textBox("phoneTextBox").requireEmpty();
			window.textBox("emailTextBox").requireEmpty();
		}

		@Test
		@DisplayName("Contact Removed should remove the contact from the list and then clear the error log label - testContactRemovedShouldRemoveTheContactFromTheListAndThenClearTheErrorLogLabel()")
		void testContactRemovedShouldRemoveTheContactFromTheListAndThenClearTheErrorLogLabel() {
			// Setup.
			Contact contactToRemove = new Contact("1", "testFirstName1", "testLastName1", "0000000000",
					"test1@email.com");
			Contact anotherContact = new Contact("2", "testFirstName2", "testLastName2", "1111111111",
					"test2@email.com");

			GuiActionRunner.execute(() -> {
				DefaultListModel<Contact> listContactsModel = contactSwingView.getListContactsModel();
				listContactsModel.addElement(contactToRemove);
				listContactsModel.addElement(anotherContact);
			});

			// Execute.
			GuiActionRunner.execute(() -> contactSwingView.contactRemoved(
					new Contact("1", "testFirstName1", "testLastName1", "0000000000", "test1@email.com")));

			// Verify.
			String[] contactsListContent = window.list("contactsList").contents();
			assertThat(contactsListContent).containsExactly(anotherContact.toString());
			window.label("messageLabel").requireText(" ");
		}

		@Test
		@DisplayName("Contact Edited should edit the contact from the list and then clear the error log label - testContactEditedShouldEditTheContactFromTheListAndThenClearTheErrorLogLabel()")
		void testContactEditedShouldEditTheContactFromTheListAndThenClearTheErrorLogLabel() {
			// Setup.
			Contact contactToEdit = new Contact("testFirstName1", "testLastName1", "0000000000", "test1@email.com");
			Contact anotherContact = new Contact("testFirstName2", "testLastName2", "1111111111", "test2@email.com");

			GuiActionRunner.execute(() -> {
				DefaultListModel<Contact> listContactsModel = contactSwingView.getListContactsModel();
				listContactsModel.addElement(contactToEdit);
			});

			// Execute.
			GuiActionRunner.execute(() -> contactSwingView.contactEdited(anotherContact));

			// Verify.
			String[] contactsListContent = window.list("contactsList").contents();
			assertThat(contactsListContent).containsExactly(anotherContact.toString());
			window.label("messageLabel").requireText(" ");
			window.textBox("newAttributeTextBox").requireText("");
		}

	}

	@Nested
	@DisplayName("Contacts Delegations Tests")
	class ContactsDelegationsTests {

		@Test
		@DisplayName("Add Contact Button should delegate to Contact controller newContact() - testAddContactButtonShouldDelegateToContactControllerNewContact()")
		void testAddContactButtonShouldDelegateToContactControllerNewContact() {
			// Setup.
			window.textBox("firstNameTextBox").enterText("test");
			window.textBox("lastNameTextBox").enterText("test");
			window.textBox("emailTextBox").setText("test@email.com");
			window.textBox("phoneTextBox").enterText("0000000000");

			// Execute.
			window.button("addContactButton").click();

			// Verify.
			verify(contactController).newContact(new Contact("test", "test", "0000000000", "test@email.com"));
		}

		@Test
		@DisplayName("Delete Contact Button should delegate to contact controller deleteContact() - testDeleteContactButtonShouldDelegateToContactControllerDeleteContact()")
		void testDeleteContactButtonShouldDelegateToContactControllerDeleteContact() {
			// Setup.
			Contact contactToDelete = new Contact("1", "testFirstName1", "testLastName1", "0000000000",
					"test1@email.com");
			Contact anotherContact = new Contact("2", "testFirstName2", "testLastName2", "1111111111",
					"test2@email.com");
			GuiActionRunner.execute(() -> {
				DefaultListModel<Contact> listContactsModel = contactSwingView.getListContactsModel();
				listContactsModel.addElement(contactToDelete);
				listContactsModel.addElement(anotherContact);
			});
			window.list("contactsList").selectItem(0);

			// Execute.
			window.button("deleteContactButton").click();

			// Verify.
			verify(contactController).deleteContact(contactToDelete);
		}

		@Test
		@DisplayName("Edit Contact Button should delegate to contact controller updatePhone() - testEditContactButtonShouldDelegateToContactControllerUpdatePhoneIfPhoneIsSelected()")
		void testEditContactButtonShouldDelegateToContactControllerUpdatePhoneIfPhoneIsSelected() {
			// Setup.
			Contact contactToEdit = new Contact("1", "testFirstName1", "testLastName1", "0000000000",
					"test1@email.com");

			GuiActionRunner.execute(() -> {
				DefaultListModel<Contact> listContactsModel = contactSwingView.getListContactsModel();
				listContactsModel.addElement(contactToEdit);
			});
			window.list("contactsList").selectItem(0);
			window.comboBox("comboBoxEditAttribute").selectItem("Phone");
			window.textBox("newAttributeTextBox").enterText("1111111111");

			// Execute.
			window.button("editContactButton").click();

			// Verify.
			verify(contactController).updatePhone(contactToEdit, "1111111111");
		}

		@Test
		@DisplayName("Edit Contact Button should delegate to contact controller updateEmail() - testEditContactButtonShouldDelegateToContactControllerUpdateEmailIfEmailIsSelected()")
		void testEditContactButtonShouldDelegateToContactControllerUpdateEmailIfEmailIsSelected() {
			// Setup.
			Contact contactToEdit = new Contact("1", "testFirstName1", "testLastName1", "0000000000",
					"test1@email.com");

			GuiActionRunner.execute(() -> {
				DefaultListModel<Contact> listContactsModel = contactSwingView.getListContactsModel();
				listContactsModel.addElement(contactToEdit);
			});
			window.list("contactsList").selectItem(0);
			window.comboBox("comboBoxEditAttribute").selectItem("Email");
			window.textBox("newAttributeTextBox").enterText("test");

			// Execute.
			window.button("editContactButton").click();

			// Verify.
			verify(contactController).updateEmail(contactToEdit, "test");
		}

	}

}
