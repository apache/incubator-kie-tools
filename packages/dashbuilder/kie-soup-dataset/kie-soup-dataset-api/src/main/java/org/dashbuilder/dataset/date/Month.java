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

public enum Month {

    JANUARY,
    FEBRUARY,
    MARCH,
    APRIL,
    MAY,
    JUNE,
    JULY,
    AUGUST,
    SEPTEMBER,
    OCTOBER,
    NOVEMBER,
    DECEMBER;

    private static final Month[] months = values();

    /**
     * @return index of month from 1 (JANUARY) to 12 (DECEMBER)
     */
    public int getIndex() {
        return ordinal() + 1;
    }

    public static int nextIndex(int index) {
        return ++index <= 12 ? index : 1;
    }

    public static Month[] getAll() {
        return months;
    }

    public static Month getByName(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param index index of month (1= JANUARY, ... 12 = DECEMBER)
     * @return month with given index
     */
    public static Month getByIndex(int index) {
        return months[index - 1];
    }
}
