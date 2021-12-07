package net.arver.jpa.generator.util;

import java.util.StringTokenizer;

/**
 * 字符串工具类.
 * @author li gu
 * @version 1.0.0.0
 **/
public class StringUtil {

    private StringUtil() {}

    public static boolean isEmpty(final String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(final String str) {
        return !isEmpty(str);
    }

    public static boolean isTrue(final String str) {
        return "true".equalsIgnoreCase(str);
    }

    public static String composeFullyQualifiedTableName(final String catalog, final String schema, final String tableName, char separator) {
        final StringBuilder sb = new StringBuilder();
        if (isNotEmpty(catalog)) {
            sb.append(catalog);
            sb.append(separator);
        }
        if (isNotEmpty(schema)) {
            sb.append(schema);
            sb.append(separator);
        } else {
            if (sb.length() > 0) {
                sb.append(separator);
            }
        }
        sb.append(tableName);
        return sb.toString();
    }

    public static boolean containsSpace(final String str) {
        return str != null && str.indexOf(' ') != -1;
    }

    public static String escapeStringForJava(final String str) {
        final StringTokenizer st = new StringTokenizer(str, "\"", true);
        final StringBuilder sb = new StringBuilder();
        while (st.hasMoreTokens()) {
            final String token = st.nextToken();
            if ("\"".equals(token)) {
                sb.append("\\\"");
            } else {
                sb.append(token);
            }
        }
        return sb.toString();
    }

    public static boolean containsSQLWildcard(final String str) {
        if (str == null) {
            return false;
        }
        return str.indexOf('%') != -1 || str.indexOf('_') != -1;
    }

}
