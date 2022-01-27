package org.unifi.lorenzopratesi.app.contacts.repository.mongo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.*;
import static java.util.Arrays.*;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.unifi.lorenzopratesi.app.contacts.model.Contact;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

@DisplayName("Tests for Contact Mongo Repository")
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

	@Nested
	@DisplayName("Contact Mongo Repository Happy Cases")
	class HappyCases {

		@Test
		@DisplayName("Find all should return a list of all contacts when collection is not empty - testFindAllShouldReturnAListOfAllContactsWhenCollectionIsNotEmpty()")
		void testFindAllShouldReturnAListOfAllContactsWhenCollectionIsNotEmpty() {
			Contact contact1 = new Contact("testFirstName1", "testLastName1", "1111111111", "test1@email.com");
			Contact contact2 = new Contact("testFirstName2", "testLastName2", "2222222222", "test2@email.com");
			contactCollection.insertMany(asList(contact1, contact2));
			assertThat(contactMongoRepository.findAll()).containsExactly(contact1, contact2);
		}

		@Test
		@DisplayName("Find by id should return the contact when id is found - testFindByIdShouldReturnTheContactWhenIdIsFound()")
		void testFindByIdShouldReturnTheContactWhenIdIsFound() {
			Contact contactToFind = new Contact("testFirstName1", "testLastName1", "1111111111", "test1@email.com");
			Contact anotherContact = new Contact("testFirstName2", "testLastName2", "2222222222", "test2@email.com");
			contactCollection.insertMany(asList(contactToFind, anotherContact));
			assertThat(contactMongoRepository.findById(contactToFind.getId())).isEqualTo(contactToFind);
		}

		@Test
		@DisplayName("Save should save a contact in the collection - testSaveShouldSaveAContactInTheCollection()")
		void testSaveShouldSaveAContactInTheCollection() {
			Contact contact = new Contact("testFirstName", "testLastName", "1111111111", "test1@email.com");
			contactMongoRepository.save(contact);
			assertThat(contactCollection.find().first()).isEqualTo(contact);
		}

		@Test
		@DisplayName("Delete should delete a contact from the collection - testDeleteShouldDeleteAContactFromTheCollection()")
		void testDeleteShouldDeleteAContactFromTheCollection() {
			Contact contactToDelete = new Contact("testFirstName", "testLastName", "1111111111", "test1@email.com");
			contactCollection.insertOne(contactToDelete);
			contactMongoRepository.delete(contactToDelete.getId());
			assertThat(contactCollection.countDocuments()).isZero();
		}

		@Test
		@DisplayName("Update phone should update contact phone from the collection - testUpdatePhoneShouldUpdateAContactPhoneFromTheCollection()")
		void testUpdatePhoneShouldUpdateAContactPhoneFromTheCollection() {
			Contact contactToUpdate = new Contact("testFirstName", "testLastName", "1111111111", "test1@email.com");
			contactCollection.insertOne(contactToUpdate);
			contactMongoRepository.updatePhone(contactToUpdate.getId(), "2222222222");
			assertThat(contactCollection.find().first().getPhone()).isEqualTo("2222222222");
		}

		@Test
		@DisplayName("Update email should update contact email from the collection - testUpdateEmailShouldUpdateAContactEmailFromTheCollection()")
		void testUpdateEmailShouldUpdateAContactEmailFromTheCollection() {
			Contact contactToUpdate = new Contact("testFirstName", "testLastName", "1111111111", "test1@email.com");
			contactCollection.insertOne(contactToUpdate);
			contactMongoRepository.updateEmail(contactToUpdate.getId(), "test2@email.com");
			assertThat(contactCollection.find().first().getEmail()).isEqualTo("test2@email.com");
		}

	}

	@Nested
	@DisplayName("Contact Mongo Repository Exceptional Cases")
	class ExceptionalCases {
		
		@Test
		@DisplayName("Find all should return an empty list when contact collection is empty - testFindAllShouldReturnAnEmptyListWhenContactCollectionIsEmpty()")
		void testFindAllShouldReturnAnEmptyListWhenContactCollectionIsEmpty() {
			assertThat(contactMongoRepository.findAll()).isEmpty();
		}

		@Test
		@DisplayName("Find by id should return null when string id is not parsable into an object id - testFindByIdShouldReturnNullWhenStringIdIsNotParsableIntoAnObjectId()")
		void testFindByIdShouldReturnNullWhenStringIdIsNotParsableIntoAnObjectId() {
			assertThat(contactMongoRepository.findById("1")).isNull();
			assertThat(contactMongoRepository.findById("-")).isNull();
			assertThat(contactMongoRepository.findById("$")).isNull();
			assertThat(contactMongoRepository.findById("aaa")).isNull();
		}

		@Test
		@DisplayName("Find by id should return null when contact id is not found - testFindByIdShouldReturnNullWhenContactIdIsNotFound()")
		void testFindByIdShouldReturnNullWhenContactIdIsNotFound() {
			assertThat(contactMongoRepository.findById(new ObjectId().toString())).isNull();
		}

		@Test
		@DisplayName("Delete should do nothing when string id is not parsable into an object id - testDeleteShouldDoNothingWhenStringIdIsNotParsableIntoAnObjectId()")
		void testDeleteShouldDoNothingWhenStringIdIsNotParsableIntoAnObjectId() {
			Contact contact = new Contact("testFirstName1", "testLastName1", "test1@email.com", "1111111111");
			contactCollection.insertOne(contact);
			contactMongoRepository.delete("1");
			contactMongoRepository.delete("-");
			contactMongoRepository.delete("$");
			contactMongoRepository.delete("aaa");
			assertThat(contactCollection.countDocuments()).isEqualTo(1);
		}

	}

	@ParameterizedTest
	@MethodSource("provideValuesForFindByNameTest")
	@DisplayName("Find by name testing with some matching examples, just for documentation - testFindByNameShouldReturnAListOfContactMatches()")
	void testFindByNameShouldReturnAListOfContactMatches(List<Contact> contacts, String input, List<Contact> expected) {
		contactCollection.insertMany(contacts);
		assertThat(contactMongoRepository.findByName(input)).isEqualTo(expected);
	}

	private static Stream<Arguments> provideValuesForFindByNameTest() {
		Contact contact1 = new Contact("testFirstName1", "testLastName1", "1111111111", "test1@email.com");
		Contact contact2 = new Contact("testFirstName2", "testLastName2", "2222222222", "test2@email.com");
		List<Contact> contacts = asList(contact1, contact2);
		return Stream.of(Arguments.of(contacts, "testFirstName1", asList(contact1)),
				Arguments.of(contacts, "FirstName1", asList(contact1)),
				Arguments.of(contacts, "FirstName", asList(contact1, contact2)),
				Arguments.of(contacts, "LastName1", asList(contact1)),
				Arguments.of(contacts, "LastName", asList(contact1, contact2)),
				Arguments.of(contacts, "nonMatchingContact", emptyList()),
				Arguments.of(contacts, "", asList(contact1, contact2)), Arguments.of(contacts, " ", emptyList()));
	}

}
