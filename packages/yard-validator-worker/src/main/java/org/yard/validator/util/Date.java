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
package org.yard.validator.util;

/**
 * Our own custom Date.
 * We do not need all the features normal date has, just the ability to compare.
 * This brings less trouble and dependencies for the J2CL.
 */
public class Date implements Comparable {

    private final String value;
    private final int years;
    private final int months;
    private final int days;


    public Date(final String value) {
        this.value = value;

        final String[] split = value.split("-");
        // Format yyyy-MM-dd
        try {
            years = Integer.parseInt(split[0]);
            months = Integer.parseInt(split[1]);
            days = Integer.parseInt(split[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Date) {
            final Date other = (Date) o;
            final int compareYears = Integer.compare(years, other.years);
            if (compareYears == 0) {
                final int compareMonths = Integer.compare(months, other.months);
                if (compareMonths == 0) {
                    return Integer.compare(days, other.days);
                } else {
                    return compareMonths;
                }
            } else {
                return compareYears;
            }
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
