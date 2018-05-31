/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.scriptEditor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.FieldEditorPresenter;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.FieldEditorPresenterBaseTest;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.bpmn.forms.model.ScriptTypeMode;
import org.mockito.ArgumentCaptor;
import org.uberfire.commons.data.Pair;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ScriptTypeFieldEditorPresenterTest
        extends FieldEditorPresenterBaseTest<ScriptTypeValue, ScriptTypeFieldEditorPresenter, ScriptTypeFieldEditorPresenter.View> {

    private static final String SCRIPT = "SCRIPT";

    private static final String LANGUAGE = "LANGUAGE";

    @Override
    public ArgumentCaptor<ScriptTypeValue> newArgumentCaptor() {
        return ArgumentCaptor.forClass(ScriptTypeValue.class);
    }

    @Override
    public ScriptTypeFieldEditorPresenter.View mockEditorView() {
        return mock(ScriptTypeFieldEditorPresenter.View.class);
    }

    @Override
    public ScriptTypeFieldEditorPresenter newEditorPresenter(ScriptTypeFieldEditorPresenter.View view) {
        return new ScriptTypeFieldEditorPresenter(view);
    }

    @SuppressWarnings("unchecked")
    @Override
    public FieldEditorPresenter.ValueChangeHandler<ScriptTypeValue> mockChangeHandler() {
        return mock(FieldEditorPresenter.ValueChangeHandler.class);
    }

    @Test
    public void testSetValue() {
        ScriptTypeValue value = new ScriptTypeValue(LANGUAGE,
                                                    SCRIPT);
        editor.setValue(value);
        verify(view,
               times(1)).setLanguage(LANGUAGE);
        verify(view,
               times(1)).setScript(SCRIPT);
    }

    @Test
    public void testSetCompletionConditionMode() {
        editor.setMode(ScriptTypeMode.COMPLETION_CONDITION);
        verifyOptionsWhereSet("mvel",
                              "drools");
    }

    @Test
    public void testSetFlowConditionMode() {
        editor.setMode(ScriptTypeMode.FLOW_CONDITION);
        verifyOptionsWhereSet("java",
                              "javascript",
                              "mvel",
                              "drools");
    }

    @Test
    public void testSetActionScriptMode() {
        editor.setMode(ScriptTypeMode.ACTION_SCRIPT);
        verifyOptionsWhereSet("java",
                              "javascript",
                              "mvel");
    }

    private void verifyOptionsWhereSet(String... options) {
        List<Pair<String, String>> optionsList = Arrays.stream(options).map(option -> new Pair<>(option,
                                                                                                 option)).collect(Collectors.toList());
        verify(view,
               times(1)).setLanguageOptions(optionsList);
    }

    @Test
    public void testOnLanguageChange() {
        ScriptTypeValue oldValue = new ScriptTypeValue();
        editor.setValue(oldValue);
        when(view.getLanguage()).thenReturn(LANGUAGE);
        when(view.getScript()).thenReturn(SCRIPT);
        editor.onLanguageChange();
        verifyValueChange(oldValue,
                          new ScriptTypeValue(LANGUAGE,
                                              SCRIPT));
    }

    @Test
    public void testOnScriptChange() {
        ScriptTypeValue oldValue = new ScriptTypeValue();
        editor.setValue(oldValue);
        when(view.getLanguage()).thenReturn(LANGUAGE);
        when(view.getScript()).thenReturn(SCRIPT);
        editor.onScriptChange();
        verifyValueChange(oldValue,
                          new ScriptTypeValue(LANGUAGE,
                                              SCRIPT));
    }

    @Test
    public void testSetReadonlyTrue() {
        editor.setReadOnly(true);
        verify(view,
               times(1)).setReadOnly(true);
    }

    @Test
    public void testSetReadonlyFalse() {
        editor.setReadOnly(false);
        verify(view,
               times(1)).setReadOnly(false);
    }
}
