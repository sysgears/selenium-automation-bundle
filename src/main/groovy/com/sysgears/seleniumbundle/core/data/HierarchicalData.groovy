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
 * Represents hierarchical data retrieved from .yml files.
 */
@Slf4j
class HierarchicalData extends AbstractTestData {

    /**
     * Instantiates a hierarchical data object.
     *
     * @param rawData raw data retrieved from a .yml file
     * @param clazz a test class that contains meta information (annotations on methods) for getting test data
     */
    HierarchicalData(List rawData, Class clazz) {
        super(rawData, clazz)
    }

    /**
     * Finds subsets of test data required for test method execution from the given test data collection by using
     * the {@link Query} and {@link Find} annotations defined on the method, identifies specific data entries in the
     * sub-sets by {@link Locator}. Returns selected data entries as Object[][].
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
    protected Object[][] map(List data, Method method)
            throws MissingLocatorAnnotationException, RuntimeException {

        // gets all annotations from method parameters
        List<Map> annotations = AnnotationHelper.getParameterAnnotations(method)

        // finds and collects particular test data from data sets by specified locators for each criterion
        filter(data, getCriteria(method)).collect { Map dataSet ->
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
     * Filters given list of data by given search criteria.
     *
     * @param data list of data
     * @param criteria search criteria as map of strings
     *
     * @return list with data sets that correspond to given criteria
     */
    private List filter(List data, Map<String, String> criteria) {
        if (criteria) {
            criteria.collect { criterion ->
                data.findAll { Map dataSet ->
                    criterion.value == MapHelper.getValue(criterion.key, dataSet)
                }
            }.flatten()
        } else {
            data
        }
    }

    /**
     * Reads search criteria from {@link Query} annotations applied the given method.
     * (helps to find specific test records in the raw data)
     *
     * @param method test method to get annotations from
     *
     * @return map of string pairs retrieved from {@link Find} annotations
     */
    private Map<String, String> getCriteria(Method method) {
        method.getAnnotation(Query.class)?.value()
                ?.collectEntries { findAnnotation -> [findAnnotation.name(), findAnnotation.value()] }
    }
}
