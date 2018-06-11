package com.sysgears.seleniumbundle.core.selenide.commands

import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import groovy.util.logging.Slf4j
import org.openqa.selenium.WebElement

import static com.codeborne.selenide.Configuration.clickViaJs
import static com.codeborne.selenide.Selenide.actions
import static com.codeborne.selenide.Selenide.executeJavaScript

/**
 * Reimplementation of default Selenide command Click.
 */
@Slf4j
class Click extends com.codeborne.selenide.commands.Click {

    @Override
    public Void execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
        if (args == null || args.length == 0) {
            click(locator.getWebElement())
        } else if (args.length == 2) {
            click(locator.getWebElement(), (int) args[0], (int) args[1])
        }
        return null;
    }

    protected void click(WebElement element) {
        if (clickViaJs) {
            executeJavaScript("arguments[0].click()", element)
        } else {
            element.click()
        }
    }

    protected void click(WebElement element, int offsetX, int offsetY) {
        if (clickViaJs) {
            executeJavaScript("arguments[0].dispatchEvent(new MouseEvent('click', {" +
                    "'view': window," +
                    "'bubbles': true," +
                    "'cancelable': true," +
                    "'clientX': arguments[0].getClientRects()[0].left + arguments[1]," +
                    "'clientY': arguments[0].getClientRects()[0].top + arguments[2]" +
                    "}))",
                    element,
                    offsetX,
                    offsetY)
        } else {
            actions()
                    .moveToElement(element, offsetX, offsetY)
                    .click()
                    .build()
                    .perform()
        }
    }
}