/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.client.navbar;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

@ApplicationScoped
public class MenuBuilderHelper {

    public MenuItem buildMenuItem(String title, IconType icon, Command action) {
        AnchorListItem link = new AnchorListItem();
        link.setIcon(icon);

        link.getWidget(0).setStyleName("nav-item-iconic"); // Fix for IE11
        link.setTitle(title);

        link.addClickHandler(e -> action.execute());
        return new BaseMenuCustom<IsWidget>() {

            @Override
            public IsWidget build() {
                return link;
            }

            @Override
            public MenuPosition getPosition() {
                return MenuPosition.RIGHT;
            }
        };
    }

}