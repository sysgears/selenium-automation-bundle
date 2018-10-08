package com.sysgears.seleniumbundle.common

import com.sysgears.seleniumbundle.core.mongodb.DBConnection
import com.sysgears.seleniumbundle.core.mongodb.MongoService
import org.testng.annotations.BeforeSuite

/**
 * The middle layer class for functional tests that use MongoService.
 * Initializes a connection to a MongoDB database.
 */
class FunctionalTestWithMongo extends FunctionalTest {

    /**
     * Connection to the database.
     */
    protected DBConnection dbConnection

    /**
     * Instance of Mongo service.
     */
    protected MongoService mongoService

    /**
     * Initialization of a connection to a MongoDB database.
     */
    @BeforeSuite(alwaysRun = true)
    void initMongoConnection() {
        def properties = conf.properties.mongodb

        dbConnection = new DBConnection(properties.dbName, properties.host, properties.port, properties.auth.username,
                properties.auth.password, properties.auth.authDb)
    }

    /**
     * Initialization of MongoDB service.
     */
    @BeforeSuite(alwaysRun = true, dependsOnMethods = "initMongoConnection")
    void initMongoService() {
        mongoService = new MongoService(dbConnection.database, conf.properties.mongodb.dumpPath)
    }
}

