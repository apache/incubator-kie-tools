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

package org.uberfire.client.workbench.widgets.menu.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.client.workbench.widgets.menu.HasMenus;
import org.uberfire.client.workbench.widgets.menu.MenuItemVisibilityHandler;
import org.uberfire.client.workbench.widgets.menu.events.PerspectiveVisibiltiyChangeEvent;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import static org.uberfire.plugin.PluginUtil.ensureIterable;

public abstract class WorkbenchBaseMenuPresenter implements HasMenus {

    private List<MenuItemVisibilityHandler> visibilityHandlers;
    private List<Menus> addedMenus;

    protected abstract WorkbenchBaseMenuView getBaseView();

    protected abstract void visitMenus(final Menus menus);

    public abstract void onPerspectiveChange(final PerspectiveChange perspectiveChange);

    @Override
    public void addMenus(final Menus menus) {
        if (menus != null && !menus.getItems().isEmpty()) {

            if (addedMenus == null) {
                addedMenus = new ArrayList<>();
            }

            if (visibilityHandlers == null) {
                visibilityHandlers = new ArrayList<>();
            }

            addedMenus.add(menus);

            if (menusMustBeReordered(menus)) {
                reorderMenus();
                getBaseView().clear();

                for (Menus currentMenus : addedMenus) {
                    visitMenus(currentMenus);
                }
            } else {
                visitMenus(menus);
            }
        }
    }

    private boolean menusMustBeReordered(final Menus menus) {
        if (addedMenus.size() < 2) {
            return false;
        }

        final Menus previousMenus = addedMenus.get(addedMenus.size() - 2);
        return previousMenus.getOrder() > menus.getOrder();
    }

    private void reorderMenus() {
        Collections.sort(addedMenus,
                         (o1, o2) -> o1.getOrder() - o2.getOrder());
    }

    public List<Menus> getAddedMenus() {
        return this.addedMenus;
    }

    //Force UI to update to state of MenuItems. Should be called after MenuItems are configured with EnabledStateChangeListener's.
    protected void synchronizeUIWithMenus(final List<MenuItem> menuItems) {
        for (MenuItem menuItem : ensureIterable(menuItems)) {
            if (menuItem instanceof MenuGroup) {
                synchronizeUIWithMenus(((MenuGroup) menuItem).getItems());
            } else {
                menuItem.setEnabled(menuItem.isEnabled());
            }
        }
    }

    protected void registerVisibilityChangeHandler(MenuItemVisibilityHandler handler) {
        visibilityHandlers.add(handler);
    }

    public void onPerspectiveVisibilityChange(PerspectiveVisibiltiyChangeEvent event) {
        if (visibilityHandlers != null) {
            visibilityHandlers.stream()
                    .filter(handler -> handler.getIdentifier().equals(event.getPerspectiveId()))
                    .forEach(handler -> handler.run(event.isVisible()));
        }
    }
}
