package gms.callback;

import com.genesyslab.functional.tests.gms.helper.CallbackServicesAPI;
import com.genesyslab.functional.tests.gms.psdk.Reconfiguration;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.genesyslab.functional.tests.gms.files.WorkWithResources.*;
import static com.genesyslab.functional.tests.gms.helper.CallbackServiceHelper.*;
import static com.genesyslab.functional.tests.gms.helper.CallbackServicesAPI.*;
import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertyConfiguration;
import static com.genesyslab.functional.tests.gms.helper.RemoteHostOperations.*;
import static com.genesyslab.functional.tests.gms.helper.ServiceOptions.*;
import static com.genesyslab.functional.tests.gms.helper.TimeOperations.*;
import static com.genesyslab.functional.tests.gms.helper.TimeOperations.getMethodExecutionTime;
import static com.genesyslab.functional.tests.gms.helper.URLsHelper.getGMSBaseURL;
import static com.genesyslab.functional.tests.gms.helper.URLsHelper.getGMSFullLoginURL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/*
 * created by Olga Ukolova on 07/20/2017
 */

public class CallbackStateChangeNotifications {
	    private static String propertiesFile = "./config_2nd.properties";//uncomment this line in case you want to use another .properties file
		private static Reconfiguration env = new Reconfiguration(propertiesFile);
	    private static String remoteHost = getPropertyConfiguration("gms.host");

		private static String gmsAppName = getPropertyConfiguration(propertiesFile, "gms.app.name");//uncomment this line in case you want to use another application name
		private static String gmsBaseUrl = getGMSBaseURL(propertiesFile);
		private static String gmsUrl = getGMSFullLoginURL(propertiesFile);
		private static String username = getPropertyConfiguration(propertiesFile, "gms.user.name");
		private static String password = getPropertyConfiguration(propertiesFile, "gms.user.password");
		private static String customerNumber = "5115";
		
		private static String callbackServiceName_imm = "cb_gms-3674_imm";
	    private static String callbackServiceName_sch = "cb_gms-3674-sch";
		private static String BHServiceName = "bh_24x7";
	    private static String capacityServiceName = "cap_1000x24x7";

	    private static final long POLLING_TIME_MILLIS = 500;

	    @BeforeClass
	    public static void oneTimeSetUp() throws Exception {
	        long startTime = System.nanoTime();
	        System.out.println("@BeforeClass method processing...");
	        CallbackServicesAPI.setGMSBaseURL(getGMSBaseURL(propertiesFile));
	        long endTime = System.nanoTime();
	        getMethodExecutionTime(startTime, endTime, "@BeforeClass");
	     	          
	        env.createService(gmsAppName, callbackServiceName_imm, getUserTermImmCallbackServiceOptions());
			env.createService(gmsAppName, callbackServiceName_sch,getUserTermSchCallbackServiceOptions());
			env.createService(gmsAppName, BHServiceName, getRegularOfficeHours());
	        env.createService(gmsAppName, capacityServiceName, getCapacityOptions());       		
	   }
	  
	    @AfterClass
	    public static void oneTimeTearDown() throws Exception {
	        System.out.println("@AfterClass method processing...");
	        env.deleteService(gmsAppName, callbackServiceName_imm); //for troubleshooting purposes comment this command
			env.deleteService(gmsAppName, callbackServiceName_sch); //for troubleshooting purposes comment this command
	        env.deleteService(gmsAppName, BHServiceName); //for troubleshooting purposes comment this command
	        env.deleteService(gmsAppName, capacityServiceName); //for troubleshooting purposes comment this command
	        env.deactivate();
	    }
	    
	    @After
	    public void tearDown() throws Exception {
	        System.out.println("@After method processing...");
	        cancelUnfinishedCallbacks(callbackServiceName_imm);
	        deleteProcessOnRemoteWinHost("node");
	        env.deleteTransaction("GMS_Events");       
	    }

	    
	    @Test
	    // Enable Default Status Notifications in a Callback Service
	  /*  public void test1() throws Exception {
	    	execCmdCommandOnRemoteHost("call C:/test.bat");
	    	List<String> events = new ArrayList<String>();
	    	events.add("_cbe_on_service_create");
	    	events.add("_cbe_on_virtual_ixn_create");
	    	events.add("_cbe_on_target_found");
	    	events.add("_cbe_on_dial_init");
	    	events.add("_cbe_on_dial_done");
	    	events.add("_cbe_on_connect_treatment_start");
	    	events.add("_cbe_on_customer_queued");
	    	events.add("_cbe_on_route_to_agent");
	    	events.add("_cbe_on_service_exit");
	    		    	
	    	
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_imm, "_enable_status_notification", "subscribe_notify");	
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_imm, "_status_notification_provider", "");
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_imm, "_status_notification_type", "httpcb");
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_imm, "_status_notification_target", "http://135.17.38.71:1664/test");
	    	
	    	Response cbResponse = startImmediateCallback(callbackServiceName_imm, "5115");
			assertEquals(200, cbResponse.getStatusCode());
			
			Response customHTTPresponse=getCustomHTTPServerContent();
			
			String body = customHTTPresponse.getBody().asString();
			for(String s : events)assertTrue(body.contains(s));
				//System.out.printf("no notification");};
			}*/
		   		    	
	    	    
	    // _cbe_on_service_create
	    public void test2() throws Exception {
	    	
	    	System.out.printf("TEST2 _cbe_on_service_create notification \n");
	    	
	    	execCmdCommandOnRemoteHost("call C:/start_customhttp.bat");
	    	Thread.sleep(50000);
	    	
	    	env.createTransaction("GMS_Events", "CFGTRTList");
	    	env.addSectionToTransaction("GMS_Events", "properties");
	    	
	    	env.addOptionToTransaction("GMS_Events","properties", "_enable_status_notification", "subscribe_notify"); 
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_provider", "");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_type", "httpcb");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_target", "http://135.17.38.71:1664/test");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_attempts", "3");
	    	
	    	env.addSectionToTransaction("GMS_Events", "_cbe_on_service_create");
	    	env.addOptionToTransaction("GMS_Events", "_cbe_on_service_create", "notify_params", "_service_id,_service_name,_customer_number,_urs_virtual_queue");
	    	
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_imm, "_enable_status_notification", "subscribe_notify");	
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_imm, "_callback_events_list", "GMS_Events");	
	    	
	    	Response cbResponse = startImmediateCallback(callbackServiceName_imm, "5115");
			assertEquals(200, cbResponse.getStatusCode());
			
		    Thread.sleep(10000);
			
			Response customHTTPresponse=getCustomHTTPServerContent();
			
			String body = customHTTPresponse.getBody().asString();
			assertTrue(body.contains("_cbe_on_service_create"));
		   		
	    }
	    
	    @Test
	    // _cbe_on_virtual_ixn_create
	    public void test3() throws Exception {
	    	
	    	System.out.printf("TEST3 _cbe_on_virtual_ixn_create notification \n");
	    	
	    	execCmdCommandOnRemoteHost("call C:/start_customhttp.bat");
	    	
	    	env.createTransaction("GMS_Events", "CFGTRTList");
	    	env.addSectionToTransaction("GMS_Events", "properties");
	    	
	    	env.addOptionToTransaction("GMS_Events","properties", "_enable_status_notification", "subscribe_notify"); 
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_provider", "");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_type", "httpcb");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_target", "http://135.17.38.71:1664/test");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_attempts", "3");
	    	
	    	env.addSectionToTransaction("GMS_Events", "_cbe_on_virtual_ixn_create");
	    	env.addOptionToTransaction("GMS_Events", "_cbe_on_virtual_ixn_create", "notify_params", "_service_id,_service_name");
	    	
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_imm, "_enable_status_notification", "subscribe_notify");	
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_imm, "_callback_events_list", "GMS_Events");	
	    	
	    	Response cbResponse = startImmediateCallback(callbackServiceName_imm, "5115");
			assertEquals(200, cbResponse.getStatusCode());
		
			Response customHTTPresponse=getCustomHTTPServerContent();
			
			String body = customHTTPresponse.getBody().asString();
			assertTrue(body.contains("_cbe_on_virtual_ixn_create"));
			
	    }
	    
	    @Test
	    // _cbe_on_dial_init
	    public void test4() throws Exception {
	    	
	    	System.out.printf("TEST4 _cbe_on_dial_init notification \n");
	    	
	    	execCmdCommandOnRemoteHost("call C:/start_customhttp.bat");
	    	Thread.sleep(50000);
	    	
	    	env.createTransaction("GMS_Events", "CFGTRTList");
	    	env.addSectionToTransaction("GMS_Events", "properties");
	    	
	    	env.addOptionToTransaction("GMS_Events","properties", "_enable_status_notification", "subscribe_notify"); 
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_provider", "");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_type", "httpcb");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_target", "http://135.17.38.71:1664/test");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_attempts", "3");
	    	
	    	env.addSectionToTransaction("GMS_Events", "_cbe_on_dial_init");
	    	env.addOptionToTransaction("GMS_Events", "_cbe_on_dial_init", "notify_params", "_service_id,_service_name,_customer_number,_c_dialed_number");
	    	
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_imm, "_enable_status_notification", "subscribe_notify");	
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_imm, "_callback_events_list", "GMS_Events");	
	    	
	    	Response cbResponse = startImmediateCallback(callbackServiceName_imm, "5115");
			assertEquals(200, cbResponse.getStatusCode());
				
			Response customHTTPresponse=getCustomHTTPServerContent();
			
			String body = customHTTPresponse.getBody().asString();
			assertTrue(body.contains("_cbe_on_dial_init"));
			
	    }
	    
	    @Test
	    // _cbe_on_connect_treatment_start
	    public void test5() throws Exception {
	    	
	    	System.out.printf("TEST5 _cbe_on_connect_treatment_start notification \n");
	    	
	    	execCmdCommandOnRemoteHost("call C:/start_customhttp.bat");
	    	Thread.sleep(50000);
	    	
	    	env.createTransaction("GMS_Events", "CFGTRTList");
	    	env.addSectionToTransaction("GMS_Events", "properties");
	    	
	    	env.addOptionToTransaction("GMS_Events","properties", "_enable_status_notification", "subscribe_notify"); 
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_provider", "");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_type", "httpcb");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_target", "http://135.17.38.71:1664/test");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_attempts", "3");

	    	
	    	env.addSectionToTransaction("GMS_Events", "_cbe_on_connect_treatment_start");
	    	env.addOptionToTransaction("GMS_Events", "_cbe_on_connect_treatment_start", "notify_params", "_service_id,_service_name,_vq_for_outbound_calls,_c_dialed_number");
	    	
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_imm, "_enable_status_notification", "subscribe_notify");	
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_imm, "_callback_events_list", "GMS_Events");
			env.changeOptionValueInService(gmsAppName, callbackServiceName_imm, "_treatment_customer_connect", "http://localhost:8080/genesys/1/document/service_template/callback/Resources/SampleTreatments/customer_announcement.wav");
	    	
	    	Response cbResponse = startImmediateCallback(callbackServiceName_imm, "5115");
			assertEquals(200, cbResponse.getStatusCode());
			
			Response customHTTPresponse=getCustomHTTPServerContent();
			
			String body = customHTTPresponse.getBody().asString();
			assertTrue(body.contains("_cbe_on_connect_treatment_start"));
			
	    }
	    
	    @Test
	    // _cbe_on_service_exit
	    public void test6() throws Exception {
	    	
	    	System.out.printf("TEST6 _cbe_on_service_exit notification \n");
	    	
	    	execCmdCommandOnRemoteHost("call C:/start_customhttp.bat");
	    	Thread.sleep(50000);
	    	
	    	env.createTransaction("GMS_Events", "CFGTRTList");
	    	env.addSectionToTransaction("GMS_Events", "properties");
	    	
	    	env.addOptionToTransaction("GMS_Events","properties", "_enable_status_notification", "subscribe_notify"); 
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_provider", "");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_type", "httpcb");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_target", "http://135.17.38.71:1664/test");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_attempts", "3");
	    	
	    	env.addSectionToTransaction("GMS_Events", "_cbe_on_service_exit");
	    	env.addOptionToTransaction("GMS_Events", "_cbe_on_service_exit", "notify_params", "_service_id,_service_name,_c_dialed_number,_c_termination_type");
	    	
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_imm, "_enable_status_notification", "subscribe_notify");	
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_imm, "_callback_events_list", "GMS_Events");	
	    	
	    	Response cbResponse = startImmediateCallback(callbackServiceName_imm, "5115");
			assertEquals(200, cbResponse.getStatusCode());
			String callbackID = cbResponse.then().extract().path("_id");
			cancelCallback(callbackServiceName_imm, callbackID).then().assertThat().statusCode(200);
			waitingForCallbackState(callbackID, "COMPLETED", 66, POLLING_TIME_MILLIS);

			Response customHTTPresponse=getCustomHTTPServerContent();
			
			String body = customHTTPresponse.getBody().asString();
			assertTrue(body.contains("_cbe_on_service_exit"));
			
	    }
	    
	    @Test
	    // _cbe_on_callback_cancelled
	    public void test7() throws Exception {
	    	
	    	System.out.printf("TEST7 _cbe_on_callback_cancelled notification \n");
	    	
	    	execCmdCommandOnRemoteHost("call C:/start_customhttp.bat");
	    	Thread.sleep(50000);
	    	
	    	env.createTransaction("GMS_Events", "CFGTRTList");
	    	env.addSectionToTransaction("GMS_Events", "properties");
	    	
	    	env.addOptionToTransaction("GMS_Events","properties", "_enable_status_notification", "subscribe_notify"); 
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_provider", "");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_type", "httpcb");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_target", "http://135.17.38.71:1664/test");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_attempts", "3");
	    	
	    	env.addSectionToTransaction("GMS_Events", "_cbe_on_callback_cancelled");
	    	env.addOptionToTransaction("GMS_Events", "_cbe_on_callback_cancelled", "notify_params", "_service_id,_service_name,_desired_time,_customer_number,_v_queue");
	    	
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_imm, "_enable_status_notification", "subscribe_notify");	
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_imm, "_callback_events_list", "GMS_Events");	
	    	
	    	Response cbResponse = startImmediateCallback(callbackServiceName_imm, "5115");
			assertEquals(200, cbResponse.getStatusCode());
			String callbackID = cbResponse.then().extract().path("_id");
			cancelCallback(callbackServiceName_imm, callbackID).then().assertThat().statusCode(200);
			waitingForCallbackState(callbackID, "COMPLETED", 66, POLLING_TIME_MILLIS);

			Response customHTTPresponse=getCustomHTTPServerContent();
			
			String body = customHTTPresponse.getBody().asString();
			assertTrue(body.contains("_cbe_on_callback_cancelled"));
			
	    }
	    
	    @Test
	    // _cbe_on_callback_queued
	    public void test8() throws Exception {
	    	
	    	System.out.printf("TEST8 _cbe_on_callback_queued notification \n");
	    	Thread.sleep(50000);
	    	
	    	execCmdCommandOnRemoteHost("call C:/start_customhttp.bat");
	    	
	    	env.createTransaction("GMS_Events", "CFGTRTList");
	    	env.addSectionToTransaction("GMS_Events", "properties");
	    	
	    	env.addOptionToTransaction("GMS_Events","properties", "_enable_status_notification", "subscribe_notify"); 
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_provider", "");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_type", "httpcb");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_target", "http://135.17.38.71:1664/test");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_attempts", "3");
	    	
	    	env.addSectionToTransaction("GMS_Events", "_cbe_on_callback_queued");
	    	env.addOptionToTransaction("GMS_Events", "_cbe_on_callback_queued", "notify_params", "_service_id,_service_name,_desired_time,_customer_number,_v_queue");
	    	
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_imm, "_enable_status_notification", "subscribe_notify");	
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_imm, "_callback_events_list", "GMS_Events");	
	    	
	    	Response cbResponse = startImmediateCallback(callbackServiceName_imm, "5115");
			assertEquals(200, cbResponse.getStatusCode());
			
			Response customHTTPresponse=getCustomHTTPServerContent();
			
			String body = customHTTPresponse.getBody().asString();
			assertTrue(body.contains("_cbe_on_callback_queued"));
			
	    }
	    
	    @Test
	    // _cbe_on_callback_status_updated	
	    public void test9() throws Exception {
	    	
	    	System.out.printf("TEST9 _cbe_on_callback_status_updated notification \n");
	    	
	    	execCmdCommandOnRemoteHost("call C:/start_customhttp.bat");
	    	Thread.sleep(50000);
	    	
	    	env.createTransaction("GMS_Events", "CFGTRTList");
	    	env.addSectionToTransaction("GMS_Events", "properties");
	    	
	    	env.addOptionToTransaction("GMS_Events","properties", "_enable_status_notification", "subscribe_notify"); 
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_provider", "");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_type", "httpcb");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_target", "http://135.17.38.71:1664/test");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_attempts", "3");
	    	
	    	env.addSectionToTransaction("GMS_Events", "_cbe_on_callback_status_updated");
	    	env.addOptionToTransaction("GMS_Events", "_cbe_on_callback_status_updated", "notify_params", "_service_id,_service_name,_desired_time,_customer_number,_v_queue");
	    	
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_imm, "_enable_status_notification", "subscribe_notify");	
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_imm, "_callback_events_list", "GMS_Events");	
	    	
	    	Response cbResponse = startImmediateCallback(callbackServiceName_imm, "5115");
			assertEquals(200, cbResponse.getStatusCode());
			
			Response customHTTPresponse=getCustomHTTPServerContent();
			
			String body = customHTTPresponse.getBody().asString();
			assertTrue(body.contains("_cbe_on_callback_status_updated"));
			
	    }
	    /*
	    //_cbe_on_callback_status_updated	 ???
	    public void test999() throws Exception {
	    	
	    	//System.out.printf("START TEST");
	    	
	    	execCmdCommandOnRemoteHost("call C:/start_customhttp.bat");
	    	
	    	env.createTransaction("GMS_Events", "CFGTRTList");
	    	env.addSectionToTransaction("GMS_Events", "properties");
	    	
	    	env.addOptionToTransaction("GMS_Events","properties", "_enable_status_notification", "subscribe_notify"); 
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_provider", "");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_type", "httpcb");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_target", "http://135.17.38.71:1664/test");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_attempts", "3");
	    	
	    	env.addSectionToTransaction("GMS_Events", "_cbe_on_service_create");
	    	env.addOptionToTransaction("GMS_Events", "_cbe_on_service_create", "notify_params", "_service_id,_service_name,_customer_number,_urs_virtual_queue");
	    	
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_imm, "_enable_status_notification", "subscribe_notify");	
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_imm, "_callback_events_list", "GMS_Events");	
	    	
	    	Response cbResponse = startImmediateCallback(callbackServiceName_imm, "5115");
			assertEquals(200, cbResponse.getStatusCode());
			
		    Thread.sleep(10000);
			
			Response customHTTPresponse=getCustomHTTPServerContent();
			
			String body = customHTTPresponse.getBody().asString();
			assertTrue(body.contains("_cbe_on_service_create"));
			
	    }
	    */
	    
	    @Test
	    // _cbe_on_callback_submitted	
	    public void test10() throws Exception {
	    	
	    	System.out.printf("TEST10 _cbe_on_callback_submitted notification \n");
	    	
	    	execCmdCommandOnRemoteHost("call C:/start_customhttp.bat");
	    	Thread.sleep(50000);
	    	
	    	env.createTransaction("GMS_Events", "CFGTRTList");
	    	env.addSectionToTransaction("GMS_Events", "properties");
	    	
	    	env.addOptionToTransaction("GMS_Events","properties", "_enable_status_notification", "subscribe_notify"); 
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_provider", "");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_type", "httpcb");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_target", "http://135.17.38.71:1664/test");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_attempts", "3");
	    	
	    	env.addSectionToTransaction("GMS_Events", "_cbe_on_callback_submitted");
	    	env.addOptionToTransaction("GMS_Events", "_cbe_on_callback_submitted", "notify_params", "_service_id,_service_name,_desired_time,_customer_number,_v_queue");
	    	
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_imm, "_enable_status_notification", "subscribe_notify");	
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_imm, "_callback_events_list", "GMS_Events");	
	    	
	    	Response cbResponse = startImmediateCallback(callbackServiceName_imm, "5115");
			assertEquals(200, cbResponse.getStatusCode());
			
			Response customHTTPresponse=getCustomHTTPServerContent();
			
			String body = customHTTPresponse.getBody().asString();
			assertTrue(body.contains("_cbe_on_callback_submitted"));
			
	    }
	    
	    @Test
	    // _cbe_on_callback_scheduled
	    public void test11() throws Exception {
	    	
	    	System.out.printf("TEST11 _cbe_on_callback_scheduled notification \n");
	    	
	    	execCmdCommandOnRemoteHost("call C:/start_customhttp.bat");
	    	Thread.sleep(50000);
	    	
	    	env.createTransaction("GMS_Events", "CFGTRTList");
	    	env.addSectionToTransaction("GMS_Events", "properties");
	    	
	    	env.addOptionToTransaction("GMS_Events","properties", "_enable_status_notification", "subscribe_notify"); 
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_provider", "");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_type", "httpcb");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_target", "http://135.17.38.71:1664/test");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_attempts", "3");
	    	
	    	env.addSectionToTransaction("GMS_Events", "_cbe_on_callback_scheduled");
	    	env.addOptionToTransaction("GMS_Events", "_cbe_on_callback_scheduled", "notify_params", "_service_id,_service_name,_desired_time,_customer_number,_v_queue");
	    	
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_sch, "_enable_status_notification", "subscribe_notify");
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_sch, "_callback_events_list", "GMS_Events");
	    	
	    	Response cbResponse = startScheduledCallback(callbackServiceName_sch, "5115",180);
			assertEquals(200, cbResponse.getStatusCode());
			String callbackID = cbResponse.then().extract().path("_id");
			waitingForCallbackState(callbackID, "SCHEDULED", 66, POLLING_TIME_MILLIS);

			Response customHTTPresponse=getCustomHTTPServerContent();
			
			String body = customHTTPresponse.getBody().asString();
			assertTrue(body.contains("_cbe_on_callback_scheduled"));
			
	    }
	    
	    @Test
	    // _cbe_on_callback_rescheduled
	    public void test12() throws Exception {
	    	
	    	System.out.printf("TEST12 _cbe_on_callback_rescheduled notification \n");
	    	
	    	execCmdCommandOnRemoteHost("call C:/start_customhttp.bat");
	    	Thread.sleep(50000);
	    	
	    	env.createTransaction("GMS_Events", "CFGTRTList");
	    	env.addSectionToTransaction("GMS_Events", "properties");
	    	
	    	env.addOptionToTransaction("GMS_Events","properties", "_enable_status_notification", "subscribe_notify"); 
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_provider", "");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_type", "httpcb");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_target", "http://135.17.38.71:1664/test");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_attempts", "3");
	    	
	    	env.addSectionToTransaction("GMS_Events", "_cbe_on_callback_rescheduled");
	    	env.addOptionToTransaction("GMS_Events", "_cbe_on_callback_rescheduled", "notify_params", "_service_id,_service_name,_desired_time,_customer_number,_v_queue");
	    	
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_sch, "_enable_status_notification", "subscribe_notify");
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_sch, "_callback_events_list", "GMS_Events");

			Response cbResponse = startScheduledCallback(callbackServiceName_sch, "5115",180);
			assertEquals(200, cbResponse.getStatusCode());
			String callbackID = cbResponse.then().extract().path("_id");
			waitingForCallbackState(callbackID, "SCHEDULED", 66, POLLING_TIME_MILLIS);
			
			Response customHTTPresponse=getCustomHTTPServerContent();
			
			String body = customHTTPresponse.getBody().asString();
			assertTrue(body.contains("_cbe_on_callback_rescheduled"));
			
	    }
	    
	    @Test
	    //_cbe_on_callback_submit_failed
	    public void test13() throws Exception {
	    	
	    	System.out.printf("TEST13 _cbe_on_callback_submit_failed notification \n");
	    	
	    	execCmdCommandOnRemoteHost("call C:/start_customhttp.bat");
	    	Thread.sleep(50000);
	    	
	    	env.createTransaction("GMS_Events", "CFGTRTList");
	    	env.addSectionToTransaction("GMS_Events", "properties");
	    	
	    	env.addOptionToTransaction("GMS_Events","properties", "_enable_status_notification", "subscribe_notify"); 
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_provider", "");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_type", "httpcb");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_target", "http://135.17.38.71:1664/test");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_attempts", "3");
	    	
	    	env.addSectionToTransaction("GMS_Events", "_cbe_on_callback_submit_failed");
	    	env.addOptionToTransaction("GMS_Events", "_cbe_on_callback_submit_failed", "notify_params", "_service_id,_service_name,_desired_time,_customer_number,_v_queue");
	    	
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_imm, "_enable_status_notification", "subscribe_notify");	
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_imm, "_callback_events_list", "GMS_Events");	
	    	
	    	Response cbResponse = startImmediateCallback(callbackServiceName_imm, "5115");
			assertEquals(200, cbResponse.getStatusCode());
			
			Response customHTTPresponse=getCustomHTTPServerContent();
			
			String body = customHTTPresponse.getBody().asString();
			assertTrue(body.contains("_cbe_on_callback_submit_failed"));
			
	    }
	    /*
	  //_cbe_on_callback_processing_failed
	    public void test14() throws Exception {
	    	
	    	//System.out.printf("START TEST");
	    	
	    	execCmdCommandOnRemoteHost("call C:/start_customhttp.bat");
	    	
	    	env.createTransaction("GMS_Events", "CFGTRTList");
	    	env.addSectionToTransaction("GMS_Events", "properties");
	    	
	    	env.addOptionToTransaction("GMS_Events","properties", "_enable_status_notification", "subscribe_notify"); 
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_provider", "");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_type", "httpcb");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_target", "http://135.17.38.71:1664/test");
	    	env.addOptionToTransaction("GMS_Events","properties", "_status_notification_attempts", "3");
	    	
	    	env.addSectionToTransaction("GMS_Events", "_cbe_on_service_create");
	    	env.addOptionToTransaction("GMS_Events", "_cbe_on_service_create", "notify_params", "_service_id,_service_name,_customer_number,_urs_virtual_queue");
	    	
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_imm, "_enable_status_notification", "subscribe_notify");	
	    	env.changeOptionValueInService(gmsAppName, callbackServiceName_imm, "_callback_events_list", "GMS_Events");	
	    	
	    	Response cbResponse = startImmediateCallback(callbackServiceName_imm, "5115");
			assertEquals(200, cbResponse.getStatusCode());
			
		    Thread.sleep(10000);
			
			Response customHTTPresponse=getCustomHTTPServerContent();
			
			String body = customHTTPresponse.getBody().asString();
			assertTrue(body.contains("_cbe_on_service_create"));
			
	    }
	    */

	private Response getCustomHTTPServerContent() {
		String requestURL = "http://"+remoteHost+":1664/test";
		Response r = RestAssured.given().contentType(ContentType.JSON).get(requestURL);
		System.out.printf("Content of CustomHTTPServer is: \n %s%n", r.then().extract().response().asString());
		return r;
	}
	    
}
