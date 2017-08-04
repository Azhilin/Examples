package gms.ui.callback;

import com.genesyslab.functional.tests.gms.ui.abstractpage.GMSAbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by bvolovyk on 30.06.2017.
 */
public class GMSDownloadReportsPopUpPage extends GMSAbstractPage {
    private By popUpLocator = By.xpath("//div[@class='modal-content']");
    private By refreshBtnLocator = By.xpath("//*[@ng-click='getCancelledCallbacksCSV()' and @id='refreshQueue']/span");
    private By exportCancelledCallbacksLocator = By.xpath("///tbody/tr[2]/td[@class='ng-binding']");
    private By exportBtnLocator = By.xpath("//button[@ng-click='exportCancelledCallbacksCSV()']");
    private By spinnerLocator = By.xpath("//div[@ng-show='showDownload']//div[@class='spin-circle']");
    private By cancellationSummaryReportLocator = By.xpath("//tr[@ng-if='cancellationComplete']/td[1]");
    private By greenCheckmarkIconLocator = By.xpath("//td/span[@ng-if='cancellationSuccess' and @class='fonticon icon-alert-checkmark ng-scope']");
    private By redXIconLocator = By.xpath("//td/span[@ng-if='!cancellationSuccess' and @class='fonticon icon-circle-close ng-scope']");
    private By downloadBtnLocator = By.xpath("//button[@ng-click='downloadCallbackCancellationReport()']");
    private By closeBtnLocator = By.xpath("//button[@ng-click='cancel()']");

    public GMSDownloadReportsPopUpPage(WebDriver driver) {
        super(driver);
        waitForElementToBeClickable(exportBtnLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        waitForElementToBeClickable(closeBtnLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        if (!driver.findElement(popUpLocator).isDisplayed()) {
            throw new IllegalStateException("Download Reports isn't displayed!!!");
        }
    }

    public void clickDownload() {
        driver.findElement(downloadBtnLocator).click();
    }
    
    public void clickExport(){
        driver.findElement(exportBtnLocator).click();
    }

    public void clickClose() {
        driver.findElement(closeBtnLocator).click();
    }

    public GMSCallbackPage closeDownloadReports() {
        clickClose();
        return new GMSCallbackPage(driver);
    }

    public boolean isCancellationSummaryReportPresent() {
        return isElementPresent(cancellationSummaryReportLocator);
    }

    public boolean isGreenCheckmarkIconPresent() {
        return isElementPresent(greenCheckmarkIconLocator);
    }

    public boolean isRedXIconPresent() {
        return isElementPresent(redXIconLocator);
    }
}
