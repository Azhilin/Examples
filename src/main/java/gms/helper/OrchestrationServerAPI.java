package gms.helper;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertiesFile;
import static com.genesyslab.functional.tests.gms.helper.URLsHelper.getORSBaseURL;
import static io.restassured.RestAssured.given;

/**
 * Created by bvolovyk on 02.12.2016.
 */
public class OrchestrationServerAPI {

    private static String orsBaseURL = getORSBaseURL(getPropertiesFile());
    private static String scxmlSessionURL = "/scxml/session/";
    private static String terminateURL = "/terminate";
    private static String checkQueuePositionURL = "/request/check-queue-position";

    public static void setORSBaseURL(String orsBaseURL) {
        OrchestrationServerAPI.orsBaseURL = orsBaseURL;
        System.out.printf("OrchestrationServerAPI.orsBaseURL was set to %s value.%n", orsBaseURL);
    }

    public static Response killORSSession(String orsSessionID) {
        String requestURL = orsBaseURL + scxmlSessionURL + orsSessionID + terminateURL;
        Response r = given().contentType(ContentType.JSON).post(requestURL);
        System.out.printf("Response for Terminate ORS Session by Id %s: %s%n", orsSessionID, r.then().extract().response().asString());
        return r;
    }

    public static Response checkQueuePosition(String orsSessionID) {
        String requestURL = orsBaseURL + scxmlSessionURL + orsSessionID + checkQueuePositionURL;
        return given().contentType(ContentType.JSON).post(requestURL);
    }
}
