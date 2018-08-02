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
     * Opens web page.
     *
     * @return page object
     */
    T open() {
        Selenide.open(url)
        assertUrl()
        (T) this
    }

    /**
     * Asserts whether current URL matches the page URL.
     */
    void assertUrl() {
        assertTrue(url().contains(url), "Given url is: [$url], real url is: [${url()}]")
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
     * @param list web element list to be cleared
     */
    void clearTextInputs(List<SelenideElement> list) {
        list.each {
            clearTextInput(it)
        }
    }

    /**
     * Checks if the static elements of the page object are loaded and exist in DOM.
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
                    log.error("Unable to get element: ${field.name}", e)
                    throw new IllegalAccessException("Unable to get element: ${field.name}")
                }
            }
        }
        log.info("${this.getClass().getSimpleName()} has been loaded")
        (T) this
    }

    /**
     * Sets CSS style visibility to hidden in order to hide an element.
     *
     * @param elements element which has to be hidden
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
     * Sets CSS style visibility to hidden in order to hide a collection of elements.
     *
     * @param elementsCollections collection of elements which has to be hidden
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