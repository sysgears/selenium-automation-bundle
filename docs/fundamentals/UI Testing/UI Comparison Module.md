# UI Comparison Module

Selenium Automation Bundle provides the UI Comparison module to simplify creating and running UI (layout) tests. All the
custom code for testing the user interface under the `src/main/.../core/uicomparison/` directory. Further below, you can
find more information about the custom classes and commands available in the bundle.

## UIComparison Trait

The `UIComparison` trait contains the core functionality for UI testing. `UIComparison` provides the following methods
that you'll call on your page objects in tests:

* `compareLayout()` creates the baseline screenshots and compares the actual and base layouts.
* `setIgnoredElements()` allows you to ignore UI elements not used for comparison.
* `setEnvironment()` sets the test environment to run the tests.

### compareLayout Method

The `compareLayout()` method is a **key method** provided by the bundle and is available in your page objects through
the `UIComparison` trait. You'll be using `compareLayout()` extensively to take and compare screenshots of the
application pages.

In short, here's how the `compareLayout()` method works:

* It always takes the actual screenshots of the layout if you run the method in your tests.
* If you run the tests with the [baseline mode](#creating-baseline-screenshots) set to `true`, the method will only take
screenshots and _attach_ them to the test report (no comparison is made).
* If you run the tests with the baseline mode set to `false`, the method will take the actual screenshots and compare
them to the baseline screenshots.
    * If no baseline screenshots were found, your tests will fail and the report will show an error
    `No baseline screenshot found`.
    * If there's a difference between the actual and baseline screenshots, the test will also fail. The method will
    attach three screenshots to the report for each failed test: the baseline, actual, and difference screenshots.
    * If there are no differences between the actual and baseline screenshots, the test will pass successfully.

## AShotService

`AShotService` is a class that implements the [`compareLayout()` method](#comparelayout-method) and creates instances of
aShot.

[aShot] is the core library that the UI module in the bundle is built on. aShot can take screenshots of each application
page, compare them, and highlight the changed element(s). This library uses Selenium under the hood to take screenshots
for comparison. But, more importantly, aShot also _highlights_ the elements or components that differ between the
original and new layouts.

aShot has many configurations, the most notable ones are different shooting strategies that you can define for each
browser and operating system. But we've simplified using aShot to just one method &ndash; `compareLayout()`. All the
configurations are handled for you in `AShotService`. The key aShot configurations are listed below:

* Shooting strategies
* Timeouts before taking a screenshot

## UI Comparison Module Commands

You can find all the information about the UI Comparison module commands in a dedicated guide:

* [Commands]

[commands]: https://github.com/sysgears/selenium-automation-bundle/blob/master/docs/Commands.md