# Creating Baseline Screenshots

When you're using Selenium Automation Bundle for user interface testing, you'll be creating baseline screenshots of the
application layouts for comparison. In this section, we'll discuss various important aspects for creating baseline
screenshots.

The baseline screenshots are saved under the directory `test/resources/uicomparison/baseline/`, which is added
automatically. Note that the bundle will create additional directories inside `baseline/` consistently with the
environment. In other words, if you run the UI tests under Ubuntu in Chrome, the baseline screenshots will be put into
the directory `baseline/linux/chrome/`.

You can change the path where baseline screenshots will be saved in the `ApplicationProperties.groovy` file:

```groovy
// ui tests configuration
ui {
    path {
        baseline = "src/test/resources/uicomparison/baseline" // Change the path for baseline screenshots if necessary
        actual = "build/reports/tests/uicomparison/actual" // new screenshots
        difference = "build/reports/tests/uicomparison/difference" // diff images
    }
    ignoredElements = "src/test/resources/ignored_elements.yml" // a list of ignored elements for page objects
}
```

## General Considerations of Running the UI Tests in Baseline Mode

Here are several considerations that you must take into account when creating the baseline screenshots:

* The baseline screenshots must be taken for a tested layout with no bugs. We recommend you to manually test the UI
before creating the baseline screenshots.
* You must take different baseline screenshots for different environments.
* You need to call the `compareLayout()` method on the page objects in your UI test classes so that the bundle knows
when to take the screenshots.
* Avoid running the UI tests in baseline mode more than once.
* If new application pages were created, run the UI tests in baseline mode _only for the new pages_. You can set the
necessary test classes for new pages in `testng.xml`.

We also advise against running all the UI test in baseline mode each time because the bundle will replace the previous
baseline screenshots. Therefore, you may unintentionally replace the baseline screenshots that have the incorrect or
broken layouts, and in the next runs, the bundle will compare the application screens with those layouts.

Also note that if you don't create the baseline screenshots before running UI tests for comparison, the tests that run
`compareLayout()` will fail. See the screenshot below:

${img (location:"../../images/selenium-automation-bundle-failed-ui-test-no-baseline-screenshot.png",
        alt:"Selenium Automation Bundle UI Testing - Failed Test")}

## Set Baseline Mode via the Command Line (recommended)

You can set the baseline mode in the [global application properties](#set-baseline-mode-in-application-properties) or
via the command line when you run the tests. By default, this property is set to `false`.

You can set the baseline mode when running the tests with Gradle:

```bash
./gradlew -Dtest.baselineMode=true
```

**NOTE**: If you set the baseline property in the global application properties _and_ via the command line, the value
set via the command line will overwrite the value in the global properties.

## Set Baseline Mode in Application Properties

Open the `ApplicationProperties.groovy` file, which is located in the `src/main/resources/config/` directory, and add
the `baselineMode` property:

```groovy
package config

// global configuration

baseUrl = "https://google.co.uk"  // the URL to website under the test
os = System.getProperty("os.name").split(/\s/).first().toLowerCase() // OS identifier to configure environment
remoteUrl = System.getProperty("test.remoteUrl") ?: null // a link to Selenium Grid
// Add baselineMode
baselineMode = true

// other properties
```

After setting `baselineMode`, you can run the tests with `./gradlew` or `./gradlew clean test allureServe`. The latter
command will also create a report and serve it to the browser.

**NOTE**: We advise against enabling the baseline mode in `ApplicationProperties.groovy` because you may accidentally
overwrite the baseline screenshots for all the application pages. Instead, you should enable this mode via the
[command line](#set-baselinemode-via-the-command-line) when it’s _definitely necessary_ to create the baseline
screenshots.

As an additional caution, if a new application page is created, and you've written UI tests for that new page, you
should enable the baseline mode for that page only in order not to overwrite the baseline screenshots for other pages.

To run the baseline mode only for the new page, specify the test file to run in `testng.xml` or your TestNG
configuration file. Once you've created the baseline screenshots for the new page, you can run the UI tests without the
baseline mode to verify any changes with the layouts.