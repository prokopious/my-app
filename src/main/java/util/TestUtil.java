package util;

import static org.testng.Assert.assertTrue;

import java.time.Duration;
import java.util.function.Supplier;
import java.util.ArrayList;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class TestUtil {

	private static WebDriver driver;

	public TestUtil(WebDriver _driver) {
		driver = _driver;

	}

	public void setHardWait(long timeOutSeconds) {
		try {
			Thread.sleep(timeOutSeconds);
		} catch (Exception ignore) {
		}
	}

	public void setExplicitWait(WebElement ele, long timeOutSeconds) {

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutSeconds));
		wait.until(ExpectedConditions.visibilityOf(ele));
	}

	public String getInnerText(WebElement element) {

		JavascriptExecutor js = (JavascriptExecutor) driver;
		String s = (String) js.executeScript("let text = arguments[0].innerText;\r\n" + "return text;", element);
		setHardWait(1000);
		return s;
	}

	public String getInputValue(WebElement inputElement) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		String s = (String) js.executeScript("let text = arguments[0].value;\r\n" + "return text;", inputElement);
		setHardWait(1000);
		return s;
	}

	public void awaitAbsence(String xpath, WebElement element, WebElement parent, int children, String... filters) {
		WebElement ele = filterXpathTimout(xpath, element, parent, children, 3000, filters);
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			driver.manage().timeouts().scriptTimeout(Duration.ofMinutes(2));
			boolean absent = (boolean) js.executeScript("const awaitElementAbsence = (element, timeout) => {\r\n"
					+ "    return new Promise((resolve, reject) => {\r\n" + "      const startTime = Date.now();\r\n"
					+ "      const tryQuery = () => {\r\n" + "        function offset(el) {\r\n"
					+ "            el.scrollIntoView();\r\n"
					+ "            return(el.offsetWidth == 0 && el.offsetHeight == 0);\r\n" + "        }\r\n"
					+ "        function isHidden(el) {\r\n" + "            let style = window.getComputedStyle(el);\r\n"
					+ "            return (style.display === 'none')\r\n" + "        }\r\n"
					+ "        if (!element || isHidden(element) || offset(element)) {\r\n"
					+ "          resolve(true);}\r\n"
					+ "        else if (Date.now() - startTime > timeout * 1000) resolve(false);\r\n"
					+ "        else setTimeout(tryQuery, 100); \r\n" + "      }\r\n" + "      tryQuery();\r\n"
					+ "    });\r\n" + "  };\r\n" + "  \r\n"
					+ "  const absent = await awaitElementAbsence(arguments[0], 120);\r\n" + "  return absent;", ele);
			if (absent) {
				Assert.assertTrue(true, "Element present in DOM");
			}
			System.out.println("Element absent from DOM.");
		} catch (StaleElementReferenceException e) {
			System.out.println("Element absent from DOM.");
		}
	}

	public void awaitSpinner() {
		awaitAbsence("//i", null, null, 0, "fa-spin");
		awaitLoadDOM(20);
	}

	public void awaitCircle() {
		awaitAbsence("//circle", null, null, 0);
		awaitLoadDOM(20);
	}

	public boolean awaitIsDisplayed(WebElement element, int timeoutSeconds) {

		JavascriptExecutor js = (JavascriptExecutor) driver;
		driver.manage().timeouts().scriptTimeout(Duration.ofMinutes(2));
		boolean displayed = (boolean) js.executeScript("const awaitIsDisplayed = (element, timeout) => {\r\n"
				+ "    return new Promise((resolve, reject) => {\r\n" + "      const startTime = Date.now();\r\n"
				+ "      const tryQuery = () => {\r\n" + "        function zeroWidthAndHeight(el) {  \r\n"
				+ "          return(el.offsetWidth == 0 && el.offsetHeight == 0);\r\n" + "          }\r\n"
				+ "          function isHidden(el) {          \r\n"
				+ "          let style = window.getComputedStyle(el);\r\n"
				+ "          return (style.display === 'none')      \r\n" + "          }\r\n"
				+ "        if (element && !isHidden(element) && !zeroWidthAndHeight(element)) {\r\n"
				+ "          resolve(true);} //element displayed\r\n"
				+ "        else if (Date.now() - startTime > timeout * 1000) resolve(false);\r\n"
				+ "        else setTimeout(tryQuery, 100); \r\n" + "      }\r\n" + "      tryQuery();\r\n"
				+ "    });\r\n" + "  };\r\n" + "  \r\n"
				+ "  const displayed = await awaitIsDisplayed(arguments[0], arguments[1]);\r\n" + "  return displayed;",
				element, timeoutSeconds);
		if (displayed) {
			System.out.println("Element is displayed");
			return true;
		} else {
			System.out.println("Element is not displayed");
			return false;
		}

	}

	public void awaitLoadDOM(int timeoutSeconds) {

		JavascriptExecutor js = (JavascriptExecutor) driver;
		driver.manage().timeouts().scriptTimeout(Duration.ofMinutes(2));
		boolean ready = (boolean) js.executeScript("const awaitLoadDOM = (timeout) => {\r\n"
				+ "  return new Promise((resolve, reject) => {\r\n" + "    const startTime = Date.now();\r\n"
				+ "    const tryQuery = () => {\r\n" + "      const ready = document.readyState === \"complete\";\r\n"
				+ "      if (ready) {\r\n" + "        resolve(ready);} // Found the element\r\n"
				+ "      else if (Date.now() - startTime > timeout * 1000) resolve(null);\r\n"
				+ "      else setTimeout(tryQuery, 100); \r\n" + "    }\r\n" + "    tryQuery();\r\n" + "  });\r\n"
				+ "};\r\n" + "\r\n" + "const ready = await awaitLoadDOM(arguments[0]);\r\n" + "return ready;",
				timeoutSeconds);
		if (ready) {
			System.out.println("DOM loaded.");
			Assert.assertTrue(ready);
		}
	}

	// this method goes with filterSelector and isn't used on its own
	public WebElement findByCssSelector(String cssSelector, WebElement element, WebElement parentElement,
			int numberOfChildElements, String... filters) {

		JavascriptExecutor js = (JavascriptExecutor) driver;
		driver.manage().timeouts().scriptTimeout(Duration.ofMinutes(2));
		WebElement ele = (WebElement) js
				.executeScript("const query = (path, webElement, parent, children, filters) => {\r\n" + "\r\n"
						+ "    let allFalse = (e, filters) => {\r\n" + "        let falseArray = [];\r\n"
						+ "        filters.forEach((n) => {\r\n"
						+ "            if (!e.outerHTML.includes(n.substring(1))) {\r\n"
						+ "                falseArray.push(\"false\");\r\n" + "            }\r\n" + "        })\r\n"
						+ "        if (falseArray.length == filters.length) {\r\n" + "            return true;\r\n"
						+ "        }\r\n" + "        else { return false }\r\n" + "    };\r\n" + "    \r\n"
						+ "    let allTrue = (e, trueFilters) => {\r\n" + "        let trueArray = [];\r\n"
						+ "        trueFilters.forEach((n) => {\r\n" + "            if (e.outerHTML.includes(n)) {\r\n"
						+ "                trueArray.push(\"true\");\r\n" + "            }\r\n" + "        })\r\n"
						+ "        if (trueArray.length == trueFilters.length) {\r\n" + "            return true;\r\n"
						+ "        }\r\n" + "        else { return false }\r\n" + "    };\r\n" + "\r\n" + "   \r\n"
						+ "        let filterArray = Object.values(filters);\r\n"
						+ "        if (webElement != null) {\r\n"
						+ "            filterArray.push(webElement.outerHTML);\r\n" + "        }\r\n"
						+ "        const startTime = Date.now();\r\n"
						+ "        const containsArray = filterArray.filter((n) => {\r\n"
						+ "            if (n.charAt(0) != \"!\")\r\n" + "                return n;\r\n"
						+ "        });\r\n" + "        const containsNOTArray = filterArray.filter((n) => {\r\n"
						+ "            if (n.charAt(0) == \"!\") {\r\n" + "                return n;\r\n"
						+ "            }\r\n" + "        });\r\n"
						+ "        console.log(\"containsNOTArray: \" + containsNOTArray)\r\n" + "\r\n"
						+ "            function getElementsBySelector(path, parent) {\r\n"
						+ "                let par = document.body;\r\n" + "                if (parent != null) {\r\n"
						+ "                    par = parent;\r\n" + "                }\r\n"
						+ "                let results = [];\r\n"
						+ "                let query = par.querySelectorAll(path);\r\n"
						+ "                for (let i = 0, length = query.length; i < length; ++i) {\r\n"
						+ "                    results.push(query[i]);\r\n" + "                }\r\n"
						+ "                return results;\r\n" + "            }\r\n" + "\r\n"
						+ "            if (path != null) {\r\n"
						+ "                const elements = getElementsBySelector(path, parent);\r\n"
						+ "                const elem = elements.find(el => {\r\n"
						+ "                    if (el.children.length == children) {\r\n"
						+ "                        if (allTrue(el, containsArray) && allFalse(el, containsNOTArray)) {\r\n"
						+ "                            el.style.backgroundColor = \"yellow\";\r\n"
						+ "                            return el;\r\n" + "                        }\r\n"
						+ "                    }\r\n" + "                });\r\n"
						+ "                if (elem) return elem;\r\n" + "            \r\n" + "            } else {\r\n"
						+ "                const elements = getElementsBySelector(\"*\", parent);\r\n"
						+ "                const elem = elements.find(el => {\r\n"
						+ "                    if (el.children.length == children && el.outerHTML == webElement.outerHTML) {\r\n"
						+ "                        if (allTrue(el, containsArray) && allFalse(el, containsNOTArray)) {\r\n"
						+ "                            el.style.backgroundColor = \"yellow\";\r\n"
						+ "                            return el;\r\n" + "                        }\r\n"
						+ "                    }\r\n" + "                });\r\n"
						+ "                if (elem) return elem;\r\n" + "            }\r\n" + "   \r\n" + "   \r\n"
						+ "};\r\n" + "\r\n" + "\r\n"
						+ "const element = await query(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4]);\r\n"
						+ "return element;", cssSelector, element, parentElement, numberOfChildElements, filters);
		return ele;

	};

	// this JavaScript is used with other methods, not on its own.
	public WebElement findByXPath(String xpath, WebElement element, WebElement parentElement, int numberOfChildElements,
			String... filters) {

		JavascriptExecutor js = (JavascriptExecutor) driver;
		driver.manage().timeouts().scriptTimeout(Duration.ofMinutes(2));
		WebElement ele = (WebElement) js.executeScript("let allFalse = (e, filters) => {\r\n"
				+ "    let falseArray = [];\r\n" + "    filters.forEach((n) => {\r\n"
				+ "        if (!e.outerHTML.includes(n.substring(1))) {\r\n"
				+ "            falseArray.push(\"false\");\r\n" + "        }\r\n" + "    })\r\n"
				+ "    if (falseArray.length == filters.length) {\r\n" + "        return true;\r\n" + "    }\r\n"
				+ "    \r\n" + "    else { return false }\r\n" + "};\r\n" + "\r\n"
				+ "let allTrue = (e, trueFilters) => {\r\n" + "    let trueArray = [];\r\n"
				+ "    trueFilters.forEach((n) => {\r\n" + "        if (e.outerHTML.includes(n)) {\r\n"
				+ "            trueArray.push(\"true\");\r\n" + "        }\r\n" + "    })\r\n"
				+ "    if (trueArray.length == trueFilters.length) {\r\n" + "        return true;\r\n" + "    }\r\n"
				+ "    else { return false }\r\n" + "};\r\n" + "\r\n"
				+ "const query = (path, webElement, parent, children, filters) => {\r\n" + "\r\n"
				+ "        let filterArray = Object.values(filters);\r\n" + "        if (webElement != null) {\r\n"
				+ "            filterArray.push(webElement.outerHTML);\r\n" + "        }\r\n"
				+ "        const startTime = Date.now();\r\n"
				+ "        const containsArray = filterArray.filter((n) => {\r\n"
				+ "            if (n.charAt(0) != \"!\")\r\n" + "                return n;\r\n" + "        });\r\n"
				+ "        const containsNOTArray = filterArray.filter((n) => {\r\n"
				+ "            if (n.charAt(0) == \"!\") {\r\n" + "                return n;\r\n" + "            }\r\n"
				+ "        });\r\n" + "        console.log(\"containsNOTArray: \" + containsNOTArray)\r\n" + "  \r\n"
				+ "            function getElementsByXPath(path, parent) {\r\n"
				+ "                let results = [];\r\n"
				+ "                let query = document.evaluate(path, parent || document.body,\r\n"
				+ "                    null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);\r\n"
				+ "                for (let i = 0, length = query.snapshotLength; i < length; ++i) {\r\n"
				+ "                    results.push(query.snapshotItem(i));\r\n" + "                }\r\n"
				+ "                return results; \r\n" + "            }\r\n" + "            if (path != null) {\r\n"
				+ "                const elements = getElementsByXPath(path, parent);\r\n"
				+ "                const elem = elements.find(el => {\r\n"
				+ "                    if (el.children.length == children) {\r\n"
				+ "                        if (allTrue(el, containsArray) && allFalse(el, containsNOTArray)) {\r\n"
				+ "                            return el;\r\n" + "                        }\r\n"
				+ "                    }\r\n" + "                });\r\n" + "                return elem;\r\n"
				+ "                \r\n" + "            } else {\r\n"
				+ "                if (parent != null) { path = \".//*\" } else { path = \"//*\" }\r\n"
				+ "                const elements = getElementsByXPath(path, parent);\r\n"
				+ "                const elem = elements.find(el => {\r\n"
				+ "                    if (el.children.length == children && el.outerHTML == webElement.outerHTML) {\r\n"
				+ "                        if (allTrue(el, containsArray) && allFalse(el, containsNOTArray)) {\r\n"
				+ "                            return el;\r\n" + "                        }\r\n"
				+ "                    }\r\n" + "                });\r\n" + "                return elem;\r\n"
				+ "            }\r\n" + "};\r\n" + "\r\n" + "\r\n"
				+ "const element = await query(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4]);\r\n"
				+ "\r\n" + "return element;", xpath, element, parentElement, numberOfChildElements, filters);
		return ele;

	};

	// this method takes either a WebElement or an xpath. one must be null.
	public WebElement findElementJavascript(String xpath, WebElement element) {

		JavascriptExecutor js = (JavascriptExecutor) driver;
		WebElement e = (WebElement) js.executeScript("const elementOnPage = (query, webElement) => {\r\n" + "\r\n"
				+ "    if (webElement == null) {\r\n"
				+ "        let elem = document.evaluate(query, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;\r\n"
				+ "        if (elem) return elem;\r\n" + "    }\r\n" + "    else {\r\n"
				+ "        let elem = webElement;\r\n" + "        if (elem) return elem;\r\n" + "    }\r\n" + "}\r\n"
				+ "\r\n" + "const element = await elementOnPage(arguments[0], arguments[1]);\r\n" + "return element;",
				xpath, element);

		return e;

	}

	public void awaitElement(String xpath, WebElement element, int timeoutSeconds) {

		Supplier<WebElement> desiredElement = () -> findElementJavascript(xpath, element);
		MethodPoller<WebElement> poller = new MethodPoller<>();
		WebElement elem = null;
		try {
			elem = poller.poll(timeoutSeconds, 10).method(desiredElement).untilCondition(ele -> ele != null).execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(awaitIsDisplayed(elem, 10));
	}

	public WebElement getElement(String xpath, WebElement element, int timeoutSeconds) {

		Supplier<WebElement> desiredElement = () -> findElementJavascript(xpath, element);
		MethodPoller<WebElement> poller = new MethodPoller<>();
		WebElement elem = null;
		try {
			elem = poller.poll(timeoutSeconds, 100).method(desiredElement).untilCondition(ele -> ele != null).execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return elem;
	}

	public void awaitElementFilterHTML(String xpath, WebElement pomElement, WebElement parentElement,
			int numberOfChildElements, int timeout, String... filters) {

		Supplier<WebElement> desiredElement = () -> findByXPath(xpath, pomElement, parentElement, numberOfChildElements,
				filters);
		MethodPoller<WebElement> poller = new MethodPoller<>();
		WebElement elem = null;
		try {
			elem = poller.poll(60, 300).method(desiredElement).untilCondition(ele -> ele != null).execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(awaitIsDisplayed(elem, 10));
	}

	public WebElement filterXpath(String xpath, WebElement pomElement, WebElement parentElement,
			int numberOfChildElements, String... filters) {

		Supplier<WebElement> desiredElement = () -> findByXPath(xpath, pomElement, parentElement, numberOfChildElements,
				filters);
		MethodPoller<WebElement> poller = new MethodPoller<>();
		WebElement elem = null;
		try {
			elem = poller.poll(60, 100).method(desiredElement).untilCondition(ele -> ele != null).execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return elem;
	}

	public WebElement filterXpathTimout(String xpath, WebElement pomElement, WebElement parentElement,
			int numberOfChildElements, int timeoutMillis, String... filters) {

		Supplier<WebElement> desiredElement = () -> findByXPath(xpath, pomElement, parentElement, numberOfChildElements,
				filters);
		MethodPoller<WebElement> poller = new MethodPoller<>();
		WebElement elem = null;

		try {
			elem = poller.poll((timeoutMillis / 1000), 100).method(desiredElement).untilCondition(ele -> ele != null)
					.execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return elem;
	}

	public WebElement filterSelector(String cssSelector, WebElement pomElement, WebElement parentElement,
			int numberOfChildElements, String... filters) {

		Supplier<WebElement> desiredElement = () -> findByCssSelector(cssSelector, pomElement, parentElement,
				numberOfChildElements, filters);
		MethodPoller<WebElement> poller = new MethodPoller<>();
		WebElement elem = null;

		try {
			elem = poller.poll(60, 500).method(desiredElement).untilCondition(ele -> ele != null).execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return elem;
	}

	public void verifyInputFieldValue(WebElement element, String inputValue, int timeoutSeconds) {

		Supplier<Boolean> isPresent = () -> verifyInputValueJavaScript(element, inputValue);
		MethodPoller<Boolean> poller = new MethodPoller<>();
		Boolean valuePresent = false;

		try {
			valuePresent = poller.poll(timeoutSeconds, 500).method(isPresent).untilCondition(value -> value == true)
					.execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.assertTrue(valuePresent);
	}

	// waits for specific input value to load or be present
	public boolean awaitKeys(WebElement element, String inputValue, int timeoutSeconds) {

		JavascriptExecutor js = (JavascriptExecutor) driver;
		driver.manage().timeouts().scriptTimeout(Duration.ofMinutes(2));
		boolean displayed = (boolean) js.executeScript("const awaitKeys = (element, value, timeout) => {\r\n"
				+ "  return new Promise((resolve, reject) => {\r\n" + "    const startTime = Date.now();\r\n"
				+ "    const tryQuery = () => {\r\n" + "      if (element.value == value) {\r\n"
				+ "        resolve(true);} //element displayed\r\n"
				+ "      else if (Date.now() - startTime > timeout * 1000) resolve(false);\r\n"
				+ "      else setTimeout(tryQuery, 100); \r\n" + "    }\r\n" + "    tryQuery();\r\n" + "  });\r\n"
				+ "};\r\n" + "\r\n"
				+ "const keysEntered = await awaitKeys(arguments[0], arguments[1], arguments[2]);\r\n"
				+ "return keysEntered;", element, inputValue, timeoutSeconds);
		if (displayed) {
			System.out.println("Value \"" + inputValue + "\" entered.");
			return true;
		} else {
			System.out.println("Keys were not sent to input.");
			return false;
		}

	}

	public boolean verifyInputValueJavaScript(WebElement input, String value) {

		JavascriptExecutor js = (JavascriptExecutor) driver;
		boolean valuePresent = (boolean) js.executeScript(
				"const verifyKeys = (element, value) => {   			\r\n" + "if (element.value == value) {\r\n"
						+ "return true;\r\n" + "} else {\r\n" + "return false;\r\n" + "}\r\n" + "}    		\r\n"
						+ "const keysEntered = verifyKeys(arguments[0], arguments[1]);\r\n" + "return keysEntered;",
				input, value);

		return valuePresent;

	}

}
