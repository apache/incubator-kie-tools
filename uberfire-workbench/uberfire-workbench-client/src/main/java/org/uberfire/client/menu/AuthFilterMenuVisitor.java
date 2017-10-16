/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.menu;

import java.util.List;

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.authz.ResourceActionRef;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemCommand;
import org.uberfire.workbench.model.menu.MenuItemPerspective;
import org.uberfire.workbench.model.menu.MenuItemPlain;
import org.uberfire.workbench.model.menu.MenuVisitor;
import org.uberfire.workbench.model.menu.Menus;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.uberfire.plugin.PluginUtil.ensureIterable;

/**
 * Wraps a menu visitor, filtering out menu items that a given user is not allowed to access. The wrapped visitor only
 * sees the items that the user is allowed to see.
 */
public class AuthFilterMenuVisitor implements MenuVisitor {

    private final AuthorizationManager authzManager;
    private final User user;
    private final MenuVisitor chainedVisitor;

    /**
     * Wraps the given menu visitor, only forwarding calls that represent menu items the given user is allowed to see.
     *
     * @param authzManager   The authorization manager that decides what is visible. Not null.
     * @param user           The user who will see the menus being visited. Not null.
     * @param chainedVisitor The menu visitor that receives calls for all authorized parts of the menu tree. Not null.
     */
    public AuthFilterMenuVisitor(AuthorizationManager authzManager,
                                 User user,
                                 MenuVisitor chainedVisitor) {
        this.authzManager = checkNotNull("authzManager",
                                         authzManager);
        this.user = checkNotNull("user",
                                 user);
        this.chainedVisitor = checkNotNull("chainedVisitor",
                                           chainedVisitor);
    }

    @Override
    public boolean visitEnter(Menus menus) {
        return chainedVisitor.visitEnter(menus);
    }

    @Override
    public void visitLeave(Menus menus) {
        chainedVisitor.visitLeave(menus);
    }

    @Override
    public boolean visitEnter(MenuGroup menuGroup) {
        if (!authorize(menuGroup)) {
            return false;
        }
        return chainedVisitor.visitEnter(menuGroup);
    }

    @Override
    public void visitLeave(MenuGroup menuGroup) {
        chainedVisitor.visitLeave(menuGroup);
    }

    @Override
    public void visit(MenuItemPlain menuItemPlain) {
        if (authorize(menuItemPlain)) {
            chainedVisitor.visit(menuItemPlain);
        }
    }

    @Override
    public void visit(MenuItemCommand menuItemCommand) {
        if (authorize(menuItemCommand)) {
            chainedVisitor.visit(menuItemCommand);
        }
    }

    @Override
    public void visit(MenuCustom<?> menuCustom) {
        if (authorize(menuCustom)) {
            chainedVisitor.visit(menuCustom);
        }
    }

    @Override
    public void visit(MenuItemPerspective menuItemPerspective) {
        if (authorize(menuItemPerspective)) {
            chainedVisitor.visit(menuItemPerspective);
        }
    }

    /**
     * Check the user is allowed to access the given menu item.
     * <p>
     * <p>If the item has any references to resource actions {@link ResourceActionRef} or custom permissions
     * then the access is granted provided all those references are also granted.</p>
     */
    public boolean authorize(MenuItem item) {
        List<ResourceActionRef> actions = item.getResourceActions();
        if (actions != null && !actions.isEmpty()) {
            for (ResourceActionRef ref : ensureIterable(actions)) {
                if (!authzManager.authorize(ref.getResource(),
                                            ref.getAction(),
                                            user)) {
                    return false;
                }
            }
        }
        List<String> permissions = ensureIterable(item.getPermissions());
        if (permissions != null && !permissions.isEmpty()) {
            for (String p : permissions) {
                if (!authzManager.authorize(p,
                                            user)) {
                    return false;
                }
            }
        }
        // Check the item
        boolean itemResult = authzManager.authorize(item,
                                                    user);
        boolean denied = false;

        // For menu groups ensure at least one child item can be accessed
        if (item instanceof MenuGroup) {
            MenuGroup group = (MenuGroup) item;
            for (MenuItem child : ensureIterable(group.getItems())) {
                if (authorize(child)) {
                    return itemResult;
                } else {
                    denied = true;
                }
            }
        }
        return itemResult && !denied;
    }
}
