package gms.ui.abstractpage;


import com.google.common.base.Function;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.given;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.containsString;

//A place for common methods of all pages
public abstract class AbstractPage {
    private By busyIndicatorLocator = By.xpath("//img[@src='img/big-spinner.gif']");
    protected WebDriver driver;
    protected Actions builder;
    protected static final long TIMEOUT_SECONDS = 120;
    protected static final long POLLING_TIME_MILLIS = 200;

    public AbstractPage(WebDriver driver) {
        this.driver = driver;
        builder = new Actions(driver);
    }

    public String getTitle() {
        return driver.getTitle();
    }

    // wait till the progress "waiting" icon disappears
    public void waitForProgress() {
        // wait some time in case progress icon doesn't appear immediately
        waitFor(500);
        // wait till progress icon disappears
        (new WebDriverWait(driver, TIMEOUT_SECONDS)).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver d) {
                return !(d.findElement(busyIndicatorLocator).isDisplayed());
            }
        });
        // wait till everything is rendered
        waitFor(1000);
    }

    // wait till the appearance of some element
    public void waitForLocator(final By locator) {
        (new WebDriverWait(driver, TIMEOUT_SECONDS)).until(new ExpectedCondition<WebElement>() {
            @Override
            public WebElement apply(WebDriver d) {
                return d.findElement(locator);
            }
        });
    }

    private static Wait<WebDriver> createWait(WebDriver driver, long waitSeconds, long pollMillis) {
        return (new FluentWait<WebDriver>(driver)).withTimeout(waitSeconds, TimeUnit.SECONDS)
                .pollingEvery(pollMillis, TimeUnit.MILLISECONDS).ignoring(NoSuchElementException.class);
    }

    public WebElement waitForElement(By locator, long timeoutSeconds, long pollingTimeMillis) {
        Wait<WebDriver> wait = createWait(this.driver, timeoutSeconds, pollingTimeMillis);
        return (WebElement) wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public WebElement waitForElementToBeClickable(By locator, long timeoutSeconds, long pollingTimeMillis) {
        Wait<WebDriver> wait = createWait(this.driver, timeoutSeconds, pollingTimeMillis);
        return (WebElement) wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public void waitForElementToBeNotClickable(By locator, long timeoutSeconds, long pollingTimeMillis) {
        Wait<WebDriver> wait = createWait(this.driver, timeoutSeconds, pollingTimeMillis);
        wait.until(ExpectedConditions.not(ExpectedConditions.elementToBeClickable(locator)));
    }

    public WebElement waitForElementToBeVisible(By locator, long timeoutSeconds, long pollingTimeMillis) {
        Wait<WebDriver> wait = createWait(this.driver, timeoutSeconds, pollingTimeMillis);
        return (WebElement) wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public void waitForElementToBeNotVisible(By locator, long timeoutSeconds, long pollingTimeMillis) {
        Wait<WebDriver> wait = createWait(this.driver, timeoutSeconds, pollingTimeMillis);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public void waitForElementContainsText(final By locator, long timeoutSeconds, long pollingTimeMillis) {
        Wait<WebDriver> wait = createWait(this.driver, timeoutSeconds, pollingTimeMillis);
        wait.until(new Function<WebDriver, Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return driver
                        .findElement(locator)
                        .getText()
                        .trim()
                        .matches("^(?!.?\\<!--\\sngIf\\:\\sitem\\.caret\\s==\\strue\\s--\\>).+((?<=\\s\\<!--\\sngIf\\:\\sitem\\.caret\\s==\\strue\\s--\\>)|(?<!\\s\\<!--\\sngIf\\:\\sitem\\.caret\\s==\\strue\\s--\\>))$");
            }
        });
    }

    public void waitForElementSpanContainsText(final By locator, long timeoutSeconds, long pollingTimeMillis) {
        Wait<WebDriver> wait = createWait(this.driver, timeoutSeconds, pollingTimeMillis);
        wait.until(new Function<WebDriver, Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                WebElement el = driver.findElement(locator);
                String text = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].innerHTML", el);
                return text
                        .trim()
                        .matches("^(?!.?\\<!--\\sngIf\\:\\sitem\\.caret\\s==\\strue\\s--\\>).+((?<=\\s\\<!--\\sngIf\\:\\sitem\\.caret\\s==\\strue\\s--\\>)|(?<!\\s\\<!--\\sngIf\\:\\sitem\\.caret\\s==\\strue\\s--\\>))$");
            }
        });
    }

    // wrapper for the Thread.sleep()
    public static void waitFor(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
        }
    }

    public String getText(By by) {
        waitForLocator(by);
        return driver.findElement(by).getText().trim();
    }

    public String getTextFromSpan(By by) {
        WebElement el = driver.findElement(by);
        String name = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].innerHTML", el);
        return name.trim();
    }

    public String getTextFromPlaceholder(By by) {
        return driver.findElement(by).getAttribute("placeholder").trim();
    }

    public void waitToClickOnElement(By locator, long timeoutSeconds, long pollingTimeMillis) {
        given().ignoreExceptions()
                .with()
                .pollDelay(pollingTimeMillis, MILLISECONDS)
                .and()
                .pollInterval(pollingTimeMillis, MILLISECONDS)
                .await()
                .atMost(timeoutSeconds, SECONDS)
                .until(clickingOnElement(locator), containsString("noErrors"));
    }

    public boolean isElementPresent(By by) {
        return !driver.findElements(by).isEmpty();
    }

    private Callable<String> clickingOnElement(final By locator) {
        return new Callable<String>() {
            @Override
            public String call() {
                try {
                    driver.findElement(locator).click();
                } catch (Exception e) {
                    String errorMsg = e.getMessage();
//                    System.out.println("CLICKING ON THE ELEMENT: " + errorMsg);
                    return errorMsg;
                }
                return "noErrors"; // The condition supplier part
            }
        };
    }
}
