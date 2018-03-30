package com.sysgears.seleniumbundle.core.data.exceptions

import com.sysgears.seleniumbundle.core.data.annotations.Locator

/**
 * Is thrown in case a parameter of a test method which uses data mapping mechanism doesn't have {@link Locator}.
 */
class MissingLocatorAnnotationException extends RuntimeException {

    /**
     * Constructs a new MissingLocatorAnnotationException with the specified detail message.
     *
     * @param message error message
     */
    MissingLocatorAnnotationException(String message) {
        super(message)
    }
}
