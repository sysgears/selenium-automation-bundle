package com.sysgears.seleniumbundle.core.webdriver

/**
 * Defines options for mobile devices which are used for running chrome WebDriver in mobile emulation mode.
 * Enum values are used for gradle task parametrization e.g. gradle test -Ddevice=iPad command will start chrome browser
 * in iPad emulation mode.
 */
enum MobileOptions {

    IPHONE5("iPhone 5", 320, 568, 2.0, "Mozilla/5.0 (iPhone CPU iPhone OS 9_1 like Mac OS X) " +
            "AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"),

    IPHONE6("iPhone 6", 375, 667, 2.0, "Mozilla/5.0 (iPhone CPU iPhone OS 9_1 like Mac OS X) " +
            "AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"),

    IPHONE6PLUS("iPhone 6 Plus", 414, 736, 2.0, "Mozilla/5.0 (iPhone CPU iPhone OS 9_1 like Mac OS X) " +
            "AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"),

    IPAD("iPad", 1024, 768, 2.0, "Mozilla/5.0 (iPad CPU OS 9_1 like Mac OS X) AppleWebKit/601.1.46 " +
            "(KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"),

    IPADPRO("iPad Pro", 1366, 1024, 2.0, "Mozilla/5.0 (iPad CPU OS 9_1 like Mac OS X) AppleWebKit/601.1.46 " +
            "(KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"),

    S8PLUS("S8 plus", 412, 846, 3.5, "Mozilla/5.0 (Linux Android 7.0 " +
            "SAMSUNG SM-G950F Build/NRD90M) AppleWebKit/537.36 (KHTML, like Gecko) " +
            "SamsungBrowser/5.2 Chrome/51.0.2704.106 Mobile Safari/537.36")

    /**
     * Human readable name for printing.
     */
    private String displayName

    /**
     * Map of device options required for starting browser in mobile emulation mode.
     */
    private chromeOptions

    /**
     * Creates an instants of MobileOptions.
     *
     * @param name name of device
     * @param width browser viewport width
     * @param height browser viewport height
     * @param pixelRatio pixelRatio
     * @param userAgent userAgent
     */
    MobileOptions(String name, int width, int height, double pixelRatio, String userAgent) {
        this.displayName = name
        this.chromeOptions = [mobileEmulation: [deviceMetrics: [width: width, height: height, pixelRatio: pixelRatio],
                                                userAgent    : userAgent]]
    }

    String getDisplayName() {
        displayName
    }

    Map getChromeOptions() {
        chromeOptions
    }

    @Override
    String toString() {
        this.name()
    }
}
