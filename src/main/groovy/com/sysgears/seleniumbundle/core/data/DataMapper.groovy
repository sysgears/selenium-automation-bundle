package com.sysgears.seleniumbundle.core.data

import com.sysgears.seleniumbundle.core.data.annotations.Find
import com.sysgears.seleniumbundle.core.data.annotations.Locator
import com.sysgears.seleniumbundle.core.data.annotations.Query
import com.sysgears.seleniumbundle.core.data.exceptions.MissingLocatorAnnotationException
import com.sysgears.seleniumbundle.core.data.utils.MapHelper
import com.sysgears.seleniumbundle.core.utils.AnnotationHelper
import groovy.util.logging.Slf4j

import java.lang.reflect.Method

/**
 * Designed to be used with the TestNG Data Provider methods. Responsible for processing test data, finding requested
 * entries from the data set and converting them into Object[][].
 */
@Slf4j
class DataMapper {

    /**
     * Finds subsets of the test data required for test method execution from the given test data collection by using
     * the {@link Query} and {@link Find} annotations defined on the method, identifies specific data entries in the
     * subsets by {@link Locator}. Returns selected data entries as Object[][].
     *
     * @param data structured test data
     * @param method method which uses TestNG Data Provider
     *
     * @return Object[][]
     *
     * @throws MissingLocatorAnnotationException if method parameter lacks the locator annotation
     * @throws RuntimeException if any of the non-terminal steps in the nested map return a non-map instance while
     * getting the value from a data set
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
}
