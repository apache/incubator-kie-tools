/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.navigation.workbench;

import java.util.Iterator;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.navigation.NavDivider;
import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.NavTree;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.ActivityResourceType;

/**
 * A security interface for controlling access {@link NavTree} and {@link NavItem} instances.
 */
@ApplicationScoped
public class NavSecurityController {

    private AuthorizationManager authorizationManager;
    private User user;

    @Inject
    public NavSecurityController(AuthorizationManager authorizationManager, User user) {
        this.authorizationManager = authorizationManager;
        this.user = user;
    }

    public NavTree secure(NavTree navTree, boolean removeEmptyGroups) {
        NavTree clone = navTree.cloneTree();
        secure(clone.getRootItems(), removeEmptyGroups);
        return clone;
    }

    public void secure(List<NavItem> itemList, boolean removeEmptyGroups) {
        Iterator<NavItem> it = itemList.iterator();
        while (it.hasNext()) {
            NavItem navItem = it.next();
            if (!canRead(navItem)) {
                it.remove();
            } else if (navItem instanceof NavGroup) {
                List<NavItem> children = ((NavGroup) navItem).getChildren();
                if (removeEmptyGroups) {
                    removeEmptyGroups(children);
                }
                secure(children, removeEmptyGroups);
            }
        }
        if (removeEmptyGroups) {
            removeEmptyGroups(itemList);
        }
    }

    private void removeEmptyGroups(List<NavItem> itemList) {
        Iterator<NavItem> it = itemList.iterator();
        while (it.hasNext()) {
            NavItem navItem = it.next();
            if (navItem instanceof NavGroup) {
                if (isEmpty((NavGroup) navItem)) {
                    it.remove();
                }
            }
        }
    }

    private boolean isEmpty(NavGroup navGroup) {
        for (NavItem navItem : navGroup.getChildren()) {

            // Group found => non empty if subgroup non empty as well.
            if (navItem instanceof NavGroup) {
                if (!isEmpty((NavGroup) navItem)) {
                    return false;
                }
            }
            // Item found => non empty
            else if (!(navItem instanceof NavDivider)) {
                return false;
            }
        }
        return true;
    }

    public boolean canRead(String navItemCtx) {
        NavWorkbenchCtx ctx = NavWorkbenchCtx.get(navItemCtx);

        // Check permissions
        for (String p : ctx.getPermissions()) {
            if (!authorizationManager.authorize(p, user)) {
                return false;
            }
        }
        // Check resource access
        String resourceId = ctx.getResourceId();
        ActivityResourceType resourceType = ctx.getResourceType();
        if (resourceId != null && resourceType != null) {
            ResourceRef resourceRef = new ResourceRef(resourceId, resourceType);
            if (!authorizationManager.authorize(resourceRef, user)) {
                return false;
            }
        }
        // The item is available
        return true;
    }

    public boolean canRead(NavItem navItem) {
        if (navItem == null) {
            return false;
        }
        return canRead(navItem.getContext());
    }
}