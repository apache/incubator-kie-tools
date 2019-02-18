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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.multipleInstanceVariableEditor;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.FieldEditorPresenter;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.FieldEditorPresenterBaseTest;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MultipleInstanceVariableEditorPresenterTest
        extends FieldEditorPresenterBaseTest<String, MultipleInstanceVariableEditorPresenter, MultipleInstanceVariableEditorPresenter.View> {

    @Override
    public ArgumentCaptor<String> newArgumentCaptor() {
        return ArgumentCaptor.forClass(String.class);
    }

    @Override
    public MultipleInstanceVariableEditorPresenter.View mockEditorView() {
        return mock(MultipleInstanceVariableEditorPresenter.View.class);
    }

    @Override
    public MultipleInstanceVariableEditorPresenter newEditorPresenter(MultipleInstanceVariableEditorPresenter.View view) {
        return new MultipleInstanceVariableEditorPresenter(view);
    }

    @Test
    public void testSetValue() {
        editor.setValue("value");
        verify(view).setVariableName("value");
    }

    @Test
    public void testSetReadonlyTrue() {
        editor.setReadOnly(true);
        verify(view).setReadOnly(true);
    }

    @Test
    public void testSetReadonlyFalse() {
        editor.setReadOnly(false);
        verify(view).setReadOnly(false);
    }

    @Test
    public void testOnVariableNameChange() {
        editor.setValue("oldValue");
        when(view.getVariableName()).thenReturn("newValue");
        editor.onVariableNameChange();
        verifyValueChange("oldValue", "newValue");
    }

    @Override
    @SuppressWarnings("unchecked")
    public FieldEditorPresenter.ValueChangeHandler<String> mockChangeHandler() {
        return mock(FieldEditorPresenter.ValueChangeHandler.class);
    }
}
