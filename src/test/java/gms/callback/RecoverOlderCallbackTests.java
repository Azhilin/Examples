package gms.callback;

import com.genesyslab.ats.ex.AtsCfgComponentException;
import com.genesyslab.functional.tests.gms.helper.CallbackServicesAPI;
import com.genesyslab.functional.tests.gms.helper.OrchestrationServerAPI;
import com.genesyslab.functional.tests.gms.psdk.Reconfiguration;
import com.genesyslab.functional.tests.gms.ui.GMSLoginPage;
import com.genesyslab.functional.tests.gms.ui.GMSMainPage;
import com.genesyslab.functional.tests.gms.ui.configuredservices.GMSConfiguredServicesPage;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static com.genesyslab.functional.tests.gms.helper.CallbackServiceHelper.*;
import static com.genesyslab.functional.tests.gms.helper.CallbackServicesAPI.*;
import static com.genesyslab.functional.tests.gms.helper.OrchestrationServerAPI.killORSSession;
import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertyConfiguration;
import static com.genesyslab.functional.tests.gms.helper.SectionOptions.getGMSCallbackSectionOptions;
import static com.genesyslab.functional.tests.gms.helper.ServiceOptions.getUserTermSchCallbackServiceOptions;
import static com.genesyslab.functional.tests.gms.helper.TimeOperations.getMethodExecutionTime;
import static com.genesyslab.functional.tests.gms.helper.URLsHelper.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by bvolovyk on 25.11.2016.
 */

/*
 * [Test Suite] Recover older Callback (resubmit periodically): https://jira.genesys.com/browse/GMS-4224
 */
public class RecoverOlderCallbackTests {
    //    private static String propertiesFile = getPropertiesFile();
    private static String propertiesFile = "./config_2nd.properties";//uncomment this line in case you want to use another .properties file
    private static Reconfiguration env = new Reconfiguration(propertiesFile);
    private static String gmsClusterAppName = getPropertyConfiguration(propertiesFile, "gms.cluster.app.name");
    private static String gmsAppName = getPropertyConfiguration(propertiesFile, "gms.app.name");
    private static String orsAppName = getPropertyConfiguration(propertiesFile, "ors.app.name");
    private static String gmsUrl = getGMSFullLoginURL(propertiesFile);
    private static String username = getPropertyConfiguration(propertiesFile, "gms.user.name");
    private static String password = getPropertyConfiguration(propertiesFile, "gms.user.password");
    private static String customerNumber_1 = "5115";
    private static String customerNumber_2 = "5125";
    private static String callbackSchServiceName = "cb_term_sch_gms-3984";
    private static String callbackSchServiceName_UI = null;
    private static String sectionName = "callback";

    private static final int SCHEDULED_QUEUED_CB_TIME = 15; //(in seconds)
    private static final int SCHEDULED_CB_TIME = 30; //(in seconds)
    private static final long GMS_START_DURATION = 45; //(in seconds) time needed for GMS launching
    private static final int MAX_POSSIBLE_EWT = 1800; //set >300 sec to improve reliability of TCs (should be <10000 anyway!!!)
    private static final long POLLING_TIME_MILLIS = 500;

    private WebDriver driver = null;
    private GMSLoginPage loginPage = null;
    private GMSMainPage mainPage = null;
    private GMSConfiguredServicesPage configuredServicesPage = null;

    @BeforeClass
    public static void oneTimeSetUp() throws AtsCfgComponentException {
        long startTime = System.nanoTime();
        System.out.println("@BeforeClass method processing...");
        env.addSection(gmsClusterAppName, sectionName, getGMSCallbackSectionOptions());
        env.createService(gmsClusterAppName, callbackSchServiceName, getUserTermSchCallbackServiceOptions());
//            env.restartApplication(gmsAppName);
//            env.restartApplication(orsAppName);
        CallbackServicesAPI.setGMSBaseURL(getGMSBaseURL(propertiesFile));
        OrchestrationServerAPI.setORSBaseURL(getORSBaseURL(propertiesFile));
        cancelUnfinishedCallbacks(Integer.parseInt(customerNumber_1));
        long endTime = System.nanoTime();
        getMethodExecutionTime(startTime, endTime, "@BeforeClass");
    }

    @After
    public void tearDown() throws AtsCfgComponentException {
        System.out.println("@After method processing...");
        cancelUnfinishedCallbacks(Integer.parseInt(customerNumber_1));
        if (driver != null)
            driver.quit();
        if (callbackSchServiceName_UI != null) {
            env.deleteService(gmsClusterAppName, callbackSchServiceName_UI); //for troubleshooting purposes comment this command
        }
        callbackSchServiceName_UI = null;
    }

    @AfterClass
    public static void oneTimeTearDown() throws Exception {
        System.out.println("@AfterClass method processing...");
        env.addSection(gmsClusterAppName, sectionName, getGMSCallbackSectionOptions());
        env.deleteService(gmsClusterAppName, callbackSchServiceName); //for troubleshooting purposes comment this command
        Thread.sleep(500);
        env.deactivate();
    }

    @Test
    public void test1_01() throws Exception {
        //reconfigure corresponding callback service options
        tuneCallbackService(callbackSchServiceName, "-2,-1", "-2,120",
                "-1440,-2", "15");
        //reconfigure corresponding callback section options
        tuneCallbackSection("5", "60");
        //schedule new callback and save it id to variable
        Response scheduleCallback = startScheduledCallback(callbackSchServiceName, customerNumber_1, SCHEDULED_CB_TIME);
        String callbackId = scheduleCallback.then().extract().path("_id");

        assertThat(getCallbackDetailByKey(callbackId, "_callback_state"), equalTo("SCHEDULED"));
        env.restartApplicationWithDelay(gmsAppName, 150000 - GMS_START_DURATION * 1000);
        waitingForCallbackState(callbackId, "QUEUED", 80, POLLING_TIME_MILLIS);
        assertThat(getCallbackEWT(callbackId), lessThanOrEqualTo(MAX_POSSIBLE_EWT));
        cancelCallback(callbackSchServiceName, callbackId).then().assertThat().statusCode(200);
    }

    @Test
    public void test1_02() throws Exception {
        //reconfigure corresponding callback service options
        tuneCallbackService(callbackSchServiceName, "-2,2", "-2,120",
                "-1440,-2", "15");
        //reconfigure corresponding callback section options
        tuneCallbackSection("5", "60");
        //schedule new callback and save it id to variable
        Response scheduleCallback = startScheduledCallback(callbackSchServiceName, customerNumber_1, SCHEDULED_CB_TIME);
        String callbackId = scheduleCallback.then().extract().path("_id");

        assertThat(getCallbackDetailByKey(callbackId, "_callback_state"), equalTo("SCHEDULED"));
        env.restartApplicationWithDelay(gmsAppName, 110000 - GMS_START_DURATION * 1000);
        waitingForCallbackState(callbackId, "QUEUED", 66, POLLING_TIME_MILLIS);
        assertThat(getCallbackEWT(callbackId), lessThanOrEqualTo(MAX_POSSIBLE_EWT));
        cancelCallback(callbackSchServiceName, callbackId).then().assertThat().statusCode(200);
    }

    @Test
    public void test1_03() throws Exception {
        //reconfigure corresponding callback service options
        tuneCallbackService(callbackSchServiceName, "-2,-1", "-2,120",
                "-1440,-2", "15");
        //reconfigure corresponding callback section options
        tuneCallbackSection("5", "60");
        //schedule new callback and save it id to variable
        Response scheduleCallback = startScheduledCallback(callbackSchServiceName, customerNumber_1, SCHEDULED_QUEUED_CB_TIME);
        String callbackId = scheduleCallback.then().extract().path("_id");

        assertThat(getCallbackDetailByKey(callbackId, "_callback_state"), equalTo("QUEUED"));
        env.restartApplicationWithDelay(gmsAppName, 135000 - GMS_START_DURATION * 1000);
        waitingForCallbackState(callbackId, "QUEUED", 66, POLLING_TIME_MILLIS);
        assertThat(getCallbackEWT(callbackId), lessThanOrEqualTo(MAX_POSSIBLE_EWT));
        cancelCallback(callbackSchServiceName, callbackId).then().assertThat().statusCode(200);
    }

    @Test//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public void test1_04() throws Exception {
        //reconfigure corresponding callback service options
        tuneCallbackService(callbackSchServiceName, "-2,-1", "-2,120",
                "-1440,-2", "15");
        //reconfigure corresponding callback section options
        tuneCallbackSection("5", "60");
        //schedule new callback and save it id to variable
        Response scheduleCallback = startScheduledCallback(callbackSchServiceName, customerNumber_1, SCHEDULED_CB_TIME);
        String callbackId = scheduleCallback.then().extract().path("_id");

        assertThat(getCallbackDetailByKey(callbackId, "_callback_state"), equalTo("SCHEDULED"));
        env.restartApplicationWithDelay(orsAppName, 150000);
        waitingForCallbackState(callbackId, "COMPLETED", 66, POLLING_TIME_MILLIS);
        assertThat(getCallbackDetailByKey(callbackId, "_callback_reason"), equalTo("SUBMIT_FAILED"));
        Thread.sleep(10000);//to prevent effect on the next tests
    }

    @Test//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public void test1_05() throws Exception {
        //reconfigure corresponding callback service options
        tuneCallbackService(callbackSchServiceName, "-2,-1", "-2,120",
                "-1440,-2", "15");
        //reconfigure corresponding callback section options
        tuneCallbackSection("5", "60");
        //schedule new callback and save it id to variable
        Response scheduleCallback = startScheduledCallback(callbackSchServiceName, customerNumber_1, SCHEDULED_QUEUED_CB_TIME);
        String callbackId = scheduleCallback.then().extract().path("_id");

        assertThat(getCallbackDetailByKey(callbackId, "_callback_state"), equalTo("QUEUED"));
        env.restartApplicationWithDelay(orsAppName, 135000);
        waitingForCallbackState(callbackId, "COMPLETED", 66, POLLING_TIME_MILLIS);
        assertThat(getCallbackDetailByKey(callbackId, "_callback_reason"), equalTo("SUBMIT_FAILED"));
        Thread.sleep(10000);//to prevent effect on the next tests
    }

    @Test
    public void test1_06() throws Exception {
        //reconfigure corresponding callback service options
        tuneCallbackService(callbackSchServiceName, "-2,-1", "-2,120",
                "-1440,-2", "15");
        //reconfigure corresponding callback section options
        tuneCallbackSection("5", "60");
        //schedule new callback and save it id to variable
        Response scheduleCallback = startScheduledCallback(callbackSchServiceName, customerNumber_1, SCHEDULED_QUEUED_CB_TIME);
        String callbackId = scheduleCallback.then().extract().path("_id");

        assertThat(getCallbackDetailByKey(callbackId, "_callback_state"), equalTo("QUEUED"));
        //terminate ORS session
        killORSSession(getCallbackDetailByKey(callbackId, "_ors_session_id"))
                .then()
                .assertThat()
                .statusCode(200);
        env.restartApplicationWithDelay(gmsAppName, 135000 - GMS_START_DURATION * 1000);
        //check that callback still not re-submitted
        checkQueuePosition(callbackId)
                .then()
                .assertThat()
                .statusCode(500)
                .and()
                .body(containsString("404 Not Found"));
        waitingForCallbackEWTValue(callbackId, MAX_POSSIBLE_EWT, 66, POLLING_TIME_MILLIS);
        cancelCallback(callbackSchServiceName, callbackId).then().assertThat().statusCode(200);
    }

    @Test
    public void test1_07() throws Exception {
        //reconfigure corresponding callback service options
        tuneCallbackService(callbackSchServiceName, "-2,-1", "-2,120",
                "-3,-2", "15");
        //reconfigure corresponding callback section options
        tuneCallbackSection("5", "60");
        //schedule new callback and save it id to variable
        Response scheduleCallback = startScheduledCallback(callbackSchServiceName, customerNumber_1, SCHEDULED_CB_TIME);
        String callbackId = scheduleCallback.then().extract().path("_id");

        assertThat(getCallbackDetailByKey(callbackId, "_callback_state"), equalTo("SCHEDULED"));
        env.restartApplicationWithDelay(gmsAppName, 210000 - GMS_START_DURATION * 1000);
        assertThat(getCallbackDetailByKey(callbackId, "_callback_state"), equalTo("SCHEDULED"));
        System.out.printf("Waiting %s seconds until time coming...%n", 66);
        Thread.sleep(66000);//wait queue-polling-rate-recover + 6 sec
        //check that callback state is the same still
        assertThat(getCallbackDetailByKey(callbackId, "_callback_state"), equalTo("SCHEDULED"));
        cancelCallback(callbackSchServiceName, callbackId).then().assertThat().statusCode(200);
    }

    @Test
    public void test1_08() throws Exception {
        //reconfigure corresponding callback service options
        tuneCallbackService(callbackSchServiceName, "-2,-1", "-2,120",
                "-3,-2", "15");
        //reconfigure corresponding callback section options
        tuneCallbackSection("5", "60");
        //schedule new callback and save it id to variable
        Response scheduleCallback = startScheduledCallback(callbackSchServiceName, customerNumber_1, SCHEDULED_QUEUED_CB_TIME);
        String callbackId = scheduleCallback.then().extract().path("_id");

        assertThat(getCallbackDetailByKey(callbackId, "_callback_state"), equalTo("QUEUED"));
        env.restartApplicationWithDelay(gmsAppName, 195000 - GMS_START_DURATION * 1000);
        waitingForCallbackEWTValue(callbackId, MAX_POSSIBLE_EWT, 66, POLLING_TIME_MILLIS);
        cancelCallback(callbackSchServiceName, callbackId).then().assertThat().statusCode(200);
    }

    @Test
    public void test1_09() throws Exception {
        //reconfigure corresponding callback service options
        tuneCallbackService(callbackSchServiceName, "-2,-1", "-2,120",
                "-3,-2", "15");
        //reconfigure corresponding callback section options
        tuneCallbackSection("5", "60");
        //schedule new callback and save it id to variable
        Response scheduleCallback = startScheduledCallback(callbackSchServiceName, customerNumber_1, SCHEDULED_QUEUED_CB_TIME);
        String callbackId = scheduleCallback.then().extract().path("_id");

        assertThat(getCallbackDetailByKey(callbackId, "_callback_state"), equalTo("QUEUED"));
        //terminate ORS session
        killORSSession(getCallbackDetailByKey(callbackId, "_ors_session_id"))
                .then()
                .assertThat()
                .statusCode(200);
        env.restartApplicationWithDelay(gmsAppName, 195000 - GMS_START_DURATION * 1000);
        assertThat(getCallbackDetailByKey(callbackId, "_callback_state"), equalTo("QUEUED"));
        //check that callback is not re-submitted
        checkQueuePosition(callbackId)
                .then()
                .assertThat()
                .statusCode(500)
                .and()
                .body(containsString("404 Not Found"));
        System.out.printf("Waiting %s seconds until time coming...%n", 66);
        Thread.sleep(66000);//wait queue-polling-rate-recover + 6 sec
        //check that callback still not re-submitted
        checkQueuePosition(callbackId)
                .then()
                .assertThat()
                .statusCode(500)
                .and()
                .body(containsString("404 Not Found"));
        //check that callback state is the same still
        assertThat(getCallbackDetailByKey(callbackId, "_callback_state"), equalTo("QUEUED"));
        cancelCallback(callbackSchServiceName, callbackId).then().assertThat().statusCode(400);
    }

    @Test
    public void test1_10() throws Exception {
        //add second callback named as current
        env.createService(gmsClusterAppName, callbackSchServiceName + "_cur", getUserTermSchCallbackServiceOptions());
        //reconfigure corresponding callback service options
        tuneCallbackService(callbackSchServiceName, "-3,-1", "-3,120",
                "-1440,-3", "15");
        //reconfigure corresponding callback_cur service options
        tuneCallbackService(callbackSchServiceName + "_cur", "-3,-1", "-3,120",
                "-1440,-3", "180");
        //reconfigure corresponding callback section options
        tuneCallbackSection("5", "60");
        //schedule new callback and save it id to variable
        Response scheduleCallback = startScheduledCallback(callbackSchServiceName, customerNumber_1, SCHEDULED_QUEUED_CB_TIME);
        String callbackId = scheduleCallback.then().extract().path("_id");
        //schedule new callback_cur and save it id to variable
        Response scheduleCallback_cur = startScheduledCallback(callbackSchServiceName + "_cur", customerNumber_2, 180);
        String callbackId_cur = scheduleCallback_cur.then().extract().path("_id");

        assertThat(getCallbackDetailByKey(callbackId, "_callback_state"), equalTo("QUEUED"));
        assertThat(getCallbackDetailByKey(callbackId_cur, "_callback_state"), equalTo("QUEUED"));
        //terminate ORS session
        killORSSession(getCallbackDetailByKey(callbackId, "_ors_session_id"))
                .then()
                .assertThat()
                .statusCode(200);
        //terminate ORS session
        killORSSession(getCallbackDetailByKey(callbackId_cur, "_ors_session_id"))
                .then()
                .assertThat()
                .statusCode(200);
        env.restartApplicationWithDelay(gmsAppName, 240000 - GMS_START_DURATION * 1000);
        waitingForCallbackEWTValue(callbackId, MAX_POSSIBLE_EWT, 66, POLLING_TIME_MILLIS);
        waitingForCallbackEWTValue(callbackId_cur, MAX_POSSIBLE_EWT, 66, POLLING_TIME_MILLIS);
        cancelCallback(callbackSchServiceName + "_cur", callbackId_cur).then().assertThat().statusCode(200);
        cancelCallback(callbackSchServiceName, callbackId).then().assertThat().statusCode(200);

        env.deleteService(gmsClusterAppName, callbackSchServiceName + "_cur");
    }

    @Test
    public void test1_11() throws Exception {
        //reconfigure corresponding callback service options
        tuneCallbackService(callbackSchServiceName, "-120,10", "-120,120",
                "-1440,-120", "15");
        //reconfigure corresponding callback section options
        tuneCallbackSection("5", "60");
        //schedule new callback and save it id to variable
        Response scheduleCallback = startScheduledCallback(callbackSchServiceName, customerNumber_1, SCHEDULED_CB_TIME);
        String callbackId = scheduleCallback.then().extract().path("_id");

        assertThat(getCallbackDetailByKey(callbackId, "_callback_state"), equalTo("SCHEDULED"));
        env.restartApplicationWithDelay(gmsAppName, 1000);
        waitingForCallbackState(callbackId, "QUEUED", 66, POLLING_TIME_MILLIS);
        assertThat(getCallbackEWT(callbackId), lessThanOrEqualTo(MAX_POSSIBLE_EWT));
        cancelCallback(callbackSchServiceName, callbackId).then().assertThat().statusCode(200);
    }

    @Test
    public void test1_12() throws Exception {
        //reconfigure corresponding callback service options
        tuneCallbackService(callbackSchServiceName, "-120,-1", "-120,120",
                "-1440,-120", "15");
        //reconfigure corresponding callback section options
        tuneCallbackSection("60", "60");
        //schedule new callback and save it id to variable
        Response scheduleCallback = startScheduledCallback(callbackSchServiceName, customerNumber_1, SCHEDULED_QUEUED_CB_TIME);
        String callbackId = scheduleCallback.then().extract().path("_id");

        assertThat(getCallbackDetailByKey(callbackId, "_callback_state"), equalTo("QUEUED"));
        //terminate ORS session
        killORSSession(getCallbackDetailByKey(callbackId, "_ors_session_id"))
                .then()
                .assertThat()
                .statusCode(200);
        env.restartApplicationWithDelay(gmsAppName, 75000 - GMS_START_DURATION * 1000);
        waitingForCallbackEWTValue(callbackId, MAX_POSSIBLE_EWT, 66, POLLING_TIME_MILLIS);
        cancelCallback(callbackSchServiceName, callbackId).then().assertThat().statusCode(200);
    }

    @Test
    public void test2_01() throws Exception {
        callbackSchServiceName_UI = "cb_term_sch_gms-3984_ui";
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(gmsUrl);
        loginPage = new GMSLoginPage(driver);
        mainPage = loginPage.logIn(username, password);
        configuredServicesPage = mainPage.goToConfiguredServicesUIPage();
        configuredServicesPage.createCallbackService(callbackSchServiceName_UI, "User Terminated Delayed");
        assertThat(configuredServicesPage.getDisplayedServiceName(), containsString(callbackSchServiceName_UI));

        String ttlValue = configuredServicesPage.getOptionValueText("_ttl");
        assertThat(ttlValue, equalTo("86400"));
    }

    @Test
    public void test2_02() throws Exception {
        callbackSchServiceName_UI = "cb_term_sch_gms-3984_ui";
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(gmsUrl);
        loginPage = new GMSLoginPage(driver);
        mainPage = loginPage.logIn(username, password);
        configuredServicesPage = mainPage.goToConfiguredServicesUIPage();
        configuredServicesPage.createCallbackService(callbackSchServiceName_UI, "User Terminated Delayed");
        assertThat(configuredServicesPage.getDisplayedServiceName(), containsString(callbackSchServiceName_UI));

        String queuePollPeriodDescription = configuredServicesPage.getOptionDescriptionText("_queue_poll_period");
        String queuePollPeriodRecoveryDescription = configuredServicesPage.getOptionDescriptionText("_queue_poll_period_recovery");
        String queuePingOrsPeriodDescription = configuredServicesPage.getOptionDescriptionText("_queue_ping_ors_period");
        assertThat(queuePollPeriodDescription, containsString("Defines the time range in minutes to pick callbacks in queue from the current time."));
        assertThat(queuePollPeriodRecoveryDescription, containsString("Defines the time range in minutes to pick callbacks in queue from the current time for the recovery period."));
        assertThat(queuePingOrsPeriodDescription, containsString("Defines the time range in minutes to ping ORS for resubmission."));
    }

    private void tuneCallbackService(String cbServiceName,
                                     String queuePingORSPeriod,
                                     String queuePollPeriod,
                                     String queuePollPeriodRecovery,
                                     String requestExecutionTimeBuffer) throws Exception {
        env.changeOptionValueInService(gmsClusterAppName, cbServiceName, "_queue_ping_ors_period", queuePingORSPeriod);
        env.changeOptionValueInService(gmsClusterAppName, cbServiceName, "_queue_poll_period", queuePollPeriod);
        env.changeOptionValueInService(gmsClusterAppName, cbServiceName, "_queue_poll_period_recovery", queuePollPeriodRecovery);
        env.changeOptionValueInService(gmsClusterAppName, cbServiceName, "_request_execution_time_buffer", requestExecutionTimeBuffer);
    }

    private void tuneCallbackSection(String queuePollingRate, String queuePollingRateRecover) throws Exception {
        env.changeOptionValueInSection(gmsClusterAppName, sectionName, "queue-polling-rate", queuePollingRate);
        env.changeOptionValueInSection(gmsClusterAppName, sectionName, "queue-polling-rate-recover", queuePollingRateRecover);
    }

}