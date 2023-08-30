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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.artifacts;

import javax.enterprise.event.Event;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.DataTypeNamesService;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.CustomDataTypeTextBox;
import org.kie.workbench.common.stunner.bpmn.definition.property.artifacts.DataObjectTypeValue;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataObjectTypeWidgetTest {

    @Mock
    private DataTypeNamesService dataTypeNamesService = mock(DataTypeNamesService.class);

    private CustomDataTypeTextBox customDataType;

    private ValueListBox<String> dataType;

    private ComboBox dataTypeComboBox;

    private Event<NotificationEvent> notification = mock(EventSourceMock.class);

    @Captor
    private ArgumentCaptor<KeyDownHandler> keyDownHandlerCaptor;

    @Captor
    private ArgumentCaptor<ClickHandler> clickHandlerCaptor;

    @Captor
    private ArgumentCaptor<ChangeHandler> changeHandlerCaptor;

    private DataObjectTypeWidget widget;

    @Before
    public void setUp() throws Exception {
        GwtMockito.initMocks(this);
        customDataType = mock(CustomDataTypeTextBox.class);

        dataType = mock(ValueListBox.class);
        dataTypeComboBox = mock(ComboBox.class);
        widget = mock(DataObjectTypeWidget.class);
        widget.customDataType = customDataType;
        widget.dataType = dataType;
        widget.dataTypeComboBox = dataTypeComboBox;
        widget.clientDataTypesService = dataTypeNamesService;
        when(dataTypeNamesService.call(any(Path.class))).thenReturn(null);

        doCallRealMethod().when(widget).init();
        doCallRealMethod().when(widget).getModelValue(any(ValueListBox.class));
        doCallRealMethod().when(widget).setValue(any());
        doCallRealMethod().when(widget).setValue(any(), anyBoolean());

        doCallRealMethod().when(widget).setValue(any(), anyBoolean());
        doCallRealMethod().when(widget).setTextBoxModelValue(any(TextBox.class),
                                                             anyString());
        doCallRealMethod().when(widget).setListBoxModelValue(any(ValueListBox.class),
                                                             anyString());
        doCallRealMethod().when(widget).setReadOnly(anyBoolean());
        doCallRealMethod().when(widget).notifyModelChanged();
    }

    @Test
    public void testSetTextBoxModelValueCustomDataType() {
        final DataObjectTypeValue myValue = new DataObjectTypeValue("com.custom.myClass");
        DataObjectTypeWidget.doneLoading = true;
        widget.setValue(myValue);
        verify(dataType, times(1)).setValue("com.custom.myClass");
    }

    @Test
    public void testSetReadOnlyTrue() {
        widget.setReadOnly(true);
        verify(dataTypeComboBox,
               times(1)).setReadOnly(true);
    }

    @Test
    public void testSetReadOnlyFalse() {
        widget.setReadOnly(false);
        verify(dataTypeComboBox,
               times(1)).setReadOnly(false);
    }
}
