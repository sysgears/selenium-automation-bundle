# Data Driven Testing Module

In this section, you can find more information about the classes and annotations that Selenium Automation Bundle provides for Data Driven Testing.

## Data Module

The `data` module is located in `src/main/groovy/com/sysgears/seleniumbundle/core/`. This module contains the classes, annotations, and other components that make possible accessing and mapping test data in data driven tests.

### Test Classes Hierarchy for DDT

The data module is represented as a hierarchy of three class: one base class `AbstractTestData` and two child classes `PlainData` and `HierarchicalData`.

#### `AbstractTestData`

The abstract class to be extended by the subclasses that provide concrete implementations for the test data objects and the `map()` method. `AbstractTestData` provides the constructor to create Test Data instances and a couple of methods &mdash; `map()` and `getData()`.

#### `PlainData`

This class represents plain data retrieved from plain data files stored in `.csv`.

#### `HierarchicalData`

This class represents hierarchical data retrieved from `.yml` files.

___

### `DataLoader`

`DataLoader` is a custom class that reads the contents of CSV or YAML files and loads (read: returns) values as `List` or `Map` to the test class.

`DataLoader` provides three methods to request data from CSV or YAML files. Depending on how your data is structured in a YAML file, you can get a _map_ (an associative array `[:]`) using `readMapFromYaml()` or a _list_ using `readListFromYaml()`, and for CSV files, `DataLoader` provides `readListFromPlainDataFile()`.

___

#### `readListFromPlainDataFile()`

A static method that retrieves data as Groovy `List`.

**Parameters**

`filePath` &nbsp;&nbsp;&nbsp; `String` &nbsp;&nbsp;&nbsp; A path to a `.csv` file with test data.

**Returns**

The file contents as Groovy `List` with text split by a pattern specified in `ApplicationProperties.groovy`.

**Usage**

You need to pass the path to a data file in YAML format as the first argument when instantiating the `HierarchicalData` class discussed above.

```groovy
class Tools_PlainData extends FunctionalTest {
  private data = new PlainData(DataLoader.readListFromPlainDataFile("src/test/resources/data/google/test_data.csv",
              conf.data.plain.dataSetSeparator), this.class).data
}
```

Notice that the method uses the separator specified in `ApplicationProperties.groovy`, which is `"\n\n"` by default.

___

#### `readMapFromYml()`

A static method that retrieves data as Groovy `Map` by running a private method `readRawDataFromYml()` in `DataLoader`.

**Parameters**

`file` &nbsp;&nbsp;&nbsp; `String` &nbsp;&nbsp;&nbsp; A path to a `.yml` file with test data.

**Returns**

The file contents as Groovy `Map`.

**Usage**

You need to pass the path to a data file in YAML format as the first argument when instantiating `HierarchicalData`.

```groovy
class Tools_HierarchicalData extends FunctionalTest {
  private data = new HierarchicalData(DataLoader.readMapFromYml("src/test/resources/data/google/test_data.yml"),
              this.class).data
}
```

___

#### `readListFromYaml()`

A static method that retrieves data as Groovy `List` by running a private method `readRawDataFromYml()` in `DataLoader`.

**Parameters**

`file` &nbsp;&nbsp;&nbsp; `String` &nbsp;&nbsp;&nbsp; A path to a `.yml` file with test data.

**Returns**

The file contents as Groovy `List`.

**Usage**

You need to pass the path to a data file in YAML format as the first argument when instantiating the `HierarchicalData` class discussed above.

```groovy
class Tools_HierarchicalData extends FunctionalTest {
  private data = new HierarchicalData(DataLoader.readListFromYml("src/test/resources/data/google/test_data.yml"),
              this.class).data
}
```
___

### Annotations

Selenium Automation Bundle provides three annotations to help you build requests when you store data in YAML files. 

#### `@Query`

You need to use the `@Query` annotation before the test class. `@Query` is always used with [`@Find`](#@find) annotation.

Usage example:

```groovy
@Query(@Find(name = "category", value = "News"))
void myMethod() {}
```

#### `@Find`

The `@Find` annotation is used to get the arguments for the [`@Query` annotation](#@query).

Usage example:

```groovy
@Query(@Find(name = "tomato", value = "potato" ))
void myMethod() {
    myPageObject.doSomethingToTest()
}
```

To make this annotation work, your YAML file must contain the field `tomato` with the value `potato`:

```yaml
- tomato: potato
```

#### `@Locator`

The `@Locator` annotation will let you specify the values you need to use in your test. The annotation accepts a string
with any level of nesting. The nested elements must be separated by a dot.

Usage example: For example, your Yaml file may have the following structure:

```yml
- query: something on the highest level
  result:
    food: value on a lower level
```

To access the values stored in `query` and `food`, you can use `@Locator` in your test classes this way:

```groovy
void checkSomething(@Locator("query") String query, @Locator("result.food") String food) {
     pageObject.doSomethingWith(query).doSomethingAgain(food)
}
```

As you can see, you need to pass only the last nested element to the page object method. `@Locator` will return the 
correct data according to the request.

[data provider]: http://testng.org/doc/documentation-main.html#annotations
[csv-like format]: https://github.com/sysgears/selenium-automation-bundle/blob/master/docs/fundamentals/Data%20Driven%20Testing/Data%20Driven%20Tests%20with%20CSV.md