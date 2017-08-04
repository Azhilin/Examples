package gms.ui.adminui;

import com.genesyslab.functional.tests.gms.ui.abstractpage.GMSAdminUINavBarAbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Created by bvolovyk on 12.07.2017.
 */
public class GMSMonitorPage extends GMSAdminUINavBarAbstractPage {
    private By spinnerLocator = By.xpath("//*[@id='initial_loading']/img");
    private By lastUpdatedLocator = By.xpath("//*[@id='system-timestamp']/i");
    private By tokenStatusLocator = By.xpath("//div[@class='node-state']");
    private By loadDataCenterRackOwnRunningSinceLocator = By.xpath("//div[@class='node-skills']");
    private By loadingLocator = By.xpath("//*[@id='loading' and text()]");//arises every 3 minutes by default with text "Loading..."
    private By nodeWidgetLocator = By.xpath("//div[contains(@class, 'node-widget')]");

    public GMSMonitorPage(WebDriver driver) {
        super(driver);
        waitForElementToBeVisible(nodeWidgetLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
    }

    public String getLastUpdatedText() {
        return getText(lastUpdatedLocator);
    }

    public String getTokenText() {
        WebElement el = driver.findElement(tokenStatusLocator);
        String text = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].firstChild.textContent", el);
        return text.trim();
    }

    public String getStatusText() {
        WebElement el = driver.findElement(tokenStatusLocator);
        String text = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].lastChild.textContent", el);
        return text.trim();
    }

    public String getLoadText() {
        WebElement el = driver.findElement(loadDataCenterRackOwnRunningSinceLocator);
        String text = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].firstChild.textContent", el);
        return text.trim();
    }

    public String getDataCenterText() {
        WebElement el = driver.findElement(loadDataCenterRackOwnRunningSinceLocator);
        String text = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].childNodes[2].textContent", el);
        return text.trim();
    }

    public String getRackText() {
        WebElement el = driver.findElement(loadDataCenterRackOwnRunningSinceLocator);
        String text = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].childNodes[4].textContent", el);
        return text.trim();
    }

    public String getOwnText() {
        WebElement el = driver.findElement(loadDataCenterRackOwnRunningSinceLocator);
        String text = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].childNodes[6].textContent", el);
        return text.trim();
    }

    public String getRunningSinceText() {
        WebElement el = driver.findElement(loadDataCenterRackOwnRunningSinceLocator);
        String text = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].childNodes[8].textContent", el);
        return text.trim();
    }

    public String getLoadingText() {
        System.out.println("Waiting 180 seconds until \"Loading...\" element arises.");
        waitForElementToBeClickable(loadingLocator, 185, 10);
        return getText(loadingLocator);
    }
}
