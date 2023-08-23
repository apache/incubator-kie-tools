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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import org.kie.workbench.common.stunner.core.util.StringUtils;

public class DateValueFormatter {

    static final String PREFIX = "date(\"";
    static final String SUFFIX = "\")";
    private static final DateTimeFormat RAW_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd");
    private static final DateTimeFormat DISPLAY_FORMAT = DateTimeFormat.getFormat("dd MMM yyyy");

    public String toDisplay(final String rawValue) {
        if (StringUtils.isEmpty(rawValue)) {
            return "";
        }

        final String dateString = removePrefixAndSuffix(rawValue);

        try {
            final Date date = RAW_FORMAT.parse(dateString);
            return DISPLAY_FORMAT.format(date);
        } catch (final IllegalArgumentException exception) {
            return "";
        }
    }

    String removePrefixAndSuffix(final String rawValue) {
        return rawValue.replace(PREFIX, "").replace(SUFFIX, "").trim();
    }

    public String addPrefixAndSuffix(final String value) {
        return PREFIX + value + SUFFIX;
    }

    String toRaw(final String displayValue) {
        try {
            final Date date = DISPLAY_FORMAT.parse(displayValue);
            return addPrefixAndSuffix(RAW_FORMAT.format(date));
        } catch (final IllegalArgumentException exception) {
            return "";
        }
    }

    public String getDate(final String raw) {
        return removePrefixAndSuffix(raw);
    }
}