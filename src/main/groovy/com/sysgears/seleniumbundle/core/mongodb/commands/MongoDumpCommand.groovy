package com.sysgears.seleniumbundle.core.mongodb.commands

import com.sysgears.seleniumbundle.core.command.AbstractCommand
import com.sysgears.seleniumbundle.core.conf.Config
import com.sysgears.seleniumbundle.core.implicitinit.annotations.ImplicitInit
import com.sysgears.seleniumbundle.core.mongodb.DBConnection
import com.sysgears.seleniumbundle.core.mongodb.MongoService

class MongoDumpCommand extends AbstractCommand {

    /**
     * Project properties.
     */
    private Config conf

    /**
     * Connection to mongodb.
     */
    private DBConnection dbConnection

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
        this.conf = conf
        dbConnection = new DBConnection(conf)
    }

    /**
     * Executes backing up for given collections.
     * Stores files by path that is configured in ApplicationProperties.groovy + sub-path that is specified for command.
     *
     * @throws IOException in case writing to file operation produces an error
     */
    @Override
    void execute() throws IOException {
        new MongoService(conf).exportMongoCollectionsToJson(subPath?.first(), collections)
    }
}
