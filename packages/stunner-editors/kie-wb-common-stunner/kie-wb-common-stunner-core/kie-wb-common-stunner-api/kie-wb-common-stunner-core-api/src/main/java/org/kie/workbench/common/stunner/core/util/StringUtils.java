/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
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
     * Replacing unsafe characters by HTML escaping.
     * <p>
     * IMPORTANT NOTE
     * Url encoding is not supported on the Engine side so this method should be used for attribute values.
     * @param value a string to escape illegal characters on the client side for Data Objects
     * @return an escaped string
     */
    public static String replaceIllegalCharsForDataObjects(final String value) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            final char c = value.charAt(i);

            switch (c) {
                case '#':
                case '"':
                case ':':
                case ' ':
                    sb.append("-");
                    break;
                case '\n': // Leave as is
                    break;

                // Normal Characters
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                case '\'':
                case '?':
                case '*':
                case '/':
                case '+':
                case '_':
                case '-':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    sb.append(c);
                    break;
                default:
                    sb.append("-");
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
