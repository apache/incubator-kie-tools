/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.ext.uberfire.social.activities.client;

import java.util.Date;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ext.uberfire.social.activities.client.widgets.utils.SocialDateFormatter;

import static org.junit.Assert.*;

@RunWith(GwtMockitoTestRunner.class)
public class SocialDateFormatterTest {

    private Date createDateFromNow( int daysToSubtract ) {
        Date today = new Date();
        int miliSecondsToSubtract =  daysToSubtract * 24 * 60 * 60 * 1000;
        Date targetDate = new Date( today.getTime() - miliSecondsToSubtract );
        return targetDate;
    }

    @Test
    public void printsToday() throws Exception {
        assertEquals("Today", SocialDateFormatter.format(new Date()));
    }

    @Test
    public void printsOneDayAgo() throws Exception {
        assertEquals("1 DayAgo", SocialDateFormatter.format(createDateFromNow(1)));
    }

    @Test
    public void printsTwoDayAgo() throws Exception {
        assertEquals("2 DaysAgo", SocialDateFormatter.format(createDateFromNow(2)));
    }

    @Test
    public void printsOneWeekAgo() throws Exception {
        assertEquals("OneWeekAgo", SocialDateFormatter.format(createDateFromNow(7)));
    }

    @Test
    public void printsOneWeekAgo2() throws Exception {
        assertEquals("OneWeekAgo", SocialDateFormatter.format(createDateFromNow(8)));
    }

    @Test
    public void printsTwoWeekAgo1() throws Exception {
        assertEquals("2 WeeksAgo", SocialDateFormatter.format(createDateFromNow(14)));
    }

    @Test
    public void printsTwoWeekAgo() throws Exception {
        assertEquals("2 WeeksAgo", SocialDateFormatter.format(createDateFromNow(15)));
    }
}
