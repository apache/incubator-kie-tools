/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.webapp.kogito.common.client.services;

import java.util.Objects;

import org.kie.workbench.common.dmn.api.editors.types.RangeValue;
import org.kie.workbench.common.stunner.core.util.StringUtils;

public class FEELRangeParser {

    private static final String SEPARATOR = "..";
    private static final int SEPARATOR_LENGTH = SEPARATOR.length();
    private static final String INCLUDE_START = "[";
    private static final String EXCLUDE_START = "(";
    private static final String INCLUDE_END = "]";
    private static final String EXCLUDE_END = ")";

    public static RangeValue parse(final String input) {
        final RangeValue rangeValue = new RangeValue();
        if (Objects.isNull(input)) {
            return rangeValue;
        }
        final String trimmedInput = input.trim();
        if (!(trimmedInput.startsWith(INCLUDE_START) || trimmedInput.startsWith(EXCLUDE_START))) {
            return rangeValue;
        }
        if (!(trimmedInput.endsWith(INCLUDE_END) || trimmedInput.endsWith(EXCLUDE_END))) {
            return rangeValue;
        }

        boolean inQuotes = false;
        boolean includeStartValue = trimmedInput.startsWith(INCLUDE_START);
        boolean includeEndValue = trimmedInput.endsWith(INCLUDE_END);
        String startValue = "";
        String endValue = "";

        for (int current = 0; current < trimmedInput.length(); current++) {
            if (trimmedInput.charAt(current) == '\"') {
                inQuotes = !inQuotes;
            }

            if (isSeparator(current, inQuotes, trimmedInput)) {
                startValue = trimmedInput.substring(1, current).trim();
                endValue = trimmedInput.substring(current + SEPARATOR_LENGTH, trimmedInput.length() - 1).trim();
                break;
            }
        }

        if (StringUtils.isEmpty(startValue) || StringUtils.isEmpty(endValue)) {
            return rangeValue;
        }

        rangeValue.setIncludeStartValue(includeStartValue);
        rangeValue.setStartValue(startValue);
        rangeValue.setIncludeEndValue(includeEndValue);
        rangeValue.setEndValue(endValue);

        return rangeValue;
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
}
