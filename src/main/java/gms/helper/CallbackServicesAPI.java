package gms.helper;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertiesFile;
import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertyConfiguration;
import static com.genesyslab.functional.tests.gms.helper.TimeOperations.getUTCTimeInFuture;
import static com.genesyslab.functional.tests.gms.helper.URLsHelper.getGMSBaseURL;
import static io.restassured.RestAssured.given;

/**
 * Created by bvolovyk on 01.12.2016.
 */
public class CallbackServicesAPI {
    private static String gmsBaseURL = getGMSBaseURL(getPropertiesFile());
    private static String serviceURL = "/genesys/1/service/";
    private static String serviceCallbackURL = serviceURL + "callback";
    private static String checkQueuePositionURL = "/check-queue-position";
    private static String watermarksURL = "/genesys/1/admin/callback/watermarks";
    private static String queryCallbackByQueueURL = "/genesys/1/admin/callback/queues";
    private static String exportCancelledURL = "/genesys/1/admin/callback/reportcancelled";
    private static String cbCancelURL="/genesys/1/admin/callback/cancel";

    public static void setGMSBaseURL(String gmsBaseURL) {
        CallbackServicesAPI.gmsBaseURL = gmsBaseURL;
        System.out.printf("CallbackServicesAPI.gmsBaseURL was set to %s value.%n", gmsBaseURL);
    }

    public static Response startImmediateCallback(final String cbServiceName, final String custNumber) {
        String requestURL = gmsBaseURL + serviceCallbackURL + "/" + cbServiceName;
        String cbRequestBody = createBody(custNumber).toJSONString();
        Response immediateCallback = given().body(cbRequestBody).contentType(ContentType.JSON).post(requestURL);
        System.out.printf("Callback creation response for %s service: %s%n", cbServiceName,
                immediateCallback.then().extract().response().asString());
        return immediateCallback;
    }
    
    public static Response startImmediateCallback(final String cbServiceName, final Map<String, ?> params) {
        String requestURL = gmsBaseURL + serviceCallbackURL + "/" + cbServiceName;
        String cbRequestBody = createBody(params).toJSONString();
        Response immediateCallback = given().body(cbRequestBody).contentType(ContentType.JSON).post(requestURL);
        System.out.printf("Callback creation response for %s service: %s%n", cbServiceName,
                immediateCallback.then().extract().response().asString());
        return immediateCallback;
    }

    public static Response startImmediateCallback(final String cbServiceName,
                                                  final String custNumber,
                                                  final Map<String, ?> headers) {
        String requestURL = gmsBaseURL + serviceCallbackURL + "/" + cbServiceName;
        String cbRequestBody = createBody(custNumber).toJSONString();
        return given()
                .auth().preemptive()
                .basic(getPropertyConfiguration("cb.api.user.name"), getPropertyConfiguration("cb.api.user.password"))
                .body(cbRequestBody)
                .contentType(ContentType.JSON)
                .headers(headers)
                .post(requestURL);
    }

    public static Response startScheduledCallback(final String cbServiceName,
                                                  final String custNumber,
                                                  final int secondsFromNowForDesiredTime) {
        String requestURL = gmsBaseURL + serviceCallbackURL + "/" + cbServiceName;
        String cbRequestBody = createBody(custNumber, secondsFromNowForDesiredTime).toJSONString();
        Response scheduledCallback = given().body(cbRequestBody).contentType(ContentType.JSON).post(requestURL);
        System.out.printf("Callback creation response for %s service: %s%n", cbServiceName,
                scheduledCallback.then().extract().response().asString());
        return scheduledCallback;
    }

    public static Response startScheduledCallback(final String cbServiceName,
                                                  final String custNumber,
                                                  final int secondsFromNowForDesiredTime,
                                                  final Map<String, ?> headers) {
        String requestURL = gmsBaseURL + serviceCallbackURL + "/" + cbServiceName;
        String cbRequestBody = createBody(custNumber, secondsFromNowForDesiredTime).toJSONString();
        return given()
                .auth().preemptive()
                .basic(getPropertyConfiguration("cb.api.user.name"), getPropertyConfiguration("cb.api.user.password"))
                .body(cbRequestBody)
                .contentType(ContentType.JSON)
                .headers(headers)
                .post(requestURL);
    }

    public static Response startScheduledCallback(final String callbackServiceName,
    											  final String customerNumber,
    											  final String desiredTime) {
    	String requestURL = gmsBaseURL + serviceCallbackURL + "/" + callbackServiceName;
    	String cbRequestBody = createBody(customerNumber, desiredTime).toJSONString();
    	Response scheduledCallback = given().body(cbRequestBody).contentType(ContentType.JSON).post(requestURL);
    	System.out.println("Callback creation response: " +
    			scheduledCallback.then().extract().response().asString());
    	return scheduledCallback;
    }
    
    public static Response startScheduledCallback(final String callbackServiceName, final String customerNumber,
			  final Map<String,?> map ) {
		String requestURL = gmsBaseURL + serviceCallbackURL + "/" + callbackServiceName;
		String cbRequestBody = createBody(customerNumber, map).toJSONString();		
		Response scheduledCallback = given().body(cbRequestBody).contentType(ContentType.JSON).post(requestURL);
		System.out.println("Callback creation response: " +
		scheduledCallback.then().extract().response().asString());
		return scheduledCallback;
	}
    
    /**
     * Update aka reschedule cb
     * @param callbackServiceName
     * @param customerNumber
     * @param map
     * @return
     */
    public static Response updateCallback(final String callbackServiceName, String cbServiceId, final String customerNumber,
			  final Map<String,?> map ) {
		String requestURL = gmsBaseURL + serviceCallbackURL + "/" + callbackServiceName+"/"+ cbServiceId;
		String cbRequestBody = createBody(customerNumber, map).toJSONString();		
		Response scheduledCallback = given().body(cbRequestBody).contentType(ContentType.JSON).put(requestURL);
		System.out.println("Callback creation response: " +
		scheduledCallback.then().extract().response().asString());
		return scheduledCallback;
	}

    public static Response queryCallbackByID(final String cbId) {
        String requestURL = gmsBaseURL + serviceCallbackURL;
        Response r = given().param("_id", cbId).contentType(ContentType.URLENC).get(requestURL);
        System.out.printf("Response for Query-Callback by Id %s: %s%n", cbId, r.then().extract().response().asString());
        return r;
    }

    public static Response queryCallbackByState(final String custNumber, final String cbState) {
        String requestURL = gmsBaseURL + serviceCallbackURL;
        Response queryResponse = given().param("_customer_number", custNumber)
                .param("_callback_state", cbState)
                .contentType(ContentType.URLENC).get(requestURL);
        System.out.println("Callbacks in " + cbState + " state: " +
                queryResponse.then().extract().response().asString());
        return queryResponse;
    }

    public static Response checkQueuePosition(final String cbId) {
        String requestURL = gmsBaseURL + serviceURL + cbId + checkQueuePositionURL;
        Response r = given().contentType(ContentType.URLENC).get(requestURL);
        System.out.printf("Response for Check-Queue-Position by Id %s: %s%n", cbId, r.then().extract().response().asString());
        return r;
    }

    public static Response cancelCallback(final String cbServiceName, final String cbId) {
        String requestURL = gmsBaseURL + serviceCallbackURL + "/" + cbServiceName + "/" + cbId;
        Response r = given().contentType(ContentType.JSON).delete(requestURL);
        System.out.printf("Response for Cancel Callback by Id %s: %s%n", cbId, r.then().extract().response().asString());
        return r;
    }

    @SuppressWarnings("unchecked")
	public static Response cancelCallbacksByReason(final String callbackReason, List<String> cbIds) {
        String requestURL = gmsBaseURL + cbCancelURL;
        JSONObject cbRequestBody = new JSONObject();
        cbRequestBody.put("reason", callbackReason);
        cbRequestBody.put("ids", cbIds);
    	Response r = given().body(cbRequestBody).contentType(ContentType.JSON).post(requestURL);

    	System.out.printf("Response for Cancel Callbacks by Id %s: %s%n", cbIds, r.then().extract().response().asString());
        return r;
    }

    public static Response queryCounterWatermarks() {
        String requestURL = gmsBaseURL + watermarksURL;
        Response r = given().contentType(ContentType.URLENC).get(requestURL);
        System.out.printf("Response for Query-Counter-Watermarks: %s%n", r.then().extract().response().asString());
        return r;
    }

    public static Response queryCounterWatermarks(final List<String> uriParams) {
        String requestURL = gmsBaseURL + watermarksURL;
        Response r = given().param("service_name", uriParams).contentType(ContentType.URLENC).get(requestURL);
        System.out.printf("Response for Query-Counter-Watermarks: %s%n", r.then().extract().response().asString());
        return r;
    }

    @SuppressWarnings("unchecked")
    private static JSONObject createBody(final String custNumber) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("_customer_number", custNumber);
        return jsonBody;
    }

    @SuppressWarnings("unchecked")
    public static JSONObject createBody(final String custNumber, final Map<String, ?> addBodyParams) {
        JSONObject jsonBody = createBody(custNumber);
        jsonBody.putAll(addBodyParams);
        return jsonBody;
    }
    
    @SuppressWarnings("unchecked")
    public static JSONObject createBody(final Map<String, ?> addBodyParams) {
    	JSONObject jsonBody = new JSONObject();
        jsonBody.putAll(addBodyParams);
        return jsonBody;
    }

    @SuppressWarnings("unchecked")
    private static JSONObject createBody(final String custNumber, final int secondsFromNowForDesiredTime) {
        JSONObject jsonBody = createBody(custNumber);
        jsonBody.put("_desired_time", getUTCTimeInFuture(secondsFromNowForDesiredTime));
        return jsonBody;
    }

    @SuppressWarnings("unchecked")
    private static JSONObject createBody(final String custNumber,
                                         final int secondsFromNowForDesiredTime,
                                         final Map<String, ?> addParams) {
        JSONObject jsonBody = createBody(custNumber, secondsFromNowForDesiredTime);
        jsonBody.putAll(addParams);
        return jsonBody;
    }

    @SuppressWarnings("unchecked")
    private static JSONObject createBody(final String customerNumber, final String desiredTime) {
        JSONObject jsonBody = createBody(customerNumber);
        jsonBody.put("_desired_time", desiredTime);
        return jsonBody;
    }

    public static Response queryCallbackByQueue() {
        String requestURL = gmsBaseURL + queryCallbackByQueueURL;
        return given().contentType(ContentType.URLENC).get(requestURL);
    }

    public static Response queryCallbackByQueue(Map<String, ?> headers) {
        String requestURL = gmsBaseURL + queryCallbackByQueueURL;
        return given().headers(headers).contentType(ContentType.URLENC).get(requestURL);
    }

    public static Response queryCallbackByQueue(final String cbServiceName, final String startTime, final String endTime) {
        String requestURL = gmsBaseURL + queryCallbackByQueueURL;
        return given()
                .param("target", cbServiceName)
                .param("start_time", startTime)
                .param("end_time", endTime).contentType(ContentType.URLENC).get(requestURL);
    }
    
    public static Response queryCallbackByQueue(final String startTime, final Map<String, ?> addFilter ) {
        String requestURL = gmsBaseURL + queryCallbackByQueueURL;
        Response response = given()
                .param("start_time", startTime)
                .params(addFilter).contentType(ContentType.URLENC).get(requestURL);
    	System.out.println(
				"Response body for callback queue: " + response.then().extract().response().asString());
    return response;
    }
        
        
    public static Response queryCallbackByQueue(final String cbServiceName) {
        String requestURL = gmsBaseURL + queryCallbackByQueueURL;
        return given()
                .param("target", cbServiceName)
                .contentType(ContentType.URLENC).get(requestURL);
    }

    //this method search callbacks within time interval 15 min before till 30 days ahead
    //callbackStates could be comma separated list, e.g. "QUEUED,SCHEDULED"
    public static Response queryCallbackByStates(final String cbServiceName, final String cbStates) {
        String requestURL = gmsBaseURL + queryCallbackByQueueURL;
        int Day = 86400;
        return given()
                .param("target", cbServiceName)
                .param("end_time", getUTCTimeInFuture(30 * Day)) //without this line time interval is 15 min before till 24 hours ahead
                .param("states", cbStates).contentType(ContentType.URLENC).get(requestURL);
    }

    public static Response reportCancelled(final String callbackServiceName, final String callbackID) {
        String requestURL = gmsBaseURL + serviceCallbackURL + "/" + callbackServiceName + "/" + callbackID;
        return given().contentType(ContentType.JSON).delete(requestURL);
    }

    @SuppressWarnings("unchecked")
    public static Response exportCancelled(final String callbackReason, List<String> addParams) {
        String requestURL = gmsBaseURL + exportCancelledURL;
        JSONObject cbRequestBody = new JSONObject();
        cbRequestBody.put("callback_reason", callbackReason);
        cbRequestBody.put("exported_properties", addParams);
    	Response exportCancelled = given().body(cbRequestBody).contentType(ContentType.JSON).post(requestURL);
    	return exportCancelled;
    }
}
