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

import org.dashbuilder.dataset.group.DateIntervalType;
import org.dashbuilder.dataset.group.GroupStrategy;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DateUtilsTest {

    @Test
    public void testMonthFormat() throws Exception {
        String result = DateUtils.ensureTwoDigits("2015-M1", DateIntervalType.MONTH);
        assertThat(result).isEqualTo("2015-01");
        result = DateUtils.ensureTwoDigits("2015-M12", DateIntervalType.MONTH);
        assertThat(result).isEqualTo("2015-12");
    }

    @Test
    public void testDayFormat() throws Exception {
        String result = DateUtils.ensureTwoDigits("2015-M1-D23", DateIntervalType.DAY);
        assertThat(result).isEqualTo("2015-01-23");
        result = DateUtils.ensureTwoDigits("2015-M11-D01", DateIntervalType.DAY);
        assertThat(result).isEqualTo("2015-11-01");
    }

    @Test
    public void testHourFormat() throws Exception {
        String result = DateUtils.ensureTwoDigits("2015-M1-D23 H3", DateIntervalType.HOUR);
        assertThat(result).isEqualTo("2015-01-23 03");
        result = DateUtils.ensureTwoDigits("2015-M11-D01 H23", DateIntervalType.HOUR);
        assertThat(result).isEqualTo("2015-11-01 23");
    }

    @Test
    public void testMinuteFormat() throws Exception {
        String result = DateUtils.ensureTwoDigits("2015-M1-D23 H3:m0", DateIntervalType.MINUTE);
        assertThat(result).isEqualTo("2015-01-23 03:00");
        result = DateUtils.ensureTwoDigits("2015-M11-D01 H23:m59", DateIntervalType.MINUTE);
        assertThat(result).isEqualTo("2015-11-01 23:59");
    }

    @Test
    public void testSecondsFormat() throws Exception {
        String result = DateUtils.ensureTwoDigits("2015-M1-D23 H3:m0:s0", DateIntervalType.SECOND);
        assertThat(result).isEqualTo("2015-01-23 03:00:00");
        result = DateUtils.ensureTwoDigits("2015-M11-D01 H23:m59:s59", DateIntervalType.SECOND);
        assertThat(result).isEqualTo("2015-11-01 23:59:59");
    }

    public static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    public void testParseSecond() throws Exception {
        Date result = DateUtils.parseDate(DateIntervalType.SECOND, GroupStrategy.DYNAMIC, "2015-1-23 3:0:1");
        assertThat(df.format(result)).isEqualTo("2015-01-23 03:00:01");
    }

    @Test
    public void testParseMinute() throws Exception {
        Date result = DateUtils.parseDate(DateIntervalType.MINUTE, GroupStrategy.DYNAMIC, "2015-1-23 3:1");
        assertThat(df.format(result)).isEqualTo("2015-01-23 03:01:00");
    }

    @Test
    public void testParseHour() throws Exception {
        Date result = DateUtils.parseDate(DateIntervalType.HOUR, GroupStrategy.DYNAMIC, "2015-1-23 3");
        assertThat(df.format(result)).isEqualTo("2015-01-23 03:00:00");
    }

    @Test
    public void testParseDay() throws Exception {
        Date result = DateUtils.parseDate(DateIntervalType.DAY, GroupStrategy.DYNAMIC, "2015-1-23");
        assertThat(df.format(result)).isEqualTo("2015-01-23 00:00:00");
    }

    @Test
    public void testParseMonth() throws Exception {
        Date result = DateUtils.parseDate(DateIntervalType.MONTH, GroupStrategy.DYNAMIC, "2015-1");
        assertThat(df.format(result)).isEqualTo("2015-01-01 00:00:00");
    }
}
