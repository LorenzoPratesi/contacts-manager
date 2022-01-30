package org.unifi.lorenzopratesi.app.contacts.view.swing;

import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.unifi.lorenzopratesi.app.contacts.controller.ContactController;
import org.unifi.lorenzopratesi.app.contacts.model.Contact;
import org.unifi.lorenzopratesi.app.contacts.view.ContactView;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.Font;

public class ContactSwingView extends JFrame implements ContactView {

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JTextField txtFirstName;
	private JTextField txtLastName;
	private JTextField txtPhone;
	private JTextField txtEmail;
	private transient ContactController contactController;
	private JLabel lblMessage;
	private JButton btnAddContact;
	private JList<Contact> listContacts;
	private DefaultListModel<Contact> listContactsModel;
	private JButton btnDeleteSelected;
	private JLabel lblContactList;
	private JLabel lblAddNewContact;
	private JLabel lblEditSelectedContact;
	private JLabel lblAttributeToEdit;

	@SuppressWarnings("rawtypes")
	private JComboBox comboBoxEditAttribute;
	private JButton btnEditAttribute;
	private JLabel lblNewAttributeValue;
	private JTextField textFieldNewAttribute;
	private JLabel lblInfoMessages;
	private JLabel lblNewLabel;
	private JTextField textFieldSearch;

	/**
	 * Create the frame.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ContactSwingView() {
		setTitle("Contact Manager");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 782, 544);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 86, 56, 0, 0, 0, 44, 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0, 0, 0, 40, 23, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE, 1.0, 0.0, 0.0, 0.0, 1.0 };
		gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		KeyAdapter btnAddContactEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				btnAddContact.setEnabled(addContactEnabledFields());
			}
		};

		lblContactList = new JLabel("Contacts");
		lblContactList.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		GridBagConstraints gbc_lblContactList = new GridBagConstraints();
		gbc_lblContactList.gridwidth = 5;
		gbc_lblContactList.insets = new Insets(0, 0, 5, 5);
		gbc_lblContactList.gridx = 0;
		gbc_lblContactList.gridy = 0;
		contentPane.add(lblContactList, gbc_lblContactList);

		lblAddNewContact = new JLabel("Add new contact");
		lblAddNewContact.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		GridBagConstraints gbc_lblAddNewContact = new GridBagConstraints();
		gbc_lblAddNewContact.anchor = GridBagConstraints.WEST;
		gbc_lblAddNewContact.gridwidth = 2;
		gbc_lblAddNewContact.insets = new Insets(0, 0, 5, 0);
		gbc_lblAddNewContact.gridx = 6;
		gbc_lblAddNewContact.gridy = 0;
		contentPane.add(lblAddNewContact, gbc_lblAddNewContact);

		lblNewLabel = new JLabel("Search");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 1;
		contentPane.add(lblNewLabel, gbc_lblNewLabel);

		textFieldSearch = new JTextField();
		textFieldSearch.setName("textFieldSearch");
		GridBagConstraints gbc_textFieldSearch = new GridBagConstraints();
		gbc_textFieldSearch.gridwidth = 4;
		gbc_textFieldSearch.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldSearch.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldSearch.gridx = 1;
		gbc_textFieldSearch.gridy = 1;
		contentPane.add(textFieldSearch, gbc_textFieldSearch);
		textFieldSearch.setColumns(10);

		textFieldSearch.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				filter();
			}

			private void filter() {
				listContactsModel.clear();
				contactController.findByName(textFieldSearch.getText());
			}
		});

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridheight = 14;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridwidth = 5;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 2;
		contentPane.add(scrollPane, gbc_scrollPane);

		listContactsModel = new DefaultListModel<>();
		listContacts = new JList<>(listContactsModel);
		listContacts.addListSelectionListener(e -> {
			boolean isNotEmpty = listContacts.getSelectedIndex() != -1;
			btnDeleteSelected.setEnabled(isNotEmpty);
			lblEditSelectedContact.setEnabled(isNotEmpty);
			lblAttributeToEdit.setEnabled(isNotEmpty);
			comboBoxEditAttribute.setEnabled(isNotEmpty);
			lblNewAttributeValue.setEnabled(isNotEmpty);
			textFieldNewAttribute.setEnabled(isNotEmpty);
		});
		scrollPane.setViewportView(listContacts);
		listContacts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listContacts.setName("contactsList");

		JLabel lblNewLabel_1 = new JLabel("First Name");
		lblNewLabel_1.setName("firstNameLabel");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 6;
		gbc_lblNewLabel_1.gridy = 1;
		contentPane.add(lblNewLabel_1, gbc_lblNewLabel_1);

		txtFirstName = new JTextField();
		txtFirstName.addKeyListener(btnAddContactEnabler);
		txtFirstName.setName("firstNameTextBox");
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.insets = new Insets(0, 0, 5, 0);
		gbc_textField_1.gridx = 7;
		gbc_textField_1.gridy = 1;
		contentPane.add(txtFirstName, gbc_textField_1);
		txtFirstName.setColumns(10);

		JLabel lblNewLabel_2 = new JLabel("Last Name");
		lblNewLabel_2.setName("lastNameLabel");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 6;
		gbc_lblNewLabel_2.gridy = 2;
		contentPane.add(lblNewLabel_2, gbc_lblNewLabel_2);

		txtLastName = new JTextField();
		txtLastName.addKeyListener(btnAddContactEnabler);
		txtLastName.setName("lastNameTextBox");
		GridBagConstraints gbc_textField_2 = new GridBagConstraints();
		gbc_textField_2.insets = new Insets(0, 0, 5, 0);
		gbc_textField_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_2.gridx = 7;
		gbc_textField_2.gridy = 2;
		contentPane.add(txtLastName, gbc_textField_2);
		txtLastName.setColumns(10);

		JLabel lblNewLabel_3 = new JLabel("Phone");
		lblNewLabel_3.setName("phoneLabel");
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridx = 6;
		gbc_lblNewLabel_3.gridy = 3;
		contentPane.add(lblNewLabel_3, gbc_lblNewLabel_3);

		txtPhone = new JTextField();
		txtPhone.addKeyListener(btnAddContactEnabler);
		txtPhone.setName("phoneTextBox");
		GridBagConstraints gbc_textField_3 = new GridBagConstraints();
		gbc_textField_3.insets = new Insets(0, 0, 5, 0);
		gbc_textField_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_3.gridx = 7;
		gbc_textField_3.gridy = 3;
		contentPane.add(txtPhone, gbc_textField_3);
		txtPhone.setColumns(10);

		JLabel lblNewLabel_4 = new JLabel("Email");
		lblNewLabel_4.setName("emailLabel");
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_4.gridx = 6;
		gbc_lblNewLabel_4.gridy = 4;
		contentPane.add(lblNewLabel_4, gbc_lblNewLabel_4);

		txtEmail = new JTextField();
		txtEmail.addKeyListener(btnAddContactEnabler);
		txtEmail.setName("emailTextBox");
		GridBagConstraints gbc_textField_4 = new GridBagConstraints();
		gbc_textField_4.insets = new Insets(0, 0, 5, 0);
		gbc_textField_4.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_4.gridx = 7;
		gbc_textField_4.gridy = 4;
		contentPane.add(txtEmail, gbc_textField_4);
		txtEmail.setColumns(10);

		btnDeleteSelected = new JButton("Delete Selected");
		btnDeleteSelected.addActionListener(e -> contactController.deleteContact(listContacts.getSelectedValue()));

		btnAddContact = new JButton("Add Contact");
		btnAddContact.setName("addContactButton");
		btnAddContact.addActionListener(e -> contactController.newContact(
				new Contact(txtFirstName.getText(), txtLastName.getText(), txtPhone.getText(), txtEmail.getText())));
		btnAddContact.setEnabled(false);
		GridBagConstraints gbc_btnAddContact = new GridBagConstraints();
		gbc_btnAddContact.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnAddContact.insets = new Insets(0, 0, 5, 0);
		gbc_btnAddContact.gridx = 7;
		gbc_btnAddContact.gridy = 5;
		contentPane.add(btnAddContact, gbc_btnAddContact);
		btnDeleteSelected.setName("deleteContactButton");
		btnDeleteSelected.setEnabled(false);
		GridBagConstraints gbc_btnDeleteSelected = new GridBagConstraints();
		gbc_btnDeleteSelected.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnDeleteSelected.insets = new Insets(0, 0, 5, 0);
		gbc_btnDeleteSelected.gridx = 7;
		gbc_btnDeleteSelected.gridy = 6;
		contentPane.add(btnDeleteSelected, gbc_btnDeleteSelected);

		lblEditSelectedContact = new JLabel("Edit selected contact");
		lblEditSelectedContact.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblEditSelectedContact.setEnabled(false);
		GridBagConstraints gbc_lblEditSelectedContact = new GridBagConstraints();
		gbc_lblEditSelectedContact.anchor = GridBagConstraints.WEST;
		gbc_lblEditSelectedContact.gridwidth = 2;
		gbc_lblEditSelectedContact.insets = new Insets(0, 0, 5, 0);
		gbc_lblEditSelectedContact.gridx = 6;
		gbc_lblEditSelectedContact.gridy = 8;
		contentPane.add(lblEditSelectedContact, gbc_lblEditSelectedContact);

		lblAttributeToEdit = new JLabel("Attribute");
		lblAttributeToEdit.setEnabled(false);
		GridBagConstraints gbc_lblAttributeToEdit = new GridBagConstraints();
		gbc_lblAttributeToEdit.anchor = GridBagConstraints.WEST;
		gbc_lblAttributeToEdit.insets = new Insets(0, 0, 5, 5);
		gbc_lblAttributeToEdit.gridx = 6;
		gbc_lblAttributeToEdit.gridy = 9;
		contentPane.add(lblAttributeToEdit, gbc_lblAttributeToEdit);

		comboBoxEditAttribute = new JComboBox<>();
		comboBoxEditAttribute.setName("comboBoxEditAttribute");
		comboBoxEditAttribute.setEnabled(false);
		comboBoxEditAttribute.setModel(new DefaultComboBoxModel(new String[] { "Phone", "Email" }));
		GridBagConstraints gbc_comboBoxEditAttribute = new GridBagConstraints();
		gbc_comboBoxEditAttribute.insets = new Insets(0, 0, 5, 0);
		gbc_comboBoxEditAttribute.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBoxEditAttribute.gridx = 7;
		gbc_comboBoxEditAttribute.gridy = 9;
		contentPane.add(comboBoxEditAttribute, gbc_comboBoxEditAttribute);

		lblNewAttributeValue = new JLabel("New value");
		lblNewAttributeValue.setEnabled(false);
		GridBagConstraints gbc_lblNewAttributeValue = new GridBagConstraints();
		gbc_lblNewAttributeValue.anchor = GridBagConstraints.WEST;
		gbc_lblNewAttributeValue.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewAttributeValue.gridx = 6;
		gbc_lblNewAttributeValue.gridy = 10;
		contentPane.add(lblNewAttributeValue, gbc_lblNewAttributeValue);

		textFieldNewAttribute = new JTextField();
		textFieldNewAttribute.setName("newAttributeTextBox");
		textFieldNewAttribute.setEnabled(false);
		GridBagConstraints gbc_textFieldNewAttribute = new GridBagConstraints();
		gbc_textFieldNewAttribute.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldNewAttribute.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldNewAttribute.gridx = 7;
		gbc_textFieldNewAttribute.gridy = 10;
		contentPane.add(textFieldNewAttribute, gbc_textFieldNewAttribute);
		textFieldNewAttribute.setColumns(10);
		KeyAdapter editProductButtonEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				btnEditAttribute.setEnabled(!textFieldNewAttribute.getText().isEmpty());
			}
		};
		textFieldNewAttribute.addKeyListener(editProductButtonEnabler);

		btnEditAttribute = new JButton("Edit");
		btnEditAttribute.setName("editContactButton");
		btnEditAttribute.setEnabled(false);
		GridBagConstraints gbc_btnEditAttribute = new GridBagConstraints();
		gbc_btnEditAttribute.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnEditAttribute.insets = new Insets(0, 0, 5, 0);
		gbc_btnEditAttribute.gridx = 7;
		gbc_btnEditAttribute.gridy = 11;
		contentPane.add(btnEditAttribute, gbc_btnEditAttribute);

		btnEditAttribute.addActionListener(e -> {
			Object selectedItem = comboBoxEditAttribute.getSelectedItem();
			if (selectedItem == "Phone") {
				contactController.updatePhone(listContacts.getSelectedValue(), textFieldNewAttribute.getText());
			}

			if (selectedItem == "Email") {
				contactController.updateEmail(listContacts.getSelectedValue(), textFieldNewAttribute.getText());
			}
		});

		lblInfoMessages = new JLabel("Info messages");
		lblInfoMessages.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		GridBagConstraints gbc_lblInfoMessages = new GridBagConstraints();
		gbc_lblInfoMessages.anchor = GridBagConstraints.WEST;
		gbc_lblInfoMessages.gridwidth = 2;
		gbc_lblInfoMessages.insets = new Insets(0, 0, 5, 0);
		gbc_lblInfoMessages.gridx = 6;
		gbc_lblInfoMessages.gridy = 13;
		contentPane.add(lblInfoMessages, gbc_lblInfoMessages);

		lblMessage = new JLabel("");
		lblMessage.setName("messageLabel");
		GridBagConstraints gbc_lblMessage = new GridBagConstraints();
		gbc_lblMessage.gridwidth = 2;
		gbc_lblMessage.gridheight = 2;
		gbc_lblMessage.gridx = 6;
		gbc_lblMessage.gridy = 14;
		contentPane.add(lblMessage, gbc_lblMessage);

	}

	protected boolean addContactEnabledFields() {
		return !txtFirstName.getText().trim().isEmpty() && !txtLastName.getText().trim().isEmpty()
				&& !txtPhone.getText().trim().isEmpty() && !txtEmail.getText().trim().isEmpty();
	}

	@Override
	public void showContacts(List<Contact> contacts) {
		contacts.forEach(listContactsModel::addElement);
	}

	@Override
	public void contactAdded(Contact contact) {
		listContactsModel.addElement(contact);
		txtFirstName.setText("");
		txtLastName.setText("");
		txtEmail.setText("");
		txtPhone.setText("");
		btnAddContact.setEnabled(false);
		clearErrorLog();
	}

	private void clearErrorLog() {
		lblMessage.setText(" ");
	}

	@Override
	public void contactEdited(Contact contact) {
		OptionalInt contactIdx = contactExistsInModel(contact);
		if (contactIdx.isPresent()) {
			listContactsModel.set(contactIdx.getAsInt(), contact);
			textFieldNewAttribute.setText("");
			btnEditAttribute.setEnabled(false);
			clearErrorLog();
		} else {
			throw new IndexOutOfBoundsException(String.format("Internal error: %s not found.", contact));
		}
	}

	private OptionalInt contactExistsInModel(Contact contact) {
		return IntStream.range(0, listContactsModel.size())
				.filter(c -> listContactsModel.get(c).getId().equals(contact.getId())).findFirst();
	}

	@Override
	public void contactRemoved(Contact contactToDelete) {
		listContactsModel.removeElement(contactToDelete);
		clearErrorLog();
	}

	@Override
	public void showMessage(String message) {
		lblMessage.setText(message);
	}

	public void setContactController(ContactController contactController) {
		this.contactController = contactController;
	}

	public DefaultListModel<Contact> getListContactsModel() {
		return listContactsModel;
	}

}