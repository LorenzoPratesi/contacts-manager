package org.unifi.lorenzopratesi.app.contacts.app.swing;

import java.awt.EventQueue;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.unifi.lorenzopratesi.app.contacts.controller.ContactController;
import org.unifi.lorenzopratesi.app.contacts.repository.mongo.ContactMongoRepository;
import org.unifi.lorenzopratesi.app.contacts.validation.controller.ControllerInputValidator;
import org.unifi.lorenzopratesi.app.contacts.view.swing.ContactSwingView;

import com.mongodb.MongoClient;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(mixinStandardHelpOptions = true)
public class ContactSwingApp implements Callable<Void> {

	@Option(names = { "--mongo-host" }, description = "MongoDB Host Address")
	private String mongoHost = "localhost";

	@Option(names = { "--mongo-port" }, description = "MongoDB Host Port")
	private int mongoPort = 27017;

	@Option(names = { "--db-name" }, description = "Database Name")
	private String databaseName = "contact-manager";

	@Option(names = { "--db-contact-collection" }, description = "Contact Collection Name")
	private String contactCollectionName = "contact";

	public static void main(String[] args) {
		new CommandLine(new ContactSwingApp()).execute(args);
	}

	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {
			try {
				ContactMongoRepository contactRepository = new ContactMongoRepository(new MongoClient(mongoHost, mongoPort),
						databaseName, contactCollectionName);
				
				ControllerInputValidator inputValidation = new ControllerInputValidator();
				ContactSwingView contactView = new ContactSwingView();
				ContactController contactController = new ContactController(contactRepository, contactView, inputValidation);
				
				contactView.setContactController(contactController);
				contactView.setVisible(true);
				contactController.allContacts();
			} catch (Exception e) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, "An exception was thrown!", e);
			}
		});
		return null;
	}

}
