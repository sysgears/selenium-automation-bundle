package com.sysgears.seleniumbundle.core.uicomparison

/**
 * Adds methods to perform layout comparison with baseline screenshots.
 *
 * @param < T >  particular Page Object type that implements trait is required for chaining methods
 */
trait UIComparison<T> {

    /**
     * AShot service.
     */
    private AShotService aShotService

    /**
     * Test environment object.
     */
    private IEnvironment environment

    /**
     * List of elements that should be excluded from comparison analysis (advertisement banners, etc.).
     */
    private List ignoredElements

    /**
     * Compares layout for web page.
     *
     * @param name name of a screenshot
     *
     * @return page object
     */
    T compareLayout(String name) {
        ashotService.compareLayout(name)
        this as T
    }

    /**
     * Sets test environment.
     *
     * @param environment environment object
     *
     * @return page object
     */
    T setEnvironment(IEnvironment environment) {
        this.environment = environment
        aShotService = null
        this as T
    }

    /**
     * Sets elements which should be excluded from comparison analysis
     *
     * @param ignoredElements list of css locators for elements which should be excluded from comparison analysis
     *
     * @return page object
     */
    T setIgnoredElements(List ignoredElements) {
        this.ignoredElements = ignoredElements
        this as T
    }

    /**
     * Returns AShot service instance, creates if the service is null.
     *
     * @return AShot service
     */
    private AShotService getAshotService() {
        aShotService = aShotService ?: new AShotService(conf, environment, ignoredElements ?: [])
    }
}