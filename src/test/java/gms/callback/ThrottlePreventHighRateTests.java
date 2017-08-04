package gms.callback;

import com.genesyslab.ats.ex.AtsCfgComponentException;
import com.genesyslab.functional.tests.gms.helper.CallbackServicesAPI;
import com.genesyslab.functional.tests.gms.psdk.Reconfiguration;
import com.genesyslab.platform.commons.collections.KeyValueCollection;
import io.restassured.response.Response;
import org.junit.*;

import java.time.Instant;
import java.util.concurrent.Callable;

import static com.genesyslab.functional.tests.gms.helper.CallbackServiceHelper.cancelUnfinishedCallbacks;
import static com.genesyslab.functional.tests.gms.helper.CallbackServicesAPI.startImmediateCallback;
import static com.genesyslab.functional.tests.gms.helper.CallbackServicesAPI.startScheduledCallback;
import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertiesFile;
import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertyConfiguration;
import static com.genesyslab.functional.tests.gms.helper.SectionOptions.getGMSCallbackSectionOptions;
import static com.genesyslab.functional.tests.gms.helper.ServiceOptions.getUserTermImmCallbackServiceOptions;
import static com.genesyslab.functional.tests.gms.helper.ServiceOptions.getUserTermSchCallbackServiceOptions;
import static com.genesyslab.functional.tests.gms.helper.TimeOperations.getMethodExecutionTime;
import static com.genesyslab.functional.tests.gms.helper.URLsHelper.getGMSBaseURL;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

/**
 * Created by bvolovyk on 27.04.2017.
 */

/*
 * [Test suite] [Callback] Throttle service: https://jira.genesys.com/browse/GMS-4804
 */
public class ThrottlePreventHighRateTests {
    private static String propertiesFile = getPropertiesFile();
    //    private static String propertiesFile = "./config_2nd.properties";//uncomment this line in case you want to use another .properties file
    private static Reconfiguration env = new Reconfiguration(propertiesFile);
    private static String gmsClusterAppName = getPropertyConfiguration(propertiesFile,"gms.cluster.app.name");
    private static int customerNumberCounter = 1000;
    private static String callbackServiceName_serv = "cb_term_im_gms-4688_serv";
    private static String callbackServiceName_glob = "cb_term_im_gms-4688_glob";
    private static String callbackServiceName_serv_tc;
    private static String callbackServiceName_glob_tc;
    private static String sectionName = "callback";
    //    private static String throttleRequestParametersLimitKey = "_throttle_customer_number_limit"; //for GMS up to 8.5.110.00 use this option instead "_throttle_request_parameters_limit"
    private static String throttleRequestParametersLimitKey = "_throttle_request_parameters_limit";

    private static int timeMarkFlag = 0;
    private static Long timeMark;

    @BeforeClass
    public static void oneTimeSetUp() throws AtsCfgComponentException {
        long startTime = System.nanoTime();
        System.out.println("@BeforeClass method processing...");
        CallbackServicesAPI.setGMSBaseURL(getGMSBaseURL(propertiesFile));
        long endTime = System.nanoTime();
        getMethodExecutionTime(startTime, endTime, "@BeforeClass");
    }

    @Before
    public void setUp() throws InterruptedException, AtsCfgComponentException {
        long startTime = System.nanoTime();
        System.out.println("@Before method processing...");
        env.addSection(gmsClusterAppName, sectionName, getGMSCallbackSectionOptions());
        Thread.sleep(1000);
        long endTime = System.nanoTime();
        getMethodExecutionTime(startTime, endTime, "@Before");
    }

    @After
    public void tearDown() throws InterruptedException, AtsCfgComponentException {
        System.out.println("@After method processing...");
        cancelUnfinishedCallbacks(callbackServiceName_serv_tc);
        cancelUnfinishedCallbacks(callbackServiceName_glob_tc);
        env.deleteService(gmsClusterAppName, callbackServiceName_serv_tc); //for troubleshooting purposes comment this command
        env.deleteService(gmsClusterAppName, callbackServiceName_glob_tc); //for troubleshooting purposes comment this command
        Thread.sleep(1000);
    }

    @AfterClass
    public static void oneTimeTearDown() throws InterruptedException, AtsCfgComponentException {
        System.out.println("@AfterClass method processing...");
        env.addSection(gmsClusterAppName, sectionName, getGMSCallbackSectionOptions());
        Thread.sleep(500);
        env.deactivate();
    }

    @Test
    public void test_00() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "2";
        String throttleTTL1_g = "10";
        String throttleCallbacksPerService2_g = "";
        String throttleTTL2_g = "absent";
        //service level options
        String throttleCallbacksPerService1_s = "2";
        String throttleTTL1_s = "14";
        String throttleCallbacksPerService2_s = "2";
        String throttleTTL2_s = "7";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_00";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_00";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_s);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
    }

    @Test
    public void test_01() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "";
        String throttleTTL1_g = "10";
        String throttleCallbacksPerService2_g = "2";
        String throttleTTL2_g = "25";
        //service level options
        String throttleCallbacksPerService1_s = "2";
        String throttleTTL1_s = "7";
        String throttleCallbacksPerService2_s = "2";
        String throttleTTL2_s = "7";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_01";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_01";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL2_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService2_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL2_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL2_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService2_g);
    }

    @Test
    public void test_02() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "2"; //default 500
        String throttleTTL1_g = "absent"; //default 300
        String throttleCallbacksPerService2_g = "absent"; //default 1000
        String throttleTTL2_g = "10"; //default 3600
        //service level options
        String throttleCallbacksPerService1_s = "3";
        String throttleTTL1_s = "";
        String throttleCallbacksPerService2_s = "3";
        String throttleTTL2_s = "19";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_02";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_02";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, 300);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_s);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, 300);
        waitSinceTimeMarkFlagInitializedByZero(300);//default
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, 300);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, 300);
        waitSinceTimeMarkFlagInitializedByZero(300);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, 1);
    }

    @Test
    public void test_03() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "1";
        String throttleTTL1_g = "20";
        String throttleCallbacksPerService2_g = "2";
        String throttleTTL2_g = "10";
        //service level options
        String throttleCallbacksPerService1_s = "2";
        String throttleTTL1_s = "14";
        String throttleCallbacksPerService2_s = "";
        String throttleTTL2_s = "";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_03";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_03";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
    }

    @Test
    public void test_04() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "1";
        String throttleTTL1_g = "";//300
        String throttleCallbacksPerService2_g = "";//1000
        String throttleTTL2_g = "absent";//3600
        //service level options
        String throttleCallbacksPerService1_s = "";
        String throttleTTL1_s = "14";
        String throttleCallbacksPerService2_s = "3";
        String throttleTTL2_s = "7";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_04";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_04";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once      
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_g);//throttleCallbacksPerService1_g=1
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_s);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_g);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, 300);
        waitSinceTimeMarkFlagInitializedByZero(300);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, 1);
    }

    @Test
    public void test_05() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "absent"; //default 500
        String throttleTTL1_g = ""; //default 300
        String throttleCallbacksPerService2_g = "1"; //default 1000
        String throttleTTL2_g = "25"; //default 3600
        //service level options
        String throttleCallbacksPerService1_s = "3";
        String throttleTTL1_s = "7";
        String throttleCallbacksPerService2_s = "";
        String throttleTTL2_s = "19";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_05";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_05";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService2_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL2_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_s);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL2_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService2_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL2_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService2_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL2_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService2_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL2_g);
    }

    @Test
    public void test_06() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "2";
        String throttleTTL1_g = "20";//300
        String throttleCallbacksPerService2_g = "1";//1000
        String throttleTTL2_g = "";//3600
        //service level options
        String throttleCallbacksPerService1_s = "";
        String throttleTTL1_s = "";
        String throttleCallbacksPerService2_s = "2";
        String throttleTTL2_s = "7";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_06";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_06";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        // use this flag to initiate timeMark by system time once      
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_g);//throttleCallbacksPerService1_g=2
        timeMarkFlag = 0;
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_s);//7
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_g);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService2_g);//1
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, 3600);
        waitSinceTimeMarkFlagInitializedByZero(20);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, 3600);
    }

    @Test
    public void test_07() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = ""; //default 500
        String throttleTTL1_g = ""; //default 300
        String throttleCallbacksPerService2_g = "1"; //default 1000
        String throttleTTL2_g = "absent"; //default 3600
        //service level options
        String throttleCallbacksPerService1_s = "3";
        String throttleTTL1_s = "14";
        String throttleCallbacksPerService2_s = "2";
        String throttleTTL2_s = "";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_07";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_07";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService2_s);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, 3600);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_s);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, 3600);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService2_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, 3600);
        waitSinceTimeMarkFlagInitializedByZero(300);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, 3600);
    }

    @Test
    public void test_08() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "absent";//500
        String throttleTTL1_g = "";//300
        String throttleCallbacksPerService2_g = "2";//1000
        String throttleTTL2_g = "";//3600
        //service level options
        String throttleCallbacksPerService1_s = "2";
        String throttleTTL1_s = "7";
        String throttleCallbacksPerService2_s = "3";
        String throttleTTL2_s = "19";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_08";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_08";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);//2
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_s);//7
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, 1);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL2_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, 1);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");//2 in 3600       
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService2_g);//2
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, 3600);
        waitSinceTimeMarkFlagInitializedByZero(20);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, 3600);
    }

    @Test
    public void test_09() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "2"; //default 500
        String throttleTTL1_g = ""; //default 300
        String throttleCallbacksPerService2_g = "absent"; //default 1000
        String throttleTTL2_g = "absent"; //default 3600
        //service level options
        String throttleCallbacksPerService1_s = "2";
        String throttleTTL1_s = "7";
        String throttleCallbacksPerService2_s = "";
        String throttleTTL2_s = "";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_09";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_09";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, 300);
        waitSinceTimeMarkFlagInitializedByZero(300);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, 300);
    }

    @Test
    public void test_10() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "1";//500
        String throttleTTL1_g = "20";//300
        String throttleCallbacksPerService2_g = "";//1000
        String throttleTTL2_g = "absent";//3600
        //service level options
        String throttleCallbacksPerService1_s = "3";
        String throttleTTL1_s = "";//20
        String throttleCallbacksPerService2_s = "2";
        String throttleTTL2_s = "19";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_10";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_10";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService2_s);//2
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL2_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_s);//
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, 1);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");//2 in 3600       
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);//2
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);//2
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
    }

    @Test
    public void test_11() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "absent"; //default 500
        String throttleTTL1_g = "20"; //default 300
        String throttleCallbacksPerService2_g = "2"; //default 1000
        String throttleTTL2_g = "25"; //default 3600
        //service level options
        String throttleCallbacksPerService1_s = "";
        String throttleTTL1_s = "";
        String throttleCallbacksPerService2_s = "3";
        String throttleTTL2_s = "";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_11";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_11";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService2_s);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL2_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL2_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService2_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService2_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL2_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL2_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService2_g);
    }

    @Test
    public void test_12() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "1";//1
        String throttleTTL1_g = "absent";//300
        String throttleCallbacksPerService2_g = "";//1000
        String throttleTTL2_g = "25";//3600
        //service level options
        String throttleCallbacksPerService1_s = "";//1
        String throttleTTL1_s = "7";//
        String throttleCallbacksPerService2_s = "3";
        String throttleTTL2_s = "";//25

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_12";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_12";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, 1);//1
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_s);//
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, 1);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");//      
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);//2
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, 300);
        waitSinceTimeMarkFlagInitializedByZero("300");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);//2
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, 300);
    }

    @Test
    public void test_13() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "2"; //default 500
        String throttleTTL1_g = "10"; //default 300
        String throttleCallbacksPerService2_g = "absent"; //default 1000
        String throttleTTL2_g = "25"; //default 3600
        //service level options
        String throttleCallbacksPerService1_s = "3";
        String throttleTTL1_s = "14";
        String throttleCallbacksPerService2_s = "";
        String throttleTTL2_s = "7";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_13";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_13";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_s);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
    }

    @Test
    public void test_14() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "";//500
        String throttleTTL1_g = "absent";//300
        String throttleCallbacksPerService2_g = "1";//1000
        String throttleTTL2_g = "";//3600
        //service level options
        String throttleCallbacksPerService1_s = "2";
        String throttleTTL1_s = "14";
        String throttleCallbacksPerService2_s = "";//1
        String throttleTTL2_s = "7";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_14";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_14";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, 1);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL2_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, 1);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");//      
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, 1);//2
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, 3600);
        waitSinceTimeMarkFlagInitializedByZero("20");
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, 3600);
    }

    @Test
    public void test_15() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "1"; //default 500
        String throttleTTL1_g = "10"; //default 300
        String throttleCallbacksPerService2_g = ""; //default 1000
        String throttleTTL2_g = ""; //default 3600
        //service level options
        String throttleCallbacksPerService1_s = "";
        String throttleTTL1_s = "";
        String throttleCallbacksPerService2_s = "3";
        String throttleTTL2_s = "19";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_15";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_15";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_g);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
    }

    @Test //not stable: throttleTTL1_g and throttleTTL2_g increased from 10 to 12 for stability
    public void test_16() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "1";//500
        String throttleTTL1_g = "12";//300
        String throttleCallbacksPerService2_g = "1";//1000
        String throttleTTL2_g = "12";//3600
        //service level options
        String throttleCallbacksPerService1_s = "3";
        String throttleTTL1_s = "7";
        String throttleCallbacksPerService2_s = "3";
        String throttleTTL2_s = "";//10

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_16";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_16";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_s);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL2_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");//      
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);//2
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService2_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL2_g);
    }

    @Test
    public void test_17() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "absent"; //default 500
        String throttleTTL1_g = "absent"; //default 300
        String throttleCallbacksPerService2_g = "2"; //default 1000
        String throttleTTL2_g = "10"; //default 3600
        //service level options
        String throttleCallbacksPerService1_s = "";
        String throttleTTL1_s = "14";
        String throttleCallbacksPerService2_s = "2";
        String throttleTTL2_s = "7";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_17";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_17";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService2_s);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL2_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService2_s);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL2_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService2_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL2_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService2_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL2_g);
    }

    @Test
    public void test_18() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "1";//500
        String throttleTTL1_g = "absent";//300
        String throttleCallbacksPerService2_g = "absent";//1000
        String throttleTTL2_g = "absent";//3600
        //service level options
        String throttleCallbacksPerService1_s = "";//1
        String throttleTTL1_s = "14";
        String throttleCallbacksPerService2_s = "2";
        String throttleTTL2_s = "19";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_18";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_18";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, 1);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, 1);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");//      
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, 1);//2
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, 300);
        waitSinceTimeMarkFlagInitializedByZero("20");
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, 300);
    }

    @Test
    public void test_19() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "absent"; //default 500
        String throttleTTL1_g = "10"; //default 300
        String throttleCallbacksPerService2_g = "2"; //default 1000
        String throttleTTL2_g = "absent"; //default 3600
        //service level options
        String throttleCallbacksPerService1_s = "2";
        String throttleTTL1_s = "";
        String throttleCallbacksPerService2_s = "";
        String throttleTTL2_s = "19";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_19";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_19";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL2_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_g);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService2_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, 3600);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, 3600);
    }

    @Test
    public void test_20() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "2";//500
        String throttleTTL1_g = "20";//300
        String throttleCallbacksPerService2_g = "2";//1000
        String throttleTTL2_g = "10";//3600
        //service level options
        String throttleCallbacksPerService1_s = "3";//1
        String throttleTTL1_s = "7";
        String throttleCallbacksPerService2_s = "2";
        String throttleTTL2_s = "";//10

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_20";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_20";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService2_s);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL2_g);//10
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_s);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL2_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService2_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");// 2 in 20     
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);//2
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
    }

    @Test
    public void test_21() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "2"; //default 500
        String throttleTTL1_g = "20"; //default 300
        String throttleCallbacksPerService2_g = "absent"; //default 1000
        String throttleTTL2_g = ""; //default 3600
        //service level options
        String throttleCallbacksPerService1_s = "3";
        String throttleTTL1_s = "";
        String throttleCallbacksPerService2_s = "3";
        String throttleTTL2_s = "19";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_21";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_21";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_g);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
    }

    @Test
    public void test_22() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "";//500
        String throttleTTL1_g = "20";//300
        String throttleCallbacksPerService2_g = "1";//1000
        String throttleTTL2_g = "10";//3600
        //service level options
        String throttleCallbacksPerService1_s = "2";
        String throttleTTL1_s = "";//20
        String throttleCallbacksPerService2_s = "3";
        String throttleTTL2_s = "19";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_22";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_22";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_g);//20
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_g);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");// 2 in 20     
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, 1);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL2_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, 1);
    }

    @Test
    public void test_23() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "1"; //default 500
        String throttleTTL1_g = "10"; //default 300
        String throttleCallbacksPerService2_g = "2"; //default 1000
        String throttleTTL2_g = "25"; //default 3600
        //service level options
        String throttleCallbacksPerService1_s = "2";
        String throttleTTL1_s = "7";
        String throttleCallbacksPerService2_s = "3";
        String throttleTTL2_s = "19";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_23";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_23";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        cancelUnfinishedCallbacks(callbackServiceName_serv_tc);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, 1);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL2_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        cancelUnfinishedCallbacks(callbackServiceName_glob_tc);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, 1);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL2_g);
    }

    @Test
    public void test_24() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "2";//500
        String throttleTTL1_g = "";//300
        String throttleCallbacksPerService2_g = "";//1000
        String throttleTTL2_g = "10";//3600
        //service level options
        String throttleCallbacksPerService1_s = "2";
        String throttleTTL1_s = "14";
        String throttleCallbacksPerService2_s = "3";
        String throttleTTL2_s = "";//10

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_24";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_24";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);//10
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);//14
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");// 2 in 300     
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, 300);
        waitSinceTimeMarkFlagInitializedByZero(300);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
    }

    //all bellow tests are negative
    @Test
    public void test_25() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "1"; //default 500
        String throttleTTL1_g = "10"; //default 300
        String throttleCallbacksPerService2_g = "0"; //default 1000
        String throttleTTL2_g = "25"; //default 3600
        //service level options
        String throttleCallbacksPerService1_s = "2";
        String throttleTTL1_s = "7";
        String throttleCallbacksPerService2_s = "3";
        String throttleTTL2_s = "19";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_25";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_25";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, 1);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL2_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL2_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL2_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL2_g);
    }

    @Test
    public void test_26() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "1";//500
        String throttleTTL1_g = "0";//300
        String throttleCallbacksPerService2_g = "2";//1000
        String throttleTTL2_g = "25";//3600
        //service level options
        String throttleCallbacksPerService1_s = "2";
        String throttleTTL1_s = "7";
        String throttleCallbacksPerService2_s = "3";
        String throttleTTL2_s = "19";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_26";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_26";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);//7
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, 1);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL2_s);//14
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, 1);

        System.out.println("VERIFICATIONFOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService2_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL2_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService2_g);
        //       createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        //       timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
//        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        //       waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_g);
        //       verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
    }

    @Test
    public void test_27() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "1"; //default 500
        String throttleTTL1_g = "10"; //default 300
        String throttleCallbacksPerService2_g = "2"; //default 1000
        String throttleTTL2_g = "0"; //default 3600
        //service level options
        String throttleCallbacksPerService1_s = "2";
        String throttleTTL1_s = "7";
        String throttleCallbacksPerService2_s = "3";
        String throttleTTL2_s = "19";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_27";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_27";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, 1);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL2_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
    }

    @Test
    public void test_28() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "1";//500
        String throttleTTL1_g = "-1";//300
        String throttleCallbacksPerService2_g = "2";//1000
        String throttleTTL2_g = "25";//3600
        //service level options
        String throttleCallbacksPerService1_s = "2";
        String throttleTTL1_s = "7";
        String throttleCallbacksPerService2_s = "3";
        String throttleTTL2_s = "19";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_28";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_28";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);//7
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, 1);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL2_s);//14
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, 300);
        waitSinceTimeMarkFlagInitializedByZero(300);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
    }

    @Test
    public void test_29() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "0"; //default 500
        String throttleTTL1_g = "10"; //default 300
        String throttleCallbacksPerService2_g = "2"; //default 1000
        String throttleTTL2_g = "25"; //default 3600
        //service level options
        String throttleCallbacksPerService1_s = "2";
        String throttleTTL1_s = "7";
        String throttleCallbacksPerService2_s = "3";
        String throttleTTL2_s = "19";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_29";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_29";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, 1);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL2_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
    }

    @Test
    public void test_30() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "1";//500
        String throttleTTL1_g = "10";//300
        String throttleCallbacksPerService2_g = "2";//1000
        String throttleTTL2_g = "25";//3600
        //service level options
        String throttleCallbacksPerService1_s = "2";
        String throttleTTL1_s = "7";
        String throttleCallbacksPerService2_s = "0";
        String throttleTTL2_s = "19";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_30";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_30";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL2_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_s);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL2_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL2_g);
    }

    @Test
    public void test_31() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "2"; //default 500
        String throttleTTL1_g = "10"; //default 300
        String throttleCallbacksPerService2_g = "3"; //default 1000
        String throttleTTL2_g = "25"; //default 3600
        //service level options
        String throttleCallbacksPerService1_s = "-1";//500
        String throttleTTL1_s = "7";
        String throttleCallbacksPerService2_s = "3";
        String throttleTTL2_s = "19";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_31";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_31";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService2_s);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL2_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_s);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL2_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService2_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, 1);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL2_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
    }

    @Test
    public void test_32() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "1";//500
        String throttleTTL1_g = "10";//300
        String throttleCallbacksPerService2_g = "-1";//1000
        String throttleTTL2_g = "25";//3600
        //service level options
        String throttleCallbacksPerService1_s = "2";
        String throttleTTL1_s = "7";
        String throttleCallbacksPerService2_s = "3";
        String throttleTTL2_s = "19";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_32";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_32";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);//7
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, 1);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL2_s);//14
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
    }

    @Test
    public void test_33() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "1"; //default 500
        String throttleTTL1_g = "10"; //default 300
        String throttleCallbacksPerService2_g = "2"; //default 1000
        String throttleTTL2_g = "25"; //default 3600
        //service level options
        String throttleCallbacksPerService1_s = "2";
        String throttleTTL1_s = "0";
        String throttleCallbacksPerService2_s = "3";
        String throttleTTL2_s = "19";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_33";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_33";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService2_s);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL2_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService2_s);
        //      createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        //       timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        //       verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);
        //       waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_s);
        //       verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL2_g);
    }

    @Test
    public void test_34() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "1";//500
        String throttleTTL1_g = "9";//300
        String throttleCallbacksPerService2_g = "2";//1000
        String throttleTTL2_g = "25";//3600
        //service level options
        String throttleCallbacksPerService1_s = "2";
        String throttleTTL1_s = "-1";//300
        String throttleCallbacksPerService2_s = "3";
        String throttleTTL2_s = "19";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_34";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_34";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, 300);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_s);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, 300);
        waitSinceTimeMarkFlagInitializedByZero(300);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL2_g);
    }

    @Test
    public void test_35() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "1"; //default 500
        String throttleTTL1_g = "10"; //default 300
        String throttleCallbacksPerService2_g = "2"; //default 1000
        String throttleTTL2_g = "25"; //default 3600
        //service level options
        String throttleCallbacksPerService1_s = "0";
        String throttleTTL1_s = "7";
        String throttleCallbacksPerService2_s = "3";
        String throttleTTL2_s = "19";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_35";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_35";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_s);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL2_g);
    }

    @Test
    public void test_36() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "1";//500
        String throttleTTL1_g = "10";//300
        String throttleCallbacksPerService2_g = "2";//1000
        String throttleTTL2_g = "25";//3600
        //service level options
        String throttleCallbacksPerService1_s = "2";
        String throttleTTL1_s = "7";//10
        String throttleCallbacksPerService2_s = "3";
        String throttleTTL2_s = "0";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_36";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_36";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, 1);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, 1);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
    }

    @Test
    public void test_37() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "1"; //default 500
        String throttleTTL1_g = "10"; //default 300
        String throttleCallbacksPerService2_g = "2"; //default 1000
        String throttleTTL2_g = "25"; //default 3600
        //service level options
        String throttleCallbacksPerService1_s = "2";
        String throttleTTL1_s = "7";
        String throttleCallbacksPerService2_s = "3";
        String throttleTTL2_s = "19";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = "cb_term_sch_gms-4688_serv" + "_37";
        callbackServiceName_glob_tc = "cb_term_sch_gms-4688_glob" + "_37";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermSchCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermSchCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfSchCallbacks(callbackServiceName_serv_tc, 4, 300);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfSchCallbacks(callbackServiceName_glob_tc, 3, 300);
    }

    @Test
    public void test_38() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "1";//500
        String throttleTTL1_g = "10";//300
        String throttleCallbacksPerService2_g = "2";//1000
        String throttleTTL2_g = "25";//3600
        //service level options
        String throttleCallbacksPerService1_s = "2";
        String throttleTTL1_s = "7";//10
        String throttleCallbacksPerService2_s = "3";
        String throttleTTL2_s = "-1";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_38";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_38";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, 1);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, 1);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
    }

    @Test
    public void test_39() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "true";
        String throttleCallbacksPerService1_g = "1"; //default 500
        String throttleTTL1_g = "10"; //default 300
        String throttleCallbacksPerService2_g = "2"; //default 1000
        String throttleTTL2_g = "25"; //default 3600
        //service level options
        String throttleCallbacksPerService1_s = "2";
        String throttleTTL1_s = "7";
        String throttleCallbacksPerService2_s = "3";
        String throttleTTL2_s = "19";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_39";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_39";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, 1);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL2_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, 1);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL2_g);
    }

    @Test
    public void test_40() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "-1";//500
        String throttleTTL1_g = "10";//300
        String throttleCallbacksPerService2_g = "2";//1000
        String throttleTTL2_g = "25";//3600
        //service level options
        String throttleCallbacksPerService1_s = "2";
        String throttleTTL1_s = "7";//10
        String throttleCallbacksPerService2_s = "3";
        String throttleTTL2_s = "19";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_40";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_40";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);//7
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, 1);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL2_s);//14
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, 1);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService2_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL2_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, 1);
    }

    @Test
    public void test_41() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "1"; //default 500
        String throttleTTL1_g = "10"; //default 300
        String throttleCallbacksPerService2_g = "2"; //default 1000
        String throttleTTL2_g = "-1"; //default 3600
        //service level options
        String throttleCallbacksPerService1_s = "2";
        String throttleTTL1_s = "7";
        String throttleCallbacksPerService2_s = "3";
        String throttleTTL2_s = "19";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_41";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_41";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, 1);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL2_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, 3600);
    }

    @Test
    public void test_42() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "1";//500
        String throttleTTL1_g = "10";//300
        String throttleCallbacksPerService2_g = "2";//1000
        String throttleTTL2_g = "25";//3600
        //service level options
        String throttleCallbacksPerService1_s = "2";
        String throttleTTL1_s = "7";//10
        String throttleCallbacksPerService2_s = "-1";
        String throttleTTL2_s = "19";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_42";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_42";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_s);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, 1);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
    }

    @Test
    public void test_43() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "1"; //default 500
        String throttleTTL1_g = "10"; //default 300
        String throttleCallbacksPerService2_g = "2"; //default 1000
        String throttleTTL2_g = "25"; //default 3600
        //service level options
        String throttleCallbacksPerService1_s = "2";
        String throttleTTL1_s = "0";
        String throttleCallbacksPerService2_s = "3";
        String throttleTTL2_s = "0";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_43";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_43";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, 4);
//        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s);
        //       timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
//        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);
        //       waitSinceTimeMarkFlagInitializedByZero(10);
//        verifySecondsInImmCbCreationErrMsg(callbackServiceName_serv_tc, throttleTTL1_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName_glob_tc, throttleTTL2_g);
    }

    @Test
    public void test_44() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleCallbacksPerService1_g = "1"; //default 500
        String throttleTTL1_g = "9"; //default 300
        String throttleCallbacksPerService2_g = "2"; //default 1000
        String throttleTTL2_g = "25"; //default 3600
        //service level options
        String throttleCallbacksPerService1_s = "2";
        String throttleTTL1_s = "7";
        String throttleCallbacksPerService2_s = "3";
        String throttleTTL2_s = "19";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = "cb_term_sch_gms-4688_serv" + "_44";
        callbackServiceName_glob_tc = "cb_term_sch_gms-4688_glob" + "_44";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermSchCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermSchCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleCallbacksPerService1_g, throttleTTL1_g,
                throttleCallbacksPerService2_g, throttleTTL2_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, throttleTTL1_s,
                throttleCallbacksPerService2_s, throttleTTL2_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfSchCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, 30);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInSchCbCreationErrMsg(callbackServiceName_serv_tc, 30, throttleTTL1_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_s);
        createSpecifiedNumberOfSchCallbacks(callbackServiceName_serv_tc, 1, 30);
        verifySecondsInSchCbCreationErrMsg(callbackServiceName_serv_tc, 30, throttleTTL2_s);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL2_s);
        createSpecifiedNumberOfSchCallbacks(callbackServiceName_serv_tc, throttleCallbacksPerService1_s, 30);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfSchCallbacks(callbackServiceName_glob_tc, throttleCallbacksPerService1_g, 30);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInSchCbCreationErrMsg(callbackServiceName_glob_tc, 30, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        createSpecifiedNumberOfSchCallbacks(callbackServiceName_glob_tc, 1, 30);
        timeMarkFlag = 0; // use this flag to initiate timeMark by system time once
        verifySecondsInSchCbCreationErrMsg(callbackServiceName_glob_tc, 30, throttleTTL1_g);
        waitSinceTimeMarkFlagInitializedByZero(throttleTTL1_g);
        verifySecondsInSchCbCreationErrMsg(callbackServiceName_glob_tc, 30, throttleTTL2_g);
    }

    private void createTwoCallbackServices(String firstCbServiceName, KeyValueCollection firstCbServiceOptions,
                                           String secondCbServiceName, KeyValueCollection secondCbServiceOptions) throws Exception {
        env.createService(gmsClusterAppName, firstCbServiceName, firstCbServiceOptions);
        Thread.sleep(1000);
        env.createService(gmsClusterAppName, secondCbServiceName, secondCbServiceOptions);
        Thread.sleep(1000);
        env.deleteOptionFromService(gmsClusterAppName, firstCbServiceName, "_enable_in_queue_checking");
        deleteAllThrottleOptions(secondCbServiceName);
    }

    private static void createSpecifiedNumberOfImmCallbacks(String callbackServiceName, int callbackQuantity) {
        for (int curCustNumb = customerNumberCounter; curCustNumb <= (customerNumberCounter + (callbackQuantity - 1)); curCustNumb++) {
            timeMarkFlag++;
            String currentCustomerNumber = String.valueOf(curCustNumb);
            Response callback = startImmediateCallback(callbackServiceName, currentCustomerNumber);
            if (timeMarkFlag == 1) {
                timeMark = Instant.now().getEpochSecond();
//                timeMark = System.currentTimeMillis();
            }
            callback.then().assertThat().statusCode(200);
        }
        customerNumberCounter += callbackQuantity;
    }

    private static void createSpecifiedNumberOfImmCallbacks(String callbackServiceName, String callbackQuantity) {
        int cbQuantity = Integer.parseInt(callbackQuantity);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName, cbQuantity);
    }

    private static void createSpecifiedNumberOfSchCallbacks(String callbackServiceName,
                                                            int callbackQuantity,
                                                            int secondsFromNowForDesiredTime) {
        for (int curCustNumb = customerNumberCounter; curCustNumb <= (customerNumberCounter + (callbackQuantity - 1)); curCustNumb++) {
            timeMarkFlag++;
            String currentCustomerNumber = String.valueOf(curCustNumb);
            Response callback = startScheduledCallback(callbackServiceName, currentCustomerNumber, secondsFromNowForDesiredTime);
            if (timeMarkFlag == 1) {
                timeMark = Instant.now().getEpochSecond();
//                timeMark = System.currentTimeMillis();
            }
            callback.then().assertThat().statusCode(200);
        }
        customerNumberCounter += callbackQuantity;
    }

    private static void createSpecifiedNumberOfSchCallbacks(String callbackServiceName,
                                                            String callbackQuantity,
                                                            int secondsFromNowForDesiredTime) {
        int cbQuantity = Integer.parseInt(callbackQuantity);
        createSpecifiedNumberOfSchCallbacks(callbackServiceName, cbQuantity, secondsFromNowForDesiredTime);
    }

    private Callable<Long> currentEpochTime() {
        return new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return Instant.now().getEpochSecond(); // The condition supplier part
//                return System.currentTimeMillis();
            }
        };
    }

    private static void deleteAllThrottleOptions(String cbServiceName) throws InterruptedException, AtsCfgComponentException {
        env.deleteOptionFromService(gmsClusterAppName, cbServiceName, "_enable_in_queue_checking");
        env.deleteOptionFromService(gmsClusterAppName, cbServiceName, "_throttle_callbacks_per_service_1");
        env.deleteOptionFromService(gmsClusterAppName, cbServiceName, "_throttle_ttl_1");
        env.deleteOptionFromService(gmsClusterAppName, cbServiceName, "_throttle_callbacks_per_service_2");
        env.deleteOptionFromService(gmsClusterAppName, cbServiceName, "_throttle_ttl_2");
        env.deleteOptionFromService(gmsClusterAppName, cbServiceName, throttleRequestParametersLimitKey);
        env.deleteOptionFromService(gmsClusterAppName, cbServiceName, "_throttle_request_parameters");
        Thread.sleep(1000);
    }

    private void tuneCallbackSection(String enableInQueueChecking,
                                     String throttleCallbacksPerService1,
                                     String throttleTTL1,
                                     String throttleCallbacksPerService2,
                                     String throttleTTL2) throws Exception {
        env.changeOptionValueInSection(gmsClusterAppName, sectionName, "_enable_in_queue_checking", enableInQueueChecking);
        if (throttleCallbacksPerService1 == "absent") {
            env.deleteOptionFromSection(gmsClusterAppName, sectionName, "_throttle_callbacks_per_service_1");
        } else {
            env.changeOptionValueInSection(gmsClusterAppName, sectionName, "_throttle_callbacks_per_service_1", throttleCallbacksPerService1);
        }
        if (throttleTTL1 == "absent") {
            env.deleteOptionFromSection(gmsClusterAppName, sectionName, "_throttle_ttl_1");
        } else {
            env.changeOptionValueInSection(gmsClusterAppName, sectionName, "_throttle_ttl_1", throttleTTL1);
        }
        if (throttleCallbacksPerService2 == "absent") {
            env.deleteOptionFromSection(gmsClusterAppName, sectionName, "_throttle_callbacks_per_service_2");
        } else {
            env.changeOptionValueInSection(gmsClusterAppName, sectionName, "_throttle_callbacks_per_service_2", throttleCallbacksPerService2);
        }
        if (throttleTTL2 == "absent") {
            env.deleteOptionFromSection(gmsClusterAppName, sectionName, "_throttle_ttl_2");
        } else {
            env.changeOptionValueInSection(gmsClusterAppName, sectionName, "_throttle_ttl_2", throttleTTL2);
        }
        Thread.sleep(1000);
    }

    private void tuneCallbackService(String cbServiceName,
                                     String throttleCallbacksPerService1,
                                     String throttleTTL1,
                                     String throttleCallbacksPerService2,
                                     String throttleTTL2) throws Exception {
        if (throttleCallbacksPerService1 == "absent") {
            env.deleteOptionFromService(gmsClusterAppName, cbServiceName, "_throttle_callbacks_per_service_1");
        } else {
            env.changeOptionValueInService(gmsClusterAppName, cbServiceName, "_throttle_callbacks_per_service_1", throttleCallbacksPerService1);
        }
        if (throttleTTL1 == "absent") {
            env.deleteOptionFromService(gmsClusterAppName, cbServiceName, "_throttle_ttl_1");
        } else {
            env.changeOptionValueInService(gmsClusterAppName, cbServiceName, "_throttle_ttl_1", throttleTTL1);
        }
        if (throttleCallbacksPerService2 == "absent") {
            env.deleteOptionFromService(gmsClusterAppName, cbServiceName, "_throttle_callbacks_per_service_2");
        } else {
            env.changeOptionValueInService(gmsClusterAppName, cbServiceName, "_throttle_callbacks_per_service_2", throttleCallbacksPerService2);
        }
        if (throttleTTL2 == "absent") {
            env.deleteOptionFromService(gmsClusterAppName, cbServiceName, "_throttle_ttl_2");
        } else {
            env.changeOptionValueInService(gmsClusterAppName, cbServiceName, "_throttle_ttl_2", throttleTTL2);
        }
        Thread.sleep(1000);
    }

    private static void verifySecondsInImmCbCreationErrMsg(String callbackServiceName, int secondsInResponseMsg) {
        timeMarkFlag++;
        String currentCustomerNumber = String.valueOf(customerNumberCounter);
        Response callback = startImmediateCallback(callbackServiceName, currentCustomerNumber);
        if (timeMarkFlag == 1) {
            timeMark = Instant.now().getEpochSecond();
        }
        callback.then().assertThat().statusCode(503);
        callback.then().assertThat().body("message",
                containsString("Limit of queued callbacks for " + callbackServiceName + " is reached for interval " + secondsInResponseMsg + "s."));
        customerNumberCounter += 1;
    }

    private static void verifySecondsInImmCbCreationErrMsg(String callbackServiceName, String secondsInResponseMsg) {
        int seconds = Integer.parseInt(secondsInResponseMsg);
        verifySecondsInImmCbCreationErrMsg(callbackServiceName, seconds);
    }

    private static void verifySecondsInSchCbCreationErrMsg(String callbackServiceName,
                                                           int secondsFromNowForDesiredTime,
                                                           int secondsInResponseMsg) {
        timeMarkFlag++;
        String currentCustomerNumber = String.valueOf(customerNumberCounter);
        Response callback = startScheduledCallback(callbackServiceName, currentCustomerNumber, secondsFromNowForDesiredTime);
        if (timeMarkFlag == 1) {
            timeMark = Instant.now().getEpochSecond();
//                timeMark = System.currentTimeMillis();
        }
        callback.then().assertThat().statusCode(503);
        callback.then().assertThat().body("message",
                containsString("Limit of queued callbacks for " + callbackServiceName + " is reached for interval " + secondsInResponseMsg + "s."));
        customerNumberCounter += 1;
    }

    private static void verifySecondsInSchCbCreationErrMsg(String callbackServiceName,
                                                           int secondsFromNowForDesiredTime,
                                                           String secondsInResponseMsg) {
        int seconds = Integer.parseInt(secondsInResponseMsg);
        verifySecondsInSchCbCreationErrMsg(callbackServiceName, secondsFromNowForDesiredTime, seconds);
    }

    private void waitSinceTimeMarkFlagInitializedByZero(int seconds) {
        long currentMoment = Instant.now().getEpochSecond();
//        long delay = (timeMark + seconds*1000) - currentMoment;
        long delay = (timeMark + seconds) - currentMoment;
        System.out.printf("Waiting %s seconds until time coming...%n", delay);
        given().ignoreExceptions()
                .await().atMost(305, SECONDS)
                .until(currentEpochTime(), greaterThanOrEqualTo(timeMark + seconds));
    }

    private void waitSinceTimeMarkFlagInitializedByZero(String seconds) {
        int sec = Integer.parseInt(seconds);
        waitSinceTimeMarkFlagInitializedByZero(sec);
    }
}