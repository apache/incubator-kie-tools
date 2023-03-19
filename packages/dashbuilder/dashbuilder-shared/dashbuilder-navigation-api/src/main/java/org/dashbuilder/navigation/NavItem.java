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

/**
 * A navigation item is a way to classify different assets, like for instance, perspectives. A tag can be referenced by other
 * tags, either as a parent or as a child. The links between different tags creates a {@link NavTree} structure
 * that can be used to provide navigation services across the different assets referenced by the tags in the tree.
 */
public interface NavItem {

    enum Type {
        ITEM,
        GROUP,
        DIVIDER;
    }

    /**
     * A unique id within the same {@link NavTree}
     */
    String getId();

    /**
     * Change the item's id
     */
    void setId(String id);

    /**
     * The item's name
     */
    String getName();

    /**
     * Change the item's name
     */
    void setName(String  name);

    /**
     * A brief description of the item (optional, if not provided the name is used instead)
     */
    String getDescription();

    /**
     * Change the item's description
     */
    void setDescription(String description);

    /**
     * The item's {@link Type}
     */
    Type getType();

    /**
     * Get the parent of this item (if any)
     *
     * @return The item this one is a child of. Or null if this is a root item
     */
    NavGroup getParent();

    /**
     * Change the item's parent
     */
    void setParent(NavGroup parent);

    /**
     * Flag indicating if the item can be modified from a tree once added
     */
    boolean isModifiable();

    /**
     * Change the item's modifiable flag
     */
    void setModifiable(boolean modifiable);

    /**
     * An optional string that can be used to attach contextual information, like an external reference for instance.
     */
    String getContext();

    /**
     * Change the item's context attribute
     */
    void setContext(String ctx);

    /**
     * Entry point for visitor interfaces
     */
    void accept(NavItemVisitor visitor);

    /**
     * Creates a brand new copy of this item
     */
    NavItem cloneItem();
}
