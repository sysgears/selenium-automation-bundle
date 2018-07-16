package com.sysgears.seleniumbundle.listeners

import com.codeborne.selenide.Screenshots
import com.sysgears.seleniumbundle.core.utils.AnnotationHelper
import com.sysgears.seleniumbundle.listeners.exceptions.AllureAttachmentException
import groovy.util.logging.Slf4j
import io.qameta.allure.Allure
import org.apache.commons.io.FileUtils
import org.testng.ITestContext
import org.testng.ITestListener
import org.testng.ITestResult
import org.testng.annotations.Listeners

/**
 * Provides additional processing for tests.
 */
@Slf4j
class AllureListener implements ITestListener {

    /**
     * Handles post-processing for failed tests, takes screenshots and attaches them to Allure report.
     * Makes additional check if the listener should be applied to the test.
     *
     * @param result test result
     */
    @Override
    void onTestFailure(ITestResult result) throws AllureAttachmentException {
        if (isListenerAppliedGlobally(result) || isListenerAppliedTo(result.testClass.realClass)) {
            try {
                Allure.addAttachment("Screenshot",
                        new ByteArrayInputStream(
                                FileUtils.readFileToByteArray(
                                        Screenshots.takeScreenShotAsFile())))
            } catch (IOException e) {
                log.error("Screenshot was not attached to Allure report.", e)
                throw new AllureAttachmentException("Screenshot was not attached to Allure report.", e)
            }
        }
    }

    /**
     * Checks if the listener is applied globally on the test suite level in the testng.xml file.
     *
     * @param result test result
     *
     * @return true - if testng.xml has this listener in configuration, false - if not
     */
    private boolean isListenerAppliedGlobally(ITestResult result) {
        def listeners = result.testContext.currentXmlTest.suite.listeners
        listeners.contains(this.class.name)
    }

    /**
     * Checks if the listener is applied to a particular class.
     *
     * @param testClass class of tests
     *
     * @return true - if the listener is applied to class, false - if not
     */
    private boolean isListenerAppliedTo(Class testClass) {
        Listeners listenersAnnotation = (Listeners) AnnotationHelper.getClassAnnotation(testClass, Listeners.class)
        listenersAnnotation && (listenersAnnotation.value()).contains(this.class)
    }

    @Override
    void onTestStart(ITestResult result) {

    }

    @Override
    void onTestSuccess(ITestResult result) {

    }

    @Override
    void onTestSkipped(ITestResult result) {

    }

    @Override
    void onTestFailedButWithinSuccessPercentage(ITestResult result) {

    }

    @Override
    void onStart(ITestContext context) {

    }

    @Override
    void onFinish(ITestContext context) {

    }
}
