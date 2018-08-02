package com.sysgears.seleniumbundle.pagemodel

import com.codeborne.selenide.SelenideElement
import com.sysgears.seleniumbundle.core.pagemodel.AbstractPage
import com.sysgears.seleniumbundle.core.uicomparison.UIComparison
import org.openqa.selenium.By

import static com.codeborne.selenide.Selenide.$

class OrbitmediaPage extends AbstractPage<OrbitmediaPage> implements UIComparison<OrbitmediaPage> {

    private SelenideElement linkToFreePrivacyPolicySite = $(By.linkText("Free Privacy Policy Generator."))

    OrbitmediaPage() {
        this.url = "https://www.orbitmedia.com/blog/website-footer-design-best-practices/"
    }

    void goToFreePrivacyPolicySite() {
        linkToFreePrivacyPolicySite.click()
    }

    OrbitmediaPage hideFixedFooter() {
        hideElements($(".StickyFooter"))
    }
}