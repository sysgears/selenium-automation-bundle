package com.sysgears.seleniumbundle.core.uicomparison

/**
 * Interface that represents test environment and provides information about operation system and browser.
 */
interface IEnvironment {

    String getOs()

    String getBrowser()
}