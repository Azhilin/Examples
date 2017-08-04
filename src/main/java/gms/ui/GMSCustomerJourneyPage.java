package gms.ui;

import com.genesyslab.functional.tests.gms.ui.abstractpage.GMSAbstractPage;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.sikuli.webdriver.ImageElement;
import org.sikuli.webdriver.SikuliFirefoxDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class GMSCustomerJourneyPage extends GMSAbstractPage {
    private static final By usernameLocator = By
            .cssSelector("a.ng-scope.dropdown-toggle > span.ng-scope.ng-binding");
    private static final By logOutButtonLocator = By.linkText("Log out");
//	private By contextHelpLocator = By.xpath("//div/div/span[@id=\"context-help\"]");
    private By contextHelpLocator = By.xpath("//div[@id=\"context-help\"]/div/span");
    private By clarification = By.xpath("//h3");// which of these customers are
    // you referring to?
    private By searchBoxLocator = By.id("search-box");
    // private By clearTextLocator = By
    // .xpath("//div/span[2][@ng-show=\"hasContent\"]");// "X"
    private By clearTextLocator = By.xpath("//div/span[2]");
    // private By listOfFiltersLocator =By.className("filter-option");
    private By listOfFiltersLocator = By
            .xpath("//span[@class=\"filter-option\"]");
    private By cancelButton = By.xpath("//button[contains(text(),'Cancel')]");
    private By resetButton = By.xpath("//button[contains(text(),'Reset')]");
    private By homeButtonLocator = By.linkText("Home");
    private By selectionLocator = By.cssSelector("label.ng-scope.ng-binding");
    private By moveToCurrentDate = By
            .xpath("//div[@id='my-timeline']/div/div[2]/div[3]/div[5]");
    private By customerProfile = By.xpath("//li/a[contains(text(),\"Customer Profile\")]");
    private By KPIs = By.linkText("KPIs");
    private By journeyDetails = By.linkText("Journey Details");
    private By filterButtonLocator = By.cssSelector("button.btn.btn-primary.filterButton.ng-scope");
    private By applyfilterButtonLocator = By.cssSelector("div.modal-footer > button.btn.btn-primary");
    private By statusFilterDropdown = By.xpath("//div[3]/div/div/div/div[4]/div[1]/div/button");
    private By dateTimeFilterDropdown = By.xpath("//div[3]/div/div/div/div[3]/div[1]/div/button");
    private By serviceTypeFilterDropdown = By.xpath("//div[3]/div/div/div/div[3]/div[2]/div/button");
    private By stateTypeFilterDropdown = By.xpath("//button[@ng-options=\"type as type for type in stateTypes\"]");
    private By filterByDropdown = By.xpath("//div[3]/div/div/div/div[3]/div[1]/div[2]/span/div/button");
    private By filterByNameTextField = By.xpath("//div[3]/div/div/div/div[5]/div/input[@type='text']");
    private By filterFormLocator = By.xpath("//div[@class='modal-content']");
    private By alertIcon = By.xpath("//span[@class=\"fonticon icon-alert-circle\"]");
    private By alertMessage = By.cssSelector("div.modal-dialog div.modal-content div.ng-scope div h3.ng-scope.ng-binding");
    private String pathToFile = "file:///C:\\Mercurial\\cs\\gms_cs\\gmscs\\images\\";
    private ImageElement image;

    public GMSCustomerJourneyPage(WebDriver driver) {
        super(driver);
        if (!"GMS Development".equals(driver.getTitle())) {
            throw new IllegalStateException(
                    "This is not the ContextServices page");
        }
    }

    public By getSearchBoxLocator() {
        return searchBoxLocator;
    }

    public By getSelectionLocator() {
        return selectionLocator;
    }

    public void clickLogOutButton() {
        waitForLocator(usernameLocator);
        driver.findElement(usernameLocator).click();
        driver.findElement(logOutButtonLocator).click();
        waitForLocator(By.xpath("//h2[text()='Welcome']"));
    }

    public void clickCancelButton() {
        waitForLocator(cancelButton);
        driver.findElement(cancelButton).click();
    }

    public void clickResetButton() {
        waitForLocator(resetButton);
        driver.findElement(resetButton).click();
    }

    public void verifyCustomerName(String customerName) {
        String name = driver.findElement(By.cssSelector("td.ng-binding")).getText();
        assertTrue(name.equals(customerName));
    }

    public void verifyCustomerTitle(String customerTitle) {
        String title = driver.findElement(By.xpath("//div[2]/div[2]/div[2]/div[1]/table/tbody/tr[9]/td[@class=\"ng-scope ng-binding\"]")).getText();
        assertEquals(customerTitle, title);
    }

    public void verifyCustomerCity(String customerCity) {
        String city = driver.findElement(By.xpath("//div/div[2]/div[2]/div[2]/div[3]/table/tbody/tr[9]/td[@class=\"ng-scope ng-binding\"]")).getText();
        assertEquals(customerCity, city);
    }

    public void verifyCustomerLanguage(String customerLanguage) {
        String language = driver.findElement(By.xpath("//div/div[2]/div[2]/div[2]/div[3]/table/tbody/tr[10]/td[@class=\"ng-scope ng-binding\"]")).getText();
        assertEquals(customerLanguage, language);
    }

    public void verifyCustomerPhone(String customerPhone) {
        String phone = driver.findElement(By.xpath("//div[2]/div[2]/div[2]/div[5]/table/tbody/tr[4]/td")).getText();
        assertEquals(customerPhone, phone);
    }

    public void verifyCustomerEmail(String customerEmail) {
        String email = driver.findElement(By.xpath("//div[2]/div[2]/div[2]/div[5]/table/tbody/tr[5]/td")).getText();
        assertEquals(customerEmail, email);
    }

    public void verifyNoServiceSelected(String message) {
        ///html/body/div/div[1]/div/div[3]/div/h1
        String error = driver.findElement(By.xpath("//div[@class=\"ServiceInfo ng-scope\"]/h2")).getText();
        assertTrue(error.contains(message));
    }

    public void verifyJourneyDetails(String res_typeStarted, String res_typeCompleted, String med_typeStarted,
                                     String med_typeCompleted, String inter_idStarted, String inter_idCompleted
            , String app_typeStarted, String app_typeCompleted) {
        //resource_type started
        String resource_typeStarted = driver.findElement(By.xpath("//table/tbody/tr[3]/td[2]")).getText();
        assertEquals(res_typeStarted, resource_typeStarted);
        //resource_type completed
        String resource_typeCompleted = driver.findElement(By.xpath("//table/tbody/tr[4]/td[2]")).getText();
        assertEquals(res_typeCompleted, resource_typeCompleted);
        //media_type started
        String media_typeStarted = driver.findElement(By.xpath("//table/tbody/tr[5]/td[2]")).getText();
        assertEquals(med_typeStarted, media_typeStarted);
        //resource_type completed
        String media_typeCompleted = driver.findElement(By.xpath("//table/tbody/tr[6]/td[2]")).getText();
        assertEquals(med_typeCompleted, media_typeCompleted);
        //interaction_id started
        String interaction_idStarted = driver.findElement(By.xpath("//table/tbody/tr[7]/td[2]")).getText();
        assertEquals(inter_idStarted, interaction_idStarted);
        //interaction_id completed
        String interaction_idCompleted = driver.findElement(By.xpath("//table/tbody/tr[8]/td[2]")).getText();
        assertEquals(inter_idCompleted, interaction_idCompleted);
        //application_type started
        String application_typeStarted = driver.findElement(By.xpath("//table/tbody/tr[9]/td[2]")).getText();
        assertEquals(app_typeStarted, application_typeStarted);
        //application_type completed
        String application_typeCompleted = driver.findElement(By.xpath("//table/tbody/tr[10]/td[2]")).getText();
        assertEquals(app_typeCompleted, application_typeCompleted);
    }

    public void verifyJourneyDetailsActiveState(String res_type, String med_type, String inter_id, String app_type) {
        String resource_type = driver.findElement(By.xpath("//table/tbody/tr[2]/td[2]")).getText();
        assertEquals(res_type, resource_type);

        String media_type = driver.findElement(By.xpath("//table/tbody/tr[3]/td[2]")).getText();
        assertEquals(med_type, media_type);

        String interaction_id = driver.findElement(By.xpath("//table/tbody/tr[4]/td[2]")).getText();
        assertEquals(inter_id, interaction_id);

        String application_type = driver.findElement(By.xpath("//table/tbody/tr[5]/td[2]")).getText();
        assertEquals(app_type, application_type);
    }

    public void verifyJourneyDetailsCompletedState(String res_typeStarted, String res_typeCompleted, String med_typeStarted,
                                                   String med_typeCompleted, String inter_idStarted, String inter_idCompleted
            , String app_typeStarted, String app_typeCompleted) {

        String resource_typeStarted = driver.findElement(By.xpath("//table/tbody/tr[3]/td[2]")).getText();
        assertEquals(res_typeStarted, resource_typeStarted);
        //resource_type completed
        String resource_typeCompleted = driver.findElement(By.xpath("//table/tbody/tr[4]/td[2]")).getText();
        assertEquals(res_typeCompleted, resource_typeCompleted);
        //media_type started
        String media_typeStarted = driver.findElement(By.xpath("//table/tbody/tr[5]/td[2]")).getText();
        assertEquals(med_typeStarted, media_typeStarted);
        //resource_type completed
        String media_typeCompleted = driver.findElement(By.xpath("//table/tbody/tr[6]/td[2]")).getText();
        assertEquals(med_typeCompleted, media_typeCompleted);
        //interaction_id started
        String interaction_idStarted = driver.findElement(By.xpath("//table/tbody/tr[7]/td[2]")).getText();
        assertEquals(inter_idStarted, interaction_idStarted);
        //interaction_id completed
        String interaction_idCompleted = driver.findElement(By.xpath("//table/tbody/tr[8]/td[2]")).getText();
        assertEquals(inter_idCompleted, interaction_idCompleted);
        //application_type started
        String application_typeStarted = driver.findElement(By.xpath("//table/tbody/tr[9]/td[2]")).getText();
        assertEquals(app_typeStarted, application_typeStarted);
        //application_type completed
        String application_typeCompleted = driver.findElement(By.xpath("//table/tbody/tr[10]/td[2]")).getText();
        assertEquals(app_typeCompleted, application_typeCompleted);
    }

    public void verifyTooltipExpandTimeline() {//@class='fonticon icon-zoom-in zoom-in'
        WebElement zoomIn = driver.findElement(By.xpath("//div[@class='vco-toolbar']/div[1]"));
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        builder.clickAndHold(zoomIn).perform();
        String toolTipText = zoomIn.getAttribute("data-original-title");
        assertEquals("Expand Timeline", toolTipText);

//		new WebDriverWait(driver, 5000)
//	    .ignoring(StaleElementReferenceException.class)
//	    .until(new Predicate<WebDriver>() {
//	        @Override
//	        public boolean apply(WebDriver driver) {
//	        	WebElement zoomIn = driver.findElement(By.xpath("//div[@class='vco-toolbar']/div[1]"));	
//	    	    builder.clickAndHold(zoomIn).perform();		    	  
//	    		String toolTipText = zoomIn.getAttribute("data-original-title");
//	    		assertEquals("Expand Timeline", toolTipText);	    	
//	    		System.out.println(toolTipText);
//	            return true;
//	        }
//	    });

    }

    public void verifyTooltipContractTimeline() {
        new WebDriverWait(driver, 5000)
                .ignoring(StaleElementReferenceException.class)
                .until(new Function<WebDriver, Boolean>() {
                    @Override
                    public Boolean apply(WebDriver driver) {
                        WebElement zoomIn = driver.findElement(By.xpath("//div[@class='vco-toolbar']/div[2]"));
                        builder.clickAndHold(zoomIn).perform();
                        String toolTipText = zoomIn.getAttribute("data-original-title");
                        assertEquals("Contract Timeline", toolTipText);
                        System.out.println(toolTipText);
                        return true;
                    }
                });
    }

    public void verifyTooltipMoveToFirst() {
        new WebDriverWait(driver, 5000)
                .ignoring(StaleElementReferenceException.class)
                .until(new Function<WebDriver, Boolean>() {
                    @Override
                    public Boolean apply(WebDriver driver) {
                        WebElement zoomIn = driver.findElement(By.xpath("//div[@class='vco-toolbar']/div[3]"));
                        builder.clickAndHold(zoomIn).perform();
                        String toolTipText = zoomIn.getAttribute("data-original-title");
                        assertEquals("Move to First", toolTipText);
                        System.out.println(toolTipText);
                        return true;
                    }
                });
    }

    public void verifyTooltipMoveToLast() {
        new WebDriverWait(driver, 5000)
                .ignoring(StaleElementReferenceException.class)
                .until(new Function<WebDriver, Boolean>() {
                    @Override
                    public Boolean apply(WebDriver driver) {
                        WebElement zoomIn = driver.findElement(By.xpath("//div[@class='vco-toolbar']/div[4]"));
                        builder.clickAndHold(zoomIn).perform();
                        String toolTipText = zoomIn.getAttribute("data-original-title");
                        assertEquals("Move to Last", toolTipText);
                        System.out.println(toolTipText);
                        return true;
                    }
                });
    }

    public void verifyTooltipMoveToCurrentDate() {
        new WebDriverWait(driver, 5000)
                .ignoring(StaleElementReferenceException.class)
                .until(new Function<WebDriver, Boolean>() {
                    @Override
                    public Boolean apply(WebDriver driver) {
                        WebElement zoomIn = driver.findElement(By.xpath("//div[@class='vco-toolbar']/div[5]"));
                        builder.clickAndHold(zoomIn).perform();
                        String toolTipText = zoomIn.getAttribute("data-original-title");
                        assertEquals("Move to Current Date", toolTipText);
                        System.out.println(toolTipText);
                        return true;
                    }
                });
    }

    /**
     * overall services created
     *
     * @param totalJourneys
     */
    public void verifyTotalJourneys(String totalJourneys) {
        String totalJourneysValue = driver.findElement(By.cssSelector("div.KPIs.ng-scope div.infoBox.col-sm-6.col-md-6.col-lg-6.ng-scope div h2 b.ng-binding")).getText();
        assertEquals(totalJourneys, totalJourneysValue);
    }

    /**
     * active services
     *
     * @param activeJourneys
     */
    public void verifyActiveJourneys(String activeJourneys) {
        String activeJourneysValue = driver.findElement(By.xpath("//div[3]/div[2]/div[2]/div[3]/div/h2/b")).getText();
        assertEquals(activeJourneys, activeJourneysValue);
    }

    public void verifyCompletedJourneys(String completedJourneys) {
        String completedJourneysValue = driver.findElement(By.xpath("//div[3]/div[2]/div[2]/div[4]/div/h2/b")).getText();
        assertEquals(completedJourneys, completedJourneysValue);
    }

    public void verifyTotalStates(String totalStates) {
        String totalStatesValue = driver.findElement(By.xpath("//div[3]/div[2]/div[2]/div[5]/div/h2/b")).getText();
        assertEquals(totalStates, totalStatesValue);
    }

    public void verifyActiveStates(String activeStates) {
        String activeStatesValue = driver.findElement(By.xpath("//div[3]/div[2]/div[2]/div[7]/div/h2/b")).getText();
        assertEquals(activeStates, activeStatesValue);
    }

    public void verifyCompletedStates(String completedStates) {
        String completedStatesValue = driver.findElement(By.xpath("//div[3]/div[2]/div[2]/div[8]/div/h2/b")).getText();
        assertEquals(completedStates, completedStatesValue);
    }

    public void verifyLabelForServiceSelection(String serviceLabel) {
        String label = driver.findElement(By.xpath("//b[@ng-bind=\"serviceNameLabel\"]")).getText();
        assertEquals(serviceLabel, label);
    }

    public void verifySomeLabelForServiceSelectionPresent(String serviceLabel) {
        String label = driver.findElement(By.xpath("//b[@ng-bind=\"serviceNameLabel\"]")).getText();
        assertTrue(label.contains(serviceLabel) || label.contains("No states exist"));
    }

    public void verifyStatePresent(String stateName) {
        String state = driver.findElement(By.xpath("//h4[contains(text(),'" + stateName + "')]")).getText();
        assertEquals(stateName, state);
    }

    public void verifyNoStatesExist() {
        String noState = driver.findElement(By.xpath("//b[@ng-bind=\"noStatesLabel\"]")).getText();
        assertEquals("No states exist.", noState);
    }

    public void clickState1() {
        //	driver.findElement(By.xpath("//h4[contains(text(),'"+ stateName +"')]")).click();
        driver.findElement(By.xpath("//ul[@id='interaction-inner']/li[2]/div/div[2]")).click();
    }

    public void clickState2() {
        driver.findElement(By.xpath("//ul[@id='interaction-inner']/li[3]/div/div[2]/h4")).click();
    }

    /**
     * Wait for error such as "Couldn't find any services.."
     *
     * @return
     */
    public String waitForContextHelp() {
        waitForLocator(contextHelpLocator);
        waitFor(500);
        return driver.findElement(contextHelpLocator).getText();
    }

    /**
     * Wait for clarification such as
     * "Which of these customers are you referring to"
     *
     * @return
     */
    public String waitForClarification() {
        waitForLocator(clarification);
        waitFor(500);
        return driver.findElement(clarification).getText();
    }

    /**
     * Get Column header in the table
     *
     * @param columnNumber
     * @return
     */
    public WebElement getTableColumnHeader(int columnNumber) {
        return driver.findElement(By.xpath("//thead/tr/th[" + columnNumber
                + "]/strong"));
    }

    /**
     * Get record from the table [row;column]
     *
     * @param rowNumber
     * @param columnNumber
     * @return
     */
    public WebElement getTableRow(int rowNumber, int columnNumber) {
        return driver.findElement(By.xpath("//tbody/tr[" + rowNumber + "]/td["
                + columnNumber + "]"));
    }

    public void clearInputText() {
        click(clearTextLocator);
    }

    public void clickHomeButton() {
        click(homeButtonLocator);
    }

    public void moveToCurrentDate() {
        click(moveToCurrentDate);
    }

    private void click(By by) {
        driver.findElement(by).click();
    }

    public void clickCustomerProfile() {
        click(customerProfile);
    }

    public void clickKPIs() {
        new WebDriverWait(driver, 5000)
                .ignoring(StaleElementReferenceException.class)
                .until(new Function<WebDriver, Boolean>() {
                    @Override
                    public Boolean apply(WebDriver driver) {
                        click(KPIs);
                        return true;
                    }
                });
    }

    public void clickJourneyDetails() {
        new WebDriverWait(driver, 5000)
                .ignoring(StaleElementReferenceException.class)
                .until(new Function<WebDriver, Boolean>() {
                    @Override
                    public Boolean apply(WebDriver driver) {
                        click(journeyDetails);
                        return true;
                    }
                });
    }

    public void verifyService(final String serviceName) {
        new WebDriverWait(driver, 5000)
                .ignoring(StaleElementReferenceException.class)
                .until(new Function<WebDriver, Boolean>() {
                    @Override
                    public Boolean apply(WebDriver driver) {
                        driver.findElement(By.xpath("//h3[contains(text(),'" + serviceName + "')]"));
                        return true;
                    }
                });
    }

    public void clickService(final String serviceName) {
        new WebDriverWait(driver, 5000)
                .ignoring(StaleElementReferenceException.class)
                .until(new Function<WebDriver, Boolean>() {
                    @Override
                    public Boolean apply(WebDriver driver) {
                        driver.findElement(By.xpath("//h3[contains(text(),'" + serviceName + "')]")).sendKeys(Keys.TAB);
                        driver.findElement(By.xpath("//h3[contains(text(),'" + serviceName + "')]")).click();
                        return true;
                    }
                });
    }

    public void clickMoveToCurrentDate() {
        WebElement home = driver.findElement(By.cssSelector("div.vco-navigation div.vco-toolbar div.fonticon.icon-calendar-month-highlight.current-date"));
        home.click();
    }

    /**
     * @param activeStates
     * @param completedStates
     * @param activeTasks
     * @param completedTasks
     */
    public void verifyKPIDetails(String activeStates, String completedStates, String activeTasks, String completedTasks) {
        String activeStatesValue = driver.findElement(By.xpath("//div/div[2]/div[2]/div[2]/div/div/div[2]/h3")).getText();
        assertEquals(activeStates, activeStatesValue);

        String completedStatesValue = driver.findElement(By.xpath("//div/div[2]/div[2]/div[2]/div/div/div[4]/h3")).getText();
        assertEquals(completedStates, completedStatesValue);

        String activeTasksValue = driver.findElement(By.xpath("//div/div[2]/div[2]/div[2]/div/div/div[6]/h3")).getText();
        assertEquals(activeTasks, activeTasksValue);

        String completedTasksValue = driver.findElement(By.xpath("//div/div[2]/div[2]/div[2]/div/div/div[8]/h3")).getText();
        assertEquals(completedTasks, completedTasksValue);
    }

    public void verifyKPIDetailsActiveState(String activeTasks, String completedTasks) {
        String activeTasksValue = driver.findElement(By.xpath("//div[2]/div[2]/div[2]/div/div/div[2]/div[4]/h3")).getText();
        assertEquals(activeTasks, activeTasksValue);

        String completedTasksValue = driver.findElement(By.xpath("//div[2]/div[2]/div[2]/div/div/div[2]/div[6]/h3")).getText();
        assertEquals(completedTasks, completedTasksValue);
    }

    public void verifyKPIDetailsCompletedState(String activeTasks, String completedTasks) {
        String activeTasksValue = driver.findElement(By.xpath("//div[2]/div[2]/div[2]/div/div/div[2]/div[5]/h3")).getText();
        assertEquals(activeTasks, activeTasksValue);

        String completedTasksValue = driver.findElement(By.xpath("//div[2]/div[2]/div[2]/div/div/div[2]/div[7]/h3")).getText();
        assertEquals(completedTasks, completedTasksValue);
    }

    public void inputToSearchBox(String filterName, String inputValue) {
        waitForLocator(listOfFiltersLocator);
        driver.findElement(listOfFiltersLocator).click();
        driver.findElement(By.linkText(filterName)).click();
        waitForLocator(searchBoxLocator);
        driver.findElement(searchBoxLocator).clear();
        driver.findElement(searchBoxLocator).sendKeys(inputValue, Keys.ENTER);
    }

    public void verifyCustomerNameDisplayed(String name, String lastName) {
        String selectorText = getText(selectionLocator);
        assertTrue(selectorText.startsWith("Customer Name : " + lastName + " " + name));
    }

    public void verifyAnonymousIDdisplayed(String contact_key) {
        String selectorText = getText(selectionLocator);
        assertTrue(selectorText.startsWith("Anonymous ID: " + contact_key));
    }

    public void verifyFilterAppliedLabel(String filter) {
        String filterApplied = driver.findElement(By.xpath("//div/div[2]/div[1]/div/div[1]/div[2]/label")).getText();
        assertTrue(filterApplied.startsWith("Filter Applied:"));

        String filterName = driver.findElement(By.xpath("//div/div[2]/div[1]/div/div[1]/div[2]/label/span")).getText();
        assertTrue(filterName.startsWith(filter));
    }

    public void inServiceTypeFilterSelect(String item) {
        WebElement filterForm = driver.findElement(filterFormLocator);
        WebElement dropdown = filterForm.findElement(serviceTypeFilterDropdown);
        waitFor(5000);
        dropdown.click();
        waitFor(5000);
        builder.moveToElement(dropdown)
                .moveToElement(
                        driver.findElement(By.linkText(item))).click().build()
                .perform();
        dropdown.click();
    }

    public void inServiceTypeFilterSelect(String item1, String item2) {
        WebElement filterForm = driver.findElement(filterFormLocator);
        WebElement dropdown = filterForm.findElement(serviceTypeFilterDropdown);
        waitFor(5000);
        dropdown.click();
        waitFor(5000);
        builder.moveToElement(dropdown)
                .moveToElement(
                        driver.findElement(By.linkText(item1))).click().moveToElement(driver.findElement(By.linkText(item2))).click().build()
                .perform();
        dropdown.click();
    }

    public void inFilterByNameSelect(String inputValue) {
        WebElement filterForm = driver.findElement(filterFormLocator);
        waitFor(5000);
        // waitForLocator(filterByNameTextField);

        filterForm.findElement(filterByNameTextField).clear();
        filterForm.findElement(filterByNameTextField).sendKeys(inputValue, Keys.ENTER);
    }

    public void inStateTypeFilterSelect(String item) {

        WebElement filterForm = driver.findElement(filterFormLocator);

        WebElement dropdown = filterForm.findElement(stateTypeFilterDropdown);
        waitFor(5000);
        dropdown.click();
        waitFor(5000);

        builder.moveToElement(dropdown)
                .moveToElement(
                        driver.findElement(By.linkText(item))).click().build()
                .perform();
        dropdown.click();
    }

    public void inStateTypeFilterSelect(String item1, String item2) {
        WebElement filterForm = driver.findElement(filterFormLocator);
        WebElement dropdown = filterForm.findElement(stateTypeFilterDropdown);
        waitFor(5000);
        dropdown.click();
        waitFor(5000);
        builder.moveToElement(dropdown)
                .moveToElement(
                        driver.findElement(By.linkText(item1))).click().moveToElement(driver.findElement(By.linkText(item2))).click().build()
                .perform();
        dropdown.click();
    }

    public void inStatusFilterSelect(String item) {
        //  clickFilterButton();
        WebElement filterForm = driver.findElement(filterFormLocator);
        WebElement dropdown = filterForm.findElement(statusFilterDropdown);
        waitFor(5000);
        dropdown.click();
        waitFor(5000);

        builder.moveToElement(dropdown)
                .moveToElement(
                        driver.findElement(By.linkText(item))).click().build()
                .perform();
        //clickApplyFilterButton();
    }

    public void inDateTimeFilterSelect(String item) {
        WebElement filterForm = driver.findElement(filterFormLocator);
        WebElement dropdown = filterForm.findElement(dateTimeFilterDropdown);
        waitFor(5000);
        dropdown.click();
        waitFor(5000);

        builder.moveToElement(dropdown)
                .moveToElement(
                        driver.findElement(By.linkText(item))).click().build()
                .perform();
    }

    public void inFilterBySelect(String item) {
        WebElement filterForm = driver.findElement(filterFormLocator);
        WebElement dropdown = filterForm.findElement(filterByDropdown);
        waitFor(5000);
        dropdown.click();
        waitFor(5000);

        builder.moveToElement(dropdown)
                .moveToElement(
                        driver.findElement(By.linkText(item))).click().build()
                .perform();
    }

    /**
     * selects dates yesterday and today
     */
    public void selectDateInDatePicker() {
        DateFormat dateFormat1 = new SimpleDateFormat("dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String yesterday = dateFormat1.format(cal.getTime());
        System.out.println("Yesterday's date = " + yesterday);

        driver.findElement(By.xpath("//div[3]/div/div/div/div[3]/div[1]/div[2]/span[2]/input[1]")).click();

        WebElement dateWidget = driver.findElement(By.xpath("//div[@class='day-table']/table/tbody"));
        List<WebElement> rows = dateWidget.findElements(By.tagName("tr"));
        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            for (WebElement cell : cells) {
                if (cell.getText().equals(yesterday)) {
                    cell.findElement(By.xpath("//span[contains(text(),'" + yesterday + "')]")).click();
                    break;
                }
            }
        }

        waitFor(5000);
        driver.findElement(By.xpath("//div[3]/div/div/div/div[3]/div[1]/div[2]/span[2]/input[2]")).click();

        DateFormat dateFormat2 = new SimpleDateFormat("dd");
        Date dateTo = new Date();
        String today = dateFormat2.format(dateTo);

        WebElement dateWidget2 = driver.findElement(By.xpath("//div[@class='day-table']/table/tbody"));
        rows = dateWidget2.findElements(By.tagName("tr"));
        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            for (WebElement cell : cells) {
                if (cell.getText().equals(today)) {
                    cell.findElement(By.xpath("//span[contains(text(),'" + today + "')]")).click();
                    break;
                }
            }
        }
        waitFor(5000);
    }

    public void inStatusFilterSelect(String item1, String item2) {
        // clickFilterButton();
        WebElement filterForm = driver.findElement(filterFormLocator);
        WebElement dropdown = filterForm.findElement(statusFilterDropdown);
        waitFor(5000);
        dropdown.click();
        waitFor(5000);
        builder.moveToElement(dropdown)
                .moveToElement(
                        driver.findElement(By.linkText(item1))).click().moveToElement(driver.findElement(By.linkText(item2))).click().build()
                .perform();
//			  new WebDriverWait(driver, 5000)
//			    .ignoring(StaleElementReferenceException.class)
//			    .until(new Predicate<WebDriver>() {
//			        @Override
//			        public boolean apply(WebDriver driver) {
//			        	  WebElement filterForm =driver.findElement(filterFormLocator);
//			    		  WebElement dropdown = filterForm.findElement(statusFilterDropdown);
//			    		    dropdown.click();		
//
//			    		 	builder.moveToElement(dropdown)
//			    			.moveToElement(
//			    					driver.findElement(By.linkText("Active"))).click().moveToElement(driver.findElement(By.linkText("Completed"))).click().build()
//			    			.perform(); 
//			            return true;
//			        }
//			    });
    }

    public void inStatusFilterSelect(String item1, String item2, String item3, String item4) {
        // clickFilterButton();
        WebElement filterForm = driver.findElement(filterFormLocator);
        WebElement dropdown = filterForm.findElement(statusFilterDropdown);
        waitFor(5000);
        dropdown.click();
        waitFor(5000);
        builder.moveToElement(dropdown)
                .moveToElement(
                        driver.findElement(By.linkText(item1))).click().moveToElement(driver.findElement(By.linkText(item2))).click()
                .moveToElement(driver.findElement(By.linkText(item3))).click().moveToElement(driver.findElement(By.linkText(item4))).click()
                .build()
                .perform();
    }

    public void inStatusFilterSelect(String item1, String item2, String item3, String item4, String item5) {
        //	  clickFilterButton();
        WebElement filterForm = driver.findElement(filterFormLocator);
        WebElement dropdown = filterForm.findElement(statusFilterDropdown);
        waitFor(5000);
        dropdown.click();
        waitFor(5000);
        builder.moveToElement(dropdown)
                .moveToElement(
                        driver.findElement(By.linkText(item1))).click().moveToElement(driver.findElement(By.linkText(item2))).click()
                .moveToElement(driver.findElement(By.linkText(item3))).click().moveToElement(driver.findElement(By.linkText(item4))).click()
                .moveToElement(driver.findElement(By.linkText(item5))).click()
                .build()
                .perform();
    }

    public void clickFilterButton() {
        click(filterButtonLocator);
    }

    public void clickApplyFilterButton() {
        click(applyfilterButtonLocator);
    }


    public boolean verifyApplyFilterButtonEnabled() {
        return driver.findElement(applyfilterButtonLocator).isEnabled();
    }

    public void verifyAlertIcon() {
        driver.findElement(alertIcon);
    }

    public void verifyAlertMessage() {
        String error = driver.findElement(alertMessage).getText();
        assertEquals("Error : Status filter cannot be blank", error);
    }

    public void verifyServiceDisplayed(String text) {
        driver.findElement(By.xpath("//div[@class=\"flag-content\"]/h3[contains(text(),'" + text + "')]")).isDisplayed();
    }

    public void verifyServiceNotDisplayed(String serviceName) {
        // verify not present
        try {
            Boolean isPresent = driver.findElement(
                    By.xpath("//div[@class=\"flag-content\"]/h3[contains(text(),'" + serviceName + "')]"))
                    .isDisplayed();
            assertTrue(!isPresent);
        } catch (NoSuchElementException e) {
            assertTrue(true);
        }
    }

    public void scroll(FirefoxDriver driver) {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollBy(0,300)", "");
    }

    public void clickOnThisImage(SikuliFirefoxDriver driver, String imageName) {
        try {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            image = driver.findImageElement(new URL(pathToFile + imageName + ".png"));
            image.click();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            assertTrue("Image was not found", false);
        }
    }

    @Override
    public boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
