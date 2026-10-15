package com.taxplan.utils;

import com.aventstack.extentreports.Status;
import com.taxplan.base.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    private static final Logger logger = LogManager.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("=== TEST STARTED: " + result.getName() + " ===");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("=== TEST PASSED: " + result.getName() + " ===");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("=== TEST FAILED: " + result.getName() + " ===");
        logger.error("Failure: " + result.getThrowable().getMessage());
        if (BaseTest.extentTest != null) {
            BaseTest.extentTest.log(Status.FAIL, result.getThrowable());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("=== TEST SKIPPED: " + result.getName() + " ===");
        if (BaseTest.extentTest != null) {
            BaseTest.extentTest.log(Status.SKIP, "Test skipped: " + result.getThrowable());
        }
    }

    @Override
    public void onStart(ITestContext context) {
        logger.info("========== SUITE STARTED: " + context.getName() + " ==========");
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info("========== SUITE FINISHED: " + context.getName() + " ==========");
        logger.info("Passed: " + context.getPassedTests().size() +
                ", Failed: " + context.getFailedTests().size() +
                ", Skipped: " + context.getSkippedTests().size());
    }
}
