package net.arver.jpa.generator.config;

/**
 * .
 * @author li gu
 * @version 1.0.0.0
 **/
public class TypedPropertyHolder extends PropertyHolder{

    private String configurationType;

    protected TypedPropertyHolder() {
        super();
    }

    public String getConfigurationType() {
        return configurationType;
    }

    public void setConfigurationType(final String configurationType) {
        if (!"DEFAULT".equalsIgnoreCase(configurationType)) {
            this.configurationType = configurationType;
        }
    }
}
