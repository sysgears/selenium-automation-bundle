package com.sysgears.seleniumbundle.core.uicomparison

import com.sysgears.seleniumbundle.core.conf.Config
import com.sysgears.seleniumbundle.core.data.DataLoader
import org.openqa.selenium.By
import ru.yandex.qatools.ashot.AShot
import ru.yandex.qatools.ashot.shooting.ShootingStrategies
import ru.yandex.qatools.ashot.shooting.ShootingStrategy

/**
 * Provides configured AShot instance for a page object.
 */
class AShotFactory {

    /**
     * Package where your page object models are stored.
     */
    private static final POMPACKAGE = "com.sysgears.seleniumbundle.pagemodel."

    /**
     * Project properties.
     */
    private Config conf

    /**
     * Creates an instance of AShotFactory.
     *
     * @param conf project properties
     */
    AShotFactory(Config conf) {
        this.conf = conf
    }

    /**
     * Returns a configured aShot instance for a particular page object type.
     * Can be configured to ignore some page elements like advertising banners etc.
     *
     * @param clazz class of a page object.
     *
     * @return configured AShot instance.
     *
     * @throws FileNotFoundException if the file with ignored elements was not found
     */
    AShot getAShotForPage(String os, String browser, Class clazz) throws FileNotFoundException {
        getAShot(os, browser).ignoredElements(getSetOfIgnoredElements(clazz))
    }

    /**
     * Returns a configured aShot instance.
     *
     * @return configured AShot instance
     */
    private AShot getAShot(String os, String browser) {
        def strategies = [
                mac  : [chrome : ShootingStrategies.viewportRetina(250, 0, 0, 2),
                        firefox: ShootingStrategies.viewportRetina(250, 0, 0, 2),
                        safari : ShootingStrategies.simple()]
        ]

        ShootingStrategy strategy = strategies."$os"?."$browser" ?: ShootingStrategies.viewportPasting(250)

        new AShot().shootingStrategy(strategy)
    }

    /**
     * Returns a set of selectors for ignored elements.
     *
     * @param clazz class of a page which`s elements has to be ignored
     *
     * @return Set of By.byCssSelector
     *
     * @throws FileNotFoundException if the file with ignored elements was not found
     */
    private Set<By> getSetOfIgnoredElements(Class clazz) throws FileNotFoundException {
        def data = DataLoader.readMapFromYml(conf.ui.ignoredElements)
        def currentClass = clazz.name - POMPACKAGE
        def locators = (data as ConfigObject)?.flatten()?.get(currentClass)

        locators.collect { it ->
            new By.ByCssSelector(it as String)
        }
    }
}
