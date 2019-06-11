/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.library.client.util;

import java.util.Date;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DateUtilsTest {

    private DateUtils dateUtils;

    @Mock
    private TranslationService translationService;

    @Before
    public void setUp() {
        dateUtils = new DateUtils(translationService);
    }

    private Date createDateFromNow(int daysToSubtract) {
        Date today = new Date();
        int msToSubtract = daysToSubtract * DateUtils.ONE_DAY_IN_MS;
        Date targetDate = new Date(today.getTime() - msToSubtract);
        return targetDate;
    }

    @Test
    public void testFormatToday() {
        String expected = "today";

        when(translationService.getTranslation(LibraryConstants.Today)).thenReturn(expected);

        String actual = dateUtils.format(new Date());

        assertEquals(expected, actual);
    }

    @Test
    public void testFormatOneDayAgo() {
        String expected = "1 day ago";
        int nDays = 1;

        when(translationService.getTranslation(LibraryConstants.OneDayAgo)).thenReturn(expected);

        String actual = dateUtils.format(createDateFromNow(nDays));

        assertEquals(expected, actual);
    }

    @Test
    public void testFormatTwoDaysAgo() {
        String expected = "2 days ago";
        int nDays = 2;

        when(translationService.format(LibraryConstants.DaysAgo, nDays)).thenReturn(expected);

        String actual = dateUtils.format(createDateFromNow(nDays));

        assertEquals(expected, actual);
    }

    @Test
    public void testFormatOneWeekAgoWithSevenDays() {
        String expected = "1 week ago";
        int nDays = 7;

        when(translationService.getTranslation(LibraryConstants.OneWeekAgo)).thenReturn(expected);

        String actual = dateUtils.format(createDateFromNow(nDays));

        assertEquals(expected, actual);
    }

    @Test
    public void testFormatOneWeekAgoWithEightDays() {
        String expected = "1 week ago";
        int nDays = 8;

        when(translationService.getTranslation(LibraryConstants.OneWeekAgo)).thenReturn(expected);

        String actual = dateUtils.format(createDateFromNow(nDays));

        assertEquals(expected, actual);
    }

    @Test
    public void testFormatTwoWeeksAgoWithFourteenDays() {
        String expected = "2 weeks ago";
        int nDays = 14;
        int nWeeks = 2;

        when(translationService.format(LibraryConstants.WeeksAgo, nWeeks)).thenReturn(expected);

        String actual = dateUtils.format(createDateFromNow(nDays));

        assertEquals(expected, actual);
    }

    @Test
    public void testFormatTwoWeeksAgoWithFifteenDays() {
        String expected = "2 weeks ago";
        int nDays = 15;
        int nWeeks = 2;

        when(translationService.format(LibraryConstants.WeeksAgo, nWeeks)).thenReturn(expected);

        String actual = dateUtils.format(createDateFromNow(nDays));

        assertEquals(expected, actual);
    }
}