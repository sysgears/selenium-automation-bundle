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
     * This test tries to click on the link which leads to other site, but fails because when Selenium scrolls this link
     * to click on it appears under the fixed footer.
     */
    @Test(invocationCount = 5)
    void clickOnElementUnderTheFixedFooter() {
        softAssert.catch {
            orbitmediaPage.goToWebSitePlagiarismArticle()
        }.assertAll()
    }

    /**
     * This test hides the fixed footer and then clicks on the link. As a final step it check the url of the new web
     * page.
     */
    @Test(invocationCount = 2)
    void hideFixedFooterAndClickOnTheElement() {
        orbitmediaPage
                .hideFixedFooter()
                .goToWebSitePlagiarismArticle()
        new FreePrivacyPolicyPage().waitForPageToLoadElements()
                .checkTheUrl()
    }
}
