# Data Driven Testing with Selenium Automation Bundle

Selenium Automation Bundle embraces the Data Driven Testing (DDT) pattern and lets you separate test data from test classes and store data in a table- or tree-like structure while giving a convenient way to access it.

In this introduction guide, we show a couple of demo tests to get your feet wet with Data Driven Testing. But first, we have a look at the DDT approach in general to ensure that we're in the same boat.

## What's Data Driven Testing?

Imagine that you need to test an ecommerce website. The website supports two currencies &mdash; dollar and euro; offers
three types of discounts &mdash; 5%, 10%, and 15%; and provides nine product groups (we leave them to your imagination). 

Overall, you get 54 combinations &mdash; 2 currencies * 3 discount types * 9 product groups &mdash; and you need to test your application with each of them to find out whether the amounts to be paid are calculated correctly. Therefore, you have to create individual test objects with the properties `currency`, `discount`, and `productGroup` and various values for those properties for each combination.

This is what Data Driven Testing is about: We use _dozens_ of objects with different data to feed them to a _single_ test case. In DDT, the **test data** rather than the test scenarios helps to find defects in software applications. When testing our applications using large sets of data, we can understand how the application handles different user inputs and what outputs it gives for those inputs.

But DDT also comes with a problem: How should we store and manage test data?

### How to manage test data for data-driven tests

You may not want to flood your tests with test data even if you're a strong adherent of the Keep It Simple, Stupid principle. If you do hardcode data in test classes, then whenever you decide to change a property in an object, you'll spend time changing the same property in _other_ tests that use the same object. And if your test objects are complex, meaning they include other objects, managing them becomes a yet harder challenge.

Overall, handling test data scattered across dozens of files isn't the best approach to Data Driven Testing.

What if you had all the sets of data stored in CSV or YAML files easily accessible from your tests? Selenium Automation Bundle helps you achieve that by separating test data from test classes and arranging it in a readable way.

## Selenium Automation Bundle mechanism for Data Driven Testing

Our mechanism for writing data-driven tests is built of a few custom classes and annotations powered by Data Providers, 
a TestNG facility to request data in test classes.

Here are the classes and annotations that form our DDT mechanism:

* `DataLoader`, a simple class that gets data from CSV and YAML files
* `PlainData`, a class that transforms CSV data to a `Map` or `List`
* `HierarchicalData`, a class that transforms YAML data to a `Map` or `List`
* `@Locator`, `@Find`, and `@Query` annotations for building queries for YAML data

You can find out more about the listed classes and annotations in a dedicated guide:

* [Data Module for Data Driven Tests]

## Data Driven Testing in action

Selenium Automation Bundle comes a couple of demo data-driven tests that verify the same thing &mdash; the existence of certain parameters in the URL returned after a search request was sent to Google.

Here are the test classes and data files we created to demo DDT with our bundle:

* `src/test/groovy/.../tests/demo/Tools_PlainData.groovy`, the test class that gets data from CSV
* `src/test/groovy/.../tests/demo/Tools_HierarchicalData.groovy`, the test class that uses data from YAML
* `src/test/resources/data/google/test_data.csv`, the test data in the CSV-like format
* `src/test/resources/data/google/test_data.yml`, the test data in the YAML format

Both `Tools_PlainData` and `Tools_HierarchicalData` open the Google search page, send a request using data from `test_data.csv` or `test_data.yml` data files respectively, and verify that the URL of the returned page has the parameters specified in those data files.

The two approaches to managing test data are suitable for different situations but aim to fulfill the same tasks:

* Provide a convenient method to structure data
* Make it simple to retrieve data in test classes
* Ensure great readability and manageability of data

In the dedicated guides, [Data Driven Tests with CSV Files] and [Data Driven Tests with YAML Files], we explain the advantages and disadvantages of storing and handling test data in CSV and YAML files. The key idea is that these two approaches aren't reciprocally exclusive: You can mix them depending on the needs of your application to handle particular testing scenarios in the best possible way.

To run the demo data-driven tests, first change the default TestNG configuration in the `src/test/resources/testng.xml`
file. Use the configuration below:

```xml
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd" >

<suite name="Suite">
    <test name="Data Driven Testing with Selenium Automation Bundle">
        <classes>
            <class name="com.sysgears.seleniumbundle.tests.demo.Tools_HierarchicalData"/>
            <class name="com.sysgears.seleniumbundle.tests.demo.Tools_PlainData"/>
        </classes>
    </test>
</suite>
```

Now, run the tests and open a report in a browser using the command below:

```bash
./gradlew clean test allureServe
```

> **NOTE**: If you see the error `Cannot find allure commandline` after the test were executed, download and run the
Allure reporter with `./gradlew downloadAllure allureServe`. Your default browser will automatically open the report.
You can learn more about reporting with Selenium Automation Bundle in the [Reporting] guide.

Once the test execution is completed, you'll a report similar to this:

![Selenium Automation Bundle Data Driven Test Report Example](https://user-images.githubusercontent.com/21691607/54425135-3debd480-471d-11e9-95c2-dbe674ff694c.png)

For now, it's only clear what the test classes _do_, not how they _work_. Other than viewing the code, you can check out these two guides with detailed explanations of the demo data driven tests:

* [Data Driven Tests with CSV Files]
* [Data Driven Tests with YAML Files]

[data module for data driven tests]: https://github.com/sysgears/selenium-automation-bundle/blob/master/docs/fundamentals/Data%20Driven%20Testing/Data%20Module%20for%20Data-Driven%20Tests.md
[reporting]: https://github.com/sysgears/selenium-automation-bundle/blob/master/docs/fundamentals/Reporting.md
[data driven tests with csv files]: https://github.com/sysgears/selenium-automation-bundle/blob/master/docs/fundamentals/Data%20Driven%20Testing/Data%20Driven%20Tests%20with%20CSV.md
[data driven tests with yaml files]: https://github.com/sysgears/selenium-automation-bundle/blob/master/docs/fundamentals/Data%20Driven%20Testing/Data%20Driven%20Tests%20with%20YAML.md 