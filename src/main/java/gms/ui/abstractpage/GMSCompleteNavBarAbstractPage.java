package gms.ui.abstractpage;

import com.genesyslab.functional.tests.gms.ui.GMSPatternsPage;
import com.genesyslab.functional.tests.gms.ui.callback.GMSCallbackPage;
import com.genesyslab.functional.tests.gms.ui.configuredservices.GMSConfiguredServicesPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by bvolovyk on 26.06.2017.
 */
public abstract class GMSCompleteNavBarAbstractPage extends GMSNavBarAbstractPage {
    private By configuredServicesBtnLocator = By.xpath("//*[@id='nav_configured_services']//span[text()]");
    private By callbackBtnLocator = By.xpath("//*[@id='nav_callback']//span[text()]");
    private By toolsBtnLocator = By.xpath("//*[@id='nav_tools']//span[text()]");
    private By toolsMenuLocator = By.xpath("//*[@id='nav_tools' and contains(@class, 'open active')]//ul");//expanded drop down menu
    private By serviceTemplatesBtnLocator = By.xpath("//*[@id='nav_tools_service_templates']/a");//item in expanded drop down menu
    private By resourcesBtnLocator = By.xpath("//*[@id='nav_tools_resources']/a");
    private By patternsBtnLocator = By.xpath("//*[@id='nav_tools_patterns']/a");
    private By downloadDfmBtnLocator = By.xpath("//*[@id='nav_tools_download_dfm']/a");

    public GMSCompleteNavBarAbstractPage(WebDriver driver) {
        super(driver);
        if (isElementPresent(
                By.xpath("//*[@id='nav_configured_services' and contains(@class, 'active-dropdown')]//span[text()]"))) {
            waitForElementContainsText(configuredServicesBtnLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        }
        waitForElementContainsText(callbackBtnLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        waitForElementToBeClickable(callbackBtnLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
    }

    public boolean isConfiguredServicesButtonDisplayed() {
        return driver.findElement(configuredServicesBtnLocator).isDisplayed();
    }

    public void clickConfiguredServices() {
        driver.findElement(configuredServicesBtnLocator).click();
    }

    public GMSConfiguredServicesPage goToConfiguredServicesUIPage() {
        clickConfiguredServices();
        return new GMSConfiguredServicesPage(driver);
    }

    public String getConfiguredServicesBtnName() {
        return getTextFromSpan(configuredServicesBtnLocator);
    }

    public boolean isCallbackBtnDisplayed() {
        return driver.findElement(callbackBtnLocator).isDisplayed();
    }

    public void clickCallback() {
        driver.findElement(callbackBtnLocator).click();
    }

    public GMSCallbackPage goToCallbackManagementUIPage() {
        clickCallback();
        return new GMSCallbackPage(driver);
    }

    public String getCallbackBtnName() {
        return getTextFromSpan(callbackBtnLocator);
    }

    public boolean isToolsBtnDisplayed() {
        return driver.findElement(toolsBtnLocator).isDisplayed();
    }

    public void clickTools() {
        driver.findElement(toolsBtnLocator).click();
    }

    public String getToolsBtnName() {
        return getTextFromSpan(toolsBtnLocator);
    }

    //use this method if you want to know whether Tools drop down menu displayed after clicking on Tools button
    public boolean isToolsMenuPresent() {
        return isElementPresent(toolsMenuLocator);
    }

    public void clickServiceTemplates() {
        clickTools();
        driver.findElement(serviceTemplatesBtnLocator).click();
    }

//    TODO: implement GMSServiceTemplatesPage and uncomment the below code
//    public GMSServiceTemplatesPage goToServiceTemplatesUIPage() {
//        clickServiceTemplates();
//        return new GMSServiceTemplatesPage(driver);
//    }

    public String getServiceTemplatesBtnName() {
        if (!isToolsMenuPresent()) {
            clickTools();
        }
        return getText(serviceTemplatesBtnLocator);
    }

    public void clickResources() {
        clickTools();
        driver.findElement(resourcesBtnLocator).click();
    }

//    TODO: implement GMSResourcesPage and uncomment the below code
//    public GMSResourcesPage goToResourcesUIPage() {
//        clickResources();
//        return new GMSResourcesPage(driver);
//    }

    public String getResourcesBtnName() {
        if (!isToolsMenuPresent()) {
            clickTools();
        }
        return getText(resourcesBtnLocator);
    }

    public void clickPatterns() {
        clickTools();
        driver.findElement(patternsBtnLocator).click();
    }

    public GMSPatternsPage goToPatternsUIPage() {
        clickPatterns();
        return new GMSPatternsPage(driver);
    }

    public String getPatternsBtnName() {
        if (!isToolsMenuPresent()) {
            clickTools();
        }
        return getText(patternsBtnLocator);
    }

    public void clickDownloadDfm() {
        clickTools();
        driver.findElement(downloadDfmBtnLocator).click();
    }

//    TODO: implement GMSDownloadDfmPage and uncomment the below code
//    public GMSDownloadDfmPage goToDownloadDfmUIPage() {
//        clickDownloadDfm();
//        return new GMSDownloadDfmPage(driver);
//    }

    public String getDownloadDfmBtnName() {
        if (!isToolsMenuPresent()) {
            clickTools();
        }
        return getText(downloadDfmBtnLocator);
    }
}
