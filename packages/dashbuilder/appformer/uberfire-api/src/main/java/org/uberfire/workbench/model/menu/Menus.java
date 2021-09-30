/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.workbench.model.menu;

import java.util.List;
import java.util.Map;

import jsinterop.annotations.JsType;

/**
 * Menus that includes permission
 */
@JsType
public interface Menus {

    /**
     * Returns the items in this collection of menus, in the order they should appear in the user interface.
     */
    public List<MenuItem> getItems();

    @Deprecated
    public Map<Object, MenuItem> getItemsMap();

    /**
     * Causes the given {@link MenuVisitor} to visit this menu, then each item in turn (they will pass the visitor to
     * their descendants). The menu items will be visited via an pre-order traversal (parents are visited before their
     * children). Top-level menu items are visited in the same order as they are returned from {@link #getItems()}.
     */
    public void accept(MenuVisitor visitor);

    /**
     * Defines the order on what these menus will be inserted.
     */
    public int getOrder();
}
