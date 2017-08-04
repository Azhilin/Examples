package gms.ui;

import com.genesyslab.functional.tests.gms.ui.abstractpage.GMSAbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

public class GMSPatternsPage extends GMSAbstractPage {

	private By navAccount = By.id("nav_account");
	private By logOut = By.xpath(".//*[@id='nav_logout']/a");

	// Left side window box
	private By patternGroupsLocator = By
			.xpath(".//*[@id='serviceTemplatesContainer']//label[contains(text(), 'Pattern Groups')]");
	private By addPatternGroup = By.xpath(".//*[@id='sidebarContainer']//span[contains(text(), 'Add')]");
	private By deletePatternGroup = By.xpath(".//*[@id='sidebarContainer']//span[contains(text(), 'Delete')]");

	// Right side window box
	private By regexBox = By.id("regex-box");
	private By helpButton = By.xpath(".//span[text()='Help']");
	private By addNewPatternButton = By.xpath(".//span[text()='Add New']");
	private By deletePatternButton = By.xpath(".//span[@ng-click='delete();']/span[text()='Delete']");
	private By refreshPatternButton = By.xpath(".//span[text()='Refresh']");
	private By addNewPatternNameFieldInput = By
			.xpath(".//tr[contains(@class, 'table-highlight')]//span[@ng-show='edit.editID']/form/input");
	private By addNewPatternValueFieldInput = By
			.xpath(".//tr[contains(@class, 'table-highlight')]//span[@ng-show='edit.editValue']/form/input");

	// Help dialog window
	private By helpWindowHeader = By.xpath(".//strong[text()='A regular expression']");
	private By helpWindowJavaRegularExpressionLink = By
			.xpath(".//a[@href='http://docs.oracle.com/javase/tutorial/essential/regex/'"
					+ " and text()='Java Regular Expression Lesson by Oracle']");
	private By helpCloseDialogWindowButton = By.xpath(".//span[@ng-click='cancel()']");

	// Add new pattern group dialog window
	private By addNewPatternGroupWindowHeader1 = By.xpath(".//h1[contains(text(), 'Add New Pattern Group')]");
	private By addNewPatternGroupWindowHeader2 = By.xpath(".//h2[contains(text(), 'New Pattern Group Name')]");
	private By addNewPatternGroupInputField = By.xpath(".//div/form[@ng-submit='add()']/input");
	private By addNewPatternGroupAddButton = By.xpath(".//button[contains(text(), 'Add')]");
	private By addNewPatternGroupCancelButton = By.xpath(".//button[contains(text(), 'Cancel')]");
	private By addNewPatternGroupCloseDialogWindow = By.xpath(".//span[contains(@ng-click, 'cancel()')]");
	private By addNewPatternGroupAlertMessageSpecChar = By
			.xpath(".//h3[contains(text(), 'You must not include any illegal characters')]");
	private By addNewPatternGroupAlertMessageDuplicate = By
			.xpath(".//h3[contains(text(), 'Pattern group name must be unique')]");

	// Delete pattern group dialog window
	private By deletePatternGroupWindowHeader1 = By.xpath(".//h1[contains(text(), 'Delete Resource Group')]");
	private By deletePatternGroupWindowHeader2 = By.xpath(".//p[contains(text(),'Delete Resource Group:')]");
	private By deletePatternGroupConfirmButton = By.xpath(".//button[contains(text(),'Confirm')]");
	private By deletePatternGroupCancelButton = By.xpath(".//button[contains(text(),'Cancel')]");
	private By deletePatternGroupCloseDialogWindow = By.xpath(".//span[contains(@ng-click, 'cancel()')]");

	// Delete pattern dialog window
	private By deletePatternWindowHeader1 = By.xpath(".//h1[contains(text(), 'Delete Pattern')]");
	private By deletePatternWindowHeader2 = By.xpath(".//p[contains(text(),'Are you sure you want to remove')]");
	private By deletePatternConfirmButton = By.xpath(".//button[contains(text(),'Confirm')]");
	private By deletePatternCancelButton = By.xpath(".//button[contains(text(),'Cancel')]");
	private By deletePatternCloseDialogWindow = By.xpath(".//span[contains(@ng-click, 'cancel()')]");

	// Alert and Confirmation messages
	private By duplicateAlertMessage = By.xpath(".//h1/div[text()='This key already exists']");
	private By deletePatternConfirmationMessage = By.xpath(".//h1/div[text()='Delete Successful']");
	private By addPatternOrValueConfirmationMessage = By.xpath(".//h1/div[text()='Success']");
	private By refreshConfirmationMessage = By.xpath(".//h1/div[text()='Refreshed']");
	private By addPatternGroupConfirmationMessage = By.xpath(".//h1/div[text()='Created pattern group']");
	private By deletePatternGroupConfirmationMessage = By.xpath(".//h1/div[text()='Success Deleting']");

	public GMSPatternsPage(WebDriver driver) {
		super(driver);
		if (!"GMS Management UI".equals(driver.getTitle())) {
			throw new IllegalStateException("Page title is incorrect!!!");
		}
	}

	private By locatePatternGroupByName(String patternGroupName) {
		return By.xpath(".//*[@id='serviceTemplatesContainer']//span[contains(text(), '" + patternGroupName + "')]");
	}

	private By locatePatternByNameValuePair(String patternName, String patternValue) {
		return By.xpath(".//span[@ng-show='!edit.editID' and text()='" + patternName
				+ "']/../..//span[@ng-show='!edit.editValue' and contains(text(), '" + patternValue + "')]");
	}

	private By locateCheckBoxByPatternName(String patternName) {
		return By.xpath(".//span[@ng-show='!edit.editID' and text()='" + patternName
				+ "']/../..//td[contains(@ng-click,'setCheckboxSettings();')]/span");
	}
	
	private By locatePatternValueByPatternName(String patternName) {
		return By.xpath(".//span[@ng-show='!edit.editID' and text()='" + patternName
				+ "']/../..//span[@ng-show='!edit.editValue']");
	}
	
	private By locatePatternValueSubmitButtonByPatternName(String patternName) {
		return By.xpath(".//span[@ng-show='!edit.editID' and text()='" + patternName
				+ "']/../..//span[@ng-show='edit.editValue']/form/span/button[@type='submit']");
	}
	
	private By locatePatternValueCancelButtonByPatternName(String patternName) {
		return By.xpath(".//span[@ng-show='!edit.editID' and text()='" + patternName
				+ "']/../..//span[@ng-show='edit.editValue']/form/span/button[@type='button']");
	}

	private By locateMatchedHighLightedPatternByName(String patternName) {
		return By.xpath(".//tr[@class='ng-scope table-highlight'"
				+ " or @class='ng-scope table-highlight table-highlight-checkbox'"
				+ " or @class='ng-scope table-highlight-checkbox table-highlight'"
				+ " or @class='ng-scope zebra-strips table-highlight table-highlight-checkbox'"
				+ " or @class='ng-scope zebra-strips table-highlight'"
				+ " or @class='ng-scope zebra-strips table-highlight-checkbox table-highlight']"
				+ "//span[@ng-show='!edit.editID' and text()='" + patternName + "']");
	}

	private By locateCheckedHighLightedPatternByName(String patternName) {
		return By.xpath(".//tr[@class='ng-scope table-highlight-checkbox'"
				+ " or @class='ng-scope table-highlight table-highlight-checkbox'"
				+ " or @class='ng-scope table-highlight-checkbox table-highlight'"
				+ " or @class='ng-scope zebra-strips table-highlight table-highlight-checkbox'"
				+ " or @class='ng-scope zebra-strips table-highlight-ckeckbox'"
				+ " or @class='ng-scope zebra-strips table-highlight-checkbox table-highlight']"
				+ "//span[@ng-show='!edit.editID' and text()='" + patternName + "']");
	}

	public GMSLoginPage clickLogOut(WebDriver driver) {
		waitFor(1000);
		waitForElement(navAccount, 10, 1000);
		driver.findElement(navAccount).click();
		waitFor(1000);
		waitForElement(navAccount, 10, 1000);
		driver.findElement(logOut).click();
		waitForElement(By.xpath(".//button[contains(text(), 'Log In')]"), 10, 1000);
		return new GMSLoginPage(driver);
	}

	public boolean isPatternGroupExist(String patternGroupName) {
		try {
			waitForElement(locatePatternGroupByName(patternGroupName), 5, 1000);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isPatternGroupNotExist(String patternGroupName) {
		try {
			waitForElement(locatePatternGroupByName(patternGroupName), 5, 1000);
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	public boolean isPatternMatchedInGroup(String groupName, String patternName, String patternValue) {
		try {
			waitForElement(locatePatternGroupByName(groupName), 5, 1000);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isAddPatternGroupConfimationMessageExist() {
		try {
			waitForElement(addPatternGroupConfirmationMessage, 10, 1000);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isDeletePatternGroupConfimationMessageExist() {
		try {
			waitForElement(deletePatternGroupConfirmationMessage, 10, 1000);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isDuplicateAlertMessageExist() {
		try {
			waitForElement(By.xpath(".//h1/div[text()='This key already exists']"), 10, 1000);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean addNewPatternGroup(String patternGroupName) {
		try {
			waitForElement(addPatternGroup, 10, 1000);
			driver.findElement(addPatternGroup).click();

			waitForElement(addNewPatternGroupWindowHeader1, 10, 1000);
			waitForElement(addNewPatternGroupWindowHeader2, 10, 1000);
			waitForElement(addNewPatternGroupInputField, 10, 1000);
			waitForElement(addNewPatternGroupAddButton, 10, 1000);
			waitForElement(addNewPatternGroupCancelButton, 10, 1000);
			waitForElement(addNewPatternGroupCloseDialogWindow, 10, 1000);

			driver.findElement(addNewPatternGroupInputField).sendKeys(patternGroupName);
			driver.findElement(addNewPatternGroupAddButton).click();

			waitForElement(addPatternGroupConfirmationMessage, 10, 1000);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean addNewPatternGroupCancel(String patternGroupName) {
		try {
			waitForElement(addPatternGroup, 10, 1000);
			driver.findElement(addPatternGroup).click();

			waitForElement(addNewPatternGroupWindowHeader1, 10, 1000);
			waitForElement(addNewPatternGroupWindowHeader2, 10, 1000);
			waitForElement(addNewPatternGroupInputField, 10, 1000);
			waitForElement(addNewPatternGroupAddButton, 10, 1000);
			waitForElement(addNewPatternGroupCancelButton, 10, 1000);
			waitForElement(addNewPatternGroupCloseDialogWindow, 10, 1000);

			driver.findElement(addNewPatternGroupInputField).sendKeys(patternGroupName);
			driver.findElement(addNewPatternGroupCancelButton).click();

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean deletePatternGroup(String patternGroupName) {
		try {
			waitForElement(locatePatternGroupByName(patternGroupName), 60, 500);
			driver.findElement(locatePatternGroupByName(patternGroupName)).click();

			waitForElement(deletePatternGroup, 20, 1000);
			driver.findElement(deletePatternGroup).click();

			waitFor(1000);

			waitForElement(deletePatternGroupWindowHeader1, 20, 1000);
			waitForElement(deletePatternGroupWindowHeader2, 20, 1000);
			waitForElement(deletePatternGroupCancelButton, 20, 1000);
			waitForElement(deletePatternGroupCloseDialogWindow, 20, 1000);
			waitForElement(deletePatternGroupConfirmButton, 20, 1000);

			driver.findElement(deletePatternGroupConfirmButton).click();

			waitForElement(deletePatternGroupConfirmationMessage, 20, 1000);

			waitFor(1000);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean deletePatternGroupCancel(String patternGroupName) {
		try {
			waitForElement(locatePatternGroupByName(patternGroupName), 30, 1000);
			driver.findElement(locatePatternGroupByName(patternGroupName)).click();

			waitForElement(deletePatternGroup, 30, 1000);
			driver.findElement(deletePatternGroup).click();

			waitFor(1000);

			waitForElement(deletePatternGroupCancelButton, 30, 1000);
			driver.findElement(deletePatternGroupCancelButton).click();

			waitFor(1000);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean deletePatternGroupClose(String patternGroupName) {
		try {
			waitForElement(locatePatternGroupByName(patternGroupName), 30, 1000);
			driver.findElement(locatePatternGroupByName(patternGroupName)).click();

			waitForElement(deletePatternGroup, 30, 1000);
			driver.findElement(deletePatternGroup).click();

			waitFor(1000);

			waitForElement(deletePatternGroupCloseDialogWindow, 30, 1000);
			driver.findElement(deletePatternGroupCloseDialogWindow).click();

			waitFor(1000);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean deletePattern() {
		try {
			waitForElement(deletePatternButton, 10, 1000);
			driver.findElement(deletePatternButton).click();

			waitFor(1000);

			waitForElement(deletePatternWindowHeader1, 10, 1000);
			waitForElement(deletePatternWindowHeader2, 10, 1000);
			waitForElement(deletePatternCancelButton, 10, 1000);
			waitForElement(deletePatternCloseDialogWindow, 10, 1000);
			waitForElement(deletePatternConfirmButton, 10, 1000);
			
			driver.findElement(deletePatternConfirmButton).click();

			waitForElement(deletePatternConfirmationMessage, 10, 1000);

			waitFor(2000);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean addNewPatternInGroup(String groupName, String patternName, String patternValue) {
		try {
			waitForElement(locatePatternGroupByName(groupName), 10, 1000);
			driver.findElement(locatePatternGroupByName(groupName)).click();

			waitForElement(addNewPatternButton, 10, 1000);
			driver.findElement(addNewPatternButton).click();

			waitFor(1000);

			waitForElement(addNewPatternNameFieldInput, 10, 1000);
			driver.findElement(addNewPatternNameFieldInput).click();
			driver.findElement(addNewPatternNameFieldInput).sendKeys(patternName);
			driver.findElement(addNewPatternNameFieldInput).sendKeys(Keys.ENTER);

			waitForElement(addPatternOrValueConfirmationMessage, 10, 1000);

			waitFor(1000);

			waitForElement(addNewPatternValueFieldInput, 10, 1000);
			driver.findElement(addNewPatternValueFieldInput).click();
			driver.findElement(addNewPatternValueFieldInput).sendKeys(patternValue);
			driver.findElement(addNewPatternValueFieldInput).sendKeys(Keys.ENTER);

			waitForElement(addPatternOrValueConfirmationMessage, 10, 1000);

			waitFor(1000);
			
			if (isPatternCheckedByName(patternName))
				clickPatternCheckboxByName(patternName);

			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean resavePatternValueByPattenNameInGroup(String groupName, String patternName) {
		try {
			waitForElement(locatePatternGroupByName(groupName), 10, 1000);
			driver.findElement(locatePatternGroupByName(groupName)).click();

			waitFor(1000);

			waitForElement(locatePatternValueByPatternName(patternName), 10, 1000);
			driver.findElement(locatePatternValueByPatternName(patternName)).click();
			
			waitFor(1000);
			
			driver.findElement(locatePatternValueSubmitButtonByPatternName(patternName)).click();

			waitFor(1000);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean addNewPatternInGroupDuplicateName(String groupName, String patternName) {
		try {
			waitForElement(locatePatternGroupByName(groupName), 10, 1000);
			driver.findElement(locatePatternGroupByName(groupName)).click();

			waitForElement(addNewPatternButton, 10, 1000);
			driver.findElement(addNewPatternButton).click();

			waitFor(1000);

			waitForElement(addNewPatternNameFieldInput, 10, 1000);
			driver.findElement(addNewPatternNameFieldInput).click();
			driver.findElement(addNewPatternNameFieldInput).sendKeys(patternName);
			driver.findElement(addNewPatternNameFieldInput).sendKeys(Keys.ENTER);

			waitForElement(duplicateAlertMessage, 10, 1000);

			waitForElement(refreshPatternButton, 10, 1000);
			driver.findElement(refreshPatternButton).click();
			waitForElement(refreshConfirmationMessage, 10, 1000);

			waitFor(1000);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean setTextInRegexBox(String text) {
		try {
			waitForElement(regexBox, 10, 1000);
			driver.findElement(regexBox).click();
			
			waitFor(1000);
			driver.findElement(regexBox).sendKeys(text);
			
			waitFor(1000);
			
			driver.findElement(regexBox).sendKeys(Keys.ENTER);

			waitFor(1000);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isTextExistOnPage(String text) {
		try {
			waitForElement(By.xpath(".//*[contains(text(),'" + text + "')"), 10, 1000);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean clearRegexBox() {
		try {
			waitForElement(regexBox, 10, 1000);
			driver.findElement(regexBox).clear();

			waitFor(1000);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isPatternNameValuePairExistInGroup(String groupName, String patternName, String patternValue) {
		try {
			waitForElement(locatePatternGroupByName(groupName), 10, 1000);
			driver.findElement(locatePatternGroupByName(groupName)).click();

			waitFor(1000);

			driver.findElement(locatePatternByNameValuePair(patternName, patternValue));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isMatchedPatternHighlighted(String patternName) {
		try {
			waitForElement(locateMatchedHighLightedPatternByName(patternName), 10, 500);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isPatternCheckedByName(String patternName) {
		try {
			waitForElement(locateCheckedHighLightedPatternByName(patternName), 10, 500);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean clickPatternCheckboxByName(String patternName) {
		try {
			waitForElement(locateCheckBoxByPatternName(patternName), 10, 200);
			driver.findElement(locateCheckBoxByPatternName(patternName)).click();
			waitFor(1000);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean checkHelpLink() {
		try {
			waitForElement(helpButton, 10, 1000);
			driver.findElement(helpButton).click();

			waitFor(1000);

			waitForElement(helpWindowHeader, 10, 1000);
			waitForElement(helpWindowJavaRegularExpressionLink, 10, 1000);

			waitForElement(helpCloseDialogWindowButton, 10, 1000);
			driver.findElement(helpCloseDialogWindowButton).click();

			waitFor(1000);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isAddNewGroupSpecValidationMessageExist() {
		try {
			waitForElement(addNewPatternGroupAlertMessageSpecChar, 10, 1000);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isAddNewGroupDuplicateValidationMessageExist() {
		try {
			waitForElement(addNewPatternGroupAlertMessageDuplicate, 10, 1000);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean addNewPatternGroupCloseWindow() {
		try {
			waitForElement(addNewPatternGroupCloseDialogWindow, 10, 1000);
			driver.findElement(addNewPatternGroupCloseDialogWindow).click();

			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
