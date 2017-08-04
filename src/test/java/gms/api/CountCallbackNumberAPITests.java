package gms.api;

import com.genesyslab.ats.ex.AtsCfgComponentException;
import com.genesyslab.functional.tests.gms.helper.CallbackServicesAPI;
import com.genesyslab.functional.tests.gms.psdk.Reconfiguration;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.genesyslab.functional.tests.gms.helper.CallbackServiceHelper.cancelUnfinishedCallbacks;
import static com.genesyslab.functional.tests.gms.helper.CallbackServiceHelper.semiAutomaticCallbackProcessing;
import static com.genesyslab.functional.tests.gms.helper.CallbackServicesAPI.*;
import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertiesFile;
import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertyConfiguration;
import static com.genesyslab.functional.tests.gms.helper.ServiceOptions.getUserTermSchCallbackServiceOptions;
import static com.genesyslab.functional.tests.gms.helper.TimeOperations.getMethodExecutionTime;
import static com.genesyslab.functional.tests.gms.helper.URLsHelper.getGMSBaseURL;
import static org.hamcrest.Matchers.*;

/**
 * Created by bvolovyk on 17.03.2017.
 */

/*
 * [Test Suite] API to count number of callbacks: https://jira.genesys.com/browse/GMS-4315
 */
public class CountCallbackNumberAPITests {
    private static String propertiesFile = getPropertiesFile();
    //    private static String propertiesFile = "./config_2nd.properties";//uncomment this line in case you want to use another .properties file
    private static Reconfiguration env = new Reconfiguration(propertiesFile);
    private static String gmsClusterAppName = getPropertyConfiguration(propertiesFile, "gms.cluster.app.name");
    private static String customerNumber_1 = "5115";
    private static String customerNumber_2 = "5125";
    private static String callbackSchServiceName_1 = "cb_term_sch_gms-3502_1";
    private static String callbackSchServiceName_2 = "cb_term_sch_gms-3502_2";

    private static final int SCHEDULED_CB_TIME = 200;
    private static final int SCHEDULED_QUEUED_CB_TIME = 15;
    private static final long DELAY_FOR_PLACING_CALLBACK_TO_QUEUE = 10;//placing to queue takes approximately 10 sec main. problem with this
    private static final long POLLING_TIME_MILLIS = 1000;
    private static final long TIMEOUT_FOR_SEMI_AUTOMATIC_PART = 140;


    @BeforeClass
    public static void oneTimeSetUp() throws AtsCfgComponentException {
        long startTime = System.nanoTime();
        System.out.println("@BeforeClass method processing...");
        env.createService(gmsClusterAppName, callbackSchServiceName_1, getUserTermSchCallbackServiceOptions());
        env.createService(gmsClusterAppName, callbackSchServiceName_2, getUserTermSchCallbackServiceOptions());
        CallbackServicesAPI.setGMSBaseURL(getGMSBaseURL(propertiesFile));
        cancelUnfinishedCallbacks(Integer.parseInt(customerNumber_1));
        long endTime = System.nanoTime();
        getMethodExecutionTime(startTime, endTime, "@BeforeClass");
    }

    @After
    public void tearDown() {
        System.out.println("@After method processing...");
        cancelUnfinishedCallbacks(Integer.parseInt(customerNumber_1));
        cancelUnfinishedCallbacks(Integer.parseInt(customerNumber_2));
    }

    @AfterClass
    public static void oneTimeTearDown() throws Exception {
        env.deleteService(gmsClusterAppName, callbackSchServiceName_1); //for troubleshooting purposes comment this command
        env.deleteService(gmsClusterAppName, callbackSchServiceName_2); //for troubleshooting purposes comment this command
        env.deactivate();
    }

    @Test
    public void test_01() throws InterruptedException {
        Thread.sleep(DELAY_FOR_PLACING_CALLBACK_TO_QUEUE * 1000);
        //create request
        Response query = queryCounterWatermarks();
        query
                .then()
                .assertThat().statusCode(200)
                .and()
                .assertThat().body("services." + callbackSchServiceName_1, equalTo(0))
                .and()
                .assertThat().body("services." + callbackSchServiceName_2, equalTo(0))
                .and()
                .assertThat().body("total", equalTo(0));
    }

    @Test
    public void test_02() throws InterruptedException {
        Thread.sleep(DELAY_FOR_PLACING_CALLBACK_TO_QUEUE * 1000);
        //create URI parameters for request
        List<String> uriParams = new ArrayList<>();
        uriParams.add(callbackSchServiceName_1);
        //create request
        Response query = queryCounterWatermarks(uriParams);
        query
                .then()
                .assertThat().statusCode(200)
                .and()
                .assertThat().body("services." + callbackSchServiceName_1, equalTo(0))
                .and()
                .assertThat().body(not(containsString(callbackSchServiceName_2)))
                .and()
                .assertThat().body("total", equalTo(0));
    }

    @Test
    public void test_03() throws InterruptedException {
        //schedule new callback
        Response scheduleCallback = startScheduledCallback(callbackSchServiceName_1, customerNumber_1, SCHEDULED_CB_TIME);
        scheduleCallback.then().assertThat().statusCode(200);
        Thread.sleep(DELAY_FOR_PLACING_CALLBACK_TO_QUEUE * 1000);
        //create request
        Response query = queryCounterWatermarks();
        query
                .then()
                .assertThat().statusCode(200)
                .and()
                .assertThat().body("services." + callbackSchServiceName_1, equalTo(0))
                .and()
                .assertThat().body("services." + callbackSchServiceName_2, equalTo(0))
                .and()
                .assertThat().body("total", equalTo(0));
    }

    @Test
    public void test_04() throws InterruptedException {
        //schedule new callback
        Response scheduleCallback = startScheduledCallback(callbackSchServiceName_1, customerNumber_1, SCHEDULED_CB_TIME);
        scheduleCallback.then().assertThat().statusCode(200);
        Thread.sleep(DELAY_FOR_PLACING_CALLBACK_TO_QUEUE * 1000);
        //create URI parameters for request
        List<String> uriParams = new ArrayList<>();
        uriParams.add(callbackSchServiceName_1);
        //create request
        Response query = queryCounterWatermarks(uriParams);
        query
                .then()
                .assertThat().statusCode(200)
                .and()
                .assertThat().body("services." + callbackSchServiceName_1, equalTo(0))
                .and()
                .assertThat().body(not(containsString(callbackSchServiceName_2)))
                .and()
                .assertThat().body("total", equalTo(0));
    }

    @Test
    public void test_05() throws InterruptedException {
        //schedule new callback
        Response scheduleCallback = startScheduledCallback(callbackSchServiceName_1, customerNumber_1, SCHEDULED_QUEUED_CB_TIME);
        scheduleCallback.then().assertThat().statusCode(200);
        Thread.sleep(DELAY_FOR_PLACING_CALLBACK_TO_QUEUE * 1000);
        //create URI parameters for request
        List<String> uriParams = new ArrayList<>();
        uriParams.add(callbackSchServiceName_1);
        //create request
        Response query = queryCounterWatermarks(uriParams);
        query
                .then()
                .assertThat().statusCode(200)
                .and()
                .assertThat().body("services." + callbackSchServiceName_1, equalTo(1))
                .and()
                .assertThat().body(not(containsString(callbackSchServiceName_2)))
                .and()
                .assertThat().body("total", equalTo(1));
    }

    @Test //semi-automated scenario for PROCESSING state
    public void test_06() throws InterruptedException {
        //schedule new callback
        Response scheduleCallback = startScheduledCallback(callbackSchServiceName_1, customerNumber_1, SCHEDULED_QUEUED_CB_TIME);
        scheduleCallback.then().assertThat().statusCode(200);
        String callbackId = scheduleCallback.then().extract().path("_id");
        Thread.sleep(DELAY_FOR_PLACING_CALLBACK_TO_QUEUE * 1000);

        //TODO: implement automatic answer on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId, "PROCESSING", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);

        //create request
        Response query = queryCounterWatermarks();
        query
                .then()
                .assertThat().statusCode(200)
                .and()
                .assertThat().body("services." + callbackSchServiceName_1, equalTo(1))
                .and()
                .assertThat().body("services." + callbackSchServiceName_2, equalTo(0))
                .and()
                .assertThat().body("total", equalTo(1));

        //TODO: implement automatic hang up on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId, "COMPLETED", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);
    }

    @Test //semi-automated scenario for PROCESSING state
    public void test_07() throws InterruptedException {
        //schedule new callback
        Response scheduleCallback = startScheduledCallback(callbackSchServiceName_1, customerNumber_1, 5);
        scheduleCallback.then().assertThat().statusCode(200);
        String callbackId = scheduleCallback.then().extract().path("_id");
        Thread.sleep(DELAY_FOR_PLACING_CALLBACK_TO_QUEUE * 1000);

        //TODO: implement automatic answer on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId, "PROCESSING", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);

        //create URI parameters for request
        Map<String, String> uriParams = new HashMap<>();
        uriParams.put("service_name", callbackSchServiceName_1);
        uriParams.put("service_name", callbackSchServiceName_2);
        //create request
        Response query = queryCounterWatermarks();
        query
                .then()
                .assertThat().statusCode(200)
                .and()
                .assertThat().body("services." + callbackSchServiceName_1, equalTo(1))
                .and()
                .assertThat().body("services." + callbackSchServiceName_2, equalTo(0))
                .and()
                .assertThat().body("total", equalTo(1));

        //TODO: implement automatic hang up on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId, "COMPLETED", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);
    }

    @Test
    public void test_08() throws InterruptedException {
        //schedule new callback
        Response scheduleCallback = startScheduledCallback(callbackSchServiceName_1, customerNumber_1, SCHEDULED_CB_TIME);
        scheduleCallback.then().assertThat().statusCode(200);
        Thread.sleep(DELAY_FOR_PLACING_CALLBACK_TO_QUEUE * 1000);
        cancelCallback(callbackSchServiceName_1, scheduleCallback.then().extract().path("_id").toString())
                .then()
                .assertThat()
                .statusCode(200);
        Thread.sleep(DELAY_FOR_PLACING_CALLBACK_TO_QUEUE * 1000);
        //create URI parameters for request
        List<String> uriParams = new ArrayList<>();
        uriParams.add(callbackSchServiceName_1);
        //create request
        Response query = queryCounterWatermarks(uriParams);
        query
                .then()
                .assertThat().statusCode(200)
                .and()
                .assertThat().body("services." + callbackSchServiceName_1, equalTo(0))
                .and()
                .assertThat().body(not(containsString(callbackSchServiceName_2)))
                .and()
                .assertThat().body("total", equalTo(0));
    }

    @Test
    public void test_09() throws InterruptedException {
        //schedule new callbacks
        Response scheduleCallback_1 = startScheduledCallback(callbackSchServiceName_1, customerNumber_1, SCHEDULED_QUEUED_CB_TIME);
        scheduleCallback_1.then().assertThat().statusCode(200);
        Response scheduleCallback_2 = startScheduledCallback(callbackSchServiceName_2, customerNumber_2, SCHEDULED_QUEUED_CB_TIME);
        scheduleCallback_2.then().assertThat().statusCode(200);
        Thread.sleep(DELAY_FOR_PLACING_CALLBACK_TO_QUEUE * 1000);
        //create URI parameters for request
        List<String> uriParams = new ArrayList<>();
        uriParams.add(callbackSchServiceName_1);
        uriParams.add(callbackSchServiceName_2);
        //create request
        Response query = queryCounterWatermarks(uriParams);
        query
                .then()
                .assertThat().statusCode(200)
                .and()
                .assertThat().body("services." + callbackSchServiceName_1, equalTo(1))
                .and()
                .assertThat().body("services." + callbackSchServiceName_2, equalTo(1))
                .and()
                .assertThat().body("total", equalTo(2));
    }

    @Test
    public void test_10() throws InterruptedException {
        //schedule new callbacks
        Response scheduleCallback_1 = startScheduledCallback(callbackSchServiceName_1, customerNumber_1, SCHEDULED_CB_TIME);
        scheduleCallback_1.then().assertThat().statusCode(200);
        Response scheduleCallback_2 = startScheduledCallback(callbackSchServiceName_2, customerNumber_2, SCHEDULED_QUEUED_CB_TIME);
        scheduleCallback_2.then().assertThat().statusCode(200);
        Thread.sleep(DELAY_FOR_PLACING_CALLBACK_TO_QUEUE * 1000);
        //create URI parameters for request
        List<String> uriParams = new ArrayList<>();
        uriParams.add(callbackSchServiceName_1);
        uriParams.add(callbackSchServiceName_2);
        //create request
        Response query = queryCounterWatermarks(uriParams);
        query
                .then()
                .assertThat().statusCode(200)
                .and()
                .assertThat().body("services." + callbackSchServiceName_1, equalTo(0))
                .and()
                .assertThat().body("services." + callbackSchServiceName_2, equalTo(1))
                .and()
                .assertThat().body("total", equalTo(1));
    }

    @Test
    public void test_11() throws InterruptedException {
        //schedule new callbacks
        Response scheduleCallback_1 = startScheduledCallback(callbackSchServiceName_1, customerNumber_1, SCHEDULED_CB_TIME);
        scheduleCallback_1.then().assertThat().statusCode(200);
        Response scheduleCallback_2 = startScheduledCallback(callbackSchServiceName_2, customerNumber_2, SCHEDULED_QUEUED_CB_TIME);
        scheduleCallback_2.then().assertThat().statusCode(200);
        Thread.sleep(DELAY_FOR_PLACING_CALLBACK_TO_QUEUE * 1000);
        //place first callback in COMPLETED state
        cancelCallback(callbackSchServiceName_1, scheduleCallback_1.then().extract().path("_id").toString())
                .then()
                .assertThat()
                .statusCode(200);
        Thread.sleep(DELAY_FOR_PLACING_CALLBACK_TO_QUEUE * 1000);
        //create request
        Response query = queryCounterWatermarks();
        query
                .then()
                .assertThat().statusCode(200)
                .and()
                .assertThat().body("services." + callbackSchServiceName_1, equalTo(0))
                .and()
                .assertThat().body("services." + callbackSchServiceName_2, equalTo(1))
                .and()
                .assertThat().body("total", equalTo(1));
    }

    @Test //semi-automated scenario for PROCESSING state
    public void test_12() throws InterruptedException {
        //schedule new callbacks
        Response scheduleCallback_1 = startScheduledCallback(callbackSchServiceName_1, customerNumber_1, SCHEDULED_CB_TIME);
        scheduleCallback_1.then().assertThat().statusCode(200);
        Response scheduleCallback_2 = startScheduledCallback(callbackSchServiceName_2, customerNumber_2, SCHEDULED_QUEUED_CB_TIME);
        scheduleCallback_2.then().assertThat().statusCode(200);
        String callbackId_2 = scheduleCallback_2.then().extract().path("_id");
        Thread.sleep(DELAY_FOR_PLACING_CALLBACK_TO_QUEUE * 1000);
        //place first callback in COMPLETED state
        cancelCallback(callbackSchServiceName_1, scheduleCallback_1.then().extract().path("_id").toString())
                .then()
                .assertThat()
                .statusCode(200);
        Thread.sleep(DELAY_FOR_PLACING_CALLBACK_TO_QUEUE * 1000);

        //TODO: implement automatic answer on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId_2, "PROCESSING", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);

        //create URI parameters for request
        List<String> uriParams = new ArrayList<>();
        uriParams.add(callbackSchServiceName_1);
        uriParams.add(callbackSchServiceName_2);
        //create request
        Response query = queryCounterWatermarks(uriParams);
        query
                .then()
                .assertThat().statusCode(200)
                .and()
                .assertThat().body("services." + callbackSchServiceName_1, equalTo(0))
                .and()
                .assertThat().body("services." + callbackSchServiceName_2, equalTo(1))
                .and()
                .assertThat().body("total", equalTo(1));

        //TODO: implement automatic hang up on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId_2, "COMPLETED", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);
    }

    @Test //semi-automated scenario for PROCESSING state; two agents should be Ready
    public void test_13() throws InterruptedException {
        //schedule new callbacks
        Response scheduleCallback_1 = startScheduledCallback(callbackSchServiceName_1, customerNumber_1, SCHEDULED_QUEUED_CB_TIME);
        scheduleCallback_1.then().assertThat().statusCode(200);
        String callbackId_1 = scheduleCallback_1.then().extract().path("_id");
        Response scheduleCallback_2 = startScheduledCallback(callbackSchServiceName_2, customerNumber_2, SCHEDULED_QUEUED_CB_TIME);
        scheduleCallback_2.then().assertThat().statusCode(200);
        String callbackId_2 = scheduleCallback_2.then().extract().path("_id");
        Thread.sleep(DELAY_FOR_PLACING_CALLBACK_TO_QUEUE * 1000);

        //TODO: implement automatic answer on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId_1, "PROCESSING", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);

        semiAutomaticCallbackProcessing(callbackId_2, "PROCESSING", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);

        //create request
        Response query = queryCounterWatermarks();
        query
                .then()
                .assertThat().statusCode(200)
                .and()
                .assertThat().body("services." + callbackSchServiceName_1, equalTo(1))
                .and()
                .assertThat().body("services." + callbackSchServiceName_2, equalTo(1))
                .and()
                .assertThat().body("total", equalTo(2));

        //TODO: implement automatic hang up on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId_1, "COMPLETED", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);

        semiAutomaticCallbackProcessing(callbackId_2, "COMPLETED", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);
    }

    @Test //semi-automated scenario for PROCESSING state; two agents should be Ready
    public void test_14() throws InterruptedException {
        //schedule new callbacks
        Response scheduleCallback_1 = startScheduledCallback(callbackSchServiceName_1, customerNumber_1, SCHEDULED_QUEUED_CB_TIME);
        scheduleCallback_1.then().assertThat().statusCode(200);
        String callbackId_1 = scheduleCallback_1.then().extract().path("_id");
        Response scheduleCallback_2 = startScheduledCallback(callbackSchServiceName_2, customerNumber_2, SCHEDULED_QUEUED_CB_TIME);
        scheduleCallback_2.then().assertThat().statusCode(200);
        String callbackId_2 = scheduleCallback_2.then().extract().path("_id");
        Thread.sleep(DELAY_FOR_PLACING_CALLBACK_TO_QUEUE * 1000);

        //TODO: implement automatic answer on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId_1, "PROCESSING", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);

        semiAutomaticCallbackProcessing(callbackId_2, "PROCESSING", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);

        //create URI parameters for request
        List<String> uriParams = new ArrayList<>();
        uriParams.add(callbackSchServiceName_1);
        //create request
        Response query = queryCounterWatermarks(uriParams);
        query
                .then()
                .assertThat().statusCode(200)
                .and()
                .assertThat().body("services." + callbackSchServiceName_1, equalTo(1))
                .and()
                .assertThat().body(not(containsString(callbackSchServiceName_2)))
                .and()
                .assertThat().body("total", equalTo(1));

        //TODO: implement automatic hang up on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId_1, "COMPLETED", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);

        semiAutomaticCallbackProcessing(callbackId_2, "COMPLETED", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);
    }
}

