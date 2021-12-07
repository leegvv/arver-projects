package net.arver.jpa.generator.config;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import net.arver.jpa.generator.util.MessageUtil;
import net.arver.jpa.generator.util.StringUtil;

/**
 * .
 * @author li gu
 * @version 1.0.0.0
 **/
public class IgnoredColumnPattern {

    private final String patternRegex;
    private final Pattern pattern;
    private final List<IgnoredColumnException> exceptions;

    public IgnoredColumnPattern(final String patternRegex) {
        this.patternRegex = patternRegex;
        pattern = Pattern.compile(patternRegex);
        exceptions = new ArrayList<>();
    }

    public void addException(final IgnoredColumnException exception) {
        exceptions.add(exception);
    }

    public boolean matches(final String columnName) {
        boolean matches = pattern.matcher(columnName).matches();
        if (matches) {
            for (final IgnoredColumnException exception : exceptions) {
                if (exception.matches(columnName)) {
                    matches = false;
                    break;
                }
            }
        }
        return matches;
    }

    public void validate(final List<String> errors, final String tableName) {
        if (StringUtil.isEmpty(patternRegex)) {
            errors.add(MessageUtil.getString("ValidationError.27", tableName));
        }
    }
}
