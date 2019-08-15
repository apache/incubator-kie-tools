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

import java.util.ArrayList;
import java.util.List;

import org.dashbuilder.navigation.NavDivider;
import org.dashbuilder.navigation.NavFactory;
import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.NavItemContext;
import org.dashbuilder.navigation.NavTree;
import org.dashbuilder.navigation.NavItemVisitor;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class NavTreeImpl implements NavTree {

    private NavGroup root;

    public NavTreeImpl() {
        root = NavFactory.get().createNavGroup();
    }

    public NavTreeImpl(NavGroup root) {
        this.root = root;
    }

    public List<NavItem> getRootItems() {
        return root.getChildren();
    }

    @Override
    public NavItem getItemById(String id) {
        return getItemById(id, root.getChildren());
    }

    private NavItem getItemById(String id, List<NavItem> navItemList) {
        if (id == null) {
            return null;
        }
        for (NavItem navItem : navItemList) {
            if (navItem.getId() != null && navItem.getId().equals(id)) {
                return navItem;
            }
            if (navItem instanceof NavGroup) {
                NavItem child = getItemById(id, ((NavGroup) navItem).getChildren());
                if (child != null) {
                    return child;
                }
            }
        }
        return null;
    }

    @Override
    public NavTree getItemAsTree(String id) {
        NavItem item = getItemById(id);
        return item != null ? NavFactory.get().createNavTree(item) : null;
    }

    @Override
    public List<NavItem> searchItems(NavItemContext ctx) {
        return searchItems(ctx, getRootItems());
    }

    public List<NavItem> searchItems(NavItemContext ctx, List<NavItem> navItemList) {
        List<NavItem> result = new ArrayList<>();
        if (ctx == null) {
            return result;
        }
        NavItemContext niCtx = NavItemContext.create();
        for (NavItem navItem : navItemList) {
            niCtx.init(navItem.getContext());

            if (niCtx.includesPropertiesOf(ctx)) {
                result.add(navItem);
            }
            if (navItem instanceof NavGroup) {
                List<NavItem> children = searchItems(ctx, ((NavGroup) navItem).getChildren());
                result.addAll(children);
            }
        }
        return result;
    }

    @Override
    public NavGroup addGroup(String id, String name, String description, String parentId, boolean modifiable) {
        NavGroup newNavItem = NavFactory.get().createNavGroup();
        newNavItem.setId(id);
        newNavItem.setName(name);
        newNavItem.setDescription(description);
        newNavItem.setModifiable(modifiable);

        addItem(newNavItem, parentId);
        return newNavItem;
    }

    @Override
    public NavItem addItem(String id, String name, String description, String parentId, boolean modifiable, String context) {
        NavItem newNavItem = NavFactory.get().createNavItem();
        newNavItem.setId(id);
        newNavItem.setName(name);
        newNavItem.setDescription(description);
        newNavItem.setModifiable(modifiable);
        newNavItem.setContext(context);

        addItem(newNavItem, parentId);
        return newNavItem;
    }

    @Override
    public NavDivider addDivider(String parentId, boolean modifiable) {
        NavDivider newNavItem = NavFactory.get().createDivider();
        newNavItem.setId(Integer.toString(newNavItem.hashCode()));
        newNavItem.setModifiable(modifiable);

        addItem(newNavItem, parentId);
        return newNavItem;
    }

    private void addItem(NavItem item, String parentId) {

        // Ensure the parent exists (if defined)
        NavItem parent = getItemById(parentId);
        if (parentId != null && parent == null) {
            throw new RuntimeException("Parent '" + parentId + "' not found");
        }

        // Ensure the parent is a group
        if (parent != null && !(parent instanceof NavGroup)) {
            throw new RuntimeException("Parent '" + parentId + "' is not a group");
        }

        // Register the item
        if (parent == null) {
            item.setParent(null);
            root.getChildren().add(item);
        } else {
            ((NavGroup) parent).getChildren().add(item);
            item.setParent((NavGroup) parent);
        }
    }

    @Override
    public NavItem deleteItem(String id) {
        NavItem navItem = getItemById(id);
        if (navItem != null) {
            if (navItem.getParent() == null) {
                root.getChildren().remove(navItem);
            } else {
                navItem.getParent().getChildren().remove(navItem);
                navItem.setParent(null);
            }
        }
        return navItem;
    }

    @Override
    public NavItem setItemName(String id, String name) {
        NavItem navItem = getItemById(id);
        if (navItem == null) {
            throw new RuntimeException("Item not found: " + id);
        }
        navItem.setName(name);
        return navItem;
    }

    @Override
    public NavItem setItemDescription(String id, String description) {
        NavItem navItem = getItemById(id);
        if (navItem == null) {
            throw new RuntimeException("Item not found: " + id);
        }
        navItem.setDescription(description);
        return navItem;
    }

    @Override
    public NavItem setItemModifiable(String id, boolean modifiable) {
        NavItem navItem = getItemById(id);
        if (navItem == null) {
            throw new RuntimeException("Item not found: " + id);
        }
        navItem.setModifiable(modifiable);
        return navItem;
    }

    @Override
    public NavItem setItemContext(String id, String context) {
        NavItem navItem = getItemById(id);
        if (navItem == null) {
            throw new RuntimeException("Item not found: " + id);
        }
        navItem.setContext(context);
        return navItem;
    }

    @Override
    public NavItem moveItem(String id, String newParentId) {
        NavItem navItem = getItemById(id);
        if (navItem == null) {
            throw new RuntimeException("Item not found: " + id);
        }

        // Ensure the new target parent exists
        NavItem newParent = getItemById(newParentId);
        if (newParent == null && newParentId != null) {
            throw new RuntimeException("Parent not found: " + newParentId);
        }

        // Ensure the parent is a group
        if (newParent != null && !(newParent instanceof NavGroup)) {
            throw new RuntimeException("Parent '" + newParentId + "' is not a group");
        }

        // Avoid loops
        if (newParentId != null && newParentId.equals(id)) {
            throw new RuntimeException("The parent can't be the item itself: " + newParentId);
        }

        // Do not move if nothing changes
        String parentId = navItem.getParent() != null ? navItem.getParent().getId(): null;
        if ((parentId == null && newParentId == null) ||
            (parentId != null && newParentId != null && parentId.equals(newParentId))) {
            return navItem;
        }

        // Do move
        if (navItem.getParent() != null) {
            navItem.getParent().getChildren().remove(navItem);
        }
        navItem.setParent(null);
        if (newParent != null) {
            ((NavGroup) newParent).getChildren().add(navItem);
            navItem.setParent((NavGroup) newParent);
        }

        return navItem;
    }

    @Override
    public void moveItemFirst(String id) {
        changePosition(id, true, null);
    }

    @Override
    public void moveItemLast(String id) {
        changePosition(id, false, null);
    }

    @Override
    public void moveItemUp(String id) {
        changePosition(id, true, 1);
    }

    @Override
    public void moveItemDown(String id) {
        changePosition(id, false, 1);
    }

    public void changePosition(String id, boolean up, Integer npositions) {
        NavItem navItem = getItemById(id);
        if (navItem == null) {
            throw new RuntimeException("Item not found: " + id);
        }
        NavGroup parent = navItem.getParent();
        List<NavItem> itemList = parent != null ? parent.getChildren() : getRootItems();
        int idx = itemList.indexOf(navItem);
        int newPos = npositions == null ? (up ? 0 : itemList.size()-1) : (up ? idx-npositions: idx+npositions);

        if ((up && newPos < 0) || (!up && newPos > itemList.size()-1)) {
            throw new RuntimeException("Item '" + id + "' position out of range (old=" + idx + ", new=" + newPos + ")");
        }

        itemList.remove(idx);
        itemList.add(newPos, navItem);
    }

    @Override
    public void accept(NavItemVisitor visitor) {
        for (NavItem item : root.getChildren()) {
            item.accept(visitor);
        }
    }

    @Override
    public NavTree cloneTree() {
        NavGroup rootClone = (NavGroup) this.root.cloneItem();
        rootClone.getChildren().forEach(e -> e.setParent(null));
        return new NavTreeImpl(rootClone);
    }
}
