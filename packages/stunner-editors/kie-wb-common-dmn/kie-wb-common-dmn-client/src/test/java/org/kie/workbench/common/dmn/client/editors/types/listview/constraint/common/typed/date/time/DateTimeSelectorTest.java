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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DateTimeSelectorTest {

    @Mock
    private DateTimeSelector.View view;

    @Mock
    private DateTimeValueConverter converter;

    @Mock
    private DateTimeValue dateTime;

    private DateTimeSelector selector;

    @Before
    public void setup() {

        selector = new DateTimeSelector(view, converter);
        when(view.getValue()).thenReturn(dateTime);
    }

    @Test
    public void testSetValue() {

        final String dmnString = "dmnString";
        when(converter.fromDMNString(dmnString)).thenReturn(dateTime);
        selector.setValue(dmnString);

        verify(view).setValue(dateTime);
    }

    @Test
    public void testToDisplay() {

        final String raw = "raw";
        final String display = "display";

        when(converter.toDisplay(raw)).thenReturn(display);

        final String actual = selector.toDisplay(raw);

        assertEquals(display, actual);
    }
}