# Introduction to User Interface (Layout) Testing with Selenium Automation Bundle

Selenium Automation Bundle provides a simple way to test applications' user interface. You only need to take screenshots
of the initial layout that was tested and looks fine. After that, when the UI changes, you need to run the same UI tests
again.

When running your UI tests, the bundle will take new screenshots of applications' layouts, find the differences, create
new images with highlighted differences, and attach them to the report. Finally, your task is to generate a report and
visually validate what has changed, and report bugs if any.

You can also consult the [advanced guide] for more details about testing the user interface. In this short guide, you'll
go through the basic steps of testing the UI.

## Table of Contents

* [Demo Test Classes and Page Objects](#demo-ui-test)
* [Steps for Testing the User Interface](#steps-for-testing-the-user-inteface)
* [Page Object Demo for UI Testing](#page-object-demo-for-ui-testing)
* [Demo UI Test](#demo-ui-test)
* [Creating Baseline Screenshots](#creating-baseline-screenshots)
* [Conclusions](#conclusions)

## Demo UI Test

Selenium Automation Bundle comes with a basic example of a UI test. Here are the two files we're going to look at:

* The `ShopidaiPage` page object, located at `src/main/.../seleniumbundle/pagemodel/` .
* The `UITestExample` test class for `ShopidaiPage`, located at `test/.../tests/ui/`.

> To learn more about page objects and test classes, consult the [page object] and [test classes] sections in our
general guide to testing.

## Steps for Testing the User Interface

Let's review the steps for writing a UI test with Selenium Automation Bundle.

> **NOTE**: Before you write your first UI tests, test the initial (basic) application layout manually to ensure that
there are no issues.

> **NOTE**: You may also want to familiarize yourself with the general approach to writing tests with Selenium
Automation Bundle. Check out the [introduction] or the [advanced guide] to writing tests.

Here's a list of steps when testing the UI with Selenium Automation Bundle:

1. Create a page object and make sure it implements the `UIComparison` trait.
2. Create a test class and make sure it inherits the `UITest`.
3. Create baseline screenshots in baseline mode.
4. Wait until the layout is redesigned or updated.
5. Run the tests with the baseline mode set to `false` (this mode is `false` by default).
6. [Generate a report] and review the attached screenshots with highlighted differences.
7. Report bugs if layouts are broken or changes weren't correct.
8. Replace the baseline screenshots.

### Demo Page Object for UI Testing

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

### Demo UI Test

When writing your test classes for testing the UI, we recommend that you do the following:

* Set invocation count in `@Test` to 5-10 calls to make the tests more stable.
* Call the `compareLayout()` method on the page object instance to determine when a screenshot is taken. Optionally, you
can pass a string description to the method.

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
the `UIComparison` trait and can be useful when you're testing a web page with animations.

With `ShopidaiPage` and `UITestExample` in place, run this UI test. To run only the `UIComparison` test, change the path
to the test in `testng.xml`:

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

Now just run the following command:

```bash
./gradlew clean test allureServe
```

`allureServe` will generate a report, and you'll see that the test with the method `withoutIgnoringElements()` has
failed, and the test `withIgnoringElements()` has successfully passed.

<p align="center">
    <img src="./img/ui-testing-failed-tests.jpg" />
</p>

### Creating Baseline Screenshots

Run the UI tests with the `baselineMode` set to `true`:

```bash
./gradlew -Dtest.baselineMode=true
```

By default, this mode is set to `false`.

The baseline screenshots will be saved under the directory `test/resources/uicomparison/baseline/`. Consult the
[baseline screenshots] section in the advanced guide to UI testing for more information.

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