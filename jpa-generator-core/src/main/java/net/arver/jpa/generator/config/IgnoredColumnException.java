package net.arver.jpa.generator.config;

import java.util.List;
import net.arver.jpa.generator.util.MessageUtil;
import net.arver.jpa.generator.util.StringUtil;

/**
 * .
 * @author li gu
 * @version 1.0.0.0
 **/
public class IgnoredColumnException extends IgnoredColumn{

    public IgnoredColumnException(final String columnName) {
        super(columnName);
    }

    @Override
    public void validate(final List<String> errors, final String tableName) {
        if (StringUtil.isEmpty(columnName)) {
            errors.add(MessageUtil.getString("ValidationError.26", tableName));
        }
    }
}
