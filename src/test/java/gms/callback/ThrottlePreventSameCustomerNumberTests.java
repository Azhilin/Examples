package gms.callback;

import com.genesyslab.ats.ex.AtsCfgComponentException;
import com.genesyslab.functional.tests.gms.helper.CallbackServicesAPI;
import com.genesyslab.functional.tests.gms.helper.RemoteHostOperations;
import com.genesyslab.functional.tests.gms.psdk.Reconfiguration;
import com.genesyslab.platform.commons.collections.KeyValueCollection;
import io.restassured.response.Response;
import org.junit.*;

import static com.genesyslab.functional.tests.gms.helper.CallbackServiceHelper.cancelUnfinishedCallbacks;
import static com.genesyslab.functional.tests.gms.helper.CallbackServicesAPI.startImmediateCallback;
import static com.genesyslab.functional.tests.gms.helper.CallbackServicesAPI.startScheduledCallback;
import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertiesFile;
import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertyConfiguration;
import static com.genesyslab.functional.tests.gms.helper.RemoteHostOperations.changeDateOnRemoteHostAhead;
import static com.genesyslab.functional.tests.gms.helper.RemoteHostOperations.changeDateOnRemoteHostBack;
import static com.genesyslab.functional.tests.gms.helper.SectionOptions.getGMSCallbackSectionOptions;
import static com.genesyslab.functional.tests.gms.helper.ServiceOptions.getUserTermImmCallbackServiceOptions;
import static com.genesyslab.functional.tests.gms.helper.ServiceOptions.getUserTermSchCallbackServiceOptions;
import static com.genesyslab.functional.tests.gms.helper.TimeOperations.getMethodExecutionTime;
import static com.genesyslab.functional.tests.gms.helper.URLsHelper.getGMSBaseURL;
import static org.hamcrest.Matchers.containsString;

/**
 * Created by bvolovyk on 15.05.2017.
 */

/*
 * [Test suite] [Callback] Throttle service: https://jira.genesys.com/browse/GMS-4804
 */
public class ThrottlePreventSameCustomerNumberTests {
    private static String propertiesFile = getPropertiesFile();
    //    private static String propertiesFile = "./config_2nd.properties";//uncomment this line in case you want to use another .properties file
    private static Reconfiguration env = new Reconfiguration(propertiesFile);
    private static String gmsClusterAppName = getPropertyConfiguration(propertiesFile,"gms.cluster.app.name");
    private static String customerNumber1 = "5115";
    private static String customerNumber2 = "5125";
    private static String callbackServiceName_serv = "cb_term_im_gms-4695_serv";
    private static String callbackServiceName_glob = "cb_term_im_gms-4695_glob";
    private static String callbackServiceName_serv_tc;
    private static String callbackServiceName_glob_tc;
    private static String sectionName = "callback";
    //    private static String throttleRequestParametersLimitKey = "_throttle_customer_number_limit"; //for GMS up to 8.5.110.00 use this option instead "_throttle_request_parameters_limit"
    private static String throttleRequestParametersLimitKey = "_throttle_request_parameters_limit";

    @BeforeClass
    public static void oneTimeSetUp() throws AtsCfgComponentException {
        long startTime = System.nanoTime();
        System.out.println("@BeforeClass method processing...");
        CallbackServicesAPI.setGMSBaseURL(getGMSBaseURL(propertiesFile));
        RemoteHostOperations.setRemoteHost(getPropertyConfiguration(propertiesFile,"gms.host"));
        long endTime = System.nanoTime();
        getMethodExecutionTime(startTime, endTime, "@BeforeClass");
    }

    @Before
    public void setUp() throws AtsCfgComponentException, InterruptedException {
        long startTime = System.nanoTime();
        System.out.println("@Before method processing...");
        env.addSection(gmsClusterAppName, sectionName, getGMSCallbackSectionOptions());
        Thread.sleep(1000);
        long endTime = System.nanoTime();
        getMethodExecutionTime(startTime, endTime, "@Before");
    }

    @After
    public void tearDown() throws AtsCfgComponentException, InterruptedException {
        System.out.println("@After method processing...");
        cancelUnfinishedCallbacks(callbackServiceName_serv_tc);
        cancelUnfinishedCallbacks(callbackServiceName_glob_tc);
        env.deleteService(gmsClusterAppName, callbackServiceName_serv_tc); //for troubleshooting purposes comment this command
        env.deleteService(gmsClusterAppName, callbackServiceName_glob_tc); //for troubleshooting purposes comment this command
        Thread.sleep(1000);
        changeDateOnRemoteHostBack(0);
    }

    @AfterClass
    public static void oneTimeTearDown() throws AtsCfgComponentException, InterruptedException {
        System.out.println("@AfterClass method processing...");
        env.addSection(gmsClusterAppName, sectionName, getGMSCallbackSectionOptions());
        Thread.sleep(500);
        env.deactivate();
    }

    @Test //negative test
    // /lunch this test only on the fresh environment (with fresh Cassandra)
    public void test_13() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleRequestParametersLimit_g = "0";//default "6"
        String throttleRequestParameters_g = "_customer_number";//default "_customer_number"
        //service level options
        String throttleRequestParametersLimit_s = "2";
        String throttleRequestParameters_s = "";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_13";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_13";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleRequestParametersLimit_g, throttleRequestParameters_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleRequestParametersLimit_s, throttleRequestParameters_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, customerNumber1, throttleRequestParametersLimit_s);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_serv_tc, customerNumber1, throttleRequestParametersLimit_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_glob_tc, customerNumber1, throttleRequestParametersLimit_g);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_glob_tc, customerNumber2, throttleRequestParametersLimit_g);

        cancelUnfinishedCallbacks(callbackServiceName_serv_tc);
        cancelUnfinishedCallbacks(callbackServiceName_glob_tc);

        System.out.println("VERIFICATION FOR SERVICE LEVEL AFTER DATE MODIFICATION...");
        changeDateOnRemoteHostAhead(1);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, customerNumber1, throttleRequestParametersLimit_s);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_serv_tc, customerNumber1, throttleRequestParametersLimit_s);
    }

    @Test
    public void test_14() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleRequestParametersLimit_g = "2";//default "6"
        String throttleRequestParameters_g = "";//default "_customer_number"
        //service level options
        String throttleRequestParametersLimit_s = "";
        String throttleRequestParameters_s = "";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_14";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_14";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleRequestParametersLimit_g, throttleRequestParameters_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleRequestParametersLimit_s, throttleRequestParameters_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, customerNumber1, throttleRequestParametersLimit_g);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_serv_tc, customerNumber1, throttleRequestParametersLimit_g);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, customerNumber1, throttleRequestParametersLimit_g);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_glob_tc, customerNumber1, throttleRequestParametersLimit_g);

        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, customerNumber2, throttleRequestParametersLimit_g);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_glob_tc, customerNumber2, throttleRequestParametersLimit_g);

        cancelUnfinishedCallbacks(callbackServiceName_serv_tc);
        cancelUnfinishedCallbacks(callbackServiceName_glob_tc);

        System.out.println("VERIFICATION FOR SERVICE LEVEL AFTER DATE MODIFICATION...");
        changeDateOnRemoteHostAhead(1);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, customerNumber1, throttleRequestParametersLimit_g);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_serv_tc, customerNumber1, throttleRequestParametersLimit_g);
    }

    @Test
    public void test_15() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleRequestParametersLimit_g = "2";//default "6"
        String throttleRequestParameters_g = "";//default "_customer_number"
        //service level options
        String throttleRequestParametersLimit_s = "2";
        String throttleRequestParameters_s = "absent";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_15";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_15";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleRequestParametersLimit_g, throttleRequestParameters_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleRequestParametersLimit_s, throttleRequestParameters_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, customerNumber1, throttleRequestParametersLimit_s);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_serv_tc, customerNumber1, throttleRequestParametersLimit_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, customerNumber1, throttleRequestParametersLimit_g);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_glob_tc, customerNumber1, throttleRequestParametersLimit_g);

        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, customerNumber2, throttleRequestParametersLimit_g);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_glob_tc, customerNumber2, throttleRequestParametersLimit_g);

        cancelUnfinishedCallbacks(callbackServiceName_serv_tc);
        cancelUnfinishedCallbacks(callbackServiceName_glob_tc);

        System.out.println("VERIFICATION FOR SERVICE LEVEL AFTER DATE MODIFICATION...");
        changeDateOnRemoteHostAhead(1);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, customerNumber1, throttleRequestParametersLimit_s);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_serv_tc, customerNumber1, throttleRequestParametersLimit_s);
    }

    @Test
    public void test_16() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleRequestParametersLimit_g = "";//default "6"
        String throttleRequestParameters_g = "_customer_number";//default "_customer_number"
        //service level options
        String throttleRequestParametersLimit_s = "absent";
        String throttleRequestParameters_s = "absent";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_16";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_16";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleRequestParametersLimit_g, throttleRequestParameters_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleRequestParametersLimit_s, throttleRequestParameters_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, customerNumber1, 6);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_serv_tc, customerNumber1, 6);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, customerNumber1, 6);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_glob_tc, customerNumber1, 6);

        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, customerNumber2, 6);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_glob_tc, customerNumber2, 6);

        cancelUnfinishedCallbacks(callbackServiceName_serv_tc);
        cancelUnfinishedCallbacks(callbackServiceName_glob_tc);

        System.out.println("VERIFICATION FOR SERVICE LEVEL AFTER DATE MODIFICATION...");
        changeDateOnRemoteHostAhead(1);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, customerNumber1, 6);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_serv_tc, customerNumber1, 6);
    }

    @Test
    public void test_17() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleRequestParametersLimit_g = "";//default "6"
        String throttleRequestParameters_g = "";//default "_customer_number"
        //service level options
        String throttleRequestParametersLimit_s = "absent";
        String throttleRequestParameters_s = "";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_17";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_17";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleRequestParametersLimit_g, throttleRequestParameters_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleRequestParametersLimit_s, throttleRequestParameters_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, customerNumber1, 6);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_serv_tc, customerNumber1, 6);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, customerNumber1, 6);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_glob_tc, customerNumber1, 6);

        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, customerNumber2, 6);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_glob_tc, customerNumber2, 6);

        cancelUnfinishedCallbacks(callbackServiceName_serv_tc);
        cancelUnfinishedCallbacks(callbackServiceName_glob_tc);

        System.out.println("VERIFICATION FOR SERVICE LEVEL AFTER DATE MODIFICATION...");
        changeDateOnRemoteHostAhead(1);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, customerNumber1, 6);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_serv_tc, customerNumber1, 6);
    }

    @Test //negative test
    public void test_18() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleRequestParametersLimit_g = "";//default "6"
        String throttleRequestParameters_g = "_customer_number";//default "_customer_number"
        //service level options
        String throttleRequestParametersLimit_s = "0";
        String throttleRequestParameters_s = "_customer_number";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_18";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_18";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleRequestParametersLimit_g, throttleRequestParameters_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleRequestParametersLimit_s, throttleRequestParameters_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_serv_tc, customerNumber1, throttleRequestParametersLimit_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, customerNumber1, 6);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_glob_tc, customerNumber1, 6);

        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, customerNumber2, 6);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_glob_tc, customerNumber2, 6);

        cancelUnfinishedCallbacks(callbackServiceName_serv_tc);
        cancelUnfinishedCallbacks(callbackServiceName_glob_tc);

        System.out.println("VERIFICATION FOR SERVICE LEVEL AFTER DATE MODIFICATION...");
        changeDateOnRemoteHostAhead(1);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_serv_tc, customerNumber1, throttleRequestParametersLimit_s);
    }

    @Test //negative test
    public void test_19() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleRequestParametersLimit_g = "0";//default "6"
        String throttleRequestParameters_g = "";//default "_customer_number"
        //service level options
        String throttleRequestParametersLimit_s = "0";
        String throttleRequestParameters_s = "absent";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_19";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_19";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleRequestParametersLimit_g, throttleRequestParameters_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleRequestParametersLimit_s, throttleRequestParameters_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_serv_tc, customerNumber1, throttleRequestParametersLimit_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_glob_tc, customerNumber1, throttleRequestParametersLimit_g);

        verifyNumberInImmCbCreationErrMsg(callbackServiceName_glob_tc, customerNumber2, throttleRequestParametersLimit_g);

        cancelUnfinishedCallbacks(callbackServiceName_serv_tc);
        cancelUnfinishedCallbacks(callbackServiceName_glob_tc);

        System.out.println("VERIFICATION FOR SERVICE LEVEL AFTER DATE MODIFICATION...");
        changeDateOnRemoteHostAhead(1);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_serv_tc, customerNumber1, throttleRequestParametersLimit_s);
    }

    @Test //negative test
    public void test_20() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleRequestParametersLimit_g = "0";//default "6"
        String throttleRequestParameters_g = "";//default "_customer_number"
        //service level options
        String throttleRequestParametersLimit_s = "absent";
        String throttleRequestParameters_s = "_customer_number";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_20";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_20";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleRequestParametersLimit_g, throttleRequestParameters_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleRequestParametersLimit_s, throttleRequestParameters_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_serv_tc, customerNumber1, throttleRequestParametersLimit_g);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_glob_tc, customerNumber1, throttleRequestParametersLimit_g);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_glob_tc, customerNumber2, throttleRequestParametersLimit_g);

        cancelUnfinishedCallbacks(callbackServiceName_serv_tc);
        cancelUnfinishedCallbacks(callbackServiceName_glob_tc);

        System.out.println("VERIFICATION FOR SERVICE LEVEL AFTER DATE MODIFICATION...");
        changeDateOnRemoteHostAhead(1);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_serv_tc, customerNumber1, throttleRequestParametersLimit_g);
    }

    @Test //negative test
    public void test_21() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleRequestParametersLimit_g = "0";//default "6"
        String throttleRequestParameters_g = "_customer_number";//default "_customer_number"
        //service level options
        String throttleRequestParametersLimit_s = "";
        String throttleRequestParameters_s = "_customer_number";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_21";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_21";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleRequestParametersLimit_g, throttleRequestParameters_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleRequestParametersLimit_s, throttleRequestParameters_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_serv_tc, customerNumber1, throttleRequestParametersLimit_g);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_glob_tc, customerNumber1, throttleRequestParametersLimit_g);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_glob_tc, customerNumber2, throttleRequestParametersLimit_g);

        cancelUnfinishedCallbacks(callbackServiceName_serv_tc);
        cancelUnfinishedCallbacks(callbackServiceName_glob_tc);

        System.out.println("VERIFICATION FOR SERVICE LEVEL AFTER DATE MODIFICATION...");
        changeDateOnRemoteHostAhead(1);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_serv_tc, customerNumber1, throttleRequestParametersLimit_g);
    }

    @Test
    public void test_22() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleRequestParametersLimit_g = "";//default "6"
        String throttleRequestParameters_g = "_customer_number";//default "_customer_number"
        //service level options
        String throttleRequestParametersLimit_s = "2";
        String throttleRequestParameters_s = "_customer_number";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_22";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_22";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleRequestParametersLimit_g, throttleRequestParameters_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleRequestParametersLimit_s, throttleRequestParameters_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, customerNumber1, throttleRequestParametersLimit_s);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_serv_tc, customerNumber1, throttleRequestParametersLimit_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, customerNumber1, 6);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_glob_tc, customerNumber1, 6);

        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, customerNumber2, 6);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_glob_tc, customerNumber2, 6);

        cancelUnfinishedCallbacks(callbackServiceName_serv_tc);
        cancelUnfinishedCallbacks(callbackServiceName_glob_tc);

        System.out.println("VERIFICATION FOR SERVICE LEVEL AFTER DATE MODIFICATION...");
        changeDateOnRemoteHostAhead(1);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, customerNumber1, throttleRequestParametersLimit_s);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_serv_tc, customerNumber1, throttleRequestParametersLimit_s);
    }

    @Test
    public void test_23() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleRequestParametersLimit_g = "2";//default "6"
        String throttleRequestParameters_g = "_customer_number";//default "_customer_number"
        //service level options
        String throttleRequestParametersLimit_s = "absent";
        String throttleRequestParameters_s = "_customer_number";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_23";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_23";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleRequestParametersLimit_g, throttleRequestParameters_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleRequestParametersLimit_s, throttleRequestParameters_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, customerNumber1, throttleRequestParametersLimit_g);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_serv_tc, customerNumber1, throttleRequestParametersLimit_g);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, customerNumber1, throttleRequestParametersLimit_g);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_glob_tc, customerNumber1, throttleRequestParametersLimit_g);

        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, customerNumber2, throttleRequestParametersLimit_g);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_glob_tc, customerNumber2, throttleRequestParametersLimit_g);

        cancelUnfinishedCallbacks(callbackServiceName_serv_tc);
        cancelUnfinishedCallbacks(callbackServiceName_glob_tc);

        System.out.println("VERIFICATION FOR SERVICE LEVEL AFTER DATE MODIFICATION...");
        changeDateOnRemoteHostAhead(1);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, customerNumber1, throttleRequestParametersLimit_g);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_serv_tc, customerNumber1, throttleRequestParametersLimit_g);
    }

    @Test //negative test
    public void test_24() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleRequestParametersLimit_g = "2";//default "6"
        String throttleRequestParameters_g = "_customer_number";//default "_customer_number"
        //service level options
        String throttleRequestParametersLimit_s = "0";
        String throttleRequestParameters_s = "";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_24";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_24";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleRequestParametersLimit_g, throttleRequestParameters_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleRequestParametersLimit_s, throttleRequestParameters_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_serv_tc, customerNumber1, throttleRequestParametersLimit_s);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, customerNumber1, throttleRequestParametersLimit_g);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_glob_tc, customerNumber1, throttleRequestParametersLimit_g);

        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, customerNumber2, throttleRequestParametersLimit_g);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_glob_tc, customerNumber2, throttleRequestParametersLimit_g);

        cancelUnfinishedCallbacks(callbackServiceName_serv_tc);
        cancelUnfinishedCallbacks(callbackServiceName_glob_tc);

        System.out.println("VERIFICATION FOR SERVICE LEVEL AFTER DATE MODIFICATION...");
        changeDateOnRemoteHostAhead(1);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_serv_tc, customerNumber1, throttleRequestParametersLimit_s);
    }

    @Test
    public void test_25() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleRequestParametersLimit_g = "";//default "6"
        String throttleRequestParameters_g = "_customer_number";//default "_customer_number"
        //service level options
        String throttleRequestParametersLimit_s = "";
        String throttleRequestParameters_s = "absent";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_25";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_25";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleRequestParametersLimit_g, throttleRequestParameters_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleRequestParametersLimit_s, throttleRequestParameters_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, customerNumber1, 6);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_serv_tc, customerNumber1, 6);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, customerNumber1, 6);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_glob_tc, customerNumber1, 6);

        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, customerNumber2, 6);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_glob_tc, customerNumber2, 6);

        cancelUnfinishedCallbacks(callbackServiceName_serv_tc);
        cancelUnfinishedCallbacks(callbackServiceName_glob_tc);

        System.out.println("VERIFICATION FOR SERVICE LEVEL AFTER DATE MODIFICATION...");
        changeDateOnRemoteHostAhead(1);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, customerNumber1, 6);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_serv_tc, customerNumber1, 6);
    }

    @Test //negative test
    public void test_26() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleRequestParametersLimit_g = "-1";//default "6"
        String throttleRequestParameters_g = "_customer_number";//default "_customer_number"
        //service level options
        String throttleRequestParametersLimit_s = "absent";
        String throttleRequestParameters_s = "_customer_number";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_26";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_26";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleRequestParametersLimit_g, throttleRequestParameters_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleRequestParametersLimit_s, throttleRequestParameters_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, customerNumber1, 6);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_serv_tc, customerNumber1, 6);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, customerNumber1, 6);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_glob_tc, customerNumber1, 6);

        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, customerNumber2, 6);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_glob_tc, customerNumber2, 6);

        cancelUnfinishedCallbacks(callbackServiceName_serv_tc);
        cancelUnfinishedCallbacks(callbackServiceName_glob_tc);

        System.out.println("VERIFICATION FOR SERVICE LEVEL AFTER DATE MODIFICATION...");
        changeDateOnRemoteHostAhead(1);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, customerNumber1, 6);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_serv_tc, customerNumber1, 6);
    }

    @Test //negative test
    public void test_27() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleRequestParametersLimit_g = "0";//default "6"
        String throttleRequestParameters_g = "";//default "_customer_number"
        //service level options
        String throttleRequestParametersLimit_s = "absent";
        String throttleRequestParameters_s = "absent";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = "cb_term_sch_gms-4695_serv" + "_27";
        callbackServiceName_glob_tc = "cb_term_sch_gms-4695_glob" + "_27";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermSchCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermSchCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleRequestParametersLimit_g, throttleRequestParameters_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleRequestParametersLimit_s, throttleRequestParameters_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfSchCallbacks(callbackServiceName_serv_tc, customerNumber1, 2, 300);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfSchCallbacks(callbackServiceName_glob_tc, customerNumber1, 2, 300);
    }

    @Test //negative test
    public void test_28() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleRequestParametersLimit_g = "";//default "6"
        String throttleRequestParameters_g = "_customer_number";//default "_customer_number"
        //service level options
        String throttleRequestParametersLimit_s = "7";
        String throttleRequestParameters_s = "_customer_number";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = "cb_term_sch_gms-4695_serv" + "_28";
        callbackServiceName_glob_tc = "cb_term_sch_gms-4695_glob" + "_28";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermSchCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermSchCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleRequestParametersLimit_g, throttleRequestParameters_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleRequestParametersLimit_s, throttleRequestParameters_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfSchCallbacks(callbackServiceName_serv_tc, customerNumber1, 8, 600);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfSchCallbacks(callbackServiceName_glob_tc, customerNumber1, 7, 600);
    }

    @Test //negative test
    public void test_29() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleRequestParametersLimit_g = "2";//default "6"
        String throttleRequestParameters_g = "_customer_number";//default "_customer_number"
        //service level options
        String throttleRequestParametersLimit_s = "-1";
        String throttleRequestParameters_s = "_customer_number";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = callbackServiceName_serv + "_27";
        callbackServiceName_glob_tc = callbackServiceName_glob + "_27";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermImmCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermImmCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleRequestParametersLimit_g, throttleRequestParameters_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleRequestParametersLimit_s, throttleRequestParameters_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, customerNumber1, 6);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_serv_tc, customerNumber1, 6);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, customerNumber1, throttleRequestParametersLimit_g);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_glob_tc, customerNumber1, throttleRequestParametersLimit_g);

        createSpecifiedNumberOfImmCallbacks(callbackServiceName_glob_tc, customerNumber2, throttleRequestParametersLimit_g);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_glob_tc, customerNumber2, throttleRequestParametersLimit_g);

        cancelUnfinishedCallbacks(callbackServiceName_serv_tc);
        cancelUnfinishedCallbacks(callbackServiceName_glob_tc);

        System.out.println("VERIFICATION FOR SERVICE LEVEL AFTER DATE MODIFICATION...");
        changeDateOnRemoteHostAhead(1);
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_serv_tc, customerNumber1, 6);
        verifyNumberInImmCbCreationErrMsg(callbackServiceName_serv_tc, customerNumber1, 6);
    }

    @Test
    public void test_30() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleRequestParametersLimit_g = "7";//default "6"
        String throttleRequestParameters_g = "";//default "_customer_number"
        //service level options
        String throttleRequestParametersLimit_s = "";
        String throttleRequestParameters_s = "";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = "cb_term_sch_gms-4695_serv" + "_30";
        callbackServiceName_glob_tc = "cb_term_sch_gms-4695_glob" + "_30";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermSchCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermSchCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleRequestParametersLimit_g, throttleRequestParameters_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleRequestParametersLimit_s, throttleRequestParameters_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfSchCallbacks(callbackServiceName_serv_tc, customerNumber1, 8, 900);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfSchCallbacks(callbackServiceName_glob_tc, customerNumber1, 8, 900);
    }

    @Test //negative test
    public void test_31() throws Exception {
        //global (callback section) level options
        String enableInQueueChecking_g = "false";
        String throttleRequestParametersLimit_g = "7";//default "6"
        String throttleRequestParameters_g = "";//default "_customer_number"
        //service level options
        String throttleRequestParametersLimit_s = "0";
        String throttleRequestParameters_s = "_customer_number";

        System.out.println("ENVIRONMENT PREPARATION...");
        callbackServiceName_serv_tc = "cb_term_sch_gms-4695_serv" + "_31";
        callbackServiceName_glob_tc = "cb_term_sch_gms-4695_glob" + "_31";

        createTwoCallbackServices(callbackServiceName_serv_tc, getUserTermSchCallbackServiceOptions(),
                callbackServiceName_glob_tc, getUserTermSchCallbackServiceOptions());

        tuneCallbackSection(enableInQueueChecking_g, throttleRequestParametersLimit_g, throttleRequestParameters_g);
        tuneCallbackService(callbackServiceName_serv_tc, throttleRequestParametersLimit_s, throttleRequestParameters_s);

        System.out.println("VERIFICATION FOR SERVICE LEVEL...");
        createSpecifiedNumberOfSchCallbacks(callbackServiceName_serv_tc, customerNumber1, 2, 1200);

        System.out.println("VERIFICATION FOR GLOBAL LEVEL...");
        createSpecifiedNumberOfSchCallbacks(callbackServiceName_glob_tc, customerNumber1, 8, 1200);
    }

    private void createTwoCallbackServices(String firstCbServiceName, KeyValueCollection firstCbServiceOptions,
                                           String secondCbServiceName, KeyValueCollection secondCbServiceOptions) throws Exception {
        env.createService(gmsClusterAppName, firstCbServiceName, firstCbServiceOptions);
        env.createService(gmsClusterAppName, secondCbServiceName, secondCbServiceOptions);
        env.deleteOptionFromService(gmsClusterAppName, firstCbServiceName, "_enable_in_queue_checking");
        deleteAllThrottleOptions(secondCbServiceName);
    }

    private static void createSpecifiedNumberOfImmCallbacks(String cbServiceName, String customerNumber, int cbQuantity) {
        for (int i = 0; i < cbQuantity; i++) {
            Response callback = startImmediateCallback(cbServiceName, customerNumber);
            callback.then().assertThat().statusCode(200);
        }
    }

    private static void createSpecifiedNumberOfImmCallbacks(String cbServiceName, String customerNumber, String cbQuantity) {
        int callbackQuantity = Integer.parseInt(cbQuantity);
        createSpecifiedNumberOfImmCallbacks(cbServiceName, customerNumber, callbackQuantity);
    }

    private static void createSpecifiedNumberOfSchCallbacks(String cbServiceName,
                                                            String customerNumber,
                                                            int cbQuantity,
                                                            int secondsFromNowForDesiredTime) {
        for (int i = 0; i < cbQuantity; i++) {
            Response callback = startScheduledCallback(cbServiceName, customerNumber, secondsFromNowForDesiredTime);
            callback.then().assertThat().statusCode(200);
        }
    }

    private static void createSpecifiedNumberOfSchCallbacks(String cbServiceName,
                                                            String customerNumber,
                                                            String cbQuantity,
                                                            int secondsFromNowForDesiredTime) {
        int callbackQuantity = Integer.parseInt(cbQuantity);
        createSpecifiedNumberOfSchCallbacks(cbServiceName, customerNumber, callbackQuantity, secondsFromNowForDesiredTime);
    }

    private static void deleteAllThrottleOptions(String cbServiceName) throws Exception {
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
                                     String throttleRequestParametersLimit,
                                     String throttleRequestParameters) throws Exception {
        env.changeOptionValueInSection(gmsClusterAppName, sectionName, "_enable_in_queue_checking", enableInQueueChecking);
        if (throttleRequestParametersLimit == "absent") {
            env.deleteOptionFromSection(gmsClusterAppName, sectionName, throttleRequestParametersLimitKey);
        } else {
            env.changeOptionValueInSection(gmsClusterAppName, sectionName, throttleRequestParametersLimitKey, throttleRequestParametersLimit);
        }
        if (throttleRequestParameters == "absent") {
            env.deleteOptionFromSection(gmsClusterAppName, sectionName, "_throttle_request_parameters");
        } else {
            env.changeOptionValueInSection(gmsClusterAppName, sectionName, "_throttle_request_parameters", throttleRequestParameters);
        }
        Thread.sleep(1000);
    }

    private void tuneCallbackService(String cbServiceName,
                                     String throttleRequestParametersLimit,
                                     String throttleRequestParameters) throws Exception {
        if (throttleRequestParametersLimit == "absent") {
            env.deleteOptionFromService(gmsClusterAppName, cbServiceName, throttleRequestParametersLimitKey);
        } else {
            env.changeOptionValueInService(gmsClusterAppName, cbServiceName, throttleRequestParametersLimitKey, throttleRequestParametersLimit);
        }
        if (throttleRequestParameters == "absent") {
            env.deleteOptionFromService(gmsClusterAppName, cbServiceName, "_throttle_request_parameters");
        } else {
            env.changeOptionValueInService(gmsClusterAppName, cbServiceName, "_throttle_request_parameters", throttleRequestParameters);
        }
        Thread.sleep(1000);
    }

    private static void verifyNumberInImmCbCreationErrMsg(String cbServiceName, String customerNumber, int number) {
        Response callback = startImmediateCallback(cbServiceName, customerNumber);
        callback.then().assertThat().statusCode(503);
        callback.then().assertThat().body("message",
                containsString("Callback for _customer_number=" + customerNumber
                        + " rejected because previous callbacks for this parameter were requested for more than "
                        + number + " times today."));
    }

    private static void verifyNumberInImmCbCreationErrMsg(String cbServiceName, String customerNumber, String secondsInResponseMsg) {
        int seconds = Integer.parseInt(secondsInResponseMsg);
        verifyNumberInImmCbCreationErrMsg(cbServiceName, customerNumber, seconds);
    }
}
