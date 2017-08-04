package gms.helper;

import java.io.*;
import java.util.Properties;

/**
 * Initialization of properties from configuration file config.properties in the root of the project
 *
 * @author igabduli
 */

public class PropertiesInitialization {
    private static String propertiesFile = "./config.properties";

    public static String getPropertiesFile() {
        return propertiesFile;
    }

    public static void setPropertiesFile(String propFile) {
        PropertiesInitialization.propertiesFile = propFile;
    }

    public static Properties getProperties(String propFile) {
        Properties properties = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream(propFile);
            // load a properties file
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return properties;
    }

    public static Properties getProperties() {
        return getProperties(getPropertiesFile());
    }

    public static void writePropertyToFile(String key, String value) {
        Properties prop = new Properties();
        OutputStream output = null;
        try {
            output = new FileOutputStream("config.properties");
            // set the properties value
            prop.setProperty(key, value);
            // save properties to project root folder
            prop.store(output, null);
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static String getPropertyConfiguration(String key) {
        Properties prop = getProperties(getPropertiesFile());
        String value = "";
        value = prop.getProperty(key);
        return value;
    }

    public static String getPropertyConfiguration(String propFile, String key) {
        Properties prop = getProperties(propFile);
        String value = "";
        value = prop.getProperty(key);
        return value;
    }

}
