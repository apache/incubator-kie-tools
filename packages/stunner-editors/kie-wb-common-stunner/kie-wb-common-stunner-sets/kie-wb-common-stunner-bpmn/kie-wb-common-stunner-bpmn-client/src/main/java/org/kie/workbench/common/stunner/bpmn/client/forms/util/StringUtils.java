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


package org.kie.workbench.common.stunner.bpmn.client.forms.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.google.gwt.regexp.shared.RegExp;
import org.kie.workbench.common.stunner.core.util.Patterns;

/**
 * String utility functions
 */
public class StringUtils {

    public static final String ALPHA_NUM_REGEXP = "^[a-zA-Z0-9\\-\\_]*$";
    public static final String ALPHA_NUM_UNDERSCORE_DOT_GT_LT_REGEXP = "^[a-zA-Z0-9<>,\\_\\.]*$";
    public static final String ALPHA_NUM_SPACE_REGEXP = "^[a-zA-Z0-9\\-\\_\\ ]*$";
    public static final RegExp EXPRESSION = RegExp.compile(Patterns.EXPRESSION);
    public static final String REPEATING_DOTS_MSG = "Repeating .";
    public static final String REPEATING_DOTS = "..";
    public static final String EMPTY_GENERICS_MSG = "Empty Generics <>";
    public static final String EMPTY_GENERICS = "<>";
    public static final String MALFORMED_GENERICS_MSG = "Malformed Generics ><";
    public static final String MALFORMED_GENERICS = "><";

    private static URL url = new URL();

    private StringUtils() {
    }

    /**
     * Creates a string for a list by concatenating each object's String separated by commas
     * @param objects
     * @return
     */
    public static String getStringForList(List<? extends Object> objects) {
        return getStringForList(objects, ",");
    }

    /**
     * Creates a string for a list by concatenating each object's String separated by a custom delimiter
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

    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    @SuppressWarnings("rawtypes")
    public static boolean nonEmpty(Collection collection) {
        return !isEmpty(collection);
    }

    /**
     * Create display name for a datatype, e.g. for "org.test.Person", returns "Person [org.test]"
     * @param dataType
     * @return
     */
    public static String createDataTypeDisplayName(String dataType) {
        int i = dataType.lastIndexOf('.');
        int genericsIndexLT = dataType.lastIndexOf('<');
        int genericsIndexGT = dataType.lastIndexOf('>');

        if (genericsIndexLT != -1 || genericsIndexGT != -1) {
            return dataType;
        }
        StringBuilder formattedDataType = new StringBuilder();
        formattedDataType.append(dataType.substring(i + 1));
        if (i != -1) {
            formattedDataType.append(" [").append(dataType, 0, i).append("]");
        }
        return formattedDataType.toString();
    }

    /**
     * returns set of Data Types"
     * @param value
     * @return
     */
    public static Set<String> getSetDataTypes(String value) {
        value = preFilterVariablesForGenerics(value);
        Set<String> types = new HashSet<>();
        if (value == null || value.isEmpty()) {
            return types;
        }
        final String[] split = value.split(",");
        for (String dataType : split) {
            if (dataType.chars().filter(ch -> ch == ':').count() == 2) {
                String type = dataType.substring(dataType.indexOf(':') + 1, dataType.lastIndexOf(':'));
                types.add(postFilterForGenerics(type));
            }
        }

        return types;
    }

    /**
     * This method is for test purposes only. It needed to replace client side native code by
     * the mock.
     * @param u - mocked {@link URL} object
     */
    public static void setURL(URL u) {
        url = u;
    }

    /**
     * returns if a string is balanced for generics if it contains generics characters.
     * ie
     * List<String>             -> true
     * Map<String,String>       -> true
     * List<>                   -> false empty
     * List<String><String>     -> false
     * @param string
     * @return
     */
    public static boolean isOkWithGenericsFormat(String string) {
        Stack<Integer> stack = new Stack<>();
        List<List<Integer>> ranges = new ArrayList<>();
        int maximumLength = 0;
        int maximumIndex = 0;

        if (string.startsWith("<") || string.contains("<<") || string.contains("<.>") || string.contains(",<")) {
            return false;
        }

        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c == '<') {
                stack.push(i);
            } else if (c == '>') {
                if (stack.size() != 0) {
                    final Integer peek = stack.peek();
                    List<Integer> range = new ArrayList<>();
                    range.add(peek);
                    range.add(i);
                    ranges.add(range);
                    stack.pop();
                    int length = (i + 1) - peek;
                    if (length == 2) { // Empty <>
                        return false;
                    }
                    if (length > maximumLength) {
                        maximumLength = length;
                        maximumIndex = ranges.size() - 1;
                    }
                } else {
                    return false;
                }
            }
        }

        if (ranges.size() != 0) {

            List<Integer> maximumString = ranges.get(maximumIndex);
            int maximumBegin = maximumString.get(0);
            int maximumEnd = maximumString.get(1);

            for (int i = 0; i < ranges.size(); i++) {
                if (i == maximumIndex) {
                    continue;
                }
                List<Integer> range = ranges.get(i);
                int begin = range.get(0);
                int end = range.get(1) + 1;

                if ((begin < maximumBegin || end < maximumBegin)        // Left
                        || (begin > maximumEnd || end > maximumEnd)) {  // Right
                    return false;
                }
            }
        }
        return stack.size() == 0;
    }

    /**
     * prefilters a string to be formated for generics .*
     * @param string
     * @return
     */
    public static String preFilterVariablesForGenerics(String string) {

        if (string != null && !string.isEmpty()) {
            int index = 0;
            boolean done = false;
            do {
                int nameIndex = string.indexOf(":", index);
                String name = string.substring(index, nameIndex);

                int typeIndex = string.indexOf(":", nameIndex + 1);

                if (typeIndex == -1) {
                    typeIndex = string.length();
                    done = true;
                }
                String type = string.substring(nameIndex + 1, typeIndex).replace(",", "*");
                string = string.substring(0, nameIndex + 1) + type + string.substring(typeIndex);

                int tagsIndex = string.indexOf(":", typeIndex + 1);
                if (tagsIndex != -1) {
                    tagsIndex = string.indexOf(",", typeIndex + 1);
                    String tags = string.substring(typeIndex + 1, tagsIndex);
                }
                index = tagsIndex + 1;
                if (tagsIndex == -1) {
                    done = true;
                }
            } while (!done);
        }
        return string;
    }

    /**
     * prefilters a string to be formated for generics .*
     * @param string
     * @return
     */
    public static String preFilterVariablesTwoSemicolonForGenerics(String string) {

        if (string != null) {
            Stack<Integer> stack = new Stack<>();
            List<List<Integer>> ranges = new ArrayList<>();
            for (int i = 0; i < string.length(); i++) {
                char c = string.charAt(i);
                if (c == '<') {
                    stack.push(i);
                } else if (c == '>') {
                    if (stack.size() != 0) {
                        final Integer peek = stack.peek();
                        List<Integer> range = new ArrayList<>();
                        range.add(peek);
                        range.add(i);
                        ranges.add(range);
                        stack.pop();
                    }
                }
            }

            if (ranges.size() == 0) { // No Generics
                return string;
            }

            for (int i = 0; i < ranges.size(); i++) {
                List<Integer> range = ranges.get(i);
                int begin = range.get(0);
                int end = range.get(1) + 1;
                String theString = string.substring(begin, end).replace(",", "*");
                string = string.substring(0, begin) + theString + string.substring(end);
            }
        }
        return string;
    }

    /**
     * prefilters a string to be formated for generics .*
     * @param string
     * @return
     */
    public static String postFilterForGenerics(String string) {
        if (string != null && string.contains("*")) {
            string = string.replace("*", ",");
        }

        return string;
    }
}
