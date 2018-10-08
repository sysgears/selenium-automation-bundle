package com.sysgears.seleniumbundle.tests.ui

import com.sysgears.seleniumbundle.common.UITest
import com.sysgears.seleniumbundle.pagemodel.ShopidaiPage
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

class UITestIgnoringElementsExample extends UITest {

    private ShopidaiPage shopidaiPage

    @BeforeMethod
    void openApplication() {
        shopidaiPage = new ShopidaiPage().setEnvironment(this).open().waitForPageToLoadElements()
    }

    /**
     * Shows that the test can fail because of dynamic changes on the page.
     */
    @Test(invocationCount = 2)
    void withoutIgnoringElements() {
        shopidaiPage.compareLayout("withoutIgnoring")
    }

    /**
     * Shows how the elements can be ignored if they may affect test result because of their dynamic nature.
     */
    @Test(invocationCount = 2)
    void withIgnoringElements() {
        shopidaiPage.setIgnoredElements([".animation-1"]).compareLayout("withIgnoring")
    }
}
