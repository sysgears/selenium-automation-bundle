package com.sysgears.seleniumbundle.tests.demo

import com.sysgears.seleniumbundle.common.FunctionalTest
import com.sysgears.seleniumbundle.core.data.DataLoader
import com.sysgears.seleniumbundle.pagemodel.GooglePage
import com.sysgears.seleniumbundle.pagemodel.ResultsPage
import org.testng.annotations.BeforeMethod
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

import java.lang.reflect.Method

class ToolsDemoForCsvData extends FunctionalTest {

    protected GooglePage googlePage
    private final static String DATAFILE = "src/test/resources/data/google/test_data.csv"

    @BeforeMethod
    void openApplication() {
        googlePage = new GooglePage().open().waitForPageToLoadElements().selectLanguage()
    }

    @DataProvider(name = 'getTestDataFromCsv')
    Object[][] getTestDataFromCsv(Method m) {
        mapper.mapFromDataFile(DataLoader.readMapFromDataFile(DATAFILE, conf.data.csv.setSeparator), m)
    }

    @Test(dataProvider = "getTestDataFromCsv",
            description = "Checks that url parameters specified in test data are changed based on chosen category")
    void checkUrlParameterChanges(String query, String category, Map params) {
        googlePage
                .searchFor(query)

        new ResultsPage()
                .waitForPageToLoadElements()
                .selectCategory(category)
                .validateUrlParams(params)
    }

    @Test(dataProvider = "getTestDataFromCsv", description = "Checks that specific tools are available for chosen category")
    void checkOptionsForCategories(String query, String category, List tools) {
        googlePage
                .searchFor(query)

        new ResultsPage()
                .waitForPageToLoadElements()
                .selectCategory(category)
                .openToolsMenu()
                .areToolsPresent(tools)
    }
}
