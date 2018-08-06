package com.sysgears.seleniumbundle.pagemodel

import com.codeborne.selenide.SelenideElement
import com.sysgears.seleniumbundle.core.pagemodel.AbstractPage
import com.sysgears.seleniumbundle.core.pagemodel.annotations.StaticElement
import com.sysgears.seleniumbundle.core.uicomparison.UIComparison
import org.openqa.selenium.By

import static com.codeborne.selenide.Selenide.$

class FreePrivacyPolicyPage extends AbstractPage<FreePrivacyPolicyPage> implements UIComparison<FreePrivacyPolicyPage> {

    @StaticElement
    private SelenideElement logoFreePrivacyPolicy = $(By.linkText("FreePrivacyPolicy.com"))

    FreePrivacyPolicyPage() {
        this.url = "https://www.freeprivacypolicy.com/"
    }

    FreePrivacyPolicyPage checkTheUrl() {
        assertUrl()
        this
    }
}
