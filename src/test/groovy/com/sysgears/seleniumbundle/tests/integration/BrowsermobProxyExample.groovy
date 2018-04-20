package com.sysgears.seleniumbundle.tests.integration

import com.codeborne.selenide.Selenide
import com.sysgears.seleniumbundle.common.FunctionalTest
import org.testng.Assert
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

class BrowsermobProxyExample extends FunctionalTest {

    @BeforeMethod
    void createHar() {
        browserProxy.proxyServer.newHar()
    }

    @BeforeMethod(dependsOnMethods = "createHar")
    void openApplication() {
        Selenide.open("https://google.com")
    }

    @Test
    void testGoogle() {

        def status = browserProxy.harEntries.find {
            it.request.url == "https://google.com/"
        }.response.status

        Assert.assertEquals(status, 301, "Response status [$status] is not as expected [301]")
    }
}
