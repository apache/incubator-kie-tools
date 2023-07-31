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

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLUListElement;
import jsinterop.base.Js;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class SelectView implements Select.View {
    // pf-m-expanded

    @Inject
    @DataField
    HTMLDivElement selectRoot;

    @Inject
    @DataField
    HTMLButtonElement btnToggle;

    @Inject
    @DataField
    @Named("span")
    HTMLElement selectHint;

    @Inject
    @DataField
    HTMLUListElement itemsContainer;

    boolean itemsVisible;

    @Inject
    Elemental2DomUtil util;

    @Override
    public void init(Select presenter) {
        itemsVisible = false;
        toggleMenu();
        btnToggle.onclick = e -> {
            itemsVisible = !itemsVisible;
            toggleMenu();
            return null;
        };
        DomGlobal.document.addEventListener("click", e -> {
            if (!selectRoot.contains(Js.cast(e.target))) {
                closeMenu();
            }
        });
    }

    @Override
    public HTMLElement getElement() {
        return selectRoot;
    }

    public void addItem(SelectItem item) {
        itemsContainer.appendChild(item.getElement());
    }

    @Override
    public void setPromptText(String hint) {
        selectHint.textContent = hint;
    }

    @Override
    public void clear() {
        util.removeAllElementChildren(itemsContainer);
    }

    @Override
    public void closeMenu() {
        itemsVisible = false;
        toggleMenu();
    }

    private void toggleMenu() {
        itemsContainer.style.display = itemsVisible ? "block" : "none";
    }

}
