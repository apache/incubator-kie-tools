/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataset.date;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MonthDayQuarterEnumTest {

    @Test
    public void monthIndex() {
        int expectedIndex = 1;
        for (Month month : Month.getAll()) {
            assertEquals(expectedIndex++, month.getIndex());
            assertEquals(month, Month.getByIndex(month.getIndex()));
        }
    }

    @Test
    public void nextMonthIndex() {
        assertEquals(2, Month.nextIndex(1));
        assertEquals(3, Month.nextIndex(2));
        assertEquals(4, Month.nextIndex(3));
        assertEquals(5, Month.nextIndex(4));
        assertEquals(6, Month.nextIndex(5));
        assertEquals(7, Month.nextIndex(6));
        assertEquals(8, Month.nextIndex(7));
        assertEquals(9, Month.nextIndex(8));
        assertEquals(10, Month.nextIndex(9));
        assertEquals(11, Month.nextIndex(10));
        assertEquals(12, Month.nextIndex(11));
        assertEquals(1, Month.nextIndex(12));
    }

    @Test
    public void dayIndex() {
        assertEquals(1, DayOfWeek.SUNDAY.getIndex());
        assertEquals(2, DayOfWeek.MONDAY.getIndex());
        assertEquals(3, DayOfWeek.TUESDAY.getIndex());
        assertEquals(4, DayOfWeek.WEDNESDAY.getIndex());
        assertEquals(5, DayOfWeek.THURSDAY.getIndex());
        assertEquals(6, DayOfWeek.FRIDAY.getIndex());
        assertEquals(7, DayOfWeek.SATURDAY.getIndex());
    }

    @Test
    public void dayByIndex() {
        assertEquals(DayOfWeek.SUNDAY, DayOfWeek.getByIndex(1));
        assertEquals(DayOfWeek.MONDAY, DayOfWeek.getByIndex(2));
        assertEquals(DayOfWeek.TUESDAY, DayOfWeek.getByIndex(3));
        assertEquals(DayOfWeek.WEDNESDAY, DayOfWeek.getByIndex(4));
        assertEquals(DayOfWeek.THURSDAY, DayOfWeek.getByIndex(5));
        assertEquals(DayOfWeek.FRIDAY, DayOfWeek.getByIndex(6));
        assertEquals(DayOfWeek.SATURDAY, DayOfWeek.getByIndex(7));
    }

    @Test
    public void nextDayIndex() {
        assertEquals(1, DayOfWeek.nextIndex(7));
        assertEquals(2, DayOfWeek.nextIndex(1));
        assertEquals(3, DayOfWeek.nextIndex(2));
        assertEquals(4, DayOfWeek.nextIndex(3));
        assertEquals(5, DayOfWeek.nextIndex(4));
        assertEquals(6, DayOfWeek.nextIndex(5));
        assertEquals(7, DayOfWeek.nextIndex(6));
    }

    @Test
    public void quarterIndex() {
        assertEquals(1, Quarter.Q1.getIndex());
        assertEquals(2, Quarter.Q2.getIndex());
        assertEquals(3, Quarter.Q3.getIndex());
        assertEquals(4, Quarter.Q4.getIndex());
    }
}
