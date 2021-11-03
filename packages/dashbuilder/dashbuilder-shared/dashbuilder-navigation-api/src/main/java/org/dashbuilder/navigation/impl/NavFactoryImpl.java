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
package org.dashbuilder.navigation.impl;

import org.dashbuilder.navigation.NavDivider;
import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavFactory;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.NavTree;

public class NavFactoryImpl implements NavFactory {

    @Override
    public NavTree createNavTree(NavItem navItem) {
        if (navItem instanceof NavGroup) {
            NavGroup root = (NavGroup) navItem.cloneItem();
            root.setParent(null);
            root.getChildren().forEach(e -> e.setParent(null));
            return new NavTreeImpl(root);
        }
        else {
            NavItem i = navItem.cloneItem();
            i.setParent(null);
            NavTreeImpl tree = new NavTreeImpl();
            tree.getRootItems().add(i);
            return tree;
        }
    }

    @Override
    public NavTree createNavTree() {
        return new NavTreeImpl();
    }

    @Override
    public NavGroup createNavGroup() {
        return new NavGroupImpl();
    }

    @Override
    public NavGroup createNavGroup(NavTree navTree) {
        NavGroup navGroup = createNavGroup();
        navGroup.setChildren(navTree.cloneTree().getRootItems());
        navGroup.getChildren().forEach(child -> child.setParent(navGroup));
        return navGroup;
    }

    @Override
    public NavItem createNavItem() {
        return new NavItemImpl();
    }

    @Override
    public NavDivider createDivider() {
        return new NavDividerImpl();
    }
}
