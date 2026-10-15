package com.taxplan.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static final Logger logger = LogManager.getLogger(ConfigReader.class);
    private final Properties properties;

    public ConfigReader() {
        properties = new Properties();
        loadProperties();
    }

    private void loadProperties() {
        String configPath = System.getProperty("user.dir") + "\\src\\test\\resources\\config.properties";
        try (FileInputStream fis = new FileInputStream(configPath)) {
            properties.load(fis);
            logger.info("Configuration properties loaded successfully from: " + configPath);
        } catch (IOException e) {
            logger.error("Failed to load configuration properties from: " + configPath, e);
            throw new RuntimeException("Configuration file not found: " + configPath, e);
        }
    }

    public String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            logger.warn("Property key not found: " + key);
        }
        return value;
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public int getIntProperty(String key) {
        String value = getProperty(key);
        return value != null ? Integer.parseInt(value.trim()) : 0;
    }
}
