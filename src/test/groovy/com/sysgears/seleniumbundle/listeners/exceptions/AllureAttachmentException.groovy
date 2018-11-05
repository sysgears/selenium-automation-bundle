package com.sysgears.seleniumbundle.listeners.exceptions

/**
 * Is thrown in case of I/O exception that occurs when a screenshot for a failed test
 * is attached to the Allure test report.
 */
class AllureAttachmentException extends IOException {

    /**
     * Instantiates a new AllureAttachmentException with a specified message and cause.
     *
     * @param message error message
     * @param cause error cause
     */
    AllureAttachmentException(String message, Throwable cause) {
        super(message, cause)
    }
}
