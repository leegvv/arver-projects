package net.arver.jpa.generator.config;

import java.util.List;
import net.arver.jpa.generator.util.MessageUtil;
import net.arver.jpa.generator.util.StringUtil;

/**
 * .
 * @author li gu
 * @version 1.0.0.0
 **/
public class ConnectionFactoryConfiguration extends TypedPropertyHolder{

    public ConnectionFactoryConfiguration() {
        super();
    }

    public void validate(final List<String> errors) {
        if (getConfigurationType() == null || "DEFAULT".equals(getConfigurationType())) { //$NON-NLS-1$
            if (StringUtil.isEmpty(getProperty("driverClass"))) {
                errors.add(MessageUtil.getString("ValidationError.18", "connectionFactory", "driverClass"));
            }

            if (StringUtil.isEmpty(getProperty("connectionURL"))) {
                errors.add(MessageUtil.getString("ValidationError.18", "connectionFactory", "connectionURL"));
            }
        }
    }
}
