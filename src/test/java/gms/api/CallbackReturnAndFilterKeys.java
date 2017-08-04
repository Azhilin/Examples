package gms.api;

import com.genesyslab.functional.tests.gms.helper.CallbackServicesAPI;
import com.genesyslab.functional.tests.gms.psdk.Reconfiguration;
import com.genesyslab.platform.commons.collections.KeyValueCollection;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.genesyslab.functional.tests.gms.helper.CallbackServiceHelper.*;
import static com.genesyslab.functional.tests.gms.helper.CallbackServicesAPI.queryCallbackByQueue;
import static com.genesyslab.functional.tests.gms.helper.CallbackServicesAPI.startImmediateCallback;
import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertyConfiguration;
import static com.genesyslab.functional.tests.gms.helper.RemoteHostOperations.*;
import static com.genesyslab.functional.tests.gms.helper.ServiceOptions.*;
import static com.genesyslab.functional.tests.gms.helper.TimeOperations.*;
import static com.genesyslab.functional.tests.gms.helper.URLsHelper.getGMSBaseURL;
import static com.genesyslab.functional.tests.gms.helper.URLsHelper.getGMSFullLoginURL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/*
 * created by Olga Ukolova on 07/25/2017
 */

public class CallbackReturnAndFilterKeys {
	    private static String propertiesFile = "./config_2nd.properties";//uncomment this line in case you want to use another .properties file
		private static Reconfiguration env = new Reconfiguration(propertiesFile);

		private static String gmsAppName = getPropertyConfiguration(propertiesFile, "gms.app.name");//uncomment this line in case you want to use another application name
		private static String gmsBaseUrl = getGMSBaseURL(propertiesFile);
		private static String gmsUrl = getGMSFullLoginURL(propertiesFile);
		private static String username = getPropertyConfiguration(propertiesFile, "gms.user.name");
		private static String password = getPropertyConfiguration(propertiesFile, "gms.user.password");
		private static String customerNumber = "5115";
		
		private static String callbackServiceName_imm = "cb_gms-5158_imm";
		private static String BHServiceName = "bh_24x7";
	    private static String capacityServiceName = "cap_1000x24x7";

	    @BeforeClass
	    public static void oneTimeSetUp() throws Exception {
	        long startTime = System.nanoTime();
	        System.out.println("@BeforeClass method processing...");
	        CallbackServicesAPI.setGMSBaseURL(getGMSBaseURL(propertiesFile));
	        long endTime = System.nanoTime();
	        getMethodExecutionTime(startTime, endTime, "@BeforeClass");
	     	          
	        env.createService(gmsAppName, callbackServiceName_imm, getUserTermImmCallbackServiceOptions());
	        env.createService(gmsAppName, BHServiceName, getRegularOfficeHours());
	        env.createService(gmsAppName, capacityServiceName, getCapacityOptions());       		
	   }
	  
	    @AfterClass
	    public static void oneTimeTearDown() throws Exception {
	        System.out.println("@AfterClass method processing...");
	       // env.deleteService(gmsAppName, callbackServiceName_imm); //for troubleshooting purposes comment this command
	        //env.deleteService(gmsAppName, BHServiceName); //for troubleshooting purposes comment this command
	        //env.deleteService(gmsAppName, capacityServiceName); //for troubleshooting purposes comment this command
	        env.deactivate();
	    }
	    
	    @After
	    public void tearDown() {
	        System.out.println("@After method processing...");
	        cancelUnfinishedCallbacks(callbackServiceName_imm);
	    }

	    
	    @Test
	    // 
	    public void test1() throws Exception {
	    	 // env.addOptionToSection(gmsAppName, "service." + callbackServiceName_imm, "_reject_past_desired_time", String.valueOf(seconds));	
	    	KeyValueCollection callbackSectionOptionList = new KeyValueCollection();
	        callbackSectionOptionList.addString("filter-keys", "_service_type");
	        callbackSectionOptionList.addString("returned-keys", "_service_type");
	        
	    	env.addSection(gmsAppName, "callback", callbackSectionOptionList);
	    	
	    	
	    	Random random = new Random();
	    	// generate a random integer from 0 to 899, then add 100
	    	int x = random.nextInt(10000);
	    	String y="test"+ String.valueOf(x);
	    	
	    	System.out.println(
					"_service_type = " + String.valueOf(y));
	    	
	    	Map<String, String> map = new HashMap<String, String>();
			map.put("_customer_number", "5115");
			map.put("_urs_virtual_queue", "SIP_VQ_SIP_Switch");
			map.put("_service_type", y);
			
			Response cbResponse = startImmediateCallback(callbackServiceName_imm, map);
			assertEquals(200, cbResponse.getStatusCode());
			
			// save callback id to variable
			String callbackID = cbResponse.then().extract().path("_id");
			
			//get callbacks on queue		
			Response cbInQueueResponse = queryCallbackByQueue(map);		
			assertEquals(200, cbInQueueResponse.getStatusCode());
			
			String body = cbInQueueResponse.getBody().asString();
			assertTrue(body.contains(callbackID));
			assertTrue(body.contains(y));
			
			map.clear();
			map.put("_service_type", y);
			
			String startTime=getCurrentDate()+"T"+BEGIN_OF_DAY_SUFFIX.trim()+".000Z";
			
								
			int cb_amount=countCallbacksByAPI(startTime,map);
			System.out.println(
					"Count of callbacks: " + String.valueOf(cb_amount));
			
			assertEquals(cb_amount, 1);
				    	
	    }
	    
	    @Test
	    // 
	    public void test2() throws Exception {
	   // env.addOptionToSection(gmsAppName, "service." + callbackServiceName_imm, "_reject_past_desired_time", String.valueOf(seconds));	
	    	KeyValueCollection callbackSectionOptionList = new KeyValueCollection();
	        callbackSectionOptionList.addString("filter-keys", "_callback_state");
	        	        
	    	env.addSection(gmsAppName, "callback", callbackSectionOptionList);
	    	
	    		    	    	
	    	Map<String, String> map = new HashMap<String, String>();
			map.put("_customer_number", "5115");
			map.put("_urs_virtual_queue", "SIP_VQ_SIP_Switch");
			map.put("_callback_state", "QUEUED");
			
			Response cbResponse = startImmediateCallback(callbackServiceName_imm, map);
			assertEquals(200, cbResponse.getStatusCode());
			
			// save callback id to variable
			String callbackID = cbResponse.then().extract().path("_id");
			
			//get callbacks on queue		
			Response cbInQueueResponse = queryCallbackByQueue(map);		
			assertEquals(200, cbInQueueResponse.getStatusCode());
			
			String body = cbInQueueResponse.getBody().asString();
			assertTrue(body.contains(callbackID));
						
			map.clear();
			map.put("_callback_state", "QUEUED");
			
			String startTime=getCurrentDate()+"T"+BEGIN_OF_DAY_SUFFIX.trim()+".000Z";
			
								
			int cb_amount=countCallbacksByAPI(startTime,map);
			System.out.println(
					"Count of callbacks: " + String.valueOf(cb_amount));
			
			assertEquals(cb_amount, 1);
		
	    	
	    }
	    
	    
}


