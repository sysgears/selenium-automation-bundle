package com.sysgears.seleniumbundle.common

import com.sysgears.seleniumbundle.core.utils.SoftAssert
import com.sysgears.seleniumbundle.listeners.AllureListener
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Listeners
import org.testng.annotations.Test

@Listeners(AllureListener)
@Test(groups = "functional")
class FunctionalTest extends BaseTest {

    /**
     * Allows not to interrupt test method execution until the last assertion will be done.
     */
    protected SoftAssert softAssert = new SoftAssert()

    @BeforeMethod
    void cleanUp() {
        softAssert.clean()
    }
}
