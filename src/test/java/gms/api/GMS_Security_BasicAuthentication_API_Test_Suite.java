package gms.api;

import com.genesyslab.ats.cfg.actor.CfgManager;
import com.genesyslab.ats.ex.AtsCfgComponentException;
import com.genesyslab.functional.tests.gms.psdk.Reconfiguration;
import com.genesyslab.scsmanager.SCSManager;
import com.jayway.restassured.response.Response;
import org.junit.*;

import java.io.FileInputStream;
import java.util.Properties;

import static com.genesyslab.functional.tests.gms.helper.ServiceOptions.getCallbackOrigServiceOptions;
import static com.genesyslab.functional.tests.gms.helper.ServiceOptions.getChatServiceOptions;
import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

/*
 * [Test suite] Security. Basic Authentication https://jira.genesys.com/browse/GMS-2522
 */

public class GMS_Security_BasicAuthentication_API_Test_Suite {
	private static String gmsHost;
	private static String gmsPort;

	private static String chatServiceName = "Chat_immediate_ba";
	private static String callbackServiceName = "user_orig_immediate_ba";
	private String content_type = "application/x-www-form-urlencoded";
	private String requestBody = "_subject=efg&_customer_number=5115";

	private String chatServiceURL = "http://" + gmsHost + ":" + gmsPort + "/genesys/1/service/" + chatServiceName;
	private String callbackServiceURL = "http://" + gmsHost + ":" + gmsPort + "/genesys/1/service/callback/"
			+ callbackServiceName;
	private String storageServiceURL = "http://" + gmsHost + ":" + gmsPort + "/genesys/1/storage/60";

	private static String chatSectionName = "service." + chatServiceName;
	private static String callbackSectionName = "service." + callbackServiceName;

	private String usernameServerSection = "general";
	private String passwordServerSection = "password";
	private String usernameServiceSection = "genesys";
	private String passwordServiceSection = "qwerty";

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

			scsManager.init(properties);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().addSection(callbackSectionName,
					getCallbackOrigServiceOptions());
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().addSection(chatSectionName,
					getChatServiceOptions());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void oneTimeTearDown() {

		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(chatSectionName).deleteSection();
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName).deleteSection();
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").deleteOption("username");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").deleteOption("password");
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

	/**
	 * GMS-2522 [Test suite] Security. Basic Authentication Check for Storage
	 * service. Username and password set for "server" section. Positive
	 */
	@Test
	public void test_01_01() {
		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("username",
					usernameServerSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("password",
					passwordServerSection);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().auth().basic(usernameServerSection, passwordServerSection).contentType(content_type)
				.body(requestBody).post(storageServiceURL);

		assertEquals(200, r.getStatusCode());

	}

	/**
	 * GMS-2522 [Test suite] Security. Basic Authentication Check for Storage
	 * service. Username and password set for "server" section. Negative
	 */
	@Test
	public void test_01_02() {
		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("username",
					usernameServerSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("password",
					passwordServerSection);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().auth().basic(usernameServerSection + "1", passwordServerSection).contentType(content_type)
				.body(requestBody).post(storageServiceURL);

		assertEquals(401, r.getStatusCode());

	}

	/**
	 * GMS-2522 [Test suite] Security. Basic Authentication Check for Chat
	 * service. Username and password set for "server" section. Positive
	 */
	@Test
	public void test_01_03() {
		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("username",
					usernameServerSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("password",
					passwordServerSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(chatSectionName)
					.deleteOption("username");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(chatSectionName)
					.deleteOption("password");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().auth().basic(usernameServerSection, passwordServerSection).contentType(content_type)
				.body(requestBody).post(chatServiceURL);

		assertEquals(200, r.getStatusCode());

	}

	/**
	 * GMS-2522 [Test suite] Security. Basic Authentication Check for Chat
	 * service. Username and password set for "server" section. Negative
	 */
	@Test
	public void test_01_04() {
		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("username",
					usernameServerSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("password",
					passwordServerSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(chatSectionName)
					.deleteOption("username");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(chatSectionName)
					.deleteOption("password");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().auth().basic(usernameServerSection + "1", passwordServerSection).contentType(content_type)
				.body(requestBody).post(chatServiceURL);

		assertEquals(401, r.getStatusCode());

	}

	/**
	 * GMS-2522 [Test suite] Security. Basic Authentication Check for CallBack
	 * service. Username and password set for "server" section. Positive
	 */
	@Test
	public void test_01_05() {
		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("username",
					usernameServerSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("password",
					passwordServerSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.deleteOption("username");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.deleteOption("password");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().auth().basic(usernameServerSection, passwordServerSection).contentType(content_type)
				.body(requestBody).post(callbackServiceURL);

		assertEquals(200, r.getStatusCode());

	}

	/**
	 * GMS-2522 [Test suite] Security. Basic Authentication Check for CallBack
	 * service. Username and password set for "server" section. Negative
	 */
	@Test
	public void test_01_06() {
		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("username",
					usernameServerSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("password",
					passwordServerSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.deleteOption("username");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.deleteOption("password");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().auth().basic(usernameServerSection + "1", passwordServerSection).contentType(content_type)
				.body(requestBody).post(callbackServiceURL);

		assertEquals(401, r.getStatusCode());

	}

	/**
	 * GMS-2522 [Test suite] Security. Basic Authentication Check for Chat
	 * service. Username and password set for "service" section. Positive
	 */
	@Test
	public void test_01_07() {
		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(chatSectionName).addOption("username",
					usernameServiceSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(chatSectionName).addOption("password",
					passwordServiceSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").deleteOption("username");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").deleteOption("password");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().auth().basic(usernameServiceSection, passwordServiceSection).contentType(content_type)
				.body(requestBody).post(chatServiceURL);

		assertEquals(200, r.getStatusCode());

	}

	/**
	 * GMS-2522 [Test suite] Security. Basic Authentication Check for Chat
	 * service. Username and password set for "service" section. Negative
	 */
	@Test
	public void test_01_08() {
		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(chatSectionName).addOption("username",
					usernameServiceSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(chatSectionName).addOption("password",
					passwordServiceSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").deleteOption("username");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").deleteOption("password");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().auth().basic(usernameServiceSection + "1", passwordServiceSection)
				.contentType(content_type).body(requestBody).post(chatServiceURL);

		assertEquals(401, r.getStatusCode());

	}

	/**
	 * GMS-2522 [Test suite] Security. Basic Authentication Check for Chat
	 * service. Username and password set for "server" section and for "service"
	 * section. Positive
	 */
	@Test
	public void test_01_09() {
		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(chatSectionName).addOption("username",
					usernameServiceSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(chatSectionName).addOption("password",
					passwordServiceSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("username",
					usernameServerSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("password",
					usernameServerSection);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().auth().basic(usernameServiceSection, passwordServiceSection).contentType(content_type)
				.body(requestBody).post(chatServiceURL);

		assertEquals(200, r.getStatusCode());

	}

	/**
	 * GMS-2522 [Test suite] Security. Basic Authentication Check for Chat
	 * service. Username and password set for "server" section and for "service"
	 * section. Negative
	 */
	@Test
	public void test_01_10() {
		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(chatSectionName).addOption("username",
					usernameServiceSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(chatSectionName).addOption("password",
					passwordServiceSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("username",
					usernameServerSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("password",
					usernameServerSection);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().auth().basic(usernameServerSection, passwordServerSection).contentType(content_type)
				.body(requestBody).post(chatServiceURL);

		assertEquals(401, r.getStatusCode());

	}

	/**
	 * GMS-2522 [Test suite] Security. Basic Authentication Check for Callback
	 * service. Username and password set for "service" section. Positive
	 */
	@Test
	public void test_01_11() {
		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.addOption("username", usernameServiceSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.addOption("password", passwordServiceSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").deleteOption("username");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").deleteOption("password");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().auth().basic(usernameServiceSection, passwordServiceSection).contentType(content_type)
				.body(requestBody).post(callbackServiceURL);

		assertEquals(200, r.getStatusCode());

	}

	/**
	 * GMS-2522 [Test suite] Security. Basic Authentication Check for Callback
	 * service. Username and password set for "service" section. Negative
	 */
	@Test
	public void test_01_12() {
		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.addOption("username", usernameServiceSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.addOption("password", passwordServiceSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").deleteOption("username");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").deleteOption("password");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().auth().basic(usernameServiceSection + "1", passwordServiceSection)
				.contentType(content_type).body(requestBody).post(callbackServiceURL);

		assertEquals(401, r.getStatusCode());

	}

	/**
	 * GMS-2522 [Test suite] Security. Basic Authentication Check for Callback
	 * service. Username and password set for "server" section and for "service"
	 * section. Positive
	 */
	@Test
	public void test_01_13() {
		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.addOption("username", usernameServiceSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.addOption("password", passwordServiceSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("username",
					usernameServerSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("password",
					passwordServiceSection);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().auth().basic(usernameServiceSection, passwordServiceSection).contentType(content_type)
				.body(requestBody).post(callbackServiceURL);

		assertEquals(200, r.getStatusCode());

	}

	/**
	 * GMS-2522 [Test suite] Security. Basic Authentication Check for Callback
	 * service. Username and password set for "server" section and for "service"
	 * section. Negative
	 */
	@Test
	public void test_01_14() {
		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.addOption("username", usernameServiceSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.addOption("password", passwordServiceSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("username",
					usernameServerSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("password",
					passwordServiceSection);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().auth().basic(usernameServerSection, passwordServerSection).contentType(content_type)
				.body(requestBody).post(callbackServiceURL);

		assertEquals(401, r.getStatusCode());

	}

	/**
	 * GMS-2522 [Test suite] Security. Basic Authentication Check for Chat
	 * service. Username set for "server" section. Positive
	 */
	@Test
	public void test_01_15() {
		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(chatSectionName)
					.deleteOption("username");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(chatSectionName)
					.deleteOption("password");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("username",
					usernameServerSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").deleteOption("password");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().contentType(content_type).body(requestBody).post(chatServiceURL);

		assertEquals(200, r.getStatusCode());

	}

	/**
	 * GMS-2522 [Test suite] Security. Basic Authentication Check for Callback
	 * service. Username set for "server" section. Positive
	 */
	@Test
	public void test_01_16() {
		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.deleteOption("username");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.deleteOption("password");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("username",
					usernameServerSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").deleteOption("password");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().contentType(content_type).body(requestBody).post(callbackServiceURL);

		assertEquals(200, r.getStatusCode());

	}

	/**
	 * GMS-2522 [Test suite] Security. Basic Authentication Check for Chat
	 * service. Username set for "service" section. Positive
	 */
	@Test
	public void test_01_17() {
		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(chatSectionName).addOption("username",
					usernameServiceSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(chatSectionName)
					.deleteOption("password");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").deleteOption("username");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").deleteOption("password");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().contentType(content_type).body(requestBody).post(chatServiceURL);

		assertEquals(200, r.getStatusCode());

	}

	/**
	 * GMS-2522 [Test suite] Security. Basic Authentication Check for Callback
	 * service. Username set for "service" section. Positive
	 */
	@Test
	public void test_01_18() {
		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.addOption("username", usernameServiceSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.deleteOption("password");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").deleteOption("username");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").deleteOption("password");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().contentType(content_type).body(requestBody).post(callbackServiceURL);

		assertEquals(200, r.getStatusCode());

	}

	/**
	 * GMS-2522 [Test suite] Security. Basic Authentication Check for Chat
	 * service. Password set for "server" section. Positive
	 */
	@Test
	public void test_01_19() {
		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(chatSectionName)
					.deleteOption("username");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(chatSectionName)
					.deleteOption("password");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").deleteOption("username");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("password",
					passwordServerSection);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().contentType(content_type).body(requestBody).post(chatServiceURL);

		assertEquals(401, r.getStatusCode());

	}

	/**
	 * GMS-2522 [Test suite] Security. Basic Authentication Check for Callback
	 * service. Password set for "server" section. Positive
	 */
	@Test
	public void test_01_20() {
		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.deleteOption("username");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.deleteOption("password");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").deleteOption("username");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("password",
					passwordServerSection);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().contentType(content_type).body(requestBody).post(callbackServiceURL);

		assertEquals(401, r.getStatusCode());

	}

	/**
	 * GMS-2522 [Test suite] Security. Basic Authentication Check for Chat
	 * service. Password set for "service" section. Positive
	 */
	@Test
	public void test_01_21() {
		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(chatSectionName)
					.deleteOption("username");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(chatSectionName).addOption("password",
					passwordServiceSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").deleteOption("username");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").deleteOption("password");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().contentType(content_type).body(requestBody).post(chatServiceURL);

		assertEquals(401, r.getStatusCode());

	}

	/**
	 * GMS-2522 [Test suite] Security. Basic Authentication Check for Callback
	 * service. Password set for "service" section. Positive
	 */
	@Test
	public void test_01_22() {
		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.deleteOption("username");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.addOption("password", passwordServiceSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").deleteOption("username");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").deleteOption("password");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().contentType(content_type).body(requestBody).post(callbackServiceURL);

		assertEquals(401, r.getStatusCode());

	}

	/**
	 * GMS-2522 [Test suite] Security. Basic Authentication Check for Chat
	 * service. Username set for "server" section and password set for "service"
	 * section. Positive
	 */
	@Test
	public void test_01_23() {
		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(chatSectionName)
					.deleteOption("username");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(chatSectionName).addOption("password",
					passwordServiceSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("username",
					usernameServerSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").deleteOption("password");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().auth().basic(usernameServerSection, passwordServiceSection).contentType(content_type)
				.body(requestBody).post(chatServiceURL);

		assertEquals(200, r.getStatusCode());

	}

	/**
	 * GMS-2522 [Test suite] Security. Basic Authentication Check for CallBack
	 * service. Username set for "server" section and password set for "service"
	 * section. Positive
	 */
	@Test
	public void test_01_24() {
		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.deleteOption("username");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.addOption("password", passwordServiceSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("username",
					usernameServerSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").deleteOption("password");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().auth().basic(usernameServerSection, passwordServiceSection).contentType(content_type)
				.body(requestBody).post(callbackServiceURL);

		assertEquals(200, r.getStatusCode());

	}

	/**
	 * GMS-2522 [Test suite] Security. Basic Authentication Check for Chat
	 * service. Username and password set for "server" section and password set
	 * for "service" section. Positive
	 */
	@Test
	public void test_01_25() {
		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(chatSectionName)
					.deleteOption("username");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(chatSectionName).addOption("password",
					passwordServiceSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("username",
					usernameServerSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("password",
					passwordServerSection);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Reconfiguration reconfiguration = new Reconfiguration();
		reconfiguration.openConnectionToConfig();

		Response r = given().auth().basic(usernameServerSection, passwordServiceSection).contentType(content_type)
				.body(requestBody).post(chatServiceURL);

		assertEquals(200, r.getStatusCode());

	}

	/**
	 * GMS-2522 [Test suite] Security. Basic Authentication Check for CallBack
	 * service. Username and password set for "server" section and password set
	 * for "service" section. Positive
	 */
	@Test
	public void test_01_26() {
		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.deleteOption("username");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.addOption("password", passwordServiceSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("username",
					usernameServerSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("password",
					passwordServerSection);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().auth().basic(usernameServerSection, passwordServiceSection).contentType(content_type)
				.body(requestBody).post(callbackServiceURL);

		assertEquals(200, r.getStatusCode());

	}

	/**
	 * GMS-2522 [Test suite] Security. Basic Authentication Check for Chat
	 * service. Username and password set for "server" section and username set
	 * for "service" section. Positive
	 */
	@Test
	public void test_01_27() {
		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(chatSectionName).addOption("username",
					usernameServiceSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(chatSectionName)
					.deleteOption("password");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("username",
					usernameServerSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("password",
					passwordServerSection);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().auth().basic(usernameServiceSection, passwordServerSection).contentType(content_type)
				.body(requestBody).post(chatServiceURL);

		assertEquals(200, r.getStatusCode());

	}

	/**
	 * GMS-2522 [Test suite] Security. Basic Authentication Check for CallBack
	 * service. Username and password set for "server" section and username set
	 * for "service" section. Positive
	 */
	@Test
	public void test_01_28() {
		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.addOption("username", usernameServiceSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.deleteOption("password");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("username",
					usernameServerSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("password",
					passwordServerSection);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().auth().basic(usernameServiceSection, passwordServerSection).contentType(content_type)
				.body(requestBody).post(callbackServiceURL);

		assertEquals(200, r.getStatusCode());

	}

	/**
	 * GMS-2522 [Test suite] Security. Basic Authentication Check for Chat
	 * service. Password set for "server" section and username set for "service"
	 * section. Positive
	 */
	@Test
	public void test_01_29() {
		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(chatSectionName).addOption("username",
					usernameServiceSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(chatSectionName)
					.deleteOption("password");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").deleteOption("username");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("password",
					passwordServerSection);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().auth().basic(usernameServiceSection, passwordServerSection).contentType(content_type)
				.body(requestBody).post(chatServiceURL);

		assertEquals(200, r.getStatusCode());

	}

	/**
	 * GMS-2522 [Test suite] Security. Basic Authentication Check for CallBack
	 * service. Password set for "server" section and username set for "service"
	 * section. Positive
	 */
	@Test
	public void test_01_30() {
		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.addOption("username", usernameServiceSection);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSectionName)
					.deleteOption("password");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").deleteOption("username");
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("server").addOption("password",
					passwordServerSection);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = given().auth().basic(usernameServiceSection, passwordServerSection).contentType(content_type)
				.body(requestBody).post(callbackServiceURL);

		assertEquals(500, r.getStatusCode());

	}

}
