package dev.heatwave.revolut.util;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class PropertyResolver {

    private static PropertiesConfiguration propertiesConfiguration;
    private static final String PROPERTIES_FILE_NAME = "application.properties";

    public static final String SERVER_PORT = "server.port";
    public static final String PERSISTENCE_UNIT_NAME = "persistence.unit.name";

    public static PropertiesConfiguration getConfiguration() {
        return propertiesConfiguration;
    }

    static {
        try {
            propertiesConfiguration = new PropertiesConfiguration(PROPERTIES_FILE_NAME);
        } catch (ConfigurationException e) {
            throw new RuntimeException("Failed to load properties", e);
        }
    }
}
