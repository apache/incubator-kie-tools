/*
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.util.dropdown;

import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLLIElement;
import elemental2.dom.HTMLUListElement;
import elemental2.dom.MouseEvent;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.widgets.client.widget.ListItemView;

@Templated
public class KieDropdownElementView implements KieDropdownElement.View,
                                               IsElement {

    @Inject
    @DataField("root")
    private HTMLDivElement root;

    @Inject
    @DataField("dropdown-menu")
    private HTMLUListElement ul;

    @Inject
    @DataField("toggle")
    private HTMLAnchorElement toggle;

    @Inject
    @Named("span")
    @DataField("selected-label")
    private HTMLElement selectedLabel;

    private KieDropdownElement presenter;

    @Override
    public void init(final KieDropdownElement presenter) {
        this.presenter = presenter;
    }

    @Override
    public HTMLUListElement getUl() {
        return ul;
    }

    @Override
    public void setSelectedLabel(final String label) {
        selectedLabel.textContent = label;
    }

    @Templated("KieDropdownElementView.html")
    public static class Item<T> implements ListItemView<KieDropdownElement.ItemElement<T>>,
                                           IsElement {

        @Inject
        @DataField("option")
        private HTMLLIElement option;

        @Inject
        @DataField("option-link")
        private HTMLAnchorElement optionLink;

        @Inject
        @Named("span")
        @DataField("option-label")
        private HTMLElement optionLabel;

        @Inject
        @Named("span")
        @DataField("option-check")
        private HTMLElement optionCheck;

        @Inject
        @DataField("separator")
        private HTMLLIElement separator;

        @Inject
        @DataField("action")
        private HTMLLIElement action;

        @Inject
        @DataField("action-link")
        private HTMLAnchorElement actionLink;

        @Inject
        @Named("span")
        @DataField("action-label")
        private HTMLElement actionLabel;

        private KieDropdownElement.ItemElement<T> presenter;

        @EventHandler("option-link")
        public void onOptionClicked(final @ForEvent("click") MouseEvent e) {
            presenter.onClick();
        }

        @EventHandler("action-link")
        public void onActionClicked(final @ForEvent("click") MouseEvent e) {
            presenter.onClick();
        }

        @Override
        public void init(final KieDropdownElement.ItemElement<T> presenter) {
            this.presenter = presenter;
        }

        public void setLabel(final String label) {
            this.optionLabel.textContent = label;
            this.actionLabel.textContent = label;
        }

        public void setStatus(final KieDropdownElement.Item.Status status) {
            if (KieDropdownElement.Item.Status.UNCHECKED.equals(status)) {
                optionCheck.classList.add("hidden");
            }
        }

        @Override
        public HTMLElement getElement() {
            if (presenter.getObject().type.equals(KieDropdownElement.Item.Type.OPTION)) {
                return option;
            } else if (presenter.getObject().type.equals(KieDropdownElement.Item.Type.SEPARATOR)) {
                return separator;
            } else if (presenter.getObject().type.equals(KieDropdownElement.Item.Type.ACTION)) {
                return action;
            }

            throw new RuntimeException("Unsupported item type.");
        }
    }
}
