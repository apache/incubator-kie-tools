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

import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.workbench.widgets.menu.megamenu.contextmenuitem.ChildContextMenuItemPresenter;
import org.uberfire.mvp.Command;

@Templated
public class ChildContextMenuItemView implements IsElement,
                                                 ChildContextMenuItemPresenter.View {

    private ChildContextMenuItemPresenter presenter;

    @Inject
    @DataField("container")
    ListItem container;

    @Inject
    @DataField("item")
    Anchor item;

    @Override
    public void init(final ChildContextMenuItemPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setLabel(final String label) {
        item.setTextContent(label);
    }

    @Override
    public void setCommand(final Command command) {
        item.setOnclick(event -> command.execute());
    }

    @Override
    public void enable() {
        item.removeAttribute("disabled");
    }

    @Override
    public void disable() {
        item.setAttribute("disabled",
                          "disabled");
    }

    @Override
    public void pullRight() {
        container.getClassList().add("right");
    }

    @Override
    public void select() {
        item.getClassList().add("active");
    }
}
