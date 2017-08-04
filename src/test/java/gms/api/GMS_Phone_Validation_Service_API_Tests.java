package gms.api;

import com.genesyslab.ats.cfg.actor.CfgManager;
import com.genesyslab.ats.ex.AtsCfgComponentException;
import com.genesyslab.scsmanager.SCSManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import org.junit.*;

import java.io.FileInputStream;
import java.util.Properties;

import static com.genesyslab.functional.tests.gms.helper.ServiceOptions.getCallbackTermServiceOptions;
import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/*
 * [Test suite] [Callback] Phone Number validation service https://jira.genesys.com/browse/GMS-4604
 */

public class GMS_Phone_Validation_Service_API_Tests {
	private static String gmsHost;
	private static String gmsPort;

	private static String callbackServiceName = "callbackPhoneNumberValidationService";
	private String requestBody = "";

	private static String gmsPhoneNumberURL;
	private static String callbackServiceURL;

	private static String callbackSectionName = "service." + callbackServiceName;

	private static CfgManager cfgManager = new CfgManager();
	private static SCSManager scsManager = new SCSManager(cfgManager);
	private static Properties properties = new Properties();

	private static String gmsAppName;

	@BeforeClass
	public static void oneTimeSetUp() {
		try {
			properties.load(new FileInputStream("C:\\configgms851.properties"));

			gmsHost = properties.getProperty("gms.host");
			gmsPort = properties.getProperty("gms.port");
			gmsAppName = properties.getProperty("gms.app.name");

			gmsPhoneNumberURL = "http://" + gmsHost + ":" + gmsPort + "/genesys/1/phonenumber";
			callbackServiceURL = "http://" + gmsHost + ":" + gmsPort + "/genesys/1/service/callback/"
					+ callbackServiceName;

			scsManager.init(properties);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().addSection(callbackSectionName,
					getCallbackTermServiceOptions());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@AfterClass
	public static void oneTimeTearDown() {

		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName).deleteSection();
		} catch (AtsCfgComponentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			scsManager.deactivate();
			cfgManager.deactivate();
		}

	}

	@Before
	public void setUp() {

	}

	@After
	public void tearDown() {

	}

	// Test 1. Example Operation success (French Mobile)
	@Test
	public void test_01_01() {

		Response r = given().get(gmsPhoneNumberURL + "?number=0604120405&country=FR&geocodingLocale=DE");

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r.getStatusCode());

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		// System.out.println(responseBodyObject.get("time-zone").getAsString());

		assertTrue(responseBodyObject.get("carrier").getAsString().equals("SFR"));
		assertEquals(responseBodyObject.get("country-code").getAsByte(), 33);
		assertTrue(responseBodyObject.get("country-code-source").getAsString().equals("FROM_DEFAULT_COUNTRY"));
		assertTrue(responseBodyObject.get("country-default").getAsString().equals("FR"));
		assertTrue(responseBodyObject.get("extension").getAsString().equals(""));
		assertEquals(responseBodyObject.get("is-possible-number").getAsBoolean(), true);
		assertEquals(responseBodyObject.get("is-premium").getAsBoolean(), false);
		assertEquals(responseBodyObject.get("is-valid-number").getAsBoolean(), true);
		assertEquals(responseBodyObject.get("is-valid-number-for-region").getAsBoolean(), true);
		assertEquals(responseBodyObject.get("italian-leading-zero").getAsBoolean(), false);
		assertTrue(responseBodyObject.get("language-specified").getAsString().equals("de"));
		assertTrue(responseBodyObject.get("location").getAsString().equals("Frankreich"));
		assertTrue(responseBodyObject.get("number-E164").getAsString().equals("+33604120405"));
		assertTrue(responseBodyObject.get("number-international").getAsString().equals("+33 6 04 12 04 05"));
		assertTrue(responseBodyObject.get("number-national").getAsString().equals("06 04 12 04 05"));
		assertEquals(responseBodyObject.get("number-national-short").getAsInt(), 604120405);
		assertTrue(responseBodyObject.get("number-original").getAsString().equals("06 04 12 04 05"));
		assertTrue(responseBodyObject.get("number-specified").getAsString().equals("0604120405"));
		assertTrue(responseBodyObject.get("number-type").getAsString().equals("MOBILE"));
		assertTrue(responseBodyObject.get("region-code").getAsString().equals("FR"));
		assertTrue(responseBodyObject.get("time-zone").getAsString().equals("Europe/Paris"));
	}

	// Test 2. Example Operation success (German fixed line)
	@Test
	public void test_01_02() {

		Response r = given().get(gmsPhoneNumberURL + "?number=+4989125016040&country=DE&geocodingLocale=FR");

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r.getStatusCode());

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		// System.out.println(responseBodyObject.get("time-zone").getAsString());

		// assertTrue(responseBodyObject.get("carrier").getAsString().equals("SFR"));
		assertEquals(responseBodyObject.get("country-code").getAsByte(), 49);
		assertTrue(responseBodyObject.get("country-code-source").getAsString().equals("FROM_NUMBER_WITH_PLUS_SIGN"));
		assertTrue(responseBodyObject.get("country-default").getAsString().equals("DE"));
		assertTrue(responseBodyObject.get("extension").getAsString().equals(""));
		assertEquals(responseBodyObject.get("is-possible-number").getAsBoolean(), true);
		assertEquals(responseBodyObject.get("is-premium").getAsBoolean(), false);
		assertEquals(responseBodyObject.get("is-valid-number").getAsBoolean(), true);
		assertEquals(responseBodyObject.get("is-valid-number-for-region").getAsBoolean(), true);
		assertEquals(responseBodyObject.get("italian-leading-zero").getAsBoolean(), false);
		assertTrue(responseBodyObject.get("language-specified").getAsString().equals("fr"));
		assertTrue(responseBodyObject.get("location").getAsString().equals("Munich"));
		assertTrue(responseBodyObject.get("number-E164").getAsString().equals("+4989125016040"));
		assertTrue(responseBodyObject.get("number-international").getAsString().equals("+49 89 125016040"));
		assertTrue(responseBodyObject.get("number-national").getAsString().equals("089 125016040"));
		assertTrue(responseBodyObject.get("number-national-short").getAsString().equals("89125016040"));
		assertTrue(responseBodyObject.get("number-original").getAsString().equals("+49 89 125016040"));
		assertTrue(responseBodyObject.get("number-specified").getAsString().equals("+4989125016040"));
		assertTrue(responseBodyObject.get("number-type").getAsString().equals("FIXED_LINE"));
		assertTrue(responseBodyObject.get("region-code").getAsString().equals("DE"));
		assertTrue(responseBodyObject.get("time-zone").getAsString().equals("Europe/Berlin"));
	}

	// Test 3. Example Operation success (US fixed line)
	@Test
	public void test_01_03() {

		Response r = given().get(gmsPhoneNumberURL + "?number=+16504661100&country=US&geocodingLocale=FR");

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r.getStatusCode());

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		// System.out.println(responseBodyObject.get("time-zone").getAsString());

		assertTrue(responseBodyObject.get("carrier").getAsString().equals(""));
		assertEquals(responseBodyObject.get("country-code").getAsByte(), 1);
		assertTrue(responseBodyObject.get("country-code-source").getAsString().equals("FROM_NUMBER_WITH_PLUS_SIGN"));
		assertTrue(responseBodyObject.get("country-default").getAsString().equals("US"));
		assertTrue(responseBodyObject.get("extension").getAsString().equals(""));
		assertEquals(responseBodyObject.get("is-possible-number").getAsBoolean(), true);
		assertEquals(responseBodyObject.get("is-premium").getAsBoolean(), false);
		assertEquals(responseBodyObject.get("is-valid-number").getAsBoolean(), true);
		assertEquals(responseBodyObject.get("is-valid-number-for-region").getAsBoolean(), true);
		assertEquals(responseBodyObject.get("italian-leading-zero").getAsBoolean(), false);
		assertTrue(responseBodyObject.get("language-specified").getAsString().equals("fr"));
		assertTrue(responseBodyObject.get("location").getAsString().equals("California"));
		assertTrue(responseBodyObject.get("number-E164").getAsString().equals("+16504661100"));
		assertTrue(responseBodyObject.get("number-international").getAsString().equals("+1 650-466-1100"));
		assertTrue(responseBodyObject.get("number-national").getAsString().equals("(650) 466-1100"));
		assertTrue(responseBodyObject.get("number-national-short").getAsString().equals("6504661100"));
		assertTrue(responseBodyObject.get("number-type").getAsString().equals("FIXED_LINE_OR_MOBILE"));
		assertTrue(responseBodyObject.get("region-code").getAsString().equals("US"));
		assertTrue(responseBodyObject.get("time-zone").getAsString().equals("America/Los_Angeles"));
	}

	// Test 4. Example Operation success (Ukraine mobile)
	@Test
	public void test_01_04() {

		Response r = given().get(gmsPhoneNumberURL + "?number=509113790&country=UA");

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r.getStatusCode());

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		// System.out.println(responseBodyObject.get("time-zone").getAsString());

		assertTrue(responseBodyObject.get("carrier").getAsString().equals("MTS"));
		assertEquals(responseBodyObject.get("country-code").getAsInt(), 380);
		assertTrue(responseBodyObject.get("country-code-source").getAsString().equals("FROM_DEFAULT_COUNTRY"));
		assertTrue(responseBodyObject.get("country-default").getAsString().equals("UA"));
		assertTrue(responseBodyObject.get("extension").getAsString().equals(""));
		assertEquals(responseBodyObject.get("is-possible-number").getAsBoolean(), true);
		assertEquals(responseBodyObject.get("is-premium").getAsBoolean(), false);
		assertEquals(responseBodyObject.get("is-valid-number").getAsBoolean(), true);
		assertEquals(responseBodyObject.get("is-valid-number-for-region").getAsBoolean(), true);
		assertEquals(responseBodyObject.get("italian-leading-zero").getAsBoolean(), false);
		assertTrue(responseBodyObject.get("language-specified").getAsString().equals("en"));
		assertTrue(responseBodyObject.get("location").getAsString().equals("Ukraine"));
		assertTrue(responseBodyObject.get("number-E164").getAsString().equals("+380509113790"));
		assertTrue(responseBodyObject.get("number-international").getAsString().equals("+380 50 911 3790"));
		assertTrue(responseBodyObject.get("number-national").getAsString().equals("050 911 3790"));
		assertEquals(responseBodyObject.get("number-national-short").getAsInt(), 509113790);
		assertTrue(responseBodyObject.get("number-original").getAsString().equals("50 911 3790"));
		assertTrue(responseBodyObject.get("number-specified").getAsString().equals("509113790"));
		assertTrue(responseBodyObject.get("number-type").getAsString().equals("MOBILE"));
		assertTrue(responseBodyObject.get("region-code").getAsString().equals("UA"));
		assertTrue(responseBodyObject.get("time-zone").getAsString().equals("Europe/Bucharest"));
	}

	// Test 5. Example Operation failed
	@Test
	public void test_01_05() {

		Response r = given().get(gmsPhoneNumberURL + "?number=bad&country=DE&geocodingLocale=de");

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(400, r.getStatusCode());

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		// System.out.println(responseBodyObject.get("time-zone").getAsString());

		assertTrue(responseBodyObject.get("exception").getAsString().equals("com.genesyslab.gsg.GSGException"));
		assertTrue(responseBodyObject.get("message").getAsString()
				.equals("Invalid Phone Number bad for Country DE (parsing impossible)"));
	}

	// Test 6. CallbackNumberValidation
	// _disallow_premium_phone_numbers = false
	// _default_country = US
	@Test
	public void test_01_06() {
		JsonObject requestBodyObject = new JsonObject();
		requestBodyObject.addProperty("_customer_number", "+19005551234");
		requestBodyObject.addProperty("_urs_virtual_queue", "SIP_VQ_SIP_Switch");

		requestBody = requestBodyObject.toString();

		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.addOption("_disallow_premium_phone_numbers", "false");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.addOption("_default_country", "US");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().contentType(ContentType.JSON).body(requestBody).post(callbackServiceURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r.getStatusCode());

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		// System.out.println(responseBodyObject.get("time-zone").getAsString());

		assertTrue(responseBodyObject.get("_ok_title").getAsString().equals("Ok"));
		assertTrue(responseBodyObject.get("_action").getAsString().equals("ConfirmationDialog"));
		assertTrue(responseBodyObject.get("_text").getAsString().equals("You will receive the call shortly"));
		assertTrue(responseBodyObject.get("_dialog_id").getAsString().equals("0"));
		assertTrue(responseBodyObject.get("_id").getAsString() != null);

	}

	// Test 7. CallbackNumberValidation
	// _disallow_premium_phone_numbers = true
	// _default_country = US
	@Test
	public void test_01_07() {
		JsonObject requestBodyObject = new JsonObject();
		requestBodyObject.addProperty("_customer_number", "+19005551234");
		requestBodyObject.addProperty("_urs_virtual_queue", "SIP_VQ_SIP_Switch");

		requestBody = requestBodyObject.toString();

		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.addOption("_disallow_premium_phone_numbers", "true");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.addOption("_default_country", "US");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().contentType(ContentType.JSON).body(requestBody).post(callbackServiceURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(400, r.getStatusCode());

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		// System.out.println(responseBodyObject.get("time-zone").getAsString());

		assertTrue(responseBodyObject.get("exception").getAsString()
				.equals("com.genesyslab.gsg.services.callback.exceptions.CallbackExceptionNumber"));
		assertTrue(responseBodyObject.get("message").getAsString().equals(
				"Customer Number [+19005551234] is disallowed, because it's a premium number. Check option _disallow_premium_phone_numbers"));

	}

	// Test 8. CallbackNumberValidation
	// _disallow_premium_phone_numbers = true
	// _default_country = ""
	@Test
	public void test_01_08() {
		JsonObject requestBodyObject = new JsonObject();
		requestBodyObject.addProperty("_customer_number", "+19005551234");
		requestBodyObject.addProperty("_urs_virtual_queue", "SIP_VQ_SIP_Switch");

		requestBody = requestBodyObject.toString();

		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.addOption("_disallow_premium_phone_numbers", "true");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.addOption("_default_country", "");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().contentType(ContentType.JSON).body(requestBody).post(callbackServiceURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(400, r.getStatusCode());

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		// System.out.println(responseBodyObject.get("time-zone").getAsString());

		assertTrue(responseBodyObject.get("exception").getAsString()
				.equals("com.genesyslab.gsg.services.callback.exceptions.CallbackExceptionNumber"));
		assertTrue(responseBodyObject.get("message").getAsString()
				.equals("Service option callbackPhoneNumberValidationService / _default_country is not configured."
						+ " But option _disallow_premium_phone_numbers is set. We cannot validate phone numbers without knowing the country."));
	}

	// Test 9. CallbackNumberValidation
	// _disallow_impossible_phone_numbers = false
	// _default_country = US
	@Test
	public void test_01_09() {
		JsonObject requestBodyObject = new JsonObject();
		requestBodyObject.addProperty("_customer_number", "111");
		requestBodyObject.addProperty("_urs_virtual_queue", "SIP_VQ_SIP_Switch");

		requestBody = requestBodyObject.toString();

		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.addOption("_disallow_impossible_phone_numbers", "false");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.addOption("_default_country", "US");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().contentType(ContentType.JSON).body(requestBody).post(callbackServiceURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r.getStatusCode());

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		// System.out.println(responseBodyObject.get("time-zone").getAsString());

		assertTrue(responseBodyObject.get("_ok_title").getAsString().equals("Ok"));
		assertTrue(responseBodyObject.get("_action").getAsString().equals("ConfirmationDialog"));
		assertTrue(responseBodyObject.get("_text").getAsString().equals("You will receive the call shortly"));
		assertTrue(responseBodyObject.get("_dialog_id").getAsString().equals("0"));
		assertTrue(responseBodyObject.get("_id").getAsString() != null);

	}

	// Test 10. CallbackNumberValidation
	// _disallow_impossible_phone_numbers = true
	// _default_country = US
	@Test
	public void test_01_10() {
		JsonObject requestBodyObject = new JsonObject();
		requestBodyObject.addProperty("_customer_number", "111");
		requestBodyObject.addProperty("_urs_virtual_queue", "SIP_VQ_SIP_Switch");

		requestBody = requestBodyObject.toString();

		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.addOption("_disallow_impossible_phone_numbers", "true");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.addOption("_default_country", "US");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().contentType(ContentType.JSON).body(requestBody).post(callbackServiceURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(400, r.getStatusCode());

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		// System.out.println(responseBodyObject.get("time-zone").getAsString());

		assertTrue(responseBodyObject.get("exception").getAsString()
				.equals("com.genesyslab.gsg.services.callback.exceptions.CallbackExceptionNumber"));
		assertTrue(responseBodyObject.get("message").getAsString().equals(
				"Customer Number [111] is disallowed, because it is invalid. Check option _disallow_impossible_phone_numbers"));

	}

	// Test 11. CallbackNumberValidation
	// _disallow_impossible_phone_numbers = true
	// _default_country = ""
	@Test
	public void test_01_11() {
		JsonObject requestBodyObject = new JsonObject();
		requestBodyObject.addProperty("_customer_number", "111");
		requestBodyObject.addProperty("_urs_virtual_queue", "SIP_VQ_SIP_Switch");

		requestBody = requestBodyObject.toString();

		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.addOption("_disallow_impossible_phone_numbers", "true");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.addOption("_default_country", "");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().contentType(ContentType.JSON).body(requestBody).post(callbackServiceURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(400, r.getStatusCode());

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		// System.out.println(responseBodyObject.get("time-zone").getAsString());

		assertTrue(responseBodyObject.get("exception").getAsString()
				.equals("com.genesyslab.gsg.services.callback.exceptions.CallbackExceptionNumber"));
		assertTrue(responseBodyObject.get("message").getAsString()
				.equals("Service option callbackPhoneNumberValidationService / _default_country is not configured."
						+ " But option _disallow_impossible_phone_numbers is set. We cannot validate phone numbers without knowing the country."));
	}
}
