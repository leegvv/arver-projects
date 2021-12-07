package net.arver.jpa.generator.config;

import java.util.Properties;

/**
 * 属性对象.
 * @author li gu
 * @version 1.0.0.0
 **/
public abstract class PropertyHolder {

    private final Properties properties;

    protected PropertyHolder() {
        properties = new Properties();
    }

    public void addProperty(final String name, final String value) {
        properties.setProperty(name, value);
    }

    public String getProperty(final String name) {
        return properties.getProperty(name);
    }

    public Properties getProperties() {
        return properties;
    }

}
