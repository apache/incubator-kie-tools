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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.FieldEditorPresenter;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.bpmn.forms.model.ScriptTypeMode;

public class ScriptTypeListFieldEditorPresenter
        extends FieldEditorPresenter<ScriptTypeListValue> {

    private final ScriptTypeFieldEditorPresenter scriptTypePresenter;

    @Inject
    public ScriptTypeListFieldEditorPresenter(final ScriptTypeFieldEditorPresenter scriptTypePresenter) {
        this.scriptTypePresenter = scriptTypePresenter;
    }

    @PostConstruct
    public void init() {
        scriptTypePresenter.addChangeHandler(this::onValueChange);
    }

    protected void onValueChange(ScriptTypeValue oldScriptValue,
                                 ScriptTypeValue newScriptValue) {
        ScriptTypeListValue oldValue = value;
        value = new ScriptTypeListValue();
        value.getValues().add(newScriptValue);
        notifyChange(oldValue,
                     value);
    }

    public IsElement getView() {
        return scriptTypePresenter.getView();
    }

    @Override
    public void setValue(ScriptTypeListValue value) {
        super.setValue(value);
        if (value != null && value.getValues() != null && !value.getValues().isEmpty()) {
            scriptTypePresenter.setValue(value.getValues().get(0));
        } else {
            scriptTypePresenter.setValue(null);
        }
    }

    public void setMode(ScriptTypeMode mode) {
        scriptTypePresenter.setMode(mode);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        scriptTypePresenter.setReadOnly(readOnly);
    }
}