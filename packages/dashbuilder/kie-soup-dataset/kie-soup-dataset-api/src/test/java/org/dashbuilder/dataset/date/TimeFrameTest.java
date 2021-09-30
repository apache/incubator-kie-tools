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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TimeFrameTest {

    public static final SimpleDateFormat _dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    protected String formatDate(Date d) {
        return _dateTimeFormat.format(d);
    }

    @Before
    public void setUp() throws Exception {
        Date startTime = new Date();
        startTime.setYear(115);
        startTime.setMonth(10);
        startTime.setDate(10);
        startTime.setHours(12);
        startTime.setMinutes(50);
        startTime.setSeconds(30);
        TimeInstant.START_TIME = startTime;

    }

    @Test
    public void testCurrentQuarter() throws Exception {
        TimeFrame timeFrame = TimeFrame.parse("begin[quarter] till end[quarter]");
        Date start = timeFrame.getFrom().getTimeInstant();
        Date end = timeFrame.getTo().getTimeInstant();
        assertEquals(formatDate(start), "2015-10-01 00:00:00");
        assertEquals(formatDate(end), "2016-01-01 00:00:00");
    }

    @Test
    public void testCurrentQuarter2() throws Exception {
        TimeFrame timeFrame = TimeFrame.parse("begin[quarter march] till end[quarter march]");
        Date start = timeFrame.getFrom().getTimeInstant();
        Date end = timeFrame.getTo().getTimeInstant();
        assertEquals(formatDate(start), "2015-09-01 00:00:00");
        assertEquals(formatDate(end), "2015-12-01 00:00:00");
    }

    @Test
    public void testPastSeconds() throws Exception {
        TimeFrame timeFrame = TimeFrame.parse("-10second");
        Date start = timeFrame.getFrom().getTimeInstant();
        Date end = timeFrame.getTo().getTimeInstant();
        assertEquals(formatDate(start), "2015-11-10 12:50:20");
        assertEquals(formatDate(end), "2015-11-10 12:50:30");
    }

    @Test
    public void testPastSecondsLastMinute() throws Exception {
        TimeFrame timeFrame = TimeFrame.parse("begin[minute] -10second");
        Date start = timeFrame.getFrom().getTimeInstant();
        Date end = timeFrame.getTo().getTimeInstant();
        assertEquals(formatDate(start), "2015-11-10 12:49:50");
        assertEquals(formatDate(end), "2015-11-10 12:50:30");
    }

    @Test
    public void testFutureSeconds() throws Exception {
        TimeFrame timeFrame = TimeFrame.parse("100second");
        Date start = timeFrame.getFrom().getTimeInstant();
        Date end = timeFrame.getTo().getTimeInstant();
        assertEquals(formatDate(start), "2015-11-10 12:50:30");
        assertEquals(formatDate(end), "2015-11-10 12:52:10");
    }

    @Test
    public void testFirst10Seconds() throws Exception {
        TimeFrame timeFrame = TimeFrame.parse("begin[minute] till 10second");
        Date start = timeFrame.getFrom().getTimeInstant();
        Date end = timeFrame.getTo().getTimeInstant();
        assertEquals(formatDate(start), "2015-11-10 12:50:00");
        assertEquals(formatDate(end), "2015-11-10 12:50:10");
    }

    @Test
    public void testFirstSeconds() throws Exception {
        TimeFrame timeFrame = TimeFrame.parse("begin[minute] till now");
        Date start = timeFrame.getFrom().getTimeInstant();
        Date end = timeFrame.getTo().getTimeInstant();
        assertEquals(formatDate(start), "2015-11-10 12:50:00");
        assertEquals(formatDate(end), "2015-11-10 12:50:30");
    }

    @Test
    public void testCurrentMinute() throws Exception {
        TimeFrame timeFrame = TimeFrame.parse("begin[minute] till 60second");
        Date start = timeFrame.getFrom().getTimeInstant();
        Date end = timeFrame.getTo().getTimeInstant();
        assertEquals(formatDate(start), "2015-11-10 12:50:00");
        assertEquals(formatDate(end), "2015-11-10 12:51:00");
    }

    @Test
    public void testPastMonths() throws Exception {
        TimeFrame timeFrame = TimeFrame.parse("-24month");
        Date start = timeFrame.getFrom().getTimeInstant();
        Date end = timeFrame.getTo().getTimeInstant();
        assertEquals(formatDate(start), "2013-11-10 12:50:30");
        assertEquals(formatDate(end), "2015-11-10 12:50:30");
    }

    @Test
    public void testFutureMonths() throws Exception {
        TimeFrame timeFrame = TimeFrame.parse("60month");
        Date start = timeFrame.getFrom().getTimeInstant();
        Date end = timeFrame.getTo().getTimeInstant();
        assertEquals(formatDate(start), "2015-11-10 12:50:30");
        assertEquals(formatDate(end), "2020-11-10 12:50:30");
    }

    @Test
    public void testPastYears() throws Exception {
        TimeFrame timeFrame = TimeFrame.parse("-2year");
        Date start = timeFrame.getFrom().getTimeInstant();
        Date end = timeFrame.getTo().getTimeInstant();
        assertEquals(formatDate(start), "2013-11-10 12:50:30");
        assertEquals(formatDate(end), "2015-11-10 12:50:30");
    }

    @Test
    public void testFutureYears() throws Exception {
        TimeFrame timeFrame = TimeFrame.parse("100year");
        Date start = timeFrame.getFrom().getTimeInstant();
        Date end = timeFrame.getTo().getTimeInstant();
        assertEquals(formatDate(start), "2015-11-10 12:50:30");
        assertEquals(formatDate(end), "2115-11-10 12:50:30");
    }

    @Test
    public void testOneYearSinceNow() throws Exception {
        TimeFrame timeFrame = TimeFrame.parse("now till 1year");
        Date start = timeFrame.getFrom().getTimeInstant();
        Date end = timeFrame.getTo().getTimeInstant();
        assertEquals(formatDate(start), "2015-11-10 12:50:30");
        assertEquals(formatDate(end), "2016-11-10 12:50:30");
    }

    @Test
    public void testTillEndOfYear() throws Exception {
        TimeFrame timeFrame = TimeFrame.parse("now till end[year january]");
        Date start = timeFrame.getFrom().getTimeInstant();
        Date end = timeFrame.getTo().getTimeInstant();
        assertEquals(formatDate(start), "2015-11-10 12:50:30");
        assertEquals(formatDate(end), "2016-01-01 00:00:00");
    }

    @Test
    public void testThisYearPlusNext() throws Exception {
        TimeFrame timeFrame = TimeFrame.parse("now till end[year March] 1year");
        Date start = timeFrame.getFrom().getTimeInstant();
        Date end = timeFrame.getTo().getTimeInstant();
        assertEquals(formatDate(start), "2015-11-10 12:50:30");
        assertEquals(formatDate(end), "2017-03-01 00:00:00");
    }

    @Test
    public void testNextYear() throws Exception {
        TimeFrame timeFrame = TimeFrame.parse("end[year March] till 1year");
        Date start = timeFrame.getFrom().getTimeInstant();
        Date end = timeFrame.getTo().getTimeInstant();
        assertEquals(formatDate(start), "2016-03-01 00:00:00");
        assertEquals(formatDate(end), "2017-03-01 00:00:00");
    }

    @Test
    public void testSinceBeginningLastYear() throws Exception {
        TimeFrame timeFrame = TimeFrame.parse("begin[year March] -1year till now");
        Date start = timeFrame.getFrom().getTimeInstant();
        Date end = timeFrame.getTo().getTimeInstant();
        assertEquals(formatDate(start), "2014-03-01 00:00:00");
        assertEquals(formatDate(end), "2015-11-10 12:50:30");
    }

    @Test
    public void testLastWeekOfLastYear() throws Exception {
        TimeFrame timeFrame = TimeFrame.parse("begin[year March] -7day till begin[year March]");
        Date start = timeFrame.getFrom().getTimeInstant();
        Date end = timeFrame.getTo().getTimeInstant();
        assertEquals(formatDate(start), "2015-02-22 00:00:00");
        assertEquals(formatDate(end), "2015-03-01 00:00:00");
    }


    @Test
    public void testThirdQuarterOfNextYear() throws Exception {
        TimeFrame timeFrame = TimeFrame.parse("end[year March] +2quarter till 1quarter");
        Date start = timeFrame.getFrom().getTimeInstant();
        Date end = timeFrame.getTo().getTimeInstant();
        assertEquals(formatDate(start), "2016-09-01 00:00:00");
        assertEquals(formatDate(end), "2016-12-01 00:00:00");
    }
}
