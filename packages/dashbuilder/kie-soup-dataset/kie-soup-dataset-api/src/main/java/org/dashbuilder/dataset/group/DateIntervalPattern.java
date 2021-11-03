/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.dataset.group;

public class DateIntervalPattern {

    public static final String YEAR = "yyyy";
    public static final String MONTH = "yyyy-MM";
    public static final String DAY = "yyyy-MM-dd";
    public static final String HOUR = "yyyy-MM-dd HH";
    public static final String MINUTE = "yyyy-MM-dd HH:mm";
    public static final String SECOND = "yyyy-MM-dd HH:mm:ss";

    public static String getPattern(DateIntervalType type) {
        if (type.getIndex() <= DateIntervalType.SECOND.getIndex()) {
            return SECOND;
        }
        if (DateIntervalType.MINUTE.equals(type)) {
            return MINUTE;
        }
        if (DateIntervalType.HOUR.equals(type)) {
            return HOUR;
        }
        if (DateIntervalType.DAY.equals(type) || DateIntervalType.DAY_OF_WEEK.equals(type) 
                || DateIntervalType.WEEK.equals(type)) {
            return DAY;
        }
        if (DateIntervalType.MONTH.equals(type) || DateIntervalType.QUARTER.equals(type)) {
            return MONTH;
        }
        return YEAR;
    }
}
