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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.day.time;

import java.util.Objects;
import java.util.stream.Stream;

class DayTimeValue {

    static final Integer NONE = null;

    private Integer days;

    private Integer hours;

    private Integer minutes;

    private Integer seconds;

    DayTimeValue() {
        this(NONE, NONE, NONE, NONE);
    }

    DayTimeValue(final Integer days,
                 final Integer hours,
                 final Integer minutes,
                 final Integer seconds) {
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    Integer getDays() {
        return days;
    }

    Integer getHours() {
        return hours;
    }

    Integer getMinutes() {
        return minutes;
    }

    Integer getSeconds() {
        return seconds;
    }

    void setDays(final Integer days) {
        this.days = days;
    }

    void setHours(final Integer hours) {
        this.hours = hours;
    }

    void setMinutes(final Integer minutes) {
        this.minutes = minutes;
    }

    void setSeconds(final Integer seconds) {
        this.seconds = seconds;
    }

    boolean isEmpty() {
        return dayTimeValues().allMatch(this::isNone);
    }

    private Stream<Integer> dayTimeValues() {
        return Stream.of(days, hours, minutes, seconds);
    }

    private boolean isNone(final Integer value) {
        return Objects.equals(value, NONE);
    }
}
