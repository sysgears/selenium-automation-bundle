package com.sysgears.seleniumbundle.core.conf

import com.sysgears.seleniumbundle.core.implicitinit.ParameterMapper
import com.sysgears.seleniumbundle.core.implicitinit.annotations.ImplicitInit
import com.sysgears.seleniumbundle.core.utils.FileHelper
import groovy.util.logging.Slf4j
import org.apache.commons.io.FilenameUtils

/**
 * Provides access to the project properties that can be configured in the ApplicationProperties.groovy file.
 */
@Slf4j
class Config {

    /**
     * The path to the main module resources.
     */
    private static final String RESOURCES_PATH = "src/main/resources/config/"

    /**
     * The name of the file with project properties.
     */
    private static final String PROJECT_PROPERTIES_FILENAME = "ApplicationProperties"

    /**
     * The path to the project configuration file.
     */
    private static final String CONF_FILE = "${RESOURCES_PATH}${PROJECT_PROPERTIES_FILENAME}.groovy"

    /**
     * The base URL of the web application under test.
     */
    @ImplicitInit(isRequired = true, pattern = "^http.*")
    String baseUrl

    /**
     * The URL of Selenium Grid.
     */
    @ImplicitInit
    String remoteUrl

    /**
     * The code name of the operating system such as "linux", "windows", or "mac".
     */
    @ImplicitInit
    String os

    /**
     * The browser properties.
     */
    @ImplicitInit
    Map<String, ?> browser

    /**
     * The project properties map.
     */
    final Map<String, ?> properties = [:]

    /**
     * Creates an instance of Config.
     *
     * @throws IllegalArgumentException if a value is missing for a mandatory parameter
     * or the value doesn't match the validation pattern
     * @throws IOException if an I/O error occurs while getting the files with properties
     */
    private Config() throws IllegalArgumentException, IOException {
        def configSlurper = new ConfigSlurper()
        def parameterMapper = new ParameterMapper()

        def rootDirectory = FilenameUtils.separatorsToSystem(RESOURCES_PATH)

        // reads the application properties
        properties << configSlurper.parse(new File(CONF_FILE).toURI().toURL())

        // reads all the other files with properties; they may override properties in ApplicationProperties.groovy
        properties << FileHelper.getFiles(rootDirectory, "groovy").findAll {
            it.name.toLowerCase().contains("properties") &&
                    !it.name.toLowerCase().contains(PROJECT_PROPERTIES_FILENAME.toLowerCase())
        }.collectEntries {
            configSlurper.parse(new File(it.path).toURI().toURL())
        }

        // overrides property values from the system properties passed via the command line
        properties << System.properties.findAll { it.key.contains("test.") }
                .collectEntries { [it.key - "test.", it.value] }

        parameterMapper.initParameters(this, properties)
    }

    /**
     * Instantiates Config only when the getInstance method of the outer class is called.
     * Bill Pugh's singleton pattern.
     */
    private static class ConfigHolder {
        private static final Config INSTANCE = new Config()
    }

    /**
     * Returns the Config instance.
     *
     * @return thread-safe instance of the application Config
     */
    static Config getInstance() {
        ConfigHolder.INSTANCE
    }

    /**
     * Returns any extra property from the configuration file that was not specified in the Config class.
     *
     * @param path to property value
     *
     * @return value of the specified in the configuration file property
     */
    def propertyMissing(String path) {
        properties.clone()[path]
    }
}