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

import java.util.ArrayList;
import java.util.List;

import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.NavItemContext;
import org.dashbuilder.navigation.impl.NavItemContextImpl;
import org.uberfire.workbench.model.ActivityResourceType;

/**
 * A {@link NavItemContext} which contains workbench related context like for instance:
 * <ul>
 *     <li>A list of permissions the nav item is tied to</li>
 *     <li>An identifier of a resource this item is referring to</li>
 * </ul>
 */
public class NavWorkbenchCtx extends NavItemContextImpl {

    public static final String PERMISSIONS = "permissions";
    public static final String RESOURCE_ID = "resourceId";
    public static final String RESOURCE_TYPE = "resourceType";
    public static final String NAV_GROUP_ID = "navGroupId";

    public static NavWorkbenchCtx get(NavItem navItem) {
        return navItem != null ? new NavWorkbenchCtx(navItem.getContext()): new NavWorkbenchCtx();
    }

    public static NavWorkbenchCtx get(String navItemCtx) {
        return new NavWorkbenchCtx(navItemCtx);
    }

    public static NavWorkbenchCtx perspective(String perspectiveId) {
        NavWorkbenchCtx ctx = new NavWorkbenchCtx();
        ctx.setResourceId(perspectiveId);
        ctx.setResourceType(ActivityResourceType.PERSPECTIVE);
        return ctx;
    }

    public static NavWorkbenchCtx permission(String... permission) {
        NavWorkbenchCtx ctx = new NavWorkbenchCtx();
        for (String p : permission) {
            ctx.addPermission(p);
        }
        return ctx;
    }

    public NavWorkbenchCtx() {
        super();
    }

    public NavWorkbenchCtx(String ctx) {
        super(ctx);
    }

    public String getResourceId() {
        return super.getProperty(RESOURCE_ID);
    }

    public NavWorkbenchCtx setResourceId(String resourceId) {
        if (resourceId == null) {
            super.removeProperty(RESOURCE_ID);
        } else {
            super.setProperty(RESOURCE_ID, resourceId);
        }
        return this;
    }

    public ActivityResourceType getResourceType() {
        String type = super.getProperty(RESOURCE_TYPE);
        return type != null ? ActivityResourceType.valueOf(type.toUpperCase()) : null;
    }

    public NavWorkbenchCtx setResourceType(ActivityResourceType resourceType) {
        super.setProperty(RESOURCE_TYPE, resourceType.getName().toUpperCase());
        return this;
    }

    public String getNavGroupId() {
        return super.getProperty(NAV_GROUP_ID);
    }

    public NavWorkbenchCtx setNavGroupId(String navGroupId) {
        if (navGroupId == null) {
            super.removeProperty(NAV_GROUP_ID);
        } else {
            super.setProperty(NAV_GROUP_ID, navGroupId);
        }
        return this;
    }

    public NavWorkbenchCtx clearPermissions() {
        super.removeProperty(PERMISSIONS);
        return this;
    }

    public List<String> getPermissions() {
        List<String> permissionList = new ArrayList<>();
        String str = super.getProperty(PERMISSIONS);
        if (str != null) {
            for (String p : str.split(",")) {
                permissionList.add(p.trim());
            }
        }
        return permissionList;
    }

    public NavWorkbenchCtx addPermission(String permission) {
        String str = super.getProperty(PERMISSIONS);
        str = str == null ? permission : str + "," + permission;
        super.setProperty(PERMISSIONS, str);
        return this;
    }
}
