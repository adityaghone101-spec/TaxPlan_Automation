package com.taxplan.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {

    private static final Logger logger = LogManager.getLogger(LoginPage.class);
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators
    @FindBy(xpath = "//h2[contains(text(),'Sign in')]")
    private WebElement signInHeader;

    @FindBy(xpath = "//input[@type='email' or @placeholder='Enter your email']")
    private WebElement emailField;

    @FindBy(xpath = "//input[@type='password']")
    private WebElement passwordField;

    @FindBy(xpath = "//button[contains(text(),'Sign in') or @type='submit']")
    private WebElement signInButton;

    @FindBy(xpath = "//a[contains(text(),'Consultant Access')]")
    private WebElement consultantAccessLink;

    @FindBy(xpath = "//a[contains(text(),'Forgot password')]")
    private WebElement forgotPasswordLink;

    @FindBy(xpath = "//a[contains(text(),'Create an account')]")
    private WebElement createAccountLink;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        PageFactory.initElements(driver, this);
    }

    public void navigateToLoginPage(String loginUrl) {
        driver.get(loginUrl);
        logger.info("Navigated to login page: " + loginUrl);
    }

    public boolean isLoginPageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(signInHeader)).isDisplayed();
        } catch (Exception e) {
            logger.warn("Login page header not found: " + e.getMessage());
            return false;
        }
    }

    public void enterEmail(String email) {
        wait.until(ExpectedConditions.visibilityOf(emailField)).clear();
        emailField.sendKeys(email);
        logger.info("Entered email: " + email);
    }

    public void enterPassword(String password) {
        wait.until(ExpectedConditions.visibilityOf(passwordField)).clear();
        passwordField.sendKeys(password);
        logger.info("Entered password");
    }

    public void clickSignIn() {
        wait.until(ExpectedConditions.elementToBeClickable(signInButton)).click();
        logger.info("Clicked Sign In button");
    }

    public void clickConsultantAccess() {
        wait.until(ExpectedConditions.elementToBeClickable(consultantAccessLink)).click();
        logger.info("Clicked Consultant Access link");
    }

    public void clickForgotPassword() {
        wait.until(ExpectedConditions.elementToBeClickable(forgotPasswordLink)).click();
        logger.info("Clicked Forgot Password link");
    }

    public void login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickSignIn();
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public boolean isErrorDisplayed() {
        try {
            WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    org.openqa.selenium.By.xpath("//*[contains(@class,'error') or contains(text(),'Invalid') or contains(text(),'incorrect') or contains(text(),'Wrong') or contains(text(),'Error')]")
            ));
            return errorMsg.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getErrorMessage() {
        try {
            WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    org.openqa.selenium.By.xpath("//*[contains(@class,'error') or contains(text(),'Invalid') or contains(text(),'incorrect') or contains(text(),'Wrong')]")
            ));
            return errorMsg.getText();
        } catch (Exception e) {
            return "";
        }
    }
}