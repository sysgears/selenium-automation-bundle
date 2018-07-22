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
 * Is designed to be used together with TestNJ Data Provider methods. Is responsible for test data processing, finding
 * required data entries from the test data set and converting them into an Object[][].
 */
@Slf4j
class DataMapper {

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
    Object[][] map(List<Map> data, Method method, Object testClass)
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
