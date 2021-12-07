package net.arver.jpa.generator.config;

import java.util.List;
import net.arver.jpa.generator.db.DatabaseDialects;
import net.arver.jpa.generator.util.MessageUtil;
import net.arver.jpa.generator.util.StringUtil;

/**
 * .
 * @author li gu
 * @version 1.0.0.0
 **/
public class GeneratedKey {

    private final String column;

    private final String runtimeSqlStatement;

    private final boolean isIdentity;

    private final String type;

    public GeneratedKey(final String column, final String configuredSqlStatement, final boolean isIdentity, final String type) {
        super();
        this.column = column;
        this.type = type;
        this.isIdentity = isIdentity;

        final DatabaseDialects dialect = DatabaseDialects.getDatabaseDialect(configuredSqlStatement);
        if (dialect == null) {
            this.runtimeSqlStatement = configuredSqlStatement;
        } else {
            this.runtimeSqlStatement = dialect.getIdentityRetrievalStatement();
        }
    }

    public void validate(List<String> errors, String tableName) {
        if (StringUtil.isEmpty(runtimeSqlStatement)) {
            errors.add(MessageUtil.getString("ValidationError.7", tableName));
        }

        if (StringUtil.isNotEmpty(type) && !"pre".equals(type) && !"post".equals(type)) {
            errors.add(MessageUtil.getString("ValidationError.15", tableName));
        }

        if ("pre".equals(type) && isIdentity) {
            errors.add(MessageUtil.getString("ValidationError.23", tableName));
        }

        if ("post".equals(type) && !isIdentity) {
            errors.add(MessageUtil.getString("ValidationError.24", tableName));
        }
    }

    public boolean isJdbcStandard() {
        return "JDBC".equals(runtimeSqlStatement);
    }

    public String getColumn() {
        return column;
    }

    public String getRuntimeSqlStatement() {
        return runtimeSqlStatement;
    }

    public boolean isIdentity() {
        return isIdentity;
    }

    public String getType() {
        return type;
    }
}
