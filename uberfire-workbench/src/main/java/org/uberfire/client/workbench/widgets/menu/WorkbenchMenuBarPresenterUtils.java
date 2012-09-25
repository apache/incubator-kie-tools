/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.client.workbench.widgets.menu;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.security.Identity;
import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;

/**
 * Utilities for WorkbenchMenuBarPresenter to filter menu items
 */
@ApplicationScoped
public class WorkbenchMenuBarPresenterUtils {

    private final RuntimeAuthorizationManager authzManager;

    private final Identity identity;

    @Inject
    public WorkbenchMenuBarPresenterUtils(final RuntimeAuthorizationManager authzManager, final Identity identity) {
        this.authzManager = authzManager;
        this.identity = identity;
    }

    //Remove menu bar items for which there are insufficient permissions
    public List<AbstractMenuItem> filterMenuItemsByPermission(final List<AbstractMenuItem> items) {
        final List<AbstractMenuItem> itemsClone = new ArrayList<AbstractMenuItem>();
        for (AbstractMenuItem item : items) {
            final AbstractMenuItem itemClone = filterMenuItemByPermission(item);
            if (itemClone != null) {
                itemsClone.add(itemClone);
            }
        }
        return itemsClone;
    }

    //Remove menu bar items for which there are insufficient permissions
    public AbstractMenuItem filterMenuItemByPermission(final AbstractMenuItem item) {
        if (!authzManager.authorize(item, identity)) {
            return null;
        }
        if (item instanceof CommandMenuItem) {
            return item;

        } else if (item instanceof SubMenuItem) {
            final SubMenuItem subMenuItem = (SubMenuItem) item;
            final WorkbenchMenuBar menuBarClone = cloneMenuBar(filterMenuItemsByPermission(subMenuItem.getSubMenu().getItems()));
            final SubMenuItem itemClone = new SubMenuItem(subMenuItem.getCaption(),
                    menuBarClone);
            return itemClone;
        }
        throw new IllegalArgumentException("item type [" + item.getClass().getName() + "] is not recognised.");
    }

    private static WorkbenchMenuBar cloneMenuBar(final List<AbstractMenuItem> items) {
        final WorkbenchMenuBar menuBar = new WorkbenchMenuBar();
        for (AbstractMenuItem item : items) {
            menuBar.addItem(item);
        }
        return menuBar;
    }

}
