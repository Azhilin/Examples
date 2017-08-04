package gms.ui.callback;

import com.genesyslab.functional.tests.gms.ui.abstractpage.GMSAbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Created by bvolovyk on 29.06.2017.
 */
public class GMSCancelCallbacksConfirmationPopUpPage extends GMSAbstractPage {
    private By popUpLocator = By.xpath("//div[@class='modal-content']");
    private By closeBtnLocator = By.xpath("//button[@ng-click='cancel()']");
    private By confirmAndCancelCallbacksBtnLocator = By.xpath("//button[@ng-click='cancelSelectedCallbacks()']");
    private By callbacksToCancelNameLocator = By.xpath("//h4[@ng-if='!cancelAllCallbacks']");
    private By callbacksToCancelTblRowsLocator = By.xpath("//div[@class='modal-body']//tr");
    private By cancelAllCallbacksInCurrentTimeRangeCheckboxLocator = By.xpath("//div[@ng-if='enableBulkCancelAndExportCallback']/label");

    public GMSCancelCallbacksConfirmationPopUpPage(WebDriver driver) {
        super(driver);
        waitForElementToBeClickable(confirmAndCancelCallbacksBtnLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        waitForElementToBeClickable(closeBtnLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        if (!isCancelCallbacksConfirmationPopUpPresent()) {
            throw new IllegalStateException("Cancel Callbacks Confirmation isn't displayed!!!");
        }
    }

    public int countCallbackEntriesToCancel() {
        List<WebElement> callbackEntriesToCancel = driver.findElements(callbacksToCancelTblRowsLocator);
        int quantity = callbackEntriesToCancel.size();
        System.out.println("Callbacks to Cancel in Current Page displayed by UI: " + quantity);
        return quantity;
    }

    public void clickClose() {
        driver.findElement(closeBtnLocator).click();
    }

    public void clickConfirmAndCancelCallbacks() {
        driver.findElement(confirmAndCancelCallbacksBtnLocator).click();
    }

    public GMSCallbackPage confirmAndCancelCallbacks() {
        clickConfirmAndCancelCallbacks();
        return new GMSCallbackPage(driver);
    }

    public GMSCallbackPage confirmAndCancelCallbacks(GMSCancelCallbacksConfirmationPopUpPage cancelPopUpPage) {
        clickConfirmAndCancelCallbacks();
        waitForElementToBeNotVisible(popUpLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);//once the user clicks cancel the pop up will be dismissed
        return new GMSCallbackPage(driver, cancelPopUpPage);
    }

    public GMSCallbackPage closeCancelCallbacksConfirmation() {
        clickClose();
        return new GMSCallbackPage(driver);
    }

    public void checkCancelAllCallbacksInCurrentTimeRange() {
        driver.findElement(cancelAllCallbacksInCurrentTimeRangeCheckboxLocator).click();
    }

    public String getCancelAllCallbacksInCurrentTimeRangeCheckboxName() {
        return driver.findElement(cancelAllCallbacksInCurrentTimeRangeCheckboxLocator).getText();
    }

    public String getCallbacksToCancelName() {
        List<WebElement> elements = driver.findElements(callbacksToCancelNameLocator);
        return elements.get(0).getText();
    }

    public boolean isCancelAllCallbacksInCurrentTimeRangeChecked() {
        return driver
                .findElement(cancelAllCallbacksInCurrentTimeRangeCheckboxLocator)
                .findElement(By.xpath("//input"))
                .isSelected();
    }

    public boolean isCancelCallbacksConfirmationPopUpPresent() {
        return isElementPresent(popUpLocator);
    }
}
