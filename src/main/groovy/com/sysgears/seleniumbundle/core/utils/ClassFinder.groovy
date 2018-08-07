package com.sysgears.seleniumbundle.core.utils

import com.sysgears.seleniumbundle.core.command.AbstractCommand
import com.sysgears.seleniumbundle.core.command.CommandArgs
import com.sysgears.seleniumbundle.core.conf.Config
import com.sysgears.seleniumbundle.core.data.cloud.AbstractCloudService
import groovy.util.logging.Slf4j
import org.apache.commons.io.FilenameUtils

/**
 * Provides methods to find classes by criteria.
 */
@Slf4j
class ClassFinder {

    /**
     * The path to main sources.
     */
    private final static String GROOVY_SOURCE_PATH = "src/main/groovy/"

    /**
     * The root package to start searching from.
     */
    private final static String ROOT_DIR = "${GROOVY_SOURCE_PATH}com/sysgears/seleniumbundle"

    /**
     * Finds a command by a given name which extends Abstract command.
     *
     * @param args parsed command arguments which have command name and arguments
     * @param instance of config
     *
     * @return an instance of the found command initialized with the given arguments and the config
     */
    static <T> T findCommand(CommandArgs args, Config conf) {
        find(args.name, AbstractCommand, "Command", "commands")
                .newInstance(args.arguments, conf) as T
    }

    /**
     * Finds a cloud service by a given name which extends AbstractCloudService.
     *
     * @param className name of a class
     * @param conf instance of config
     *
     * @return an instance of the found cloud service initialized with the config
     */
    static <T> T findCloudService(String className, Config conf) {
        find(className, AbstractCloudService, "Service")
                .newInstance(conf) as T
    }

    /**
     * Finds a class by the given name which has the given sub name and is stored in the given sub folder.
     *
     * @param className name of a class
     * @param parentClass class which the sought class extends
     * @param subName sub name of the class
     * @param subFolder name of the sub folder to search for the class in
     *
     * @return class of the found groovy class
     *
     * @throws IllegalArgumentException if command has not been found
     */
    static Class find(String className, Class parentClass, String subName, String subFolder = "")
            throws IllegalArgumentException {
        def clazz = FileHelper.getFiles(ROOT_DIR, "groovy").findAll {
            it.path.matches(/^(\w*${File.separator})*$subFolder(${File.separator}\w*)*$subName\.groovy$/)
        }.findAll { File file ->
            getClassName(file, subName).equalsIgnoreCase(className)
        }.findResult {
            def clazz = Class.forName(getClassPath(it.path))

            (hasParent(clazz, parentClass)) ? clazz : null
        }

        clazz ?: {
            log.error("Class [$className] wasn't found.")
            throw new IllegalArgumentException("Class [$className] wasn't found.")
        }()
    }

    private static String getClassName(File file, String subName) {
        file.name - "${subName}.groovy"
    }

    private static String getClassPath(String filePath) {
        (filePath - FilenameUtils.separatorsToSystem(GROOVY_SOURCE_PATH) - ".groovy")
                .split(File.separator).join(".")
    }

    /**
     * Checks if a class or any of its superclasses has a target class as parent.
     *
     * @param clazz class to start the check from
     * @param targetClass expected parent
     *
     * @return true if class has a target class as one of the superclasses, false otherwise
     */
    private static Boolean hasParent(Class clazz, Class targetClass) {
        def parent = clazz.getSuperclass()

        parent ? parent == targetClass ?: hasParent(parent, targetClass) : false
    }
}
