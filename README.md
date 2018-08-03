# Selenium Automation Bundle

Selenium Automation Bundle is a seed project for Quality Assurance engineers to help them start designing, writing,
and running automated data-driven tests with Selenide, TestNG, and Allure.

## Features

 - Automatic initialization of Selenium WebDriver for Chrome, Firefox, Edge, and Safari
 - Structured test reports with detailed logs of test execution generated with the help of Allure
 - Integrated Selenide for handling dynamic behavior on your pages and for writing less code in page objects
 - Custom annotations for accessing and mapping test data stored in a tree-like structure to streamline data-driven testing
 - A simple mechanism to add your own CLI commands for running tests or handling test data
 - Groovy support to write concise test classes and page objects

## Technologies

* Selenium
* Selenide
* TestNG
* Allure
* Groovy
* aShot

## What You Should Know Before Using Selenium Automation Bundle

Selenium Automation Bundle is built around Selenium ecosystem, which is why you should have a basic understanding of
Java, Selenium, and TestNG. The core modules of the bundle are actually written in Groovy, but you can still use Java to
write test classes and page objects.

## Installing the Bundle and Running the Tests

In this section, you're going to install Selenium Automation Bundle on your computer, run the tests, and view the test
results in the browser.

## Prerequisites

### Java

Install Java 8 or higher.

### Google Chrome

Google Chrome is the default browser that we’re using with Selenium Automation Bundle. Make sure that you have the
latest version of Chrome before you run the demo tests later in this guide.

### IDE

You can use any favorite IDE such as Intellij IDEA, Eclipse, or NetBeans when working with the bundle.

## Clone Selenium Automation Bundle

Clone the repository using the following command:

```bash
git clone https://github.com/sysgears/selenium-automation-bundle.git
```

## Run the Demo Tests

There are several test examples to help you get started. To run them, change the current working directory:

```bash
cd selenium-automation-bundle
```

And now you can run the demo tests using the command below:

```bash
./gradlew
```

> Note that if you're using Windows, you should execute `gradlew.bat` instead of `./gradlew`.

The [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) will
take it from here: It will run the demo tests in Chrome and show a confirmation in the terminal that the tests were
completed. During the execution of the demo tests, Chrome will automatically open and close several times.

Once the tests are completed, you can generate and serve a report to your default browser by running the following
commands:

```bash
./gradlew downloadAllure
./gradlew allureServe
```

Congratulations, you've just started using Selenium Automation Bundle!

## Why Was Selenium Automation Bundle Created?

As a Quality Assurance specialist, you have to take several steps before you can write automated tests for a web
application. You usually have to:

* Determine what approaches and patterns should be used to write tests.
* Find the best tools to write and run tests, handle test data, and create test reports.
* Configure the tools and make them work together efficiently.
* Create your own abstractions and tools to fulfill basic testing tasks.

However, you can avoid all that hassle by using Selenium Automation Bundle.

Selenium Automation Bundle is basically a fully prepared infrastructure that helps you to start writing automated tests
instantly. The infrastructure is built of many classes that make it simple to create tests and page objects, interact
with databases and REST API, handle test data, and fulfill other important tasks.

You can also adapt the bundle to your specific requirements. In other words, you retain full control over the provided
tools, and you can configure them and add your own libraries as you need.

The main idea is to give you the tools and approaches to carry out most of the tasks for automated testing, so you don't
have to make decisions every time you start a new project for your tests.
___

Having explained _why_ the bundle exists, let's discuss _what exactly_ it provides. Below, you'll find more information
about the 
[bundle structure](#project-structure),
as well as the
[patterns](#patterns),
[libraries](#core-stack), and
[abstractions](#other-libraries-and-abstractions)
that the bundle is built around.

## Project Structure

The directory structure of Selenium Automation Bundle is typical of Groovy-based projects generated with Gradle.
Here's what the project looks like (note that several unimportant directories aren't shown in the diagram):

```
selenium-automation-bundle
├── allure/
├── .gradle
├── build
    ├── allure-results/
    ├── reports/
        ├── allure-report
        └── tests/
            └── test/
├── gradle/
├── src/
    ├── main/
        ├── groovy/com/sysgears/seleniumbundle/
            ├── core/
            ├── pagemodel/
            └── Main.groovy
        └── resources/
            └── config/ApplicationProperties.groovy
    └── test/
        ├── groovy/com/sysgears/seleniumbundle/
            ├── common/
            ├── listeners/
            └── tests/
        └── resources/
            ├── data/
            └── testng.xml
├── .gitignore
├── build.gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
├── LICENSE
├── README.md
└── settings.gradle
```

In the table below, we discuss only the directories and files that you may use when writing tests with the bundle.

| Directory or File | Purpose |
|----------|----------|
| `build/`    | The build code and test reports are generated into this directory. |
| `build/reports/` | The `reports` directory will contain the test reports generated by TestNG and Allure. |
|----------|----------|
| `src/main/` | *Bundle Code* |
| `src/main/.../core/` | Contains the bundle code. You don't need to change anything in `core/` unless you want to add a very specific functionality for your test project. |
| `src/main/.../pagemodel/` | Stores page objects. You can view the demo page objects and put your page objects in this directory. |
| `src/main/resources/config/ApplicationProperties.groovy` | Stores the global application properties. You can set the base URL for your app or change the browser for running tests in this file. |
|----------|----------|
| `src/test/` | *Test-Related Code* |
| `src/test/.../common/` | Contains the basic test classes such as `BaseTest` and `FunctionalTest`, which provide default configurations for your tests. You can add more methods to these test classes or create your own classes to fulfill other basic test tasks. |
| `src/test/.../tests/` | Contains all kinds of tests for your app. You can put your functional, end-to-end, integration, and UI tests into this directory. |
| `src/test/resources/data/` | Stores YAML files with test data easily accessible from test classes with custom annotations to help to use the data driven testing approach. |
| `src/test/resources/testng.xml` | Provides default TestNG configurations. You may need to update `testng.xml` to change test suites or classes to run. You can also add new XML files with specific TestNG configurations next to `testng.xml`. |
|----------|----------|
| `build.gradle` | Stores build configurations. You can find the list of dependencies, plugins, and Gradle tasks in this file. |
| `gradle.properties` | Contains various project properties. For example, you can change the groups of tests, which should run, in this file. |
| `LICENSE` | License information. |
| `README.md` | The document you are reading now. |
<br />

## Patterns

### Page Object Pattern

The page object pattern was first introduced by Selenium community, and it encourages
[the reuse of code and the separation of test code from the presentation](https://www.seleniumhq.org/docs/06_test_design_considerations.jsp#page-object-design-pattern).
You can encapsulate repetitive code that works with HTML elements in page object methods and then reuse them
in your tests. Page objects also don't contain any test code to make it simple to maintain the tests. To learn more
about page objects, you can consult [this article](https://martinfowler.com/bliki/PageObject.html).

### Data-Driven Testing

Selenium Automation Bundle suggests using the [data-driven approach](https://www.guru99.com/data-driven-testing.html).
for creating tests. Internally, the bundle uses TestNG in conjunction with custom annotations and classes to implement
the Data-Driven Testing pattern. As a result, it'll be easier for you to access test data (stored in a tree-like
structure in YAML files) and map data to test classes.

## Technology Stack

The core of Selenium Automation Bundle consists of the following tools: Selenide (includes Selenium), TestNG, and
Allure. With these tools, you can fulfill roughly 90% of testing tasks: write and run tests, and generate test reports.

But the bundle gives you much more. For example, UI testing is greatly simplified thanks to the _automatic_ comparison
of screenshots of the user interface. For that, we've integrated [aShot](#taking-and-comparing-screenshots)
into the bundle.

Further below, we give more details about the bundle abstractions and included tools. Naturally, if you’re familiar with
any library that we integrated into the bundle, you may skip the section about it.

### Core Stack

#### Selenium

You may consider Selenium a medium between the browser and your web application. Selenium lets you handle browsers when
testing your app. We recommend that you refresh your memory about [Selenium WebDriver](https://www.seleniumhq.org/docs/03_webdriver.jsp).
You can also consult [a guide](https://wiki.saucelabs.com/display/DOCS/Getting+Started+with+Selenium+for+Automated+Website+Testing)
to learn more about the main Selenium concepts.

#### Selenide

Selenide is a framework built around Selenium, and it provides simple methods to find and manipulate elements on an
HTML page, and, more importantly, to handle pages that _change dynamically_. With Selenide, you’ll write less code
[than with Selenium](https://github.com/codeborne/selenide/wiki/Selenide-vs-Selenium).

#### TestNG

TestNG is a framework that manages the testing process. You should be familiar with these three aspects of TestNG: how
to [configure TestNG](http://testng.org/doc/documentation-main.html#testng-xml), how to use
[its annotations](http://testng.org/doc/documentation-main.html#annotations), and how to
create [test groups](http://testng.org/doc/documentation-main.html#test-groups). Additionally,
you may want to run through a [TestNG tutorial](https://www.guru99.com/all-about-testng-and-selenium.html).

#### Allure

Allure can generate HTML-based [reports](https://docs.qameta.io/allure/#_report_structure) with full logs and timeline
to help inspect test results presented in suites or diagrams. Additionally, Allure can serve reports to your favorite
browser and work with various Continuous Integration systems.

#### Groovy

Groovy is a programming language for the Java platform, and it features
[a simpler syntax than Java](http://groovy-lang.org/differences.html). Although Selenium Automation Bundle is written in
Groovy, you can still use Java as the main language to create page objects and test classes, or write any additional code.

### Other Libraries and Abstractions

#### Taking and Comparing Screenshots

UI testing can be complicated, but Selenium Automation Bundle makes it easy by including the
[aShot](https://github.com/yandex-qatools/ashot) library. Although Selenium allows you to
take screenshots of the UI, its functionality is rather limited. aShot, on the other hand, can take screenshots of
particular elements, viewports, or entire pages, and highlight elements. The bundle provides custom methods to
seamlessly incorporate screenshot comparison with aShot directly into your tests.

#### Communicating with REST API

The bundle defines a few convenient methods, which you can use to intercept and send HTTP requests. You can easily tap
into the network traffic between the client code and the server in order to, for example, verify if correct requests
were sent by the client.

#### Connecting to Cloud Storage

Selenium Automation Bundle comes with its own methods and commands to let you move test data such as page screenshots
and video recordings to and from cloud storage such as Dropbox.

#### Working with MongoDB

The bundle implements methods and commands so you can work with MongoDB directly from your tests. For example, the
methods can be used to verify if the app state, stored in MongoDB, was changed as expected after running the tests.
[MongoDB Java Driver](http://mongodb.github.io/mongo-java-driver/3.8/driver/getting-started/quick-start/)
is used for this functionality.

#### Debugging with Video Recorder

Video Recorder is a simple library sometimes necessary to debug tests. When some of your tests fail, it can be quite
difficult to figure out why that happened. But with Video Recorder, you can record all the dynamic changes that happen
on the web page during testing. Video Recorder has [a simple API for TestNG](http://automation-remarks.com/video-recorder-java/#_testng)
which you may have a look at.

___

## What's Next?

After running the demo tests, you'll want to know _how_ they were created. You can read the
[introduction to writing tests](https://github.com/sysgears/selenium-automation-bundle/wiki/Intro-to-Writing-Tests)
with Selenium Automation Bundle and start testing your application right away.

___

## License

Copyright © 2016, 2017 [SysGears INC]. This source code is licensed under the [MIT] license.

[mit]: LICENSE
[sysgears inc]: https://sysgears.com