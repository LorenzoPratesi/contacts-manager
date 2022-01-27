package org.unifi.lorenzopratesi.app.contacts.repository.mongo;


import java.util.List;
import static java.util.stream.Collectors.toList;
import java.util.stream.StreamSupport;

import static java.util.Arrays.asList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.unifi.lorenzopratesi.app.contacts.model.Contact;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@Testcontainers
@DisplayName("Integration Tests for Contact Mongo Repository")
class ContactMongoRepositoryTestcontainersIT {
	
	private static final String DATABASE_NAME = "contact-manager";
	private static final String COLLECTION_NAME = "contact";

	@Container
	static final MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");

	private MongoClient client;
	private ContactMongoRepository contactMongoRepository;
	private MongoCollection<Contact> contactCollection;

	@BeforeEach
	void setup() {
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getFirstMappedPort()));
		contactMongoRepository = new ContactMongoRepository(client, DATABASE_NAME, COLLECTION_NAME);
		CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		MongoDatabase database = client.getDatabase(DATABASE_NAME).withCodecRegistry(pojoCodecRegistry);
		database.drop();
		contactCollection = database.getCollection(COLLECTION_NAME, Contact.class);
	}

	@AfterEach
	void tearDown() {
		client.close();
	}
	
	@Test
	@DisplayName("Find all contact in the collection - testFindAll()")
	void testFindAll() {
		Contact contact1 = new Contact("testFirstName1", "testLastName1", "1111111111", "test1@email.com");
		Contact contact2 = new Contact("testFirstName2", "testLastName2", "2222222222", "test2@email.com");
		contactCollection.insertMany(asList(contact1, contact2));
		assertThat(contactMongoRepository.findAll()).containsExactly(
				new Contact(contact1.getId(), "testFirstName1", "testLastName1", "1111111111", "test1@email.com"),
				new Contact(contact2.getId(), "testFirstName2", "testLastName2", "2222222222", "test2@email.com"));
	}
	
	@Test
	@DisplayName("Save a contact in the collection - testSave()")
	void testSave() {
		Contact contactToSave = new Contact("testFirstName1", "testLastName1", "1111111111", "test1@email.com");
		contactMongoRepository.save(contactToSave);
		assertThat(getContactsList()).containsExactly(
				new Contact(contactToSave.getId(), "testFirstName1", "testLastName1", "1111111111", "test1@email.com"));
	}

	@Test
	@DisplayName("Find a contact in the collection with his id - testFindById()")
	void testFindById() {
		Contact contactToFind = new Contact("testFirstName1", "testLastName1", "1111111111", "test1@email.com");
		Contact anotherContact = new Contact("testFirstName2", "testLastName2", "2222222222", "test2@email.com");
		contactCollection.insertMany(asList(contactToFind, anotherContact));
		assertThat(contactMongoRepository.findById(contactToFind.getId())).isEqualTo(
				new Contact(contactToFind.getId(), "testFirstName1", "testLastName1", "1111111111", "test1@email.com"));

	}

	@Test
	@DisplayName("Delete a contact in the collection with his id - testDelete()")
	void testDelete() {
		Contact contactToDelete = new Contact("testFirstName1", "testLastName1", "1111111111", "test1@email.com");
		Contact anotherContact = new Contact("testFirstName2", "testLastName2", "2222222222", "test2@email.com");
		contactCollection.insertMany(asList(contactToDelete, anotherContact));
		contactMongoRepository.delete(contactToDelete.getId());
		assertThat(getContactsList()).containsExactly(anotherContact);
	}
	
	@Test
	@DisplayName("Update contact phone in the collection with his id - testUpdatePhone()")
	void testUpdatePhone() {
		Contact contactToUpdate = new Contact("testFirstName1", "testLastName1", "1111111111", "test1@email.com");
		contactCollection.insertMany(asList(contactToUpdate));
		contactMongoRepository.updatePhone(contactToUpdate.getId(), "0000000000");

		Contact contactUpdated = new Contact(contactToUpdate.getId(), "testFirstName1", "testLastName1", "0000000000", "test1@email.com");
		assertThat(getContactsList()).containsExactly(contactUpdated);
	}
	
	@Test
	@DisplayName("Update contact email in the collection with his id - testUpdateEmail()")
	void testUpdateEmail() {
		Contact contactToUpdate = new Contact("testFirstName1", "testLastName1", "1111111111", "test1@email.com");
		contactCollection.insertMany(asList(contactToUpdate));
		contactMongoRepository.updateEmail(contactToUpdate.getId(), "test2@email.com");

		Contact contactUpdated = new Contact(contactToUpdate.getId(), "testFirstName1", "testLastName1", "1111111111", "test2@email.com");
		assertThat(getContactsList()).containsExactly(contactUpdated);
	}

	private List<Contact> getContactsList() {
		return StreamSupport.stream(contactCollection.find().spliterator(), false).collect(toList());
	}

}
