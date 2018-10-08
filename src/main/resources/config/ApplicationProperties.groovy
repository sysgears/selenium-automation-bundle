package config

// global configurations

baseUrl = "https://google.co.uk"  // the URL to the website under the test
os = System.getProperty("os.name").split(/\s/).first().toLowerCase() // the OS identifier to configure environment
remoteUrl = System.getProperty("test.remoteUrl") ?: null // a link to Selenium Grid

browser {
    name = "chrome" // the browser to execute tests; options: "chrome", "firefox", "MicrosoftEdge", "headless"
    width = "1920"
    height = "1080"
    version = ""
}

// UI tests configuration
ui {
    path {
        baseline = "src/test/resources/uicomparison/baseline" // baseline screenshots
        actual = "build/reports/tests/uicomparison/actual" // new screenshots
        difference = "build/reports/tests/uicomparison/difference" // difference images
    }
    ignoredElements = "src/test/resources/ignored_elements.yml" // a list of ignored elements for page objects
}

// MongoDB configuration
mongodb {
    dbName = "testdb"
    host = "localhost"
    port = "27017"
    auth {
        username = "enter username"
        password = "enter user password"
        authDb = "enter authentication database name" // database with authentication records
    }
    dumpPath = "src/test/resources/data/dump" // path to the root dump folder
}