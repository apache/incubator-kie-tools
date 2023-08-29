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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.collaborationEditor.popup;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.client.forms.fields.collaborationEditor.popup.editor.CorrelationsEditorValidationItem;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.collaborationEditor.popup.editor.CorrelationsEditorValidator;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.diagram.CorrelationsValue;

public class CorrelationsEditor implements CorrelationsEditorView.Presenter {

    public interface GetDataCallback {

        void getData(CorrelationsValue correlationsValue);
    }

    private CorrelationsEditor.GetDataCallback callback = null;

    @Inject
    private CorrelationsEditorView correlationsEditorView;

    @Override
    public CorrelationsValue getCorrelationsValue() {
        return new CorrelationsValue(correlationsEditorView.getCorrelations());
    }

    public void setCorrelationsValue(CorrelationsValue correlationsValue) {
        if (correlationsValue != null) {
            correlationsEditorView.setCorrelations(correlationsValue.getCorrelations());
        }
    }

    @PostConstruct
    public void init() {
        correlationsEditorView.init(this);
        update();
    }

    @Override
    public void ok() {
        if (callback != null) {
            CorrelationsValue correlationsValue = new CorrelationsValue(correlationsEditorView.getCorrelations());
            callback.getData(correlationsValue);
        }
        correlationsEditorView.hideView();
    }

    @Override
    public void cancel() {
        correlationsEditorView.hideView();
    }

    @Override
    public void show() {
        correlationsEditorView.showView();
    }

    @Override
    public void update() {
        List<CorrelationsEditorValidationItem> validationItems =
                CorrelationsEditorValidator.validate(getCorrelationsValue().getCorrelations());
        correlationsEditorView.updateView(validationItems);
    }

    @Override
    public void setCallback(final CorrelationsEditor.GetDataCallback callback) {
        this.callback = callback;
    }
}