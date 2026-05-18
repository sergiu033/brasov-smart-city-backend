package com.smartcity.security;

import org.springframework.web.util.HtmlUtils;

public class SanitizationUtils {

    /**
     * Sanitizes a string by trimming it and escaping HTML characters.
     * This prevents XSS by ensuring that any HTML tags provided by the user
     * are rendered as literal text rather than executed as scripts.
     */
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        return HtmlUtils.htmlEscape(input.trim());
    }
}
