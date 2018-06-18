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
        client = MongoClients.create(buildMongoClientSettings(host, port))
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
        databaseName = dbName
        client = MongoClients.create(buildMongoClientSettings(host, port, userName, password, authDb))
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
        client = MongoClients.create(buildMongoClientSettings(host, port, credential))
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

    /**
     * Returns Mongo client settings object for configuring mongo client.
     *
     * @param host mongo database server address or alias
     * @param port port of the mongo database
     *
     * @return object of mongo client settings
     */
    private MongoClientSettings buildMongoClientSettings(String host, String port) {
        MongoClientSettings.builder()
                .applyToClusterSettings({ builder ->
            builder.hosts([new ServerAddress(host, port as int)])
        })
                .build()
    }

    /**
     * Returns Mongo client settings object for configuring mongo client with credentials for authentication using
     * the SCRAM-SHA-1 or MONGODB_CR method.
     *
     * @param host mongo server address or alias
     * @param port port of mongo database
     * @param user user with access to the database
     * @param password password
     * @param authDb database that contains user record for authentication
     *
     * @return object of mongo client settings
     */
    private MongoClientSettings buildMongoClientSettings(String host, String port,
                                                         String user, String password, String authDb) {
        MongoCredential credential = MongoCredential.createCredential(user, authDb, password as char[])
        MongoClientSettings.builder()
                .applyToClusterSettings({ builder ->
            builder.hosts([new ServerAddress(host, port as int)])
        })
                .credential(credential)
                .build()
    }

    /**
     * Returns Mongo client settings object for configuring mongo client, uses prepared credential object.
     *
     * @param host mongo server address or alias
     * @param port port of mongo database
     * @param credential Mongo client credential
     *
     * @return object of mongo client settings
     */
    private MongoClientSettings buildMongoClientSettings(String host, String port, MongoCredential credential) {
        MongoClientSettings.builder()
                .applyToClusterSettings({ builder ->
            builder.hosts([new ServerAddress(host, port as int)])
        })
                .credential(credential)
                .build()
    }
}

