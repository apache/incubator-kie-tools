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

import org.dashbuilder.dataset.date.TimeAmount;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TimeAmountEditorTest {

    @Mock
    TimeAmountEditor.View timeAmountView;

    @Mock
    Command changeCommand;

    @Test
    public void testViewInitialization() {
        TimeAmount timeAmount = new TimeAmount(10, DateIntervalType.DAY);
        TimeAmountEditor editor = new TimeAmountEditor(timeAmountView);
        editor.init(timeAmount, changeCommand);

        verify(timeAmountView).clearIntervalTypeSelector();
        verify(timeAmountView, times(TimeAmountEditor.INTERVAL_TYPES.size())).addIntervalTypeItem(any(DateIntervalType.class));
        verify(timeAmountView).setSelectedTypeIndex(3);
        verify(timeAmountView).setQuantity(10);
    }

    @Test
    public void testNullInitialization() {
        TimeAmountEditor editor = new TimeAmountEditor(timeAmountView);
        editor.init(null, changeCommand);

        verify(timeAmountView).clearIntervalTypeSelector();
        verify(timeAmountView, times(TimeAmountEditor.INTERVAL_TYPES.size())).addIntervalTypeItem(any(DateIntervalType.class));

        // "0year" set by default
        verify(timeAmountView).setSelectedTypeIndex(7);
        verify(timeAmountView).setQuantity(0);
    }

    @Test
    public void testDecreaseQuantity() {
        TimeAmount timeAmount = new TimeAmount(10, DateIntervalType.DAY);
        TimeAmountEditor timeAmountEditor = new TimeAmountEditor(timeAmountView);
        timeAmountEditor.init(timeAmount, changeCommand);
        timeAmountEditor.decreaseQuantity();

        verify(timeAmountView).setQuantity(9);
        verify(changeCommand).execute();

        assertEquals(timeAmount.getQuantity(), 9);
    }

    @Test
    public void testIncreaseQuantity() {
        TimeAmount timeAmount = new TimeAmount(10, DateIntervalType.DAY);
        TimeAmountEditor timeAmountEditor = new TimeAmountEditor(timeAmountView);
        timeAmountEditor.init(timeAmount, changeCommand);
        timeAmountEditor.increaseQuantity();

        verify(timeAmountView).setQuantity(11);
        verify(changeCommand).execute();

        assertEquals(timeAmount.getQuantity(), 11);
    }
}