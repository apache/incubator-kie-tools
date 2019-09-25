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
package org.drools.workbench.screens.testscenario.client;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.TextBox;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.uberfire.client.callbacks.Callback;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class EditableTextBoxTest {

    @Mock
    Callback<String> valueChanged;

    @Mock
    TextBox view;
    
    @Captor
    ArgumentCaptor<ValueChangeHandler> valueChangeHandlerArgumentCaptor;

    @Test
    public void defaults() {
        new EditableTextBox(valueChanged,
                            view,
                            "age",
                            null);
        final InOrder o = inOrder(view);

        o.verify(view).addValueChangeHandler(any());
        o.verify(view).setText(null);
        verify(view).setTitle("age");
    }

    @Test
    public void valueHasBeenSetPreviously() {

        new EditableTextBox(valueChanged,
                            view,
                            "age",
                            "11");
        verify(view).setText("11");
        verify(view).addValueChangeHandler(valueChangeHandlerArgumentCaptor.capture());

        final ValueChangeEvent valueChangeEvent = mock(ValueChangeEvent.class);
        doReturn("123").when(valueChangeEvent).getValue();
        valueChangeHandlerArgumentCaptor.getValue().onValueChange(valueChangeEvent);

        verify(valueChanged).callback("123");
    }
}