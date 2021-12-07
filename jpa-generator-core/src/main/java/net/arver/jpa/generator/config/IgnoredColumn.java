package net.arver.jpa.generator.config;

import java.util.List;
import net.arver.jpa.generator.util.MessageUtil;
import net.arver.jpa.generator.util.StringUtil;

/**
 * .
 * @author li gu
 * @version 1.0.0.0
 **/
public class IgnoredColumn {

    protected final String columnName;

    private boolean isColumnNameDelimited;

    public IgnoredColumn(final String columnName) {
        super();
        this.columnName = columnName;
        isColumnNameDelimited = StringUtil.containsSpace(columnName);
    }

    public void validate(final List<String> errors, final String tableName) {
        if (StringUtil.isEmpty(columnName)) {
            errors.add(MessageUtil.getString("ValidationError.21",tableName));
        }
    }

    public boolean matches(final String columnName) {
        if (isColumnNameDelimited) {
            return this.columnName.equals(columnName);
        } else {
            return this.columnName.equalsIgnoreCase(columnName);
        }
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnNameDelimited(final boolean columnNameDelimited) {
        isColumnNameDelimited = columnNameDelimited;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof IgnoredColumn)) {
            return false;
        }
        return columnName.equals(((IgnoredColumn) o).getColumnName());
    }

    @Override
    public int hashCode() {
        return columnName.hashCode();
    }
}
