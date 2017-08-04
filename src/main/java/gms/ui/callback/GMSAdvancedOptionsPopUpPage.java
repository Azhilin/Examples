package gms.ui.callback;

import com.genesyslab.functional.tests.gms.ui.abstractpage.GMSAbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Created by bvolovyk on 29.06.2017.
 */
public class GMSAdvancedOptionsPopUpPage extends GMSAbstractPage {
    private By popUpLocator = By.xpath("//div[@class='modal-content']");
    private By includeServiceIdColumnLocator = By.xpath("//input[@ng-model='columnData.serviceID']");///parent::*");
    private By stateCheckboxListLocator = By.xpath("//div[@class='float-parent']/div[1]//label/input");///parent::*");
    private By serviceCheckboxListLocator = By.xpath("//div[@class='float-parent']/div[2]//label/input");///parent::*");
    private By closeBtnLocator = By.xpath("//button[@ng-click='cancel()']");
    private By saveBtnLocator = By.xpath("//button[@ng-click='save()']");

    public GMSAdvancedOptionsPopUpPage(WebDriver driver) {
        super(driver);
        waitForElementToBeClickable(saveBtnLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        waitForElementToBeClickable(closeBtnLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        if (!driver.findElement(popUpLocator).isDisplayed()) {
            throw new IllegalStateException("Advanced Options isn't displayed!!!");
        }
    }

    public void checkIncludeServiceIdColumn() {
        WebElement element = driver.findElement(includeServiceIdColumnLocator);
        if (!element.isSelected()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
        GMSCallbackPage.setTableColumnCounter(1);
    }

    public void uncheckIncludeServiceIdColumn() {
        WebElement element = driver.findElement(includeServiceIdColumnLocator);
        if (element.isSelected()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);//use this in case you click on span elemen
        }
        GMSCallbackPage.setTableColumnCounter(0);
    }

    public void checkFilterTableByState(String cbState) {
        driver.findElement(By.xpath(String.format("//label[text()[normalize-space()='%s']]", cbState))).click();
        System.out.printf("Filter Callback Table by %s State.%n", cbState);
    }

    public void uncheckAllStates() {
        List<WebElement> stateList = driver.findElements(stateCheckboxListLocator);
        for (WebElement state : stateList) {
            if (state.isSelected()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", state);//use this in case you click on span elemen
            }
        }
        System.out.println("All checkboxes for Filter Callback Table by States have been unchecked.");
    }

    public void checkFilterTableByService(String cbServiceName) {
        driver.findElement(By.xpath(String.format("//label[text()[normalize-space()='%s']]", cbServiceName))).click();
        System.out.printf("Filter Callback Table by %s Service.%n", cbServiceName);
    }

    public void uncheckAllServices() {
        List<WebElement> serviceList = driver.findElements(serviceCheckboxListLocator);
        for (WebElement service : serviceList) {
            if (service.isSelected()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", service);//use this in case you click on span element
            }
        }
        System.out.println("All checkboxes for Filter Callback Table by Services have been unchecked.");
    }

    public void uncheckAllStatesAndServices() {
        uncheckAllStates();
        uncheckAllServices();
    }

    public void clickClose() {
        driver.findElement(closeBtnLocator).click();
    }

    public GMSCallbackPage closeAdvancedOptions() {
        clickClose();
        return new GMSCallbackPage(driver);
    }

    public void clickSave() {
        driver.findElement(saveBtnLocator).click();
    }

    public GMSCallbackPage saveAdvancedOptions() {
        clickSave();
        return new GMSCallbackPage(driver);
    }
}
