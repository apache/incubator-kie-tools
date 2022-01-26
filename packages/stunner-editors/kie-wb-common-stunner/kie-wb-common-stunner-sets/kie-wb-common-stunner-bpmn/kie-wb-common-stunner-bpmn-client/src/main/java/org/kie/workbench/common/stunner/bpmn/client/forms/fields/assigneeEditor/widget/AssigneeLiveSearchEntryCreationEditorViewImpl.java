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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assigneeEditor.widget;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.Event;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLLabelElement;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.uberfire.client.views.pfly.widgets.ValidationState;

@Templated
@Dependent
public class AssigneeLiveSearchEntryCreationEditorViewImpl implements AssigneeLiveSearchEntryCreationEditorView,
                                                                      IsElement {

    @Inject
    @DataField
    private HTMLDivElement assigneeInputFormGroup;

    @Inject
    @DataField
    private HTMLLabelElement assigneeInputLabel;

    @Inject
    @DataField
    private HTMLInputElement assigneeInput;

    @Inject
    @DataField
    @Named("span")
    private HTMLElement assigneeInputHelpBlock;

    @Inject
    @DataField
    private HTMLAnchorElement acceptButton;

    @Inject
    @DataField
    private HTMLAnchorElement cancelButton;

    private Presenter presenter;

    @Override
    public void init(Presenter presenter) {
        assigneeInput.id = "AssigneeLiveSearchEntryCreationEditorViewImpl";
        assigneeInput.type = "text";
        this.presenter = presenter;

        assigneeInputLabel.textContent = (presenter.getFieldLabel());
    }

    @Override
    public void clear() {
        assigneeInput.value = ("");
        clearErrors();
    }

    @Override
    public String getValue() {
        return assigneeInput.value;
    }

    @Override
    public void showError(String errorMessage) {
        DOMUtil.addCSSClass(assigneeInputFormGroup, ValidationState.ERROR.getCssName());
        assigneeInputHelpBlock.textContent = (errorMessage);
    }

    @Override
    public void clearErrors() {
        DOMUtil.removeCSSClass(assigneeInputFormGroup, ValidationState.ERROR.getCssName());
        assigneeInputHelpBlock.textContent = ("");
    }

    @EventHandler("acceptButton")
    public void onAccept(@ForEvent("click")Event event) {
        presenter.onAccept();
        event.stopPropagation();
    }

    @EventHandler("cancelButton")
    public void onCancel(@ForEvent("click") Event event) {
        presenter.onCancel();
        event.stopPropagation();
    }
}
