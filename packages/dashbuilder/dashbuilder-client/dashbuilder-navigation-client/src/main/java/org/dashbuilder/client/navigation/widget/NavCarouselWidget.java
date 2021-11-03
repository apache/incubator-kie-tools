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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.dashbuilder.client.navigation.NavigationManager;
import org.dashbuilder.client.navigation.plugin.PerspectivePluginManager;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.layout.LayoutRecursionIssue;
import org.dashbuilder.navigation.layout.LayoutRecursionIssueI18n;
import org.uberfire.ext.plugin.event.PluginSaved;
import org.uberfire.ext.plugin.model.Plugin;

@Dependent
public class NavCarouselWidget extends BaseNavWidget implements HasDefaultNavItem {

    public interface View extends NavWidgetView<NavCarouselWidget>, LayoutRecursionIssueI18n {

        void addContentSlide(IsWidget widget);

        void infiniteRecursionError(String cause);
    }

    View view;
    PerspectivePluginManager perspectivePluginManager;
    List<String> perspectiveIds = new ArrayList<>();
    String defaultNavItemId = null;

    @Inject
    public NavCarouselWidget(View view, NavigationManager navigationManager, PerspectivePluginManager perspectivePluginManager) {
        super(view, navigationManager);
        this.view = view;
        this.perspectivePluginManager = perspectivePluginManager;
        super.setMaxLevels(1);
    }

    @Override
    public boolean areSubGroupsSupported() {
        return false;
    }

    @Override
    public String getDefaultNavItemId() {
        return defaultNavItemId;
    }

    @Override
    public void setDefaultNavItemId(String defaultNavItemId) {
        this.defaultNavItemId = defaultNavItemId;
    }

    @Override
    public void show(List<NavItem> itemList) {
        // Discard everything but runtime perspectives
        List<NavItem> itemsFiltered = itemList.stream()
                .filter(perspectivePluginManager::isRuntimePerspective)
                .collect(Collectors.toList());

        // Get the default item configured (if any)
        NavItem defaultNavItem = null;
        if (defaultNavItemId != null) {
            for (NavItem navItem : itemsFiltered) {
                if (defaultNavItemId.equals(navItem.getId())) {
                    defaultNavItem = navItem;
                }
            }
        }
        // Place the default item at the beginning of the carousel
        if (defaultNavItem != null) {
            itemsFiltered.remove(defaultNavItem);
            itemsFiltered.add(0, defaultNavItem);
        }

        perspectiveIds.clear();
        super.show(itemsFiltered);
    }

    @Override
    protected void showItem(NavItem navItem) {
        // Only runtime perspectives can be displayed
        String perspectiveId = perspectivePluginManager.getRuntimePerspectiveId(navItem);
        if (perspectiveId != null) {
            perspectiveIds.add(perspectiveId);
            perspectivePluginManager.buildPerspectiveWidget(perspectiveId, view::addContentSlide, this::onInfiniteRecursion);
        }
    }

    public void onInfiniteRecursion(LayoutRecursionIssue issue) {
        String cause = issue.printReport(navigationManager.getNavTree(), view);
        view.infiniteRecursionError(cause);
    }

    // Catch changes on runtime perspectives so as to display the most up to date changes

    private void onPerspectiveChanged(@Observes PluginSaved event) {
        Plugin plugin = event.getPlugin();
        String pluginName = plugin.getName();
        if (perspectiveIds.contains(pluginName)) {
            super.refresh();
        }
    }
}