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

import java.text.DateFormatSymbols;
import java.util.Locale;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(GwtMockitoTestRunner.class)
public class DateValueFormatterTest {

    private DateValueFormatter dateValueFormatter;
    private String[] localizedShortMonths;

    @Before
    public void setup() {
        dateValueFormatter = new DateValueFormatter();
        localizedShortMonths = new DateFormatSymbols(new Locale("en")).getShortMonths();
    }

    @Test
    public void testToDisplayNull() {
        testToDisplay(null, "");
    }

    @Test
    public void testToDisplayInvalidDateString() {
        testToDisplay("someInvalidDate", "");
    }

    @Test
    public void testToDisplayValidDates() {
        testToDisplay("date(\"2019-1-1\")", "01 " + localizedShortMonths[0] + " 2019");
        testToDisplay("date(\"2019-6-2\")", "02 " + localizedShortMonths[5] + " 2019");
        testToDisplay("date(\"2019-12-31\")", "31 " + localizedShortMonths[11] + " 2019");
    }

    @Test
    public void testToDisplayValidDateTwoDigitsMonthAndDay() {
        testToDisplay("date(\"2019-02-01\")", "01 " + localizedShortMonths[1] + " 2019");
    }

    @Test
    public void testToRawValidDates() {

        String input = "28 " + localizedShortMonths[1] + " 2019";
        testToRaw(input, addPrefixAndSuffix("2019-02-28"));

        input = "31 " + localizedShortMonths[11] + " 2019";
        testToRaw(input, addPrefixAndSuffix("2019-12-31"));

        input = "1 " + localizedShortMonths[0] + " 2019";
        testToRaw(input, addPrefixAndSuffix("2019-01-01"));
    }

    @Test
    public void testToRawInvalidValue() {
        testToRaw("randomString", "");
    }

    @Test
    public void testRemovePrefixAndSuffix() {

        final String date = "2019-12-25";
        final String input = addPrefixAndSuffix(date);

        final String actual = dateValueFormatter.removePrefixAndSuffix(input);

        assertEquals(date, actual);
    }

    @Test
    public void testRemovePrefixAndSuffixWithSpaces() {

        final String date = "   2019-12-25   ";
        final String expected = "2019-12-25";
        final String input = addPrefixAndSuffix(date);

        final String actual = dateValueFormatter.removePrefixAndSuffix(input);

        assertEquals(expected, actual);
    }

    @Test
    public void testAddPrefixAndSuffix() {

        final String input = "2019-12-25";
        final String expected = addPrefixAndSuffix(input);

        final String actual = dateValueFormatter.addPrefixAndSuffix(input);

        assertEquals(expected, actual);
    }

    private String addPrefixAndSuffix(final String date) {
        return DateValueFormatter.PREFIX + date + DateValueFormatter.SUFFIX;
    }

    private void testToRaw(final String input,
                           final String expected) {

        final String actual = dateValueFormatter.toRaw(input);

        assertEquals(expected, actual);
    }

    private void testToDisplay(final String input,
                               final String expected) {

        final String actual = dateValueFormatter.toDisplay(input);

        assertEquals(expected, actual);
    }
}