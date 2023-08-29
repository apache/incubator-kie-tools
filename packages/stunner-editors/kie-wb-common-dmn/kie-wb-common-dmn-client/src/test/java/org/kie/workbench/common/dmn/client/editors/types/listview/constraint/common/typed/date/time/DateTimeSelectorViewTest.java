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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.time;

import java.util.function.Consumer;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.ConstraintPlaceholderHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.DateSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelector;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DateTimeSelectorViewTest {

    @Mock
    private HTMLDivElement dateSelectorContainer;

    @Mock
    private HTMLDivElement timeSelectorContainer;

    @Mock
    private DateSelector dateSelector;

    @Mock
    private TimeSelector timeSelector;

    @Mock
    private ConstraintPlaceholderHelper placeholderHelper;

    private DateTimeSelectorView view;

    @Before
    public void setup() {

        view = spy(new DateTimeSelectorView(dateSelectorContainer,
                                            timeSelectorContainer,
                                            dateSelector,
                                            timeSelector,
                                            placeholderHelper));
    }

    @Test
    public void testInit() {

        final Element dateSelectorElement = mock(Element.class);
        final Element timeSelectorElement = mock(Element.class);
        final String datePlaceHolder = "date placeholder";
        final String timePlaceHolder = "time placeholder";

        when(dateSelector.getElement()).thenReturn(dateSelectorElement);
        when(timeSelector.getElement()).thenReturn(timeSelectorElement);

        when(placeholderHelper.getPlaceholderSample(BuiltInType.DATE.toString())).thenReturn(datePlaceHolder);
        when(placeholderHelper.getPlaceholderSample(BuiltInType.TIME.toString())).thenReturn(timePlaceHolder);

        view.init();

        verify(dateSelectorContainer).appendChild(dateSelectorElement);
        verify(timeSelectorContainer).appendChild(timeSelectorElement);

        verify(dateSelector).setPlaceholder(datePlaceHolder);
        verify(timeSelector).setPlaceholder(timePlaceHolder);
    }

    @Test
    public void testOnBlurCallback() {

        final BlurEvent blurEvent = mock(BlurEvent.class);
        final Consumer eventConsumer = mock(Consumer.class);
        final Object target = mock(Object.class);

        doReturn(eventConsumer).when(view).getOnValueInputBlur();
        doReturn(target).when(view).getEventTarget(blurEvent);

        when(dateSelector.isChild(target)).thenReturn(false);
        when(timeSelector.isChild(target)).thenReturn(false);

        view.onBlurCallback(blurEvent);

        verify(eventConsumer).accept(blurEvent);
    }

    @Test
    public void testOnBlurCallbackWhenTargetIsChildrenOfDate() {

        final BlurEvent blurEvent = mock(BlurEvent.class);
        final Consumer eventConsumer = mock(Consumer.class);
        final Object target = mock(Object.class);

        doReturn(eventConsumer).when(view).getOnValueInputBlur();
        doReturn(target).when(view).getEventTarget(blurEvent);

        when(dateSelector.isChild(target)).thenReturn(true);
        when(timeSelector.isChild(target)).thenReturn(false);

        view.onBlurCallback(blurEvent);

        verify(eventConsumer, never()).accept(blurEvent);
    }

    @Test
    public void testOnBlurCallbackWhenTargetIsChildrenOfTime() {

        final BlurEvent blurEvent = mock(BlurEvent.class);
        final Consumer eventConsumer = mock(Consumer.class);
        final Object target = mock(Object.class);

        doReturn(eventConsumer).when(view).getOnValueInputBlur();
        doReturn(target).when(view).getEventTarget(blurEvent);

        when(dateSelector.isChild(target)).thenReturn(false);
        when(timeSelector.isChild(target)).thenReturn(true);

        view.onBlurCallback(blurEvent);

        verify(eventConsumer, never()).accept(blurEvent);
    }

    @Test
    public void testOnBlurCallbackWhenTargetIsNull() {

        final BlurEvent blurEvent = mock(BlurEvent.class);
        final Consumer eventConsumer = mock(Consumer.class);

        doReturn(eventConsumer).when(view).getOnValueInputBlur();
        doReturn(null).when(view).getEventTarget(blurEvent);

        view.onBlurCallback(blurEvent);

        verify(eventConsumer, never()).accept(any());
    }

    @Test
    public void testGetValue() {

        final String dateValue = "dateValue";
        final String timeValue = "timeValue";

        when(dateSelector.getValue()).thenReturn(dateValue);
        when(timeSelector.getValue()).thenReturn(timeValue);

        final DateTimeValue actual = view.getValue();

        assertEquals(dateValue, actual.getDate());
        assertEquals(timeValue, actual.getTime());
    }

    @Test
    public void testSetValue() {

        final String dateValue = "dateValue";
        final String timeValue = "timeValue";

        final DateTimeValue dateTimeValue = new DateTimeValue();
        dateTimeValue.setDate(dateValue);
        dateTimeValue.setTime(timeValue);

        view.setValue(dateTimeValue);

        verify(dateSelector).setValue(dateValue);
        verify(timeSelector).setValue(timeValue);
    }

    @Test
    public void setSetOnValueChanged() {

        final Consumer consumer = mock(Consumer.class);

        view.setOnValueChanged(consumer);

        verify(dateSelector).setOnInputChangeCallback(consumer);
        verify(timeSelector).setOnInputChangeCallback(consumer);
    }
}