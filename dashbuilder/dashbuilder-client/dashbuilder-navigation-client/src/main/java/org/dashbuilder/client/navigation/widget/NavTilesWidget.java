/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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
package org.dashbuilder.client.navigation.widget;

import java.util.List;
import java.util.Stack;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.dashbuilder.client.navigation.NavigationManager;
import org.dashbuilder.client.navigation.plugin.PerspectivePluginManager;
import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.layout.LayoutRecursionIssue;
import org.dashbuilder.navigation.layout.LayoutRecursionIssueI18n;
import org.dashbuilder.navigation.layout.LayoutTemplateContext;
import org.dashbuilder.navigation.workbench.NavWorkbenchCtx;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.plugin.event.PluginSaved;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.ActivityResourceType;

/**
 * A navigation widget that displays a set of navigation items using a navigable tile based approach where
 * {@link NavGroup} instances are displayed as folders and {@link NavItem} are shown as links to a specific
 * target asset (f.i: a perspective).
 */
@Dependent
public class NavTilesWidget extends BaseNavWidget {

    public interface View extends NavWidgetView<NavTilesWidget>, LayoutRecursionIssueI18n {

        void addTileWidget(IsElement tileWidget);

        void showTileContent(IsWidget tileContent);

        void clearBreadcrumb();

        void addBreadcrumbItem(String navItemName);

        void addBreadcrumbItem(String navItemName, Command onClicked);

        void infiniteRecursionError(String cause);
    }

    View view;
    PerspectivePluginManager perspectivePluginManager;
    PlaceManager placeManager;
    SyncBeanManager beanManager;
    NavItem currentPerspectiveNavItem = null;
    Stack<NavItem> navItemStack = new Stack<>();

    @Inject
    public NavTilesWidget(View view,
                          NavigationManager navigationManager,
                          PerspectivePluginManager perspectivePluginManager,
                          PlaceManager placeManager,
                          SyncBeanManager beanManager) {
        super(view, navigationManager);
        this.view = view;
        this.perspectivePluginManager = perspectivePluginManager;
        this.placeManager = placeManager;
        this.beanManager = beanManager;
    }

    public Stack<NavItem> getNavItemStack() {
        return navItemStack;
    }

    @Override
    public void show(NavGroup navGroup) {
        this.show(navGroup, true);
    }

    @Override
    public void show(List<NavItem> itemList) {
        currentPerspectiveNavItem = null;
        super.show(itemList);
    }

    public void show(NavGroup navGroup, boolean clearBreadcrumb) {
        if (navGroup == null) {
            view.errorNavGroupNotFound();
        }
        else {
            NavGroup clone = (NavGroup) navGroup.cloneItem();
            clone.setParent(null);

            if (clearBreadcrumb) {
                navItemStack.clear();
                updateBreadcrumb();
            }
            currentPerspectiveNavItem = null;
            super.show(clone);
        }
    }

    @Override
    protected void showItem(NavItem navItem) {
        NavItemTileWidget tileWidget = beanManager.lookupBean(NavItemTileWidget.class).getInstance();
        tileWidget.setOnClick(() -> this.openItem(navItem));
        tileWidget.show(navItem);
        view.addTileWidget(tileWidget);
    }

    @Override
    protected void showGroup(NavGroup navGroup) {
        showItem(navGroup);
    }

    public void openItem(NavItem navItem) {
        NavItem parent = navItem.getParent();
        if (navItemStack.isEmpty()) {
            if (parent != null) {
                navItemStack.add(parent);
                navItemStack.add(navItem);
            }
        } else {
            navItemStack.add(navItem);
        }
        this.updateBreadcrumb();

        if (navItem instanceof NavGroup) {
            this.show((NavGroup) navItem, false);
        }
        else {
            NavWorkbenchCtx navCtx = NavWorkbenchCtx.get(navItem);
            String resourceId = navCtx.getResourceId();
            if (resourceId != null && ActivityResourceType.PERSPECTIVE.equals(navCtx.getResourceType())) {

                // Runtime perspectives are displayed inline
                if (perspectivePluginManager.isRuntimePerspective(resourceId)) {
                    openPerspective(navItem);
                }
                // Classic UF perspectives take over the entire window
                else {
                    placeManager.goTo(resourceId);
                }
            }
        }
    }

    protected void openPerspective(NavItem perspectiveItem) {
        NavWorkbenchCtx navCtx = NavWorkbenchCtx.get(perspectiveItem);
        String perspectiveId = navCtx.getResourceId();
        String navRootId = navCtx.getNavGroupId();
        currentPerspectiveNavItem = perspectiveItem;
        LayoutTemplateContext layoutCtx = new LayoutTemplateContext(navRootId);
        perspectivePluginManager.buildPerspectiveWidget(perspectiveId, layoutCtx, view::showTileContent, this::onInfiniteRecursion);
    }

    public void onInfiniteRecursion(LayoutRecursionIssue issue) {
        String cause = issue.printReport(navigationManager.getNavTree(), view);
        view.infiniteRecursionError(cause);
    }

    protected void updateBreadcrumb() {
        view.clearBreadcrumb();
        for (int i=0; i<navItemStack.size(); i++) {
            final NavItem navItem = navItemStack.get(i);
            if (i == navItemStack.size()-1) {
                view.addBreadcrumbItem(navItem.getName());
            } else {
                view.addBreadcrumbItem(navItem.getName(), () -> gotoBreadcrumbItem(navItem));
            }
        }
    }

    public void gotoBreadcrumbItem(NavItem navItem) {
        while (navItemStack.peek() != navItem) {
            navItemStack.pop();
        }
        // Re-open the item
        if (!navItemStack.isEmpty()) {
            navItemStack.pop();
        }
        openItem(navItem);
    }

    // Catch changes on runtime perspectives so as to display the most up to date changes

    private void onPerspectiveChanged(@Observes PluginSaved event) {
        if (currentPerspectiveNavItem != null) {
            NavWorkbenchCtx navCtx = NavWorkbenchCtx.get(currentPerspectiveNavItem);
            String perspectiveId = navCtx.getResourceId();
            if (event.getPlugin().getName().equals(perspectiveId)) {
                openPerspective(currentPerspectiveNavItem);
            }
        }
    }
}