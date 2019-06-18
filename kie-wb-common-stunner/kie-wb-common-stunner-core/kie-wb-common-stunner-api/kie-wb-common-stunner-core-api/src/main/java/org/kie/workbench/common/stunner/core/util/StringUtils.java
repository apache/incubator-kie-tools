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
    public static boolean isQuotedConstant(String str) {
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
    public static String createQuotedConstant(final String str) {
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
    public static String createQuotedConstantOptionalNumeric(String str) {
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
     * Removes double-quotes from around a string
     * @param str
     * @return
     */
    public static String createUnquotedConstant(String str) {
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
