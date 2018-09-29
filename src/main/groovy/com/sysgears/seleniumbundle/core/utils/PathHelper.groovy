package com.sysgears.seleniumbundle.core.utils

import org.apache.commons.io.FilenameUtils

/**
 * Provides methods to interact with file paths.
 */
class PathHelper {

    /**
     * Converts path to Unix like style.
     *
     * @param path file path
     *
     * @return converted path
     */
    static String convertToUnixLike(String path) {
        path.replaceAll("\\\\", "/")
    }

    /**
     * Modifies path to be relative to the basePath.
     *
     * @param pathToModify path that should be modified
     * @param basePath path to make relative to
     *
     * @return relative path
     */
    static String getRelativePath(String pathToModify, String basePath) {
        pathToModify - FilenameUtils.separatorsToSystem(basePath)
    }
}
