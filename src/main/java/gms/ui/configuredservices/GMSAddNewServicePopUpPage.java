package gms.ui.configuredservices;

import com.genesyslab.functional.tests.gms.ui.abstractpage.GMSAbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Created by bvolovyk on 08.06.2017.
 */
public class GMSAddNewServicePopUpPage extends GMSAbstractPage {
    private By serviceTemplateDropDownLocator = By.xpath("//button[@ng-model='template.templateChosen']/span");
    private By serviceNameFieldLocator = By.xpath("//form/input[contains(@class, 'form-control')]");
    private By commonDefaultConfigurationDropDownLocator = By.xpath("//button[@ng-model='template.profileChosen']/span");
    private By cancelBtnLocator = By.xpath("//button[@ng-click='cancel()']");
    private By addBtnLocator = By.xpath("//button[@ng-click='add()']");

    public GMSAddNewServicePopUpPage(WebDriver driver) {
        super(driver);
        waitForElementToBeClickable(serviceNameFieldLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
    }

    public void setServiceTemplate(String item) {
        WebElement dropdown = driver.findElement(serviceTemplateDropDownLocator);
        dropdown.click();
        builder.moveToElement(dropdown)
                .moveToElement(
                        driver.findElement(By.linkText(item))).click()
                .build()
                .perform();
        System.out.println("Service Template was set to: " + item);
    }

    public void setCommonDefaultConfiguration(String item) {
        driver.findElement(commonDefaultConfigurationDropDownLocator).click();
        driver.findElement(By.linkText(item)).click();
        System.out.println("Common Default Configuration was set to: " + item);
    }

    public GMSAddNewServicePopUpPage typeServiceName(String serviceName) {
        driver.findElement(serviceNameFieldLocator).clear();
        driver.findElement(serviceNameFieldLocator).sendKeys(serviceName);
        return this;
    }

    public void clickAdd() {
        driver.findElement(addBtnLocator).click();
    }

    public GMSConfiguredServicesPage addCallbackService(String cbServiceName, String cbDefaultConfiguration) {
        setServiceTemplate("callback");
        typeServiceName(cbServiceName);
        setCommonDefaultConfiguration(cbDefaultConfiguration);
        clickAdd();
        return new GMSConfiguredServicesPage(driver);
    }

}
