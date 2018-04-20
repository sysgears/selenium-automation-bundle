package com.sysgears.seleniumbundle.tests.demo

import com.sysgears.seleniumbundle.common.FunctionalTest
import com.sysgears.seleniumbundle.pagemodel.GooglePage
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

class Search extends FunctionalTest {

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
