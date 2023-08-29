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
package org.kie.workbench.common.dmn.webapp.kogito.common.client.services;

import java.util.Objects;

import org.kie.workbench.common.dmn.api.editors.types.RangeValue;

public class FEELRangeParser {

    private static final String SEPARATOR = "..";
    private static final int SEPARATOR_LENGTH = SEPARATOR.length();
    private static final String INCLUDE_START = "[";
    private static final String EXCLUDE_START = "(";
    private static final String INCLUDE_END = "]";
    private static final String EXCLUDE_END = ")";

    public static RangeValue parse(final String input) {
        if (Objects.isNull(input)) {
            return new RangeValue();
        }

        final String trimmedInput = input.trim();

        if (!hasLeadingAndEndingParenthesis(trimmedInput)) {
            return new RangeValue();
        }

        final boolean includeStartValue = trimmedInput.startsWith(INCLUDE_START);
        final boolean includeEndValue = trimmedInput.endsWith(INCLUDE_END);

        boolean inQuotes = false;
        String startValue = "";
        String endValue = "";
        for (int current = 0; current < trimmedInput.length(); current++) {
            if (trimmedInput.charAt(current) == '\"') {
                inQuotes = !inQuotes;
            }

            if (isSeparator(current, inQuotes, trimmedInput)) {
                startValue = trimmedInput.substring(1, current).trim();
                endValue = trimmedInput.substring(current + SEPARATOR_LENGTH, trimmedInput.length() - 1).trim();
                if (isRangeValueValid(startValue) && isRangeValueValid(endValue)) {
                    final RangeValue rangeValue = new RangeValue();

                    rangeValue.setIncludeStartValue(includeStartValue);
                    rangeValue.setStartValue(startValue);
                    rangeValue.setIncludeEndValue(includeEndValue);
                    rangeValue.setEndValue(endValue);

                    return rangeValue;
                }
            }
        }

        return new RangeValue();
    }

    private static boolean isSeparator(final int current,
                                       final boolean inQuotes,
                                       final String trimmedInput) {
        if (inQuotes) {
            return false;
        }
        if (current > trimmedInput.length() - SEPARATOR_LENGTH) {
            return false;
        }
        return trimmedInput.substring(current, current + SEPARATOR_LENGTH).equals(SEPARATOR);
    }

    private static boolean hasLeadingAndEndingParenthesis(final String input) {
        if (!(input.startsWith(INCLUDE_START) || input.startsWith(EXCLUDE_START))) {
            return false;
        }
        if (!(input.endsWith(INCLUDE_END) || input.endsWith(EXCLUDE_END))) {
            return false;
        }
        return true;
    }

    private static boolean isRangeValueValid(final String input) {
        if (input.length() > 0) {
            return input.charAt(0) != '.' && input.charAt(input.length() - 1) != '.';
        } else {
            return false;
        }
    }
}
