package com.sysgears.seleniumbundle.common

import com.automation.remarks.testng.VideoListener
import com.codeborne.selenide.Configuration
import com.codeborne.selenide.WebDriverRunner
import com.sysgears.seleniumbundle.core.conf.Config
import com.sysgears.seleniumbundle.core.data.DataMapper
import com.sysgears.seleniumbundle.core.webdriver.DriverInitializer
import org.testng.annotations.*

import static com.codeborne.selenide.Selenide.*
import static com.codeborne.selenide.WebDriverRunner.clearBrowserCache

/**
 * The main configuration class for tests execution. Sets global properties, initializes WebDriver, configures Selenide,
 * handles common pre-/post-conditions.
 */
@Listeners([VideoListener.class])
class BaseTest {

    /**
     * Project properties.
     */
    protected Config conf = Config.instance

    /**
     * Test data mapper, is responsible for test data preparation for TestNG data providers.
     */
    protected DataMapper mapper = new DataMapper()

    /**
     *  OS that is used for test execution.
     */
    protected String os

    /**
     * Browser name.
     */
    protected String browser

    /**
     * Sets Selenide global configuration properties. Static configuration of Selenide is thread safe. They are set as a
     * global parameters per suite before starting parallel threads.
     */
    @BeforeSuite(alwaysRun = true)
    void initSelenideConfiguration() {
        System.setProperty("wdm.targetPath", "${System.getProperty("user.dir")}/src/test/resources/webdrivers")
        Configuration.baseUrl = conf.baseUrl // sets base url for the application under the test
        Configuration.screenshots = false // disables screenshot for failed tests
        Configuration.savePageSource = false // prevents page source saving for failed tests
    }

    @BeforeClass(alwaysRun = true)
    @Parameters(["platform", "browser"])
    void setupGlobalParameters(@Optional String platform, @Optional String browser) {
        this.os = platform ?: conf.os // falls back to globally configured value
        this.browser = browser ?: conf.browser // falls back to globally configured value
    }

    /**
     * Driver initialization is thread safe as it is initialized per thread / class.
     */
    @BeforeClass(alwaysRun = true, dependsOnMethods = "setupGlobalParameters")
    void initSelenideWebDriverRunner() {
        def driver = !conf.gridUrl ?
                DriverInitializer.createDriver(browser) :
                DriverInitializer.createRemoteDriver(conf.gridUrl, os, browser)
        driver.manage().window().maximize()
        WebDriverRunner.setWebDriver(driver)
    }

    @AfterClass(alwaysRun = true)
    void closeWebDriver() {
        WebDriverRunner.closeWebDriver()
    }

    /**
     * Handles test method post-conditions, cleans browser state before the next execution.
     */
    @AfterMethod(alwaysRun = true)
    void logout() {
        clearBrowserCache()
        clearBrowserCookies()
        clearBrowserLocalStorage()
        executeJavaScript("sessionStorage.clear();")
    }
}