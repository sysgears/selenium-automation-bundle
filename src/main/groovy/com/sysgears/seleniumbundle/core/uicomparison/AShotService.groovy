package com.sysgears.seleniumbundle.core.uicomparison

import com.codeborne.selenide.WebDriverRunner
import com.sysgears.seleniumbundle.core.conf.Config
import com.sysgears.seleniumbundle.core.utils.AllureHelper
import com.sysgears.seleniumbundle.core.utils.PathHelper
import groovy.util.logging.Slf4j
import org.openqa.selenium.By
import ru.yandex.qatools.ashot.AShot
import ru.yandex.qatools.ashot.Screenshot
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider
import ru.yandex.qatools.ashot.shooting.ShootingStrategies
import ru.yandex.qatools.ashot.shooting.ShootingStrategy

import java.awt.image.BufferedImage

/**
 * Provides methods to interact with aShot.
 */
@Slf4j
class AShotService {

    /**
     * Instance of AllureHelper.
     */
    private AllureHelper allure = new AllureHelper()

    /**
     * Instance of ScreenshotLoader.
     */
    private ScreenshotLoader screenshotLoader = new ScreenshotLoader()

    /**
     * Instance of ScreenshotHandler.
     */
    private ScreenshotHandler handler

    /**
     * Project properties.
     */
    private Config conf

    /**
     * OS name.
     */
    private String os

    /**
     * Browser name.
     */
    private String browser

    /**
     * Creates an instance of AShotService.
     *
     * @param os name of the os
     * @param browser name of the os
     */
    AShotService(Config conf, IEnvironment environment, List ignoredElements) {
        this.conf = conf
        this.os = environment.getOs()
        this.browser = environment.getBrowser()
        handler = new ScreenshotHandler(getAShot(os, browser, ignoredElements),
                WebDriverRunner.getWebDriver())
    }

    /**
     * Captures screenshot and compares it with the previously captured base screenshot. In case there are differences
     * saves the new screenshot and the image with marked discrepancies. In case there is no previously captured base
     * screenshot, saves the new screenshot and throws AssertionError.
     *
     * @param screenshotName name of the screenshot
     *
     * @throws IOException is thrown if there is no baseline screenshot during comparison or file with ignored
     * elements wasn't found
     * @throws AssertionError is thrown if layout of the screenshot doesn't match to the baseline screenshot.
     */
    void compareLayout(String screenshotName) throws IOException, AssertionError {
        def fullPaths = getPathsForScreenshot(screenshotName)

        Screenshot screenshot = handler.capture()

        if (conf.baselineMode) {
            screenshotLoader.save(screenshot.getImage(), fullPaths.baseline)

            allure.attach("Baseline screenshot for: $screenshotName", screenshot.getImage())
        } else {
            try {
                def baseScreenshot = new Screenshot(screenshotLoader.retrieve(fullPaths.baseline))
                baseScreenshot.setIgnoredAreas(screenshot.getIgnoredAreas())

                BufferedImage markedImage = handler.compare(baseScreenshot, screenshot)

                if (markedImage) {
                    screenshotLoader.save(markedImage, fullPaths.difference)
                    screenshotLoader.save(screenshot.getImage(), fullPaths.actual)

                    allure.attach("Baseline screenshot for: $screenshotName", baseScreenshot.getImage())
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

    /**
     * Returns a configured aShot instance for a particular page object type.
     * Can be configured to ignore some page elements like advertising banners etc.
     *
     * @param os os where the tests will be executed
     * @param browser browser that will be used for tests launch
     * @param ignoredElements the list of css locators for elements that should be ignored while ui comparison analysis
     *
     * @return configured AShot instance
     */

    private AShot getAShot(String os, String browser, List ignoredElements) {
        def strategies = [ // TODO move aShot shooting strategies configuration to Application.properties
                           mac: [chrome : ShootingStrategies.viewportRetina(750, 0, 0, 2),
                                 firefox: ShootingStrategies.viewportRetina(750, 0, 0, 2),
                                 safari : ShootingStrategies.simple()]
        ]

        ShootingStrategy strategy = strategies."$os"?."$browser" ?: ShootingStrategies.viewportPasting(750)

        new AShot().shootingStrategy(strategy).coordsProvider(new WebDriverCoordsProvider())
                .ignoredElements(ignoredElements.collect { element ->
            new By.ByCssSelector(element as String)
        } as Set<By>)
    }

    /**
     * Generates paths to screenshots by a given screenshot name.
     *
     * @param screenshotName name of the screenshot
     *
     * @return Map of paths to screenshots
     */
    private Map getPathsForScreenshot(String screenshotName) {
        conf.ui.path.collectEntries {
            [it.key, PathHelper.convertPathForPlatform("${it.value}/$os/$browser/${screenshotName}.png")]
        } as Map<String, String>
    }
}
