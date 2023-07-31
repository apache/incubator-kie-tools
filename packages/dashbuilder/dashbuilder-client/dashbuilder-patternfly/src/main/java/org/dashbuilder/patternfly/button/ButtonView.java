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
package org.dashbuilder.patternfly.button;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class ButtonView implements Button.View {

    private Button presenter;

    @Inject
    @DataField
    HTMLButtonElement btn;

    @Inject
    Elemental2DomUtil util;

    @Override
    public void init(Button presenter) {
        this.presenter = presenter;
        btn.onclick = e -> {
            presenter.onClick();
            return null;
        };
    }

    @Override
    public HTMLElement getElement() {
        return btn;
    }

    public void setClassName(String className) {
        btn.classList.add(className);
    }

    @Override
    public void setText(String text) {
        btn.textContent = text;
    }

    @Override
    public void setVisible(boolean visible) {
        btn.style.display = visible ? "block" :  "none";        
    }

}
