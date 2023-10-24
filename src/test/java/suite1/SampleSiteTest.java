package suite1;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import util.TestBase;

public class SampleSiteTest extends TestBase {


    @Test
    public void testSiteHeader2() throws InterruptedException {

        driver.get("http://example.com");
        testUtil.awaitLoadDOM(20);
        WebElement header = testUtil.getElement("//h1", null, 20);
        System.out.println(header.getText());
        testUtil.setHardWait(3000);
     

        // Assert that the header text is as expected.
//        Assert.assertEquals(header.getText(), "Expected Header Text", "Header text does not match expected text.");
    }
}
