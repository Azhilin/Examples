package gms.ui;

import com.genesyslab.functional.tests.gms.ui.configuredservices.GMSConfiguredServicesPage;
import org.junit.*;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

/**
 * Created by azhilin on 03.08.2017.
 */
public class TempClassForTest_NewUI_Tests {
    static WebDriver driver;

    private static Properties properties = new Properties();

    private static String gmsHost;
    private static String gmsPort;

    private static String gmsUrl;
    private static String gmsPatternsApiURL;

    private static GMSLoginPage loginPage = null;
    private static GMSMainPage mainPage = null;
    private static GMSConfiguredServicesPage configuredServicesPage = null;
    private static GMSPatternsPage patternsPage = null;

    private static String username = "demo";
    private static String password = "";

    // Pattern Group names
    private static String patternGroupNameBasic = "patternGroup";
    private static int patternGroupNumber = 1;

    // Pattern Group names for test data
    private static String patternGroupInvalidName = "PatternGroup InvalidName";

    @BeforeClass
    public static void oneTimeSetUp() {
        try {
            properties.load(new FileInputStream("C:\\configgms851.properties"));

            gmsHost = properties.getProperty("gms.host");
            gmsPort = properties.getProperty("gms.port");

            gmsUrl = "http://" + gmsHost + ":" + gmsPort + "/genesys/admin/login.jsp";
            gmsPatternsApiURL = "http://" + gmsHost + ":" + gmsPort + "/genesys/1/patterns";

        } catch (Exception e) {
            e.printStackTrace();
        }

        DesiredCapabilities cap = new DesiredCapabilities("firefox", "stable", Platform.WINDOWS);

        try {
            driver = new RemoteWebDriver(new URL("http://135.225.54.31:4444/wd/hub"), cap);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // driver = new FirefoxDriver();

        driver.get(gmsUrl);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.manage().window().maximize();

        loginPage = new GMSLoginPage(driver);
        mainPage = loginPage.logIn(username, password);
//		configuredServicesPage = mainPage.clickCallbackLocatorUINew();
//		patternsPage = configuredServicesPage.clickPatternsMenuUINew();

        configuredServicesPage = mainPage.goToConfiguredServicesUIPage();
        patternsPage = configuredServicesPage.goToPatternsUIPage();

    }

    @AfterClass
    public static void oneTimeTearDown() {

        // Deleting patterGroups with dynamic names
        for (int i = 1; i < patternGroupNumber; i++) {
            String patGroupTemp = patternGroupNameBasic + i;
            if (patternsPage.isPatternGroupExist(patGroupTemp))
                patternsPage.deletePatternGroup(patGroupTemp);
        }

        // Deleting patterGroups with static names
        if (patternsPage.isPatternGroupExist(patternGroupInvalidName))
            patternsPage.deletePatternGroup(patternGroupInvalidName);

        loginPage = patternsPage.clickLogOut(driver);
        if (driver != null)
            driver.quit();

    }

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
        patternsPage.clearRegexBox();

    }

    // Test 1. Test pattern value with several patterns in group
    @Test
    public void test01() {
        String patternGroup = patternGroupNameBasic + patternGroupNumber++;
        String patternName1 = "Pattern1";
        String patternValue1 = "PatternValue1";
        String patternName2 = "Pattern2";
        String patternValue2 = "PatternValue2";
        String patternName3 = "Pattern3";
        String patternValue3 = "PatternValue3";

        if (patternsPage.isPatternGroupNotExist(patternGroup))
            assertTrue(patternsPage.addNewPatternGroup(patternGroup));

        if (!patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName1, patternValue1))
            patternsPage.addNewPatternInGroup(patternGroup, patternName1, patternValue1);
        if (!patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName2, patternValue2))
            patternsPage.addNewPatternInGroup(patternGroup, patternName2, patternValue2);
        if (!patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName3, patternValue3))
            patternsPage.addNewPatternInGroup(patternGroup, patternName3, patternValue3);

        assertTrue(patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName1, patternValue1));
        assertTrue(patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName2, patternValue2));
        assertTrue(patternsPage.isPatternNameValuePairExistInGroup(patternGroup, patternName3, patternValue3));

        //assertTrue(patternsPage.setTextInRegexBox(patternValue2));
        //assertTrue(patternsPage.isMatchedPatternHighlighted(patternName2));

        boolean result = false;

        for (int i = 0; i < 5; i++) {
            patternsPage.clearRegexBox();
            if (patternsPage.setTextInRegexBox(patternValue2)) {
                if (patternsPage.isMatchedPatternHighlighted(patternName2)) {
                    result = true;
                    break;
                }
            }
        }
        assertTrue(result);

    }
}
