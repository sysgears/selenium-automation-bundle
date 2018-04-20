package com.sysgears.seleniumbundle.core.webdriver

import org.openqa.selenium.Platform
import org.openqa.selenium.Proxy
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.CapabilityType
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver

/**
 * Provides driver instance.
 */
class DriverInitializer {

    /**
     * Configures and instantiates WebDriver for local test execution.
     *
     * @param browser browser, e.g. chrome, firefox, microsoftedge, headless
     *
     * @return WebDriver instance
     */
    static WebDriver createDriver(String browser) {
        Driver.getDriver(browser).getWebDriverObject()
    }

    /**
     * Configures and instantiates WebDriver for ChromeDriver emulation mode.
     *
     * @param device name of the mobile device, e.g. 
     *
     * @return WebDriver instance
     */
    static WebDriver createMobileDriver(String device) {
        Driver.CHROME.getWebDriverObject(prepareMobileCapabilities(device))
    }

    /**
     * Configures and instantiates WebDriver for local test execution with given proxy.
     *
     * @param proxy instance of proxy
     *
     * @return WebDriver instance
     */
    static WebDriver createChromeWithProxy(Proxy proxy) {
        def capabilities = new DesiredCapabilities()
        capabilities.setCapability(CapabilityType.PROXY, proxy)

        Driver.CHROME.getWebDriverObject(capabilities)
    }

    /**
     * Configures and instantiates WebDriver for remote test execution.
     *
     * @param gridUrl url of GridHub
     * @param platform platform on which the tests should be run, e.g. linux, windows
     * @param browser browser, e.g. chrome, firefox, microsoftedge, headless
     *
     * @return WebDriver instance
     */
    static WebDriver createRemoteDriver(String gridUrl, String platform, String browser) {
        new RemoteWebDriver(URI.create(gridUrl).toURL(), prepareCapabilities(platform, browser))
    }

    /**
     * Prepares capabilities for WebDriver.
     *
     * @param platform platform on which the tests should be run, e.g. linux, mac, windows
     * @param browser name of the browser
     *
     * @return DesiredCapabilities instance
     */
    static private DesiredCapabilities prepareCapabilities(String platform, String browser) {
        new DesiredCapabilities(browser, "", Platform.fromString(platform))
    }

    /**
     * Prepares capabilities for WebDriver to run ChromeDriver in mobile emulation mode.
     *
     * @param device name of the mobile device which should be used for setting ChromeDriver to emulation mode
     *
     * @return DesiredCapabilities instance that sets WebDriver to mobile emulation mode
     */
    static private DesiredCapabilities prepareMobileCapabilities(String device) {
        def capabilities = new DesiredCapabilities()
        capabilities.setCapability(ChromeOptions.CAPABILITY, MobileOptions.valueOf(device).chromeOptions)
        capabilities
    }
}
