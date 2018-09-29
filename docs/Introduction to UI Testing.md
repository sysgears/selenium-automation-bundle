# Introduction to User Interface (Layout) Testing with Selenium Automation Bundle

## Testing the User Interface by Comparing Screenshots

Selenium Automation Bundle provides a simple way to test application layouts. Using the bundle, you can take screenshots
of the initial, tested layout that looks fine. After that, when the UI changes, you can run the same UI tests again,
generate a report and simply view where the changes were made. The bundle will take new screenshots, find the
differences, create new images with highlighted differences between the original and updated layouts, and attach them to
the report. Your only task is to validate what has changed, and report bugs if any.

You can also consult the [advanced guide] on UI testing, where we explain in great details how test the UI using our
bundle.

## Table of Contents

* [Demo Test Classes and Page Objects](#demo-ui-test)
* [Steps for Testing the User Interface](#steps-for-testing-the-user-inteface)
* [Creating Baseline Screenshots](#creating-baseline-screenshots)
* [Page Object Demo for UI Testing](#page-object-demo-for-ui-testing)
* [UI Test Class Demo](#ui-test-class-demo)
* [Conclusions](#conclusions)

## Demo UI Test

Selenium Automation Bundle comes with a basic example of UI test. Here are two files we're going to look at in this
guide:

* The `ShopidaiPage` page object, located at `src/main/.../seleniumbundle/pagemodel/` .
* The `UITestExample` test class for `ShopidaiPage`, located at `test/.../tests/ui/`.

> To learn more about page objects and test classes, consult the [page object] and [test classes] sections in the
general guide to testing with the bundle.

## Steps for Testing the User Interface

Let's review the steps for writing a UI test with Selenium Automation Bundle.

> **NOTE**: Before you write the first UI tests, test the application manually to ensure that there are no issues with
the layout.

> **NOTE**: You may also want to familiarize yourself with the general approach to writing tests with Selenium
Automation Bundle. Check out the [introduction] or the [advanced guide] to writing tests.

Here's a list of steps when testing the UI with Selenium Automation Bundle:

1. Create a page object and make sure it implements the `UIComparison` trait.
2. Create a test class and make sure it inherits the `UITest`.
3. Create baseline screenshots in baseline mode.
4. Wait until the layout is redesigned or updated.
5. Run the tests with the baseline mode set to `false` (this mode defaults to `false`).
6. [Generate a report] and review the attached screenshots with compared layouts.
7. Report bugs if layouts are broken or changes weren't correct (optional).
8. Replace the baseline screenshots.

### Creating Baseline Screenshots

Run the UI tests with the `baselineMode` set to `true`:

```bash
./gradlew -Dtest.baselineMode=true
```

By default, this mode is set to `false`.

The baseline screenshots will be saved under the directory `test/resources/uicomparison/baseline/`. Consult the
[baseline screenshots] section in the advanced guide to UI testing for more information.

### Page Object Demo for UI Testing

When you create new page objects for UI tests, make sure that your page objects:

* Inherit `AbstractPage`, and
* Implement `UIComparison`;

The following example demonstrates a page object that'll be used for testing the layout of a custom web page. The page
under test is located in the `test/resources/web_page_with_animation/` directory:

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

### UI Test Class Demo

When writing your test classes for testing the UI, we recommend that you do the following:

* Set TestNG `invocationCount` to 5-10 calls to make the tests more stable.
* Call the `compareLayout()` method on the page object with a string description of the test. `compareLayout()`
determines when a screenshot of a layout is taken.

The following UI test class is written for the [`ShopidaiPage` page object](#page-object-demo-for-ui-testing):

```groovy
package com.sysgears.seleniumbundle.tests.ui

import com.sysgears.seleniumbundle.common.UITest
import com.sysgears.seleniumbundle.pagemodel.ShopidaiPage
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

class UITestExample extends UITest {

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

Notice that you can ignore the HTML elements using the `setIgnoredElements()`  method. This method is available through
the `UIComparison` trait and is useful when you're testing a web page with animation.

With `ShopidaiPage` and `UITestExample` in place, run the UI test and generate a report:

```bash
./gradlew clean test allureServe
```

To run only the `UIComparison` test, change the path to the test in `testng.xml`:

```xml
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd" >

<suite name="Suite">
    <test name="UITest">
        <packages>
            <package name="com.sysgears.seleniumbundle.tests.ui.*"/>
        </packages>
    </test>
</suite>
```

Once the report is generated, you'll see that the test with the method `withoutIgnoringElements()` failed, and the
test `withIgnoringElements()` successfully passes.

<p align="center">
    <img src="./img/ui-testing-failed-tests.jpg" />
</p>

## Conclusions

Selenium Automation Bundle provides great possibilities for testing the user interface (application layouts). If you
want to know more about UI testing with our bundle, consult the [advanced guide] to writing and running the UI tests.

[advanced guide]: https://github.com/sysgears/selenium-automation-bundle/docs/advanced/Advanced-Guide-to-UI-Testing
[baseline screenshots]: https://github.com/sysgears/selenium-automation-bundle/docs/advanced/Advanced-Guide-to-UI-Testing#creating-baseline-screenshots
[page object]: https://github.com/sysgears/selenium-automation-bundle/wiki/Guide-to-Writing-Tests#why-use-page-objects
[test classes]: https://github.com/sysgears/selenium-automation-bundle/wiki/Guide-to-Writing-Tests#creating-a-test
[introduction]: https://github.com/sysgears/selenium-automation-bundle/wiki/Intro-to-Writing-Tests
[advanced guide]: https://github.com/sysgears/selenium-automation-bundle/wiki/Guide-to-Writing-Tests
[generate a report]: https://github.com/sysgears/selenium-automation-bundle/wiki/Intro-to-Writing-Tests#step-5-run-the-test
[ashot]: https://github.com/yandex-qatools/ashot