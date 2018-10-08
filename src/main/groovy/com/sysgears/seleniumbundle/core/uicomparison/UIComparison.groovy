package com.sysgears.seleniumbundle.core.uicomparison
/**
 * The trait that adds methods to perform layout comparison using baseline screenshots.
 *
 * @param < T > concrete page object type that implements the trait is required for chaining methods
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
     * List of CSS locators for elements which should be excluded from comparison.
     */
    private List<String> ignoredElements

    /**
     * Compares the layout of a web page.
     *
     * @param name name of a screenshot
     *
     * @return page object
     *
     * @throws IOException if there is no baseline screenshot during comparison or a file with ignored
     * elements was not found
     * @throws AssertionError if the layout of the new screenshot does not match the baseline screenshot
     * @throws IllegalArgumentException if ApplicationProperties.groovy has no "ui.path" properties with
     * the paths to screenshots
     */
    T compareLayout(String name) throws IOException, AssertionError, IllegalArgumentException {
        AShotService.compareLayout(name)
        this as T
    }

    /**
     * Sets the test environment.
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
     * Sets the elements that should be excluded from comparison.
     *
     * @param ignoredElements list of CSS locators for the elements to be excluded from comparison
     *
     * @return page object
     */
    T setIgnoredElements(List<String> ignoredElements) {
        this.ignoredElements = ignoredElements
        aShotService = null
        this as T
    }

    /**
     * Returns an AShotService instance or creates a new instance of AShotService if the service is null.
     *
     * @return AShot service
     */
    AShotService getAShotService() {
        aShotService = aShotService ?: new AShotService(conf, environment, ignoredElements ?: [])
    }
}