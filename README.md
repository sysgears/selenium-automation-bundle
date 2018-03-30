## Selenium Automation Bundle

Selenium Automation Bundle is a seed project to start Selenium test automation with TestNG and Selenide.
The bundle does not aim to solve every possible task for every new test automation project, but instead provide 
the quality assurance community with a good starting point for configuring local test environment. It’s not obligatory 
to use any of the tools provided in the bundle unless you find them necessary for your tasks. The bundle contains simple, 
open and well-documented code which we encourage everyone to dig into and modify. Any suggestions and contributions 
are most welcome!

##### Features
 - Automated Selenium Driver initialization for Chrome, Firefox, Edge and Safari
 - Allure Reporting for structured test execution reports with timeline and charts
 - Page Object Model based test structure with custom annotations and examples
 - Test data lookup from YAML files through custom annotations on test methods
 - Integrated Selenide for writing fluent and concise code in your POMs
 - Interface for creating custom command-line commands
 - Groovy language support for Test and POM objects

##### Technologies

Selenium, Selenide, TestNG, Allure, aShot and Groovy


### Creating POM classes

Selenium Automation Bundle does not put any restrictions on how you can write POM objects. This principle will apply to other 
bundle functions. Instead of forcing you to follow a specific approach to creating test objects and test methods, 
the bundle tries to provide utility methods and examples on which you can rely during test design.

For example, we added an abstract class `AbstractPage` which you can inherit from. `AbstractPage` contains a few 
basic methods (the list is yet to be extended) for manipulating page state, and implements a mechanism for waiting 
until all the page elements annotated with `@StaticElement` are loaded. 

Your POM class that inherits `AbstractPage` and uses Selenide can look like the following:

```groovy
class GooglePage extends AbstractPage<GooglePage> {

    @StaticElement
    private SelenideElement queryField = $(By.name("q"))

    GooglePage() {
        this.url = "/"
    }

    @Step("Perform search")
    ResultsPage searchFor(String query) {
        enterQuery(query)
        submit()
    }

    @Step("Enter query")
    private GooglePage enterQuery(String query) {
        queryField.val(query)
        this
    }

    @Step("Submit search")
    private ResultsPage submit() {
        queryField.pressEnter()
        new ResultsPage()
    }
}
```

@Step is an Allure tool annotation, and its usage will be described in the reporting section below.  


### Creating Functional Tests

Selenium Automation Bundle relies on TestNG for test execution. Since this tool is widely used by the most part 
of the community, the bundle does not change anything in the underlying mechanisms of TestNG. You can create test 
methods as you regularly do, or you can inheriting `BaseTest` class to automate some of the driver initialization 
tasks:

```groovy
@Listeners([AllureListener.class])
@Test(groups = "functional")
class Search extends BaseTest {

    protected GooglePage googlePage

    @BeforeMethod
    void openApplication() {
        googlePage = new GooglePage().open().waitForPageToLoadElements().selectLanguage()
    }

    @Test(description = "Checks number of results on the first page")
    void checkSearch() {
        googlePage
                .searchFor("SysGears")
                .isResultSize(10)
    }
}
```

As you can see, this test relies on a parent `BaseTest` class which initializes Selenide and 
*org.openqa.selenium.WebDriver* for POM classes by interpreting testNG.xml parameters and project configuration 
settings defined the in `ApplicationProperties.groovy` configuration file. This all done in a thread-safe way, 
so it’s possible to run in parallel several test classes that inherit `BaseTest` if your test requirements allow it.


### Retrieving Test Data in Test Classes

Selenium Automation Bundle adds a utility `DataMapper` class and `@Location` and `@Query` annotations which work together 
with TestNG DataProvider and allow to lookup and map test data to a test method from a YAML file.

`DataMapper` class provides `Object[][] map(List<Map> data, Method testMethod)` method which should be executed in 
TestNG DataProvider. The method expects test data list and a reference to a test method for which the data provider 
is used. The test method must define `@Locator` annotations on its arguments, so the `DataMapper` will know which 
values should be mapped to which arguments:

```groovy
@DataProvider(name = 'getTestData')
Object[][] getTestData(Method m) {
    mapper.map(DataLoader.readListFromYml(DATAFILE), m, this)
}
```

YAML file "test_data.yml":

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

Test method:

```groovy
@Test(dataProvider = "getTestData",
            description = "Checks that url parameters specified in test data are changed based on chosen category")
    void checkUrlParameterChanges(
            @Locator("query") String query,
            @Locator("category") String category,
            @Locator("result.url.params") Map params) {
        googlePage
                .searchFor(query)
                .selectCategory(category)
                .validateUrlParams(params)
    }
```

Since the data is retrieved by `DataMapper` is passed to the DataProvider, the test method will be executed as many times 
as there are entries in the YAML file.

Additionally, you can specify `@Query` annotation on a test method to make `DataMapper` retrieve only specific entries 
from the list defined in the YAML file:

```groovy
@Test(dataProvider = "getTestData", description = "Checks that specific tools are available for chosen category")
    @Query(@Find(name = "category", value = "News"))
    void checkOptionsForCategories(
            @Locator("query") String query,
            @Locator("category") String category,
            @Locator("result.page_elements.tools") List tools) {
        googlePage
                .searchFor(query)
                .selectCategory(category)
                .openToolsMenu()
                .areToolsPresent(tools)
    }
```
In this case `DataMapper` will filter all the data sets except the one with the "News" category.

As you can see `DataMapper` is a very simple, non-obligatory utility which can help you create data driven scenarios 
and easily share the same test data sets between different test methods.

### Reporting

// TODO

### UI Testing

// TODO

### Defining Custom Project Configuration

// TODO

### Creating Custom Command-line Commands

// TODO

### Configuring Bundle for CI

// TODO

## License

Copyright © 2016, 2017 [SysGears INC]. This source code is licensed under the [MIT] license.

[mit]: LICENSE
[sysgears inc]: http://sysgears.com