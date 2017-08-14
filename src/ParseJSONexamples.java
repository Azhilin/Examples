package gms.shared.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import org.junit.*;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/*
* [Test suite] [Shared GMS] Enable authentication against GWS https://jira.genesys.com/browse/GMS-4605
*/

public class GMS_shared_GWS_authentication_API_Tests {
	private String gwsURL = "http://s-usw1-htcc.genhtcc.com";
	private String gwsMeURL = gwsURL + "/api/v2/me";
	private String gwsConfigURL = gwsURL + "/api/v2/platform/configuration";

	private String username = "3501_admin";
	private String password = "3501_admin";

	@BeforeClass
	public static void oneTimeSetUp() {		

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

	// Test 1. Get tenants info
	@Test
	public void test_01_01() {

		Response r = given().auth().preemptive().basic(username, password).get(gwsMeURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r.getStatusCode());

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		// System.out.println(responseBodyObject.get("statusCode").getAsString());

		assertTrue(responseBodyObject.get("statusCode").getAsString().equals("0"));

		JsonObject userBodyObject = new JsonObject();
		userBodyObject = responseBodyObject.get("user").getAsJsonObject();

		assertTrue(userBodyObject.get("id").getAsString().equals("062329213def43b2b92602c99a5c799a"));
		assertTrue(userBodyObject.get("userName").getAsString().equals("3501_admin"));
		assertTrue(userBodyObject.get("firstName").getAsString().equals("3501_admin"));
		assertTrue(userBodyObject.get("lastName").getAsString().equals("3501_admin"));
		assertTrue(userBodyObject.get("emailAddress").getAsString().equals(""));
		assertTrue(userBodyObject.get("roles").getAsString().equals("ROLE_ADMIN"));
		assertTrue(userBodyObject.get("enabled").getAsString().equals("true"));
		assertTrue(userBodyObject.get("changePasswordOnFirstLogin").getAsString().equals("false"));
		assertTrue(userBodyObject.get("contactCenterId").getAsString().equals("8318fd12-9e8f-4d72-b43c-dc0917c5103e"));
		assertTrue(userBodyObject.get("uri").getAsString()
				.equals("https://htcc-demo.genhtcc.com/api/v2/users/062329213def43b2b92602c99a5c799a"));
		assertTrue(userBodyObject.get("path").getAsString().equals("/users/062329213def43b2b92602c99a5c799a"));

		// System.out.println(userBodyObject.get("roles").getAsString());

	}

	// Test 2. Retrieve all configuration objects directly from Config Server
	// CfgAccessGroup: URI name - access-groups
	@Test
	public void test_01_02() {
		String cfgObjectURL = gwsConfigURL + "/access-groups";

		Response r = given().auth().preemptive().basic(username, password).get(cfgObjectURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r.getStatusCode());

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		// System.out.println(responseBodyObject.get("statusCode").getAsString());

		assertTrue(responseBodyObject.get("statusCode").getAsString().equals("0"));

		JsonArray accessGroups = responseBodyObject.get("access-groups").getAsJsonArray();

		assertEquals(accessGroups.size(), 19);

		// System.out.println("accessGroups: " + accessGroups.get(0));

		// System.out.println("accessGroups: " + accessGroups.size());

		JsonObject jsonItem = new JsonObject();
		JsonObject cfgGroupObject = new JsonObject();

		for (int i = 0; i < accessGroups.size(); i++) {
			jsonItem = accessGroups.get(i).getAsJsonObject();
			cfgGroupObject = jsonItem.get("CfgGroup").getAsJsonObject();
			if (cfgGroupObject.get("name").getAsString().equals("Administrators")) {
				break;
			}
			;
		}

		// System.out.println("jsonItem: " + jsonItem);

		assertTrue(jsonItem.get("type").getAsString().equals("3"));

		assertTrue(cfgGroupObject.get("state").getAsString().equals("1"));
		assertTrue(cfgGroupObject.get("name").getAsString().equals("Administrators"));
		assertTrue(cfgGroupObject.get("tenantDBID").getAsString().equals("1"));
		assertTrue(cfgGroupObject.get("DBID").getAsString().equals("102"));
		assertTrue(cfgGroupObject.get("capacityTableDBID").getAsString().equals("0"));
		assertTrue(cfgGroupObject.get("siteDBID").getAsString().equals("0"));
		assertTrue(cfgGroupObject.get("capacityRuleDBID").getAsString().equals("0"));
		assertTrue(cfgGroupObject.get("contractDBID").getAsString().equals("0"));
		assertTrue(cfgGroupObject.get("quotaTableDBID").getAsString().equals("0"));

	}

	// Test 3. Retrieve specific configuration object directly from Config
	// Server
	// CfgAccessGroup: URI name - access-groups
	@Test
	public void test_01_03() {
		String cfgObjectURL = gwsConfigURL + "/access-groups/102";

		Response r = given().auth().preemptive().basic(username, password).get(cfgObjectURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r.getStatusCode());

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		assertTrue(responseBodyObject.get("statusCode").getAsString().equals("0"));

		JsonObject accessGroup = responseBodyObject.get("access-group").getAsJsonObject();

		assertTrue(accessGroup.get("type").getAsString().equals("3"));

		JsonObject cfgGroupObject = new JsonObject();
		cfgGroupObject = accessGroup.get("CfgGroup").getAsJsonObject();

		assertTrue(cfgGroupObject.get("state").getAsString().equals("1"));
		assertTrue(cfgGroupObject.get("name").getAsString().equals("Administrators"));
		assertTrue(cfgGroupObject.get("tenantDBID").getAsString().equals("1"));
		assertTrue(cfgGroupObject.get("DBID").getAsString().equals("102"));
		assertTrue(cfgGroupObject.get("capacityTableDBID").getAsString().equals("0"));
		assertTrue(cfgGroupObject.get("siteDBID").getAsString().equals("0"));
		assertTrue(cfgGroupObject.get("capacityRuleDBID").getAsString().equals("0"));
		assertTrue(cfgGroupObject.get("contractDBID").getAsString().equals("0"));
		assertTrue(cfgGroupObject.get("quotaTableDBID").getAsString().equals("0"));

	}

	// Test 4. Retrieve all configuration objects directly from Config Server
	// CfgAccessGroup: URI name - applications
	@Test
	public void test_01_04() {
		String cfgObjectURL = gwsConfigURL + "/applications";

		Response r = given().auth().preemptive().basic(username, password).get(cfgObjectURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r.getStatusCode());

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		// System.out.println(responseBodyObject.get("statusCode").getAsString());

		assertTrue(responseBodyObject.get("statusCode").getAsString().equals("0"));

		JsonArray applications = responseBodyObject.get("applications").getAsJsonArray();

		// assertEquals(accessGroups.size(), 19);

		// System.out.println("applications: " + applications.size());

		JsonObject application = new JsonObject();

		for (int i = 0; i < applications.size(); i++) {
			application = applications.get(i).getAsJsonObject();
			if (application.get("name").getAsString().equals("GMS_Node_1")) {
				break;
			}
			;
		}

		// System.out.println("jsonItem: " + jsonItem);

		assertTrue(application.get("DBID").getAsString().equals("404"));
		assertTrue(application.get("isServer").getAsString().equals("2"));
		assertTrue(application.get("type").getAsString().equals("107"));
		assertTrue(application.get("version").getAsString().equals("8.5.108.02"));
		assertTrue(application.get("state").getAsString().equals("1"));
		assertTrue(application.get("redundancyType").getAsString().equals("1"));
		assertTrue(application.get("startupType").getAsString().equals("1"));
		assertTrue(application.get("appPrototypeDBID").getAsString().equals("166"));
		assertTrue(application.get("commandLine").getAsString().equals("./launcher"));
		assertTrue(application.get("commandLineArguments").getAsString()
				.equals("-host smfwk-35-01-p.usw1.genhtcc.com -port 8888 -app GMS_Node_1  -vms_host sauxfe-35-01-001"));
		assertTrue(application.get("componentType").getAsString().equals("0"));
		assertTrue(application.get("isPrimary").getAsString().equals("2"));
		assertTrue(application.get("shutdownTimeout").getAsString().equals("90"));
		assertTrue(application.get("workDirectory").getAsString().equals("/genesys/GMS-8.5.108.02/"));
		assertTrue(application.get("tenantDBIDs").getAsString().equals("1"));
		assertTrue(application.get("startupTimeout").getAsString().equals("90"));
		assertTrue(application.get("name").getAsString().equals("GMS_Node_1"));

	}

	// Test 5. Retrieve specific configuration object directly from Config
	// Server
	// CfgAccessGroup: URI name - applications
	@Test
	public void test_01_05() {
		String cfgObjectURL = gwsConfigURL + "/applications/404";

		Response r = given().auth().preemptive().basic(username, password).get(cfgObjectURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r.getStatusCode());

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		// System.out.println(responseBodyObject.get("statusCode").getAsString());

		assertTrue(responseBodyObject.get("statusCode").getAsString().equals("0"));

		JsonObject application = responseBodyObject.get("application").getAsJsonObject();

		assertTrue(application.get("DBID").getAsString().equals("404"));
		assertTrue(application.get("isServer").getAsString().equals("2"));
		assertTrue(application.get("type").getAsString().equals("107"));
		assertTrue(application.get("version").getAsString().equals("8.5.108.02"));
		assertTrue(application.get("state").getAsString().equals("1"));
		assertTrue(application.get("redundancyType").getAsString().equals("1"));
		assertTrue(application.get("startupType").getAsString().equals("1"));
		assertTrue(application.get("appPrototypeDBID").getAsString().equals("166"));
		assertTrue(application.get("commandLine").getAsString().equals("./launcher"));
		assertTrue(application.get("commandLineArguments").getAsString()
				.equals("-host smfwk-35-01-p.usw1.genhtcc.com -port 8888 -app GMS_Node_1  -vms_host sauxfe-35-01-001"));
		assertTrue(application.get("componentType").getAsString().equals("0"));
		assertTrue(application.get("isPrimary").getAsString().equals("2"));
		assertTrue(application.get("shutdownTimeout").getAsString().equals("90"));
		assertTrue(application.get("workDirectory").getAsString().equals("/genesys/GMS-8.5.108.02/"));
		assertTrue(application.get("tenantDBIDs").getAsString().equals("1"));
		assertTrue(application.get("startupTimeout").getAsString().equals("90"));
		assertTrue(application.get("name").getAsString().equals("GMS_Node_1"));

	}

	// Test 6. Retrieve specific configuration object directly from Config
	// Server using filter
	// CfgAccessGroup: URI name - applications
	@Test
	public void test_01_06() {
		String cfgObjectURL = gwsConfigURL + "/applications?name=\"GMS_Node_1\"";

		Response r = given().auth().preemptive().basic(username, password).get(cfgObjectURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r.getStatusCode());

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		// System.out.println(responseBodyObject.get("statusCode").getAsString());

		assertTrue(responseBodyObject.get("statusCode").getAsString().equals("0"));

		JsonArray applications = responseBodyObject.get("applications").getAsJsonArray();

		assertEquals(applications.size(), 1);

		JsonObject application = new JsonObject();
		application = applications.get(0).getAsJsonObject();

		assertTrue(application.get("DBID").getAsString().equals("404"));
		assertTrue(application.get("isServer").getAsString().equals("2"));
		assertTrue(application.get("type").getAsString().equals("107"));
		assertTrue(application.get("version").getAsString().equals("8.5.108.02"));
		assertTrue(application.get("state").getAsString().equals("1"));
		assertTrue(application.get("redundancyType").getAsString().equals("1"));
		assertTrue(application.get("startupType").getAsString().equals("1"));
		assertTrue(application.get("appPrototypeDBID").getAsString().equals("166"));
		assertTrue(application.get("commandLine").getAsString().equals("./launcher"));
		assertTrue(application.get("commandLineArguments").getAsString()
				.equals("-host smfwk-35-01-p.usw1.genhtcc.com -port 8888 -app GMS_Node_1  -vms_host sauxfe-35-01-001"));
		assertTrue(application.get("componentType").getAsString().equals("0"));
		assertTrue(application.get("isPrimary").getAsString().equals("2"));
		assertTrue(application.get("shutdownTimeout").getAsString().equals("90"));
		assertTrue(application.get("workDirectory").getAsString().equals("/genesys/GMS-8.5.108.02/"));
		assertTrue(application.get("tenantDBIDs").getAsString().equals("1"));
		assertTrue(application.get("startupTimeout").getAsString().equals("90"));
		assertTrue(application.get("name").getAsString().equals("GMS_Node_1"));

	}

	// Test 7. Retrieve all configuration objects directly from Config Server
	// CfgAccessGroup: URI name - skills
	@Test
	public void test_01_07() {
		String cfgObjectURL = gwsConfigURL + "/skills";

		Response r = given().auth().preemptive().basic(username, password).get(cfgObjectURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r.getStatusCode());

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		// System.out.println(responseBodyObject.get("statusCode").getAsString());

		assertTrue(responseBodyObject.get("statusCode").getAsString().equals("0"));

		JsonArray skills = responseBodyObject.get("skills").getAsJsonArray();

		// assertEquals(accessGroups.size(), 19);

		// System.out.println("applications: " + applications.size());

		JsonObject skill = new JsonObject();

		for (int i = 0; i < skills.size(); i++) {
			skill = skills.get(i).getAsJsonObject();
			if (skill.get("name").getAsString().equals("QAART_SKILL_1")) {
				break;
			}
			;
		}

		// System.out.println("jsonItem: " + jsonItem);

		assertTrue(skill.get("DBID").getAsString().equals("218"));
		assertTrue(skill.get("state").getAsString().equals("1"));
		assertTrue(skill.get("tenantDBID").getAsString().equals("1"));
		assertTrue(skill.get("name").getAsString().equals("QAART_SKILL_1"));

	}

	// Test 8. Retrieve specific configuration object directly from Config
	// Server
	// CfgAccessGroup: URI name - skills
	@Test
	public void test_01_08() {
		String cfgObjectURL = gwsConfigURL + "/skills/218";

		Response r = given().auth().preemptive().basic(username, password).get(cfgObjectURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r.getStatusCode());

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		// System.out.println(responseBodyObject.get("statusCode").getAsString());

		assertTrue(responseBodyObject.get("statusCode").getAsString().equals("0"));

		JsonObject skill = responseBodyObject.get("skill").getAsJsonObject();

		assertTrue(skill.get("DBID").getAsString().equals("218"));
		assertTrue(skill.get("state").getAsString().equals("1"));
		assertTrue(skill.get("tenantDBID").getAsString().equals("1"));
		assertTrue(skill.get("name").getAsString().equals("QAART_SKILL_1"));

	}

	// Test 9. Retrieve specific configuration object directly from Config
	// Server using filter
	// CfgAccessGroup: URI name - skills
	@Test
	public void test_01_09() {
		String cfgObjectURL = gwsConfigURL + "/skills?name=\"QAART_SKILL_1\"";

		Response r = given().auth().preemptive().basic(username, password).get(cfgObjectURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r.getStatusCode());

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		// System.out.println(responseBodyObject.get("statusCode").getAsString());

		assertTrue(responseBodyObject.get("statusCode").getAsString().equals("0"));

		JsonArray skills = responseBodyObject.get("skills").getAsJsonArray();

		assertEquals(skills.size(), 1);

		JsonObject skill = new JsonObject();
		skill = skills.get(0).getAsJsonObject();

		assertTrue(skill.get("DBID").getAsString().equals("218"));
		assertTrue(skill.get("state").getAsString().equals("1"));
		assertTrue(skill.get("tenantDBID").getAsString().equals("1"));
		assertTrue(skill.get("name").getAsString().equals("QAART_SKILL_1"));

	}

	// Test 10. Create configuration object directly in Config Server
	// CfgAccessGroup: URI name - skills
	@Test
	public void test_01_10() {
		String cfgObjectURL = gwsConfigURL + "/skills";
		String skillName = "TEST_SKILL_1";

		JsonObject skillBody = new JsonObject();
		skillBody.addProperty("name", skillName);
		skillBody.addProperty("tenantDBID", "1");
		skillBody.addProperty("state", "1");

		JsonObject requestBody = new JsonObject();
		requestBody.add("skill", skillBody);

		// System.out.println("requestBody " + requestBody);

		Response r = given().auth().preemptive().basic(username, password).contentType(ContentType.JSON)
				.body(requestBody.toString()).post(cfgObjectURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r.getStatusCode());

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		String skillUrl = responseBodyObject.get("uri").getAsString();

		Response r2 = given().auth().preemptive().basic(username, password).get(skillUrl);

		String responseBody2 = r2.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r2.getStatusCode());

		JsonObject responseBodyObject2 = new JsonParser().parse(responseBody2).getAsJsonObject();

		// System.out.println(responseBodyObject.get("statusCode").getAsString());

		assertTrue(responseBodyObject2.get("statusCode").getAsString().equals("0"));

		JsonObject skill = responseBodyObject2.get("skill").getAsJsonObject();

		assertTrue(skill.get("state").getAsString().equals("1"));
		assertTrue(skill.get("tenantDBID").getAsString().equals("1"));
		assertTrue(skill.get("name").getAsString().equals(skillName));

		Response r3 = given().auth().preemptive().basic(username, password).contentType(ContentType.JSON)
				.delete(skillUrl);

		// String responseBody3 = r3.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r3.getStatusCode());

	}

	// Test 11. Create configuration object directly in Config Server
	// Duplicate name
	// CfgAccessGroup: URI name - skills
	@Test
	public void test_01_11() {
		String cfgObjectURL = gwsConfigURL + "/skills";
		String skillName = "TEST_SKILL_1";

		JsonObject skillBody = new JsonObject();
		skillBody.addProperty("name", skillName);
		skillBody.addProperty("tenantDBID", "1");
		skillBody.addProperty("state", "1");

		JsonObject requestBody = new JsonObject();
		requestBody.add("skill", skillBody);

		// System.out.println("requestBody " + requestBody);

		Response r = given().auth().preemptive().basic(username, password).contentType(ContentType.JSON)
				.body(requestBody.toString()).post(cfgObjectURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r.getStatusCode());

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		String skillUrl = responseBodyObject.get("uri").getAsString();

		Response r2 = given().auth().preemptive().basic(username, password).contentType(ContentType.JSON)
				.body(requestBody.toString()).post(cfgObjectURL);

		String responseBody2 = r2.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(500, r2.getStatusCode());

		JsonObject responseBodyObject2 = new JsonParser().parse(responseBody2).getAsJsonObject();

		// System.out.println(responseBodyObject.get("statusCode").getAsString());

		assertTrue(responseBodyObject2.get("statusCode").getAsString().equals("4"));
		assertTrue(responseBodyObject2.get("statusMessage").getAsString()
				.equals("Platform exception: Uniqueness constraint violated"));
		assertTrue(responseBodyObject2.get("platformCode").getAsString().equals("5836044"));

		Response r3 = given().auth().preemptive().basic(username, password).contentType(ContentType.JSON)
				.delete(skillUrl);

		// String responseBody3 = r3.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r3.getStatusCode());

	}

	// Test 12. Update configuration object directly in Config Server
	// CfgAccessGroup: URI name - skills
	@Test
	public void test_01_12() {
		String cfgObjectURL = gwsConfigURL + "/skills";
		String skillName1 = "TEST_SKILL_1";
		String skillName2 = "TEST_SKILL_2";

		JsonObject skillBody = new JsonObject();
		skillBody.addProperty("name", skillName1);
		skillBody.addProperty("tenantDBID", "1");
		skillBody.addProperty("state", "1");

		JsonObject requestBody = new JsonObject();
		requestBody.add("skill", skillBody);

		// System.out.println("requestBody " + requestBody);

		Response r = given().auth().preemptive().basic(username, password).contentType(ContentType.JSON)
				.body(requestBody.toString()).post(cfgObjectURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r.getStatusCode());

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		String skillId = responseBodyObject.get("id").getAsString();
		String skillURL = responseBodyObject.get("uri").getAsString();

		JsonObject updateBody = new JsonObject();
		JsonObject deltaSkillBody = new JsonObject();
		JsonObject cfgSkillBody = new JsonObject();

		cfgSkillBody.addProperty("name", skillName2);
		cfgSkillBody.addProperty("DBID", skillId);

		deltaSkillBody.add("CfgSkill", cfgSkillBody);
		updateBody.add("delta-skill", deltaSkillBody);

		Response r2 = given().auth().preemptive().basic(username, password).contentType(ContentType.JSON)
				.body(updateBody.toString()).put(cfgObjectURL);

		String responseBody2 = r2.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r2.getStatusCode());

		JsonObject responseBodyObject2 = new JsonParser().parse(responseBody2).getAsJsonObject();

		// System.out.println(responseBodyObject.get("statusCode").getAsString());

		assertTrue(responseBodyObject2.get("statusCode").getAsString().equals("0"));

		Response r3 = given().auth().preemptive().basic(username, password).get(skillURL);

		String responseBody3 = r3.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r3.getStatusCode());

		JsonObject responseBodyObject3 = new JsonParser().parse(responseBody3).getAsJsonObject();

		// System.out.println(responseBodyObject.get("statusCode").getAsString());

		assertTrue(responseBodyObject3.get("statusCode").getAsString().equals("0"));

		JsonObject skill = responseBodyObject3.get("skill").getAsJsonObject();

		assertTrue(skill.get("state").getAsString().equals("1"));
		assertTrue(skill.get("tenantDBID").getAsString().equals("1"));
		assertTrue(skill.get("name").getAsString().equals(skillName2));

		Response r4 = given().auth().preemptive().basic(username, password).contentType(ContentType.JSON)
				.delete(skillURL);

		// String responseBody3 = r3.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r4.getStatusCode());
	}

	// Test 13. Update configuration object directly in Config Server
	// Bad DBID
	// CfgAccessGroup: URI name - skills
	@Test
	public void test_01_13() {
		String cfgObjectURL = gwsConfigURL + "/skills";
		String skillName1 = "TEST_SKILL_1";
		String skillName2 = "TEST_SKILL_2";

		JsonObject skillBody = new JsonObject();
		skillBody.addProperty("name", skillName1);
		skillBody.addProperty("tenantDBID", "1");
		skillBody.addProperty("state", "1");

		JsonObject requestBody = new JsonObject();
		requestBody.add("skill", skillBody);

		// System.out.println("requestBody " + requestBody);

		Response r = given().auth().preemptive().basic(username, password).contentType(ContentType.JSON)
				.body(requestBody.toString()).post(cfgObjectURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r.getStatusCode());

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		String skillId = responseBodyObject.get("id").getAsString() + "1";
		String skillURL = responseBodyObject.get("uri").getAsString();

		JsonObject updateBody = new JsonObject();
		JsonObject deltaSkillBody = new JsonObject();
		JsonObject cfgSkillBody = new JsonObject();

		cfgSkillBody.addProperty("name", skillName2);
		cfgSkillBody.addProperty("DBID", skillId);

		deltaSkillBody.add("CfgSkill", cfgSkillBody);
		updateBody.add("delta-skill", deltaSkillBody);

		Response r2 = given().auth().preemptive().basic(username, password).contentType(ContentType.JSON)
				.body(updateBody.toString()).put(cfgObjectURL);

		String responseBody2 = r2.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(500, r2.getStatusCode());

		JsonObject responseBodyObject2 = new JsonParser().parse(responseBody2).getAsJsonObject();

		// System.out.println(responseBodyObject.get("statusCode").getAsString());

		assertTrue(responseBodyObject2.get("statusCode").getAsString().equals("4"));
		assertTrue(responseBodyObject2.get("statusMessage").getAsString()
				.equals("Platform exception: No object found for the delta [CfgSkillUpdate], DBID [" + skillId + "]"));
		assertTrue(responseBodyObject2.get("platformCode").getAsString().equals("10"));

		Response r3 = given().auth().preemptive().basic(username, password).get(skillURL);

		String responseBody3 = r3.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r3.getStatusCode());

		JsonObject responseBodyObject3 = new JsonParser().parse(responseBody3).getAsJsonObject();

		// System.out.println(responseBodyObject.get("statusCode").getAsString());

		assertTrue(responseBodyObject3.get("statusCode").getAsString().equals("0"));

		JsonObject skill = responseBodyObject3.get("skill").getAsJsonObject();

		assertTrue(skill.get("state").getAsString().equals("1"));
		assertTrue(skill.get("tenantDBID").getAsString().equals("1"));
		assertTrue(skill.get("name").getAsString().equals(skillName1));

		Response r4 = given().auth().preemptive().basic(username, password).contentType(ContentType.JSON)
				.delete(skillURL);

		// String responseBody3 = r3.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r4.getStatusCode());

	}

	// Test 14. Delete configuration object directly from Config Server
	// CfgAccessGroup: URI name - skills
	@Test
	public void test_01_14() {
		String cfgObjectURL = gwsConfigURL + "/skills";
		String skillName1 = "TEST_SKILL_1";

		JsonObject skillBody = new JsonObject();
		skillBody.addProperty("name", skillName1);
		skillBody.addProperty("tenantDBID", "1");
		skillBody.addProperty("state", "1");

		JsonObject requestBody = new JsonObject();
		requestBody.add("skill", skillBody);

		// System.out.println("requestBody " + requestBody);

		Response r = given().auth().preemptive().basic(username, password).contentType(ContentType.JSON)
				.body(requestBody.toString()).post(cfgObjectURL);

		String responseBody = r.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r.getStatusCode());

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		String skillURL = responseBodyObject.get("uri").getAsString();

		Response r3 = given().auth().preemptive().basic(username, password).get(skillURL);

		String responseBody3 = r3.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(200, r3.getStatusCode());

		JsonObject responseBodyObject3 = new JsonParser().parse(responseBody3).getAsJsonObject();

		// System.out.println(responseBodyObject.get("statusCode").getAsString());

		assertTrue(responseBodyObject3.get("statusCode").getAsString().equals("0"));

		JsonObject skill = responseBodyObject3.get("skill").getAsJsonObject();

		assertTrue(skill.get("state").getAsString().equals("1"));
		assertTrue(skill.get("tenantDBID").getAsString().equals("1"));
		assertTrue(skill.get("name").getAsString().equals(skillName1));

		Response r4 = given().auth().preemptive().basic(username, password).contentType(ContentType.JSON)
				.delete(skillURL);

		assertEquals(200, r4.getStatusCode());

		Response r5 = given().auth().preemptive().basic(username, password).get(skillURL);

		String responseBody5 = r5.asString();

		// System.out.println("response body: " + responseBody);

		assertEquals(404, r5.getStatusCode());

		JsonObject responseBodyObject5 = new JsonParser().parse(responseBody5).getAsJsonObject();

		// System.out.println(responseBodyObject.get("statusCode").getAsString());

		assertTrue(responseBodyObject5.get("statusCode").getAsString().equals("6"));
		assertTrue(responseBodyObject5.get("statusMessage").getAsString().equals("Resource not found"));

	}

	// Test 15. Delete configuration object directly from Config Server
	// Bad DBID
	// CfgAccessGroup: URI name - skills
	@Test
	public void test_01_15() {
		String cfgObjectURL = gwsConfigURL + "/skills";
		String badSkillBDID = "99999";
		String badSkillURL = cfgObjectURL + "/" + badSkillBDID;

		Response r = given().auth().preemptive().basic(username, password).contentType(ContentType.JSON)
				.delete(badSkillURL);

		String responseBody = r.asString();

		assertEquals(500, r.getStatusCode());

		JsonObject responseBodyObject = new JsonParser().parse(responseBody).getAsJsonObject();

		// System.out.println(responseBodyObject.get("statusCode").getAsString());

		assertTrue(responseBodyObject.get("statusCode").getAsString().equals("4"));
		assertTrue(
				responseBodyObject.get("statusMessage").getAsString().equals("Platform exception: Object not found"));
		assertTrue(responseBodyObject.get("platformCode").getAsString().equals("10"));

	}
}
