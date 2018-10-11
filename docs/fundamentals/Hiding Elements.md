# Hiding Elements When Testing with Selenium Automation Bundle

In this section, we'll review the demo example of a UI test with hiding elements.

Hiding page elements can be useful for UI testing when there are overlapping HTML elements on a page. For example, your
application may have a footer that appears at the bottom of a page when the page is scrolled, and then footer becomes
always visible.

When you use Selenide to scroll to some element, the element may be hidden in this case by the footer, and your test
will fail. The UI Comparison module in our bundle provides a very simple solution: You can call the `hideElements()`
method on your page objects when you need to ignore an element, and your test will run.

The `hideElements()` method basically sets the style for the element that you need to hide.

## Page Object for Demoing Hiding Elements

The page object `OrbitmediaPage` below will be used for testing the layout with a "sticky" footer. `OrbitmediaPage` is
located in the `src/main/.../seleniumbundle/pagemodel/` directory next to other demo page objects.

Notice that `OrbitmediaPage` inherits `AbstractPage`, a base page object that implements the method `hideElements()`.
You'll be able to call this method when testing the page:

```groovy
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
```

As we can see, this page object implements the `hideFixedFooter()` method, which internally calls `hideElements()`.
The `hideElements` method accepts a Selenide locator, in this case, a CSS class selector for the footer &ndash;
`.StickyFooter`.

## Testing a Page with Hidden Elements

The `HideElementsExample` test class below is written for `OrbitmediaPage`. As you can see in the method
`hideFixedFooterAndClickOnTheElement()`, you just call `hideFixedFooter()` on the instance of `OrbitmediaPage` before
running other methods:

```groovy
package com.sysgears.seleniumbundle.tests.hide_elements

import com.sysgears.seleniumbundle.common.FunctionalTest
import com.sysgears.seleniumbundle.pagemodel.FreePrivacyPolicyPage
import com.sysgears.seleniumbundle.pagemodel.OrbitmediaPage
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

class HideElementsExample extends FunctionalTest {

    protected OrbitmediaPage orbitmediaPage

    @BeforeMethod
    void openApplication() {
        orbitmediaPage = new OrbitmediaPage().setEnvironment(this).open().waitForPageToLoadElements()
    }

    /**
     * This test tries to click on the link, which leads to another site, but fails because when Selenium scrolls to this
     * link to click on, the link appears under the fixed footer.
     */
    @Test(invocationCount = 2)
    void clickOnElementUnderTheFixedFooter() {
        orbitmediaPage.goToFreePrivacyPolicySite()
    }

    /**
     * This test hides the fixed footer and then clicks on the link. As a final step, it checks the url of the new web
     * page.
     */
    @Test(invocationCount = 2)
    void hideFixedFooterAndClickOnTheElement() {
        orbitmediaPage
                .hideFixedFooter()
                .goToFreePrivacyPolicySite()

        new FreePrivacyPolicyPage().waitForPageToLoadElements()
                .assertUrl()
    }
}
```

## Running UI Tests with Hiding Elements

You may want to set up TestNG configurations to run only `HideElementsExample`. To do that, change the test package from
`"com.sysgears.seleniumbundle.tests.demo.*"` to `"com.sysgears.seleniumbundle.tests.hide_elements.*"` in
`src/test/resources/testng.xml` or your TestNG configuration file.

If you haven't created the baseline screenshots, run the following command:

```bash
./gradlew -Dtest.baselineMode=true
```

Gradle will run the test `HideElementsExample`, and then you can view the created baseline screenshots in the
`src/test/resources/uicomparison/baseline/` directory.

To execute `HideElementsExample` with screenshots comparison, just run the following command _after_ you created the
baseline screenshots:

```bash
./gradlew clean test allureServe
```

This command will execute the tests and generate a report. If the report wasn't generated, you need to install Allure
with `./gradlew downloadAllure`, and then run `./gradlew clean test allureServe` again.

Only one test in `UITestExample` class will pass successfully:

<p align="center">
    <img src="./img/ui-test-example.jpg" />
</p>