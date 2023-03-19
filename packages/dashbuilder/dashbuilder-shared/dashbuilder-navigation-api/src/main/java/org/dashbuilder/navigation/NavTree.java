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
package org.dashbuilder.navigation;

import java.util.List;

/**
 * A tree like structure containing a list of parent and children nodes where every node is a {@link NavItem} instance.
 */
public interface NavTree {

    /**
     * The list of items belonging to the first tree level
     *
     * @return A list of {@link NavItem} instances
     */
    List<NavItem> getRootItems();

    /**
     * Get an item given its id. All the tree nodes are compared, no matter its level.
     *
     * @param id The unique identifier of the item
     * @return The first instance found
     */
    NavItem getItemById(String id);

    /**
     * Given an existing item, it creates a brand new subtree where the item (or its children in case of groups)
     * is taken as the root node.
     *
     * @param id The unique identifier of the item
     * @return The first instance found
     */
    NavTree getItemAsTree(String id);

    /**
     * Get the items that match the given context
     *
     * @param ctx The context to search for
     * @return The list of nav items that match the target context
     */
    List<NavItem> searchItems(NavItemContext ctx);

    /**
     * Creates and register a brand new {@link NavGroup} item.
     *
     * @param id The unique id of the item
     * @param name The item's name
     * @param description A brief description of the item
     * @param groupId The identifier of an existing node in the tree where to append the newly created item as a child
     * @param modifiable Flag indicating if the item can be modified once added
     *
     * @return The instance just created
     */
    NavGroup addGroup(String id, String name, String description, String groupId, boolean modifiable);

    /**
     * Creates and register a brand new {@link NavItem} item.
     *
     * @param id The unique id of the item
     * @param name The item's name
     * @param description A brief description of the item
     * @param groupId The identifier of an existing node in the tree where to append the newly created item as a child
     * @param modifiable Flag indicating if the item can be modified once added
     * @param context An string that contains item related information, like an external reference for instance.
     *
     * @return The instance just created
     */
    NavItem addItem(String id, String name, String description, String groupId, boolean modifiable, String context);

    /**
     * Creates and register a brand new {@link NavDivider} item.
     *
     * @param groupId The identifier of an existing node in the tree where to append the newly created item as a child
     * @param modifiable Flag indicating if the item can be modified once added
     *
     * @return The instance just created
     */
    NavDivider addDivider(String groupId, boolean modifiable);

    /**
     * Delete an existing item including all its children.
     *
     * @param id The unique identifier of the item
     * @return The instance removed or null if the instance is not found
     */
    NavItem deleteItem(String id);

    /**
     * Updates the name attribute of an existing item
     *
     * @param id The unique id of the item
     * @param name A brief description of the item
     *
     * @return The instance being updated
     */
    NavItem setItemName(String id, String name);

    /**
     * Updates the description attribute of an existing item
     *
     * @param id The unique id of the item
     * @param description A brief description of the item
     *
     * @return The instance being updated
     */
    NavItem setItemDescription(String id, String description);

    /**
     * Updates the modifiable flag attribute of an existing item
     *
     * @param id The unique id of the item
     * @param modifiable Flag indicating if the item can be modified
     *
     * @return The instance being updated
     */
    NavItem setItemModifiable(String id, boolean modifiable);

    /**
     * Updates the perspective attached to an existing perspective link
     *
     * @param id The unique id of the item
     * @param context The item's context
     *
     * @return The item being updated
     */
    NavItem setItemContext(String id, String context);

    /**
     * Changes the location of an item in the tree
     *
     * @param id The unique id of the item
     * @param newParentId The identifier of an existing node in the tree where to move the item
     *
     * @return The instance being updated
     */
    NavItem moveItem(String id, String newParentId);

    /**
     * Move the specified item to the first position within its parent's children list
     *
     * @param id The unique id of the item
     */
    void moveItemFirst(String id);

    /**
     * Move the specified item to the last position within its parent's children list
     *
     * @param id The unique id of the item
     */
    void moveItemLast(String id);

    /**
     * Move an item one position up in its parent's children list
     *
     * @param id The unique id of the item
     */
    void moveItemUp(String id);

    /**
     * Move an item one position down in its parent's children list
     *
     * @param id The unique id of the item
     */
    void moveItemDown(String id);

    /**
     * Entry point for visitor interfaces
     */
    void accept(NavItemVisitor visitor);

    /**
     * Creates a brand new copy of this tree
     */
    NavTree cloneTree();
}
