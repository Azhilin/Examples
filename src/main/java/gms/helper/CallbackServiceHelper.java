package gms.helper;

import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

import static com.genesyslab.functional.tests.gms.helper.CallbackServicesAPI.*;
import static com.jayway.awaitility.Awaitility.given;
import static io.restassured.path.json.JsonPath.from;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

//import static io.restassured.RestAssured.given;


/**
 * Created by bvolovyk on 17.05.2017.
 */
public class CallbackServiceHelper {

    public static void cancelUnfinishedCallbacks(int custNumber) {
        String custNumb = String.valueOf(custNumber);
        Response queryResponse = queryCallbackByState(custNumb, "!COMPLETED");
        queryResponse.then().assertThat().statusCode(200);
        ArrayList<Map<String, ?>> jsonAsArrayList =
                from(queryResponse.then().extract().response().asString()).get("");
        if (jsonAsArrayList.size() > 0) {
            int i = 0;
            for (Map<String, ?> cbEntry : jsonAsArrayList) {
                i++;
                String cbId = cbEntry.get("_id").toString();
                String cbServiceName = cbEntry.get("_service_name").toString();
                Response cancellation = cancelCallback(cbServiceName, cbId);
                System.out.println("Status code for " + cbServiceName + " callback cancellation #" + i + ": " +
                        cancellation.then().extract().response().getStatusCode());
            }
        } else {
            System.out.printf("There are no callbacks in '!COMPLETED' state for customer number \"%s\" at the current moment.%n", custNumb);
        }
    }

    public static void cancelUnfinishedCallbacks(String cbServiceName) {
        Response queryResponse = queryCallbackByStates(cbServiceName, "QUEUED,SCHEDULED");
        queryResponse.then().assertThat().statusCode(200);
        ArrayList<Map<String, ?>> jsonAsArrayList =
                from(queryResponse.then().extract().response().asString()).get(cbServiceName);
        if (jsonAsArrayList.size() > 0) {
            int i = 0;
            for (Map<String, ?> cbEntry : jsonAsArrayList) {
                i++;
                String cbId = cbEntry.get("_id").toString();
                Response cancellation = cancelCallback(cbServiceName, cbId);
                System.out.println("Status code for " + cbServiceName + " callback cancellation #" + i + ": " +
                        cancellation.then().extract().response().getStatusCode());
            }
        } else {
            System.out.println("There are no " + cbServiceName +
                    " callbacks in QUEUED or SCHEDULED states at the current moment.");
        }
    }

    public static int countCallbacksByAPI(String startTime, String endTime) {
        int quantity = 0;
        Response queryResponse = queryCallbackByQueue();
        queryResponse.then().assertThat().statusCode(200);
        Map<String, ?> jsonWithCbServiceNames =
                from(queryResponse.then().extract().response().asString()).get("");
        for (String cbServiceName : jsonWithCbServiceNames.keySet()) {
            Response r = queryCallbackByQueue(cbServiceName, startTime, endTime);
            r.then().assertThat().statusCode(200);
            ArrayList<Map<String, ?>> jsonWithCallbacks =
                    from(r.then().extract().response().asString()).get(cbServiceName);
            if (jsonWithCallbacks.size() > 0) {
                quantity += jsonWithCallbacks.size();
            }
        }
        System.out.println("CALLBACK ENTRIES BY API: " + quantity);
        return quantity;
    }
          
    public static int countCallbacksByAPI() {
        int quantity = 0;
        Response queryResponse = queryCallbackByQueue();
        queryResponse.then().assertThat().statusCode(200);
        Map<String, ?> jsonWithCbServiceNames =
                from(queryResponse.then().extract().response().asString()).get("");
        for (String cbServiceName : jsonWithCbServiceNames.keySet()) {
            Response r = queryCallbackByQueue(cbServiceName);
            r.then().assertThat().statusCode(200);
            ArrayList<Map<String, ?>> jsonWithCallbacks =
                    from(r.then().extract().response().asString()).get(cbServiceName);
            if (jsonWithCallbacks.size() > 0) {
                quantity += jsonWithCallbacks.size();
            }
        }
        System.out.println("CALLBACK ENTRIES BY API: " + quantity);
        return quantity;
    }
    
    public static int countCallbacksByAPI(String startTime, final Map<String, ?> addFilter) {
        int quantity = 0;
        Response queryResponse = queryCallbackByQueue(startTime,addFilter);
        queryResponse.then().assertThat().statusCode(200);
        Map<String, ?> jsonWithCbServiceNames =
                from(queryResponse.then().extract().response().asString()).get("");
        for (String cbServiceName : jsonWithCbServiceNames.keySet()) {
            Response r = queryCallbackByQueue(cbServiceName);
            r.then().assertThat().statusCode(200);
            ArrayList<Map<String, ?>> jsonWithCallbacks =
                    from(r.then().extract().response().asString()).get(cbServiceName);
            if (jsonWithCallbacks.size() > 0) {
            	quantity ++;
        	}
        	
        	
        }
        System.out.println("CALLBACK ENTRIES BY API: " + quantity);
        return quantity;
    }
    public static int countActiveCallbacksByAPI(String cbServiceName) {
        Response queryResponse = queryCallbackByStates(cbServiceName, "QUEUED,SCHEDULED");
        queryResponse.then().assertThat().statusCode(200);
        ArrayList<Map<String, ?>> jsonAsArrayList =
                from(queryResponse.then().extract().response().asString()).get(cbServiceName);
        return jsonAsArrayList.size();
    }

    public static int countCompletedCallbacksByReason(String customerNumber, String reason) {
        int quantity = 0;
        Response r = queryCallbackByState(customerNumber,"COMPLETED");
            r.then().assertThat().statusCode(200);
            ArrayList<Map<String, ?>> jsonWithCallbacks = from(r.then().extract().response().asString()).get("");
            for (Map<String, ?> cbEntry : jsonWithCallbacks)
           {
        	   //System.out.println("callback reason " + cbEntry.get("_callback_reason").toString() +"for cb id "+ cbEntry.get("_id").toString());
            if (cbEntry.get("_callback_reason").toString().equals(reason)) { //jsonWithCallbacks.size() > 0 
            	 quantity ++;
            }
                   }
        System.out.println("There are " + quantity +" callback(s) completed with the "+reason+" reason");
        return quantity;
    }
    
    private static String parseArrayInResponse(String jsonAsString, String key) {
        ArrayList<Map<String, ?>> jsonAsArrayList = from(jsonAsString).get("");
        return jsonAsArrayList.get(0).get(key).toString();
    }

    public static String getCallbackDetailByKey(String cbId, String key) {
        System.out.printf("GETTING \"%s\" VALUE FROM ", key);
        Response queryResponse = queryCallbackByID(cbId);
        queryResponse.then().assertThat().statusCode(200);
        return parseArrayInResponse(queryResponse.then().extract().response().asString(), key);
    }

    public static int getCallbackEWT(String cbId) {
        String key = "ewt";
        System.out.printf("GETTING \"%s\" VALUE FROM ", key);
        Response queryResponse = checkQueuePosition(cbId);
        queryResponse.then().assertThat().statusCode(200);
        Double cbEWTToDouble =
                Double.parseDouble(queryResponse.then().extract().jsonPath().getString(key));
        int cbEWTToInt = cbEWTToDouble.intValue();
        System.out.println("Callback EWT parsed to int is: " + cbEWTToInt);
        return cbEWTToInt;
    }

    public static void createSpecifiedNumberOfSchCallbacks(String cbServiceName, int custNumberCounter, int secondsFromNowForDesiredTime, int cbQuantity) {
        for (int number = custNumberCounter; number < (custNumberCounter + cbQuantity); number++) {
            String customerNumber = String.valueOf(number);
            Response callback = startScheduledCallback(cbServiceName, customerNumber, secondsFromNowForDesiredTime);
            callback.then().assertThat().statusCode(200);
            System.out.printf("Callback %s to customer number %s was successfully created.%n", cbServiceName, customerNumber);
        }
    }

    public static void createSpecifiedNumberOfImmCallbacks(String cbServiceName, int custNumberCounter, int cbQuantity) {
        for (int number = custNumberCounter; number < (custNumberCounter + cbQuantity); number++) {
            String customerNumber = String.valueOf(number);
            Response callback = startImmediateCallback(cbServiceName, customerNumber);
            callback.then().assertThat().statusCode(200);
            System.out.printf("Callback %s to customer number %s was successfully created.%n", cbServiceName, customerNumber);
        }
    }

    private static Callable<String> actualCallbackDetailValue(final String cbId, final String key) {
        return new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getCallbackDetailByKey(cbId, key); // The condition supplier part
            }
        };
    }

    //TODO: implement automatic pick up and hang up on both customer and agent sides
    public static void semiAutomaticCallbackProcessing(String cbId, String cbState, long timeoutSeconds, long pollingTimeMillis) {
        if (Objects.equals(cbState, "COMPLETED")) {
            System.err.printf("NOW MANUALLY FINISH THE CALL WITHIN %s SECONDS!!!%n", timeoutSeconds);
        } else {
            System.err.printf("NOW MANUALLY PLACE THE CALL IN %s STATE WITHIN %s SECONDS!!!%n", cbState, timeoutSeconds);
        }
        given().ignoreExceptions()
                .with()
                .pollDelay(pollingTimeMillis, MILLISECONDS)
                .and()
                .pollInterval(pollingTimeMillis, MILLISECONDS)
                .await()
                .atMost(timeoutSeconds, SECONDS)
                .until(actualCallbackDetailValue(cbId, "_callback_state"), equalTo(cbState));
    }


    private static Callable<Integer> actualCallbackEWT(final String cbId) {
        return new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return getCallbackEWT(cbId); // The condition supplier part
            }
        };
    }

    public static void waitingForCallbackState(String cbId, String cbState, long timeoutSeconds, long pollingTimeMillis) {
        given().ignoreExceptions()
                .with()
                .pollDelay(pollingTimeMillis, MILLISECONDS)
                .and()
                .pollInterval(pollingTimeMillis, MILLISECONDS)
                .await()
                .atMost(timeoutSeconds, SECONDS)
                .until(actualCallbackDetailValue(cbId, "_callback_state"), equalTo(cbState));
    }

    public static void waitingForCallbackEWTValue(String cbId, int cbEWT, long timeoutSeconds, long pollingTimeMillis) {
        //wait some time for corresponding callback EWT value
        given().ignoreExceptions()
                .with()
                .pollDelay(pollingTimeMillis, MILLISECONDS)
                .and()
                .pollInterval(pollingTimeMillis, MILLISECONDS)
                .await()
                .atMost(timeoutSeconds, SECONDS)
                .until(actualCallbackEWT(cbId), lessThanOrEqualTo(cbEWT));
    }

    }
