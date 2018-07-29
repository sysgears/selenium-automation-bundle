package com.sysgears.seleniumbundle.common

import com.sysgears.seleniumbundle.core.utils.SoftAssert
import groovy.util.logging.Slf4j
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

/**
 * Specific configuration class for UI tests.
 */
@Slf4j
@Test(groups = "ui")
class UITest extends BaseTest {

    /**
     * Allows not to interrupt test method execution until the last assertion will be done.
     */
    protected SoftAssert softAssert = new SoftAssert()

    @BeforeMethod
    void cleanUp() {
        softAssert.clean()
    }
}
