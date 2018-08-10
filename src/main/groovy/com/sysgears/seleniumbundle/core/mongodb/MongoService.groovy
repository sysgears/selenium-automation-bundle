package com.sysgears.seleniumbundle.core.mongodb

import com.mongodb.client.MongoDatabase
import com.sysgears.seleniumbundle.core.utils.FileHelper
import groovy.util.logging.Slf4j
import org.apache.commons.io.FilenameUtils
import org.bson.Document
import org.bson.json.JsonMode
import org.bson.json.JsonWriterSettings

/**
 * Provides methods to work with Mongo database.
 */
@Slf4j
class MongoService {

    /**
     * Mongo database.
     */
    MongoDatabase database

    /**
     * Path to dump files location.
     */
    private String dumpPath

    /**
     * Creates an instance of MongoService.
     *
     * @param database connection to Mongo database
     * @param dumpPath path to parent folder with all dumps
     */
    MongoService(MongoDatabase database, String dumpPath) {
        this.database = database
        this.dumpPath = dumpPath
    }

    /**
     * Exports multiple Mongo collections to Json file.
     *
     * @param subPath sub-path to a folder where the files should be stored, if empty, files will be stored into
     * "default" folder
     * @param collections list of Mongo collections names e.g. "users", can be empty
     *
     * @throws IOException in case writing to file operation produces an error
     */
    void exportMongoCollectionsToJson(String subPath = null, List<String> collections = null) throws IOException {
        def path = FilenameUtils.separatorsToSystem("${dumpPath}/${subPath ?: "default"}")
        (collections ?: database.listCollectionNames()).each {
            exportMongoCollectionToJson(path, it)
        }
    }

    /**
     * Imports multiple Mongo collections to a JSON file. Drops the database in order to clean database state before
     * import.
     *
     * @param subPath sub-path to a dump folder that should be used for restoring, can be empty
     * @param collections list of Mongo collections names e.g. "users", can be empty
     * @param keepOtherCollections flag that shows if database should be dropped before the import process, can be empty
     *
     * @throws IOException  in case reading from file operation produces an error or dump files are absent
     */
    void importMongoCollectionsFromJson(String subPath = null, List<String> collections = null) throws IOException {
        def path = "${dumpPath}/${subPath ?: "default"}"
        def dumpCollections = getCollectionsNamesFromDump(path)

        if (!dumpCollections) {
            log.info("You have no dump file to restore database from")
            throw new IOException("You have no dump file to restore database from")
        }

        database.drop()

        (collections ?: dumpCollections).each {
            importMongoCollectionFromJson(path, it)
        }
    }

    /**
     * Exports single Mongo collection to a JSON file.
     *
     * @param path path to a folder where the files should be stored
     * @param collectionName name of a Mongo collection to be stored
     *
     * @throws IOException in case writing to file operation produces an error
     */
    private void exportMongoCollectionToJson(String path, String collectionName) throws IOException {
        def collection = database.getCollection(collectionName)

        // making folder tree
        new File(path).mkdirs()

        def writer = new BufferedWriter(new FileWriter(FilenameUtils.separatorsToSystem("$path/${collectionName}.json")))

        try {
            log.info("Starting export process for [$collectionName] collection...")
            JsonWriterSettings settings = JsonWriterSettings.builder().outputMode(JsonMode.EXTENDED).build();
            collection?.find()?.each { Document doc ->
                writer.write(doc.toJson(settings))
                writer.newLine()
            }
        } catch (IOException e) {
            log.info("Unable to export [$collectionName] collection")
            throw new IOException("Unable to export [$collectionName] collection", e)
        } finally {
            writer.close()
        }

        log.info("Import process for [$collectionName] collection completed")
    }

    /**
     * Imports single Mongo collection from a JSON file. Drops collection in order to get clear state before import.
     *
     * @param path path to a folder with dump file to be used for restoring
     * @param collectionName name of a Mongo collection
     *
     * @throws IOException in case reading from file operation produces an error
     */
    private void importMongoCollectionFromJson(String path, String collectionName) throws IOException {
        def collection = database.getCollection(collectionName)

        def file = new File(FilenameUtils.separatorsToSystem("$path/${collectionName}.json"))
        def reader = new BufferedReader(new StringReader(normalizeJSON(file)))
        try {
            log.info("Starting import process for [$collectionName] collection...")
            String json
            while (json = reader.readLine()) {
                collection.insertOne(Document.parse(json))
            }
        } catch (IOException e) {
            log.info("Unable to import [$collectionName] collection")
            throw new IOException("Unable to import [$collectionName] collection", e)
        }

        log.info("Import process for [$collectionName] collection completed")
    }

    /**
     * Returns a list of collections names from a specific Mongo dump.
     *
     * @param path path to Mongo dump
     *
     * @return list of collections names
     */
    private List<String> getCollectionsNamesFromDump(String path) {
        FileHelper.getFiles(path)*.path.collect {
            (it =~ /$path\/(.*)\.json$/)[0][1]
        } as List<String>
    }

    /**
     * Converts JSON file with tree view format into single line per record format.
     *
     * @param file JSON file
     *
     * @return JSON string with one record per line
     */
    private String normalizeJSON(File file) {

        // removes all new lines, adds new line between records
        file.text.replaceAll(/\n/, "").replaceAll("\\}\\{", "}\n{")
    }
}
