package com.sysgears.seleniumbundle.common

import com.automation.remarks.testng.VideoListener
import com.codeborne.selenide.Configuration
import com.codeborne.selenide.WebDriverRunner
import com.codeborne.selenide.commands.Commands
import com.sysgears.seleniumbundle.core.conf.Config
import com.sysgears.seleniumbundle.core.data.DataMapper
import com.sysgears.seleniumbundle.core.proxy.BrowserProxy
import com.sysgears.seleniumbundle.core.selenide.commands.Click
import com.sysgears.seleniumbundle.core.uicomparison.IEnvironment
import com.sysgears.seleniumbundle.core.webdriver.DriverInitializer
import net.lightbody.bmp.BrowserMobProxyServer
import org.openqa.selenium.Dimension
import org.testng.annotations.*

import static com.codeborne.selenide.Selenide.clearBrowserCookies
import static com.codeborne.selenide.Selenide.executeJavaScript
import static com.codeborne.selenide.WebDriverRunner.clearBrowserCache
import static com.sysgears.seleniumbundle.core.webdriver.Driver.HEADLESS
import static com.sysgears.seleniumbundle.core.webdriver.Driver.getDriverType

/**
 * The main configuration class for tests execution. Sets global properties, initializes WebDriver, configures Selenide,
 * and handles common pre- and post-conditions for running tests.
 */
@Listeners([VideoListener.class])
class BaseTest implements IEnvironment {

    /**
     * Project properties.
     */
    protected Config conf = Config.instance

    /**
     * Test data mapper, responsible for preparing test data for TestNG data providers.
     */
    protected DataMapper mapper = new DataMapper()

    /**
     * Instance of BrowserProxy.
     */
    protected BrowserProxy browserProxy

    /**
     * The operating system used for test execution.
     */
    protected String os

    /**
     * Browser.
     */
    protected String browser

    /**
     * Implementation of the {@link IEnvironment} interface.
     *
     * @return os property
     */
    @Override
    String getOs() {
        os
    }

    /**
     * Implementation of the {@link IEnvironment} interface.
     *
     * @return browser property.
     */
    @Override
    String getBrowser() {
        browser
    }

    /**
     * Sets the global configuration parameters for Selenide. They are set per suite before starting
     * parallel threads. The static Selenide configuration is thread safe by default.
     */
    @BeforeSuite(alwaysRun = true)
    void initSelenideConfiguration() {
        System.setProperty("wdm.targetPath", "${System.getProperty("user.dir")}/src/test/resources/webdrivers")
        Configuration.baseUrl = conf.baseUrl // sets the base URL for the application under test
        Configuration.screenshots = false // disables taking screenshots for failed tests
        Configuration.savePageSource = false // prevents saving the page source for failed tests
    }

    /**
     * Replaces the default Selenide command 'click' with the custom implementation.
     * The method is implemented as a workaround for the issue with the method 'click()' in
     * Microsoft Edge.
     */
    @BeforeSuite(alwaysRun = true, dependsOnMethods = "initSelenideConfiguration")
    void customizeSelenideCommands() {
        Commands.getInstance().add("click", new Click())
    }

    @BeforeClass(alwaysRun = true)
    @Parameters(["platform", "browser"])
    void setupGlobalParameters(@Optional String platform, @Optional String browser) {
        this.os = platform ?: conf.os // falls back to the globally configured value
        this.browser = browser ?: conf.browser.name // falls back to the globally configured value
    }

    /**
     * Driver initialization is thread safe because it is initialized per thread and class.
     */
    @BeforeClass(alwaysRun = true, dependsOnMethods = "setupGlobalParameters")
    void initSelenideWebDriverRunner() {

        // Works only for Chrome. Ignored if used with any other browser.
        browserProxy = new BrowserProxy(new BrowserMobProxyServer())

        def driver = !conf.remoteUrl ? DriverInitializer.createDriver(browser, browserProxy.seleniumProxy)
                : DriverInitializer.createRemoteDriver(conf.remoteUrl, os, browser, browserProxy.seleniumProxy)

        def size = new Dimension(conf.browser.width as Integer, conf.browser.height as Integer)
        if (getDriverType(conf.browser.name as String) != HEADLESS) driver.manage().window().setSize(size)

        WebDriverRunner.setWebDriver(driver)
    }

    @AfterClass(alwaysRun = true)
    void closeWebDriver() {
        WebDriverRunner.closeWebDriver()
    }

    /**
     * Handles test method post-conditions and cleans the browser's state before the next execution.
     */
    @AfterMethod(alwaysRun = true)
    void logout() {
        clearBrowserCache()
        clearBrowserCookies()
        //clearBrowserLocalStorage()
        executeJavaScript("sessionStorage.clear();")
    }
}