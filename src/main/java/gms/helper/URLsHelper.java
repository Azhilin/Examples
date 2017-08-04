package gms.helper;

import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertyConfiguration;

/**
 * Created by bvolovyk on 16.06.2017.
 */
public class URLsHelper {
    public static String getGMSBaseURL(String propFile) {
        String gmsProtocol = getPropertyConfiguration(propFile, "gms.protocol");
        String gmsHost = getPropertyConfiguration(propFile, "gms.host");
        String gmsPort = getPropertyConfiguration(propFile, "gms.port");
        return gmsProtocol + gmsHost + ":" + gmsPort;
    }

    public static String getGMSFullLoginURL(String propFile) {
        String gmsBaseURL = getGMSBaseURL(propFile);
        String gmsLoginURL = getPropertyConfiguration(propFile, "gms.login.url");
        return gmsBaseURL + gmsLoginURL;
    }

    public static String getORSBaseURL(String propFile) {
        String orsProtocol = getPropertyConfiguration(propFile, "ors.protocol");
        String orsHost = getPropertyConfiguration(propFile, "ors.host");
        String orsPort = getPropertyConfiguration(propFile, "ors.http.port");
        return orsProtocol + orsHost + ":" + orsPort;
    }

    public static String getCallbackPageURL(String gmsBaseURL) {
        return gmsBaseURL + "/genesys/develop/index.html#/callback";
    }

}
