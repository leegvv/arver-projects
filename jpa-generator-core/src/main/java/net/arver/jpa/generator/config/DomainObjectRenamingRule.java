package net.arver.jpa.generator.config;

import java.util.List;
import net.arver.jpa.generator.util.MessageUtil;
import net.arver.jpa.generator.util.StringUtil;

/**
 * .
 * @author li gu
 * @version 1.0.0.0
 **/
public class DomainObjectRenamingRule {

    private String searchString;

    private String replaceString;

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(final String searchString) {
        this.searchString = searchString;
    }

    public String getReplaceString() {
        return replaceString;
    }

    public void setReplaceString(final String replaceString) {
        this.replaceString = replaceString;
    }

    public void validate(final List<String> errors, final String tableName) {
        if (StringUtil.isEmpty(searchString)) {
            errors.add(MessageUtil.getString("ValidationError.28", tableName));
        }
    }
}
