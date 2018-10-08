package com.sysgears.seleniumbundle.core.mongodb.commands

import com.sysgears.seleniumbundle.core.conf.Config
import com.sysgears.seleniumbundle.core.mongodb.MongoService

/**
 * The command restores a Mongo database from a given dump.
 */
class MongoRestoreCommand extends AbstractMongoCommand {

    /**
     * Creates an instance of MongoDumpCommand.
     *
     * @param arguments map that contains command arguments
     * @param conf project properties
     *
     * @throws IllegalArgumentException is thrown if a value is missing for a mandatory parameter or
     * the value does not match the validation pattern
     */
    MongoRestoreCommand(Map<String, List<String>> arguments, Config conf) {
        super(arguments, conf)
    }

    /**
     * Restores the given collections from the dump files. Takes files by path (configured in
     * ApplicationProperties.groovy) and sub-path (specified for the command).
     *
     * @throws IOException if writing to a file produces an error or if there are no dump files
     * to restore from
     */
    @Override
    void execute() throws IOException {
        new MongoService(database, conf.properties.mongodb.dumpPath as String)
                .importMongoCollectionsFromJson(subPath?.first(), collections)
    }
}
