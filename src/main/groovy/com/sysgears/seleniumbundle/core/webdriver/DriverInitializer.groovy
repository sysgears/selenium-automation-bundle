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
 * Provides a driver instance.
 */
@Slf4j
class DriverInitializer {

    /**
     * Configures and instantiates WebDriver with required capabilities for local test execution.
     *
     * @param browser browser, for example, "chrome", "firefox", "MicrosoftEdge", or "headless" for headless Chrome
     * @param capabilities capabilities to start browser with, can be empty
     *
     * @return WebDriver instance
     */
    static WebDriver createDriver(String browser, DesiredCapabilities capabilities = null) {
        Driver.getDriverType(browser).createDriver(capabilities)
    }

    /**
     * Configures and instantiates WebDriver with a given proxy for local test execution. Currently, the proxy only
     * works with Chrome.
     *
     * @param browser browser, chrome
     * @param proxy proxy for capturing the network traffic
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
     * @param remoteUrl URL of the Selenium-Grid hub or a remote Selenium server
     * @param capabilities capabilities to start browser with
     *
     * @return WebDriver instance
     *
     * @throws IllegalArgumentException if @param capabilities is null
     */
    static WebDriver createRemoteDriver(String remoteUrl, DesiredCapabilities capabilities)
            throws IllegalArgumentException {
        capabilities ? new RemoteWebDriver(URI.create(remoteUrl).toURL(), capabilities) : {
            log.error("No capabilities were provided to create a remote driver.")
            throw new IllegalArgumentException("Please provide the capabilities object.")
        }()
    }

    /**
     * Configures and instantiates WebDriver with a given proxy for remote test execution. Currently, only Chrome
     * works with proxy.
     *
     * @param remoteUrl URL of the Selenium-Grid hub or a remote Selenium server
     * @param platform platform on which tests should run, for example, "linux", "windows", or "mac"
     * @param browser browser, for example, "chrome", "firefox", "MicrosoftEdge", or "headless" for headless Chrome
     * @param proxy instance of proxy, only for Chrome
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
     * @param device name of the mobile device w/o spaces, for example, IPHONE6, IPAD
     *
     * @return WebDriver instance configured to start Chrome in mobile emulation mode
     */
    static WebDriver createMobileDriver(String device) {
        def capabilities = new DesiredCapabilities()
        capabilities.setCapability(ChromeOptions.CAPABILITY, MobileOptions.valueOf(device.toUpperCase()).chromeOptions)
        Driver.CHROME.createDriver(capabilities)
    }

    /**
     * Returns the capabilities object to configure WebDriver with additional parameters such as the platform, browser,
     * and others.
     *
     * @param platform platform on which tests should run, for example, "linux", "windows", or "mac"
     * @param browser browser, for example, "chrome", "firefox", "MicrosoftEdge", or "headless" for headless Chrome
     * @param browserVersion browser browserVersion
     *
     * @return capabilities object
     */
    static DesiredCapabilities prepareCapabilities(String platform, String browser, String browserVersion = "") {
        new DesiredCapabilities(browser, browserVersion, Platform.fromString(platform))
    }

    /**
     * Returns the capabilities object to configure ChromeDriver with additional parameters such as the platform, proxy
     * and others. Sets the arguments for running Chrome in special mode such as size or headless mode.
     *
     * @param platform platform name such as "mac", "linux", or "windows"
     * @param proxy Selenium proxy to capture the network traffic
     * @param browserVersion browser version
     * @param arguments arguments to run browser with, for example, "--disable-gpu"
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
