# The Selenium Automation Bundle Overview

The Selenium Automation Bundle is a seed project for Quality Assurance engineers to help them start designing, writing, 
and running automated data-driven tests with Selenide, TestNG, and Allure.

In this overview, we’re going to look at the technology stack, concepts, and patterns that the bundle is built on.

## What You Should Know Before Using the Selenium Automation Bundle

The Selenium Automation Bundle is built around the Selenium ecosystem, which is why having a basic understanding of 
Java, Selenium, and TestNG is recommended. The core modules of the bundle are actually written in Groovy, but you don't 
have to write your tests in this programming language. You can still use Java to write test classes and page objects. 

We'll explain what you should know about Selenium, TestNG, and other tools in 
[the Technology Stack section](https://github.com/sysgears/selenium-automation-bundle/blob/docs/docs/OVERVIEW.md#core-stack).
For now, let's discuss the ideas behind the Selenium Automation Bundle.

## Why Was the Selenium Automation Bundle Created?

As a Quality Assurance specialist, you have to take several steps before you can write automated tests for a web 
application. You usually have to:

* Determine what approaches and patterns should be used to write tests.
* Find the best tools to write and run tests, handle test data, and create test reports.
* Configure the tools and make them work together efficiently.
* Create your own abstractions and tools to solve basic testing problems.

However, you can avoid all that hassle by using the Selenium Automation Bundle.

The Selenium Automation Bundle is basically a fully prepared infrastructure that helps you to start writing automated 
tests instantly. The infrastructure is built of many classes that make it simple to create tests and page objects, 
interact with databases and REST API, handle test data, and fulfill other important tasks.

You can also adapt the bundle to your specific requirements. In other words, you retain full control over the tools 
provided, and you can configure them and add your own libraries as you need.

The main idea is to give you the tools and approaches to carry out most of the tasks for automated testing, so you don't 
have to make a decision every time you start a new project for your tests.

Having explained _why_ the bundle exists, let's discuss _what exactly_ it provides. Below, you'll find more information 
about the abstractions, patterns, and libraries.

## Patterns

### Page Object Pattern

The page object pattern was first introduced by the Selenium community, and it encourages 
[the reuse of code and the separation of test code from the presentation](https://www.seleniumhq.org/docs/06_test_design_considerations.jsp#page-object-design-pattern).
You can encapsulate repetitive code that works with an HTML element in the methods of a page object, and then reuse them 
in your tests. Page objects also don't contain any test code to make it simple to maintain the tests. To learn more 
about page objects, you can consult [this article](https://martinfowler.com/bliki/PageObject.html).

### Data-Driven Testing

The Selenium Automation Bundle suggests using the [data-driven approach](https://www.guru99.com/data-driven-testing.html) 
for creating tests. Internally, the bundle uses TestNG, in conjunction with custom convenient annotations and classes, 
to implement the Data-Driven Testing pattern. As a result, it'll be easier for you to access test data and map data to 
test classes.

## Technology Stack

The core of the Selenium Automation Bundle consists of the following tools: Selenide (includes Selenium), TestNG, and 
Allure. With these tools, you can fulfill roughly 90% of testing tasks: write and run tests, and generate test reports.

Moreover, you can benefit from additional libraries that come together with code samples and abstractions for easier 
use. For example, UI testing is greatly simplified thanks to _automatic_ comparison of screenshots 
of user interface. For that, 
[aShot](https://github.com/sysgears/selenium-automation-bundle/blob/docs/docs/OVERVIEW.md#taking-and-comparing-screenshots) 
was integrated into the bundle. Further down, we'll explain more about created abstractions and tools. 

We'll also provide links to resources where you can read more about each tool.
Naturally, if you’re familiar with any library mentioned below, you may skip the information about it. 

### Core Stack

#### Selenium

You may consider Selenium a medium between the browser and your web application. Selenium lets you handle browsers when 
testing your app. We recommend that you refresh your memory about 
[Selenium WebDriver](https://www.seleniumhq.org/docs/03_webdriver.jsp). You can also consult 
[a guide](https://wiki.saucelabs.com/display/DOCS/Getting+Started+with+Selenium+for+Automated+Website+Testing) 
about the main Selenium concepts.

#### Selenide

Selenide is a framework built around Selenium, and it provides simple methods to find and manipulate elements on an 
HTML page, and, more importantly, to handle pages that _change dynamically_. With Selenide, you’ll write less code with 
[than with Selenium](https://github.com/codeborne/selenide/wiki/Selenide-vs-Selenium). 

#### TestNG

TestNG is a testing framework that manages the testing process. You should be familiar with these three aspects of 
TestNG: how to [configure TestNG](http://testng.org/doc/documentation-main.html#testng-xml), 
how to use [its annotations](http://testng.org/doc/documentation-main.html#annotations), and how to create 
[test groups](http://testng.org/doc/documentation-main.html#test-groups). Additionally, you may want to run 
through a [TestNG tutorial](https://www.guru99.com/all-about-testng-and-selenium.html).

#### Allure

Allure can generate HTML-based [reports](https://docs.qameta.io/allure/#_report_structure) with full logs and 
timeline to help inspect test results presented in suites or diagrams. Additionally, Allure can serve reports to your
favorite browser and work with various Continuous Integration systems.

#### Groovy

Groovy is a programming language for the Java platform, and it features 
[a simpler syntax than Java](http://groovy-lang.org/differences.html). 
Although the Selenium Automation Bundle is written in Groovy, you can still use Java as the main language to create page 
objects and test classes, or write any additional code.

### Other Libraries and Abstractions

#### Taking and Comparing Screenshots

UI testing can be complicated, but Selenium Automation Bundle makes it easy by including the 
[aShot](https://github.com/yandex-qatools/ashot) library. Although Selenium also allows you to take screenshots of UI, 
its functionality is rather limited. aShot, on the other hand, can take screenshots of elements, viewports, or pages, 
and highlight elements. The bundle provides custom methods to seamlessly incorporate screenshot comparison with aShot 
directly into your tests.

#### Communicating with REST API

The bundle defines a few convenient methods which you can use to intercept and send HTTP requests. You can easily tap 
into the network traffic between the client code and the server in order to, for example, verify if correct requests 
were sent by the client.

#### Connecting to Cloud Storage

The Selenium Automation Bundle comes with its own methods and commands to let you move test data, for example, page 
screenshots and video recordings, to and from cloud storage such as Dropbox.
 
#### Working with MongoDB

The bundle implements methods and commands so you can work with MongoDB directly from your tests. For example, the 
methods can be used to verify if the app state, stored in MongoDB, was changed as expected after running the tests.
[MongoDB Java Driver](http://mongodb.github.io/mongo-java-driver/3.8/driver/getting-started/quick-start/) 
is used for such functionality.

#### Video Recording

Video Recorder is a simple library sometimes necessary to debug tests. When some of your tests fail, it can be quite 
difficult to figure out why that happened. But with Video Recorder, you can record all the dynamic changes on the page 
during testing. Video Recorder has [a simple API for TestNG](http://automation-remarks.com/video-recorder-java/#_testng)
which you may have a look at.

________________________________________________________________________________________________________________________

Now that you’re familiar with the technology stack and patterns used in the Selenium Automation Bundle, you can go to 
this short introduction to start writing your first tests, or you can consult an advanced guide where we review testing
process in greater details.