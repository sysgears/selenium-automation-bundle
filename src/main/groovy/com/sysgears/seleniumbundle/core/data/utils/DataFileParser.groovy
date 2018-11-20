package com.sysgears.seleniumbundle.core.data.utils

/**
 * Provides methods to parse test data from .data (special format) files
 */
class DataFileParser {

    /**
     * Parses string value to list or map depending on syntax used in .data file e.g. [1,2,3] or [a:1, b:2, c:3].
     *
     * @param value string value to be parsed
     *
     * @return List or Map depending on syntax
     */
    static parseValue(String value) {
        def list = getList(value)
        (list.every { it.contains(":") }) ? getMap(list) : list
    }

    /**
     * Parses string value to list
     *
     * @param value string value to parse
     *
     * @return list
     */
    static private List getList(String value) {
        value[1..value.length() - 2].split(",").collect { String item -> item.trim() }
    }

    /**
     * Transforms a list into a map by splitting list values using ":" pattern.
     *
     * @param list list of string values to contain ":"
     *
     * @return map
     */
    static private Map getMap(List<String> list) {
        list.collectEntries {
            def pair = it.split(":")
            [pair[0].trim(), pair[1].trim()]
        }
    }
}
