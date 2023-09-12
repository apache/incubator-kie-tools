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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.client.mvp.UberElement;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public abstract class FieldEditorPresenterBaseTest<T, E extends FieldEditorPresenter<T>, V extends UberElement<E>> {

    protected E editor;

    protected V view;

    protected FieldEditorPresenter.ValueChangeHandler<T> changeHandler;

    protected ArgumentCaptor<T> newValueCaptor;

    protected ArgumentCaptor<T> oldValueCaptor;

    @Before
    public void setUp() {
        oldValueCaptor = newArgumentCaptor();
        newValueCaptor = newArgumentCaptor();
        view = mockEditorView();
        editor = spy(newEditorPresenter(view));
        editor.init();
        changeHandler = mockChangeHandler();
        editor.addChangeHandler(changeHandler);
        verify(view,
               times(1)).init(editor);
    }

    public abstract ArgumentCaptor<T> newArgumentCaptor();

    public abstract V mockEditorView();

    public abstract E newEditorPresenter(V view);

    public abstract FieldEditorPresenter.ValueChangeHandler<T> mockChangeHandler();

    @Test
    public void testGetView() {
        assertEquals(view,
                     editor.getView());
    }

    protected void verifyValueChange(T expectedOldValue, T expectedNewValue) {
        verify(changeHandler,
               times(1)).onValueChange(oldValueCaptor.capture(),
                                       newValueCaptor.capture());
        assertEquals(expectedOldValue,
                     oldValueCaptor.getValue());
        assertEquals(expectedNewValue,
                     newValueCaptor.getValue());
    }
}
