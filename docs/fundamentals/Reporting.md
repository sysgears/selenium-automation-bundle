# Reporting with Selenium Automation Bundle

## Table of Contents
* [Key Allure Features](#key-allure-features)
* [Allure Configurations](#allure-configurations)
* [Allure Report Results](#allure-report-results)
* [Allure Commands](#allure-commands)
    * [downloadAllure](#downloadallure)
    * [allureReport](#allurereport)
    * [allureServe](#allureserve)
* [Attaching Screenshots to Allure Reports](#attaching-screenshots-to-allure-reports)
    * [How to Use Allure Listener](#how-to-use-allurelistener)
        * [Apply AllureListener in a Class](#apply-allurelistener-in-a-class)
        * [Apply AllureListener in TestNG Configurations](#apply-allurelistener-in-testng-configurations)
        * [Apply AllureListener in build.gradle](#apply-allurelistener-in-buildgradle)
* [Using @Step Annotation for Allure Reports](#using-step-for-allure-reports)
* [Standard Reports](#standard-reports)

Reporting is important for automated testing, and Selenium Automation Bundle comes with great capabilities regarding
report generation. We've built the Allure report library into the bundle to create detailed test reports presented by a
convenient web application that you can open in your favorite browser.

Allure can be used as a command line tool, but there's no need to install it on your computer unless you want to merge
test results from different environments (we'll discuss how to merge multiple requests into on in the
[Allure results](#allure-report-results) section). We've included the Allure plugin into the bundle, so you can use all
key Allure features without additional installation.

Instead of Allure, you can also use the [default reporter](#standard-reports), which we'll discuss in the last section.

## Key Allure Features

* Generating reports with detailed logs and charts
* Merging multiple reports into a single report
* Attaching various files to test reports
* Serving reports to the browser

### Allure Configurations

There are several configurations related to Allure in the `build.gradle` file.

First, Selenium Automation Bundle comes with the Gradle plugin:

```groovy
buildscript {
    // other code

    dependencies {
        classpath "io.qameta.allure:allure-gradle:2.5"
    }
}
```

There's one more Allure dependency in `build.gradle`:

```groovy
`io.qameta.allure:allure-testng:2.6.0`
```

Note that the versions of the `allure-testng` library and `allure-gradle` plugin are different. These components of
Allure installation aren't strictly dependent on each other. Therefore, if you decide to update Allure, the versions of
`allure-testng` and `allure-gradle` don't need to be the same.

Here are also the default Allure configurations in `build.gradle`:

```groovy
// Allure configuration.
allure {
    autoconfigure = true
    version = '2.6.0'
}
```

You don't need to change the default Allure configurations to work with Selenium Automation Bundle.

### Allure Report Results

Each time you run your tests, the Allure plugin creates the `allure-results/` directory under `build/` and populates it
with JSON files that store the test results. Allure uses these JSON files to generate a client-side application with the
report, which you can then view in your browser.

Allure also allows you to merge the test results from several `allure-results/` directories.

When testing your application in different environments on remote servers, you'll be generating a separate test report
in each environment. Because it can be inconvenient to view those reports individually, you can download the
`allure-results/` directories from different remote servers on your computer and then merge them into a single HTML
report.

To be able to merge the test results from several `allure-results/` directories, you'll need the
[Allure command line tool](https://docs.qameta.io/allure/#_installing_a_commandline). Once you install the Allure CLI,
you need to copy your `allure-results` directories into one place, for example, into a directory
`global-allure-results/` and run:

```bash
cd global-allure-results
allure generate allure-results-1 allure-results-2 allure-results-3
```

Naturally, different directories with Allure results should be named differently.

Allure will then create the `allure-report/` directory with combined test results. Note that if your tests execution
yielded the same results in different environments, then Allure will simply show the results of the latest report. And
if some test didn't pass in a specific environment, while in other environments the execution of the same test was
successful, then Allure will add _only the failed test_ to the combined report.

**NOTE**: Allure can only merge the JSON files into a single report. If you generated client-side applications with the
`allureReport` command ([explained below](#allurereport)), you won't be able to merge several _applications_ with
`allure generate`. The Allure reports that you _can_ merge are located in the `build/allure-results/` directory.

### Allure Commands

Selenium Automation Bundle uses the Allure plugin that gives us three useful commands to carry out several Allure tasks.
(Allure provides additional commands, but you'll need the [Allure command line tool] to use them.) You can run Allure
commands with `./gradlew` like this:

```bash
./gradlew <command provided by Allure plugin>
```

Here are the three commands that you'll use with Selenium Automation Bundle:

#### downloadAllure

`downloadAllure` is necessary to download the Allure plugin. You need to run this command only once after cloning
Selenium Automation Bundle on your computer. After running `downloadAllure`, a new `.allure/` directory is created in
the root of the project.

If you don't run `./gradlew downloadAllure` before generating an Allure report with [`allureServe`](#allureserve) for
the first time, you'll get an error:

```bash
./gradlew allureServe

> Task :allureServe
Cannot find allure commanline in /home/sviatoslav/projects/test-selenium-bundle/.allure/allure-2.6.0
```

#### allureReport

`allureReport` is used for generating a client-side application with the report so that you can view it in your browser.
The application uses the data from the JSON files located under `build/allure-results/`.

`allureReport` saves the application to the `build/reports/allure-report/` directory. To view the test results, just
open the `index.html` file, which is stored under the `allure-report/` directory.

`allureReport` not only generates a report, but it can also _download Allure_ if necessary (`allureReport` first runs
`downloadAllure` and then creates a report). Once you clone the repository with the bundle, you can run the following
command to run the demo tests and generate a report without specifically running `downloadAllure`:

```bash
./gradlew clean test allureReport
```

`allureReport` can be useful when you need to generate a complete report and send it to a manager, client,
Quality Assurance specialist, or developer in your team. You only need to send the `build/reports/allure-report/`
folder.

#### allureServe

`allureServe` does two things: It generates an application with the  report and _serves_ it to the browser. (To serve
the reports, Allure creates an instance of the Jetty HTTP server.)

When you run `allureServe`, your default browser will be automatically opened with the report at
`http://localhost:12345/index.html` (`12345` isn't the actual port; Allure arbitrarily sets the port to open the
report).

Note that the `allureServe` command does _not_ create a report under `build/reports/allure-report/`. If you want the
application files with the report, use [`allureReport`](#allurereport).

Also note that `allureServe`, unlike `allureReport`, does _not_ download Allure on the first run! Therefore, if  you
clone Selenium Automation Bundle and try to run `allureServe` immediately, you'll get an error:

```bash
> Task :allureServe
Cannot find allure commanline in /home/your-computer/selenium-automation-bundle/.allure/allure-2.6.0
```

To fix this, use [`downloadAllure`](#downloadallure) or run:

```bash
./gradlew clean test downloadAllure allureServe
```

Again, you need to download Allure only once.

## Attaching Screenshots to Allure Reports

Selenium Automation Bundle fully controls the creation of screenshots for the functional and UI tests. In this section,
we'll review the `AllureListener` class, which you can use to create screenshots of failed tests and attach them to the
Allure reports. The creation of screenshots for UI tests is handled separately, and you can consult the UI Testing Guide
to know more about this feature.

Although Selenide itself provides the functionality to take screenshots, we turned it off in order not to double
screenshots when you execute your tests. Another reason for not using Selenide's functionality for taking screenshots is
that we manually attach them to Allure reports, and this functionality isn't available by default with Selenide.

The `AllureListener` class implements the `ITestListener` interface (provided by TestNG) and uses Selenide to listen to
failed tests and take screenshots automatically. Basically, the `AllureListener` class works like this. Whenever a test
fails, the `onTestFailure()` TestNG method, implemented by `AllureListener`, will be called, and Allure will attach a
screenshot to the failed test. If you want to view the entire implementation of `AllureListener`, you can find the class
under `./src/test/.../listeners/`.

### How to Use AllureListener

You can apply `AllureListener` in one of three places:

* In a test class;
* In TestNG XML file in the tag `listeners`; or
* In `build.gradle` (not recommended due to unstable behavior).

You can apply `AllureListener` globally. Note, however, that using `AllureListener` globally isn't recommended when you
write tests for the user interface. Selenium Automation Bundle suggests another approach for handling screenshots
creation for testing the UI. If you'll use `AllureListener` for UI testing, you'll get extra screenshots of the HTML
pages.

#### Apply AllureListener in a Class

You can attach an AllureListener to a specific class like this:

```groovy
package tests.my_tests

import com.sysgears.seleniumbundle.listeners.AllureListener
import org.testng.annotations.Listeners
import org.testng.annotations.Test

@Listeners(AllureListener)
@Test(description = "Verify if the user can login")
class LoginPageTest {
    // Your code
}
```

Thanks to this line - `@Listeners(AllureListener)` - screenshots of the page under test will be attached to the Allure
report if any test methods failed during execution.

The `AllureListener` class is already applied on the `FunctionalTest` class. Therefore, you don't need to additionally
apply the listener in a functional test class that inherits `FunctionalTest`. (For the implementation of
`FunctionalTest`, look for `./src/test/groovy/com/sysgears/seleniumbundle/common/FunctionalTest.groovy` file.)

#### Apply AllureListener in TestNG Configurations

You can use the `listeners` and `listener` tags to attach `AllureListener` (and your custom listeners) to a test suite
in TestNG configurations, the `testng.xml` file:

```xml
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd" >

<suite name="LoginTestSuite">
    <listeners>
        <listener class-name="com.sysgears.seleniumbundle.listeners.AllureListener" />
    </listeners>

    <test name="LoginPageTest">
        <classes>
            <class name="com.sysgears.seleniumbundle.tests.login.LoginPageTest"/>
        </classes>
    </test>
</suite>
```

#### Apply AllureListener in build.gradle

This is how you can add `AllureListener` in the `build.gradle` file:

```groovy
/**
 * Configures test task.
 */
test {
    doFirst {
        useTestNG() {
            listeners << "com.sysgears.seleniumbundle.listeners.AllureListener"
        }
    }
}
```

Unfortunately, adding a listener to TestNG in `build.gradle` may not work correctly in some environments, which is why
we recommend that you apply `AllureListener` [in a class](#apply-allurelistener-in-a-class) or in
[TestNG configurations](#apply-allurelistener-in-testng-configurations).

## Using @Step for Allure Reports

You can use the `@Step` annotation, which is provided by Allure to give a more detailed description for the page object
methods. These descriptions will be shown in Allure reports. Using `@Step` isn't obligatory, however.

As shown in the code sample below, you can add `@Step` annotations before method definitions in your page objects.
`@Step` accepts various parameters, in particular, a string description of what a test method does. If you omit the
string description, `@Step` will automatically use the method name for a step.

```groovy
class GooglePage extends AbstractPage<GooglePage> {
    // Setup code goes here...

    @Step("Perform search")
    void searchFor(String query) {
        enterQuery(query)
        submit()
    }

    @Step("Enter query")
    private GooglePage enterQuery(String query) {
        queryField.val(query)
        this
    }

    @Step("Submit search")
    private void submit() {
        queryField.pressEnter()
    }
}
```

You can view these annotations in your report.

> There are many more annotations provided by Allure. You can find them in the `External Libraries` directory (you can
use your IDE to view this directory). You need to open the `Gradle:io.qameta.allure:allure-java-commons:2.6.0`
directory, and then find `allure-java-commons-2.6.0.jar`. There, all the Allure annotations are located in the
`io.qameta.allure/` directory.

## Standard Reports

Although it's unlikely that you'll use a default report generated by Gradle, we should still talk about it.

A default report is generated to `build/reports/tests/test/` each time you run tests with `./gradlew`. There isn't much
information in this test report, which is why using Allure is recommended.

You can copy the directory with the default test report files to another place or email it to a manager in your team.

[allure command line tool]: https://docs.qameta.io/allure/#_installing_a_commandline