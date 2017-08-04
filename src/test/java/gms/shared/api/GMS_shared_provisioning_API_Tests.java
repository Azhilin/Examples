package gms.shared.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import org.junit.*;

import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.*;

/*
* [Test suite] [Shared GMS] Provisioning API https://jira.genesys.com/browse/GMS-4606
*/

public class GMS_shared_provisioning_API_Tests {
	private static String gwsURL = "http://s-usw1-htcc.genhtcc.com/api/v2/me";
	private String sharedGmsURL = "http://gms.usw1.genhtcc.com/genesys/1/tenants";

	private static String username = "3501_admin";
	private static String password = "3501_admin";

	private static String contactCenterId;
	private static Map<String, String> cookies;

	@BeforeClass
	public static void oneTimeSetUp() {

		Response r = given().auth().preemptive().basic(username, password).get(gwsURL);

		String responseBody = r.asString();

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		JsonObject userBodyObject = new JsonObject();
		userBodyObject = responseBodyObject.get("user").getAsJsonObject();

		contactCenterId = userBodyObject.get("contactCenterId").getAsString();
		cookies = r.cookies();

		// System.out.println("contactCenterId: " + contactCenterId);
		// System.out.println("cookies" + cookies);

	}

	@AfterClass
	public static void oneTimeTearDown() {

	}

	@Before
	public void setUp() {

	}

	@After
	public void tearDown() {

	}

	// Test 1. Get services
	@Test
	public void test_01_01() {
		String requestURL = sharedGmsURL;

		Response r = given().cookies(cookies).get(requestURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r.getStatusCode());

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

//		JsonObject authBodyObject = new JsonObject();
//		authBodyObject = responseBodyObject.get("authentication").getAsJsonObject();
//		assertTrue(authBodyObject.get("username").getAsString().equals("3501_admin"));
//		assertTrue(authBodyObject.get("password").getAsString().equals("3501_admin"));

		JsonObject serviceBodyObject = new JsonObject();
		serviceBodyObject = responseBodyObject.get("service.test1").getAsJsonObject();
		assertTrue(serviceBodyObject.get("key1").getAsString().equals("value1"));
		assertTrue(serviceBodyObject.get("key2").getAsString().equals("value2"));
		assertTrue(serviceBodyObject.get("key3").getAsString().equals("value3"));
	}

	// Test 2. Get services. 403 Forbidden
	@Test
	public void test_01_02() {
		String requestURL = sharedGmsURL;

		Response r = given().get(requestURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(403, r.getStatusCode());

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		assertTrue(responseBodyObject.get("exception").getAsString().equals("java.rmi.AccessException"));
		assertTrue(responseBodyObject.get("message").getAsString().equals("Requires permission"));

	}

	// Test 3. Get tenants info (ORS + URS) for all tenants
	@Test
	public void test_01_03() {
		String requestURL = sharedGmsURL + "/info";

		Response r = given().get(requestURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r.getStatusCode());

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		JsonObject tenantBodyObject = new JsonObject();
		tenantBodyObject = responseBodyObject.get(contactCenterId).getAsJsonObject();

		JsonObject cfgOrsServer = new JsonObject();
		cfgOrsServer = tenantBodyObject.get("CFGOrchestrationServer").getAsJsonObject();
		JsonObject orsPrimary = new JsonObject();
		orsPrimary = cfgOrsServer.get("primary").getAsJsonObject();
		assertTrue(orsPrimary.get("ORS").getAsString().equals("http://svoice-35-01-p.usw1.genhtcc.com:9098"));
		assertTrue(orsPrimary.get("ORS_B").getAsString().equals("http://svoice-35-01-b.usw1.genhtcc.com:9098"));

		JsonObject cfgUrsServer = new JsonObject();
		cfgUrsServer = tenantBodyObject.get("CFGRouterServer").getAsJsonObject();
		JsonObject ursPrimary = new JsonObject();
		ursPrimary = cfgUrsServer.get("primary").getAsJsonObject();
		assertTrue(ursPrimary.get("URS").getAsString().equals("http://svoice-35-01-p.usw1.genhtcc.com:3072"));
		assertTrue(ursPrimary.get("URS_B").getAsString().equals("http://svoice-35-01-b.usw1.genhtcc.com:3072"));

	}

	// Test 4. Get tenants info (ORS + URS) for specific tenant
	@Test
	public void test_01_04() {
		String requestURL = sharedGmsURL + "/info";

		Response r = given().header("contactCenterId", contactCenterId).get(requestURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r.getStatusCode());

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		JsonObject tenantBodyObject = new JsonObject();
		tenantBodyObject = responseBodyObject.get(contactCenterId).getAsJsonObject();

		JsonObject cfgOrsServer = new JsonObject();
		cfgOrsServer = tenantBodyObject.get("CFGOrchestrationServer").getAsJsonObject();
		JsonObject orsPrimary = new JsonObject();
		orsPrimary = cfgOrsServer.get("primary").getAsJsonObject();
		assertTrue(orsPrimary.get("ORS").getAsString().equals("http://svoice-35-01-p.usw1.genhtcc.com:9098"));
		assertTrue(orsPrimary.get("ORS_B").getAsString().equals("http://svoice-35-01-b.usw1.genhtcc.com:9098"));

		JsonObject cfgUrsServer = new JsonObject();
		cfgUrsServer = tenantBodyObject.get("CFGRouterServer").getAsJsonObject();
		JsonObject ursPrimary = new JsonObject();
		ursPrimary = cfgUrsServer.get("primary").getAsJsonObject();
		assertTrue(ursPrimary.get("URS").getAsString().equals("http://svoice-35-01-p.usw1.genhtcc.com:3072"));
		assertTrue(ursPrimary.get("URS_B").getAsString().equals("http://svoice-35-01-b.usw1.genhtcc.com:3072"));

	}

	// Test 5. Get tenants info (ORS + URS) for specific tenant. 404 Not found
	@Test
	public void test_01_05() {
		String requestURL = sharedGmsURL + "/info";

		Response r = given().header("contactCenterId", "badCCID").get(requestURL);

		String responseBody = r.asString();

		assertEquals(404, r.getStatusCode());

		assertTrue(responseBody.equals(""));

	}

	// Test 6. Create/Update service for specific tenant
	@Test
	public void test_01_06() {
		String requestURL = sharedGmsURL;
		String serviceName = "service.test1";
		String key1 = "key1";
		String key2 = "key2";
		String key3 = "key3";
		String value1 = "value1";
		String value2 = "value2";
		String value3 = "value3";

		JsonObject serviceBody = new JsonObject();
		serviceBody.addProperty(key1, value1);
		serviceBody.addProperty(key2, value2);
		serviceBody.addProperty(key3, value3);

		JsonObject requestBody = new JsonObject();
		requestBody.add(serviceName, serviceBody);

		Response r = given().cookies(cookies).contentType(ContentType.JSON).body(requestBody.toString())
				.post(requestURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r.getStatusCode());

		assertTrue(responseBody.equals(""));

		Response r2 = given().cookies(cookies).get(requestURL);

		String responseBody2 = r2.asString();

		JsonObject responseBodyObject = new JsonParser().parse(responseBody2).getAsJsonObject();

		JsonObject serviceBodyObject = new JsonObject();
		serviceBodyObject = responseBodyObject.get(serviceName).getAsJsonObject();
		assertTrue(serviceBodyObject.get(key1).getAsString().equals(value1));
		assertTrue(serviceBodyObject.get(key2).getAsString().equals(value2));
		assertTrue(serviceBodyObject.get(key3).getAsString().equals(value3));
		
		Response r1 = given().cookies(cookies).contentType(ContentType.JSON).body("[\"" + serviceName + "\"]")
				.delete(requestURL);

		String responseBody1 = r1.asString();

		//System.out.println("response body: " + responseBody1);

		assertEquals(200, r1.getStatusCode());

		assertTrue(responseBody1.equals(""));
	}

	// Test 7. Create/Update service for specific tenant. 415 Unsupported Media
	// Type
	@Test
	public void test_01_07() {
		String requestURL = sharedGmsURL;
		String serviceName = "service.test1";
		String key1 = "key1";
		String key2 = "key2";
		String key3 = "key3";
		String value1 = "value1";
		String value2 = "value2";
		String value3 = "value3";

		JsonObject serviceBody = new JsonObject();
		serviceBody.addProperty(key1, value1);
		serviceBody.addProperty(key2, value2);
		serviceBody.addProperty(key3, value3);

		JsonObject requestBody = new JsonObject();
		requestBody.add(serviceName, serviceBody);

		Response r = given().cookies(cookies).contentType(ContentType.URLENC).body(requestBody.toString())
				.post(requestURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(415, r.getStatusCode());

		assertTrue(responseBody.equals("Unsupported Media Type"));

		Response r2 = given().cookies(cookies).get(requestURL);

		String responseBody2 = r2.asString();

		JsonObject responseBodyObject = new JsonParser().parse(responseBody2).getAsJsonObject();

		Boolean isServiceExist = true;

		try {
			responseBodyObject.get(serviceName).getAsJsonObject();
			isServiceExist = true;
		} catch (NullPointerException ex) {
			isServiceExist = false;
		}

		// System.out.println(isServiceExist);

		 assertFalse(isServiceExist);

	}

	// Test 8. Create/Update service for specific tenant. 403 Forbidden
	@Test
	public void test_01_08() {
		String requestURL = sharedGmsURL;
		String serviceName = "service.test1";
		String key1 = "key1";
		String key2 = "key2";
		String key3 = "key3";
		String value1 = "value1";
		String value2 = "value2";
		String value3 = "value3";

		JsonObject serviceBody = new JsonObject();
		serviceBody.addProperty(key1, value1);
		serviceBody.addProperty(key2, value2);
		serviceBody.addProperty(key3, value3);

		JsonObject requestBody = new JsonObject();
		requestBody.add(serviceName, serviceBody);

		Response r = given().cookies("BadCookie", "123456789").contentType(ContentType.JSON)
				.body(requestBody.toString()).post(requestURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(403, r.getStatusCode());

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		assertTrue(responseBodyObject.get("exception").getAsString().equals("java.rmi.AccessException"));
		assertTrue(responseBodyObject.get("message").getAsString()
				.equals("Requires permission: GWS Server error, unable to connect"));

	}

	// Test 9. Refresh Contact Center API for specific tenant
	@Test
	public void test_01_09() {
		String requestURL = sharedGmsURL + "/refresh";

		Response r = given().header("contactCenterId", contactCenterId).post(requestURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(202, r.getStatusCode());

		assertTrue(responseBody.equals(""));

	}

	// Test 10. Refresh Contact Center API for specific tenant. 404 Not Found
	@Test
	public void test_01_10() {
		String requestURL = sharedGmsURL + "/refresh";

		Response r = given().header("contactCenterId", "badCCID").post(requestURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(404, r.getStatusCode());

		assertTrue(responseBody.equals(""));

	}

	// Test 11. Refresh Contact Center API for specific tenant. 400 Bad Request
	@Test
	public void test_01_11() {
		String requestURL = sharedGmsURL + "/refresh";

		Response r = given().post(requestURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(400, r.getStatusCode());

		assertTrue(responseBody.equals(""));

	}

	// Test 12. Delete service for specific tenant
	@Test
	public void test_01_12() {
		String requestURL = sharedGmsURL;
		String serviceName = "service.test1";
		String key1 = "key1";
		String key2 = "key2";
		String key3 = "key3";
		String value1 = "value1";
		String value2 = "value2";
		String value3 = "value3";

		JsonObject serviceBody = new JsonObject();
		serviceBody.addProperty(key1, value1);
		serviceBody.addProperty(key2, value2);
		serviceBody.addProperty(key3, value3);

		JsonObject requestBody = new JsonObject();
		requestBody.add(serviceName, serviceBody);

		Response r = given().cookies(cookies).contentType(ContentType.JSON).body(requestBody.toString())
				.post(requestURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r.getStatusCode());

		assertTrue(responseBody.equals(""));		

		Response r1 = given().cookies(cookies).contentType(ContentType.JSON).body("[\"" + serviceName + "\"]")
				.delete(requestURL);

		String responseBody1 = r1.asString();

		//System.out.println("response body: " + responseBody1);

		assertEquals(200, r1.getStatusCode());

		assertTrue(responseBody1.equals(""));

		Response r2 = given().cookies(cookies).get(requestURL);

		String responseBody2 = r2.asString();

		JsonObject responseBodyObject = new JsonParser().parse(responseBody2).getAsJsonObject();

		Boolean isServiceExist = true;

		try {
			responseBodyObject.get(serviceName).getAsJsonObject();
			isServiceExist = true;
		} catch (NullPointerException ex) {
			isServiceExist = false;
		}

		// System.out.println(isServiceExist);

		assertFalse(isServiceExist);

	}

	// Test 13. Delete service for specific tenant 403 Forbidden
	@Test
	public void test_01_13() {
		String requestURL = sharedGmsURL;
		String serviceName = "service.test1";
		String key1 = "key1";
		String key2 = "key2";
		String key3 = "key3";
		String value1 = "value1";
		String value2 = "value2";
		String value3 = "value3";

		JsonObject serviceBody = new JsonObject();
		serviceBody.addProperty(key1, value1);
		serviceBody.addProperty(key2, value2);
		serviceBody.addProperty(key3, value3);

		JsonObject requestBody = new JsonObject();
		requestBody.add(serviceName, serviceBody);

		Response r = given().cookies(cookies).contentType(ContentType.JSON).body(requestBody.toString())
				.post(requestURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r.getStatusCode());

		assertTrue(responseBody.equals(""));

		Response r1 = given().contentType(ContentType.JSON).body("[\"" + serviceName + "\"]").delete(requestURL);

		String responseBody1 = r1.asString();

		//System.out.println("response body: " + responseBody1);

		assertEquals(200, r1.getStatusCode());

		assertTrue(responseBody1.equals(""));

		assertTrue(responseBody1.equals(""));

		Response r2 = given().cookies(cookies).get(requestURL);

		String responseBody2 = r2.asString();

		JsonObject responseBodyObject = new JsonParser().parse(responseBody2).getAsJsonObject();

		Boolean isServiceExist = true;

		try {
			responseBodyObject.get(serviceName).getAsJsonObject();
			isServiceExist = true;
		} catch (NullPointerException ex) {
			isServiceExist = false;
		}

		// System.out.println(isServiceExist);

		assertTrue(isServiceExist);

	}

}
