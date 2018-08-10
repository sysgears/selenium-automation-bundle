package config

// global configuration

baseUrl = "https://google.co.uk"  // the URL to website under the test
os = System.getProperty("os.name").split(/\s/).first().toLowerCase() // OS identifier to configure environment
remoteUrl = System.getProperty("test.remoteUrl") ?: null                     // a link to Selenium Grid

browser {
    name = "chrome" // default browser to execute tests (chrome, firefox, microsoftedge, headless)
    width = "1920"
    height = "1080"
    version = ""
}

// ui tests configuration
ui {
    path {
        baseline = "src/test/resources/uicomparison/baseline" // baseline screenshots
        actual = "build/reports/tests/uicomparison/actual" // new screenshots
        difference = "build/reports/tests/uicomparison/difference" // diff images
    }
    ignoredElements = "src/test/resources/ignored_elements.yml" // a list of ignored elements for page objects
}

// mongodb configuration
mongodb {
    dbName = "testdb"
    host = "localhost"
    port = "27017"
    auth {
        username = "enter username"
        password = "enter user password"
        authDb = "enter authentication database name" //database with authentication records
    }
    dumpPath = "src/test/resources/data/dump" // path to root dump folder
}