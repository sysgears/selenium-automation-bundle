package com.sysgears.seleniumbundle.core.uicomparison

import org.openqa.selenium.By
import ru.yandex.qatools.ashot.AShot
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider
import ru.yandex.qatools.ashot.shooting.ShootingStrategies
import ru.yandex.qatools.ashot.shooting.ShootingStrategy

/**
 * Provides configured aShot instance for a page object.
 */
class AShotFactory {

    /**
     * Returns a configured aShot instance for a particular page object type.
     * Can be configured to ignore some page elements like advertising banners etc.
     *
     * @param clazz class of a page object
     *
     * @return configured AShot instance
     *
     * @throws FileNotFoundException if the file with ignored elements was not found
     */
    AShot getAShotForPage(String os, String browser, List ignoredElements = []) throws FileNotFoundException {
        def aShot = getAShot(os, browser)

        aShot.ignoredElements(ignoredElements.collect { element ->
            new By.ByCssSelector(element as String)
        } as Set<By>)
    }

    /**
     * Returns a configured aShot instance.
     *
     * @return configured AShot instance
     */
    private AShot getAShot(String os, String browser) {
        def strategies = [
                mac: [chrome : ShootingStrategies.viewportRetina(750, 0, 0, 2),
                      firefox: ShootingStrategies.viewportRetina(750, 0, 0, 2),
                      safari : ShootingStrategies.simple()]
        ]

        ShootingStrategy strategy = strategies."$os"?."$browser" ?: ShootingStrategies.viewportPasting(750)

        new AShot().shootingStrategy(strategy).coordsProvider(new WebDriverCoordsProvider())
    }
}
