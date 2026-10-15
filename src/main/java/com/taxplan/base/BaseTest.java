package com.taxplan.base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.taxplan.utils.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

public class BaseTest {

    public static WebDriver driver;
    public static ExtentReports extent;
    public static ExtentTest extentTest;
    protected static final Logger logger = LogManager.getLogger(BaseTest.class);
    protected static ConfigReader config;

    @BeforeSuite
    public void setUpSuite() {
        if (config == null) {
            config = new ConfigReader();
        }
        initExtentReports();
        logger.info("Extent Reports initialized successfully");
    }

    @BeforeMethod
    @Parameters("browser")
    public void setUp(@Optional("chrome") String browser, Method method) {
        if (config == null) {
            config = new ConfigReader();
        }
        extentTest = extent.createTest(method.getName());
        extentTest.log(Status.INFO, "Starting test: " + method.getName());
        initDriver(browser);
        logger.info("Browser launched: " + browser + " for test: " + method.getName());
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            extentTest.log(Status.FAIL, "Test Case FAILED: " + result.getName());
            extentTest.log(Status.FAIL, "Failure Reason: " + result.getThrowable());
            String screenshotPath = captureScreenshot(result.getName());
            if (screenshotPath != null) {
                try {
                    extentTest.addScreenCaptureFromPath(screenshotPath);
                } catch (Exception e) {
                    logger.error("Failed to attach screenshot to report", e);
                }
            }
            logger.error("Test FAILED: " + result.getName(), result.getThrowable());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            extentTest.log(Status.PASS, "Test Case PASSED: " + result.getName());
            logger.info("Test PASSED: " + result.getName());
        } else if (result.getStatus() == ITestResult.SKIP) {
            extentTest.log(Status.SKIP, "Test Case SKIPPED: " + result.getName());
            logger.info("Test SKIPPED: " + result.getName());
        }

        if (driver != null) {
            driver.quit();
            logger.info("Browser closed for test: " + result.getName());
        }
    }

    @AfterSuite
    public void tearDownSuite() {
        if (extent != null) {
            extent.flush();
        }
        logger.info("Test suite completed. Extent report generated.");
    }

    private void initDriver(String browser) {
        String browserType = (browser != null && !browser.isEmpty()) ? browser : config.getProperty("browser");
        boolean headless = Boolean.parseBoolean(config.getProperty("headless"));

        switch (browserType.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                if (headless) chromeOptions.addArguments("--headless=new");
                chromeOptions.addArguments("--start-maximized", "--disable-notifications", "--remote-allow-origins=*");
                chromeOptions.addArguments("--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage", "--window-size=1920,1080");
                driver = new ChromeDriver(chromeOptions);
                break;
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) firefoxOptions.addArguments("--headless");
                driver = new FirefoxDriver(firefoxOptions);
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                if (headless) edgeOptions.addArguments("--headless=new");
                driver = new EdgeDriver(edgeOptions);
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browserType);
        }

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(Integer.parseInt(config.getProperty("implicit.wait"))));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(Integer.parseInt(config.getProperty("page.load.timeout"))));
    }

    private void initExtentReports() {
        String reportPath = config.getProperty("report.path");
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setDocumentTitle("Taxplan Advisor QA Assessment Report");
        sparkReporter.config().setReportName("Taxplan Advisor Test Execution Report");
        sparkReporter.config().setTheme(com.aventstack.extentreports.reporter.configuration.Theme.DARK);

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Application", "Taxplan Advisor UAT");
        extent.setSystemInfo("Environment", "Staging");
        extent.setSystemInfo("Tester", "Aditya Ghone");
        extent.setSystemInfo("URL", config.getProperty("url"));
    }

    public String captureScreenshot(String testName) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String screenshotDir = config.getProperty("screenshot.path");
            File dir = new File(screenshotDir);
            if (!dir.exists()) dir.mkdirs();

            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String destination = screenshotDir + testName + "_" + timestamp + ".png";
            FileUtils.copyFile(source, new File(destination));
            logger.info("Screenshot captured: " + destination);
            return destination;
        } catch (IOException e) {
            logger.error("Failed to capture screenshot", e);
            return null;
        }
    }

    public WebDriver getDriver() {
        return driver;
    }
}
