package com.sysgears.seleniumbundle.core.mongodb

import com.sysgears.seleniumbundle.core.conf.Config
import com.sysgears.seleniumbundle.core.utils.FileHelper
import groovy.util.logging.Slf4j
import org.bson.Document
import org.bson.json.JsonMode
import org.bson.json.JsonWriterSettings

/**
 * Provides methods to work with mongo database.
 */
@Slf4j
class MongoService {

    /**
     * Connection to database.
     */
    DBConnection dbConnection

    /**
     * Project properties.
     */
    private Config conf

    /**
     * Creates an instance of MongoService.
     *
     * @param conf project properties
     */
    MongoService(Config conf) {
        this.conf = conf
        dbConnection = new DBConnection(conf)
    }

    /**
     * Exports multiple mongo collections to Json file.
     *
     * @param subPath sub-path to a folder where the files should be stored, can be empty
     * @param collections list of mongo collections names e.g. "users", can be empty
     *
     * @throws IOException in case writing to file operation produces an error
     */
    void exportMongoCollectionsToJson(String subPath = null, List<String> collections = null) throws IOException {
        def path = "${conf.properties.mongodb.dumpPath}${File.separator}${subPath ?: "default"}"

        (collections ?: dbConnection.getDatabase().listCollectionNames()).each {
            exportMongoCollectionToJson(path, it)
        }
    }

    /**
     * Imports multiple mongo collections to a JSON file. Depending on flag keepOtherCollections drops the database
     * in order to remove collections that are not present in dump files.
     *
     * @param subPath sub-path to a dump folder that should be used for restoring, can be empty
     * @param collections list of mongo collections names e.g. "users", can be empty
     * @param keepOtherCollections flag that shows if database should be dropped before the import process, can be empty
     *
     * @throws IOException  in case reading from file operation produces an error
     */
    void importMongoCollectionsFromJson(String subPath = null, List<String> collections = null,
                                        boolean keepOtherCollections = false) throws IOException {
        if (!keepOtherCollections) {
            dbConnection.getDatabase().drop()
        }

        def path = "${conf.properties.mongodb.dumpPath}${File.separator}${subPath ?: "default"}"

        (collections ?: getCollectionsNamesFromDump(path)).each {
            importMongoCollectionFromJson(path, it)
        }
    }

    /**
     * Exports single mongo collection to a JSON file.
     *
     * @param path path to a folder where the files should be stored
     * @param collectionName name of a mongo collection to be stored
     *
     * @throws IOException in case writing to file operation produces an error
     */
    private void exportMongoCollectionToJson(String path, String collectionName) throws IOException {
        def collection = dbConnection.getDatabase().getCollection(collectionName)

        // Making folder tree
        new File(path).mkdirs()

        def writer = new BufferedWriter(new FileWriter("$path${File.separator}${collectionName}.json"))

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
            log.info("Import process for [$collectionName] collection completed")
        }
    }

    /**
     * Imports single mongo collection from a JSON file. Drops collection in order to get clear state before import.
     *
     * @param path path to a folder with dump file to be used for restoring
     * @param collectionName name of a mongo collection
     *
     * @throws IOException in case reading from file operation produces an error
     */
    private void importMongoCollectionFromJson(String path, String collectionName) throws IOException {
        def collection = dbConnection.getDatabase().getCollection(collectionName)

        // dropping collection to get clear state before import
        collection.drop()

        def reader = new BufferedReader(new FileReader("$path${File.separator}${collectionName}.json"))
        try {
            log.info("Starting import process for [$collectionName] collection...")
            String json
            while (json = reader.readLine()) {
                collection.insertOne(Document.parse(json))
            }
        } catch (IOException e) {
            log.info("Unable to import [$collectionName] collection")
            throw new IOException("Unable to import [$collectionName] collection", e)
        } finally {
            log.info("Import process for [$collectionName] collection completed")
        }
    }

    /**
     * Returns a list of collections names from a specific mongo dump.
     *
     * @param path path to mongo dump
     *
     * @return list of collections names
     */
    private List<String> getCollectionsNamesFromDump(String path) {
        FileHelper.getFiles(path)*.path.collect {
            (it =~ /$path\/(.*)\.json$/)[0][1]
        } as List<String>
    }
}
