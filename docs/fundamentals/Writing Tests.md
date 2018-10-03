# A Guide to Writing Tests with Selenium Automation Bundle

This guide explores how to write complex tests with Selenium Automation Bundle. If you have already read through the
[simple guide], but you still have questions how to work with the bundle, this document will further clarify the
workflow for you.

Let's briefly review how you can write tests with the bundle. You need to:

1. Create a new [page object](#creating-a-page-object), which will serve as an intermediary between your tests and a web
page.
2. Create a new [test class](#creating-a-test) with test methods for a web page. The created page object will be used in
the test class to test functionality of the page.
3. [Update TestNG configurations](#updating-testng-configurations) to specify the test classes or suites to execute.
4. [Run your test(s)](#running-the-tests).
5. [Generate an Allure report](#generating-a-detailed-test-report-with-allure) and inspect the test results in the
browser.

We'll start with the very first step &ndash; creating a page object.

## Creating a Page Object

The two sections that follow are more theoretical rather than practical. You can jump to the [Google Page Object](#google-page-object)
section, and then come back if you need more clarification on the concepts.

### Why Use Page Objects

When you start working with Selenium Automation Bundle, we recommend creating page objects for all HTML pages under
test. The bundle embraces the page object pattern and encourages you to stick with it when you write automated tests.

But why do we use page objects?

You may have seen many examples of simple automated tests that help you get started with testing your app. The demo
tests, however, are usually very simple, and they shy away from the problem that many QA specialists have to deal with
when testing a _real_ web application. That problem is code duplication.

Think of this common issue: You need to test an HTML form with many input fields, and this form is reused across several
web pages. In a series of tests, you enter different string data into the form and submit it. Eventually, you have to
repeat the same actions in different tests:

* Find the same input elements;
* Enter string data into them; and
* Submit the form.

But how can you avoid repeating our code? As a matter of fact, you can use page objects.

[Page objects] encapsulate the code that handles HTML elements. All you need is implement necessary methods in a page
object class to work with an element on an HTML page, and then you can **reuse** these methods in your tests. You can
also think of a page object as an interface between the HTML page and your test class.

### General Considerations Before Writing Page Objects

Here’s how you can create your own page objects:

1. Create separate page objects for different web pages and components on a page.
2. Make sure that you don't adapt your page objects to the needs of a specific test but rather isolate your page objects
from test classes. This approach will let you change the tests without changing the page object in the future.
3. Put new page objects into the `src/main/.../seleniumbundle/pagemodel/` directory.
4. Make sure that your page objects inherit `AbstractPage`. Also set the type: `AbstractPage<MyPageObject>`. Note that
there's a distinction between the "main" page object, which handles a concrete HTML page, and page objects that work
only _with certain components_ on a web page. In most cases, only the "main" page object should inherit `AbstractPage`.
5. Initialize necessary class fields with type `SelenideElement` or `ElementsCollection` and map these fields to the
HTML elements (or a collection of elements) of the web page under test. Use the `@StaticElement` annotation before the
element definition to indicate that the element must be presented on the web page after the page was loaded.
6. Initialize a relative URL for the HTML page in the constructor so that the browser will open a correct page for
testing. (The base URL must also be defined in `ApplicationProperties.groovy` in the `src/main/resources/config/`
directory.)
7. Implement the methods, which read or modify web page elements.

If you follow the recommendations above, it will be easier to write tests and rework your page objects in the future,
when your tests will become more complex.

Now we can focus on creating a concrete page object.

### Google Page Object

Have a look at the `GooglePage` class below, which is a simple example of a page object. `GooglePage` provides the code
to work with a typical HTML page with the Google search field.

```groovy
package com.sysgears.seleniumbundle.pagemodel

import com.codeborne.selenide.SelenideElement
import com.sysgears.seleniumbundle.core.pagemodel.AbstractPage
import com.sysgears.seleniumbundle.core.pagemodel.annotations.StaticElement
import com.sysgears.seleniumbundle.core.uicomparison.UIComparison
import io.qameta.allure.Step
import org.openqa.selenium.By

import static com.codeborne.selenide.Selenide.$

/**
 * A page object class that works with the Google search page. It implements
 * the methods for handling page state. Inherits the base AbstractPage class,
 * which provides default methods to work with any web pages.
 */
class GooglePage extends AbstractPage<GooglePage> {

    /**
     * The queryField property that references the Google search field.
     *
     * The @StaticElement annotation indicates that the element must be present
     * after the page was loaded.
     */
    @StaticElement
    private SelenideElement queryField = $(By.name("q"))

    /**
     * Sets the relative URL of the tested HTML page.
     */
    GooglePage() {
        this.url = "/"
    }

    /**
     * Changes the language of the Google search page. Sets the language to English
     * by default.
     *
     * The @Step annotation helps to provide a method description for Allure reports.
     *
     * @param Google search page language
     *
     * @return GooglePage instance
     */
    @Step("Select language")
    GooglePage selectLanguage(String language = "English") {
        def element = $(By.linkText(language))

        if (element.exists()) {
            element.click()
        }
        this
    }

    /**
     * Enters a given search query into the search input on the Google page and
     * submits the request.
     *
     * @param query search request
     */
    @Step("Perform search")
    void searchFor(String query) {
        enterQuery(query)
        submit()
    }

    /**
     * Sets the value of the queryField to a given string value.
     *
     * @param query search query
     *
     * @return GooglePage instance
     */
    @Step("Enter query")
    private GooglePage enterQuery(String query) {
        queryField.val(query)
        this
    }

    /**
     * Submits the value entered into the queryField to Google search.
     */
    @Step("Submit search")
    private ResultsPage submit() {
        queryField.pressEnter()
    }
}
```

As we can see, `GooglePage` is a typical Groovy class. (Although `GooglePage` is written in Groovy, you can also write
your page objects in Java when working with Selenium Automation Bundle.)

Here's how `GooglePage` works:

1. It inherits `AbstractPage` and sets itself as the type: `AbstractPage<GooglePage>`.
2. It initializes a new field `queryField` with the type `SelenideElement`. `queryField` references the Google search
field.
3. It sets the relative URL to `/`. The current base URL is `https://google.co.uk`, as defined in application
properties.
4. It provides methods `enterQuery()` and `submit()` to work with the search field.

It's also worth mentioning that `GooglePage` also uses `@Step` annotations before method definitions. `@Step` is
provided by Allure and it helps to describe page object methods for Allure reports. But using this annotation in your
page objects isn't obligatory.

We should now turn our attention to the `AbstractPage` class, which is inherited by `GooglePage`.

### Introduction to AbstractPage

The `AbstractPage` class is one of the core classes available in the bundle. `AbstractPage` comes with several
general-purpose methods to be used by your page objects.

More specifically, `AbstractPage`:

* Holds the URL field for a page object.
* Implements the `open()` method to open an HTML page with a given URL in the browser.
* Implements the `waitForPageToLoadElements()` method to verify that all elements are loaded on the page. (To make sure
that this method works, you should use the `@StaticElement` annotation before the definition of an element in your
concrete page object.)

You can have a look at `AbstractPage` in the `src/main/.../core/pagemodel/` directory.

We'll clarify how to use `AbstractPage` methods in the following section, where we explain how to write a test for
`GooglePage` and, essentially, for any page object.

## Creating a Test

### General Considerations Before Writing Tests

Selenium Automation Bundle encourages you to stick to the following approach when writing tests:

* Save page-specific tests to a separate directory under `src/test/.../tests/`.
* Make sure that your functional and UI test classes inherit `FunctionalTest` and `UITest` respectively. The
`FunctionalTest` and `UITest` classes are provided by the bundle and are necessary to handle basic configurations for
your tests.
* Use TestNG annotations before method definitions.
    * `@Test` to indicate that the method that follows is a test method;
    * `@BeforeMethod` to run a particular method before calling each test method; and so on.
* Add a class field for the page object that will be tested in the class.
* Create new page objects in test class methods if necessary.

Let's clarify the last point.

When you're writing code for testing a page object, you often need to instantiate a different page object. There are
basically two ways to do that. We prefer to explicitly initialize a new page object in
[test methods](#example-of-a-functional-test), as you'll see in the next section where we review a functional test.

You don't have to follow our approach, though. You can instantiate your page objects directly in methods of other page
objects, like this:

```groovy
class GooglePage extends AbstractPage<GooglePage> {
    // other methods and properties are omitted

    @Step("Submit search")
    private ResultsPage submit() {
        queryField.pressEnter()
        new ResultsPage()
    }
}
```

This way, you'll be able to call several methods in a test class one by one without breaking the chain:

```groovy
// code from a test method
googlePage
    .searchFor(str) // which internally calls submit() and returns an instance of ResultsPage
    .waitForPageToLoadElements() // called on resultsPage
    .isResultSize(num) // called on resultsPage
```

However, with this approach it may be unclear that some methods are actually called on a different page object. In the
previous code snippet, such methods are `waitForPagetoLoadElements()` and `isResultSize()`: They're called on the
instance of `ResultsPage`. Ultimately, it's up to you to choose the best approach to writing your tests.

Now we can review a concrete example of a functional test that we wrote for `GooglePage`.

### Example of a Functional Test

Let's review how a test is written according to the recommendations above. A basic `Search` test class might look as
follows:

```groovy
package com.sysgears.seleniumbundle.tests.demo

import com.sysgears.seleniumbundle.common.FunctionalTest
import com.sysgears.seleniumbundle.pagemodel.GooglePage
import com.sysgears.seleniumbundle.pagemodel.ResultsPage
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

/**
 * A test class that provides methods to test Google search using the
 * GooglePage instance.
 *
 * Extends the FunctionalTest class that is provided by the bundle.
 */
class Search extends FunctionalTest {

    /**
     * The googlePage property that will reference the instance of GooglePage.
     */
    protected GooglePage googlePage

    /**
     * Initializes an instance of GooglePage. Opens the URL of the web page in
     * the browser. Checks if the Google search element was loaded.
     * Selects a language for search (defaults to English).
     *
     * The @BeforeMethod annotation is provided by TestNG and is necessary
     * to run the method before each test method.
     */
    @BeforeMethod
    void openApplication() {
        googlePage = new GooglePage().open().waitForPageToLoadElements().selectLanguage()
    }

    /**
     * Tests the Google page. Submits a request to Google search.
     *
     * Explicitly instantiates and sets up a new page object to work with
     * the search results page using an instance of ResultsPage.
     */
    @Test(description = "Checks number of results on the first page")
    void checkSearch() {
        googlePage
                .searchFor("SysGears")

        new ResultsPage()
                .waitForPageToLoadElements()
                .isResultSize(10)
    }
}
```

### How openApplication() works

The `openApplication()` method:

* Initializes a new instance of `GooglePage`.
* Runs `open()` on the `GooglePage` instance to open the page in the browser.
* Runs `waitForPageToLoadElements()` to wait until the elements are loaded before running any test methods. This method
works together with the `@StaticElement` annotation [used in a page object](#google-page-object) to indicate that the
HTML element must be loaded before the tests run.
* Runs `selectLanguage()` to change the search language to English.

The `open()` and `waitForPageToLoadElements()` methods are provided by the `AbstractPage` class, and they're typical
methods that help you "prepare" any page object instance for testing.

### How checkSearch() works

The `checkSearch()` method:

* Submits a request "SysGears" using `searchFor()` to Google search.
* Creates a new instance of `ResultsPage` and verifies if the returned HTML page has the given number of search results.
* Returns the type `void` explicitly. Although Groovy allows you to omit the returned type, you still have to explicitly
set the type to ensure that TestNG will run this test.

Although what `checkSearch()` does is fairly obvious, we should explain what exactly happens in this method. In
particular, why was a new page object instantiated?

The answer is quite simple. After you submit a request to Google search, the browser will go to a new web page with the
Google search results. As we discussed in the section [general considerations before writing page objects](#general-considerations-before-writing-page-objects), you should create different page objects for different pages. Therefore, the other page object &ndash; `ResultsPage` &ndash; must instantiate necessary elements and provide its own methods to work with the result page.

Note that you may need to perform various actions with a newly created page object and then call the assertion methods.
For example, you can call `waitForPageToLoadElements()` to make sure that all elements are loaded before calling the
assertion method.

```groovy
@Test(description = "Checks number of results on the first page")
    void checkSearch() {
        googlePage
                .searchFor("SysGears")

        new ResultsPage()
                .waitForPageToLoadElements()
                .isResultSize(10)
}
```

Check out the `ResultsPage.groovy` file in the `src/main/.../seleniumbundle/pagemodel/` directory for full
implementation of `ResultsPage`.

### Conclusions

* Your test class should call page object methods to instantiate and handle page elements.
* In your test methods, you can instantiate new page objects to handle new HTML pages.
* Both your test class and page object can implement assertions in their methods.
* Page objects must be concerned with different web pages or different HTML elements on a single web page.

You're _almost_ ready to run the test. But first you need to update TestNG configurations to make sure that the `Search`
class will run.

## Updating TestNG Configurations

To run a particular test class, you need to update the TestNG configurations.

Selenium Automation Bundle has a default TestNG configuration file &ndash; `testng.xml`, which is located in the
`src/test/resources/` directory. You can change this file each time you want to run different test suites or classes.
But there’s a more convenient way to use different TestNG configurations. You can add your own TestNG configuration
files and then switch between them via the command line before running the tests.

We'll show how to select various TestNG configurations in the section [Running the Tests](#running-the-tests).
For now, let's create a new XML file in `src/test/resources` and call it `testng_search_config.xml`. After that, copy
this code into the file:

```xml
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd" >
<suite name="Demo">
  <test name="Search">
    <classes>
      <class name="com.sysgears.seleniumbundle.tests.demo.Search"/>
    </classes>
  </test>
</suite>
```

As defined in the configuration above, you can use the `classes` and `class` tags to specify the test class you want to
run.

Alternatively, you can use the `packages` and `package` tags to specify the directories with tests. For example, you can
set the package's `name` attribute to "com.sysgears.seleniumbundle.tests.*", as shown in the example:

```xml
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd" >
<suite name="Suite">
  <test name="Tests">
    <packages>
      <package name="com.sysgears.seleniumbundle.tests.*"/>
    </packages>
  </test>
</suite>
```

With this configuration, TestNG will run _all_ the tests (note the asterisk) located under the `src/test/.../tests/`
directory.

That's how you can configure TestNG. Finally, you can run the tests.

## Running the Tests

It's time to run the test using the Gradle Wrapper command:

```bash
./gradlew -PtestngConfig=testng_search_config
```

`./gradlew` will run two default Gradle tasks - "clean" and "test". The Google Chrome browser will open and you'll see
how tests are running.

The second part of the command shows how you can set a different configuration file for a test run. Provided that you
created [the `testng_search_config.xml` file](#updating-testng-configurations), the part
`-PtestngConfig=testng_search_config` will tell TestNG to use this file for a test run.

You just need to set the `testngConfig` property (defined in the `gradle.properties`) to the name of the configuration
file without the extension - `testng_search_config` in our example. `-P` before `testngConfig` just means "a project
property".

You can also run `./gradlew` without passing the TestNG configurations. In this case, the default `testng.xml` will be
used.

Now it's time to view the test results.

## Generating a Detailed Test Report with Allure

Selenium Automation Bundle comes with a report tool called Allure to help you generate detailed, user-friendly HTML
reports with test results conveniently arranged for easy navigation.

You need to run two commands to create a report. First, download Allure:

```bash
./gradlew downloadAllure
```

> You need to run `./gradlew downloadAllure` only once &ndash; before you run the tests for the first time. Afterwards,
when you need to generate a test report, you don't have to download Allure again.

Next, run the following command to generate and serve a report with Allure:

```bash
./gradlew allureServe
```

Your default browser will open the report generated by Allure at http://localhost:55555/index.html. (The port may be
different from 55555; it's randomly set by Allure.)

Note that the `allureServe` command **doesn't re-run** the tests. It will only generate a test report and serve it to
the browser. And if you need to inspect the results of new or updated tests, you should run:

```bash
./gradlew clean test allureServe
```

This command will clean up the build folder, run the tests, generate a _new_ report, and open it in your default
browser.

[simple guide]: https://github.com/sysgears/selenium-automation-bundle/docs/Introduction%20%to%20%Writing%20%Tests.md
[page objects]: https://martinfowler.com/bliki/PageObject.html