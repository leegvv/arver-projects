package net.arver.jpa.generator.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.arver.jpa.generator.util.MessageUtil;
import net.arver.jpa.generator.util.StringUtil;

/**
 * .
 * @author li gu
 * @version 1.0.0.0
 **/
public class TableConfiguration extends PropertyHolder{

    private final List<ColumnOverride> columnOverrides;

    private final Map<IgnoredColumn, Boolean> ignoredColumns;

    private GeneratedKey generatedKey;

    private String catalog;

    private String schema;

    private String tableName;

    private String domainObjectName;

    private String alias;

    private boolean wildcardEscapingEnabled;

    private boolean delimitIdentifiers;

    private DomainObjectRenamingRule domainObjectRenamingRule;

    private ColumnRenamingRule columnRenamingRule;

    private boolean isAllColumnDelimitingEnabled;

    private String repositoryName;

    private final List<IgnoredColumnPattern> ignoredColumnPatterns = new ArrayList<>();

    public TableConfiguration(final Context context) {
        super();
        columnOverrides = new ArrayList<>();
        ignoredColumns = new HashMap<>();
    }

    public boolean isColumnIgnored(final String columnName) {
        for (final Map.Entry<IgnoredColumn, Boolean> entry : ignoredColumns.entrySet()) {
            if (entry.getKey().matches(columnName)) {
                entry.setValue(Boolean.TRUE);
                return true;
            }
        }
        for (final IgnoredColumnPattern ignoredColumnPattern : ignoredColumnPatterns) {
            if (ignoredColumnPattern.matches(columnName)) {
                return true;
            }
        }
        return false;
    }

    private void addIgnoredColumn(final IgnoredColumn ignoredColumn) {
        ignoredColumns.put(ignoredColumn, Boolean.FALSE);
    }

    private void addIgnoredColumnPattern(final IgnoredColumnPattern ignoredColumnPattern) {
        ignoredColumnPatterns.add(ignoredColumnPattern);
    }

    public void addColumnOverride(final ColumnOverride columnOverride) {
        columnOverrides.add(columnOverride);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TableConfiguration)) {
            return false;
        }

        final TableConfiguration that = (TableConfiguration) o;
        return Objects.equals(this.catalog, that.catalog)
                && Objects.equals(this.schema, that.schema)
                && Objects.equals(this.tableName, that.tableName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(catalog, schema, tableName);
    }

    public ColumnOverride getColumnOverride(final String columnName) {
        for (final ColumnOverride co : columnOverrides) {
            if (co.isColumnNameDelimited()) {
                if (columnName.equals(co.getColumnName())) {
                    return co;
                }
            } else {
                if (columnName.equalsIgnoreCase(co.getColumnName())) {
                    return co;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return StringUtil.composeFullyQualifiedTableName(catalog, schema, tableName, '.');
    }

    public void validate(List<String> errors, int listPosition) {
        if (StringUtil.isEmpty(tableName)) {
            errors.add(MessageUtil.getString("ValidationError.6", Integer.toString(listPosition)));
        }

        String fqTableName = StringUtil.composeFullyQualifiedTableName(catalog, schema, tableName, '.');

        if (generatedKey != null) {
            generatedKey.validate(errors, fqTableName);
        }

        if (domainObjectRenamingRule != null) {
            domainObjectRenamingRule.validate(errors, fqTableName);
        }

        if (columnRenamingRule != null) {
            columnRenamingRule.validate(errors, fqTableName);
        }

        for (ColumnOverride columnOverride : columnOverrides) {
            columnOverride.validate(errors, fqTableName);
        }

        for (IgnoredColumn ignoredColumn : ignoredColumns.keySet()) {
            ignoredColumn.validate(errors, fqTableName);
        }

        for (IgnoredColumnPattern ignoredColumnPattern : ignoredColumnPatterns) {
            ignoredColumnPattern.validate(errors, fqTableName);
        }
    }

    public List<String> getIgnoredColumnsInError() {
        List<String> answer = new ArrayList<>();

        for (Map.Entry<IgnoredColumn, Boolean> entry : ignoredColumns.entrySet()) {
            if (Boolean.FALSE.equals(entry.getValue())) {
                answer.add(entry.getKey().getColumnName());
            }
        }
        return answer;
    }



    public List<ColumnOverride> getColumnOverrides() {
        return columnOverrides;
    }

    public Map<IgnoredColumn, Boolean> getIgnoredColumns() {
        return ignoredColumns;
    }

    public Optional<GeneratedKey> getGeneratedKey() {
        return Optional.ofNullable(generatedKey);
    }

    public void setGeneratedKey(final GeneratedKey generatedKey) {
        this.generatedKey = generatedKey;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(final String catalog) {
        this.catalog = catalog;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(final String schema) {
        this.schema = schema;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }

    public String getDomainObjectName() {
        return domainObjectName;
    }

    public void setDomainObjectName(final String domainObjectName) {
        this.domainObjectName = domainObjectName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(final String alias) {
        this.alias = alias;
    }

    public boolean isWildcardEscapingEnabled() {
        return wildcardEscapingEnabled;
    }

    public void setWildcardEscapingEnabled(final boolean wildcardEscapingEnabled) {
        this.wildcardEscapingEnabled = wildcardEscapingEnabled;
    }

    public boolean isDelimitIdentifiers() {
        return delimitIdentifiers;
    }

    public void setDelimitIdentifiers(final boolean delimitIdentifiers) {
        this.delimitIdentifiers = delimitIdentifiers;
    }

    public DomainObjectRenamingRule getDomainObjectRenamingRule() {
        return domainObjectRenamingRule;
    }

    public void setDomainObjectRenamingRule(final DomainObjectRenamingRule domainObjectRenamingRule) {
        this.domainObjectRenamingRule = domainObjectRenamingRule;
    }

    public ColumnRenamingRule getColumnRenamingRule() {
        return columnRenamingRule;
    }

    public void setColumnRenamingRule(final ColumnRenamingRule columnRenamingRule) {
        this.columnRenamingRule = columnRenamingRule;
    }

    public boolean isAllColumnDelimitingEnabled() {
        return isAllColumnDelimitingEnabled;
    }

    public void setAllColumnDelimitingEnabled(final boolean allColumnDelimitingEnabled) {
        isAllColumnDelimitingEnabled = allColumnDelimitingEnabled;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(final String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public List<IgnoredColumnPattern> getIgnoredColumnPatterns() {
        return ignoredColumnPatterns;
    }
}
