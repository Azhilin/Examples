package gms.ui;

import com.genesyslab.functional.tests.gms.ui.abstractpage.GMSNavBarAbstractPage;
import com.genesyslab.functional.tests.gms.ui.adminui.GMSMonitorPage;
import com.genesyslab.functional.tests.gms.ui.callback.GMSCallbackPage;
import com.genesyslab.functional.tests.gms.ui.configuredservices.GMSConfiguredServicesPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertyConfiguration;
import static com.genesyslab.functional.tests.gms.helper.URLsHelper.getCallbackPageURL;

public class GMSMainPage extends GMSNavBarAbstractPage {
    private static String baseURL_UI = getPropertyConfiguration("base.url.ui"); //http://10.10.15.19:3080/
    private By adminUIImgLocator = By.xpath("//img[contains(@ng-src, 'administration')]");
    private By adminUITextLocator = By.xpath("//img[contains(@ng-src, 'administration')]/ancestor::*[@class='widget-placeholder']//*[@ng-bind='item.caption']");
    private By callbackImgLocator = By.xpath("//img[contains(@ng-src, 'Callback')]");
    private By callbackTextLocator = By.xpath("//img[contains(@ng-src, 'Callback')]/ancestor::*[@class='widget-placeholder']//*[@ng-bind='item.caption']");
    private By contextServicesImgLocator = By.xpath("//img[contains(@ng-src, 'service-status')]");
    private By contextServicesTextLocator = By.xpath("//img[contains(@ng-src, 'service-status')]/ancestor::*[@class='widget-placeholder']//*[@ng-bind='item.caption']");
    //    private By contextServicesTextLocator = By.xpath("//img[contains(@ng-src, 'service-status')]/ancestor::*[@class='widget-icon-placeholder']//following-sibling::*//*[@ng-bind='item.caption']");
    //TODO: change below two locators for Customer Journey icon
    private By customerJourneyImgLocator = By.linkText("Journey Timeline");
    private By customerJourneyTextLocator = By.linkText("Journey Timeline");

    public GMSMainPage(WebDriver driver) {
        super(driver);
        waitForElementToBeVisible(callbackImgLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        waitForElementToBeClickable(callbackImgLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        waitForElementContainsText(callbackTextLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        if (!"GMS Management UI".equals(driver.getTitle())) {
            throw new IllegalStateException("This is not the main page");
        }
    }

    public boolean isAdminUIPresent() {
        return isElementPresent(adminUIImgLocator);
    }

    public void clickAdminUI() {
        driver.findElement(adminUIImgLocator).click();
    }

    public GMSMonitorPage goToMonitorAdminUIPage() {
        clickAdminUI();
        return new GMSMonitorPage(driver);
    }

    public String getAdminUIIconName() {
        return getText(adminUITextLocator);
    }

    public boolean isCallbackAndMobileEngagementPresent() {
        return isElementPresent(callbackImgLocator);
    }

    public void clickCallbackAndMobileEngagement() {
        driver.findElement(callbackImgLocator).click();
    }

    public GMSConfiguredServicesPage goToConfiguredServicesUIPage() {
        clickCallbackAndMobileEngagement();
        return new GMSConfiguredServicesPage(driver);
    }

    //use this method when UI passes you from Main page to Callback page (in case Shared GMS or Supervisor permissions)
    public GMSCallbackPage goToCallbackManagementUIPage() {
        clickCallbackAndMobileEngagement();
        return new GMSCallbackPage(driver);
    }

    public String getCallbackAndMobileEngagementIconName() {
        return getText(callbackTextLocator);
    }

    public boolean isContextServicesPresent() {
        return isElementPresent(contextServicesImgLocator);
    }

    public void clickContextServices() {
        driver.findElement(contextServicesImgLocator).click();
    }

    public GMSContextServicesPage goToContextServicesUIPage() {
        clickContextServices();
        return new GMSContextServicesPage(driver);
    }

    public String getContextServicesIconName() {
        return getText(contextServicesTextLocator);
    }

    public boolean isCustomerJourneyPresent() {
        return isElementPresent(customerJourneyImgLocator);
    }

    public void clickCustomerJourney() {
        driver.findElement(customerJourneyImgLocator).click();
    }

    public GMSCustomerJourneyPage goToCustomerJourneyUIPage() {
        clickCustomerJourney();
        return new GMSCustomerJourneyPage(driver);
    }

    public String getCustomerJourneyIconName() {
        return getText(customerJourneyTextLocator);
    }

    public GMSCallbackPage getCallbackManagementUIPage(String gmsBaseUrl) {
        driver.get(getCallbackPageURL(gmsBaseUrl));
        return new GMSCallbackPage(driver);
    }

    public GMSCustomerJourneyWorkspacePage getCustomerJourneyUIPage(String customer_id, boolean customerProfile) {
        driver.get(baseURL_UI + "genesys/develop/remote-index.html#?customer=" + customer_id + "&customerProfile=" + customerProfile);
        return new GMSCustomerJourneyWorkspacePage(driver);
    }

    public GMSContextServicesPage clickContextServicesUI() {
        driver.findElement(contextServicesImgLocator).click();
        waitForLocator(By.id("search-box"));
        return new GMSContextServicesPage(driver);
    }

    public GMSContextServicesPageNew clickContextServicesUINew() {
        waitForLocator(contextServicesImgLocator);
        driver.findElement(contextServicesImgLocator).click();
        waitForLocator(By.id("search-box"));
        return new GMSContextServicesPageNew(driver);
    }

    public GMSCustomerJourneyPage clickCustomerJourneyUI() {
        driver.findElement(customerJourneyImgLocator).click();
        waitForLocator(By.id("search-box"));
        return new GMSCustomerJourneyPage(driver);
    }
}
