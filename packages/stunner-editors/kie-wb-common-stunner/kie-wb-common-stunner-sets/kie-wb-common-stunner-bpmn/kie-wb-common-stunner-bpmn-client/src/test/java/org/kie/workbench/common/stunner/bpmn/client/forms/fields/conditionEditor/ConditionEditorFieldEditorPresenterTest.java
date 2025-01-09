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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.scriptEditor.ScriptTypeFieldEditorPresenter;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.FieldEditorPresenter;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.PromiseMock;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.Condition;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.GenerateConditionResult;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.ParseConditionResult;
import org.kie.workbench.common.stunner.bpmn.forms.model.ScriptTypeMode;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.client.promise.Promises;
import org.uberfire.promise.SyncPromises;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor.ConditionEditorFieldEditorPresenter.SCRIPT_PARSING_ERROR;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor.ConditionEditorFieldEditorPresenter.UNEXPECTED_SCRIPT_GENERATION_ERROR;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor.ConditionEditorFieldEditorPresenter.UNEXPECTED_SCRIPT_PARSING_ERROR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ConditionEditorFieldEditorPresenterTest {

    private static final String TRANSLATED_MESSAGE = "TRANSLATED_MESSAGE";
    private static final String SCRIPT_VALUE = "SCRIPT_VALUE";
    private static final String ERROR = "ERROR";

    @Mock
    private ConditionEditorFieldEditorPresenter.View view;

    @Mock
    private SimpleConditionEditorPresenter simpleConditionEditor;

    @Mock
    private SimpleConditionEditorPresenter.View simpleConditionEditorView;

    @Mock
    private HTMLElement simpleConditionEditorElement;

    @Mock
    private ScriptTypeFieldEditorPresenter scriptEditor;

    @Mock
    private ScriptTypeFieldEditorPresenter.View scriptEditorView;

    @Mock
    private HTMLElement scriptEditorElement;

    @Mock
    private ConditionEditorParsingService conditionEditorParsingService;

    @Mock
    private ConditionEditorGeneratorService conditionEditorGeneratorService;

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private ClientSession session;

    private ConditionEditorFieldEditorPresenter presenter;

    @Mock
    private FieldEditorPresenter.ValueChangeHandler<ScriptTypeValue> changeHandler;

    @Captor
    private ArgumentCaptor<ScriptTypeValue> scriptTypeValueCaptor;

    private Promises promises = new SyncPromises();

    @Before
    public void setUp() {
        when(scriptEditor.getView()).thenReturn(scriptEditorView);
        when(scriptEditorView.getElement()).thenReturn(scriptEditorElement);
        when(simpleConditionEditor.getView()).thenReturn(simpleConditionEditorView);
        when(simpleConditionEditorView.getElement()).thenReturn(simpleConditionEditorElement);

        presenter = spy(new ConditionEditorFieldEditorPresenter(view,
                                                                simpleConditionEditor,
                                                                scriptEditor,
                                                                conditionEditorParsingService,
                                                                conditionEditorGeneratorService,
                                                                translationService));
        presenter.addChangeHandler(changeHandler);
    }

    @Test
    public void testInit() {
        presenter.init();
        verify(view).init(presenter);
        verify(scriptEditor).setMode(ScriptTypeMode.FLOW_CONDITION);
        verify(scriptEditor).addChangeHandler(any());
        verify(simpleConditionEditor).addChangeHandler(any());
        verifyShowSimpleConditionEditor();
        assertEquals(view, presenter.getView());
    }

    @Test
    public void testInitSession() {
        presenter.init(session);
        verify(simpleConditionEditor).init(session);
    }

    @Test
    public void testSetReadonlyTrue() {
        testSetReadonly(true);
    }

    @Test
    public void testSetReadonlyFalse() {
        testSetReadonly(false);
    }

    @Test
    public void testSetValueNull() {
        presenter.setValue(null);
        verifySetValueCommonActions(null);
        verify(simpleConditionEditor).setValue(null);
        verifyShowSimpleConditionEditor();
    }

    @Test
    public void testSetValueWithScriptNonInJava() {
        ScriptTypeValue value = new ScriptTypeValue("mvel", SCRIPT_VALUE);
        presenter.setValue(value);
        verifySetValueCommonActions(value);
        verifyShowScriptEditor();
    }

    @Test
    public void testSetValueWithScriptInJavaEmpty() {
        ScriptTypeValue value = new ScriptTypeValue("java", "");
        presenter.setValue(value);
        verifySetValueCommonActions(value);
        verifyShowSimpleConditionEditor();
    }

    @Test
    public void testSetValueWithScriptInJavaParseable() {
        ScriptTypeValue value = new ScriptTypeValue("java", SCRIPT_VALUE);
        ParseConditionResult result = mock(ParseConditionResult.class);
        Condition condition = mock(Condition.class);
        when(result.hasError()).thenReturn(false);
        when(result.getCondition()).thenReturn(condition);
        doReturn(PromiseMock.success(result))
                .when(conditionEditorParsingService)
                .call(eq(SCRIPT_VALUE));
        presenter.setValue(value);

        verifySetValueCommonActions(value);
        verify(simpleConditionEditor).setValue(condition);
        verifyShowSimpleConditionEditor();
    }

    @Test
    public void testSetValueWithScriptInJavaParseableInClient() {
        when(conditionEditorGeneratorService.isAvailable()).thenReturn(true);

        ScriptTypeValue value = new ScriptTypeValue("java", SCRIPT_VALUE);
        ParseConditionResult result = mock(ParseConditionResult.class);
        Condition condition = mock(Condition.class);
        when(result.hasError()).thenReturn(false);
        when(result.getCondition()).thenReturn(condition);
        doReturn(PromiseMock.success(result))
                .when(conditionEditorParsingService)
                .call(eq(SCRIPT_VALUE));
        presenter.setValue(value);

        verifySetValueCommonActions(value);
        verify(simpleConditionEditor, never()).setValue(any());
        verifyShowScriptEditor();
    }

    @Test
    public void testSetValueWithScriptInJavaNotParseable() {
        ScriptTypeValue value = new ScriptTypeValue("java", SCRIPT_VALUE);
        ParseConditionResult result = mock(ParseConditionResult.class);
        when(result.hasError()).thenReturn(true);
        doReturn(PromiseMock.success(result))
                .when(conditionEditorParsingService)
                .call(eq(SCRIPT_VALUE));
        presenter.setValue(value);

        verifySetValueCommonActions(value);
        verify(simpleConditionEditor, never()).setValue(any());
        verifyShowScriptEditor();
    }

    @Test
    public void testOnSimpleConditionSelectedWithParseableScript() {
        ScriptTypeValue value = new ScriptTypeValue("java", SCRIPT_VALUE);
        ParseConditionResult result = mock(ParseConditionResult.class);
        Condition condition = mock(Condition.class);
        when(result.hasError()).thenReturn(false);
        when(result.getCondition()).thenReturn(condition);
        doReturn(PromiseMock.success(result))
                .when(conditionEditorParsingService)
                .call(eq(SCRIPT_VALUE));

        //at some point the script has changed
        presenter.onScriptChange(mock(ScriptTypeValue.class), value);
        //and the user wants to go to the condition editor.
        presenter.onSimpleConditionSelected();
        verify(view).clearError();
        verify(simpleConditionEditor).setValue(condition);
        verifyShowSimpleConditionEditor();
    }

    @Test
    public void testOnSimpleConditionSelectedWithNonParseableScript() {
        ScriptTypeValue value = new ScriptTypeValue("java", SCRIPT_VALUE);
        ParseConditionResult result = mock(ParseConditionResult.class);
        when(result.hasError()).thenReturn(true);
        doReturn(PromiseMock.success(result))
                .when(conditionEditorParsingService)
                .call(eq(SCRIPT_VALUE));
        when(translationService.getValue(SCRIPT_PARSING_ERROR)).thenReturn(TRANSLATED_MESSAGE);

        //at some point the script has changed
        presenter.onScriptChange(mock(ScriptTypeValue.class), value);
        //and the user wants to go to the condition editor.
        presenter.onSimpleConditionSelected();
        verify(simpleConditionEditor).setValue(null);
        verify(view).clearError();
        verify(view).showError(TRANSLATED_MESSAGE);
        verifyShowSimpleConditionEditor();
    }

    @Test
    public void testOnSimpleConditionSelectedAndNoServiceAvailable() {
        when(conditionEditorGeneratorService.isAvailable()).thenReturn(true);

        ScriptTypeValue value = new ScriptTypeValue("java", SCRIPT_VALUE);
        //at some point the script has changed
        presenter.onScriptChange(mock(ScriptTypeValue.class), value);
        //and the user wants to go to the condition editor.
        when(translationService.getValue(UNEXPECTED_SCRIPT_PARSING_ERROR, ERROR)).thenReturn(TRANSLATED_MESSAGE);
        ParseConditionResult result = mock(ParseConditionResult.class);
        when(result.hasError()).thenReturn(true);

        doReturn(PromiseMock.success(result))
                .when(conditionEditorParsingService)
                .call(eq(SCRIPT_VALUE));

        presenter.onSimpleConditionSelected();

        verifyShowSimpleConditionEditor();
    }

    @Test
    public void testOnScriptEditorSelected() {
        presenter.onScriptEditorSelected();
        verify(scriptEditor).setValue(any());
        verify(view).clearError();
        verifyShowScriptEditor();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnSimpleConditionChangeWithConditionGenerationSuccessful() {
        Condition oldValue = mock(Condition.class);
        Condition newValue = mock(Condition.class);
        GenerateConditionResult result = mock(GenerateConditionResult.class);
        when(result.hasError()).thenReturn(false);
        when(result.getExpression()).thenReturn(SCRIPT_VALUE);
        doReturn(PromiseMock.success(result))
                .when(conditionEditorGeneratorService)
                .call(eq(newValue));
        when(simpleConditionEditor.isValid()).thenReturn(true);
        presenter.onSimpleConditionChange(oldValue, newValue);
        verify(view).clearError();
        ScriptTypeValue expectedValue = new ScriptTypeValue("java", SCRIPT_VALUE);
        assertEquals(expectedValue, presenter.getValue());
        verify(changeHandler).onValueChange(any(), scriptTypeValueCaptor.capture());
        assertEquals(expectedValue, scriptTypeValueCaptor.getValue());
    }

    @Test
    public void testOnSimpleConditionChangeWithConditionGenerationErrors() {
        Condition oldValue = mock(Condition.class);
        Condition newValue = mock(Condition.class);
        GenerateConditionResult result = mock(GenerateConditionResult.class);
        when(result.hasError()).thenReturn(true);
        when(result.getError()).thenReturn(ERROR);
        doReturn(PromiseMock.success(result))
                .when(conditionEditorGeneratorService)
                .call(eq(newValue));
        when(simpleConditionEditor.isValid()).thenReturn(true);
        presenter.onSimpleConditionChange(oldValue, newValue);
        verify(view).clearError();
        verify(view).showError(ERROR);
        verify(changeHandler, never()).onValueChange(any(), any());
    }

    @Test
    public void testOnSimpleConditionChangeWithServiceError() {
        when(translationService.getValue(UNEXPECTED_SCRIPT_GENERATION_ERROR, ERROR)).thenReturn(TRANSLATED_MESSAGE);
        when(conditionEditorGeneratorService.call(any())).thenReturn(promises.reject(new Throwable(ERROR)));
        when(simpleConditionEditor.isValid()).thenReturn(true);
        presenter.onSimpleConditionChange(mock(Condition.class), mock(Condition.class));
        verify(changeHandler, never()).onValueChange(any(), any());
    }

    @Test
    public void testOnSingleSelection() {
        when(conditionEditorGeneratorService.isAvailable()).thenReturn(true);
        presenter = spy(new ConditionEditorFieldEditorPresenter(view,
                                                                simpleConditionEditor,
                                                                scriptEditor,
                                                                conditionEditorParsingService,
                                                                conditionEditorGeneratorService,
                                                                translationService));
        presenter.init();
        verify(view,
               times(1)).setSingleOptionSelection();
    }

    @Test
    public void testNotOnSingleSelection() {
        when(conditionEditorGeneratorService.isAvailable()).thenReturn(false);
        presenter = spy(new ConditionEditorFieldEditorPresenter(view,
                                                                simpleConditionEditor,
                                                                scriptEditor,
                                                                conditionEditorParsingService,
                                                                conditionEditorGeneratorService,
                                                                translationService));
        presenter.init();
        verify(view,
               never()).setSingleOptionSelection();
    }

    @Test
    public void testOnScriptChangeWhenLanguageIsJava() {
        testOnScriptChangeWhenLanguageIsJava("java");
    }

    @Test
    public void testOnScriptChangeWhenLanguageIsNotJava() {
        testOnScriptChangeWhenLanguageIsJava("drools");//or whatever any other than java
    }

    private void testOnScriptChangeWhenLanguageIsJava(String language) {
        ScriptTypeValue oldValue = mock(ScriptTypeValue.class);
        ScriptTypeValue newValue = mock(ScriptTypeValue.class);
        when(newValue.getLanguage()).thenReturn(language);

        presenter.onScriptChange(oldValue, newValue);
        assertEquals(newValue, presenter.getValue());
        verify(changeHandler).onValueChange(oldValue, newValue);
        if ("java".equals(language)) {
            verify(view).setSimpleConditionEnabled(true);
        } else {
            verify(view).setSimpleConditionEnabled(false);
        }
    }

    private void verifySetValueCommonActions(ScriptTypeValue value) {
        verify(scriptEditor).setValue(value);
        verify(simpleConditionEditor).clear();
        verify(view).clearError();
        assertEquals(value, presenter.getValue());
    }

    private void testSetReadonly(boolean value) {
        presenter.setReadOnly(value);
        verify(simpleConditionEditor).setReadOnly(value);
        verify(scriptEditor).setReadOnly(value);
    }

    private void verifyShowSimpleConditionEditor() {
        verify(view).setSimpleConditionChecked(true);
        verify(view).setScriptConditionChecked(false);
        verify(view).setContent(simpleConditionEditorElement);
    }

    private void verifyShowScriptEditor() {
        verify(view).setSimpleConditionChecked(false);
        verify(view).setScriptConditionChecked(true);
        verify(view).setContent(scriptEditorElement);
    }
}
