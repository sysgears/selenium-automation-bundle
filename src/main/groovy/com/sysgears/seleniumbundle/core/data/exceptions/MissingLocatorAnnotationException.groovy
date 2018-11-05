package com.sysgears.seleniumbundle.core.data.exceptions

import com.sysgears.seleniumbundle.core.data.annotations.Locator

/**
 * Is thrown when a parameter of a test method that uses the data mapping mechanism does not have {@link Locator}.
 */
class MissingLocatorAnnotationException extends RuntimeException {

    /**
     * Constructs a new MissingLocatorAnnotationException with the specified message.
     *
     * @param message error message
     */
    MissingLocatorAnnotationException(String message) {
        super(message)
    }
}
