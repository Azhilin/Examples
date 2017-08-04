package gms.ui;

import com.genesyslab.functional.tests.gms.psdk.Reconfiguration;
import com.genesyslab.functional.tests.gms.ui.callback.GMSCallbackPage;
import com.genesyslab.functional.tests.gms.ui.configuredservices.GMSConfiguredServicesPage;
import org.junit.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertiesFile;
import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertyConfiguration;
import static com.genesyslab.functional.tests.gms.helper.SectionOptions.getGMSCviewSectionOptions;
import static com.genesyslab.functional.tests.gms.helper.TimeOperations.getMethodExecutionTime;
import static com.genesyslab.functional.tests.gms.helper.URLsHelper.getGMSFullLoginURL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;

/**
 * Created by bvolovyk on 03.10.2016.
 * First commented part of the code was written by Selenide using
 */

/*
 * [Test Suite] RBA for GMS: https://jira.genesys.com/browse/GMS-2957
 */
public class RBAForGMSTests {
//    @Before
//    public void setUp() {
//        System.setProperty("webdriver.chrome.driver", "./src/resources/drivers/chromedriver.exe");
//        Configuration.browser = "chrome";
////        Configuration.timeout = 20000; //configure implicitWait
//    }
//
//    @Test
//    public void noLoginWithoutPersonGMSSection() throws Exception {
//        open(getPropertyConfiguration("base.url"));
//        $("#user").setValue("gms_no_access");
//        $("#pass").setValue("password");
//        $(byText("Log In")).click();
//        $(".-ersample-loginror-messages.ng-binding").shouldHave(matchText("Incorrect username and/or password."));
//    }

    private static String propertiesFile = getPropertiesFile();
    //    private static String propertiesFile = "./config_2nd.properties";//uncomment this line in case you want to use another .properties file
    private static Reconfiguration env = new Reconfiguration(propertiesFile);
    private static String gmsClusterAppName = getPropertyConfiguration(propertiesFile,"gms.cluster.app.name");
    private static String gmsUrl = getGMSFullLoginURL(propertiesFile);
    private static String callbackImmServiceName = "cb_term_im_test";

    private WebDriver driver = null;
    private GMSLoginPage loginPage = null;
    private GMSMainPage mainPage = null;
    private GMSConfiguredServicesPage configuredServicesPage = null;
    private GMSCallbackPage callbackPage = null;

    @BeforeClass
    public static void oneTimeSetUp() throws Exception {
        long startTime = System.nanoTime();
        System.out.println("@BeforeClass method processing...");
        env.addSection(gmsClusterAppName, "cview", getGMSCviewSectionOptions());
        env.changeOptionValueInSection(gmsClusterAppName, "cview", "allow-custom-ids", "true");
        env.changeOptionValueInSection(gmsClusterAppName, "cview", "data-validation", "true");
        env.changeOptionValueInSection(gmsClusterAppName, "cview", "enabled", "true");
        env.changeOptionValueInSection(gmsClusterAppName, "cview", "use-role", "true");
//            cfgManager.getPersonApi().setFirstName("gms_no_access2");
//            cfgManager.getPersonApi().setEmployeeId("gms_no_access2");
//            cfgManager.getPersonApi().setUserName("gms_no_access2");
//            cfgManager.getPersonApi().setPassword("gms_no_access2");
//            cfgManager.getPersonApi().setIsAgent(false);
//            cfgManager.getPersonApi().createNewPerson();
//            List<CfgAppRank> demoRank = (List<CfgAppRank>) cfgManager.getPersonApi().getPerson("demo").getAppRanks();
        long endTime = System.nanoTime();
        getMethodExecutionTime(startTime, endTime, "@BeforeClass");
    }

    @Before
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(gmsUrl);

        loginPage = new GMSLoginPage(driver);
    }

    @After
    public void tearDown() {
        if (driver != null)
            driver.quit();
    }

    @AfterClass
    public static void oneTimeTearDown() throws Exception {
        System.out.print("@AfterClass method processing...: ");
        env.deleteService(gmsClusterAppName, callbackImmServiceName); //for troubleshooting purposes comment this command
        env.deactivate();
    }

    @Test
    public void test_01() {
        //TODO: in GMS app object in Security tab/Log On As manually change to "SYSTEM Account" and restart application.
        loginPage.typeUsername("gms_no_access");
        loginPage.typePassword("gms_no_access");
        loginPage.clickLogin();
        assertThat(loginPage.getErrorMessage(), containsString("Incorrect username and/or password."));
    }

    @Test
    public void test_02() {
        //TODO: in GMS app object in Security tab/Log On As manually change to "SYSTEM Account" and restart application.
        mainPage = loginPage.logIn("gms_supervisor", "gms_supervisor");
        assertThat(mainPage.isAdminUIPresent(), is(false));
        assertThat(mainPage.isCallbackAndMobileEngagementPresent(), is(true));
        callbackPage = mainPage.goToCallbackManagementUIPage();
        assertThat(callbackPage.isRefreshBtnPresent(), is(true));
    }

    @Test
    public void test_03() {
        //TODO: in GMS app object in Security tab/Log On As manually change to "SYSTEM Account" and restart application.
        mainPage = loginPage.logIn("gms_administrator", "gms_administrator");
        assertThat(mainPage.isAdminUIPresent(), is(true));
        assertThat(mainPage.isCallbackAndMobileEngagementPresent(), is(true));
        assertThat(mainPage.isContextServicesPresent(), is(true));
        configuredServicesPage = mainPage.goToConfiguredServicesUIPage();
        configuredServicesPage.createCallbackService(callbackImmServiceName, "User Terminated Immediate");
        assertThat(configuredServicesPage.getDisplayedServiceName(), containsString(callbackImmServiceName));
    }
}