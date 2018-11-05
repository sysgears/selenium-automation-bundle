package com.sysgears.seleniumbundle.core.pagemodel

import com.codeborne.selenide.*
import com.sysgears.seleniumbundle.core.conf.Config
import com.sysgears.seleniumbundle.core.pagemodel.annotations.StaticElement
import groovy.util.logging.Slf4j
import org.openqa.selenium.Keys

import java.lang.reflect.Field

import static com.codeborne.selenide.WebDriverRunner.url
import static org.testng.Assert.assertTrue

/**
 * Provides common methods for page model objects.
 */
@Slf4j
abstract class AbstractPage<T> {

    /**
     * Instance of Config.
     */
    protected Config conf = Config.instance

    /**
     * URL of the page.
     */
    protected String url

    /**
     * Opens a web page.
     *
     * @return page object
     */
    T open() {
        Selenide.open(url)
        assertUrl()
        (T) this
    }

    /**
     * Asserts whether the current URL matches the page's URL.
     */
    void assertUrl() {
        assertTrue(url().contains(url), "Given URL is: [$url], real URL is: [${url()}]")
    }

    /**
     * Clears a page text input.
     *
     * @param element web element to be cleared
     */
    void clearTextInput(SelenideElement element) {
        element.getValue()?.length()?.times {
            element.sendKeys(Keys.BACK_SPACE)
        }
    }

    /**
     * Clears a set of text inputs.
     *
     * @param list list of web elements to be cleared
     */
    void clearTextInputs(List<SelenideElement> list) {
        list.each {
            clearTextInput(it)
        }
    }

    /**
     * Checks if static elements of the page object are loaded and exist in Document Object Model.
     *
     * @return page object
     */
    T waitForPageToLoadElements() {
        Field[] fields = this.getClass().getDeclaredFields()

        fields.each { field ->
            StaticElement annotation = field.getAnnotation(StaticElement.class)
            if (annotation && field.getType() in [SelenideElement.class, ElementsCollection.class]) {
                field.setAccessible(true)

                try {
                    def element = field.get(this)
                    if (field.getType() == SelenideElement.class) {
                        log.trace("Checking if Selenide Element exists: $element")
                        (element as SelenideElement).should(Condition.exist)
                    } else {
                        log.trace("Checking if Elements Collection exists: $element")
                        (element as ElementsCollection).shouldHave(CollectionCondition.sizeGreaterThan(0))
                    }
                } catch (IllegalAccessException e) {
                    log.error("Unable to get the element: ${field.name}", e)
                    throw new IllegalAccessException("Unable to the get element: ${field.name}")
                }
            }
        }
        log.info("${this.getClass().getSimpleName()} has been loaded")
        (T) this
    }

    /**
     * Sets an element's visibility to hidden in order to hide an element.
     *
     * @param elements element to be hidden
     *
     * @return page object
     */
    protected T hideElements(SelenideElement... elements) {
        elements.each {
            Selenide.executeJavaScript("arguments[0].style.visibility='hidden'", it)
        }
        (T) this
    }

    /**
     * Sets the visibility to hidden for a collection of elements.
     *
     * @param elementsCollections collection of elements to be hidden
     *
     * @return page object
     */
    protected T hideElements(ElementsCollection... elementsCollections) {
        elementsCollections.each { collection ->
            collection.each {
                hideElements(it)
            }
        }
        (T) this
    }
}