package com.sysgears.seleniumbundle.core.mongodb.commands

import com.sysgears.seleniumbundle.core.conf.Config
import com.sysgears.seleniumbundle.core.mongodb.MongoService

/**
 * The command restores Mongo database from given dump.
 */
class MongoRestoreCommand extends AbstractMongoCommand {

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
        new MongoService(database, conf.properties.mongodb.dumpPath as String)
                .importMongoCollectionsFromJson(subPath?.first(), collections)
    }
}
