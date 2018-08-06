package com.sysgears.seleniumbundle.core.mongodb.commands

import com.sysgears.seleniumbundle.core.conf.Config
import com.sysgears.seleniumbundle.core.mongodb.MongoService

/**
 * The command generates a dump of current state of Mongo database.
 */
class MongoDumpCommand extends AbstractMongoCommand {

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
    }

    /**
     * Executes backing up for given collections. Stores files by path that is configured in
     * ApplicationProperties.groovy plus sub-path that is specified for the command.
     *
     * @throws IOException in case writing to file operation produces an error
     */
    @Override
    void execute() throws IOException {
        new MongoService(database, conf.properties.mongodb.dumpPath as String)
                .exportMongoCollectionsToJson(subPath?.first(), collections)
    }
}
