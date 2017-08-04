package gms.ui;

import com.genesyslab.functional.tests.gms.ui.abstractpage.GMSAbstractPage;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GMSContextServicesPage extends GMSAbstractPage {
    private static final By usernameLocator = By.cssSelector("a.ng-scope.dropdown-toggle > span.ng-scope.ng-binding");
    private static final By logOutButtonLocator = By.linkText("Log out");

    private boolean acceptNextAlert = true;
    private By searchItemField = By.xpath("//input[@placeholder=\"Search Items\"]");

    private By searchBoxLocator = By.id("search-box");
    //	private By clearTextLocator = By
//			.xpath("//div/span[2][@ng-show=\"hasContent\"]");// "X"
    private By clearTextLocator = By
            .xpath("//div/span[2]");

    // private By listOfFiltersLocator =By.className("filter-option");
    private By listOfFiltersLocator = By
            .xpath("//span[@class=\"filter-option\"]");
    private By errorMessage = By.xpath("//form/span[@id=\"context-help\"]");
    private By clarification = By.xpath("//h3");// which of these customers are
    // you referring to?

    private By homeButtonLocator = By.linkText("Home");
    private By httpRequestHistoryLocator = By.xpath("(//li[@id=''])[3]");
    private By serviceIDlocator = By
            .xpath("//label[contains(text(),'Service ID:')]");
    // field for service_id in state/task filters
    private By contextQuery = By.id("context-query");
    // private By newButtonLocator =
    // By.xpath("//button[contains(text(),'New')]");
    private By newButtonLocator = By.xpath("(//button[@type='button'])[2]");// new
    // button
    private By completeButton = By.xpath("//div[contains(text(),'Complete')]");//complete button
    ///html/body/div/div[1]/div/div[6]/div/div[2]/div[1]/div/form/div[2]/button
    private By completeServiceButton = By.xpath("//button[contains(text(),'Complete Service')]");
    /**
     * locators from New State-${state_name} form
     */
    private By newStateFormLocator = By.xpath("//form[@id='State Type']");

    /**
     * locators from New Task-${task_name} form
     */
    private By newTaskFormLocator = By.xpath("//form[@id='Task Type']");
    /**
     * locators from New Service-${service_name} form
     */
    private By newServiceFormLocator = By.xpath("//form[@id='Service Type']");
    // new service from new button drop-down list
    private By newServiceLocator = By
            .xpath("//li[@class='dropdown-submenu'][contains(text(),'Service')]");
    private By newStateLocator = By
            .xpath("//li[@class='dropdown-submenu'][contains(text(),'State')]");
    private By newTaskLocator = By
            .xpath("//li[@class='dropdown-submenu'][contains(text(),'Task')]");
    private By cancelButtonLocator = By
            .xpath("//div[contains(text(),'Cancel')]");// By.cssSelector("div.btn.btn-default")
    private By session_idLabelLocator = By
            .xpath("//label[contains(text(),'session_id')]");
    private By interaction_idLabelLocator = By
            .xpath("//label[contains(text(),'interaction_id')]");
    private By application_typeLabelLocator = By
            .xpath("//label[contains(text(),'application_type')]");
    private By application_idLabelLocator = By
            .xpath("//label[contains(text(),'application_id')]");
    private By resource_typeLabelLocator = By
            .xpath("//label[contains(text(),'resource_type')]");
    private By resource_idLabelLocator = By
            .xpath("//label[contains(text(),'resource_id')]");
    private By media_typeLabelLocator = By
            .xpath("//label[contains(text(),'media_type')]");
    private By est_durationLabelLocator = By
            .xpath("//label[contains(text(),'est_duration')]");
    private By timestampLabelLocator = By
            .xpath("//label[contains(text(),'timestamp')]");

    private By sessionIdTextFiled = By.id("session_id");
    private By intercationIDTextField = By.id("interaction_id");
    private By appIdTextField = By.id("application_id");
    private By resIdTextField = By.id("resource_id");
    private By estDurationTextField = By.id("est_duration");

    private By httpRequestHistoryPanelHeaderLocator = By
            .cssSelector("div.panel.heading");
    private By httpRequestHistoryPanelExit = By
            .cssSelector("div.panel.heading > span.icon-close");
    private By selectionLocator = By.cssSelector("span.nodeLabel.ng-binding");

    public GMSContextServicesPage(WebDriver driver) {
        super(driver);
        if (!"GMS Development".equals(driver.getTitle())) {
            throw new IllegalStateException(
                    "This is not the ContextServices page");
        }
    }


    public boolean isAcceptNextAlert() {
        return acceptNextAlert;
    }


    public void setAcceptNextAlert(boolean acceptNextAlert) {
        this.acceptNextAlert = acceptNextAlert;
    }


    public By getSelectionLocator() {
        return selectionLocator;
    }

    public void setSelectionLocator(By selectionLocator) {
        this.selectionLocator = selectionLocator;
    }

    public By getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(By errorMessage) {
        this.errorMessage = errorMessage;
    }

    public By getClarification() {
        return clarification;
    }

    public void setClarification(By clarification) {
        this.clarification = clarification;
    }

    public By getHttpRequestHistoryPanelExit() {
        return httpRequestHistoryPanelExit;
    }

    public void setHttpRequestHistoryPanelExit(By httpRequestHistoryPanelExit) {
        this.httpRequestHistoryPanelExit = httpRequestHistoryPanelExit;
    }

    public By getSearchBoxLocator() {
        return searchBoxLocator;
    }

    public void setSearchBoxLocator(By searchBoxLocator) {
        this.searchBoxLocator = searchBoxLocator;
    }

    public By getClearTextLocator() {
        return clearTextLocator;
    }

    public void setClearTextLocator(By clearTextLocator) {
        this.clearTextLocator = clearTextLocator;
    }

    public By getListOfFiltersLocator() {
        return listOfFiltersLocator;
    }

    public void setListOfFiltersLocator(By listOfFiltersLocator) {
        this.listOfFiltersLocator = listOfFiltersLocator;
    }

    public By getHomeButtonLocator() {
        return homeButtonLocator;
    }

    public void setHomeButtonLocator(By homeButtonLocator) {
        this.homeButtonLocator = homeButtonLocator;
    }

    public By getHttpRequestHistoryLocator() {
        return httpRequestHistoryLocator;
    }

    public void setHttpRequestHistoryLocator(By httpRequestHistoryLocator) {
        this.httpRequestHistoryLocator = httpRequestHistoryLocator;
    }

    public By getHttpRequestHistoryPanelHeaderLocator() {
        return httpRequestHistoryPanelHeaderLocator;
    }

    public void setHttpRequestHistoryPanelHeaderLocator(
            By httpRequestHistotyPanelHeaderLocator) {
        this.httpRequestHistoryPanelHeaderLocator = httpRequestHistotyPanelHeaderLocator;
    }

    public void inputToSearchBox(String filterName, String inputValue) {
        waitForLocator(listOfFiltersLocator);
        driver.findElement(listOfFiltersLocator).click();
        driver.findElement(By.linkText(filterName)).click();
        waitForLocator(searchBoxLocator);
        driver.findElement(searchBoxLocator).clear();
        driver.findElement(searchBoxLocator).sendKeys(inputValue, Keys.ENTER);

    }

    public void clickLogOutButton() {
        waitForLocator(usernameLocator);
        driver.findElement(usernameLocator).click();
        driver.findElement(logOutButtonLocator).click();
        waitForLocator(By.xpath("//h2[text()='Welcome']"));

    }

    public void inputServiceID(String service_id) {
        waitForLocator(serviceIDlocator);

        driver.findElement(contextQuery).clear();
        driver.findElement(contextQuery).sendKeys(service_id, Keys.ENTER);

    }

    public void clickCompleteButton() {
        driver.findElement(completeButton).click();
    }

    public void clickCompleteService() {
        assertEquals("Properties", driver.findElement(By.cssSelector("h3")).getText());

        WebElement e = driver.findElement(By.xpath("//div/div[1]/div/div[6]/div/div[2]"));
        //e.findElement(completeServiceButton).click();
        e.findElement(By.xpath("//div[1]/div/form/div[2]/button")).click();
    }

    public void clickCompleteStateOrTask() {
        driver.findElement(By.xpath("//form/div[2]/button")).click();
    }

    public void clickOnNewServiceName(String serviceName) {
        waitForLocator(newButtonLocator);
        driver.findElement(newButtonLocator).click();

        WebElement newService = driver.findElement(newServiceLocator);

        builder.moveToElement(newService)
                .moveToElement(
                        driver.findElement(By.xpath("//li/a[contains(text(),'"
                                + serviceName + "')]"))).click().build()
                .perform();

    }

    public void clickOnNewStateName(String stateName) {
        waitForLocator(newButtonLocator);
        driver.findElement(newButtonLocator).click();

        WebElement newState = driver.findElement(newStateLocator);

        builder.moveToElement(newState)
                .moveToElement(
                        driver.findElement(By.xpath("//li/a[contains(text(),'"
                                + stateName + "')]"))).click().build()
                .perform();

    }

    public void clickOnNewTaskName(String taskName) {
        waitForLocator(newButtonLocator);
        driver.findElement(newButtonLocator).click();

        WebElement newTask = driver.findElement(newTaskLocator);

        builder.moveToElement(newTask)
                .moveToElement(
                        driver.findElement(By.xpath("//li/a[contains(text(),'"
                                + taskName + "')]"))).click().build()
                .perform();

    }

    public void pressStartButtonOnNewServiceForm() {
        driver.findElement(By.id("start-button")).click();
    }

    public void pressStartButtonOnNewStateForm() {
        waitForLocator(newStateFormLocator);
        WebElement element = driver.findElement(newStateFormLocator);
        element.findElement(By.id("start-button")).click();
    }

    public void pressStartButtonOnNewTaskForm() {
        waitForLocator(newTaskFormLocator);
        WebElement element = driver.findElement(newTaskFormLocator);
        element.findElement(By.id("start-button")).click();
    }

    public void pressCancelButton() {
        driver.findElement(cancelButtonLocator).click();
    }

    public void pressNewButton() {
        driver.findElement(newButtonLocator).click();
    }

    public void verifyAlertPresent(String alertMessage) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, 2);
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            // alert.accept();

            assertEquals(alertMessage, alert.getText());
            // alert.dismiss();

        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    public String closeAlertAndGetItsText() {
        try {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            if (acceptNextAlert) {
                alert.accept();
            } else {
                alert.dismiss();
            }
            return alertText;
        } finally {
            acceptNextAlert = true;
        }
    }

    public void waitForStateTable(String stateName) {
        for (int second = 0; ; second++) {
            if (second >= 60)
                fail("timeout");
            try {
//				if ("New State - MyState1".equals(driver.findElement(
//						By.cssSelector("h3.ng-binding")).getText()))
					
				if (driver.findElement(
							By.cssSelector("h3.ng-binding")).getText().startsWith("New State - "+ stateName))	
					break;
			} catch (Exception e) {
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void waitForTaskTable(String taskName){
		for (int second = 0;; second++) {
			if (second >= 60)
				fail("timeout");
			try {
					
				if (driver.findElement(
							By.cssSelector("h3.ng-binding")).getText().startsWith("New Task - "+ taskName))	
					break;
			} catch (Exception e) {
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {		
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	public void verifyElementColor(WebElement element, String expectedColor){
		String color = element.getCssValue("background-color");
		assertEquals(expectedColor, color);
	}
	
	public void verifyBAinfoTable(String application_type, String interaction_id, String media_type, String resource_type){
		assertTrue(driver.findElement(By
				.xpath("//table/tbody/tr[1]/th")).getText().equals("Application type (Started)"));	
		assertTrue(driver.findElement(By
				.xpath("//div[2]/table/tbody/tr[1]/td")).getText().equals(application_type));

        assertTrue(driver.findElement(By
                .xpath("//table/tbody/tr[2]/th")).getText().equals("Interaction id (Started)"));
        assertTrue(driver.findElement(By
                .xpath("//div[2]/table/tbody/tr[2]/td")).getText().equals(interaction_id));

        assertTrue(driver.findElement(By
                .xpath("//table/tbody/tr[3]/th")).getText().equals("Media type (Started)"));
        assertTrue(driver.findElement(By
                .xpath("//div[2]/table/tbody/tr[3]/td")).getText().equals(media_type));

        assertTrue(driver.findElement(By
                .xpath("//table/tbody/tr[4]/th")).getText().equals("Resource type (Started)"));
        assertTrue(driver.findElement(By
                .xpath("//div[2]/table/tbody/tr[4]/td")).getText().equals(resource_type));
        ///html/body/div/div[1]/div/div[6]/div/div[2]/div[2]/table/tbody/tr[6]/th
        assertTrue(driver.findElement(By
                .xpath("//table/tbody/tr[6]/th")).getText().equals("Application type (Completed)"));
        assertTrue(driver.findElement(By
                .xpath("//div[2]/table/tbody/tr[6]/td")).getText().equals(application_type));

        assertTrue(driver.findElement(By
                .xpath("//table/tbody/tr[7]/th")).getText().equals("Interaction id (Completed)"));
        assertTrue(driver.findElement(By
                .xpath("//div[2]/table/tbody/tr[7]/td")).getText().equals(interaction_id));

        assertTrue(driver.findElement(By
                .xpath("//table/tbody/tr[8]/th")).getText().equals("Media type (Completed)"));
        assertTrue(driver.findElement(By
                .xpath("//div[2]/table/tbody/tr[8]/td")).getText().equals(media_type));

        assertTrue(driver.findElement(By
                .xpath("//table/tbody/tr[9]/th")).getText().equals("Resource type (Completed)"));
        assertTrue(driver.findElement(By
                .xpath("//div[2]/table/tbody/tr[9]/td")).getText().equals(resource_type));
    }

    public void inputSearchItem(String searchValue) {
        driver.findElement(searchItemField).clear();
        driver.findElement(searchItemField).sendKeys(searchValue, Keys.ENTER);
    }

    /**
     * verify session_id label is present
     */
    public void verifySessionIdPresent() {
        assertEquals("session_id", driver.findElement(session_idLabelLocator)
                .getText());
    }

    public void verifyInteractionIdPresent() {
        assertEquals("interaction_id",
                driver.findElement(interaction_idLabelLocator).getText());
    }

    public void verifyApplicationTypePresent() {
        assertEquals("application_type",
                driver.findElement(application_typeLabelLocator).getText());
    }

    public void verifyApplicationIdPresent() {
        assertEquals("application_id",
                driver.findElement(application_idLabelLocator).getText());
    }

    public void verifyResourceTypePresent() {
        assertEquals("resource_type",
                driver.findElement(resource_typeLabelLocator).getText());
    }

    public void verifyResourceIdPresent() {
        assertEquals("resource_id", driver.findElement(resource_idLabelLocator)
                .getText());
    }

    public void verifyMediaTypePresent() {
        assertEquals("media_type", driver.findElement(media_typeLabelLocator)
                .getText());
    }

    public void verifyEstDurationPresent() {
        assertEquals("est_duration",
                driver.findElement(est_durationLabelLocator).getText());
    }

    public void verifyTimestampPresent() {
        assertEquals("timestamp", driver.findElement(timestampLabelLocator)
                .getText());
    }

    public void enterSessionId(String session_id) {
        driver.findElement(sessionIdTextFiled).clear();
        driver.findElement(sessionIdTextFiled).sendKeys(session_id);
    }

    public void enterInteractionId(String interaction_id) {
        driver.findElement(intercationIDTextField).clear();
        driver.findElement(intercationIDTextField).sendKeys(interaction_id);
    }

    public void enterAppId(String application_id) {
        driver.findElement(appIdTextField).clear();
        driver.findElement(appIdTextField).sendKeys(application_id);
    }

    public void enterResId(String resource_id) {
        driver.findElement(resIdTextField).clear();
        driver.findElement(resIdTextField).sendKeys(resource_id);
    }

    public void estDuration(String estDuration) {
        driver.findElement(estDurationTextField).clear();
        driver.findElement(estDurationTextField).sendKeys(estDuration);
    }

    public void enterAnonymousIDonNewServiceForm(String contact_key) {
        WebElement serviceForm = driver.findElement(newServiceFormLocator);
        WebElement dropdown = serviceForm.findElement(By.xpath("//span[contains(text(), \"Customer ID\")]"));
        dropdown.click();

        driver.findElement(By.xpath("//span[contains(text(), \"Anonymous Contact ID\")]"));


        driver.findElement(By.xpath("//form[@id='Service Type']/div/div/div/div/ul/li[2]/a/span")).click();
        driver.findElement(By.xpath("//form[@id='Service Type']/div/div[2]/input")).clear();
        driver.findElement(By.xpath("//form[@id='Service Type']/div/div[2]/input")).sendKeys(contact_key);
    }

    public void clearInputText() {
        click(clearTextLocator);
    }

    public void clickHomeButton() {
        click(homeButtonLocator);
    }

    private void click(By by) {
        driver.findElement(by).click();
    }

    public void clickHttpRequestHistoryButton() {
        click(httpRequestHistoryLocator);
    }

    public void clickHttpRequestHistoryPanel() {
        click(httpRequestHistoryPanelHeaderLocator);
    }

    public void clickSelection() {
        waitForLocator(selectionLocator);
        click(selectionLocator);
    }

    /**
     * Wait for error such as "Couldn't find any services.."
     *
     * @return
     */
    public String waitForError() {
        waitForLocator(errorMessage);
        waitFor(500);
        return driver.findElement(errorMessage).getText();
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

    /**
     * Exit from HTTP request history panel with X
     */
    public void clickExitFromHttpRequestHistoryPanel() {
        driver.findElement(httpRequestHistoryPanelExit).click();
    }

    public void clickServiceNode(int nodeNumber) {
        driver.findElement(By.xpath("//ul/li[" + nodeNumber + "]/span[6]/span"))
                .click();
    }

}
