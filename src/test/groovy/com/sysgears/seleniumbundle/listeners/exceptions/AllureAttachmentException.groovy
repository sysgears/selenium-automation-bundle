package com.sysgears.seleniumbundle.listeners.exceptions

/**
 * Is thrown in case of I/O exception occurred while attaching a screenshot for failed test to allure test report.
 */
class AllureAttachmentException extends IOException {

    /**
     * Constructs a new AllureAttachmentException with the specified detail message, and cause.
     *
     * @param message error message
     * @param cause error cause
     */
    AllureAttachmentException(String message, Throwable cause) {
        super(message, cause)
    }
}
