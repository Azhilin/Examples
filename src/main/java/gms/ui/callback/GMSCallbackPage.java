package gms.ui.callback;

import com.genesyslab.functional.tests.gms.ui.abstractpage.GMSCompleteNavBarAbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by bvolovyk on 26.05.2017.
 */
public class GMSCallbackPage extends GMSCompleteNavBarAbstractPage {
    private static int tableColumnCounter = 0;
    private By dateFilterDropDwnLocator = By.xpath("//*[@id='callback_options']//button");
    private By dateFilterMenuLocator = By.xpath(".//*[@id='callback_options']//ul[@ng-show]");//expanded drop down menu
    private By dateFilterLast24HoursLocator = By.xpath(".//ul[@ng-show]/li[1]//*[contains(@id, '-select-default-id-')]/span");//item in expanded drop down menu
    private By dateFilterLast7DaysLocator = By.xpath(".//ul[@ng-show]/li[2]//*[contains(@id, '-select-default-id-')]/span");
    private By dateFilterLast30DaysLocator = By.xpath(".//ul[@ng-show]/li[3]//*[contains(@id, '-select-default-id-')]/span");
    private By dateFilterTodayLocator = By.xpath(".//ul[@ng-show]/li[4]//*[contains(@id, '-select-default-id-')]/span");
    private By dateFilterNext24HoursLocator = By.xpath(".//ul[@ng-show]/li[5]//*[contains(@id, '-select-default-id-')]/span");
    private By dateFilterNext7DaysLocator = By.xpath(".//ul[@ng-show]/li[6]//*[contains(@id, '-select-default-id-')]/span");
    private By dateFilterNext30DaysLocator = By.xpath(".//ul[@ng-show]/li[7]//*[contains(@id, '-select-default-id-')]/span");
    private By dateFilterCustomDateRangeLocator = By.xpath(".//ul[@ng-show]/li[8]//*[contains(@id, '-select-default-id-')]/span");
    private By custDateRangeErrMsgLocator = By.xpath("//div[@class='form-group has-feedback has-error']/span");
    private By custDateRangeStartDateLocator = By.xpath("//*[@id='startDate']");
    private By custDateRangeStartDayLocator = By.xpath("//ul[1]//span[@class='ng-binding text-info']");
    private By custDateRangeStartMonthAndYearLocator = By.xpath("//ul[1]//div[@class='month-index']//strong");
    private By custDateRangeStartYearLocator = By.xpath("//ul[1]//button[@type='button']/strong");
    private By custDateRangeEndDateLocator = By.xpath("//*[@id='endDate']");
    private By custDateRangeEndDayLocator = By.xpath("//ul[2]//span[@class='ng-binding text-info']");
    private By custDateRangeEndMonthAndYearLocator = By.xpath("//ul[2]//div[@class='month-index']//strong");
    private By custDateRangeEndYearLocator = By.xpath("//ul[2]//button[@type='button']/strong");
    private By submitCustomDateRangeBtnLocator = By.xpath("//input[@type='submit']");
    private By createCallbackBtnLocator = By.xpath("//div[@id='new-callback']/div");
    private By advancedOptionsBtnLocator = By.xpath("//*[@id='advanced-options']//*[@id='configure-table']");
    private By refreshBtnLocator = By.xpath("//*[@id='refreshQueue']");
    private By cancelCallbacksBtnLocator = By.xpath("//div[@ng-click='cancelSelectedCallbacks();']");
    private By spinnerForCallbackCancellation = By.xpath("//*[@ng-show='cancelStatus.cancellationActive']//div[@class='spin-circle']");
    private By downloadReportsBtnLocator = By.xpath("//div[@ng-if='enableBulkCancelAndExportCallback']");
    private By greenCheckmarkIconLocator = By.xpath("//span[@ng-if='cancelStatus.cancellationSuccess' and @class='fonticon icon-alert-checkmark ng-scope']");
    private By redXIconLocator = By.xpath("//span[@ng-if='!cancelStatus.cancellationSuccess' and @class='fonticon icon-circle-close ng-scope']");
    private By numberCallbacksFoundLocator = By.xpath("//*[@id='callback-table-container']//div[contains(@class, 'display-summary')]");
    private By spinnerAboveTableLocator = By.xpath("//div[@class='row-wrapper']//div[@class='spin-circle']");
    private By multipleCallbackSelectionCheckboxLocator = By.xpath("//*[@id='callback-table']//th[1]/span/span");
    private By serviceIdBtnLocator = By.xpath(String.format(".//th[%s]/*[@id='callback_state_header']", tableColumnCounter + 1));
    private By stateBtnLocator = By.xpath(String.format(".//th[%s]/*[@id='callback_state_header']", tableColumnCounter + 2));
    private By desiredCallbackTimeBtnLocator = By.xpath(String.format(".//th[%s]/*[@id='callback_state_header']", tableColumnCounter + 3));
    private By phoneNumberBtnLocator = By.xpath(String.format(".//th[%s]/*[@id='callback_state_header']", tableColumnCounter + 4));
    private By serviceNameBtnLocator = By.xpath(String.format(".//th[%s]/*[@id='callback_state_header']", tableColumnCounter + 5));
    private By noCallbacksFoundMsgLocator = By.xpath("//*[@id='no-callbacks']//p");
    private By callbackSettingsTblRowsLocator = By.xpath("//*[@id='CallbackSettingsTable']/tr");
    private By spinnerInsideTableLocator = By.xpath("//*[@id='callback-table']//div[@class='spin-circle']");
    private By backToTopBtnLocator = By.xpath("//*[@id='configure-table' and @ng-click='goToTop()']");

    public GMSCallbackPage(WebDriver driver) {
        super(driver);
        waitForElementToBeClickable(refreshBtnLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        waitFor(3000);//wait for spinner
        if (!driver.findElement(spinnerAboveTableLocator).isDisplayed()) {
            waitToClickOnElement(refreshBtnLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);// we added this because this place looks like bug
        }
//        waitForElementToBeClickable(spinnerAboveTableLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        if (!"GMS Management UI".equals(driver.getTitle())) {
            throw new IllegalStateException("Page title is incorrect!!!");
        }
        waitForElementToBeNotVisible(spinnerAboveTableLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
    }

    //use this constructor in case you purge (admin cancel) huge number of callbacks through UI
    //i.e. when after purge a test goes from GMSCancelCallbacksConfirmationPopUpPage to GMSCallbackPage
    public GMSCallbackPage(WebDriver driver, GMSCancelCallbacksConfirmationPopUpPage cancelPopUpPage) {
        super(driver);
        if (cancelPopUpPage != null) {
            assertThat(this.isCallbackCancelSpinnerDisplayed(), is(true));//once the user clicks cancel a spinner will appear near the cancellation button
            waitForElementToBeNotVisible(spinnerForCallbackCancellation, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);//after the cancellation is complete will this disappear
        }
        waitForElementToBeClickable(refreshBtnLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        waitForElementToBeClickable(spinnerAboveTableLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        if (!"GMS Management UI".equals(driver.getTitle())) {
            throw new IllegalStateException("Page title is incorrect!!!");
        }
        waitForElementToBeNotVisible(spinnerAboveTableLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
    }

    public static int getTableColumnCounter() {
        return tableColumnCounter;
    }

    public static void setTableColumnCounter(int tableColumnCounter) {
        GMSCallbackPage.tableColumnCounter = tableColumnCounter;
    }

    public void clickDateFilter() {
        driver.findElement(dateFilterDropDwnLocator).click();
    }

    public void setDateFilter(String item) {
        String initialDateFilterValue = getCurrentDateFilterName();
        String targetDateFilterValue;
        switch (item) {
            case "Last 24 Hours":
                targetDateFilterValue = getLast24HoursDateFilterName();
                break;
            case "Last 7 Days":
                targetDateFilterValue = getLast7DaysDateFilterName();
                break;
            case "Last 30 Days":
                targetDateFilterValue = getLast30DaysDateFilterName();
                break;
            case "Today":
                targetDateFilterValue = getTodayDateFilterName();
                break;
            case "Next 24 Hours":
                targetDateFilterValue = getNext24HoursDateFilterName();
                break;
            case "Next 7 Days":
                targetDateFilterValue = getNext7DaysDateFilterName();
                break;
            case "Next 30 Days":
                targetDateFilterValue = getNext30DaysDateFilterName();
                break;
            case "Custom Date Range":
                targetDateFilterValue = getCustomDateRangeDateFilterName();
                break;
            default:
                throw new NoSuchElementException("Please, choose correct date filter name!!!");
        }
        clickDateFilter();
        if (!targetDateFilterValue.equals(initialDateFilterValue)) {
            clickDateFilter();
            switch (item) {
                case "Last 24 Hours":
                    builder.moveToElement(driver.findElement(dateFilterLast24HoursLocator)).click()
                            .build()
                            .perform();
                    break;
                case "Last 7 Days":
                    builder.moveToElement(driver.findElement(dateFilterLast7DaysLocator)).click()
                            .build()
                            .perform();
                    break;
                case "Last 30 Days":
                    builder.moveToElement(driver.findElement(dateFilterLast30DaysLocator)).click()
                            .build()
                            .perform();
                    break;
                case "Today":
                    builder.moveToElement(driver.findElement(dateFilterTodayLocator)).click()
                            .build()
                            .perform();
                    break;
                case "Next 24 Hours":
                    builder.moveToElement(driver.findElement(dateFilterNext24HoursLocator)).click()
                            .build()
                            .perform();
                    break;
                case "Next 7 Days":
                    builder.moveToElement(driver.findElement(dateFilterNext7DaysLocator)).click()
                            .build()
                            .perform();
                    break;
                case "Next 30 Days":
                    builder.moveToElement(driver.findElement(dateFilterNext30DaysLocator)).click()
                            .build()
                            .perform();
                    break;
                case "Custom Date Range":
                    builder.moveToElement(driver.findElement(dateFilterCustomDateRangeLocator)).click()
                            .build()
                            .perform();
                    break;
            }
        } else {
            waitToClickOnElement(refreshBtnLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        }
//        waitForElementToBeClickable(spinnerAboveTableLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        waitFor(3000);//wait for spinner
        waitForElementToBeNotVisible(spinnerAboveTableLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        System.out.println("CALLBACK DATE FILTER WAS SET TO: " + item);
    }

    public String getCurrentDateFilterName() {
        return getText(dateFilterDropDwnLocator);
    }

    public String getLast24HoursDateFilterName() {
        if (!isElementPresent(dateFilterMenuLocator)) {
            clickDateFilter();
            waitForElementToBeVisible(dateFilterMenuLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        }
        return getTextFromSpan(dateFilterLast24HoursLocator);
    }

    public String getLast7DaysDateFilterName() {
        if (!isElementPresent(dateFilterMenuLocator)) {
            clickDateFilter();
            waitForElementToBeVisible(dateFilterMenuLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        }
        return getTextFromSpan(dateFilterLast7DaysLocator);
    }

    public String getLast30DaysDateFilterName() {
        if (!isElementPresent(dateFilterMenuLocator)) {
            clickDateFilter();
            waitForElementToBeVisible(dateFilterMenuLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        }
        return getTextFromSpan(dateFilterLast30DaysLocator);
    }

    public String getTodayDateFilterName() {
        if (!isElementPresent(dateFilterMenuLocator)) {
            clickDateFilter();
            waitForElementToBeVisible(dateFilterMenuLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        }
        return getTextFromSpan(dateFilterTodayLocator);
    }

    public String getNext24HoursDateFilterName() {
        if (!isElementPresent(dateFilterMenuLocator)) {
            clickDateFilter();
            waitForElementToBeVisible(dateFilterMenuLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        }
        return getTextFromSpan(dateFilterNext24HoursLocator);
    }

    public String getNext7DaysDateFilterName() {
        if (!isElementPresent(dateFilterMenuLocator)) {
            clickDateFilter();
            waitForElementToBeVisible(dateFilterMenuLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        }
        return getTextFromSpan(dateFilterNext7DaysLocator);
    }

    public String getNext30DaysDateFilterName() {
        if (!isElementPresent(dateFilterMenuLocator)) {
            clickDateFilter();
            waitForElementToBeVisible(dateFilterMenuLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        }
        return getTextFromSpan(dateFilterNext30DaysLocator);
    }

    public String getCustomDateRangeDateFilterName() {
        if (!isElementPresent(dateFilterMenuLocator)) {
            clickDateFilter();
            waitForElementToBeVisible(dateFilterMenuLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        }
        return getTextFromSpan(dateFilterCustomDateRangeLocator);
    }

    public String getCreateCallbackBtnName() {
        return getText(createCallbackBtnLocator);
    }

    public void clickAdvancedOptions() {
        driver.findElement(advancedOptionsBtnLocator).click();
    }

    public GMSAdvancedOptionsPopUpPage openAdvancedOptions() {
        clickAdvancedOptions();
        return new GMSAdvancedOptionsPopUpPage(driver);
    }

    public String getAdvancedOptionsBtnName() {
        return getText(advancedOptionsBtnLocator);
    }

    public boolean isRefreshBtnPresent() {
        return isElementPresent(refreshBtnLocator);
    }

    public void clickRefresh() {
        driver.findElement(refreshBtnLocator).click();
    }

    public String getRefreshBtnName() {
        return getText(refreshBtnLocator);
    }

    public void clickCancelCallbacks() {
        driver.findElement(cancelCallbacksBtnLocator).click();
    }

    public GMSCancelCallbacksConfirmationPopUpPage cancelCallbacks() {
        clickCancelCallbacks();
        return new GMSCancelCallbacksConfirmationPopUpPage(driver);
    }

    public String getCancelCallbacksBtnName() {
        return getText(cancelCallbacksBtnLocator);
    }

    public void clickDownloadReports() {
        driver.findElement(downloadReportsBtnLocator).click();
    }

    public GMSDownloadReportsPopUpPage downloadReports() {
        clickDownloadReports();
        return new GMSDownloadReportsPopUpPage(driver);
    }

    public boolean isCallbackCancelSpinnerDisplayed() {
        return driver.findElement(spinnerForCallbackCancellation).isDisplayed();
    }

    public String getDownloadReportsBtnName() {
        return getText(downloadReportsBtnLocator);
    }

    public boolean isGreenCheckmarkIconPresent() {
        return isElementPresent(greenCheckmarkIconLocator);
    }

    public boolean isRedXIconPresent() {
        return isElementPresent(redXIconLocator);
    }

    public String getCallbacksFoundText() {
        WebElement el = driver.findElement(numberCallbacksFoundLocator);
        String text = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].childNodes[2].textContent", el);
        return text.trim();
    }

    public void checkMultipleCallbackSelection() {
        driver.findElement(multipleCallbackSelectionCheckboxLocator).click();
    }

    public String getServiceIdBtnName() {
        if (tableColumnCounter == 0) {
            throw new NoSuchElementException("Include Service ID Column isn't checked in Advanced Options!!!");
        } else {
            return getText(serviceIdBtnLocator);
        }
    }

    public String getStateBtnName() {
        return getText(stateBtnLocator);
    }

    public String getDesiredCallbackTimeBtnName() {
        return getText(desiredCallbackTimeBtnLocator);
    }

    public String getPhoneNumberBtnName() {
        return getText(phoneNumberBtnLocator);
    }

    public String getServiceNameBtnName() {
        return getText(serviceNameBtnLocator);
    }

    public boolean isNoCallbacksFoundMsgDisplayed() {
        return driver.findElement(noCallbacksFoundMsgLocator).isDisplayed();
    }

    public String getNoCallbacksFoundMsg() {
        return getText(noCallbacksFoundMsgLocator);
    }

    public String getBackToTopBtnName() {
        return getText(backToTopBtnLocator);
    }

    public void setCustomDateRangeStartDate(String day, String month, String year) {
        driver.findElement(custDateRangeStartDateLocator).click();
        waitForElementToBeClickable(custDateRangeStartMonthAndYearLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS).click();
        driver.findElement(custDateRangeStartYearLocator).click();
        setCustomDateRangeStartYear(year);
        setCustomDateRangeStartMonth(month);
        setCustomDateRangeStartDay(day);
    }

    public String getCustomDateRangeStartDate() {
        driver.findElement(custDateRangeStartDateLocator).click();
        String selectedDay = getCustomDateRangeStartDay();
        String selectedMonthAndYear = getCustomDateRangeStartMonthAndYear();
        return selectedDay + " " + selectedMonthAndYear;
    }

    public void setCustomDateRangeEndDate(String day, String month, String year) {
        driver.findElement(custDateRangeEndDateLocator).click();
        driver.findElement(custDateRangeEndMonthAndYearLocator).click();
        driver.findElement(custDateRangeEndYearLocator).click();
        setCustomDateRangeEndYear(year);
        setCustomDateRangeEndMonth(month);
        setCustomDateRangeEndDay(day);
    }

    public String getCustomDateRangeEndDate() {
        driver.findElement(custDateRangeEndDateLocator).click();
        String selectedDay = getCustomDateRangeEndDay();
        String selectedMonthAndYear = getCustomDateRangeEndMonthAndYear();
        return selectedDay + " " + selectedMonthAndYear;
    }

    public void submitCustomDateRange() {
        driver.findElement(submitCustomDateRangeBtnLocator).click();
        if (!isCustomDateRangeErrMsgPresent()) {
            waitForElementToBeClickable(spinnerAboveTableLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
            waitForElementToBeNotVisible(spinnerAboveTableLocator, TIMEOUT_SECONDS, POLLING_TIME_MILLIS);
        }
    }

    public boolean isCustomDateRangeErrMsgPresent() {
        return isElementPresent(custDateRangeErrMsgLocator);
    }

    public String getCustomDateRangeErrMsg() {
        return getText(custDateRangeErrMsgLocator);
    }

    public int countCallbackEntriesByUI() {
        List<WebElement> callbackEntries = driver.findElements(callbackSettingsTblRowsLocator);
        int quantity = callbackEntries.size();
        System.out.println("CALLBACK ENTRIES IN UI: " + quantity);
        return quantity;
    }

    private void setCustomDateRangeStartDay(String day) {
        driver.findElement(By.xpath(String.format("//ul[1]//span[normalize-space(text())='%s' and not(@class='ng-binding text-muted')]", day))).click();
    }

    private String getCustomDateRangeStartDay() {
        return driver.findElement(custDateRangeStartDayLocator).getText().trim();
    }

    private void setCustomDateRangeStartMonth(String month) {
        driver.findElement(By.xpath(String.format("//ul[1]//span[contains(text(),'%s')]", month))).click();
    }

    private void setCustomDateRangeStartYear(String year) {
        driver.findElement(By.xpath(String.format("//ul[1]//span[contains(text(),'%s')]", year))).click();
    }

    private String getCustomDateRangeStartMonthAndYear() {
        return driver.findElement(custDateRangeStartMonthAndYearLocator).getText().trim();
    }

    private void setCustomDateRangeEndDay(String day) {
        driver.findElement(By.xpath(String.format("//ul[2]//span[normalize-space(text())='%s' and not(@class='ng-binding text-muted')]", day))).click();
    }

    private String getCustomDateRangeEndDay() {
        return driver.findElement(custDateRangeEndDayLocator).getText().trim();
    }

    private void setCustomDateRangeEndMonth(String month) {
        driver.findElement(By.xpath(String.format("//ul[2]//span[contains(text(),'%s')]", month))).click();
    }

    private void setCustomDateRangeEndYear(String year) {
        driver.findElement(By.xpath(String.format("//ul[2]//span[contains(text(),'%s')]", year))).click();
    }

    private String getCustomDateRangeEndMonthAndYear() {
        return driver.findElement(custDateRangeEndMonthAndYearLocator).getText().trim();
    }
}
