package util;
import io.github.bonigarcia.wdm.WebDriverManager;
import util.TestUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.*;

public class TestBase {

    protected WebDriver driver;
    protected TestUtil testUtil;

    @BeforeMethod
    public void setUp() {
    	
    	  String projectPath = System.getProperty("user.dir"); // This gets us the project's root folder
          System.setProperty("webdriver.chrome.driver", projectPath + "/chromedriver.exe"); // This sets the location of the driver

          // Now you can initialize your ChromeDriver without WebDriverManager
          ChromeOptions options = new ChromeOptions();
          options.addArguments("--headless");
          options.addArguments("--disable-gpu"); // If the machine is Windows, this flag is needed
          options.addArguments("--window-size=1920,1200");

          try {
              driver = new ChromeDriver(options); // This should initialize the 'driver'
          } catch (Exception e) {
              e.printStackTrace(); // Log the exception, it will help you understand the possible issue during initialization
              return; // If exception occurs, the driver would be null. Handle this appropriately based on your test workflow.
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
