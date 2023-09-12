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

import javax.inject.Inject;

import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.RadioInput;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class ConditionEditorFieldEditorView
        implements IsElement,
                   ConditionEditorFieldEditorPresenter.View {

    @Inject
    @DataField("simple-condition-radio")
    private RadioInput simpleCondition;

    @Inject
    @DataField("script-condition-label-span")
    private Span scriptConditionLabelSpan;

    @Inject
    @DataField("script-condition-radio")
    private RadioInput scriptCondition;

    @Inject
    @DataField("script-condition-label")
    private Span scriptLabel;

    @Inject
    @DataField("simple-condition-label")
    private Span conditionLabel;

    @Inject
    @DataField("editor-container")
    private Div editorContainer;

    @Inject
    @DataField("editor-error-form")
    private Div editorErrorForm;

    @Inject
    @DataField("editor-error")
    private Span editorError;

    private ConditionEditorFieldEditorPresenter presenter;

    @Override
    public void init(ConditionEditorFieldEditorPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setSimpleConditionChecked(boolean checked) {
        simpleCondition.setChecked(checked);
    }

    @Override
    public void setSimpleConditionEnabled(boolean enabled) {
        simpleCondition.setDisabled(!enabled);
    }

    @Override
    public void setScriptConditionChecked(boolean checked) {
        scriptCondition.setChecked(checked);
    }

    @Override
    public void setContent(HTMLElement content) {
        DOMUtil.removeAllChildren(editorContainer);
        editorContainer.appendChild(content);
    }

    @Override
    public void showError(String error) {
        DOMUtil.addCSSClass(editorErrorForm, "has-error");
        editorError.setTextContent(error);
    }

    @Override
    public void clearError() {
        DOMUtil.removeCSSClass(editorErrorForm, "has-error");
        editorError.setTextContent(null);
    }

    @Override
    public void setSingleOptionSelection() {
        simpleCondition.setHidden(true);
        scriptCondition.setHidden(true);
        scriptLabel.setHidden(true);
        conditionLabel.setTextContent(scriptLabel.getTextContent());
        scriptLabel.setTextContent("");
        scriptConditionLabelSpan.setAttribute("style", "margin-left: 0px;");
    }

    @EventHandler("simple-condition-radio")
    private void onSimpleConditionChange(@ForEvent("change") final Event event) {
        presenter.onSimpleConditionSelected();
    }

    @EventHandler("script-condition-radio")
    private void onScriptConditionChange(@ForEvent("change") final Event event) {
        presenter.onScriptEditorSelected();
    }
}
