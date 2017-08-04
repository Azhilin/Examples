package gms.ui;

import com.genesyslab.ats.ex.AtsCfgComponentException;
import com.genesyslab.functional.tests.gms.helper.CallbackServicesAPI;
import com.genesyslab.functional.tests.gms.psdk.Reconfiguration;
import com.genesyslab.functional.tests.gms.ui.callback.GMSCallbackPage;
import io.restassured.response.Response;
import org.junit.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static com.genesyslab.functional.tests.gms.helper.CallbackServiceHelper.*;
import static com.genesyslab.functional.tests.gms.helper.CallbackServicesAPI.startImmediateCallback;
import static com.genesyslab.functional.tests.gms.helper.CallbackServicesAPI.startScheduledCallback;
import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertyConfiguration;
import static com.genesyslab.functional.tests.gms.helper.ServiceOptions.getUserTermImmCallbackServiceOptions;
import static com.genesyslab.functional.tests.gms.helper.ServiceOptions.getUserTermSchCallbackServiceOptions;
import static com.genesyslab.functional.tests.gms.helper.TimeOperations.*;
import static com.genesyslab.functional.tests.gms.helper.URLsHelper.getGMSBaseURL;
import static com.genesyslab.functional.tests.gms.helper.URLsHelper.getGMSFullLoginURL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

/**
 * Created by bvolovyk on 26.05.2017.
 */

/*
 * [Test Suite] Callback Management UI. Search Filters: https://jira.genesys.com/browse/GMS-4083
 */
public class CallbackSearchFiltersTests {
    //    private static String propertiesFile = getPropertiesFile();
    private static String propertiesFile = "./config_2nd.properties";//uncomment this line in case you want to use another .properties file
    private static Reconfiguration env = new Reconfiguration(propertiesFile);
    private static String gmsClusterAppName = getPropertyConfiguration(propertiesFile, "gms.cluster.app.name");
    private static String gmsBaseUrl = getGMSBaseURL(propertiesFile);
    private static String gmsUrl = getGMSFullLoginURL(propertiesFile);
    private static String username = getPropertyConfiguration(propertiesFile, "gms.user.name");
    private static String password = getPropertyConfiguration(propertiesFile, "gms.user.password");
    private static String customerNumber = "5115";
    private static String callbackImmServiceName = "cb_term_im_gms-4083";
    private static String callbackSchServiceName = "cb_term_sch_gms-4083";

    private static final int DAY = 86400;
    private static final int CURRENT_MOMENT = 0;
    private static final int SCHEDULED_CB_TIME_NOT_FAR = 10;
    private static final int SCHEDULED_CB_TIME_FAR = 3600;
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String LAST_DAYS_SUFFIX = "T21:00:00.000Z";
    private static final String NEXT_DAYS_SUFFIX = "T20:59:59.999Z";
    private static final long POLLING_TIME_MILLIS = 1000;
    private static final long TIMEOUT_FOR_SEMI_AUTOMATIC_PART = 140;

    private WebDriver driver = null;
    private GMSCallbackPage callbackPage = null;

    @BeforeClass
    public static void oneTimeSetUp() throws AtsCfgComponentException {
        long startTime = System.nanoTime();
        System.out.println("@BeforeClass method processing...");
        env.createService(gmsClusterAppName, callbackImmServiceName, getUserTermImmCallbackServiceOptions());
        env.createService(gmsClusterAppName, callbackSchServiceName, getUserTermSchCallbackServiceOptions());
        CallbackServicesAPI.setGMSBaseURL(getGMSBaseURL(propertiesFile));
        long endTime = System.nanoTime();
        getMethodExecutionTime(startTime, endTime, "@BeforeClass");
    }

    @Before
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(gmsUrl);

        GMSLoginPage loginPage = new GMSLoginPage(driver);
        GMSMainPage mainPage = loginPage.logIn(username, password);
        callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
    }

    @After
    public void tearDown() {
        cancelUnfinishedCallbacks(callbackImmServiceName);
        cancelUnfinishedCallbacks(callbackSchServiceName);
        if (driver != null)
            driver.quit();
    }

    @AfterClass
    public static void oneTimeTearDown() throws Exception {
        System.out.print("@AfterClass method processing...: ");
        env.deleteService(gmsClusterAppName, callbackImmServiceName); //for troubleshooting purposes comment this command
        env.deleteService(gmsClusterAppName, callbackSchServiceName); //for troubleshooting purposes comment this command
        env.deactivate();
    }

    @Test //lunch this test only on the fresh environment (with fresh Cassandra)
    public void test_00() {
        Response scheduleCallback = startScheduledCallback(callbackSchServiceName, customerNumber, SCHEDULED_CB_TIME_FAR);
        scheduleCallback.then().assertThat().statusCode(200);
        callbackPage.setDateFilter("Last 24 Hours");
        assertThat(callbackPage.isNoCallbacksFoundMsgDisplayed(), is(true));
        callbackPage.setDateFilter("Last 7 Days");
        assertThat(callbackPage.isNoCallbacksFoundMsgDisplayed(), is(true));
        callbackPage.setDateFilter("Last 30 Days");
        assertThat(callbackPage.isNoCallbacksFoundMsgDisplayed(), is(true));
    }

    @Test
    public void test_01() {
        Response scheduleCallback = startScheduledCallback(callbackSchServiceName, customerNumber, SCHEDULED_CB_TIME_FAR);
        scheduleCallback.then().assertThat().statusCode(200);
        verifyLast24HoursFilter();
        verifyLast7DaysFilter();
        verifyLast30DaysFilter();
    }

    @Test
    public void test_02() {
        Response scheduleCallback = startScheduledCallback(callbackSchServiceName, customerNumber, SCHEDULED_CB_TIME_FAR);
        scheduleCallback.then().assertThat().statusCode(200);
        verifyNext24HoursFilter();
        verifyNext7DaysFilter();
        verifyNext30DaysFilter();
    }

    @Test
    public void test_03() {
        Response scheduleCallback = startScheduledCallback(callbackSchServiceName, customerNumber, SCHEDULED_CB_TIME_FAR);
        scheduleCallback.then().assertThat().statusCode(200);
        verifyTodayFilter();
    }

    @Test
    public void test_04() throws InterruptedException {
        Response scheduleCallback = startScheduledCallback(callbackSchServiceName, customerNumber, SCHEDULED_CB_TIME_NOT_FAR);
        scheduleCallback.then().assertThat().statusCode(200);
        System.out.printf("Waiting %s seconds until time coming...%n", SCHEDULED_CB_TIME_NOT_FAR);
        Thread.sleep(SCHEDULED_CB_TIME_NOT_FAR * 1000);
        cancelUnfinishedCallbacks(callbackSchServiceName);
        verifyLast24HoursFilter();
        verifyLast7DaysFilter();
        verifyLast30DaysFilter();
    }

    @Test
    public void test_05() throws InterruptedException {
        Response scheduleCallback = startScheduledCallback(callbackSchServiceName, customerNumber, SCHEDULED_CB_TIME_NOT_FAR);
        scheduleCallback.then().assertThat().statusCode(200);
        System.out.printf("Waiting %s seconds until time coming...%n", SCHEDULED_CB_TIME_NOT_FAR);
        Thread.sleep(SCHEDULED_CB_TIME_NOT_FAR * 1000);
        cancelUnfinishedCallbacks(callbackSchServiceName);
        verifyNext24HoursFilter();
        verifyNext7DaysFilter();
        verifyNext30DaysFilter();
    }

    @Test
    public void test_06() throws InterruptedException {
        Response scheduleCallback = startScheduledCallback(callbackSchServiceName, customerNumber, SCHEDULED_CB_TIME_NOT_FAR);
        scheduleCallback.then().assertThat().statusCode(200);
        System.out.printf("Waiting %s seconds until time coming...%n", SCHEDULED_CB_TIME_NOT_FAR);
        Thread.sleep(SCHEDULED_CB_TIME_NOT_FAR * 1000);
        cancelUnfinishedCallbacks(callbackSchServiceName);
        verifyTodayFilter();
    }

    @Test //semi-automated scenario for PROCESSING state
    public void test_07() throws InterruptedException, AtsCfgComponentException {
        Response immediateCallback = startImmediateCallback(callbackImmServiceName, customerNumber);
        immediateCallback.then().assertThat().statusCode(200);
        String callbackId = immediateCallback.then().extract().path("_id");

        //TODO: implement automatic answer on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId, "PROCESSING", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);

        verifyLast24HoursFilter();
        verifyLast7DaysFilter();
        verifyLast30DaysFilter();

        //TODO: implement automatic hang up on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId, "COMPLETED", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);
    }

    @Test //semi-automated scenario for PROCESSING state
    public void test_08() throws InterruptedException, AtsCfgComponentException {
        Response immediateCallback = startImmediateCallback(callbackImmServiceName, customerNumber);
        immediateCallback.then().assertThat().statusCode(200);
        String callbackId = immediateCallback.then().extract().path("_id");

        //TODO: implement automatic answer on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId, "PROCESSING", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);

        verifyNext24HoursFilter();
        verifyNext7DaysFilter();
        verifyNext30DaysFilter();

        //TODO: implement automatic hang up on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId, "COMPLETED", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);
    }

    @Test //semi-automated scenario for PROCESSING state
    public void test_09() throws InterruptedException, AtsCfgComponentException {
        Response immediateCallback = startImmediateCallback(callbackImmServiceName, customerNumber);
        immediateCallback.then().assertThat().statusCode(200);
        String callbackId = immediateCallback.then().extract().path("_id");

        //TODO: implement automatic answer on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId, "PROCESSING", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);

        verifyTodayFilter();

        //TODO: implement automatic hang up on both customer and agent sides
        semiAutomaticCallbackProcessing(callbackId, "COMPLETED", TIMEOUT_FOR_SEMI_AUTOMATIC_PART, POLLING_TIME_MILLIS);
    }

    @Test
    public void test_10() {
        callbackPage.setDateFilter("Last 30 Days");
        callbackPage.setDateFilter("Custom Date Range");
        callbackPage.submitCustomDateRange();
        assertThat(callbackPage.isCustomDateRangeErrMsgPresent(), is(false));
    }

    @Test
    public void test_11() {
        callbackPage.setDateFilter("Next 30 Days");
        callbackPage.setDateFilter("Custom Date Range");
        callbackPage.submitCustomDateRange();
        assertThat(callbackPage.isCustomDateRangeErrMsgPresent(), is(false));
    }

    @Test
    public void test_12() {
        callbackPage.setDateFilter("Last 24 Hours");
        callbackPage.setDateFilter("Custom Date Range");
        setCustomDateRangeStartAndEndDayForToday();
        callbackPage.submitCustomDateRange();
        assertThat(callbackPage.isCustomDateRangeErrMsgPresent(), is(false));
    }

    @Test
    public void test_13() {
        callbackPage.setDateFilter("Last 7 Days");
        callbackPage.setDateFilter("Custom Date Range");
        setCustomDateRangeStartAndEndDayForToday();
        callbackPage.submitCustomDateRange();
        assertThat(callbackPage.isCustomDateRangeErrMsgPresent(), is(false));
    }

    @Test
    public void test_14() {
        callbackPage.setDateFilter("Last 30 Days");
        callbackPage.setDateFilter("Custom Date Range");
        setCustomDateRangeStartAndEndDayForToday();
        callbackPage.submitCustomDateRange();
        assertThat(callbackPage.isCustomDateRangeErrMsgPresent(), is(false));
    }

    @Test
    public void test_15() {
        callbackPage.setDateFilter("Today");
        callbackPage.setDateFilter("Custom Date Range");
        setCustomDateRangeStartAndEndDayForToday();
        callbackPage.submitCustomDateRange();
        assertThat(callbackPage.isCustomDateRangeErrMsgPresent(), is(false));
    }

    @Test
    public void test_16() {
        callbackPage.setDateFilter("Next 24 Hours");
        callbackPage.setDateFilter("Custom Date Range");
        setCustomDateRangeStartAndEndDayForToday();
        callbackPage.submitCustomDateRange();
        assertThat(callbackPage.isCustomDateRangeErrMsgPresent(), is(false));
    }

    @Test
    public void test_17() {
        callbackPage.setDateFilter("Next 7 Days");
        callbackPage.setDateFilter("Custom Date Range");
        setCustomDateRangeStartAndEndDayForToday();
        callbackPage.submitCustomDateRange();
        assertThat(callbackPage.isCustomDateRangeErrMsgPresent(), is(false));
    }

    @Test
    public void test_18() {
        callbackPage.setDateFilter("Next 30 Days");
        callbackPage.setDateFilter("Custom Date Range");
        setCustomDateRangeStartAndEndDayForToday();
        callbackPage.submitCustomDateRange();
        assertThat(callbackPage.isCustomDateRangeErrMsgPresent(), is(false));
    }

    @Test
    public void test_19() {
        callbackPage.setDateFilter("Custom Date Range");
//        callbackPage.submitCustomDateRange();

        String startDay = getDateDaysAhead(getLocalTimeZoneOffset(), CURRENT_MOMENT, "d");
        String startMonth = getDateDaysAhead(getLocalTimeZoneOffset(), CURRENT_MOMENT, "MMMMM");
        String startYear = getDateDaysAhead(getLocalTimeZoneOffset(), CURRENT_MOMENT, "yyyy");

        String endDay = getDateDaysBack(getLocalTimeZoneOffset(), 1, "d");
        String endMonth = getDateDaysBack(getLocalTimeZoneOffset(), 1, "MMMMM");
        String endYear = getDateDaysBack(getLocalTimeZoneOffset(), 1, "yyyy");

        callbackPage.setCustomDateRangeStartDate(startDay, startMonth, startYear);
        callbackPage.setCustomDateRangeEndDate(endDay, endMonth, endYear);
        callbackPage.submitCustomDateRange();

        assertThat(callbackPage.isCustomDateRangeErrMsgPresent(), is(true));
        assertThat(callbackPage.getCustomDateRangeErrMsg(),
                containsString("Start date is not before the end date!"));

        callbackPage.setDateFilter("Today");
        callbackPage.setDateFilter("Custom Date Range");
        callbackPage.submitCustomDateRange();

        String currentDate = getDateDaysAhead(getLocalTimeZoneOffset(), CURRENT_MOMENT, "d MMMMM yyyy");

        assertThat(callbackPage.getCustomDateRangeStartDate(), containsString(currentDate));
        assertThat(callbackPage.getCustomDateRangeEndDate(), containsString(currentDate));
        assertThat(callbackPage.isCustomDateRangeErrMsgPresent(), is(false));
    }

    @Test
    public void test_20() {
        callbackPage.setDateFilter("Custom Date Range");
//        callbackPage.submitCustomDateRange();

        String startDay = getDateDaysAhead(getLocalTimeZoneOffset(), CURRENT_MOMENT, "d");
        String startMonth = getDateDaysAhead(getLocalTimeZoneOffset(), CURRENT_MOMENT, "MMMMM");
        String startYear = getDateDaysAhead(getLocalTimeZoneOffset(), CURRENT_MOMENT, "yyyy");

        String endDay = getDateDaysAhead(getLocalTimeZoneOffset(), 32, "d");
        String endMonth = getDateDaysAhead(getLocalTimeZoneOffset(), 32, "MMMMM");
        String endYear = getDateDaysAhead(getLocalTimeZoneOffset(), 32, "yyyy");

        callbackPage.setCustomDateRangeStartDate(startDay, startMonth, startYear);
        callbackPage.setCustomDateRangeEndDate(endDay, endMonth, endYear);
        callbackPage.submitCustomDateRange();

        assertThat(callbackPage.isCustomDateRangeErrMsgPresent(), is(true));
        assertThat(callbackPage.getCustomDateRangeErrMsg(),
                containsString("Maximum range is one month!"));

        callbackPage.setDateFilter("Today");
        callbackPage.setDateFilter("Custom Date Range");
        callbackPage.submitCustomDateRange();

        String currentDate = getDateDaysAhead(getLocalTimeZoneOffset(), CURRENT_MOMENT, "d MMMMM yyyy");

        assertThat(callbackPage.getCustomDateRangeStartDate(), containsString(currentDate));
        assertThat(callbackPage.getCustomDateRangeEndDate(), containsString(currentDate));
        assertThat(callbackPage.isCustomDateRangeErrMsgPresent(), is(false));
    }

    private void verifyLast24HoursFilter() {
        callbackPage.setDateFilter("Last 24 Hours");
        String startTime = getUTCTimeInPast(DAY);
        String endTime = getUTCTimeInPast(CURRENT_MOMENT);
        assertThat(callbackPage.countCallbackEntriesByUI(), equalTo(countCallbacksByAPI(startTime, endTime)));
    }

    private void verifyLast7DaysFilter() {
        callbackPage.setDateFilter("Last 7 Days");
        String startTime = getDateDaysBack(0, 8, DATE_PATTERN) + LAST_DAYS_SUFFIX;
        String endTime = getUTCTimeInPast(CURRENT_MOMENT);
        assertThat(callbackPage.countCallbackEntriesByUI(), equalTo(countCallbacksByAPI(startTime, endTime)));
    }

    private void verifyLast30DaysFilter() {
        callbackPage.setDateFilter("Last 30 Days");
        String startTime = getDateDaysBack(0, 31, DATE_PATTERN) + LAST_DAYS_SUFFIX;
        String endTime = getUTCTimeInPast(CURRENT_MOMENT);
        assertThat(callbackPage.countCallbackEntriesByUI(), equalTo(countCallbacksByAPI(startTime, endTime)));
    }

    private void verifyTodayFilter() {
        callbackPage.setDateFilter("Today");
        String startTime = getDateDaysBack(0, 1, DATE_PATTERN) + LAST_DAYS_SUFFIX;
        String endTime = getDateDaysAhead(0, CURRENT_MOMENT, DATE_PATTERN) + NEXT_DAYS_SUFFIX;
        assertThat(callbackPage.countCallbackEntriesByUI(), equalTo(countCallbacksByAPI(startTime, endTime)));
    }

    private void verifyNext24HoursFilter() {
        callbackPage.setDateFilter("Next 24 Hours");
        String startTime = getUTCTimeInFuture(CURRENT_MOMENT);
        String endTime = getUTCTimeInFuture(DAY);
        assertThat(callbackPage.countCallbackEntriesByUI(), equalTo(countCallbacksByAPI(startTime, endTime)));
    }

    private void verifyNext7DaysFilter() {
        callbackPage.setDateFilter("Next 7 Days");
        String startTime = getUTCTimeInFuture(CURRENT_MOMENT);
        String endTime = getDateDaysAhead(0, 7, DATE_PATTERN) + NEXT_DAYS_SUFFIX;
        assertThat(callbackPage.countCallbackEntriesByUI(), equalTo(countCallbacksByAPI(startTime, endTime)));
    }

    private void verifyNext30DaysFilter() {
        callbackPage.setDateFilter("Next 30 Days");
        String startTime = getUTCTimeInFuture(CURRENT_MOMENT);
        String endTime = getDateDaysAhead(0, 30, DATE_PATTERN) + NEXT_DAYS_SUFFIX;
        assertThat(callbackPage.countCallbackEntriesByUI(), equalTo(countCallbacksByAPI(startTime, endTime)));
    }

    private void setCustomDateRangeStartAndEndDayForToday() {
        String day = getDateDaysAhead(getLocalTimeZoneOffset(), CURRENT_MOMENT, "d");
        String month = getDateDaysAhead(getLocalTimeZoneOffset(), CURRENT_MOMENT, "MMMMM");
        String year = getDateDaysAhead(getLocalTimeZoneOffset(), CURRENT_MOMENT, "yyyy");
        callbackPage.setCustomDateRangeStartDate(day, month, year);
        callbackPage.setCustomDateRangeEndDate(day, month, year);
    }
}