# Data Driven Testing with Selenium Automation Bundle

Selenium Automation Bundle embraces the [Data Driven Testing] (DDT) pattern to help you manage large sets of data that 
you need to use in your tests. With YAML files and our `data` module, you get a simple way to store and retrieve test 
data in your data-driven tests.

In this guide, you'll know more about our implementation of the Data Driven Testing pattern, and you'll run a demo DDT 
test.

## Why Data Driven Testing Is Important

Think of writing a test that verifies a registration form by feeding 50 or more test users (objects with properties 
`username`, `email`, `password`, and `passwordRepeat`). Using those 50 test user objects, you'll be able to check how 
the application handles registration if various user names, emails, and passwords were entered into the form.

That's what Data Driven Testing is about: DDT means testing your application using huge sets of data to understand how 
the application handles different user inputs and what outputs it gives for those inputs.

DDT is an important part of testing as it helps to determine how stable is an application. But DDT also leads to
headaches when you need to handle the huge sets of data in your tests. And if your objects with data are complex,
meaning they can include other objects, managing them becomes even more difficult.

You may hardcode the test data in your test classes, but whenever you decide to change a property in an object, it'll 
take time to change the same property in others tests. Overall, handling test data scattered across dozens of files 
isn't the best approach to Data Driven Testing.

What if you had all your sets of data stored in YAML files and be easily accessible from your tests? Selenium Automation
Bundle makes that possible and helps you completely separate test data from your tests.

Let's discuss _how_ Selenium Automation Bundle simplifies handling the large data sets.

### Selenium Automation Bundle Mechanism for Data Driven Testing

Our mechanism for writing data-driven tests is built around the TestNG functionality, more specifically, the [Data 
Providers]. We've created several custom classes and annotations that help you build your requests for data from YAML 
files. Here are they:

* The `DataLoader` class to load the data from YAML files;
* The `DataMapper` class to transform loaded data to a `Map` or `List`;
* The `Locator`, `@Find`, and `@Query` annotations to specify data queries.

With our mechanism for DDT, you can:

* Add and remove test data directly without the necessity to call database methods
* Request an entire data set from a YAML file or only the specific test data
* Create data objects with high level of complexity

Speaking of storing data in files, you can consider YAML files as your "database" that's much easier to access and 
handle than a conventional relational (MySQL) or document-oriented (MongoDB) database.

## Running a Data Driven Test

Selenium Automation Bundle provides a demo data-driven test `Tools`. To run this test, first replace the default TestNG 
configuration with the configuration below (look for `testng.xml` file in `src/test/resources`):

```xml
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd" >

<suite name="Suite">
    <test name="Demo Data Driven Test">
        <classes>
            <class name="com.sysgears.seleniumbundle.tests.demo.Tools"/>
        </classes>
    </test>
</suite>
```

Now you can run the test using the following command:

```bash
./gradlew clean test allureServe
```

**NOTE**: If you see the error `Cannot find allure commandline`, you need to download Allure and generate the report
with the command `./gradlew downloadAllure allureServe`. Learn more about report generation in [Reporting].

Once the test is completed, your default browser will open the report:

<p align="center">
  <img src="./images/selenium-automation-bundle-data-driven-test-example.png" alt="Selenium Automation Bundle - Data Driven Test" />
</p>

The demo example that you've just run includes the following files:

* `src/test/groovy/.../tests/demo/Tools.groovy`, the test class
* `src/test/resources/data/google/test_data.yml`, the YAML file with the test data

In the following section, we'll briefly review how the demo data-driven test works. If you need a detailed explanation,
you can follow to [Data Driven Testing in Action].

## Demo Test Class for Data Driven Testing

The data-driven test `Tools`, shown below, demonstrates the use of DDT in Selenium Automation Bundle:

```groovy
package com.sysgears.seleniumbundle.tests.demo

import com.sysgears.seleniumbundle.common.FunctionalTest

/**
 * Import annotations and classes from the data module
 */
import com.sysgears.seleniumbundle.core.data.DataLoader
import com.sysgears.seleniumbundle.core.data.annotations.Locator

/**
 * Import page objects for testing
 */
import com.sysgears.seleniumbundle.pagemodel.GooglePage
import com.sysgears.seleniumbundle.pagemodel.ResultsPage

import org.testng.annotations.BeforeMethod
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

import java.lang.reflect.Method

class Tools extends FunctionalTest {
    /**
     * Create the page object and data file properties.
     */
    protected GooglePage googlePage
    private final static String DATAFILE = "src/test/resources/data/google/test_data.yml"

    @BeforeMethod
    void openApplication() {
        googlePage = new GooglePage().open().waitForPageToLoadElements().selectLanguage()
    }
    
    /**
     * Create the method that will get the data from YAML file and map it to your test.     *  
     */
    @DataProvider(name = 'getTestData')
    Object[][] getTestData(Method m) {
        mapper.map(DataLoader.readListFromYml(DATAFILE), m, this)
    }

    @Test(dataProvider = "getTestData",
            description = "Checks that url parameters specified in test data are changed based on chosen category")
    void checkUrlParameterChanges(
            @Locator("query") String query,
            @Locator("category") String category,
            @Locator("result.url.params") Map params) {

        googlePage
                .searchFor(query)

        new ResultsPage()
                .waitForPageToLoadElements()
                .selectCategory(category)
                .validateUrlParams(params)
    }
    // other code is omitted
}
```

Let's review how the class works:

1. The class instantiates the `test_data.yml` file with data to be used in test.
2. The class creates `getTestData()`, which is a TestNG Data Provider. `getTestData()` uses `DataLoader` to first read 
test data from a file, and then map data using `mapper`, an instance of `DataMapper`. `mapper` is available through 
`FunctionalTest`.
3. The test method `checkUrlParameterChanges()` is annotated with `@Test`, which sets `dataProvider` to `getTestData()`.
4. `checkUrlParameterChanges()` accepts three `@Locator` annotations with the string queries for necessary data.
5. The Data Provider `getTestData()` will inject the data from YAML files into the test method according to requests 
passed to `@Locator`.

In order to complete the overall picture, it's worth looking at the demo YAML file `src/test/resources/data/google/test_data.yml`. 
Here's its excerpt:

```yml
- query: google
  category: Images
  result:
    url:
      params:
        tbm: isch
        q: google
```

With all that information, the test will work like this:

1. A `GooglePage` instance is created, and the method `searchFor()` is with the query `query` that was passed to the 
first `@Locator` annotation in `checkUrlParameterChanges()` method.
2. The `query` value is `google` and is retrieved by the Data Provider `getTestData()` from `test_data.yml`. (Yes, our 
demo test searches for `google` using Google).
3. Once the request `google` is sent, the browser shows the search results page. Therefore, the test instantiates the 
`ResultsPage` class.
4. The `resultsPage` instance waits until the page is fully loaded, and then calls the method `selectCategory()` with 
the `category` value as requested in `@Locator` and retrieved by `getTestData()` from `test_data.yml`. The value for
`category` is `Images`. In other words, once the results page for the request `google` is loaded, Selenide clicks on 
`Images` to open the images results.
5. Finally, the `validateUrlParams()` is called with the `params` from `test_data.yml`. 

The URL for the page looks like this:

```
https://www.google.com/search?q=google&source=lnms&tbm=isch&sa=X&ved=0ahUKEwiekPniiP_dAhXBlYsKHau7CeIQ_AUIDigB&biw=1174&bih=588
```

You can see the parameters in the URL such as `q` and `tbm`. The `validateUrlParams()` simply compares the parameters' 
values taken from the URL to the values stored in `test_data.yml` file. If you get back to `test_data.yml`, you'll 
see the values `google` and `isch` for the properties `result.url.params.q` and `result.url.params.tbm` respectively. 

Because the values for the parameters `q` and `tbm` are identical in the URL and `test_data.yml`, this data driven test 
will pass successfully.

___

In a real application, when your application uses complex objects, it's easy to mimic the object structure in a YAML 
file and retrieve various object properties in your tests.

We've only scratched the surface of the Data Driven Testing approach in Selenium Automation Bundle. If you want to know
more, follow to [Data Driven Testing in Action].

[data driven testing]: https://en.wikipedia.org/wiki/Data-driven_testing
[data providers]: http://testng.org/doc/documentation-main.html#parameters-dataproviders
[reporting]: https://github.com/sysgears/selenium-automation-bundle/blob/docs/docs/fundamentals/Reporting.md
[data driven testing in action]: https://github.com/sysgears/selenium-automation-bundle/blob/docs/docs/fundamentals/Data%20Driven%20Testing/Data%20Driven%20Testing%20in%20Action.md
[creating test classes]: https://github.com/sysgears/selenium-automation-bundle/blob/docs/docs/advanced/Writing%20Tests.md#general-considerations-before-writing-tests