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

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.FieldEditorPresenter;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.Condition;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.FunctionDef;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.ParamDef;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDown;
import org.uberfire.ext.widgets.common.client.dropdown.SingleLiveSearchSelectionHandler;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor.SimpleConditionEditorPresenter.CONDITION_MAL_FORMED;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor.SimpleConditionEditorPresenter.FUNCTION_NOT_SELECTED_ERROR;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor.SimpleConditionEditorPresenter.VARIABLE_NOT_SELECTED_ERROR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SimpleConditionEditorPresenterTest {

    private static final String TRANSLATED_MESSAGE = "TRANSLATED_MESSAGE";

    private static final String FUNCTION = "FUNCTION";
    private static final String FUNCTION_TRANSLATED_NAME = "FUNCTION_TRANSLATED_NAME";
    private static final String PARAM1 = "PARAM1";
    private static final String PARAM1_NAME = "PARAM1_NAME";
    private static final String PARAM1_TYPE = "PARAM1_TYPE";
    private static final String PARAM1_TRANSLATED_NAME = "PARAM1_TRANSLATED_NAME";
    private static final String PARAM1_TRANSLATED_HELP = "PARAM1_TRANSLATED_HELP";

    private static final String PARAM2 = "PARAM2";
    private static final String PARAM2_TYPE = "PARAM2_TYPE";
    private static final String PARAM2_NAME = "PARAM2_NAME";
    private static final String PARAM2_TRANSLATED_NAME = "PARAM2_TRANSLATED_NAME";
    private static final String PARAM2_TRANSLATED_HELP = "PARAM2_TRANSLATED_HELP";

    private static final String VARIABLE = "VARIABLE";
    private static final String TYPE = "TYPE";
    private static final String VALUE = "VALUE";

    @Mock
    private SimpleConditionEditorPresenter.View view;

    @Mock
    private ManagedInstance<ConditionParamPresenter> paramInstance;

    @Mock
    private VariableSearchService variableSearchService;

    @Mock
    private FunctionSearchService functionSearchService;

    @Mock
    private FunctionNamingService functionNamingService;

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private LiveSearchDropDown<String> variableSelectorDropDown;

    @Captor
    private ArgumentCaptor<SingleLiveSearchSelectionHandler<String>> variableSearchSelectionHandlerCaptor;

    @Mock
    private SingleLiveSearchSelectionHandler<String> variableSearchSelectionHandler;

    @Mock
    private LiveSearchDropDown<String> conditionSelectorDropDown;

    @Captor
    private ArgumentCaptor<SingleLiveSearchSelectionHandler<String>> functionSearchSelectionHandlerCaptor;

    @Mock
    private SingleLiveSearchSelectionHandler<String> functionSearchSelectionHandler;

    @Captor
    private ArgumentCaptor<Command> commandCaptor;

    @Captor
    private ArgumentCaptor<Command> paramCommandCaptor;

    private SimpleConditionEditorPresenter presenter;

    @Mock
    private FieldEditorPresenter.ValueChangeHandler<Condition> changeHandler;

    @Captor
    private ArgumentCaptor<Condition> conditionCaptor;

    @Mock
    private ClientSession session;

    private List<ConditionParamPresenter> paramPresenterInstances = new ArrayList<>();

    @Before
    public void setUp() {
        when(view.getConditionSelectorDropDown()).thenReturn(conditionSelectorDropDown);
        when(view.getVariableSelectorDropDown()).thenReturn(variableSelectorDropDown);
        presenter = new SimpleConditionEditorPresenterWrapper(view,
                                                              paramInstance,
                                                              variableSearchService,
                                                              functionSearchService,
                                                              functionNamingService,
                                                              translationService);
        presenter.addChangeHandler(changeHandler);
    }

    @Test
    public void testInit() {
        presenter.init();
        verify(view).init(presenter);
        verify(variableSelectorDropDown).init(variableSearchService, variableSearchSelectionHandler);
        verify(variableSelectorDropDown).setOnChange(any());
        verify(conditionSelectorDropDown).init(functionSearchService, functionSearchSelectionHandler);
        verify(conditionSelectorDropDown).setOnChange(any());
        verify(conditionSelectorDropDown).setSearchCacheEnabled(false);
    }

    @Test
    public void setGetView() {
        assertEquals(view, presenter.getView());
    }

    @Test
    public void testInitSession() {
        presenter.init(session);
        verify(variableSearchService).init(session);
        verify(functionSearchService).init(session);
    }

    @Test
    public void testSetValueNull() {
        presenter.setValue(null);
        verifyClear();
        assertNull(presenter.getValue());
        verify(paramInstance, never()).get();
    }

    @Test
    public void testSetValueWhenConditionMalFormed() {
        Condition condition = new Condition();
        when(translationService.getValue(CONDITION_MAL_FORMED)).thenReturn(TRANSLATED_MESSAGE);
        presenter.setValue(condition);
        verify(view).setConditionError(TRANSLATED_MESSAGE);
        verifyClear();
        assertEquals(condition, presenter.getValue());
        verify(paramInstance, never()).get();
    }

    @Test
    public void testSetValueWhenConditionOK() {
        Condition condition = new Condition(FUNCTION);
        condition.addParam(PARAM1);
        condition.addParam(PARAM2);
        when(variableSearchService.getOptionType(PARAM1)).thenReturn(PARAM1_TYPE);

        FunctionDef functionDef = mockFunctionDef(FUNCTION);
        when(functionSearchService.getFunction(FUNCTION)).thenReturn(functionDef);

        presenter.setValue(condition);

        verify(functionSearchService).reload(eq(PARAM1_TYPE), commandCaptor.capture());
        commandCaptor.getValue().execute();
        variableSelectorDropDown.setSelectedItem(PARAM1);
        conditionSelectorDropDown.setSelectedItem(FUNCTION);

        verify(paramPresenterInstances.get(0)).setValue(PARAM2);
        verify(paramPresenterInstances.get(0)).setName(PARAM2_TRANSLATED_NAME);
        verify(paramPresenterInstances.get(0)).setHelp(PARAM2_TRANSLATED_HELP);
        verify(view).addParam(paramPresenterInstances.get(0).getView().getElement());
        verify(paramPresenterInstances.get(0)).setOnChangeCommand(any());
        assertEquals(paramPresenterInstances.size(), condition.getParams().size() - 1);
        verify(paramInstance, times(condition.getParams().size() - 1)).get();
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
    public void testClear() {
        presenter.clear();
        verifyClear();
    }

    @Test
    public void testOnVariableChangeWhenVariableSelected() {
        when(variableSearchSelectionHandler.getSelectedKey()).thenReturn(VARIABLE);
        when(variableSearchService.getOptionType(VARIABLE)).thenReturn(TYPE);
        when(translationService.getValue(FUNCTION_NOT_SELECTED_ERROR)).thenReturn(TRANSLATED_MESSAGE);
        presenter.onVariableChange();
        verify(functionSearchService).reload(eq(TYPE), commandCaptor.capture());
        verifyClearError();
        commandCaptor.getValue().execute();
        verify(functionSearchSelectionHandler).clearSelection();
        verify(conditionSelectorDropDown).clear();
        assertFalse(presenter.isValid());
        verify(paramInstance, never()).get();
        view.setConditionError(translationService.getValue(FUNCTION_NOT_SELECTED_ERROR));
    }

    @Test
    public void testOnVariableChangeWhenVariableNotSelected() {
        when(variableSearchSelectionHandler.getSelectedKey()).thenReturn(null);
        when(translationService.getValue(VARIABLE_NOT_SELECTED_ERROR)).thenReturn(TRANSLATED_MESSAGE);
        presenter.onVariableChange();
        verifyClearError();
        verify(functionSearchService).clear();
        verify(functionSearchSelectionHandler).clearSelection();
        verify(conditionSelectorDropDown).clear();
        verify(view).setVariableError(TRANSLATED_MESSAGE);
        verify(changeHandler).onValueChange(any(), conditionCaptor.capture());
        assertNull(conditionCaptor.getValue().getFunction());
        assertEquals(0, conditionCaptor.getValue().getParams().size());
        assertFalse(presenter.isValid());
        verify(paramInstance, never()).get();
    }

    @Test
    public void testOnConditionChangeWhenConditionSelected() {
        when(variableSearchSelectionHandler.getSelectedKey()).thenReturn(VARIABLE);
        when(functionSearchSelectionHandler.getSelectedKey()).thenReturn(FUNCTION);
        FunctionDef functionDef = mockFunctionDef(FUNCTION);
        when(functionSearchService.getFunction(FUNCTION)).thenReturn(functionDef);
        presenter.onConditionChange();

        verify(view).clearConditionError();
        verify(paramPresenterInstances.get(0)).setValue(null);
        verify(paramPresenterInstances.get(0)).setName(PARAM2_TRANSLATED_NAME);
        verify(paramPresenterInstances.get(0)).setHelp(PARAM2_TRANSLATED_HELP);
        verify(view).addParam(paramPresenterInstances.get(0).getView().getElement());
        verify(paramPresenterInstances.get(0)).setOnChangeCommand(any());
        assertEquals(paramPresenterInstances.size(), functionDef.getParams().size() - 1);
        verify(paramInstance, times(functionDef.getParams().size() - 1)).get();
    }

    @Test
    public void testOnConditionChangeWhenConditionNotSelected() {
        when(functionSearchSelectionHandler.getSelectedKey()).thenReturn(null);
        when(translationService.getValue(FUNCTION_NOT_SELECTED_ERROR)).thenReturn(TRANSLATED_MESSAGE);
        presenter.onConditionChange();
        verify(view).clearConditionError();
        verify(view).setConditionError(TRANSLATED_MESSAGE);
        assertFalse(presenter.isValid());
        verify(paramInstance, never()).get();
    }

    @Test
    public void testOnParamChangeWhenParamsAreValid() {
        prepareForParamChangeTest(true);
        paramCommandCaptor.getValue().execute();
        verifyConditionWasCreated();
        assertTrue(presenter.isValid());
    }

    @Test
    public void testOnParamChangeWhenParamsAreNotValid() {
        prepareForParamChangeTest(false);
        paramCommandCaptor.getValue().execute();
        verifyConditionWasCreated();
        assertFalse(presenter.isValid());
    }

    public class SimpleConditionEditorPresenterWrapper extends SimpleConditionEditorPresenter {

        public SimpleConditionEditorPresenterWrapper(View view,
                                                     ManagedInstance<ConditionParamPresenter> paramInstance,
                                                     VariableSearchService variableSearchService,
                                                     FunctionSearchService functionSearchService,
                                                     FunctionNamingService functionNamingService,
                                                     ClientTranslationService translationService) {
            super(view, paramInstance, variableSearchService, functionSearchService, functionNamingService, translationService);
        }

        @Override
        SingleLiveSearchSelectionHandler<String> newVariableSelectionHandler() {
            return variableSearchSelectionHandler;
        }

        @Override
        SingleLiveSearchSelectionHandler<String> newFunctionSelectionHandler() {
            return functionSearchSelectionHandler;
        }

        @Override
        ConditionParamPresenter newParamPresenter() {
            ConditionParamPresenter paramPresenter = mockParamPresenter();
            when(paramInstance.get()).thenReturn(paramPresenter);
            paramPresenterInstances.add(paramPresenter);
            return super.newParamPresenter();
        }
    }

    private void prepareForParamChangeTest(boolean paramIsValid) {
        //set user selection
        when(variableSearchSelectionHandler.getSelectedKey()).thenReturn(VARIABLE);
        when(functionSearchSelectionHandler.getSelectedKey()).thenReturn(FUNCTION);
        when(variableSearchService.getOptionType(VARIABLE)).thenReturn(TYPE);
        FunctionDef functionDef = mockFunctionDef(FUNCTION);
        when(functionSearchService.getFunction(FUNCTION)).thenReturn(functionDef);
        presenter.onConditionChange();
        paramPresenterInstances.clear();
        presenter.onVariableChange();
        verify(functionSearchService).reload(eq(TYPE), commandCaptor.capture());
        commandCaptor.getValue().execute();

        when(paramPresenterInstances.get(0).getValue()).thenReturn(VALUE);
        when(paramPresenterInstances.get(0).validateParam(any())).thenReturn(paramIsValid);
        verify(paramPresenterInstances.get(0)).setOnChangeCommand(paramCommandCaptor.capture());
    }

    private void verifyConditionWasCreated() {
        verify(changeHandler, times(3)).onValueChange(any(), conditionCaptor.capture());
        Condition expectedCondition = new Condition(FUNCTION);
        expectedCondition.addParam(VARIABLE);
        expectedCondition.addParam(VALUE);
        assertEquals(expectedCondition, conditionCaptor.getValue());
        assertEquals(expectedCondition, presenter.getValue());
    }

    private void testSetReadonly(boolean readonly) {
        presenter.setReadOnly(readonly);
        verify(variableSelectorDropDown).setEnabled(!readonly);
        verify(conditionSelectorDropDown).setEnabled(!readonly);
    }

    private void verifyClear() {
        verify(variableSearchSelectionHandler).clearSelection();
        verify(functionSearchSelectionHandler).clearSelection();
        functionSearchService.clear();
        verify(view).removeParams();
        verifyClearError();
    }

    private void verifyClearError() {
        verify(view).clearConditionError();
        verify(view).clearVariableError();
    }

    private FunctionDef mockFunctionDef(String function) {
        List<ParamDef> params = new ArrayList<>();
        params.add(mockParamDef(PARAM1_NAME, PARAM1_TYPE));
        params.add(mockParamDef(PARAM2_NAME, PARAM2_TYPE));
        FunctionDef functionDef = new FunctionDef(function, params);

        when(functionNamingService.getFunctionName(function)).thenReturn(FUNCTION_TRANSLATED_NAME);
        when(functionNamingService.getParamName(function, PARAM1_NAME)).thenReturn(PARAM1_TRANSLATED_NAME);
        when(functionNamingService.getParamHelp(function, PARAM1_NAME)).thenReturn(PARAM1_TRANSLATED_HELP);
        when(functionNamingService.getParamName(function, PARAM2_NAME)).thenReturn(PARAM2_TRANSLATED_NAME);
        when(functionNamingService.getParamHelp(function, PARAM2_NAME)).thenReturn(PARAM2_TRANSLATED_HELP);
        return functionDef;
    }

    private ParamDef mockParamDef(String name, String type) {
        ParamDef paramDef = mock(ParamDef.class);
        when(paramDef.getName()).thenReturn(name);
        when(paramDef.getType()).thenReturn(type);
        return paramDef;
    }

    private ConditionParamPresenter mockParamPresenter() {
        ConditionParamPresenter paramPresenter = mock(ConditionParamPresenter.class);
        ConditionParamPresenter.View paramPresenterView = mock(ConditionParamPresenter.View.class);
        HTMLElement element = mock(HTMLElement.class);
        when(paramPresenter.getView()).thenReturn(paramPresenterView);
        when(paramPresenterView.getElement()).thenReturn(element);
        return paramPresenter;
    }
}
