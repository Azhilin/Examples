package gms.callback;

import com.genesyslab.ats.cfg.actor.CfgManager;
import com.genesyslab.ats.cfg.actor.app.OptionsApi;
import com.genesyslab.ats.ex.AtsCfgComponentException;
import com.genesyslab.platform.applicationblocks.com.ConfigException;
import com.genesyslab.scsmanager.SCSManager;
import io.restassured.response.Response;
import org.junit.*;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.Callable;

import static com.genesyslab.functional.tests.gms.helper.CallbackServiceHelper.getCallbackDetailByKey;
import static com.genesyslab.functional.tests.gms.helper.CallbackServicesAPI.*;
import static com.genesyslab.functional.tests.gms.helper.ServiceOptions.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class SuspendingCallback {
	private static String gmsHost;
	private static String gmsPort;

	private static String callbackImmServiceName = "cb_gms-4709-imm";
	private static String callbackSchedServiceName = "cb_gms-4709-sch";
	private static String BHServiceName = "business_hours";
    private static String capacityServiceName = "cap1";
    private static String gmsPhoneNumberURL;
	private static String callbackServiceURL;
	private static String anytext = "anytext";

	
	private static String callbackImmSectionName = "service." + callbackImmServiceName;
	private static String callbackSchedSectionName = "service." + callbackSchedServiceName;

	
	private static CfgManager cfgManager = new CfgManager();
	private static SCSManager scsManager = new SCSManager(cfgManager);
	private static Properties properties = new Properties();

	private static String gmsAppName;

	
	
	@BeforeClass
	public static void oneTimeSetUp() {
		try {
			properties.load(new FileInputStream("./config.properties"));

			gmsHost = properties.getProperty("gms.host");
			gmsPort = properties.getProperty("gms.port");
			gmsAppName = properties.getProperty("gms.app.name");

			//callbackServiceURL = "http://" + gmsHost + ":" + gmsPort + "/genesys/1/service/callback/"
				//	+ callbackImmServiceName;

			scsManager.init(properties);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().addSection(callbackImmSectionName,getUserTermImmCallbackServiceOptions());
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().addSection(callbackSchedSectionName,getUserTermSchCallbackServiceOptions());
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().addSection("service." + BHServiceName, getRegularOfficeHours());
            cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().addSection("service." + capacityServiceName, getCapacityOptions());
            

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@AfterClass
	public static void oneTimeTearDown() {

		try {
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackImmSectionName).deleteSection();
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackSchedSectionName).deleteSection();
		} catch (AtsCfgComponentException e) {
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

	// Test 1. customer first immediate scenario / service name in TList
	@Test
	public void test_01_01() throws Exception {
		
		// need to be changed after ATS-1384 fix - by the now, it is necessary to create TList manually with any value on Annex tab 
		//addOptionToTList("GMS_Paused_Services","services", callbackImmServiceName , "false");
	    changeOptionInTList("GMS_Paused_Services","services", callbackImmServiceName , "false");
		tuneCallbackService(callbackImmServiceName, "CUSTOMER", "false","false");
	    
		//create new immediate callback
		Response scheduleCallback = startImmediateCallback(callbackImmServiceName, "5115");
		System.out.println("Response body for callback creating: " + scheduleCallback.then().extract().response().asString());
		assertThat(scheduleCallback.getStatusCode(), equalTo(200));		     
		
		//wait some time for corresponding callback state
		String callbackID = scheduleCallback.then().extract().path("_id");
		given().ignoreExceptions().await().atMost(80, SECONDS).until(actualCallbackDetailValue(callbackID, "_callback_state"), equalTo("QUEUED"));
		        
		changeOptionInTList("GMS_Paused_Services","services", callbackImmServiceName , "true");
		
		//wait some time for corresponding callback state
		given().ignoreExceptions().await().atMost(80, SECONDS).until(actualCallbackDetailValue(callbackID, "_callback_state"),equalTo("PAUSED"));
	   	assertThat(getCallbackDetailByKey(callbackID, "_callback_state"),equalTo("PAUSED"));
		 
		changeOptionInTList("GMS_Paused_Services","services", callbackImmServiceName , "false");
		 
		//wait some time for corresponding callback state
		given().ignoreExceptions().await().atMost(80, SECONDS).until(actualCallbackDetailValue(callbackID, "_callback_state"),equalTo("QUEUED"));
		
		cancelCallback(callbackImmServiceName, callbackID).then().assertThat().statusCode(200);
		given().ignoreExceptions().await().atMost(80, SECONDS).until(actualCallbackDetailValue(callbackID, "_callback_state"),equalTo("COMPLETED"));
		
	}
	
	// Test 2. customer first scheduled scenario / service name in TList
		@Test
		public void test_02_01() throws Exception {
			addOptionToTList("GMS_Paused_Services","services", callbackSchedServiceName , "false");
			tuneCallbackService(callbackSchedServiceName, "CUSTOMER", "true","true");
		    
			//create new scheduled callback		      
			Response scheduleCallback = startScheduledCallback(callbackSchedServiceName,"5115", 180);
			System.out.println("Response body for callback creating: " + scheduleCallback.then().extract().response().asString());
			assertThat(scheduleCallback.getStatusCode(), equalTo(200));		
		    
			//check that scheduled callback is created
			String callbackID = scheduleCallback.then().extract().path("_id");
		    assertThat(getCallbackDetailByKey(callbackID, "_callback_state"),equalTo("SCHEDULED"));
			
			//wait some time for corresponding callback state
			given().ignoreExceptions().await().atMost(80, SECONDS).until(actualCallbackDetailValue(callbackID, "_callback_state"), equalTo("QUEUED"));
			        
			changeOptionInTList("GMS_Paused_Services","services", callbackSchedServiceName , "true");
			
			//wait some time for corresponding callback state
			given().ignoreExceptions().await().atMost(80, SECONDS).until(actualCallbackDetailValue(callbackID, "_callback_state"),equalTo("PAUSED"));
			assertThat(getCallbackDetailByKey(callbackID, "_callback_state"),equalTo("PAUSED"));
			 
			changeOptionInTList("GMS_Paused_Services","services", callbackSchedServiceName , "false");
			 
			//wait some time for corresponding callback state
			given().ignoreExceptions().await().atMost(80, SECONDS).until(actualCallbackDetailValue(callbackID, "_callback_state"),equalTo("QUEUED"));
						
			cancelCallback(callbackSchedServiceName, callbackID).then().assertThat().statusCode(200);
			given().ignoreExceptions().await().atMost(80, SECONDS).until(actualCallbackDetailValue(callbackID, "_callback_state"),equalTo("COMPLETED"));
		}
		
		
		// Test 3. customer first immediate scenario / kvp in TList
		@Test
		public void test_01_02() throws Exception {
						
			try {
				cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection(callbackImmSectionName).addOption("_paused_services_id", anytext);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
			addOptionToTList("GMS_Paused_Services","services", anytext , "false");
			tuneCallbackService(callbackImmServiceName, "CUSTOMER", "false","false");
					    
			//create new immediate callback
			Response scheduleCallback = startImmediateCallback(callbackImmServiceName, "5115");
			System.out.println("Response body for callback creating: " + scheduleCallback.then().extract().response().asString());
			assertThat(scheduleCallback.getStatusCode(), equalTo(200));		     
			
			//wait some time for corresponding callback state
			String callbackID = scheduleCallback.then().extract().path("_id");
			given().ignoreExceptions().await().atMost(80, SECONDS).until(actualCallbackDetailValue(callbackID, "_callback_state"), equalTo("QUEUED"));
			        
			changeOptionInTList("GMS_Paused_Services","services", anytext , "true");
			
			//wait some time for corresponding callback state
			given().ignoreExceptions().await().atMost(80, SECONDS).until(actualCallbackDetailValue(callbackID, "_callback_state"),equalTo("PAUSED"));
		   	assertThat(getCallbackDetailByKey(callbackID, "_callback_state"),equalTo("PAUSED"));
			 
		   	changeOptionInTList("GMS_Paused_Services","services", anytext , "false");
			 
			//wait some time for corresponding callback state
			given().ignoreExceptions().await().atMost(80, SECONDS).until(actualCallbackDetailValue(callbackID, "_callback_state"),equalTo("QUEUED"));
			
			cancelCallback(callbackImmServiceName, callbackID).then().assertThat().statusCode(200);
			given().ignoreExceptions().await().atMost(80, SECONDS).until(actualCallbackDetailValue(callbackID, "_callback_state"),equalTo("COMPLETED"));
			
		}
	

		// Test 4. 2 callbacks / customer first immediate and scheduled scenario / 'all' in TList
		@Test
		public void test_03_03() throws Exception {
						
			addOptionToTList("GMS_Paused_Services","services", "all" , "false");
			tuneCallbackService(callbackImmServiceName, "CUSTOMER", "false","false");
					    
			//create new immediate callback
			Response scheduleCallback = startImmediateCallback(callbackImmServiceName, "5115");
			System.out.println("Response body for callback creating: " + scheduleCallback.then().extract().response().asString());
			assertThat(scheduleCallback.getStatusCode(), equalTo(200));		     
			
			//wait some time for corresponding callback state
			String callbackID = scheduleCallback.then().extract().path("_id");
			given().ignoreExceptions().await().atMost(80, SECONDS).until(actualCallbackDetailValue(callbackID, "_callback_state"), equalTo("QUEUED"));
			        
			changeOptionInTList("GMS_Paused_Services","services", "all" , "true");
			
			//wait some time for corresponding callback state
			given().ignoreExceptions().await().atMost(80, SECONDS).until(actualCallbackDetailValue(callbackID, "_callback_state"),equalTo("PAUSED"));
		   	assertThat(getCallbackDetailByKey(callbackID, "_callback_state"),equalTo("PAUSED"));
			 
		   	changeOptionInTList("GMS_Paused_Services","services", "all" , "false");
			 
			//wait some time for corresponding callback state
			given().ignoreExceptions().await().atMost(80, SECONDS).until(actualCallbackDetailValue(callbackID, "_callback_state"),equalTo("QUEUED"));
			
			cancelCallback(callbackImmServiceName, callbackID).then().assertThat().statusCode(200);
			given().ignoreExceptions().await().atMost(80, SECONDS).until(actualCallbackDetailValue(callbackID, "_callback_state"),equalTo("COMPLETED"));
			
		}
		
	
		  public  void tuneCallbackService(String callbackServiceName_imm,
					String FirstConnectParty,
	                String WaitForAgent,
	                String WaitForUserConfirm) throws Exception {
		    	OptionsApi serviceForTuning = cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("service." + callbackServiceName_imm);
		    	serviceForTuning.changeOptionValue("_userterminated_first_connect_party", FirstConnectParty);
		    	serviceForTuning.changeOptionValue("_wait_for_agent", WaitForAgent);
		    	serviceForTuning.changeOptionValue("_wait_for_user_confirm", WaitForUserConfirm);
		    		}
	    
	
		  private Callable<String> actualCallbackDetailValue(final String callbackID, final String key) {
		        return new Callable<String>() {
		            @Override
		            public String call() throws Exception {
		                return getCallbackDetailByKey(callbackID, key); // The condition supplier part
		            }
		        };
		    }
		    
		  public void addOptionToTList(String TListName, String sectionName, String optionName, String optionValue)  throws AtsCfgComponentException, ConfigException, InterruptedException  {
			  	cfgManager.getTransactionApi().setTransactionName(TListName).setTransactionType("List").addUserPropertyValue(sectionName, optionName, optionValue);
			  	// cfgManager.getTransactionApi().setTransactionName("GMSNotif").setTransactionType("List").addUserPropertyValue("section", "_notif", "555").createNewTransaction();
		   	}
	
		  public void changeOptionInTList(String TListName, String sectionName, String optionName, String optionValue) throws AtsCfgComponentException, ConfigException, InterruptedException {
				cfgManager.getTransactionApi().setTransactionName(TListName).deleteUserPropertyValueIfPresent(sectionName, optionName);
				cfgManager.getTransactionApi().setTransactionName(TListName).setTransactionType("List").addUserPropertyValue(sectionName, optionName, optionValue);
			}
	
}
