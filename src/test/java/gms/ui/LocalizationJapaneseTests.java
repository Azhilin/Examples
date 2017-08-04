package gms.ui;

import com.genesyslab.ats.ex.AtsCfgComponentException;
import com.genesyslab.functional.tests.gms.helper.CallbackServicesAPI;
import com.genesyslab.functional.tests.gms.psdk.Reconfiguration;
import com.genesyslab.functional.tests.gms.ui.adminui.GMSMonitorPage;
import com.genesyslab.functional.tests.gms.ui.callback.GMSAdvancedOptionsPopUpPage;
import com.genesyslab.functional.tests.gms.ui.callback.GMSCallbackPage;
import com.genesyslab.functional.tests.gms.ui.configuredservices.GMSConfiguredServicesPage;
import org.junit.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertyConfiguration;
import static com.genesyslab.functional.tests.gms.helper.TimeOperations.getMethodExecutionTime;
import static com.genesyslab.functional.tests.gms.helper.URLsHelper.getGMSBaseURL;
import static com.genesyslab.functional.tests.gms.helper.URLsHelper.getGMSFullLoginURL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by bvolovyk on 12.07.2017.
 */

/*
 * [Test Suite] Localization Japanese: https://jira.genesys.com/browse/GMS-2527
 */
public class LocalizationJapaneseTests {
    //    private static String propertiesFile = getPropertiesFile();
    private static String propertiesFile = "./config_2nd.properties";//uncomment this line in case you want to use another .properties file
    private static Reconfiguration env = new Reconfiguration(propertiesFile);
    private static String gmsClusterAppName = getPropertyConfiguration(propertiesFile, "gms.cluster.app.name");
    private static String gmsBaseUrl = getGMSBaseURL(propertiesFile);
    private static String gmsUrl = getGMSFullLoginURL(propertiesFile);
    private static String username = getPropertyConfiguration(propertiesFile, "gms.user.name");
    private static String password = getPropertyConfiguration(propertiesFile, "gms.user.password");
    private static String customerNumber = "5115";
    private static String callbackImmServiceName = "cb_term_im_gms-4083";
    private static String callbackSchServiceName = "cb_term_sch_gms-4083";

    private static final int DAY = 86400;
    private static final int CURRENT_MOMENT = 0;
    private static final int SCHEDULED_CB_TIME_NOT_FAR = 10;
    private static final int SCHEDULED_CB_TIME_FAR = 3600;
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String LAST_DAYS_SUFFIX = "T21:00:00.000Z";
    private static final String NEXT_DAYS_SUFFIX = "T20:59:59.999Z";
    private static final long POLLING_TIME_MILLIS = 1000;
    private static final long TIMEOUT_FOR_SEMI_AUTOMATIC_PART = 140;

    private WebDriver driver = null;
    GMSLoginPage loginPage = null;

    @BeforeClass
    public static void oneTimeSetUp() throws AtsCfgComponentException {
        long startTime = System.nanoTime();
        System.out.println("@BeforeClass method processing...");
//        env.createService(gmsClusterAppName, callbackImmServiceName, getUserTermImmCallbackServiceOptions());
//        env.createService(gmsClusterAppName, callbackSchServiceName, getUserTermSchCallbackServiceOptions());
        CallbackServicesAPI.setGMSBaseURL(getGMSBaseURL(propertiesFile));
        long endTime = System.nanoTime();
        getMethodExecutionTime(startTime, endTime, "@BeforeClass");
    }

    @Before
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(gmsUrl);

        loginPage = new GMSLoginPage(driver);
        loginPage.setLanguage("Japanese");
    }

    @After
    public void tearDown() {
//        cancelUnfinishedCallbacks(callbackImmServiceName);
//        cancelUnfinishedCallbacks(callbackSchServiceName);
        if (driver != null)
            driver.quit();
    }

    @AfterClass
    public static void oneTimeTearDown() throws Exception {
        System.out.println("@AfterClass method processing...: ");
        env.createBaseGMSServices(gmsClusterAppName);
//        env.deleteService(gmsClusterAppName, callbackImmServiceName); //for troubleshooting purposes comment this command
//        env.deleteService(gmsClusterAppName, callbackSchServiceName); //for troubleshooting purposes comment this command
        env.deactivate();
    }

    @Test
    public void test_01_loginPage() {
        assertThat(loginPage.getWelcomeText(), equalTo("ようこそ"));
        assertThat(loginPage.getUsernamePlaceholderText(), equalTo("ユーザー名"));
        assertThat(loginPage.getPasswordPlaceholderText(), equalTo("パスワード"));
        assertThat(loginPage.getLanguageMenuText(), equalTo("日本語"));
        assertThat(loginPage.getLogInBtnName(), equalTo("ログイン"));
    }

    @Test
    public void test_02_errMsgIncorrectCredentialsAtLoginPage() {
        loginPage.typeUsername(username);
        loginPage.typePassword(password + "bla");
        loginPage.clickLogin();

        assertThat(loginPage.getWelcomeText(), equalTo("ようこそ"));
        assertThat(loginPage.getLanguageMenuText(), equalTo("日本語"));
        assertThat(loginPage.getLogInBtnName(), equalTo("ログイン"));
        assertThat(loginPage.getErrorMessage(), equalTo("ユーザー名またはパスワードが正しくありません。"));
    }

    @Test
    public void test_03_errMsgNoCredentialsAtLoginPage() {
        loginPage.clickLogin();

        assertThat(loginPage.getWelcomeText(), equalTo("ようこそ"));
        assertThat(loginPage.getUsernamePlaceholderText(), equalTo("ユーザー名"));
        assertThat(loginPage.getPasswordPlaceholderText(), equalTo("パスワード"));
        assertThat(loginPage.getLanguageMenuText(), equalTo("日本語"));
        assertThat(loginPage.getLogInBtnName(), equalTo("ログイン"));
        assertThat(loginPage.getErrorMessage(), equalTo("必須フィールドはすべて入力してください"));
    }

    @Test
    //Related to: https://jira.genesys.com/browse/GMS-3473
    public void test_05_errMsgNoAccess() {
        loginPage.typeUsername("gms_no_access");
        loginPage.typePassword("gms_no_access");
        loginPage.clickLogin();

        assertThat(loginPage.getErrorMessage(), equalTo("ユーザー名またはパスワードが正しくありません。"));
    }

    @Test//here we verify navigation bar localization too
    //Related to: https://jira.genesys.com/browse/GMS-3275
    public void test_06_homePage() {
        GMSMainPage mainPage = loginPage.logIn(username, password);

        assertThat(mainPage.getHomeBtnName(), equalTo("ホーム"));
        assertThat(mainPage.getLogOutBtnName(), equalTo("ログアウト"));
        if (mainPage.isAdminUIPresent()) {
            assertThat(mainPage.getAdminUIIconName(), equalTo("管理UI"));
        }
        assertThat(mainPage.getCallbackAndMobileEngagementIconName(), equalTo("コールバックおよびMobile Engagement"));
        if (mainPage.isContextServicesPresent()) {
            assertThat(mainPage.getContextServicesIconName(), equalTo("コンテキスト サービス"));
        }
        //TODO: implement localization verification for Journey Timeline icon
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_07_monitorAdminUIPage() {
        GMSMainPage mainPage = loginPage.logIn(username, password);
        GMSMonitorPage monitorPage = mainPage.goToMonitorAdminUIPage();

        assertThat(monitorPage.getHomeBtnName(), equalTo("ホーム"));
        assertThat(monitorPage.getWelcomeText(), containsString("ようこそ, "));
        assertThat(monitorPage.getLogOutBtnName(), equalTo("ログアウト"));
        assertThat(monitorPage.getMonitorBtnName(), equalTo("モニター"));
        assertThat(monitorPage.getServicesBtnName(), equalTo("サービス"));
        assertThat(monitorPage.getCallbackBtnName(), equalTo("コールバック"));
        assertThat(monitorPage.getReportingBtnName(), equalTo("レポート"));
        assertThat(monitorPage.getToolsBtnName(), equalTo("ツール"));
        assertThat(monitorPage.getLabBtnName(), equalTo("ラボ"));
        assertThat(monitorPage.getLastUpdatedText(), containsString("最終更新日: "));
        assertThat(monitorPage.getTokenText(), containsString("トークン: "));
        assertThat(monitorPage.getStatusText(), containsString("ステータス: "));
        assertThat(monitorPage.getLoadText(), containsString("ロード: "));
        assertThat(monitorPage.getDataCenterText(), containsString("データセンター: "));
        assertThat(monitorPage.getRackText(), containsString("ラック: "));
        assertThat(monitorPage.getOwnText(), containsString("所有: "));
        assertThat(monitorPage.getRunningSinceText(), containsString("稼働開始: "));
        //verification for day of week
        assertThat(monitorPage.getRunningSinceText(), anyOf(
                containsString("月曜日, "), containsString("火曜日, "),
                containsString("水曜日, "), containsString("木曜日, "),
                containsString("金曜日, "), containsString("土曜日, "),
                containsString("日曜日, ")));
        //verification for month
        assertThat(monitorPage.getRunningSinceText(), anyOf(
                containsString("1月 "), containsString("2月 "), containsString("3月 "),
                containsString("4月 "), containsString("5月 "), containsString("6月 "),
                containsString("7月 "), containsString("8月 "), containsString("9月 "),
                containsString("10月 "), containsString("11月 "), containsString("12月 ")));
        //TODO: implement verification of "Loading..." string localization which arises for a moment once within 3 minutes
//        assertThat(monitorPage.getLoadingText(), equalTo("ロード中..."));
    }

    @Test//here we verify navigation bar localization too
    //Related to: https://jira.genesys.com/browse/GMS-3480
    public void test_08_configuredServicesPageWithoutServices() throws AtsCfgComponentException {
        GMSMainPage mainPage = loginPage.logIn(username, password);
        env.deleteAllServices(gmsClusterAppName);
        GMSConfiguredServicesPage configuredServicesPage = mainPage.goToConfiguredServicesUIPage();

        assertThat(configuredServicesPage.getConfiguredServicesBtnName(), containsString("構成されたサービス "));//returns value "構成されたサービス <!-- ngIf: item.caret == true -->"
        assertThat(configuredServicesPage.getCallbackBtnName(), containsString("コールバック "));
        assertThat(configuredServicesPage.getToolsBtnName(), containsString("ツール "));
        assertThat(configuredServicesPage.getServiceTemplatesBtnName(), equalTo("サービス テンプレート"));
        assertThat(configuredServicesPage.getResourcesBtnName(), equalTo("リソース"));
        assertThat(configuredServicesPage.getPatternsBtnName(), equalTo("パターン"));
        assertThat(configuredServicesPage.getDownloadDfmBtnName(), equalTo("XXXXX"));//not localized
        assertThat(configuredServicesPage.getSearchItemsPlaceholderText(), equalTo("項目を検索"));
        assertThat(configuredServicesPage.getCreateBtnName(), equalTo("作成"));
        assertThat(configuredServicesPage.getDeleteBtnName(), equalTo("削除"));
        assertThat(configuredServicesPage.getConfiguredServicesText(), equalTo("構成されたサービス"));
        assertThat(configuredServicesPage.getOfficeHoursText(), equalTo("営業時間"));
        assertThat(configuredServicesPage.getCapacityText(), equalTo("XXXCapacityXXX"));//not localized
        assertThat(configuredServicesPage.getNoServicesFoundText(), equalTo("XXXNo services foundXXX"));//not localized
    }

    @Test
    public void test_09_callbackManagementUIPageWithoutCallbacks() throws AtsCfgComponentException {
        GMSMainPage mainPage = loginPage.logIn(username, password);
        GMSCallbackPage callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");
        GMSAdvancedOptionsPopUpPage advancedOptionsPopUpPage = callbackPage.openAdvancedOptions();
        advancedOptionsPopUpPage.checkIncludeServiceIdColumn();
        callbackPage = advancedOptionsPopUpPage.saveAdvancedOptions();

        assertThat(callbackPage.getCreateCallbackBtnName(), equalTo("コールバックを作成する"));
        assertThat(callbackPage.getAdvancedOptionsBtnName(), equalTo("詳細オプション"));
        assertThat(callbackPage.getRefreshBtnName(), equalTo("リフレッシュ"));
        assertThat(callbackPage.getCancelCallbacksBtnName(), equalTo("XXXCancel CallbacksXXX"));
        assertThat(callbackPage.getDownloadReportsBtnName(), equalTo("XXXDownload ReportsXXX"));
        assertThat(callbackPage.getCallbacksFoundText(), containsString("コールバックが検出されました"));
        assertThat(callbackPage.getServiceIdBtnName(), equalTo("サービスID"));
        assertThat(callbackPage.getStateBtnName(), equalTo("状態"));
        assertThat(callbackPage.getDesiredCallbackTimeBtnName(), containsString("希望するコールバック時間"));
        assertThat(callbackPage.getPhoneNumberBtnName(), equalTo("電話番号"));
        assertThat(callbackPage.getServiceNameBtnName(), equalTo("サービス名"));
        assertThat(callbackPage.getNoCallbacksFoundMsg(), equalTo("コールバックが見つかりません。"));
        assertThat(callbackPage.getBackToTopBtnName(), equalTo("先頭に戻る"));
    }

    @Test
    public void test_10_dateFilterDropDwnAtCallbackManagementUIPage() throws AtsCfgComponentException {
        GMSMainPage mainPage = loginPage.logIn(username, password);
        GMSCallbackPage callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);

        assertThat(callbackPage.getLast24HoursDateFilterName(), equalTo("直近24時間"));
        assertThat(callbackPage.getLast7DaysDateFilterName(), equalTo("直近7日間"));
        assertThat(callbackPage.getLast30DaysDateFilterName(), equalTo("直近30日間"));
        assertThat(callbackPage.getTodayDateFilterName(), equalTo("本日"));
        assertThat(callbackPage.getNext24HoursDateFilterName(), equalTo("次の24時間"));
        assertThat(callbackPage.getNext7DaysDateFilterName(), equalTo("次の7日間"));
        assertThat(callbackPage.getNext30DaysDateFilterName(), equalTo("次の30日間"));
        assertThat(callbackPage.getCustomDateRangeDateFilterName(), equalTo("カスタム日付範囲"));
    }
}
