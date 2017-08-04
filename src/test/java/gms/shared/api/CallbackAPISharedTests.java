package gms.shared.api;

import com.genesyslab.ats.cfg.actor.CfgManager;
import com.genesyslab.ats.cfg.actor.app.OptionsApi;
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
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

public class CallbackAPISharedTests {
	private static String callbackServiceName = "cb_new";
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
	//	cancelUnfinishedCallbacks(phoneNumber);
	}
	
	//****************************************************************************************
	// GMS-5034	Callback: NPE when using bad ContactCenterId in scheduled callback
	
	
	/**
	 * NullpointerException is missing using bad ContactCenterId in scheduled callback in shared gms
	 * @throws Exception
	 */
	
	@Test
	public void test1_01() throws Exception {
		String contactCenterId="test";
		
		// reconfigure corresponding callback service options
		tuneCallbackService(callbackServiceName, "USERTERMINATED", "true", "true");

		// Step1 : schedule new callback with header ContactCenterId
		Map<String, String> header = new HashMap<String, String>();
		header.put("ContactCenterId", contactCenterId);		
		
		
		Response response =startScheduledCallback(callbackServiceName, phoneNumber, 30000, header);
		assertEquals(400, response.getStatusCode());
		System.out.println(
				"Response body for callback creating: " + response.then().extract().response().asString());
		
		
		String message =from(response.asString()).get("message").toString();
		assertEquals("Callback Contact Center ID: " + contactCenterId + " is not configured in transaction list.", message);
		
		String exception =   from(response.asString()).get("exception").toString();
		assertEquals("com.genesyslab.gsg.services.callback.exceptions.CallbackExceptionNotFound", exception);
		

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
	}

	

	private static String parseArrayInResponse(String jsonAsString, String key) {
		ArrayList<Map<String, ?>> jsonAsArrayList = from(jsonAsString).get("");
		return jsonAsArrayList.get(0).get(key).toString();
	}

}
