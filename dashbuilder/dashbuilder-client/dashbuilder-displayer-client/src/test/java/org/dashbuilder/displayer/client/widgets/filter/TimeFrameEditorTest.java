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
package org.dashbuilder.displayer.client.widgets.filter;

import org.dashbuilder.dataset.date.Month;
import org.dashbuilder.dataset.date.TimeFrame;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TimeFrameEditorTest {

    @Mock
    SyncBeanManager beanManager;

    @Mock
    Command changeCommand;

    @Mock
    TimeFrameEditor.View timeFrameView;

    @Mock
    TimeAmountEditor.View timeAmountView;

    @Mock
    TimeInstantEditor.View timeInstantView;

    TimeAmountEditor fromAmountEditor;
    TimeAmountEditor toAmountEditor;
    TimeInstantEditor fromInstantEditor;
    TimeInstantEditor toInstantEditor;
    TimeFrameEditor timeFrameEditor;

    public static final TimeFrame TEN_DAYS = TimeFrame.parse("begin[year March] till 10day");
    public static final TimeFrame LAST_DAY = TimeFrame.parse("now -1day till now");
    public static final TimeFrame CURRENT_YEAR = TimeFrame.parse("begin[year] till end[year]");
    public static final TimeFrame UNDEFINED = null;

    @Before
    public void init() {
        fromAmountEditor = new TimeAmountEditor(timeAmountView);
        toAmountEditor = new TimeAmountEditor(timeAmountView);
        fromInstantEditor = new TimeInstantEditor(timeInstantView, fromAmountEditor);
        toInstantEditor = new TimeInstantEditor(timeInstantView, toAmountEditor);
        timeFrameEditor = new TimeFrameEditor(timeFrameView, beanManager);

        SyncBeanDef tieBeanDef = mock(SyncBeanDef.class);
        when(beanManager.lookupBean(TimeInstantEditor.class)).thenReturn(tieBeanDef);
        when(tieBeanDef.newInstance()).thenReturn(fromInstantEditor, toInstantEditor);
    }

    @Test
    public void testViewInitialization() {
        timeFrameEditor.init(TEN_DAYS, changeCommand);

        assertEquals(timeFrameView, timeFrameEditor.view);
        verify(timeFrameView).init(timeFrameEditor);
        verify(timeFrameView).clearFirstMonthSelector();
        verify(timeFrameView, times(Month.values().length)).addFirstMonthItem(any(Month.class));
        verify(timeFrameView).setSelectedFirstMonthIndex(Month.MARCH.getIndex() - 1);
    }

    @Test
    public void testNullInitialization() {
        timeFrameEditor.init(UNDEFINED, changeCommand);

        assertEquals(timeFrameView, timeFrameEditor.view);
        verify(timeFrameView).init(timeFrameEditor);
        verify(timeFrameView).clearFirstMonthSelector();
        verify(timeFrameView, times(Month.values().length)).addFirstMonthItem(any(Month.class));
        verify(timeFrameView).setSelectedFirstMonthIndex(Month.JANUARY.getIndex() - 1);
    }

    @Test
    public void testChangeTimeAmountQuantity() {
        TimeFrame timeFrame = TimeFrame.parse("now -1year till now");
        timeFrameEditor.init(timeFrame, changeCommand);

        long qb = timeFrame.getFrom().getTimeAmount().getQuantity();
        fromAmountEditor.decreaseQuantity();
        long qa = timeFrame.getFrom().getTimeAmount().getQuantity();

        verify(changeCommand).execute();
        assertEquals(qb - 1, qa);
    }

    @Test
    public void testChangeTimeAmountType() {
        TimeFrame timeFrame = TimeFrame.parse("now -1year till now");
        timeFrameEditor.init(timeFrame, changeCommand);

        when(timeAmountView.getSelectedTypeIndex()).thenReturn(3);
        fromAmountEditor.changeIntervalType();
        DateIntervalType type = timeFrame.getFrom().getTimeAmount().getType();

        verify(changeCommand).execute();
        assertEquals(type, DateIntervalType.DAY);
    }

    @Test
    public void testChangeTimeInstant() {
        TimeFrame timeFrame = TimeFrame.parse("now -1year till now");
        timeFrameEditor.init(timeFrame, changeCommand);

        when(timeInstantView.getSelectedIntervalTypeIndex()).thenReturn(3);
        fromInstantEditor.changeIntervalType();
        DateIntervalType type = timeFrame.getFrom().getIntervalType();
        verify(changeCommand).execute();
        assertEquals(type, DateIntervalType.MONTH);
    }

    @Test
    public void testFirstMonthAvailable() {
        timeFrameEditor.init(CURRENT_YEAR, changeCommand);
        assertEquals(timeFrameEditor.isFirstMonthAvailable(), true);
    }

    @Test
    public void testFirstMonthUnavailable() {
        timeFrameEditor.init(LAST_DAY, changeCommand);
        assertEquals(timeFrameEditor.isFirstMonthAvailable(), false);
    }
}
