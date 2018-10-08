package com.sysgears.seleniumbundle.core.uicomparison

/**
 * The interface that represents a test environment and provides information about the operation system and the browser.
 */
interface IEnvironment {

    String getOs()

    String getBrowser()
}