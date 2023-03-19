/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.displayer.client;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import org.dashbuilder.common.client.StringUtils;
import org.dashbuilder.dataset.client.resources.i18n.DayOfWeekConstants;
import org.dashbuilder.dataset.client.resources.i18n.MonthConstants;
import org.dashbuilder.dataset.date.DayOfWeek;
import org.dashbuilder.dataset.date.Month;
import org.dashbuilder.displayer.ColumnSettings;

public class DisplayerGwtFormatter implements AbstractDisplayer.Formatter {

    protected static Map<String,NumberFormat> numberPatternMap = new HashMap<String, NumberFormat>();
    protected static Map<String,DateTimeFormat> datePatternMap = new HashMap<String, DateTimeFormat>();

    @Override
    public Date parseDate(String pattern, String d) {
        DateTimeFormat df = getDateFormat(pattern);
        return df.parse(d);
    }

    @Override
    public String formatDate(String pattern, Date d) {
        DateTimeFormat df = getDateFormat(pattern);
        return df.format(d);
    }

    @Override
    public String formatNumber(String pattern, Number n) {
        NumberFormat f = getNumberFormat(pattern);
        return f.format(n);
    }

    @Override
    public String formatDayOfWeek(DayOfWeek dayOfWeek) {
        return DayOfWeekConstants.INSTANCE.getString(dayOfWeek.name());
    }

    @Override
    public String formatMonth(Month month) {
        return MonthConstants.INSTANCE.getString(month.name());
    }

    protected NumberFormat getNumberFormat(String pattern) {
        if (StringUtils.isBlank(pattern)) {
            return getNumberFormat(ColumnSettings.NUMBER_PATTERN);
        }
        NumberFormat format = numberPatternMap.get(pattern);
        if (format == null) {
            format = NumberFormat.getFormat(pattern);
            numberPatternMap.put(pattern, format);
        }
        return format;
    }

    protected DateTimeFormat getDateFormat(String pattern) {
        if (StringUtils.isBlank(pattern)) {
            return getDateFormat(ColumnSettings.DATE_PATTERN);
        }
        DateTimeFormat format = datePatternMap.get(pattern);
        if (format == null) {
            format = DateTimeFormat.getFormat(pattern);
            datePatternMap.put(pattern, format);
        }
        return format;
    }
}
