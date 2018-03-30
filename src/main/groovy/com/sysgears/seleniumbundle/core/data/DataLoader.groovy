package com.sysgears.seleniumbundle.core.data

import groovy.util.logging.Slf4j
import org.yaml.snakeyaml.Yaml

/**
 * Provides methods for getting data from data files.
 */
@Slf4j
class DataLoader {

    /**
     * Reads a map from the .yml file.
     *
     * @param file path to the .yml file
     *
     * @return data from the file as a map
     *
     * @throws FileNotFoundException if the file was not found
     */
    static Map readMapFromYml(String file) throws FileNotFoundException {
        readRawDataFromYml(file) as Map
    }

    /**
     * Reads a list from the .yml file.
     *
     * @param file path to the file
     *
     * @return file content as a list
     *
     * @throws FileNotFoundException if the file was not found
     */
    static List readListFromYml(String file) throws FileNotFoundException {
        readRawDataFromYml(file) as List
    }

    /**
     * Reads raw data from the .yml file.
     *
     * @param filePath path to file
     *
     * @return data from the .yml file that can be casted to specific type
     *
     * @throws FileNotFoundException if the file was not found
     */
    static private Object readRawDataFromYml(String filePath) throws FileNotFoundException {
        try {
            new Yaml().load(new File(filePath).newInputStream().text)
        } catch (FileNotFoundException e) {
            log.error("Could not find data file: $filePath, specify valid path.", e)
            throw new FileNotFoundException("Could not find data file: $filePath, specify valid path.")
        }
    }
}
