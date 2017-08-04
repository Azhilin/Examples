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
 * Created by bvolovyk on 11.07.2017.
 */

/*
 * [Test Suite] Localization French Canadian: https://jira.genesys.com/browse/GMS-2523
 */
public class LocalizationFrenchCanadianTests {
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
        loginPage.setLanguage("French");
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
        System.out.print("@AfterClass method processing...: ");
        env.createBaseGMSServices(gmsClusterAppName);
//        env.deleteService(gmsClusterAppName, callbackImmServiceName); //for troubleshooting purposes comment this command
//        env.deleteService(gmsClusterAppName, callbackSchServiceName); //for troubleshooting purposes comment this command
        env.deactivate();
    }

    @Test
    public void test_01_loginPage() {
        assertThat(loginPage.getWelcomeText(), equalTo("Bienvenue"));
        assertThat(loginPage.getUsernamePlaceholderText(), equalTo("Nom d'utilisateur"));
        assertThat(loginPage.getPasswordPlaceholderText(), equalTo("Mot de passe"));
        assertThat(loginPage.getLanguageMenuText(), equalTo("Français"));
        assertThat(loginPage.getLogInBtnName(), equalTo("Se connecter"));
    }

    @Test
    public void test_02_errMsgIncorrectCredentialsAtLoginPage() {
        loginPage.typeUsername(username);
        loginPage.typePassword(password + "bla");
        loginPage.clickLogin();

        assertThat(loginPage.getWelcomeText(), equalTo("Bienvenue"));
        assertThat(loginPage.getLanguageMenuText(), equalTo("Français"));
        assertThat(loginPage.getLogInBtnName(), equalTo("Se connecter"));
        assertThat(loginPage.getErrorMessage(), equalTo("Nom d'utilisateur et/ou mot de passe incorrect(s)."));
    }

    @Test
    public void test_03_errMsgNoCredentialsAtLoginPage() {
        loginPage.clickLogin();

        assertThat(loginPage.getWelcomeText(), equalTo("Bienvenue"));
        assertThat(loginPage.getUsernamePlaceholderText(), equalTo("Nom d'utilisateur"));
        assertThat(loginPage.getPasswordPlaceholderText(), equalTo("Mot de passe"));
        assertThat(loginPage.getLanguageMenuText(), equalTo("Français"));
        assertThat(loginPage.getLogInBtnName(), equalTo("Se connecter"));
        assertThat(loginPage.getErrorMessage(), equalTo("Veuillez remplir tous les champs obligatoires"));
    }

    @Test
    //Related to: https://jira.genesys.com/browse/GMS-3473
    public void test_05_errMsgNoAccess() {
        loginPage.typeUsername("gms_no_access");
        loginPage.typePassword("gms_no_access");
        loginPage.clickLogin();

        assertThat(loginPage.getErrorMessage(), equalTo("Nom d'utilisateur et/ou mot de passe incorrect(s)."));
    }

    @Test//here we verify navigation bar localization too
    //Related to: https://jira.genesys.com/browse/GMS-3275
    public void test_06_homePage() {
        GMSMainPage mainPage = loginPage.logIn(username, password);

        assertThat(mainPage.getHomeBtnName(), equalTo("Accueil"));
        assertThat(mainPage.getLogOutBtnName(), equalTo("Se déconnecter"));
        if (mainPage.isAdminUIPresent()) {
            assertThat(mainPage.getAdminUIIconName(), equalTo("Interface utilisateur de l'administrateur"));
        }
        assertThat(mainPage.getCallbackAndMobileEngagementIconName(), equalTo("Rappel et discussion mobile"));
        if (mainPage.isContextServicesPresent()) {
            assertThat(mainPage.getContextServicesIconName(), equalTo("Services de contexte"));
        }
        //TODO: implement localization verification for Journey Timeline icon
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_07_monitorAdminUIPage() {
        GMSMainPage mainPage = loginPage.logIn(username, password);
        GMSMonitorPage monitorPage = mainPage.goToMonitorAdminUIPage();

        assertThat(monitorPage.getHomeBtnName(), equalTo("Accueil"));
        assertThat(monitorPage.getWelcomeText(), containsString("Bienvenue, "));
        assertThat(monitorPage.getLogOutBtnName(), equalTo("Se déconnecter"));
        assertThat(monitorPage.getMonitorBtnName(), equalTo("SURVEILLER"));
        assertThat(monitorPage.getServicesBtnName(), equalTo("SERVICES"));
        assertThat(monitorPage.getCallbackBtnName(), equalTo("RAPPEL"));
        assertThat(monitorPage.getReportingBtnName(), equalTo("RAPPORTS"));
        assertThat(monitorPage.getToolsBtnName(), equalTo("OUTILS"));
        assertThat(monitorPage.getLabBtnName(), equalTo("LAB"));
        assertThat(monitorPage.getLastUpdatedText(), containsString("Dernière mise à jour: "));
        assertThat(monitorPage.getTokenText(), containsString("Jeton: "));
        assertThat(monitorPage.getStatusText(), containsString("État: "));
        assertThat(monitorPage.getLoadText(), containsString("Charger: "));
        assertThat(monitorPage.getDataCenterText(), containsString("Centre de données: "));
        assertThat(monitorPage.getRackText(), containsString("Bâti: "));
        assertThat(monitorPage.getOwnText(), containsString("Propre: "));
        assertThat(monitorPage.getRunningSinceText(), containsString("Fonctionne depuis: "));
        //verification for day of week
        assertThat(monitorPage.getRunningSinceText(), anyOf(
                containsString("dimanche, "), containsString("lundi, "),
                containsString("mardi, "), containsString("mercredi, "),
                containsString("jeudi, "), containsString("vendredi, "),
                containsString("samedi, ")));
        //verification for month
        assertThat(monitorPage.getRunningSinceText(), anyOf(
                containsString("janvier "), containsString("février "), containsString("mars "),
                containsString("avril "), containsString("mai "), containsString("juin "),
                containsString("juillet "), containsString("août "), containsString("septembre "),
                containsString("octobre "), containsString("novembre "), containsString("décembre ")));
        //TODO: implement verification of "Loading..." string localization which arises for a moment once within 3 minutes
//        assertThat(monitorPage.getLoadingText(), equalTo("Chargement en cours..."));
    }

    @Test//here we verify navigation bar localization too
    //Related to: https://jira.genesys.com/browse/GMS-3480
    public void test_08_configuredServicesPageWithoutServices() throws AtsCfgComponentException {
        GMSMainPage mainPage = loginPage.logIn(username, password);
        env.deleteAllServices(gmsClusterAppName);
        GMSConfiguredServicesPage configuredServicesPage = mainPage.goToConfiguredServicesUIPage();

        assertThat(configuredServicesPage.getConfiguredServicesBtnName(), containsString("Services configurés "));//returns string "Services configurés <!-- ngIf: item.caret == true -->"
        assertThat(configuredServicesPage.getCallbackBtnName(), containsString("Rappel "));//returns string "Rappel <!-- ngIf: item.caret == true -->"
        assertThat(configuredServicesPage.getToolsBtnName(), containsString("Outils "));//returns string "Outils <!-- ngIf: item.caret == true -->"
        assertThat(configuredServicesPage.getServiceTemplatesBtnName(), equalTo("Modèles de service"));
        assertThat(configuredServicesPage.getResourcesBtnName(), equalTo("Ressources"));
        assertThat(configuredServicesPage.getPatternsBtnName(), equalTo("Modèles"));
        assertThat(configuredServicesPage.getDownloadDfmBtnName(), equalTo("XXXXX"));//not localized
        assertThat(configuredServicesPage.getSearchItemsPlaceholderText(), equalTo("Rechercher des éléments"));
        assertThat(configuredServicesPage.getCreateBtnName(), equalTo("Créer"));
        assertThat(configuredServicesPage.getDeleteBtnName(), equalTo("Supprimer"));
        assertThat(configuredServicesPage.getConfiguredServicesText(), equalTo("Services configurés"));
        assertThat(configuredServicesPage.getOfficeHoursText(), equalTo("Heures d'ouverture"));
        assertThat(configuredServicesPage.getCapacityText(), equalTo("XXXCapacityXXX"));//not localized
        assertThat(configuredServicesPage.getNoServicesFoundText(), equalTo("XXXNo services foundXXX"));//not localized
    }

    @Test
    public void test_09_callbackManagementUIPageWithoutCallbacks() throws AtsCfgComponentException {
        GMSMainPage mainPage = loginPage.logIn(username, password);
        env.deleteAllServices(gmsClusterAppName);
        GMSCallbackPage callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);
        callbackPage.setDateFilter("Today");
        GMSAdvancedOptionsPopUpPage advancedOptionsPopUpPage = callbackPage.openAdvancedOptions();
        advancedOptionsPopUpPage.checkIncludeServiceIdColumn();
        callbackPage = advancedOptionsPopUpPage.saveAdvancedOptions();

        assertThat(callbackPage.getCreateCallbackBtnName(), equalTo("Créer un rappel"));
        assertThat(callbackPage.getAdvancedOptionsBtnName(), equalTo("Options avancées"));
        assertThat(callbackPage.getRefreshBtnName(), equalTo("Actualiser"));
        assertThat(callbackPage.getCancelCallbacksBtnName(), equalTo("XXXCancel CallbacksXXX"));
        assertThat(callbackPage.getDownloadReportsBtnName(), equalTo("XXXDownload ReportsXXX"));
        assertThat(callbackPage.getCallbacksFoundText(), containsString("Rappel(s) trouvé(s)"));
        assertThat(callbackPage.getServiceIdBtnName(), equalTo("ID de service"));
        assertThat(callbackPage.getStateBtnName(), equalTo("État"));
        assertThat(callbackPage.getDesiredCallbackTimeBtnName(), containsString("Heure de rappel souhaitée"));
        assertThat(callbackPage.getPhoneNumberBtnName(), equalTo("Numéro de téléphone"));
        assertThat(callbackPage.getServiceNameBtnName(), equalTo("Nom du service"));
        assertThat(callbackPage.getNoCallbacksFoundMsg(), equalTo("Aucun rappel trouvé."));
        assertThat(callbackPage.getBackToTopBtnName(), equalTo("Retour au début"));
    }

    @Test
    public void test_10_dateFilterDropDwnAtCallbackManagementUIPage() throws AtsCfgComponentException {
        GMSMainPage mainPage = loginPage.logIn(username, password);
        GMSCallbackPage callbackPage = mainPage.getCallbackManagementUIPage(gmsBaseUrl);

        assertThat(callbackPage.getLast24HoursDateFilterName(), equalTo("24 dernières heures"));
        assertThat(callbackPage.getLast7DaysDateFilterName(), equalTo("7 derniers jours"));
        assertThat(callbackPage.getLast30DaysDateFilterName(), equalTo("30 derniers jours"));
        assertThat(callbackPage.getTodayDateFilterName(), equalTo("Aujourd'hui"));
        assertThat(callbackPage.getNext24HoursDateFilterName(), equalTo("24 prochaines heures"));
        assertThat(callbackPage.getNext7DaysDateFilterName(), equalTo("7 prochains jours"));
        assertThat(callbackPage.getNext30DaysDateFilterName(), equalTo("30 prochains jours"));
        assertThat(callbackPage.getCustomDateRangeDateFilterName(), equalTo("Plage de dates personnalisée"));
    }
}
