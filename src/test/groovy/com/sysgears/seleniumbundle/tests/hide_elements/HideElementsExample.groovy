package com.sysgears.seleniumbundle.tests.hide_elements

import com.sysgears.seleniumbundle.common.FunctionalTest
import com.sysgears.seleniumbundle.pagemodel.FreePrivacyPolicyPage
import com.sysgears.seleniumbundle.pagemodel.OrbitmediaPage
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

class HideElementsExample extends FunctionalTest {

    protected OrbitmediaPage orbitmediaPage

    @BeforeMethod
    void openApplication() {
        orbitmediaPage = new OrbitmediaPage().setEnvironment(this).open().waitForPageToLoadElements()
    }

    /**
     * This test tries to click on the link, which leads to another site, but fails because when Selenium scrolls to this
     * link to click on, the link appears under the fixed footer.
     */
    @Test(invocationCount = 2)
    void clickOnElementUnderTheFixedFooter() {
        orbitmediaPage.goToFreePrivacyPolicySite()
    }

    /**
     * This test hides the fixed footer and then clicks on the link. As a final step, it checks the url of the new web
     * page.
     */
    @Test(invocationCount = 2)
    void hideFixedFooterAndClickOnTheElement() {
        orbitmediaPage
                .hideFixedFooter()
                .goToFreePrivacyPolicySite()

        new FreePrivacyPolicyPage().waitForPageToLoadElements()
                .assertUrl()
    }
}
