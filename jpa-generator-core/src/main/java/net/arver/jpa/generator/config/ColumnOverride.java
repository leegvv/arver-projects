package net.arver.jpa.generator.config;

import java.util.List;
import net.arver.jpa.generator.util.MessageUtil;
import net.arver.jpa.generator.util.StringUtil;

/**
 * .
 * @author li gu
 * @version 1.0.0.0
 **/
public class ColumnOverride extends PropertyHolder{

    private final String columnName;

    private String javaProperty;

    private String jdbcType;

    private String javaType;

    private String typeHandler;

    private boolean isColumnNameDelimited;

    private boolean isGeneratedAlways;

    public ColumnOverride(final String columnName) {
        super();
        this.columnName = columnName;
        isColumnNameDelimited = StringUtil.containsSpace(columnName);
    }

    public void validate(final List<String> errors, final String tableName) {
        if (StringUtil.isEmpty(columnName)) {
            errors.add(MessageUtil.getString("ValidationError.22", tableName));
        }
    }

    public String getColumnName() {
        return columnName;
    }

    public String getJavaProperty() {
        return javaProperty;
    }

    public void setJavaProperty(final String javaProperty) {
        this.javaProperty = javaProperty;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(final String jdbcType) {
        this.jdbcType = jdbcType;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(final String javaType) {
        this.javaType = javaType;
    }

    public String getTypeHandler() {
        return typeHandler;
    }

    public void setTypeHandler(final String typeHandler) {
        this.typeHandler = typeHandler;
    }

    public boolean isColumnNameDelimited() {
        return isColumnNameDelimited;
    }

    public void setColumnNameDelimited(final boolean columnNameDelimited) {
        isColumnNameDelimited = columnNameDelimited;
    }

    public boolean isGeneratedAlways() {
        return isGeneratedAlways;
    }

    public void setGeneratedAlways(final boolean generatedAlways) {
        isGeneratedAlways = generatedAlways;
    }
}
