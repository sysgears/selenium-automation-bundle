package com.sysgears.seleniumbundle.tests.demo

import com.sysgears.seleniumbundle.common.BaseTest
import com.sysgears.seleniumbundle.listeners.AllureListener
import com.sysgears.seleniumbundle.pagemodel.GooglePage
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Listeners
import org.testng.annotations.Test

@Listeners([AllureListener.class])
@Test(groups = "functional")
class Search extends BaseTest {

    protected GooglePage googlePage

    @BeforeMethod
    void openApplication() {
        googlePage = new GooglePage().open().waitForPageToLoadElements().selectLanguage()
    }

    @Test(description = "Checks number of results on the first page")
    void checkSearch() {
        googlePage
                .searchFor("SysGears")
                .isResultSize(10)
    }
}
