package gms.ui;

import com.genesyslab.functional.tests.gms.ui.abstractpage.GMSAbstractPage;
import org.openqa.selenium.*;

public class GMSLoginPage extends GMSAbstractPage {
    private By genesysLogoImgLocator = By.xpath("//img");
    private By welcomeLocator = By.xpath("//h2");
    private By usernameLocator = By.id("user");
    private By passwordLocator = By.id("pass");
    private By languageMenuLocator = By.xpath("//button/span[@class='filter-option pull-left']");
    private By englishLanguageLocator = By.xpath("//li[1]//span[@class='text']");
    private By frenchLanguageLocator = By.xpath("//li[2]//span[@class='text']");
    private By japaneseLanguageLocator = By.xpath("//li[3]//span[@class='text']");
    private By loginButtonLocator = By.xpath("//button[@ng-click]");
    private By errIconLocator = By.xpath("//span[@class='icon-alert-octo']");
    private By errMsgLocator = By.xpath("//div[@class='sample-login-error-messages ng-binding']");

    private boolean acceptNextAlert = true;
    private JavascriptExecutor js = (JavascriptExecutor) driver;

    public GMSLoginPage(WebDriver driver) {
        super(driver);
        waitForElementToBeVisible(genesysLogoImgLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        if (!"GMS Management UI - Login".equals(driver.getTitle())) {
            throw new IllegalStateException("This is not the login page");
        }
    }

    public GMSMainPage logIn(String username, String password) {
        typeUsername(username);
        typePassword(password);
        clickLogin();
        return new GMSMainPage(driver);
    }

    // utility methods
    public GMSLoginPage typeUsername(String username) {
        WebElement element = driver.findElement(usernameLocator);
        element.clear();
        driver.findElement(usernameLocator).sendKeys(username);
        return this;
    }

    public GMSLoginPage typePassword(String password) {
        WebElement element = driver.findElement(passwordLocator);
        element.clear();
        driver.findElement(passwordLocator).sendKeys(password);
        return this;
    }

    public void clickLogin() {
        driver.findElement(loginButtonLocator).click();
    }

//    private boolean isElementPresent(By by) {
//        try {
//            driver.findElement(by);
//            return true;
//        } catch (NoSuchElementException e) {
//            return false;
//        }
//    }

    private String closeAlertAndGetItsText() {
        try {
            Alert alert = driver.switchTo().alert();
            if (acceptNextAlert) {
                alert.accept();
            } else {
                alert.dismiss();
            }
            return alert.getText();
        } finally {
            acceptNextAlert = true;
        }
    }

    public String getErrorMessage() {
        waitForElementToBeVisible(errMsgLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        return getText(errMsgLocator);
    }

    public boolean isErrorMessagePresent() {
        return isElementPresent(errMsgLocator);
    }

    public String getWelcomeText() {
        return getText(welcomeLocator);
    }

    public String getUsernamePlaceholderText() {
        return getTextFromPlaceholder(usernameLocator);
    }

    public String getPasswordPlaceholderText() {
        return getTextFromPlaceholder(passwordLocator);
    }

    public String getLanguageMenuText() {
        return getTextFromSpan(languageMenuLocator);
    }

    public String getLogInBtnName() {
        return getText(loginButtonLocator);
    }

    public void clickLanguageMenu() {
        driver.findElement(languageMenuLocator).click();
    }

    public void setLanguage(String language) {
        clickLanguageMenu();
        switch (language) {
            case "English":
                driver.findElement(englishLanguageLocator).click();
                break;
            case "French":
                driver.findElement(frenchLanguageLocator).click();
                break;
            case "Japanese":
                driver.findElement(japaneseLanguageLocator).click();
                break;
            default:
                System.out.println("Please, choose one of three possible language: English, French, Japanese.");
                break;
        }
    }
}
