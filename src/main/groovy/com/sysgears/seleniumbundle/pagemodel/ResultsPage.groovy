package com.sysgears.seleniumbundle.pagemodel

import com.codeborne.selenide.*
import com.sysgears.seleniumbundle.core.pagemodel.AbstractPage
import com.sysgears.seleniumbundle.core.pagemodel.annotations.StaticElement
import com.sysgears.seleniumbundle.core.uicomparison.UIComparison
import io.qameta.allure.Step
import org.testng.Assert

import static com.codeborne.selenide.Selenide.$
import static com.codeborne.selenide.Selenide.$$

class ResultsPage extends AbstractPage<ResultsPage> implements UIComparison<ResultsPage> {

    @StaticElement
    private SelenideElement toolsButton = $("#hdtb-tls")

    @StaticElement
    private ElementsCollection results = $$("#ires .g"),
                               resultCategories = $$(".hdtb-imb")

    private ElementsCollection tools = $$(".hdtb-mn-hd")

    @Step("Select category")
    ResultsPage selectCategory(String name) {
        def category = resultCategories.find(Condition.text(name))

        if (!category.has(Condition.cssClass("hdtb-msel"))) {
            category.find("a").click()
        }
        this
    }

    @Step("Open tools menu")
    ResultsPage openToolsMenu() {
        toolsButton.click()
        this
    }

    @Step("Check the number of results")
    ResultsPage isResultSize(int size) {
        results.shouldHave(CollectionCondition.size(size))
        this
    }

    @Step("Check that all tools are present")
    ResultsPage areToolsPresent(List expectedTool) {
        tools.shouldHave(CollectionCondition.exactTexts(expectedTool))
        this
    }

    @Step("Check that there are given params in url")
    ResultsPage validateUrlParams(Map params) {
        def url = WebDriverRunner.url()
        def result = params.every { k, v ->
            v ? url.contains("$k=$v") : !url.contains("$k")
        }

        Assert.assertTrue(result, "Query '$params' were not found in url.")
        this
    }
}
