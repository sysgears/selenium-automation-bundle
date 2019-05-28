# How to Structure Test Data in YAML and CSV Files

This guide explains how to structure test data in CSV and YAML with Selenium Automation Bundle.

## Structuring test data in CSV

The CSV-like structure provides a readable and convenient way to handle test data. Take a look at the example below:

```csv
method: checkUrlParameterChanges
QUERY                            | CATEGORY | RESULT.URL.PARAMS
google                           | Images   | [tbm:isch, q:google]
bing                             | News     | [tbm:nws, q:bing]
```

> The example is taken from the demo test data file `src/test/resources/data/google/test_data.csv`. 

This structure can be read as follows:

* The test method is `checkUrlParameterChanges()`.
* The column `QUERY` holds the search queries.
* The column `CATEGORY` holds the possible category value on the search results page.
* The column `RESULT.URL.PARAMS` holds the array of tested parameters that might be available in the URL.

The structure of your CSV files with test must follow these guidelines:

1. Each data set for a particular test method starts with the string `method: ` followed by the method name:

```csv
method: yourTestMethod
```

**NOTE**: Always divide the part `method:` from the method name with a space.

2. After the method name goes the table head with the names of the parameters to be used in a test. Separate the table
cells with the pipe `|`:

```csv
QUERY | CATEGORY | RESULT.URL.PARAMS
```

**NOTE**: Capitalizing the parameters in the table head is optional.

In our demo tests, the `QUERY`, `CATEGORY`, and `PARAMS` are automatically mapped to the parameters list in the test method, for example:

```groovy
def checkUrlParameterChange(String query, String category, Map params)
```

3. Each dataset to be used in the test method must span on its own line. The data in the dataset must be divided by the pipe respective to the column:

```csv
QUERY  | CATEGORY | SUBCATEGORY | RESULT.QUERY.PARAMS
kitten | Images   | fluffy      | [q:kitten, tbm:isch]
```

4. The data to be tested can be presented as a map, for example, `[data1, data2, data3]` or as list of comma-separated values.

### Configuring delimiters for CSV files
 
You can change the delimiters for data in `ApplicationProperties.groovy`. Have a look at the following configuration with defaults:

```groovy
data {
  plain {
    dataSetSeparator = "\n\n"
    delimiter = "\\|" // do not use comma as it is reserved for separating values for List and Map
    hasHeader = true // should be set to true in case plain data files contain header
  }
}
```

## Structuring test data in YAML

The YAML-like structure provides a tree-like view of your test data. Here's an example:

```yaml
- query: google
  category: Images
  result:
    url:
      params:
        tbm: isch
        q: google
    page_elements:
      tools:
        - Size
        - Color
        - Usage rights
        - Type
        - Time
        - More tools

- query: bing
  category: News
  result:
    url:
      params:
        tbm: nws
        q: bing
    page_elements:
      tools:
        - All news
        - Recent
        - Sorted by relevance
```

> The example is taken from the demo test data file `src/test/resources/data/google/test_data.yml`.

The data is structured similarly to how you would structure an object you might have used for the tests:

```
{
  query: "google",
  category: "Images",
  result: {
    url: {
      params: {
        tbm: "isch",
        q: "google"
      }
    },
    page_elements: {
      tools: ["Size", "Color", "Usage rights", "Type", "Time", "More tools"]
    }
  }
}
```

Follow these guidelines when creating YAML files with test data:

* Each new dataset in YAML must start with a dash `-`. 
* The key-value pairs are divided by a colon `:`.
* The lists or maps of multiple values start with the dash `-` on a new line

As you can see, it's very easy to create test data files with Selenium Automation Bundle.