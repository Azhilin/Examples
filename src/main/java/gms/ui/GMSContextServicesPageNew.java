package gms.ui;

import com.genesyslab.functional.tests.gms.ui.abstractpage.GMSAbstractPage;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * New Context Services UI since 8.5.103.XX
 *
 * @author igabduli
 */

public class GMSContextServicesPageNew extends GMSAbstractPage {

    private static final By usernameLocator = By.cssSelector("a.ng-scope.dropdown-toggle > span.ng-scope.ng-binding");

    private static final By logOutButtonLocator = By.linkText("Log out");

    private boolean acceptNextAlert = true;

    private By errorMessage = By.xpath("//form/span[@id=\"context-help\"]");

    // which of these customers are you referring to?
    private By clarification = By.xpath("//h3[contains(text(),'Which')]");


    private By listOfFiltersLocator = By
            .xpath("//span[@class=\"filter-option\"]");

    private By searchBoxLocator = By.id("search-box");


    private By clearTextLocator = By.className("search-box-cancel");

    private By homeButtonLocator = By.linkText("Home");

    private By httpRequestHistoryLocator = By.xpath("(//li[@id=''])[3]");

    private By httpRequestHistoryPanelExit = By.xpath("//div[@class='panel heading']/span[@class='icon-close']");
    ///html/body/div/div[1]/div/div[4]/div[3]
    //private By servicesBlockLocator =By.xpath("//div[@class=\"row\"]/div[3]");

    private By httpRequestHistoryLocator2 = By
            .xpath("//div[@class='history in']");
    private By servicesBlockLocator = By.xpath("//div/div[4]/div[3]");

    //private By statesBlockLocator =By.xpath("//div[@class=\"row\"]/div[4]");
    private By statesBlockLocator = By.xpath("//div/div[4]/div[4]");

    //private By tasksBlockLocator =By.xpath("//div[@class=\"row\"]/div[5]");
    private By tasksBlockLocator = By.xpath("//div/div[4]/div[5]");

    private By generalInformationTableLocator = By.xpath("//table[@class=\"table table-default ng-scope\"]");

    private By businessAttributesTableLocator = By.xpath("//table[@class=\"table table-default\"]");

    private By startButtonSelector = By.id("start-button");

    private By dropdownForSSTtypesLocator = By.xpath("//button[@class='btn btn-default btn-dropdown dropdown-toggle entitySelectionButton ng-pristine ng-valid ng-binding']");


    private By cancelButtonLocator = By
            .xpath("//div[contains(text(),'Cancel')]");// By.cssSelector("div.btn.btn-default")

    private By propertiesFormLocator = By.xpath("//div[@id='properties-form']");

    private By completeButton = By.xpath("//div[contains(text(),'Complete')]");//complete button

    private By completeSSTLocator = By.xpath("//div[5]/div/div/form/div[2]/button");

    private By newServiceFormLocator = By.xpath("//form[@id='Service Type']");


    private By searchItemField = By.xpath("//input[@placeholder=\"Search Items\"]");


    private By deleteServiceModalPop = By.cssSelector("div.ng-scope > p.ng-binding");

    private By confirmDelete = By.xpath("//div[@class='modal-footer']/button[contains(text(),'Confirm')]");

    private By cancelDelete = By.xpath("//div[@class='modal-footer']/button[contains(text(),'Cancel')]");

    private By startServiceModalPop = By.xpath("//div[@id='toast-container']/div[2]/div/h1/div");

    private By serviceIDlocator = By
            .xpath("//label[contains(text(),'Service ID:')]");
    // field for service_id in state/task filters
    private By contextQuery = By.id("context-query");

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


    public GMSContextServicesPageNew(WebDriver driver) {
        super(driver);
        if (!"GMS Development".equals(driver.getTitle())) {
            throw new IllegalStateException(
                    "This is not the ContextServices page");
        }
    }


    public boolean isAcceptNextAlert() {
        return acceptNextAlert;
    }


    public By getErrorMessage() {
        return errorMessage;
    }

    public By getClarification() {
        return clarification;
    }

    public By getHttpRequestHistoryPanelExit() {
        return httpRequestHistoryPanelExit;
    }

    public By getSearchBoxLocator() {
        return searchBoxLocator;
    }

    public By getClearTextLocator() {
        return clearTextLocator;
    }

    public By getListOfFiltersLocator() {
        return listOfFiltersLocator;
    }

    public By getHomeButtonLocator() {
        return homeButtonLocator;
    }

    public By getHttpRequestHistoryLocator() {
        return httpRequestHistoryLocator;
    }

    public By getHttpRequestHistoryPanelHeaderLocator() {
        return httpRequestHistoryPanelHeaderLocator;
    }

    public void setAcceptNextAlert(boolean acceptNextAlert) {
        this.acceptNextAlert = acceptNextAlert;
    }


    public void inputToSearchBox(String filterName, String inputValue) {
        waitForLocator(listOfFiltersLocator);
        driver.findElement(listOfFiltersLocator).click();
        driver.findElement(By.linkText(filterName)).click();
        waitForLocator(searchBoxLocator);
        driver.findElement(searchBoxLocator).clear();
        driver.findElement(searchBoxLocator).sendKeys(inputValue, Keys.ENTER);

    }

    public void inputToFilterField(String inputValue) {
        By filter = By.xpath("//div[4]/div[1]/div[1]/input");
        waitForLocator(filter);
        driver.findElement(filter).clear();
        driver.findElement(filter).sendKeys(inputValue, Keys.ENTER);
    }

    public void clickLogOutButton() {
        waitForLocator(usernameLocator);
        driver.findElement(usernameLocator).click();
        driver.findElement(logOutButtonLocator).click();
        waitForLocator(By.xpath("//h2[text()='Welcome']"));

    }

    public void logIn(String username, String password) {
        WebElement element = driver.findElement(By.id("username"));

        element.clear();
        element.sendKeys(username);

        element = driver.findElement(By.id("password"));

        element.clear();
        element.sendKeys(password);

        driver.findElement(By.className("login-btn")).click();
    }

    //since 8.5.104.00 GMS-3054
    public void verifyDeleteAlertText(String text) {
        String t = driver.findElement(deleteServiceModalPop).getText();
        System.out.println(t);
        assertEquals("Are you sure you want to delete \"" + text + "\"?", t);
    }

    public void clickConfirmDelete() {
        driver.findElement(confirmDelete).click();
    }

    public void clickCancelDelete() {
        driver.findElement(cancelDelete).click();
    }

    public void verifyAlertText(String text) {
        String t = driver.findElement(startServiceModalPop).getText();
        System.out.println(t);
        assertEquals(text, t);
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

    public void verifyNumberOfHeadersNoMoreThan(String columnNumber) {
        // verify not present
        try {
            Boolean isPresent = driver.findElement(
                    By.xpath("//thead/tr/th[" + columnNumber
                            + "]/strong"))
                    .isDisplayed();
            assertTrue(!isPresent);
        } catch (NoSuchElementException e) {
            assertTrue(true);
        }
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

    private void click(By by) {
        driver.findElement(by).click();
    }

    public void clickHttpRequestHistoryButton() {
        click(httpRequestHistoryLocator);
    }

    /**
     * Exit from HTTP request history panel with X
     */
    public void clickExitFromHttpRequestHistoryPanel() {
        driver.findElement(httpRequestHistoryPanelExit).click();
    }

    public void verifyHttpHistoryTableHeaders() {

        WebElement header = driver.findElement(By.xpath("//div[@class=\"ngHeaderContainer\"]"));

        assertEquals("Action",
                header.findElement(By.xpath("//div[2]/div/div/div[2]/div"))
                        .getText());
        assertEquals("Path", header
                .findElement(By.xpath("//div[2]/div[2]/div")).getText());
        assertEquals("Data", header
                .findElement(By.xpath("//div[3]/div[2]/div")).getText());
        assertEquals("Method",
                header.findElement(By.xpath("//div[4]/div[2]/div/div"))
                        .getText());
        assertEquals("Status",
                header.findElement(By.xpath("//div[5]/div[2]/div/div"))
                        .getText());
        assertEquals("Time",
                header.findElement(By.xpath("//div[6]/div[2]/div/div"))
                        .getText());

    }


    public void verifyServiceNotDisplayed(String serviceName) {
        // verify not present
        //retrieve names from element 1 and 2 and check they do not equal to service name

        WebElement servicesBlock = driver.findElement(servicesBlockLocator);
        List<WebElement> names = servicesBlock.findElements(By.xpath("//p"));

        boolean matchFound = false;
        for (WebElement element : names) {
            if (element.getText().equals(serviceName)) matchFound = true;
        }

        assertTrue(!matchFound);


    }


    public void verifyServiceDisplayed(String serviceName) {
        // verify not present
        //retrieve service names

        WebElement servicesBlock = driver.findElement(servicesBlockLocator);
        List<WebElement> names = servicesBlock.findElements(By.xpath("//p"));

        boolean matchFound = false;
        for (WebElement element : names) {
            if (element.getText().equals(serviceName)) matchFound = true;

        }

        if (matchFound == false) assertTrue(serviceName + " was not displayed!!!", false);

        assertTrue(matchFound);


    }

    public void clickService(String name) {
        WebElement servicesBlock = driver.findElement(servicesBlockLocator);
        servicesBlock.findElement(By.xpath("//p[contains(text(),'" + name + "')]")).click();
    }

    public void deleteService(String serviceName, int index) {
        WebElement servicesBlock = driver.findElement(servicesBlockLocator);
        servicesBlock.findElement(By.xpath("//p[" + index + "]/span[@class=\"icon-close\"]")).click();


    }

    public void clickAddService() {
        WebElement servicesBlock = driver.findElement(servicesBlockLocator);
        servicesBlock.findElement(By.xpath("//h3/span[@class=\"icon-add\"]")).click();


    }

    public void pressStartButton() {
        driver.findElement(startButtonSelector).click();
    }

    public void selectSSTypeDropdown(String item) {

        WebElement dropdown = driver.findElement(dropdownForSSTtypesLocator);

        dropdown.click();

        builder.moveToElement(dropdown)
                .moveToElement(
                        driver.findElement(By.linkText(item))).click().build()
                .perform();
        //dropdown.click();
    }

    public void pressCancelButton() {
        driver.findElement(cancelButtonLocator).click();
    }

    public void verifyStateDisplayed(String stateName) {
        WebElement statesBlock = driver.findElement(statesBlockLocator);
        List<WebElement> names = statesBlock.findElements(By.xpath("//p"));

        boolean matchFound = false;
        for (WebElement element : names) {
            if (element.getText().equals(stateName)) matchFound = true;
        }

        assertTrue(matchFound);

    }


    public void verifyStateNotDisplayed(String stateName) {
        WebElement statesBlock = driver.findElement(statesBlockLocator);
        List<WebElement> names = statesBlock.findElements(By.xpath("//p"));

        boolean matchFound = false;
        for (WebElement element : names) {
            if (element.getText().equals(stateName)) matchFound = true;
        }

        assertTrue(!matchFound);

    }


    public void clickState(String name) {
        WebElement statesBlock = driver.findElement(statesBlockLocator);
        statesBlock.findElement(By.xpath("//p[contains(text(),'" + name + "')]")).click();
    }

    public void clickAddState() {
        WebElement statesBlock = driver.findElement(statesBlockLocator);
        statesBlock.findElement(By.xpath("//div[4]/h3/span[@class=\"icon-add\"]")).click();


    }

    public void verifyTaskDisplayed(String taskName) {

        WebElement tasksBlock = driver.findElement(tasksBlockLocator);
        List<WebElement> names = tasksBlock.findElements(By.xpath("//p"));

        boolean matchFound = false;
        for (WebElement element : names) {
            if (element.getText().equals(taskName)) matchFound = true;
        }

        assertTrue(matchFound);

    }


    public void verifyTaskNotDisplayed(String taskName) {

        WebElement tasksBlock = driver.findElement(tasksBlockLocator);
        List<WebElement> names = tasksBlock.findElements(By.xpath("//p"));

        boolean matchFound = false;
        for (WebElement element : names) {
            if (element.getText().equals(taskName)) matchFound = true;
        }

        assertTrue(!matchFound);

    }


    public void clickTask(String name) {
        WebElement tasksBlock = driver.findElement(tasksBlockLocator);
        tasksBlock.findElement(By.xpath("//p[contains(text(),'" + name + "')]")).click();
    }

    public void clickAddTask() {
        WebElement statesBlock = driver.findElement(statesBlockLocator);
        statesBlock.findElement(By.xpath("//div[5]/h3/span[@class=\"icon-add\"]")).click();

    }

    public void clickCompleteButton() {
        driver.findElement(completeButton).click();
    }

    public void clickCompleteSST() {
        driver.findElement(completeSSTLocator).click();
    }

    public void inputServiceID(String service_id) {
        waitForLocator(serviceIDlocator);

        driver.findElement(contextQuery).clear();
        driver.findElement(contextQuery).sendKeys(service_id, Keys.ENTER);

    }


    public void verifyServiceGeneralInformationTable(boolean isAnonymous, String customer_id, String id, String type, boolean isCompleted) {
        // Grab the table
        WebElement generalInformationtable = driver.findElement(generalInformationTableLocator);

        // Now get all the TR elements from the table
        List<WebElement> allRows = generalInformationtable.findElements(By.tagName("tr"));

        if (isAnonymous) {
            assertTrue(allRows.get(0).findElement(By.tagName("th")).getText().equals("Anonymous ID"));
            assertEquals(customer_id, allRows.get(0).findElement(By.tagName("td")).getText());

        } else {
            assertTrue(allRows.get(0).findElement(By.tagName("th")).getText().equals("Customer ID"));
            assertEquals(customer_id, allRows.get(0).findElement(By.tagName("td")).getText());
        }

        assertTrue(allRows.get(1).findElement(By.tagName("th")).getText().equals("ID"));
        assertEquals(id, allRows.get(1).findElement(By.tagName("td")).getText());

        assertTrue(allRows.get(2).findElement(By.tagName("th")).getText().equals("Service Type"));
        assertEquals(type, allRows.get(2).findElement(By.tagName("td")).getText());

        assertTrue(allRows.get(4).findElement(By.tagName("th")).getText().equals("Completed"));


        if (isCompleted) {
            assertTrue(allRows.get(5).findElement(By.xpath("//td/span[@ng-if=\"tabData.completed\"]")).isDisplayed());
        } else {
            assertTrue(allRows.get(5).findElement(By.xpath("//td/div/div")).getText().equals("Complete"));
        }


    }


    public void verifyStateGeneralInformationTable(String id, String type, boolean isCompleted) {
        // Grab the table
        WebElement generalInformationtable = driver.findElement(generalInformationTableLocator);

        // Now get all the TR elements from the table
        List<WebElement> allRows = generalInformationtable.findElements(By.tagName("tr"));

        System.out.println(allRows.get(0).findElement(By.tagName("th")).getText());
        assertTrue(allRows.get(0).findElement(By.tagName("th")).getText().equals("ID"));
        assertEquals(id, allRows.get(0).findElement(By.tagName("td")).getText());

        assertTrue(allRows.get(1).findElement(By.tagName("th")).getText().equals("State Type"));
        assertEquals(type, allRows.get(1).findElement(By.tagName("td")).getText());

        assertTrue(allRows.get(3).findElement(By.tagName("th")).getText().equals("Completed"));


        if (isCompleted) {
            assertTrue(allRows.get(4).findElement(By.xpath("//td/span[@ng-if=\"tabData.completed\"]")).isDisplayed());
        } else {
            assertTrue(allRows.get(4).findElement(By.xpath("//td/div/div")).getText().equals("Complete"));
        }


    }

    public void verifyTaskGeneralInformationTable(String id, String type, boolean isCompleted) {
        // Grab the table
        WebElement generalInformationtable = driver.findElement(generalInformationTableLocator);

        // Now get all the TR elements from the table
        List<WebElement> allRows = generalInformationtable.findElements(By.tagName("tr"));

        System.out.println(allRows.get(0).findElement(By.tagName("th")).getText());
        assertTrue(allRows.get(0).findElement(By.tagName("th")).getText().equals("ID"));
        assertEquals(id, allRows.get(0).findElement(By.tagName("td")).getText());

        assertTrue(allRows.get(1).findElement(By.tagName("th")).getText().equals("Task Type"));
        assertEquals(type, allRows.get(1).findElement(By.tagName("td")).getText());

        assertTrue(allRows.get(3).findElement(By.tagName("th")).getText().equals("Completed"));


        if (isCompleted) {
            assertTrue(allRows.get(4).findElement(By.xpath("//td/span[@ng-if=\"tabData.completed\"]")).isDisplayed());
        } else {
            assertTrue(allRows.get(4).findElement(By.xpath("//td/div/div")).getText().equals("Complete"));
        }


    }

    public void verifyBusinessAttributesTable(String appType, String interactID, String medType, String resType, boolean isCompleted) {
        // Grab the table

        WebElement businessAttributes = driver.findElement(businessAttributesTableLocator);

        // Now get all the TR elements from the table
        List<WebElement> allRows = businessAttributes.findElements(By.tagName("tr"));


        assertTrue(allRows.get(0).findElement(By.tagName("th")).getText().equals("Application type (Started)"));
        assertEquals(appType, allRows.get(0).findElement(By.tagName("td")).getText());

        assertTrue(allRows.get(1).findElement(By.tagName("th")).getText().equals("Interaction id (Started)"));
        assertEquals(interactID, allRows.get(1).findElement(By.tagName("td")).getText());

        assertTrue(allRows.get(2).findElement(By.tagName("th")).getText().equals("Media type (Started)"));
        assertEquals(medType, allRows.get(2).findElement(By.tagName("td")).getText());

        assertTrue(allRows.get(3).findElement(By.tagName("th")).getText().equals("Resource type (Started)"));
        assertEquals(resType, allRows.get(3).findElement(By.tagName("td")).getText());

        if (isCompleted) {
            assertTrue(allRows.get(5).findElement(By.tagName("th")).getText().equals("Application type (Completed)"));
            assertEquals(appType, allRows.get(5).findElement(By.tagName("td")).getText());

            assertTrue(allRows.get(6).findElement(By.tagName("th")).getText().equals("Interaction id (Completed)"));
            assertEquals(interactID, allRows.get(6).findElement(By.tagName("td")).getText());

            assertTrue(allRows.get(7).findElement(By.tagName("th")).getText().equals("Media type (Completed)"));
            assertEquals(medType, allRows.get(7).findElement(By.tagName("td")).getText());

            assertTrue(allRows.get(8).findElement(By.tagName("th")).getText().equals("Resource type (Completed)"));
            assertEquals(resType, allRows.get(8).findElement(By.tagName("td")).getText());

        }


    }

    public void verifyActiveEventExtension(String extensionKey, String extensionValue) {

        WebElement extensionElKey = driver.findElement(By.xpath("//div[6]/div/table[2]/tbody/tr[9]/th"));
        System.out.println(extensionElKey.getText());
        assertTrue(extensionElKey.getText().equalsIgnoreCase(extensionKey + " (Extension)"));

        WebElement extensionElValue = driver.findElement(By.xpath("//div[6]/div/table[2]/tbody/tr[9]/td"));
        assertTrue(extensionElValue.getText().equalsIgnoreCase(extensionValue));
    }

    public void verifyCompletedEventExtension(String extensionKey, String extensionValue) {

        WebElement extensionElKey = driver.findElement(By.xpath("//div[6]/div/table[2]/tbody/tr[14]/th"));
        System.out.println(extensionElKey.getText());
        assertTrue(extensionElKey.getText().equalsIgnoreCase(extensionKey + " (Extension)"));

        WebElement extensionElValue = driver.findElement(By.xpath("//div[6]/div/table[2]/tbody/tr[14]/td"));
        assertTrue(extensionElValue.getText().equalsIgnoreCase(extensionValue));
    }

    ///html/body/div/div[1]/div/div[6]/div/table[2]/tbody/tr[14]/th

    /**
     * verification of HTTP request history column Action
     */
    public void verifyAction(String a, int rowNumber) {
        WebElement historyTable = driver.findElement(httpRequestHistoryLocator);
        WebElement action = historyTable.findElement(By
                .xpath("//div[" + rowNumber + "]/div[1]/div[2]/div/span"));
        assertEquals(a, action.getText());

    }

    /**
     * verification of HTTP request history column Path
     */
    public void verifyPath(String p, int rowNumber) {
        WebElement historyTable = driver.findElement(httpRequestHistoryLocator);
        WebElement path = historyTable.findElement(By
                .xpath("//div[" + rowNumber + "]/div[2]/div[2]/div/span"));
        assertEquals(p, path.getText());
    }

    /**
     * verification of HTTP request history column Data
     */
    public void verifyDataNotNull(int rowNumber) {
        WebElement historyTable = driver.findElement(httpRequestHistoryLocator);
        WebElement data = historyTable.findElement(By
                .xpath("//div[" + rowNumber + "]/div[3]/div[2]/div/span"));


        assertTrue(!data.getTagName().equals("") && (data.getText() != null));
    }


    /**
     * verification of HTTP request history column Method
     */
    public void verifyMethod(String m, int rowNumber) {
        WebElement historyTable = driver.findElement(httpRequestHistoryLocator);
        WebElement method = historyTable.findElement(By
                .xpath("//div[" + rowNumber + "]/div[4]/div[2]/div/span"));
        assertEquals(m, method.getText());

    }

    /**
     * verification of HTTP request history column Status
     */
    public void verifyStatus(String s, int rowNumber) {
        WebElement historyTable = driver.findElement(httpRequestHistoryLocator);
        WebElement status = historyTable.findElement(By
                .xpath("//div[" + rowNumber + "]/div[5]/div[2]/div/span"));
        assertEquals(s, status.getText());

    }

    /**
     * verification of HTTP request history column Time
     */
    public void verifyTimeNotNull(int rowNumber) {
        WebElement historyTable = driver.findElement(httpRequestHistoryLocator);
        WebElement time = historyTable.findElement(By
                .xpath("//div[" + rowNumber + "]/div[6]/div[2]/div/span"));
        //	assertNotNull("Time is not displayed", time.getText());
        assertTrue(!time.getTagName().equals("") && (time.getText() != null));

    }

    /**
     * verification of HTTP request history column Action
     */
    public void verifyAction2(String a, int rowNumber) {
        WebElement historyTable = driver.findElement(httpRequestHistoryLocator2);
        WebElement action = historyTable.findElement(By
                .xpath("//div[" + rowNumber + "]/div[1]/div[2]/div/span"));
        assertEquals(a, action.getText());

    }

    /**
     * verification of HTTP request history column Path
     */
    public void verifyPath2(String p, int rowNumber) {
        WebElement historyTable = driver.findElement(httpRequestHistoryLocator2);
        WebElement path = historyTable.findElement(By
                .xpath("//div[" + rowNumber + "]/div[2]/div[2]/div/span"));
        assertEquals(p, path.getText());
    }

    /**
     * verification of HTTP request history column Data
     */
    public void verifyDataNotNull2(int rowNumber) {
        WebElement historyTable = driver.findElement(httpRequestHistoryLocator2);
        WebElement data = historyTable.findElement(By
                .xpath("//div[" + rowNumber + "]/div[3]/div[2]/div/span"));
        //	assertNotNull("Request data is not displayed", data.getText());
        assertTrue(!data.getTagName().equals("") && (data.getText() != null));

    }

    /**
     * verification of HTTP request history column Data
     */
    public void verifyDataNull2(int rowNumber) {
        WebElement historyTable = driver.findElement(httpRequestHistoryLocator2);
        WebElement data = historyTable.findElement(By
                .xpath("//div[" + rowNumber + "]/div[3]/div[2]/div/span"));
        System.out.println("******" + data.getText() + "*****");

        assertTrue(data.getTagName().equals("") || (data.getText() == null));


    }

    /**
     * verification of HTTP request history column Method
     */
    public void verifyMethod2(String m, int rowNumber) {
        WebElement historyTable = driver.findElement(httpRequestHistoryLocator2);
        WebElement method = historyTable.findElement(By
                .xpath("//div[" + rowNumber + "]/div[4]/div[2]/div/span"));
        assertEquals(m, method.getText());

    }

    /**
     * verification of HTTP request history column Status
     */
    public void verifyStatus2(String s, int rowNumber) {
        WebElement historyTable = driver.findElement(httpRequestHistoryLocator2);
        WebElement status = historyTable.findElement(By
                .xpath("//div[" + rowNumber + "]/div[5]/div[2]/div/span"));
        assertEquals(s, status.getText());

    }

    /**
     * verification of HTTP request history column Time
     */
    public void verifyTimeNotNull2(int rowNumber) {
        WebElement historyTable = driver.findElement(httpRequestHistoryLocator2);
        WebElement time = historyTable.findElement(By
                .xpath("//div[" + rowNumber + "]/div[6]/div[2]/div/span"));
        //	assertNotNull("Time is not displayed", time.getText());
        assertTrue(!time.getTagName().equals("") && (time.getText() != null));

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

    //todo????
//		
//		public void verifyCustomerID(String id){
//			
//			WebElement serviceForm = driver.findElement(newServiceFormLocator);
//
//			WebElement el = serviceForm.findElement(By.xpath("//form[@id='Service Type']/div/div[2]/input"));
//			el.click();
//			
//			el.sendKeys(Keys.chord(Keys.CONTROL,"a"));
//			el.sendKeys(Keys.chord(Keys.CONTROL,"c"));
//		
//			
//			 
//			 WebElement filter = driver.findElement(By.xpath("//div[4]/div[1]/div[1]/input"));
//			 filter.sendKeys(Keys.chord(Keys.CONTROL,"v"));
//			 filter.sendKeys(Keys.ENTER);
//			 el.click();
//			 waitFor(5000);
//			 String customer_id = filter.getText();
//	          
//			assertEquals(id, customer_id);
//		}

    /**
     * in dropdown select Customer ID or Amonymous ID
     */
    public void selectTypeOfID(boolean isAnonymous) {

        WebElement dropdown = driver.findElement(By.xpath("//createitem/form/div[1]/div[1]/div/button"));
        dropdown.click();
        WebElement idType;
        if (isAnonymous) {
            idType = driver.findElement(By.xpath("//a[@id='-select-default-id-Anonymous Contact ID']/span"));
        } else {
            idType = driver.findElement(By.xpath("//a[@id='-select-default-id-Customer ID']/span"));
        }

        idType.click();

    }

    public void clickCustomerID() {

        WebElement el = driver.findElement(By.xpath("//form[@id='Service Type']/div/div[2]/input"));
        el.click();

    }

    public void clearCustomerOrAnonymousID() {
        WebElement id = driver.findElement(By.xpath("//form[@id='Service Type']/div/div[2]/input"));
        id.clear();
    }

    public void enterIDType(String idType, String id) {

        WebElement IDType = driver.findElement(By.xpath("//span[contains(text(),'Customer ID')]"));
        builder.moveToElement(IDType).click();
        builder.moveToElement(driver.findElement(By.xpath("//span[contains(text(),'" + idType + "')]"))).click().build().perform();

        driver.findElement(By.xpath("//form[@id='Service Type']/div/div[2]/input")).sendKeys(id, Keys.ENTER);

    }

    public void enterSessionId(String session_id) {
        driver.findElement(sessionIdTextFiled).clear();
        driver.findElement(sessionIdTextFiled).sendKeys(session_id);
    }

    public void enterInteractionId(String interaction_id) {
        driver.findElement(intercationIDTextField).clear();
        driver.findElement(intercationIDTextField).sendKeys(interaction_id);
    }

    public void enterAppType(String item) {
        WebElement propertiesForm = driver.findElement(propertiesFormLocator);
        WebElement dropdown = propertiesForm.findElement(By.xpath("//div[3]/div[2]/div/button"));

        dropdown.click();

        builder.moveToElement(dropdown)
                .moveToElement(
                        driver.findElement(By.linkText(item))).click().build()
                .perform();
        //dropdown.click();

    }

    public void enterAppId(String application_id) {
        driver.findElement(appIdTextField).clear();
        driver.findElement(appIdTextField).sendKeys(application_id);
    }

    public void enterResId(String resource_id) {
        driver.findElement(resIdTextField).clear();
        driver.findElement(resIdTextField).sendKeys(resource_id);
    }

    public void enterResType(String item) {
        WebElement propertiesForm = driver.findElement(propertiesFormLocator);
        WebElement dropdown = propertiesForm.findElement(By.xpath("//div[5]/div[2]/div/button"));

        dropdown.click();

        builder.moveToElement(dropdown)
                .moveToElement(
                        driver.findElement(By.linkText(item))).click().build()
                .perform();
        //dropdown.click();

    }

    public void enterMediaType(String item) {
        WebElement propertiesForm = driver.findElement(propertiesFormLocator);
        WebElement dropdown = propertiesForm.findElement(By.xpath("//div[7]/div[2]/div/button"));

        dropdown.click();

        builder.moveToElement(dropdown)
                .moveToElement(
                        driver.findElement(By.linkText(item))).click().build()
                .perform();
        //dropdown.click();

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

    public void enterCustomerIDonNewServiceForm(String customer_id) {
        driver.findElement(By.xpath("//form[@id='Service Type']/div/div[2]/input")).clear();
        driver.findElement(By.xpath("//form[@id='Service Type']/div/div[2]/input")).sendKeys(customer_id);
    }

    public void chooseAmongCustomerOrAnonymousID(boolean isAnonymous, String id) {
        WebElement serviceForm = driver.findElement(newServiceFormLocator);
        //form[@id='Service Type']/div[1]/div[1]/div/button
        WebElement dropdown = serviceForm.findElement(By.xpath("//form[@id='Service Type']/div[1]/div[1]/div/button"));
        dropdown.click();
        if (isAnonymous) {

            builder.moveToElement(dropdown)
                    .moveToElement(
                            driver.findElement(By.xpath("//span[contains(text(), \"Anonymous Contact ID\")]"))).click().build()
                    .perform();
        } else {
            builder.moveToElement(dropdown)
                    .moveToElement(
                            driver.findElement(By.xpath("//span[contains(text(), \"Customer ID\")]"))).click().build()
                    .perform();
        }
        driver.findElement(By.xpath("//form[@id='Service Type']/div/div[2]/input")).clear();
        driver.findElement(By.xpath("//form[@id='Service Type']/div/div[2]/input")).sendKeys(id);
    }

    public void enterExtensionsOnServiceStart(String key, String value) {
        WebElement serviceForm = driver.findElement(newServiceFormLocator);
        WebElement add = serviceForm.findElement(By.xpath("//propertylist/div[3]/span"));
        add.click();

        WebElement extensionKey = driver.findElement(By.xpath("//createitem/form/propertylist/div[4]/div/div[1]/input"));

        extensionKey.sendKeys(key, Keys.ENTER);

        WebElement extensionValue = serviceForm.findElement(By.xpath("//div[4]/div/div[2]/input"));
        extensionValue.sendKeys(value, Keys.ENTER);
    }

    public void enterExtensionsOnStateStart(String key, String value) {

        WebElement add = driver.findElement(By.xpath("//createitem/form/propertylist/div[3]/span"));
        add.click();

        WebElement extensionKey = driver.findElement(By.xpath("//createitem/form/propertylist/div[4]/div/div[1]/input"));

        extensionKey.sendKeys(key, Keys.ENTER);

        WebElement extensionValue = driver.findElement(By.xpath("//div[4]/div/div[2]/input"));
        extensionValue.sendKeys(value, Keys.ENTER);
    }

    public void enterExtensionsOnTaskStart(String key, String value) {

        WebElement add = driver.findElement(By.xpath("//createitem/form/propertylist/div[3]/span"));
        add.click();

        WebElement extensionKey = driver.findElement(By.xpath("//createitem/form/propertylist/div[4]/div/div[1]/input"));

        extensionKey.sendKeys(key, Keys.ENTER);

        WebElement extensionValue = driver.findElement(By.xpath("//div[4]/div/div[2]/input"));
        extensionValue.sendKeys(value, Keys.ENTER);
    }

    public void enterExtensionsOnCompleteEvent(String key, String value) {
        //WebElement serviceForm = driver.findElement(newServiceFormLocator);
        WebElement add = driver.findElement(By.xpath("//propertylist/div[3]/span"));
        add.click();
///                             html/body/div/div[1]/div/div[5]/div/div/form/div[1]/propertylist/div[4]/div/div[1]/input
        WebElement extensionKey = driver.findElement(By.xpath("//form/div[1]/propertylist/div[4]/div/div[1]/input"));

        extensionKey.sendKeys(key, Keys.ENTER);
        ///html/body/div/div[1]/div/div[5]/div/div/form/div[1]/propertylist/div[4]/div/div[2]/input
        WebElement extensionValue = driver.findElement(By.xpath("//div[4]/div/div[2]/input"));
        extensionValue.sendKeys(value, Keys.ENTER);
    }

}
