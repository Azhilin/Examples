package gms.api;

import com.genesyslab.ats.cfg.actor.CfgManager;
import com.genesyslab.ats.ex.AtsCfgComponentException;
import com.genesyslab.functional.tests.gms.files.WorkWithResources;
import com.genesyslab.platform.applicationblocks.com.ConfigException;
import com.genesyslab.platform.applicationblocks.com.objects.CfgApplication;
import com.genesyslab.platform.commons.collections.KeyValueCollection;
import com.genesyslab.platform.commons.collections.KeyValuePair;
import com.genesyslab.platform.commons.protocol.ProtocolException;
import com.genesyslab.scsmanager.APP_STATUS;
import com.genesyslab.scsmanager.SCSManager;
import io.restassured.response.Response;
import org.junit.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;

import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertyConfiguration;
import static com.genesyslab.functional.tests.gms.helper.TimeOperations.getMethodExecutionTime;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

/**
 * Created by bvolovyk on 03.10.2016.
 */

/*
 * [Test Suite] ORS Hardening: https://jira.genesys.com/browse/GMS-2521
 */
public class ORSHardeningTests {
    private static Properties properties = new Properties();
    private static Properties properties_2nd = new Properties();
    private static CfgManager cfgManager = new CfgManager();
    private static CfgManager cfgManager_2nd = new CfgManager();
    private static SCSManager scsManager = new SCSManager(cfgManager);
    private static SCSManager scsManager_2nd = new SCSManager(cfgManager_2nd);
    private static CfgApplication cfgGMSApplication;
    private KeyValuePair helloService;
    private KeyValuePair orsSection;
    private static KeyValueCollection helloServiceSectionData;
    private static KeyValueCollection orsSectionData;
    private static String gmsAppName = getPropertyConfiguration("gms.app.name");
    private static String orsAppName = "Orchestration_Server";
    private static String gmsBaseURL = "http://10.10.26.150:8080";
    private String ors1stURL = "http://10.10.26.150:7210";
    private String ors2ndURL = "http://135.17.36.8:7210";
    private String ors3rdURL = "http://135.17.36.9:7210";
    private String requestUrl = gmsBaseURL + "/genesys/1/service/hello";
    private String requestBody = "";
    private String gmsLogsDir = "\\\\10.10.26.150\\GenesysMobileServices";
    private String ext = "log";
    private String successfulRequestToHost = "<<<Create service result: {msg=hello}";
    private String failedRequestTo1stHost = "ORS request failed: " + ors1stURL +
            "/scxml/session/start java.net.ConnectException: Connection refused: connect";
    private String failedRequestTo2ndHost = "ORS request failed: " + ors2ndURL +
            "/scxml/session/start java.net.ConnectException: Connection refused: connect";
    private String failedRequestTo3rdHost = "ORS request failed: " + ors3rdURL +
            "/scxml/session/start java.net.ConnectException: Connection refused: connect";

    int maxDesyncInSec = 3;
//    private String successfulRequestTo1stHost = "(GET) Client IP Address: 135.17.36.157, URI:http://135.17.36.157:8080/genesys/1/document/service_template/hello/hello.scxml, Params: <empty>";
//    private String successfulRequestTo2ndHost = "(GET) Client IP Address: 135.17.36.8, URI:http://135.17.36.157:8080/genesys/1/document/service_template/hello/hello.scxml, Params: <empty>";

    @BeforeClass
    public static void oneTimeSetUp() {
        long startTime = System.nanoTime();
        try {
            properties.load(new FileInputStream("./config.properties"));
            properties_2nd.load(new FileInputStream("./config_2nd.properties"));
            scsManager.init(properties);
            scsManager_2nd.init(properties_2nd);
            cfgGMSApplication = cfgManager.getAppApi().getApp(gmsAppName);
            scsManager.restartApplication(gmsAppName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long endTime = System.nanoTime();
        getMethodExecutionTime(startTime, endTime, "@BeforeClass");
    }

    @AfterClass
    public static void oneTimeTearDown() {
        scsManager.deactivate();
        scsManager_2nd.deactivate();
    }

    @Before
    public void setUp() {
        helloServiceSectionData = new KeyValueCollection();
        orsSectionData = new KeyValueCollection();
    }

    @After
    public void tearDown() throws ConfigException {
        cfgGMSApplication.getOptions().remove(helloService);
        cfgGMSApplication.save();
        helloServiceSectionData = null;
        orsSectionData = null;
    }

    @Test
    public void test1_01() throws AtsCfgComponentException, ProtocolException, ConfigException {
        scsManager.restartApplication(orsAppName);

        addHelloService(cfgGMSApplication, ors1stURL, "linear");
        setMaxORSRequestAttempts(cfgGMSApplication, "3");

        WorkWithResources firstRequestStartTime = new WorkWithResources();
        firstRequestStartTime.setCurrentTimeNotStat();

        Response firstResponse = postService(requestUrl, requestBody);
        assertThat(firstResponse.getStatusCode(), equalTo(200));
        assertThat(firstResponse.getBody().asString(), equalTo("{\"msg\":\"hello\"}"));

        String GMSLogFile = getNewestFileFromDirAsString(gmsLogsDir, ext);

        System.out.println("mark_before_firstRequestStartTime = " + GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat()));
        System.out.println("time_before_firstRequestStartTime = " + firstRequestStartTime.getTimeNotStat());

        searchReqTimeMarkInLogs(GMSLogFile, firstRequestStartTime, maxDesyncInSec);
//        while (-1 == GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat())) {
//            firstRequestStartTime.addSecondsToCurrentTime(1);
//        }

        System.out.println("mark_after_firstRequestStartTime = " + GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat()));
        System.out.println("time_after_firstRequestStartTime = " + firstRequestStartTime.getTimeNotStat());

        assertThat(GMSLogFile.indexOf(successfulRequestToHost, GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat())),
                greaterThan(GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat())));
    }

    @Test
    public void test1_02() throws AtsCfgComponentException, ProtocolException, ConfigException {
        scsManager.restartApplication(orsAppName);
        scsManager_2nd.restartApplication(orsAppName);

        addHelloService(cfgGMSApplication, ors1stURL + "," + ors2ndURL, "circular");
        setMaxORSRequestAttempts(cfgGMSApplication, "3");

        WorkWithResources firstRequestStartTime = new WorkWithResources();
        firstRequestStartTime.setCurrentTimeNotStat();

        Response firstResponse = postService(requestUrl, requestBody);
        assertThat(firstResponse.getStatusCode(), equalTo(200));
        assertThat(firstResponse.getBody().asString(), equalTo("{\"msg\":\"hello\"}"));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WorkWithResources secondRequestStartTime = new WorkWithResources();
        secondRequestStartTime.setCurrentTimeNotStat();

        Response secondResponse = postService(requestUrl, requestBody);
        assertThat(secondResponse.getStatusCode(), equalTo(200));
        assertThat(secondResponse.getBody().asString(), equalTo("{\"msg\":\"hello\"}"));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WorkWithResources thirdRequestStartTime = new WorkWithResources();
        thirdRequestStartTime.setCurrentTimeNotStat();

        Response thirdResponse = postService(requestUrl, requestBody);
        assertThat(thirdResponse.getStatusCode(), equalTo(200));
        assertThat(thirdResponse.getBody().asString(), equalTo("{\"msg\":\"hello\"}"));

        String GMSLogFile = getNewestFileFromDirAsString(gmsLogsDir, ext);

        System.out.println("mark_before_firstRequestStartTime = " + GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat()));
        System.out.println("mark_before_secondRequestStartTime = " + GMSLogFile.indexOf(secondRequestStartTime.getTimeNotStat()));
        System.out.println("mark_before_thirdRequestStartTime = " + GMSLogFile.indexOf(thirdRequestStartTime.getTimeNotStat()));
        System.out.println("time_before_firstRequestStartTime = " + firstRequestStartTime.getTimeNotStat());
        System.out.println("time_before_secondRequestStartTime = " + secondRequestStartTime.getTimeNotStat());
        System.out.println("time_before_thirdRequestStartTime = " + thirdRequestStartTime.getTimeNotStat());

        searchReqTimeMarkInLogs(GMSLogFile, firstRequestStartTime, maxDesyncInSec);

        searchReqTimeMarkInLogs(GMSLogFile, secondRequestStartTime, maxDesyncInSec);

        searchReqTimeMarkInLogs(GMSLogFile, thirdRequestStartTime, maxDesyncInSec);

//        while (-1 == GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat())) {
//            firstRequestStartTime.addSecondsToCurrentTime(1);
//        }
//
//        while (-1 == GMSLogFile.indexOf(secondRequestStartTime.getTimeNotStat())) {
//            secondRequestStartTime.addSecondsToCurrentTime(1);
//        }
//
//        while (-1 == GMSLogFile.indexOf(thirdRequestStartTime.getTimeNotStat())) {
//            thirdRequestStartTime.addSecondsToCurrentTime(1);
//        }

        System.out.println("mark_after_firstRequestStartTime = " + GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat()));
        System.out.println("mark_after_secondRequestStartTime = " + GMSLogFile.indexOf(secondRequestStartTime.getTimeNotStat()));
        System.out.println("mark_after_thirdRequestStartTime = " + GMSLogFile.indexOf(thirdRequestStartTime.getTimeNotStat()));
        System.out.println("time_after_firstRequestStartTime = " + firstRequestStartTime.getTimeNotStat());
        System.out.println("time_after_secondRequestStartTime = " + secondRequestStartTime.getTimeNotStat());
        System.out.println("time_after_thirdRequestStartTime = " + thirdRequestStartTime.getTimeNotStat());

        assertThat(GMSLogFile.indexOf(successfulRequestToHost, GMSLogFile.indexOf(secondRequestStartTime.getTimeNotStat())),
                greaterThan(GMSLogFile.indexOf(successfulRequestToHost, GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat()))));

        assertThat(GMSLogFile.indexOf(successfulRequestToHost, GMSLogFile.indexOf(thirdRequestStartTime.getTimeNotStat())),
                greaterThan(GMSLogFile.indexOf(successfulRequestToHost, GMSLogFile.indexOf(secondRequestStartTime.getTimeNotStat()))));
    }

    @Test
    public void test1_03() throws AtsCfgComponentException, ProtocolException, ConfigException {
        if (!scsManager.getApplicationStatus(orsAppName).equals(APP_STATUS.STOPPED)) {
            scsManager.stopApplication(orsAppName);
        }

        scsManager_2nd.restartApplication(orsAppName);

        addHelloService(cfgGMSApplication, ors1stURL + "," + ors2ndURL, "linear");
        setMaxORSRequestAttempts(cfgGMSApplication, "3");

        WorkWithResources firstRequestStartTime = new WorkWithResources();
        firstRequestStartTime.setCurrentTimeNotStat();

        Response firstResponse = postService(requestUrl, requestBody);
        assertThat(firstResponse.getStatusCode(), equalTo(200));
        assertThat(firstResponse.getBody().asString(), equalTo("{\"msg\":\"hello\"}"));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WorkWithResources secondRequestStartTime = new WorkWithResources();
        secondRequestStartTime.setCurrentTimeNotStat();

        Response secondResponse = postService(requestUrl, requestBody);
        assertThat(secondResponse.getStatusCode(), equalTo(200));
        assertThat(secondResponse.getBody().asString(), equalTo("{\"msg\":\"hello\"}"));

        String GMSLogFile = getNewestFileFromDirAsString(gmsLogsDir, ext);

        System.out.println("mark_before_firstRequestStartTime = " + GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat()));
        System.out.println("mark_before_secondRequestStartTime = " + GMSLogFile.indexOf(secondRequestStartTime.getTimeNotStat()));
        System.out.println("time_before_firstRequestStartTime = " + firstRequestStartTime.getTimeNotStat());
        System.out.println("time_before_secondRequestStartTime = " + secondRequestStartTime.getTimeNotStat());

        searchReqTimeMarkInLogs(GMSLogFile, firstRequestStartTime, maxDesyncInSec);

        searchReqTimeMarkInLogs(GMSLogFile, secondRequestStartTime, maxDesyncInSec);

//        while (-1 == GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat())) {
//            firstRequestStartTime.addSecondsToCurrentTime(1);
//        }
//
//        while (-1 == GMSLogFile.indexOf(secondRequestStartTime.getTimeNotStat())) {
//            secondRequestStartTime.addSecondsToCurrentTime(1);
//        }

        System.out.println("mark_after_firstRequestStartTime = " + GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat()));
        System.out.println("mark_after_secondRequestStartTime = " + GMSLogFile.indexOf(secondRequestStartTime.getTimeNotStat()));
        System.out.println("time_after_firstRequestStartTime = " + firstRequestStartTime.getTimeNotStat());
        System.out.println("time_after_secondRequestStartTime = " + secondRequestStartTime.getTimeNotStat());

        assertThat(GMSLogFile.indexOf(successfulRequestToHost, GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat())),
                greaterThan(GMSLogFile.indexOf(failedRequestTo1stHost, GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat()))));

        assertThat(GMSLogFile.indexOf(successfulRequestToHost, GMSLogFile.indexOf(secondRequestStartTime.getTimeNotStat())),
                greaterThan(GMSLogFile.indexOf(failedRequestTo1stHost, GMSLogFile.indexOf(secondRequestStartTime.getTimeNotStat()))));

        assertThat(GMSLogFile.indexOf(failedRequestTo1stHost, GMSLogFile.indexOf(secondRequestStartTime.getTimeNotStat())),
                greaterThan(GMSLogFile.indexOf(successfulRequestToHost, GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat()))));
    }

    @Test
    public void test1_04() throws AtsCfgComponentException, ProtocolException, ConfigException {
        if (!scsManager.getApplicationStatus(orsAppName).equals(APP_STATUS.STOPPED)) {
            scsManager.stopApplication(orsAppName);
        }

        scsManager_2nd.restartApplication(orsAppName);

        addHelloService(cfgGMSApplication, ors1stURL + "," + ors2ndURL, "circular");
        setMaxORSRequestAttempts(cfgGMSApplication, "3");

        WorkWithResources firstRequestStartTime = new WorkWithResources();
        firstRequestStartTime.setCurrentTimeNotStat();

        Response firstResponse = postService(requestUrl, requestBody);
        assertThat(firstResponse.getStatusCode(), equalTo(200));
        assertThat(firstResponse.getBody().asString(), equalTo("{\"msg\":\"hello\"}"));

        String GMSLogFile = getNewestFileFromDirAsString(gmsLogsDir, ext);

        System.out.println("mark_before_firstRequestStartTime = " + GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat()));
        System.out.println("time_before_firstRequestStartTime = " + firstRequestStartTime.getTimeNotStat());

        searchReqTimeMarkInLogs(GMSLogFile, firstRequestStartTime, maxDesyncInSec);

//        while (-1 == GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat())) {
//            firstRequestStartTime.addSecondsToCurrentTime(1);
//        }

        System.out.println("mark_after_firstRequestStartTime = " + GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat()));
        System.out.println("time_after_firstRequestStartTime = " + firstRequestStartTime.getTimeNotStat());

        assertThat(GMSLogFile.indexOf(successfulRequestToHost, GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat())),
                greaterThan(GMSLogFile.indexOf(failedRequestTo1stHost, GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat()))));
    }

    @Test
    public void test1_05() throws AtsCfgComponentException, ProtocolException, ConfigException {
        if (!scsManager.getApplicationStatus(orsAppName).equals(APP_STATUS.STOPPED)) {
            scsManager.stopApplication(orsAppName);
        }

        scsManager_2nd.restartApplication(orsAppName);

        addHelloService(cfgGMSApplication, ors1stURL + "," + ors2ndURL, "linear");
        setMaxORSRequestAttempts(cfgGMSApplication, "3");

        WorkWithResources firstRequestStartTime = new WorkWithResources();
        firstRequestStartTime.setCurrentTimeNotStat();

        Response firstResponse = postService(requestUrl, requestBody);
        assertThat(firstResponse.getStatusCode(), equalTo(200));
        assertThat(firstResponse.getBody().asString(), equalTo("{\"msg\":\"hello\"}"));

        String GMSLogFile = getNewestFileFromDirAsString(gmsLogsDir, ext);

        System.out.println("mark_after_firstRequestStartTime = " + GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat()));
        System.out.println("time_before_firstRequestStartTime = " + firstRequestStartTime.getTimeNotStat());

        searchReqTimeMarkInLogs(GMSLogFile, firstRequestStartTime, maxDesyncInSec);

//        while (-1 == GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat())) {
//            firstRequestStartTime.addSecondsToCurrentTime(1);
//        }

        System.out.println("mark_after_firstRequestStartTime = " + GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat()));
        System.out.println("time_after_firstRequestStartTime = " + firstRequestStartTime.getTimeNotStat());

        assertThat(GMSLogFile.indexOf(successfulRequestToHost, GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat())),
                greaterThan(GMSLogFile.indexOf(failedRequestTo1stHost, GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat()))));
    }

    @Test
    public void test1_06() throws AtsCfgComponentException, ProtocolException, ConfigException {
        if (!scsManager.getApplicationStatus(orsAppName).equals(APP_STATUS.STOPPED)) {
            scsManager.stopApplication(orsAppName);
        }

        scsManager_2nd.restartApplication(orsAppName);

        addHelloService(cfgGMSApplication, ors1stURL + "," + ors2ndURL, "circular");
        setMaxORSRequestAttempts(cfgGMSApplication, "1");

        WorkWithResources firstRequestStartTime = new WorkWithResources();
        firstRequestStartTime.setCurrentTimeNotStat();

        Response firstResponse = postService(requestUrl, requestBody);
        assertThat(firstResponse.getStatusCode(), equalTo(500));
        assertThat(firstResponse.getBody().asString(),
                equalTo("{\"exception\":\"com.genesyslab.gsg.services.ors.OrsServiceException\",\"message\":\"ORS request failed: cannot connect to ORS. IO issue.\"}"));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WorkWithResources secondRequestStartTime = new WorkWithResources();
        secondRequestStartTime.setCurrentTimeNotStat();

        Response secondResponse = postService(requestUrl, requestBody);
        assertThat(secondResponse.getStatusCode(), equalTo(200));
        assertThat(secondResponse.getBody().asString(), equalTo("{\"msg\":\"hello\"}"));

        String GMSLogFile = getNewestFileFromDirAsString(gmsLogsDir, ext);

        System.out.println("mark_before_firstRequestStartTime = " + GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat()));
        System.out.println("mark_before_secondRequestStartTime = " + GMSLogFile.indexOf(secondRequestStartTime.getTimeNotStat()));
        System.out.println("time_before_firstRequestStartTime = " + firstRequestStartTime.getTimeNotStat());
        System.out.println("time_before_secondRequestStartTime = " + secondRequestStartTime.getTimeNotStat());

//        searchReqTimeMarkInLogs(GMSLogFile, firstRequestStartTime, maxDesyncInSec);
//
//        searchReqTimeMarkInLogs(GMSLogFile, secondRequestStartTime, maxDesyncInSec);

        while (!GMSLogFile.contains(firstRequestStartTime.getTimeNotStat())) {
            firstRequestStartTime.addSecondsToCurrentTime(1);
        }

        while (!GMSLogFile.contains(secondRequestStartTime.getTimeNotStat())) {
            secondRequestStartTime.addSecondsToCurrentTime(1);
        }

        System.out.println("mark_after_firstRequestStartTime = " + GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat()));
        System.out.println("mark_after_secondRequestStartTime = " + GMSLogFile.indexOf(secondRequestStartTime.getTimeNotStat()));
        System.out.println("time_after_firstRequestStartTime = " + firstRequestStartTime.getTimeNotStat());
        System.out.println("time_after_secondRequestStartTime = " + secondRequestStartTime.getTimeNotStat());

        assertThat(GMSLogFile.indexOf(successfulRequestToHost, GMSLogFile.indexOf(secondRequestStartTime.getTimeNotStat())),
                greaterThan(GMSLogFile.indexOf(failedRequestTo1stHost, GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat()))));
    }

    @Test
    public void test1_07() throws AtsCfgComponentException, ProtocolException, ConfigException {
        if (!scsManager.getApplicationStatus(orsAppName).equals(APP_STATUS.STOPPED)) {
            scsManager.stopApplication(orsAppName);
        }

        scsManager_2nd.restartApplication(orsAppName);

        addHelloService(cfgGMSApplication, ors1stURL + "," + ors3rdURL + "," + ors2ndURL, "circular");
        setMaxORSRequestAttempts(cfgGMSApplication, "");

        WorkWithResources firstRequestStartTime = new WorkWithResources();
        firstRequestStartTime.setCurrentTimeNotStat();

        Response firstResponse = postService(requestUrl, requestBody);
        assertThat(firstResponse.getStatusCode(), equalTo(200));
        assertThat(firstResponse.getBody().asString(),
                equalTo("{\"msg\":\"hello\"}"));

        String GMSLogFile = getNewestFileFromDirAsString(gmsLogsDir, ext);

        System.out.println("mark_before_firstRequestStartTime = " + GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat()));
        System.out.println("time_before_firstRequestStartTime = " + firstRequestStartTime.getTimeNotStat());

//        searchReqTimeMarkInLogs(GMSLogFile, firstRequestStartTime, maxDesyncInSec);

        while (!GMSLogFile.contains(firstRequestStartTime.getTimeNotStat())) {
            firstRequestStartTime.addSecondsToCurrentTime(1);
        }

        System.out.println("mark_after_firstRequestStartTime = " + GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat()));
        System.out.println("time_after_firstRequestStartTime = " + firstRequestStartTime.getTimeNotStat());

        assertThat(GMSLogFile.indexOf(failedRequestTo3rdHost, GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat())),
                greaterThan(GMSLogFile.indexOf(failedRequestTo1stHost, GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat()))));

        assertThat(GMSLogFile.indexOf(successfulRequestToHost, GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat())),
                greaterThan(GMSLogFile.indexOf(failedRequestTo3rdHost, GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat()))));
    }

    @Test
    public void test1_08() throws AtsCfgComponentException, ProtocolException, ConfigException {
        if (!scsManager.getApplicationStatus(orsAppName).equals(APP_STATUS.STOPPED)) {
            scsManager.stopApplication(orsAppName);
        }
        if (!scsManager_2nd.getApplicationStatus(orsAppName).equals(APP_STATUS.STOPPED)) {
            scsManager_2nd.stopApplication(orsAppName);
        }

        addHelloService(cfgGMSApplication, ors1stURL + "," + ors2ndURL, "circular");
        setMaxORSRequestAttempts(cfgGMSApplication, "3");

        WorkWithResources firstRequestStartTime = new WorkWithResources();
        firstRequestStartTime.setCurrentTimeNotStat();

        Response firstResponse = postService(requestUrl, requestBody);
        assertThat(firstResponse.getStatusCode(), equalTo(500));
        assertThat(firstResponse.getBody().asString(),
                equalTo("{\"exception\":\"com.genesyslab.gsg.services.ors.OrsServiceException\",\"message\":\"ORS request failed: cannot connect to ORS. IO issue.\"}"));

        String GMSLogFile = getNewestFileFromDirAsString(gmsLogsDir, ext);

        System.out.println("mark_before_firstRequestStartTime = " + GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat()));
        System.out.println("time_before_firstRequestStartTime = " + firstRequestStartTime.getTimeNotStat());

//        searchReqTimeMarkInLogs(GMSLogFile, firstRequestStartTime, maxDesyncInSec);

        while (!GMSLogFile.contains(firstRequestStartTime.getTimeNotStat())) {
            firstRequestStartTime.addSecondsToCurrentTime(1);
        }

        System.out.println("mark_after_firstRequestStartTime = " + GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat()));
        System.out.println("time_after_firstRequestStartTime = " + firstRequestStartTime.getTimeNotStat());

        assertThat(GMSLogFile.indexOf(failedRequestTo2ndHost, GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat())),
                greaterThan(GMSLogFile.indexOf(failedRequestTo1stHost, GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat()))));

        assertThat(GMSLogFile.lastIndexOf(failedRequestTo1stHost),
                greaterThan(GMSLogFile.indexOf(failedRequestTo2ndHost, GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat()))));
    }

    @Test
    public void test1_09() throws AtsCfgComponentException, ProtocolException, ConfigException {
        if (!scsManager.getApplicationStatus(orsAppName).equals(APP_STATUS.STOPPED)) {
            scsManager.stopApplication(orsAppName);
        }

        addHelloService(cfgGMSApplication, ors1stURL, "linear");
        setMaxORSRequestAttempts(cfgGMSApplication, "3");

        WorkWithResources firstRequestStartTime = new WorkWithResources();
        firstRequestStartTime.setCurrentTimeNotStat();

        Response firstResponse = postService(requestUrl, requestBody);
        assertThat(firstResponse.getStatusCode(), equalTo(500));
        assertThat(firstResponse.getBody().asString(),
                equalTo("{\"exception\":\"com.genesyslab.gsg.services.ors.OrsServiceException\",\"message\":\"ORS request failed: cannot connect to ORS. IO issue.\"}"));
        // before getting remote log-file please find this file through Windows Search programs and files field
        String GMSLogFile = getNewestFileFromDirAsString(gmsLogsDir, ext);

        System.out.println("mark_before_firstRequestStartTime = " + GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat()));
        System.out.println("time_before_firstRequestStartTime = " + firstRequestStartTime.getTimeNotStat());

        searchReqTimeMarkInLogs(GMSLogFile, firstRequestStartTime, maxDesyncInSec);

//        while (-1 == GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat())) {
//            firstRequestStartTime.addSecondsToCurrentTime(1);
//        }

        System.out.println("mark_after_firstRequestStartTime = " + GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat()));
        System.out.println("time_after_firstRequestStartTime = " + firstRequestStartTime.getTimeNotStat());

        assertThat(GMSLogFile.indexOf(failedRequestTo1stHost, GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat())),
                greaterThan(GMSLogFile.indexOf(firstRequestStartTime.getTimeNotStat())));
    }

    public void addHelloService(CfgApplication appConfigObj, String orsValue, String strategy) throws ConfigException {
        helloServiceSectionData.addString("_name", "The Hello World Service");
        helloServiceSectionData.addString("_ors", orsValue);
        helloServiceSectionData.addString("_ors_lb_strategy", strategy);
        helloServiceSectionData.addString("_service", "hello");
        helloServiceSectionData.addString("_ttl", "3600");
        helloServiceSectionData.addString("_type", "ors");
        helloService = new KeyValuePair("service.hello", helloServiceSectionData);
        appConfigObj.getOptions().add(helloService);
        appConfigObj.save();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setMaxORSRequestAttempts(CfgApplication appConfigObj, String maxORSRequestAttempts) throws ConfigException {
        orsSectionData.addString("max_ors_request_attempts", maxORSRequestAttempts);
        orsSection = new KeyValuePair("ors", orsSectionData);
        appConfigObj.getOptions().add(orsSection);
        appConfigObj.save();
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Response postService(String requestUrl, String requestBody) {
        return given().body(requestBody).contentType("application/x-www-form-urlencoded").header("grs-user", "1fh23fg").post(requestUrl);
    }

    public File[] findFilesInDirByExtension(String dirPath, final String extension) {
        File dir = new File(dirPath);
        return dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.toLowerCase().endsWith(extension);
            }
        });
    }

    public File getNewestFileFromDir(String dirPath, String extension) {
        File[] fileList = findFilesInDirByExtension(dirPath, extension);
        // сортируем лог-файлы, чтобы получить самый последний
        Arrays.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o2.lastModified() < o1.lastModified()) return -1;
                if (o2.lastModified() > o1.lastModified()) return 1;
                return 0;
            }
        });
        File newestFile = fileList[0];
        return newestFile;
    }

    public String getNewestFileFromDirAsString(String dirPath, final String extension) {
        File dir = new File(dirPath);
        File[] fileList = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.toLowerCase().endsWith(extension);
            }
        });
        // сортируем лог-файлы, чтобы получить самый последний
        Arrays.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o2.lastModified() < o1.lastModified()) return -1;
                if (o2.lastModified() > o1.lastModified()) return 1;
                return 0;
            }
        });
        File newestFile = fileList[0];
        WorkWithResources res = new WorkWithResources();
        return res.getContentFromFileAsString(newestFile, extension);
    }

    //this method provide possibility to find your request event in GMS log-file in case
    //desynchronization of the two hosts at few seconds (provide reliability for TCs)
    public void searchReqTimeMarkInLogs(String logFile, WorkWithResources estTimeThatShouldBeFound, int maxDesyncInSec) {
        int i = 1;
        int j = 1;
        while (!logFile.contains(estTimeThatShouldBeFound.getTimeNotStat())) {
            estTimeThatShouldBeFound.addSecondsToCurrentTime(+1);
            if (i == maxDesyncInSec) {
                break;
            } else i++;
        }
        while (!logFile.contains(estTimeThatShouldBeFound.getTimeNotStat())) {
            estTimeThatShouldBeFound.addSecondsToCurrentTime(-(1 + i));
            if (j == maxDesyncInSec) {
                break;
            } else j++;
            i = 0;
        }
    }
    //------------------------------------------------training----------------------------------------------------------------------------------------------------------------------------
    private SCSManager initInstance() throws Exception {
        SCSManager result = new SCSManager(new CfgManager()).setCfgHost(getPropertyConfiguration("config.server.host"))
                .setCfgPort(getPropertyConfiguration("config.server.port"))
                .setClientName(getPropertyConfiguration("username"))
                .setUserName(getPropertyConfiguration("config.client.name"))
                .setPassword(getPropertyConfiguration("password")).init();
        assertThat(result.isInitialized(), is(true));
        return result;
    }

}