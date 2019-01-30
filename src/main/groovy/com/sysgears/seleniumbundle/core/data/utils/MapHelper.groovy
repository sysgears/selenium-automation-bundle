package com.sysgears.seleniumbundle.core.data.utils

import groovy.util.logging.Slf4j
import org.apache.commons.lang3.StringUtils

/**
 * Provides utility methods for working with maps.
 */
@Slf4j
class MapHelper {

    /**
     * Gets a value from a multidimensional map by the specified path.
     *
     * @param path path to a value, the path is specified with the dot separator, for example: data.users.admin
     * @param map multidimensional map to get data from
     *
     * @return value from the map if found
     *
     * @throws RuntimeException if any of the non-terminal steps return a non-map instance
     */
    static Object getValue(String path, Map map) throws RuntimeException {
        def key = StringUtils.substringBefore(path, ".")
        def remainingPath = StringUtils.substringAfter(path, ".")

        def value = map?.get(key)

        if (remainingPath.isEmpty()) {
            value
        } else if (value && value instanceof Map) {
            getValue(remainingPath, value as Map)
        } else {
            log.error("The value [$value] is not a map, check the path: [$path] for $map")
            throw new RuntimeException("The value [$value] is not a map, check the path: [$path] for $map")
        }
    }
}
