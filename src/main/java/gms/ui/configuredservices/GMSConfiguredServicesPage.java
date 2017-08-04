package gms.ui.configuredservices;

import com.genesyslab.functional.tests.gms.ui.abstractpage.GMSCompleteNavBarAbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.concurrent.Callable;

import static com.jayway.awaitility.Awaitility.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public class GMSConfiguredServicesPage extends GMSCompleteNavBarAbstractPage {
    private By toolsMenuLocator = By.id("nav_tools");
    private By patternsMenu = By.id("nav_tools_patterns");
    private By searchItemsFieldLocator = By.xpath("//*[@id='officeSearchBar']//input");
    private By createServiceBtnLocator = By.xpath("//span[@ng-click='createService()']/span[text()]");
    private By deleteServiceBtnLocator = By.xpath("//span[@ng-click='deleteService()']/span[text()]");
    private By configuredServicesTitleLocator = By.xpath("//span[contains(@class, 'icon-phone')]/following-sibling::label");
    private By configuredServicesListLocator = By.xpath("//ul/li[1]//ul//span[contains(@class,'htmlContentSpan') or @title]");
    private By officeHoursTitleLocator = By.xpath("//span[contains(@class, 'icon-calendar')]/following-sibling::label");
    private By officeHoursListLocator = By.xpath("//ul/li[2]//ul//span[contains(@class,'htmlContentSpan') or @title]");
    private By capacityTitleLocator = By.xpath("//span[contains(@class, 'icon-view')]/following-sibling::label");
    private By capacityListLocator = By.xpath("//ul/li[2]//ul//span[contains(@class,'htmlContentSpan') or @title]");
    private By noServicesFoundLocator = By.xpath("//div[@ng-if='empty']/h2");
    private By serviceNameLocator = By.xpath("//span[@class='pull-left']/h2[@class='ng-binding']");
    private By spinnerLocator = By.xpath("//div[@class='spin-circle']");
    private By toasterNotificationLocator = By.xpath("//div[@ng-class='config.title']");

    public GMSConfiguredServicesPage(WebDriver driver) {
        super(driver);
        if (!isElementPresent(noServicesFoundLocator)) {
//            waitForElementToBeClickable(spinnerLocator, TIMEOUT_SECONDS, TIMEOUT_SECONDS);
            waitForElementToBeVisible(serviceNameLocator, TIMEOUT_SECONDS, TIMEOUT_SECONDS);
        }
        if (!"GMS Management UI".equals(driver.getTitle())) {
            throw new IllegalStateException("Page title is incorrect!!!");
        }
    }

    public String getSearchItemsPlaceholderText() {
        return getTextFromPlaceholder(searchItemsFieldLocator);
    }

    public void clickCreate() {
        driver.findElement(createServiceBtnLocator).click();
    }

    public GMSAddNewServicePopUpPage createService() {
        clickCreate();
        return new GMSAddNewServicePopUpPage(driver);
}

    public String getCreateBtnName() {
        return getTextFromSpan(createServiceBtnLocator);
    }

    public void createCallbackService(String cbServiceName, String cbDefaultConfiguration) {
        System.out.println("Callback service creation through UI...");
        int initialConfiguredServicesNumber = getConfiguredServices().size();
        System.out.printf("There are %s Configured Services already existed.%n", initialConfiguredServicesNumber);
        GMSAddNewServicePopUpPage addNewServicePopUpPage = createService();
        addNewServicePopUpPage.addCallbackService(cbServiceName, cbDefaultConfiguration);
        waitForElementToBeVisible(toasterNotificationLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        System.out.println(getToasterNotificationText());
        waitForElementToBeClickable(spinnerLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        waitForElementToBeVisible(serviceNameLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
    }

    public void clickDelete() {
        driver.findElement(deleteServiceBtnLocator).click();
    }

//    TODO: implement GMSDeleteServicePopUpPage and uncomment the below code
//    public GMSDeleteServicePopUpPage deleteService() {
//        clickDelete();
//        return new GMSDeleteServicePopUpPage(driver);
//    }

    public String getDeleteBtnName() {
        return getTextFromSpan(deleteServiceBtnLocator);
    }

    public String getConfiguredServicesText() {
        return getText(configuredServicesTitleLocator);
    }

    public List<WebElement> getConfiguredServices() {
        return driver.findElements(configuredServicesListLocator);
    }

    public String getOfficeHoursText() {
        return getText(officeHoursTitleLocator);
    }

    public List<WebElement> getOfficeHoursServices() {
        return driver.findElements(officeHoursListLocator);
    }

    public String getCapacityText() {
        return getText(capacityTitleLocator);
    }

    public List<WebElement> getCapacityServices() {
        return driver.findElements(capacityListLocator);
    }

    public String getNoServicesFoundText() {
        return getText(noServicesFoundLocator);
    }

    public String getDisplayedServiceName() {
        return getText(serviceNameLocator).trim();
    }

    public boolean isRequestParameterOption(String option) {
        List<WebElement> elements = driver.findElements(By.xpath(String.format("//span[text()[normalize-space()='%s']]//parent::*/following-sibling::*[contains(@class,'table-description')]//span[@ng-if]", option)));
        return !elements.isEmpty();
    }

    public String getOptionDescriptionText(String option) {
        WebElement el = driver.findElement(By.xpath(String.format("//span[text()[normalize-space()='%s']]/parent::*/following-sibling::*[contains(@class,'table-description')]//span[not(@ng-if)]", option)));
        String optionDes = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].innerHTML", el);
        return optionDes.trim();
    }

    public String getOptionValueText(String option) {
        WebElement el = driver.findElement(By.xpath(String.format("//span[text()[normalize-space()='%s']]//parent::*/following-sibling::*[contains(@class,'table-value')]//span[@ng-if]", option)));
        String optionVal = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].innerHTML", el);
        return optionVal.trim();
    }

    public String getToasterNotificationText() {
        return getText(toasterNotificationLocator);
    }

    public void waitingChangesInConfiguredServicesList(int initialSize, long timeoutSeconds) {
        given().ignoreExceptions()
                .await()
                .atMost(timeoutSeconds, SECONDS)
                .until(gettingConfiguredServicesListSize(), not(equalTo(initialSize)));
    }

    private Callable<Integer> gettingConfiguredServicesListSize() {
        return new Callable<Integer>() {
            @Override
            public Integer call() {
                return getConfiguredServices().size(); // The condition supplier part
            }
        };
    }

}
