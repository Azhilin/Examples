package gms.tests;


import com.genesyslab.ats.cfg.actor.CfgManager;
import com.genesyslab.ats.ex.AtsOperationFailed;
import com.genesyslab.scsmanager.SCSManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.concurrent.Callable;

import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertyConfiguration;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.given;
import static org.hamcrest.Matchers.equalTo;

public class ConfigurationTests {

	private static CfgManager cfgManager = new CfgManager();
	private static String gmsAppName = getPropertyConfiguration("gms.app.name");
	private static String configServerHost = getPropertyConfiguration("config.server.host");
	private static Properties properties = new Properties();
	private static SCSManager scsManager = new SCSManager(cfgManager);

	@BeforeClass
	public static void oneTimeSetUp() {
		try {
			properties.load(new FileInputStream("./config.properties"));
			scsManager.init(properties);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		cfgManager.deactivate();
		scsManager.deactivate();
	}

	@After
	public void tearDown() {
		System.out.print("@After method processing...: ");

	}

	private void executeBatch(String command) {
		try {
			Process child = Runtime.getRuntime().exec(command);

			// Get output stream to write from it
			OutputStream out = child.getOutputStream();

			out.write("cd C:/ /r/n".getBytes());
			out.flush();
			out.write("dir /r/n".getBytes());
			out.close();
		} catch (IOException e) {
		}
	}

	/**
	 * GMS-2494 LCA don´t see GMS startet after LCA restart
	 * @throws InterruptedException
	 */
	@Test
	public void test_01() throws InterruptedException {
		given().ignoreExceptions().await().atMost(80, SECONDS).until(getGMSAppStatus(), equalTo("APP_STATUS_RUNNING"));

		System.out.println("stopping LCA");
		executeBatch("wmic /node:" + configServerHost
				+ " /user:administrator /password:genesys process call create \"cmd.exe /c net stop LCA64\"");
		System.out.println("LCA stopped");
		// execute this if you run tests on the same host as gms
		// executeBatch("net stop LCA64");
		given().ignoreExceptions().await().atMost(80, SECONDS).until(getGMSAppStatus(), equalTo("APP_STATUS_UNKNOWN"));

		System.out.println("starting LCA");
		executeBatch("wmic /node:" + configServerHost
				+ " /user:administrator /password:genesys process call create \"cmd.exe /c net start LCA64\"");
		System.out.println("LCA started");

		given().ignoreExceptions().await().atMost(80, SECONDS).until(getGMSAppStatus(), equalTo("APP_STATUS_RUNNING"));
	}

	private Callable<String> getGMSAppStatus() {
		return new Callable<String>() {
			@Override
			public String call() throws Exception {
				return getStatus(); 
			}
		};
	}

	private String getStatus() {
		try {
			String status = scsManager.getApplicationStatus(gmsAppName);
			System.out.println(status + ",");
			return status;
		} catch (AtsOperationFailed e) {
			e.printStackTrace();
			return null;
		}
	}

}