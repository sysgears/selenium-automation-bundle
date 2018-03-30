package com.sysgears.seleniumbundle.core.uicomparison

import org.openqa.selenium.WebDriver
import ru.yandex.qatools.ashot.AShot
import ru.yandex.qatools.ashot.Screenshot
import ru.yandex.qatools.ashot.comparison.ImageDiff
import ru.yandex.qatools.ashot.comparison.ImageDiffer

import java.awt.image.BufferedImage

/**
 * Provides methods for capturing and comparing screenshots.
 */
class ScreenshotHandler {

    /**
     * AShot instance to be used.
     */
    private AShot aShot

    /**
     * Instance of WebDriver to be used for capturing screenshots.
     */
    private WebDriver driver

    /**
     * Creates an instance of the ScreenshotHandler.
     *
     * @param aShot AShot instance
     * @param driver WebDriver instance
     */
    ScreenshotHandler(AShot aShot, WebDriver driver) {
        this.aShot = aShot
        this.driver = driver
    }

    /**
     * Captures the screenshot of the page opened in the browser.
     *
     * @return Screenshot
     */
    Screenshot capture() {
        aShot.takeScreenshot(driver)
    }

    /**
     * Compares baseScreenshot screenshot with new screenshot.
     *
     * @param baseScreenshot to be compared with
     * @param newScreenshot screenshot which is made on the moment of comparison
     *
     * @return Buffered image with marked discrepancies if there are any
     */
    BufferedImage compare(Screenshot baseScreenshot, Screenshot newScreenshot) {
        ImageDiff diff = new ImageDiffer().makeDiff(baseScreenshot, newScreenshot)
        diff.hasDiff() ? diff.getMarkedImage() : null
    }
}
