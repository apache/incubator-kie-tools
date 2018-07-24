/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.util;

import java.util.List;

import com.google.gwt.http.client.URL;

/**
 * String utility functions
 */
public class StringUtils {

    public static final String ALPHA_NUM_REGEXP = "^[a-zA-Z0-9\\-\\_]*$";

    /**
     * Puts strings inside quotes and numerics are left as they are.
     * @param str
     * @return
     */
    public static String createQuotedConstant(String str) {
        if (str == null || str.isEmpty()) {
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
        if (str == null || str.isEmpty()) {
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
     * Creates a string for a list by concatenating each object's String separated by commas
     * @param objects
     * @return
     */
    public static String getStringForList(List<? extends Object> objects) {
        StringBuilder sb = new StringBuilder();
        objects.forEach(object -> {
            String value = object.toString();
            if (value != null && !value.isEmpty()) {
                sb.append(value).append(',');
            }
        });
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * URLEncode a string
     * @param s
     * @return
     */
    public static String urlEncode(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        return URL.encodeQueryString(s);
    }

    /**
     * URLDecode a string
     * @param s
     * @return
     */
    public static String urlDecode(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        return URL.decodeQueryString(s);
    }

    /**
     * Create display name for a datatype, e.g. for "org.test.Person", returns "Person [org.test]"
     * @param dataType
     * @return
     */
    public static String createDataTypeDisplayName(String dataType) {
        int i = dataType.lastIndexOf('.');
        StringBuilder formattedDataType = new StringBuilder();
        formattedDataType.append(dataType.substring(i + 1));
        formattedDataType.append(" [").append(dataType.substring(0,
                                                                 i)).append("]");
        return formattedDataType.toString();
    }
}
