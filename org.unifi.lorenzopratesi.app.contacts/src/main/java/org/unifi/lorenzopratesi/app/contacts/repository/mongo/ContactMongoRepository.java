package org.unifi.lorenzopratesi.app.contacts.repository.mongo;

import static java.util.Arrays.asList;
import java.util.List;
import static java.util.stream.Collectors.*;
import java.util.stream.StreamSupport;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.unifi.lorenzopratesi.app.contacts.model.Contact;
import org.unifi.lorenzopratesi.app.contacts.repository.ContactRepository;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class ContactMongoRepository implements ContactRepository {

	private MongoCollection<Contact> contactCollection;

	public ContactMongoRepository(MongoClient client, String databaseName, String collectionName) {
		CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		MongoDatabase database = client.getDatabase(databaseName).withCodecRegistry(pojoCodecRegistry);
		contactCollection = database.getCollection(collectionName, Contact.class);
	}

	@Override
	public List<Contact> findAll() {
		return getCollectionListFrom(contactCollection.find());
	}

	@Override
	public Contact findById(String id) {
		return contactCollection.find(Filters.eq("_id", stringToObjectId(id))).first();
	}

	@Override
	public List<Contact> findByName(String name) {
		Document rgxSearch = new Document("$regex", ".*" + name + ".*");
		Document query = new Document("$or", asList(
				new Document("firstName", rgxSearch), 
				new Document("lastName", rgxSearch)
		));
		return getCollectionListFrom(contactCollection.find(query));
	}

	@Override
	public void save(Contact contact) {
		contactCollection.insertOne(contact);

	}

	@Override
	public void updatePhone(String id, String phone) {
		Document set = new Document("$set", new Document("phone", phone));
		contactCollection.updateOne(Filters.eq("_id", stringToObjectId(id)), set);
	}

	@Override
	public void updateEmail(String id, String email) {
		Document set = new Document("$set", new Document("email", email));
		contactCollection.updateOne(Filters.eq("_id", stringToObjectId(id)), set);
	}

	@Override
	public void delete(String id) {
		contactCollection.deleteOne(Filters.eq("_id", stringToObjectId(id)));
	}

	private List<Contact> getCollectionListFrom(FindIterable<Contact> find) {
		return StreamSupport.stream(find.spliterator(), false).collect(toList());
	}

	private Object stringToObjectId(String id) {
		try {
			return new ObjectId(id);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}
