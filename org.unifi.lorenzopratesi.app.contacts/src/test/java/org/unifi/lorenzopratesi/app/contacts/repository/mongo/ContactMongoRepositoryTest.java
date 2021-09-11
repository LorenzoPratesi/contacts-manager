package org.unifi.lorenzopratesi.app.contacts.repository.mongo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.net.InetSocketAddress;
import static java.util.Arrays.*;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.unifi.lorenzopratesi.app.contacts.model.Contact;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

class ContactMongoRepositoryTest {
	
	private static final String DATABASE_NAME = "contact-manager";
	private static final String COLLECTION_NAME = "contact";

	private static MongoServer server;
	private static InetSocketAddress serverAddress;
	private ContactMongoRepository contactMongoRepository;
	private MongoClient client;
	private MongoCollection<Contact> contactCollection;

	@BeforeAll
	static void setupServer() {
		server = new MongoServer(new MemoryBackend());
		serverAddress = server.bind();
	}

	@AfterAll
	static void shutdownServer() {
		server.shutdown();
	}

	@BeforeEach
	void setup() {
		client = new MongoClient(new ServerAddress(serverAddress));
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
	void testFindAllShouldReturnAnEmptyListWhenContactCollectionIsEmpty() {
		assertThat(contactMongoRepository.findAll()).isEmpty();
	}

	@Test
	void testFindAllShouldReturnAListOfAllContactsWhenCollectionIsNotEmpty() {
		Contact contact1 = new Contact("testFirstName1", "testLastName1", "1111111111", "test1@email.com");
		Contact contact2 = new Contact("testFirstName2", "testLastName2", "2222222222", "test2@email.com");
		contactCollection.insertMany(asList(contact1, contact2));
		assertThat(contactMongoRepository.findAll()).containsExactly(contact1, contact2);
	}

	@Test
	void testFindByIdShouldReturnNullWhenStringIdIsNotParsableIntoAnObjectId() {
		assertThat(contactMongoRepository.findById("1")).isNull();
		assertThat(contactMongoRepository.findById("-")).isNull();
		assertThat(contactMongoRepository.findById("$")).isNull();
		assertThat(contactMongoRepository.findById("aaa")).isNull();
	}

	@Test
	void testDeleteShouldDoNothingWhenStringIdIsNotParsableIntoAnObjectId() {
		Contact contact = new Contact("testFirstName1", "testLastName1", "test1@email.com", "1111111111");
		contactCollection.insertOne(contact);
		contactMongoRepository.delete("1");
		contactMongoRepository.delete("-");
		contactMongoRepository.delete("$");
		contactMongoRepository.delete("aaa");
		assertThat(contactCollection.countDocuments()).isEqualTo(1);
	}

	@Test
	void testFindByIdShouldReturnNullWhenGuestIdIsNotFound() {
		assertThat(contactMongoRepository.findById(new ObjectId().toString())).isNull();
	}

	@Test
	void testFindByIdShouldReturnTheGuestWhenIdIsFound() {
		Contact contactToFind = new Contact("testFirstName1", "testLastName1", "1111111111", "test1@email.com");
		Contact anotherContact = new Contact("testFirstName2", "testLastName2", "2222222222", "test2@email.com");
		contactCollection.insertMany(asList(contactToFind, anotherContact));
		assertThat(contactMongoRepository.findById(contactToFind.getId())).isEqualTo(contactToFind);
	}

	@Test
	void testSaveShouldSaveAContactInTheCollection() {
		Contact contact = new Contact("testFirstName", "testLastName", "1111111111", "test1@email.com");
		contactMongoRepository.save(contact);
		assertThat(contactCollection.find().first()).isEqualTo(contact);
	}

	@Test
	void testDeleteShouldDeleteAContactFromTheCollection() {
		Contact contactToDelete = new Contact("testFirstName", "testLastName", "1111111111", "test1@email.com");
		contactCollection.insertOne(contactToDelete);
		contactMongoRepository.delete(contactToDelete.getId());
		assertThat(contactCollection.countDocuments()).isZero();
	}

}
