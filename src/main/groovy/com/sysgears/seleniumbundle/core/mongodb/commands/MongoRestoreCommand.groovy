package com.sysgears.seleniumbundle.core.mongodb.commands

import com.sysgears.seleniumbundle.core.command.AbstractCommand
import com.sysgears.seleniumbundle.core.conf.Config
import com.sysgears.seleniumbundle.core.implicitinit.annotations.ImplicitInit
import com.sysgears.seleniumbundle.core.mongodb.DBConnection
import com.sysgears.seleniumbundle.core.mongodb.MongoService

class MongoRestoreCommand extends AbstractCommand {

    /**
     * Project properties.
     */
    private Config conf

    /**
     * Connection to mongodb.
     */
    private DBConnection dbConnection

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
     * Sub-path to dumps directory.
     */
    @ImplicitInit
    private List<String> dropDB

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
        this.conf = conf
        dbConnection = new DBConnection(conf)
    }

    /**
     * Executes restoring of given collections from dump files. Takes files by path that is configured in
     * ApplicationProperties.groovy plus sub-path that is specified for the command.
     *
     * @throws IOException in case writing to file operation produces an error
     */
    @Override
    void execute() throws IOException {
        new MongoService(conf).importMongoCollectionsFromJson(subPath?.first(), collections, dropDB?.first() as boolean)
    }
}
