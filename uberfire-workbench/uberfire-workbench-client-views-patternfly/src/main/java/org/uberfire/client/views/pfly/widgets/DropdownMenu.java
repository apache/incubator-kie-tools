/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.views.pfly.widgets;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLLIElement;
import elemental2.dom.HTMLUListElement;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class DropdownMenu implements IsElement {

    @Inject
    @DataField("dropdown")
    HTMLDivElement dropdown;

    @Inject
    @DataField("text")
    @Named("span")
    HTMLElement text;

    @Inject
    @DataField("dropdown-menu")
    HTMLUListElement dropdownMenu;

    public void setText(final String text) {
        this.text.textContent = text;
    }

    public void addDropdownItem(final HTMLLIElement item) {
        dropdownMenu.appendChild(item);
    }

    public void setItemsAlignment(final ItemsAlignment alighment) {
        if (alighment == ItemsAlignment.RIGHT) {
            dropdownMenu.classList.add("dropdown-menu-right");
        }
    }

    @Override
    public HTMLElement getElement() {
        return dropdown;
    }

    public enum ItemsAlignment {
        LEFT,
        RIGHT
    }
}
