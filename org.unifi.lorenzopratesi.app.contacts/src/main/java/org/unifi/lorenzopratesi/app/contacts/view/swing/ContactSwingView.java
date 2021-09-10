package org.unifi.lorenzopratesi.app.contacts.view.swing;

import java.awt.EventQueue;
import java.util.List;

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

public class ContactSwingView extends JFrame implements ContactView {

	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JTextField txtFirstName;
	private JTextField txtLastName;
	private JTextField txtPhone;
	private JTextField txtEmail;
	private ContactController contactController;
	private JLabel lblMessage;
	private JButton btnAddContact;
	private JList<Contact> listContacts;
	private DefaultListModel<Contact> listContactsModel;
	private JButton btnDeleteSelected;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ContactSwingView frame = new ContactSwingView();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ContactSwingView() {
		setTitle("Contact Manager");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 592, 432);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);
		KeyAdapter btnAddContactEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				btnAddContact
						.setEnabled(!txtFirstName.getText().trim().isEmpty() && !txtLastName.getText().trim().isEmpty()
								&& (!txtPhone.getText().trim().isEmpty() || !txtEmail.getText().trim().isEmpty()));
			}
		};

		JLabel lblNewLabel_1 = new JLabel("First Name");
		lblNewLabel_1.setName("firstNameLabel");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 0;
		contentPane.add(lblNewLabel_1, gbc_lblNewLabel_1);

		txtFirstName = new JTextField();
		txtFirstName.addKeyListener(btnAddContactEnabler);
		txtFirstName.setName("firstNameTextBox");
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 5, 0);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 1;
		gbc_textField_1.gridy = 0;
		contentPane.add(txtFirstName, gbc_textField_1);
		txtFirstName.setColumns(10);

		JLabel lblNewLabel_2 = new JLabel("Last Name");
		lblNewLabel_2.setName("lastNameLabel");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 1;
		contentPane.add(lblNewLabel_2, gbc_lblNewLabel_2);

		txtLastName = new JTextField();
		txtLastName.addKeyListener(btnAddContactEnabler);
		txtLastName.setName("lastNameTextBox");
		GridBagConstraints gbc_textField_2 = new GridBagConstraints();
		gbc_textField_2.insets = new Insets(0, 0, 5, 0);
		gbc_textField_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_2.gridx = 1;
		gbc_textField_2.gridy = 1;
		contentPane.add(txtLastName, gbc_textField_2);
		txtLastName.setColumns(10);

		JLabel lblNewLabel_3 = new JLabel("Phone");
		lblNewLabel_3.setName("phoneLabel");
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridx = 0;
		gbc_lblNewLabel_3.gridy = 2;
		contentPane.add(lblNewLabel_3, gbc_lblNewLabel_3);

		txtPhone = new JTextField();
		txtPhone.addKeyListener(btnAddContactEnabler);
		txtPhone.setName("phoneTextBox");
		GridBagConstraints gbc_textField_3 = new GridBagConstraints();
		gbc_textField_3.insets = new Insets(0, 0, 5, 0);
		gbc_textField_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_3.gridx = 1;
		gbc_textField_3.gridy = 2;
		contentPane.add(txtPhone, gbc_textField_3);
		txtPhone.setColumns(10);

		JLabel lblNewLabel_4 = new JLabel("Email");
		lblNewLabel_4.setName("emailLabel");
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_4.gridx = 0;
		gbc_lblNewLabel_4.gridy = 3;
		contentPane.add(lblNewLabel_4, gbc_lblNewLabel_4);

		txtEmail = new JTextField();
		txtEmail.addKeyListener(btnAddContactEnabler);
		txtEmail.setName("emailTextBox");
		GridBagConstraints gbc_textField_4 = new GridBagConstraints();
		gbc_textField_4.insets = new Insets(0, 0, 5, 0);
		gbc_textField_4.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_4.gridx = 1;
		gbc_textField_4.gridy = 3;
		contentPane.add(txtEmail, gbc_textField_4);
		txtEmail.setColumns(10);

		btnAddContact = new JButton("Add Contact");
		btnAddContact.setName("addContactButton");
		btnAddContact.addActionListener(e -> contactController.newContact(
				new Contact(txtFirstName.getText(), txtLastName.getText(), txtPhone.getText(), txtEmail.getText())));
		btnAddContact.setEnabled(false);
		GridBagConstraints gbc_btnAddContact = new GridBagConstraints();
		gbc_btnAddContact.gridwidth = 3;
		gbc_btnAddContact.insets = new Insets(0, 0, 5, 0);
		gbc_btnAddContact.gridx = 0;
		gbc_btnAddContact.gridy = 4;
		contentPane.add(btnAddContact, gbc_btnAddContact);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridheight = 2;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 5;
		contentPane.add(scrollPane, gbc_scrollPane);

		listContactsModel = new DefaultListModel<>();
		listContacts = new JList<>(listContactsModel);
		listContacts.addListSelectionListener(e -> btnDeleteSelected.setEnabled(listContacts.getSelectedIndex() != -1));
		scrollPane.setViewportView(listContacts);
		listContacts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listContacts.setName("contactsList");

		btnDeleteSelected = new JButton("Delete Selected");
		btnDeleteSelected.addActionListener(e -> contactController.deleteContact(listContacts.getSelectedValue()));
		btnDeleteSelected.setName("deleteContactButton");
		btnDeleteSelected.setEnabled(false);
		GridBagConstraints gbc_btnDeleteSelected = new GridBagConstraints();
		gbc_btnDeleteSelected.insets = new Insets(0, 0, 5, 0);
		gbc_btnDeleteSelected.gridwidth = 2;
		gbc_btnDeleteSelected.gridx = 0;
		gbc_btnDeleteSelected.gridy = 7;
		contentPane.add(btnDeleteSelected, gbc_btnDeleteSelected);

		lblMessage = new JLabel("");
		lblMessage.setName("messageLabel");
		GridBagConstraints gbc_lblMessage = new GridBagConstraints();
		gbc_lblMessage.gridwidth = 2;
		gbc_lblMessage.gridx = 0;
		gbc_lblMessage.gridy = 8;
		contentPane.add(lblMessage, gbc_lblMessage);
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
		clearErrorLog();
	}

	private void clearErrorLog() {
		lblMessage.setText(" ");
	}

	@Override
	public void contactEdited(Contact contact) {
		// TODO Auto-generated method stub

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
