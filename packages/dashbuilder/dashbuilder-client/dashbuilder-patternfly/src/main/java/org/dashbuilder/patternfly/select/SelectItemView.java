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
package org.dashbuilder.patternfly.select;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLLIElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class SelectItemView implements SelectItem.View {

    private static final String SELECTED_CLASS = "pf-m-selected";

    @Inject
    @DataField
    HTMLLIElement item;

    @Inject
    @DataField
    HTMLButtonElement btn;

    @Inject
    @DataField
    @Named("span")
    HTMLElement selectedIcon;

    @Inject
    Elemental2DomUtil util;

    private boolean selected;

    @Override
    public void init(SelectItem presenter) {
        btn.onclick = e -> {
            setSelected(!selected);
            presenter.itemClicked();
            return null;
        };
    }

    @Override
    public HTMLElement getElement() {
        return item;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            btn.classList.add(SELECTED_CLASS);
        } else {
            btn.classList.remove(SELECTED_CLASS);
        }
        selectedIcon.style.display = selected ? "block" : "none";
    }

    @Override
    public void setText(String text) {
        btn.textContent = text;
        btn.appendChild(selectedIcon);
    }

}
