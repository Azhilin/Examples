package gms.helper;

import com.google.gson.Gson;
import io.restassured.response.Response;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertyConfiguration;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.path.json.JsonPath.from;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//import static io.restassured.RestAssured.given;
//import io.restassured.RestAssured;
//import io.restassured.http.ContentType;
//import io.restassured.response.Response;

/**
 * Class with methods to send various GET, POST, PUT requests for CS API
 * 
 * @author igabduli
 *
 */
public class ContextServices {
    private static String baseUrl = getPropertyConfiguration("base.url");
    private static String  baseUCSUrl = getPropertyConfiguration("base.ucs.url");
    private static String content_type = "application/json;charset=UTF-8";

    private static ContextServices instance = null;

    private ContextServices() {

    }

    public static ContextServices getInstance() {
        if (instance == null) {
            instance = new ContextServices();
        }
        return instance;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    // **********************************************************************************************************************
    // ************************** Query service by service_id
    // ********************************

    /**
     * GET genesys/1/cs/services/${service_id} request to query service by
     * service id
     * 
     * @param service_id
     * @return
     */

    public static Response getServiceByServiceId(String service_id) {
        Response response = get(baseUrl + "services/" + service_id);
        // String body = response.getBody().asString();
        return response;

    }
    
   
    /**
     * GET genesys/1/cs/services/${service_id} request to query service by
     * service id Multitenancy
     * @param service_id
     * @param contactCenterId
     * @return
     */
    public static Response getServiceByServiceId(String service_id, String contactCenterId) {
     //   Response response = get(baseUrl + "services/" + service_id);
        String requestUrl = baseUrl + "services/" + service_id;
        Response  r = given().contentType("application/json").header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).get(requestUrl);
        return r;

    }
    
    /**
     * GET genesys/1/cs/services/${service_id} request to query service by
     * service id Multitenancy
     * @param service_id
     * @param headerName
     * @param contactCenterId
     * @return
     */
    public static Response getServiceByServiceId(String service_id, String headerName, String contactCenterId) {
        //   Response response = get(baseUrl + "services/" + service_id);
           String requestUrl = baseUrl + "services/" + service_id;
           Response  r = given().contentType("application/json").header("Content-Type", "application/json").header(headerName, contactCenterId).get(requestUrl);
           return r;

       }
    
    /**
     * GET genesys/1/cs/services/${service_id} request to query service by
     * service_id with URL parameters
     * 
     * @param service_id
     * @param parametersMap
     * @return
     */

    public static Response getServiceByServiceId(String service_id, Map<String, String> parametersMap) {
        String requestUrl = baseUrl + "services/" + service_id;
        Response r = given().contentType(content_type).parameters(parametersMap).get(requestUrl);
        // String body = r.getBody().asString();
        // System.out.print(body);
        return r;
    }
    
   /**
    * GET genesys/1/cs/services/${service_id} request to query service by
     * service_id with URL parameters Multitenancy
    * @param service_id
    * @param parametersMap
    * @param contactCenterId
    * @return
    */
    public static Response getServiceByServiceIdMultit(String service_id, Map<String, String> parametersMap, String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id;
        Response  r = given().contentType("application/json").parameters(parametersMap).header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).get(requestUrl);
        return r;
    }
    
    /**
     * GET genesys/1/cs/services/${service_id} request to query service by
     * service_id with URL parameters
     * 
     * @param service_id
     * @param parametersMap
     * @param filterName
     * @param filterList
     * @return
     */
    public static Response getServiceByServiceId(String service_id, Map<String, String> parametersMap,
            String filterName, List<String> filterList) {
        String requestUrl = baseUrl + "services/" + service_id;
        Response r = given().contentType(content_type).parameters(parametersMap).param(filterName, filterList)
                .get(requestUrl);
        return r;
    }

    // ************************************ Query service by service_id
    // **********************************************************************************************************************

    // **********************************************************************************************************************
    // ************************** GET services on customer

    /**
     * GET /genesys/1/cs/customers/${customer_id}/services/active request to get
     * active services on customer with filter on Service_type
     * 
     * @param customer_id
     * @param service_type
     * @return
     */

    public static Response getActiveServicesOnCustomer(String customer_id, String service_type) {
        String requestUrl = baseUrl + "customers/" + customer_id + "/services/active";
        Response r = given().contentType(content_type).param("service_types", service_type).get(requestUrl);
        return r;
    }

    /**
     * GET /genesys/1/cs/customers/${customer_id}/services/active request to get
     * active services on customer with filter name and a list of Values for a
     * filter
     * 
     * @param customer_id
//     * @param parameterValues
     * @return
     */

    public static Response getActiveServicesOnCustomer(String customer_id, String filterName, List<String> filterValues) {
        String requestUrl = baseUrl + "customers/" + customer_id + "/services/active";
        Response r = given().contentType(content_type).param(filterName, filterValues).get(requestUrl);
        return r;
    }

    /**
     * GET /genesys/1/cs/customers/${customer_id}/services/active request to get
     * active services on customer with filter on Service_type and started_from,
     * started_to parameters
     * 
     * @param customer_id
     * @param service_type
     * @param started_from
     * @param started_to
     * @return
     */

    public static Response getActiveServicesOnCustomer(String customer_id, String service_type, String started_from,
            String started_to) {
        String requestUrl = baseUrl + "customers/" + customer_id + "/services/active";
        Response r = given().contentType(content_type).param("service_types", service_type)
                .param("started_from", started_from).param("started_to", started_to).get(requestUrl);
        return r;
    }
    /**
     * * GET /genesys/1/cs/customers/${customer_id}/services/active request to
     * get active services on customer with filter on started_from, started_to
     * parameters
     * 
     * @param customer_id
     * @param started_from
     * @param started_to
     * @return
     */
    public static Response getActiveServicesOnCustomer(String customer_id, String started_from, String started_to) {
        String requestUrl = baseUrl + "customers/" + customer_id + "/services/active";
        Response r = given().contentType(content_type).param("started_from", started_from)
                .param("started_to", started_to).get(requestUrl);
        return r;
    }

    /**
     * GET /genesys/1/cs/customers/${customer_id}/services/active request to get
     * active services on customer with no filter
     * 
     * @param customer_id
//     * @param service_type
     * @return
     */

    public static Response getActiveServicesOnCustomer(String customer_id) {
        String requestUrl = baseUrl + "customers/" + customer_id + "/services/active";
        Response r = given().contentType(content_type).get(requestUrl);
        // String body = r.getBody().asString();
        // System.out.print(body);
        return r;
    }

    /**
     * GET /genesys/1/cs/customers/${customer_id}/services/active request to get
     * active services on customer with collection of URI parameters
     * 
     * @param customer_id
     * @param parametersMap
     * @return
     */

    public static Response getActiveServicesOnCustomer(String customer_id, Map<String, String> parametersMap) {
        String requestUrl = baseUrl + "customers/" + customer_id + "/services/active";
        Response r = given().contentType(content_type).parameters(parametersMap).get(requestUrl);
        // String body = r.getBody().asString();
        // System.out.print(body);
        return r;
    }
    
   /**
    *  GET /genesys/1/cs/customers/${customer_id}/services/active request to get
     * active services on customer Multitenancy
    * @param customer_id
    * @param contactCenterId
    * @return
    */
    public static Response getActiveServicesOnCustomerMultit(String customer_id, String contactCenterId) {
        String requestUrl = baseUrl + "customers/" + customer_id + "/services/active";
        Response  r = given().contentType("application/json").header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).get(requestUrl);
        return r;
    }
    
    /**
     *  GET /genesys/1/cs/customers/${customer_id}/services/active request to get
     * active services on customer Multitenancy
     * @param customer_id
     * @param headerName
     * @param contactCenterId
     * @return
     */
    public static Response getActiveServicesOnCustomerMultit(String customer_id, String headerName, String contactCenterId) {
        String requestUrl = baseUrl + "customers/" + customer_id + "/services/active";
        Response  r = given().contentType("application/json").header("Content-Type", "application/json").header(headerName, contactCenterId).get(requestUrl);
        return r;
    }
   
    /**
     * 
     * @param customer_id
     * @param service_type
     * @param headerName
     * @param contactCenterId
     * @return
     */
    public static Response getActiveServicesOnCustomerMultit(String customer_id,String service_type, String headerName, String contactCenterId) {
        String requestUrl = baseUrl + "customers/" + customer_id + "/services/active";
        Response  r = given().contentType("application/json").param("service_types", service_type).header("Content-Type", "application/json").header(headerName, contactCenterId).get(requestUrl);
        return r;
    }
    // *********************************************************************************************************************
    /**
     * GET /genesys/1/cs/customers/${customer_id}/services/completed request to
     * get completed services on customer with filter on Service_type
     * 
     * @param customer_id
     * @param service_type
     * @return
     */

    public static Response getCompletedServicesOnCustomer(String customer_id, String service_type) {
        String requestUrl = baseUrl + "customers/" + customer_id + "/services/completed";
        Response r = given().contentType(content_type).param("service_types", service_type).get(requestUrl);
        return r;
    }

    /**
     * GET /genesys/1/cs/customers/${customer_id}/services/completed request to
     * get completed services on customer with filter name and filter values
     * list
     * 
     * @param customer_id
//     * @param parameterValues
     * @return
     */

    public static Response getCompletedServicesOnCustomer(String customer_id, String filterName,
            List<String> filterValues) {
        String requestUrl = baseUrl + "customers/" + customer_id + "/services/completed";
        Response r = given().contentType(content_type).param(filterName, filterValues).get(requestUrl);
        return r;
    }

    /**
     * GET /genesys/1/cs/customers/${customer_id}/services/completed request to
     * get completed services on customer with filter on Service_type and
     * started_from, started_to parameters
     * 
     * @param customer_id
     * @param service_type
     * @param started_from
     * @param started_to
     * @return
     */

    public static Response getCompletedServicesOnCustomer(String customer_id, String service_type, String started_from,
            String started_to) {
        String requestUrl = baseUrl + "customers/" + customer_id + "/services/completed";
        Response r = given().contentType(content_type).param("service_types", service_type)
                .param("started_from", started_from).param("started_to", started_to).get(requestUrl);
        return r;
    }
    /**
     * * GET /genesys/1/cs/customers/${customer_id}/services/completed request
     * to get completed services on customer with filter on started_from,
     * started_to parameters
     * 
     * @param customer_id
     * @param started_from
     * @param started_to
     * @return
     */
    public static Response getCompletedServicesOnCustomer(String customer_id, String started_from, String started_to) {
        String requestUrl = baseUrl + "customers/" + customer_id + "/services/completed";
        Response r = given().contentType(content_type).param("started_from", started_from)
                .param("started_to", started_to).get(requestUrl);
        return r;
    }

    /**
     * GET /genesys/1/cs/customers/${customer_id}/services/completed request to
     * get completed services on customer with no filter
     * 
     * @param customer_id
//     * @param service_type
     * @return
     */

    public static Response getCompletedServicesOnCustomer(String customer_id) {
        String requestUrl = baseUrl + "customers/" + customer_id + "/services/completed";
        Response r = given().contentType(content_type).get(requestUrl);
        // String body = r.getBody().asString();
        // System.out.print(body);
        return r;
    }

    /**
     * GET /genesys/1/cs/customers/${customer_id}/services/completed request to
     * get completed services on customer with collection of URI parameters
     * 
     * @param customer_id
     * @param parametersMap
     * @return
     */

    public static Response getCompletedServicesOnCustomer(String customer_id, Map<String, String> parametersMap) {
        String requestUrl = baseUrl + "customers/" + customer_id + "/services/completed";
        Response r = given().contentType(content_type).parameters(parametersMap).get(requestUrl);
        // String body = r.getBody().asString();
        // System.out.print(body);
        return r;
    }
    
    /**
     * GET /genesys/1/cs/customers/${customer_id}/services/completed request to
     * get completed services on customer Multitenancy
     * @param customer_id
     * @param contactCenterId
     * @return
     */
    public static Response getCompletedServicesOnCustomerMultit(String customer_id,String contactCenterId) {
        String requestUrl = baseUrl + "customers/" + customer_id + "/services/completed";     
        Response  r = given().contentType("application/json").header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).get(requestUrl);
            return r;
    }
    
    /**
     *  GET /genesys/1/cs/customers/${customer_id}/services/completed request to
     * get completed services on customer Multitenancy
     * @param customer_id
     * @param headerName
     * @param contactCenterId
     * @return
     */
    public static Response getCompletedServicesOnCustomerMultit(String customer_id,String headerName, String contactCenterId) {
        String requestUrl = baseUrl + "customers/" + customer_id + "/services/completed";     
        Response  r = given().contentType("application/json").header("Content-Type", "application/json").header(headerName, contactCenterId).get(requestUrl);
            return r;
    }
    
    /**
     * 
     * @param customer_id
     * @param service_type
     * @param headerName
     * @param contactCenterId
     * @return
     */
    public static Response getCompletedServicesOnCustomerMultit(String customer_id, String service_type, String headerName, String contactCenterId) {
        String requestUrl = baseUrl + "customers/" + customer_id + "/services/completed";     
        Response  r = given().contentType("application/json").param("service_types", service_type).header("Content-Type", "application/json").header(headerName, contactCenterId).get(requestUrl);
            return r;
    }
    // *********************************************************************************************************************
    /**
     * GET /genesys/1/cs/customers/${customer_id}/services request to get all
     * services on customer with filter on Service_type
     * 
     * @param customer_id
     * @param service_type
     * @return
     */

    public static Response getAllServicesOnCustomer(String customer_id, String service_type) {
        String requestUrl = baseUrl + "customers/" + customer_id + "/services";
        Response r = given().contentType(content_type).param("service_types", service_type).get(requestUrl);
        return r;
    }

    /**
     * GET /genesys/1/cs/customers/${customer_id}/services request to get all
     * services on customer with filter name and collection of values
     * 
     * @param customer_id
//     * @param parameterValues
     * @return
     */

    public static Response getAllServicesOnCustomer(String customer_id, String filterName, List<String> filterValues) {
        String requestUrl = baseUrl + "customers/" + customer_id + "/services";
        Response r = given().contentType(content_type).param(filterName, filterValues).get(requestUrl);
        return r;
    }
    /**
     * GET /genesys/1/cs/customers/${customer_id}/services request to get all
     * services on customer with filter on Service_type and started_from,
     * started_to parameters
     * 
     * @param customer_id
     * @param service_type
     * @param started_from
     * @param started_to
     * @return
     */

    public static Response getAllServicesOnCustomer(String customer_id, String service_type, String started_from,
            String started_to) {
        String requestUrl = baseUrl + "customers/" + customer_id + "/services";
        Response r = given().contentType(content_type).param("service_types", service_type)
                .param("started_from", started_from).param("started_to", started_to).get(requestUrl);
        return r;
    }
    /**
     * * GET /genesys/1/cs/customers/${customer_id}/services request to get all
     * services on customer with filter on started_from, started_to parameters
     * 
     * @param customer_id
     * @param started_from
     * @param started_to
     * @return
     */
    public static Response getAllServicesOnCustomer(String customer_id, String started_from, String started_to) {
        String requestUrl = baseUrl + "customers/" + customer_id + "/services";
        Response r = given().contentType(content_type).param("started_from", started_from)
                .param("started_to", started_to).get(requestUrl);
        return r;
    }

    /**
     * GET /genesys/1/cs/customers/${customer_id}/services request to get active
     * services on customer with no filter
     * 
     * @param customer_id
//     * @param service_type
     * @return
     */

    public static Response getAllServicesOnCustomer(String customer_id) {
        String requestUrl = baseUrl + "customers/" + customer_id + "/services";
        Response r = given().contentType(content_type).get(requestUrl);
        // String body = r.getBody().asString();
        // System.out.print(body);
        return r;
    }
    /**
     * GET /genesys/1/cs/customers/${customer_id}/services request to get all
     * services on customer with URL params
     * 
     * @param customer_id
     * @param parametersMap
     * @return
     */

    public static Response getAllServicesOnCustomer(String customer_id, Map<String, String> parametersMap) {
        String requestUrl = baseUrl + "customers/" + customer_id + "/services";
        Response r = given().contentType(content_type).parameters(parametersMap).get(requestUrl);
        // String body = r.getBody().asString();
        // System.out.print(body);
        return r;
    }
    
    /**
     * GET /genesys/1/cs/customers/${customer_id}/services request to get all
     * services on customer Multitenancy
     * @param customer_id
     * @param contactCenterId
     * @return
     */
    public static Response getAllServicesOnCustomerMultit(String customer_id, String contactCenterId) {
        String requestUrl = baseUrl + "customers/" + customer_id + "/services";
        Response  r = given().contentType("application/json").header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).get(requestUrl);
        return r;
    }
    
    /**
     * GET /genesys/1/cs/customers/${customer_id}/services request to get all
     * services on customer Multitenancy
     * @param customer_id
     * @param headerName
     * @param contactCenterId
     * @return
     */
    public static Response getAllServicesOnCustomerMultit(String customer_id,String headerName, String contactCenterId) {
        String requestUrl = baseUrl + "customers/" + customer_id + "/services";
        Response  r = given().contentType("application/json").header("Content-Type", "application/json").header(headerName, contactCenterId).get(requestUrl);
        return r;
    }
    
    /**
     * 
     * @param customer_id
     * @param service_type
     * @param headerName
     * @param contactCenterId
     * @return
     */
    public static Response getAllServicesOnCustomerMultit(String customer_id,String service_type,String headerName, String contactCenterId) {
        String requestUrl = baseUrl + "customers/" + customer_id + "/services";
        Response  r = given().contentType("application/json").param("service_types", service_type).header("Content-Type", "application/json").header(headerName, contactCenterId).get(requestUrl);
        return r;
    }
    
    // *********************************
    // ************************** GET services on customer
    // ********************************
    // ********************************************************************************************************************

    // **********************************************************************************************************************
    // ************************** GET anonymous services

    /**
     * GET /genesys/1/cs/services/anonymous/${contact_key} request to get all
     * anonymous services by contact_key
     * 
     * @param contact_key
     * @return
     */
    public static Response getAnonymousServicesByContact_key(String contact_key) {
        String requestUrl = baseUrl + "services/anonymous/" + contact_key;
        Response r = given().contentType(content_type).get(requestUrl);
        // String body = r.getBody().asString();
        // System.out.print(body);
        return r;
    }
    /**
     * GET /genesys/1/cs/services/anonymous/${contact_key} request to get all
     * anonymous services by contact_key with parmeters started_from and to
     * 
     * @param contact_key
     * @param started_from
     * @param started_to
     * @return
     */
    public static Response getAnonymousServicesByContact_key(String contact_key, String started_from, String started_to) {
        String requestUrl = baseUrl + "services/anonymous/" + contact_key;
        Response r = given().contentType(content_type).param("started_from", started_from)
                .param("started_to", started_to).get(requestUrl);
        // String body = r.getBody().asString();
        // System.out.print(body);
        return r;
    }

    /**
     * GET /genesys/1/cs/services/anonymous/${contact_key} request to get all
     * anonymous services by contact_key with parmeters started_from and to and
     * service_type
     * 
     * @param contact_key
     * @param started_from
     * @param started_to
     * @return
     */
    public static Response getAnonymousServicesByContact_key(String contact_key, String service_type,
            String started_from, String started_to) {
        String requestUrl = baseUrl + "services/anonymous/" + contact_key;
        Response r = given().contentType(content_type).param("service_types", service_type)
                .param("started_from", started_from).param("started_to", started_to).get(requestUrl);
        // String body = r.getBody().asString();
        // System.out.print(body);
        return r;
    }

    /**
     * GET /genesys/1/cs/services/anonymous/${contact_key} request to get all
     * anonymous services by contact_key with parameter service_type
     * 
     * @param contact_key
     * @param service_type
     * @return
     */
    public static Response getAnonymousServicesByContact_key(String contact_key, String service_type) {
        String requestUrl = baseUrl + "services/anonymous/" + contact_key;
        Response r = given().contentType(content_type).param("service_types", service_type).get(requestUrl);
        return r;
    }
    
    /**
     * GET /genesys/1/cs/services/anonymous/${contact_key} request to get all
     * anonymous services by contact_key Multitenancy
     * @param contact_key
//     * @param service_type
     * @param contactCenterId
     * @return
     */
    public static Response getAnonymousServicesByContact_keyMultit(String contact_key, String contactCenterId) {
        String requestUrl = baseUrl + "services/anonymous/" + contact_key;      
        Response  r = given().contentType("application/json").header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).get(requestUrl);
        return r;
    }
    
    /**
     * GET /genesys/1/cs/services/anonymous/${contact_key} request to get all
     * anonymous services by contact_key Multitenancy
     * @param contact_key
     * @param headerName
     * @param contactCenterId
     * @return
     */
    public static Response getAnonymousServicesByContact_keyMultit(String contact_key, String headerName, String contactCenterId) {
        String requestUrl = baseUrl + "services/anonymous/" + contact_key;      
        Response  r = given().contentType("application/json").header("Content-Type", "application/json").header(headerName, contactCenterId).get(requestUrl);
        return r;
    }
    
    /**
     * 
     * @param contact_key
     * @param service_type
     * @param headerName
     * @param contactCenterId
     * @return
     */
    public static Response getAnonymousServicesByContact_keyMultit(String contact_key,String service_type, String headerName, String contactCenterId) {
        String requestUrl = baseUrl + "services/anonymous/" + contact_key;      
        Response  r = given().contentType("application/json").params("service_types", service_type).header("Content-Type", "application/json").header(headerName, contactCenterId).get(requestUrl);
        return r;
    }

    /**
     * GET /genesys/1/cs/services/anonymous/${contact_key} request to get all
     * anonymous services by contact_key with filter name and filter values list
     * 
     * @param contact_key
//     * @param parameterValues
     * @return
     */
    public static Response getAnonymousServicesByContact_key(String contact_key, String filterName,
            List<String> filterValues) {
        String requestUrl = baseUrl + "services/anonymous/" + contact_key;
        Response r = given().contentType(content_type).param(filterName, filterValues).get(requestUrl);
        return r;
    }

    /**
     * GET /genesys/1/cs/services/anonymous/${contact_key} request to get all
     * anonymous services by contact_key with collection of filters
     * 
     * @param contact_key
     * @param parametersMap
     * @return
     */
    public static Response getAnonymousServicesByContact_key(String contact_key, Map<String, String> parametersMap) {
        String requestUrl = baseUrl + "services/anonymous/" + contact_key;
        Response r = given().contentType(content_type).parameters(parametersMap).get(requestUrl);
        // String body = r.getBody().asString();
        // System.out.print(body);
        return r;
    }

    // ******************************************************************************************************************

    /**
     * GET /genesys/1/cs/services/anonymous/${contact_key}/completed request to
     * get completed anonymous services by contact_key
     * 
     * @param contact_key
     * @return
     */
    public static Response getCompletedAnonymousServicesByContact_key(String contact_key) {
        String requestUrl = baseUrl + "services/anonymous/" + contact_key + "/completed";
        Response r = given().contentType(content_type).get(requestUrl);
        // String body = r.getBody().asString();
        // System.out.print(body);
        return r;
    }
    /**
     * GET /genesys/1/cs/services/anonymous/${contact_key}/completed request to
     * get completed anonymous services by contact_key with parmeters
     * started_from and to
     * 
     * @param contact_key
     * @param started_from
     * @param started_to
     * @return
     */
    public static Response getCompletedAnonymousServicesByContact_key(String contact_key, String started_from,
            String started_to) {
        String requestUrl = baseUrl + "services/anonymous/" + contact_key + "/completed";
        Response r = given().contentType(content_type).param("started_from", started_from)
                .param("started_to", started_to).get(requestUrl);
        // String body = r.getBody().asString();
        // System.out.print(body);
        return r;
    }

    /**
     * GET /genesys/1/cs/services/anonymous/${contact_key}/completed request to
     * get completed anonymous services by contact_key with parmeters
     * started_from and to and service_type
     * 
     * @param contact_key
     * @param started_from
     * @param started_to
     * @return
     */
    public static Response getCompletedAnonymousServicesByContact_key(String contact_key, String service_type,
            String started_from, String started_to) {
        String requestUrl = baseUrl + "services/anonymous/" + contact_key + "/completed";
        Response r = given().contentType(content_type).param("service_types", service_type)
                .param("started_from", started_from).param("started_to", started_to).get(requestUrl);
        // String body = r.getBody().asString();
        // System.out.print(body);
        return r;
    }

    /**
     * GET /genesys/1/cs/services/anonymous/${contact_key}/completed request to
     * get completed anonymous services by contact_key with parameter
     * service_type
     * 
     * @param contact_key
     * @param service_type
     * @return
     */
    public static Response getCompletedAnonymousServicesByContact_key(String contact_key, String service_type) {
        String requestUrl = baseUrl + "services/anonymous/" + contact_key + "/completed";
        Response r = given().contentType(content_type).param("service_types", service_type).get(requestUrl);
        return r;
    }

    /**
     * GET /genesys/1/cs/services/anonymous/${contact_key}/completed request to
     * get completed anonymous services by contact_key with collection of
     * service types
     * 
     * @param contact_key
//     * @param parameterValues
     * @return
     */
    public static Response getCompletedAnonymousServicesByContact_key(String contact_key, String filterName,
            List<String> filterValues) {
        String requestUrl = baseUrl + "services/anonymous/" + contact_key + "/completed";
        Response r = given().contentType(content_type).param(filterName, filterValues).get(requestUrl);
        return r;
    }

    /**
     * GET /genesys/1/cs/services/anonymous/${contact_key}/completed request to
     * get completed anonymous services by contact_key with collection of
     * filters
     * 
     * @param contact_key
     * @param parametersMap
     * @return
     */
    public static Response getCompletedAnonymousServicesByContact_key(String contact_key,
            Map<String, String> parametersMap) {
        String requestUrl = baseUrl + "services/anonymous/" + contact_key + "/completed";
        Response r = given().contentType(content_type).parameters(parametersMap).get(requestUrl);
        // String body = r.getBody().asString();
        // System.out.print(body);
        return r;
    }
    
    /**
     * GET /genesys/1/cs/services/anonymous/${contact_key}/completed request to
     * get completed anonymous services by contact_key Multitenancy
     * @param contact_key
     * @param contactCenterId
     * @return
     */
    public static Response getCompletedAnonymousServicesByContact_keyMultit(String contact_key,String contactCenterId) {
        String requestUrl = baseUrl + "services/anonymous/" + contact_key + "/completed";       
        Response  r = given().contentType("application/json").header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).get(requestUrl);
          return r;
    }
    
    /**
     * GET /genesys/1/cs/services/anonymous/${contact_key}/completed request to
     * get completed anonymous services by contact_key Multitenancy
     * @param contact_key
     * @param headerName
     * @param contactCenterId
     * @return
     */
    public static Response getCompletedAnonymousServicesByContact_keyMultit(String contact_key,String headerName, String contactCenterId) {
        String requestUrl = baseUrl + "services/anonymous/" + contact_key + "/completed";       
        Response  r = given().contentType("application/json").header("Content-Type", "application/json").header(headerName, contactCenterId).get(requestUrl);
          return r;
    }
    
    public static Response getCompletedAnonymousServicesByContact_keyMultit(String contact_key,String service_type,String headerName, String contactCenterId) {
        String requestUrl = baseUrl + "services/anonymous/" + contact_key + "/completed";       
        Response  r = given().contentType("application/json").params("service_types",service_type).header("Content-Type", "application/json").header(headerName, contactCenterId).get(requestUrl);
          return r;
    }

    // **************************************************

    /**
     * GET /genesys/1/cs/services/anonymous/${contact_key}/active request to get
     * active anonymous services by contact_key
     * 
     * @param contact_key
     * @return
     */
    public static Response getActiveAnonymousServicesByContact_key(String contact_key) {
        String requestUrl = baseUrl + "services/anonymous/" + contact_key + "/active";
        Response r = given().contentType(content_type).get(requestUrl);
        // String body = r.getBody().asString();
        // System.out.print(body);
        return r;
    }
    /**
     * GET /genesys/1/cs/services/anonymous/${contact_key}/active request to get
     * active anonymous services by contact_key with parmeters started_from and
     * to
     * 
     * @param contact_key
     * @param started_from
     * @param started_to
     * @return
     */
    public static Response getActiveAnonymousServicesByContact_key(String contact_key, String started_from,
            String started_to) {
        String requestUrl = baseUrl + "services/anonymous/" + contact_key + "/active";
        Response r = given().contentType(content_type).param("started_from", started_from)
                .param("started_to", started_to).get(requestUrl);
        // String body = r.getBody().asString();
        // System.out.print(body);
        return r;
    }

    /**
     * GET /genesys/1/cs/services/anonymous/${contact_key}/active request to get
     * active anonymous services by contact_key with parmeters started_from and
     * to and service_type
     * 
     * @param contact_key
     * @param started_from
     * @param started_to
     * @return
     */
    public static Response getActiveAnonymousServicesByContact_key(String contact_key, String service_type,
            String started_from, String started_to) {
        String requestUrl = baseUrl + "services/anonymous/" + contact_key + "/active";
        Response r = given().contentType(content_type).param("service_types", service_type)
                .param("started_from", started_from).param("started_to", started_to).get(requestUrl);
        // String body = r.getBody().asString();
        // System.out.print(body);
        return r;
    }

    /**
     * GET /genesys/1/cs/services/anonymous/${contact_key}/active request to get
     * active anonymous services by contact_key with parameter service_type
     * 
     * @param contact_key
     * @param service_type
     * @return
     */
    public static Response getActiveAnonymousServicesByContact_key(String contact_key, String service_type) {
        String requestUrl = baseUrl + "services/anonymous/" + contact_key + "/active";
        Response r = given().contentType(content_type).param("service_types", service_type).get(requestUrl);
        return r;
    }

    /**
     * GET /genesys/1/cs/services/anonymous/${contact_key}/active request to get
     * active anonymous services by contact_key with collection of service types
     * 
     * @param contact_key
//     * @param parameterValues
     * @return
     */
    public static Response getActiveAnonymousServicesByContact_key(String contact_key, String filterName,
            List<String> filterValues) {
        String requestUrl = baseUrl + "services/anonymous/" + contact_key + "/active";
        Response r = given().contentType(content_type).param(filterName, filterValues).get(requestUrl);
        return r;
    }

    /**
     * GET /genesys/1/cs/services/anonymous/${contact_key}/active request to get
     * active anonymous services by contact_key with collection of filters
     * 
     * @param contact_key
     * @param parametersMap
     * @return
     */
    public static Response getActiveAnonymousServicesByContact_key(String contact_key, Map<String, String> parametersMap) {
        String requestUrl = baseUrl + "services/anonymous/" + contact_key + "/active";
        Response r = given().contentType(content_type).parameters(parametersMap).get(requestUrl);
        // String body = r.getBody().asString();
        // System.out.print(body);
        return r;
    }
    
    /**
     *  GET /genesys/1/cs/services/anonymous/${contact_key}/active request to get
     * active anonymous services by contact_ke Multitenancy
     * @param contact_key
     * @param contactCenterId
     * @return
     */
    
    public static Response getActiveAnonymousServicesByContact_keyMultit(String contact_key, String contactCenterId) {
        String requestUrl = baseUrl + "services/anonymous/" + contact_key + "/active";
        Response  r = given().contentType("application/json").header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).get(requestUrl);
        return r;
    }
    
    /**
     * GET /genesys/1/cs/services/anonymous/${contact_key}/active request to get
     * active anonymous services by contact_ke Multitenancy
     * @param contact_key
     * @param headerName
     * @param contactCenterId
     * @return
     */
    public static Response getActiveAnonymousServicesByContact_keyMultit(String contact_key, String headerName, String contactCenterId) {
        String requestUrl = baseUrl + "services/anonymous/" + contact_key + "/active";
        Response  r = given().contentType("application/json").header("Content-Type", "application/json").header(headerName, contactCenterId).get(requestUrl);
        return r;
    }
   
    /**
     * 
     * @param contact_key
     * @param service_type
     * @param headerName
     * @param contactCenterId
     * @return
     */
    public static Response getActiveAnonymousServicesByContact_keyMultit(String contact_key, String service_type,String headerName, String contactCenterId) {
        String requestUrl = baseUrl + "services/anonymous/" + contact_key + "/active";
        Response  r = given().contentType("application/json").param("service_types", service_type).header("Content-Type", "application/json").header(headerName, contactCenterId).get(requestUrl);
        return r;
    }

    // **********************************
    // ************************** GET anonymous services
    // ********************************
    // ************************************************************************************************************

    /**
     * POST genesys/1/cs/services/start request to start a service
     * 
     * @param requestBody
     * @return
     */

    public static Response startService(String requestBody) {
//   BV     String requestUrl = baseUrl + "services/start";
        String requestUrl = "http://135.17.36.157:8080/genesys/1/datadepot/1/configuration"; //BV added this on 11/10/2016
        Response r = given().contentType(content_type).body(requestBody).post(requestUrl);
        // String service_id = from(r.asString()).get("service_id").toString();
        // assertNotNull("Service_id is null", service_id);
        return r;
    }

    public static Response startServiceWithBasicAuthentication(String username, String password, String requestBody) {
        String requestUrl = "http://135.17.36.157:8080/genesys/1/datadepot/1/configuration"; //BV added this on 11/10/2016
        Response r = given().auth().preemptive().basic(username, password).body(requestBody).post(requestUrl);
        return r;
    }
    
    /**
     * POST genesys/1/cs/services/start request to start a service on multitenant env
     * @param requestBody
     * @param contactCenterId dbid or name of the tenant
     * @return
     */
    public static Response startService(String requestBody,String contactCenterId) {
        String requestUrl = baseUrl + "services/start"; 
     //         RestAssured.requestContentType(ContentType.JSON);
      //        RestAssured.responseContentType(ContentType.JSON);
       
       // Response r = given().header("Content-Type","application/json").header("ContactCenterId",contactCenterId).post(requestUrl);
     //   Response r = given().header("Content-Type",content_type).header("ContactCenterId",contactCenterId).post(requestUrl);
      //  Response r = given().header("Content-Type","application/json","ContactCenterId",contactCenterId).post(requestUrl);
        Response response = given().body(requestBody).contentType("application/json").header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).post(requestUrl);
        return response;
    
    }
    
    /**
     *  POST genesys/1/cs/services/start request to start a service on multitenant env
     * @param requestBody
//     * @param headers
     * @return
     */
    public static Response startService(String requestBody,String headerName, String contactCenterId ) {
        String requestUrl = baseUrl + "services/start"; 
    
        Response response = given().body(requestBody).contentType("application/json").header("Content-Type", "application/json").header(headerName, contactCenterId).post(requestUrl);
        return response;
    
    }
    
    
    /**
     * POST /services/${service_id}/end request to end service
     * 
     * @param service_id
     * @param requestBody
     * @return
     */

    public static Response completeService(String service_id, String requestBody) {
        String requestUrl = baseUrl + "services/" + service_id + "/end";
        Response r = given().contentType(content_type).body(requestBody).post(requestUrl);
        // String service_id = from(r.asString()).get("service_id").toString();
        // assertNotNull("Service_id is null", service_id);
        return r;
    }
    
   
     
   /**
    * POST /services/${service_id}/end request to end service for multitenancy install
    * @param service_id
    * @param requestBody
    * @param contactCenterId
    * @return
    */
    public static Response completeService(String service_id, String requestBody, String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/end";
        Response  r = given().body(requestBody).contentType("application/json").header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).post(requestUrl);
        return r;
    }
    
    
   /**
    * POST /services/${service_id}/end request to end service for multitenancy install
    * @param service_id
    * @param requestBody
    * @param headerName
    * @param contactCenterId
    * @return
    */
    public static Response completeService(String service_id, String requestBody, String headerName,String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/end";
        Response  r = given().body(requestBody).contentType("application/json").header("Content-Type", "application/json").header(headerName, contactCenterId).post(requestUrl);
        return r;
    }
    
    /**
     * POST /customers/${customer_id}/services/${service_id} request to
     * associate service with the customer
     * 
     * @param requestBody
     * @param customer_id
     * @param service_id
     * @return
     */
    public static Response associateServiceWithCustomer(String requestBody, String customer_id, String service_id) {
        String requestUrl = "";
        Response r = null;
        try {
            requestUrl = baseUrl + "customers/" + customer_id + "/services/" + service_id;
            r = given().contentType(content_type).body(requestBody).post(requestUrl);
            return r;
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
            return null;
        }

    }
    
    /**
     *  POST /customers/${customer_id}/services/${service_id} request to
     * associate service with the customer on Multitenant env
     * @param requestBody
     * @param customer_id
     * @param service_id
     * @param contactCenterId
     * @return
     */
    public static Response associateServiceWithCustomer(String requestBody, String customer_id, String service_id, String contactCenterId) {
        String requestUrl = "";
        Response r = null;
        try {
            requestUrl = baseUrl + "customers/" + customer_id + "/services/" + service_id;          
            r = given().body(requestBody).contentType("application/json").header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).post(requestUrl);
            return r;
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
            return null;
        }

    }
    
    /**
     *  POST /customers/${customer_id}/services/${service_id} request to
     * associate service with the customer on Multitenant env
     * @param requestBody
     * @param customer_id
     * @param service_id
     * @param contactCenterId
     * @return
     */
    public static Response associateServiceWithCustomer(String requestBody, String customer_id, String service_id, String headerName,String contactCenterId) {
        String requestUrl = "";
        Response r = null;
        try {
            requestUrl = baseUrl + "customers/" + customer_id + "/services/" + service_id;          
            r = given().body(requestBody).contentType("application/json").header("Content-Type", "application/json").header(headerName, contactCenterId).post(requestUrl);
            return r;
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
            return null;
        }

    }

    /**
     * PUT /services/${service_id}/extensions/${ext_name} Updates service
     * extension value
     * 
     * @param service_id
     * @param extensionName
     * @param requestBody
     * @return
     */
    public static Response updateServiceExtension(String service_id, String extensionName, String requestBody) {
        String requestUrl = baseUrl + "services/" + service_id + "/extensions/" + extensionName;
        Response r = given().contentType(content_type).body(requestBody).put(requestUrl);
        return r;
    }
    
    /**
     *  PUT /services/${service_id}/extensions/${ext_name} Updates service
     * extension value Multitenancy
     * @param service_id
     * @param extensionName
     * @param requestBody
     * @param contactCenterId
     * @return
     */
    public static Response updateServiceExtensionMultit(String service_id, String extensionName, String requestBody, String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/extensions/" + extensionName;           
        Response  r = given().contentType("application/json").body(requestBody).header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).put(requestUrl);
        return r;
    }
    
    /**
     * PUT /services/${service_id}/extensions/${ext_name} Updates service
     * extension value Multitenancy
     * @param service_id
     * @param extensionName
     * @param requestBody
     * @param headerName
     * @param contactCenterId
     * @return
     */
    public static Response updateServiceExtensionMultit(String service_id, String extensionName, String requestBody, String headerName, String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/extensions/" + extensionName;           
        Response  r = given().contentType("application/json").body(requestBody).header("Content-Type", "application/json").header(headerName, contactCenterId).put(requestUrl);
        return r;
    }
    
    public static Response deleteService(String service_id) {
        String requestUrl = baseUrl + "services/" + service_id;
      //  content_type="application/json;charset=UTF-8";
  //      RestAssured.requestContentType(ContentType.JSON);
  //      RestAssured.responseContentType(ContentType.JSON);
        
    // Response r = given().contentType("application/json;charset=UTF-8").delete(requestUrl);
        Response r = given().header("Content-Type","application/json").delete(requestUrl);
      
        return r;
    }
    
    
    public static Response deleteServiceMultit(String service_id, String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id;  
        Response  r = given().contentType("application/json").header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).delete(requestUrl);
        return r;
    }
  

    // **************************************************************************************************************************************************
    // ****************State

    /**
     * POST /services/${service_id}/states/start request starts state on service
     * 
     * @param requestBody
     * @param service_id
     * @return
     */
    public static Response startState(String requestBody, String service_id) {
        String requestUrl = baseUrl + "services/" + service_id + "/states/start";
        Response r = given().contentType(content_type).body(requestBody).post(requestUrl);
        return r;
    }
    
    /**
     * POST /services/${service_id}/states/start request starts state on service Multitenancy
     * @param requestBody
     * @param service_id
     * @param contactCenterId
     * @return
     */
    public static Response startStateMultit(String requestBody, String service_id, String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/states/start";    
        Response response = given().body(requestBody).contentType("application/json").
        		header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).post(requestUrl);
        return response;
        
    }
    
    /**
     * POST /services/${service_id}/states/start request starts state on service Multitenancy
     * @param requestBody
     * @param service_id
     * @param headerName
     * @param contactCenterId
     * @return
     */
    public static Response startStateMultit(String requestBody, String service_id, String headerName,String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/states/start";    
        Response response = given().body(requestBody).contentType("application/json").
        		header("Content-Type", "application/json").header(headerName, contactCenterId).post(requestUrl);
        return response;
        
    }

    /**
     * 
     * POST /services/${service_id}/states/${state_id}/end request to completed
     * a state
     * 
     * @param requestBody
     * @param service_id
     * @param state_id
     * @return
     */
    public static Response completeState(String requestBody, String service_id, String state_id) {
        String requestUrl = baseUrl + "services/" + service_id + "/states/" + state_id + "/end";
        Response r = given().contentType(content_type).body(requestBody).post(requestUrl);
        return r;
    }
    
    /**
     * POST /services/${service_id}/states/${state_id}/end request to completed
     * a state Multitenancy
     * @param requestBody
     * @param service_id
     * @param state_id
     * @param contactCenterId
     * @return
     */
    public static Response completeStateMultit(String requestBody, String service_id, String state_id, String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/states/" + state_id + "/end";       
        Response response = given().body(requestBody).contentType("application/json").
        		header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).post(requestUrl);
        return response;
    }
    
    /**
     * POST /services/${service_id}/states/${state_id}/end request to completed
     * a state Multitenancy
     * @param requestBody
     * @param service_id
     * @param state_id
     * @param headerName
     * @param contactCenterId
     * @return
     */
    public static Response completeStateMultit(String requestBody, String service_id, String state_id, String headerName, String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/states/" + state_id + "/end";       
        Response response = given().body(requestBody).contentType("application/json").
        		header("Content-Type", "application/json").header(headerName, contactCenterId).post(requestUrl);
        return response;
    }

    /**
     * POST /services/${service_id}/states/transition
     * 
     * @param requestBody
     * @param service_id
     * @return
     */
    public static Response performStateTransition(String requestBody, String service_id) {
        String requestUrl = baseUrl + "services/" + service_id + "/states/transition";
        Response r = given().contentType(content_type).body(requestBody).post(requestUrl);
        return r;
    }
    
    /**
     * POST /services/${service_id}/states/transition Multitenancy
     * @param requestBody
     * @param service_id
     * @param contactCenterId
     * @return
     */
    public static Response performStateTransitionMultit(String requestBody, String service_id, String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/states/transition";
        Response response = given().body(requestBody).contentType("application/json").
        		header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).post(requestUrl);
        return response;
    }
    
    /**
     * POST /services/${service_id}/states/transition Multitenancy
     * @param requestBody
     * @param service_id
     * @param headerName
     * @param contactCenterId
     * @return
     */
    public static Response performStateTransitionMultit(String requestBody, String service_id, String headerName, String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/states/transition";
        Response response = given().body(requestBody).contentType("application/json").
        		header("Content-Type", "application/json").header(headerName, contactCenterId).post(requestUrl);
        return response;
    }

    /**
     * PUT /genesys/1/cs/services/${service_id}/states/${state_id}/extensions/${
     * ext_name} Updates state extension value
     * 
     * @param service_id
     * @param state_id
     * @param extensionName
     * @param requestBody
     * @return
     */
    public static Response updateStateExtension(String service_id, String state_id, String extensionName,
            String requestBody) {
        String requestUrl = baseUrl + "services/" + service_id + "/states/" + state_id + "/extensions/" + extensionName;
        Response r = given().contentType(content_type).body(requestBody).put(requestUrl);
        return r;
    }
    
    /**
     * PUT /genesys/1/cs/services/${service_id}/states/${state_id}/extensions/${
     * ext_name} Updates state extension value Multitenancy
     * @param service_id
     * @param state_id
     * @param extensionName
     * @param requestBody
     * @param contactCenterId
     * @return
     */
    public static Response updateStateExtensionMultit(String service_id, String state_id, String extensionName,
            String requestBody, String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/states/" + state_id + "/extensions/" + extensionName;
      
        Response response = given().body(requestBody).contentType("application/json").
        		header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).put(requestUrl);
        return response;
    }
    
    /**
     *  PUT /genesys/1/cs/services/${service_id}/states/${state_id}/extensions/${
     * ext_name} Updates state extension value Multitenancy
     * @param service_id
     * @param state_id
     * @param extensionName
     * @param requestBody
     * @param headerName
     * @param contactCenterId
     * @return
     */
    public static Response updateStateExtensionMultit(String service_id, String state_id, String extensionName,
            String requestBody, String headerName, String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/states/" + state_id + "/extensions/" + extensionName;
      
        Response response = given().body(requestBody).contentType("application/json").
        		header("Content-Type", "application/json").header(headerName, contactCenterId).put(requestUrl);
        return response;
    }
    
    

    /**
     * GET /services/${service_id}/states/${state_id}
     * 
     * @param service_id
     * @param state_id
     * @return
     */
    public static Response getStateByStateId(String service_id, String state_id) {
        String requestUrl = baseUrl + "services/" + service_id + "/states/" + state_id;
        Response r = given().contentType(content_type).get(requestUrl);
        return r;
    }
    
    /**
     * GET /services/${service_id}/states/${state_id} Multitenancy
     * @param service_id
     * @param state_id
     * @param contactCenterId
     * @return
     */
    public static Response getStateByStateIdMultit(String service_id, String state_id, String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/states/" + state_id;       
        Response response = given().contentType("application/json").
        		header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).get(requestUrl);
        return response;
    }
    
    /**
     * GET /services/${service_id}/states/${state_id} Multitenancy
     * @param service_id
     * @param state_id
     * @param headerName
     * @param contactCenterId
     * @return
     */
    public static Response getStateByStateIdMultit(String service_id, String state_id, String headerName,String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/states/" + state_id;       
        Response response = given().contentType("application/json").
        		header("Content-Type", "application/json").header(headerName, contactCenterId).get(requestUrl);
        return response;
    }

    /**
     * GET /services/${service_id}/states/${state_id} with parameters map
     * 
     * @param service_id
     * @param state_id
     * @param parametersMap
     * @return
     */
    public static Response getStateByStateId(String service_id, String state_id, Map<String, String> parametersMap) {
        String requestUrl = baseUrl + "services/" + service_id + "/states/" + state_id;
        Response r = given().contentType(content_type).params(parametersMap).get(requestUrl);
        return r;
    }
    /**
     * GET /services/${service_id}/states request to get all states on service
     * 
     * @param service_id
     * @return
     */

    public static Response getAllStatesOnService(String service_id) {
        String requestUrl = baseUrl + "services/" + service_id + "/states";
        Response r = given().contentType(content_type).get(requestUrl);
        return r;
    }
    
    /**
     * GET /services/${service_id}/states request to get all states on service Multitenancy
     * @param service_id
     * @param contactCenterId
     * @return
     */
    public static Response getAllStatesOnServiceMultit(String service_id, String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/states";      
        Response response = given().contentType("application/json").
        		header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).get(requestUrl);
        return response;
    }
    
    /**
     *  GET /services/${service_id}/states request to get all states on service Multitenancy
     * @param service_id
     * @param headerName
     * @param contactCenterId
     * @return
     */
    public static Response getAllStatesOnServiceMultit(String service_id,String headerName, String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/states";      
        Response response = given().contentType("application/json").
        		header("Content-Type", "application/json").header(headerName, contactCenterId).get(requestUrl);
        return response;
    }
    
    /**
     * GET /services/${service_id}/states request to get all states on service
     * with parameters
     * 
     * @param service_id
     * @param parametersMap
     * @return
     */
    public static Response getAllStatesOnService(String service_id, Map<String, String> parametersMap) {
        String requestUrl = baseUrl + "services/" + service_id + "/states";
        Response r = given().contentType(content_type).params(parametersMap).get(requestUrl);
        return r;
    }

    /**
     * GET /services/${service_id}/states request to get active states on
     * service
     * 
     * @param service_id
     * @return
     */
    public static Response getActiveStatesOnService(String service_id) {
        String requestUrl = baseUrl + "services/" + service_id + "/states/active";
        Response r = given().contentType(content_type).get(requestUrl);
        return r;
    }
    
    /**
     * GET /services/${service_id}/states request to get active states on
     * service Multitenancy
     * @param service_id
     * @param contactCenterId
     * @return
     */
    public static Response getActiveStatesOnServiceMultit(String service_id, String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/states/active";      
        Response response = given().contentType("application/json").
        		header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).get(requestUrl);
        return response;
    }
    
    /**
     * GET /services/${service_id}/states request to get active states on
     * service Multitenancy
     * @param service_id
     * @param headerName
     * @param contactCenterId
     * @return
     */
    public static Response getActiveStatesOnServiceMultit(String service_id, String headerName,String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/states/active";      
        Response response = given().contentType("application/json").
        		header("Content-Type", "application/json").header(headerName, contactCenterId).get(requestUrl);
        return response;
    }
    
    /**
     * 
     * @param service_id
     * @param contactCenterId
     * @return
     */
    public static Response getActiveStatesOnService(String service_id, String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/states/active";       
        Response response = given().contentType("application/json").
        		header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).get(requestUrl);
        return response;
    }

    /**
     * GET /services/${service_id}/states request to get active states on
     * service with parameters
     * 
     * @param service_id
     * @param parametersMap
     * @return
     */
    public static Response getActiveStatesOnService(String service_id, Map<String, String> parametersMap) {
        String requestUrl = baseUrl + "services/" + service_id + "/states/active";
        Response r = given().contentType(content_type).params(parametersMap).get(requestUrl);
        return r;
    }

    /**
     * GET /services/${service_id}/states request to get completed states on
     * service
     * 
     * @param service_id
     * @return
     */
    public static Response getCompletedStatesOnService(String service_id) {
        String requestUrl = baseUrl + "services/" + service_id + "/states/completed";
        Response r = given().contentType(content_type).get(requestUrl);
        return r;
    }
    
    /**
     * GET /services/${service_id}/states request to get completed states on
     * service Multitenancy
     * @param service_id
     * @param contactCenterId
     * @return
     */
    public static Response getCompletedStatesOnServiceMultit(String service_id, String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/states/completed";       
        Response response = given().contentType("application/json").
        		header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).get(requestUrl);
        return response;
    }
    
    /**
     * GET /services/${service_id}/states request to get completed states on
     * service Multitenancy
     * @param service_id
     * @param headerName
     * @param contactCenterId
     * @return
     */
    public static Response getCompletedStatesOnServiceMultit(String service_id, String headerName, String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/states/completed";       
        Response response = given().contentType("application/json").
        		header("Content-Type", "application/json").header(headerName, contactCenterId).get(requestUrl);
        return response;
    }
    
    

    /**
     * GET /services/${service_id}/states request to get completed states on
     * service with parameters
     * 
     * @param service_id
     * @param parametersMap
     * @return
     */
    public static Response getCompletedStatesOnService(String service_id, Map<String, String> parametersMap) {
        String requestUrl = baseUrl + "services/" + service_id + "/states/completed";
        Response r = given().contentType(content_type).params(parametersMap).get(requestUrl);
        return r;
    }

    // ****************************************** state
    // *******************************************************************************************************************************************************

    // ******************************************* task
    // *******************************************************************************************************
    /**
     * POST /services/${service_id}/tasks/start - request to start a task on
     * service
     * 
     * @param requestBody
     * @param service_id
     * @return
     */
    public static Response startTask(String requestBody, String service_id) {
        String requestUrl = baseUrl + "services/" + service_id + "/tasks/start";
        Response r = given().contentType(content_type).body(requestBody).post(requestUrl);
        return r;
    }
    
    /**
     * POST /services/${service_id}/tasks/start - request to start a task on
     * service Multitenancy
     * @param requestBody
     * @param service_id
     * @param contactCenterId
     * @return
     */
    public static Response startTaskMultit(String requestBody, String service_id, String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/tasks/start";      
        Response response = given().contentType("application/json").body(requestBody).
        		header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).post(requestUrl);
        return response;
    }
    
    /**
     *  POST /services/${service_id}/tasks/start - request to start a task on
     * service Multitenancy
     * @param requestBody
     * @param service_id
     * @param headerName
     * @param contactCenterId
     * @return
     */
    public static Response startTaskMultit(String requestBody, String service_id,String headerName, String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/tasks/start";      
        Response response = given().contentType("application/json").body(requestBody).
        		header("Content-Type", "application/json").header(headerName, contactCenterId).post(requestUrl);
        return response;
    }
    
    
    /**
     * POST /services/${service_id}/tasks/${task_id}/end - request to end task
     * 
     * @param requestBody
     * @param service_id
     * @param task_id
     * @return
     */
    public static Response completeTask(String requestBody, String service_id, String task_id) {
        String requestUrl = baseUrl + "services/" + service_id + "/tasks/" + task_id + "/end";
        Response r = given().contentType(content_type).body(requestBody).post(requestUrl);
        return r;
    }
    
    /**
     * POST /services/${service_id}/tasks/${task_id}/end - request to end task Multitenancy
     * @param requestBody
     * @param service_id
     * @param task_id
     * @param contactCenterId
     * @return
     */
    public static Response completeTaskMultit(String requestBody, String service_id, String task_id, String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/tasks/" + task_id + "/end";       
        Response response = given().contentType("application/json").body(requestBody).
        		header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).post(requestUrl);
        return response;
    }
    
    /**
     *  POST /services/${service_id}/tasks/${task_id}/end - request to end task Multitenancy
     * @param requestBody
     * @param service_id
     * @param task_id
     * @param headerName
     * @param contactCenterId
     * @return
     */
    public static Response completeTaskMultit(String requestBody, String service_id, String task_id, String headerName,String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/tasks/" + task_id + "/end";       
        Response response = given().contentType("application/json").body(requestBody).
        		header("Content-Type", "application/json").header(headerName, contactCenterId).post(requestUrl);
        return response;
    }

    /**
     * PUT /genesys/1/cs/services/${service_id}/tasks/${task_id}/extensions/${
     * extension_name} Updates state extension value
     * 
     * @param service_id
     * @param task_id
     * @param extensionName
     * @param requestBody
     * @return
     */
    public static Response updateTaskExtension(String service_id, String task_id, String extensionName,
            String requestBody) {
        String requestUrl = baseUrl + "services/" + service_id + "/tasks/" + task_id + "/extensions/" + extensionName;
        Response r = given().contentType(content_type).body(requestBody).put(requestUrl);
        return r;
    }
    
    /**
     * PUT /genesys/1/cs/services/${service_id}/tasks/${task_id}/extensions/${
     * extension_name} Updates state extension value Multitenancy
     * 
     * @param service_id
     * @param task_id
     * @param extensionName
     * @param requestBody
     * @param contactCenterId
     * @return
     */
    public static Response updateTaskExtensionMultit(String service_id, String task_id, String extensionName,
            String requestBody, String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/tasks/" + task_id + "/extensions/" + extensionName;
        Response response = given().contentType("application/json").body(requestBody).
        	header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).put(requestUrl);
        return response;
    }
    
    /**
     * PUT /genesys/1/cs/services/${service_id}/tasks/${task_id}/extensions/${
     * extension_name} Updates state extension value Multitenancy
     * 
     * @param service_id
     * @param task_id
     * @param extensionName
     * @param requestBody
     * @param headerName
     * @param contactCenterId
     * @return
     */
    public static Response updateTaskExtensionMultit(String service_id, String task_id, String extensionName,
            String requestBody, String headerName,String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/tasks/" + task_id + "/extensions/" + extensionName;
        Response response = given().contentType("application/json").body(requestBody).
        	header("Content-Type", "application/json").header(headerName, contactCenterId).put(requestUrl);
        return response;
    }

    /**
     * GET /services/${service_id}/tasks/${task_id} request to get task by task
     * id
     * 
     * @param service_id
     * @param task_id
     * @return
     */
    public static Response getTaskByTaskId(String service_id, String task_id) {
        String requestUrl = baseUrl + "services/" + service_id + "/tasks/" + task_id;
        Response r = given().contentType(content_type).get(requestUrl);
        return r;
    }
    
    /**
     *  GET /services/${service_id}/tasks/${task_id} request to get task by task
     * id Multitenancy
     * @param service_id
     * @param task_id
     * @param contactCenterId
     * @return
     */
    public static Response getTaskByTaskIdMultit(String service_id, String task_id, String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/tasks/" + task_id;      
        Response response = given().contentType("application/json").
        		header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).get(requestUrl);
        return response;
    }
    
    /**
     * GET /services/${service_id}/tasks/${task_id} request to get task by task
     * id Multitenancy
     * 
     * @param service_id
     * @param task_id
     * @param headerName
     * @param contactCenterId
     * @return
     */
    public static Response getTaskByTaskIdMultit(String service_id, String task_id, String headerName, String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/tasks/" + task_id;      
        Response response = given().contentType("application/json").
        		header("Content-Type", "application/json").header(headerName, contactCenterId).get(requestUrl);
        return response;
    }

    /**
     * GET /services/${service_id}/tasks/${task_id} request to get task by task
     * id with extensions filter
     * 
     * @param service_id
     * @param task_id
     * @param filterName
     * @param filterValues
     * @return
     */
    public static Response getTaskByTaskId(String service_id, String task_id, String filterName,
            List<String> filterValues) {
        String requestUrl = baseUrl + "services/" + service_id + "/tasks/" + task_id;
        Response r = given().contentType(content_type).param(filterName, filterValues).get(requestUrl);
        return r;
    }
    /**
     * GET /services/${service_id}/tasks/${task_id} request to get task by task
     * id with extensions filter
     * 
     * @param service_id
     * @param task_id
     * @param parametersMap
     * @return
     */
    public static Response getTaskByTaskId(String service_id, String task_id, Map<String, String> parametersMap) {
        String requestUrl = baseUrl + "services/" + service_id + "/tasks/" + task_id;
        Response r = given().contentType(content_type).params(parametersMap).get(requestUrl);
        return r;
    }
    /**
     * GET All (default): /services/${service_id}/tasks - request to get all
     * tasks on service
     * 
     * @param service_id
     * @return
     */
    public static Response getAllTasksOnService(String service_id) {
        String requestUrl = baseUrl + "services/" + service_id + "/tasks";
        Response r = given().contentType(content_type).get(requestUrl);
        return r;
    }
    
    /**
     *  GET All (default): /services/${service_id}/tasks - request to get all
     * tasks on service Multitenancy
     * @param service_id
     * @param contactCenterId
     * @return
     */
    public static Response getAllTasksOnServiceMultit(String service_id, String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/tasks";     
        Response response = given().contentType("application/json").
        		header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).get(requestUrl);
        return response;
    }
    
    /**
     * GET All (default): /services/${service_id}/tasks - request to get all
     * tasks on service Multitenancy
     * @param service_id
     * @param headerName
     * @param contactCenterId
     * @return
     */
    public static Response getAllTasksOnServiceMultit(String service_id, String headerName, String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/tasks";     
        Response response = given().contentType("application/json").
        		header("Content-Type", "application/json").header(headerName, contactCenterId).get(requestUrl);
        return response;
    }
    
    

    /**
     * GET /services/${service_id}/tasks - request to get all tasks on service
     * with parameters
     * 
     * @param service_id
     * @param parametersMap
     * @return
     */
    public static Response getAllTasksOnService(String service_id, Map<String, String> parametersMap) {
        String requestUrl = baseUrl + "services/" + service_id + "/tasks";
        Response r = given().contentType(content_type).params(parametersMap).get(requestUrl);
        return r;
    }
    /**
     * GET /services/${service_id}/tasks - request to get all tasks with
     * parameter name and list of values
     * 
     * @param service_id
     * @param filterName
     * @param filterValues
     * @return
     */
    public static Response getAllTasksOnService(String service_id, String filterName, List<String> filterValues) {
        String requestUrl = baseUrl + "services/" + service_id + "/tasks";
        Response r = given().contentType(content_type).param(filterName, filterValues).get(requestUrl);
        return r;
    }

    /**
     * GET All (default): /services/${service_id}/tasks/active - request to get
     * aactive tasks on service
     * 
     * @param service_id
     * @return
     */
    public static Response getActiveTasksOnService(String service_id) {
        String requestUrl = baseUrl + "services/" + service_id + "/tasks/active";
        Response r = given().contentType(content_type).get(requestUrl);
        return r;
    }
    
    /**
     * GET All (default): /services/${service_id}/tasks/active - request to get
     * aactive tasks on service Multitenancy
     * @param service_id
     * @param contactCenterId
     * @return
     */
    public static Response getActiveTasksOnServiceMultit(String service_id, String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/tasks/active";      
        Response response = given().contentType("application/json").
        		header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).get(requestUrl);
        return response;
    }
    
    /**
     * GET All (default): /services/${service_id}/tasks/active - request to get
     * aactive tasks on service Multitenancy
     * @param service_id
     * @param headerName
     * @param contactCenterId
     * @return
     */
    public static Response getActiveTasksOnServiceMultit(String service_id, String headerName,String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/tasks/active";      
        Response response = given().contentType("application/json").
        		header("Content-Type", "application/json").header(headerName, contactCenterId).get(requestUrl);
        return response;
    }

    /**
     * GET /services/${service_id}/tasks/active - request to get active tasks on
     * service with parameters
     * 
     * @param service_id
     * @param parametersMap
     * @return
     */
    public static Response getActiveTasksOnService(String service_id, Map<String, String> parametersMap) {
        String requestUrl = baseUrl + "services/" + service_id + "/tasks/active";
        Response r = given().contentType(content_type).params(parametersMap).get(requestUrl);
        return r;
    }

    /**
     * GET /services/${service_id}/tasks/active - request to get active tasks on
     * service with parameter value and list of values
     * 
     * @param service_id
     * @param filterName
     * @param filterValues
     * @return
     */
    public static Response getActiveTasksOnService(String service_id, String filterName, List<String> filterValues) {
        String requestUrl = baseUrl + "services/" + service_id + "/tasks/active";
        Response r = given().contentType(content_type).param(filterName, filterValues).get(requestUrl);
        return r;
    }

    /**
     * GET All (default): /services/${service_id}/tasks/completed - request to
     * get completed tasks on service
     * 
     * @param service_id
     * @return
     */
    public static Response getCompletedTasksOnService(String service_id) {
        String requestUrl = baseUrl + "services/" + service_id + "/tasks/completed";
        Response r = given().contentType(content_type).get(requestUrl);
        return r;
    }
    
    /**
     * GET All (default): /services/${service_id}/tasks/completed - request to
     * get completed tasks on service Multitenancy
     * @param service_id
     * @param contactCenterId
     * @return
     */
    public static Response getCompletedTasksOnServiceMultit(String service_id, String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/tasks/completed";        
        Response response = given().contentType("application/json").
        		header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).get(requestUrl);
        return response;
    }
    
    /**
     * GET All (default): /services/${service_id}/tasks/completed - request to
     * get completed tasks on service Multitenancy
     * @param service_id
     * @param headerName
     * @param contactCenterId
     * @return
     */
    public static Response getCompletedTasksOnServiceMultit(String service_id, String headerName,String contactCenterId) {
        String requestUrl = baseUrl + "services/" + service_id + "/tasks/completed";        
        Response response = given().contentType("application/json").
        		header("Content-Type", "application/json").header(headerName, contactCenterId).get(requestUrl);
        return response;
    }

    /**
     * GET /services/${service_id}/tasks/completed - request to get all tasks on
     * service with parameters
     * 
     * @param service_id
     * @param parametersMap
     * @return
     */
    public static Response getCompletedTasksOnService(String service_id, Map<String, String> parametersMap) {
        String requestUrl = baseUrl + "services/" + service_id + "/tasks/completed";
        Response r = given().contentType(content_type).params(parametersMap).get(requestUrl);
        return r;
    }

    /**
     * GET /services/${service_id}/tasks/completed - request to get all tasks on
     * service with parameter and list of values
     * 
     * @param service_id
     * @param filterName
     * @param filterValues
     * @return
     */
    public static Response getCompletedTasksOnService(String service_id, String filterName, List<String> filterValues) {
        String requestUrl = baseUrl + "services/" + service_id + "/tasks/completed";
        Response r = given().contentType(content_type).param(filterName, filterValues).get(requestUrl);
        return r;
    }
    // ************************************************************************************************************************************

    /**
     * GET /metadata/business-attributes Returns the schema for the list of
     * available business attributes.
     * 
     * @return
     */
    public static Response getBusinessAttributesSchema() {
        String requestUrl = baseUrl + "metadata/business-attributes";
        Response r = given().contentType(content_type).get(requestUrl);
        return r;
    }
    
    /**
     * GET /metadata/business-attributes Returns the schema for the list of
     * available business attributes. Multitenancy
     * @param contactCenterId
     * @return
     */
    public static Response getBusinessAttributesSchemaMultit(String contactCenterId) {
        String requestUrl = baseUrl + "metadata/business-attributes";        
        Response response = given().contentType("application/json").
        		header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).get(requestUrl);
        return response;
    }

    /**
     * GET /metadata/business-attributes/${business-attribute-name}
     * 
     * @param BAName
     * @return
     */
    public static Response getBusinessAttributesByBAName(String BAName) {
        String requestUrl = baseUrl + "metadata/business-attributes/" + BAName;
        Response r = given().contentType(content_type).get(requestUrl);
        return r;
    }
    
    /**
     * GET /metadata/business-attributes/${business-attribute-name} Multitenancy
     * @param BAName
     * @param contactCenterId
     * @return
     */
    public static Response getBusinessAttributesByBANameMultit(String BAName, String contactCenterId) {
        String requestUrl = baseUrl + "metadata/business-attributes/" + BAName;        
        Response response = given().contentType("application/json").
        		header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).get(requestUrl);
        return response;
    }
    
    /**
     * GET /metadata/business-attributes?cv-attribute=Service.application_type
     * 
     * @param name
     * @return
     */
    public static Response queryModelAttributeFullName(String name) {
        String requestUrl = baseUrl + "metadata/business-attributes";
        Response r = given().contentType(content_type).param("cv-attribute", name).get(requestUrl);
        return r;
    }
    
    /**
     * GET /metadata/business-attributes?cv-attribute=Service.application_type Multitenancy
     * @param name
     * @param contactCenterId
     * @return
     */
    public static Response queryModelAttributeFullNameMultit(String name, String contactCenterId) {
        String requestUrl = baseUrl + "metadata/business-attributes";      
        Response response = given().contentType("application/json").param("cv-attribute", name).
        		header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).get(requestUrl);
        return response;
    }
    
    // **************************************************************************************************************************
    // ****************************************purge***************************************************************
    public static Response purgeServices(String requestBody) {
        String requestUrl = baseUrl + "maintenance/purge";
       // content_type = "application/json;charset=UTF-8";       
      
        Response r = given().contentType(content_type).body(requestBody).post(requestUrl);
        return r;
    }
    
    public static Response purgeServicesMultit(String requestBody, String contactCenterId) {
        String requestUrl = baseUrl + "maintenance/purge";     
        Response response = given().contentType("application/json").body(requestBody).
        		header("Content-Type", "application/json").header("ContactCenterId", contactCenterId).post(requestUrl);
        return response;
    }
    //*****************************************export**************************************************************
    public static Response exportServicesToJsonStreamByDate(Map<String, String> parametersMap) {
        String requestUrl = baseUrl + "maintenance/services/stream";
        content_type = "application/json;charset=UTF-8";
        Response r = given().contentType(content_type).params(parametersMap).get(requestUrl);
        return r;
    }

    
    public static Response exportServicesToFileByDate(Map<String, String> parametersMap) {
        String requestUrl = baseUrl + "maintenance/services/files";
     //   content_type = "application/json;charset=UTF-8";
        content_type = "application/json";
        Response r = given().contentType(content_type).header("Content-Type", "application/json").queryParams(parametersMap).when().post(requestUrl);
        return r;
    }
    
    
 
    public static Response exportServicesToFileByDate(String time_from) {
        String requestUrl = baseUrl + "maintenance/services/files";
     //   content_type = "application/json;charset=UTF-8";
        content_type = "application/json";
        Response r = given().contentType(content_type).queryParam("time_from", time_from).when().post(requestUrl);
        return r;
    }
    
    // ********************************************************************************************************************************************************
   
   /**
    * POST /genesys/1/cs/profiles request to UCS to create customer profile
    * @param requestBody
    * @return
    */
    public static Response createCustomerProfile(String requestBody) {
        String requestUrl = baseUCSUrl + "profiles";
        Response r = given().contentType(content_type).body(requestBody).post(requestUrl);
        return r;
    }
    
    //***********************************************************************************************************
    /**
     * method gets service id from the response as String
     * 
     * @param response
     * @return
     */
    public static String getServiceId(Response response) {
        String service_id = from(response.asString()).get("service_id").toString();
        assertNotNull("Service_id is null", service_id);
        return service_id;
    }
    /**
     * method gets state id from the response as String
     * 
     * @param response
     * @return
     */
    public static String getStateId(Response response) {
        String state_id = from(response.asString()).get("state_id").toString();
        assertNotNull("State_id is null", state_id);
        return state_id;
    }
    
    /**
     * method gets task id from the response as String
     * 
     * @param response
     * @return
     */
    public static String getTaskId(Response response) {
        String state_id = from(response.asString()).get("task_id").toString();
        assertNotNull("Task_id is null", state_id);
        return state_id;
    }
    
    /**
     * method gets customer id from the response as String
     * @param response
     * @return
     */
    public static String getCustomerId(Response response) {
        String customer_id = from(response.asString()).get("customer_id").toString();
        assertNotNull("Customer is null", customer_id);
        return customer_id;
    }
    
    public static String getCreatedFileName(Response response) {
        String nameOfFile = from(response.asString()).get("created").toString();
        int beginIndex = nameOfFile.indexOf("_");
        int  endIndex = nameOfFile.indexOf(".");
        nameOfFile= nameOfFile.substring(beginIndex,endIndex);
       
        assertNotNull("File name is null", nameOfFile);
        return nameOfFile;
    }
    
    public static List<String> getCreatedFileNames(Response response) {
        String namesOfFiles = from(response.asString()).get("created").toString();
        
    	String [] names = namesOfFiles.split(", ");
		System.out.println(names[0]);
		System.out.println(names[1]);
		System.out.println(names[2]);
		int beginIndex;
		int  endIndex;
		String nameOfFile="";
		List<String> list = new ArrayList<>();
		for (int i=0; i<names.length;i++){
			beginIndex = names[i].indexOf("_");
			endIndex = names[i].indexOf(".");
			nameOfFile= names[i].substring(beginIndex,endIndex);
			list.add(nameOfFile);
			assertNotNull("File name is null", nameOfFile);
		}
       
        assertNotNull("File name is null", namesOfFiles);
        return list;
    }
    /**
     * method gets timestamp for started event from the response as String
     * @param response
     * @return
     */
    public static String getStartedTimestamp(Response response) {
        String started = from(response.asString()).get("started.timestamp").toString();
        assertNotNull("Started\timestamp is null", started);
        return started;
    }
    
    /**
     * method gets timestamp for completed event from the response as String
     * @param response
     * @return
     */
    public static String getCompletedTimestamp(Response response) {
        String completed = from(response.asString()).get("completed.timestamp").toString();
        assertNotNull("Completed\timestamp is null", completed);
        return completed;
    }
    
    /**
     * method gets state_type from active states e.g. MyState1
     * @param response
     * @return
     */
    public static String getStateTypeFromActiveStates(Response response) {
        String active = from(response.asString()).get("active_states.state_type").toString();
        assertNotNull("active_states is null", active);
        return active;
    }
    
    /**
     * method gets state_type from completed states e.g. MyState2
     * @param response
     * @return
     */
    public static String getStateTypeFromCompletedStates(Response response) {
        String completed = from(response.asString()).get("completed_states.state_type").toString();
        assertNotNull("completed_states is null", completed);
        return completed;
    }

    /**
     * method gets task_type from active tasks e.g. MyTask1
     * @param response
     * @return
     */
    public static String getTaskTypeFromActiveTasks(Response response) {
        String active = from(response.asString()).get("active_tasks.task_type").toString();
        assertNotNull("active_tasks is null", active);
        return active;
    }
    
    /**
     * method gets task_type from completed tasks e.g. MyTask3
     * @param response
     * @return
     */
    public static String getTaskTypeFromCompletedTasks(Response response) {
        String completed = from(response.asString()).get("completed_tasks.task_type").toString();
        assertNotNull("completed_tasks is null", completed);
        return completed;
    }
    
    /**
     * get service_type BA from response
     * @param response
     * @return
     */
    public static String getServiceType(Response response) {
        String service_type = from(response.asString()).get("service_type").toString();
        assertNotNull("Service_type is null", service_type);
        return service_type;
    }
    
    /**
     * get state_type BA from response
     * @param response
     * @return
     */
    public static String getStateType(Response response) {
        String state_type = from(response.asString()).get("state_type").toString();
        assertNotNull("State_type is null", state_type);
        return state_type;
    }
    
    /**
     * get task_type BA from response
     * @param response
     * @return
     */
    public static String getTaskType(Response response) {
        String task_type = from(response.asString()).get("task_type").toString();
        assertNotNull("Task_type is null", task_type);
        return task_type;
    }
    
    /**
     * get application type BA from response
     * @param response
     * @return
     */
    public static String getApplicationType(Response response) {
        String application_type = from(response.asString()).get("started.application_type").toString();
        assertNotNull("Application_type is null", application_type);
        return application_type;
    }
    
    /**
     * get application type BA from response
     * @param response
     * @return
     */
    public static String getApplicationTypeCompleted(Response response) {
        String application_type = from(response.asString()).get("completed.application_type").toString();
        assertNotNull("Application_type is null", application_type);
        return application_type;
    }
    
    /**
     * get resource type BA from response
     * @param response
     * @return
     */
    public static String getResourceType(Response response) {
        String resource_type = from(response.asString()).get("started.resource_type").toString();
        assertNotNull("Resource_type is null", resource_type);
        return resource_type;
    }
    
    /**
     * get resource type BA from response
     * @param response
     * @return
     */
    public static String getResourceTypeCompleted(Response response) {
        String resource_type = from(response.asString()).get("completed.resource_type").toString();
        assertNotNull("Resource_type is null", resource_type);
        return resource_type;
    }
    
    /**
     * get media type BA from response
     * @param response
     * @return
     */
    public static String getMediaType(Response response) {
        String media_type = from(response.asString()).get("started.media_type").toString();
        assertNotNull("Media_type is null", media_type);
        return media_type;
    }
    
    /**
     * get media type BA from response
     * @param response
     * @return
     */
    public static String getMediaTypeCompleted(Response response) {
        String media_type = from(response.asString()).get("completed.media_type").toString();
        assertNotNull("Media_type is null", media_type);
        return media_type;
    }
    
    /**
     * get disposition BA from response
     * @param response
     * @return
     */
    public static String getDisposition(Response response) {
        String disposition = from(response.asString()).get("disposition").toString();
        assertNotNull("Disposition is null", disposition);
        return disposition;
    }

    /**
     * method gets message from the response as String
     * 
     * @param response
     * @return
     */

    public static String getErrorMessage(Response response) {
        String message;
        try {
            message = from(response.asString()).get("message").toString();
            if (message == null) {
                assertTrue(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        assertNotNull("Message is null", message);
        return message;
    }

    /**
     * method gets exception from the response as String
     * 
     * @param response
     * @return
     */

    public static String getErrorException(Response response) {
        String exception;
        try {
            exception = from(response.asString()).get("exception").toString();
            if (exception == null) {
                assertTrue(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        assertNotNull("Exception is null", exception);

        return exception;

    }

    /**
     * Method verifies the status code of the response
     * 
     * @param response
     * @param statusCode
     * @return
     */
    public static boolean verifyStatusCode(Response response, int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
        if (statusCode == response.getStatusCode()) {
            return true;
        } else {
            return false;
        }

    }
    
    /**
     * Method verifies if the param2 is present in param1
     * @param body
     * @param valueToFind
     * @return
     */

    public static boolean contains(String body, String valueToFind) {
        if (body.contains(valueToFind) == true) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Method verifies if the param2 is NOT present in param1
     * @param body
     * @param valueToFind
     * @return
     */
    public static boolean notContains(String body, String valueToFind) {
        if (body.contains(valueToFind) == true) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Generating some time in the future
     * note: 29 of February in the leap year; use leap years in code to generate date in the future
     * @return
     */
    @SuppressWarnings("deprecation")
	public static String getTimeInFuture() {
        Date date = new Date();
//        String time = "2017-" + (date.getMonth()+1) + "-" + date.getDate() + "T" + date.getHours() + ":"
//                + date.getMinutes() + ":" + date.getSeconds() + ".634Z";
        String formatted = new SimpleDateFormat("MM-dd'T'HH:mm:ss").format(date);
        formatted="2020-"+formatted + ".000Z";
        return formatted;
    }
    
    /**
     * Generating some time in the future far from now
     * @return
     */
    @SuppressWarnings("deprecation")
	public static String getTimeInFutureFarFromNow() {
        Date date = new Date();
    String formatted = new SimpleDateFormat("MM-dd'T'HH:mm:ss").format(date);
        
        return "2024-"+formatted+".000Z";
    }
    
    /**
     * Generating some time in the future with wrong date format
     * @return
     */
    @SuppressWarnings("deprecation")
	public static String getTimeInFutureWrongDateFormat() {
        Date date = new Date();      
        String formatted = new SimpleDateFormat("dd'T'HH:mm:ss").format(date);
        
        return "2016-Dec-"+formatted+".000Z";
    }
    
    /**
     * Generating 3 hours in the past
     * @return
     */
    @SuppressWarnings("deprecation")
	public static String getTimeInPast() {
        Date date = new Date();
        String time = "2016-" + (date.getMonth()+1) + "-" + date.getDate() + "T" + (date.getHours() - 10) + ":"
                + date.getMinutes() + ":" + date.getSeconds() + ".634Z";
        return time;
    }
    
    @SuppressWarnings("deprecation")
	public static String getCurrentTime() {
        Date date = new Date();
     //   String time = "2015-" + date.getMonth() + "-" + date.getDate() + "T" + date.getHours() + ":"
     //           + date.getMinutes() + ":" + date.getSeconds() + ".634Z";
        String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(date);
        
        return formatted+".000Z";
    }

//    /**
//     * Construct anonymous service body with minimum parameters
//     *
//     * @param service_type
//     * @param customer_id
//     * @return
//     */
//    public static String constructOnCustomerServiceBodyMin(String service_type, String customer_id) {
//        OnCustomerServiceBodyMin body = new OnCustomerServiceBodyMin(service_type, customer_id);
//        return body.toJSON();
//    }
//
//    /**
//     * Method creates non anonymous service body with RelatedOffers extension
//     *
//     * @param service_type
//     * @param customer_id
//     * @param interaction_id
//     * @param application_type
//     * @param media_type
//     * @param resource_type
//     * @return
//     */
//
//    @SuppressWarnings("rawtypes")
//	public static String constructOnCustomerServiceBody(String service_type, String customer_id, String interaction_id,
//            String application_type, String media_type, String resource_type) {
//
//        OnCustomerServiceBody body = new OnCustomerServiceBody(service_type, customer_id, interaction_id,
//                application_type, media_type, resource_type);
//
//        List<Map> list = constructRelatedOffersExtension();
//
//        body.setRelatedOffersService(list);
//
//        return body.toJSON();
//    }
//
//    /**Method creates non anonymous service body with RelatedOffers extension and timestamp
//     *
//     * @param service_type
//     * @param customer_id
//     * @param interaction_id
//     * @param application_type
//     * @param media_type
//     * @param resource_type
//     * @param timestamp
//     * @return
//     */
//    @SuppressWarnings("rawtypes")
//	public static String constructOnCustomerServiceBodyWithTimestamp(String service_type, String customer_id,
//            String interaction_id, String application_type, String media_type, String resource_type, String timestamp) {
//
//        OnCustomerServiceBodyWithTimestamp body = new OnCustomerServiceBodyWithTimestamp(service_type, customer_id,
//                interaction_id, application_type, media_type, resource_type, timestamp);
//
//        List<Map> list = constructRelatedOffersExtension();
//
//        body.setRelatedOffersService(list);
//
//        return body.toJSON();
//    }
//
//    /**
//     * Method creates non anonymous service body with RelatedOffers extension
//     *
//     * @param service_type
//     * @param contact_key
//     * @param interaction_id
//     * @param application_type
//     * @param media_type
//     * @param resource_type
//     * @return
//     */
//
//    @SuppressWarnings("rawtypes")
//	public static String constructAnonymousServiceBody(String service_type, String contact_key, String interaction_id,
//            String application_type, String media_type, String resource_type) {
//
//        AnonymousServiceBody body = new AnonymousServiceBody(service_type, contact_key, interaction_id,
//                application_type, media_type, resource_type);
//
//        List<Map> list = constructRelatedOffersExtension();
//
//        body.setRelatedOffersService(list);
//
//        return body.toJSON();
//    }
//    /**
//     * Method creates non anonymous service body with RelatedOffers extension
//     * and disposition
//     *
//     * @param service_type
//     * @param contact_key
//     * @param interaction_id
//     * @param application_type
//     * @param media_type
//     * @param resource_type
//     * @param disposition
//     * @return
//     */
//
//    @SuppressWarnings("rawtypes")
//	public static String constructAnonymousServiceBodyWithDisp(String service_type, String contact_key,
//            String interaction_id, String application_type, String media_type, String resource_type, String disposition) {
//
//        AnonymousServiceBodyWithDisp body = new AnonymousServiceBodyWithDisp(service_type, contact_key, interaction_id,
//                application_type, media_type, resource_type, disposition);
//
//        List<Map> list = constructRelatedOffersExtension();
//
//        body.setRelatedOffersService(list);
//
//        return body.toJSON();
//    }
//    /**
//     * Method creates anonymous service body with minimum required parameters
//     *
//     * @param service_type
//     * @param contact_key
//     * @return
//     */
//    public static String constructAnonymousServiceBodyMin(String service_type, String contact_key) {
//        AnonymousServiceBodyMin body = new AnonymousServiceBodyMin(service_type, contact_key);
//        return body.toJSON();
//    }
//
//    /**
//     * to create Anonymous servicebody with timestamp provided
//     * @param service_type
//     * @param contact_key
//     * @param interaction_id
//     * @param application_type
//     * @param media_type
//     * @param resource_type
//     * @param timestamp
//     * @return
//     */
//	public static String constructAnonymousServiceBodyWithTimestamp(String service_type, String contact_key,
//            String interaction_id, String application_type, String media_type, String resource_type, String timestamp) {
//
//        AnonymousServiceBodyWithTimestamp body = new AnonymousServiceBodyWithTimestamp(service_type, contact_key,
//                interaction_id, application_type, media_type, resource_type, timestamp);
//
//        List<Map> list = constructRelatedOffersExtension();
//
//        body.setRelatedOffersService(list);
//
//        return body.toJSON();
//    }
//
//    /**
//     * Method creates anonymous state body with minimum required parameters
//     *
//     * @param state_type
//     * @return
//     */
//    public static String constructStateBodyMin(String state_type) {
//        StateBodyMin body = new StateBodyMin(state_type);
//        return body.toJSON();
//    }
//
//    /**
//     * Method creates state body
//     * @param state_type
//     * @param interaction_id
//     * @param application_type
//     * @param media_type
//     * @param resource_type
//     * @return
//     */
//
//    @SuppressWarnings("rawtypes")
//	public static String constructStateBody(String state_type, String interaction_id, String application_type,
//            String media_type, String resource_type) {
//        StateBody body = new StateBody(state_type, interaction_id, application_type, media_type, resource_type);
//        List<Map> feedbackState = constructFeedbackExtension();
//        List<Map> satisfactionState = constructSatisfactionExtension();
//        body.setFeedbackState(feedbackState);
//        body.setSatisfactionState(satisfactionState);
//        return body.toJSON();
//    }
//
//    /**
//     * Method creates state body with disposition parameter
//     * @param state_type
//     * @param interaction_id
//     * @param application_type
//     * @param media_type
//     * @param resource_type
//     * @param disposition
//     * @return
//     */
//    @SuppressWarnings("rawtypes")
//	public static String constructStateBodyWithDisp(String state_type, String interaction_id, String application_type,
//            String media_type, String resource_type, String disposition) {
//        StateBodyWithDisp body = new StateBodyWithDisp(state_type, interaction_id, application_type, media_type,
//                resource_type, disposition);
//        List<Map> feedbackState = constructFeedbackExtension();
//        List<Map> satisfactionState = constructSatisfactionExtension();
//        body.setFeedbackState(feedbackState);
//        body.setSatisfactionState(satisfactionState);
//        return body.toJSON();
//    }
//
//    /**
//     * Method creates state body with timestamp
//     * @param state_type
//     * @param interaction_id
//     * @param application_type
//     * @param media_type
//     * @param resource_type
//     * @param timestamp
//     * @return
//     */
//    @SuppressWarnings("rawtypes")
//	public static String constructStateBodyWithTimestamp(String state_type, String interaction_id,
//            String application_type, String media_type, String resource_type, String timestamp) {
//        StateBodyWithTimestamp body = new StateBodyWithTimestamp(state_type, interaction_id, application_type,
//                media_type, resource_type);
//        List<Map> feedbackState = constructFeedbackExtension();
//        List<Map> satisfactionState = constructSatisfactionExtension();
//        body.setFeedbackState(feedbackState);
//        body.setSatisfactionState(satisfactionState);
//
//        body.setTimestamp(timestamp);
//        return body.toJSON();
//    }
//
//    /**
//     * Method creates task body with minimum paramaters
//     * @param task_type
//     * @return
//     */
//    public static String constructTaskBodyMin(String task_type) {
//        TaskBodyMin body = new TaskBodyMin(task_type);
//        return body.toJSON();
//    }
//
//    /**
//     * Method creates task body
//     * @param task_type
//     * @param interaction_id
//     * @param application_type
//     * @param media_type
//     * @param resource_type
//     * @return
//     */
//    @SuppressWarnings("rawtypes")
//	public static String constructTaskBody(String task_type, String interaction_id, String application_type,
//            String media_type, String resource_type) {
//        TaskBody body = new TaskBody(task_type, interaction_id, application_type, media_type, resource_type);
//        List<Map> feedbackTask = constructFeedbackExtension();
//        List<Map> satisfactionTask = constructSatisfactionExtension();
//        body.setFeedbackTask(feedbackTask);
//        body.setSatisfactionTask(satisfactionTask);
//        return body.toJSON();
//    }
//
//    /**
//     * Method creates task body with disposition parameter
//     * @param task_type
//     * @param interaction_id
//     * @param application_type
//     * @param media_type
//     * @param resource_type
//     * @param disposition
//     * @return
//     */
//    @SuppressWarnings("rawtypes")
//	public static String constructTaskBodyWithDisp(String task_type, String interaction_id, String application_type,
//            String media_type, String resource_type, String disposition) {
//        TaskBodyWithDisp body = new TaskBodyWithDisp(task_type, interaction_id, application_type, media_type,
//                resource_type, disposition);
//        List<Map> feedbackTask = constructFeedbackExtension();
//        List<Map> satisfactionTask = constructSatisfactionExtension();
//        body.setFeedbackTask(feedbackTask);
//        body.setSatisfactionTask(satisfactionTask);
//        return body.toJSON();
//    }
//
//	public static String constructTaskBodyWithTimestamp(String task_type, String interaction_id,
//            String application_type, String media_type, String resource_type, String timestamp) {
//        TaskBodyWithTimestamp body = new TaskBodyWithTimestamp(task_type, interaction_id, application_type,
//                media_type, resource_type);
//        List<Map> feedbackTask = constructFeedbackExtension();
//        List<Map> satisfactionTask = constructSatisfactionExtension();
//        body.setFeedbackTask(feedbackTask);
//        body.setSatisfactionTask(satisfactionTask);
//
//        body.setTimestamp(timestamp);
//        return body.toJSON();
//    }
//
//    /**
//     * Method creates task body with state id
//     * @param task_type
//     * @param interaction_id
//     * @param application_type
//     * @param media_type
//     * @param resource_type
//     * @param state_id
//     * @return
//     */
//    public static String constructTaskBodyWithStateId(String task_type, String interaction_id, String application_type,
//            String media_type, String resource_type, String state_id) {
//        TaskBodyWithStateId body = new TaskBodyWithStateId(task_type, interaction_id, application_type, media_type,
//                resource_type, state_id);
//        List<Map> feedbackTask = constructFeedbackExtension();
//        List<Map> satisfactionTask = constructSatisfactionExtension();
//        body.setFeedbackTask(feedbackTask);
//        body.setSatisfactionTask(satisfactionTask);
//        return body.toJSON();
//    }
//
//    public static String constructTaskBodyMax(String task_type, String interaction_id, String application_type,
//            String media_type, String resource_type, String state_id, String timestamp) {
//        TaskBodyMax body = new TaskBodyMax(task_type, interaction_id, application_type, media_type,
//                resource_type, state_id, timestamp);
//        List<Map> feedbackTask = constructFeedbackExtension();
//        List<Map> satisfactionTask = constructSatisfactionExtension();
//        body.setFeedbackTask(feedbackTask);
//        body.setSatisfactionTask(satisfactionTask);
//        return body.toJSON();
//    }
//
//    /**
//     * Method creates request body for Create Profile requet
//     * @param FirstName
//     * @param LastName
//     * @param PhoneNumber
//     * @param emailsList
//     * @return
//     */
//    public static String constructProfileBody(String FirstName, String LastName, String PhoneNumber, List<String> emailsList) {
//
//        CustomerProfileBody body = new CustomerProfileBody(FirstName, LastName, PhoneNumber);
//        body.setEmailAddress(emailsList);
//
//        return body.toJSON();
//    }
//
//    /**
//     *
//     * @param FirstName
//     * @param LastName
//     * @param PhoneNumber
//     * @param emailsList
//     * @param title
//     * @param city
//     * @param language
//     * @return
//     */
//    public static String constructProfileBody(String FirstName, String LastName, String PhoneNumber, List<String> emailsList,String title,String city,String language) {
//
//        CustomerProfileExtendedBody body = new CustomerProfileExtendedBody(FirstName, LastName, PhoneNumber,title, city,language);
//        body.setEmailAddress(emailsList);
//
//        return body.toJSON();
//    }
//
//    /**
//     * construct body for purge services request
//     * @param limit
//     * @param operation
//     * @return
//     */
//    public static String constructPurgeRequestBody(String limit, String operation) {
//        PurgeRequestBody body = new PurgeRequestBody(limit, operation);
//        return body.toJSON();
//    }
//    //*********************************************************************************************

    /**
     * Method creates a list for Related Offers extension
     * 
     * @return
     */

    public static List<Map> constructRelatedOffersExtension() {
        List<Map> list = new ArrayList<Map>();
        Map<String, String> map = new HashMap<String, String>();
        map.put("offer_name", "VIP credit card black ed");
        map.put("type", "9");
        map.put("comments", "proposed to all clients");

        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("offer_name", "3 times payment GOLD");
        map2.put("type", "4");
        map2.put("comments", "imited offer");

        Map<String, String> map3 = new HashMap<String, String>();
        map3.put("offer_name", "life insurance");
        map3.put("type", "3");
        map3.put("comments", "health check to be done before approval");

        list.add(map);
        list.add(map2);
        list.add(map3);
        return list;
    }

    /**
     * Method creates a list for SatisfactionState extension
     * 
     * @return
     */
    public static List<Map> constructSatisfactionExtension() {
        List<Map> list = new ArrayList<Map>();
        Map<String, String> map = new HashMap<String, String>();
        map.put("rating", "2");
        map.put("pertinence", "8");
        map.put("useful", "true");
        map.put("place", "Terranova mexico resort");

        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("rating", "8");
        map2.put("pertinence", "4");
        map2.put("useful", "false");
        map2.put("place", "Fancy resort Paris");

        list.add(map);
        list.add(map2);

        return list;
    }
    /**
     * Method creates a list for FeedbackState extension
     * 
     * @return
     */
    public static List<Map> constructFeedbackExtension() {
        List<Map> list = new ArrayList<Map>();
        Map<String, String> map = new HashMap<String, String>();
        map.put("FeedbackType", "survey");
        map.put("rating", "7");
        map.put("notes", "warm welcome at frontdesk, thanks for the nice trip");

        list.add(map);

        return list;
    }

    private <T> void validateResponse(String body, Class<T> classOfT) {
        Gson gson = new Gson();
        T obj = gson.fromJson(body, classOfT);

    }
    //***********************************************************************
    //*********** supplementary methods for CS UI
    
    /**
     * 
     * @param number
     * @param email
     * @param firstName
     * @param lastName
     * @param phoneNumber
     */
//    public static void startCustomersWithSameData(int number,String email, String firstName, String lastName, String phoneNumber) {
//		  List<String> list = new ArrayList<String>();
//		  list.add(email);
//		  for (int i=0; i < number; i++ ){
//			  startCustomerProfile(firstName, lastName, phoneNumber, list );
//		  }
//
//	  }
    
    /**
     * 
     * @param email
     * @param firstName
     * @param lastName
     * @param phoneNumber
     * @return
     */
//    public static String startOneCustomer(String email, String firstName, String lastName, String phoneNumber) {
//		  List<String> list = new ArrayList<String>();
//		  list.add(email);
//
//		  String customer_id = startCustomerProfile(firstName, lastName, phoneNumber, list );
//
//		  return customer_id;
//	  }
    
    
    /**
     * 
     * @param email
     * @param firstName
     * @param lastName
     * @param phoneNumber
     * @param title
     * @param city
     * @param language
     * @return
     */
//    public static String startOneCustomer(String email, String firstName, String lastName, String phoneNumber,String title,String city,String language) {
//		  List<String> list = new ArrayList<String>();
//		  list.add(email);
//
//		  String customer_id = startCustomerProfile(firstName, lastName, phoneNumber, list,title,city,language );
//
//		  return customer_id;
//	  }
	  
    /**
     * 
     * @param FirstName
     * @param LastName
     * @param PhoneNumber
     * @param emailsList
     * @return
     */
//	  private static String startCustomerProfile(String FirstName, String LastName, String PhoneNumber, List<String> emailsList){
//
//		    String requestBody = constructProfileBody(FirstName, LastName,  PhoneNumber, emailsList);
//
//
//	        Response response = createCustomerProfile(requestBody);
//	        assertEquals(201, response.getStatusCode());
//
//	        String customer_id = getCustomerId(response);
//	        assertNotNull("Service_id is null",customer_id);
//
//	        return customer_id;
//	  }
	  
	  /**
	   * 
	   * @param FirstName
	   * @param LastName
	   * @param PhoneNumber
	   * @param emailsList
	   * @param title
	   * @param city
	   * @param language
	   * @return
	   */
//	  private static String startCustomerProfile(String FirstName, String LastName, String PhoneNumber, List<String> emailsList,String title,String city,String language){
//
//		    String requestBody = constructProfileBody(FirstName, LastName,  PhoneNumber, emailsList,title,city,language);
//
//
//	        Response response = createCustomerProfile(requestBody);
//	        assertEquals(201, response.getStatusCode());
//
//	        String customer_id = getCustomerId(response);
//	        assertNotNull("Service_id is null",customer_id);
//
//	        return customer_id;
//	  }

}
