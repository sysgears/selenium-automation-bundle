package com.sysgears.seleniumbundle.tests.ui

import com.sysgears.seleniumbundle.common.UITest
import com.sysgears.seleniumbundle.pagemodel.GooglePage
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

class UITestExample extends UITest {

    private GooglePage googlePage

    @BeforeMethod
    void openApplication() {
        googlePage = new GooglePage()
                .setEnvironment(this)
                .open()
                .waitForPageToLoadElements()

        /** setIgnoredElements() is necessary to remove instability caused by the blinking of the cursor
         *  and CSS shadow property changes under the Google query field.
         */
                .setIgnoredElements(["#tsf"])
    }

    /**
     * Checks the layout of the Google search page that wasn't modified.
     */
    @Test
    void checkWithoutChangesInLayout() {
        googlePage.compareLayout("successful_case")
    }

    /**
     * Checks the layout of the Google search page that was modified.
     */
    @Test
    void checkWithChangesInLayout() {

        /**
         * Checks if the test runs in baseline mode.
         * If the baselineMode is true, then the Google logo will be replaced and the UI
         * test will fail.
         */
        if (!conf.baselineMode) {
            googlePage.replaceLogo()
        }

        googlePage.compareLayout("case_with_changes")
    }
}
