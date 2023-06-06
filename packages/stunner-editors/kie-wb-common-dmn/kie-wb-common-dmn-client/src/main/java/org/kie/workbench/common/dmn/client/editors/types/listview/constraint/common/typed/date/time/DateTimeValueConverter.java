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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.time;

import javax.inject.Inject;

import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.DateValueFormatter;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeValueFormatter;
import org.kie.workbench.common.stunner.core.util.StringUtils;

public class DateTimeValueConverter {

    static final String PREFIX = "date and time(\"";
    static final String SUFFIX = "\")";

    private static final int DATE_LENGTH = 10;

    private final DateValueFormatter dateValueFormatter;
    private final TimeValueFormatter timeValueFormatter;

    @Inject
    public DateTimeValueConverter(final DateValueFormatter dateValueFormatter,
                                  final TimeValueFormatter timeValueFormatter) {
        this.dateValueFormatter = dateValueFormatter;
        this.timeValueFormatter = timeValueFormatter;
    }

    public String toDMNString(final DateTimeValue value) {

        if (!value.hasDate()) {
            return "";
        }

        String dmnString = getDate(value);
        if (value.hasTime()) {
            dmnString += "T" + getTime(value);
        }

        return appendPrefixAndSuffix(dmnString);
    }

    String appendPrefixAndSuffix(final String value) {
        return PREFIX + value + SUFFIX;
    }

    String removePrefixAndSuffix(final String rawValue) {
        return rawValue.replace(PREFIX, "").replace(SUFFIX, "").trim();
    }

    String getTime(final DateTimeValue value) {
        return timeValueFormatter.getTime(value.getTime());
    }

    String getDate(final DateTimeValue value) {
        return dateValueFormatter.getDate(value.getDate());
    }

    public DateTimeValue fromDMNString(final String dmnString) {

        final String value = removePrefixAndSuffix(dmnString);

        final String date = extractDate(value);
        final String time = extractTime(value);

        final DateTimeValue dateTimeValue = new DateTimeValue();

        dateTimeValue.setDate(dateValueFormatter.addPrefixAndSuffix(date));
        if (!StringUtils.isEmpty(time)) {
            dateTimeValue.setTime(timeValueFormatter.appendPrefixAndSuffix(time));
        }

        return dateTimeValue;
    }

    String extractTime(final String value) {

        if (value.length() <= DATE_LENGTH + 1) {
            return "";
        }
        return value.substring(DATE_LENGTH + 1);
    }

    String extractDate(final String value) {
        return (StringUtils.isEmpty(value) || value.trim().length() < DATE_LENGTH) ? ""
                : value.substring(0, DATE_LENGTH);
    }

    public String toDisplay(final String rawValue) {

        final DateTimeValue value = fromDMNString(rawValue);

        final String date = dateValueFormatter.toDisplay(value.getDate());
        if (value.hasTime()) {
            return date + ", " + timeValueFormatter.toDisplay(value.getTime());
        } else {
            return date;
        }
    }
}
