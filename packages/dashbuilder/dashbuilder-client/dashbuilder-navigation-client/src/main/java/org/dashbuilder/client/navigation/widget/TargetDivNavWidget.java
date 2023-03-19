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
package org.dashbuilder.client.navigation.widget;

import java.util.List;

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
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.plugin.event.PluginSaved;
import org.uberfire.ext.plugin.model.Plugin;

/**
 * Base class for nav widgets that uses a target div to show a nav item's content once clicked.
 */
public abstract class TargetDivNavWidget extends BaseNavWidget implements HasTargetDiv, HasDefaultNavItem {

    public interface View<T extends TargetDivNavWidget> extends NavWidgetView<T>, LayoutRecursionIssueI18n {

        void clearContent(String targetDivId);

        void showContent(String targetDivId, IsWidget content);

        void infiniteRecursionError(String targetDivId, String cause);
    }

    View view;
    PerspectivePluginManager pluginManager;
    PlaceManager placeManager;
    String targetDivId = null;
    String defaultNavItemId = null;
    boolean gotoItemEnabled = false;

    @Inject
    public TargetDivNavWidget(View view,
                              PerspectivePluginManager pluginManager,
                              PlaceManager placeManager,
                              NavigationManager navigationManager) {
        super(view, navigationManager);
        this.view = view;
        this.pluginManager = pluginManager;
        this.placeManager = placeManager;
    }

    public View getView() {
        return view;
    }

    public void setGotoItemEnabled(boolean enabled) {
        this.gotoItemEnabled = enabled;
    }

    @Override
    public String getTargetDivId() {
        return targetDivId;
    }

    @Override
    public void setTargetDivId(String targetDivId) {
        this.targetDivId = targetDivId;
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
        super.show(itemList);
        if (parent == null && gotoItemEnabled) {
            gotoDefaultItem();
        }
    }

    protected boolean gotoDefaultItem() {
        boolean gotoItem = _gotoDefaultItem();
        if (!gotoItem) {
            defaultNavItemId = getFirstRuntimePerspective(navItemList);
            gotoItem = _gotoDefaultItem();
        }
        return gotoItem;
    }

    protected boolean _gotoDefaultItem() {
        if (defaultNavItemId != null) {
            if (setSelectedItem(defaultNavItemId)) {
                gotoNavItem(true);
                if (parent != null && onItemSelectedCommand != null) {
                    onItemSelectedCommand.execute();
                }
                return true;
            }
        }
        return false;
    }

    protected String getFirstRuntimePerspective(List<NavItem> itemList) {
        if (itemList.isEmpty()) {
            return null;
        }
        for (NavItem navItem : itemList) {
            if (pluginManager.isRuntimePerspective(navItem)) {
                return navItem.getId();
            }
            if (navItem instanceof NavGroup) {
                String result = getFirstRuntimePerspective(((NavGroup) navItem).getChildren());
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    @Override
    public void onItemClicked(NavItem navItem) {
        super.onItemClicked(navItem);
        this.gotoNavItem(false);
    }

    @Override
    public void onSubGroupItemClicked(NavWidget subGroup) {
        super.onSubGroupItemClicked(subGroup);
        this.gotoNavItem(false);
    }

    protected void gotoNavItem(boolean onlyRuntimePerspectives) {
        if (parent == null && gotoItemEnabled) {
            NavWorkbenchCtx navCtx = NavWorkbenchCtx.get(getItemSelected());
            String resourceId = navCtx.getResourceId();
            String navRootId = navCtx.getNavGroupId();
            if (resourceId != null) {
                if (pluginManager.isRuntimePerspective(resourceId)) {
                    LayoutTemplateContext layoutCtx = new LayoutTemplateContext(navRootId);
                    pluginManager.buildPerspectiveWidget(resourceId, layoutCtx,
                            w -> view.showContent(targetDivId, w),
                            this::onInfiniteRecursion);
                }
                else if (!onlyRuntimePerspectives) {
                    placeManager.goTo(resourceId);
                }
            } else {
                view.clearContent(targetDivId);
            }
        }
    }

    public void onInfiniteRecursion(LayoutRecursionIssue issue) {
        String cause = issue.printReport(navigationManager.getNavTree(), view);
        view.infiniteRecursionError(targetDivId, cause);
    }

    // Catch changes on runtime perspectives so as to display the most up to date changes

    private void onPluginSaved(@Observes PluginSaved event) {
        Plugin plugin = event.getPlugin();
        String pluginName = plugin.getName();
        String selectedPerspectiveId = pluginManager.getRuntimePerspectiveId(itemSelected);
        if (selectedPerspectiveId != null && selectedPerspectiveId.equals(pluginName)) {
            gotoNavItem(true);
        }
    }
}
