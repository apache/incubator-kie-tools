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


package org.kie.workbench.common.stunner.bpmn.client.forms.widgets;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.core.client.GWT;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class TimeZonePickerTest {

    @Mock
    private Select tzSelect;

    private TimeZonePickerViewImpl view;

    private TimeZonePicker tested;

    @Before
    public void setUp() {
        view = GWT.create(TimeZonePickerViewImpl.class);
        doCallRealMethod().when(view).setValue(anyString());
        doCallRealMethod().when(view).populateTzSelector();
        doCallRealMethod().when(tzSelect).setValue(anyString());
        doCallRealMethod().when(tzSelect).setValue(anyString(), anyBoolean());

        view.tzSelect = tzSelect;

        tested = spy(new TimeZonePicker(view));
    }

    @Test
    public void testSetValue() {
        tested.setValue("-04:00");
        verify(view,
               times(1)).setValue("-04:00");
        verify(view.tzSelect,
               times(1)).setValue("-04:00");
    }

    @Test
    public void testSetValueToUserTimeZone() {
        tested.setValue("0");
        verify(view,
               times(1)).setValue("0");
        verify(view.tzSelect,
               times(1)).setValue(view.userTimeZone);
    }
}