package gms.callback;

import com.genesyslab.ats.cfg.actor.CfgManager;
import com.genesyslab.ats.cfg.actor.app.OptionsApi;
import com.genesyslab.ats.ex.AtsCfgComponentException;
import com.genesyslab.platform.applicationblocks.com.ConfigException;
import com.genesyslab.platform.commons.collections.KeyValueCollection;
import com.genesyslab.scsmanager.SCSManager;
import io.restassured.response.Response;
import org.junit.*;

import java.io.FileInputStream;
import java.util.Properties;

import static com.genesyslab.functional.tests.gms.helper.CallbackServiceHelper.cancelUnfinishedCallbacks;
import static com.genesyslab.functional.tests.gms.helper.CallbackServiceHelper.getCallbackDetailByKey;
import static com.genesyslab.functional.tests.gms.helper.CallbackServicesAPI.*;
import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertyConfiguration;
import static com.genesyslab.functional.tests.gms.helper.ServiceOptions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

//import static io.restassured.RestAssured.given;

/*
 * created by Olga Ukolova on 12/22/2016
 */


public class LimitNumberQueuedCallbacksPerService {

	    private static String callbackServiceName_imm = "cb_gms-4060_imm";
	    private static String callbackServiceName_sch = "cb_gms-4060_sch";
	    private static String BHServiceName = "business_hours";
	    private static String capacityServiceName = "cap1";
	    private static CfgManager cfgManager = new CfgManager();
	    private static String gmsAppName = getPropertyConfiguration("gms.app.name");
	    private static String gmsBaseURL = "http://135.17.38.71:8080";
	    private static String orsAppName = "Orchestration_Server";
	    private static Properties properties = new Properties();
	    private static SCSManager scsManager = new SCSManager(cfgManager);
	    private static final long GMS_START_DURATION = 40000; //(in millisec) time needed for GMS launching

	    @BeforeClass
	    public static void oneTimeSetUp() {
	        try {
	            properties.load(new FileInputStream("./config.properties"));
	            scsManager.init(properties);
	            cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().addSection("service." + callbackServiceName_imm, getUserTermImmCallbackServiceOptions());
	            cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().addSection("service." + callbackServiceName_sch, getUserTermSchCallbackServiceOptions());
	            cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().addSection("service." + BHServiceName, getRegularOfficeHours());
	            cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().addSection("service." + capacityServiceName, getCapacityOptions());
	            scsManager.restartApplication(gmsAppName);  
	            scsManager.restartApplication(orsAppName);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    @AfterClass
	    public static void oneTimeTearDown() throws Exception {
	        cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("service." + callbackServiceName_imm).deleteSection();
	        cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("service." + BHServiceName).deleteSection();
	        cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("service." + capacityServiceName).deleteSection();
	        cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("callback").deleteSection(); 
	     
	        cfgManager.deactivate();
	    }

	    @Before
	    public void setUp() {
	        cancelUnfinishedCallbacks(5115);
	       try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	     
	    }

	    
	    @After
	    public void tearDown() throws ConfigException {
	    	 try {
		    	   
					cfgManager.getAppApi().setApp(gmsAppName)
					.getOptionsApi().setSection("callback").deleteSection();
					
					// need to be removed after GMS-4956 implementation
					cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("service." + callbackServiceName_imm).deleteOption("_max_queued_callbacks_per_service");
					
				} catch (AtsCfgComponentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	 cancelUnfinishedCallbacks(5115);
		       try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    
	    }

	    
	    @Test
	    // check that it is impossible to create callback when callback section/max_queued_callbacks_per_service=0. returns 503 error Service Unavailable
	    public void test1_01() throws Exception {
	    	KeyValueCollection callbackSectionOptionList = new KeyValueCollection();
	        callbackSectionOptionList.addString("max_queued_callbacks_per_service", "0");
	    	
	    	cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().addSection("callback",callbackSectionOptionList);
            // scsManager.restartApplication(gmsAppName);  
	        // scsManager.restartApplication(orsAppName);
	        
	        System.out.println("Value of max_queued_callbacks_per_service in service: " + cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().getSection("callback").toString());
	      
	        tuneCallbackService(callbackServiceName_imm, "CUSTOMER", "false","false");
	     	
	        //wait 10 sec
            Thread.sleep(10000);
	        
	        //create new immediate callback
	        Response scheduleCallback = startImmediateCallback(callbackServiceName_imm,"5115");
	 	        
	        System.out.println("Response body for callback creating: " +
	                scheduleCallback.then().extract().response().asString());
	       
	        assertThat(scheduleCallback.getStatusCode(),equalTo(503));
	        assertThat(scheduleCallback.then().extract().path("message").toString(),equalTo("Limit of queued callbacks for "+callbackServiceName_imm+" is reached."));
	        assertThat(scheduleCallback.then().extract().path("exception").toString(),equalTo("com.genesyslab.gsg.services.callback.exceptions.CallbackExceptionServiceUnavailable"));
	    	    }
	    @Test
	    // check that Limit of queued callbacks is reached when callback section/max_queued_callbacks_per_service=1. CUSTOMER first scenario
	    public void test1_02() throws Exception {
	    	KeyValueCollection callbackSectionOptionList = new KeyValueCollection();
	        callbackSectionOptionList.addString("max_queued_callbacks_per_service", "1");
	    	cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().addSection("callback",callbackSectionOptionList);
            //scsManager.restartApplication(gmsAppName);  
	        //scsManager.restartApplication(orsAppName);
	        tuneCallbackService(callbackServiceName_imm, "CUSTOMER", "false","false");
	        
	    	        
	        //wait 10 sec
            Thread.sleep(10000);
            
	        //create new immediate callback
		    Response scheduleCallback1 = startImmediateCallback(callbackServiceName_imm,"5115");
		       
		    System.out.println("Response body for callback creating: " +  scheduleCallback1.then().extract().response().asString());
		    assertThat(scheduleCallback1.getStatusCode(),equalTo(200));		     
		  		        
		    //wait 10 sec
		    Thread.sleep(10000);
		           
		    //create new immediate callback
			Response scheduleCallback2 = startImmediateCallback(callbackServiceName_imm,"5115");
	       
	       
		    System.out.println("Response body for callback creating: " +  scheduleCallback2.then().extract().response().asString());
			assertThat(scheduleCallback2.getStatusCode(),equalTo(503));
		    assertThat(scheduleCallback2.then().extract().path("message").toString(),equalTo("Limit of queued callbacks for "+callbackServiceName_imm+" is reached."));
			assertThat(scheduleCallback2.then().extract().path("exception").toString(),equalTo("com.genesyslab.gsg.services.callback.exceptions.CallbackExceptionServiceUnavailable"));
	    }
	    
	    @Test
	    // check that Limit of queued callbacks is reached when callback section/max_queued_callbacks_per_service=1. AGENT first scenario
	    public void test1_03() throws Exception {
	    	KeyValueCollection callbackSectionOptionList = new KeyValueCollection();
	        callbackSectionOptionList.addString("max_queued_callbacks_per_service", "1");
	    	cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().addSection("callback",callbackSectionOptionList);
            //scsManager.restartApplication(gmsAppName);  
	        //scsManager.restartApplication(orsAppName);
	        tuneCallbackService(callbackServiceName_imm, "AGENT", "true","false");
	        
	        //wait 10 sec
            Thread.sleep(10000);
	        
	        //create new immediate callback
		    Response scheduleCallback1 = startImmediateCallback(callbackServiceName_imm,"5115");
		    System.out.println("Response body for callback creating: " +  scheduleCallback1.then().extract().response().asString());
		    assertThat(scheduleCallback1.getStatusCode(),equalTo(200));
		        
		    //wait 10 sec
	        Thread.sleep(10000);
		        
		    //create new immediate callback
			Response scheduleCallback2 = startImmediateCallback(callbackServiceName_imm,"5115");
			System.out.println("Response body for callback creating: " +  scheduleCallback2.then().extract().response().asString());
			assertThat(scheduleCallback2.getStatusCode(),equalTo(503));
			assertThat(scheduleCallback2.then().extract().path("message").toString(),equalTo("Limit of queued callbacks for "+callbackServiceName_imm+" is reached."));
			assertThat(scheduleCallback2.then().extract().path("exception").toString(),equalTo("com.genesyslab.gsg.services.callback.exceptions.CallbackExceptionServiceUnavailable"));
	    }
	    
	    @Test
	    //  check that it is impossible to create callback when callback service/max_queued_callbacks_per_service=0. returns 503 error Service Unavailable
	    public void test1_04() throws Exception {
	    	cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("service." + callbackServiceName_imm).addOption("_max_queued_callbacks_per_service", "0");
            //scsManager.restartApplication(gmsAppName);  
	        //scsManager.restartApplication(orsAppName);
	        
	        tuneCallbackService(callbackServiceName_imm, "CUSTOMER", "false","false");
	     	
	        //wait 10 sec
            Thread.sleep(10000);
		     
		    //create new immediate callback
		    Response scheduleCallback = startImmediateCallback(callbackServiceName_imm,"5115");
		    System.out.println("Response body for callback creating: " +  scheduleCallback.then().extract().response().asString());
		      
		    assertThat(scheduleCallback.getStatusCode(),equalTo(503));
		    assertThat(scheduleCallback.then().extract().path("message").toString(),equalTo("Limit of queued callbacks for "+callbackServiceName_imm+" is reached."));
		    assertThat(scheduleCallback.then().extract().path("exception").toString(),equalTo("com.genesyslab.gsg.services.callback.exceptions.CallbackExceptionServiceUnavailable"));
	    	    }
	    @Test
	    // check that Limit of queued callbacks is reached when callback service/_max_queued_callbacks_per_service=1 CUSTOMER first scenario
	    public void test1_05() throws Exception {
	    	cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("service." + callbackServiceName_imm).addOption("_max_queued_callbacks_per_service", "1");
            //scsManager.restartApplication(gmsAppName);  
	        //scsManager.restartApplication(orsAppName);
	        tuneCallbackService(callbackServiceName_imm, "CUSTOMER", "false","false");
	     	
	        //wait 10 sec
            Thread.sleep(10000);
            
	        //create new immediate callback
		    Response scheduleCallback1 = startImmediateCallback(callbackServiceName_imm,"5115");
		    System.out.println("Response body for callback creating: " +  scheduleCallback1.then().extract().response().asString());
		    assertThat(scheduleCallback1.getStatusCode(),equalTo(200));
		        
		    //wait 10 sec
	        Thread.sleep(10000);
		        
		    //create new immediate callback
			Response scheduleCallback2 = startImmediateCallback(callbackServiceName_imm,"5115");
			System.out.println("Response body for callback creating: " +  scheduleCallback2.then().extract().response().asString());
			assertThat(scheduleCallback2.getStatusCode(),equalTo(503));
			assertThat(scheduleCallback2.then().extract().path("message").toString(),equalTo("Limit of queued callbacks for "+callbackServiceName_imm+" is reached."));
			assertThat(scheduleCallback2.then().extract().path("exception").toString(),equalTo("com.genesyslab.gsg.services.callback.exceptions.CallbackExceptionServiceUnavailable"));
			    }

@Test
	    // check that Limit of queued callbacks is reached when callback service/max_queued_callbacks_per_service=1 AGENT first scenario
	    public void test1_06() throws Exception {
	    	cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("service." + callbackServiceName_imm).addOption("_max_queued_callbacks_per_service", "1");
            //scsManager.restartApplication(gmsAppName);  
	        //scsManager.restartApplication(orsAppName);
	        tuneCallbackService(callbackServiceName_imm, "AGENT", "true","false");
	        
	     	
	        //wait 10 sec
            Thread.sleep(10000);
	        
	        //create new immediate callback
		    Response scheduleCallback1 = startImmediateCallback(callbackServiceName_imm,"5115");
		    System.out.println("Response body for callback creating: " +  scheduleCallback1.then().extract().response().asString());
		    assertThat(scheduleCallback1.getStatusCode(),equalTo(200));
		        
		    //wait 10 sec
	        Thread.sleep(10000);
		        
		    //create new immediate callback
			Response scheduleCallback2 = startImmediateCallback(callbackServiceName_imm,"5115");
			System.out.println("Response body for callback creating: " +  scheduleCallback2.then().extract().response().asString());
			assertThat(scheduleCallback2.getStatusCode(),equalTo(503));
			assertThat(scheduleCallback2.then().extract().path("message").toString(),equalTo("Limit of queued callbacks for "+callbackServiceName_imm+" is reached."));
			assertThat(scheduleCallback2.then().extract().path("exception").toString(),equalTo("com.genesyslab.gsg.services.callback.exceptions.CallbackExceptionServiceUnavailable"));
			    }
	    
	    @Test
	    // check that it is possible to create new cb after 10 sec after previous cb was cancelled 
	    public void test1_07() throws Exception {
	    	cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("service." + callbackServiceName_imm).addOption("_max_queued_callbacks_per_service", "1");
            //scsManager.restartApplication(gmsAppName);  
	        //scsManager.restartApplication(orsAppName);
	        tuneCallbackService(callbackServiceName_imm, "CUSTOMER", "false","false");
	     	
	        //wait 10 sec
            Thread.sleep(10000);
	        
	        //create new immediate callback
		    Response scheduleCallback1 = startImmediateCallback(callbackServiceName_imm,"5115");
		    System.out.println("Response body for callback creating: " +  scheduleCallback1.then().extract().response().asString());
		    assertThat(scheduleCallback1.getStatusCode(),equalTo(200));
		    //need step to make Agent 'Not Ready' here
		    String cbID = scheduleCallback1.then().extract().path("_id");
		    assertThat(getCallbackDetailByKey(cbID, "_callback_state"),equalTo("QUEUED"));
		        
		    //wait 10 sec
	        Thread.sleep(10000);
		        
		    //cancel first callback
			Response cancellation = cancelCallback(callbackServiceName_imm, scheduleCallback1.then().extract().path("_id").toString());
	        System.out.println("Status code for callback cancellation #"+cancellation.then().extract().response().getStatusCode());
	        assertThat(cancellation.then().extract().response().getStatusCode(), equalTo(200));
				                
	        //wait 10 sec
		    Thread.sleep(10000);
	                
	        //create new immediate callback
	 		Response scheduleCallback2 = startImmediateCallback(callbackServiceName_imm,"5115");
	 		System.out.println("Response body for callback creating: " +  scheduleCallback2.then().extract().response().asString());
	 		assertThat(scheduleCallback2.getStatusCode(),equalTo(200));
	            }
	    
	    // check that it is possible to create new cb after 10 sec after previous cb was completed - NEED TO BE ADDED 
	    
	    @Test
	    // check that callback service/_max_queued_callbacks_per_service doesn't affect scheduled callbacks
	    public void test1_09() throws Exception {
			// need to be removed after GMS-4956 implementation
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("service." + callbackServiceName_sch).deleteOption("_max_queued_callbacks_per_service");

	    	cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().setSection("service." + callbackServiceName_sch).addOption("_max_queued_callbacks_per_service", "1");
            //scsManager.restartApplication(gmsAppName);  
	        //scsManager.restartApplication(orsAppName);
	        tuneCallbackService(callbackServiceName_sch, "CUSTOMER", "true","true");
	     	
	        //wait 10 sec
            Thread.sleep(10000);
            
	        //create scheduled callback
		    Response scheduleCallback1 = startScheduledCallback(callbackServiceName_sch,"5115", 180);
		    System.out.println("Response body for callback creating: " +  scheduleCallback1.then().extract().response().asString());
		        
		    String cbID = scheduleCallback1.then().extract().path("_id");
		    assertThat(getCallbackDetailByKey(cbID, "_callback_state"),equalTo("SCHEDULED"));
		    assertThat(scheduleCallback1.getStatusCode(),equalTo(200));
		        
		    //wait 10 sec
	        Thread.sleep(10000);
		        
		    //create another scheduled callback
			Response scheduleCallback2 = startScheduledCallback(callbackServiceName_sch,"5115", 180);
			System.out.println("Response body for callback creating: " +  scheduleCallback2.then().extract().response().asString());
			        
		    cbID = scheduleCallback2.then().extract().path("_id");
			assertThat(getCallbackDetailByKey(cbID, "_callback_state"),equalTo("SCHEDULED"));
			assertThat(scheduleCallback2.getStatusCode(),equalTo(200));       
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
	  
	  	    
}
