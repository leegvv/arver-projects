package net.arver.jpa.generator.util;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * MessageUtil.
 * @author li gu
 * @version 1.0.0.0
 **/
public class MessageUtil {

    private static final String BUNDLE_NAME = "net.arver.jpa.generator.util.message.messages";

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private MessageUtil(){}

    public static String getString(final String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (final MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    public static String getString(final String key, final String ... params) {
        try {
            return MessageFormat.format(RESOURCE_BUNDLE.getString(key), params);
        } catch (final MissingResourceException e) {
            return '!' + key + '!';
        }
    }


}
