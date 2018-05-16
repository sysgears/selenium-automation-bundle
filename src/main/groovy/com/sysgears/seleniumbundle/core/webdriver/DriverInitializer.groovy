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
     * Configures and instantiates WebDriver with given proxy for local test execution.
     *
     * @param browser browser, e.g. chrome, firefox, microsoftedge, headless
     * @param capabilities capabilities to start browser with
     *
     * @return WebDriver instance
     */
    static WebDriver createDriver(Proxy proxy) {
        def capabilities = new DesiredCapabilities()
        capabilities.setCapability(CapabilityType.PROXY, proxy)

        Driver.CHROME.createDriver(capabilities)
    }

    /**
     * Configures and instantiates WebDriver with given desired capabilities for local test execution.
     *
     * @param browser browser, e.g. chrome, firefox, microsoftedge, headless
     * @param capabilities capabilities to start browser with
     *
     * @return WebDriver instance
     */
    static WebDriver createDriver(String browser, DesiredCapabilities capabilities = null) {
        Driver.getDriverType(browser).createDriver(capabilities)
    }

    /**
     * Configures and instantiates WebDriver with given proxy for remote test execution.
     *
     * @param remoteUrl url of GridHub
     * @param platform platform on which the tests should be run, e.g. linux, windows
     * @param browser browser, e.g. chrome, firefox, microsoftedge, headless
     * @param proxy instance of proxy
     *
     * @return WebDriver instance
     */
    static WebDriver createRemoteDriver(String remoteUrl, String platform, String browser, Proxy proxy = null,
                                        String browserVersion = "") {
        def capabilities = new DesiredCapabilities(browser, browserVersion, Platform.fromString(platform))

        if (browser == "chrome") capabilities.setCapability(CapabilityType.PROXY, proxy)

        new RemoteWebDriver(URI.create(remoteUrl).toURL(), capabilities)
    }

    /**
     * Configures and instantiates WebDriver with given desired capabilities for remote test execution.
     *
     * @param remoteUrl url of GridHub
     * @param platform platform on which the tests should be run, e.g. linux, windows
     * @param browser browser, e.g. chrome, firefox, microsoftedge, headless
     * @param proxy instance of proxy
     *
     * @return WebDriver instance
     *
     * @throws IllegalArgumentException if @param capabilities is null
     */
    static WebDriver createRemoteDriver(String remoteUrl, DesiredCapabilities capabilities)
            throws IllegalArgumentException {
        capabilities ? new RemoteWebDriver(URI.create(remoteUrl).toURL(), capabilities) : {
            throw new IllegalArgumentException("Please provide capabilities object")
        }()
    }

    /**
     * Configures and instantiates WebDriver for Chrome mobile emulation mode.
     *
     * @param device name of the mobile device, e.g.
     *
     * @return WebDriver instance
     */
    static WebDriver createMobileDriver(String device) {
        def capabilities = new DesiredCapabilities()
        capabilities.setCapability(ChromeOptions.CAPABILITY, MobileOptions.valueOf(device).chromeOptions)
        capabilities
        Driver.CHROME.createDriver(capabilities)
    }

    /**
     * Returns capabilities object to configure WebDriver with additional parameter e.g. browser type, platform etc.
     * Allows set argument for running browser in special mode e.g. size or headless mode.
     *
     * @param platform platform name such as mac, linux , windows
     * @param browser browser name such as chrome, firefox, safari, microsoftedge
     * @param proxy selenium proxy to capture network traffic
     * @param browserVersion browser browserVersion
     * @param arguments arguments to run browser with like "--disable-gpu"
     *
     * @return capabilities object
     */
    static DesiredCapabilities prepareChromeCapabilitiesWithOptions(String platform = "ANY",
                                                                    Proxy proxy = null, String browserVersion = "",
                                                                    String... arguments) {
        def capabilities = new DesiredCapabilities("chrome", browserVersion, Platform.fromString(platform))
        capabilities.setCapability(CapabilityType.PROXY, proxy)
        capabilities.setCapability(ChromeOptions.CAPABILITY, new ChromeOptions().addArguments(arguments))

        capabilities
    }
}
