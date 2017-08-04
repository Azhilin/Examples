package gms.callback;

import com.genesyslab.ats.ex.AtsCfgComponentException;
import com.genesyslab.functional.tests.gms.helper.CallbackServicesAPI;
import com.genesyslab.functional.tests.gms.psdk.Reconfiguration;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.genesyslab.functional.tests.gms.helper.CallbackServiceHelper.cancelUnfinishedCallbacks;
import static com.genesyslab.functional.tests.gms.helper.CallbackServicesAPI.createBody;
import static com.genesyslab.functional.tests.gms.helper.CallbackServicesAPI.startImmediateCallback;
import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertyConfiguration;
import static com.genesyslab.functional.tests.gms.helper.ServiceOptions.*;
import static com.genesyslab.functional.tests.gms.helper.TimeOperations.getMethodExecutionTime;
import static com.genesyslab.functional.tests.gms.helper.URLsHelper.getGMSBaseURL;
import static com.genesyslab.functional.tests.gms.helper.URLsHelper.getORSBaseURL;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

/**
 * Created by bvolovyk on 10.05.2017.
 */

/*
 * [Test Suite] Support URL rewriting: https://jira.genesys.com/browse/GMS-4302
 */
public class SupportURLRewritingTests {
    //    private final static Logger logger = Logger.getLogger(SupportURLRewritingTests.class);
    //    private static String propertiesFile = getPropertiesFile();
    private static String propertiesFile = "./config_2nd.properties";//uncomment this line in case you want to use another .properties file
    private static Reconfiguration env = new Reconfiguration(propertiesFile);
    private static String gmsClusterAppName = getPropertyConfiguration(propertiesFile, "gms.cluster.app.name");
    private static String gmsBaseURL = getGMSBaseURL(propertiesFile);
    private static String customerNumber = "5115";
    private static String callbackImmServiceName = "cb_term_im_gms-4232";
    private static String requestInteractionServiceName = "request-interaction";
    private static String helloServiceName = "hello";

    @BeforeClass
    public static void oneTimeSetUp() throws AtsCfgComponentException {
        long startTime = System.nanoTime();
        System.out.println("@BeforeClass method processing...");
        env.createService(gmsClusterAppName, callbackImmServiceName, getUserTermImmCallbackServiceOptions());
        env.createService(gmsClusterAppName, requestInteractionServiceName, getRequestInteractionServiceOptions());
        env.createService(gmsClusterAppName, helloServiceName, getHelloServiceOptions(getORSBaseURL(propertiesFile)));
        CallbackServicesAPI.setGMSBaseURL(gmsBaseURL);
        long endTime = System.nanoTime();
        getMethodExecutionTime(startTime, endTime, "@BeforeClass");
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("@After method processing...");
        cancelUnfinishedCallbacks(callbackImmServiceName);
        deleteAllOverwritableOptions(callbackImmServiceName);
    }

    @AfterClass
    public static void oneTimeTearDown() throws AtsCfgComponentException, InterruptedException {
        System.out.println("@AfterClass method processing...");
        env.deleteService(gmsClusterAppName, callbackImmServiceName); //for troubleshooting purposes comment this command
        env.deleteService(gmsClusterAppName, helloServiceName); //for troubleshooting purposes comment this command
        Thread.sleep(500);
        env.deactivate();
    }

    @Test
    public void test_01() throws Exception {
        env.addOptionToService(gmsClusterAppName, callbackImmServiceName, "_overwritable_options", "_redirect");
        env.addOptionToService(gmsClusterAppName, callbackImmServiceName, "_redirect", "hello");

        Response callback = startImmediateCallback(callbackImmServiceName, customerNumber);
        callback.then().assertThat().statusCode(200);
        callback.then().assertThat().body("msg", containsString("hello"));
    }

    @Test
    public void test_02() throws Exception {
        env.addOptionToService(gmsClusterAppName, callbackImmServiceName, "_overwritable_options", "_redirect");
        env.addOptionToService(gmsClusterAppName, callbackImmServiceName, "_redirect", "hello");

        JSONObject cbRequestBody = createCbRequestBody(customerNumber, "request-interaction");
        Response callback = startImmCallbackJSON(callbackImmServiceName, cbRequestBody);

        callback.then().assertThat().statusCode(200);
        callback.then().assertThat().body(containsString("_access_number"),
                containsString("_expiration_time"), containsString("_id"));
    }

    @Test
    public void test_03() throws Exception {
        env.addOptionToService(gmsClusterAppName, callbackImmServiceName, "_overwritable_options", "_redirect");
        env.addOptionToService(gmsClusterAppName, callbackImmServiceName, "_redirect", "hello");

        JSONObject cbRequestBody = createCbRequestBody(customerNumber, "");
        Response callback = startImmCallbackJSON(callbackImmServiceName, cbRequestBody);

        callback.then().assertThat().statusCode(200);
        callback.then().assertThat().body("_text", containsString("You will receive the call shortly"));
    }

    @Test
    public void test_04() throws Exception {
        env.addOptionToService(gmsClusterAppName, callbackImmServiceName, "_redirect", "hello");

        JSONObject cbRequestBody = createCbRequestBody(customerNumber, "");
        Response callback = startImmCallbackJSON(callbackImmServiceName, cbRequestBody);

        callback.then().assertThat().statusCode(200);
        callback.then().assertThat().body("msg", containsString("hello"));
    }

    @Test
    public void test_05() throws Exception {
        env.addOptionToService(gmsClusterAppName, callbackImmServiceName, "_redirect", "hello");

        Response callback = startImmediateCallback(callbackImmServiceName, customerNumber);
        callback.then().assertThat().statusCode(200);
        callback.then().assertThat().body("msg", containsString("hello"));
    }

    @Test
    public void test_06() throws Exception {
        env.addOptionToService(gmsClusterAppName, callbackImmServiceName, "_overwritable_options", "_redirect");

        Response callback = startImmediateCallback(callbackImmServiceName, customerNumber);
        callback.then().assertThat().statusCode(200);
        callback.then().assertThat().body("_text", containsString("You will receive the call shortly"));
    }

    @Test
    public void test_07() throws Exception {
        env.addOptionToService(gmsClusterAppName, callbackImmServiceName, "_overwritable_options", "");
        env.addOptionToService(gmsClusterAppName, callbackImmServiceName, "_redirect", "hello");

        JSONObject cbRequestBody = createCbRequestBody(customerNumber, "request-interaction");
        Response callback = startImmCallbackJSON(callbackImmServiceName, cbRequestBody);

        callback.then().assertThat().statusCode(200);
        callback.then().assertThat().body("msg", containsString("hello"));
    }

    @Test
    public void test_08() throws Exception {
        env.addOptionToService(gmsClusterAppName, callbackImmServiceName, "_overwritable_options", "_redirect");
        env.addOptionToService(gmsClusterAppName, callbackImmServiceName, "_redirect", "");

        Response callback = startImmediateCallback(callbackImmServiceName, customerNumber);
        callback.then().assertThat().statusCode(200);
        callback.then().assertThat().body("_text", containsString("You will receive the call shortly"));
    }

    @Test
    public void test_09() throws Exception {
        env.addOptionToService(gmsClusterAppName, callbackImmServiceName, "_overwritable_options", "_redirect");

        JSONObject cbRequestBody = createCbRequestBody(customerNumber, "request-interaction");
        Response callback = startImmCallbackJSON(callbackImmServiceName, cbRequestBody);

        callback.then().assertThat().statusCode(200);
        callback.then().assertThat().body(containsString("_access_number"),
                containsString("_expiration_time"), containsString("_id"));
    }

    @Test
    public void test_10() throws Exception {
        env.addOptionToService(gmsClusterAppName, callbackImmServiceName, "_overwritable_options", "_redirect");
        env.addOptionToService(gmsClusterAppName, callbackImmServiceName, "_redirect", "hello");

        Map<String, String> addParams = new HashMap<>();
        addParams.put("_customer_number", customerNumber);
        Response callback = startImmCallbackURLENC(callbackImmServiceName, addParams);

        callback.then().assertThat().statusCode(200);
        callback.then().assertThat().body("msg", containsString("hello"));
    }

    @Test
    public void test_11() throws Exception {
        env.addOptionToService(gmsClusterAppName, callbackImmServiceName, "_overwritable_options", "_redirect");
        env.addOptionToService(gmsClusterAppName, callbackImmServiceName, "_redirect", "hello");

        Map<String, String> addParams = new HashMap<>();
        addParams.put("_customer_number", customerNumber);
        addParams.put("_redirect", "");
        Response callback = startImmCallbackURLENC(callbackImmServiceName, addParams);

        callback.then().assertThat().statusCode(200);
        callback.then().assertThat().body("_text", containsString("You will receive the call shortly"));
    }

    @Test
    public void test_12() throws Exception {
        env.addOptionToService(gmsClusterAppName, callbackImmServiceName, "_overwritable_options", "_redirect");
        env.addOptionToService(gmsClusterAppName, callbackImmServiceName, "_redirect", "hello");

        Map<String, String> addParams = new HashMap<>();
        addParams.put("_customer_number", customerNumber);
        addParams.put("_redirect", "request-interaction");
        Response callback = startImmCallbackURLENC(callbackImmServiceName, addParams);

        callback.then().assertThat().statusCode(200);
        callback.then().assertThat().body(containsString("_access_number"),
                containsString("_expiration_time"), containsString("_id"));
    }

    @Test
    public void test_13() throws Exception {
        env.addOptionToService(gmsClusterAppName, callbackImmServiceName, "_overwritable_options", "_redirect");

        Map<String, String> addParams = new HashMap<>();
        addParams.put("_customer_number", customerNumber);
        addParams.put("_redirect", "");
        Response callback = startImmCallbackURLENC(callbackImmServiceName, addParams);

        callback.then().assertThat().statusCode(200);
        callback.then().assertThat().body("_text", containsString("You will receive the call shortly"));
    }

    @Test
    public void test_14() throws Exception {
        env.addOptionToService(gmsClusterAppName, callbackImmServiceName, "_overwritable_options", "_redirect");
        env.addOptionToService(gmsClusterAppName, callbackImmServiceName, "_redirect", "");

        Map<String, String> addParams = new HashMap<>();
        addParams.put("_customer_number", customerNumber);
        addParams.put("_redirect", "request-interaction");
        Response callback = startImmCallbackURLENC(callbackImmServiceName, addParams);

        callback.then().assertThat().statusCode(200);
        callback.then().assertThat().body(containsString("_access_number"),
                containsString("_expiration_time"), containsString("_id"));
    }

    @Test
    public void test_15() throws Exception {
        env.addOptionToService(gmsClusterAppName, callbackImmServiceName, "_overwritable_options", "");
        env.addOptionToService(gmsClusterAppName, callbackImmServiceName, "_redirect", "hello");

        Map<String, String> addParams = new HashMap<>();
        addParams.put("_customer_number", customerNumber);
        Response callback = startImmCallbackURLENC(callbackImmServiceName, addParams);

        callback.then().assertThat().statusCode(200);
        callback.then().assertThat().body("msg", containsString("hello"));
    }

    @Test
    public void test_16() throws Exception {
        env.addOptionToService(gmsClusterAppName, callbackImmServiceName, "_overwritable_options", "_redirect");
        env.addOptionToService(gmsClusterAppName, callbackImmServiceName, "_redirect", "");

        Map<String, String> addParams = new HashMap<>();
        addParams.put("_customer_number", customerNumber);
        addParams.put("_redirect", "");
        Response callback = startImmCallbackURLENC(callbackImmServiceName, addParams);

        callback.then().assertThat().statusCode(200);
        callback.then().assertThat().body("_text", containsString("You will receive the call shortly"));
    }

    @Test
    public void test_17() throws Exception {
        env.addOptionToService(gmsClusterAppName, callbackImmServiceName, "_redirect", "hello");

        Map<String, String> addParams = new HashMap<>();
        addParams.put("_customer_number", customerNumber);
        addParams.put("_redirect", "request-interaction");
        Response callback = startImmCallbackURLENC(callbackImmServiceName, addParams);

        callback.then().assertThat().statusCode(200);
        callback.then().assertThat().body("msg", containsString("hello"));
    }

    @Test
    public void test_18() throws Exception {
        env.addOptionToService(gmsClusterAppName, callbackImmServiceName, "_overwritable_options", "");
        env.addOptionToService(gmsClusterAppName, callbackImmServiceName, "_redirect", "hello");

        Map<String, String> addParams = new HashMap<>();
        addParams.put("_customer_number", customerNumber);
        addParams.put("_redirect", "");
        Response callback = startImmCallbackURLENC(callbackImmServiceName, addParams);

        callback.then().assertThat().statusCode(200);
        callback.then().assertThat().body("msg", containsString("hello"));
    }

    @Test
    public void test_19() throws Exception {
        env.addOptionToService(gmsClusterAppName, requestInteractionServiceName, "_overwritable_options", "_redirect");
        env.addOptionToService(gmsClusterAppName, requestInteractionServiceName, "_redirect", "hello");

        Map<String, String> addParams = new HashMap<>();
        addParams.put("_customer_number", customerNumber);

        String requestURL = gmsBaseURL + "/genesys/1/service/request-interaction";
        Response requestInter = given().contentType(ContentType.URLENC).formParams(addParams).post(requestURL);
        System.out.println("Callback creation response: " + requestInter.then().extract().response().asString());
        requestInter.then().assertThat().statusCode(200);
        requestInter.then().assertThat().body(containsString("_access_number"),
                containsString("_expiration_time"), containsString("_id"));
    }

    private static JSONObject createCbRequestBody(String custNumber, String redirectValue) {
        Map<String, String> addBodyParams = new HashMap<>();
        addBodyParams.put("_redirect", redirectValue);
        return createBody(custNumber, addBodyParams);
    }

    public static Response startImmCallbackJSON(final String cbServiceName, final JSONObject cbRequestBody) {
        String requestURL = gmsBaseURL + "/genesys/1/service/callback" + "/" + cbServiceName;
//        String cbRequestBody = createBody(customerNumber).toJSONString();
        Response immediateCallback = given().body(cbRequestBody.toJSONString()).contentType(ContentType.JSON).post(requestURL);
        System.out.println("Callback creation response: " +
                immediateCallback.then().extract().response().asString());
        return immediateCallback;
    }

    public static Response startImmCallbackURLENC(final String cbServiceName, final Map<String, ?> addParams) {
        String requestURL = gmsBaseURL + "/genesys/1/service/callback" + "/" + cbServiceName;
//        String cbRequestBody = createBody(customerNumber).toJSONString();
        Response immediateCallback = given().contentType(ContentType.URLENC).formParams(addParams).post(requestURL);
        System.out.println("Callback creation response: " +
                immediateCallback.then().extract().response().asString());
        return immediateCallback;
    }

    private static void deleteAllOverwritableOptions(String cbServiceName) throws Exception {
        env.deleteOptionFromService(gmsClusterAppName, cbServiceName, "_overwritable_options");
        env.deleteOptionFromService(gmsClusterAppName, cbServiceName, "_redirect");
        Thread.sleep(1000);
    }
}