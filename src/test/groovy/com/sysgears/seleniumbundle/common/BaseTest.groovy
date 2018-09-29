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
 * handles common pre-/post-conditions.
 */
@Listeners([VideoListener.class])
class BaseTest implements IEnvironment {

    /**
     * Project properties.
     */
    protected Config conf = Config.instance

    /**
     * Test data mapper, is responsible for test data preparation for TestNG data providers.
     */
    protected DataMapper mapper = new DataMapper()

    /**
     * Instance of BrowserProxy.
     */
    protected BrowserProxy browserProxy

    /**
     *  OS that is used for test execution.
     */
    protected String os

    /**
     * Browser name.
     */
    protected String browser

    /**
     * Implementation of {@link IEnvironment} interface.
     *
     * @return os property
     */
    @Override
    String getOs() {
        os
    }

    /**
     * Implementation of {@link IEnvironment} interface.
     *
     * @return browser property.
     */
    @Override
    String getBrowser() {
        browser
    }

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

    /**
     * Workaround for an issue with method 'click()' in Microsoft Edge. Methods replaces default Selenide command 'click'
     * with our custom implementation.
     */
    @BeforeSuite(alwaysRun = true, dependsOnMethods = "initSelenideConfiguration")
    void customizeSelenideCommands() {
        Commands.getInstance().add("click", new Click())
    }

    @BeforeClass(alwaysRun = true)
    @Parameters(["platform", "browser"])
    void setupGlobalParameters(@Optional String platform, @Optional String browser) {
        this.os = platform ?: conf.os // falls back to globally configured value
        this.browser = browser ?: conf.browser.name // falls back to globally configured value
    }

    /**
     * Driver initialization is thread safe as it is initialized per thread / class.
     */
    @BeforeClass(alwaysRun = true, dependsOnMethods = "setupGlobalParameters")
    void initSelenideWebDriverRunner() {

        // works only with Chrome, if used with any browser except Chrome it will be ignored
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
     * Handles test method post-conditions, cleans browser state before the next execution.
     */
    @AfterMethod(alwaysRun = true)
    void logout() {
        clearBrowserCache()
        clearBrowserCookies()
        //clearBrowserLocalStorage()
        executeJavaScript("sessionStorage.clear();")
    }
}