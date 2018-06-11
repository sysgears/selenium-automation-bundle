package com.sysgears.seleniumbundle.core.mongodb

import com.mongodb.MongoClient
import com.mongodb.MongoClientOptions
import com.mongodb.ServerAddress
import com.mongodb.client.MongoDatabase
import com.sysgears.seleniumbundle.core.conf.Config
import org.bson.codecs.configuration.CodecRegistry

/**
 * Provides connection to the database.
 */
class DBConnection {

    /**
     * Project properties.
     */
    private Config conf

    /**
     * Instance of the database.
     */
    private MongoDatabase database

    /**
     * Constructor for the database connection.
     */
    DBConnection(Config conf) {
        this.conf = conf
    }

    /**
     * Gets the instance of the database.
     *
     * @return instance of the database
     */
    MongoDatabase getDatabase() {
        if (!database) {
            CodecRegistry codecRegistry = new CustomCodecRegistry(conf.properties.mongodb.pojos)
            MongoClientOptions options = MongoClientOptions.builder().codecRegistry(codecRegistry).build()
            MongoClient mainClient = new MongoClient(new ServerAddress(conf.properties.mongodb.url as String), options)
            database = mainClient.getDatabase(conf.properties.mongodb.name)
        }
        database
    }
}

