# Data Driven Testing in Action

Selenium Automation Bundle provides a demo test that takes advantage of the Data Driven Testing approach. There are two
files you may want to look at:

* `src/test/groovy/.../tests/demo/Tools.groovy`, the demo test class
* `src/test/resources/data/google/test_data.yml`, the YAML file with the test data

You may have looked at a short review of the `Tools` class in [Introduction to Data Driven Testing]. In this guide we're
going give more details how the data-driven tests work and how they should be created.

## General Considerations for Creating Test Classes for Data Driven Testing

First, it's necessary to mention the available functionalities provided by the bundle for Data Driven Testing:
 
* `DataLoader`, the class that let's you load test data from YAML files
* `DataMapper`, the class that let's you map the test data retrieved from YAML files by `DataLoader`
* `@Find`, `@Locator`, and `@Query`, the annotations to build queries for test data

Here are a few considerations for writing tests that are used in Data Driven Testing scenarios. Your test class should:

* Import `DataLoader` from `src/main/groovy/.../core/data/`.
* Import necessary annotations from `src/main/groovy/.../core/data/annotations/`.
* Inherit `FunctionalTest`, a base test class that provides a `DataMapper` instance.
* Create a property in your class to reference the YAML file with the test data.
* Use TestNG `@DataProvider` to create a Data Provider method to be able to retrieve test data.
    
Let's put the listed recommendations into the context. In the section below, we discuss a concrete data-driven test.

## Demo Test Class Tools for Data Driven Testing

The data-driven test, shown below, follows all the recommendations from the [previous section](#general-considerations-for-creating-test-classes-for-data-driven-testing). 
The `Tools` class below is fairly long, but we'll break it [into chunks](#creating-page-object-and-data-file-properties) 
and explain each part of the test.

```groovy
package com.sysgears.seleniumbundle.tests.demo

import com.sysgears.seleniumbundle.common.FunctionalTest
import com.sysgears.seleniumbundle.core.data.DataLoader
import com.sysgears.seleniumbundle.core.data.annotations.Find
import com.sysgears.seleniumbundle.core.data.annotations.Locator
import com.sysgears.seleniumbundle.core.data.annotations.Query
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

### Creating Page Object and Data File Properties

The following two lines are self-explanatory, but it's worth mentioning that you should instantiate the YAML file with 
your data. `Tools` stores the reference to the demo file `test_data.yml` in `DATAFILE` that will be used in Data 
Provider method:

```groovy
protected GooglePage googlePage
private final static String DATAFILE = "src/test/resources/data/google/test_data.yml"
```

### Creating a Data Provider

As required by TestNG, your Data Provider method must be annotated with `@DataProvider` and return an array of array of
objects &ndash; `Object[][]`. 

The `Tools` class creates its own Data Provider method `getTestData()` to respect this requirement:

```groovy
@DataProvider(name = 'getTestData')
Object[][] getTestData(Method m) {
    mapper.map(DataLoader.readListFromYml(DATAFILE), m, this)
}
```

Here's how Data Provider in Selenium Automation Bundle is created:

* `@DataProvider` accepts the `name` parameter, which you'll use to reference the created Data Provider.
* Data Provider calls `map()` on `mapper` (the instance of `DataMapper` available through `FunctionalTest`). 
[The method `map()`] returns `Object[][]` created from data from the YAML file.  
* Data Provider uses `DataLoader.readListFromYml()` to read data from the YAML file.

Notice that the parameter `m` passed to `getTestData()` is the method (such as `checkOptionsForCategories()`) that will 
use `getTestData()`. Also notice that you need to call the method `mapper.map()` and pass three parameters to it: the 
retrieved test data as `List<Map>`, the method that will use `getTestData()`, and the `this` object.

As you can see from the example, you can use the `DataLoader` static methods `readListFromYml()` and `readMapfromYaml()` 
to retrieve data from YAML file and pass it as the first argument to `mapper.map()`. 
 
Now that you know how a Data Provider is created with Selenium Automation Bundle, you can have a look at the actual test 
that uses `getTestData()`.

### Using Data Providers in Tests

The `Tools` class has two tests, each of which uses the capabilities provided by the `data` module. Since we've already 
looked at the method `testSomethingWithData()` in [Introduction to Data Driven Testing], we'll now focus on the
`checkOptionsForCategories()` method, which is more complex.

#### checkOptionsForCategories

The following test `checkOptionsForCategories()` demonstrates the use of custom bundle annotations and the Data Provider 
method: 

```groovy
@Test(dataProvider = "getTestData", 
      description = "Checks that specific tools are available for a chosen category")
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
```

Here's how it works:

1. The `@Test` annotation sets the `dataProvider` to `getTestData` to retrieve the test data from `test_data.yml`.
2. The `@Query` annotation allows you to specify what data you need from a YAML file. `@Query` uses another annotation,
`@Find`, as an argument. `@Find` lets you specify the key-value pairs for data search.
3. The `@Locator` annotation accepts a string with the query.

`query` and `category` passed to the first two `@Locator`s are simple. But you can also get specific values with the 
complex requests such as `result.page_elements.tools`. This request corresponds to the structure of the object in a YAML 
file. 

The query `"result.page_elements.tools"` will be able to get the data from the YAML file with the following structure:

```yaml
- query: any_query
  result:
    page_elements:
      tools:
        - All news
        - Recent
        - Sorted by relevance
```

Now, as you run the test:

* The search request `any_query` will be sent to Google.
* The page with results will be returned.
* Using `ResultsPage`, Selenide clicks on the links to open menus.
* The actual testing happens in the last method `areToolsPresent()`. The list of tools `All news`, `Recent`, and 
`Sorted by relevance` are retrieved from `test_data.yml` using the request `result.page_elements.tools`.

### YAML File

The YAML file with the demo data is located in `src/test/resources/data/google/test_data.yml`. Here's what it looks like:

```yaml
- query: google
  category: Images
  result:
    url:
      params:
        tbm: isch
        q: google

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

In your tests, the objects in the YAML file would look like this:

```
{
    query: 'google',
    category: 'Images',
    result: {
        url: {
            params: {
                tbm: 'isch',
                q: 'google'
            }
        }
    }
}

{
    query: 'bing',
    category: 'News',
    result: {
        url: {
            params: {
                tbm: 'nws',
                q: 'bing'
            }
        },
        page_elements: {
            tools: [
                'All news', 
                'Recent', 
                'Sorted by relevance'
            ]
        }
    }
}
```

As you can see, using a simple and clean syntax, you can create complex objects in YAML and avoid hardcoding these 
objects in your data-driven tests. What's great is that getting the values is still very simple: with requests similar 
to `result.page_elements.tools` you can get access to any value however deep the value is nested.

[the method `map()`]: https://github.com/sysgears/selenium-automation-bundle/blob/docs/docs/fundamentals/Data%20Driven%20Testing/Data%20Driven%20Testing%20Module.md#datamapper
[creating test classes]: https://github.com/sysgears/selenium-automation-bundle/blob/docs/docs/advanced/Writing%20Tests.md#general-considerations-before-writing-tests