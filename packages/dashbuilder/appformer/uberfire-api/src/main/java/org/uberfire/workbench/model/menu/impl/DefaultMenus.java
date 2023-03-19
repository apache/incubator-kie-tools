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

package org.uberfire.workbench.model.menu.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuVisitor;
import org.uberfire.workbench.model.menu.Menus;

@JsType
public class DefaultMenus implements Menus {

    private final List<MenuItem> menuItems;
    private final int order;

    @JsIgnore
    public DefaultMenus(List<MenuItem> menuItems,
                        int order) {
        this.menuItems = menuItems;
        this.order = order;
    }

    @JsIgnore
    @Override
    public List<MenuItem> getItems() {
        return Collections.unmodifiableList(menuItems);
    }

    @Override
    public void accept(MenuVisitor visitor) {
        if (visitor.visitEnter(this)) {
            for (MenuItem item : menuItems) {
                item.accept(visitor);
            }
            visitor.visitLeave(this);
        }
    }

    @JsIgnore
    @Override
    public Map<Object, MenuItem> getItemsMap() {
        return new HashMap<Object, MenuItem>() {

            {
                for (final MenuItem menuItem : menuItems) {
                    put(menuItem,
                        menuItem);
                }
            }
        };
    }

    @Override
    public int getOrder() {
        return order;
    }
}