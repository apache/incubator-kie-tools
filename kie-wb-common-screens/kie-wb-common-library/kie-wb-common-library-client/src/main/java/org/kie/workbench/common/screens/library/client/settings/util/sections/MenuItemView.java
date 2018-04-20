/*
 * Copyright (C) 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.util.sections;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class MenuItemView<T> implements MenuItem.View<T> {

    @Inject
    @Named("sup")
    private HTMLElement dirtyIndicator;

    @Inject
    @DataField("section-menu-item-link")
    private HTMLAnchorElement sectionMenuItemLink;

    private MenuItem presenter;

    @Override
    public void init(final MenuItem<T> presenter) {
        this.presenter = presenter;
    }

    @EventHandler("section-menu-item-link")
    public void onSectionMenuItemLinkClicked(final ClickEvent ignore) {
        presenter.showSection();
    }

    @Override
    public void setLabel(final String label) {
        sectionMenuItemLink.textContent = label;
    }

    @Override
    public void markAsDirty(final boolean dirty) {
        if (dirty && sectionMenuItemLink.childElementCount == 0) {
            sectionMenuItemLink.appendChild(newDirtyIndicator());
        } else if (!dirty && sectionMenuItemLink.childElementCount > 0) {
            sectionMenuItemLink.removeChild(sectionMenuItemLink.lastElementChild);
        }
    }

    @Override
    public void setActive() {
        getElement().classList.add("active");
    }

    private HTMLElement newDirtyIndicator() {
        final HTMLElement dirtyIndicator = (HTMLElement) this.dirtyIndicator.cloneNode(false);
        dirtyIndicator.textContent = " *";
        return dirtyIndicator;
    }
}
