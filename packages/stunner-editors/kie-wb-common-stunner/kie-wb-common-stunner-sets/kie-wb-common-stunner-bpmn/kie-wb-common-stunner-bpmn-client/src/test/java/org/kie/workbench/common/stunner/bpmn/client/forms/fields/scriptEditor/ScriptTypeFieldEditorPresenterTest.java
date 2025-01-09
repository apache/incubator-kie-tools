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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.scriptEditor;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.client.components.monaco_editor.MonacoEditorLanguage;
import org.kie.workbench.common.stunner.bpmn.client.components.monaco_editor.MonacoEditorPresenter;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.FieldEditorPresenter;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.FieldEditorPresenterBaseTest;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.bpmn.forms.model.ScriptTypeMode;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
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
        return spy(new ScriptTypeFieldEditorPresenter.View(mock(MonacoEditorPresenter.class)));
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
        verify(view, times(1)).setValue(eq(LANGUAGE), eq(SCRIPT));
    }

    @Test
    public void testSetCompletionConditionMode() {
        editor.setMode(ScriptTypeMode.COMPLETION_CONDITION);
        verify(view, times(1)).addLanguage(eq(MonacoEditorLanguage.MVEL));
        verify(view, times(1)).addLanguage(eq(MonacoEditorLanguage.DROOLS));
        verify(view, never()).addLanguage(eq(MonacoEditorLanguage.JAVA));
        verify(view, never()).addLanguage(eq(MonacoEditorLanguage.FEEL));
    }

    @Test
    public void testSetFlowConditionMode() {
        editor.setMode(ScriptTypeMode.FLOW_CONDITION);
        verify(view, times(1)).addLanguage(eq(MonacoEditorLanguage.JAVA));
        verify(view, times(1)).addLanguage(eq(MonacoEditorLanguage.MVEL));
        verify(view, times(1)).addLanguage(eq(MonacoEditorLanguage.DROOLS));
        verify(view, times(1)).addLanguage(eq(MonacoEditorLanguage.FEEL));
    }

    @Test
    public void testSetActionScriptMode() {
        editor.setMode(ScriptTypeMode.ACTION_SCRIPT);
        verify(view, times(1)).addLanguage(eq(MonacoEditorLanguage.JAVA));
        verify(view, times(1)).addLanguage(eq(MonacoEditorLanguage.MVEL));
        verify(view, never()).addLanguage(eq(MonacoEditorLanguage.DROOLS));
        verify(view, never()).addLanguage(eq(MonacoEditorLanguage.FEEL));
    }

    @Test
    public void testSetDroolsConditionScriptMode() {
        editor.setMode(ScriptTypeMode.DROOLS_CONDITION);
        verify(view, times(1)).addLanguage(eq(MonacoEditorLanguage.DROOLS));
        verify(view, never()).addLanguage(eq(MonacoEditorLanguage.JAVA));
        verify(view, never()).addLanguage(eq(MonacoEditorLanguage.MVEL));
        verify(view, never()).addLanguage(eq(MonacoEditorLanguage.FEEL));
    }

    @Test
    public void testOnLanguageChange() {
        ScriptTypeValue oldValue = new ScriptTypeValue();
        editor.setValue(oldValue);
        when(view.getLanguageId()).thenReturn(LANGUAGE);
        when(view.getValue()).thenReturn(SCRIPT);
        editor.onChange();
        verifyValueChange(oldValue,
                          new ScriptTypeValue(LANGUAGE,
                                              SCRIPT));
    }

    @Test
    public void testOnScriptChange() {
        ScriptTypeValue oldValue = new ScriptTypeValue();
        editor.setValue(oldValue);
        when(view.getLanguageId()).thenReturn(LANGUAGE);
        when(view.getValue()).thenReturn(SCRIPT);
        editor.onChange();
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

    @Test
    public void testMonacoEditorView() {
        MonacoEditorPresenter monaco = mock(MonacoEditorPresenter.class);
        ScriptTypeFieldEditorPresenter.View view = new ScriptTypeFieldEditorPresenter.View(monaco);
        view.init(editor);
        verify(monaco, times(1)).setOnChangeCallback(any());
        verify(monaco, times(1)).setWidthPx(anyInt());
        verify(monaco, times(1)).setHeightPx(anyInt());
        view.addLanguage(MonacoEditorLanguage.JAVA);
        verify(monaco, times(1)).addLanguage(eq(MonacoEditorLanguage.JAVA));
        view.setValue("java", "someValue");
        verify(monaco, times(1)).setValue(eq("java"), eq("someValue"));
        view.setReadOnly(true);
        verify(monaco, times(1)).setReadOnly(eq(true));
        when(monaco.getLanguageId()).thenReturn("l1");
        assertEquals("l1", view.getLanguageId());
        when(monaco.getValue()).thenReturn("v1");
        assertEquals("v1", view.getValue());
    }
}
