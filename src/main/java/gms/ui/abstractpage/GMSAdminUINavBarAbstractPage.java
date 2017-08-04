package gms.ui.abstractpage;

import com.genesyslab.functional.tests.gms.ui.GMSLoginPage;
import com.genesyslab.functional.tests.gms.ui.GMSMainPage;
import com.genesyslab.functional.tests.gms.ui.adminui.GMSMonitorPage;
import com.genesyslab.functional.tests.gms.ui.callback.GMSCallbackPage;
import com.genesyslab.functional.tests.gms.ui.configuredservices.GMSConfiguredServicesPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by bvolovyk on 13.07.2017.
 */
public class GMSAdminUINavBarAbstractPage extends GMSAbstractPage {
    private By homeImgLocator = By.xpath("//*[@id='logo']");
    private By homeBtnLocator = By.xpath("//*[@id='home']");
    private By welcomeLocator = By.xpath("//*[@id='user-account']//a");
    private By logOutBtnLocator = By.xpath("//*[@id='logout-account']//a");
    private By monitorBtnLocator = By.xpath("//*[@id='system-nav']/span");
    private By servicesBtnLocator = By.xpath("//*[@id='services-nav']/span");
    private By callbackBtnLocator = By.xpath("//*[@id='callback-nav']/span");
    private By reportingBtnLocator = By.xpath("//*[@id='reporting-nav']/span");
    private By toolsBtnLocator = By.xpath("//*[@id='tools-nav']/span");
    private By labBtnLocator = By.xpath("//*[@id='lab-nav']/span");

    public GMSAdminUINavBarAbstractPage(WebDriver driver) {
        super(driver);
        waitForElementToBeVisible(homeImgLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        waitForElementToBeVisible(homeBtnLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        waitForElementToBeClickable(logOutBtnLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        if (!"GSG Admin".equals(driver.getTitle())) {
            throw new IllegalStateException("This is not the Admin UI page");
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
        return getText(homeBtnLocator);
    }

    public String getWelcomeText() {
        return getText(welcomeLocator);
    }

    public void clickLogOut() {
        driver.findElement(logOutBtnLocator).click();
    }

    public GMSLoginPage logOut() {
        clickLogOut();
        return new GMSLoginPage(driver);
    }

    public String getLogOutBtnName() {
        return getText(logOutBtnLocator);
    }

    public void clickMonitor() {
        driver.findElement(monitorBtnLocator).click();
    }

    public GMSMonitorPage goToMonitorAdminUIPage() {
        clickMonitor();
        return new GMSMonitorPage(driver);
    }

    public String getMonitorBtnName() {
        return getText(monitorBtnLocator);
    }

    public void clickServices() {
        driver.findElement(servicesBtnLocator).click();
    }

    public GMSConfiguredServicesPage goToConfiguredServicesUIPage() {
        clickServices();
        return new GMSConfiguredServicesPage(driver);
    }

    public String getServicesBtnName() {
        return getText(servicesBtnLocator);
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

    public void clickReporting() {
        driver.findElement(reportingBtnLocator).click();
    }

//    TODO: implement GMSReportingPage and uncomment the below code
//    public GMSReportingPage goToReportingAdminUIPage() {
//        clickReporting();
//        return new GMSReportingPage(driver);
//    }

    public String getReportingBtnName() {
        return getText(reportingBtnLocator);
    }

    public void clickTools() {
        driver.findElement(toolsBtnLocator).click();
    }

//    TODO: implement GMSServiceTemplatesPage and uncomment the below code
//    public GMSServiceTemplatesPage goToServiceTemplatesUIPage() {
//        clickTools();
//        return new GMSServiceTemplatesPage(driver);
//    }

    public String getToolsBtnName() {
        return getText(toolsBtnLocator);
    }

    public void clickLab() {
        driver.findElement(labBtnLocator).click();
    }

//    TODO: implement GMSLabPage and uncomment the below code
//    public GMSLabPage goToLabAdminUIPage() {
//        clickLab();
//        return new GMSLabPage(driver);
//    }

    public String getLabBtnName() {
        return getText(labBtnLocator);
    }
}
