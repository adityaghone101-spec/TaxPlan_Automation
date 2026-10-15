package com.taxplan.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class HomePage {

    private static final Logger logger = LogManager.getLogger(HomePage.class);
    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(xpath = "//h2[contains(text(),'India') and contains(text(),'Marketplace')]")
    private WebElement marketplaceHeader;

    @FindBy(xpath = "//button[contains(text(),'File ITR')]")
    private WebElement fileITRButton;

    @FindBy(xpath = "//button[contains(text(),'Consultation')]")
    private WebElement consultationButton;

    @FindBy(xpath = "//button[contains(text(),'Talk to a Tax Advisor')]")
    private WebElement talkToAdvisorButton;

    @FindBy(xpath = "//a[contains(text(),'View Filing Services')]")
    private WebElement viewFilingServicesLink;

    @FindBy(xpath = "//a[contains(text(),'View Registration Services')]")
    private WebElement viewRegistrationServicesLink;

    @FindBy(xpath = "//a[contains(text(),'Resolve a Notice')]")
    private WebElement resolveNoticeLink;

    @FindBy(xpath = "//a[contains(text(),'Book a Consultation')]")
    private WebElement bookConsultationLink;

    @FindBy(xpath = "//a[contains(text(),'Refer Now')]")
    private WebElement referNowLink;

    @FindBy(xpath = "//a[contains(text(),'Sign in') or contains(text(),'Login')]")
    private WebElement loginLink;

    @FindBy(xpath = "//a[contains(text(),'Sign up') or contains(text(),'Create')]")
    private WebElement signupLink;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        PageFactory.initElements(driver, this);
    }

    public void navigateToHomePage(String url) {
        driver.get(url);
        logger.info("Navigated to home page: " + url);
    }

    public boolean isHomePageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(marketplaceHeader)).isDisplayed();
        } catch (Exception e) {
            logger.warn("Home page header not found: " + e.getMessage());
            return false;
        }
    }

    public void clickFileITR() {
        wait.until(ExpectedConditions.elementToBeClickable(fileITRButton)).click();
        logger.info("Clicked File ITR button");
    }

    public void clickConsultation() {
        wait.until(ExpectedConditions.elementToBeClickable(consultationButton)).click();
        logger.info("Clicked Consultation button");
    }

    public void clickTalkToAdvisor() {
        wait.until(ExpectedConditions.elementToBeClickable(talkToAdvisorButton)).click();
        logger.info("Clicked Talk to a Tax Advisor button");
    }

    public void clickViewFilingServices() {
        wait.until(ExpectedConditions.elementToBeClickable(viewFilingServicesLink)).click();
        logger.info("Clicked View Filing Services link");
    }

    public void clickViewRegistrationServices() {
        wait.until(ExpectedConditions.elementToBeClickable(viewRegistrationServicesLink)).click();
        logger.info("Clicked View Registration Services link");
    }

    public void clickResolveNotice() {
        wait.until(ExpectedConditions.elementToBeClickable(resolveNoticeLink)).click();
        logger.info("Clicked Resolve a Notice link");
    }

    public void clickBookConsultation() {
        wait.until(ExpectedConditions.elementToBeClickable(bookConsultationLink)).click();
        logger.info("Clicked Book a Consultation link");
    }

    public void clickReferNow() {
        wait.until(ExpectedConditions.elementToBeClickable(referNowLink)).click();
        logger.info("Clicked Refer Now link");
    }

    public void clickLoginLink() {
        wait.until(ExpectedConditions.elementToBeClickable(loginLink)).click();
        logger.info("Clicked Login link");
    }

    public int getConsultantCardCount() {
        try {
            List<WebElement> cards = driver.findElements(By.xpath("//div[contains(@class,'card') or contains(@class,'consultant')]"));
            return cards.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public boolean isServiceDisplayed(String serviceName) {
        try {
            WebElement service = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(),'" + serviceName + "')]")
            ));
            return service.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickServiceByName(String serviceName) {
        WebElement service = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[contains(text(),'" + serviceName + "')]")
        ));
        service.click();
        logger.info("Clicked service: " + serviceName);
    }
}