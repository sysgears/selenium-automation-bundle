package com.sysgears.seleniumbundle.core.mongodb

import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import org.bson.codecs.configuration.CodecRegistry

/**
 * Provides connection to the database.
 */
class DBConnection {

    /**
     * Name of the database.
     */
    private String databaseName

    /**
     * Instance of the database.
     */
    private MongoDatabase database

    /**
     * Mongo client, representation of a MongoDB cluster.
     */
    private MongoClient client

    /**
     * Constructor for the database connection without authentication.
     *
     * @param host mongo server address or alias
     * @param port port of mongo database
     * @param dbName database name
     */
    DBConnection(String dbName, String host, String port) {
        databaseName = dbName

        def settings = MongoClientSettings.builder()
                .applyToClusterSettings({ builder ->
            builder.hosts([new ServerAddress(host, port as int)])
        })
                .build()
        client = MongoClients.create(settings)
    }

    /**
     * Constructor for the database connection. Accepts configured Mongo credential.
     *
     * @param dbName database name
     * @param host mongo server address or alias
     * @param port port of mongo database
     * @param credential user's credential that should be used for authentication
     */
    DBConnection(String dbName, String host, String port, MongoCredential credential) {
        databaseName = dbName

        def settings = MongoClientSettings.builder()
                .applyToClusterSettings({ builder ->
            builder.hosts([new ServerAddress(host, port as int)])
        })
                .credential(credential)
                .build()
        client = MongoClients.create(settings)
    }

    /**
     * Constructor for the database connection. Use for SCRAM-SHA-1 or MONGODB_CR authentication mechanism.
     *
     * @param dbName database name
     * @param host mongo server address or alias
     * @param port port of mongo database
     * @param userName name of a user with access to the database
     * @param password password
     * @param authDb database that contains user record for authentication
     */
    DBConnection(String dbName, String host, String port, String userName, String password, String authDb) {
        this(dbName, host, port, MongoCredential.createCredential(userName, authDb, password as char[]))

        databaseName = dbName
    }

    /**
     * Constructor for the database connection. Accepts configured Mongo client.
     *
     * @param client configured Mongo client instance
     * @param dbName database name
     */
    DBConnection(String dbName, MongoClient client) {
        databaseName = dbName
        this.client = client
    }

    /**
     * Gets the instance of the database.
     *
     * @return instance of the database
     */
    MongoDatabase getDatabase() {
        CodecRegistry codecRegistry = new CustomCodecRegistry()
        if (!database) {
            database = client.getDatabase(databaseName).withCodecRegistry(codecRegistry)
        }
        database
    }
}

