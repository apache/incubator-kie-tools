/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.views.pfly.menu.megamenu.contextmenuitem;

import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.workbench.widgets.menu.megamenu.contextmenuitem.GroupContextMenuItemPresenter;

@Templated
public class GroupContextMenuItemView implements org.jboss.errai.ui.client.local.api.IsElement,
                                                 GroupContextMenuItemPresenter.View {

    private GroupContextMenuItemPresenter presenter;

    @Inject
    @DataField("container")
    ListItem container;

    @Inject
    @DataField("dropdown")
    Anchor dropdown;

    @Inject
    @DataField("items")
    UnorderedList items;

    @Override
    public void init(final GroupContextMenuItemPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setLabel(final String label) {
        this.dropdown.setTextContent(label);
    }

    @Override
    public void addItem(final IsElement item) {
        this.items.appendChild(item.getElement());
    }

    @Override
    public void enable() {
        dropdown.removeAttribute("disabled");
    }

    @Override
    public void disable() {
        dropdown.setAttribute("disabled",
                              "disabled");
    }

    @Override
    public void pullRight() {
        container.getClassList().add("right");
    }
}
