# Detailed Guide on UI Testing with Selenium Automation Bundle

To be updated...

## UI Test Example

```groovy
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

        /** setIgnoredElements() is necessary to remove instability related to cursor blinking
         *  and shadow changes under the Google query field.
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

        // the block below changes the default Google logo to custom one if we don't run the tests in the baseline mode
        if (!conf.baselineMode) {
            googlePage.replaceLogo()
        }

        googlePage.compareLayout("case_with_changes")
    }
}
```