package com.sysgears.seleniumbundle.core.mongodb.commands

import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import com.sysgears.seleniumbundle.core.command.AbstractCommand
import com.sysgears.seleniumbundle.core.conf.Config
import com.sysgears.seleniumbundle.core.implicitinit.annotations.ImplicitInit
import com.sysgears.seleniumbundle.core.mongodb.DBConnection
import com.sysgears.seleniumbundle.core.mongodb.MongoService

class MongoDumpCommand extends AbstractCommand {

    /**
     * Mongo configuration properties.
     */
    private Map<String, ?> properties

    /**
     * Mongo database.
     */
    private MongoDatabase database

    /**
     * Collections to back up.
     */
    @ImplicitInit
    private List<String> collections

    /**
     * SubPath from dump directory. Path to dump directory is defined in ApplicationProperties.
     */
    @ImplicitInit
    private List<String> subPath

    /**
     * Connection string parameter for initialization of Mongo Client.
     */
    @ImplicitInit
    private List<String> connectionString

    /**
     * Creates an instance of MongoDumpCommand.
     *
     * @param arguments map that contains command arguments
     * @param conf project properties
     *
     * @throws IllegalArgumentException is thrown in case a value is missing for a mandatory parameter or
     * the value doesn't match the validation pattern
     */
    MongoDumpCommand(Map<String, List<String>> arguments, Config conf) {
        super(arguments, conf)

        properties = conf.properties.mongodb as Map
        String dbName = properties.dbName,
               host = properties.host,
               port = properties.port,
               username = properties.auth.username,
               password = properties.auth.password,
               authDb = properties.auth.authDb

        DBConnection dbConnection

        if (connectionString) {
            dbConnection = new DBConnection(dbName, MongoClients.create(connectionString.first()))
        } else if (username && password && authDb) {
            dbConnection = new DBConnection(dbName, host, port, username, password, authDb)
        } else {
            dbConnection = new DBConnection(dbName, host, port)
        }

        database = dbConnection.database
    }

    /**
     * Executes backing up for given collections. Stores files by path that is configured in
     * ApplicationProperties.groovy plus sub-path that is specified for the command.
     *
     * @throws IOException in case writing to file operation produces an error
     */
    @Override
    void execute() throws IOException {
        new MongoService(database, properties.dumpPath as String)
                .exportMongoCollectionsToJson(subPath?.first(), collections)
    }
}
