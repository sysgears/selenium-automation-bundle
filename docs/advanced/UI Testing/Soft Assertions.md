# Soft Assertions

This section of UI Testing discusses the soft assertions mechanism, a feature that makes testing complicated test
scenarios simpler.

In complex test scenarios that include several steps of interaction with the user interface, you can use the soft
assertions. This mechanism is available through the base class `UITest`, which your concrete UI test classes should
inherit.

For the implementation of the `SoftAssert` class, look for `src/main/.../core/utils/SoftAssert.groovy` directory.

## How Do Soft Assertions Work?

Consider this test scenario:

1. Open an HTML page. Take a screenshot of the entire page.
2. Open the side panel on this page. Take a screenshot of the page with the side panel.
3. Click on a popup to subscribe. Take a screenshot of the popup.

You _might_ write three separate tests, open the same page three times, and perform necessary actions while taking
screenshots at each step for comparison. But, as you well know, repeating the same code in different tests isn't the
best idea as the test code becomes bloated.

You can write only one test to avoid repeating the code, and then you make several assertions in it. But this approach
also creates a problem: When any assertion fails, the other assertions won't even run.

To solve this problem, we offer to use soft assertions in your UI tests. Thanks to soft asserts, when only one assertion
fails, the other _will run_ and may pass successfully. And in the test report, you'll see that the screenshots were
attached only for the failed assertion.