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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.serviceEditor;

import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.Option;
import org.jboss.errai.common.client.dom.OptionsCollection;
import org.jboss.errai.common.client.dom.Select;
import org.jboss.errai.common.client.dom.TextInput;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ReflectionUtilsTest;
import org.kie.workbench.common.stunner.bpmn.definition.property.service.GenericServiceTaskValue;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WithClassesToStub({Select.class, OptionsCollection.class})
@RunWith(GwtMockitoTestRunner.class)
public class GenericServiceTaskEditorWidgetTest extends ReflectionUtilsTest {

    @GwtMock
    private GenericServiceTaskEditorWidget widget;

    @Mock
    private Select implementation;

    @Mock
    private TextInput serviceInterface;

    @Mock
    private TextInput serviceOperation;

    @Mock
    private OptionsCollection optionsCollection;

    @Mock
    private Option option;

    @Mock
    private Select select;

    @Mock
    private Event event;

    @Before
    public void setUp() throws Exception {
        setFieldValue(widget, "implementation", implementation);
        setFieldValue(widget, "serviceInterface", serviceInterface);
        setFieldValue(widget, "serviceOperation", serviceOperation);
        setFieldValue(widget, "value", new GenericServiceTaskValue());
        doCallRealMethod().when(widget).setReadOnly(any(boolean.class));
        doCallRealMethod().when(widget).getValue();
        doCallRealMethod().when(widget).onChange();
        doCallRealMethod().when(widget).init();
        doCallRealMethod().when(widget).clearSelect(any(Select.class));
        doCallRealMethod().when(widget).onImplementationChange(any(Event.class));
        doCallRealMethod().when(widget).onServiceInterfaceChange(any(Event.class));
        doCallRealMethod().when(widget).onServiceOperationChange(any(Event.class));
        doCallRealMethod().when(widget).getImplementationOptions();
        doCallRealMethod().when(widget).setServiceImplementationOptions(anyList());
        doCallRealMethod().when(widget).setValue(any(GenericServiceTaskValue.class));
        doCallRealMethod().when(widget).setValue(any(GenericServiceTaskValue.class), any(boolean.class));
        doCallRealMethod().when(widget).addValueChangeHandler(any(ValueChangeHandler.class));

        when(implementation.getOptions()).thenReturn(optionsCollection);
        when(optionsCollection.getLength()).thenReturn(0);
        when(select.getOptions()).thenReturn(optionsCollection);
        when(optionsCollection.getLength()).thenReturn(0);
    }

    @Test
    public void init() {
        widget.init();
        verify(widget,
               times(1)).setServiceImplementationOptions(any());
        verify(implementation,
               times(1)).setValue(any());
    }

    @Test
    public void setReadOnly() {
        widget.setReadOnly(true);

        verify(implementation,
               times(1)).setDisabled(true);
        verify(serviceInterface,
               times(1)).setDisabled(true);
        verify(serviceOperation,
               times(1)).setDisabled(true);
    }

    @Test
    public void getValue() {
        assertEquals(new GenericServiceTaskValue(),
                     widget.getValue());
    }

    @Test
    public void setEmptyValue() {
        GenericServiceTaskValue value = new GenericServiceTaskValue();
        widget.setValue(value);
        assertEquals(value,
                     widget.getValue());
    }

    @Test
    public void setValue() {
        GenericServiceTaskValue value = new GenericServiceTaskValue();
        value.setServiceImplementation("JAVA");
        value.setServiceInterface("AAAAAAAAAAAAAA");
        value.setServiceOperation("BBBBBBBBBBBBBB");
        widget.setValue(value);
        assertEquals(value,
                     widget.getValue());
    }

    @Test
    public void getImplementationOptions() {
        List<String> options = widget.getImplementationOptions();
        assertEquals(2, options.size());
        assertEquals("Java", options.get(0));
        assertEquals("WebService", options.get(1));
    }

    @Test
    public void setServiceImplementationOptions() {
        List<String> options = widget.getImplementationOptions();
        doNothing().when(widget).clearSelect(any(Select.class));
        when(widget.newOption(any(String.class), any(String.class))).thenReturn(option);
        doNothing().when(implementation).add(any(Select.class));
        widget.setServiceImplementationOptions(options);
        verify(implementation,
               times(2)).add(any());
    }

    @Test
    public void clearSelect() {
        widget.clearSelect(select);
        verify(select,
               times(1)).getOptions();
    }

    @Test
    public void onChange() {
        widget.onChange();
        verify(widget,
               times(1)).setValue(any(GenericServiceTaskValue.class), any(boolean.class));
    }

    @Test
    public void onImplementationChange() {
        widget.onImplementationChange(event);
        verify(widget,
               times(1)).onChange();
    }

    @Test
    public void onServiceInterfaceChange() {
        widget.onServiceInterfaceChange(event);
        verify(widget,
               times(1)).onChange();
    }

    @Test
    public void onServiceOperationChange() {
        widget.onServiceOperationChange(event);
        verify(widget,
               times(1)).onChange();
    }
}