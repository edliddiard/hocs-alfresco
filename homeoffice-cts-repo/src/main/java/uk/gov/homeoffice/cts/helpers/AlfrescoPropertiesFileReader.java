package uk.gov.homeoffice.cts.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Class to read property from a property file.
 */

public class AlfrescoPropertiesFileReader {

    public String property;
    Properties properties = new Properties();
    public static final String PROPERTIES_FILE = "alfresco-global.properties";

    public AlfrescoPropertiesFileReader() {
        ClassLoader classLoader = Thread.currentThread()
                .getContextClassLoader();
        InputStream inputStream = classLoader
                .getResourceAsStream(PROPERTIES_FILE);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getProperty(String property) {
        return properties.getProperty(property);
    }

    public void setProperty(String property) {
        this.property = property;
    }
}
