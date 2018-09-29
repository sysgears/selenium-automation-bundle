package com.sysgears.seleniumbundle.core.webdriver

import groovy.util.logging.Slf4j
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
@Slf4j
class DriverInitializer {

    /**
     * Configures and instantiates WebDriver with given desired capabilities for local test execution.
     *
     * @param browser browser, e.g. chrome, firefox, microsoftedge, headless for headless chrome
     * @param capabilities capabilities to start browser with, can be empty
     *
     * @return WebDriver instance
     */
    static WebDriver createDriver(String browser, DesiredCapabilities capabilities = null) {
        Driver.getDriverType(browser).createDriver(capabilities)
    }

    /**
     * Configures and instantiates WebDriver with given proxy for local test execution. Currently, proxy works only with
     * Chrome browser.
     *
     * @param browser browser, chrome
     * @param proxy proxy for capturing network traffic
     *
     * @return WebDriver instance
     */
    static WebDriver createDriver(String browser, Proxy proxy) {
        def capabilities = new DesiredCapabilities()

        if (browser == "chrome") {
            capabilities.setCapability(CapabilityType.PROXY, proxy)
        }

        Driver.getDriverType(browser).createDriver(capabilities)
    }

    /**
     * Configures and instantiates WebDriver with given desired capabilities for remote test execution.
     *
     * @param remoteUrl url of Selenium-Grid hub or remote Selenium server
     * @param capabilities capabilities to start browser with
     *
     * @return WebDriver instance
     *
     * @throws IllegalArgumentException if @param capabilities is null
     */
    static WebDriver createRemoteDriver(String remoteUrl, DesiredCapabilities capabilities)
            throws IllegalArgumentException {
        capabilities ? new RemoteWebDriver(URI.create(remoteUrl).toURL(), capabilities) : {
            log.error("No capabilities were provided to create remote driver.")
            throw new IllegalArgumentException("Please provide capabilities object.")
        }()
    }

    /**
     * Configures and instantiates WebDriver with a given proxy for remote test execution. Currently, only Chrome
     * browser works with proxy.
     *
     * @param remoteUrl url of Selenium-Grid hub or remote Selenium server
     * @param platform platform on which tests should be run, e.g. linux, windows
     * @param browser browser, e.g. chrome, firefox, microsoftedge, headless for headless chrome
     * @param proxy instance of proxy, only for Chrome browser
     *
     * @return WebDriver instance
     */
    static WebDriver createRemoteDriver(String remoteUrl, String platform, String browser, Proxy proxy = null,
                                        String browserVersion = "") {
        def capabilities = new DesiredCapabilities(browser, browserVersion, Platform.fromString(platform))

        if (proxy && browser == "chrome") {
            capabilities.setCapability(CapabilityType.PROXY, proxy)
        }

        new RemoteWebDriver(URI.create(remoteUrl).toURL(), capabilities)
    }

    /**
     * Configures and instantiates WebDriver for Chrome mobile emulation mode.
     *
     * @param device name of the mobile device w/o spaces, e.g. IPHONE6, IPAD
     *
     * @return WebDriver instance configured to start Chrome browser in mobile emulation mode
     */
    static WebDriver createMobileDriver(String device) {
        def capabilities = new DesiredCapabilities()
        capabilities.setCapability(ChromeOptions.CAPABILITY, MobileOptions.valueOf(device.toUpperCase()).chromeOptions)
        Driver.CHROME.createDriver(capabilities)
    }

    /**
     * Returns capabilities object to configure WebDriver with additional parameters e.g. platform, browser, etc.
     *
     * @param platform platform on which the tests should be run, e.g. linux, windows
     * @param browser browser, e.g. chrome, firefox, microsoftedge, headless for headless chrome
     * @param browserVersion browser browserVersion
     *
     * @return capabilities object
     */
    static DesiredCapabilities prepareCapabilities(String platform, String browser, String browserVersion = "") {
        new DesiredCapabilities(browser, browserVersion, Platform.fromString(platform))
    }

    /**
     * Returns capabilities object to configure ChromeDriver with additional parameters e.g. platform, proxy etc.
     * Sets arguments for running Chrome browser in special mode e.g. size or headless mode.
     *
     * @param platform platform name such as mac, linux , windows
     * @param proxy selenium proxy to capture network traffic
     * @param browserVersion browser version
     * @param arguments arguments to run browser with, like "--disable-gpu"
     *
     * @return capabilities object
     */
    static DesiredCapabilities prepareCapabilitiesWithChromeOptions(String platform = "ANY", Proxy proxy = null,
                                                                    String browserVersion = "", String... arguments) {
        def capabilities = prepareCapabilities(platform, "chrome", browserVersion)
        capabilities.setCapability(CapabilityType.PROXY, proxy)
        capabilities.setCapability(ChromeOptions.CAPABILITY, new ChromeOptions().addArguments(arguments))

        capabilities
    }
}
