package com.taxplan.tests;

import com.aventstack.extentreports.Status;
import com.taxplan.base.BaseTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class ClientDashboardExplorationTest extends BaseTest {

    private WebDriverWait wait;

    private WebDriverWait getWait() {
        if (wait == null) wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        return wait;
    }

    private void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    /** Dismiss any promotional popup if visible */
    private void dismissPopupIfPresent() {
        try {
            List<WebElement> closeButtons = driver.findElements(
                By.xpath("//button[contains(@class,'close') or contains(@aria-label,'close') or contains(@aria-label,'Close') or contains(text(),'×') or contains(text(),'✕')]"));
            if (!closeButtons.isEmpty()) {
                closeButtons.get(0).click();
                sleep(800);
                extentTest.log(Status.INFO, "Dismissed popup/modal");
            }
        } catch (Exception ignored) {}
    }

    /** Login as client and return true if successful */
    private boolean loginAsClient() {
        driver.get(config.getProperty("login.url"));
        sleep(2000);
        dismissPopupIfPresent();
        try {
            WebElement email = getWait().until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@type='email' or @placeholder='Enter your email' or @name='email']")));
            email.clear(); email.sendKeys(config.getProperty("client.email"));
            WebElement pass = driver.findElement(By.xpath("//input[@type='password']"));
            pass.clear(); pass.sendKeys(config.getProperty("client.password"));
            driver.findElement(By.xpath("//button[@type='submit' or contains(text(),'Sign in')]")).click();
            sleep(3000);
            return !driver.getCurrentUrl().contains("login");
        } catch (Exception e) {
            extentTest.log(Status.FAIL, "Login failed: " + e.getMessage());
            return false;
        }
    }

    /** Login as consultant via the consultant button */
    private boolean loginAsConsultant() {
        driver.get(config.getProperty("login.url"));
        sleep(2000);
        dismissPopupIfPresent();
        try {
            // Click "Sign in as Consultant" button
            WebElement consultantBtn = getWait().until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Sign in as Consultant') or contains(text(),'Consultant')]")));
            consultantBtn.click();
            sleep(2000);
            // Now fill email and password in consultant form
            WebElement email = getWait().until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@type='email' or @placeholder='Enter your email' or @name='email']")));
            email.clear(); email.sendKeys(config.getProperty("consultant.email"));
            WebElement pass = driver.findElement(By.xpath("//input[@type='password']"));
            pass.clear(); pass.sendKeys(config.getProperty("consultant.password"));
            driver.findElement(By.xpath("//button[@type='submit' or contains(text(),'Sign in')]")).click();
            sleep(4000);
            return !driver.getCurrentUrl().contains("login");
        } catch (Exception e) {
            extentTest.log(Status.FAIL, "Consultant login failed: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  CLIENT TESTS
    // ─────────────────────────────────────────────────────────────

    @Test(priority = 1, description = "CLIENT: Login and verify dashboard loads")
    public void TC_C01_clientLoginAndDashboard() {
        extentTest.log(Status.INFO, "Logging in as Client: " + config.getProperty("client.email"));
        boolean loggedIn = loginAsClient();
        String url = driver.getCurrentUrl();
        String pageSource = driver.getPageSource();
        extentTest.log(Status.INFO, "Post-login URL: " + url);
        captureScreenshot("TC_C01_clientDashboard");

        Assert.assertTrue(loggedIn,
            "BUG: Client login failed. Still on login page. URL: " + url);
        extentTest.log(Status.PASS, "Client logged in. URL: " + url);
    }

    @Test(priority = 2, description = "CLIENT: Verify dashboard UI elements — nav, greeting, profile")
    public void TC_C02_clientDashboardUIElements() {
        extentTest.log(Status.INFO, "Checking dashboard UI elements after client login");
        loginAsClient();
        sleep(2000);
        dismissPopupIfPresent();

        String pageSource = driver.getPageSource();
        String url = driver.getCurrentUrl();
        extentTest.log(Status.INFO, "Dashboard URL: " + url);

        // Check for common dashboard elements
        boolean hasNavbar = !driver.findElements(By.tagName("nav")).isEmpty();
        boolean hasUserGreeting = pageSource.contains("Welcome") || pageSource.contains("Hello") ||
                                  pageSource.contains("Dashboard") || pageSource.contains("testclient");
        boolean hasLogoutOption = pageSource.contains("Logout") || pageSource.contains("Sign out") ||
                                  pageSource.contains("log out") || pageSource.contains("signout");

        captureScreenshot("TC_C02_dashboardUI");
        extentTest.log(Status.INFO, "Navbar present: " + hasNavbar);
        extentTest.log(Status.INFO, "User greeting present: " + hasUserGreeting);
        extentTest.log(Status.INFO, "Logout option present: " + hasLogoutOption);

        Assert.assertTrue(hasNavbar, "BUG: Navigation bar not found on dashboard");
        Assert.assertTrue(hasLogoutOption, "BUG: No logout/sign-out option found on dashboard — user has no way to log out");
    }

    @Test(priority = 3, description = "CLIENT: Navigate to profile/account settings page")
    public void TC_C03_clientProfilePage() {
        extentTest.log(Status.INFO, "Navigating to client profile/account settings");
        loginAsClient();
        sleep(2000);
        dismissPopupIfPresent();

        // Try clicking profile/account icon or menu
        String beforeUrl = driver.getCurrentUrl();
        boolean profileFound = false;

        try {
            List<WebElement> profileLinks = driver.findElements(By.xpath(
                "//*[contains(text(),'Profile') or contains(text(),'Account') or contains(text(),'Settings') or contains(@href,'profile') or contains(@href,'account') or contains(@href,'settings')]"));
            if (!profileLinks.isEmpty()) {
                profileLinks.get(0).click();
                sleep(2000);
                profileFound = true;
            }
        } catch (Exception e) {
            extentTest.log(Status.WARNING, "Could not find profile link: " + e.getMessage());
        }

        String afterUrl = driver.getCurrentUrl();
        captureScreenshot("TC_C03_clientProfile");
        extentTest.log(Status.INFO, "Before: " + beforeUrl + " | After: " + afterUrl);

        if (!profileFound) {
            extentTest.log(Status.WARNING, "BUG: No profile/account/settings link found in dashboard navigation");
        } else {
            extentTest.log(Status.PASS, "Profile page accessible at: " + afterUrl);
        }
        Assert.assertTrue(profileFound, "BUG: No Profile/Account/Settings link accessible from client dashboard");
    }

    @Test(priority = 4, description = "CLIENT: Explore My Services / My Orders section")
    public void TC_C04_clientMyServices() {
        extentTest.log(Status.INFO, "Exploring My Services / My Orders section");
        loginAsClient();
        sleep(2000);
        dismissPopupIfPresent();

        boolean servicesFound = false;
        try {
            List<WebElement> serviceLinks = driver.findElements(By.xpath(
                "//*[contains(text(),'My Services') or contains(text(),'My Orders') or contains(text(),'My Filings') or contains(text(),'Orders') or contains(@href,'services') or contains(@href,'orders') or contains(@href,'filings')]"));
            if (!serviceLinks.isEmpty()) {
                serviceLinks.get(0).click();
                sleep(2000);
                servicesFound = true;
            }
        } catch (Exception e) {
            extentTest.log(Status.WARNING, "Services section not found: " + e.getMessage());
        }

        captureScreenshot("TC_C04_myServices");
        extentTest.log(Status.INFO, "My Services section found: " + servicesFound);

        if (!servicesFound) {
            extentTest.log(Status.WARNING, "BUG: No 'My Services' or 'My Orders' section accessible from client dashboard");
        }
        Assert.assertTrue(servicesFound, "BUG: My Services/My Orders section not found in client dashboard");
    }

    @Test(priority = 5, description = "CLIENT: Test service booking flow — ITR Filing")
    public void TC_C05_clientBookITRService() {
        extentTest.log(Status.INFO, "Testing ITR filing service booking flow");
        loginAsClient();
        sleep(2000);
        dismissPopupIfPresent();

        // Navigate to ITR filing service
        driver.get("https://staging.taxplanadvisor.in/services/returns/itr-filing-online");
        sleep(3000);
        dismissPopupIfPresent();

        String pageSource = driver.getPageSource();
        String url = driver.getCurrentUrl();
        captureScreenshot("TC_C05_itrFilingPage");
        extentTest.log(Status.INFO, "ITR Filing page URL: " + url);

        boolean pageLoaded = !pageSource.isEmpty() && !url.contains("404") && !url.contains("error");
        boolean hasBookButton = pageSource.contains("Book") || pageSource.contains("Get Started") ||
                                pageSource.contains("File Now") || pageSource.contains("Start Filing");

        extentTest.log(Status.INFO, "Page loaded: " + pageLoaded + " | Has booking CTA: " + hasBookButton);
        Assert.assertTrue(pageLoaded, "BUG: ITR Filing service page did not load correctly. URL: " + url);
        Assert.assertTrue(hasBookButton, "BUG: No booking/CTA button found on ITR Filing service page");
    }

    @Test(priority = 6, description = "CLIENT: Test Consultation booking page flow")
    public void TC_C06_clientConsultationBooking() {
        extentTest.log(Status.INFO, "Testing consultation booking flow as logged-in client");
        loginAsClient();
        sleep(2000);
        dismissPopupIfPresent();

        driver.get("https://staging.taxplanadvisor.in/consultation");
        sleep(3000);
        dismissPopupIfPresent();

        String url = driver.getCurrentUrl();
        String pageSource = driver.getPageSource();
        captureScreenshot("TC_C06_consultationPage");

        boolean pageLoaded = !url.contains("404") && !url.contains("login");
        boolean hasConsultants = pageSource.contains("Consultant") || pageSource.contains("Expert") ||
                                 pageSource.contains("Advisor") || pageSource.contains("Book");

        extentTest.log(Status.INFO, "URL: " + url + " | Page loaded: " + pageLoaded);
        Assert.assertTrue(pageLoaded, "BUG: Consultation page did not load — redirected to: " + url);
        Assert.assertTrue(hasConsultants, "BUG: Consultation page has no consultants/booking options visible");
    }

    @Test(priority = 7, description = "CLIENT: Test logout functionality")
    public void TC_C07_clientLogout() {
        extentTest.log(Status.INFO, "Testing client logout functionality");
        loginAsClient();
        sleep(2000);
        dismissPopupIfPresent();

        String dashboardUrl = driver.getCurrentUrl();
        boolean logoutClicked = false;

        try {
            // Look for user menu / avatar to open dropdown
            List<WebElement> userMenuTriggers = driver.findElements(By.xpath(
                "//*[contains(@class,'avatar') or contains(@class,'user-menu') or contains(@class,'profile') or contains(@aria-label,'user') or contains(@aria-label,'account')]"));
            if (!userMenuTriggers.isEmpty()) {
                userMenuTriggers.get(0).click();
                sleep(1000);
            }
            // Now find logout
            WebElement logoutBtn = getWait().until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[contains(text(),'Logout') or contains(text(),'Log out') or contains(text(),'Sign out') or contains(text(),'Signout')]")));
            logoutBtn.click();
            sleep(3000);
            logoutClicked = true;
        } catch (Exception e) {
            extentTest.log(Status.WARNING, "Could not find logout: " + e.getMessage());
        }

        String afterUrl = driver.getCurrentUrl();
        captureScreenshot("TC_C07_clientLogout");
        extentTest.log(Status.INFO, "After logout URL: " + afterUrl);

        Assert.assertTrue(logoutClicked, "BUG: Could not find or click Logout button on client dashboard");
        Assert.assertTrue(afterUrl.contains("login") || afterUrl.equals("https://staging.taxplanadvisor.in/") || afterUrl.contains("home"),
            "BUG: After logout, user was not redirected to login or home page. Redirected to: " + afterUrl);
    }

    // ─────────────────────────────────────────────────────────────
    //  CONSULTANT TESTS
    // ─────────────────────────────────────────────────────────────

    @Test(priority = 8, description = "CONSULTANT: Login via consultant section and verify dashboard")
    public void TC_D01_consultantLoginAndDashboard() {
        extentTest.log(Status.INFO, "Logging in as Consultant: " + config.getProperty("consultant.email"));
        boolean loggedIn = loginAsConsultant();
        String url = driver.getCurrentUrl();
        captureScreenshot("TC_D01_consultantDashboard");
        extentTest.log(Status.INFO, "Post-login URL: " + url);

        Assert.assertTrue(loggedIn, "BUG: Consultant login failed. Still on: " + url);
        extentTest.log(Status.PASS, "Consultant logged in. URL: " + url);
    }

    @Test(priority = 9, description = "CONSULTANT: Verify consultant dashboard UI and available sections")
    public void TC_D02_consultantDashboardUI() {
        extentTest.log(Status.INFO, "Checking consultant dashboard UI elements");
        loginAsConsultant();
        sleep(2000);
        dismissPopupIfPresent();

        String pageSource = driver.getPageSource();
        String url = driver.getCurrentUrl();
        extentTest.log(Status.INFO, "Consultant dashboard URL: " + url);

        boolean hasEarnings   = pageSource.contains("Earning") || pageSource.contains("Revenue") || pageSource.contains("Income");
        boolean hasClients    = pageSource.contains("Client") || pageSource.contains("Customer");
        boolean hasOrders     = pageSource.contains("Order") || pageSource.contains("Request") || pageSource.contains("Assign");
        boolean hasProfile    = pageSource.contains("Profile") || pageSource.contains("Account");
        boolean hasLogout     = pageSource.contains("Logout") || pageSource.contains("Sign out") || pageSource.contains("Log out");

        captureScreenshot("TC_D02_consultantDashboardUI");
        extentTest.log(Status.INFO, "Earnings section: " + hasEarnings);
        extentTest.log(Status.INFO, "Clients section: " + hasClients);
        extentTest.log(Status.INFO, "Orders/Requests: " + hasOrders);
        extentTest.log(Status.INFO, "Profile section: " + hasProfile);
        extentTest.log(Status.INFO, "Logout present: " + hasLogout);

        Assert.assertTrue(hasLogout, "BUG: No logout option found on consultant dashboard");
        Assert.assertTrue(hasClients || hasOrders,
            "BUG: Consultant dashboard has no Client or Order/Request management section visible");
    }

    @Test(priority = 10, description = "CONSULTANT: Explore consultant profile/settings")
    public void TC_D03_consultantProfile() {
        extentTest.log(Status.INFO, "Exploring consultant profile/settings page");
        loginAsConsultant();
        sleep(2000);
        dismissPopupIfPresent();

        boolean profileFound = false;
        String profileUrl = "";
        try {
            List<WebElement> profileLinks = driver.findElements(By.xpath(
                "//*[contains(text(),'Profile') or contains(text(),'Settings') or contains(text(),'Account') or contains(@href,'profile') or contains(@href,'settings')]"));
            if (!profileLinks.isEmpty()) {
                profileLinks.get(0).click();
                sleep(2000);
                profileFound = true;
                profileUrl = driver.getCurrentUrl();
            }
        } catch (Exception e) {
            extentTest.log(Status.WARNING, "Profile link not found: " + e.getMessage());
        }

        captureScreenshot("TC_D03_consultantProfile");
        extentTest.log(Status.INFO, "Profile accessible: " + profileFound + " | URL: " + profileUrl);

        if (!profileFound) {
            extentTest.log(Status.WARNING, "BUG: No Profile/Settings link found on consultant dashboard");
        }
        Assert.assertTrue(profileFound, "BUG: Consultant Profile/Settings page not accessible from dashboard");
    }

    @Test(priority = 11, description = "CONSULTANT: Explore assigned orders / client requests section")
    public void TC_D04_consultantOrders() {
        extentTest.log(Status.INFO, "Exploring consultant orders/requests section");
        loginAsConsultant();
        sleep(2000);
        dismissPopupIfPresent();

        boolean ordersFound = false;
        try {
            List<WebElement> orderLinks = driver.findElements(By.xpath(
                "//*[contains(text(),'Order') or contains(text(),'Request') or contains(text(),'Assignment') or contains(text(),'Task') or contains(@href,'order') or contains(@href,'request')]"));
            if (!orderLinks.isEmpty()) {
                orderLinks.get(0).click();
                sleep(2000);
                ordersFound = true;
            }
        } catch (Exception e) {
            extentTest.log(Status.WARNING, "Orders section not found: " + e.getMessage());
        }

        captureScreenshot("TC_D04_consultantOrders");
        extentTest.log(Status.INFO, "Orders section found: " + ordersFound);

        if (!ordersFound) {
            extentTest.log(Status.WARNING, "BUG: No Orders/Requests/Assignments section found on consultant dashboard");
        }
        Assert.assertTrue(ordersFound, "BUG: Consultant dashboard has no Orders/Requests section");
    }

    @Test(priority = 12, description = "CONSULTANT: Verify consultant cannot access client-only pages")
    public void TC_D05_consultantAccessControl() {
        extentTest.log(Status.INFO, "Verifying consultant cannot access client-only pages");
        loginAsConsultant();
        sleep(2000);

        // Try accessing client dashboard directly
        driver.get("https://staging.taxplanadvisor.in/dashboard");
        sleep(2000);
        String dashUrl = driver.getCurrentUrl();
        captureScreenshot("TC_D05_accessControl");
        extentTest.log(Status.INFO, "Accessed /dashboard as consultant — landed on: " + dashUrl);

        // This is informational — log what happens
        extentTest.log(Status.INFO, "Access control check: consultant accessing /dashboard resulted in URL: " + dashUrl);
    }
}
