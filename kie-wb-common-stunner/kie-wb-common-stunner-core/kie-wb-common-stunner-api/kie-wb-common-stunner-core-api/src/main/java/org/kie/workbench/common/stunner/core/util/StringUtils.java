/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.core.util;

import java.util.Arrays;

public class StringUtils {

    private StringUtils() {
    }

    public static boolean isEmpty(final String s) {
        return null == s || s.trim().length() == 0;
    }

    public static boolean nonEmpty(final String s) {
        return !isEmpty(s);
    }

    public static boolean hasNonEmpty(final String... values) {
        return values != null && Arrays.stream(values).anyMatch(StringUtils::nonEmpty);
    }

    /**
     * Returns true if string starts and ends with double-quote
     * @param str
     * @return
     */
    public static boolean isQuoted(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return (str.startsWith("\"") && str.endsWith("\""));
    }

    /**
     * Puts strings inside quotes.
     * @param str
     * @return
     */
    public static String createQuotedString(final String str) {
        if (isEmpty(str)) {
            return str;
        }
        return "\"" + str + "\"";
    }

    /**
     * Puts strings inside quotes and numerics are left as they are.
     * @param str
     * @return
     */
    public static String createQuotedStringIfNotNumeric(String str) {
        if (isEmpty(str)) {
            return str;
        }
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return "\"" + str + "\"";
        }
        return str;
    }

    /**
     * Replacing unsafe characters by HTML escaping.
     * <p>
     * IMPORTANT NOTE
     * Url encoding is not supported on the Engine side so this method should be used for attribute values.
     * @param value a string to escape illegal characters on the client side
     * @return an escaped string
     */
    public static String replaceIllegalCharsAttribute(final String value) {
        if (isEmpty(value)) {
            return value;
        }

        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            final char c = value.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }

    /**
     * Returning unsafe characters by HTML escaping to the string.
     * @param value string encoded by {@link StringUtils#replaceIllegalCharsAttribute}
     * @return a decoded string
     */
    public static String revertIllegalCharsAttribute(final String value) {
        if (isEmpty(value)) {
            return value;
        }

        return value.replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&quot;", "\"");
    }

    /**
     * Removes double-quotes from around a string
     * @param str
     * @return
     */
    public static String createUnquotedString(String str) {
        if (isEmpty(str)) {
            return str;
        }
        if (str.startsWith("\"")) {
            str = str.substring(1);
        }
        if (str.endsWith("\"")) {
            str = str.substring(0,
                                str.length() - 1);
        }
        return str;
    }
}
