package com.sysgears.seleniumbundle.core.conf

import com.sysgears.seleniumbundle.core.implicitinit.ParameterMapper
import com.sysgears.seleniumbundle.core.implicitinit.annotations.ImplicitInit
import com.sysgears.seleniumbundle.core.utils.FileHelper
import groovy.util.logging.Slf4j
import org.apache.commons.io.FilenameUtils

/**
 * Provides access to project properties which can be configured via ApplicationProperties.groovy configuration file.
 */
@Slf4j
class Config {

    /**
     * The path to main module resources.
     */
    private static final String RESOURCES_PATH = "src/main/resources/config/"

    /**
     * Name of the file with project properties.
     */
    private static final String PROJECT_PROPERTIES_FILENAME = "ApplicationProperties"

    /**
     * The path to project configuration file.
     */
    private static final String CONF_FILE = "${RESOURCES_PATH}${PROJECT_PROPERTIES_FILENAME}.groovy"

    /**
     * Base URL of the application under test.
     */
    @ImplicitInit(isRequired = true, pattern = "^http.*")
    String baseUrl

    /**
     * URL of Selenium Grid.
     */
    @ImplicitInit
    String remoteUrl

    /**
     * Code name of an operating system e.g. "linux", "windows".
     */
    @ImplicitInit
    String os

    /**
     * Browser properties.
     */
    @ImplicitInit
    Map<String, ?> browser

    /**
     * Project properties map.
     */
    final Map<String, ?> properties = [:]

    /**
     * Creates an instance of Config.
     *
     * @throws IllegalArgumentException if a value is missing for a mandatory parameter
     * or the value doesn't match the validation pattern
     * @throws IOException if an I/O error occurs while getting the properties files
     */
    private Config() throws IllegalArgumentException, IOException {
        def configSlurper = new ConfigSlurper()
        def parameterMapper = new ParameterMapper()

        def rootDirectory = FilenameUtils.separatorsToSystem(RESOURCES_PATH)

        // reads application properties
        properties << configSlurper.parse(new File(CONF_FILE).toURI().toURL())

        // reads all the other property files (they can override properties in the application.properties file)
        properties << FileHelper.getFiles(rootDirectory, "groovy").findAll {
            it.name.toLowerCase().contains("properties") &&
                    !it.name.toLowerCase().contains(PROJECT_PROPERTIES_FILENAME.toLowerCase())
        }.collectEntries {
            configSlurper.parse(new File(it.path).toURI().toURL())
        }

        // overrides property values from the system properties passed from command-line
        properties << System.properties.findAll { it.key.contains("test.") }
                .collectEntries { [it.key - "test.", it.value] }

        parameterMapper.initParameters(this, properties)
    }

    /**
     * Instantiates Config only when getInstance method of outer class is called.
     * Bill Pugh's singleton pattern.
     */
    private static class ConfigHolder {
        private static final Config INSTANCE = new Config()
    }

    /**
     * Returns Config instance.
     *
     * @return thread-safe instance of the application Config
     */
    static Config getInstance() {
        ConfigHolder.INSTANCE
    }

    /**
     * Returns any extra property from the configuration file which wasn't specified in the Config class.
     *
     * @param path to property value
     *
     * @return value of the specified in the configuration file property
     */
    def propertyMissing(String path) {
        properties.clone()[path]
    }
}