/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

    private StringUtils() {
    }

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
     * Encode a url parameters see {@link URL#encodeQueryString(String)}
     * <p>
     * IMPORTANT NOTE
     * Url encoding is not supported on the Engine side use this method for the internal communication
     * or to UI/Marshaller communications only.
     * For storing data in XML use {@link org.kie.workbench.common.stunner.core.util.StringUtils#replaceIllegalCharsAttribute}
     * @param s
     * @return
     */
    public static String urlEncode(String s) {
        if (isEmpty(s)) {
            return s;
        }
        return url.encodeQueryString(s);
    }

    /**
     * Decode a url parameters see {@link URL#decodeQueryString(String)}
     *
     * @param s
     * @return
     */
    public static String urlDecode(String s) {
        if (isEmpty(s)) {
            return s;
        }
        return url.decodeQueryString(s);
    }

    /**
     * Equivalent of {@link java.net.URLEncoder#encode(String, String)} in UTF-8 encoding on server side
     * <p>
     * IMPORTANT NOTE
     * Url encoding is not supported on the jBPM Engine side use this method for the internal communication
     * or to UI/Marshaller communications only.
     * For storing data in XML use {@link org.kie.workbench.common.stunner.core.util.StringUtils#replaceIllegalCharsAttribute}
     * @param s a string to encode on the client side
     * @return an encoded string
     */
    public static String encode(String s) {
        if (isEmpty(s)) {
            return s;
        }
        return url.encode(s);
    }

    /**
     * Equivalent of {@link java.net.URLDecoder#decode(String, String)} in UTF-8 decoding on server side
     * @param s a string to decode on the client side
     * @return a decoded string
     */
    public static String decode(String s) {
        if (isEmpty(s)) {
            return s;
        }
        return url.decode(s);
    }

    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static boolean nonEmpty(String value) {
        return !isEmpty(value);
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

    /**
     * This method is for test purposes only. It needed to replace client side native code by
     * the mock.
     * @param u - mocked {@link URL} object
     */
    public static void setURL(URL u) {
        url = u;
    }
}
