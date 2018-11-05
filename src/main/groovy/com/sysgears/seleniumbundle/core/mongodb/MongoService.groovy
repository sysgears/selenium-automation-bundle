package com.sysgears.seleniumbundle.core.mongodb

import com.mongodb.client.MongoDatabase
import com.sysgears.seleniumbundle.core.utils.FileHelper
import groovy.util.logging.Slf4j
import org.apache.commons.io.FilenameUtils
import org.bson.Document
import org.bson.json.JsonMode
import org.bson.json.JsonWriterSettings

/**
 * Provides methods to work with the Mongo database.
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
     * @param database connection to the Mongo database
     * @param dumpPath path to parent folder with all dumps
     */
    MongoService(MongoDatabase database, String dumpPath) {
        this.database = database
        this.dumpPath = dumpPath
    }

    /**
     * Exports multiple Mongo collections to a JSON file.
     *
     * @param subPath sub-path to the folder where the files should be stored, if empty, files will be stored into
     * the "default" folder
     * @param collections list of names of Mongo collections, for example, "users"; can be empty
     *
     * @throws IOException if writing to a file produces an error
     */
    void exportMongoCollectionsToJson(String subPath = null, List<String> collections = null) throws IOException {
        def path = FilenameUtils.separatorsToSystem("${dumpPath}/${subPath ?: "default"}")
        (collections ?: database.listCollectionNames()).each {
            exportMongoCollectionToJson(path, it)
        }
    }

    /**
     * Imports multiple Mongo collections to a JSON file. Drops the database in order to clean the database state
     * before importing.
     *
     * @param subPath sub-path to a dump folder that should be used for restoring a database, can be empty
     * @param collections list of names of Mongo collections, for example, "users"; can be empty
     * @param keepOtherCollections flag that shows if a database should be dropped before importing, can be empty
     *
     * @throws IOException if reading from a file produces an error or if the dump files are absent
     */
    void importMongoCollectionsFromJson(String subPath = null, List<String> collections = null) throws IOException {
        def path = "${dumpPath}/${subPath ?: "default"}"
        def dumpCollections = getCollectionsNamesFromDump(path)

        if (!dumpCollections) {
            log.info("You have no dump file to restore the database")
            throw new IOException("You have no dump file to restore the database")
        }

        database.drop()

        (collections ?: dumpCollections).each {
            importMongoCollectionFromJson(path, it)
        }
    }

    /**
     * Exports a single Mongo collection to a JSON file.
     *
     * @param path path to the folder where the files should be stored
     * @param collectionName name of a Mongo collection to be stored
     *
     * @throws IOException if writing to a file produces an error
     */
    private void exportMongoCollectionToJson(String path, String collectionName) throws IOException {
        def collection = database.getCollection(collectionName)

        // making folder tree
        new File(path).mkdirs()

        def writer = new BufferedWriter(new FileWriter(FilenameUtils.separatorsToSystem("$path/${collectionName}.json")))

        try {
            log.info("Exporting the [$collectionName] collection...")
            JsonWriterSettings settings = JsonWriterSettings.builder().outputMode(JsonMode.EXTENDED).build();
            collection?.find()?.each { Document doc ->
                writer.write(doc.toJson(settings))
                writer.newLine()
            }
        } catch (IOException e) {
            log.info("Unable to export the [$collectionName] collection")
            throw new IOException("Unable to export the [$collectionName] collection", e)
        } finally {
            writer.close()
        }

        log.info("Importing the [$collectionName] collection is completed")
    }

    /**
     * Imports a single Mongo collection from a JSON file. Drops the collection in order to get a clear state before
     * importing.
     *
     * @param path path to a folder with the dump file to be used for restoring
     * @param collectionName name of the Mongo collection
     *
     * @throws IOException if reading from file produces an error
     */
    private void importMongoCollectionFromJson(String path, String collectionName) throws IOException {
        def collection = database.getCollection(collectionName)

        def file = new File(FilenameUtils.separatorsToSystem("$path/${collectionName}.json"))
        def reader = new BufferedReader(new StringReader(normalizeJSON(file)))
        try {
            log.info("Importing the [$collectionName] collection...")
            String json
            while (json = reader.readLine()) {
                collection.insertOne(Document.parse(json))
            }
        } catch (IOException e) {
            log.info("Unable to import the [$collectionName] collection")
            throw new IOException("Unable to import the [$collectionName] collection", e)
        }

        log.info("Importing the [$collectionName] collection is completed")
    }

    /**
     * Returns a list of names of collections from a given Mongo dump.
     *
     * @param path path to Mongo dump
     *
     * @return list of names of collections
     */
    private List<String> getCollectionsNamesFromDump(String path) {
        FileHelper.getFiles(path)*.path.collect {
            (it =~ /$path\/(.*)\.json$/)[0][1]
        } as List<String>
    }

    /**
     * Converts the JSON file from the tree-view format into a single-line-per-record format.
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
