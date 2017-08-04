package gms.ui.abstractpage;

import com.genesyslab.functional.tests.gms.ui.GMSLoginPage;
import com.genesyslab.functional.tests.gms.ui.GMSMainPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by bvolovyk on 26.06.2017.
 */
public abstract class GMSNavBarAbstractPage extends GMSAbstractPage {
    private By navBarLocator = By.xpath("//nav/div");
    private By homeImgLocator = By.xpath("//*[@id='nav_header']//img");
    private By homeBtnLocator = By.xpath("//*[@id='nav_header']//span[text()]");
    private By usernameBtnLocator = By.xpath("//*[@id='nav_account']//span");
    private By logOutBtnLocator = By.xpath("//*[@id='nav_logout']//a");

    public GMSNavBarAbstractPage(WebDriver driver) {
        super(driver);
        waitForElementToBeVisible(homeImgLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        waitForElementContainsText(homeBtnLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        waitForElementToBeClickable(usernameBtnLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        if (!driver.findElement(navBarLocator).isDisplayed()) {
            throw new IllegalStateException("Navigation Bar isn't displayed!!!");
        }
    }

    public void clickHome() {
        driver.findElement(homeBtnLocator).click();
    }

    public GMSMainPage goToHomeUIPage() {
        clickHome();
        return new GMSMainPage(driver);
    }

    public String getHomeBtnName() {
        return getTextFromSpan(homeBtnLocator);
    }

    public void clickUsername() {
        driver.findElement(usernameBtnLocator).click();
    }

    public String getUsername() {
        return getTextFromSpan(usernameBtnLocator);
    }

    public void clickLogOut() {
        clickUsername();
        driver.findElement(logOutBtnLocator).click();
    }

    public GMSLoginPage logOut() {
        clickLogOut();
        return new GMSLoginPage(driver);
    }

    public String getLogOutBtnName() {
        clickUsername();
        return getText(logOutBtnLocator);
    }
}
