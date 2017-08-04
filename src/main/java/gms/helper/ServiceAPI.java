package gms.helper;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

import static com.jayway.restassured.RestAssured.given;

public class ServiceAPI {
	
	 
	
	  public static Response requestInteraction(String baseUrl) {
	        String requestUrl = baseUrl + "genesys/1/service/request-interaction";
	        Response r = given().contentType(ContentType.URLENC).post(requestUrl);
	        return r;
	  }
	  
	  public static Response requestMatchInteraction(String baseUrl, String accessNumber) {
	        String requestUrl = baseUrl + "genesys/1/service/match-interaction";
	        Response r = given().contentType(ContentType.URLENC).parameter("_access_number", accessNumber).post(requestUrl);
	        return r;
	  }
	  public static Response requestAccess(String baseUrl, String serviceId, String resourceGroup) {
	        String requestUrl = baseUrl + "genesys/1/service/request-access";
	        Response r = given().contentType(ContentType.URLENC).parameter("_id", serviceId).parameter("_resource_group", resourceGroup).post(requestUrl);
	        return r;
	  }

}
