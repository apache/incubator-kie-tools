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

import com.google.gwt.regexp.shared.RegExp;
import org.kie.workbench.common.stunner.core.util.Patterns;

/**
 * String utility functions
 */
public class StringUtils {

    public static final String ALPHA_NUM_REGEXP = "^[a-zA-Z0-9\\-\\_]*$";
    public static final String ALPHA_NUM_UNDERSCORE_DOT_REGEXP = "^[a-zA-Z0-9\\_\\.]*$";
    public static final String ALPHA_NUM_UNDERSCORE_COMMA_REGEXP = "^[a-zA-Z0-9\\-\\_\\,]*$";
    public static final String ALPHA_NUM_SPACE_REGEXP = "^[a-zA-Z0-9\\-\\_\\ ]*$";
    public static final RegExp EXPRESSION = RegExp.compile(Patterns.EXPRESSION);

    private static URL url = new URL();

    /**
     * Creates a string for a list by concatenating each object's String separated by commas
     *
     * @param objects
     * @return
     */
    public static String getStringForList(List<? extends Object> objects) {
        return getStringForList(objects, ",");
    }

    /**
     * Creates a string for a list by concatenating each object's String separated by a custom delimiter
     *
     * @param objects
     * @param delimiter
     * @return
     */
    public static String getStringForList(List<? extends Object> objects, final String delimiter) {
        if (null == delimiter || delimiter.isEmpty()) {
            return getStringForList(objects);
        }

        StringBuilder sb = new StringBuilder();
        objects.forEach(object -> {
            String value = object.toString();
            if (value != null && !value.isEmpty()) {
                sb.append(value).append(delimiter);
            }
        });
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * URLEncode a string
     *
     * @param s
     * @return
     */
    public static String urlEncode(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        return url.encodeQueryString(s);
    }

    /**
     * URLDecode a string
     *
     * @param s
     * @return
     */
    public static String urlDecode(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        return url.decodeQueryString(s);
    }

    /**
     * Create display name for a datatype, e.g. for "org.test.Person", returns "Person [org.test]"
     *
     * @param dataType
     * @return
     */
    public static String createDataTypeDisplayName(String dataType) {
        int i = dataType.lastIndexOf('.');
        StringBuilder formattedDataType = new StringBuilder();
        formattedDataType.append(dataType.substring(i + 1));
        formattedDataType.append(" [").append(dataType, 0, i).append("]");
        return formattedDataType.toString();
    }

    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean nonEmpty(String s) {
        return !isEmpty(s);
    }

    /**
     * This method is for test purposes only. It needed to replace client side native code by
     * the mock.
     * @param u - mocked {@link URL} object
     */
    public static void setURL(URL u) {
        url = u;
    }
}
