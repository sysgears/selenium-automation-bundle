package com.sysgears.seleniumbundle.common

import com.codeborne.selenide.WebDriverRunner
import com.sysgears.seleniumbundle.core.uicomparison.AShotFactory
import com.sysgears.seleniumbundle.core.uicomparison.ScreenshotHandler
import com.sysgears.seleniumbundle.core.uicomparison.ScreenshotLoader
import com.sysgears.seleniumbundle.core.utils.AllureHelper
import com.sysgears.seleniumbundle.core.utils.SoftAssert
import groovy.util.logging.Slf4j
import org.testng.annotations.BeforeClass
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import ru.yandex.qatools.ashot.Screenshot

import java.awt.image.BufferedImage

/**
 * Specific configuration class for UI tests.
 */
@Slf4j
@Test(groups = "ui")
class UITest extends BaseTest {

    /**
     * Allows not to interrupt test method execution until the last assertion will be done.
     */
    protected SoftAssert softAssert = new SoftAssert()

    /**
     * Instance of screenshot loader.
     */
    protected ScreenshotLoader screenshotLoader = new ScreenshotLoader()

    /**
     * Instance of Ashot factory.
     */
    private AShotFactory aShotFactory = new AShotFactory(conf)

    /**
     * Instance of ScreenshotHandler.
     */
    private ScreenshotHandler handler

    /**
     * Instance of AllureHelper.
     */
    private AllureHelper allure = new AllureHelper()

    /**
     * Initializes ScreenshotHandler per class.
     */
    @BeforeClass
    void setScreenShotHandler() {
        handler = new ScreenshotHandler(aShotFactory.getAShotForPage(getClass()),
                WebDriverRunner.getWebDriver())
    }

    /**
     * Captures screenshot and compares it with the previously captured base screenshot. In case there are differences
     * saves the new screenshot and the image with marked discrepancies. In case there is no previously captured base
     * screenshot, saves the new screenshot and throws AssertionError.
     *
     * @param screenshotName name of the screenshot
     *
     * @return instance of the page
     *
     * @throws IOException is thrown if there is no baseline screenshot during comparison or file with ignored
     * elements wasn't found
     * @throws AssertionError is thrown if layout of the screenshot doesn't match to the baseline screenshot.
     */
    protected void compareLayout(String screenshotName) throws IOException, AssertionError {
        def fullPaths = conf.ui.path.collectEntries {
            [it.getKey(), it.getValue() + "/$os/$browser/" + screenshotName + ".png"]
        } as Map<String, String>

        Screenshot screenshot = handler.capture()

        if (conf.baselineMode) {
            screenshotLoader.save(screenshot.getImage(), fullPaths.baseline)

            allure.attach("Baseline screenshot for: $screenshotName", screenshot.getImage())
        } else {
            try {
                def baseScreenshot = screenshotLoader.retrieve(fullPaths.baseline)

                BufferedImage markedImage = handler.compare(new Screenshot(baseScreenshot), screenshot)

                if (markedImage) {
                    screenshotLoader.save(markedImage, fullPaths.difference)
                    screenshotLoader.save(screenshot.getImage(), fullPaths.actual)

                    allure.attach("Baseline screenshot for: $screenshotName", baseScreenshot)
                    allure.attach("Marked screenshot for: $screenshotName", markedImage)
                    allure.attach("Actual screenshot for: $screenshotName", screenshot.getImage())

                    log.info("Layout for: $screenshotName doesn't match to the base screenshot.")
                    throw new AssertionError("Layout for: $screenshotName doesn't match to the base screenshot.")
                } else {
                    log.info("Layout is identical for ${fullPaths.baseline}")
                }
            } catch (IOException e) {
                screenshotLoader.save(screenshot.getImage(), fullPaths.actual)
                log.info("New candidate screenshot has been made successfully: ${fullPaths.actual}", e)
                throw new IOException("No baseline screenshot found.", e)
            }
        }
    }

    @BeforeMethod
    void cleanUp() {
        softAssert.clean()
    }
}
