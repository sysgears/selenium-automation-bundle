package com.sysgears.seleniumbundle.core.uicomparison

import com.codeborne.selenide.WebDriverRunner
import com.sysgears.seleniumbundle.core.conf.Config
import com.sysgears.seleniumbundle.core.utils.AllureHelper
import groovy.util.logging.Slf4j
import org.apache.commons.io.FilenameUtils
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
     * Operating system.
     */
    private String os

    /**
     * Browser.
     */
    private String browser

    /**
     * Creates an instance of AShotService.
     *
     * @param os name of the operating system
     * @param browser name of the browser
     */
    AShotService(Config conf, IEnvironment environment, List ignoredElements) {
        this.conf = conf
        this.os = environment.getOs()
        this.browser = environment.getBrowser()
        handler = new ScreenshotHandler(getAShot(os, browser, ignoredElements),
                WebDriverRunner.getWebDriver())
    }

    /**
     * Captures a screenshot and compares it with the previously captured baseline screenshot. If there are
     * differences, the method saves the new screenshot and the image with marked differences. If there is no
     * baseline screenshot to compare with, the method saves the new screenshot and throws AssertionError.
     *
     * @param screenshotName name of the screenshot
     *
     * @throws IOException if there is no baseline screenshot during comparison or file with ignored
     * elements was not found
     * @throws AssertionError if the current layout does not match to the baseline screenshot
     * @throws IllegalArgumentException if ApplicationProperties.groovy does not have any ui.path property
     * that must lead to the screenshots
     */
    void compareLayout(String screenshotName) throws IOException, AssertionError, IllegalArgumentException {
        def fullPaths = getPathsForScreenshot(conf.properties.ui.path, screenshotName)

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

                    log.info("Layout for $screenshotName doesn't match to the baseline screenshot.")
                    throw new AssertionError("Layout for: $screenshotName doesn't match to the " +
                            "baseline screenshot.")
                } else {
                    log.info("Layout is identical for ${fullPaths.baseline}")
                }
            } catch (IOException e) {
                screenshotLoader.save(screenshot.getImage(), fullPaths.actual)
                log.info("New candidate screenshot was successfully taken: ${fullPaths.actual}", e)
                throw new IOException("No baseline screenshot found.", e)
            }
        }
    }

    /**
     * Returns a configured aShot instance for a particular page object type.
     * Can be configured to ignore banners, animations, and other elements on the page.
     *
     * @param os operating system where the tests will be executed
     * @param browser browser that will be used for tests launch
     * @param ignoredElements the list of CSS locators for elements that should be ignored while UI is being compared
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
     * Generates paths to the screenshots for single snapshot processing. Uses for path generation preconfigured in
     * ApplicationProperties basic paths for particular categories: "baseline", "actual", "difference", OS name,
     * browser name, and screenshot name.
     *
     * @param paths paths that should be preconfigured in the ApplicationProperties.groovy
     * @param screenshotName name of the screenshot
     *
     * @return Map of paths for a single snapshot processing
     *
     * @throws IllegalArgumentException is thrown if Application.properties doesn't have any of ui.path properties
     * "actual", "baseline" or "difference"
     */
    Map getPathsForScreenshot(Map paths, String screenshotName) throws IllegalArgumentException {
        ["baseline", "actual", "difference"].collectEntries { String category ->
            [category, FilenameUtils.separatorsToSystem("${paths[category]}/$os/$browser/${screenshotName}.png") ?:
                    {
                        throw new IllegalArgumentException("[$category] path for UI comparison is missing, check " +
                                "Application.properties")
                    }()
            ]
        }
    }
}
