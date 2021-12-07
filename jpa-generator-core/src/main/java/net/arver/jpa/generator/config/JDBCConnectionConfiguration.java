package net.arver.jpa.generator.config;

import java.util.List;
import net.arver.jpa.generator.util.MessageUtil;
import net.arver.jpa.generator.util.StringUtil;

/**
 * .
 * @author li gu
 * @version 1.0.0.0
 **/
public class JDBCConnectionConfiguration extends PropertyHolder{

    private String driverClass;

    private String connectionUrl;

    private String userId;

    private String password;

    public JDBCConnectionConfiguration() {
        super();
    }

    public void validate(final List<String> errors) {
        if (StringUtil.isEmpty(driverClass)) {
            errors.add(MessageUtil.getString("ValidationError.4"));
        }
        if (StringUtil.isEmpty(connectionUrl)) {
            errors.add(MessageUtil.getString("ValidationError.5"));
        }
    }

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(final String driverClass) {
        this.driverClass = driverClass;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public void setConnectionUrl(final String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }
}
