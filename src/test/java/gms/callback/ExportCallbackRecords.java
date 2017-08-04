package gms.callback;

import com.genesyslab.functional.tests.gms.files.ReadingCSVFile;
import com.genesyslab.functional.tests.gms.helper.CallbackServicesAPI;
import com.genesyslab.functional.tests.gms.psdk.Reconfiguration;
import com.genesyslab.functional.tests.gms.ui.GMSLoginPage;
import com.genesyslab.functional.tests.gms.ui.GMSMainPage;
import com.genesyslab.functional.tests.gms.ui.callback.GMSCallbackPage;
import com.genesyslab.functional.tests.gms.ui.callback.GMSDownloadReportsPopUpPage;
import io.restassured.response.Response;
import org.junit.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.genesyslab.functional.tests.gms.helper.CallbackServiceHelper.*;
import static com.genesyslab.functional.tests.gms.helper.CallbackServicesAPI.*;
import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertyConfiguration;
import static com.genesyslab.functional.tests.gms.helper.ServiceOptions.*;
import static com.genesyslab.functional.tests.gms.helper.TimeOperations.*;
import static com.genesyslab.functional.tests.gms.helper.URLsHelper.getGMSBaseURL;
import static com.genesyslab.functional.tests.gms.helper.URLsHelper.getGMSFullLoginURL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.time.LocalDate;
//import static com.genesyslab.functional.tests.gms.helper.CallbackServiceHelper.getCallbackDetailByKey;
//import static com.genesyslab.functional.tests.gms.helper.CallbackServiceHelper.countCompletedCallbacksByReason;

/*
 * created by Olga Ukolova on 06/05/2016
 */

public class ExportCallbackRecords {

    private static String propertiesFile = "./config_2nd.properties";//uncomment this line in case you want to use another .properties file
	private static Reconfiguration env = new Reconfiguration(propertiesFile);

	private static String gmsAppName = getPropertyConfiguration(propertiesFile, "gms.app.name");//uncomment this line in case you want to use another application name
	private static String gmsBaseUrl = getGMSBaseURL(propertiesFile);
	private static String gmsUrl = getGMSFullLoginURL(propertiesFile);
	private static String username = getPropertyConfiguration(propertiesFile, "gms.user.name");
	private static String password = getPropertyConfiguration(propertiesFile, "gms.user.password");
	private static String customerNumber = "5115";
	
	private static final long TIME_FOR_FILE_DOWNLOADING = 2;
		
	private static String callbackServiceName_sch = "cb_gms-4378_sch";
	private static String BHServiceName = "bh_24x7";
    private static String capacityServiceName = "cap_1000x24x7";
    
    private String downloadDirectory = "C:\\Users\\oukolova\\Downloads\\";
    private String reportFileName = "CancelledCallbacks";
    
    private WebDriver driver = null;
    private GMSMainPage mainPage = null;
    private GMSCallbackPage callbackPage = null;
    private GMSDownloadReportsPopUpPage downloadReportsPopUpPage = null;
    	
	private static int daysAgo=30;
	private static int daysAfter=15;
	       
    @BeforeClass
    public static void oneTimeSetUp() throws Exception {
        long startTime = System.nanoTime();
        System.out.println("@BeforeClass method processing...");
        CallbackServicesAPI.setGMSBaseURL(getGMSBaseURL(propertiesFile));
        long endTime = System.nanoTime();
        getMethodExecutionTime(startTime, endTime, "@BeforeClass");
     
        String dS=String.valueOf(getDateDaysBack(0, daysAgo, DATE_PATTERN))+BEGIN_OF_DAY_SUFFIX;
        System.out.println(dS);
     	long seconds = Timestamp.valueOf(dS).getTime() /1000;
        System.out.println(seconds);
            
        env.createService(gmsAppName, callbackServiceName_sch, getUserTermSchCallbackServiceOptions());
        env.createService(gmsAppName, BHServiceName, getRegularOfficeHours());
        env.createService(gmsAppName, capacityServiceName, getCapacityOptions());       		
        env.addOptionToSection(gmsAppName, "service." + callbackServiceName_sch, "_reject_past_desired_time", String.valueOf(seconds));
    }
  
    @AfterClass
    public static void oneTimeTearDown() throws Exception {
        System.out.println("@AfterClass method processing...");
        env.deleteService(gmsAppName, callbackServiceName_sch); //for troubleshooting purposes comment this command
        env.deleteService(gmsAppName, BHServiceName); //for troubleshooting purposes comment this command
        env.deleteService(gmsAppName, capacityServiceName); //for troubleshooting purposes comment this command
        env.deactivate();
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
    public void tearDown() {
        System.out.println("@After method processing...");
        cancelUnfinishedCallbacks(callbackServiceName_sch);
        if (driver != null)
            driver.quit();
        //deleteFileInDir(downloadDirectory, reportFileName);
    }

    
    @Test
    // 
    public void test1() throws Exception {
    	String desiredTime = null;  
    	List<String> cbIDsList = new ArrayList<String>();
    	List<String> expProperties = new ArrayList<String>();
    	
    	for (int i=1; i<5; i++)
    	{
    		desiredTime=getDesiredTimeInPeriod(daysAgo,daysAfter);
    		System.out.println(desiredTime.toString());
    		
    		//create scheduled callback
		    Response scheduleCallback1 = startScheduledCallback(callbackServiceName_sch,"5115", desiredTime);
		        
		    String cbID = scheduleCallback1.then().extract().path("_id");
		    cbIDsList.add(cbID);
		    //assertThat(getCallbackDetailByKey(cbID, "_callback_state"),equalTo("SCHEDULED"));
		    assertThat(scheduleCallback1.getStatusCode(),equalTo(200));
		        
		    //wait 5 sec
	        Thread.sleep(1000);                 		
    	}
    	
    	//cancel callbacks with CANCELLED_BY_ADMIN reason
    	cancelCallbacksByReason("CANCELLED_BY_ADMIN", cbIDsList);
    	           	
    	//create URI parameters for request
        expProperties.add("_service_id");
    	
    	Response export=exportCancelled("CANCELLED_BY_ADMIN", expProperties);
    	//System.out.println("Response body for callback EXPORT: " +  export.then().extract().response().asString());  
    	    	  	   
        //go to Callback Management UI page and set Date Filter
        callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");
        
        //on the Download Reports Dialogue click Export button
        downloadReportsPopUpPage = callbackPage.downloadReports();
        downloadReportsPopUpPage.clickExport();
        downloadReportsPopUpPage.closeDownloadReports();
        
        Thread.sleep(TIME_FOR_FILE_DOWNLOADING * 1000);
        
        //verify that amount of callbacks items in the downloaded file is equal to amount of callbacks with CANCELLED_BY_ADMIN reason
        int c1=countCompletedCallbacksByReason("5115", "CANCELLED_BY_ADMIN");
    	int c2=ReadingCSVFile.getCountOfLinesFromCSV(downloadDirectory+reportFileName+".csv")-1; //exclude title line
        System.out.println("amount of callbacks with CANCELLED_BY_ADMIN reason: " + c1);
       	System.out.println("amount of callbacks items in the downloaded file: " + c2);
      	assertThat(c1,equalTo(c2));
    	    	
    	//System.out.println("Content of file" + ReadingCSVFile.getLinesFromCSV(fileName));        	     	
    }     	
}
