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


package org.kie.workbench.common.stunner.bpmn.client.forms.util;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.mvp.UberElement;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public abstract class FieldEditorEditorWidgetBaseTest<T, E extends FieldEditorPresenter<T>, W extends FieldEditorEditorWidget<T, E>, V extends UberElement<E>> {

    protected E editor;

    protected V view;

    private W widget;

    @Mock
    protected HTMLElement element;

    @Mock
    protected Widget wrapperWidget;

    public abstract V mockEditorView();

    public abstract E mockEditorPresenter();

    public abstract W newEditorWidget(E editor);

    public abstract T mockValue();

    @Before
    public void setUp() {
        editor = mockEditorPresenter();
        view = mockEditorView();
        widget = spy(newEditorWidget(editor));
        when(editor.getView()).thenReturn(view);
        when(view.getElement()).thenReturn(element);
        widget.init();
        verify(editor,
               times(1)).addChangeHandler(any(FieldEditorPresenter.ValueChangeHandler.class));
    }

    @Test
    public void testGetValue() {
        T value = mockValue();
        when(editor.getValue()).thenReturn(value);
        assertEquals(value,
                     widget.getValue());
    }

    @Test
    public void testSetValueWithoutNotification() {
        T oldValue = mockValue();
        when(editor.getValue()).thenReturn(oldValue);
        T value = mockValue();
        widget.setValue(value);
        verify(widget,
               never()).notifyChange(any(),
                                     any());
    }

    @Test
    public void testSetValueWithNotification() {
        T oldValue = mockValue();
        when(editor.getValue()).thenReturn(oldValue);
        T value = mockValue();
        widget.setValue(value,
                        true);
        verify(widget,
               times(1)).notifyChange(oldValue,
                                      value);
    }

    @Test
    public void testSetReadonlyTrue() {
        widget.setReadOnly(true);
        verify(editor,
               times(1)).setReadOnly(true);
    }

    @Test
    public void testSetReadonlyFalse() {
        widget.setReadOnly(false);
        verify(editor,
               times(1)).setReadOnly(false);
    }
}
