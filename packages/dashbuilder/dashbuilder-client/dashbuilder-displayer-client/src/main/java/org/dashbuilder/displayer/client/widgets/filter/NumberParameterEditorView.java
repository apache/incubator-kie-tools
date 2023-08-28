/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import elemental2.dom.CSSProperties.WidthUnionType;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.dashbuilder.patternfly.textbox.TextBox;
import org.jboss.errai.ui.shared.api.annotations.DataField;

@Dependent
public class NumberParameterEditorView implements NumberParameterEditor.View {

    NumberParameterEditor presenter;

    @Inject
    @DataField
    HTMLDivElement formGroup;

    @Inject
    @DataField
    HTMLDivElement txtContainer;

    @Inject
    TextBox input;

    @Override
    public void init(final NumberParameterEditor presenter) {
        this.presenter = presenter;
        txtContainer.appendChild(input.getElement());
        input.setOnValueChangeAction(value -> {
            input.clearValidation();
            presenter.valueChanged();
        });

    }

    @Override
    public String getValue() {
        return input.getValue();
    }

    @Override
    public void setValue(String value) {
        input.setValue(value);
        input.clearValidation();
    }

    @Override
    public void setWidth(int width) {
        input.getElement().style.width = WidthUnionType.of(width + "px");
    }

    @Override
    public void setFocus(final boolean focus) {
        Scheduler.get().scheduleDeferred(() -> {
            input.setFocus(focus);
        });
    }

    @Override
    public void error() {
        input.validationError();
    }

    @Override
    public HTMLElement getElement() {
        return txtContainer;
    }
}
