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
package org.dashbuilder.client.navigation.widget;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.client.navigation.NavigationManager;
import org.dashbuilder.navigation.event.NavTreeChangedEvent;
import org.dashbuilder.navigation.NavDivider;
import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.NavTree;
import org.uberfire.ext.security.management.client.widgets.management.events.SaveGroupEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.SaveRoleEvent;
import org.uberfire.mvp.Command;

public abstract class BaseNavWidget implements NavWidget {

    NavigationManager navigationManager;
    boolean secure = true;
    boolean hideEmptyGroups = true;
    Command onItemSelectedCommand;
    Command onStaleCommand;
    NavItem itemSelected;
    NavGroup navGroup;
    NavWidget parent;
    NavWidgetView view;
    int maxLevels = -1;
    List<NavItem> navItemList = null;
    NavWidget activeNavSubgroup = null;
    List<NavWidget> navSubgroupList = new ArrayList<>();

    @Inject
    public BaseNavWidget(NavWidgetView view, NavigationManager navigationManager) {
        this.view = view;
        this.navigationManager = navigationManager;
        view.init(this);
    }

    public NavigationManager getNavigationManager() {
        return navigationManager;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    @Override
    public void setHideEmptyGroups(boolean hide) {
        this.hideEmptyGroups = hide;
    }

    @Override
    public void setOnItemSelectedCommand(Command onItemSelected) {
        this.onItemSelectedCommand = onItemSelected;
    }

    @Override
    public void setOnStaleCommand(Command onStaleCommand) {
        this.onStaleCommand = onStaleCommand;
    }

    public NavItem getItem(String id) {
        if (navItemList == null || id == null) {
            return null;
        }
        for (NavItem navItem : navItemList) {
            if (id.equals(navItem.getId())) {
                return navItem;
            }
        }
        return null;
    }

    @Override
    public int getLevel() {
        int level = 0;
        NavWidget root = parent;
        while (root != null) {
            level++;
            root = root.getParent();
        }
        return level;
    }

    @Override
    public NavWidget getParent() {
        return parent;
    }

    @Override
    public void setParent(NavWidget parent) {
        this.parent = parent;
    }

    @Override
    public int getMaxLevels() {
        return maxLevels;
    }

    @Override
    public void setMaxLevels(int maxLevels) {
        this.maxLevels = maxLevels;
    }

    @Override
    public NavGroup getNavGroup() {
        return navGroup;
    }

    public boolean areSubGroupsSupported() {
        return maxLevels < 1 || getLevel() < maxLevels-1;
    }

    protected NavWidget getSubgroupNavWidget(String groupId) {
        for (NavWidget navWidget : navSubgroupList) {
            if (navWidget.getNavGroup().getId().equals(groupId)) {
                return navWidget;
            }
        }
        return null;
    }

    protected NavWidget lookupNavGroupWidget() {
        return null;
    }

    @Override
    public void hide() {
        view.clearItems();
        navSubgroupList.forEach(NavWidget::hide);
    }

    @Override
    public void show(NavGroup navGroup) {
        this.navGroup = navGroup;
        if (navGroup == null) {
            view.errorNavGroupNotFound();
        } else {
            this.show(navGroup.getChildren());
        }
    }

    @Override
    public void show(NavTree navTree) {
        if (navTree == null) {
            view.errorNavItemsEmpty();
        } else {
            this.show(navTree.getRootItems());
        }
    }

    @Override
    public void show(List<NavItem> itemList) {
        this.hide();

        this.navItemList = itemList;
        this.navSubgroupList.clear();

        // Make sure the items shown comply with the authz policy defined
        if (secure) {
            navItemList = new ArrayList<>(itemList);
            navigationManager.secure(navItemList, hideEmptyGroups);
        }

        if (navItemList.isEmpty()) {
            view.errorNavItemsEmpty();
        }

        for (NavItem navChild : navItemList) {

            // A subgroup
            if (navChild instanceof NavGroup) {

                // Ensure to not exceed the maximum number of levels
                if (areSubGroupsSupported()) {
                    showGroup((NavGroup) navChild);
                } else {
                    showItem(navChild);
                }
            }
            // A divider
            else if (navChild instanceof NavDivider) {
                view.addDivider();
            }
            // A regular item
            else {
                showItem(navChild);
            }
        }
    }

    protected void showGroup(NavGroup navGroup) {
        NavWidget subGroupNavWidget = lookupNavGroupWidget();
        if (subGroupNavWidget != null) {
            subGroupNavWidget.setParent(this);
            subGroupNavWidget.setMaxLevels(maxLevels > 0 ? maxLevels - 1 : -1);
            subGroupNavWidget.setSecure(secure);
            subGroupNavWidget.setHideEmptyGroups(hideEmptyGroups);
            subGroupNavWidget.setOnItemSelectedCommand(() -> onSubGroupItemClicked(subGroupNavWidget));
            subGroupNavWidget.show(navGroup);
            navSubgroupList.add(subGroupNavWidget);
            view.addGroupItem(navGroup.getId(), navGroup.getName(), navGroup.getDescription(), subGroupNavWidget);
        }
    }

    protected void showItem(NavItem navItem) {
        view.addItem(navItem.getId(), navItem.getName(), navItem.getDescription(), () -> {
            onItemClicked(navItem);
        });
    }

    @Override
    public NavItem getItemSelected() {
        return itemSelected;
    }

    @Override
    public boolean setSelectedItem(String id) {
        clearSelectedItem();

        NavItem navItem = getItem(id);
        if (navItem != null) {
            itemSelected = navItem;
            view.setSelectedItem(navItem.getId());
            return true;
        }

        for (NavWidget navWidget : navSubgroupList) {
            if (navWidget.setSelectedItem(id)) {
                itemSelected = navWidget.getItemSelected();
                activeNavSubgroup = navWidget;
                return true;
            }
        }
        return false;
    }

    @Override
    public void clearSelectedItem() {
        itemSelected = null;
        view.clearSelectedItem();
        if (activeNavSubgroup != null) {
            activeNavSubgroup.clearSelectedItem();
            activeNavSubgroup = null;
        }
    }

    public void onSubGroupItemClicked(NavWidget subGroup) {
        if (activeNavSubgroup != null && activeNavSubgroup != subGroup) {
            activeNavSubgroup.clearSelectedItem();
        }

        activeNavSubgroup = subGroup;
        view.clearSelectedItem();
        view.setSelectedItem(subGroup.getNavGroup().getId());
        itemSelected = subGroup.getItemSelected();

        if (onItemSelectedCommand != null) {
            onItemSelectedCommand.execute();
        }
    }

    public void onItemClicked(NavItem navItem) {
        clearSelectedItem();

        itemSelected = navItem;
        view.setSelectedItem(navItem.getId());

        navigationManager.navItemClicked(navItem);

        if (onItemSelectedCommand != null) {
            onItemSelectedCommand.execute();
        }
    }

    protected void refresh() {
        if (onStaleCommand != null) {
            onStaleCommand.execute();
        }
    }

    @Override
    public void dispose() {
        view.clearItems();
        navSubgroupList.forEach(NavWidget::dispose);
    }

    // Listen to changes in the navigation tree

    public void onNavTreeChanged(@Observes final NavTreeChangedEvent event) {
        navigationManager.update(event.getNavTree());
        refresh();
    }

    // Listen to authorization policy changes as it might impact the menu items shown

    public void onAuthzPolicyChanged(@Observes final SaveRoleEvent event) {
        if (secure) {
            refresh();
        }
    }

    public void onAuthzPolicyChanged(@Observes final SaveGroupEvent event) {
        if (secure) {
            refresh();
        }
    }
}
