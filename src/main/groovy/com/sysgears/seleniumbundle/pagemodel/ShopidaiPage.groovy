package com.sysgears.seleniumbundle.pagemodel

import com.codeborne.selenide.Selenide
import com.sysgears.seleniumbundle.core.pagemodel.AbstractPage
import com.sysgears.seleniumbundle.core.uicomparison.UIComparison

class ShopidaiPage extends AbstractPage<ShopidaiPage> implements UIComparison<ShopidaiPage> {

    ShopidaiPage open() {
        Selenide.open("file://${System.getProperty("user.dir")}/src/test/resources/" +
                "web_page_with_animation/index.html")
        this
    }
}
