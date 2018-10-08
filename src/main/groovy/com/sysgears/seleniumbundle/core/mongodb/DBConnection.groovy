package com.sysgears.seleniumbundle.core.mongodb

import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import groovy.transform.Synchronized

/**
 * Provides a threadsafe connection to the database.
 */
class DBConnection {

    /**
     * Name of the database.
     */
    private final String databaseName

    /**
     * Instance of the database.
     */
    private MongoDatabase database

    /**
     * Mongo client, representation of a MongoDB cluster.
     */
    private final MongoClient client

    /**
     * Constructor for the database connection. Accepts a configured Mongo client.
     *
     * @param client configured Mongo client instance
     * @param dbName database name
     */
    DBConnection(String dbName, MongoClient client) {
        databaseName = dbName
        this.client = client
    }

    /**
     * Constructor for the database connection without authentication.
     *
     * @param host mongo server address or alias
     * @param port port of mongo database
     * @param dbName database name
     */
    DBConnection(String dbName, String host, String port) {
        this(dbName, MongoClients.create(settingsBuilder(host, port)))
    }

    /**
     * Constructor for the database connection. Accepts configured Mongo credentials.
     *
     * @param dbName database name
     * @param host Mongo server address or alias
     * @param port port of the Mongo database
     * @param credential user's credential that are used for authentication
     */
    DBConnection(String dbName, String host, String port, MongoCredential credential) {
        this(dbName, MongoClients.create(settingsBuilder(host, port, credential)))
    }

    /**
     * Constructor for the database connection. Use for the SCRAM-SHA-1 or MONGODB_CR authentication mechanism.
     *
     * @param dbName database name
     * @param host mongo server address or alias
     * @param port port of Mongo database
     * @param userName name of a user with access to the database
     * @param password password
     * @param authDb database that contains user record for authentication
     */
    DBConnection(String dbName, String host, String port, String userName, String password, String authDb) {
        this(dbName, host, port, MongoCredential.createCredential(userName, authDb, password as char[]))
    }

    /**
     * Gets the instance of the database.
     *
     * @return instance of the database
     */
    @Synchronized
    MongoDatabase getDatabase() {
        if (!database) {
            database = client.getDatabase(databaseName).withCodecRegistry(new CustomCodecRegistry())
        }
        database
    }

    /**
     * Prepares the settings objects for the Mongo client.
     *
     * @param host Mongo server address or alias
     * @param port port of the Mongo database
     * @param credential Mongo credential object
     *
     * @return settings object for the Mongo client
     */
    private static MongoClientSettings settingsBuilder(String host, String port, MongoCredential credential = null) {
        def builder = MongoClientSettings.builder()
                .applyToClusterSettings({ builder ->
            builder.hosts([new ServerAddress(host, port as int)])
        })

        (credential ? builder.credential(credential) : builder).build()
    }
}

