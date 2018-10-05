package com.sysgears.seleniumbundle.pagemodel

import com.codeborne.selenide.Selenide
import com.codeborne.selenide.SelenideElement
import com.sysgears.seleniumbundle.core.pagemodel.AbstractPage
import com.sysgears.seleniumbundle.core.pagemodel.annotations.StaticElement
import com.sysgears.seleniumbundle.core.uicomparison.UIComparison
import io.qameta.allure.Step
import org.apache.commons.io.FilenameUtils
import org.openqa.selenium.By

import static com.codeborne.selenide.Selenide.$

class GooglePage extends AbstractPage<GooglePage> implements UIComparison<GooglePage> {

    @StaticElement
    private SelenideElement queryField = $(By.name("q")),
                            logoImg = $("#hplogo")

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

    @Step("Replace logo image")
    GooglePage replaceLogo() {
        def path = FilenameUtils.separatorsToSystem("src/test/resources/data/google/colorfulLogo.txt")

        Selenide.executeJavaScript("arguments[0].src='${new File(path).text}'; arguments[0].srcset=''", logoImg)
        this
    }

    @Step("Remove focus from query field")
    GooglePage removeFocusFromQueryField() {
        queryField.pressEscape()
        this
    }
}
