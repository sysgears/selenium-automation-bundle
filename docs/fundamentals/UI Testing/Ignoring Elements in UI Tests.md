# Ignoring Elements in UI Testing

In this section, we'll review the demo example of a UI test with ignoring HTML elements on a page.

Ignoring page elements can be useful for UI testing especially when the page has an animation or any dynamic changes
because of the user interaction with the page.

Because any dynamic changes on a web page can make UI tests brittle, the UI Comparison module in our bundle comes with a
solution to handle dynamics in a simple way &ndash; you can use the `setIgnoredElements()` method.

The method `setIgnoredElements()` is implemented in the `UIComparison` trait (more information in the [UI Comparison]
section), and you'll need to call this method on your page object in the tests and pass the elements you want to ignore.

## Page Object Demo

The page object `ShopidaiPage` below will be used for testing the layout of the custom web page that you can find in
`test/resources/web_page_with_animation/`. As you can see, `ShopidaiPage` implements the `UIComparison` trait, which
contains the implementation of `setIgnoredElements()`.

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
According to the [general recommendations on creating UI test classes], `UITestIgnoringElementsExample` extends the
`UITest`.

```groovy
package com.sysgears.seleniumbundle.tests.ui

import com.sysgears.seleniumbundle.common.UITest
import com.sysgears.seleniumbundle.pagemodel.ShopidaiPage
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

class UITestIgnoringElementsExample extends UITest {

    private ShopidaiPage shopidaiPage

    @BeforeMethod
    void openApplication() {
        shopidaiPage = new ShopidaiPage().setEnvironment(this).open().waitForPageToLoadElements()
    }

    /**
     * Shows that the test can fail because of dynamic changes on the page.
     */
    @Test(invocationCount = 2)
    void withoutIgnoringElements() {
        shopidaiPage.compareLayout("withoutIgnoring")
    }

    /**
     * Shows that an element (such as an animation) can be ignored if they may affect test result
     * because of their dynamic nature.
     */
    @Test(invocationCount = 2)
    void withIgnoringElements() {
        shopidaiPage.setIgnoredElements([".starwars-animation"]).compareLayout("withIgnoring")
    }
}
```

Notice how in the test method `withIgnoringElements()` we call `setIgnoredElements()` on the `ShopidaiPage` instance.
Because the web page under test has an animation &ndash; the large logo of Star Wars &ndash; we need to ignore this
animation to ensure that the UI test don't fail because of it. The Star Wars animation is located using the
`.starwars-animation` CSS selector, which we passed to `setIgnoredElements()`.

**NOTE**: You should always call `setIgnoredElements()` _before_ calling the test method `compareLayout()` in the chain.

Since `ShopidaiPage` has an animation, when you run this test, the method `withoutIgnoringElements()` will fail, and the
method `withIgnoringElements()` will be successfully executed.

With `ShopidaiPage` and `IgnoreElementsExample` in place, you're ready to run the UI test to see how it works with
Selenium Automation Bundle.

## Running UI Tests with Ignoring Elements

You may want to configure TestNG to run only the `UITestIgnoringElementsExample` test class. To do that, replace the
default configuration in `src/test/resources/testng.xml` or your TestNG configuration file with the configuration below:

```xml
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd" >

<suite name="Suite">
    <test name="Demo UI Test with Ignoring Elements">
        <classes>
            <class name="com.sysgears.seleniumbundle.tests.ui.UITestIgnoringElementsExample"/>
        </classes>
    </test>
</suite>
```

You may also need to create the [baseline screenshots]. Run the following command:

```bash
./gradlew -Dtest.baselineMode=true
```

Gradle will run the test `UITestIgnoringElementsExample` and generate the baseline screenshots. You can view the created
baseline screenshots in the `src/test/resources/uicomparison/baseline/` directory. No comparison was made at this point.

To execute `UITestIgnoringElementsExample` **with** screenshots comparison, just run `./gradlew` to execute the tests
again.

```bash
./gradlew
```

Next, once the execution is completed, you'll see that in fact two tests has failed out of four (two test methods run
twice during execution, hence you'll see that four tests were executed):

```bash
Starting a Gradle Daemon (subsequent builds will be faster)

> Task :test

Suite > Demo > com.sysgears.seleniumbundle.tests.ui.UITestIgnoringElementsExample.withoutIgnoringElements FAILED
    java.lang.AssertionError at UITestIgnoringElementsExample.groovy:22

Suite > Demo > com.sysgears.seleniumbundle.tests.ui.UITestIgnoringElementsExample.withoutIgnoringElements FAILED
    java.lang.AssertionError at UITestIgnoringElementsExample.groovy:22

4 tests completed, 2 failed


FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':test'.
> There were failing tests. See the report at: file:///home/sviatoslav/projects/selenium-automation-bundle/build/reports/tests/test/index.html

* Try:
Run with --stacktrace option to get the stack trace. Run with --info or --debug option to get more log output. Run with --scan to get full insights.

* Get more help at https://help.gradle.org

Deprecated Gradle features were used in this build, making it incompatible with Gradle 5.0.
See https://docs.gradle.org/4.6/userguide/command_line_interface.html#sec:command_line_warnings

BUILD FAILED in 44s
6 actionable tasks: 6 executed
```

You can now generate the Allure report to view the tests and the screenshots for the failed UI test where the Star Wars
animation wasn't ignored:

```bash
./gradlew allureServe
```

**NOTE**: If you see the error `Cannot find allure commandline`, you need to download Allure and generate the report
with the command `./gradlew downloadAllure allureServe`. Learn more about report generation in the [Reports] guide.

As you can see in the screenshot below, the test that didn't ignore the StarWars animation has failed (the StarWars logo
is yellow, but the bundle marked the changed parts in red):

![Selenium Automation Bundle failed UI test without ignoring the StarWars animation](https://user-images.githubusercontent.com/21691607/54425123-388e8a00-471d-11e9-9d41-5c293b21dd7e.png)

[ui comparison]: https://github.com/sysgears/selenium-automation-bundle/blob/master/docs/UI_Testing/UI_Comparison_Module.mdn
[general recommendations on creating ui test classes]: https://github.com/sysgears/selenium-automation-bundle/blob/master/docs/advanced/UI_Testing/General-Concept-and-Testing-Flow.md#General-Considerations-for-Writing-Test-Classes-for-UI-Tests.m
[baseline screenshots]: https://github.com/sysgears/selenium-automation-bundle/blob/docs/docs/fundamentals/UI%20Testing/Baseline%20Mode.md
[reports]: https://github.com/sysgears/selenium-automation-bundle/blob/docs/docs/fundamentals/Reporting.md