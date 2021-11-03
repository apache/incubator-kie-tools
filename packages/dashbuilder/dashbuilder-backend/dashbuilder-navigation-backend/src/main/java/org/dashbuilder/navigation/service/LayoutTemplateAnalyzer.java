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
package org.dashbuilder.navigation.service;

import java.util.Arrays;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.NavTree;
import org.dashbuilder.navigation.layout.LayoutNavigationRef;
import org.dashbuilder.navigation.layout.LayoutNavigationRefType;
import org.dashbuilder.navigation.layout.LayoutRecursionIssue;
import org.dashbuilder.navigation.layout.LayoutTemplateContext;
import org.dashbuilder.navigation.layout.NavDragComponentType;
import org.dashbuilder.navigation.workbench.NavWorkbenchCtx;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

import static org.dashbuilder.navigation.layout.LayoutNavigationRefType.*;
import static org.dashbuilder.navigation.layout.NavDragComponentSettings.*;
import static org.dashbuilder.navigation.layout.NavDragComponentType.*;


@ApplicationScoped
public class LayoutTemplateAnalyzer {

    static final List<NavDragComponentType> ALL_NAV_GROUP_COMPONENTS = Arrays.asList(CAROUSEL, MENUBAR, TABLIST, TREE, TILES);
    static final List<NavDragComponentType> SHOW_ENTIRE_NAV_GROUP_COMPONENTS = Arrays.asList(CAROUSEL);
    static final List<NavDragComponentType> DEFAULT_ITEM_NAV_GROUP_COMPONENTS = Arrays.asList(MENUBAR, TABLIST, TREE);

    private PerspectivePluginServicesImpl pluginServices;
    private NavigationServicesImpl navigationServices;

    public LayoutTemplateAnalyzer() {
    }

    @Inject
    public LayoutTemplateAnalyzer(PerspectivePluginServicesImpl pluginServices, NavigationServicesImpl navigationServices) {
        this.pluginServices = pluginServices;
        this.navigationServices = navigationServices;
    }

    public LayoutRecursionIssue analyzeRecursion(LayoutTemplate layoutTemplate) {
        return analyzeRecursion(layoutTemplate, null);
    }

    public LayoutRecursionIssue analyzeRecursion(LayoutTemplate layoutTemplate, LayoutTemplateContext layoutCtx) {
        LayoutRecursionIssue info = new LayoutRecursionIssue();
        info.push(new LayoutNavigationRef(PERSPECTIVE, layoutTemplate.getName()));
        boolean hasIssue = analyzeRecursion(layoutTemplate, info, layoutCtx);
        if (!hasIssue) {
            info.pop();
        }
        return info;
    }

    public boolean analyzeRecursion(LayoutTemplate layoutTemplate, LayoutRecursionIssue issue, LayoutTemplateContext layoutCtx) {
        for (LayoutRow row : layoutTemplate.getRows()) {
            boolean hasIssue = analyzeRecursion(row, issue, layoutCtx);
            if (hasIssue) {
                return true;
            }
        }
        return false;
    }

    public boolean analyzeRecursion(LayoutRow row, LayoutRecursionIssue issue, LayoutTemplateContext layoutCtx) {
        NavTree navTree = navigationServices.loadNavTree();
        for (LayoutColumn column : row.getLayoutColumns()) {

            for (LayoutComponent component : column.getLayoutComponents()) {
                issue.push(new LayoutNavigationRef(NAV_COMPONENT, component.getDragTypeName()));

                // Components pointing to a perspective can cause an infinite recursion issue
                String perspectiveId = component.getProperties().get(PERSPECTIVE_ID);
                if (perspectiveId != null) {
                    boolean hasIssue = analyzeRecursion(perspectiveId, issue);
                    if (hasIssue) {
                        return true;
                    }
                }

                // Get the nav group the component is tied to
                String navGroupId = component.getProperties().get(NAV_GROUP_ID);
                String navDefaultId = component.getProperties().get(NAV_DEFAULT_ID);
                LayoutNavigationRefType navGroupRefType = NAV_GROUP_DEFINED;
                if (layoutCtx != null && layoutCtx.getNavGroupId() != null) {
                    navGroupId = layoutCtx.getNavGroupId();
                    navDefaultId = null;
                    navGroupRefType = NAV_GROUP_CONTEXT;
                }
                else {
                    LayoutNavigationRef lastDefaultItemRef = issue.getLastDefaultItemRef();
                    if (lastDefaultItemRef != null) {
                        NavItem lastDefaultItem = navTree.getItemById(lastDefaultItemRef.getName());
                        String lastDefaultGroupId = NavWorkbenchCtx.get(lastDefaultItem).getNavGroupId();
                        if (lastDefaultGroupId != null) {
                            navGroupId = lastDefaultGroupId;
                            navDefaultId = null;
                            navGroupRefType = NAV_GROUP_CONTEXT;
                        }
                    }
                }
                NavGroup navGroup = navTree != null ? (NavGroup) navTree.getItemById(navGroupId) : null;
                if (navGroup != null) {

                    // The configured default item can cause an infinite recursion issue
                    if (navDefaultId != null) {
                        issue.push(new LayoutNavigationRef(navGroupRefType, navGroup.getId()));
                        issue.push(new LayoutNavigationRef(DEFAULT_ITEM_DEFINED, navDefaultId));
                        NavItem defaultItem = navTree.getItemById(navDefaultId);
                        NavWorkbenchCtx navCtx = NavWorkbenchCtx.get(defaultItem);
                        perspectiveId = navCtx.getResourceId();
                        boolean hasIssue = analyzeRecursion(perspectiveId, issue);
                        if (hasIssue) {
                            return true;
                        } else {
                            issue.pop();
                            issue.pop();
                        }
                    }
                    // For some components the first available item is taken when there is no default item set
                    else if (hasDefaultItem(component)) {
                        NavItem firstItem = getFirstRuntimePerspective(navGroup.getChildren());
                        if (firstItem != null) {
                            issue.push(new LayoutNavigationRef(navGroupRefType, navGroup.getId()));
                            issue.push(new LayoutNavigationRef(DEFAULT_ITEM_FOUND, firstItem.getId()));
                            perspectiveId = NavWorkbenchCtx.get(firstItem).getResourceId();
                            boolean hasIssue = analyzeRecursion(perspectiveId, issue);
                            if (hasIssue) {
                                return true;
                            } else {
                                issue.pop();
                                issue.pop();
                            }
                        }
                    }

                    // Any layout component linked to a nav group can potentially lead to an infinite recursion issue.
                    boolean showAtOnce = showEntireNavGroup(component);
                    if (navGroupId != null && showAtOnce) {
                        boolean hasIssue = analyzeRecursion(navGroup, navGroupRefType, issue);
                        if (hasIssue) {
                            return true;
                        }
                    }
                }
                issue.pop();
            }

            for (LayoutRow childRow : column.getRows()) {
                boolean hasIssue = analyzeRecursion(childRow, issue, layoutCtx);
                if (hasIssue) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean analyzeRecursion(NavGroup navGroup, LayoutNavigationRefType navGroupRefType, LayoutRecursionIssue issue) {
        if (navGroup == null) {
            return false;
        }
        issue.push(new LayoutNavigationRef(navGroupRefType, navGroup.getId()));
        for (NavItem navItem : navGroup.getChildren()) {
            NavWorkbenchCtx navCtx = NavWorkbenchCtx.get(navItem);
            String perspectiveId = navCtx.getResourceId();
            boolean hasIssue = analyzeRecursion(perspectiveId, issue);
            if (hasIssue) {
                return true;
            }
        }
        issue.pop();
        return false;
    }

    public boolean analyzeRecursion(String perspectiveId, LayoutRecursionIssue issue) {
        LayoutTemplate layoutTemplate = pluginServices.getLayoutTemplate(perspectiveId);
        if (layoutTemplate != null) {

            // An infinite recursion occurs either when the perspective is linked to an already
            // traversed perspective or when the layout itself is causing so.

            LayoutNavigationRef perspectiveRef = new LayoutNavigationRef(PERSPECTIVE, perspectiveId);
            if (issue.contains(perspectiveRef)) {
                issue.push(new LayoutNavigationRef(PERSPECTIVE, perspectiveId));
                return true;
            }
            else {
                issue.push(new LayoutNavigationRef(PERSPECTIVE, perspectiveId));
                boolean hasIssue = analyzeRecursion(layoutTemplate, issue, null);
                if (hasIssue) {
                    return true;
                } else {
                    issue.pop();
                }
            }
        }
        return false;
    }

    public boolean hasNavigationComponents(LayoutTemplate layoutTemplate) {
        for (LayoutRow row : layoutTemplate.getRows()) {
            boolean hasNavComps = hasNavigationComponents(row);
            if (hasNavComps) {
                return true;
            }
        }
        return false;
    }

    public boolean hasNavigationComponents(LayoutRow row) {
        for (LayoutColumn column : row.getLayoutColumns()) {

            for (LayoutComponent component : column.getLayoutComponents()) {
                NavDragComponentType dragType = NavDragComponentType.getByClassName(component.getDragTypeName());
                if (dragType != null && ALL_NAV_GROUP_COMPONENTS.contains(dragType)) {
                    return true;
                }
            }

            for (LayoutRow childRow : column.getRows()) {
                boolean hasNavComps = hasNavigationComponents(childRow);
                if (hasNavComps) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean showEntireNavGroup(LayoutComponent component) {
        NavDragComponentType dragType = NavDragComponentType.getByClassName(component.getDragTypeName());
        return dragType != null && SHOW_ENTIRE_NAV_GROUP_COMPONENTS.contains(dragType);
    }

    protected boolean hasDefaultItem(LayoutComponent component) {
        NavDragComponentType dragType = NavDragComponentType.getByClassName(component.getDragTypeName());
        return dragType != null && DEFAULT_ITEM_NAV_GROUP_COMPONENTS.contains(dragType);
    }

    protected boolean isRuntimePerspectiveId(NavItem navItem) {
        NavWorkbenchCtx navCtx = NavWorkbenchCtx.get(navItem);
        String perspectiveId = navCtx.getResourceId();
        return pluginServices.getLayoutTemplate(perspectiveId) != null;
    }

    protected NavItem getFirstRuntimePerspective(List<NavItem> itemList) {
        if (itemList.isEmpty()) {
            return null;
        }
        for (NavItem navItem : itemList) {
            if (isRuntimePerspectiveId(navItem)) {
                return navItem;
            }
            if (navItem instanceof NavGroup) {
                NavItem result = getFirstRuntimePerspective(((NavGroup) navItem).getChildren());
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
}
