# Data Driven Testing in Action

Selenium Automation Bundle provides a demo example of a test class that takes advantage of the Data Driven Testing
approach we've implemented.

The demo files:

* `src/test/groovy/.../tests/demo/Tools.groovy`, a test class that uses `DataLoader` class.
* `src/test/resources/data/google/test_data.yml`, a Yaml file with the test data.

## Demo Test Class for Data Driven Testing

### General Considerations for Creating Test Classes for DDT

Your test classes that are be used for Data Driven Testing will need to import several classes provided by the bundle:

* `DataLoader`, a class that let's you load test data from the files.
* `Find`, `Locator`, and `Query` annotations to use before the method definitions in test class.

### Demo Test Class for Data Driven Testing

The demo test class `Tools` uses two demo page objects located in the `src/main/groovy/.../seleniumbundle/pagemodel/`
directory. `Tools` is written according to the general recommendations on [creating test classes] for functional
testing.

Here are a few considerations for writing test classes that are used in Data Driven Testing scenarios:

* Import the `DataLoader` class from `src/main/groovy/.../core/data/` directory.
* Import necessary annotations from `src/main/groovy/.../core/data/annotations/` directory.
* Create a property in your class to reference the Yaml file with the test data.
* Use TestNG annotation `@DataProvider`:
    * Set the name for the data provider
    * Create a method that will read data from Yaml file using `DataLoader` methods: `readListFromYml` or
    `readMapFromYml`

```groovy
@DataProvider(name = 'getTestData')
Object[][] getTestData(Method m) {
    mapper.map(DataLoader.readListFromYml(DATAFILE), m, this)
}
```

* Use the created data provider in your test by passing its name in the `@Test` TestNG annotation:

```groovy
@Test(dataProvider = "getTestData", description = "Some lame description")
    void testSomethingWithData(
            @Locator("query") String query,
            @Locator("category") String category,
            @Locator("result.url.params") Map params) {

        googlePage.searchFor(query)

        new ResultsPage()
                .waitForPageToLoadElements()
                .selectCategory(category)
                .validateUrlParams(params)
    }
```

Let's now have a look at an example of data driven test that uses the annotations as described earlier:

```groovy
package com.sysgears.seleniumbundle.tests.demo

import com.sysgears.seleniumbundle.common.FunctionalTest

/**
 * Import annotations and classes from the data module
 */
import com.sysgears.seleniumbundle.core.data.DataLoader
import com.sysgears.seleniumbundle.core.data.annotations.Find
import com.sysgears.seleniumbundle.core.data.annotations.Locator
import com.sysgears.seleniumbundle.core.data.annotations.Query

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

    protected GooglePage googlePage
    private final static String DATAFILE = "src/test/resources/data/google/test_data.yml"

    @BeforeMethod
    void openApplication() {
        googlePage = new GooglePage().open().waitForPageToLoadElements().selectLanguage()
    }

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

    @Test(dataProvider = "getTestData", description = "Checks that specific tools are available for chosen category")
    @Query(@Find(name = "category", value = "News"))
    void checkOptionsForCategories(
            @Locator("query") String query,
            @Locator("category") String category,
            @Locator("result.page_elements.tools") List tools) {

        googlePage
                .searchFor(query)

        new ResultsPage()
                .waitForPageToLoadElements()
                .selectCategory(category)
                .openToolsMenu()
                .areToolsPresent(tools)
    }
}
```

Here's how the flow works in this case:

1. The test class import data from Yaml file.
2. The `@DataProvider` is used to mark a method `getTestData()` that will be retrieving data from the Yaml file.
3. In the `getTestData()` method, the instance of `DataMapper` maps the data from Yaml files.
4. The `DataLoader` class is used to read the data.

[creating test classes]: https://github.com/sysgears/selenium-automation-bundle/blob/docs/docs/advanced/Writing%20Tests.md#general-considerations-before-writing-tests