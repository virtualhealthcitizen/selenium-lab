package org.virtualhealthcitizen.java.spring.maven.selenium.seleniumlab;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * React app
 */
class PortfolioWebsiteTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private String appUrl;

    @BeforeAll
    static void setupBinary() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setup() {
        appUrl = System.getProperty("appUrl", "http://localhost:3000");

        ChromeOptions options = new ChromeOptions();
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "true"));
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1400,1000");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(appUrl);
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        Thread.sleep(1000); // Leaves page open briefly after actions are performed
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void shouldNavigateToAboutFromHeader() {
        WebElement aboutBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("[data-testid='nav-about']")));
        aboutBtn.click();

        wait.until(ExpectedConditions.urlContains("/about"));
        assertTrue(driver.getCurrentUrl().contains("/about"));
    }

    @Test
    void shouldOpenMobileDrawer() {
        driver.manage().window().setSize(new Dimension(390, 844)); // mobile-ish viewport
        driver.get(appUrl);

        WebElement menuBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("[data-testid='menu-open-btn']")));
        menuBtn.click();

        // More reliable than asserting Drawer root visibility (portal/modal structure)
        WebElement skillsItem = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//li//span[normalize-space()='Skills / Certs']")));
        assertTrue(skillsItem.isDisplayed());
    }

    @Test
    void shouldGoHomeWhenClickingSkillsShortcut() {
        driver.get(appUrl + "/about");

        WebElement skillsBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("[data-testid='nav-skills']")));
        skillsBtn.click();

        wait.until(ExpectedConditions.urlToBe(appUrl + "/"));
        WebElement skillsSection = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("[data-testid='skills-section']")));
        assertTrue(skillsSection.isDisplayed());
    }

    @Test
    void shouldFilterSkillsBySearchText() {
        driver.get(appUrl + "/");
        WebElement search = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("input[data-testid='skills-search-input']")));

        search.sendKeys("react");
        openFrameworksAccordion();

        // At least one matching skill should remain visible
        WebElement reactSkill = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("[data-testid='skill-react']")));
        assertTrue(reactSkill.isDisplayed());
    }

    @Test
    void shouldFilterSkillsByTagText() {
        driver.get(appUrl + "/");
        WebElement search = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("input[data-testid='skills-search-input']")));

        search.sendKeys("frontend"); // pick a real tag from your skillsData
        openFrameworksAccordion();

        WebElement matchedSkill = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("[data-testid='skill-react']"))); // adjust to real expected skill
        assertTrue(matchedSkill.isDisplayed());
    }

    @Test
    void shouldFilterByCategory() {
        driver.get(appUrl + "/");

        // Open the MUI select trigger
        WebElement categoryTrigger = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("[data-testid='skills-category-select'] [role='combobox']")));
        categoryTrigger.click();

        // Wait until listbox is actually rendered and visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("ul[role='listbox']")));

        // Select by stable test-id (no text dependency)
        WebElement frontendOption = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("li[data-testid='skills-category-option-frameworks-/-libraries']")));
        frontendOption.click();

        WebElement frontendAccordion = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("[data-testid='skills-accordion-frameworks-/-libraries']")));
        assertTrue(frontendAccordion.isDisplayed());

        assertTrue(driver.findElements(By.cssSelector("[data-testid='skills-accordion-languages']")).isEmpty());
    }

    @Test
    void shouldExpandAndCollapseAccordion() {
        driver.get(appUrl + "/");

        WebElement summary = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("[data-testid='skills-summary-frameworks-/-libraries']")));
        summary.click();

        // Since AccordionDetails has no test-id, assert by expanded state on accordion root
        wait.until(ExpectedConditions.attributeContains(
            By.cssSelector("[data-testid='skills-accordion-frameworks-/-libraries']"),
            "class",
            "Mui-expanded"
        ));

        summary.click();

        // After collapsing, expanded class should be removed
        wait.until(ExpectedConditions.not(
            ExpectedConditions.attributeContains(
                By.cssSelector("[data-testid='skills-accordion-frameworks-/-libraries']"),
                "class",
                "Mui-expanded"
            )
        ));
    }

    @Test
    void shouldApplyCategoryAndSearchTogether() {
        driver.get(appUrl + "/");

        WebElement categoryTrigger = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("[data-testid='skills-category-select'] [role='combobox']")));
        categoryTrigger.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("ul[role='listbox']")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("li[data-testid='skills-category-option-frameworks-/-libraries']"))).click();

        WebElement search = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("input[data-testid='skills-search-input']")));

        search.sendKeys("react");
        openFrameworksAccordion();

        WebElement reactSkill = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("[data-testid='skill-react']")));
        assertTrue(reactSkill.isDisplayed());

        // Example of a non-match in filtered context
        assertTrue(driver.findElements(By.cssSelector("[data-testid='skill-java']")).isEmpty());
    }

    private void openFrameworksAccordion() {
        WebElement summary = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("[data-testid='skills-summary-frameworks-/-libraries']")));
        summary.click();

        wait.until(ExpectedConditions.attributeContains(
            By.cssSelector("[data-testid='skills-accordion-frameworks-/-libraries']"),
            "class",
            "Mui-expanded"
        ));
    }

}
