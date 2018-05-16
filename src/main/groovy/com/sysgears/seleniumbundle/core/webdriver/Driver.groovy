package com.sysgears.seleniumbundle.core.webdriver

import io.github.bonigarcia.wdm.ChromeDriverManager
import io.github.bonigarcia.wdm.EdgeDriverManager
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
        WebDriver createDriver(DesiredCapabilities capabilities) {
            ChromeDriverManager.getInstance().setup()
            new ChromeDriver(capabilities)
        }
    },
    HEADLESS{
        WebDriver createDriver(DesiredCapabilities capabilities = null) {
            ChromeDriverManager.getInstance().setup()

            if (!capabilities) {
                capabilities = new DesiredCapabilities()
                capabilities.setCapability(ChromeOptions.CAPABILITY, new ChromeOptions().setHeadless(true))
            }

            new ChromeDriver(capabilities)
        }
    },
    FIREFOX{
        WebDriver createDriver(DesiredCapabilities capabilities) {
            ChromeDriverManager.getInstance().setup()
            new FirefoxDriver(capabilities)
        }
    },
    MICROSOFTEDGE{
        WebDriver createDriver(DesiredCapabilities capabilities) {
            EdgeDriverManager.getInstance().setup()
            new EdgeDriver(capabilities)
        }
    },
    SAFARI{
        WebDriver createDriver(DesiredCapabilities capabilities) {
            // no additional driver needs to be downloaded as we included SafariDriver as dependency
            new SafariDriver(capabilities)
        }
    }

    abstract WebDriver createDriver(DesiredCapabilities capabilities = new DesiredCapabilities())

    /**
     * Returns specific enum constant by passed name (browserName).
     *
     * @param browserName name of the enum constant
     *
     * @return specific enum instance
     */
    static Driver getDriverType(String browserName = null) {
        !browserName ? CHROME : values().find {
            it.name().equalsIgnoreCase(browserName)
        }
    }
}