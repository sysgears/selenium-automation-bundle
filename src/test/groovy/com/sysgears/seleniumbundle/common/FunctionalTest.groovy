package com.sysgears.seleniumbundle.common

import com.sysgears.seleniumbundle.listeners.AllureListener
import org.testng.annotations.Listeners
import org.testng.annotations.Test

@Listeners(AllureListener)
@Test(groups = "functional")
class FunctionalTest extends BaseTest {
}
