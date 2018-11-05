# Data Driven Testing Module

In this section, you can find information about the classes and annotations you can use in your data-driven tests.

## Data Module

The `data` module is located in `src/main/groovy/com/sysgears/seleniumbundle/core/`. This module contains two classes 
and three annotations that you'll need to use in your data-driven tests for requesting and mapping data.

### DataLoader

`DataLoader` is a custom class that reads the contents of the YAML files and returns values as List or Map.

`DataLoader` provides two methods that you can use in your test classes to request data from YAML files. Depending on
how your data is structured in a YAML file, you can get a _Map_ using `readMapFromYaml()` or a _List_ using
`readListFromYaml()`.

Usage example (notice that he `DataLoader` class is intended for use with [`DataMapper`](#datamapper)):

```groovy
@DataProvider(name = 'getData')
Object[][] getData(Method m) {
    // use DataLoader static methods to read data from the YAML file
    mapper.map(DataLoader.readListFromYml(YOUR_YAML_FILE), m)
}
```

### DataMapper

`DataMapper` is a custom class that uses the custom annotations to look up the test data and map those data to a
two-dimensional array. `DataMapper` can process test data, find requested data from YAML files, and convert those data
into `Object[][]` as required by TestNG [Data Provider].

You don't have to explicitly instantiate `DataMapper` in your tests. The `DataMapper` instance is created by the base 
test class `BaseTest`. Your actual test gets the `DataMapper` instance through `FunctionalTest`: your actual test should
inherit `FunctionalTest`, which in turn inherits `BaseTest`.

#### map

The `DataMapper` class provides the method `map` that you'll be using in your test classes. `map` accepts two
parameters:

* `List<Map>`, the data retrieved from the YAML file. Use [`DataLoader`] to retrieve the data.
* `m`, the name of the test method that uses the Data Provider method. You can pass just `m` to this parameter.

Usage example:

```groovy
@DataProvider(name = 'getData')
Object[][] getData(Method m) {
    // use mapper, a DataMapper instance to map data from YAML file
    mapper.map(DataLoader.readListFromYml(DATAFILE), m)
}
```

### Annotations

Selenium Automation Bundle provides three annotations to help you build your requests to a YAML file. 

#### @Query

You need to use the `@Query` annotation before the test class. `@Query` is always used with [`@Find`](#@find) annotation.

Usage example:

```groovy
//
@Query(@Find(name = "category", value = "News"))
void myMethod() {}
```

#### @Find

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

#### Locator

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