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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.client.forms.util.FieldEditorPresenter;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.bpmn.forms.model.ScriptTypeMode;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.commons.data.Pair;

import static org.kie.workbench.common.stunner.bpmn.forms.model.ScriptTypeMode.ACTION_SCRIPT;
import static org.kie.workbench.common.stunner.bpmn.forms.model.ScriptTypeMode.COMPLETION_CONDITION;
import static org.kie.workbench.common.stunner.bpmn.forms.model.ScriptTypeMode.DROOLS_CONDITION;
import static org.kie.workbench.common.stunner.bpmn.forms.model.ScriptTypeMode.FLOW_CONDITION;

public class ScriptTypeFieldEditorPresenter
        extends FieldEditorPresenter<ScriptTypeValue> {

    private static final String JAVA = "java";

    private static final String JAVASCRIPT = "javascript";

    private static final String MVEL = "mvel";

    private static final String DROOLS = "drools";

    public interface View extends UberElement<ScriptTypeFieldEditorPresenter> {

        void setScript(String script);

        String getScript();

        void setLanguage(String language);

        String getLanguage();

        void setLanguageOptions(List<Pair<String, String>> options);

        void setReadOnly(boolean readOnly);
    }

    private final View view;

    @Inject
    public ScriptTypeFieldEditorPresenter(final View view) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public ScriptTypeFieldEditorPresenter.View getView() {
        return view;
    }

    @Override
    public void setValue(ScriptTypeValue value) {
        super.setValue(value);
        if (value != null) {
            view.setLanguage(value.getLanguage());
            view.setScript(value.getScript());
        }
    }

    public void setMode(ScriptTypeMode mode) {
        view.setLanguageOptions(getLanguageOptions(mode));
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        view.setReadOnly(readOnly);
    }

    private List<Pair<String, String>> getLanguageOptions(ScriptTypeMode mode) {
        List<Pair<String, String>> options = new ArrayList<>();
        if (mode == ACTION_SCRIPT) {
            options.add(new Pair<>(JAVA,
                                   JAVA));
            options.add(new Pair<>(JAVASCRIPT,
                                   JAVASCRIPT));
            options.add(new Pair<>(MVEL,
                                   MVEL));
        } else if (mode == COMPLETION_CONDITION) {
            options.add(new Pair<>(MVEL,
                                   MVEL));
            options.add(new Pair<>(DROOLS,
                                   DROOLS));
        } else if (mode == FLOW_CONDITION) {
            options.add(new Pair<>(JAVA,
                                   JAVA));
            options.add(new Pair<>(JAVASCRIPT,
                                   JAVASCRIPT));
            options.add(new Pair<>(MVEL,
                                   MVEL));
            options.add(new Pair<>(DROOLS,
                                   DROOLS));
        } else if (mode == DROOLS_CONDITION) {
            options.add(new Pair<>(DROOLS,
                                   DROOLS));
        }
        return options;
    }

    protected void onLanguageChange() {
        onChange();
    }

    protected void onScriptChange() {
        onChange();
    }

    protected void onChange() {
        ScriptTypeValue oldValue = value;
        value = copy(oldValue,
                     true);
        value.setScript(view.getScript());
        value.setLanguage(view.getLanguage());
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