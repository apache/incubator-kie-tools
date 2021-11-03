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

import java.util.Stack;

import org.dashbuilder.navigation.NavDivider;
import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.NavFactory;
import org.dashbuilder.navigation.NavItemContext;
import org.dashbuilder.navigation.NavTree;

public class NavTreeBuilder {

    NavFactory factory = NavFactory.get();
    Stack<NavGroup> stack = new Stack<>();
    NavGroup root = factory.createNavGroup();

    public NavTreeBuilder() {
        stack.push(root);
    }

    public NavTreeBuilder group(String id, String name, String description, boolean modifiable) {
        NavGroup item = factory.createNavGroup();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setModifiable(modifiable);
        stackGroup(item);
        return this;
    }

    public NavTreeBuilder item(String id, String name, String description, boolean modifiable) {
        return item(id, name, description, modifiable, (String) null);
    }

    public NavTreeBuilder item(String id, String name, String description, boolean modifiable, NavItemContext itemCtx) {
        return item(id, name, description, modifiable, itemCtx != null ? itemCtx.toString() : null);
    }

    public NavTreeBuilder item(String id, String name, String description, boolean modifiable, String itemCtx) {
        NavItem item = factory.createNavItem();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setModifiable(modifiable);
        item.setContext(itemCtx);
        stackItem(item);
        return this;
    }

    public NavTreeBuilder divider() {
        NavDivider item = factory.createDivider();
        item.setId(Integer.toString(item.hashCode()));
        stackItem(item);
        return this;
    }

    public NavTreeBuilder endGroup() {
        if (!stack.isEmpty()) {
            stack.pop();
        } else {
            throw new IllegalStateException("Call group first");
        }
        return this;
    }

    public NavTree build() {
        return new NavTreeImpl(root);
    }

    private void stackGroup(NavGroup item) {
        stackItem(item);
        stack.push(item);
    }

    private void stackItem(NavItem item) {
        NavGroup group = stack.peek();
        item.setParent(group == root ? null : group);
        group.getChildren().add(item);
    }
}
