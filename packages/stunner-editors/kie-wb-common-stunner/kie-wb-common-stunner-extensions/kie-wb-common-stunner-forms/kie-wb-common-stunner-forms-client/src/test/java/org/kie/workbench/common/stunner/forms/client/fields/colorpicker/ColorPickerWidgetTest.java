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


package org.kie.workbench.common.stunner.forms.client.fields.colorpicker;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.TextBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ColorPickerWidgetTest {

    @Mock
    private TextBox colorTextBoxMock;

    private ColorPickerWidget colorPicker;

    @Before
    public void setUp() throws Exception {
        colorPicker = spy(new ColorPickerWidget());

        doReturn(colorTextBoxMock).when(colorPicker).getColorTextBox();
    }

    @Test
    public void testReadOnly() {
        colorPicker.setReadOnly(true);

        verify(colorTextBoxMock).setReadOnly(true);

        reset(colorTextBoxMock);

        colorPicker.setReadOnly(false);

        verify(colorTextBoxMock).setReadOnly(false);
    }

    @Test
    public void testSetValue() {
        final String colorHexValue = "#123456";
        colorPicker.setValue(colorHexValue);

        verify(colorTextBoxMock).setText(colorHexValue);
    }

    @Test
    public void testNewValueValid() {
        final String newColorHexValue = "#123456";
        when(colorTextBoxMock.getValue()).thenReturn(newColorHexValue);

        colorPicker.onColorTextBoxChange(mock(ChangeEvent.class));

        verify(colorPicker).setValue(newColorHexValue, true);
    }

    @Test
    public void testNewValueInValid() {
        colorPicker.setValue("#000000");

        final String newColorHexValueInvalid = "#12XX56";
        when(colorTextBoxMock.getValue()).thenReturn(newColorHexValueInvalid);

        colorPicker.onColorTextBoxChange(mock(ChangeEvent.class));

        verify(colorPicker, times(2)).setValue("#000000", false);
    }
}
