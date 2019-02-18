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

import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class MultipleInstanceVariableEditorView
        implements IsElement,
                   MultipleInstanceVariableEditorPresenter.View {

    @Inject
    @DataField("variableName")
    private TextInput variableName;

    private MultipleInstanceVariableEditorPresenter presenter;

    @Override
    public void init(MultipleInstanceVariableEditorPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setVariableName(String variableName) {
        this.variableName.setValue(variableName);
    }

    @Override
    public String getVariableName() {
        return variableName.getValue();
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        variableName.setDisabled(readOnly);
    }

    @EventHandler
    private void onVariableNameChange(@ForEvent("change") final Event event) {
        presenter.onVariableNameChange();
    }
}
