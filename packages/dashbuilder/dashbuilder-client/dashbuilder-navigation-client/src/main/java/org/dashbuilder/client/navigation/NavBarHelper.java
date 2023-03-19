/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.client.navigation;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.dashbuilder.navigation.NavDivider;
import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.NavTree;
import org.dashbuilder.navigation.workbench.NavWorkbenchCtx;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuFactory.MenuBuilder;
import org.uberfire.workbench.model.menu.MenuFactory.TopLevelMenusBuilder;
import org.uberfire.workbench.model.menu.MenuItem;

@ApplicationScoped
public class NavBarHelper {

    // code taken from DefaultWorkbenchFeaturesMenusHelper

    public TopLevelMenusBuilder<MenuBuilder> buildMenusFromNavTree(NavTree navTree) {
        if (navTree == null) {
            return null;
        }

        MenuBuilder<TopLevelMenusBuilder<MenuBuilder>> builder = null;
        for (NavItem navItem : navTree.getRootItems()) {

            // Skip dividers
            if (navItem instanceof NavDivider) {
                continue;
            }
            // AF-953: Ignore empty groups
            if (navItem instanceof NavGroup && ((NavGroup) navItem).getChildren().isEmpty()) {
                continue;
            }
            // Build a top level menu entry
            if (builder == null) {
                builder = MenuFactory.newTopLevelMenu(navItem.getName());
            } else {
                builder = builder.endMenu().newTopLevelMenu(navItem.getName());
            }
            // Append its children
            if (navItem instanceof NavGroup) {
                List<MenuItem> childItems = buildMenuItemsFromNavGroup((NavGroup) navItem);
                if (!childItems.isEmpty()) {
                    builder.withItems(childItems);
                }
            }
            // Append the place request
            NavWorkbenchCtx navCtx = NavWorkbenchCtx.get(navItem);
            if (navCtx.getResourceId() != null && ActivityResourceType.PERSPECTIVE.equals(navCtx.getResourceType())) {
                PlaceRequest placeRequest = resolvePlaceRequest(navCtx.getResourceId());
                builder = builder.place(placeRequest);
            }
        }
        return builder != null ? builder.endMenu() : null;
    }

    private List<MenuItem> buildMenuItemsFromNavGroup(NavGroup navGroup) {
        List<MenuItem> result = new ArrayList<>();
        for (NavItem navItem : navGroup.getChildren()) {

            // Skip dividers
            if (navItem instanceof NavDivider) {
                continue;
            }
            // Append its children
            MenuBuilder<MenuFactory.Builder> builder = MenuFactory.newSimpleItem(navItem.getName());
            if (navItem instanceof NavGroup) {
                List<MenuItem> childItems = buildMenuItemsFromNavGroup((NavGroup) navItem);
                if (!childItems.isEmpty()) {
                    builder.withItems(childItems);
                }
            }
            // Append the place request
            NavWorkbenchCtx navCtx = NavWorkbenchCtx.get(navItem);
            if (navCtx.getResourceId() != null && ActivityResourceType.PERSPECTIVE.equals(navCtx.getResourceType())) {
                PlaceRequest placeRequest = resolvePlaceRequest(navCtx.getResourceId());
                builder.place(placeRequest);
            }
            // Build the menu item & continue with the next one
            MenuItem menuItem = builder.endMenu().build().getItems().get(0);
            result.add(menuItem);
        }
        return result;
    }

    public PlaceRequest resolvePlaceRequest(String perspectiveId) {
        return new DefaultPlaceRequest(perspectiveId);
    }

}