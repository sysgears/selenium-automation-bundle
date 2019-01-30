package com.sysgears.seleniumbundle.core.data

import java.lang.reflect.Method

/**
 * Represents plain data retrieved from plain data files
 */
class PlainData extends AbstractTestData {

    /**
     * Instantiates plain data object
     *
     * @param rawData plain data retrieved from PlainData file (pipe separated format)
     * @param clazz test class that contains meta information (annotations on methods) for getting test data
     */
    PlainData(List rawData, Class clazz) {
        super(rawData, clazz)
    }

    /**
     * Finds sub-sets of test data required for a test method execution from the given list of strings in a pipe separated format.
     * Uses "|" pipe delimiter for separating values and "new line" for separating rows.
     * Removes header if data.plain.hasHeader option in Application.properties is set to true
     *
     * @param rawData list of strings to be parsed
     * @param method method which uses TestNG Data Provider
     *
     * @return Object[][]
     */
    protected Object[][] map(List rawData, Method method) {
        rawData.find { String dataSet -> dataSet.contains(method.name) }
                .split("\n")
                .drop(conf.data.plain.hasHeader ? 2 : 1) // removes the line with method name or both including Header
                .collect { String row ->
            row.split(conf.data.plain.delimiter as String).collect {
                def value = it.trim()
                value.matches(/\[.*\]/) ? parseValue(value) : value
            }
        }
    }

    /**
     * Parses string value to list or map depending on syntax format used in plain data file
     * e.g. [1,2,3] or [a:1, b:2, c:3].
     *
     * @param value string value to be parsed
     *
     * @return List or Map depending on syntax
     */
    private parseValue(String value) {
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
    private List getList(String value) {
        value[1..value.length() - 2].split(",").collect { String item -> item.trim() }
    }

    /**
     * Transforms a list into a map by splitting list values using ":" pattern.
     *
     * @param list list of string values to contain ":"
     *
     * @return map
     */
    private Map getMap(List<String> list) {
        list.collectEntries {
            def pair = it.split(":")
            [pair[0].trim(), pair[1].trim()]
        }
    }
}
