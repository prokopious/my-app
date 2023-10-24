package util;
import io.github.bonigarcia.wdm.WebDriverManager;
import util.TestUtil;

import java.net.URL;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.*;

public class TestBase {

    protected WebDriver driver;
    protected TestUtil testUtil;

    @BeforeMethod
    public void setUp() {
    	
        WebDriver driver = null;
        TestUtil testUtil;
        String os = System.getProperty("os.name").toLowerCase(); // Identify OS

        try {
            if (os.contains("linux")) {
                // Setup for Linux and Docker
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--headless");
                options.addArguments("--disable-gpu");
                options.addArguments("--window-size=1920,1200");

                // Define URL of the remote WebDriver (your Docker container with Selenium standalone server)
                URL remoteAddress = new URL("http://172.31.26.241:4444/wd/hub"); // Adjust the URL if needed

                // Use RemoteWebDriver for Docker setup
                driver = new RemoteWebDriver(remoteAddress, options);
            } else {
                // Setup for local execution
                String projectPath = System.getProperty("user.dir");
                System.setProperty("webdriver.chrome.driver", projectPath + "/chromedriver.exe");

                ChromeOptions options = new ChromeOptions();
                options.addArguments("--headless");
                options.addArguments("--disable-gpu");
                options.addArguments("--window-size=1920,1200");

                driver = new ChromeDriver(options);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // If an exception occurs, you should handle it based on your test workflow.
            // You might want to fail the test, log the error, or have other handling logic.
            return;
        }
          testUtil = new TestUtil(driver);
    }

    @AfterMethod
    public void tearDown() {
        // Close the browser after each test.
        if (driver != null) {
            driver.quit();
        }
    }
}
