package com.taxplan.tests;

import com.aventstack.extentreports.Status;
import com.taxplan.base.BaseTest;
import com.taxplan.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    private LoginPage loginPage;

    @Test(priority = 1, description = "Verify login page loads correctly with all required elements")
    public void TC001_verifyLoginPageElements() {
        extentTest.log(Status.INFO, "Test Case: Verify login page elements");
        loginPage = new LoginPage(driver);
        loginPage.navigateToLoginPage(config.getProperty("login.url"));

        Assert.assertTrue(loginPage.isLoginPageDisplayed(), "Login page header not displayed");
        extentTest.log(Status.PASS, "Login page header is displayed");

        Assert.assertTrue(loginPage.getCurrentUrl().contains("login"), "URL does not contain 'login'");
        extentTest.log(Status.PASS, "Login page URL is correct");
    }

    @Test(priority = 2, description = "Verify client can login with valid credentials")
    public void TC002_clientLoginWithValidCredentials() {
        extentTest.log(Status.INFO, "Test Case: Client login with valid credentials");
        loginPage = new LoginPage(driver);
        loginPage.navigateToLoginPage(config.getProperty("login.url"));

        loginPage.login(config.getProperty("client.email"), config.getProperty("client.password"));

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String currentUrl = loginPage.getCurrentUrl();
        extentTest.log(Status.INFO, "Post-login URL: " + currentUrl);

        Assert.assertFalse(currentUrl.contains("login"),
                "BUG: Client login failed - still on login page. URL: " + currentUrl);
        extentTest.log(Status.PASS, "Client login successful. Redirected to: " + currentUrl);
    }

    @Test(priority = 3, description = "Verify consultant can login with valid credentials")
    public void TC003_consultantLoginWithValidCredentials() {
        extentTest.log(Status.INFO, "Test Case: Consultant login with valid credentials");
        loginPage = new LoginPage(driver);
        loginPage.navigateToLoginPage(config.getProperty("login.url"));

        loginPage.login(config.getProperty("consultant.email"), config.getProperty("consultant.password"));

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String currentUrl = loginPage.getCurrentUrl();
        extentTest.log(Status.INFO, "Post-login URL: " + currentUrl);

        Assert.assertFalse(currentUrl.contains("login"),
                "BUG: Consultant login failed - still on login page. URL: " + currentUrl);
        extentTest.log(Status.PASS, "Consultant login successful. Redirected to: " + currentUrl);
    }

    @Test(priority = 4, description = "Verify login fails with invalid password and shows error message")
    public void TC004_loginWithInvalidPassword() {
        extentTest.log(Status.INFO, "Test Case: Login with invalid password");
        loginPage = new LoginPage(driver);
        loginPage.navigateToLoginPage(config.getProperty("login.url"));

        loginPage.login(config.getProperty("client.email"), "WrongPassword@999");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        boolean errorDisplayed = loginPage.isErrorDisplayed();
        String errorMessage = loginPage.getErrorMessage();
        extentTest.log(Status.INFO, "Error displayed: " + errorDisplayed + ", Message: " + errorMessage);

        Assert.assertTrue(errorDisplayed,
                "BUG: No error message displayed for invalid password. User should not be able to login with wrong password.");
        extentTest.log(Status.PASS, "Error message displayed for invalid password: " + errorMessage);
    }

    @Test(priority = 5, description = "Verify login fails with invalid email and shows error message")
    public void TC005_loginWithInvalidEmail() {
        extentTest.log(Status.INFO, "Test Case: Login with invalid email");
        loginPage = new LoginPage(driver);
        loginPage.navigateToLoginPage(config.getProperty("login.url"));

        loginPage.login("nonexistentuser@invalid.com", config.getProperty("client.password"));

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        boolean errorDisplayed = loginPage.isErrorDisplayed();
        extentTest.log(Status.INFO, "Error displayed: " + errorDisplayed);

        Assert.assertTrue(errorDisplayed,
                "BUG: No error message displayed for invalid/non-existent email.");
        extentTest.log(Status.PASS, "Error message displayed for invalid email");
    }

    @Test(priority = 6, description = "Verify login with empty email shows validation error")
    public void TC006_loginWithEmptyEmail() {
        extentTest.log(Status.INFO, "Test Case: Login with empty email");
        loginPage = new LoginPage(driver);
        loginPage.navigateToLoginPage(config.getProperty("login.url"));

        loginPage.enterPassword(config.getProperty("client.password"));
        loginPage.clickSignIn();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Assert.assertTrue(loginPage.isLoginPageDisplayed(),
                "BUG: Login should not proceed with empty email field");
        extentTest.log(Status.PASS, "Login correctly blocked with empty email");
    }

    @Test(priority = 7, description = "Verify login with empty password shows validation error")
    public void TC007_loginWithEmptyPassword() {
        extentTest.log(Status.INFO, "Test Case: Login with empty password");
        loginPage = new LoginPage(driver);
        loginPage.navigateToLoginPage(config.getProperty("login.url"));

        loginPage.enterEmail(config.getProperty("client.email"));
        loginPage.clickSignIn();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Assert.assertTrue(loginPage.isLoginPageDisplayed(),
                "BUG: Login should not proceed with empty password field");
        extentTest.log(Status.PASS, "Login correctly blocked with empty password");
    }

    @Test(priority = 8, description = "Verify forgot password link works")
    public void TC008_verifyForgotPasswordLink() {
        extentTest.log(Status.INFO, "Test Case: Verify forgot password link");
        loginPage = new LoginPage(driver);
        loginPage.navigateToLoginPage(config.getProperty("login.url"));

        loginPage.clickForgotPassword();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String currentUrl = loginPage.getCurrentUrl();
        extentTest.log(Status.INFO, "URL after clicking forgot password: " + currentUrl);

        Assert.assertFalse(currentUrl.contains("login"),
                "BUG: Forgot password link did not navigate away from login page. URL: " + currentUrl);
        extentTest.log(Status.PASS, "Forgot password link navigated to: " + currentUrl);
    }

    @Test(priority = 9, description = "Verify consultant access link works")
    public void TC009_verifyConsultantAccessLink() {
        extentTest.log(Status.INFO, "Test Case: Verify consultant access link");
        loginPage = new LoginPage(driver);
        loginPage.navigateToLoginPage(config.getProperty("login.url"));

        loginPage.clickConsultantAccess();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String currentUrl = loginPage.getCurrentUrl();
        extentTest.log(Status.INFO, "URL after clicking consultant access: " + currentUrl);

        Assert.assertTrue(currentUrl.contains("consultant"),
                "BUG: Consultant Access link did not navigate to consultant page. URL: " + currentUrl);
        extentTest.log(Status.PASS, "Consultant Access link navigated to: " + currentUrl);
    }

    @Test(priority = 10, description = "Verify login with SQL injection attempt is blocked")
    public void TC010_loginWithSQLInjection() {
        extentTest.log(Status.INFO, "Test Case: Login with SQL injection attempt");
        loginPage = new LoginPage(driver);
        loginPage.navigateToLoginPage(config.getProperty("login.url"));

        loginPage.login("' OR '1'='1", "' OR '1'='1");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String currentUrl = loginPage.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("login"),
                "BUG: SQL injection attempt may have bypassed authentication! URL: " + currentUrl);
        extentTest.log(Status.PASS, "SQL injection attempt correctly blocked");
    }
}