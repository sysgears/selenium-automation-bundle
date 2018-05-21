package com.sysgears.seleniumbundle.pagemodel

import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.WebDriverRunner
import com.sysgears.seleniumbundle.core.pagemodel.AbstractPage
import com.sysgears.seleniumbundle.core.pagemodel.annotations.StaticElement
import com.sysgears.seleniumbundle.core.uicomparison.ScreenshotHandler
import groovy.util.logging.Slf4j
import io.qameta.allure.Step
import org.openqa.selenium.By

import static com.codeborne.selenide.Selenide.$

@Slf4j
class GooglePage extends AbstractPage<GooglePage> {

    @StaticElement
    private SelenideElement queryField = $(By.name("q"))

    GooglePage() {
        this.url = "/"
    }

    GooglePage(String os, String browser) {
        this.url = "/"
        this.os = os
        this.browser = browser
        handler = new ScreenshotHandler(aShotFactory.getAShotForPage(os, browser, getClass()),
                WebDriverRunner.getWebDriver())
    }

    @Step("Select language")
    GooglePage selectLanguage(String language = "English") {
        def element = $("#CToSde").find(By.linkText(language))

        if (element.exists()) {
            element.click()
        }
        this
    }

    @Step("Perform search")
    ResultsPage searchFor(String query) {
        enterQuery(query)
        submit()
    }

    @Step("Enter query")
    private GooglePage enterQuery(String query) {
        queryField.val(query)
        this
    }

    @Step("Submit search")
    private ResultsPage submit() {
        queryField.pressEnter()
        new ResultsPage()
    }
}
