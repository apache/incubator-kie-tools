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
package org.dashbuilder.client.navigation.impl;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.dashbuilder.client.navigation.NavigationManager;
import org.dashbuilder.client.navigation.event.NavItemGotoEvent;
import org.dashbuilder.navigation.event.NavTreeChangedEvent;
import org.dashbuilder.client.navigation.event.NavTreeLoadedEvent;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.NavTree;
import org.dashbuilder.navigation.service.NavigationServices;
import org.dashbuilder.navigation.workbench.NavSecurityController;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.mvp.Command;

@ApplicationScoped
public class NavigationManagerImpl implements NavigationManager {

    private Caller<NavigationServices> navServices;
    private Event<NavItemGotoEvent> navItemGotoEvent;
    private Event<NavTreeLoadedEvent> navTreeLoadedEvent;
    private Event<NavTreeChangedEvent> navTreeChangedEvent;
    private NavSecurityController navController;
    private NavTree navTree;
    private NavTree defaultNavTree;

    @Inject
    public NavigationManagerImpl(Caller<NavigationServices> navServices,
                                 NavSecurityController navController,
                                 Event<NavTreeLoadedEvent> navTreeLoadedEvent,
                                 Event<NavTreeChangedEvent> navTreeChangedEvent,
                                 Event<NavItemGotoEvent> navItemGotoEvent) {
        this.navServices = navServices;
        this.navController = navController;
        this.navTreeLoadedEvent = navTreeLoadedEvent;
        this.navTreeChangedEvent = navTreeChangedEvent;
        this.navItemGotoEvent = navItemGotoEvent;
    }

    @Override
    public void init(Command afterInit) {
        navServices.call((NavTree n) -> {
            navTree = n;
            if (afterInit != null) {
                afterInit.execute();
            }
            navTreeLoadedEvent.fire(new NavTreeLoadedEvent(getNavTree()));
        }).loadNavTree();
    }

    @Override
    public NavTree getDefaultNavTree() {
        return defaultNavTree;
    }

    @Override
    public void setDefaultNavTree(NavTree defaultNavTree) {
        this.defaultNavTree = defaultNavTree;
    }

    @Override
    public NavTree getNavTree() {
        return !hasNavTree() ? defaultNavTree : navTree;
    }

    @Override
    public boolean hasNavTree() {
        return Optional.ofNullable(navTree).isPresent();
    }

    @Override
    public void saveNavTree(NavTree newTree, Command afterSave) {
        navServices.call((Void v) -> {
            navTree = newTree;
            navTreeChangedEvent.fire(new NavTreeChangedEvent(newTree));
            if (afterSave != null) {
                afterSave.execute();
            }
        }).saveNavTree(newTree);
    }

    @Override
    public NavTree secure(NavTree navTree, boolean removeEmptyGroups) {
        return navController.secure(navTree, removeEmptyGroups);
    }

    @Override
    public void secure(List<NavItem> itemList, boolean removeEmptyGroups) {
        navController.secure(itemList, removeEmptyGroups);
    }

    @Override
    public void navItemClicked(NavItem navItem) {
        if (navController.canRead(navItem)) {
            navItemGotoEvent.fire(new NavItemGotoEvent(navItem));
        }
    }

    @Override
    public void update(NavTree navTree) {
        this.defaultNavTree = navTree;
    }
}
