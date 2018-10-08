# Data Driven Testing with Selenium Automation Bundle

Selenium Automation Bundle embraces the Data Driven Development pattern to help you manage large sets of data for your
tests. Using Yaml files with the `data` module, you have a simple way to store and retrieve test data from the Yaml
files.

## Why Data Driven Testing Is Important

Think of writing a test that verifies a registration form by feeding 50 or more test users (read: objects with
properties `username`, `email`, `password`, and `passwordRepeat`) to check how the application handles registration if
various user names, emails, and passwords were entered into the form.

This is what Data Driven Testing about: DDT means testing your application using huge sets of data to verify how the
application handles different user inputs and what outputs it gives for those inputs.

DDT is an important part of testing as it helps to ensure the stability of an application. But DDT can also lead to
headaches when you need to handle the huge sets of data in your tests. For example, when you hardcode the test data in
your test classes, and need to change some property, it'll take time to change the same property in many tests. Handling
these objects scattered across dozens of files with tests isn't the best approach to Data Driven Testing.

What if you had all your sets of data stored in Yaml files and be easily accessible from your tests? Selenium Automation
Bundle makes that possible and helps you **completely** separate test data from your functional tests, which will make
it easier to manage your tests.

Let's discuss how Selenium Automation Bundle simplifies handling of the data sets.

### Selenium Automation Bundle Mechanism for Data Driven Testing

Our mechanism for writing data-driven tests is built around the TestNG functionality, more specifically, the
`@DataProvider` annotation. We also created custom classes and annotations to help you request data from Yaml file.
You can request either an entire data set from a file or only the necessary test data.

Our mechanism for testing the app according to DDT consists of the following components:

* Yaml files with data structured in a specific way
* `DataLoader`
* `DataMapper`
* `@Locator`, `@Find`, and `@Query` annotations
* `@DataProvider`, a TestNG annotation

Speaking of storing data in files, you can consider Yaml files as your "database" that's much easier to access and
handle. You can add new data to a Yaml file easily, and you'll be able to request necessary data from a file much easier
than you would in case with a conventional relational or document-oriented database.

## Running a Data Driven Test

Selenium Automation Bundle provides a demo of a data driven test `Tools` that uses all the functionality available in
the `data` module.

To run the test, first change TestNG configuration file:

```xml
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd" >

<suite name="Suite">
    <test name="Data Driven Test">
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

Once the test run is completed, your default browser will open the report.

### Yaml File

You can have a look at a Yaml file with the test data that was used in the run. The file is stored under the
`src/test/resources/data/google/` directory. Here's the file:

```yml
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

You can save your data in a similar fashion.