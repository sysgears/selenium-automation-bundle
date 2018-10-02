# User Interface (Layout) Testing with Selenium Automation Bundle

This section of UI Testing guides gives a high-level overview of the concepts and ideas behind the approach to UI
testing suggested by Selenium Automation Bundle.

## UI Testing vs Layout Testing

Some people consider testing with Selenium as the _user interface testing_.

To make sure that we're in the same boat when talking about the UI testing throughout this guide, consider UI testing as
_testing the HTML layouts of your application_.

## Why Testing the User Interface Is Hard

Think of testing the user interface in the four top browsers &ndash; Chrome, Firefox, Edge, and Safari, and in three
operating systems &ndash; Windows, MacOS, and Linux. Your application has, say, 50 possible states (or screens), and you
test all of them manually.

Then a front-end developer in your team makes "just a few small changes" to the application layouts, and you have to
test the _entire application_ again. To add insult to injury, it's often (if not always) necessary to test those 50
application screens for different media queries and in the mobile version of the application.

**Now you must be feeling like _Anger_ from Inside Out: Regression testing can be really aggravating.**

To cut long story short, UI testing takes too much time and efforts of a QA specialist. With Selenium Automation Bundle,
however, testing the user interface is made simple (or so we think).

But how can we automate UI testing and make regression testing a breeze? AI, although developing at a fast pace, still
can't be helpful for regression testing. AI won't be able to decide whether the layout changes are valid or not, and
whether the application's _Look & Feel_ is positive.

There are ways to improve the workflow when testing the user interface. One approach is to write specifications using a
dedicated framework. But we don't think that it's really efficient and convenient to support yet another codebase.

The other idea is to take screenshots of the original and updated application's layouts and then compare those
screenshots **_automatically_** by highlighting the differences. Then, the task will be to review the marked differences
between the old and new layouts and report bugs if some layout is broken.

We've decided to stick to this last idea as it adds zero overhead to regression testing. And a great library called
[aShot] helped us to realize the approach.

Let's now review the UI testing flow with Selenium Automation Bundle.

### UI Testing Flow

This is how you test the layouts with the bundle:

1. Take baseline screenshots of the original UI. The application's original (initial) layout must look all right and
function correctly before you take any baseline screenshots.
2. Run your UI tests _after_ the UI was changed by the front-end team.
3. Generate an [Allure test report]; three types of screenshots for failed tests will be attached to the report.
4. View the screenshots for the failed tests to see if the UI changes were correct, and:
    * Replace the baseline screenshots with the new ones if the changes are correct; or
    * Report a bug if a layout is broken or the changes affected the layouts that the developer didn't work on.

To better explain the flow, let's discuss a real life example.

Let's assume your application has 50 possible layouts, and you've taken 50 baseline screenshots with Selenium Automation
Bundle.

In two weeks, a front-end engineer in your team updates 15 application screens out of 50. Then, after you run the UI
tests again, the bundle marks only 15 screenshots for those exact screens. In this case, the UI tests for the 15 updated
screens will always _fail_ because the layouts are different, but the test run is considered _successful_.

However, if the UI tests fail for fewer or more application screens, or for the screens that shouldn't have changed,
then the test run is considered _unsuccessful_, and you need to report the found bugs to the front-end team.

When bugs are removed, you run the UI tests again. If only 15 UI tests fail for the correct 15 application screens, then
the changes were valid, and you can replace the 15 base (original) screenshots with the 15 new (actual) screenshots
because the latter have the newer layout. (Screenshot replacement is done with a simple command [updateScreenshots].)

You may have noticed that one bizarre aspect of UI testing with our bundle: How can a test run be considered successful
if the tests have failed? In the section [Why UI Tests "Should" Fail](#why-ui-tests-"should"-fail), we'll explain this
uncommon approach.

### Why UI Tests "Should" Fail

In general, when you write automated tests, you don't want them to fail. If they do fail, it's necessary to re-write the
application logic or test code to make the bugs go away. But that's not how UI testing is done with the bundle.

In fact, UI testing with Selenium Automation Bundle is done this way: **If a UI test fails, then there was a change in a
layout, and you need to inspect that layout.**

The failed tests will explicitly show you what application pages or components were modified or were affected by
modifications of other pages or components.

Thanks to this approach, it's much easier to find the application pages that shouldn't have changed, as your task
becomes utterly simple: You only need to review the application screenshots that the bundle created. Even better, you
won't struggle _searching_ the differences between the application layouts because all the differences will be
graciously highlighted by the aShot library.

Therefore, it's okay when the tests fail for the redesigned layouts: This approach simplifies finding any
irregularities with the layouts, and you'll notice the bugs and changes really quickly.

### Types of Screenshots

Selenium Automation Bundle creates three types of screenshots: _baseline_, _actual_, and _difference_ screenshots.

Here's how the screenshots are divided by type:

* _Baseline_ screenshots of the initial application layout;
* _Actual_ screenshots of the current application layout; and
* _Difference_ screenshots with marked elements.

It's necessary to create _baseline_ screenshots only once for each page. Note that the pages must be tested and look
fine before you create any baseline screenshots.

The _actual_ screenshots are created each time you run the UI tests. If there are failed tests, then the _actual_
screenshots are taken and stored to the project. And if you run the UI tests in [baseline mode], then the actual
screenshots are saved as baseline.

The _difference_ screenshots are only created when the tests failed. aShot creates diff screenshots by comparing the
_baseline_ and _actual_ screenshots and marking any differences.

Each type of the screenshot is stored in their own directory. You can set the paths to the directories in
`src/main/resources/config/ApplicationProperties.groovy`:

```groovy
// ui tests configuration
ui {
    path {
        baseline = "src/test/resources/uicomparison/baseline" // baseline screenshots
        actual = "build/reports/tests/uicomparison/actual" // new screenshots
        difference = "build/reports/tests/uicomparison/difference" // diff images
    }
    ignoredElements = "src/test/resources/ignored_elements.yml" // a list of ignored elements for page objects
}
```

## UI Comparison Module

Selenium Automation Bundle contains all the custom code for testing the user interface under the
`src/main/.../core/uicomparison/` directory.

You can find more information about the custom classes and commands available in the bundle in a dedicated guide:

* [UI Comparison Module]

## Demo Test Classes and Page Objects

Selenium Automation Bundle comes with several page objects and test classes to demonstrate how you can write UI
tests. The [introduction to writing UI testing] discussed a very basic UI test.

You can follow to the dedicated guides that discuss more advanced aspects of UI testing such as hiding and ignoring
elements:

* [Hiding Elements in UI Tests]
* [Ignoring Elements in UI Tests]

## General Considerations for Writing Page Models for UI Tests

When you create page objects for UI testing with Selenium Automation Bundle, you should always make sure that your page
objects:

* Inherit `AbstractPage`, and
* Implement `UIComparison`.

Thanks to `AbstractPage` and `UIComparison`, you'll be able to call several useful methods on your page objects in the
UI tests:

* You can take screenshots and analyze them by running `compareLayout()`, which is available through `UIComparison`.
* You can ignore certain HTML elements with `setIgnoredElements()`, which is available through `UIComparison`.
* You can hide an HTML element or elements with `hideElements()` implemented in `AbstractPage`.

## General Considerations for Writing Test Classes for UI Tests

When you create test classes for UI testing with Selenium Automation Bundle, your test classes should always:

* Inherit `UITest`.

Thanks to `UITest`, your test class can use the [soft assertions] mechanism.

[updateScreenshots]: https://github.com/sysgears/selenium-automation-bundle/blob/docs/docs/Commands.md
[baseline mode]: https://github.com/sysgears/selenium-automation-bundle/blob/docs/docs/advanced/Baseline-Mode.md
[ui comparison module]: https://github.com/sysgears/selenium-automation-bundle/blob/docs/docs/advanced/UI-Comparison-Module.md
[introduction to writing ui testing]: https://github.com/sysgears/selenium-automation-bundle/blob/docs/docs/Introduction-to-UI-Testing.md
[hiding elements in ui tests]: https://github.com/sysgears/selenium-automation-bundle/blob/docs/docs/advanced/UI-Testing/Hiding-Elements-in-UI-Tests.md
[ignoring elements in ui tests]: https://github.com/sysgears/selenium-automation-bundle/blob/docs/docs/advanced/UI-Testing/Ignoring-Elements-in-UI-Tests.md
[soft assertions]: https://github.com/sysgears/selenium-automation-bundle/blob/docs/docs/advanced/UI-Testing/Soft-Assertions.md