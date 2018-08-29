/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.client.util;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.TimeZone;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;

import static com.google.gwt.i18n.client.TimeZone.createTimeZone;
import static org.kie.workbench.common.services.shared.preferences.ApplicationPreferences.getKieTimezoneOffset;

public class TimeZoneUtils {

    private final static String DATE_FORMAT = ApplicationPreferences.getDroolsDateFormat();

    public final static DateTimeFormat FORMATTER = DateTimeFormat.getFormat(DATE_FORMAT);

    public static Date convertFromServerTimeZone(final Date date) {
        return FORMATTER.parse(formatWithServerTimeZone(date));
    }

    public static Date convertToServerTimeZone(final Date date) {
        final String convertedDate = internalFormatter().format(date, createTimeZone(getClientOffset(date)));
        return internalFormatter().parse(convertedDate);
    }

    public static String formatWithServerTimeZone(final Date date) {
        return FORMATTER.format(date, getTimeZone());
    }

    static int getClientOffset(final Date date) {

        final int standardOffset = getTimeZone().getStandardOffset();
        final int timezoneOffset = date.getTimezoneOffset();
        final int timeZoneOffsetInMinutes = standardOffset - (timezoneOffset * 2);

        return -timeZoneOffsetInMinutes;
    }

    public static TimeZone getTimeZone() {
        return createTimeZone(-getKieTimezoneOffsetInMinutes());
    }

    public static DateTimeFormat internalFormatter() {
        return DateTimeFormat.getFormat("MM-dd-yyyy HH:mm:ss");
    }

    private static int getKieTimezoneOffsetInMinutes() {
        final int offsetInMilliseconds = getKieTimezoneOffset();
        return offsetInMilliseconds / 1000 / 60;
    }
}
