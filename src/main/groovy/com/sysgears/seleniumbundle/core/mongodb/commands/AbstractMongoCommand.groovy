package com.sysgears.seleniumbundle.core.mongodb.commands

import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import com.sysgears.seleniumbundle.core.command.AbstractCommand
import com.sysgears.seleniumbundle.core.conf.Config
import com.sysgears.seleniumbundle.core.implicitinit.annotations.ImplicitInit
import com.sysgears.seleniumbundle.core.mongodb.DBConnection

abstract class AbstractMongoCommand extends AbstractCommand {

    /**
     * Mongo database.
     */
    protected MongoDatabase database

    /**
     * Collections to restore.
     */
    @ImplicitInit
    protected List<String> collections

    /**
     * Sub-path to dumps directory.
     */
    @ImplicitInit
    protected List<String> subPath

    /**
     * Connection string parameter for initialization of Mongo Client.
     */
    @ImplicitInit
    protected List<String> connectionString

    /**
     * Creates an instance of MongoCommand initializes database property
     *
     * @param arguments map that contains command arguments
     * @param conf project properties
     *
     * @throws IllegalArgumentException is thrown in case a value is missing for a mandatory parameter or
     * the value doesn't match the validation pattern
     */
    AbstractMongoCommand(Map<String, List<String>> arguments, Config conf) throws IllegalArgumentException {
        super(arguments, conf)

        def properties = conf.properties.mongodb as Map
        def dbName = properties.dbName,
            host = properties.host,
            port = properties.port,
            username = properties.auth.username,
            password = properties.auth.password,
            authDb = properties.auth.authDb

        DBConnection dbConnection

        if (connectionString) {
            dbConnection = new DBConnection(dbName, MongoClients.create(connectionString.first()))
        } else if (username as Boolean && password as Boolean && authDb as Boolean) {
            dbConnection = new DBConnection(dbName, host, port, username, password, authDb)
        } else {
            dbConnection = new DBConnection("testdb", "localhost", "27017")
        }

        database = dbConnection.database
    }

    /**
     * Abstract method to be implemented by subclasses for executing the command.
     */
    abstract void execute()
}
