/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.widgets.management.editor.user;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.form.validator.Validator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.security.management.client.widgets.management.events.CreateUserAttributeEvent;
import org.uberfire.mocks.EventSourceMock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class NewUserAttributeEditorTest {

    @Mock EventSourceMock<CreateUserAttributeEvent> createUserAttributeEventEvent;
    @Mock NewUserAttributeEditor.View view;
    
    private NewUserAttributeEditor presenter;
    
    @Before
    public void setup() {
        when(view.configure(any(Validator.class), any(Validator.class))).thenReturn(view);
        when(view.reset()).thenReturn(view);
        when(view.setShowAddButton(anyBoolean())).thenReturn(view);
        when(view.setShowForm(anyBoolean())).thenReturn(view);
        presenter = new NewUserAttributeEditor(view, createUserAttributeEventEvent);
    }
    
    @Test
    public void testInit() {
        presenter.init();
        verify(view, times(1)).init(presenter);
        verify(view, times(1)).configure(any(Validator.class), any(Validator.class));
        verify(view, times(0)).reset();
        verify(view, times(0)).setShowAddButton(any(Boolean.class));
        verify(view, times(0)).setShowForm(any(Boolean.class));
        verify(view, times(0)).createAttributeNameError(anyString(), anyString());
        verify(view, times(0)).createAttributeValueError(anyString(), anyString());
    }

    @Test
    public void testShowAddButton() {
        presenter.showAddButton();
        verify(view, times(0)).init(any(NewUserAttributeEditor.class));
        verify(view, times(0)).configure(any(Validator.class), any(Validator.class));
        verify(view, times(1)).reset();
        verify(view, times(1)).setShowAddButton(true);
        verify(view, times(1)).setShowForm(false);
        verify(view, times(0)).createAttributeNameError(anyString(), anyString());
        verify(view, times(0)).createAttributeValueError(anyString(), anyString());
    }

    @Test
    public void testOnCancel() {
        presenter.showAddButton();
        verify(view, times(0)).init(any(NewUserAttributeEditor.class));
        verify(view, times(0)).configure(any(Validator.class), any(Validator.class));
        verify(view, times(1)).reset();
        verify(view, times(1)).setShowAddButton(true);
        verify(view, times(1)).setShowForm(false);
        verify(view, times(0)).createAttributeNameError(anyString(), anyString());
        verify(view, times(0)).createAttributeValueError(anyString(), anyString());
    }

    @Test
    public void testShowForm() {
        presenter.showForm();
        verify(view, times(0)).init(any(NewUserAttributeEditor.class));
        verify(view, times(0)).configure(any(Validator.class), any(Validator.class));
        verify(view, times(1)).reset();
        verify(view, times(1)).setShowAddButton(false);
        verify(view, times(1)).setShowForm(true);
        verify(view, times(0)).createAttributeNameError(anyString(), anyString());
        verify(view, times(0)).createAttributeValueError(anyString(), anyString());
    }

    @Test
    public void testOnNewAttributeClick() {
        presenter.showForm();
        verify(view, times(0)).init(any(NewUserAttributeEditor.class));
        verify(view, times(0)).configure(any(Validator.class), any(Validator.class));
        verify(view, times(1)).reset();
        verify(view, times(1)).setShowAddButton(false);
        verify(view, times(1)).setShowForm(true);
        verify(view, times(0)).createAttributeNameError(anyString(), anyString());
        verify(view, times(0)).createAttributeValueError(anyString(), anyString());
    }

    @Test
    public void testClear() {
        presenter.restrictedAttributeNames = new ArrayList<String>();
        presenter.clear();
        assertNull(presenter.restrictedAttributeNames);
        verify(view, times(0)).init(any(NewUserAttributeEditor.class));
        verify(view, times(0)).configure(any(Validator.class), any(Validator.class));
        verify(view, times(1)).reset();
        verify(view, times(1)).setShowAddButton(true);
        verify(view, times(1)).setShowForm(false);
        verify(view, times(0)).createAttributeNameError(anyString(), anyString());
        verify(view, times(0)).createAttributeValueError(anyString(), anyString());
    }

    @Test
    public void testSetRestrictedValues() {
        Collection<String> values = new ArrayList<String>();
        presenter.setRestrictedValues(values);
        assertEquals(values, presenter.restrictedAttributeNames);
    }


    @Test
    public void testAttributeNameValidatorSuccess() {
        Editor<String> editorMock = mock(Editor.class);
        List<EditorError> result = presenter.attributeNameValidator.validate(editorMock, "s1");
        assertTrue(result.isEmpty());
        verify(view, times(0)).init(any(NewUserAttributeEditor.class));
        verify(view, times(0)).configure(any(Validator.class), any(Validator.class));
        verify(view, times(0)).reset();
        verify(view, times(0)).setShowAddButton(any(Boolean.class));
        verify(view, times(0)).setShowForm(any(Boolean.class));
        verify(view, times(0)).createAttributeNameError(anyString(), anyString());
        verify(view, times(0)).createAttributeValueError(anyString(), anyString());
    }

    @Test
    public void testAttributeNameValidatorFail() {
        Editor<String> editorMock = mock(Editor.class);
        List<EditorError> result = presenter.attributeNameValidator.validate(editorMock, "");
        assertFalse(result.isEmpty());
        verify(view, times(0)).init(any(NewUserAttributeEditor.class));
        verify(view, times(0)).configure(any(Validator.class), any(Validator.class));
        verify(view, times(0)).reset();
        verify(view, times(0)).setShowAddButton(any(Boolean.class));
        verify(view, times(0)).setShowForm(any(Boolean.class));
        verify(view, times(1)).createAttributeNameError(anyString(), anyString());
        verify(view, times(0)).createAttributeValueError(anyString(), anyString());
    }

    @Test
    public void testAttributeValueValidatorSuccess() {
        Editor<String> editorMock = mock(Editor.class);
        List<EditorError> result = presenter.attributeValueValidator.validate(editorMock, "v1");
        assertTrue(result.isEmpty());
        verify(view, times(0)).init(any(NewUserAttributeEditor.class));
        verify(view, times(0)).configure(any(Validator.class), any(Validator.class));
        verify(view, times(0)).reset();
        verify(view, times(0)).setShowAddButton(any(Boolean.class));
        verify(view, times(0)).setShowForm(any(Boolean.class));
        verify(view, times(0)).createAttributeNameError(anyString(), anyString());
        verify(view, times(0)).createAttributeValueError(anyString(), anyString());
    }

    @Test
    public void testAttributeValueValidatorFail() {
        Editor<String> editorMock = mock(Editor.class);
        List<EditorError> result = presenter.attributeValueValidator.validate(editorMock, "");
        assertFalse(result.isEmpty());
        verify(view, times(0)).init(any(NewUserAttributeEditor.class));
        verify(view, times(0)).configure(any(Validator.class), any(Validator.class));
        verify(view, times(0)).reset();
        verify(view, times(0)).setShowAddButton(any(Boolean.class));
        verify(view, times(0)).setShowForm(any(Boolean.class));
        verify(view, times(0)).createAttributeNameError(anyString(), anyString());
        verify(view, times(1)).createAttributeValueError(anyString(), anyString());
    }

}

