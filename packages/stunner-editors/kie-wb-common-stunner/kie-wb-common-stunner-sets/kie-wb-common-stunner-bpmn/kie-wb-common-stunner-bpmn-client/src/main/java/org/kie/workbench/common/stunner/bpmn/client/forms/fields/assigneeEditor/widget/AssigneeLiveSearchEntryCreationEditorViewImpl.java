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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assigneeEditor.widget;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Label;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.views.pfly.widgets.ValidationState;

@Templated
public class AssigneeLiveSearchEntryCreationEditorViewImpl implements AssigneeLiveSearchEntryCreationEditorView,
                                                                      IsElement {

    @Inject
    @DataField
    private Div assigneeInputFormGroup;

    @Inject
    @DataField
    private Label assigneeInputLabel;

    @Inject
    @DataField
    private TextInput assigneeInput;

    @Inject
    @DataField
    private Span assigneeInputHelpBlock;

    @Inject
    @DataField
    private Anchor acceptButton;

    @Inject
    @DataField
    private Anchor cancelButton;

    private Presenter presenter;

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;

        assigneeInputLabel.setTextContent(presenter.getFieldLabel());
    }

    @Override
    public void clear() {
        assigneeInput.setValue("");
        clearErrors();
    }

    @Override
    public String getValue() {
        return assigneeInput.getValue();
    }

    @Override
    public void showError(String errorMessage) {
        DOMUtil.addCSSClass(assigneeInputFormGroup, ValidationState.ERROR.getCssName());
        assigneeInputHelpBlock.setTextContent(errorMessage);
    }

    @Override
    public void clearErrors() {
        DOMUtil.removeCSSClass(assigneeInputFormGroup, ValidationState.ERROR.getCssName());
        assigneeInputHelpBlock.setTextContent("");
    }

    @EventHandler("acceptButton")
    public void onAccept(ClickEvent event) {
        presenter.onAccept();
        event.stopPropagation();
    }

    @EventHandler("cancelButton")
    public void onCancel(ClickEvent event) {
        presenter.onCancel();
        event.stopPropagation();
    }
}
