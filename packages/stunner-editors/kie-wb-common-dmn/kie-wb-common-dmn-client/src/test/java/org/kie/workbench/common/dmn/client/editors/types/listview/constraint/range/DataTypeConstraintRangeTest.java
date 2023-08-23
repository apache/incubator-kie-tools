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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.editors.types.RangeValue;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModal;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.ConstraintPlaceholderHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.DataTypeConstraintParserWarningEvent;
import org.kie.workbench.common.dmn.client.service.DMNClientServicesProxy;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeConstraintRangeTest {

    @Mock
    private DataTypeConstraintRange.View view;

    @Mock
    private ConstraintPlaceholderHelper placeholderHelper;

    @Mock
    private DMNClientServicesProxy clientServicesProxy;

    @Mock
    private EventSourceMock<DataTypeConstraintParserWarningEvent> parserWarningEvent;

    @Mock
    private DataTypeConstraintModal modal;

    private DataTypeConstraintRange constraintRange;

    @Before
    public void setup() {
        constraintRange = spy(new DataTypeConstraintRange(view,
                                                          placeholderHelper,
                                                          clientServicesProxy,
                                                          parserWarningEvent));
    }

    @Test
    public void testSetup() {
        constraintRange.setup();

        verify(view).init(constraintRange);
    }

    @Test
    public void testDisableOkButton() {
        constraintRange.setModal(modal);
        constraintRange.disableOkButton();
        verify(modal).disableOkButton();
    }

    @Test
    public void testEnableOkButton() {
        constraintRange.setModal(modal);
        constraintRange.enableOkButton();
        verify(modal).enableOkButton();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetValue() {
        final String value = "value";

        constraintRange.setValue(value);

        verify(clientServicesProxy).parseRangeValue(eq(value),
                                                    any(ServiceCallback.class));
    }

    @Test
    public void testLoadConstraintValue() {
        constraintRange.setModal(modal);

        final RangeValue rangeValue = new RangeValue();
        rangeValue.setIncludeStartValue(true);
        rangeValue.setIncludeEndValue(true);
        rangeValue.setStartValue("0");
        rangeValue.setEndValue("1");

        constraintRange.loadConstraintValue(rangeValue);
        verify(view).setIncludeStartValue(true);
        verify(view).setIncludeEndValue(true);
        verify(view).setStartValue("0");
        verify(view).setEndValue("1");

        verify(constraintRange).enableOkButton();
    }

    @Test
    public void testLoadEmptyConstraintValue() {
        constraintRange.setModal(modal);

        final RangeValue rangeValue = new RangeValue();

        constraintRange.loadConstraintValue(rangeValue);
        verify(view).setIncludeStartValue(true);
        verify(view).setIncludeEndValue(true);
        verify(view).setStartValue("");
        verify(view).setEndValue("");

        verify(constraintRange).disableOkButton();
        verify(constraintRange, never()).enableOkButton();
    }

    @Test
    public void testGetValueExcludeBoth() {
        when(view.getIncludeStartValue()).thenReturn(false);
        when(view.getStartValue()).thenReturn("1");
        when(view.getEndValue()).thenReturn("6");
        when(view.getIncludeEndValue()).thenReturn(false);

        final String expected = "(1..6)";
        final String actual = constraintRange.getValue();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetValueIncludeBoth() {
        when(view.getIncludeStartValue()).thenReturn(true);
        when(view.getStartValue()).thenReturn("1");
        when(view.getEndValue()).thenReturn("6");
        when(view.getIncludeEndValue()).thenReturn(true);

        final String expected = "[1..6]";
        final String actual = constraintRange.getValue();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetValueIncludeStartExcludeEnd() {
        when(view.getIncludeStartValue()).thenReturn(true);
        when(view.getStartValue()).thenReturn("1");
        when(view.getEndValue()).thenReturn("6");
        when(view.getIncludeEndValue()).thenReturn(false);

        final String expected = "[1..6)";
        final String actual = constraintRange.getValue();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetValueExcludeStartIncludeEnd() {
        when(view.getIncludeStartValue()).thenReturn(false);
        when(view.getStartValue()).thenReturn("1");
        when(view.getEndValue()).thenReturn("6");
        when(view.getIncludeEndValue()).thenReturn(true);

        final String expected = "(1..6]";
        final String actual = constraintRange.getValue();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetValue() {
        when(view.getIncludeStartValue()).thenReturn(true);
        when(view.getStartValue()).thenReturn("some_value");
        when(view.getEndValue()).thenReturn("other_value");
        when(view.getIncludeEndValue()).thenReturn(true);

        final String expected = "[some_value..other_value]";
        final String actual = constraintRange.getValue();
        assertEquals(expected, actual);
    }

    @Test
    public void testSetConstraintValueType() {

        final String type = "string";
        final String placeholder = "placeholder";

        when(placeholderHelper.getPlaceholderSample(type)).thenReturn(placeholder);

        constraintRange.setConstraintValueType(type);

        verify(view).setPlaceholders(placeholder);
        verify(view).setComponentSelector(type);
    }
}
