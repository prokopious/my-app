package app;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class App {
	public static void main(String[] args) throws InterruptedException {
		
		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
		WebDriver driver = new ChromeDriver();

		// Use the driver to visit a website
		driver.get("https://www.example.com");
		Thread.sleep(9000);
		// your test code here

		driver.quit();

		Cat fido = new Cat();
		fido.makeSound();

	}
}
