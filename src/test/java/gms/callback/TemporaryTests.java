package gms.callback;

import com.genesyslab.ats.ex.AtsCfgComponentException;
import com.genesyslab.functional.tests.gms.helper.RemoteHostOperations;
import com.genesyslab.functional.tests.gms.psdk.Reconfiguration;
import io.restassured.response.Response;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static com.genesyslab.functional.tests.gms.helper.CallbackServicesAPI.startImmediateCallback;
import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertiesFile;
import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertyConfiguration;
import static com.genesyslab.functional.tests.gms.helper.RemoteHostOperations.execCmdCommandOnRemoteHost;
import static com.genesyslab.functional.tests.gms.helper.ServiceOptions.getUserTermPrevCallbackServiceOptions;

/**
 * Created by bvolovyk on 14.03.2017.
 */
public class TemporaryTests {

    //    private static String[] callbackServices = {"GSYS_SAT_VQ", "Andrey_CB"};
    private static String[] callbackServices = {"cb_term_im", "cb_term_im2", "cb_term_im3"};
    private long initialCustNumber = 13000000001L;
    private int cbQuantity = 300;


    @Test
    public void createThousandCallbacks() throws Exception {
        for (String cbServiceName : callbackServices) {
            for (long custNumber = initialCustNumber; custNumber < (initialCustNumber + 1000); custNumber++) {
                String customerNumber = String.valueOf(custNumber);
                Map<String, String> headers = new HashMap<>();
                headers.put("ContactCenterId", getPropertyConfiguration("contact.center.id"));
                Response callback = startImmediateCallback(cbServiceName, customerNumber, headers);
                System.out.println(callback.getBody().asString());
                callback.then().assertThat().statusCode(200);
                System.out.println("Callback " + cbServiceName + " to " + customerNumber + " was successfully created.");
            }
        }
    }

    @Test
    public void createSpecefiedImmCallbackQuantity_premise() throws Exception {
        int cbServiceQuantity = 20;
        String[] callbackServices = new String[cbServiceQuantity];
        for (int i = 0; i <= cbServiceQuantity - 1; i++) {
            callbackServices[i] = "cb_term_im" + (i + 1);
        }
        for (String cbServiceName : callbackServices) {
            for (long custNumber = initialCustNumber; custNumber < (initialCustNumber + cbQuantity); custNumber++) {
                String customerNumber = String.valueOf(custNumber);
                Response callback = startImmediateCallback(cbServiceName, customerNumber);
                System.out.println(callback.getBody().asString());
                callback.then().assertThat().statusCode(200);
                System.out.println("Callback " + cbServiceName + " to " + customerNumber + " was successfully created.");
            }
        }
    }

    @Test
    public void verifyRemoteHostOperations() {
        RemoteHostOperations.setRemoteHost("135.17.36.157");
        execCmdCommandOnRemoteHost("\"C:\\Users\\Administrator\\Desktop\\Restart Config Server.bat\"");
//        execCmdCommandOnRemoteHost("C:\\Program Files\\GCTI\\Genesys Mobile Services\\GMS_8510905\\startServer.bat");
    }

    //    @Test
    public void test_temporary() throws AtsCfgComponentException {
        for (int i = 1; i <= 20; i++) {
//            cfgManager.getAppApi().setApp(gmsClusterAppName)
//                    .getOptionsApi()
//                    .addSection("service.cb_term_im" + i, getUserTermImmCallbackServiceOptions());
        }
    }

    @Test
    public void create_cb_term_im_prev_service() throws Exception {
        Reconfiguration env = new Reconfiguration(getPropertiesFile());
        String gmsAppName = getPropertyConfiguration("gms.cluster.app.name");
        String serviceName = "cb_term_im_prev_test";

        env.createService(gmsAppName, serviceName, getUserTermPrevCallbackServiceOptions());

        env.changeOptionValueInService(gmsAppName, serviceName, "_enable_disposition_dialog", "true");

        env.changeOptionValueInService(gmsAppName, serviceName, "_cpd_enable", "true");

        env.changeOptionValueInService(gmsAppName, serviceName, "_plugin_on_dial_url", "http://localhost:9001/ExamplePlugin/src-gen/IPD_plugin_example.scxml");

        env.changeOptionValueInService(gmsAppName, serviceName, "_dial_retry_timeout", "15");

        env.changeOptionValueInService(gmsAppName, serviceName, "_wait_for_agent", "false");

        env.changeOptionValueInService(gmsAppName, serviceName, "_plugin_on_dial_invoke_on_call_failed", "false");

//        env.changeOptionValueInService(gmsAppName, serviceName, "_max_dial_attempts", "3");

//        env.changeOptionValueInService(gmsAppName, serviceName, "_plugin_on_dial_associate_ixn", "true");

//        env.changeOptionValueInService(gmsAppName, serviceName, "_attach_udata", "single_json");

//        env.changeOptionValueInService(gmsAppName, serviceName, "_call_direction", "USERTERMINATED");
    }

    @Test
    public void verifyDeleteAllServicesMethod() throws Exception {
        String propertiesFile = "./config_2nd.properties";
//        String propertiesFile = getPropertiesFile();
        Reconfiguration env = new Reconfiguration(propertiesFile);
        String gmsAppName = getPropertyConfiguration(propertiesFile, "gms.cluster.app.name");
        env.deleteAllServices(gmsAppName);
    }

    @Test
    public void getLocalTimeZone() throws Exception {
        TimeZone timeZone = TimeZone.getDefault();
        String timeZoneID = timeZone.getID();
        int offset = timeZone.getOffset(new Date().getTime()) / 1000 / 60;

        System.out.println(timeZone);
        System.out.println(timeZone.getDisplayName());
        System.out.println(timeZoneID);
        System.out.println(offset);
    }

    @Test
    public void verifyCreateBaseGMSServicesMethod() throws Exception {
        String propertiesFile = "./config_2nd.properties";
//        String propertiesFile = getPropertiesFile();
        Reconfiguration env = new Reconfiguration(propertiesFile);
        String gmsAppName = getPropertyConfiguration(propertiesFile, "gms.cluster.app.name");
        env.createBaseGMSServices(gmsAppName);
    }

    @Test
    public void verifyDeleteAndCreateTransactionMethod() throws Exception {
        String propertiesFile = "./config_2nd.properties";
//        String propertiesFile = getPropertiesFile();
        Reconfiguration env = new Reconfiguration(propertiesFile);
        env.deleteTransaction("GMS_Patterns");
        env.createTransaction("GMS_Patterns", "CFGTRTList");
    }
}
