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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.scriptEditor.ScriptTypeFieldEditorPresenter;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.FieldEditorPresenter;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.Condition;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.GenerateConditionResult;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.ParseConditionResult;
import org.kie.workbench.common.stunner.bpmn.forms.model.ScriptTypeMode;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.uberfire.client.mvp.UberElement;

import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

public class ConditionEditorFieldEditorPresenter
        extends FieldEditorPresenter<ScriptTypeValue> {

    private static final String DEFAULT_LANGUAGE = "java";

    static final String SCRIPT_PARSING_ERROR = "ConditionEditorFieldEditorView.ScriptParsingError";

    static final String UNEXPECTED_SCRIPT_PARSING_ERROR = "ConditionEditorFieldEditorView.UnexpectedScriptParsingError";

    static final String UNEXPECTED_SCRIPT_GENERATION_ERROR = "ConditionEditorFieldEditorView.UnexpectedScriptGenerationError";

    public interface View extends UberElement<ConditionEditorFieldEditorPresenter> {

        void setSimpleConditionChecked(boolean checked);

        void setSimpleConditionEnabled(boolean enabled);

        void setScriptConditionChecked(boolean checked);

        void setContent(HTMLElement content);

        void showError(String error);

        void clearError();

        void setSingleOptionSelection();
    }

    private final View view;

    private final SimpleConditionEditorPresenter simpleConditionEditor;

    private final ScriptTypeFieldEditorPresenter scriptEditor;

    private final ConditionEditorParsingService conditionEditorParsingService;

    private final ConditionEditorGeneratorService conditionEditorGeneratorService;

    private final ClientTranslationService translationService;

    @Inject
    public ConditionEditorFieldEditorPresenter(final View view,
                                               final SimpleConditionEditorPresenter simpleConditionEditor,
                                               final ScriptTypeFieldEditorPresenter scriptEditor,
                                               final ConditionEditorParsingService conditionEditorParsingService,
                                               final ConditionEditorGeneratorService conditionEditorGeneratorService,
                                               final ClientTranslationService translationService) {
        this.view = view;
        this.simpleConditionEditor = simpleConditionEditor;
        this.scriptEditor = scriptEditor;
        this.conditionEditorParsingService = conditionEditorParsingService;
        this.conditionEditorGeneratorService = conditionEditorGeneratorService;
        this.translationService = translationService;

        if (isServiceAvailable()) {
            enableSimpleConditionEditor(false);
            view.setSingleOptionSelection();
        }
    }

    public boolean isServiceAvailable() {
        return conditionEditorGeneratorService.isAvailable();
    }

    @Override
    protected IsElement getView() {
        return view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        scriptEditor.setMode(ScriptTypeMode.FLOW_CONDITION);
        scriptEditor.addChangeHandler(this::onScriptChange);
        simpleConditionEditor.addChangeHandler(this::onSimpleConditionChange);

        if (!isServiceAvailable()) {
            showSimpleConditionEditor();
        } else {
            showScriptEditor();
        }

    }

    public void init(ClientSession session) {
        simpleConditionEditor.init(session);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        simpleConditionEditor.setReadOnly(readOnly);
        scriptEditor.setReadOnly(readOnly);
    }

    @Override
    public void setValue(ScriptTypeValue value) {
        super.setValue(value);
        scriptEditor.setValue(value);
        simpleConditionEditor.clear();
        clearError();
        if (value != null) {
            if (isInDefaultLanguage(value) && !isServiceAvailable()) {
                if (!isEmpty(value.getScript())) {
                    conditionEditorParsingService
                            .call(value.getScript())
                            .then(result -> {
                                onSetValue(result);
                                return null;
                            })
                            .catch_(throwable -> {
                                onSetValueError((Throwable) throwable);
                                return null;
                            });
                } else {
                    showSimpleConditionEditor();
                }
            } else {
                showScriptEditor();
            }
        } else {
            simpleConditionEditor.setValue(null);
            showSimpleConditionEditor();
        }
    }

    void onSimpleConditionSelected() {
        clearError();
        if (value != null && !isEmpty(value.getScript()) && !isServiceAvailable()) {
            conditionEditorParsingService
                    .call(value.getScript())
                    .then(result -> {
                        onSimpleConditionSelected(result);
                        return null;
                    })
                    .catch_(throwable -> {
                        onSimpleConditionSelectedError((Throwable) throwable);
                        return null;
                    });
        } else {
            simpleConditionEditor.setValue(null);
            showSimpleConditionEditor();
        }
    }

    void onScriptEditorSelected() {
        scriptEditor.setValue(value);
        clearError();
        showScriptEditor();
    }

    void onSimpleConditionChange(Condition oldValue, Condition newValue) {
        if (simpleConditionEditor.isValid()) {
            conditionEditorGeneratorService
                    .call(newValue)
                    .then(result -> {
                        onSimpleConditionChange(result);
                        return null;
                    })
                    .catch_(throwable -> {
                        onSimpleConditionChangeError((Throwable) throwable);
                        return null;
                    });
        } else {
            clearError();
        }
    }

    private void onSimpleConditionChange(GenerateConditionResult result) {
        clearError();
        if (!result.hasError()) {
            ScriptTypeValue oldValue = value;
            value = new ScriptTypeValue(DEFAULT_LANGUAGE, result.getExpression());
            notifyChange(oldValue, value);
        } else {
            showError(result.getError());
        }
    }

    private boolean onSimpleConditionChangeError(Throwable throwable) {
        return false;
    }

    void onScriptChange(ScriptTypeValue oldValue, ScriptTypeValue newValue) {
        value = newValue;
        notifyChange(oldValue, newValue);
        enableSimpleConditionEditor(isInDefaultLanguage(newValue));
    }

    private void onSetValue(ParseConditionResult result) {
        if (!result.hasError()) {
            simpleConditionEditor.setValue(result.getCondition());
            showSimpleConditionEditor();
        } else {
            showScriptEditor();
        }
    }

    private boolean onSetValueError(Throwable throwable) {
        return false;
    }

    private void onSimpleConditionSelected(ParseConditionResult result) {
        if (!result.hasError()) {
            simpleConditionEditor.setValue(result.getCondition());
        } else {
            simpleConditionEditor.setValue(null);
            showError(translationService.getValue(SCRIPT_PARSING_ERROR));
        }
        showSimpleConditionEditor();
    }

    private boolean onSimpleConditionSelectedError(Throwable throwable) {
        return false;
    }

    private void enableSimpleConditionEditor(boolean enable) {
        view.setSimpleConditionEnabled(enable);
    }

    private boolean isInDefaultLanguage(ScriptTypeValue value) {
        return value != null && DEFAULT_LANGUAGE.equals(value.getLanguage());
    }

    private void showSimpleConditionEditor() {
        view.setSimpleConditionChecked(true);
        view.setScriptConditionChecked(false);
        view.setContent(simpleConditionEditor.getView().getElement());
    }

    private void showScriptEditor() {
        view.setScriptConditionChecked(true);
        view.setSimpleConditionChecked(false);
        view.setContent(scriptEditor.getView().getElement());
    }

    private void showError(String error) {
        view.showError(error);
    }

    private void clearError() {
        view.clearError();
    }
}
