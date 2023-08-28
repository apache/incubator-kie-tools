/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.patternfly.textbox;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class TextBoxView implements TextBox.View {

    private TextBox presenter;

    @Inject
    Elemental2DomUtil util;

    @Inject
    @DataField
    @Named("span")
    HTMLElement txtRoot;

    @Inject
    @DataField
    HTMLInputElement txt;

    @Inject
    @DataField
    @Named("span")
    HTMLElement validationContainer;

    @Override
    public void init(TextBox presenter) {
        this.presenter = presenter;
        txt.onchange = e -> {
            presenter.onValueChange(txt.value);
            return null;
        };
    }

    @Override
    public HTMLElement getElement() {
        return txtRoot;
    }

    @Override
    public void setValue(String value) {
        txt.value = value;
    }

    @Override
    public void setFocus(boolean focus) {
        if (focus) {
            txt.focus();
        }
    }

    @Override
    public String getValue() {
        return txt.value;
    }

    public void validationError() {
        validationContainer.style.display = "none";
    }

    public void clearValidation() {
        validationContainer.style.display = "block";
    }

}
