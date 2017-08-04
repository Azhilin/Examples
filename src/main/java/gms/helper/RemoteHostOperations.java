package gms.helper;

import java.io.IOException;

import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertyConfiguration;
import static com.genesyslab.functional.tests.gms.helper.TimeOperations.*;

/**
 * Created by bvolovyk on 17.05.2017.
 */
public class RemoteHostOperations {
    private static String remoteHost = getPropertyConfiguration("gms.host");
    private static String remoteHostUserName = "administrator";
    private static String remoteHostPassword = "genesys";
    private static long changeDateDelay = 20000;

    public static String getRemoteHost() {
        return remoteHost;
    }

    public static void setRemoteHost(String remoteHost) {
        RemoteHostOperations.remoteHost = remoteHost;
        System.out.printf("RemoteHostOperations.remoteHost was set to %s value.%n", remoteHost);
    }

    public static String getRemoteHostUserName() {
        return remoteHostUserName;
    }

    public static void setRemoteHostUserName(String remoteHostUserName) {
        RemoteHostOperations.remoteHostUserName = remoteHostUserName;
    }

    public static String getRemoteHostPassword() {
        return remoteHostPassword;
    }

    public static void setRemoteHostPassword(String remoteHostPassword) {
        RemoteHostOperations.remoteHostPassword = remoteHostPassword;
    }

    public static void execOnRemoteWinHost(String command) {
        Runtime runtime = Runtime.getRuntime();
        String cmdLine = "wmic /node:" + remoteHost + " /user:" + remoteHostUserName + " /password:" + remoteHostPassword
                + " process call create \"" + command + "\"";
        try {
            runtime.exec(cmdLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void deleteProcessOnRemoteWinHost(String command) {
        Runtime runtime = Runtime.getRuntime();
        String cmdLine = "wmic /node:" + remoteHost + " /user:" + remoteHostUserName + " /password:" + remoteHostPassword
                + " process where \"name like '%"+command+"%'\" delete";
        try {
            runtime.exec(cmdLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void execCmdCommandOnRemoteHost(String command) {
        execOnRemoteWinHost("cmd.exe /c " + command);
    }

    public static void changeDateOnRemoteHostAhead(int daysFromNow) {
        String changedDate = getDateDaysAhead(getLocalTimeZoneOffset(), daysFromNow, "MM-dd-yyyy");
        changeDateOnRemoteHost(changedDate);
    }

    public static void changeDateOnRemoteHostBack(int daysFromNow) {
        String changedDate = getDateDaysBack(getLocalTimeZoneOffset(), daysFromNow, "MM-dd-yyyy");
        changeDateOnRemoteHost(changedDate);
    }

    private static void changeDateOnRemoteHost(String changedDate) {
        System.out.println("Set system date on host " + getRemoteHost() + " to " + changedDate);
        execCmdCommandOnRemoteHost("date " + changedDate);
        System.out.printf("Waiting %s seconds for date change takes effect...%n", (changeDateDelay/1000));
        try {
            Thread.sleep(changeDateDelay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
