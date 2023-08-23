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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker;

import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.util.StringUtils;

import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeValue.TimeZoneMode.NONE;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeValue.TimeZoneMode.OFFSET;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeValue.TimeZoneMode.TIMEZONE;

public class TimeValueFormatter {

    static final String PREFIX = "time(\"";
    static final String SUFFIX = "\")";
    static final String UTC = "UTC";

    private final TimeZoneProvider timeZoneProvider;

    @Inject
    public TimeValueFormatter(final TimeZoneProvider timeZoneProvider) {
        this.timeZoneProvider = timeZoneProvider;
    }

    public String toDisplay(final String input) {
        final String timeString = removePrefixAndSuffix(input);
        return formatTime(timeString);
    }

    public String buildRawValue(final String time,
                                final String selectedTimeZone) {

        final String rawValue;
        if (StringUtils.isEmpty(selectedTimeZone)) {
            if (StringUtils.isEmpty(time)) {
                rawValue = "";
            } else {
                rawValue = appendPrefixAndSuffix(time);
            }
        } else {
            if (UTC.equals(selectedTimeZone)) {
                rawValue = appendPrefixAndSuffix(time + "Z");
            } else if (selectedTimeZone.startsWith("+") || selectedTimeZone.startsWith("-")) {
                rawValue = appendPrefixAndSuffix(time + selectedTimeZone);
            } else {
                rawValue = appendPrefixAndSuffix(time + "@" + selectedTimeZone);
            }
        }

        return rawValue;
    }

    String removePrefixAndSuffix(final String value) {
        return value.replace(PREFIX, "").replace(SUFFIX, "");
    }

    String formatTime(final String value) {

        final TimeValue timeValue = getTimeValue(value);

        switch (timeValue.getTimeZoneMode()) {

            case OFFSET:
                return timeValue.getTime() + " UTC " + timeValue.getTimeZoneValue();

            case TIMEZONE:
                return timeValue.getTime() + " " + timeValue.getTimeZoneValue();

            default:
                return timeValue.getTime();
        }
    }

    public TimeValue getTimeValue(final String val) {

        final String value = removePrefixAndSuffix(val);
        final TimeValue timeValue = new TimeValue();

        if (value.contains("@")) {
            final String[] parts = value.split("@");
            timeValue.setTime(parts[0].trim());
            timeValue.setTimeZoneValue(parts[1].trim());
            timeValue.setTimeZoneMode(TIMEZONE);
            return timeValue;
        }

        if (value.contains("-")) {
            final String[] parts = value.split("-");
            timeValue.setTime(parts[0].trim());
            timeValue.setTimeZoneValue("-" + parts[1].trim());
            timeValue.setTimeZoneMode(OFFSET);
            return timeValue;
        }

        if (value.contains("+")) {
            final String[] parts = value.split("\\+");
            timeValue.setTime(parts[0].trim());
            timeValue.setTimeZoneValue("+" + parts[1].trim());
            timeValue.setTimeZoneMode(OFFSET);
            return timeValue;
        }

        if (value.endsWith("Z")) {
            final String timePart = value.replace("Z", "");
            timeValue.setTime(timePart);
            timeValue.setTimeZoneValue("UTC");
            timeValue.setTimeZoneMode(TIMEZONE);
            return timeValue;
        }

        timeValue.setTimeZoneValue("");
        timeValue.setTimeZoneMode(NONE);
        timeValue.setTime(value);
        return timeValue;
    }

    public String toRaw(final String input) {

        if (!input.contains(" ")) {
            return appendPrefixAndSuffix(input);
        }

        final String[] parts = input.split(" ");
        if (parts.length == 2) {
            final String secondPart = parts[1].trim();
            if (timeZoneProvider.isTimeZone(secondPart)) {
                return appendPrefixAndSuffix(parts[0].trim() + "@" + secondPart);
            } else {
                return appendPrefixAndSuffix(parts[0].trim() + "Z");
            }
        }

        final String time = parts[0].trim();
        final String offSet = parts[2].trim();

        return appendPrefixAndSuffix(time + offSet);
    }

    public String getTime(final String raw) {
        return removePrefixAndSuffix(raw);
    }

    public String appendPrefixAndSuffix(final String input) {
        return PREFIX + input + SUFFIX;
    }
}
