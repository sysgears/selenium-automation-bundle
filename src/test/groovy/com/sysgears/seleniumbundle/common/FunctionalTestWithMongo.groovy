package com.sysgears.seleniumbundle.common

import com.sysgears.seleniumbundle.core.mongodb.DBConnection
import com.sysgears.seleniumbundle.core.mongodb.MongoService
import org.testng.annotations.BeforeSuite

/**
 * Middle layer class for functional tests which use MongoService. Initializes a connection to a database.
 */
class FunctionalTestWithMongo extends FunctionalTest {

    /**
     * Connection to a database.
     */
    protected DBConnection dbConnection

    /**
     * Mongo service instance.
     */
    protected MongoService mongoService

    /**
     * Initialization of the connection to the Mongo database.
     */
    @BeforeSuite(alwaysRun = true)
    void initMongoConnection() {
        def properties = conf.properties.mongodb

        dbConnection = new DBConnection(properties.dbName, properties.host, properties.port, properties.auth.username,
                properties.auth.password, properties.auth.authDb)
    }

    /**
     * Initialization of Mongo service.
     */
    @BeforeSuite(alwaysRun = true, dependsOnMethods = "initMongoConnection")
    void initMongoService() {
        mongoService = new MongoService(dbConnection.database, conf.properties.mongodb.dumpPath)
    }
}

