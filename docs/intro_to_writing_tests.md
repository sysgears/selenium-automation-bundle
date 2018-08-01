# Introduction to Writing Tests with the Selenium Automation Bundle

In this short guide, you'll learn how a simple test is created. Just follow the steps below to write and run your first 
test with the Selenium Automation Bundle.

You're going to write a test for a
[TodoMVC application (written in JavaScript)](http://todomvc.com/examples/vanilla-es6/).

## STEP 1. Set the URL for the Web Page Under Test

Change the base URL in the global application properties to the URL of the web page that you're going to test. You need
to change the `ApplicationProperties.groovy` file, which is located in the `./src/main/resources/config/` directory.

Set the URL to `"http://todomvc.com/"` as shown in the example below:

```groovy
package config

// global configuration

baseUrl = "http://todomvc.com/"  // the URL to website or web page under test
// Leave other application properties unchanged
```

You don't need to change any other properties in `ApplicationProperties.groovy`.

## STEP 2. Create a Page Object

To work with the web pages under test, you need to create page objects for them. Page objects are used to access and
modify HTML elements on a web page.

Create a new page object class called `TodoListPage` and save it to the `./src/main/.../pagemodel` directory. Then, add
this code into the file:

```groovy
package com.sysgears.seleniumbundle.pagemodel

import com.codeborne.selenide.CollectionCondition
import com.codeborne.selenide.ElementsCollection
import com.codeborne.selenide.SelenideElement
import com.sysgears.seleniumbundle.core.pagemodel.AbstractPage
import com.sysgears.seleniumbundle.core.pagemodel.annotations.StaticElement
import io.qameta.allure.Step

import static com.codeborne.selenide.Selenide.$
import static com.codeborne.selenide.Selenide.$$

class TodoListPage extends AbstractPage<TodoListPage> {

    @StaticElement
    private SelenideElement taskInputField = $(".new-todo")
    private ElementsCollection tasks = $$(".todo-list li")

    TodoListPage() {
        this.url = "http://todomvc.com/examples/vanilla-es6/"
    }

    @Step("Create a new task")
    TodoListPage createNewTask(String task) {
        taskInputField.setValue(task).pressEnter()
        this
    }

    @Step("Check the length of the task list")
    TodoListPage hasLength(int expectedLength) {
        tasks.shouldHaveSize(expectedLength)
        this
    }

    @Step("Check that the specified task was added")
    TodoListPage hasTask(String taskName) {
        tasks.shouldHave(CollectionCondition.texts(taskName))
        this
    }
}
```

The main page object that'll handle your entire HTML page should inherit the `AbstractPage` class. (If you create
other page objects to manage separate, reusable components on an HTML page, these page objects usually don't inherit
`AbstractPage`).

Also set the absolute URL to `http://todomvc.com/examples/vanilla-es6/` for the test. You can instead set `this.url` to
`examples/vanilla-es6`, which will be concatenated with the base URL `http://todomvc.com/`.

## STEP 3. Create a Test for Page Object

After creating a page object, you can write a test for it. Your test will use page object methods to access an HTML
element, modify it, and check the results.

Create a new file `TodoListPageTest.groovy` and save it to the directory `./src/test/.../tests/todo_list/`. Add the
following code into the file:

```groovy
package com.sysgears.seleniumbundle.tests.todo_list

import com.sysgears.seleniumbundle.common.FunctionalTest
import com.sysgears.seleniumbundle.pagemodel.TodoListPage
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

class TodoListPageTest extends FunctionalTest {

    protected TodoListPage todoListPage

    @BeforeMethod
    void startApplication() {
        todoListPage = new TodoListPage().open().waitForPageToLoadElements()
    }

    @Test
    void verifyTaskText() {
        def taskText = "wash my car"

        todoListPage
                .hasLength(0)
                .createNewTask(taskText)
                .hasLength(1)
                .hasTask(taskText)
    }
}
```

This simple test checks if the todo list has the task that was created. Note that this is a functional test, so it 
should inherit the `FunctionalTest` class provided by the bundle.

## STEP 4. Update TestNG Configurations

Before you can run the newly created test, you need to change the TestNG configurations. Update the `testng.xml` file as 
shown in the example (`testng.xml` is located in the `./src/test/resources/` directory):

```xml
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd" >

<suite name="TodoListSuite">
    <test name="TodoListTest">
        <classes>
            <class name="com.sysgears.seleniumbundle.tests.todo_list.TodoListPageTest"/>
        </classes>
    </test>
</suite>
```

This setting will tell TestNG what test class to run.

## STEP 5. Run the Test

Now just run the test with the following command:

```bash
$ ./gradlew clean test allureServe
```

> If a report wasn't created, you may need to download Allure with `./gradlew downloadAllure`. Then, you can run 
`./gradlew clean test allureServe` again to generate a report.

The Gradle Wrapper will remove the files from the `build/` directory, run the `TodoListPageTest` test, and generate a 
report with `allureServe`. Lastly, your default browser will open and you'll see that your test successfully passed.

___

That's how you can work with the Selenium Automation Bundle. If you need more information about the workflow and
concepts, you can have a look at an
[advanced guide](https://github.com/sysgears/selenium-automation-bundle/blob/docs/docs/detailed_guide_to_writing_tests.md)
where we review the demo tests and explain in more details how the bundle works.
