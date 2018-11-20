package com.sysgears.seleniumbundle.core.data

import com.sysgears.seleniumbundle.core.conf.Config
import com.sysgears.seleniumbundle.core.data.annotations.Find
import com.sysgears.seleniumbundle.core.data.annotations.Locator
import com.sysgears.seleniumbundle.core.data.annotations.Query
import com.sysgears.seleniumbundle.core.data.exceptions.MissingLocatorAnnotationException
import com.sysgears.seleniumbundle.core.data.utils.DataFileParser
import com.sysgears.seleniumbundle.core.data.utils.MapHelper
import com.sysgears.seleniumbundle.core.utils.AnnotationHelper
import groovy.util.logging.Slf4j

import java.lang.reflect.Method

/**
 * Is designed to be used together with TestNJ Data Provider methods. Is responsible for test data processing, finding
 * required data entries from the test data set and converting them into an Object[][].
 */
@Slf4j
class DataMapper {

    /**
     * Project properties.
     */
    private Config conf

    /**
     * Creates an instance of DataMapper
     *
     * @param conf project properties
     */
    DataMapper(Config conf) {
        this.conf = conf
    }

    /**
     * Finds sub sets of test data required for a test method execution from the given test data collection by using
     * {@link Query}, {@link Find} annotations defined on the method, identifies specific data entries in the sub
     * sets by {@link Locator}. Returns selected data entries as an Object[][].
     *
     * @param data structured test data
     * @param method method which uses TestNG Data Provider
     *
     * @return Object[][]
     *
     * @throws MissingLocatorAnnotationException if method parameter lacks locator annotation
     * @throws RuntimeException if while getting the value from data set any of the non-terminal steps into nested map
     * return non-map instance
     */
    Object[][] map(List<Map> data, Method method)
            throws MissingLocatorAnnotationException, RuntimeException {

        // reads a search criteria represented by key-value pairs (helps to find specific test records in the raw data)
        Map<String, String> criteria = method.getAnnotation(Query.class)?.value()
                ?.collectEntries { it -> [it.name(), it.value()] }

        // gets all annotations from method parameters
        List<Map> annotations = AnnotationHelper.getParameterAnnotations(method)

        // finds and collects particular test data from data sets by specified locators for each criterion
        def dataSets = criteria ? criteria.collect { criterion ->
            data.findAll { dataSet ->
                criterion.value == MapHelper.getValue(criterion.key, dataSet)
            }
        }.flatten() : data

        dataSets.collect { Map dataSet ->
            annotations.collect { Map paramAnnotations ->

                if (!paramAnnotations.locator) {
                    log.error("The [Locator] annotation is missing for some of " +
                            "the arguments of the [${method.getName()}] method.")
                    throw new MissingLocatorAnnotationException("The [Locator] annotation is missing for some of " +
                            "the arguments of the [${method.getName()}] method.")
                }

                MapHelper.getValue(paramAnnotations.locator.value(), dataSet)
            } as Object[]
        } as Object[][]
    }

    /**
     * Finds sub sets of test data required for a test method execution from the given string in a pipe separated format.
     * Uses "|" pipe delimiter for separating values and "new line" for separating data sets for different test methods.
     * Removes header for
     *
     * @param filePath path to file
     * @param method method which uses TestNG Data Provider
     * @param areHeaders if true, removes the first line of the data set with headers (column names), else does nothing
     *
     * @return Object[][]
     */
    Object[][] mapFromDataFile(Map rawMap, Method method, Boolean isHeader = true) {
        def data = isHeader ? dropHeaders(rawMap) : rawMap

        data.collectEntries { methodName, rawData ->
            Object[][] methodData = rawData.collect { String row ->
                row.split(conf.data.csv.delimiter as String).collect {
                    def value = it.trim()
                    value.matches(/\[.*\]/) ? DataFileParser.parseValue(value) : value
                }
            }
            [methodName, methodData]
        }[method.getName()]
    }

    /**
     * Removes first row (column names) of map values.
     *
     * @param data map with data
     *
     * @return map
     */
    private Map dropHeaders(Map<String, String[]> data) {
        data.collectEntries { [it.key, it.value.drop(1)] }
    }
}