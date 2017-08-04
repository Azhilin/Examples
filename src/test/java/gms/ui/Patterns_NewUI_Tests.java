package gms.ui;

import com.genesyslab.functional.tests.gms.ui.configuredservices.GMSConfiguredServicesPage;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import org.junit.*;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.*;

/*
 * [Test suite] Patterns. GUI testing https://jira.genesys.com/browse/GMS-2373
 */

public class Patterns_NewUI_Tests {

	static WebDriver driver;

	private static Properties properties = new Properties();

	private static String gmsHost;
	private static String gmsPort;

	private static String gmsUrl;
	private static String gmsPatternsApiURL;

	private static GMSLoginPage loginPage = null;
	private static GMSMainPage mainPage = null;
	private static GMSConfiguredServicesPage configuredServicesPage = null;
	private static GMSPatternsPage patternsPage = null;

	private static String username = "demo";
	private static String password = "";

	// Pattern Group names
	private static String patternGroupNameBasic = "patternGroup";
	private static int patternGroupNumber = 1;

	// Pattern Group names for test data
	private static String patternGroupInvalidName = "PatternGroup InvalidName";

	@BeforeClass
	public static void oneTimeSetUp() {
		try {
			properties.load(new FileInputStream("C:\\configgms851.properties"));

			gmsHost = properties.getProperty("gms.host");
			gmsPort = properties.getProperty("gms.port");

			gmsUrl = "http://" + gmsHost + ":" + gmsPort + "/genesys/admin/login.jsp";
			gmsPatternsApiURL = "http://" + gmsHost + ":" + gmsPort + "/genesys/1/patterns";

		} catch (Exception e) {
			e.printStackTrace();
		}

		DesiredCapabilities cap = new DesiredCapabilities("firefox", "stable", Platform.WINDOWS);

		try {
			driver = new RemoteWebDriver(new URL("http://135.225.54.31:4444/wd/hub"), cap);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// driver = new FirefoxDriver();

		driver.get(gmsUrl);
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.manage().window().maximize();

		loginPage = new GMSLoginPage(driver);
		mainPage = loginPage.logIn(username, password);
//		configuredServicesPage = mainPage.clickCallbackLocatorUINew();
//		patternsPage = configuredServicesPage.clickPatternsMenuUINew();
		
		configuredServicesPage = mainPage.goToConfiguredServicesUIPage();
		patternsPage = configuredServicesPage.goToPatternsUIPage();

	}

	@AfterClass
	public static void oneTimeTearDown() {

		// Deleting patterGroups with dynamic names
		for (int i = 1; i < patternGroupNumber; i++) {
			String patGroupTemp = patternGroupNameBasic + i;
			if (patternsPage.isPatternGroupExist(patGroupTemp))
				patternsPage.deletePatternGroup(patGroupTemp);
		}

		// Deleting patterGroups with static names
		if (patternsPage.isPatternGroupExist(patternGroupInvalidName))
			patternsPage.deletePatternGroup(patternGroupInvalidName);

		loginPage = patternsPage.clickLogOut(driver);
		if (driver != null)
			driver.quit();

	}

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
		patternsPage.clearRegexBox();

	}

	// Test 1. Test pattern value with several patterns in group
	@Test
	public void test01() {
		String patternGroup = patternGroupNameBasic + patternGroupNumber++;
		String patternName1 = "Pattern1";
		String patternValue1 = "PatternValue1";
		String patternName2 = "Pattern2";
		String patternValue2 = "PatternValue2";
		String patternName3 = "Pattern3";
		String patternValue3 = "PatternValue3";

		if (patternsPage.isPatternGroupNotExist(patternGroup))
			assertTrue(patternsPage.addNewPatternGroup(patternGroup));

		if (!patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName1, patternValue1))
			patternsPage.addNewPatternInGroup(patternGroup, patternName1, patternValue1);
		if (!patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName2, patternValue2))
			patternsPage.addNewPatternInGroup(patternGroup, patternName2, patternValue2);
		if (!patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName3, patternValue3))
			patternsPage.addNewPatternInGroup(patternGroup, patternName3, patternValue3);

		assertTrue(patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName1, patternValue1));
		assertTrue(patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName2, patternValue2));
		assertTrue(patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName3, patternValue3));

		//assertTrue(patternsPage.setTextInRegexBox(patternValue2));
		//assertTrue(patternsPage.isMatchedPatternHighlighted(patternName2));

		boolean result = false;

		for (int i=0;i<5;i++){
			patternsPage.clearRegexBox();
			if(patternsPage.setTextInRegexBox(patternValue2)) {
				if(patternsPage.isMatchedPatternHighlighted(patternName2)) {
					result = true;
					break;
				}
			}
		}
		assertTrue(result);

	}

	// Test 2. Delete pattern
	@Test
	public void test02() {
		String patternGroup = patternGroupNameBasic + patternGroupNumber++;
		String patternName1 = "Pattern1";
		String patternValue1 = "PatternValue1";
		String patternName2 = "Pattern2";
		String patternValue2 = "PatternValue2";
		String patternName3 = "Pattern3";
		String patternValue3 = "PatternValue3";

		if (patternsPage.isPatternGroupNotExist(patternGroup))
			assertTrue(patternsPage.addNewPatternGroup(patternGroup));

		if (!patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName1, patternValue1))
			patternsPage.addNewPatternInGroup(patternGroup, patternName1, patternValue1);
		if (!patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName2, patternValue2))
			patternsPage.addNewPatternInGroup(patternGroup, patternName2, patternValue2);
		if (!patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName3, patternValue3))
			patternsPage.addNewPatternInGroup(patternGroup, patternName3, patternValue3);

		assertTrue(patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName1, patternValue1));
		assertTrue(patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName2, patternValue2));
		assertTrue(patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName3, patternValue3));

		if (patternsPage.isPatternCheckedByName(patternName1))
			patternsPage.clickPatternCheckboxByName(patternName1);
		if (!patternsPage.isPatternCheckedByName(patternName2))
			patternsPage.clickPatternCheckboxByName(patternName2);
		if (!patternsPage.isPatternCheckedByName(patternName3))
			patternsPage.clickPatternCheckboxByName(patternName3);

		assertTrue(patternsPage.deletePattern());

		assertTrue(patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName1, patternValue1));
		assertFalse(patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName2, patternValue2));
		assertFalse(patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName3, patternValue3));
	}

	// Test 3. Delete pattern group with patterns
	@Test
	public void test03() {
		String patternGroup = patternGroupNameBasic + patternGroupNumber++;
		String patternName1 = "Pattern1";
		String patternValue1 = "PatternValue1";
		String patternName2 = "Pattern2";
		String patternValue2 = "PatternValue2";

		if (patternsPage.isPatternGroupNotExist(patternGroup))
			assertTrue(patternsPage.addNewPatternGroup(patternGroup));

		if (!patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName1, patternValue1))
			patternsPage.addNewPatternInGroup(patternGroup, patternName1, patternValue1);
		if (!patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName2, patternValue2))
			patternsPage.addNewPatternInGroup(patternGroup, patternName2, patternValue2);

		assertTrue(patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName1, patternValue1));
		assertTrue(patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName2, patternValue2));

		assertTrue(patternsPage.deletePatternGroup(patternGroup));
		assertFalse(patternsPage.isPatternGroupExist(patternGroup));
	}

	// Test 4. Delete empty pattern group
	@Test
	public void test04() {
		String patternGroup = patternGroupNameBasic + patternGroupNumber++;

		if (patternsPage.isPatternGroupExist(patternGroup))
			patternsPage.deletePatternGroup(patternGroup);
		assertTrue(patternsPage.addNewPatternGroup(patternGroup));

		assertTrue(patternsPage.deletePatternGroup(patternGroup));
		assertFalse(patternsPage.isPatternGroupExist(patternGroup));
	}

	// Test 5. Check the "Help" link
	@Test
	public void test05() {
		assertTrue(patternsPage.checkHelpLink());
	}

	// Test 6. Create pattern group with invalid name (spec char)
	@Test
	public void test06() {
		String patternGroup = patternGroupInvalidName;

		if (patternsPage.isPatternGroupExist(patternGroup))
			patternsPage.deletePatternGroup(patternGroup);

		if (patternsPage.isPatternGroupNotExist(patternGroup))
			patternsPage.addNewPatternGroup(patternGroup);

		assertTrue(patternsPage.isAddNewGroupSpecValidationMessageExist());
		assertTrue(patternsPage.addNewPatternGroupCloseWindow());
		assertTrue(patternsPage.isPatternGroupNotExist(patternGroup));
	}

	// Test 7. Create pattern group with invalid name (duplicate name)
	@Test
	public void test07() {
		String patternGroup = patternGroupNameBasic + patternGroupNumber++;

		if (patternsPage.isPatternGroupNotExist(patternGroup))
			assertTrue(patternsPage.addNewPatternGroup(patternGroup));

		if (patternsPage.isPatternGroupExist(patternGroup))
			patternsPage.addNewPatternGroup(patternGroup);

		assertTrue(patternsPage.isAddNewGroupDuplicateValidationMessageExist());
		assertTrue(patternsPage.addNewPatternGroupCloseWindow());
		assertTrue(patternsPage.deletePatternGroup(patternGroup));
		assertTrue(patternsPage.isPatternGroupNotExist(patternGroup));
	}

	// Test 8. Canceling pattern group creation
	@Test
	public void test08() {
		String patternGroup = patternGroupNameBasic + patternGroupNumber++;

		if (patternsPage.isPatternGroupExist(patternGroup))
			patternsPage.deletePatternGroup(patternGroup);

		if (patternsPage.isPatternGroupNotExist(patternGroup))
			patternsPage.addNewPatternGroupCancel(patternGroup);

		assertTrue(patternsPage.isPatternGroupNotExist(patternGroup));
	}

	// Test 9. Canceling pattern group removal using Cancel button
	@Test
	public void test09() {
		String patternGroup = patternGroupNameBasic + patternGroupNumber++;

		if (patternsPage.isPatternGroupNotExist(patternGroup))
			assertTrue(patternsPage.addNewPatternGroup(patternGroup));

		assertTrue(patternsPage.deletePatternGroupCancel(patternGroup));
		assertTrue(patternsPage.isPatternGroupExist(patternGroup));
	}

	// Test 10. Canceling pattern group removal using Close window button
	@Test
	public void test10() {
		String patternGroup = patternGroupNameBasic + patternGroupNumber++;

		if (patternsPage.isPatternGroupNotExist(patternGroup))
			assertTrue(patternsPage.addNewPatternGroup(patternGroup));

		assertTrue(patternsPage.deletePatternGroupClose(patternGroup));
		assertTrue(patternsPage.isPatternGroupExist(patternGroup));
	}

	// Test 11. Create pattern with spec char in name
	@Test
	public void test11() {
		String patternGroup = patternGroupNameBasic + patternGroupNumber++;
		String patternName = "!@#$%^&*()_+";
		String patternValue = "PatternValue";

		if (patternsPage.isPatternGroupNotExist(patternGroup))
			assertTrue(patternsPage.addNewPatternGroup(patternGroup));

		assertTrue(patternsPage.addNewPatternInGroup(patternGroup, patternName, patternValue));
		assertTrue(patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName, patternValue));
	}

	// Test 12. Create pattern with invalid name (duplicate name)
	@Test
	public void test12() {
		String patternGroup = patternGroupNameBasic + patternGroupNumber++;
		String patternName = "!@#$%^&*()_+";
		String patternValue = "PatternValue";

		if (patternsPage.isPatternGroupNotExist(patternGroup))
			assertTrue(patternsPage.addNewPatternGroup(patternGroup));

		assertTrue(patternsPage.addNewPatternInGroup(patternGroup, patternName, patternValue));
		assertTrue(patternsPage.addNewPatternInGroupDuplicateName(patternGroup, patternName));
		assertTrue(patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName, patternValue));
	}

	// Test 13. After clearing all items and text fields "Error: Forbidden" is
	// absent
	@Test
	public void test13() {
		String patternGroup = patternGroupNameBasic + patternGroupNumber++;
		String patternName1 = "Pattern1";
		String patternValue1 = "PatternValue1";
		String patternName2 = "Pattern2";
		String patternValue2 = "PatternValue2";

		String textOnPage = "Forbidden";

		if (patternsPage.isPatternGroupNotExist(patternGroup))
			assertTrue(patternsPage.addNewPatternGroup(patternGroup));

		if (!patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName1, patternValue1))
			patternsPage.addNewPatternInGroup(patternGroup, patternName1, patternValue1);
		if (!patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName2, patternValue2))
			patternsPage.addNewPatternInGroup(patternGroup, patternName2, patternValue2);

		assertTrue(patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName1, patternValue1));
		assertTrue(patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName2, patternValue2));

//		assertTrue(patternsPage.setTextInRegexBox(patternValue2));
//		assertTrue(patternsPage.isMatchedPatternHighlighted(patternName2));

		boolean result = false;

		for (int i=0;i<5;i++){
			patternsPage.clearRegexBox();
			if(patternsPage.setTextInRegexBox(patternValue2)) {
				if(patternsPage.isMatchedPatternHighlighted(patternName2)) {
					result = true;
					break;
				}
			}
		}
		assertTrue(result);

		if (patternsPage.isPatternCheckedByName(patternName1))
			patternsPage.clickPatternCheckboxByName(patternName1);
		if (!patternsPage.isPatternCheckedByName(patternName2))
			patternsPage.clickPatternCheckboxByName(patternName2);

		assertTrue(patternsPage.deletePattern());

		assertTrue(patternsPage.clearRegexBox());

		assertFalse(patternsPage.isTextExistOnPage(textOnPage));
	}

	// Test 14. If we delete the last pattern in the group the whole group also
	// is deleted
	@Test
	public void test14() {
		String patternGroup = patternGroupNameBasic + patternGroupNumber++;
		String patternName1 = "Pattern1";
		String patternValue1 = "PatternValue1";

		if (patternsPage.isPatternGroupExist(patternGroup))
			patternsPage.deletePatternGroup(patternGroup);
		assertTrue(patternsPage.addNewPatternGroup(patternGroup));

		if (!patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName1, patternValue1))
			patternsPage.addNewPatternInGroup(patternGroup, patternName1, patternValue1);

		assertTrue(patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName1, patternValue1));

		if (!patternsPage.isPatternCheckedByName(patternName1))
			patternsPage.clickPatternCheckboxByName(patternName1);

		assertTrue(patternsPage.deletePattern());
		assertFalse(patternsPage.isPatternGroupExist(patternGroup));
	}

	// Test 15. Need to add close button in help window
	@Test
	public void test15() {
		assertTrue(patternsPage.checkHelpLink());
	}

	// Test 16. No text in confirmation message after patterns deleting
	@Test
	public void test16() {
		String patternGroup = patternGroupNameBasic + patternGroupNumber++;
		String patternName1 = "Pattern1";
		String patternValue1 = "PatternValue1";

		if (patternsPage.isPatternGroupNotExist(patternGroup))
			assertTrue(patternsPage.addNewPatternGroup(patternGroup));

		assertTrue(patternsPage.addNewPatternInGroup(patternGroup, patternName1, patternValue1));

		assertTrue(patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName1, patternValue1));

		if (!patternsPage.isPatternCheckedByName(patternName1))
			patternsPage.clickPatternCheckboxByName(patternName1);

		assertTrue(patternsPage.deletePattern());
		assertFalse(patternsPage.isPatternGroupExist(patternGroup));
	}

	// Test 17. Deleted patterns keys can't be recreated without refreshing
	@Test
	public void test17() {
		String patternGroup = patternGroupNameBasic + patternGroupNumber++;
		String patternName1 = "Pattern1";
		String patternValue1 = "PatternValue1";
		String patternName2 = "Pattern2";
		String patternValue2 = "PatternValue2";

		if (patternsPage.isPatternGroupNotExist(patternGroup))
			assertTrue(patternsPage.addNewPatternGroup(patternGroup));

		if (!patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName1, patternValue1))
			patternsPage.addNewPatternInGroup(patternGroup, patternName1, patternValue1);
		if (!patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName2, patternValue2))
			patternsPage.addNewPatternInGroup(patternGroup, patternName2, patternValue2);

		assertTrue(patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName1, patternValue1));
		assertTrue(patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName2, patternValue2));

		if (patternsPage.isPatternCheckedByName(patternName1))
			patternsPage.clickPatternCheckboxByName(patternName1);
		if (!patternsPage.isPatternCheckedByName(patternName2))
			patternsPage.clickPatternCheckboxByName(patternName2);

		assertTrue(patternsPage.deletePattern());

		assertTrue(patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName1, patternValue1));
		assertFalse(patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName2, patternValue2));

		if (!patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName1, patternValue1))
			patternsPage.addNewPatternInGroup(patternGroup, patternName1, patternValue1);
		if (!patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName2, patternValue2))
			patternsPage.addNewPatternInGroup(patternGroup, patternName2, patternValue2);

		assertTrue(patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName1, patternValue1));
		assertTrue(patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName2, patternValue2));
	}

	// Test 18. [UI] New Patterns page. "\" sign is hidden after page refresh
	// and is lost after resave the pattern GMS-3593
	@Test
	public void test18() {
		String patternGroup = patternGroupNameBasic + patternGroupNumber++;
		String patternGroupUrl = gmsPatternsApiURL + "/group/" + patternGroup;
		String patternName = "Pattern1";
		String patternValue = "\\d{3}";

		String text = "555";

		if (patternsPage.isPatternGroupNotExist(patternGroup))
			assertTrue(patternsPage.addNewPatternGroup(patternGroup));

		assertTrue(patternsPage.addNewPatternInGroup(patternGroup, patternName, patternValue));

		Response r = given().contentType(ContentType.URLENC).parameter("param1", text).post(patternGroupUrl);

		String responseBody = r.asString();

		assertEquals(200, r.getStatusCode());

		System.out.println("response body: " + responseBody);

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		assertTrue(responseBodyObject.get("param1").getAsString().equals(patternName));
		
		assertTrue(patternsPage.resavePatternValueByPattenNameInGroup(patternGroup, patternName));

		Response r1 = given().contentType(ContentType.URLENC).parameter("param1", text).post(patternGroupUrl);

		String responseBody1 = r1.asString();

		assertEquals(200, r1.getStatusCode());

		System.out.println("response body: " + responseBody1);

		JsonObject responseBodyObject1 = new JsonParser().parse(responseBody1).getAsJsonObject();

		assertTrue(responseBodyObject1.get("param1").getAsString().equals(patternName));
	}

	// Test 19. Email. Check if parameters match the patterns
	@Test
	public void test19() {
		String patternGroup = patternGroupNameBasic + patternGroupNumber++;
		String patternGroupUrl = gmsPatternsApiURL + "/group/" + patternGroup;
		String patternName = "Email";
		String patternValue = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.)((net)||(com))$";

		String text1 = "a@int.com";
		String text2 = "a@.com";
		String text3 = "a.b@int.net";

		if (patternsPage.isPatternGroupNotExist(patternGroup))
			assertTrue(patternsPage.addNewPatternGroup(patternGroup));

		assertTrue(patternsPage.addNewPatternInGroup(patternGroup, patternName, patternValue));

		Response r = given().contentType(ContentType.URLENC).parameter("param1", text1).parameter("param2", text2)
				.parameter("param3", text3).post(patternGroupUrl);

		String responseBody = r.asString();

		assertEquals(200, r.getStatusCode());

		System.out.println("response body: " + responseBody);

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		assertTrue(responseBodyObject.get("param1").getAsString().equals(patternName));
		assertTrue(responseBodyObject.get("param3").getAsString().equals(patternName));

		Boolean isInvalidParamMatched = true;

		try {
			responseBodyObject.get("param2").getAsString().equals(patternName);
			isInvalidParamMatched = true;
		} catch (NullPointerException ex) {
			isInvalidParamMatched = false;
		}

		assertFalse(isInvalidParamMatched);
	}

	// Test 20. Time. Check if parameters match the patterns
	@Test
	public void test20() {
		String patternGroup = patternGroupNameBasic + patternGroupNumber++;
		String patternGroupUrl = gmsPatternsApiURL + "/group/" + patternGroup;
		String patternName = "Time";
		String patternValue = "^(([0-9])||(1[012])):[0-5][0-9]\\s[aApP][mM]$";

		String text1 = "10:59 am";
		String text2 = "13:11 Am";
		String text3 = "0:01 pM";

		if (patternsPage.isPatternGroupNotExist(patternGroup))
			assertTrue(patternsPage.addNewPatternGroup(patternGroup));

		assertTrue(patternsPage.addNewPatternInGroup(patternGroup, patternName, patternValue));

		Response r = given().contentType(ContentType.URLENC).parameter("param1", text1).parameter("param2", text2)
				.parameter("param3", text3).post(patternGroupUrl);

		String responseBody = r.asString();

		assertEquals(200, r.getStatusCode());

		System.out.println("response body: " + responseBody);

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		assertTrue(responseBodyObject.get("param1").getAsString().equals(patternName));
		assertTrue(responseBodyObject.get("param3").getAsString().equals(patternName));

		Boolean isInvalidParamMatched = true;

		try {
			responseBodyObject.get("param2").getAsString().equals(patternName);
			isInvalidParamMatched = true;
		} catch (NullPointerException ex) {
			isInvalidParamMatched = false;
		}

		assertFalse(isInvalidParamMatched);
	}

	// Test 21. Date. Check if parameters match the patterns
	@Test
	public void test21() {
		String patternGroup = patternGroupNameBasic + patternGroupNumber++;
		String patternGroupUrl = gmsPatternsApiURL + "/group/" + patternGroup;
		String patternName = "Date";
		String patternValue = "^(([1-9])||([012][1-9])||(3[01]))/(((0[1-9])||([1-9]))||(1[012]))/((19[0-9][0-9])||(20[0-9][0-9]))$";

		String text1 = "01/01/2000";
		String text2 = "01/13/1999";
		String text3 = "1/1/1995";

		if (patternsPage.isPatternGroupNotExist(patternGroup))
			assertTrue(patternsPage.addNewPatternGroup(patternGroup));

		assertTrue(patternsPage.addNewPatternInGroup(patternGroup, patternName, patternValue));

		Response r = given().contentType(ContentType.URLENC).parameter("param1", text1).parameter("param2", text2)
				.parameter("param3", text3).post(patternGroupUrl);

		String responseBody = r.asString();

		assertEquals(200, r.getStatusCode());

		System.out.println("response body: " + responseBody);

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		assertTrue(responseBodyObject.get("param1").getAsString().equals(patternName));
		assertTrue(responseBodyObject.get("param3").getAsString().equals(patternName));

		Boolean isInvalidParamMatched = true;

		try {
			responseBodyObject.get("param2").getAsString().equals(patternName);
			isInvalidParamMatched = true;
		} catch (NullPointerException ex) {
			isInvalidParamMatched = false;
		}

		assertFalse(isInvalidParamMatched);
	}

	// Test 22. IP. Check if parameters match the patterns
	@Test
	public void test22() {
		String patternGroup = patternGroupNameBasic + patternGroupNumber++;
		String patternGroupUrl = gmsPatternsApiURL + "/group/" + patternGroup;
		String patternName = "IP";
		String patternValue = "^((\\d)||([1-9]\\d)||(1\\d\\d)||(2[0-4]\\d)||(25[0-5]))"
				+ "\\.((\\d)||([1-9]\\d)||(1\\d\\d)||(2[0-4]\\d)||(25[0-5]))"
				+ "\\.((\\d)||([1-9]\\d)||(1\\d\\d)||(2[0-4]\\d)||(25[0-5]))"
				+ "\\.((\\d)||([1-9]\\d)||(1\\d\\d)||(2[0-4]\\d)||(25[0-5]))$";

		String text1 = "135.17.36.255";
		String text2 = "135.17.36.256";
		String text3 = "1.0.0.1";

		if (patternsPage.isPatternGroupNotExist(patternGroup))
			assertTrue(patternsPage.addNewPatternGroup(patternGroup));

		assertTrue(patternsPage.addNewPatternInGroup(patternGroup, patternName, patternValue));

		Response r = given().contentType(ContentType.URLENC).parameter("param1", text1).parameter("param2", text2)
				.parameter("param3", text3).post(patternGroupUrl);

		String responseBody = r.asString();

		assertEquals(200, r.getStatusCode());

		System.out.println("response body: " + responseBody);

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		assertTrue(responseBodyObject.get("param1").getAsString().equals(patternName));
		assertTrue(responseBodyObject.get("param3").getAsString().equals(patternName));

		Boolean isInvalidParamMatched = true;

		try {
			responseBodyObject.get("param2").getAsString().equals(patternName);
			isInvalidParamMatched = true;
		} catch (NullPointerException ex) {
			isInvalidParamMatched = false;
		}

		assertFalse(isInvalidParamMatched);
	}
}
