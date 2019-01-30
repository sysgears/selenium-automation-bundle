package com.sysgears.seleniumbundle.core.mongodb.commands

import com.sysgears.seleniumbundle.core.conf.Config
import com.sysgears.seleniumbundle.core.mongodb.MongoService

/**
 * The command generates a dump of the current state of the Mongo database.
 */
class MongoDumpCommand extends AbstractMongoCommand {

    /**
     * Creates an instance of MongoDumpCommand.
     *
     * @param arguments map that contains command arguments
     * @param conf project properties
     *
     * @throws IllegalArgumentException is thrown if a value is missing for a mandatory parameter or
     * the value does not match the validation pattern
     */
    MongoDumpCommand(Map<String, List<String>> arguments, Config conf) {
        super(arguments, conf)
    }

    /**
     * Backs up the given collections. Stores files by path (configured in
     * ApplicationProperties.groovy) and sub-path (specified for the command).
     *
     * @throws IOException if writing to a file produces an error
     */
    @Override
    void execute() throws IOException {
        new MongoService(database, conf.properties.mongodb.dumpPath as String)
                .exportMongoCollectionsToJson(subPath?.first(), collections)
    }
}
