package com.sysgears.seleniumbundle.core.utils

import groovy.util.logging.Slf4j

/**
 * Enables soft assertions in test methods with multiple assertion points.
 */
@Slf4j
class SoftAssert {

    private failedAssertions = []

    /**
     * Joins all stored assertion errors and generates new AssertionError.
     *
     * @throws AssertionError if at least one assertion error has been stored.
     */
    void assertAll() throws AssertionError {
        if (failedAssertions) {
            throw new AssertionError("${failedAssertions.size()}" +
                    " failed assertions found:\n${failedAssertions*.message.join('\n')}")
        }
    }

    /**
     * Executes the closure code and catches any assertion errors.
     *
     * @param closure that has the test logic, which should be softly asserted
     * @return SoftAssert instance for chaining methods
     */
    SoftAssert 'catch'(Closure closure) {
        try {
            closure()
        } catch (AssertionError e) {
            log.error(e.message, e)
            failedAssertions << e
        }
        this
    }

    /**
     * Cleans the list of failed assertions.
     */
    void clean() {
        failedAssertions.clear()
    }
}
