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

public enum DayOfWeek {

    SUNDAY,
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY;

    private final static DayOfWeek[] days = values();

    /**
     * @return index from 1 (SUNDAY) to 7 (SATURDAY)
     */
    public int getIndex() {
        return ordinal() + 1;
    }

    public static int nextIndex(int index) {
        return ++index <= 7 ? index : 1;
    }

    public static DayOfWeek[] getAll() {
        return days;
    }

    public static DayOfWeek getByName(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param index index of day (1 = SUNDAY,... 7 = SATURDAY)
     * @return day with given index
     */
    public static DayOfWeek getByIndex(int index) {
        return days[index - 1];
    }
}
