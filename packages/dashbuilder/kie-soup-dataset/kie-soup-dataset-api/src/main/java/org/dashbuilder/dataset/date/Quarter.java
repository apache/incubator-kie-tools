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

public enum Quarter {

    Q1,
    Q2,
    Q3,
    Q4;

    private static final Quarter[] quarters = values();

    public int getIndex() {
        return ordinal() + 1;
    }

    public static Quarter getByName(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }

    public static Quarter getByIndex(int index) {
        return quarters[index - 1];
    }

    /**
     * Given a month (from 1 to 12) it calculates the first month of its quarter.
     *
     * @param firstMonthOfYear The first month of the year considered the first month of the first quarter.
     * @param targetMonth The month we want to evaluate to which quarter belongs.
     * @return The first month of the target quarter for the given month.
     */
    public static int getQuarterFirstMonth(int firstMonthOfYear, int targetMonth) {
        int result = firstMonthOfYear;
        int index = firstMonthOfYear;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                if (index == targetMonth) return result;
                index = Month.nextIndex(index);
            }
            result = index;
        }
        throw new RuntimeException("Month not found : " + targetMonth);
    }

    /**
     * Given a month (from 1 to 12) it calculates the ordinal within the quarter it belongs.
     *
     * @param firstMonthOfYear The first month of the year considered the first month of the first quarter.
     * @param targetMonth The month we want to evaluate.
     * @return The position (from 0 to 2) of the target within the quarter.
     */
    public static int getPositionInQuarter(int firstMonthOfYear, int targetMonth) {
        int index = firstMonthOfYear;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                if (index == targetMonth) return j;
                index = Month.nextIndex(index);
            }
        }
        throw new RuntimeException("Month not found : " + targetMonth);
    }
}
