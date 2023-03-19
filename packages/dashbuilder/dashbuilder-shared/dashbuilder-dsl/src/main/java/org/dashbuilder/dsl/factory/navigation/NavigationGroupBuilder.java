/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dsl.factory.navigation;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.dashbuilder.dsl.model.NavigationGroup;
import org.dashbuilder.dsl.model.NavigationItem;
import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.impl.NavGroupImpl;

public class NavigationGroupBuilder {

    private NavGroup navGroup;

    private NavigationGroupBuilder(NavGroup navGroup) {
        this.navGroup = navGroup;
    }

    public static NavigationGroupBuilder newBuilder(String name, NavigationItem... items) {
        NavGroup group = new NavGroupImpl();
        List<NavItem> navItems = Arrays.stream(items).map(NavigationItem::getNavItem).collect(Collectors.toList());
        group.setName(name);
        group.setDescription("Some Group");
        group.setId(System.currentTimeMillis() + "");
        group.setChildren(navItems);
        group.setModifiable(false);
        return new NavigationGroupBuilder(group);
    }

    public NavigationGroupBuilder name(String name) {
        this.navGroup.setName(name);
        return this;
    }

    public NavigationGroupBuilder item(NavigationItem item) {
        navGroup.getChildren().add(item.getNavItem());
        return this;
    }

    public NavigationGroup build() {
        return NavigationGroup.of(this.navGroup);
    }

}