# Ignoring Elements in UI Testing

In this section, we'll review the demo example of a UI test with ignoring HTML elements on a page.

Ignoring page elements can be useful for UI testing especially when the page has an animation. Because of animations, UI
tests become brittle, but the UI Comparison module in our bundle comes with a solution &ndash; you can use the
`setIgnoredElements()` method that you can call on your page objects.

The method `setIgnoredElements()` is implemented in the `UIComparison` trait in the [UI Comparison] module.

## Page Object Demo

The page object `ShopidaiPage` below will be used for testing the layout of a custom web page that you can find in
`test/resources/web_page_with_animation/`. As you can see, `ShopidaiPage` implements `UIComparison`, therefore, when
you'll be testing the web page, you'll be able to use the `setIgnoredElements()` method.

```groovy
package com.sysgears.seleniumbundle.pagemodel

import com.codeborne.selenide.Selenide
import com.sysgears.seleniumbundle.core.pagemodel.AbstractPage
import com.sysgears.seleniumbundle.core.uicomparison.UIComparison

class ShopidaiPage extends AbstractPage<ShopidaiPage> implements UIComparison<ShopidaiPage> {

    ShopidaiPage open() {
        Selenide.open("file://${System.getProperty("user.dir")}/src/test/resources/" +
                "web_page_with_animation/index.html")
        this
    }
}
```

## UI Test Class Demo with Ignored Elements

The UI test class below is written for `ShopidaiPage` that we've reviewed in [Page Object Demo](#page-object-demo).
According to the [general recommendations on creating UI test classes], `IgnoreElementsExample` extends the `UITest`.

```groovy
package com.sysgears.seleniumbundle.tests.ignore_elements

import com.sysgears.seleniumbundle.common.UITest
import com.sysgears.seleniumbundle.pagemodel.ShopidaiPage
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

class IgnoreElementsExample extends UITest {

    protected ShopidaiPage shopidaiPage

    @BeforeMethod
    void openApplication() {
        shopidaiPage = new ShopidaiPage().setEnvironment(this).open().waitForPageToLoadElements()
    }

    /**
     * Shows that the test can fail due to dynamic changes on the page.
     */
    @Test(invocationCount = 2)
    void withoutIgnoringElements() {
        shopidaiPage.compareLayout("withoutIgnoring")
    }

    /**
     * Shows how the elements which may affect test result due to dynamic nature can be ignored.
     */
    @Test(invocationCount = 2)
    void withIgnoringElements() {
        shopidaiPage.setIgnoredElements([".animation-1"]).compareLayout("withIgnoring")
    }
}
```

Notice that you can ignore the HTML element with the `.animation-1` CSS selector. You can call this method at any time
in the chain, but we recommend you to ignore elements before calling the test method.

`ShopidaiPage` has an animation, so when you run this test, the method `withoutIgnoringElements()` will fail, and the
method `withIgnoringElements()` will pass successfully.

With `ShopidaiPage` and `IgnoreElementsExample` in place, you're ready to run the UI test to see how it works with Selenium
Automation Bundle.

## Running UI Tests with Ignoring Elements

You may want to set up TestNG configurations to run only `IgnoreElementsExample`. To do that, change the test package from
`"com.sysgears.seleniumbundle.tests.demo.*"` to `"com.sysgears.seleniumbundle.tests.ignore_elements.*"` in
`src/test/resources/testng.xml` or your TestNG configuration file.

If you haven't created the baseline screenshots, run the following command:

```bash
./gradlew -Dtest.baselineMode=true
```

Gradle will run the test `IgnoreElementsExample`, and then you can view the created baseline screenshots in the
`src/test/resources/uicomparison/baseline/` directory.

To execute `IgnoreElementsExample` with screenshots comparison, just run the following command to execute the tests and generate
a report:

```bash
./gradlew clean test allureServe
```

> If the report wasn't generated, you need to install Allure with `./gradlew downloadAllure`.

Only one test in `IgnoreElementsExample` class will pass successfully:

<p align="center">
    <img src="./img/ui-test-example.jpg" />
</p>

[ui comparison]: https://github.com/sysgears/selenium-automation-bundle/blob/master/docs/UI_Testing/UI_Comparison_Module.mdn
[general recommendations on creating ui test classes]: https://github.com/sysgears/selenium-automation-bundle/blob/master/docs/advanced/UI_Testing/General-Concept-and-Testing-Flow.md#General-Considerations-for-Writing-Test-Classes-for-UI-Tests.m