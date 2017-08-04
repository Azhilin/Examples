package gms.tests;

import com.genesyslab.ats.cfg.actor.CfgManager;
import com.genesyslab.platform.commons.collections.KeyValueCollection;
import com.jayway.restassured.response.Response;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileInputStream;
import java.util.Properties;

import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertyConfiguration;
import static com.genesyslab.functional.tests.gms.helper.ServiceAPI.*;
import static org.junit.Assert.*;

public class ClusterTests {
	private static String callbackServiceName = "cb_new";
	private static String phoneNumber="5115";
	
	private static CfgManager cfgManager = new CfgManager();
	
	private static String gmsClusterAppName = getPropertyConfiguration("gms.cluster.app.name");
	private static String gmsAppName = getPropertyConfiguration("gms.app.name");
	private static String gmsApp2Name = getPropertyConfiguration("gms.app2.name");
	private static String gmsHost = getPropertyConfiguration("gms.host");
	private static String gms2Host = getPropertyConfiguration("gms2.host");
	private static String gmsPort = getPropertyConfiguration("gms.port");
	private static String gms2Port = getPropertyConfiguration("gms2.port");
	private static String baseUrl1="http://"+gmsHost+":"+gmsPort+"/";
	private static String baseUrl2="http://"+gms2Host+":"+gms2Port+"/";
	
	private static String accessCodePrefix1="9";
	private static String accessCodePrefix2="8";
	private static String resourceGroup="DNIS";
	
	//private static String gmsClusterAppName = getPropertyConfiguration("gms.cluster.app.name");
	//private static String orsAppName = getPropertyConfiguration("ors.app.name");
	
	//private static SCSManager scsManager = new SCSManager(cfgManager);
	
	private static Properties properties = new Properties();
	

	@BeforeClass
	public static void oneTimeSetUp() {
		
//		long startTime = System.nanoTime();
//		
//		try {
//			properties.load(new FileInputStream("./config.properties"));
//			//scsManager.init(properties);
//			cfgManager.init(properties);
//			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi()
//					.addSection("service." + callbackServiceName, getCallbackServiceOptions());
//			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().addSection("callback",
//					getGMSCallbackSectionOptions());
//			//scsManager.restartApplication(gmsAppName);
//			//scsManager.restartApplication(orsAppName);
//			System.out.println("@BeforeClass method processing...");
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		long endTime = System.nanoTime();
//		getMethodExecutionTime(startTime, endTime, "@BeforeClass");
		setServerSection(false);
		setServicesSections();
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		
	//	cfgManager.deactivate();
		
	}

	@After
	public void tearDown() {
		System.out.print("@After method processing...: ");
		
	}
	
	//****************************************************************************************
	// 
	
	
	/**
	 * Cross-node interaction seed, non-seed
	 * 
	 */
	
	@Test
	public void test_01_01()  {

		Response response = requestInteraction(baseUrl1);
		
		assertEquals(200, response.getStatusCode());
		String accessCode =response.jsonPath().get("_access_code");
		assertNotNull(accessCode);
		String accessNumber =response.jsonPath().get("_access_number");
		assertNotNull(accessNumber);
		String expirationTime =response.jsonPath().get("_expiration_time");
		assertNotNull(expirationTime);
		String id =response.jsonPath().get("_id");
		assertNotNull(id);
		
		response = requestMatchInteraction(baseUrl2,accessNumber);
		
		assertEquals(200, response.getStatusCode());
		String dataId =response.jsonPath().get("_data_id");
		assertNotNull(dataId);
		String id2 =response.jsonPath().get("_id");
		assertNotNull(id);
		
		
		assertEquals(id, id2);
		
	}
	
	/**
	 * Cross-node interaction non-seed, seed
	 */
	@Test
	public void test_01_02()  {

		Response response = requestInteraction(baseUrl2);
		
		assertEquals(200, response.getStatusCode());
		String accessCode =response.jsonPath().get("_access_code");
		assertNotNull(accessCode);
		String accessNumber =response.jsonPath().get("_access_number");
		assertNotNull(accessNumber);
		String expirationTime =response.jsonPath().get("_expiration_time");
		assertNotNull(expirationTime);
		String id =response.jsonPath().get("_id");
		assertNotNull(id);
		
		response = requestMatchInteraction(baseUrl1,accessNumber);
		
		assertEquals(200, response.getStatusCode());
		String dataId =response.jsonPath().get("_data_id");
		assertNotNull(dataId);
		String id2 =response.jsonPath().get("_id");
		assertNotNull(id);
		
		
		assertEquals(id, id2);
		
	}
	
	/**
	 * Cross-node interaction seed, request-access to non-seed
	 */
	
	@Test
	public void test_01_03()  {

		Response response = requestInteraction(baseUrl1);
		
		assertEquals(200, response.getStatusCode());
		String accessCode =response.jsonPath().get("_access_code");
		assertNotNull(accessCode);
		String accessNumber =response.jsonPath().get("_access_number");
		assertNotNull(accessNumber);
		String expirationTime =response.jsonPath().get("_expiration_time");
		assertNotNull(expirationTime);
		String id =response.jsonPath().get("_id");
		assertNotNull(id);
		
		response = requestAccess(baseUrl2, id, resourceGroup);
		
		assertEquals(200, response.getStatusCode());
		accessNumber =response.jsonPath().get("_access_number");
		assertNotNull(accessNumber);
		expirationTime =response.jsonPath().get("_expiration_time");
		assertNotNull(expirationTime);		
		
	}
	

	/**
	 * Enhance access number access_code_prefix not set
	 * 
	 */
	
	@Test
	public void test_01_04()  {

		Response response = requestInteraction(baseUrl1);
		
		assertEquals(200, response.getStatusCode());
		String accessCode =response.jsonPath().get("_access_code");
		assertNotNull(accessCode);
		String accessNumber =response.jsonPath().get("_access_number");
		assertNotNull(accessNumber);
		String expirationTime =response.jsonPath().get("_expiration_time");
		assertNotNull(expirationTime);
		String id =response.jsonPath().get("_id");
		assertNotNull(id);
		
		assertTrue(!accessCode.startsWith(accessCodePrefix1));
		
		//node2
		response = requestInteraction(baseUrl2);
		
		assertEquals(200, response.getStatusCode());
		accessCode =response.jsonPath().get("_access_code");
		assertNotNull(accessCode);
		accessNumber =response.jsonPath().get("_access_number");
		assertNotNull(accessNumber);
		expirationTime =response.jsonPath().get("_expiration_time");
		assertNotNull(expirationTime);
		id =response.jsonPath().get("_id");
		assertNotNull(id);
		
		assertTrue(!accessCode.startsWith(accessCodePrefix2));
		
	}
	
	/**
	 * Enhance access number access_code_prefix set
	 * 
	 */
	
	@Test
	public void test_01_05()  {
		setServerSection(true);

		Response response = requestInteraction(baseUrl1);
		
		assertEquals(200, response.getStatusCode());
		String accessCode =response.jsonPath().get("_access_code");
		assertNotNull(accessCode);
		String accessNumber =response.jsonPath().get("_access_number");
		assertNotNull(accessNumber);
		String expirationTime =response.jsonPath().get("_expiration_time");
		assertNotNull(expirationTime);
		String id =response.jsonPath().get("_id");
		assertNotNull(id);
		assertTrue(accessCode.startsWith(accessCodePrefix1));
		
		//node2
		response = requestInteraction(baseUrl2);
		
		assertEquals(200, response.getStatusCode());
		accessCode =response.jsonPath().get("_access_code");
		assertNotNull(accessCode);
		accessNumber =response.jsonPath().get("_access_number");
		assertNotNull(accessNumber);
		expirationTime =response.jsonPath().get("_expiration_time");
		assertNotNull(expirationTime);
		id =response.jsonPath().get("_id");
		assertNotNull(id);
		
		assertTrue(accessCode.startsWith(accessCodePrefix2));
		
	}
	
	/**
	 * Enhance access number access_code_prefix set to time range
	 * 
	 */
	
	@Test
	public void test_01_06()  {
		accessCodePrefix1="7-9";
		accessCodePrefix2="5-7";
		setServerSection(true);

		Response response = requestInteraction(baseUrl1);
		
		assertEquals(200, response.getStatusCode());
		String accessCode =response.jsonPath().get("_access_code");
		assertNotNull(accessCode);
		String accessNumber =response.jsonPath().get("_access_number");
		assertNotNull(accessNumber);
		String expirationTime =response.jsonPath().get("_expiration_time");
		assertNotNull(expirationTime);
		String id =response.jsonPath().get("_id");
		assertNotNull(id);
		System.out.println(accessCode+"prefix "+accessCodePrefix1);
		assertTrue(accessCode.startsWith("7")||accessCode.startsWith("8")||accessCode.startsWith("9"));
		
		//node2
		response = requestInteraction(baseUrl2);
		
		assertEquals(200, response.getStatusCode());
		accessCode =response.jsonPath().get("_access_code");
		assertNotNull(accessCode);
		accessNumber =response.jsonPath().get("_access_number");
		assertNotNull(accessNumber);
		expirationTime =response.jsonPath().get("_expiration_time");
		assertNotNull(expirationTime);
		id =response.jsonPath().get("_id");
		assertNotNull(id);
		
		assertTrue(accessCode.startsWith("5")||accessCode.startsWith("6")||accessCode.startsWith("7"));
		
	}
	
	/**
	 * Enhance access number 999999 and 888888
	 */
	@Test
	public void test_01_07()  {
		accessCodePrefix1="999999";
		accessCodePrefix2="888888";
		setServerSection(true);

		Response response = requestInteraction(baseUrl1);
		
		assertEquals(500, response.getStatusCode());
		String message =response.jsonPath().get("message");		
		assertEquals("n must be positive", message);
		String exception =response.jsonPath().get("exception");
		assertEquals("java.lang.IllegalArgumentException",exception);
		
		
		//node2
		response = requestInteraction(baseUrl2);
		
		assertEquals(500, response.getStatusCode());
		message =response.jsonPath().get("message");		
		assertEquals("n must be positive", message);
		exception =response.jsonPath().get("exception");
		assertEquals("java.lang.IllegalArgumentException",exception);
		
	}
	/**
	 * Enhance access number 9999999999 and 8888888888
	 */
	@Test
	public void test_01_08()  {
		accessCodePrefix1="9999999999";
		accessCodePrefix2="8888888888";
		setServerSection(true);

		Response response = requestInteraction(baseUrl1);
		
		assertEquals(500, response.getStatusCode());
		String message =response.jsonPath().get("message");		
		assertEquals("For input string: \""+accessCodePrefix1+"\"", message);
		String exception =response.jsonPath().get("exception");
		assertEquals("java.lang.NumberFormatException",exception);
		
		
		//node2
		response = requestInteraction(baseUrl2);
		
		assertEquals(500, response.getStatusCode());
		message =response.jsonPath().get("message");		
		assertEquals("For input string: \""+accessCodePrefix2+"\"", message);
		exception =response.jsonPath().get("exception");
		assertEquals("java.lang.NumberFormatException",exception);
		
	}
	
	
		
	private static void setServerSection(boolean accessCodePrefixSet){
		
		try {
			properties.load(new FileInputStream("./config.properties"));
			//scsManager.init(properties);
			cfgManager.init(properties);
			
			
			//node1
			KeyValueCollection collection = new KeyValueCollection();		
			if (accessCodePrefixSet){
				collection.addString("access_code_prefix", accessCodePrefix1);
			}
			collection.addString("external_url_base", baseUrl1);
			collection.addString("node_id", "1");
			collection.addString("web_port", gmsPort);
			
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi()
					.addSection("server", collection);
			
			//node2
			collection = new KeyValueCollection();
			if (accessCodePrefixSet){
				collection.addString("access_code_prefix", accessCodePrefix2);
			}
			collection.addString("external_url_base", baseUrl2);
			collection.addString("node_id", "1");
			collection.addString("web_port", gmsPort);
			
			cfgManager.getAppApi().setApp(gmsApp2Name).getOptionsApi()
					.addSection("server", collection);			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			cfgManager.deactivate();
		}
		
	}
	

	
	private static void setServicesSections(){
		
		try {
			properties.load(new FileInputStream("./config.properties"));
			//scsManager.init(properties);
			cfgManager.init(properties);
			
			//cluster application
			KeyValueCollection collection = new KeyValueCollection();		
			collection.addString("_provide_code", "true");
			collection.addString("_resource_group", resourceGroup);
			collection.addString("_return_pool_health", "false");
			collection.addString("_service", "request-interaction");
			collection.addString("_ttl", "60");
			collection.addString("_type", "builtin");
			
			cfgManager.getAppApi().setApp(gmsClusterAppName).getOptionsApi()
					.addSection("service.request-interaction", collection);
			//--------------------------------------------------------------
			collection = new KeyValueCollection();
			collection.addString("_service", "match-interaction");	
			collection.addString("_type", "builtin");
			
			cfgManager.getAppApi().setApp(gmsClusterAppName).getOptionsApi()
					.addSection("service.match-interaction", collection);	
			//--------------------------------------------------------------
			
			collection = new KeyValueCollection();
			collection.addString("_resource_group", resourceGroup);	
			collection.addString("_service", "request-access");
			collection.addString("_ttl", "60");
			collection.addString("_type", "builtin");
			collection.addString("_access_code_length","4");
			
			cfgManager.getAppApi().setApp(gmsClusterAppName).getOptionsApi()
					.addSection("service.request-access", collection);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			cfgManager.deactivate();
		}
		
	}
	


}
