package gms.api;

import com.genesyslab.ats.cfg.actor.CfgManager;
import com.genesyslab.ats.cfg.actor.app.OptionsApi;
import com.genesyslab.functional.tests.gms.psdk.Reconfiguration;
import com.genesyslab.platform.commons.collections.KeyValueCollection;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.genesyslab.functional.tests.gms.helper.CallbackServicesAPI.*;
import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertyConfiguration;
import static com.genesyslab.functional.tests.gms.helper.SectionOptions.getGMSCallbackSectionOptions;
import static com.genesyslab.functional.tests.gms.helper.ServiceOptions.getUserTermSchCallbackServiceOptions;
import static com.genesyslab.functional.tests.gms.helper.TimeOperations.getMethodExecutionTime;
import static com.genesyslab.functional.tests.gms.helper.TimeOperations.getUTCTimeInFuture;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CallbackAPITests {
	
	private static String callbackServiceName = "cb_new";
	private static String businessHoursServiceName= "business_hours";
	private static String phoneNumber="5115";
	
	private static CfgManager cfgManager = new CfgManager();
	private static String gmsAppName = getPropertyConfiguration("gms.app.name");
	//private static String gmsClusterAppName = getPropertyConfiguration("gms.cluster.app.name");
	//private static String orsAppName = getPropertyConfiguration("ors.app.name");
	
	//private static SCSManager scsManager = new SCSManager(cfgManager);
	
	private static Properties properties = new Properties();
	

	@BeforeClass
	public static void oneTimeSetUp() {
		
		long startTime = System.nanoTime();
		
		try {
			properties.load(new FileInputStream("./config.properties"));
			//scsManager.init(properties);
			cfgManager.init(properties);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi()
					.addSection("service." + callbackServiceName, getUserTermSchCallbackServiceOptions());
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().addSection("callback",
					getGMSCallbackSectionOptions());
			//scsManager.restartApplication(gmsAppName);
			//scsManager.restartApplication(orsAppName);
			System.out.println("@BeforeClass method processing...");
			cancelUnfinishedCallbacks(phoneNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		long endTime = System.nanoTime();
		getMethodExecutionTime(startTime, endTime, "@BeforeClass");
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		
		cfgManager.deactivate();
		
	}

	@After
	public void tearDown() {
		System.out.print("@After method processing...: ");
		cancelUnfinishedCallbacks(phoneNumber);
	}
	
	//****************************************************************************************
	// GMS-4672	Ensure GMS in non-shared mode does not handle ContactCenterId
	
	
	/**
	 * Get callbacks on queue without ContactCenterId header
	 * @throws Exception
	 */
	
	@Test
	public void test1_01() throws Exception {

		// reconfigure corresponding callback service options
		tuneCallbackService(callbackServiceName, "USERTERMINATED", "false", "false");

		// Step1 : schedule new callback with header ContactCenterId
		Map<String, String> map = new HashMap<String, String>();
		map.put("ContactCenterId", "xxx");
		
		Response cbResponse = startImmediateCallback(callbackServiceName, phoneNumber, map);
		assertEquals(200, cbResponse.getStatusCode());
		System.out.println(
				"Response body for callback creating: " + cbResponse.then().extract().response().asString());
		
		// save scheduled callback id to variable
		String callbackID = cbResponse.then().extract().path("_id");
		
		//Steps 2 : get callbacks on queue
		Response cbInQueueResponse = queryCallbackByQueue();		
		assertEquals(200, cbInQueueResponse.getStatusCode());
		
		String body = cbInQueueResponse.getBody().asString();
		assertTrue(body.contains(callbackID));

		// delete callback
		cancelCallback(callbackServiceName, callbackID).then().assertThat().statusCode(200);
	}
	
	/**
	 * Get callbacks on queue with ContactCenterId header
	 * @throws Exception
	 */
	
	@Test
	public void test1_02() throws Exception {

		// reconfigure corresponding callback service options
		tuneCallbackService(callbackServiceName, "USERTERMINATED", "false", "false");

		// Step1 : schedule new callback with header ContactCenterId
		Map<String, String> map = new HashMap<String, String>();
		map.put("ContactCenterId", "xxx");
		
		Response cbResponse = startImmediateCallback(callbackServiceName, phoneNumber, map);
		assertEquals(200, cbResponse.getStatusCode());
		System.out.println(
				"Response body for callback creating: " + cbResponse.then().extract().response().asString());
		
		// save scheduled callback id to variable
		String callbackID = cbResponse.then().extract().path("_id");
		
		//Steps 2 : get callbacks on queue		
		Response cbInQueueResponse = queryCallbackByQueue(map);		
		assertEquals(200, cbInQueueResponse.getStatusCode());
		
		String body = cbInQueueResponse.getBody().asString();
		assertTrue(body.contains(callbackID));

		// delete callback
		cancelCallback(callbackServiceName, callbackID).then().assertThat().statusCode(200);
	}
	
	
	//GMS-5183	Callback update with empty value
	@Test
	public void test1_03() throws Exception {
		String subscriptionId = "SUB-123456";
		String customerNumber ="5115";
		callbackServiceName="GMS-5183";	
		//------------------------------------------------------------------------------
		 KeyValueCollection kv = getUserTermSchCallbackServiceOptions();
			kv.addString("_vq", "SIP_VQ_SIP_Switch");
		
			
			cfgManager.init(properties);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().addSection("service." + callbackServiceName,
					kv);		
			
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi()
					.addSection("service." + callbackServiceName, kv);
			tuneCallbackService2(callbackServiceName, "USERTERMINATED", "false", "false");
			
			 KeyValueCollection kv2 = new KeyValueCollection();
			 kv2.addString("_bh_add1", "");
			 kv2.addString("_bh_regular1", "Mon-Fri 08:00-20:00");
			 kv2.addString("_holiday1", "2020-07-15");
			 kv2.addString("_ors", "http://localhost:7210");
			 kv2.addString("_service", "office-hours");
			 kv2.addString("_timezone", "UTC");
			 kv2.addString("_ttl", "30");
			 kv2.addString("_type", "ors");
	
			 cfgManager.init(properties);
			cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi().addSection("service."+ businessHoursServiceName,
					kv2);
		//-------------------------------------------------------------------------------------
			
		// Step 1. Create the callback request
		Map map = new HashMap();
		map.put("_customer_number", customerNumber);
		map.put("_desired_time", getUTCTimeInFuture(90000));
		map.put("_service_name", callbackServiceName);
		map.put("GCS_TransferringNotepad", "WLAN Probleme");
		map.put("CB_SUBSCRIPTION_ID",subscriptionId);
		map.put("_customer_id", "111222333");
		
	
		Response cbResponse = startScheduledCallback(callbackServiceName,customerNumber,map);
		assertEquals(200, cbResponse.getStatusCode());
		System.out.println(
				"Response body for callback creating: " + cbResponse.then().extract().response().asString());
		
		String callbackID = cbResponse.then().extract().path("_id");
		
		
//		Step 2. Get the callback details
		
		cbResponse = queryCallbackByID(callbackID);
		assertEquals(200, cbResponse.getStatusCode());
	
		String body = cbResponse.getBody().asString();
		String cbSubscibId= from(body).getString("CB_SUBSCRIPTION_ID");
		cbSubscibId=cbSubscibId.substring(1, cbSubscibId.length()-1);
		
		System.out.println("\n"+cbSubscibId);		
	
		assertEquals(subscriptionId, cbSubscibId);
		
//      Step 3. Update callback request (CB_SUBSCRIPTION_ID key only)
		map = new HashMap();
		map.put("_customer_number", customerNumber);
		map.put("_desired_time", getUTCTimeInFuture(90000));
		map.put("_service_name", callbackServiceName);
		map.put("GCS_TransferringNotepad", "WLAN Probleme");
		map.put("CB_SUBSCRIPTION_ID","Test");
		map.put("_customer_id", "111222333");
		cbResponse = updateCallback(callbackServiceName,callbackID, customerNumber,map);
		assertEquals(200, cbResponse.getStatusCode());
		
//		Step 4. Get the updated callback details
		
		cbResponse = queryCallbackByID(callbackID);
		assertEquals(200, cbResponse.getStatusCode());
			
		body = cbResponse.getBody().asString();
		cbSubscibId= from(body).getString("CB_SUBSCRIPTION_ID");
		cbSubscibId=cbSubscibId.substring(1, cbSubscibId.length()-1);
		
		System.out.println("\n"+cbSubscibId);		
	
		assertEquals("Test", cbSubscibId);
		
//      Step 5. Update callback request (CB_SUBSCRIPTION_ID key only) empty String
		map = new HashMap();
		map.put("_customer_number", customerNumber);
		map.put("_desired_time", getUTCTimeInFuture(90000));
		map.put("_service_name", callbackServiceName);
		map.put("GCS_TransferringNotepad", "WLAN Probleme");
		map.put("CB_SUBSCRIPTION_ID","");
		map.put("_customer_id", "111222333");
		cbResponse = updateCallback(callbackServiceName,callbackID,customerNumber,map);
		assertEquals(200, cbResponse.getStatusCode());
		
//		Step 6. Get the updated callback details
		
		cbResponse = queryCallbackByID(callbackID);
		assertEquals(200, cbResponse.getStatusCode());
		String rbody = cbResponse.getBody().asString();
		assertTrue(!rbody.contains("CB_SUBSCRIPTION_ID"));	

		// delete callback
		cancelCallback(callbackServiceName, callbackID).then().assertThat().statusCode(200);
	}
	
	
	

	private static void cancelUnfinishedCallbacks(String customerNumber) {
		Response callbackDetails = queryCallbackByState(customerNumber, "!COMPLETED");
		ArrayList<Map<String, ?>> jsonAsArrayList = from(callbackDetails.then().extract().response().asString())
				.get("");
		if (jsonAsArrayList.size() > 0) {
			for (int i = 0; i != jsonAsArrayList.size(); ++i) {
				Response cbDetails = queryCallbackByState(customerNumber, "!COMPLETED");
				String cbID = parseArrayInResponse(cbDetails.then().extract().response().asString(), "_id");
				String cbServiceName = parseArrayInResponse(cbDetails.then().extract().response().asString(),
						"_service_name");
				System.out.println("Callbacks in '!COMPLETED' state before cleaning cycle #" + (i + 1) + ": "
						+ cbDetails.then().extract().response().asString());
				assertThat(cbDetails.getStatusCode(), equalTo(200));
				Response cancellation = cancelCallback(cbServiceName, cbID);
				System.out.println("Status code for callback cancellation #" + (i + 1) + ": "
						+ cancellation.then().extract().response().getStatusCode());
			}
		} else
			System.out.println("There are no callbacks in '!COMPLETED' state for customer number: " + customerNumber
					+ " at the current moment.");
	}

	// modify the default callback service configuration in CME

	private void tuneCallbackService(String callbackServiceName, String call_direction, String wait_for_agent,
			String wait_for_user_confirm) throws Exception {
		OptionsApi serviceForTuning = cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi()
				.setSection("service." + callbackServiceName);
		serviceForTuning.changeOptionValue("_call_direction", call_direction);

		serviceForTuning.changeOptionValue("_wait_for_agent", wait_for_agent);
		serviceForTuning.changeOptionValue("_wait_for_user_confirm", wait_for_user_confirm);
		serviceForTuning.changeOptionValue("_capacity_service", "");
	}

	private void configureOptions(String callbackServiceName){
		Reconfiguration r = new Reconfiguration();
		try {
			r.addSection(gmsAppName, "service."+callbackServiceName, getUserTermSchCallbackServiceOptions());
			tuneCallbackService2(callbackServiceName, "USERTERMINATED", "false", "false");
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}
	private void tuneCallbackService2(String callbackServiceName, String call_direction, String wait_for_agent,
			String wait_for_user_confirm) throws Exception {
		OptionsApi serviceForTuning = cfgManager.getAppApi().setApp(gmsAppName).getOptionsApi()
				.setSection("service." + callbackServiceName);
		serviceForTuning.changeOptionValue("_call_direction", call_direction);

		serviceForTuning.changeOptionValue("_wait_for_agent", wait_for_agent);
		serviceForTuning.changeOptionValue("_wait_for_user_confirm", wait_for_user_confirm);
		serviceForTuning.changeOptionValue("_agent_availability_notification_delay", "5");
		serviceForTuning.changeOptionValue("_customer_lookup_keys", "_customer_number,CB_SUBSCRIPTION_ID");
		serviceForTuning.changeOptionValue("_eta_pos_threshold","0:10,10:5,20:2,30:1,40:1,50:0" );
		serviceForTuning.changeOptionValue("_use_reporting_aggregator", "true");	
		//serviceForTuning.changeOptionValue("_vq", "SIP_VQ_SIP_Switch"); add this to kv directly
		serviceForTuning.changeOptionValue("_business_hours_service", businessHoursServiceName);
		serviceForTuning.changeOptionValue("_capacity_service", "");
	
	}
	private static String parseArrayInResponse(String jsonAsString, String key) {
		ArrayList<Map<String, ?>> jsonAsArrayList = from(jsonAsString).get("");
		return jsonAsArrayList.get(0).get(key).toString();
	}

	


}
