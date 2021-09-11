package org.unifi.lorenzopratesi.app.contacts.repository.mongo;

import java.util.List;
import static java.util.stream.Collectors.*;
import java.util.stream.StreamSupport;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.unifi.lorenzopratesi.app.contacts.model.Contact;
import org.unifi.lorenzopratesi.app.contacts.repository.ContactRepository;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;
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
		return StreamSupport.stream(contactCollection.find().spliterator(), false).collect(toList());
	}

	@Override
	public Contact findById(String id) {
		return contactCollection.find(Filters.eq("_id", stringToObjectId(id))).first();
	}

	@Override
	public Contact findByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(Contact contact) {
		contactCollection.insertOne(contact);

	}

	@Override
	public void updatePhone(Contact contact, String phone) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateEmail(Contact contact, String email) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(String id) {
		contactCollection.deleteOne(Filters.eq("_id", stringToObjectId(id)));
	}

	private Object stringToObjectId(String id) {
		try {
			return new ObjectId(id);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}
