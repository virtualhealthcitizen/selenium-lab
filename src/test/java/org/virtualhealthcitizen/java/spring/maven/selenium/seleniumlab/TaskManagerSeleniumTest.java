package org.virtualhealthcitizen.java.spring.maven.selenium.seleniumlab;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskManagerSeleniumTest {

    @LocalServerPort
    int port;

    private WebDriver driver;
    private String baseUrl;

    @BeforeAll
    static void setupDriverBinary() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setup() {
        ChromeOptions options = new ChromeOptions();
        // Headless is usually best for CI and local repeatability.
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "true"));
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1400,1000");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        baseUrl = "http://localhost:" + port;
        driver.get(baseUrl + "/");
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        Thread.sleep(1000); // Leaves page open briefly after actions are performed
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    void shouldAddTaskAndUpdateTotalCount() {
        WebElement input = driver.findElement(By.id("task-input"));
        WebElement priority = driver.findElement(By.id("priority-select"));
        WebElement addBtn = driver.findElement(By.id("add-btn"));

        input.sendKeys("Buy groceries");
        priority.sendKeys("high");
        addBtn.click();

        // Seed data starts at 3 tasks, adding one should make total 4.
        String total = driver.findElement(By.id("stat-total")).getText();
        assertEquals("4", total);

        String status = driver.findElement(By.id("status-message")).getText();
        assertTrue(status.contains("Task added"));
    }

    @Test
    @Order(2)
    void shouldToggleTaskDoneAndUpdateDoneCount() {
        // Toggle first seed task (#task-1) done.
        driver.findElement(By.id("check-1")).click();

        String doneCount = driver.findElement(By.id("stat-done")).getText();
        assertEquals("1", doneCount);

        String status = driver.findElement(By.id("status-message")).getText();
        assertTrue(status.contains("Completed"));
    }

    @Test
    @Order(3)
    void shouldDeleteTaskAndUpdateTotalCount() {
        // Delete first seed task (#task-1), total should go 3 -> 2.
        driver.findElement(By.id("delete-1")).click();

        String total = driver.findElement(By.id("stat-total")).getText();
        assertEquals("2", total);

        String status = driver.findElement(By.id("status-message")).getText();
        assertTrue(status.contains("Deleted"));
    }

    @Test
    @Order(4)
    void shouldRejectEmptyTaskInput() {
        driver.findElement(By.id("add-btn")).click();

        String total = driver.findElement(By.id("stat-total")).getText();
        assertEquals("3", total);

        String status = driver.findElement(By.id("status-message")).getText();
        assertTrue(status.contains("Please enter a task name."));
    }

    @Test
    @Order(5)
    void shouldFilterActiveAndDoneTasks() {
        driver.findElement(By.id("check-1")).click(); // mark done

        driver.findElement(By.id("filter-active")).click();
        assertTrue(driver.findElements(By.id("task-1")).isEmpty());

        driver.findElement(By.id("filter-done")).click();
        assertEquals(1, driver.findElements(By.id("task-1")).size());
    }

    @Test
    @Order(6)
    void shouldSearchTasksByKeyword() {
        WebElement search = driver.findElement(By.id("search-input"));
        search.sendKeys("documentation");

        assertEquals(1, driver.findElements(By.id("task-3")).size());
        assertTrue(driver.findElements(By.id("task-1")).isEmpty());
        assertTrue(driver.findElements(By.id("task-2")).isEmpty());

        search.clear();
        search.sendKeys(" ");
        search.sendKeys(Keys.BACK_SPACE);

        assertEquals(3, driver.findElements(By.cssSelector("#task-list .task-item")).size());
    }

    @Test
    @Order(7)
    void shouldClearOnlyDoneTasks() {
        driver.findElement(By.id("check-1")).click();
        driver.findElement(By.id("check-2")).click();

        driver.findElement(By.id("clear-btn")).click();

        assertEquals("1", driver.findElement(By.id("stat-total")).getText());
        assertEquals("0", driver.findElement(By.id("stat-done")).getText());

        String status = driver.findElement(By.id("status-message")).getText();
        assertTrue(status.contains("Cleared"));
    }

    @Test
    @Order(8)
    void shouldShowEmptyStateWhenSearchHasNoMatches() {
        driver.findElement(By.id("search-input")).sendKeys("zzzz-not-found");

        String display = driver.findElement(By.id("empty-state")).getCssValue("display");
        assertEquals("block", display);
    }

}
