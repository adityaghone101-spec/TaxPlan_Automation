package com.taxplan.tests;

import com.aventstack.extentreports.Status;
import com.taxplan.base.BaseTest;
import com.taxplan.pages.HomePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class HomePageTest extends BaseTest {

    private HomePage homePage;

    @Test(priority = 1, description = "Verify home page loads with correct title and key elements")
    public void TC011_verifyHomePageLoads() {
        extentTest.log(Status.INFO, "Test Case: Verify home page loads correctly");
        homePage = new HomePage(driver);
        homePage.navigateToHomePage(config.getProperty("url"));

        String title = homePage.getPageTitle();
        extentTest.log(Status.INFO, "Page title: " + title);

        Assert.assertNotNull(title, "BUG: Page title is null");
        Assert.assertFalse(title.isEmpty(), "BUG: Page title is empty");
        extentTest.log(Status.PASS, "Home page loaded with title: " + title);
    }

    @Test(priority = 2, description = "Verify all main service categories are displayed on home page")
    public void TC012_verifyServiceCategoriesDisplayed() {
        extentTest.log(Status.INFO, "Test Case: Verify service categories");
        homePage = new HomePage(driver);
        homePage.navigateToHomePage(config.getProperty("url"));

        String[] services = {"ITR Filing", "GST Filing", "TDS Filing", "Notice Handling", "Business Registration"};

        for (String service : services) {
            boolean isDisplayed = homePage.isServiceDisplayed(service);
            extentTest.log(Status.INFO, "Service '" + service + "' displayed: " + isDisplayed);
            Assert.assertTrue(isDisplayed, "BUG: Service category '" + service + "' is not displayed on home page");
        }
        extentTest.log(Status.PASS, "All main service categories are displayed");
    }

    @Test(priority = 3, description = "Verify File ITR button is clickable and navigates correctly")
    public void TC013_verifyFileITRButton() {
        extentTest.log(Status.INFO, "Test Case: Verify File ITR button");
        homePage = new HomePage(driver);
        homePage.navigateToHomePage(config.getProperty("url"));

        String beforeUrl = homePage.getCurrentUrl();
        homePage.clickFileITR();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String afterUrl = homePage.getCurrentUrl();
        extentTest.log(Status.INFO, "Before URL: " + beforeUrl + ", After URL: " + afterUrl);

        Assert.assertNotEquals(beforeUrl, afterUrl,
                "BUG: File ITR button click did not navigate to a new page");
        extentTest.log(Status.PASS, "File ITR button navigated to: " + afterUrl);
    }

    @Test(priority = 4, description = "Verify View Filing Services link works")
    public void TC014_verifyViewFilingServicesLink() {
        extentTest.log(Status.INFO, "Test Case: Verify View Filing Services link");
        homePage = new HomePage(driver);
        homePage.navigateToHomePage(config.getProperty("url"));

        String beforeUrl = homePage.getCurrentUrl();
        homePage.clickViewFilingServices();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String afterUrl = homePage.getCurrentUrl();
        extentTest.log(Status.INFO, "Before URL: " + beforeUrl + ", After URL: " + afterUrl);

        Assert.assertNotEquals(beforeUrl, afterUrl,
                "BUG: View Filing Services link did not navigate to a new page");
        extentTest.log(Status.PASS, "View Filing Services link navigated to: " + afterUrl);
    }

    @Test(priority = 5, description = "Verify consultant cards are displayed on home page")
    public void TC015_verifyConsultantCardsDisplayed() {
        extentTest.log(Status.INFO, "Test Case: Verify consultant cards");
        homePage = new HomePage(driver);
        homePage.navigateToHomePage(config.getProperty("url"));

        int cardCount = homePage.getConsultantCardCount();
        extentTest.log(Status.INFO, "Consultant cards found: " + cardCount);

        Assert.assertTrue(cardCount > 0, "BUG: No consultant cards displayed on home page");
        extentTest.log(Status.PASS, "Consultant cards are displayed. Count: " + cardCount);
    }

    @Test(priority = 6, description = "Verify Refer Now link works")
    public void TC016_verifyReferNowLink() {
        extentTest.log(Status.INFO, "Test Case: Verify Refer Now link");
        homePage = new HomePage(driver);
        homePage.navigateToHomePage(config.getProperty("url"));

        String beforeUrl = homePage.getCurrentUrl();
        homePage.clickReferNow();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String afterUrl = homePage.getCurrentUrl();
        extentTest.log(Status.INFO, "Before URL: " + beforeUrl + ", After URL: " + afterUrl);

        Assert.assertTrue(afterUrl.contains("refer"),
                "BUG: Refer Now link did not navigate to refer page. URL: " + afterUrl);
        extentTest.log(Status.PASS, "Refer Now link navigated to: " + afterUrl);
    }

    @Test(priority = 7, description = "Verify all navigation links are functional")
    public void TC017_verifyAllLinksAreFunctional() {
        extentTest.log(Status.INFO, "Test Case: Verify all links on home page");
        homePage = new HomePage(driver);
        homePage.navigateToHomePage(config.getProperty("url"));

        List<WebElement> links = driver.findElements(By.tagName("a"));
        extentTest.log(Status.INFO, "Total links found: " + links.size());

        Assert.assertTrue(links.size() > 0, "BUG: No links found on home page");
        extentTest.log(Status.PASS, "Home page contains " + links.size() + " links");
    }

    @Test(priority = 8, description = "Verify promotion code banner is displayed")
    public void TC018_verifyPromoCodeBanner() {
        extentTest.log(Status.INFO, "Test Case: Verify promotion code banner");
        homePage = new HomePage(driver);
        homePage.navigateToHomePage(config.getProperty("url"));

        boolean isPromoDisplayed = homePage.isServiceDisplayed("TAXPLAN10");
        extentTest.log(Status.INFO, "Promo code TAXPLAN10 displayed: " + isPromoDisplayed);

        Assert.assertTrue(isPromoDisplayed, "BUG: Promotion code banner with TAXPLAN10 is not displayed");
        extentTest.log(Status.PASS, "Promotion code banner is displayed");
    }

    @Test(priority = 9, description = "Verify consultation button works")
    public void TC019_verifyConsultationButton() {
        extentTest.log(Status.INFO, "Test Case: Verify Consultation button");
        homePage = new HomePage(driver);
        homePage.navigateToHomePage(config.getProperty("url"));

        String beforeUrl = homePage.getCurrentUrl();
        homePage.clickConsultation();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String afterUrl = homePage.getCurrentUrl();
        extentTest.log(Status.INFO, "Before URL: " + beforeUrl + ", After URL: " + afterUrl);

        Assert.assertNotEquals(beforeUrl, afterUrl,
                "BUG: Consultation button click did not navigate to a new page");
        extentTest.log(Status.PASS, "Consultation button navigated to: " + afterUrl);
    }

    @Test(priority = 10, description = "Verify footer disclaimer text is present")
    public void TC020_verifyFooterDisclaimer() {
        extentTest.log(Status.INFO, "Test Case: Verify footer disclaimer");
        homePage = new HomePage(driver);
        homePage.navigateToHomePage(config.getProperty("url"));

        boolean disclaimerPresent = homePage.isServiceDisplayed("not affiliated");
        extentTest.log(Status.INFO, "Footer disclaimer present: " + disclaimerPresent);

        Assert.assertTrue(disclaimerPresent, "BUG: Footer disclaimer about GST affiliation is missing");
        extentTest.log(Status.PASS, "Footer disclaimer is present");
    }
}
