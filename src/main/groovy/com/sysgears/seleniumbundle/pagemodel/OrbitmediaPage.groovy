package com.sysgears.seleniumbundle.pagemodel

import com.codeborne.selenide.Selenide
import com.codeborne.selenide.SelenideElement
import com.sysgears.seleniumbundle.core.pagemodel.AbstractPage
import com.sysgears.seleniumbundle.core.uicomparison.UIComparison
import org.openqa.selenium.By

import static com.codeborne.selenide.Selenide.$

class OrbitmediaPage extends AbstractPage<OrbitmediaPage> implements UIComparison<OrbitmediaPage> {

    private SelenideElement linkToFreePrivacyPolicySite = $(By.linkText("Free Privacy Policy Generator."))

    /**
     * Opens local version of Orbitmedia web page.
     *
     * @return OrbitmediaPage for chaining methods
     */
    OrbitmediaPage open() {
        Selenide.open("https://www.orbitmedia.com/blog/website-footer-design-best-practices/")
        this
    }

    void goToFreePrivacyPolicySite() {
        linkToFreePrivacyPolicySite.click()
    }

    OrbitmediaPage hideFixedFooter() {
        hideElement($(".StickyFooter"))
    }
}