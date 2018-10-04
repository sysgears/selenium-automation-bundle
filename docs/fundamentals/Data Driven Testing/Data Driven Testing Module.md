# Data Driven Testing Module

This section of Selenium Automation Bundle documentations discusses the key components of the `data` module. Here, you
can find more information about the classes you can use in your data driven tests.

## Data Module

The `data` module is located in `src/main/groovy/com/sysgears/seleniumbundle/core/`. The module contains two classes and
three annotations that you'll need to use in your data-driven tests for requesting data.

### DataLoader

`DataLoader` is a custom class that retrieves test data using the `@Locator` parameters.

`DataLoader` provides two methods that you can use in your test classes to request data from Yaml files. Depending on
how your data is structured in a Yaml file, you can get a _map_ using `readMapFromYaml()` or _list_ using
`readListFromYaml()`.

Usage example:

```groovy
// a test method that uses DataLoader methods
```

The `DataLoader` class is intended for use with [`DataMapper`](#datamapper).

### DataMapper

`DataMapper` is the main class that uses the custom annotations to look up the test data and map those data to a
two-dimensional array. `DataLoader` can process test data, find requested data from a Yaml file, and convert those data
into `Object[][]` as required by TestNG [Data Provider annotation].

The mechanics are quite simple: The `DataMapper` class gets data from a YML file, and then `@DataProvider` injects this
data in our tests.

Usage example:

```groovy
// a test method that uses DataMapper
```

### Annotations

#### Find

The `@Find` annotation is necessary to search for necessary data sets and their values that will be used in the test.
You can use `@Find` as follows:

```groovy
@Find(first = "tomato", second = "potato" )
void myMethod() {}
```

To make this annotation work, your Yaml file must contain the fields `first` and `second` with values `tomato` and
`potato`.

#### Locator

The `@Locator` annotation will let you specify the value you need to use in your test. You can pass a string for the
value on the highest level, or a string with dot separator to link to a lower-level value.

Usage example: For example, your Yaml file may have the following structure:

```yml
- query: something on the highest level
  result:
    food: value on a lower level
```

To access the values stored in `query` and `food`, you can use `@Locator` in your test classes like this:

```groovy
void checkSomething(
         @Locator("query") String query,
         @Locator("result.food") String food) {

     pageObject.doSomethingWith(query)
}
```

`@Locator` accepts a string with the name of the property. You can also pass a list of names separated by dots to access
any value on lower levels.

#### Query

You can use the `@Query` annotation before the test class and pass a query according to the structure in your Yaml file.

Usage example:

```yml

```

```groovy
//
@Query(@Find(name = "category", value = "News"))
void myMethod() {}
```

### MapHelper



[data provider annotation]: http://testng.org/doc/documentation-main.html#annotations