# Introduction to User Interface (Layout) Testing with Selenium Automation Bundle

Selenium Automation Bundle provides a simple way to test a web application's user interface (UI). To test the UI, your
first step is to take screenshots of the initial application layout (the initial layout must function properly and look
fine). The second step would be to run the same UI tests again _after_ the UI has changes (for example, the front-end
developers modified the CSS rules).

When running your UI tests after the layout modifications, the bundle will take _new_ screenshots, compare the
_initial_ and _new_ screenshots, and create new images with highlighted differences between them. Finally, your task is
to view the report with attached screenshots, visually validate what has changed, and report bugs if any.

You can also consult the [UI Testing Concept] guide for more information about the general approach to testing the user
interface. In this guide, you'll run the demo UI test to get the feeling how UI testing is done with the bundle.

## Demo User Interface Test

> You may want to first read the [introduction] or the [general guide] to writing tests with Selenium Automation Bundle.

You'll run the demo UI tests. The respective files for the demo UI tests are the following:

* `UITestExample`, a test class located in `src/test/.../tests/ui/`;
* `GooglePage`, a page object located in `src/main/.../seleniumbundle/pagemodel/`.

If you want to learn in more details how the demo UI test class works, consult the [detailed UI testing guide].

### Change TestNG Configuration File to Run UI Test

You need to change the settings in the `testng.xml` configuration file to run just the demo UI test. Open the
`src/test/resources/testng.xml` file and replace the default configuration with the configuration below:

```xml
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd" >

<suite name="Suite">
    <test name="First UI Test">
        <classes>
            <class name="com.sysgears.seleniumbundle.tests.ui.UITestExample"/>
        </classes>
    </test>
</suite>
```

### Create Baseline Screenshots

When testing the user interface with Selenium Automation Bundle, you need to create the baseline screenshots that will
be used for comparison:

```bash
./gradlew -Dtest.baselineMode=true
```

You can learn more about the baseline mode &ndash; the `test.baselineMode` property &ndash; in the [Baseline Mode]
section.

The bundle will run `UITestExample`. At this point, no comparison is made: the bundle only takes screenshots of the
current layout of the Google search page.

Once the test execution is completed, you can find the taken screenshots in `src/test/resources/uicomparison/your_os/chrome`
(instead of `your_os/`, the bundle will create a directory `linux/`, `windows/`, or `mac/` depending on the operating
system you're using).

You can also generate an Allure report with attached baseline screenshots:

```bash
./gradlew allureServe
```

**NOTE**: If you see the error `Cannot find allure commandline`, you need to download Allure and generate the report
with the command `./gradlew downloadAllure allureServe`. Learn more about report generation in the [Reports] guide.

An Allure report with the UI tests:

<p align="center">
    <img src="./images/selenium-automation-bundle-demo-ui-test-baseline-mode.png"
         alt="Selenium Automation Bundle - report with screenshots after running first UI test in baseline mode" />
</p>

### Run the UI Test with UI Comparison

After you've created the baseline screenshots, it's time to actually execute the test to find the differences between
the layouts. As we've already mentioned, the bundle can take screenshots of the application and automatically compare
them to the baseline screenshots. Just run the UI test with the following command:

```bash
./gradlew
```

In the command line, you'll see that one demo UI test has failed. That's okay as one test in the `UITestExample` class
is _intended_ to fail:

```bash
Starting a Gradle Daemon (subsequent builds will be faster)

> Task :test

Suite > First UI Test > com.sysgears.seleniumbundle.tests.ui.UITestExample.checkWithChangesInLayout FAILED
    java.lang.AssertionError at UITestExample.groovy:42

2 tests completed, 1 failed


FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':test'.
> There were failing tests. See the report at: file:///home/sviatoslav/projects/selenium-automation-bundle/build/reports/tests/test/index.html

* Try:
Run with --stacktrace option to get the stack trace. Run with --info or --debug option to get more log output. Run with --scan to get full insights.

* Get more help at https://help.gradle.org

Deprecated Gradle features were used in this build, making it incompatible with Gradle 5.0.
See https://docs.gradle.org/4.6/userguide/command_line_interface.html#sec:command_line_warnings

BUILD FAILED in 24s
6 actionable tasks: 6 executed
```

You can now generate a report to view the screenshots of the test. Run:

```bash
./gradlew allureServe
```

You default browser will open with the report. You can open the Suites tab and navigate to the test that failed:

<p align="center">
    <img src="./images/selenium-automation-bundle-demo-ui-test-screenshots-dont-match.png"
         alt="Selenium Automation Bundle - Failed UI test, screenshots don't match" />
</p>

Again, if you want to see the demo UI test code with an explanation how UI testing works, consult the [detailed UI
testing guide].

[detailed UI testing guide]: https://github.com/sysgears/selenium-automation-bundle/blob/docs/docs/fundamentals/UI%20Testing/Detailed%20Guide%20on%20UI%20Testing.md
[introduction]: https://github.com/sysgears/selenium-automation-bundle/blob/docs/docs/Introduction%20to%20Writing%20Tests.md
[general guide]: https://github.com/sysgears/selenium-automation-bundle/blob/docs/docs/fundamentals/Writing%20Tests.md
[page object]: https://github.com/sysgears/selenium-automation-bundle/blob/docs/docs/fundamentals/Writing%20Tests.md#creating-a-page-object
[test classes]: https://github.com/sysgears/selenium-automation-bundle/blob/docs/docs/fundamentals/Writing%20Tests.md#creating-a-test
[baseline mode]: https://github.com/sysgears/selenium-automation-bundle/blob/docs/docs/fundamentals/UI%20Testing/Baseline%20Mode.md
[reports]: https://github.com/sysgears/selenium-automation-bundle/blob/docs/docs/fundamentals/Reporting.md
