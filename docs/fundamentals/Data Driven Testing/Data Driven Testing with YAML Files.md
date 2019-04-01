# Data Driven Testing with YAML Files

In this guide, we give more details about how data-driven testing is done with Selenium Automation Bundle.

Selenium Automation Bundle has a demo test that follows the DDT approach:

* `src/test/groovy/.../tests/demo/Tools__HierarchicalData.groovy`, the demo test class
* `src/test/resources/data/google/test_data.yml`, the YAML file with the test data

With our mechanism for DDT and YAML files:

* You can localize data per test file
* You can store data in a hierarchical structure
* You don't need to connect to a database

### Storing and Retrieving Data from YAML Files with Tree-Like Structure

You can store test data in YAML with Selenium Automation Bundle. The format of the files is identical to the standard YAML, so you may need to refresh your memory on the [YAML syntax].

Here's an example of a YAML file `src/test/resources/data/google/test_data.yml` that we use for the demo data-driven test `src/test/groovy/.../tests/demo/Tools_HierarchicalData.groovy`:

```yaml
- query: google
  category: Images
  result:
    url:
      params:
        tbm: isch
        q: google
    page_elements:
      tools:
        - Size
        - Color
        - Usage rights
        - Type
        - Time
        - More tools

- query: bing
  category: News
  result:
    url:
      params:
        tbm: nws
        q: bing
    page_elements:
      tools:
        - All news
        - Recent
        - Sorted by relevance
```

The data in this file is the same as in `src/test/resources/data/google/test_data.csv`. The main advantages of storing test data in YAML files are as follows:

* It's easy to avoid data duplication for different tests
* The data is structured as an object in tests
* The order of arguments in test methods doesn't matter thanks to custom annotations

The last aspect proves being beneficial compared to the approach to storing data in CSV files. For more information, you can a dedicated guide [Data Driven Testing with CSV Files].

You can learn more about structuring test data in YAML in the following guide:

* [Creating Test Data in a Tree-Like Structure]

## General Considerations for Creating Test Classes for Data Driven Testing

Here are a few considerations for writing tests that are used in Data Driven Testing scenarios. Your test class must:

* Import `DataLoader` from `src/main/groovy/.../core/data/DataLoader.groovy`
* Import `HierarchicalData` from `src/main/groovy/.../core/data/HierarchicalData.groovy`
* Import necessary annotations from `src/main/groovy/.../core/data/annotations/`
* Extend `FunctionalTest` to get basic functionality for functional tests
* Have a property in the class to reference the YAML file with test data
* Use the `@DataProvider` annotation to create a data provider method to retrieve test data
    
In the next section, we give an example of a data-driven test that follows the recommendations above.

## The demo test class `Tools_HierarchicalData` for Data Driven Testing

Have a look at the data-driven test `src/test/groovy/.../tests/demo/Tools_HierarchicalData`:

```groovy
package com.sysgears.seleniumbundle.tests.demo

/**
 * Import various Selenium Automation Bundle classes.
 * FunctionalTest is the base test class to be inherited 
 * by all test classes for functional testing.
 */
import com.sysgears.seleniumbundle.common.FunctionalTest

/**
 * Import DataLoader to load raw data from YAML files.
 */
import com.sysgears.seleniumbundle.core.data.DataLoader

/**
 * Import HierarchicalData to transform raw data retrieved 
 * by DataLoader from a YAML file into a Groovy object.
 */
import com.sysgears.seleniumbundle.core.data.HierarchicalData

/**
 * Import the custom Selenium Automation Bundle annotations
 * created specifically for data-driven testing.
 */
import com.sysgears.seleniumbundle.core.data.annotations.Find
import com.sysgears.seleniumbundle.core.data.annotations.Locator
import com.sysgears.seleniumbundle.core.data.annotations.Query

/**
 * Import the page model classes to handle the Google search page
 * and the Google search results page.
 */
import com.sysgears.seleniumbundle.pagemodel.GooglePage
import com.sysgears.seleniumbundle.pagemodel.ResultsPage

/**
 * Import TestNG facilities for testing.
 */
import org.testng.annotations.BeforeMethod
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

import java.lang.reflect.Method

class Tools_HierarchicalData extends FunctionalTest {

  // Create a property to reference an instance of GooglePage
  protected GooglePage googlePage
  // Create a property to reference the data file for this test
  private data = new HierarchicalData(DataLoader.readListFromYml("src/test/resources/data/google/test_data.yml"),
          this.class).data
          
  // Initialize and prepare the Google page instance for testing
  @BeforeMethod
  void openApplication() {
    googlePage = new GooglePage().open().waitForPageToLoadElements().selectLanguage()
  }
  
  /**
   * Create a data provider method. The method must return the data.
   * The `data` property references a Groovy map [:], in which the 
   * locator annotations are keys and the values are their data.
   */
  @DataProvider(name = 'getTestData')
  Object[][] getTestData(Method method) {
    data[method.name]
  }
  
  /**
   * A test method to verify the parameters in the URI.
   * This method's annotations are used to look for respective data in the `data[:]` property.
   */
  @Test(dataProvider = "getTestData",
        description = "Checks that the URL parameters specified in test data are changed based on chosen category")
  // Use locators to find the data you need. The order of arguments doesn't matter.
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

  @Test(dataProvider = "getTestData", description = "Checks that specific tools are available for a chosen category")
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

Let's explain the key elements in this class that are related to data-driven testing.

### Creating Page Object and Data File Properties

The first two lines from `Test_HierarchicalData` are easy to understand. You need a property to reference the page object for the page under test &mdash; `googlePage`. Also, you need the property to reference the CSV file with data &mdash; `data`.

```groovy
private GooglePage googlePage
private data = new HierarchicalData(DataLoader.readListFromYml("src/test/resources/data/google/test_data.yml"),
            this.class).data
```

To read data from `test_data.yml`, `DataLoader` is used and returns raw data. The `HierarchicalData` class has the method `map` that transforms the raw data from YAML into an object. `map` reads the annotations from the parameters passed to the test class and them maps them to the data in YAML.

### Creating a Data Provider

A TestNG data provider method must be annotated with `@DataProvider` and return a two-dimensional array of objects &mdash; `Object[][]`. Here's how the `Tools_HierarchicalData` class creates a `getTestData()` data provider:

```groovy
@DataProvider(name = 'getTestData')
Object[][] getTestData(Method method) {
    data[method.name]
}
```

Notice that:

* `@DataProvider` has the `name` parameter to reference the created Data Provider from inside the test methods
* The method accepts the current test method and finds the test data for this method from `data` by its name

### Using Data Provider in Tests

`Tools__HierarchicalData` has two data-driven tests. We have a look at `checkUrlParameterChanges()`. 

`checkUrlParameterChanges()` demonstrates the use of custom annotations and the data provider method:

```groovy
@Test(dataProvider = "getTestData",
            description = "Checks that the URL parameters specified in test data are changed based on chosen category")
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
```

Here's how it works.

1. The `@Test` annotation sets `dataProvider` to `getTestData()`.
2. `getTestData()` uses the parameter annotations to get test data from `data`.
3. `googlePage` runs `searchFor()` to search the queries `google` and `bing` as defined by `test_data.yml`.

Once all of that is done, the `resultsPage` instance runs the actual test.

1. It waits until the page is fully loaded by invoking `waitForPageToLoadElements()`.
2. `selectCategory()` clicks on the category `Images` for `google` and `News` for `bing`. These values are retrieved by the locator `@Locator("category")`.
3. The `validateUrlParams()` is called with the `params` value retrieved by `@Locator("result.url.params")`.

To explain better the last step, we should take a look at the URL for the results page:

```
google.com/search?**_q=google_**&source=lnms&**_tbm=isch_**&sa=X&ved=0ahUKEwiekPniiP_dAhXBlYsKHau7CeIQ_AUIDigB&biw=1174&bih=588
```

You can see the parameters in the URL such as `q` and `tbm`. The `validateUrlParams()` simply compares the values taken from the URL to the values stored in `test_data.yml` file. If you open `test_data.yml`, you'll see the properties `result.url.params.q` and `result.url.params.tbm` store the values `google` and `isch` respectively for the `google` search request:

```yaml
- query: google
  category: Images
  result:
    url:
      params:
        tbm: isch
        q: google
```

Because the values for the parameters `q` and `tbm` are identical in the URL and `test_data.yml`, this data driven test
will pass successfully. (In fact, the test might still fail if Google and Bing change the UI for their search page results.)

### Locators

The `@Locator()` annotations passed to the method `checkUrlParametersChanges()` contain string queries to get specific data from the YAML file. In other words, given the following signature `@Locator("query") String query`, the YAML file must have a list that starts with the string `query`, which points to the actual data you'll need in your test. The data will be mapped to the parameter `String query` in `checkUrlParameterChanges()`.

___

As you can see, using a simple and clean syntax, you can create complex objects in YAML and avoid hardcoding these 
objects in your data-driven tests. What's great is that getting the values is still very simple: with requests similar 
to `result.page_elements.tools` you can get access to any value however deep the value is nested in "objects" in your YAML files.

[yaml syntax]: https://yaml.org/
[data driven testing with csv files]: https://github.com/sysgears/selenium-automation-bundle/blob/docs/docs/fundamentals/Data%20Driven%20Testing/Data%20Driven%20Testing%20with%20CSV.md#storing-and-retrieving-table-like-data-with-csv
[storing data in csv]: https://github.com/sysgears/selenium-automation-bundle/blob/docs/docs/fundamentals/Data%20Driven%20Testing/Data%20Driven%20Testing%20with%20CSV.md
[the method `map()`]: https://github.com/sysgears/selenium-automation-bundle/blob/docs/docs/fundamentals/Data%20Driven%20Testing/Data%20Driven%20Testing%20Module.md#datamapper
[creating test classes]: https://github.com/sysgears/selenium-automation-bundle/blob/docs/docs/advanced/Writing%20Tests.md#general-considerations-before-writing-tests