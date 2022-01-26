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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.jboss.errai.common.client.dom.DOMUtil;

@Templated
@Dependent
public class ConditionEditorFieldEditorView
        implements IsElement,
                   ConditionEditorFieldEditorPresenter.View {

    @Inject
    @DataField("simple-condition-radio")
    private HTMLInputElement simpleCondition;

    @Inject
    @DataField("script-condition-label-span")
    @Named("span")
    private HTMLElement scriptConditionLabelSpan;

    @Inject
    @DataField("script-condition-radio")
    private HTMLInputElement scriptCondition;

    @Inject
    @DataField("script-condition-label")
    @Named("span")
    private HTMLElement scriptLabel;

    @Inject
    @DataField("simple-condition-label")
    @Named("span")
    private HTMLElement conditionLabel;

    @Inject
    @DataField("editor-container")
    private HTMLDivElement editorContainer;

    @Inject
    @DataField("editor-error-form")
    private HTMLDivElement editorErrorForm;

    @Inject
    @DataField("editor-error")
    @Named("span")
    private HTMLElement editorError;

    private ConditionEditorFieldEditorPresenter presenter;

    @Override
    public void init(ConditionEditorFieldEditorPresenter presenter) {
        simpleCondition.type = "radio";
        scriptCondition.type = "radio";

        this.presenter = presenter;
    }

    @Override
    public void setSimpleConditionChecked(boolean checked) {
        simpleCondition.checked = (checked);
    }

    @Override
    public void setSimpleConditionEnabled(boolean enabled) {
        simpleCondition.checked = (!enabled);
    }

    @Override
    public void setScriptConditionChecked(boolean checked) {
        scriptCondition.checked = (checked);
    }

    @Override
    public void setContent(HTMLElement content) {
        DOMUtil.removeAllChildren(editorContainer);
        editorContainer.appendChild(content);
    }

    @Override
    public void showError(String error) {
        DOMUtil.addCSSClass(editorErrorForm, "has-error");
        editorError.textContent = (error);
    }

    @Override
    public void clearError() {
        DOMUtil.removeCSSClass(editorErrorForm, "has-error");
        editorError.textContent = (null);
    }

    @Override
    public void setSingleOptionSelection() {
        simpleCondition.hidden = (true);
        scriptCondition.hidden = (true);
        scriptLabel.hidden = (true);
        conditionLabel.textContent = (scriptLabel.textContent);
        scriptLabel.textContent = ("");
        scriptConditionLabelSpan.setAttribute("style", "margin-left: 0px;");
    }

    @EventHandler("simple-condition-radio")
    public void onSimpleConditionChange(@ForEvent("change") final elemental2.dom.Event event) {
        presenter.onSimpleConditionSelected();
    }

    @EventHandler("script-condition-radio")
    public void onScriptConditionChange(@ForEvent("change") final elemental2.dom.Event event) {
        presenter.onScriptEditorSelected();
    }
}
