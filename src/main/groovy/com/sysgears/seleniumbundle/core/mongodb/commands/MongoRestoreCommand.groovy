package com.sysgears.seleniumbundle.core.mongodb.commands

import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import com.sysgears.seleniumbundle.core.command.AbstractCommand
import com.sysgears.seleniumbundle.core.conf.Config
import com.sysgears.seleniumbundle.core.implicitinit.annotations.ImplicitInit
import com.sysgears.seleniumbundle.core.mongodb.DBConnection
import com.sysgears.seleniumbundle.core.mongodb.MongoService

/**
 * The command restores Mongo database from given dump.
 */
class MongoRestoreCommand extends AbstractCommand {

    /**
     * Mongo configuration properties.
     */
    private Map <String, ?> properties

    /**
     * Connection to Mongo.
     */
    private MongoDatabase database

    /**
     * Collections to restore.
     */
    @ImplicitInit
    private List<String> collections

    /**
     * Sub-path to dumps directory.
     */
    @ImplicitInit
    private List<String> subPath

    /**
     * Flag which shows that database shouldn't be removed while restoring process. Database will be removed
     * only in case there are dump files to restore from.
     */
    @ImplicitInit
    private List<String> keepDB

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
    MongoRestoreCommand(Map<String, List<String>> arguments, Config conf) {
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
     * Executes restoring of given collections from dump files. Takes files by path that is configured in
     * ApplicationProperties.groovy plus sub-path that is specified for the command.
     *
     * @throws IOException in case writing to file operation produces an error or in case there are no dump files
     * to restore from
     */
    @Override
    void execute() throws IOException {
        new MongoService(database, properties.dumpPath as String)
                .importMongoCollectionsFromJson(subPath?.first(), collections, keepDB?.first() as boolean)
    }
}
