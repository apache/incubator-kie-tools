/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataset.date;

import java.util.Date;

import org.dashbuilder.dataset.group.DateIntervalType;

/**
 * Class for the definition of time quantitys, both positive and negative. For example:
 * <ul>
 * <li><i>10second</i></li>
 * <li><i>-1year</i></li>
 * <li><i>+2quarter</i></li>
 * </ul>
 * </p>
 */
public class TimeAmount {

    private long quantity = 0;
    private DateIntervalType type = DateIntervalType.DAY;

    public TimeAmount() {
        this(0, DateIntervalType.YEAR);
    }

    public TimeAmount(long quantity, DateIntervalType sizeType) {
        this.quantity = quantity;
        this.type = sizeType;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public DateIntervalType getType() {
        return type;
    }

    public void setType(DateIntervalType type) {
        this.type = type;
    }

    public long toMillis() {
        return quantity * DateIntervalType.getDurationInMillis(type);
    }

    public String toString() {
        return quantity + " " + type.name().toLowerCase();
    }

    public TimeAmount cloneInstance() {
        TimeAmount clone = new TimeAmount();
        clone.quantity = quantity;
        clone.type = type;
        return clone;
    }

    public void adjustDate(Date d) {
        if (DateIntervalType.MILLENIUM.equals(type)) {
            d.setYear(d.getYear() + (int) quantity*1000);
        }
        if (DateIntervalType.CENTURY.equals(type)) {
            d.setYear(d.getYear() + (int) quantity*100);
        }
        if (DateIntervalType.DECADE.equals(type)) {
            d.setYear(d.getYear() + (int) quantity*10);
        }
        if (DateIntervalType.YEAR.equals(type)) {
            d.setYear(d.getYear() + (int) quantity);
        }
        if (DateIntervalType.QUARTER.equals(type)) {
            d.setMonth(d.getMonth() + (int) quantity*3);
        }
        if (DateIntervalType.MONTH.equals(type)) {
            d.setMonth(d.getMonth() + (int) quantity);
        }
        if (DateIntervalType.WEEK.equals(type)) {
            d.setDate(d.getDate() + (int) quantity*7);
        }
        if (DateIntervalType.DAY.equals(type)) {
            d.setDate(d.getDate() + (int) quantity);
        }
        if (DateIntervalType.HOUR.equals(type)) {
            d.setHours(d.getHours() + (int) quantity);
        }
        if (DateIntervalType.MINUTE.equals(type)) {
            d.setMinutes(d.getMinutes() + (int) quantity);
        }
        if (DateIntervalType.SECOND.equals(type)) {
            d.setSeconds(d.getSeconds() + (int) quantity);
        }
    }

    /**
     * Parses a time amount expression. For example: "10second", "-1year", ...
     * @return A TimeAmount instance.
     * @throws IllegalArgumentException If the expression is not valid
     */
    public static TimeAmount parse(String timeAmount) {
        if (timeAmount == null || timeAmount.length() == 0) {
            throw new IllegalArgumentException("Empty time amount expression");
        }
        String number = "";
        String expr = timeAmount.trim();
        boolean isNegative = expr.startsWith("-");
        boolean isPositive = expr.startsWith("+");
        int i =  isNegative || isPositive ? 1 : 0;
        for (; i<expr.length(); i++) {
            char ch = expr.charAt(i);
            if (Character.isDigit(ch)) number += ch;
            else break;
        }
        String type = expr.substring(i).trim();
        DateIntervalType intervalType = DateIntervalType.getByName(type);
        if (number.length() == 0) {
            throw new IllegalArgumentException("Missing quantity (ex '-10year'): "+ timeAmount);
        }
        if (intervalType == null) {
            throw new IllegalArgumentException("Invalid interval type (ex '-10year'): "+ timeAmount);
        }
        return new TimeAmount(Long.parseLong(number) * (isNegative ? -1 : 1), intervalType);
    }
}
