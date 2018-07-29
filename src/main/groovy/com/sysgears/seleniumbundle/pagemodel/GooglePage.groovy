package com.sysgears.seleniumbundle.pagemodel

import com.codeborne.selenide.SelenideElement
import com.sysgears.seleniumbundle.core.pagemodel.AbstractPage
import com.sysgears.seleniumbundle.core.pagemodel.annotations.StaticElement
import com.sysgears.seleniumbundle.core.uicomparison.UIComparison
import io.qameta.allure.Step
import org.openqa.selenium.By

import static com.codeborne.selenide.Selenide.$

class GooglePage extends AbstractPage<GooglePage> implements UIComparison<GooglePage> {

    @StaticElement
    private SelenideElement queryField = $(By.name("q"))

    GooglePage() {
        this.url = "/"
    }

    @Step("Select language")
    GooglePage selectLanguage(String language = "English") {
        def element = $(By.linkText(language))

        if (element.exists()) {
            element.click()
        }
        this
    }

    @Step("Perform search")
    void searchFor(String query) {
        enterQuery(query)
        submit()
    }

    @Step("Enter query")
    private GooglePage enterQuery(String query) {
        queryField.val(query)
        this
    }

    @Step("Submit search")
    private void submit() {
        queryField.pressEnter()
    }
}
