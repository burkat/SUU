package logic;

import com.mongodb.*;

import java.util.Map;

class DatabaseManager {
    private static DB database;

    @SuppressWarnings("deprecation")
    DatabaseManager() {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        database = mongoClient.getDB("myMongoDb");
    }

    DBCollection createCollection(String collectionName) {
        return database.createCollection(collectionName, null);
    }

    void insert(DBCollection collection, Map<String, Object> fields) {
        BasicDBObject document = new BasicDBObject();
        fields.forEach(document::put);
        collection.insert(document);
    }

    @SuppressWarnings("unused")
    void clear() {
        for (String collectionName : database.getCollectionNames()) {
            database.getCollection(collectionName).drop();
        }
    }

    void listMatchingDocuments(DBCollection collection, Map<String, Object> queryArgs) {
        BasicDBObject searchQuery = new BasicDBObject();
        queryArgs.forEach(searchQuery::put);
        DBCursor cursor = collection.find(searchQuery);

        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            printOffer(obj);
        }
    }

    void listAllDocuments(DBCollection collection) {
        DBCursor cursor = collection.find();

        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            printOffer(obj);
        }
    }

    DBObject getDocument(DBCollection collection, Map<String, Object> queryArgs) {
        BasicDBObject searchQuery = new BasicDBObject();
        queryArgs.forEach(searchQuery::put);
        return collection.findOne(searchQuery);
    }

    void delete(DBCollection collection, Map<String, Object> queryArgs) {
        BasicDBObject searchQuery = new BasicDBObject();
        queryArgs.forEach(searchQuery::put);
        collection.remove(searchQuery);
    }

    private void printOffer(DBObject obj) {
        System.out.println("id: " + obj.get("id") + "\n" +
                "AccountName: " + obj.get("accountName") + "\n" +
                "AvailableFrom: " + obj.get("availableFrom") + "\n" +
                "AvailableTo: " + obj.get("availableTo") + "\n" +
                "Price: " + obj.get("price") + "\n");
    }

}
