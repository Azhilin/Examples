package gms.tests;

//import static com.genesyslab.functional.tests.gms.helper.ContextServices.constructPurgeRequestBody;

import com.genesyslab.functional.tests.gms.files.WorkWithResources;
import com.genesyslab.functional.tests.gms.helper.ContextServices;
import com.genesyslab.functional.tests.gms.psdk.Reconfiguration;
import com.genesyslab.platform.applicationblocks.com.ConfigException;
import io.restassured.response.Response;
import org.junit.*;

import java.io.File;

import static com.genesyslab.functional.tests.gms.helper.ContextServices.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GMSLogFilterTests {

	private ContextServices cs = null;
	private String requestBody = null;
	private Response response = null;
	private String service_id = null;

	private WorkWithResources res = new WorkWithResources();
	// String logsDir = "C:/Logs/GMS/auto/";//C:\Logs\GMS\GMS_8120022
	// String version = "GMS_8510100";
	String logsDir = "C:/Logs/GMS/";
	String version = "gms_";
	String extention = "log";

	String addressToGetBat = "C:\\GCTI\\GMS\\GMS_8510100";
	// String logDirAddress="C:\\Logs\\GMS\\auto";
	String logDirAddress = "C:\\Logs\\GMS";

	private String application_type = "'application_type' [str] = \"App_type_1\"";
	private String media_type = "'media_type' [str] = \"mymedia1\"";
	private String service_type = "'service_type' [str] = \"MyService1\"";
	private String resource_type = "'resource_type' [str] = \"ResType1\"";
	private String interaction_id = "'interaction_id' [str] = \"123ABCAADFJ1259ACF\"";
	private String customer_id = "'customer_id' [str] = \"00018b9GQ5KS03BW\"";
	private String relatedOffersService = "'relatedOffersService' [str] = \"[KVList: 'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\"]\"";

	private File file = null;

	public GMSLogFilterTests() {
		cs = getInstance();
	}

	@BeforeClass
	public static void oneTimeSetUp() {
		removeSection("log-filter");
		removeSection("log-filter-data");
		configureOptionsInGMS();
//		purgeServices(constructPurgeRequestBody(getCurrentTime(),
//				"purge.service.all"));
	}

	@AfterClass
	public static void oneTimeTearDown() {
		removeSection("log-filter");
		removeSection("log-filter-data");
//		purgeServices(constructPurgeRequestBody(getCurrentTime(),
//				"purge.service.all"));
	}

	@Before
	public void setUp() {
		// ConfigurationServer.createNewOptionWithValues("log-filter-data",new
		// String []{"111111111",""});
		// ConfigurationServer.createNewOptionWithValues("log-filter",new String
		// []{"uuu",""});

	}

	@After
	public void tearDown() {

		// ConfigurationServer.createNewOptionWithValues("log-filter-data",new
		// String []{"subscriberId","hide"});
		// ConfigurationServer.createNewOptionWithValues("log-filter",new String
		// []{"default-filter-type","copy"});
		if (requestBody != null) {
			requestBody = null;
		}
		if (response != null) {
			response = null;
		}
		if (service_id != null) {
			service_id = null;
		}

	}

	private static void configureOptionsInGMS() {
		Reconfiguration reconfiguration = new Reconfiguration();
		reconfiguration.openConnectionToConfig();

		try {
			reconfiguration.addBusinessAttributesSection();
			reconfiguration.addCviewSection();
			reconfiguration.addLogSection("debug");
		} catch (ConfigException e) {
			e.printStackTrace();
		}

		reconfiguration.closeConnectionToConfig();
	}

	private static void configureLogFilterOptions(String sectionName,
			String key, String value) {
		Reconfiguration reconfiguration = new Reconfiguration();
		reconfiguration.openConnectionToConfig();

		try {
			reconfiguration.addLogFilterOptions(sectionName, key, value);

		} catch (ConfigException e) {
			e.printStackTrace();
		}

		reconfiguration.closeConnectionToConfig();
	}
	
	private static void removeSection(String sectionName) {
		Reconfiguration reconfiguration = new Reconfiguration();
		reconfiguration.openConnectionToConfig();

		try {
			reconfiguration.deleteSection(sectionName);

		} catch (ConfigException e) {
			e.printStackTrace();
		}

		reconfiguration.closeConnectionToConfig();
	}
	
	private static void removeOptionFromSection(String sectionName, String option) {
		Reconfiguration reconfiguration = new Reconfiguration();
		reconfiguration.openConnectionToConfig();

		try {
			reconfiguration.deleteOptionFromSection(sectionName, option);

		} catch (ConfigException e) {
			e.printStackTrace();
		}

		reconfiguration.closeConnectionToConfig();
	}

	private void startServiceOnGMS() {
		// starting a service
		String requestBody = "{\"interaction_id\":\"123ABCAADFJ1259ACF\",\"application_type\":\"App_type_1\",\"media_type\":\"mymedia1\",\"resource_type\":\"ResType1\",\"relatedOffersService\":[{\"offer_name\":\"VIP credit card black ed\",\"type\":\"9\",\"comments\":\"proposed to all clients\"},{\"offer_name\":\"VIP credit card black ed\",\"type\":\"9\",\"comments\":\"proposed to all clients\"},{\"offer_name\":\"VIP credit card black ed\",\"type\":\"9\",\"comments\":\"proposed to all clients\"}],\"customer_id\":\"00018b9GQ5KS03BW\",\"service_type\":\"MyService1\"}";
		response = startService(requestBody);
		assertEquals(201, response.getStatusCode());

		service_id = getServiceId(response);
		assertNotNull("Service_id is null", service_id);
	}

	private void verifyGMSLogs() {
		assertTrue(
				" Please check logs! Expression for application_type is not found! ",
				res.checkFileContains(file, extention, application_type));
		assertTrue(
				" Please check logs! Expression for media_type is not found! ",
				res.checkFileContains(file, extention, media_type));
		assertTrue(
				" Please check logs! Expression for service_type_type is not found! ",
				res.checkFileContains(file, extention, service_type));
		assertTrue(
				" Please check logs! Expression for resource_type is not found! ",
				res.checkFileContains(file, extention, resource_type));
		assertTrue(
				" Please check logs! Expression for interaction_id is not found! ",
				res.checkFileContains(file, extention, interaction_id));
		assertTrue(
				" Please check logs! Expression for customer_id is not found! ",
				res.checkFileContains(file, extention, customer_id));
//		assertTrue(
//				" Please check logs! Expression for relatedOffersService is not found! ",
//				res.checkFileContains(file, extention, relatedOffersService));
	}
	
	private void verifyNotPresentInGMSLogs() {
		assertFalse(
				" Please check logs! Expression for application_type is found! ",
				res.checkFileContains(file, extention, application_type));
		assertFalse(
				" Please check logs! Expression for media_type is found! ",
				res.checkFileContains(file, extention, media_type));
		assertFalse(
				" Please check logs! Expression for service_type_type is found! ",
				res.checkFileContains(file, extention, service_type));
		assertFalse(
				" Please check logs! Expression for resource_type is found! ",
				res.checkFileContains(file, extention, resource_type));
		assertFalse(
				" Please check logs! Expression for interaction_id is found! ",
				res.checkFileContains(file, extention, interaction_id));
		assertFalse(
				" Please check logs! Expression for customer_id is found! ",
				res.checkFileContains(file, extention, customer_id));
//		assertFalse(
//				" Please check logs! Expression for relatedOffersService is found! ",
//				res.checkFileContains(file, extention, relatedOffersService));
	}
	
	private void waitAfterReconfiguartion(){
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	//******************************** Log Filter ***************************************************

		
		
		// should run first
		/**
		 * Test1_01 logFilterHide
		 */
		@Test
		public void test1_01() {
			
			// clean up log folder
			configureLogFilterOptions("log-filter", "default-filter-type",
					"hide");
			waitAfterReconfiguartion();

			startServiceOnGMS();
			
			// check if info exists in file:
			file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);

		 	application_type = "'application_type' [output suppressed]";
			media_type = "'media_type' [output suppressed]";
			service_type = "'service_type' [output suppressed]";
			resource_type = "'resource_type' [output suppressed]";
			interaction_id = "'interaction_id' [output suppressed]";
			customer_id = "'customer_id' [output suppressed]";
			relatedOffersService = "'relatedOffersService' [output suppressed]";

			verifyGMSLogs();

		}
		
		/**
		 * test1_02 noLogFilterSection
		 */
		
		// when no section exists then should be by default: as copy
		@Test
		public void test1_02() {
			removeSection("log-filter");
			
			waitAfterReconfiguartion();

			startServiceOnGMS();
			
			// check if info exists in file:
			file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);

		 	application_type = "'application_type' [str] = \"App_type_1\"";
			media_type = "'media_type' [str] = \"mymedia1\"";
			service_type = "'service_type' [str] = \"MyService1\"";
			resource_type = "'resource_type' [str] = \"ResType1\"";
			interaction_id = "'interaction_id' [str] = \"123ABCAADFJ1259ACF\"";
			customer_id = "'customer_id' [str] = \"00018b9GQ5KS03BW\"";
			relatedOffersService = "'relatedOffersService' [str] = \"[KVList: 'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\"]\"";

			verifyGMSLogs();

		}
		
		/**
		 * Test1_03 noLogFilterOption
		 */
		// When no option exists should be by default: as copy
				@Test
				public void test1_03() {
//					String sectionName = "log-filter";
//				//	String option = "log-filter-data";
//					String option = "default-filter-type";
//					removeOptionFromSection(sectionName, option);
					removeSection("log-filter");
					configureLogFilterOptions("log-filter", "test",
							"test");
					
					waitAfterReconfiguartion();

					startServiceOnGMS();
					// check if info exists in file:
					file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);

				 	application_type = "'application_type' [str] = \"App_type_1\"";
					media_type = "'media_type' [str] = \"mymedia1\"";
					service_type = "'service_type' [str] = \"MyService1\"";
					resource_type = "'resource_type' [str] = \"ResType1\"";
					interaction_id = "'interaction_id' [str] = \"123ABCAADFJ1259ACF\"";
					customer_id = "'customer_id' [str] = \"00018b9GQ5KS03BW\"";
					relatedOffersService = "'relatedOffersService' [str] = \"[KVList: 'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\"]\"";

					verifyGMSLogs();
				}
				
			
			/**
			 * Test1_04 emptyLogFilterOption
			 */
				// When option exists but VALUE is EMPTY should be by default: as copy
				@Test
				public void test1_04() {
					
					removeSection("log-filter");
					configureLogFilterOptions("log-filter", "default-filter-type","");

					waitAfterReconfiguartion();
					startServiceOnGMS();
					
					// check if info exists in file:
					file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);

				 	application_type = "'application_type' [str] = \"App_type_1\"";
					media_type = "'media_type' [str] = \"mymedia1\"";
					service_type = "'service_type' [str] = \"MyService1\"";
					resource_type = "'resource_type' [str] = \"ResType1\"";
					interaction_id = "'interaction_id' [str] = \"123ABCAADFJ1259ACF\"";
					customer_id = "'customer_id' [str] = \"00018b9GQ5KS03BW\"";
					relatedOffersService = "'relatedOffersService' [str] = \"[KVList: 'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\"]\"";

					verifyGMSLogs();

				}
				
				/**
				 * Test1_05 incorrectLogFilterOption
				 */
				// When option exists but VALUE is INCORRECT should be by default: as copy
				@Test
				public void test1_05() {
					configureLogFilterOptions("log-filter", "default-filter-type","incorrect value");

					waitAfterReconfiguartion();
					startServiceOnGMS();
					
					// check if info exists in file:
					file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);

				 	application_type = "'application_type' [str] = \"App_type_1\"";
					media_type = "'media_type' [str] = \"mymedia1\"";
					service_type = "'service_type' [str] = \"MyService1\"";
					resource_type = "'resource_type' [str] = \"ResType1\"";
					interaction_id = "'interaction_id' [str] = \"123ABCAADFJ1259ACF\"";
					customer_id = "'customer_id' [str] = \"00018b9GQ5KS03BW\"";
					relatedOffersService = "'relatedOffersService' [str] = \"[KVList: 'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\"]\"";

					verifyGMSLogs();

				}

	/**
	 * test1_06 logFilterUnhideLastOne
	 */
	@Test
	public void test1_06() {
		// clean up log folder
		configureLogFilterOptions("log-filter", "default-filter-type",
				"unhide-last,1");
		
		waitAfterReconfiguartion();
		startServiceOnGMS();
		
		// check if info exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);

		application_type = "'application_type' [str] = \"*********1\"";
		media_type = "'media_type' [str] = \"*******1\"";
		service_type = "'service_type' [str] = \"*********1\"";
		resource_type = "'resource_type' [str] = \"*******1\"";
		interaction_id = "'interaction_id' [str] = \"*****************F\"";
		customer_id = "'customer_id' [str] = \"***************W\"";
		relatedOffersService = "'relatedOffersService' [str] = \"********************************************************************************************************************************************************************************************************************************************************************************************************************************************************************]\"";

		verifyGMSLogs();
	}

	/**
	 * test1_07 logFilterUnhideLastTwo
	 */
	@Test
	public void test1_07() {
		// clean up log folder
		configureLogFilterOptions("log-filter", "default-filter-type",
				"unhide-last,2");

		waitAfterReconfiguartion();
		startServiceOnGMS();
		
		// check if info exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);

		application_type = "'application_type' [str] = \"********_1\"";
		media_type = "'media_type' [str] = \"******a1\"";
		service_type = "'service_type' [str] = \"********e1\"";
		resource_type = "'resource_type' [str] = \"******e1\"";
		interaction_id = "'interaction_id' [str] = \"****************CF\"";
		customer_id = "'customer_id' [str] = \"**************BW\"";
		relatedOffersService = "'relatedOffersService' [str] = \"*******************************************************************************************************************************************************************************************************************************************************************************************************************************************************************\"]\"";

		verifyGMSLogs();
	}
	
	/**
	 * test1_08 logFilterUnhideFirstOne
	 */
	@Test
	public void test1_08() {
		// clean up log folder
		configureLogFilterOptions("log-filter", "default-filter-type",
				"unhide-first,1");
		
		waitAfterReconfiguartion();
		startServiceOnGMS();
		
		// check if info exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);

		application_type = "'application_type' [str] = \"A*********\"";
		media_type = "'media_type' [str] = \"m*******\"";
		service_type = "'service_type' [str] = \"M*********\"";
		resource_type = "'resource_type' [str] = \"R*******\"";
		interaction_id = "'interaction_id' [str] = \"1*****************\"";
		customer_id = "'customer_id' [str] = \"0***************\"";
		relatedOffersService = "'relatedOffersService' [str] = \"[********************************************************************************************************************************************************************************************************************************************************************************************************************************************************************\"";

		verifyGMSLogs();
	}

	/**
	 * test1_09 logFilterUnhideFirstTwo
	 */
	@Test
	public void test1_09() {
		// clean up log folder
		configureLogFilterOptions("log-filter", "default-filter-type",
				"unhide-first,2");
		
		waitAfterReconfiguartion();
		startServiceOnGMS();
		
		// check if info exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);

		application_type = "'application_type' [str] = \"Ap********\"";
		media_type = "'media_type' [str] = \"my******\"";
		service_type = "'service_type' [str] = \"My********\"";
		resource_type = "'resource_type' [str] = \"Re******\"";
		interaction_id = "'interaction_id' [str] = \"12****************\"";
		customer_id = "'customer_id' [str] = \"00**************\"";
		relatedOffersService = "'relatedOffersService' [str] = \"[K*******************************************************************************************************************************************************************************************************************************************************************************************************************************************************************\"";

		verifyGMSLogs();
	}

	/**
	 * test1_10 logFilterUnhideFirst20
	 */
	@Test
	public void test1_10() {
		// clean up log folder
		configureLogFilterOptions("log-filter", "default-filter-type",
				"unhide-first,20");

		waitAfterReconfiguartion();
		startServiceOnGMS();
		
		// check if info exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);
		
		application_type = "'application_type' [str] = \"App_type_1\"";
		media_type = "'media_type' [str] = \"mymedia1\"";
		service_type = "'service_type' [str] = \"MyService1\"";
		resource_type = "'resource_type' [str] = \"ResType1\"";
		interaction_id = "'interaction_id' [str] = \"123ABCAADFJ1259ACF\"";
		customer_id = "'customer_id' [str] = \"00018b9GQ5KS03BW\"";		
		relatedOffersService = "'relatedOffersService' [str] = \"[KVList: 'offer_name**************************************************************************************************************************************************************************************************************************************************************************************************************************************************\"";

		verifyGMSLogs();
	}

	/**
	 * test1_11 logFilterUnhideLast20
	 */
	@Test
	public void test1_11() {
		// clean up log folder
		configureLogFilterOptions("log-filter", "default-filter-type",
				"unhide-last,20");

		waitAfterReconfiguartion();
		startServiceOnGMS();
		
		// check if info exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);
		
		application_type = "'application_type' [str] = \"App_type_1\"";
		media_type = "'media_type' [str] = \"mymedia1\"";
		service_type = "'service_type' [str] = \"MyService1\"";
		resource_type = "'resource_type' [str] = \"ResType1\"";
		interaction_id = "'interaction_id' [str] = \"123ABCAADFJ1259ACF\"";
		customer_id = "'customer_id' [str] = \"00018b9GQ5KS03BW\"";	
	
		relatedOffersService = "'relatedOffersService' [str] = \"**************************************************************************************************************************************************************************************************************************************************************************************************************************************************sed to all clients\"]\"";

		verifyGMSLogs();
	}
	
	/**
	 * test1_12 logFilterHideLast20
	 */
	@Test
	public void test1_12() {
		// clean up log folder
		configureLogFilterOptions("log-filter", "default-filter-type",
				"hide-last,20");
		
		waitAfterReconfiguartion();
		
		startServiceOnGMS();
		
		// check if info exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);
		
		application_type = "'application_type' [str] = \"App_type_1\"";
		media_type = "'media_type' [str] = \"mymedia1\"";
		service_type = "'service_type' [str] = \"MyService1\"";
		resource_type = "'resource_type' [str] = \"ResType1\"";
		interaction_id = "'interaction_id' [str] = \"123ABCAADFJ1259ACF\"";
		customer_id = "'customer_id' [str] = \"00018b9GQ5KS03BW\"";		
		relatedOffersService = "'relatedOffersService' [str] = \"[KVList: 'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"propo********************\"";

		verifyGMSLogs();
	}
	

	/**
	 * test1_13 logFilterHideFirst20
	 */
	@Test
	public void test1_13() {
		// clean up log folder
		configureLogFilterOptions("log-filter", "default-filter-type",
				"hide-first,20");
		
		waitAfterReconfiguartion();
		startServiceOnGMS();
		
		// check if info exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);
		
		application_type = "'application_type' [str] = \"**********\"";
		media_type = "'media_type' [str] = \"********\"";
		service_type = "'service_type' [str] = \"**********\"";
		resource_type = "'resource_type' [str] = \"********\"";
		interaction_id = "'interaction_id' [str] = \"******************\"";
		customer_id = "'customer_id' [str] = \"****************\"";	
		relatedOffersService = "'relatedOffersService' [str] = \"********************' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\"]\"";

		verifyGMSLogs();
	}
	
	/**
	 * test1_14 logFilterHideLastOne
	 */
	@Test
	public void test1_14() {
		// clean up log folder
		configureLogFilterOptions("log-filter", "default-filter-type",
				"hide-last,1");

		waitAfterReconfiguartion();
		startServiceOnGMS();
		
		// check if info exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);
		
		application_type = "'application_type' [str] = \"App_type_*\"";
		media_type = "'media_type' [str] = \"mymedia*\"";
		service_type = "'service_type' [str] = \"MyService*\"";
		resource_type = "'resource_type' [str] = \"ResType*\"";
		interaction_id = "'interaction_id' [str] = \"123ABCAADFJ1259AC*\"";
		customer_id = "'customer_id' [str] = \"00018b9GQ5KS03B*\"";				
		relatedOffersService = "'relatedOffersService' [str] = \"[KVList: 'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\"*\"";

		verifyGMSLogs();
	}

	/**
	 * test1_15 logFilterHideLast3
	 */
	@Test
	public void test1_15() {
		// clean up log folder
		configureLogFilterOptions("log-filter", "default-filter-type",
				"hide-last,3");

		waitAfterReconfiguartion();
		startServiceOnGMS();
		
		// check if info exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);
		
		application_type = "'application_type' [str] = \"App_typ***\"";
		media_type = "'media_type' [str] = \"mymed***\"";
		service_type = "'service_type' [str] = \"MyServi***\"";
		resource_type = "'resource_type' [str] = \"ResTy***\"";
		interaction_id = "'interaction_id' [str] = \"123ABCAADFJ1259***\"";
		customer_id = "'customer_id' [str] = \"00018b9GQ5KS0***\"";				
		relatedOffersService = "'relatedOffersService' [str] = \"[KVList: 'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all client***\"";

		verifyGMSLogs();
	}
	
	/**
	 * test1_16 logFilterHideFirstOne
	 */
	@Test
	public void test1_16() {
		// clean up log folder
		configureLogFilterOptions("log-filter", "default-filter-type",
				"hide-first,1");

		waitAfterReconfiguartion();
		startServiceOnGMS();
		
		// check if info exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);

	    application_type = "'application_type' [str] = \"*pp_type_1\"";
	    media_type = "'media_type' [str] = \"*ymedia1\"";
		service_type = "'service_type' [str] = \"*yService1\"";
		resource_type = "'resource_type' [str] = \"*esType1\"";
		interaction_id = "'interaction_id' [str] = \"*23ABCAADFJ1259ACF\"";
		customer_id = "'customer_id' [str] = \"*0018b9GQ5KS03BW\"";
		relatedOffersService = "'relatedOffersService' [str] = \"*KVList: 'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\"]\"";

		verifyGMSLogs();
	}
	
	/**
	 * test1_17 logFilterHideFirst4
	 */
	@Test
	public void test1_17() {
		// clean up log folder
		configureLogFilterOptions("log-filter", "default-filter-type",
				"hide-first,4");

		waitAfterReconfiguartion();
		startServiceOnGMS();
		
		// check if info exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);

	    application_type = "'application_type' [str] = \"****type_1\"";
	    media_type = "'media_type' [str] = \"****dia1\"";
		service_type = "'service_type' [str] = \"****rvice1\"";
		resource_type = "'resource_type' [str] = \"****ype1\"";
		interaction_id = "'interaction_id' [str] = \"****BCAADFJ1259ACF\"";
		customer_id = "'customer_id' [str] = \"****8b9GQ5KS03BW\"";
		relatedOffersService = "'relatedOffersService' [str] = \"****ist: 'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\"]\"";

		verifyGMSLogs();
	}
	
	/**
	 * test1_18 logFilterSkip
	 */
	@Test
	public void test1_18() {
		// clean up log folder
		configureLogFilterOptions("log-filter", "default-filter-type",
				"skip");

		waitAfterReconfiguartion();
		startServiceOnGMS();
		
		// check if info exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);

	 	application_type = "'application_type' [str] = \"App_type_1\"";
		media_type = "'media_type' [str] = \"mymedia1\"";
		 service_type = "'service_type' [str] = \"MyService1\"";
		 resource_type = "'resource_type' [str] = \"ResType1\"";
		 interaction_id = "'interaction_id' [str] = \"123ABCAADFJ1259ACF\"";
		 customer_id = "'customer_id' [str] = \"00018b9GQ5KS03BW\"";
		 relatedOffersService = "'relatedOffersService' [str] = \"[KVList: 'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\"]\"";

		verifyNotPresentInGMSLogs();
	}
	
	/**
	 * test1_19 logFilterCopy
	 */
	@Test
	public void test1_19() {
		// clean up log folder
		configureLogFilterOptions("log-filter", "default-filter-type",
				"copy");

		waitAfterReconfiguartion();
		startServiceOnGMS();
		
		// check if info exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);

	 	application_type = "'application_type' [str] = \"App_type_1\"";
		media_type = "'media_type' [str] = \"mymedia1\"";
		service_type = "'service_type' [str] = \"MyService1\"";
		resource_type = "'resource_type' [str] = \"ResType1\"";
		interaction_id = "'interaction_id' [str] = \"123ABCAADFJ1259ACF\"";
		customer_id = "'customer_id' [str] = \"00018b9GQ5KS03BW\"";
		relatedOffersService = "'relatedOffersService' [str] = \"[KVList: 'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\"]\"";

		verifyGMSLogs();
	}
	
	/**
	 * Test1_20 logFilterTagDefault
	 */
	@Test
	public void test1_20() {
		configureLogFilterOptions("log-filter", "default-filter-type",
				"tag()");
		
		waitAfterReconfiguartion();
		startServiceOnGMS();
		
		// check if info exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);

	 	application_type = "'application_type' [str] = <#\"App_type_1\"#>";
		media_type = "'media_type' [str] = <#\"mymedia1\"#>";
		service_type = "'service_type' [str] = <#\"MyService1\"#>";
		resource_type = "'resource_type' [str] = <#\"ResType1\"#>";
		interaction_id = "'interaction_id' [str] = <#\"123ABCAADFJ1259ACF\"#>";
		customer_id = "'customer_id' [str] = <#\"00018b9GQ5KS03BW\"#>";
		relatedOffersService = "'relatedOffersService' [str] = <#\"[KVList: 'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\"]\"#>";

		verifyGMSLogs();

	}
	
	/**
	 * Test1_21 logFilterTagDefault1
	 */
	@Test
	public void test1_21() {
		configureLogFilterOptions("log-filter", "default-filter-type",
				"tag");

		waitAfterReconfiguartion();
		startServiceOnGMS();
		
		// check if info exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);

	 	application_type = "'application_type' [str] = <#\"App_type_1\"#>";
		media_type = "'media_type' [str] = <#\"mymedia1\"#>";
		service_type = "'service_type' [str] = <#\"MyService1\"#>";
		resource_type = "'resource_type' [str] = <#\"ResType1\"#>";
		interaction_id = "'interaction_id' [str] = <#\"123ABCAADFJ1259ACF\"#>";
		customer_id = "'customer_id' [str] = <#\"00018b9GQ5KS03BW\"#>";
		relatedOffersService = "'relatedOffersService' [str] = <#\"[KVList: 'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\"]\"#>";

		verifyGMSLogs();

	}
	
	/**
	 * Test1_22 logFilterTagDefault2
	 */
	@Test
	public void test1_22() {
		configureLogFilterOptions("log-filter", "default-filter-type",
				"tag(,)");

		waitAfterReconfiguartion();
		startServiceOnGMS();
		
		// check if info exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);

	 	application_type = "'application_type' [str] = <#\"App_type_1\"#>";
		media_type = "'media_type' [str] = <#\"mymedia1\"#>";
		service_type = "'service_type' [str] = <#\"MyService1\"#>";
		resource_type = "'resource_type' [str] = <#\"ResType1\"#>";
		interaction_id = "'interaction_id' [str] = <#\"123ABCAADFJ1259ACF\"#>";
		customer_id = "'customer_id' [str] = <#\"00018b9GQ5KS03BW\"#>";
		relatedOffersService = "'relatedOffersService' [str] = <#\"[KVList: 'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\"]\"#>";

		verifyGMSLogs();

	}

	/**
	 * Test1_23 logFilterTag1
	 */
	@Test
	public void test1_23() {
		configureLogFilterOptions("log-filter", "default-filter-type",
				"tag(<**,**>)");

		waitAfterReconfiguartion();
		startServiceOnGMS();
		
		// check if info exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);

	 	application_type = "'application_type' [str] = <**\"App_type_1\"**>";
		media_type = "'media_type' [str] = <**\"mymedia1\"**>";
		service_type = "'service_type' [str] = <**\"MyService1\"**>";
		resource_type = "'resource_type' [str] = <**\"ResType1\"**>";
		interaction_id = "'interaction_id' [str] = <**\"123ABCAADFJ1259ACF\"**>";
		customer_id = "'customer_id' [str] = <**\"00018b9GQ5KS03BW\"**>";
		relatedOffersService = "'relatedOffersService' [str] = <**\"[KVList: 'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\"]\"**>";

		verifyGMSLogs();

	}
	/**
	 * Test1_24 logFilterTag2
	 */
	@Test
	public void test1_24() {
		configureLogFilterOptions("log-filter", "default-filter-type",
				"tag(<!--,-->)");

		waitAfterReconfiguartion();
		startServiceOnGMS();
		
		// check if info exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);

	 	application_type = "'application_type' [str] = <!--\"App_type_1\"-->";
		media_type = "'media_type' [str] = <!--\"mymedia1\"-->";
		service_type = "'service_type' [str] = <!--\"MyService1\"-->";
		resource_type = "'resource_type' [str] = <!--\"ResType1\"-->";
		interaction_id = "'interaction_id' [str] = <!--\"123ABCAADFJ1259ACF\"-->";
		customer_id = "'customer_id' [str] = <!--\"00018b9GQ5KS03BW\"-->";
		relatedOffersService = "'relatedOffersService' [str] = <!--\"[KVList: 'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\"]\"-->";

		verifyGMSLogs();

	}

	/**
	 * Test1_25 logFilterDataTagDefault
	 */
	@Test
	public void test1_25() {
		configureLogFilterOptions("log-filter", "default-filter-type",
				"tag(<#,#>)");

		waitAfterReconfiguartion();
		startServiceOnGMS();
		
		// check if info exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);

	 	application_type = "'application_type' [str] = <#\"App_type_1\"#>";
		media_type = "'media_type' [str] = <#\"mymedia1\"#>";
		service_type = "'service_type' [str] = <#\"MyService1\"#>";
		resource_type = "'resource_type' [str] = <#\"ResType1\"#>";
		interaction_id = "'interaction_id' [str] = <#\"123ABCAADFJ1259ACF\"#>";
		customer_id = "'customer_id' [str] = <#\"00018b9GQ5KS03BW\"#>";
		relatedOffersService = "'relatedOffersService' [str] = <#\"[KVList: 'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\"]\"#>";

		verifyGMSLogs();

	}

	
//*********************************** Log Filter Data *******************************************
	/**
	 * Test2_01 logFilterDataTag1
	 */
	@Test
	public void test2_01() {

		String tagFirst = "<!--";
		String tagLast = "-->";
		removeSection("log-filter");
		configureLogFilterOptions("log-filter-data", "application_type",
				"tag("+ tagFirst+","+tagLast+")");

		waitAfterReconfiguartion();
		startServiceOnGMS();
		
		// check if info exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);

	 	application_type = "'application_type' [str] = "+tagFirst+"\"App_type_1\""+ tagLast;
	
		verifyGMSLogs();
		

	}

	
/**
 * Test2_02 logFilterDataSkip
 */
	@Test
	public void test2_02() {
		removeSection("log-filter");
		configureLogFilterOptions("log-filter-data", "application_type",
				"skip");

		waitAfterReconfiguartion();
		startServiceOnGMS();
		
		// check if info exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);

	 	application_type = "'application_type' [str] =";
	
		assertFalse(
				" Please check logs! Expression for application_type is found! ",
				res.checkFileContains(file, extention, application_type));

	}

	/**
	 * Test2_03 logFilterDataHideFirst4
	 */

	@Test
	public void test2_03() {
		removeSection("log-filter");
		configureLogFilterOptions("log-filter-data", "application_type",
				"hide-first,4");

		waitAfterReconfiguartion();
		startServiceOnGMS();
		
		// check if info exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);
	 
	 	application_type = "'application_type' [str] = \"****type_1\"";
		verifyGMSLogs();

	}
	
	/**
	 * Test2_04 logFilterDataHide
	 */
	@Test
	public void test2_04() {
		removeSection("log-filter");
		configureLogFilterOptions("log-filter-data", "application_type",
				"hide");

		waitAfterReconfiguartion();
		startServiceOnGMS();
		
		// check if info exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);
	 
	 	application_type = "'application_type' [output suppressed]";
	 	
		verifyGMSLogs();
	}
	
	//*******************************************Log Filter + Log Filter Data ******************************

	/**
	 * Test3_01 logFilterDataHideFirstLogFilterCopy
	 */
	@Test
	public void test3_01() {
		
		configureLogFilterOptions("log-filter-data", "application_type",
				"hide-first,1");
		configureLogFilterOptions("log-filter", "default-filter-type",
				"copy");

		waitAfterReconfiguartion();
		startServiceOnGMS();
		
		// check if info exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);
	 
	 	application_type = "'application_type' [str] = \"*pp_type_1\"";
		media_type = "'media_type' [str] = \"mymedia1\"";
	    service_type = "'service_type' [str] = \"MyService1\"";
        resource_type = "'resource_type' [str] = \"ResType1\"";
	    interaction_id = "'interaction_id' [str] = \"123ABCAADFJ1259ACF\"";
	    customer_id = "'customer_id' [str] = \"00018b9GQ5KS03BW\"";
		relatedOffersService = "'relatedOffersService' [str] = \"[KVList: 'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\"]\"";

	 	
		verifyGMSLogs();


	}

	/**
	 * Test3_02 logFilterDataHideLastLogFilterCopy
	 */
	@Test
	public void test3_02() {
		configureLogFilterOptions("log-filter-data", "application_type",
				"hide-last,1");
		configureLogFilterOptions("log-filter", "default-filter-type",
				"copy");

		waitAfterReconfiguartion();
		startServiceOnGMS();
		
		// check if info exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);
	 
	 	application_type = "'application_type' [str] = \"App_type_*\"";
		media_type = "'media_type' [str] = \"mymedia1\"";
	    service_type = "'service_type' [str] = \"MyService1\"";
        resource_type = "'resource_type' [str] = \"ResType1\"";
	    interaction_id = "'interaction_id' [str] = \"123ABCAADFJ1259ACF\"";
	    customer_id = "'customer_id' [str] = \"00018b9GQ5KS03BW\"";
		relatedOffersService = "'relatedOffersService' [str] = \"[KVList: 'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\"]\"";
	 	
		verifyGMSLogs();
	}

	/**
	 * Test3_03 logFilterDataHideLogFilterCopy
	 */
	@Test
	public void test3_03() {
		
		configureLogFilterOptions("log-filter-data", "application_type",
				"hide");
		configureLogFilterOptions("log-filter", "default-filter-type",
				"copy");

		waitAfterReconfiguartion();
		startServiceOnGMS();
		
		// check if info exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);
	 
	 	application_type = "'application_type' [output suppressed]";
		media_type = "'media_type' [str] = \"mymedia1\"";
	    service_type = "'service_type' [str] = \"MyService1\"";
        resource_type = "'resource_type' [str] = \"ResType1\"";
	    interaction_id = "'interaction_id' [str] = \"123ABCAADFJ1259ACF\"";
	    customer_id = "'customer_id' [str] = \"00018b9GQ5KS03BW\"";
		relatedOffersService = "'relatedOffersService' [str] = \"[KVList: 'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\"]\"";
	 	
		verifyGMSLogs();

	}

	/**
	 * Test3_04 logFilterDataSkipLogFilterCopy
	 */
	@Test
	public void test3_04() {
		
		configureLogFilterOptions("log-filter-data", "application_type",
				"skip");
		configureLogFilterOptions("log-filter", "default-filter-type",
				"copy");
		
		waitAfterReconfiguartion();
		startServiceOnGMS();

		// check if exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);

		application_type = "'application_type' [str] = \"App_type_1\"";
		media_type = "'media_type' [str] = \"mymedia1\"";
		service_type = "'service_type' [str] = \"MyService1\"";
	    resource_type = "'resource_type' [str] = \"ResType1\"";
		interaction_id = "'interaction_id' [str] = \"123ABCAADFJ1259ACF\"";
		customer_id = "'customer_id' [str] = \"00018b9GQ5KS03BW\"";
		relatedOffersService = "'relatedOffersService' [str] = \"[KVList: 'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\"]\"";
		//verify present 
		assertTrue(
				" Please check logs! Expression for media_type is not found! ",
				res.checkFileContains(file, extention, media_type));
		assertTrue(
				" Please check logs! Expression for service_type_type is not found! ",
				res.checkFileContains(file, extention, service_type));
		assertTrue(
				" Please check logs! Expression for resource_type is not found! ",
				res.checkFileContains(file, extention, resource_type));
		assertTrue(
				" Please check logs! Expression for interaction_id is not found! ",
				res.checkFileContains(file, extention, interaction_id));
		assertTrue(
				" Please check logs! Expression for customer_id is not found! ",
				res.checkFileContains(file, extention, customer_id));
//		assertTrue(
//				" Please check logs! Expression for relatedOffersService is not found! ",
//				res.checkFileContains(file, extention, relatedOffersService));
		
		//verify not present
		assertFalse(
				" Please check logs! Expression for application_type is found! ",
				res.checkFileContains(file, extention, application_type));
	

	}

	/**
	 * Test3_05 logFilterDataUnhideFirstLogFilterCopy
	 */
	@Test
	public void test3_05() {
		configureLogFilterOptions("log-filter-data", "application_type",
				"unhide-first,1");
		configureLogFilterOptions("log-filter", "default-filter-type",
				"copy");
		waitAfterReconfiguartion();
		startServiceOnGMS();

		// check if exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);

		application_type = "'application_type' [str] = \"A*********\"";
		media_type = "'media_type' [str] = \"mymedia1\"";
		service_type = "'service_type' [str] = \"MyService1\"";
	    resource_type = "'resource_type' [str] = \"ResType1\"";
		interaction_id = "'interaction_id' [str] = \"123ABCAADFJ1259ACF\"";
		customer_id = "'customer_id' [str] = \"00018b9GQ5KS03BW\"";
		relatedOffersService = "'relatedOffersService' [str] = \"[KVList: 'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\"]\"";
	

		verifyGMSLogs();

	}

	/**
	 * Test3_06 logFilterDataUnhideLastLogFilterCopy
	 */
	@Test
	public void test3_06() {

		configureLogFilterOptions("log-filter-data", "application_type",
				"unhide-last,1");
		configureLogFilterOptions("log-filter", "default-filter-type",
				"copy");
		
		waitAfterReconfiguartion();
		startServiceOnGMS();

		// check if exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);

		application_type = "'application_type' [str] = \"*********1\"";
		media_type = "'media_type' [str] = \"mymedia1\"";
		service_type = "'service_type' [str] = \"MyService1\"";
	    resource_type = "'resource_type' [str] = \"ResType1\"";
		interaction_id = "'interaction_id' [str] = \"123ABCAADFJ1259ACF\"";
		customer_id = "'customer_id' [str] = \"00018b9GQ5KS03BW\"";
		relatedOffersService = "'relatedOffersService' [str] = \"[KVList: 'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\"]\"";
	

		verifyGMSLogs();

	}

	/**
	 * Test3_07 logFilterDataHideFirstLogFilterHide
	 */
	@Test
	public void test3_07() {

		configureLogFilterOptions("log-filter-data", "application_type",
				"hide-first,1");
		configureLogFilterOptions("log-filter", "default-filter-type",
				"hide");
		
		waitAfterReconfiguartion();
		startServiceOnGMS();

		// check if exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);

		application_type = "'application_type' [str] = \"*pp_type_1\"";
		media_type = "'media_type' [output suppressed]";
		service_type = "'service_type' [output suppressed]";
		resource_type = "'resource_type' [output suppressed]";
		interaction_id = "'interaction_id' [output suppressed]";
		customer_id = "'customer_id' [output suppressed]";
		relatedOffersService = "'relatedOffersService' [output suppressed]";

		verifyGMSLogs();

	}
	
	/**
	 * Test3_08 logFilterDataHideLastLogFilterHide
	 */

	@Test
	public void test3_08() {

		configureLogFilterOptions("log-filter-data", "application_type",
				"hide-last,1");
		configureLogFilterOptions("log-filter", "default-filter-type",
				"hide");
		
		waitAfterReconfiguartion();
		startServiceOnGMS();

		// check if exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);

		application_type = "'application_type' [str] = \"App_type_*\"";
		media_type = "'media_type' [output suppressed]";
		service_type = "'service_type' [output suppressed]";
		resource_type = "'resource_type' [output suppressed]";
		interaction_id = "'interaction_id' [output suppressed]";
		customer_id = "'customer_id' [output suppressed]";
		relatedOffersService = "'relatedOffersService' [output suppressed]";

		verifyGMSLogs();

	}
	
	/**
	 * Test3_09 logFilterDataUnhideFirstLogFilterHide
	 */
	@Test
	public void test3_09() {
		configureLogFilterOptions("log-filter-data", "application_type",
				"unhide-first,1");
		configureLogFilterOptions("log-filter", "default-filter-type",
				"hide");
	
		waitAfterReconfiguartion();
		startServiceOnGMS();

		// check if exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);

		application_type = "'application_type' [str] = \"A*********\"";
		media_type = "'media_type' [output suppressed]";
		service_type = "'service_type' [output suppressed]";
		resource_type = "'resource_type' [output suppressed]";
		interaction_id = "'interaction_id' [output suppressed]";
		customer_id = "'customer_id' [output suppressed]";
		relatedOffersService = "'relatedOffersService' [output suppressed]";

		verifyGMSLogs();

	}
	
	/**
	 * Test3_10 logFilterDataUnhideLastLogFilterHide
	 */
	@Test
	public void test3_10() {

		configureLogFilterOptions("log-filter-data", "application_type",
				"unhide-last,1");
		configureLogFilterOptions("log-filter", "default-filter-type",
				"hide");
		
		waitAfterReconfiguartion();
		startServiceOnGMS();

		// check if exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);

		application_type = "'application_type' [str] = \"*********1\"";
		media_type = "'media_type' [output suppressed]";
		service_type = "'service_type' [output suppressed]";
		resource_type = "'resource_type' [output suppressed]";
		interaction_id = "'interaction_id' [output suppressed]";
		customer_id = "'customer_id' [output suppressed]";
		relatedOffersService = "'relatedOffersService' [output suppressed]";

		verifyGMSLogs();

	}
	
	/**
	 * Test 3_11 logFilterDataSkipLogFilterHide
	 */
	@Test
	public void test3_11() {

		
		configureLogFilterOptions("log-filter-data", "application_type",
				"skip");
		configureLogFilterOptions("log-filter", "default-filter-type",
				"hide");
		
		waitAfterReconfiguartion();
		startServiceOnGMS();

		// check if exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);

		application_type = "'application_type' [str] = \"App_type_1\"";
		media_type = "'media_type' [output suppressed]";
		service_type = "'service_type' [output suppressed]";
		resource_type = "'resource_type' [output suppressed]";
		interaction_id = "'interaction_id' [output suppressed]";
		customer_id = "'customer_id' [output suppressed]";
		relatedOffersService = "'relatedOffersService' [output suppressed]";

		//verify present 
		assertTrue(
				" Please check logs! Expression for media_type is not found! ",
				res.checkFileContains(file, extention, media_type));
		assertTrue(
				" Please check logs! Expression for service_type_type is not found! ",
				res.checkFileContains(file, extention, service_type));
		assertTrue(
				" Please check logs! Expression for resource_type is not found! ",
				res.checkFileContains(file, extention, resource_type));
		assertTrue(
				" Please check logs! Expression for interaction_id is not found! ",
				res.checkFileContains(file, extention, interaction_id));
		assertTrue(
				" Please check logs! Expression for customer_id is not found! ",
				res.checkFileContains(file, extention, customer_id));
		assertTrue(
				" Please check logs! Expression for relatedOffersService is not found! ",
				res.checkFileContains(file, extention, relatedOffersService));
				
		//verify not present
		assertFalse(
				" Please check logs! Expression for application_type is found! ",
				res.checkFileContains(file, extention, application_type));

	}

	/**
	 * Test 3_12 logFilterDataCopyLogFilterHide
	 */
	@Test
	public void test3_12() {

		configureLogFilterOptions("log-filter-data", "application_type",
				"copy");
		configureLogFilterOptions("log-filter", "default-filter-type",
				"hide");
		
		waitAfterReconfiguartion();
		startServiceOnGMS();

		// check if exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);

		application_type = "'application_type' [str] = \"App_type_1\"";
		media_type = "'media_type' [output suppressed]";
		service_type = "'service_type' [output suppressed]";
		resource_type = "'resource_type' [output suppressed]";
		interaction_id = "'interaction_id' [output suppressed]";
		customer_id = "'customer_id' [output suppressed]";
		relatedOffersService = "'relatedOffersService' [output suppressed]";
		
		verifyGMSLogs();

	}
	
	/**
	 * Test 3_13 logFilterDataHideFirst1LogFilterHideFirst2
	 */
	@Test
	public void test3_13() {
		configureLogFilterOptions("log-filter-data", "application_type",
				"hide-first,1");
		configureLogFilterOptions("log-filter", "default-filter-type",
				"hide-first,2");
		
		waitAfterReconfiguartion();
		startServiceOnGMS();


		// check if exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);
	    application_type = "'application_type' [str] = \"*pp_type_1\"";
	    media_type = "'media_type' [str] = \"**media1\"";
		service_type = "'service_type' [str] = \"**Service1\"";
		resource_type = "'resource_type' [str] = \"**sType1\"";
		interaction_id = "'interaction_id' [str] = \"**3ABCAADFJ1259ACF\"";
		customer_id = "'customer_id' [str] = \"**018b9GQ5KS03BW\"";
		relatedOffersService = "'relatedOffersService' [str] = \"**VList: 'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\"]\"";

		verifyGMSLogs();
	}
	
	/**
	 * Test3_14 logFilterDataHideLast1LogFilterHideFirst2
	 */
	@Test
	public void test3_14() {

		configureLogFilterOptions("log-filter-data", "application_type",
				"hide-last,1");
		configureLogFilterOptions("log-filter", "default-filter-type",
				"hide-first,2");
		
		waitAfterReconfiguartion();
		startServiceOnGMS();


		// check if exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);
	    application_type = "'application_type' [str] = \"App_type_*\"";
	    media_type = "'media_type' [str] = \"**media1\"";
		service_type = "'service_type' [str] = \"**Service1\"";
		resource_type = "'resource_type' [str] = \"**sType1\"";
		interaction_id = "'interaction_id' [str] = \"**3ABCAADFJ1259ACF\"";
		customer_id = "'customer_id' [str] = \"**018b9GQ5KS03BW\"";
		relatedOffersService = "'relatedOffersService' [str] = \"**VList: 'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\"]\"";

		verifyGMSLogs();

	}

	/**
	 * Test 3_15 logFilterDataUnhideFirst1LogFilterHideFirst2
	 */
	@Test
	public void test3_15() {

		configureLogFilterOptions("log-filter-data", "application_type",
				"unhide-first,1");
		configureLogFilterOptions("log-filter", "default-filter-type",
				"hide-first,2");
		
		waitAfterReconfiguartion();
		startServiceOnGMS();


		// check if exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);
	    application_type = "'application_type' [str] = \"A*********\"";
	    media_type = "'media_type' [str] = \"**media1\"";
		service_type = "'service_type' [str] = \"**Service1\"";
		resource_type = "'resource_type' [str] = \"**sType1\"";
		interaction_id = "'interaction_id' [str] = \"**3ABCAADFJ1259ACF\"";
		customer_id = "'customer_id' [str] = \"**018b9GQ5KS03BW\"";
		relatedOffersService = "'relatedOffersService' [str] = \"**VList: 'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\"]\"";

		verifyGMSLogs();

	}
	
	/**
	 * Test 3_16 logFilterDataUnhideLast1LogFilterHideFirst2
	 */
	@Test
	public void test3_16() {

		configureLogFilterOptions("log-filter-data", "application_type",
				"unhide-last,1");
		configureLogFilterOptions("log-filter", "default-filter-type",
				"hide-first,2");
		
		waitAfterReconfiguartion();
		startServiceOnGMS();


		// check if exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);
	    application_type = "'application_type' [str] = \"*********1\"";
	    media_type = "'media_type' [str] = \"**media1\"";
		service_type = "'service_type' [str] = \"**Service1\"";
		resource_type = "'resource_type' [str] = \"**sType1\"";
		interaction_id = "'interaction_id' [str] = \"**3ABCAADFJ1259ACF\"";
		customer_id = "'customer_id' [str] = \"**018b9GQ5KS03BW\"";
		relatedOffersService = "'relatedOffersService' [str] = \"**VList: 'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\"]\"";

		verifyGMSLogs();

	}

	/**
	 * Test 3_17 logFilterDataSkipLogFilterHideFirst2
	 */
	@Test
	public void test3_17() {

		configureLogFilterOptions("log-filter-data", "application_type",
				"skip");
		configureLogFilterOptions("log-filter", "default-filter-type",
				"hide-first,2");
		
		waitAfterReconfiguartion();
		startServiceOnGMS();


		// check if exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);
	    application_type = "'application_type' [str] = \"App_type_1\"";
	    media_type = "'media_type' [str] = \"**media1\"";
		service_type = "'service_type' [str] = \"**Service1\"";
		resource_type = "'resource_type' [str] = \"**sType1\"";
		interaction_id = "'interaction_id' [str] = \"**3ABCAADFJ1259ACF\"";
		customer_id = "'customer_id' [str] = \"**018b9GQ5KS03BW\"";
		relatedOffersService = "'relatedOffersService' [str] = \"**VList: 'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\"]\"";

		//verify present 
		assertTrue(
				" Please check logs! Expression for media_type is not found! ",
				res.checkFileContains(file, extention, media_type));
		assertTrue(
				" Please check logs! Expression for service_type_type is not found! ",
				res.checkFileContains(file, extention, service_type));
		assertTrue(
				" Please check logs! Expression for resource_type is not found! ",
				res.checkFileContains(file, extention, resource_type));
		assertTrue(
				" Please check logs! Expression for interaction_id is not found! ",
				res.checkFileContains(file, extention, interaction_id));
		assertTrue(
				" Please check logs! Expression for customer_id is not found! ",
				res.checkFileContains(file, extention, customer_id));
//		assertTrue(
//				" Please check logs! Expression for relatedOffersService is not found! ",
//				res.checkFileContains(file, extention, relatedOffersService));
						
		//verify not present
		assertFalse(
				" Please check logs! Expression for application_type is found! ",
				res.checkFileContains(file, extention, application_type));
	}

	/**
	 * Test 3_18 logFilterDataCopyLogFilterHideFirst2
	 */
	@Test
	public void test3_18() {
		configureLogFilterOptions("log-filter-data", "application_type",
				"copy");
		configureLogFilterOptions("log-filter", "default-filter-type",
				"hide-first,2");
		
		waitAfterReconfiguartion();
		startServiceOnGMS();


		// check if exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);
	    application_type = "'application_type' [str] = \"App_type_1\"";
	    media_type = "'media_type' [str] = \"**media1\"";
		service_type = "'service_type' [str] = \"**Service1\"";
		resource_type = "'resource_type' [str] = \"**sType1\"";
		interaction_id = "'interaction_id' [str] = \"**3ABCAADFJ1259ACF\"";
		customer_id = "'customer_id' [str] = \"**018b9GQ5KS03BW\"";
		relatedOffersService = "'relatedOffersService' [str] = \"**VList: 'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\"]\"";

		verifyGMSLogs();
	}

	/**
	 * Test 3_19 logFilterDataHideLogFilterHideFirst2
	 */
	@Test
	public void test3_19() {

		configureLogFilterOptions("log-filter-data", "application_type",
				"hide");
		configureLogFilterOptions("log-filter", "default-filter-type",
				"hide-first,2");
		
		waitAfterReconfiguartion();
		
		startServiceOnGMS();
		

		// check if exists in file:
		file = WorkWithResources.fileWhichNameStartsWith(logsDir, version);
		application_type = "'application_type' [output suppressed]";
	    media_type = "'media_type' [str] = \"**media1\"";
		service_type = "'service_type' [str] = \"**Service1\"";
		resource_type = "'resource_type' [str] = \"**sType1\"";
		interaction_id = "'interaction_id' [str] = \"**3ABCAADFJ1259ACF\"";
		customer_id = "'customer_id' [str] = \"**018b9GQ5KS03BW\"";
		relatedOffersService = "'relatedOffersService' [str] = \"**VList: 'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\", KVList:  'offer_name' [str] = \"VIP credit card black ed\" 'type' [str] = \"9\" 'comments' [str] = \"proposed to all clients\"]\"";

		verifyGMSLogs();

	}

	

}
