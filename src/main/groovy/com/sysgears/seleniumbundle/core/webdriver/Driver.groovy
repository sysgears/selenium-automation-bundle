package com.sysgears.seleniumbundle.core.webdriver

import io.github.bonigarcia.wdm.ChromeDriverManager
import io.github.bonigarcia.wdm.EdgeDriverManager
import io.github.bonigarcia.wdm.FirefoxDriverManager
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.edge.EdgeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.safari.SafariDriver

/**
 * Defines WebDriver types for various browsers and provides methods for instantiating the WebDriver w/ and
 * w/o capabilities.
 */
enum Driver {
    CHROME{
        WebDriver getWebDriverObject(DesiredCapabilities capabilities = null) {
            ChromeDriverManager.getInstance().setup()
            capabilities ? new ChromeDriver(capabilities) : new ChromeDriver()
        }
    },
    FIREFOX{
        WebDriver getWebDriverObject(DesiredCapabilities capabilities = null) {
            FirefoxDriverManager.getInstance().setup()
            capabilities ? new FirefoxDriver(capabilities) : new FirefoxDriver()
        }
    },
    SAFARI{
        WebDriver getWebDriverObject(DesiredCapabilities capabilities = null) {

            // no additional driver needs to be downloaded as we included SafariDriver as dependency
            capabilities ? new SafariDriver(capabilities) : new SafariDriver()
        }
    },
    MICROSOFTEDGE{
        WebDriver getWebDriverObject(DesiredCapabilities capabilities = null) {
            EdgeDriverManager.getInstance().setup()
            capabilities ? new EdgeDriver(capabilities) : new EdgeDriver()
        }
    },
    HEADLESS{
        WebDriver getWebDriverObject(DesiredCapabilities capabilities = null) {
            ChromeDriverManager.getInstance().setup()
            capabilities = capabilities ?: new DesiredCapabilities()

            // configures Chrome to start in headless mode
            capabilities.setCapability(ChromeOptions.CAPABILITY, new ChromeOptions().setHeadless(true))
            new ChromeDriver(capabilities)
        }
    }

    abstract WebDriver getWebDriverObject(DesiredCapabilities capabilities = null)

    static Driver getDriver(String name) {
        !name ? CHROME : values().find {
            it.name().equalsIgnoreCase(name)
        }
    }
}