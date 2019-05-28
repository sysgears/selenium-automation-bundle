package com.sysgears.seleniumbundle.core.data

import com.sysgears.seleniumbundle.core.conf.Config
import org.testng.annotations.Test

import java.lang.reflect.Method

/**
 * Abstract class to be extended by the classes that provide implementations for the test data objects.
 */
abstract class AbstractTestData {

    /**
     * Project properties
     */
    protected Config conf = Config.instance

    /**
     * Test data
     */
    protected Map<String, Object[][]> data

    /**
     * Creates a new Test Data instance.
     *
     * @param rawData raw test data retrieved from data files
     * @param clazz test class that contains test methods to be run
     */
    AbstractTestData(List<Map> rawData, Class clazz) {
        data = clazz.getMethods().findAll { it.getAnnotation(Test.class) }.collectEntries { method ->
            [method.name, map(rawData, method)]
        }
    }

    /**
     * Returns test data.
     *
     * @return map where the keys are the methods names and values are the test data as Object[][] for corresponding
     * test method.
     */
    Map<String, Object[][]> getData() {
        data
    }

    /**
     * Abstract method for mapping test data to particular test method parameters. Created data structure required for
     * TestNG DataProvider.
     *
     * @param data raw test data retrieved from data files
     * @param method test methods for which the data should be mapped
     *
     * @return Object[][] of test data for particular test method
     */
    abstract protected Object[][] map(List<Map> data, Method method)
}
