package gms.tests;

import com.genesyslab.functional.tests.gms.helper.ContextServices;
import com.genesyslab.functional.tests.gms.psdk.Reconfiguration;
import com.genesyslab.functional.tests.gms.ui.GMSContextServicesPageNew;
import com.genesyslab.functional.tests.gms.ui.GMSLoginPage;
import com.genesyslab.functional.tests.gms.ui.GMSMainPage;
import com.genesyslab.platform.applicationblocks.com.ConfigException;
import io.restassured.response.Response;
import org.junit.*;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Map;

import static com.genesyslab.functional.tests.gms.helper.ContextServices.*;
import static junit.framework.Assert.*;
//import com.thoughtworks.selenium.webdriven.commands.GetSelectOptions;

/**
 * New Context Services UI tests since 8.5.103.XX
 *
 * @author igabduli
 */

public class CS_New_UI_Tests {

//    final static Logger logger = Logger.getLogger(CustomerJourneyUITests.class);
    private String baseUrl;
    private StringBuffer verificationErrors = new StringBuffer();

    private String username = "demo";
    private String password = "";

    private String service1Name = "MyService1";
    private String service2Name = "MyService2";
    private String service3Name = "MyService3";
    private String state1Name = "MyState1";
    private String state2Name = "MyState2";
    private String task1Name = "MyTask1";
    private String task2Name = "MyTask2";
    private String interaction_id = "123ABCAADFJ1259ACF";
    private String application_type = "App_type_1";
    private String media_type = "mymedia1";
    private String resource_type = "ResType1";

    private String requestBody = null;
    private Response response = null;
    private String service_id = null;

    /**
     * Error message we get in response
     */
    private String message = null;

    /**
     * Exception we get in response
     */
    private String exception = null;
    private String service_id1 = null;
    private String service_id2 = null;
    private String service_id3 = null;
    private String state_id = null;
    private String state_id1 = null;
    private String state_id2 = null;
    private String state_id3 = null;
    private String state_id4 = null;
    private String task_id1 = null;
    private String task_id2 = null;
    private String task_id3 = null;
    private String task_id4 = null;
    private String body = null;
    private Map<String, String> extension = null;
    private Map<String, String> parameters = null;
    private List<String> list = null;
    private String contact_key = "";

    private WebDriver driver;
    private GMSContextServicesPageNew csPage = null;
    private GMSLoginPage loginPage = null;
    private GMSMainPage mainPage = null;

    private ContextServices cs = null;

    public CS_New_UI_Tests() {
        cs = getInstance();
    }

    @BeforeClass
    public static void oneTimeSetUp() {
        //BV configureOptionsInGMS();
        //BV purgeServices(constructPurgeRequestBody(getCurrentTime(),
        //BV         "purge.service.all"));
        //BV DBUtils.deleteDataInUCS();
    }

    @AfterClass
    public static void oneTimeTearDown() {
//		 purgeServices(constructPurgeRequestBody(getCurrentTime(),
//		 "purge.service.all"));
//		 DBUtils.deleteDataInUCS();
    }

    @Before
    public void setUp() throws Exception {

//BV        driver = new FirefoxDriver();

//		System.setProperty("webdriver.chrome.driver", "/D:/Selenium/chromeDriver/chromedriver.exe");
//		driver = new ChromeDriver();

//		System.setProperty("webdriver.ie.driver", "/D:/Selenium/IEDriver/IEDriverServer.exe");
//		DesiredCapabilities capability=DesiredCapabilities.internetExplorer();
//		capability.setCapability(
//		InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
//		driver = new InternetExplorerDriver(capability);
//		System.out.println(driver.getCurrentUrl());
//		System.out.println(driver.getClass().getSimpleName());

//BV        baseUrl = getPropertyConfiguration("baseURL_UI");
//BV        driver.get(baseUrl + "genesys/admin/login.jsp");

//BV        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
//BV        driver.manage().window().maximize();


    }

    @After
    public void tearDown() throws Exception {
        //	logout(); //this method must be uncommented for IE testing
/* BV        if (driver != null) driver.quit();
        if (isIE()) Runtime.getRuntime().exec("taskkill /F /IM iexplorer.exe");

        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
        if (requestBody != null) {
            requestBody = null;
        }
        if (response != null) {
            response = null;
        }
        if (service_id != null) {
            service_id = null;
        }
        if (message != null) {
            message = null;
        }
        if (exception != null) {
            exception = null;
        }
        if (service_id1 != null) {
            service_id1 = null;
        }
        if (service_id2 != null) {
            service_id2 = null;
        }
        if (service_id3 != null) {
            service_id3 = null;
        }
        if (body != null) {
            body = null;
        }
        if (extension != null) {
            extension = null;
        }
        if (list != null) {
            list = null;
        }
        if (state_id != null) {
            state_id = null;
        }
        if (state_id1 != null) {
            state_id1 = null;
        }
        if (state_id2 != null) {
            state_id2 = null;
        }
        if (state_id3 != null) {
            state_id3 = null;
        }
        if (state_id4 != null) {
            state_id4 = null;
        }
        if (parameters != null) {
            parameters = null;
        }
        if (task_id1 != null) {
            task_id1 = null;
        }
        if (task_id2 != null) {
            task_id2 = null;
        }
        if (task_id3 != null) {
            task_id3 = null;
        }
        if (task_id4 != null) {
            task_id4 = null;
        }
        if (contact_key != null)
            contact_key = null;
 BV */
    }

    @Rule
    public TestRule rule = new TestWatcher() {
/* BV        @Override
        protected void failed(Throwable e, Description description) {
            System.out.println("Failed " + description);
            logger.info("Failed " + description);
        }
//	    @Override
//    protected void finished(Description description) {
//    System.out.println("Finished " + description + "\n");
//    logger.info("Finished " + description);
//    }
//	    @Override
//    protected void starting(Description description) {
//    System.out.println("Starting " + description);
//    logger.info("Starting " + description);
//    }
//	    @Override
//    protected void succeeded(Description description) {
//    System.out.println("Passed " + description);
//    logger.info("Passed " + description);
//    }
BV */
    };

    private static void configureOptionsInGMS() {
        Reconfiguration reconfiguration = new Reconfiguration();
        reconfiguration.openConnectionToConfig();

        try {
            //BV reconfiguration.addBusinessAttributesSection();
            reconfiguration.addServiceHello();
            reconfiguration.addORSSection("");
        } catch (ConfigException e) {
            e.printStackTrace();
        }

        reconfiguration.closeConnectionToConfig();
    }

    private boolean isChrome() {
        return driver.getClass().getSimpleName().equals("ChromeDriver");
    }

    private boolean isFirefox() {
        return driver.getClass().getSimpleName().equals("FirefoxDriver");
    }

    private boolean isIE() {
        return driver.getClass().getSimpleName().equals("InternetExplorerDriver");
    }


    private void logAllPages() {
        loginPage = new GMSLoginPage(driver);
        mainPage = loginPage.logIn(username, password);
        csPage = mainPage.clickContextServicesUINew();
    }

    private void logout() {
        csPage.clickLogOutButton();
    }

    //******************************* Tests ***************************************************

    @Test
    public void test_20_01() throws ConfigException {
        Reconfiguration reconfiguration = new Reconfiguration();
        reconfiguration.openConnectionToConfig();

        try {
            //BV reconfiguration.addBusinessAttributesSection();
            //reconfiguration.addServiceHello();
            reconfiguration.addORSSection("");
        } catch (ConfigException e) {
            e.printStackTrace();
        }

        reconfiguration.closeConnectionToConfig();
    }

    // **********************************************************************************************
    private void startActiveAndCompletedServicesAssociatedWithST(
            String customer_id) {
        // Start service 1 associated with customer profile
//        requestBody = constructOnCustomerServiceBody(service1Name, customer_id,
//                interaction_id, application_type, media_type, resource_type);

        response = startService(requestBody);
        assertEquals(201, response.getStatusCode());

        service_id1 = getServiceId(response);
        assertNotNull("Service_id is null", service_id1);

        // Complete service1
        Response r = completeService(service_id1, requestBody);
        assertEquals(204, r.getStatusCode());

        // start state1 on service
//        requestBody = constructStateBody(state1Name, interaction_id,
//                application_type, media_type, resource_type);
        response = startState(requestBody, service_id1);
        assertEquals(201, response.getStatusCode());
        state_id1 = getStateId(response);
        assertNotNull("State_id is null", state_id1);

        // start state2 on service
//        requestBody = constructStateBody(state2Name, interaction_id,
//                application_type, media_type, resource_type);
        response = startState(requestBody, service_id1);
        assertEquals(201, response.getStatusCode());
        state_id2 = getStateId(response);
        assertNotNull("State_id is null", state_id2);

        // complete state
        response = completeState(requestBody, service_id1, state_id2);
        assertEquals(204, response.getStatusCode());

        // start task on service
//        requestBody = constructTaskBody(task1Name, interaction_id,
//                application_type, media_type, resource_type);

        response = startTask(requestBody, service_id1);
        assertEquals(201, response.getStatusCode());
        task_id1 = getTaskId(response);
        assertNotNull("Task_id is null", task_id1);

        // start task2 on service
//        requestBody = constructTaskBody(task2Name, interaction_id,
//                application_type, media_type, resource_type);

        response = startTask(requestBody, service_id1);
        assertEquals(201, response.getStatusCode());
        task_id2 = getTaskId(response);
        assertNotNull("Task_id is null", task_id2);

        // end task2
        response = completeTask(requestBody, service_id1, task_id2);
        assertEquals(204, response.getStatusCode());

        // Get service1 by id
        response = getServiceByServiceId(service_id1);
        body = response.getBody().asString();

        assertTrue(contains(body, service1Name));
        assertTrue(contains(body, customer_id));
        assertEquals(200, response.getStatusCode());

        // Start service 2 associated with customer profile
//        requestBody = constructOnCustomerServiceBody(service2Name, customer_id,
//                interaction_id, application_type, media_type, resource_type);

        response = startService(requestBody);
        assertEquals(201, response.getStatusCode());

        service_id2 = getServiceId(response);
        assertNotNull("Service_id is null", service_id2);

        // Get service2 by id
        response = getServiceByServiceId(service_id2);
        body = response.getBody().asString();

        assertTrue(contains(body, service2Name));
        assertTrue(contains(body, customer_id));
        assertEquals(200, response.getStatusCode());

        // start state1 on service2
//        requestBody = constructStateBody(state1Name, interaction_id,
//                application_type, media_type, resource_type);
        response = startState(requestBody, service_id2);
        assertEquals(201, response.getStatusCode());
        state_id3 = getStateId(response);
        assertNotNull("State_id is null", state_id3);

        // start state2 on service
//        requestBody = constructStateBody(state2Name, interaction_id,
//                application_type, media_type, resource_type);
        response = startState(requestBody, service_id2);
        assertEquals(201, response.getStatusCode());
        state_id4 = getStateId(response);
        assertNotNull("State_id is null", state_id4);

        // complete state
        response = completeState(requestBody, service_id2, state_id4);
        assertEquals(204, response.getStatusCode());

        // start task on service2
//        requestBody = constructTaskBody(task1Name, interaction_id,
//                application_type, media_type, resource_type);

        response = startTask(requestBody, service_id2);
        assertEquals(201, response.getStatusCode());
        task_id3 = getTaskId(response);
        assertNotNull("State_id is null", task_id3);

        // start task2 on service2
//        requestBody = constructTaskBody(task2Name, interaction_id,
//                application_type, media_type, resource_type);

        response = startTask(requestBody, service_id2);
        assertEquals(201, response.getStatusCode());
        task_id4 = getTaskId(response);
        assertNotNull("State_id is null", task_id4);

        // end task4
        response = completeTask(requestBody, service_id2, task_id4);
        assertEquals(204, response.getStatusCode());

        // Start service 3 associated with customer profile
//        requestBody = constructOnCustomerServiceBody(service3Name, customer_id,
//                interaction_id, application_type, media_type, resource_type);

        response = startService(requestBody);
        assertEquals(201, response.getStatusCode());

        service_id3 = getServiceId(response);
        assertNotNull("Service_id is null", service_id3);

        // Complete service3
        r = completeService(service_id3, requestBody);
        assertEquals(204, r.getStatusCode());

        // Get service3 by id
        response = getServiceByServiceId(service_id3);
        body = response.getBody().asString();

        assertTrue(contains(body, service3Name));
        assertTrue(contains(body, customer_id));
        assertEquals(200, response.getStatusCode());

    }

    // state types and task types are MyState2, MyTask2
    private void startActiveAndCompletedServicesAssociatedWithST2(
            String customer_id) {
        // Start service 1 associated with customer profile
//        requestBody = constructOnCustomerServiceBody(service1Name, customer_id,
//                interaction_id, application_type, media_type, resource_type);

        response = startService(requestBody);
        assertEquals(201, response.getStatusCode());

        service_id1 = getServiceId(response);
        assertNotNull("Service_id is null", service_id1);

        // Complete service1
        Response r = completeService(service_id1, requestBody);
        assertEquals(204, r.getStatusCode());

        // start state1 on service
//        requestBody = constructStateBody(state2Name, interaction_id,
//                application_type, media_type, resource_type);
        response = startState(requestBody, service_id1);
        assertEquals(201, response.getStatusCode());
        state_id1 = getStateId(response);
        assertNotNull("State_id is null", state_id1);

        // start state2 on service
//        requestBody = constructStateBody(state2Name, interaction_id,
//                application_type, media_type, resource_type);
        response = startState(requestBody, service_id1);
        assertEquals(201, response.getStatusCode());
        state_id2 = getStateId(response);
        assertNotNull("State_id is null", state_id2);

        // complete state
        response = completeState(requestBody, service_id1, state_id2);
        assertEquals(204, response.getStatusCode());

        // start task on service
//        requestBody = constructTaskBody(task2Name, interaction_id,
//                application_type, media_type, resource_type);

        response = startTask(requestBody, service_id1);
        assertEquals(201, response.getStatusCode());
        task_id1 = getTaskId(response);
        assertNotNull("Task_id is null", task_id1);

        // start task2 on service
//        requestBody = constructTaskBody(task2Name, interaction_id,
//                application_type, media_type, resource_type);

        response = startTask(requestBody, service_id1);
        assertEquals(201, response.getStatusCode());
        task_id2 = getTaskId(response);
        assertNotNull("Task_id is null", task_id2);

        // end task2
        response = completeTask(requestBody, service_id1, task_id2);
        assertEquals(204, response.getStatusCode());

        // Get service1 by id
        response = getServiceByServiceId(service_id1);
        body = response.getBody().asString();

        assertTrue(contains(body, service1Name));
        assertTrue(contains(body, customer_id));
        assertEquals(200, response.getStatusCode());

        // Start service 2 associated with customer profile
//        requestBody = constructOnCustomerServiceBody(service2Name, customer_id,
//                interaction_id, application_type, media_type, resource_type);

        response = startService(requestBody);
        assertEquals(201, response.getStatusCode());

        service_id2 = getServiceId(response);
        assertNotNull("Service_id is null", service_id2);

        // Get service2 by id
        response = getServiceByServiceId(service_id2);
        body = response.getBody().asString();

        assertTrue(contains(body, service2Name));
        assertTrue(contains(body, customer_id));
        assertEquals(200, response.getStatusCode());

        // start state1 on service2
//        requestBody = constructStateBody(state2Name, interaction_id,
//                application_type, media_type, resource_type);
        response = startState(requestBody, service_id2);
        assertEquals(201, response.getStatusCode());
        state_id3 = getStateId(response);
        assertNotNull("State_id is null", state_id3);

        // start state2 on service
//        requestBody = constructStateBody(state2Name, interaction_id,
//                application_type, media_type, resource_type);
        response = startState(requestBody, service_id2);
        assertEquals(201, response.getStatusCode());
        state_id4 = getStateId(response);
        assertNotNull("State_id is null", state_id4);

        // complete state
        response = completeState(requestBody, service_id2, state_id4);
        assertEquals(204, response.getStatusCode());

        // start task on service2
//        requestBody = constructTaskBody(task2Name, interaction_id,
//                application_type, media_type, resource_type);

        response = startTask(requestBody, service_id2);
        assertEquals(201, response.getStatusCode());
        task_id3 = getTaskId(response);
        assertNotNull("State_id is null", task_id3);

        // start task2 on service2
//        requestBody = constructTaskBody(task2Name, interaction_id,
//                application_type, media_type, resource_type);

        response = startTask(requestBody, service_id2);
        assertEquals(201, response.getStatusCode());
        task_id4 = getTaskId(response);
        assertNotNull("State_id is null", task_id4);

        // end task4
        response = completeTask(requestBody, service_id2, task_id4);
        assertEquals(204, response.getStatusCode());

        // Start service 3 associated with customer profile
//        requestBody = constructOnCustomerServiceBody(service3Name, customer_id,
//                interaction_id, application_type, media_type, resource_type);

        response = startService(requestBody);
        assertEquals(201, response.getStatusCode());

        service_id3 = getServiceId(response);
        assertNotNull("Service_id is null", service_id3);

        // Complete service3
        r = completeService(service_id3, requestBody);
        assertEquals(204, r.getStatusCode());

        // Get service3 by id
        response = getServiceByServiceId(service_id3);
        body = response.getBody().asString();

        assertTrue(contains(body, service3Name));
        assertTrue(contains(body, customer_id));
        assertEquals(200, response.getStatusCode());

    }

    private void startActiveAndCompletedServicesAssociatedWithTonS(
            String customer_id) {
        // Start service 1 associated with customer profile
//        requestBody = constructOnCustomerServiceBody(service1Name, customer_id,
//                interaction_id, application_type, media_type, resource_type);

        response = startService(requestBody);
        assertEquals(201, response.getStatusCode());

        service_id1 = getServiceId(response);
        assertNotNull("Service_id is null", service_id1);

        // Complete service1
        Response r = completeService(service_id1, requestBody);
        assertEquals(204, r.getStatusCode());

        // start state1 on service
//        requestBody = constructStateBody(state1Name, interaction_id,
//                application_type, media_type, resource_type);
        response = startState(requestBody, service_id1);
        assertEquals(201, response.getStatusCode());
        state_id1 = getStateId(response);
        assertNotNull("State_id is null", state_id1);

        // start state2 on service
//        requestBody = constructStateBody(state2Name, interaction_id,
//                application_type, media_type, resource_type);
        response = startState(requestBody, service_id1);
        assertEquals(201, response.getStatusCode());
        state_id2 = getStateId(response);
        assertNotNull("State_id is null", state_id2);

        // complete state
        response = completeState(requestBody, service_id1, state_id2);
        assertEquals(204, response.getStatusCode());

        // start task on service on state
//        requestBody = constructTaskBodyWithStateId(task1Name, interaction_id,
//                application_type, media_type, resource_type, state_id1);

        response = startTask(requestBody, service_id1);
        assertEquals(201, response.getStatusCode());
        task_id1 = getTaskId(response);
        assertNotNull("Task_id is null", task_id1);

        // start task2 on service on state
//        requestBody = constructTaskBodyWithStateId(task2Name, interaction_id,
//                application_type, media_type, resource_type, state_id1);

        response = startTask(requestBody, service_id1);
        assertEquals(201, response.getStatusCode());
        task_id2 = getTaskId(response);
        assertNotNull("Task_id is null", task_id2);

        // end task2
        response = completeTask(requestBody, service_id1, task_id2);
        assertEquals(204, response.getStatusCode());

        // Get service1 by id
        response = getServiceByServiceId(service_id1);
        body = response.getBody().asString();

        assertTrue(contains(body, service1Name));
        assertTrue(contains(body, customer_id));
        assertEquals(200, response.getStatusCode());

        // Start service 2 associated with customer profile
//        requestBody = constructOnCustomerServiceBody(service2Name, customer_id,
//                interaction_id, application_type, media_type, resource_type);

        response = startService(requestBody);
        assertEquals(201, response.getStatusCode());

        service_id2 = getServiceId(response);
        assertNotNull("Service_id is null", service_id2);

        // Get service2 by id
        response = getServiceByServiceId(service_id2);
        body = response.getBody().asString();

        assertTrue(contains(body, service2Name));
        assertTrue(contains(body, customer_id));
        assertEquals(200, response.getStatusCode());

        // start state1 on service2
//        requestBody = constructStateBody(state1Name, interaction_id,
//                application_type, media_type, resource_type);
        response = startState(requestBody, service_id2);
        assertEquals(201, response.getStatusCode());
        state_id3 = getStateId(response);
        assertNotNull("State_id is null", state_id3);

        // start state2 on service
//        requestBody = constructStateBody(state2Name, interaction_id,
//                application_type, media_type, resource_type);
        response = startState(requestBody, service_id2);
        assertEquals(201, response.getStatusCode());
        state_id4 = getStateId(response);
        assertNotNull("State_id is null", state_id4);

        // complete state
        response = completeState(requestBody, service_id2, state_id4);
        assertEquals(204, response.getStatusCode());

        // start task on service2
//        requestBody = constructTaskBodyWithStateId(task1Name, interaction_id,
//                application_type, media_type, resource_type, state_id3);

        response = startTask(requestBody, service_id2);
        assertEquals(201, response.getStatusCode());
        task_id3 = getTaskId(response);
        assertNotNull("State_id is null", task_id3);

        // start task2 on service2 on state
//        requestBody = constructTaskBodyWithStateId(task2Name, interaction_id,
//                application_type, media_type, resource_type, state_id3);

        response = startTask(requestBody, service_id2);
        assertEquals(201, response.getStatusCode());
        task_id4 = getTaskId(response);
        assertNotNull("State_id is null", task_id4);

        // end task4
        response = completeTask(requestBody, service_id2, task_id4);
        assertEquals(204, response.getStatusCode());

        // Start service 3 associated with customer profile
//        requestBody = constructOnCustomerServiceBody(service3Name, customer_id,
//                interaction_id, application_type, media_type, resource_type);

        response = startService(requestBody);
        assertEquals(201, response.getStatusCode());

        service_id3 = getServiceId(response);
        assertNotNull("Service_id is null", service_id3);

        // Complete service3
        r = completeService(service_id3, requestBody);
        assertEquals(204, r.getStatusCode());

        // Get service3 by id
        response = getServiceByServiceId(service_id3);
        body = response.getBody().asString();

        assertTrue(contains(body, service3Name));
        assertTrue(contains(body, customer_id));
        assertEquals(200, response.getStatusCode());

    }

    private void startActiveAndCompletedServicesAnonymousWithST(
            String contact_key) {
        // Start service 1 associated with customer profile
//        requestBody = constructAnonymousServiceBody(service1Name, contact_key,
//                interaction_id, application_type, media_type, resource_type);

        response = startService(requestBody);
        assertEquals(201, response.getStatusCode());

        service_id1 = getServiceId(response);
        assertNotNull("Service_id is null", service_id1);

        // Complete service1
        Response r = completeService(service_id1, requestBody);
        assertEquals(204, r.getStatusCode());

        // start state1 on service
//        requestBody = constructStateBody(state1Name, interaction_id,
//                application_type, media_type, resource_type);
        response = startState(requestBody, service_id1);
        assertEquals(201, response.getStatusCode());
        state_id1 = getStateId(response);
        assertNotNull("State_id is null", state_id1);

        // start state2 on service
//        requestBody = constructStateBody(state2Name, interaction_id,
//                application_type, media_type, resource_type);
        response = startState(requestBody, service_id1);
        assertEquals(201, response.getStatusCode());
        state_id2 = getStateId(response);
        assertNotNull("State_id is null", state_id2);

        // complete state
        response = completeState(requestBody, service_id1, state_id2);
        assertEquals(204, response.getStatusCode());

        // start task on service
//        requestBody = constructTaskBody(task1Name, interaction_id,
//                application_type, media_type, resource_type);

        response = startTask(requestBody, service_id1);
        assertEquals(201, response.getStatusCode());
        task_id1 = getTaskId(response);
        assertNotNull("State_id is null", task_id1);

        // start task2 on service
//        requestBody = constructTaskBody(task2Name, interaction_id,
//                application_type, media_type, resource_type);

        response = startTask(requestBody, service_id1);
        assertEquals(201, response.getStatusCode());
        task_id2 = getTaskId(response);
        assertNotNull("State_id is null", task_id2);

        // end task2
        response = completeTask(requestBody, service_id1, task_id2);
        assertEquals(204, response.getStatusCode());

        // Get service1 by id
        response = getServiceByServiceId(service_id1);
        body = response.getBody().asString();

        assertTrue(contains(body, service1Name));
        assertTrue(contains(body, contact_key));
        assertEquals(200, response.getStatusCode());

        // Start service 2 associated with customer profile
//        requestBody = constructAnonymousServiceBody(service2Name, contact_key,
//                interaction_id, application_type, media_type, resource_type);

        response = startService(requestBody);
        assertEquals(201, response.getStatusCode());

        service_id2 = getServiceId(response);
        assertNotNull("Service_id is null", service_id2);

        // Get service2 by id
        response = getServiceByServiceId(service_id2);
        body = response.getBody().asString();

        assertTrue(contains(body, service2Name));
        assertTrue(contains(body, contact_key));
        assertEquals(200, response.getStatusCode());

        // start state1 on service2
//        requestBody = constructStateBody(state1Name, interaction_id,
//                application_type, media_type, resource_type);
        response = startState(requestBody, service_id2);
        assertEquals(201, response.getStatusCode());
        state_id3 = getStateId(response);
        assertNotNull("State_id is null", state_id3);

        // start state2 on service
//        requestBody = constructStateBody(state2Name, interaction_id,
//                application_type, media_type, resource_type);
        response = startState(requestBody, service_id2);
        assertEquals(201, response.getStatusCode());
        state_id4 = getStateId(response);
        assertNotNull("State_id is null", state_id4);

        // complete state
        response = completeState(requestBody, service_id2, state_id4);
        assertEquals(204, response.getStatusCode());

        // start task on service2
//        requestBody = constructTaskBody(task1Name, interaction_id,
//                application_type, media_type, resource_type);

        response = startTask(requestBody, service_id2);
        assertEquals(201, response.getStatusCode());
        task_id3 = getTaskId(response);
        assertNotNull("State_id is null", task_id3);

        // start task2 on service2
//        requestBody = constructTaskBody(task2Name, interaction_id,
//                application_type, media_type, resource_type);

        response = startTask(requestBody, service_id2);
        assertEquals(201, response.getStatusCode());
        task_id4 = getTaskId(response);
        assertNotNull("State_id is null", task_id4);

        // end task4
        response = completeTask(requestBody, service_id2, task_id4);
        assertEquals(204, response.getStatusCode());

        // Start service 3 associated with customer profile
//        requestBody = constructAnonymousServiceBody(service3Name, contact_key,
//                interaction_id, application_type, media_type, resource_type);

        response = startService(requestBody);
        assertEquals(201, response.getStatusCode());

        service_id3 = getServiceId(response);
        assertNotNull("Service_id is null", service_id3);

        // Complete service3
        r = completeService(service_id3, requestBody);
        assertEquals(204, r.getStatusCode());

        // Get service3 by id
        response = getServiceByServiceId(service_id3);
        body = response.getBody().asString();

        assertTrue(contains(body, service3Name));
        assertTrue(contains(body, contact_key));
        assertEquals(200, response.getStatusCode());

    }

    // state and task types are MyState2 and MyTask2
    private void startActiveAndCompletedServicesAnonymousWithST2(
            String contact_key) {
        // Start service 1 associated with customer profile
//        requestBody = constructAnonymousServiceBody(service1Name, contact_key,
//                interaction_id, application_type, media_type, resource_type);

        response = startService(requestBody);
        assertEquals(201, response.getStatusCode());

        service_id1 = getServiceId(response);
        assertNotNull("Service_id is null", service_id1);

        // Complete service1
        Response r = completeService(service_id1, requestBody);
        assertEquals(204, r.getStatusCode());

        // start state1 on service
//        requestBody = constructStateBody(state2Name, interaction_id,
//                application_type, media_type, resource_type);
        response = startState(requestBody, service_id1);
        assertEquals(201, response.getStatusCode());
        state_id1 = getStateId(response);
        assertNotNull("State_id is null", state_id1);

        // start state2 on service
//        requestBody = constructStateBody(state2Name, interaction_id,
//                application_type, media_type, resource_type);
        response = startState(requestBody, service_id1);
        assertEquals(201, response.getStatusCode());
        state_id2 = getStateId(response);
        assertNotNull("State_id is null", state_id2);

        // complete state
        response = completeState(requestBody, service_id1, state_id2);
        assertEquals(204, response.getStatusCode());

        // start task on service
//        requestBody = constructTaskBody(task2Name, interaction_id,
//                application_type, media_type, resource_type);

        response = startTask(requestBody, service_id1);
        assertEquals(201, response.getStatusCode());
        task_id1 = getTaskId(response);
        assertNotNull("State_id is null", task_id1);

        // start task2 on service
//        requestBody = constructTaskBody(task2Name, interaction_id,
//                application_type, media_type, resource_type);

        response = startTask(requestBody, service_id1);
        assertEquals(201, response.getStatusCode());
        task_id2 = getTaskId(response);
        assertNotNull("State_id is null", task_id2);

        // end task2
        response = completeTask(requestBody, service_id1, task_id2);
        assertEquals(204, response.getStatusCode());

        // Get service1 by id
        response = getServiceByServiceId(service_id1);
        body = response.getBody().asString();

        assertTrue(contains(body, service1Name));
        assertTrue(contains(body, contact_key));
        assertEquals(200, response.getStatusCode());

        // Start service 2 associated with customer profile
//        requestBody = constructAnonymousServiceBody(service2Name, contact_key,
//                interaction_id, application_type, media_type, resource_type);

        response = startService(requestBody);
        assertEquals(201, response.getStatusCode());

        service_id2 = getServiceId(response);
        assertNotNull("Service_id is null", service_id2);

        // Get service2 by id
        response = getServiceByServiceId(service_id2);
        body = response.getBody().asString();

        assertTrue(contains(body, service2Name));
        assertTrue(contains(body, contact_key));
        assertEquals(200, response.getStatusCode());

        // start state1 on service2
//        requestBody = constructStateBody(state2Name, interaction_id,
//                application_type, media_type, resource_type);
        response = startState(requestBody, service_id2);
        assertEquals(201, response.getStatusCode());
        state_id3 = getStateId(response);
        assertNotNull("State_id is null", state_id3);

        // start state2 on service
//        requestBody = constructStateBody(state2Name, interaction_id,
//                application_type, media_type, resource_type);
        response = startState(requestBody, service_id2);
        assertEquals(201, response.getStatusCode());
        state_id4 = getStateId(response);
        assertNotNull("State_id is null", state_id4);

        // complete state
        response = completeState(requestBody, service_id2, state_id4);
        assertEquals(204, response.getStatusCode());

        // start task on service2
//        requestBody = constructTaskBody(task2Name, interaction_id,
//                application_type, media_type, resource_type);

        response = startTask(requestBody, service_id2);
        assertEquals(201, response.getStatusCode());
        task_id3 = getTaskId(response);
        assertNotNull("State_id is null", task_id3);

        // start task2 on service2
//        requestBody = constructTaskBody(task2Name, interaction_id,
//                application_type, media_type, resource_type);

        response = startTask(requestBody, service_id2);
        assertEquals(201, response.getStatusCode());
        task_id4 = getTaskId(response);
        assertNotNull("State_id is null", task_id4);

        // end task4
        response = completeTask(requestBody, service_id2, task_id4);
        assertEquals(204, response.getStatusCode());

        // Start service 3 associated with customer profile
//        requestBody = constructAnonymousServiceBody(service3Name, contact_key,
//                interaction_id, application_type, media_type, resource_type);

        response = startService(requestBody);
        assertEquals(201, response.getStatusCode());

        service_id3 = getServiceId(response);
        assertNotNull("Service_id is null", service_id3);

        // Complete service3
        r = completeService(service_id3, requestBody);
        assertEquals(204, r.getStatusCode());

        // Get service3 by id
        response = getServiceByServiceId(service_id3);
        body = response.getBody().asString();

        assertTrue(contains(body, service3Name));
        assertTrue(contains(body, contact_key));
        assertEquals(200, response.getStatusCode());

    }

    private void startActiveAndCompletedServicesAnonymousWithTonS(
            String contact_key) {
        // Start service 1 associated with customer profile
//        requestBody = constructAnonymousServiceBody(service1Name, contact_key,
//                interaction_id, application_type, media_type, resource_type);

        response = startService(requestBody);
        assertEquals(201, response.getStatusCode());

        service_id1 = getServiceId(response);
        assertNotNull("Service_id is null", service_id1);

        // Complete service1
        Response r = completeService(service_id1, requestBody);
        assertEquals(204, r.getStatusCode());

        // start state1 on service
//        requestBody = constructStateBody(state1Name, interaction_id,
//                application_type, media_type, resource_type);
        response = startState(requestBody, service_id1);
        assertEquals(201, response.getStatusCode());
        state_id1 = getStateId(response);
        assertNotNull("State_id is null", state_id1);

        // start state2 on service
//        requestBody = constructStateBody(state2Name, interaction_id,
//                application_type, media_type, resource_type);
        response = startState(requestBody, service_id1);
        assertEquals(201, response.getStatusCode());
        state_id2 = getStateId(response);
        assertNotNull("State_id is null", state_id2);

        // complete state
        response = completeState(requestBody, service_id1, state_id2);
        assertEquals(204, response.getStatusCode());

        // start task on service on state
//        requestBody = constructTaskBodyWithStateId(task1Name, interaction_id,
//                application_type, media_type, resource_type, state_id1);

        response = startTask(requestBody, service_id1);
        assertEquals(201, response.getStatusCode());
        task_id1 = getTaskId(response);
        assertNotNull("State_id is null", task_id1);

        // start task2 on service
//        requestBody = constructTaskBodyWithStateId(task2Name, interaction_id,
//                application_type, media_type, resource_type, state_id1);

        response = startTask(requestBody, service_id1);
        assertEquals(201, response.getStatusCode());
        task_id2 = getTaskId(response);
        assertNotNull("State_id is null", task_id2);

        // end task2
        response = completeTask(requestBody, service_id1, task_id2);
        assertEquals(204, response.getStatusCode());

        // Get service1 by id
        response = getServiceByServiceId(service_id1);
        body = response.getBody().asString();

        assertTrue(contains(body, service1Name));
        assertTrue(contains(body, contact_key));
        assertEquals(200, response.getStatusCode());

        // Start service 2 associated with customer profile
//        requestBody = constructAnonymousServiceBody(service2Name, contact_key,
//                interaction_id, application_type, media_type, resource_type);

        response = startService(requestBody);
        assertEquals(201, response.getStatusCode());

        service_id2 = getServiceId(response);
        assertNotNull("Service_id is null", service_id2);

        // Get service2 by id
        response = getServiceByServiceId(service_id2);
        body = response.getBody().asString();

        assertTrue(contains(body, service2Name));
        assertTrue(contains(body, contact_key));
        assertEquals(200, response.getStatusCode());

        // start state1 on service2
//        requestBody = constructStateBody(state1Name, interaction_id,
//                application_type, media_type, resource_type);
        response = startState(requestBody, service_id2);
        assertEquals(201, response.getStatusCode());
        state_id3 = getStateId(response);
        assertNotNull("State_id is null", state_id3);

        // start state2 on service
//        requestBody = constructStateBody(state2Name, interaction_id,
//                application_type, media_type, resource_type);
        response = startState(requestBody, service_id2);
        assertEquals(201, response.getStatusCode());
        state_id4 = getStateId(response);
        assertNotNull("State_id is null", state_id4);

        // complete state
        response = completeState(requestBody, service_id2, state_id4);
        assertEquals(204, response.getStatusCode());

        // start task on service2 on state
//        requestBody = constructTaskBodyWithStateId(task1Name, interaction_id,
//                application_type, media_type, resource_type, state_id3);

        response = startTask(requestBody, service_id2);
        assertEquals(201, response.getStatusCode());
        task_id3 = getTaskId(response);
        assertNotNull("State_id is null", task_id3);

        // start task2 on service2
//        requestBody = constructTaskBody(task2Name, interaction_id,
//                application_type, media_type, resource_type);

        response = startTask(requestBody, service_id2);
        assertEquals(201, response.getStatusCode());
        task_id4 = getTaskId(response);
        assertNotNull("State_id is null", task_id4);

        // end task4
        response = completeTask(requestBody, service_id2, task_id4);
        assertEquals(204, response.getStatusCode());

        // Start service 3 associated with customer profile
//        requestBody = constructAnonymousServiceBody(service3Name, contact_key,
//                interaction_id, application_type, media_type, resource_type);

        response = startService(requestBody);
        assertEquals(201, response.getStatusCode());

        service_id3 = getServiceId(response);
        assertNotNull("Service_id is null", service_id3);

        // Complete service3
        r = completeService(service_id3, requestBody);
        assertEquals(204, r.getStatusCode());

        // Get service3 by id
        response = getServiceByServiceId(service_id3);
        body = response.getBody().asString();

        assertTrue(contains(body, service3Name));
        assertTrue(contains(body, contact_key));
        assertEquals(200, response.getStatusCode());

    }

    // **********************************************************************************************
    private boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    // ======================helper methods==========================


//    private Profile initializeCustomer(String email, String firstName,
//                                       String lastName, String phoneNumber) {
//        Profile customer = new Profile();
//        List<String> list = new ArrayList<String>();
//        list.add(email);
//        customer.setEmailAddresses(list);
//        customer.setFirstName(firstName);
//        customer.setLastName(lastName);
//        customer.setPhoneNumber(phoneNumber);
//        return customer;
//    }
}
