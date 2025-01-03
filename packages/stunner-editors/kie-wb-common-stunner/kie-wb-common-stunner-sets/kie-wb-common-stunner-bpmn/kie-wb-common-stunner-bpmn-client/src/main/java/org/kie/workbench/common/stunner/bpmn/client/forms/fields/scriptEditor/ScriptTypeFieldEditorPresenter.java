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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.stunner.bpmn.client.components.monaco_editor.MonacoEditorLanguage;
import org.kie.workbench.common.stunner.bpmn.client.components.monaco_editor.MonacoEditorPresenter;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.FieldEditorPresenter;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.bpmn.forms.model.ScriptTypeMode;
import org.uberfire.client.mvp.UberElement;

import static org.kie.workbench.common.stunner.bpmn.forms.model.ScriptTypeMode.ACTION_SCRIPT;
import static org.kie.workbench.common.stunner.bpmn.forms.model.ScriptTypeMode.COMPLETION_CONDITION;
import static org.kie.workbench.common.stunner.bpmn.forms.model.ScriptTypeMode.DROOLS_CONDITION;
import static org.kie.workbench.common.stunner.bpmn.forms.model.ScriptTypeMode.FLOW_CONDITION;

public class ScriptTypeFieldEditorPresenter
        extends FieldEditorPresenter<ScriptTypeValue> {

    public static class View implements UberElement<ScriptTypeFieldEditorPresenter> {

        private final MonacoEditorPresenter monacoEditor;

        public View(MonacoEditorPresenter monacoEditor) {
            this.monacoEditor = monacoEditor;
        }

        @Override
        public void init(ScriptTypeFieldEditorPresenter presenter) {
            monacoEditor.setOnChangeCallback(presenter::onChange);
            monacoEditor.setWidthPx(311);
            monacoEditor.setHeightPx(200);
        }

        public void addLanguage(MonacoEditorLanguage language) {
            monacoEditor.addLanguage(language);
        }

        public void setValue(String lang, String value) {
            monacoEditor.setValue(lang, value);
        }

        public void setReadOnly(boolean readOnly) {
            monacoEditor.setReadOnly(readOnly);
        }

        public String getLanguageId() {
            return monacoEditor.getLanguageId();
        }

        public String getValue() {
            return monacoEditor.getValue();
        }

        @Override
        public HTMLElement getElement() {
            return monacoEditor.getView().getElement();
        }

        public MonacoEditorPresenter getMonacoEditor() {
            return monacoEditor;
        }
    }

    private final View view;

    @Inject
    public ScriptTypeFieldEditorPresenter(final MonacoEditorPresenter monacoEditor) {
        this(new View(monacoEditor));
    }

    ScriptTypeFieldEditorPresenter(final View view) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public IsElement getView() {
        return view;
    }

    @Override
    public void setValue(ScriptTypeValue value) {
        super.setValue(value);
        if (value != null) {
            view.setValue(value.getLanguage(), value.getScript());
        }
    }

    public void setMode(ScriptTypeMode mode) {
        getLanguages(mode).forEach(view::addLanguage);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        view.setReadOnly(readOnly);
    }

    private static List<MonacoEditorLanguage> getLanguages(ScriptTypeMode mode) {
        List<MonacoEditorLanguage> languages = null;
        if (mode == ACTION_SCRIPT) {
            languages = new ArrayList<>(3);
            languages.add(MonacoEditorLanguage.JAVA);
            languages.add(MonacoEditorLanguage.MVEL);
        } else if (mode == COMPLETION_CONDITION) {
            languages = new ArrayList<>(2);
            languages.add(MonacoEditorLanguage.MVEL);
            languages.add(MonacoEditorLanguage.DROOLS);
        } else if (mode == FLOW_CONDITION) {
            languages = new ArrayList<>(5);
            languages.add(MonacoEditorLanguage.JAVA);
            languages.add(MonacoEditorLanguage.MVEL);
            languages.add(MonacoEditorLanguage.DROOLS);
            languages.add(MonacoEditorLanguage.FEEL);
        } else if (mode == DROOLS_CONDITION) {
            languages = new ArrayList<>(1);
            languages.add(MonacoEditorLanguage.DROOLS);
        } else {
            languages = Collections.emptyList();
        }
        return languages;
    }

    protected void onChange() {
        ScriptTypeValue oldValue = value;
        value = copy(oldValue,
                     true);
        value.setScript(view.getValue());
        value.setLanguage(view.getLanguageId());

        notifyChange(oldValue,
                     value);
    }

    private ScriptTypeValue copy(ScriptTypeValue source,
                                 boolean createIfSourceNull) {
        if (source == null) {
            return createIfSourceNull ? new ScriptTypeValue() : null;
        }
        ScriptTypeValue copy = new ScriptTypeValue();
        copy.setScript(source.getScript());
        copy.setLanguage(source.getLanguage());
        return copy;
    }
}