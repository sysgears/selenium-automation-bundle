# Data Driven Testing with CSV Files

In this guide, we give more details on how to create data-driven tests with CSV files with Selenium Automation Bundle.

Selenium Automation Bundle has a demo test that uses data from a CSV file:

* `src/test/groovy/.../tests/demo/Tools_PlainData.groovy`, the demo test class
* `src/test/resources/data/google/test_data.csv`, the CSV file with the test data

With our mechanism for DDT and CSV files:

* You can localize data per test file
* You can store data in a table-like structure
* You don't need to connect to a database

### Storing and retrieving table-like data with CSV

You can store test data in CSV-like tables with Selenium Automation Bundle. The file format is basically the same as the standard CSV, but we added a custom delimiter such as the pipe that you can use to separate portions of data.

Storing and maintaining test data in a table-like structure seem more natural and simpler to read and use compared to [storing data in YAML]. You may try out both approaches and even mix them when writing your tests, though, to be more flexible.

Here's an example of a CSV file `src/test/resources/data/google/test_data.csv` that we use for the demo data-driven test `Tools_PlainData.groovy`:

```csv
method: checkUrlParameterChanges
QUERY                            | CATEGORY | RESULT.URL.PARAMS
google                           | Images   | [tbm:isch, q:google]
bing                             | News     | [tbm:nws, q:bing]

method: checkOptionsForCategories
QUERY                            | CATEGORY | RESULT.PAGE_ELEMENTS.TOOLS
google                           | Images   | [Size, Color, Usage rights, Type, Time, More tools]
bing                             | News     | [All news, Recent, Sorted by relevance]
```

Using CSV files to store test data provides the following benefits:

* Each test method has all the test data it needs
* Each data set is grouped by the method name
* Each line with a data set makes a separate method execution

For example, both `checkUrlParameterChanges` and `checkOptionsForCategories` methods will run twice for their own `google`
and `bing` data sets defined on separate lines.

Using CSV files causes a couple of minor issues compared to using YAML files:

* If you need to use the same data for different tests, you can't but repeat the data
* You have to specify the list of data in the same exact order as the list of parameters in test methods

To clarify the second issue, have a look at the method that uses the CSV file:

```groovy
void checkUrlParameterChanges(String query, String category, Map params) {
    /* The test code is omitted */
}
```

The retrieved data is mapped to the parameters in the test method `checkUrlParameterChanges()` in a succession:

* The column `QUERY` with the `google` value is mapped to the first parameter `String query`
* The column `CATEGORY` with the `Images` value is mapped to the second parameter `String category`
* The column `RESULT.URL.PARAMS` with the `[tbm:isch, q:google]` value is mapped to `Map params`

If you specify parameters in your test method in a different order than in the data file, you will get unpredictable results. With YAML files and tree-like structure for your data, you avoid this small annoyance.

You can learn more about creating test data in CSV in a dedicated guide:

* [Creating Test Data in Table-Like Structure]

## General Considerations for Creating Test Classes for Data Driven Testing

Here are a few considerations for writing data-driven tests with Selenium Automation Bundle:

* Import `DataLoader` from `src/main/groovy/.../core/data/`;
* Import `PlainData` from `src/main/groovy/.../core/data/PlainData.groovy`.
* Make the test class extend `FunctionalTest`.
* Create a reference the test data file; and
* Create a data provider method with the `@DataProvider` annotation to retrieve test data using the said reference.
    
Let's put these recommendations into the context. In the section below, we have a look at a demo data-driven test.

## The demo test class `Tools_PlainData` for Data Driven Testing

The `Tools_PlainData` class implementation is fairly long, but we'll break its key parts [into chunks](#creating-page-object-and-data-file-properties) and explain them individually.

Look for the file `src/test/groovy/.../tests/demo/Tools_PlainData.groovy`.

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
 * Import PlainData to transform raw data retrieved 
 * by DataLoader from a CSV file into a Groovy object.
 */
import com.sysgears.seleniumbundle.core.data.PlainData
 
/**
 * Import the page model classes to handle the Google search page
 * and the Google search results page.
 */
import com.sysgears.seleniumbundle.pagemodel.GooglePage
import com.sysgears.seleniumbundle.pagemodel.ResultsPage 

/**
 * Import TestNG annotations. 
 */
import org.testng.annotations.BeforeMethod
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

import java.lang.reflect.Method

class Tools_PlainData extends FunctionalTest {

  // Create a property to reference an instance of GooglePage
  private GooglePage googlePage
  // Create a property to reference the data stored in a CSV file
  private data = new PlainData(DataLoader.readListFromPlainDataFile("src/test/resources/data/google/test_data.csv",
          conf.data.plain.dataSetSeparator), this.class).data
          
  // Prepare the Google page instance for testing
  @BeforeMethod
  void openApplication() {
    googlePage = new GooglePage().open().waitForPageToLoadElements().selectLanguage()
  }
  
  /**
   * Create a data provider method. The method must return the data.
   * The `data` property references a Groovy map [:], in which the 
   * method names are keys and their data is a value.
   */
  @DataProvider(name = 'getTestDataFromCsv')
  Object[][] getTestDataFromCsv(Method method) {
    data[method.name]
  }
  
  /**
   * A test method to verify the parameters in the URI.
   * This method is automatically added to the `data[:]` property as a key. 
   * The data retrieved from the file `"src/test/resources/data/google/test_data.csv"` is
   * found by the method name and is automatically mapped to the key `checkUrkParameterChanges`
   * in the `data[:]` property.
   */
  @Test(dataProvider = "getTestDataFromCsv",
        description = "Checks that the URL parameters specified in test data are changed based on the selected category")
  void checkUrlParameterChanges(String query, String category, Map params) {
    // Search for the query `query`; the value for `query` is
    // retrieved from the `test_data.csv`
    googlePage
          .searchFor(query)

    new ResultsPage()
          .waitForPageToLoadElements()
          .selectCategory(category)
          .validateUrlParams(params)
  }
  
  /**
   * A test method to verify the parameters in the URI.
   * This method is automatically added to the `data[:]` property as a key. 
   * The data retrieved from the file `"src/test/resources/data/google/test_data.csv"` is
   * found by the method name and is automatically mapped to the key `checkUrkParameterChanges`
   * in the `data[:]` property.
   */
  @Test(dataProvider = "getTestDataFromCsv",
        description = "Checks that specific tools are available for the selected category")
  void checkOptionsForCategories(String query, String category, List tools) {
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

### Creating Page Object and Data File Properties

The following first two lines from `Test_PlainData` are self-explanatory: first, the property to reference the page object is created, and then the property to reference the CSV file with data.

```groovy
private GooglePage googlePage
private data = new PlainData(DataLoader.readListFromPlainDataFile("src/test/resources/data/google/test_data.csv",
        conf.data.plain.dataSetSeparator), this.class).data
```

Notice that when a `PlainData` object is instantiated, three parameters need to be passed to the constructor:
 
* The result of the `DataLoader.readListFromPlainDataFile()`
* The separator (which is the pipe `|` by default) used to delimit portions of data
* The instance of the current class to get all method names

To read data from `test_data.csv`, `DataLoader` is used. It returns raw data. Defined inside the `PlainData` class, the method `map` takes in the raw data from CSV, finds the necessary test data by the method name (recall the line `method: checkOptionsForCategories` in `test_data.csv`), transforms data into a map of key-value pairs or to a list of comma-separated values, and parses the data.

`data` is a map `[:]` with the method names as keys and test data as the value, which can be a list or map depending on how you structure data in the CSV file. 

The method names are automatically added to this map if they're annotated with `@DataProvider` in your test class. In our example, the `data` property will look like this:

```groovy
data = {
  checkOptionsForCategories: {
    query: "google"
    category: "images"
    result: {
      page_elements: {
        tools: ["All news", "Recent", "Sorted by relevance"]
      }
    }
  }
}
```

### Creating a Data Provider

As required by TestNG, a data provider method must be annotated with `@DataProvider` and return a two-dimensional array of objects &mdash; `Object[][]`.

The `Tools_PlainData` class creates `getTestDataFromCsv()` to respect this requirement:

```groovy
@DataProvider(name = 'getTestDataFromCsv')
Object[][] getTestDataFromCsv(Method method) {
  data[method.name]
}
```

Here's how a data provider with Selenium Automation Bundle is created:

* `@DataProvider` accepts the `name` parameter to reference the created Data Provider from inside the test methods
* The method accepts the current test method and finds the test data for this method from `data` by its name

Notice that the parameter `method` passed to `getTestDataFromCsv()` is the actual test method such as `checkUrlParameterChanges()` or `checkOptionsForCategories()` created in `Tools_PlainData`.

### Using Data Providers in Tests

`Tools__PlainData` has two tests, each of which uses the capabilities provided by the bundle's data module. For the sake of brevity, we review just one of them, `checkOptionsForCategories()`.

The following test `checkOptionsForCategories()` demonstrates the use of custom bundle annotations and the Data Provider method:

```groovy
@Test(dataProvider = "getTestDataFromCsv",
      description = "Checks that specific tools are available for chosen category")
void checkOptionsForCategories(String query, String category, List tools) {
    googlePage
            .searchFor(query)

    new ResultsPage()
            .waitForPageToLoadElements()
            .selectCategory(category)
            .openToolsMenu()
            .areToolsPresent(tools)
}
```

Here's how it works:

1. The `@Test` annotation sets `dataProvider` to `getTestDataFromCsv()`.
2. `getTestDataFromCsv()` uses the method name `checkOptionsForCategories()` to get the test data from the map `data[:]`. The data gets transformed from the raw data (a string) to a Groovy object.
3. The `googlePage` instance uses the method `searchFor` to search the queries `google` and `bing` as defined in `test_data.csv`.
4. Once the results get returned, we instantiate `ResultsPage` and wait until the page is fully loaded before interacting with it.
5. Selenium clicks on the category `Images` or `News` depending on which data set is currently used.
6. Selenium clicks on the menu item `Tools`.
7. The method `areToolsPresent()` verifies if the tools `["All news", "Recent", "Sorted by relevance"]` are presented in the list. They're shown once you click the link `Tools` on the Google results page.

The method `checkUrlParameterChanges()` works nearly the same way, just the different data from `test_data.csv` is used and other methods run.  

That's basically all you need to know about using `.csv` files in your tests for data-driven testing.

[the method `map()`]: https://github.com/sysgears/selenium-automation-bundle/blob/docs/docs/fundamentals/Data%20Driven%20Testing/Data%20Driven%20Testing%20Module.md#datamapper
[creating test classes]: https://github.com/sysgears/selenium-automation-bundle/blob/docs/docs/advanced/Writing%20Tests.md#general-considerations-before-writing-tests