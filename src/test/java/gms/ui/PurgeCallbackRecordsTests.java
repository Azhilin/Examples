package gms.ui;

import com.genesyslab.ats.ex.AtsCfgComponentException;
import com.genesyslab.functional.tests.gms.helper.CallbackServicesAPI;
import com.genesyslab.functional.tests.gms.psdk.Reconfiguration;
import com.genesyslab.functional.tests.gms.ui.callback.GMSAdvancedOptionsPopUpPage;
import com.genesyslab.functional.tests.gms.ui.callback.GMSCallbackPage;
import com.genesyslab.functional.tests.gms.ui.callback.GMSCancelCallbacksConfirmationPopUpPage;
import com.genesyslab.functional.tests.gms.ui.callback.GMSDownloadReportsPopUpPage;
import io.restassured.response.Response;
import org.junit.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static com.genesyslab.functional.tests.gms.files.WorkWithResources.countSubStringsInFile;
import static com.genesyslab.functional.tests.gms.files.WorkWithResources.deleteFileInDir;
import static com.genesyslab.functional.tests.gms.helper.CallbackServiceHelper.*;
import static com.genesyslab.functional.tests.gms.helper.CallbackServicesAPI.*;
import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertiesFile;
import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertyConfiguration;
import static com.genesyslab.functional.tests.gms.helper.ServiceOptions.getUserTermImmCallbackServiceOptions;
import static com.genesyslab.functional.tests.gms.helper.ServiceOptions.getUserTermSchCallbackServiceOptions;
import static com.genesyslab.functional.tests.gms.helper.TimeOperations.getMethodExecutionTime;
import static com.genesyslab.functional.tests.gms.helper.URLsHelper.getGMSBaseURL;
import static com.genesyslab.functional.tests.gms.helper.URLsHelper.getGMSFullLoginURL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

/**
 * Created by bvolovyk on 29.06.2017.
 */

/*
 * [Test Suite] Callback Management UI. Search Filters: https://jira.genesys.com/browse/GMS-5030
 */
public class PurgeCallbackRecordsTests {
    private static String propertiesFile = getPropertiesFile();
    //    private static String propertiesFile = "./config_2nd.properties";//uncomment this line in case you want to use another .properties file
    private static Reconfiguration env = new Reconfiguration(propertiesFile);
    private static String gmsClusterAppName = getPropertyConfiguration(propertiesFile, "gms.cluster.app.name");
    private static String gmsBaseUrl = getGMSBaseURL(propertiesFile);
    private static String gmsUrl = getGMSFullLoginURL(propertiesFile);
    private static String username = getPropertyConfiguration(propertiesFile, "gms.user.name");
    private static String password = getPropertyConfiguration(propertiesFile, "gms.user.password");
    private static String customerNumber = "5115";
    private static int customerNumberCounter = 1000;
    private static String callbackImmServiceName = "cb_term_im_gms-4869";
    private static String callbackSchServiceName = "cb_term_sch_gms-4869";
    private static String callbackServiceName_tc_1;
    private static String callbackServiceName_tc_2 = null;
    private static String sectionName = "lab";
    private static String bulkCancelAndExportCallbackKey = "disable-bulk-cancel-and-export-callback";

    private String adminName = "bvolovyk";//change to your short admin name
    private String downloadDirectory = String.format("C:/Users/%s/Downloads", adminName);
    private String reportFileName = "CancellationSummaryReport";
    private String successSubString = "\"_ok_title\": \"Ok\"";
    private String failureSubString = "\"_ok_title\": \"FAILURE_MISSING\"";

    private static final int DAY = 86400;
    private static final int ROUTING_2_DELAY = 2; //after _treatment_waiting_for_agent (next_customer_rep.wav) start before call on an agent
    private static final int ROUTING_3_DELAY = 7; //after call on an agent
    private static final int SCHEDULED_CB_TIME = 900;
    private static final int SCHEDULED_QUEUED_CB_TIME = 10;
    private static final long POLLING_TIME_MILLIS = 250;
    private static final long TIMEOUT_FOR_SEMI_AUTOMATIC_PART = 170;
    private static final long TIME_FOR_FILE_DOWNLOADING = 2;

    private WebDriver driver = null;
    private GMSMainPage mainPage = null;
    private GMSCallbackPage callbackPage = null;
    private GMSAdvancedOptionsPopUpPage advancedOptionsPopUpPage = null;
    private GMSCancelCallbacksConfirmationPopUpPage cancelCallbacksConfirmationPopUpPage = null;
    private GMSDownloadReportsPopUpPage downloadReportsPopUpPage = null;

    @BeforeClass
    public static void oneTimeSetUp() throws Exception {
        long startTime = System.nanoTime();
        System.out.println("@BeforeClass method processing...");
        CallbackServicesAPI.setGMSBaseURL(getGMSBaseURL(propertiesFile));
        env.addOptionToSection(gmsClusterAppName, sectionName, bulkCancelAndExportCallbackKey, "false");//feature flag
        long endTime = System.nanoTime();
        getMethodExecutionTime(startTime, endTime, "@BeforeClass");
    }

    @Before
    public void setUp() {
        long startTime = System.nanoTime();
        System.out.println("@Before method processing...");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(gmsUrl);

        GMSLoginPage loginPage = new GMSLoginPage(driver);
        mainPage = loginPage.logIn(username, password);
        long endTime = System.nanoTime();
        getMethodExecutionTime(startTime, endTime, "@Before");
    }

    @After
    public void tearDown() throws AtsCfgComponentException {
        System.out.println("@After method processing...");

        cancelUnfinishedCallbacks(callbackServiceName_tc_1);
        env.deleteService(gmsClusterAppName, callbackServiceName_tc_1); //for troubleshooting purposes comment this command

        if (callbackServiceName_tc_2 != null) {
            cancelUnfinishedCallbacks(callbackServiceName_tc_2);
            env.deleteService(gmsClusterAppName, callbackServiceName_tc_2); //for troubleshooting purposes comment this command
        }

        callbackServiceName_tc_2 = null;

        if (driver != null)
            driver.quit();

        deleteFileInDir(downloadDirectory, reportFileName);
    }

    @AfterClass
    public static void oneTimeTearDown() {
        System.out.println("@AfterClass method processing...");
        env.deactivate();
    }

    @Test
    public void purgeCallbackRecords_01() throws Exception {
        callbackServiceName_tc_1 = callbackImmServiceName + "_1_1";
        callbackServiceName_tc_2 = callbackImmServiceName + "_1_2";

        //create callback services
        env.createService(gmsClusterAppName, callbackServiceName_tc_1, getUserTermImmCallbackServiceOptions());
        env.createService(gmsClusterAppName, callbackServiceName_tc_2, getUserTermImmCallbackServiceOptions());

        //initiate callbacks and save its ids to variables
        Response immediateCallback_1 = startImmediateCallback(callbackServiceName_tc_1, String.valueOf(customerNumberCounter));
        String callbackId_1 = immediateCallback_1.then().extract().path("_id");
        customerNumberCounter++;
        Response immediateCallback_2 = startImmediateCallback(callbackServiceName_tc_2, String.valueOf(customerNumberCounter));
        String callbackId_2 = immediateCallback_2.then().extract().path("_id");
        customerNumberCounter++;

        //go to Callback Management UI page and set Date Filter
        callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");

        //set corresponding checkbox to filter table by states and services
        advancedOptionsPopUpPage = callbackPage.openAdvancedOptions();
        advancedOptionsPopUpPage.uncheckAllStatesAndServices();
        advancedOptionsPopUpPage.checkFilterTableByState("QUEUED");
        advancedOptionsPopUpPage.saveAdvancedOptions();

        //select multiple callbacks
        callbackPage.checkMultipleCallbackSelection();

        //cancel callbacks
        cancelCallbacksConfirmationPopUpPage = callbackPage.cancelCallbacks();
        cancelCallbacksConfirmationPopUpPage.confirmAndCancelCallbacks();

        //check the first callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_reason"), equalTo("CANCELLED_BY_ADMIN"));
        //check the second callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_2, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_2, "_callback_reason"), equalTo("CANCELLED_BY_ADMIN"));

        //download reports
        assertThat(callbackPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Download Reports" button is a green checkmark icon
        downloadReportsPopUpPage = callbackPage.downloadReports();
        assertThat(downloadReportsPopUpPage.isCancellationSummaryReportPresent(), is(true));//in Download Reports pop up is "Cancellation Summary Report" with "Download" button
        assertThat(downloadReportsPopUpPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Cancellation Summary Report" is a green checkmark icon
        downloadReportsPopUpPage.clickDownload();
        downloadReportsPopUpPage.clickClose();
        //verify report file
        Thread.sleep(TIME_FOR_FILE_DOWNLOADING * 1000);
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, successSubString), equalTo(2));//the file contains detailed results of the cancellation for each callback
    }

    @Test
    public void purgeCallbackRecords_02() throws Exception {
        callbackServiceName_tc_1 = callbackSchServiceName + "_2_1";
        callbackServiceName_tc_2 = callbackSchServiceName + "_2_2";

        //create callback services
        env.createService(gmsClusterAppName, callbackServiceName_tc_1, getUserTermSchCallbackServiceOptions());
        env.createService(gmsClusterAppName, callbackServiceName_tc_2, getUserTermSchCallbackServiceOptions());

        //initiate callbacks and save its ids to variables
        Response scheduledCallback_1 = startScheduledCallback(callbackServiceName_tc_1, String.valueOf(customerNumberCounter), SCHEDULED_CB_TIME);
        String callbackId_1 = scheduledCallback_1.then().extract().path("_id");
        customerNumberCounter++;
        Response scheduledCallback_2 = startScheduledCallback(callbackServiceName_tc_2, String.valueOf(customerNumberCounter), 2 * DAY);//2*DAY for providing callback in another time range
        String callbackId_2 = scheduledCallback_2.then().extract().path("_id");
        customerNumberCounter++;

        //go to Callback Management UI page and set Date Filter
        callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");

        //set corresponding checkbox to filter table by states and services
        advancedOptionsPopUpPage = callbackPage.openAdvancedOptions();
        advancedOptionsPopUpPage.uncheckAllStatesAndServices();
        advancedOptionsPopUpPage.checkFilterTableByService(callbackServiceName_tc_1);
        advancedOptionsPopUpPage.saveAdvancedOptions();

        //select multiple callbacks
        callbackPage.checkMultipleCallbackSelection();

        //cancel callbacks
        cancelCallbacksConfirmationPopUpPage = callbackPage.cancelCallbacks();
        cancelCallbacksConfirmationPopUpPage.checkCancelAllCallbacksInCurrentTimeRange();
        cancelCallbacksConfirmationPopUpPage.confirmAndCancelCallbacks();

        //check the first callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_reason"), equalTo("CANCELLED_BY_ADMIN"));
        //check the second callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_2, "_callback_state"), equalTo("SCHEDULED"));

        //download reports
        assertThat(callbackPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Download Reports" button is a green checkmark icon
        downloadReportsPopUpPage = callbackPage.downloadReports();
        assertThat(downloadReportsPopUpPage.isCancellationSummaryReportPresent(), is(true));//in Download Reports pop up is "Cancellation Summary Report" with "Download" button
        assertThat(downloadReportsPopUpPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Cancellation Summary Report" is a green checkmark icon
        downloadReportsPopUpPage.clickDownload();
        downloadReportsPopUpPage.clickClose();
        //verify report file
        Thread.sleep(TIME_FOR_FILE_DOWNLOADING * 1000);
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, successSubString), equalTo(1));//the file contains detailed results of the cancellation for each callback
    }

    @Test
    public void purgeCallbackRecords_03() throws Exception {
        callbackServiceName_tc_1 = callbackImmServiceName + "_3_1";

        //create callback services
        env.createService(gmsClusterAppName, callbackServiceName_tc_1, getUserTermImmCallbackServiceOptions());

        //initiate 300 immediate callbacks for different customer numbers
        createSpecifiedNumberOfImmCallbacks(callbackServiceName_tc_1, customerNumberCounter, 300);
        customerNumberCounter += 301;

        //go to Callback Management UI page and set Date Filter
        callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");

        //set corresponding checkbox to filter table by states and services
        advancedOptionsPopUpPage = callbackPage.openAdvancedOptions();
        advancedOptionsPopUpPage.uncheckAllStatesAndServices();
        advancedOptionsPopUpPage.checkFilterTableByState("QUEUED");
        advancedOptionsPopUpPage.saveAdvancedOptions();

        //select multiple callbacks
        callbackPage.checkMultipleCallbackSelection();

        //cancel callbacks
        cancelCallbacksConfirmationPopUpPage = callbackPage.cancelCallbacks();
        assertThat(cancelCallbacksConfirmationPopUpPage.getCancelAllCallbacksInCurrentTimeRangeCheckboxName(), containsString("Cancel All Callbacks in Current Time Range"));
        assertThat(cancelCallbacksConfirmationPopUpPage.isCancelAllCallbacksInCurrentTimeRangeChecked(), is(false));
        assertThat(cancelCallbacksConfirmationPopUpPage.getCallbacksToCancelName(), containsString("Callbacks to Cancel in Current Page"));
        assertThat(cancelCallbacksConfirmationPopUpPage.countCallbackEntriesToCancel(), equalTo(250));
        cancelCallbacksConfirmationPopUpPage.checkCancelAllCallbacksInCurrentTimeRange();
        assertThat(cancelCallbacksConfirmationPopUpPage.countCallbackEntriesToCancel(), equalTo(0));
        cancelCallbacksConfirmationPopUpPage.confirmAndCancelCallbacks(cancelCallbacksConfirmationPopUpPage);//once the user clicks cancel the pop up will be dismissed and a spinner will appear near the cancellation button

        //check callback quantity in active states
        assertThat(countActiveCallbacksByAPI(callbackServiceName_tc_1), equalTo(0));

        //download reports
        assertThat(callbackPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Download Reports" button is a green checkmark icon
        downloadReportsPopUpPage = callbackPage.downloadReports();
        assertThat(downloadReportsPopUpPage.isCancellationSummaryReportPresent(), is(true));//in Download Reports pop up is "Cancellation Summary Report" with "Download" button
        assertThat(downloadReportsPopUpPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Cancellation Summary Report" is a green checkmark icon
        downloadReportsPopUpPage.clickDownload();
        downloadReportsPopUpPage.clickClose();
        //verify report file
        Thread.sleep(TIME_FOR_FILE_DOWNLOADING * 1000);
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, successSubString), equalTo(300));//the file contains detailed results of the cancellation for each callback
    }

    @Test
    public void purgeCallbackRecords_04() throws Exception {
        callbackServiceName_tc_1 = callbackSchServiceName + "_4_1";
        callbackServiceName_tc_2 = callbackSchServiceName + "_4_2";

        //create callback services
        env.createService(gmsClusterAppName, callbackServiceName_tc_1, getUserTermSchCallbackServiceOptions());
        env.createService(gmsClusterAppName, callbackServiceName_tc_2, getUserTermSchCallbackServiceOptions());

        //initiate callbacks and save its ids to variables
        Response scheduledCallback_1 = startScheduledCallback(callbackServiceName_tc_1, String.valueOf(customerNumberCounter), SCHEDULED_CB_TIME);
        String callbackId_1 = scheduledCallback_1.then().extract().path("_id");
        customerNumberCounter++;
        Response scheduledCallback_2 = startScheduledCallback(callbackServiceName_tc_2, String.valueOf(customerNumberCounter), 2 * DAY);//2*DAY for providing callback in another time range
        String callbackId_2 = scheduledCallback_2.then().extract().path("_id");
        customerNumberCounter++;

        //go to Callback Management UI page and set Date Filter
        callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");

        //set corresponding checkbox to filter table by states and services
        advancedOptionsPopUpPage = callbackPage.openAdvancedOptions();
        advancedOptionsPopUpPage.uncheckAllStatesAndServices();
        advancedOptionsPopUpPage.checkFilterTableByState("SCHEDULED");
        advancedOptionsPopUpPage.saveAdvancedOptions();

        //select multiple callbacks
        callbackPage.checkMultipleCallbackSelection();

        //cancel callbacks
        cancelCallbacksConfirmationPopUpPage = callbackPage.cancelCallbacks();
        cancelCallbacksConfirmationPopUpPage.confirmAndCancelCallbacks();

        //check the first callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_reason"), equalTo("CANCELLED_BY_ADMIN"));
        //check the second callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_2, "_callback_state"), equalTo("SCHEDULED"));

        //download reports
        assertThat(callbackPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Download Reports" button is a green checkmark icon
        downloadReportsPopUpPage = callbackPage.downloadReports();
        assertThat(downloadReportsPopUpPage.isCancellationSummaryReportPresent(), is(true));//in Download Reports pop up is "Cancellation Summary Report" with "Download" button
        assertThat(downloadReportsPopUpPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Cancellation Summary Report" is a green checkmark icon
        downloadReportsPopUpPage.clickDownload();
        downloadReportsPopUpPage.clickClose();
        //verify report file
        Thread.sleep(TIME_FOR_FILE_DOWNLOADING * 1000);
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, successSubString), equalTo(1));//the file contains detailed results of the cancellation for each callback
    }

    @Test
    public void purgeCallbackRecords_05() throws Exception {
        callbackServiceName_tc_1 = callbackImmServiceName + "_5_1";
        callbackServiceName_tc_2 = callbackSchServiceName + "_5_2";

        //create callback services
        env.createService(gmsClusterAppName, callbackServiceName_tc_1, getUserTermImmCallbackServiceOptions());
        env.createService(gmsClusterAppName, callbackServiceName_tc_2, getUserTermSchCallbackServiceOptions());

        //initiate callbacks and save its ids to variables
        Response immediateCallback_1 = startImmediateCallback(callbackServiceName_tc_1, String.valueOf(customerNumberCounter));
        String callbackId_1 = immediateCallback_1.then().extract().path("_id");
        customerNumberCounter++;
        Response scheduledCallback_2 = startScheduledCallback(callbackServiceName_tc_2, String.valueOf(customerNumberCounter), SCHEDULED_QUEUED_CB_TIME);
        String callbackId_2 = scheduledCallback_2.then().extract().path("_id");
        customerNumberCounter++;

        //go to Callback Management UI page and set Date Filter
        callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");

        //set corresponding checkbox to filter table by states and services
        advancedOptionsPopUpPage = callbackPage.openAdvancedOptions();
        advancedOptionsPopUpPage.uncheckAllStatesAndServices();
        advancedOptionsPopUpPage.checkFilterTableByState("QUEUED");
        advancedOptionsPopUpPage.checkFilterTableByService(callbackServiceName_tc_1);
        advancedOptionsPopUpPage.saveAdvancedOptions();

        //select multiple callbacks
        callbackPage.checkMultipleCallbackSelection();

        //cancel callbacks
        cancelCallbacksConfirmationPopUpPage = callbackPage.cancelCallbacks();
        cancelCallbacksConfirmationPopUpPage.confirmAndCancelCallbacks();

        //check the first callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_reason"), equalTo("CANCELLED_BY_ADMIN"));
        //check the second callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_2, "_callback_state"), equalTo("QUEUED"));

        //download reports
        assertThat(callbackPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Download Reports" button is a green checkmark icon
        downloadReportsPopUpPage = callbackPage.downloadReports();
        assertThat(downloadReportsPopUpPage.isCancellationSummaryReportPresent(), is(true));//in Download Reports pop up is "Cancellation Summary Report" with "Download" button
        assertThat(downloadReportsPopUpPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Cancellation Summary Report" is a green checkmark icon
        downloadReportsPopUpPage.clickDownload();
        downloadReportsPopUpPage.clickClose();
        //verify report file
        Thread.sleep(TIME_FOR_FILE_DOWNLOADING * 1000);
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, successSubString), equalTo(1));//the file contains detailed results of the cancellation for each callback
    }

    @Test
    public void purgeCallbackRecords_06() throws Exception {
        callbackServiceName_tc_1 = callbackSchServiceName + "_6_1";

        //create callback services
        env.createService(gmsClusterAppName, callbackServiceName_tc_1, getUserTermSchCallbackServiceOptions());

        //initiate 260 scheduled callbacks for different customer numbers
        createSpecifiedNumberOfSchCallbacks(callbackServiceName_tc_1, customerNumberCounter, SCHEDULED_CB_TIME, 260);
        customerNumberCounter += 261;

        //go to Callback Management UI page and set Date Filter
        callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");

        //set corresponding checkbox to filter table by states and services
        advancedOptionsPopUpPage = callbackPage.openAdvancedOptions();
        advancedOptionsPopUpPage.uncheckAllStatesAndServices();
        advancedOptionsPopUpPage.checkFilterTableByState("SCHEDULED");
        advancedOptionsPopUpPage.saveAdvancedOptions();

        //select multiple callbacks
        callbackPage.checkMultipleCallbackSelection();

        //cancel callbacks
        cancelCallbacksConfirmationPopUpPage = callbackPage.cancelCallbacks();
        cancelCallbacksConfirmationPopUpPage.confirmAndCancelCallbacks();

        //check callback quantity in active states
        assertThat(countActiveCallbacksByAPI(callbackServiceName_tc_1), equalTo(10));

        //download reports
        assertThat(callbackPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Download Reports" button is a green checkmark icon
        downloadReportsPopUpPage = callbackPage.downloadReports();
        assertThat(downloadReportsPopUpPage.isCancellationSummaryReportPresent(), is(true));//in Download Reports pop up is "Cancellation Summary Report" with "Download" button
        assertThat(downloadReportsPopUpPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Cancellation Summary Report" is a green checkmark icon
        downloadReportsPopUpPage.clickDownload();
        downloadReportsPopUpPage.clickClose();
        //verify report file
        Thread.sleep(TIME_FOR_FILE_DOWNLOADING * 1000);
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, successSubString), equalTo(250));//the file contains detailed results of the cancellation for each callback
    }

    @Test
    public void purgeCallbackRecords_07() throws Exception {
        callbackServiceName_tc_1 = callbackSchServiceName + "_7_1";

        //create callback services
        env.createService(gmsClusterAppName, callbackServiceName_tc_1, getUserTermSchCallbackServiceOptions());

        //initiate callbacks and save its ids to variables
        Response scheduledCallback_1 = startScheduledCallback(callbackServiceName_tc_1, String.valueOf(customerNumberCounter), SCHEDULED_CB_TIME);
        String callbackId_1 = scheduledCallback_1.then().extract().path("_id");
        customerNumberCounter++;

        //go to Callback Management UI page and set Date Filter
        callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");

        //set corresponding checkbox to filter table by states and services
        advancedOptionsPopUpPage = callbackPage.openAdvancedOptions();
        advancedOptionsPopUpPage.uncheckAllStatesAndServices();
        advancedOptionsPopUpPage.saveAdvancedOptions();

        //select multiple callbacks
        callbackPage.checkMultipleCallbackSelection();

        //cancel callbacks
        cancelCallbacksConfirmationPopUpPage = callbackPage.cancelCallbacks();
        cancelCallbacksConfirmationPopUpPage.checkCancelAllCallbacksInCurrentTimeRange();
        cancelCallbacksConfirmationPopUpPage.confirmAndCancelCallbacks();

        //check the first callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_reason"), equalTo("CANCELLED_BY_ADMIN"));

        //download reports
        assertThat(callbackPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Download Reports" button is a green checkmark icon
        downloadReportsPopUpPage = callbackPage.downloadReports();
        assertThat(downloadReportsPopUpPage.isCancellationSummaryReportPresent(), is(true));//in Download Reports pop up is "Cancellation Summary Report" with "Download" button
        assertThat(downloadReportsPopUpPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Cancellation Summary Report" is a green checkmark icon
        downloadReportsPopUpPage.clickDownload();
        downloadReportsPopUpPage.clickClose();
        //verify report file
        Thread.sleep(TIME_FOR_FILE_DOWNLOADING * 1000);
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, successSubString), equalTo(1));//the file contains detailed results of the cancellation for each callback
    }

    @Test
    public void purgeCallbackRecords_08() throws Exception {
        callbackServiceName_tc_1 = callbackImmServiceName + "_8_1";
        callbackServiceName_tc_2 = callbackSchServiceName + "_8_2";

        //create callback services
        env.createService(gmsClusterAppName, callbackServiceName_tc_1, getUserTermImmCallbackServiceOptions());
        env.createService(gmsClusterAppName, callbackServiceName_tc_2, getUserTermSchCallbackServiceOptions());

        //initiate callbacks and save its ids to variables
        Response immediateCallback_1 = startImmediateCallback(callbackServiceName_tc_1, String.valueOf(customerNumberCounter));
        String callbackId_1 = immediateCallback_1.then().extract().path("_id");
        customerNumberCounter++;
        Response scheduledCallback_2 = startScheduledCallback(callbackServiceName_tc_2, String.valueOf(customerNumberCounter), SCHEDULED_CB_TIME);
        String callbackId_2 = scheduledCallback_2.then().extract().path("_id");
        customerNumberCounter++;
        Response scheduledCallback_3 = startScheduledCallback(callbackServiceName_tc_2, String.valueOf(customerNumberCounter), SCHEDULED_QUEUED_CB_TIME);
        String callbackId_3 = scheduledCallback_3.then().extract().path("_id");
        customerNumberCounter++;

        //go to Callback Management UI page and set Date Filter
        callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");

        //set corresponding checkbox to filter table by states and services
        advancedOptionsPopUpPage = callbackPage.openAdvancedOptions();
        advancedOptionsPopUpPage.uncheckAllStatesAndServices();
//        advancedOptionsPopUpPage.checkFilterTableByState("SCHEDULED");
        advancedOptionsPopUpPage.checkFilterTableByState("QUEUED");
        advancedOptionsPopUpPage.saveAdvancedOptions();

        //select multiple callbacks
        callbackPage.checkMultipleCallbackSelection();

        //cancel callbacks
        cancelCallbacksConfirmationPopUpPage = callbackPage.cancelCallbacks();
        cancelCallbacksConfirmationPopUpPage.checkCancelAllCallbacksInCurrentTimeRange();
        cancelCallbacksConfirmationPopUpPage.confirmAndCancelCallbacks();

        //check the first callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_reason"), equalTo("CANCELLED_BY_ADMIN"));
        //check the second callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_2, "_callback_state"), equalTo("SCHEDULED"));
        //check the third callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_3, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_3, "_callback_reason"), equalTo("CANCELLED_BY_ADMIN"));

        //download reports
        assertThat(callbackPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Download Reports" button is a green checkmark icon
        downloadReportsPopUpPage = callbackPage.downloadReports();
        assertThat(downloadReportsPopUpPage.isCancellationSummaryReportPresent(), is(true));//in Download Reports pop up is "Cancellation Summary Report" with "Download" button
        assertThat(downloadReportsPopUpPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Cancellation Summary Report" is a green checkmark icon
        downloadReportsPopUpPage.clickDownload();
        downloadReportsPopUpPage.clickClose();
        //verify report file
        Thread.sleep(TIME_FOR_FILE_DOWNLOADING * 1000);
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, successSubString), equalTo(2));//the file contains detailed results of the cancellation for each callback
    }

    @Test
    public void purgeCallbackRecords_09() throws Exception {
        callbackServiceName_tc_1 = callbackSchServiceName + "_9_1";

        //create callback services
        env.createService(gmsClusterAppName, callbackServiceName_tc_1, getUserTermSchCallbackServiceOptions());

        //initiate callbacks and save its ids to variables
        Response scheduledCallback_1 = startScheduledCallback(callbackServiceName_tc_1, String.valueOf(customerNumberCounter), SCHEDULED_QUEUED_CB_TIME);
        String callbackId_1 = scheduledCallback_1.then().extract().path("_id");
        customerNumberCounter++;

        //go to Callback Management UI page and set Date Filter
        callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");

        //set corresponding checkbox to filter table by states and services
        advancedOptionsPopUpPage = callbackPage.openAdvancedOptions();
        advancedOptionsPopUpPage.uncheckAllStatesAndServices();
        advancedOptionsPopUpPage.saveAdvancedOptions();

        //select multiple callbacks
        callbackPage.checkMultipleCallbackSelection();

        //cancel callbacks
        cancelCallbacksConfirmationPopUpPage = callbackPage.cancelCallbacks();
        cancelCallbacksConfirmationPopUpPage.confirmAndCancelCallbacks();

        //check the first callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_reason"), equalTo("CANCELLED_BY_ADMIN"));

        //download reports
        assertThat(callbackPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Download Reports" button is a green checkmark icon
        downloadReportsPopUpPage = callbackPage.downloadReports();
        assertThat(downloadReportsPopUpPage.isCancellationSummaryReportPresent(), is(true));//in Download Reports pop up is "Cancellation Summary Report" with "Download" button
        assertThat(downloadReportsPopUpPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Cancellation Summary Report" is a green checkmark icon
        downloadReportsPopUpPage.clickDownload();
        downloadReportsPopUpPage.clickClose();
        //verify report file
        Thread.sleep(TIME_FOR_FILE_DOWNLOADING * 1000);
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, successSubString), equalTo(1));//the file contains detailed results of the cancellation for each callback
    }

    @Test
    public void purgeCallbackRecords_10() throws Exception {
        callbackServiceName_tc_1 = callbackImmServiceName + "_10_1";
        callbackServiceName_tc_2 = callbackSchServiceName + "_10_2";

        //create callback services
        env.createService(gmsClusterAppName, callbackServiceName_tc_1, getUserTermImmCallbackServiceOptions());
        env.createService(gmsClusterAppName, callbackServiceName_tc_2, getUserTermSchCallbackServiceOptions());

        //initiate callbacks and save its ids to variables
        Response immediateCallback_1 = startImmediateCallback(callbackServiceName_tc_1, String.valueOf(customerNumberCounter));
        String callbackId_1 = immediateCallback_1.then().extract().path("_id");
        customerNumberCounter++;
        Response scheduledCallback_2 = startScheduledCallback(callbackServiceName_tc_2, String.valueOf(customerNumberCounter), SCHEDULED_CB_TIME);
        String callbackId_2 = scheduledCallback_2.then().extract().path("_id");
        customerNumberCounter++;

        //go to Callback Management UI page and set Date Filter
        callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");

        //set corresponding checkbox to filter table by states and services
        advancedOptionsPopUpPage = callbackPage.openAdvancedOptions();
        advancedOptionsPopUpPage.uncheckAllStatesAndServices();
        advancedOptionsPopUpPage.checkFilterTableByService(callbackServiceName_tc_1);
        advancedOptionsPopUpPage.saveAdvancedOptions();

        //select multiple callbacks
        callbackPage.checkMultipleCallbackSelection();

        //cancel callbacks
        cancelCallbacksConfirmationPopUpPage = callbackPage.cancelCallbacks();
        cancelCallbacksConfirmationPopUpPage.checkCancelAllCallbacksInCurrentTimeRange();
        cancelCallbacksConfirmationPopUpPage.confirmAndCancelCallbacks();

        //check the first callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_reason"), equalTo("CANCELLED_BY_ADMIN"));
        //check the second callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_2, "_callback_state"), equalTo("SCHEDULED"));

        //download reports
        assertThat(callbackPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Download Reports" button is a green checkmark icon
        downloadReportsPopUpPage = callbackPage.downloadReports();
        assertThat(downloadReportsPopUpPage.isCancellationSummaryReportPresent(), is(true));//in Download Reports pop up is "Cancellation Summary Report" with "Download" button
        assertThat(downloadReportsPopUpPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Cancellation Summary Report" is a green checkmark icon
        downloadReportsPopUpPage.clickDownload();
        downloadReportsPopUpPage.clickClose();
        //verify report file
        Thread.sleep(TIME_FOR_FILE_DOWNLOADING * 1000);
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, successSubString), equalTo(1));//the file contains detailed results of the cancellation for each callback
    }

    @Test
    public void purgeCallbackRecords_11() throws Exception {
        callbackServiceName_tc_1 = callbackImmServiceName + "_11_1";
        callbackServiceName_tc_2 = callbackImmServiceName + "_11_2";

        //create callback services
        env.createService(gmsClusterAppName, callbackServiceName_tc_1, getUserTermImmCallbackServiceOptions());
        env.createService(gmsClusterAppName, callbackServiceName_tc_2, getUserTermImmCallbackServiceOptions());

        //initiate callbacks and save its ids to variables
        Response immediateCallback_1 = startImmediateCallback(callbackServiceName_tc_1, String.valueOf(customerNumberCounter));
        String callbackId_1 = immediateCallback_1.then().extract().path("_id");
        customerNumberCounter++;
        Response immediateCallback_2 = startImmediateCallback(callbackServiceName_tc_2, String.valueOf(customerNumberCounter));
        String callbackId_2 = immediateCallback_2.then().extract().path("_id");
        customerNumberCounter++;

        //go to Callback Management UI page and set Date Filter
        callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");

        //set corresponding checkbox to filter table by states and services
        advancedOptionsPopUpPage = callbackPage.openAdvancedOptions();
        advancedOptionsPopUpPage.uncheckAllStatesAndServices();
        advancedOptionsPopUpPage.checkFilterTableByService(callbackServiceName_tc_1);
        advancedOptionsPopUpPage.saveAdvancedOptions();

        //select multiple callbacks
        callbackPage.checkMultipleCallbackSelection();

        //cancel callbacks
        cancelCallbacksConfirmationPopUpPage = callbackPage.cancelCallbacks();
        cancelCallbacksConfirmationPopUpPage.checkCancelAllCallbacksInCurrentTimeRange();
        cancelCallbacksConfirmationPopUpPage.confirmAndCancelCallbacks();

        //check the first callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_reason"), equalTo("CANCELLED_BY_ADMIN"));
        //check the second callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_2, "_callback_state"), equalTo("QUEUED"));

        //download reports
        assertThat(callbackPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Download Reports" button is a green checkmark icon
        downloadReportsPopUpPage = callbackPage.downloadReports();
        assertThat(downloadReportsPopUpPage.isCancellationSummaryReportPresent(), is(true));//in Download Reports pop up is "Cancellation Summary Report" with "Download" button
        assertThat(downloadReportsPopUpPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Cancellation Summary Report" is a green checkmark icon
        downloadReportsPopUpPage.clickDownload();
        downloadReportsPopUpPage.clickClose();
        //verify report file
        Thread.sleep(TIME_FOR_FILE_DOWNLOADING * 1000);
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, successSubString), equalTo(1));//the file contains detailed results of the cancellation for each callback
    }

    @Test
    public void purgeCallbackRecords_12() throws Exception {
        callbackServiceName_tc_1 = callbackImmServiceName + "_12_1";

        //create callback services
        env.createService(gmsClusterAppName, callbackServiceName_tc_1, getUserTermImmCallbackServiceOptions());

        //initiate callbacks and save its ids to variables
        Response immediateCallback_1 = startImmediateCallback(callbackServiceName_tc_1, String.valueOf(customerNumberCounter));
        String callbackId_1 = immediateCallback_1.then().extract().path("_id");
        customerNumberCounter++;

        //go to Callback Management UI page and set Date Filter
        callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");

        //set corresponding checkbox to filter table by states and services
        advancedOptionsPopUpPage = callbackPage.openAdvancedOptions();
        advancedOptionsPopUpPage.uncheckAllStatesAndServices();
        advancedOptionsPopUpPage.saveAdvancedOptions();

        //select multiple callbacks
        callbackPage.checkMultipleCallbackSelection();

        //cancel callbacks
        cancelCallbacksConfirmationPopUpPage = callbackPage.cancelCallbacks();
        cancelCallbacksConfirmationPopUpPage.confirmAndCancelCallbacks();

        //check the first callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_reason"), equalTo("CANCELLED_BY_ADMIN"));

        //download reports
        assertThat(callbackPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Download Reports" button is a green checkmark icon
        downloadReportsPopUpPage = callbackPage.downloadReports();
        assertThat(downloadReportsPopUpPage.isCancellationSummaryReportPresent(), is(true));//in Download Reports pop up is "Cancellation Summary Report" with "Download" button
        assertThat(downloadReportsPopUpPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Cancellation Summary Report" is a green checkmark icon
        downloadReportsPopUpPage.clickDownload();
        downloadReportsPopUpPage.clickClose();
        //verify report file
        Thread.sleep(TIME_FOR_FILE_DOWNLOADING * 1000);
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, successSubString), equalTo(1));//the file contains detailed results of the cancellation for each callback
    }

    @Test //semi-automated scenario for ROUTING_3 state
    public void purgeCallbackRecords_13() throws Exception {
        callbackServiceName_tc_1 = callbackImmServiceName + "_13_1";

        //create callback services
        env.createService(gmsClusterAppName, callbackServiceName_tc_1, getUserTermImmCallbackServiceOptions());

        //go to Callback Management UI page and set Date Filter
        callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");

        //set corresponding checkbox to filter table by states and services
        advancedOptionsPopUpPage = callbackPage.openAdvancedOptions();
        advancedOptionsPopUpPage.uncheckAllStatesAndServices();
        //initiate callbacks and save its ids to variables
        Response immediateCallback_1 = startImmediateCallback(callbackServiceName_tc_1, String.valueOf(customerNumber));
        String callbackId_1 = immediateCallback_1.then().extract().path("_id");
        advancedOptionsPopUpPage.saveAdvancedOptions();

        //select multiple callbacks
        callbackPage.checkMultipleCallbackSelection();

        //cancel callbacks
        cancelCallbacksConfirmationPopUpPage = callbackPage.cancelCallbacks();
        cancelCallbacksConfirmationPopUpPage.checkCancelAllCallbacksInCurrentTimeRange();
        //TODO: implement automatic answer on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId_1, "ROUTING", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);
        Thread.sleep(ROUTING_3_DELAY * 1000);
        cancelCallbacksConfirmationPopUpPage.confirmAndCancelCallbacks();

        //check the callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_reason"), equalTo("CANCELLED_BY_ADMIN"));

        //download reports
        assertThat(callbackPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Download Reports" button is a green checkmark icon
        downloadReportsPopUpPage = callbackPage.downloadReports();
        assertThat(downloadReportsPopUpPage.isCancellationSummaryReportPresent(), is(true));//in Download Reports pop up is "Cancellation Summary Report" with "Download" button
        assertThat(downloadReportsPopUpPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Cancellation Summary Report" is a green checkmark icon
        downloadReportsPopUpPage.clickDownload();
        downloadReportsPopUpPage.clickClose();
        //verify report file
        Thread.sleep(TIME_FOR_FILE_DOWNLOADING * 1000);
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, successSubString), equalTo(1));//the file contains detailed results of the cancellation for each callback
    }

    @Test
    public void purgeCallbackRecords_14() throws Exception {
        callbackServiceName_tc_1 = callbackSchServiceName + "_14_1";
        callbackServiceName_tc_2 = callbackSchServiceName + "_14_2";

        //create callback services
        env.createService(gmsClusterAppName, callbackServiceName_tc_1, getUserTermSchCallbackServiceOptions());
        env.createService(gmsClusterAppName, callbackServiceName_tc_2, getUserTermSchCallbackServiceOptions());

        //initiate callbacks and save its ids to variables
        Response scheduledCallback_1 = startScheduledCallback(callbackServiceName_tc_1, String.valueOf(customerNumberCounter), SCHEDULED_CB_TIME);
        String callbackId_1 = scheduledCallback_1.then().extract().path("_id");
        customerNumberCounter++;
        Response scheduledCallback_2 = startScheduledCallback(callbackServiceName_tc_2, String.valueOf(customerNumberCounter), 2 * DAY);//2*DAY for providing callback in another time range
        String callbackId_2 = scheduledCallback_2.then().extract().path("_id");
        customerNumberCounter++;

        //go to Callback Management UI page and set Date Filter
        callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");

        //set corresponding checkbox to filter table by states and services
        advancedOptionsPopUpPage = callbackPage.openAdvancedOptions();
        advancedOptionsPopUpPage.uncheckAllStatesAndServices();
        advancedOptionsPopUpPage.saveAdvancedOptions();

        //select multiple callbacks
        callbackPage.checkMultipleCallbackSelection();

        //cancel callbacks
        cancelCallbacksConfirmationPopUpPage = callbackPage.cancelCallbacks();
        cancelCallbacksConfirmationPopUpPage.checkCancelAllCallbacksInCurrentTimeRange();
        cancelCallback(callbackServiceName_tc_2, callbackId_2)
                .then()
                .assertThat()
                .statusCode(200);
        cancelCallbacksConfirmationPopUpPage.confirmAndCancelCallbacks();

        //check the first callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_reason"), equalTo("CANCELLED_BY_ADMIN"));
        //check the second callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_2, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_2, "_callback_reason"), equalTo("CANCELLED"));

        //download reports
        assertThat(callbackPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Download Reports" button is a green checkmark icon
        downloadReportsPopUpPage = callbackPage.downloadReports();
        assertThat(downloadReportsPopUpPage.isCancellationSummaryReportPresent(), is(true));//in Download Reports pop up is "Cancellation Summary Report" with "Download" button
        assertThat(downloadReportsPopUpPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Cancellation Summary Report" is a green checkmark icon
        downloadReportsPopUpPage.clickDownload();
        downloadReportsPopUpPage.clickClose();
        //verify report file
        Thread.sleep(TIME_FOR_FILE_DOWNLOADING * 1000);
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, successSubString), equalTo(1));//the file contains detailed results of the cancellation for each callback
    }

    @Test //semi-automated scenario for ROUTING_3 state
    public void purgeCallbackRecords_15() throws Exception {
        callbackServiceName_tc_1 = callbackImmServiceName + "_15_1";
        callbackServiceName_tc_2 = callbackSchServiceName + "_15_2";

        //create callback services
        env.createService(gmsClusterAppName, callbackServiceName_tc_1, getUserTermImmCallbackServiceOptions());
        env.createService(gmsClusterAppName, callbackServiceName_tc_2, getUserTermSchCallbackServiceOptions());

        //initiate callbacks and save its ids to variables
        Response immediateCallback_1 = startImmediateCallback(callbackServiceName_tc_1, String.valueOf(customerNumberCounter));
        String callbackId_1 = immediateCallback_1.then().extract().path("_id");
        customerNumberCounter++;
        Response scheduledCallback_2 = startScheduledCallback(callbackServiceName_tc_2, String.valueOf(customerNumber), SCHEDULED_QUEUED_CB_TIME);
        String callbackId_2 = scheduledCallback_2.then().extract().path("_id");

        //go to Callback Management UI page and set Date Filter
        callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");

        //set corresponding checkbox to filter table by states and services
        advancedOptionsPopUpPage = callbackPage.openAdvancedOptions();
        advancedOptionsPopUpPage.uncheckAllStatesAndServices();
        advancedOptionsPopUpPage.saveAdvancedOptions();

        //select multiple callbacks
        callbackPage.checkMultipleCallbackSelection();

        //cancel callbacks
        cancelCallbacksConfirmationPopUpPage = callbackPage.cancelCallbacks();
        cancelCallbacksConfirmationPopUpPage.checkCancelAllCallbacksInCurrentTimeRange();
        //TODO: implement automatic answer on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId_2, "ROUTING", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);
        Thread.sleep(ROUTING_3_DELAY * 1000);
        cancelCallbacksConfirmationPopUpPage.confirmAndCancelCallbacks();

        //check the first callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_reason"), equalTo("CANCELLED_BY_ADMIN"));
        //check the second callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_2, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_2, "_callback_reason"), equalTo("CANCELLED_BY_ADMIN"));

        //download reports
        assertThat(callbackPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Download Reports" button is a green checkmark icon
        downloadReportsPopUpPage = callbackPage.downloadReports();
        assertThat(downloadReportsPopUpPage.isCancellationSummaryReportPresent(), is(true));//in Download Reports pop up is "Cancellation Summary Report" with "Download" button
        assertThat(downloadReportsPopUpPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Cancellation Summary Report" is a green checkmark icon
        downloadReportsPopUpPage.clickDownload();
        downloadReportsPopUpPage.clickClose();
        //verify report file
        Thread.sleep(TIME_FOR_FILE_DOWNLOADING * 1000);
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, successSubString), equalTo(2));//the file contains detailed results of the cancellation for each callback
    }

    @Test //semi-automated scenario for PROCESSING state
    public void purgeCallbackRecords_16() throws Exception {
        callbackServiceName_tc_1 = callbackImmServiceName + "_16_1";
        callbackServiceName_tc_2 = callbackImmServiceName + "_16_2";

        //create callback services
        env.createService(gmsClusterAppName, callbackServiceName_tc_1, getUserTermImmCallbackServiceOptions());
        env.createService(gmsClusterAppName, callbackServiceName_tc_2, getUserTermImmCallbackServiceOptions());

        //initiate first callback and save its id to variable
        Response immediateCallback_1 = startImmediateCallback(callbackServiceName_tc_1, String.valueOf(customerNumberCounter));
        String callbackId_1 = immediateCallback_1.then().extract().path("_id");
        customerNumberCounter++;

        //go to Callback Management UI page and set Date Filter
        callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");

        //set corresponding checkbox to filter table by states and services
        advancedOptionsPopUpPage = callbackPage.openAdvancedOptions();
        advancedOptionsPopUpPage.uncheckAllStatesAndServices();
        //initiate second callback and save its id to variable
        Response immediateCallback_2 = startImmediateCallback(callbackServiceName_tc_2, String.valueOf(customerNumber));
        String callbackId_2 = immediateCallback_2.then().extract().path("_id");
        advancedOptionsPopUpPage.saveAdvancedOptions();

        //select multiple callbacks
        callbackPage.checkMultipleCallbackSelection();

        //cancel callbacks
        cancelCallbacksConfirmationPopUpPage = callbackPage.cancelCallbacks();
        cancelCallbacksConfirmationPopUpPage.checkCancelAllCallbacksInCurrentTimeRange();
        //TODO: implement automatic answer on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId_2, "PROCESSING", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);
        cancelCallbacksConfirmationPopUpPage.confirmAndCancelCallbacks();

        //check the first callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_reason"), equalTo("CANCELLED_BY_ADMIN"));
        //check the second callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_2, "_callback_state"), equalTo("PROCESSING"));

        //download reports
        assertThat(callbackPage.isRedXIconPresent(), is(true));//beside the "Download Reports" button is a red X icon
        downloadReportsPopUpPage = callbackPage.downloadReports();
        assertThat(downloadReportsPopUpPage.isCancellationSummaryReportPresent(), is(true));//in Download Reports pop up is "Cancellation Summary Report" with "Download" button
        assertThat(downloadReportsPopUpPage.isRedXIconPresent(), is(true));//beside the "Cancellation Summary Report" is a red X icon
        downloadReportsPopUpPage.clickDownload();
        downloadReportsPopUpPage.clickClose();
        //verify report file
        Thread.sleep(TIME_FOR_FILE_DOWNLOADING * 1000);
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, successSubString), equalTo(1));//the file contains detailed results of the cancellation for each callback
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, failureSubString), equalTo(1));

        //TODO: implement automatic hang up on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId_2, "COMPLETED", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);
    }

    @Test //semi-automated scenario for PROCESSING state
    public void purgeCallbackRecords_17() throws Exception {
        callbackServiceName_tc_1 = callbackSchServiceName + "_17_1";
        callbackServiceName_tc_2 = callbackSchServiceName + "_17_2";

        //create callback services
        env.createService(gmsClusterAppName, callbackServiceName_tc_1, getUserTermSchCallbackServiceOptions());
        env.createService(gmsClusterAppName, callbackServiceName_tc_2, getUserTermSchCallbackServiceOptions());

        //initiate callbacks and save its ids to variables
        Response scheduledCallback_1 = startScheduledCallback(callbackServiceName_tc_1, String.valueOf(customerNumberCounter), SCHEDULED_CB_TIME);
        String callbackId_1 = scheduledCallback_1.then().extract().path("_id");
        customerNumberCounter++;
        Response scheduledCallback_2 = startScheduledCallback(callbackServiceName_tc_2, String.valueOf(customerNumber), SCHEDULED_QUEUED_CB_TIME);
        String callbackId_2 = scheduledCallback_2.then().extract().path("_id");

        //go to Callback Management UI page and set Date Filter
        callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");

        //set corresponding checkbox to filter table by states and services
        advancedOptionsPopUpPage = callbackPage.openAdvancedOptions();
        advancedOptionsPopUpPage.uncheckAllStatesAndServices();
        advancedOptionsPopUpPage.saveAdvancedOptions();

        //select multiple callbacks
        callbackPage.checkMultipleCallbackSelection();

        //cancel callbacks
        cancelCallbacksConfirmationPopUpPage = callbackPage.cancelCallbacks();
        cancelCallbacksConfirmationPopUpPage.checkCancelAllCallbacksInCurrentTimeRange();
        //TODO: implement automatic answer on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId_2, "PROCESSING", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);
        cancelCallbacksConfirmationPopUpPage.confirmAndCancelCallbacks();

        //check the first callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_reason"), equalTo("CANCELLED_BY_ADMIN"));
        //check the second callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_2, "_callback_state"), equalTo("PROCESSING"));

        //download reports
        assertThat(callbackPage.isRedXIconPresent(), is(true));//beside the "Download Reports" button is a red X icon
        downloadReportsPopUpPage = callbackPage.downloadReports();
        assertThat(downloadReportsPopUpPage.isCancellationSummaryReportPresent(), is(true));//in Download Reports pop up is "Cancellation Summary Report" with "Download" button
        assertThat(downloadReportsPopUpPage.isRedXIconPresent(), is(true));//beside the "Cancellation Summary Report" is a red X icon
        downloadReportsPopUpPage.clickDownload();
        downloadReportsPopUpPage.clickClose();
        //verify report file
        Thread.sleep(TIME_FOR_FILE_DOWNLOADING * 1000);
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, successSubString), equalTo(1));//the file contains detailed results of the cancellation for each callback
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, failureSubString), equalTo(1));//the file contains detailed results of the cancellation for each callback

        //TODO: implement automatic hang up on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId_2, "COMPLETED", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);
    }

    @Test
    public void purgeCallbackRecords_18() throws Exception {
        callbackServiceName_tc_1 = callbackImmServiceName + "_18_1";

        //create callback services
        env.createService(gmsClusterAppName, callbackServiceName_tc_1, getUserTermImmCallbackServiceOptions());

        //initiate callbacks and save its ids to variables
        Response immediateCallback_1 = startImmediateCallback(callbackServiceName_tc_1, String.valueOf(customerNumberCounter));
        String callbackId_1 = immediateCallback_1.then().extract().path("_id");
        customerNumberCounter++;

        //go to Callback Management UI page and set Date Filter
        callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");

        //set corresponding checkbox to filter table by states and services
        advancedOptionsPopUpPage = callbackPage.openAdvancedOptions();
        advancedOptionsPopUpPage.uncheckAllStatesAndServices();
        advancedOptionsPopUpPage.saveAdvancedOptions();

        //select multiple callbacks
        callbackPage.checkMultipleCallbackSelection();

        //cancel callbacks
        cancelCallbacksConfirmationPopUpPage = callbackPage.cancelCallbacks();
        cancelCallbacksConfirmationPopUpPage.checkCancelAllCallbacksInCurrentTimeRange();
        cancelCallback(callbackServiceName_tc_1, callbackId_1)
                .then()
                .assertThat()
                .statusCode(200);
        cancelCallbacksConfirmationPopUpPage.confirmAndCancelCallbacks();

        //check the first callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_reason"), equalTo("CANCELLED"));

        //download reports
        assertThat(callbackPage.isRedXIconPresent(), is(true));//beside the "Download Reports" button is a red X icon
        downloadReportsPopUpPage = callbackPage.downloadReports();
        assertThat(downloadReportsPopUpPage.isCancellationSummaryReportPresent(), is(true));//in Download Reports pop up is "Cancellation Summary Report" with "Download" button
        assertThat(downloadReportsPopUpPage.isRedXIconPresent(), is(true));//beside the "Cancellation Summary Report" is a red X icon
        downloadReportsPopUpPage.clickDownload();
        downloadReportsPopUpPage.clickClose();
        //verify report file
        Thread.sleep(TIME_FOR_FILE_DOWNLOADING * 1000);
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, failureSubString), equalTo(1));//the file contains detailed results of the cancellation for each callback
    }

    @Test //semi-automated scenario for ROUTING_1 state
    public void purgeCallbackRecords_19() throws Exception {
        callbackServiceName_tc_1 = callbackSchServiceName + "_19_1";

        //create callback services
        env.createService(gmsClusterAppName, callbackServiceName_tc_1, getUserTermSchCallbackServiceOptions());

        //initiate callbacks and save its ids to variables
        Response scheduledCallback_1 = startScheduledCallback(callbackServiceName_tc_1, String.valueOf(customerNumber), SCHEDULED_QUEUED_CB_TIME);
        String callbackId_1 = scheduledCallback_1.then().extract().path("_id");

        //go to Callback Management UI page and set Date Filter
        callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");

        //set corresponding checkbox to filter table by states and services
        advancedOptionsPopUpPage = callbackPage.openAdvancedOptions();
        advancedOptionsPopUpPage.uncheckAllStatesAndServices();
        advancedOptionsPopUpPage.saveAdvancedOptions();

        //select multiple callbacks
        callbackPage.checkMultipleCallbackSelection();

        //cancel callbacks
        cancelCallbacksConfirmationPopUpPage = callbackPage.cancelCallbacks();
        cancelCallbacksConfirmationPopUpPage.checkCancelAllCallbacksInCurrentTimeRange();
        //TODO: implement automatic answer on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId_1, "ROUTING", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);
        cancelCallbacksConfirmationPopUpPage.confirmAndCancelCallbacks();

        //check the callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_reason"), equalTo("CANCELLED_BY_ADMIN"));

        //download reports
        assertThat(callbackPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Download Reports" button is a green checkmark icon
        downloadReportsPopUpPage = callbackPage.downloadReports();
        assertThat(downloadReportsPopUpPage.isCancellationSummaryReportPresent(), is(true));//in Download Reports pop up is "Cancellation Summary Report" with "Download" button
        assertThat(downloadReportsPopUpPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Cancellation Summary Report" is a green checkmark icon
        downloadReportsPopUpPage.clickDownload();
        downloadReportsPopUpPage.clickClose();
        //verify report file
        Thread.sleep(TIME_FOR_FILE_DOWNLOADING * 1000);
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, successSubString), equalTo(1));//the file contains detailed results of the cancellation for each callback
    }

    @Test //semi-automated scenario for ROUTING_1 state
    public void purgeCallbackRecords_20() throws Exception {
        callbackServiceName_tc_1 = callbackImmServiceName + "_20_1";
        callbackServiceName_tc_2 = callbackSchServiceName + "_20_2";

        //create callback services
        env.createService(gmsClusterAppName, callbackServiceName_tc_1, getUserTermImmCallbackServiceOptions());
        env.createService(gmsClusterAppName, callbackServiceName_tc_2, getUserTermSchCallbackServiceOptions());

        //initiate callbacks and save its ids to variables
        Response immediateCallback_1 = startImmediateCallback(callbackServiceName_tc_1, String.valueOf(customerNumberCounter));
        String callbackId_1 = immediateCallback_1.then().extract().path("_id");
        customerNumberCounter++;
        Response scheduledCallback_2 = startScheduledCallback(callbackServiceName_tc_2, String.valueOf(customerNumber), SCHEDULED_QUEUED_CB_TIME);
        String callbackId_2 = scheduledCallback_2.then().extract().path("_id");

        //go to Callback Management UI page and set Date Filter
        callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");

        //set corresponding checkbox to filter table by states and services
        advancedOptionsPopUpPage = callbackPage.openAdvancedOptions();
        advancedOptionsPopUpPage.uncheckAllStatesAndServices();
        advancedOptionsPopUpPage.saveAdvancedOptions();

        //select multiple callbacks
        callbackPage.checkMultipleCallbackSelection();

        //cancel callbacks
        cancelCallbacksConfirmationPopUpPage = callbackPage.cancelCallbacks();
        cancelCallbacksConfirmationPopUpPage.checkCancelAllCallbacksInCurrentTimeRange();
        //TODO: implement automatic answer on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId_2, "ROUTING", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);
        cancelCallbacksConfirmationPopUpPage.confirmAndCancelCallbacks();

        //check the first callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_reason"), equalTo("CANCELLED_BY_ADMIN"));
        //check the second callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_2, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_2, "_callback_reason"), equalTo("CANCELLED_BY_ADMIN"));

        //download reports
        assertThat(callbackPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Download Reports" button is a green checkmark icon
        downloadReportsPopUpPage = callbackPage.downloadReports();
        assertThat(downloadReportsPopUpPage.isCancellationSummaryReportPresent(), is(true));//in Download Reports pop up is "Cancellation Summary Report" with "Download" button
        assertThat(downloadReportsPopUpPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Cancellation Summary Report" is a green checkmark icon
        downloadReportsPopUpPage.clickDownload();
        downloadReportsPopUpPage.clickClose();
        //verify report file
        Thread.sleep(TIME_FOR_FILE_DOWNLOADING * 1000);
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, successSubString), equalTo(2));//the file contains detailed results of the cancellation for each callback
    }

    @Test
    public void purgeCallbackRecords_21() throws Exception {
        callbackServiceName_tc_1 = callbackImmServiceName + "_21_1";
        callbackServiceName_tc_2 = callbackSchServiceName + "_21_2";

        //create callback services
        env.createService(gmsClusterAppName, callbackServiceName_tc_1, getUserTermImmCallbackServiceOptions());
        env.createService(gmsClusterAppName, callbackServiceName_tc_2, getUserTermSchCallbackServiceOptions());

        //initiate callbacks and save its ids to variables
        Response immediateCallback_1 = startImmediateCallback(callbackServiceName_tc_1, String.valueOf(customerNumberCounter));
        String callbackId_1 = immediateCallback_1.then().extract().path("_id");
        customerNumberCounter++;
        Response scheduledCallback_2 = startScheduledCallback(callbackServiceName_tc_2, String.valueOf(customerNumberCounter), 2 * DAY);//2*DAY for providing callback in another time range
        String callbackId_2 = scheduledCallback_2.then().extract().path("_id");
        customerNumberCounter++;

        //go to Callback Management UI page and set Date Filter
        callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");

        //set corresponding checkbox to filter table by states and services
        advancedOptionsPopUpPage = callbackPage.openAdvancedOptions();
        advancedOptionsPopUpPage.uncheckAllStatesAndServices();
        advancedOptionsPopUpPage.saveAdvancedOptions();

        //select multiple callbacks
        callbackPage.checkMultipleCallbackSelection();

        //cancel callbacks
        cancelCallbacksConfirmationPopUpPage = callbackPage.cancelCallbacks();
        cancelCallbacksConfirmationPopUpPage.checkCancelAllCallbacksInCurrentTimeRange();
        cancelCallback(callbackServiceName_tc_2, callbackId_2)
                .then()
                .assertThat()
                .statusCode(200);
        cancelCallbacksConfirmationPopUpPage.confirmAndCancelCallbacks();

        //check the first callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_reason"), equalTo("CANCELLED_BY_ADMIN"));
        //check the second callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_2, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_2, "_callback_reason"), equalTo("CANCELLED"));

        //download reports
        assertThat(callbackPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Download Reports" button is a green checkmark icon
        downloadReportsPopUpPage = callbackPage.downloadReports();
        assertThat(downloadReportsPopUpPage.isCancellationSummaryReportPresent(), is(true));//in Download Reports pop up is "Cancellation Summary Report" with "Download" button
        assertThat(downloadReportsPopUpPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Cancellation Summary Report" is a green checkmark icon
        downloadReportsPopUpPage.clickDownload();
        downloadReportsPopUpPage.clickClose();
        //verify report file
        Thread.sleep(TIME_FOR_FILE_DOWNLOADING * 1000);
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, successSubString), equalTo(1));//the file contains detailed results of the cancellation for each callback
    }

    @Test
    public void purgeCallbackRecords_22() throws Exception {
        callbackServiceName_tc_1 = callbackImmServiceName + "_22_1";
        callbackServiceName_tc_2 = callbackSchServiceName + "_22_2";

        //create callback services
        env.createService(gmsClusterAppName, callbackServiceName_tc_1, getUserTermImmCallbackServiceOptions());
        env.createService(gmsClusterAppName, callbackServiceName_tc_2, getUserTermSchCallbackServiceOptions());

        //initiate callbacks and save its ids to variables
        Response immediateCallback_1 = startImmediateCallback(callbackServiceName_tc_1, String.valueOf(customerNumberCounter));
        String callbackId_1 = immediateCallback_1.then().extract().path("_id");
        customerNumberCounter++;
        Response scheduledCallback_2 = startScheduledCallback(callbackServiceName_tc_2, String.valueOf(customerNumberCounter), SCHEDULED_CB_TIME);
        String callbackId_2 = scheduledCallback_2.then().extract().path("_id");
        customerNumberCounter++;

        //go to Callback Management UI page and set Date Filter
        callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");

        //set corresponding checkbox to filter table by states and services
        advancedOptionsPopUpPage = callbackPage.openAdvancedOptions();
        advancedOptionsPopUpPage.uncheckAllStatesAndServices();
        advancedOptionsPopUpPage.saveAdvancedOptions();

        //select multiple callbacks
        callbackPage.checkMultipleCallbackSelection();

        //cancel callbacks
        cancelCallbacksConfirmationPopUpPage = callbackPage.cancelCallbacks();
        cancelCallbacksConfirmationPopUpPage.checkCancelAllCallbacksInCurrentTimeRange();
        cancelCallback(callbackServiceName_tc_2, callbackId_2)
                .then()
                .assertThat()
                .statusCode(200);
        cancelCallbacksConfirmationPopUpPage.confirmAndCancelCallbacks();

        //check the first callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_reason"), equalTo("CANCELLED_BY_ADMIN"));
        //check the second callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_2, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_2, "_callback_reason"), equalTo("CANCELLED"));

        //download reports
        assertThat(callbackPage.isRedXIconPresent(), is(true));//beside the "Download Reports" button is a red X icon
        downloadReportsPopUpPage = callbackPage.downloadReports();
        assertThat(downloadReportsPopUpPage.isCancellationSummaryReportPresent(), is(true));//in Download Reports pop up is "Cancellation Summary Report" with "Download" button
        assertThat(downloadReportsPopUpPage.isRedXIconPresent(), is(true));//beside the "Cancellation Summary Report" is a red X icon
        downloadReportsPopUpPage.clickDownload();
        downloadReportsPopUpPage.clickClose();
        //verify report file
        Thread.sleep(TIME_FOR_FILE_DOWNLOADING * 1000);
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, successSubString), equalTo(1));//the file contains detailed results of the cancellation for each callback
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, failureSubString), equalTo(1));//the file contains detailed results of the cancellation for each callback
    }

    @Test //semi-automated scenario for ROUTING_3 state
    public void purgeCallbackRecords_23() throws Exception {
        callbackServiceName_tc_1 = callbackSchServiceName + "_23_1";
        callbackServiceName_tc_2 = callbackSchServiceName + "_23_2";

        //create callback services
        env.createService(gmsClusterAppName, callbackServiceName_tc_1, getUserTermSchCallbackServiceOptions());
        env.createService(gmsClusterAppName, callbackServiceName_tc_2, getUserTermSchCallbackServiceOptions());

        //initiate callbacks and save its ids to variables
        Response scheduledCallback_1 = startScheduledCallback(callbackServiceName_tc_1, String.valueOf(customerNumber), SCHEDULED_QUEUED_CB_TIME);
        String callbackId_1 = scheduledCallback_1.then().extract().path("_id");
        Response scheduledCallback_2 = startScheduledCallback(callbackServiceName_tc_2, String.valueOf(customerNumberCounter), 2 * DAY);//2*DAY for providing callback in another time range
        String callbackId_2 = scheduledCallback_2.then().extract().path("_id");
        customerNumberCounter++;

        //go to Callback Management UI page and set Date Filter
        callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");

        //set corresponding checkbox to filter table by states and services
        advancedOptionsPopUpPage = callbackPage.openAdvancedOptions();
        advancedOptionsPopUpPage.uncheckAllStatesAndServices();
        advancedOptionsPopUpPage.saveAdvancedOptions();

        //select multiple callbacks
        callbackPage.checkMultipleCallbackSelection();

        //cancel callbacks
        cancelCallbacksConfirmationPopUpPage = callbackPage.cancelCallbacks();
        cancelCallbacksConfirmationPopUpPage.checkCancelAllCallbacksInCurrentTimeRange();
        //TODO: implement automatic answer on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId_1, "ROUTING", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);
        Thread.sleep(ROUTING_3_DELAY * 1000);
        cancelCallbacksConfirmationPopUpPage.confirmAndCancelCallbacks();

        //check the first callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_reason"), equalTo("CANCELLED_BY_ADMIN"));
        //check the second callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_2, "_callback_state"), equalTo("SCHEDULED"));

        //download reports
        assertThat(callbackPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Download Reports" button is a green checkmark icon
        downloadReportsPopUpPage = callbackPage.downloadReports();
        assertThat(downloadReportsPopUpPage.isCancellationSummaryReportPresent(), is(true));//in Download Reports pop up is "Cancellation Summary Report" with "Download" button
        assertThat(downloadReportsPopUpPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Cancellation Summary Report" is a green checkmark icon
        downloadReportsPopUpPage.clickDownload();
        downloadReportsPopUpPage.clickClose();
        //verify report file
        Thread.sleep(TIME_FOR_FILE_DOWNLOADING * 1000);
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, successSubString), equalTo(1));//the file contains detailed results of the cancellation for each callback
    }

    @Test //semi-automated scenario for ROUTING_2 state
    public void purgeCallbackRecords_24() throws Exception {
        callbackServiceName_tc_1 = callbackImmServiceName + "_24_1";

        //create callback services
        env.createService(gmsClusterAppName, callbackServiceName_tc_1, getUserTermImmCallbackServiceOptions());

        //go to Callback Management UI page and set Date Filter
        callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");

        //set corresponding checkbox to filter table by states and services
        advancedOptionsPopUpPage = callbackPage.openAdvancedOptions();
        advancedOptionsPopUpPage.uncheckAllStatesAndServices();
        //initiate callbacks and save its ids to variables
        Response immediateCallback_1 = startImmediateCallback(callbackServiceName_tc_1, String.valueOf(customerNumber));
        String callbackId_1 = immediateCallback_1.then().extract().path("_id");
        advancedOptionsPopUpPage.saveAdvancedOptions();

        //select multiple callbacks
        callbackPage.checkMultipleCallbackSelection();

        //cancel callbacks
        cancelCallbacksConfirmationPopUpPage = callbackPage.cancelCallbacks();
        cancelCallbacksConfirmationPopUpPage.checkCancelAllCallbacksInCurrentTimeRange();
        //TODO: implement automatic answer on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId_1, "ROUTING", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);
        Thread.sleep(ROUTING_2_DELAY * 1000);
        cancelCallbacksConfirmationPopUpPage.confirmAndCancelCallbacks();

        //check the callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_reason"), equalTo("CANCELLED_BY_ADMIN"));

        //download reports
        assertThat(callbackPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Download Reports" button is a green checkmark icon
        downloadReportsPopUpPage = callbackPage.downloadReports();
        assertThat(downloadReportsPopUpPage.isCancellationSummaryReportPresent(), is(true));//in Download Reports pop up is "Cancellation Summary Report" with "Download" button
        assertThat(downloadReportsPopUpPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Cancellation Summary Report" is a green checkmark icon
        downloadReportsPopUpPage.clickDownload();
        downloadReportsPopUpPage.clickClose();
        //verify report file
        Thread.sleep(TIME_FOR_FILE_DOWNLOADING * 1000);
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, successSubString), equalTo(1));//the file contains detailed results of the cancellation for each callback
    }

    @Test //semi-automated scenario for PROCESSING state
    public void purgeCallbackRecords_25() throws Exception {
        callbackServiceName_tc_1 = callbackSchServiceName + "_25_1";
        callbackServiceName_tc_2 = callbackImmServiceName + "_25_2";

        //create callback services
        env.createService(gmsClusterAppName, callbackServiceName_tc_1, getUserTermSchCallbackServiceOptions());
        env.createService(gmsClusterAppName, callbackServiceName_tc_2, getUserTermImmCallbackServiceOptions());

        //initiate first callback and save its id to variable
        Response scheduledCallback_1 = startScheduledCallback(callbackServiceName_tc_1, String.valueOf(customerNumberCounter), 2 * DAY);//2*DAY for providing callback in another time range
        String callbackId_1 = scheduledCallback_1.then().extract().path("_id");
        customerNumberCounter++;

        //go to Callback Management UI page and set Date Filter
        callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");

        //set corresponding checkbox to filter table by states and services
        advancedOptionsPopUpPage = callbackPage.openAdvancedOptions();
        advancedOptionsPopUpPage.uncheckAllStatesAndServices();
        //initiate second callback and save its id to variable
        Response immediateCallback_2 = startImmediateCallback(callbackServiceName_tc_2, String.valueOf(customerNumber));
        String callbackId_2 = immediateCallback_2.then().extract().path("_id");
        advancedOptionsPopUpPage.saveAdvancedOptions();

        //select multiple callbacks
        callbackPage.checkMultipleCallbackSelection();

        //cancel callbacks
        cancelCallbacksConfirmationPopUpPage = callbackPage.cancelCallbacks();
        cancelCallbacksConfirmationPopUpPage.checkCancelAllCallbacksInCurrentTimeRange();
        //TODO: implement automatic answer on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId_2, "PROCESSING", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);
        cancelCallbacksConfirmationPopUpPage.confirmAndCancelCallbacks();

        //check the first callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_state"), equalTo("SCHEDULED"));
        //check the second callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_2, "_callback_state"), equalTo("PROCESSING"));

        //download reports
        assertThat(callbackPage.isRedXIconPresent(), is(true));//beside the "Download Reports" button is a red X icon
        downloadReportsPopUpPage = callbackPage.downloadReports();
        assertThat(downloadReportsPopUpPage.isCancellationSummaryReportPresent(), is(true));//in Download Reports pop up is "Cancellation Summary Report" with "Download" button
        assertThat(downloadReportsPopUpPage.isRedXIconPresent(), is(true));//beside the "Cancellation Summary Report" is a red X icon
        downloadReportsPopUpPage.clickDownload();
        downloadReportsPopUpPage.clickClose();
        //verify report file
        Thread.sleep(TIME_FOR_FILE_DOWNLOADING * 1000);
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, failureSubString), equalTo(1));//the file contains detailed results of the cancellation for each callback

        //TODO: implement automatic hang up on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId_2, "COMPLETED", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);
    }

    @Test //semi-automated scenario for ROUTING_2 state
    public void purgeCallbackRecords_26() throws Exception {
        callbackServiceName_tc_1 = callbackSchServiceName + "_26_1";
        callbackServiceName_tc_2 = callbackSchServiceName + "_26_2";

        //create callback services
        env.createService(gmsClusterAppName, callbackServiceName_tc_1, getUserTermSchCallbackServiceOptions());
        env.createService(gmsClusterAppName, callbackServiceName_tc_2, getUserTermSchCallbackServiceOptions());

        //initiate callbacks and save its ids to variables
        Response scheduledCallback_1 = startScheduledCallback(callbackServiceName_tc_1, String.valueOf(customerNumber), SCHEDULED_QUEUED_CB_TIME);
        String callbackId_1 = scheduledCallback_1.then().extract().path("_id");
        Response scheduledCallback_2 = startScheduledCallback(callbackServiceName_tc_2, String.valueOf(customerNumberCounter), 2 * DAY);//2*DAY for providing callback in another time range
        String callbackId_2 = scheduledCallback_2.then().extract().path("_id");
        customerNumberCounter++;

        //go to Callback Management UI page and set Date Filter
        callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");

        //set corresponding checkbox to filter table by states and services
        advancedOptionsPopUpPage = callbackPage.openAdvancedOptions();
        advancedOptionsPopUpPage.uncheckAllStatesAndServices();
        advancedOptionsPopUpPage.saveAdvancedOptions();

        //select multiple callbacks
        callbackPage.checkMultipleCallbackSelection();

        //cancel callbacks
        cancelCallbacksConfirmationPopUpPage = callbackPage.cancelCallbacks();
        cancelCallbacksConfirmationPopUpPage.checkCancelAllCallbacksInCurrentTimeRange();
        //TODO: implement automatic answer on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId_1, "ROUTING", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);
        Thread.sleep(ROUTING_2_DELAY * 1000);
        cancelCallbacksConfirmationPopUpPage.confirmAndCancelCallbacks();

        //check the first callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_reason"), equalTo("CANCELLED_BY_ADMIN"));
        //check the second callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_2, "_callback_state"), equalTo("SCHEDULED"));

        //download reports
        assertThat(callbackPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Download Reports" button is a green checkmark icon
        downloadReportsPopUpPage = callbackPage.downloadReports();
        assertThat(downloadReportsPopUpPage.isCancellationSummaryReportPresent(), is(true));//in Download Reports pop up is "Cancellation Summary Report" with "Download" button
        assertThat(downloadReportsPopUpPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Cancellation Summary Report" is a green checkmark icon
        downloadReportsPopUpPage.clickDownload();
        downloadReportsPopUpPage.clickClose();
        //verify report file
        Thread.sleep(TIME_FOR_FILE_DOWNLOADING * 1000);
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, successSubString), equalTo(1));//the file contains detailed results of the cancellation for each callback
    }

    @Test //semi-automated scenario for ROUTING_1 state
    public void purgeCallbackRecords_27() throws Exception {
        callbackServiceName_tc_1 = callbackImmServiceName + "_27_1";
        callbackServiceName_tc_2 = callbackImmServiceName + "_27_2";

        //create callback services
        env.createService(gmsClusterAppName, callbackServiceName_tc_1, getUserTermImmCallbackServiceOptions());
        env.createService(gmsClusterAppName, callbackServiceName_tc_2, getUserTermImmCallbackServiceOptions());

        //initiate first callback and save its id to variables
        Response immediateCallback_1 = startImmediateCallback(callbackServiceName_tc_1, String.valueOf(customerNumberCounter));
        String callbackId_1 = immediateCallback_1.then().extract().path("_id");
        customerNumberCounter++;

        //go to Callback Management UI page and set Date Filter
        callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");

        //set corresponding checkbox to filter table by states and services
        advancedOptionsPopUpPage = callbackPage.openAdvancedOptions();
        advancedOptionsPopUpPage.uncheckAllStatesAndServices();
        //initiate second callback and save its id to variable
        Response immediateCallback_2 = startImmediateCallback(callbackServiceName_tc_2, String.valueOf(customerNumber));
        String callbackId_2 = immediateCallback_2.then().extract().path("_id");
        advancedOptionsPopUpPage.saveAdvancedOptions();

        //select multiple callbacks
        callbackPage.checkMultipleCallbackSelection();

        //cancel callbacks
        cancelCallbacksConfirmationPopUpPage = callbackPage.cancelCallbacks();
        cancelCallbacksConfirmationPopUpPage.checkCancelAllCallbacksInCurrentTimeRange();
        //TODO: implement automatic answer on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId_2, "ROUTING", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);
        cancelCallbacksConfirmationPopUpPage.confirmAndCancelCallbacks();

        //check the first callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_reason"), equalTo("CANCELLED_BY_ADMIN"));
        //check the second callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_2, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_2, "_callback_reason"), equalTo("CANCELLED_BY_ADMIN"));

        //download reports
        assertThat(callbackPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Download Reports" button is a green checkmark icon
        downloadReportsPopUpPage = callbackPage.downloadReports();
        assertThat(downloadReportsPopUpPage.isCancellationSummaryReportPresent(), is(true));//in Download Reports pop up is "Cancellation Summary Report" with "Download" button
        assertThat(downloadReportsPopUpPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Cancellation Summary Report" is a green checkmark icon
        downloadReportsPopUpPage.clickDownload();
        downloadReportsPopUpPage.clickClose();
        //verify report file
        Thread.sleep(TIME_FOR_FILE_DOWNLOADING * 1000);
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, successSubString), equalTo(2));//the file contains detailed results of the cancellation for each callback
    }

    @Test //semi-automated scenario for PROCESSING state
    public void purgeCallbackRecords_28() throws Exception {
        callbackServiceName_tc_1 = callbackImmServiceName + "_28_1";

        //create callback services
        env.createService(gmsClusterAppName, callbackServiceName_tc_1, getUserTermImmCallbackServiceOptions());

        //go to Callback Management UI page and set Date Filter
        callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");

        //set corresponding checkbox to filter table by states and services
        advancedOptionsPopUpPage = callbackPage.openAdvancedOptions();
        advancedOptionsPopUpPage.uncheckAllStatesAndServices();
        //initiate callbacks and save its ids to variables
        Response immediateCallback_1 = startImmediateCallback(callbackServiceName_tc_1, String.valueOf(customerNumber));
        String callbackId_1 = immediateCallback_1.then().extract().path("_id");
        advancedOptionsPopUpPage.saveAdvancedOptions();

        //select multiple callbacks
        callbackPage.checkMultipleCallbackSelection();

        //cancel callbacks
        cancelCallbacksConfirmationPopUpPage = callbackPage.cancelCallbacks();
        cancelCallbacksConfirmationPopUpPage.checkCancelAllCallbacksInCurrentTimeRange();
        //TODO: implement automatic answer on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId_1, "PROCESSING", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);
        cancelCallbacksConfirmationPopUpPage.confirmAndCancelCallbacks();

        //check the first callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_state"), equalTo("PROCESSING"));

        //download reports
        assertThat(callbackPage.isRedXIconPresent(), is(true));//beside the "Download Reports" button is a red X icon
        downloadReportsPopUpPage = callbackPage.downloadReports();
        assertThat(downloadReportsPopUpPage.isCancellationSummaryReportPresent(), is(true));//in Download Reports pop up is "Cancellation Summary Report" with "Download" button
        assertThat(downloadReportsPopUpPage.isRedXIconPresent(), is(true));//beside the "Cancellation Summary Report" is a red X icon
        downloadReportsPopUpPage.clickDownload();
        downloadReportsPopUpPage.clickClose();
        //verify report file
        Thread.sleep(TIME_FOR_FILE_DOWNLOADING * 1000);
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, failureSubString), equalTo(1));//the file contains detailed results of the cancellation for each callback

        //TODO: implement automatic hang up on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId_1, "COMPLETED", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);
    }

    @Test //semi-automated scenario for ROUTING_3 state
    public void purgeCallbackRecords_29() throws Exception {
        callbackServiceName_tc_1 = callbackImmServiceName + "_29_1";
        callbackServiceName_tc_2 = callbackSchServiceName + "_29_2";

        //create callback services
        env.createService(gmsClusterAppName, callbackServiceName_tc_1, getUserTermImmCallbackServiceOptions());
        env.createService(gmsClusterAppName, callbackServiceName_tc_2, getUserTermSchCallbackServiceOptions());

        //initiate callbacks and save its ids to variables
        Response immediateCallback_1 = startImmediateCallback(callbackServiceName_tc_1, String.valueOf(customerNumberCounter));
        String callbackId_1 = immediateCallback_1.then().extract().path("_id");
        customerNumberCounter++;
        Response scheduledCallback_2 = startScheduledCallback(callbackServiceName_tc_2, String.valueOf(customerNumber), SCHEDULED_QUEUED_CB_TIME);
        String callbackId_2 = scheduledCallback_2.then().extract().path("_id");

        //go to Callback Management UI page and set Date Filter
        callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");

        //set corresponding checkbox to filter table by states and services
        advancedOptionsPopUpPage = callbackPage.openAdvancedOptions();
        advancedOptionsPopUpPage.uncheckAllStatesAndServices();
        advancedOptionsPopUpPage.saveAdvancedOptions();

        //select multiple callbacks
        callbackPage.checkMultipleCallbackSelection();

        //cancel callbacks
        cancelCallbacksConfirmationPopUpPage = callbackPage.cancelCallbacks();
        cancelCallbacksConfirmationPopUpPage.checkCancelAllCallbacksInCurrentTimeRange();
        //TODO: implement automatic answer on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId_2, "ROUTING", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);
        Thread.sleep(ROUTING_2_DELAY * 1000);
        cancelCallbacksConfirmationPopUpPage.confirmAndCancelCallbacks();

        //check the first callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_1, "_callback_reason"), equalTo("CANCELLED_BY_ADMIN"));
        //check the second callback state and reason
        assertThat(getCallbackDetailByKey(callbackId_2, "_callback_state"), equalTo("COMPLETED"));
        assertThat(getCallbackDetailByKey(callbackId_2, "_callback_reason"), equalTo("CANCELLED_BY_ADMIN"));

        //download reports
        assertThat(callbackPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Download Reports" button is a green checkmark icon
        downloadReportsPopUpPage = callbackPage.downloadReports();
        assertThat(downloadReportsPopUpPage.isCancellationSummaryReportPresent(), is(true));//in Download Reports pop up is "Cancellation Summary Report" with "Download" button
        assertThat(downloadReportsPopUpPage.isGreenCheckmarkIconPresent(), is(true));//beside the "Cancellation Summary Report" is a green checkmark icon
        downloadReportsPopUpPage.clickDownload();
        downloadReportsPopUpPage.clickClose();
        //verify report file
        Thread.sleep(TIME_FOR_FILE_DOWNLOADING * 1000);
        assertThat(countSubStringsInFile(downloadDirectory, reportFileName, successSubString), equalTo(2));//the file contains detailed results of the cancellation for each callback
    }
}
