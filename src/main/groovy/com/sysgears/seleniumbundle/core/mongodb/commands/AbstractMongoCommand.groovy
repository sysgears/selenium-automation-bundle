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

        def mongoConf = (conf.properties.mongodb as ConfigObject).flatten()
        def properties = ["dbName", "host", "port", "auth.username", "auth.password", "auth.authDb"]
                .collect { mongoConf."$it" }.findAll()

        def dbConnection = !connectionString ? properties as DBConnection :
                new DBConnection(mongoConf.dbName, MongoClients.create(connectionString.first()))

        database = dbConnection.database
    }

    /**
     * Abstract method to be implemented by subclasses for executing the command.
     */
    abstract void execute()
}
