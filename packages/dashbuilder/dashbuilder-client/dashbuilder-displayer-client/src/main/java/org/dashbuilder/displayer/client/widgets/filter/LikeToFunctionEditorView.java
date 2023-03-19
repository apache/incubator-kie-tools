/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.displayer.client.widgets.filter;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;

@Dependent
public class LikeToFunctionEditorView extends Composite implements LikeToFunctionEditor.View {

    interface Binder extends UiBinder<Widget, LikeToFunctionEditorView> {}
    private static Binder uiBinder = GWT.create(Binder.class);

    LikeToFunctionEditor presenter;

    @UiField
    FormGroup form;

    @UiField
    TextBox searchPatternTextBox;

    @UiField
    CheckBox caseSensitiveCheckbox;

    public LikeToFunctionEditorView() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void init(LikeToFunctionEditor presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setPattern(String pattern) {
        form.setValidationState(ValidationState.NONE);
        searchPatternTextBox.setText(pattern);
    }

    @Override
    public void setCaseSensitive(boolean caseSensitive) {
        caseSensitiveCheckbox.setValue(caseSensitive);
    }

    @Override
    public String getPattern() {
        return searchPatternTextBox.getText();
    }

    @Override
    public boolean isCaseSensitive() {
        return caseSensitiveCheckbox.getValue();
    }

    @Override
    public void setFocus(final boolean focus) {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            public void execute () {
                searchPatternTextBox.setFocus(focus);
            }
        });
    }

    @UiHandler("searchPatternTextBox")
    public void onPatternChanged(ChangeEvent event) {
        presenter.viewUpdated();
        form.setValidationState(ValidationState.NONE);
    }

    @UiHandler("caseSensitiveCheckbox")
    public void onCaseChanged(ClickEvent event) {
        presenter.viewUpdated();
    }

    @Override
    public void error() {
        form.setValidationState(ValidationState.ERROR);
    }
}
