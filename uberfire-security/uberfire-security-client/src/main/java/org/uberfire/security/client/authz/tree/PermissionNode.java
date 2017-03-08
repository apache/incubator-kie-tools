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
package org.uberfire.security.client.authz.tree;

import java.util.List;

import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCollection;

/**
 * A permission tree node
 */
public interface PermissionNode {

    /**
     * Get the provider instance that built this node instance.
     */
    PermissionTreeProvider getPermissionTreeProvider();

    /**
     * Set the provider instance that built this node instance.
     */
    void setPermissionTreeProvider(PermissionTreeProvider provider);

    /**
     * Get the {@link PermissionTree} instance this root node has been attached to.
     */
    PermissionTree getPermissionTree();

    /**
     * Attach this root node to the given {@link PermissionTree} instance.
     */
    void setPermissionTree(PermissionTree tree);

    /**
     * Get the parent node.
     * @return null if this is a root node
     */
    PermissionNode getParentNode();

    /**
     * Set this node's parent
     */
    void setParentNode(PermissionNode parentNode);

    /**
     * A number indicating at what specific levels this node is placed within the {@link PermissionTree}
     * @return A positive integer from 0 to N, where <i>0=root</i>, <i>N=leaf</i>.
     */
    int getLevel();

    /**
     * Retrieves the name to display for the given node.
     * @return The name to display in the UI
     */
    String getNodeName();

    /**
     * Retrieves the full name to display for the given node.
     * @return The name to display in the UI
     */
    String getNodeFullName();

    /**
     * The position of the node within the permission tree. The position is used by {@link PermissionTree}
     * implementations to order its root nodes from lower position nodes to higher ones.
     */
    int getPositionInTree();

    /**
     * The list of permissions attached to this node.
     */
    List<Permission> getPermissionList();

    /**
     * Updates the node's permission values according to the values specified in the given collection.
     * @param collection The permission collection with the results to apply.
     */
    void updatePermissionList(PermissionCollection collection);

    /**
     * Add a permission instance to the list
     */
    void addPermission(Permission permission,
                       String name);

    /**
     * Add a permission instance to the list
     */
    void addPermission(Permission permission,
                       String grantName,
                       String denyName);

    /**
     * Retrieves the name to display for the grant action.
     * @param permission The {@link Permission} instance
     * @return The name to display in the UI
     */
    String getPermissionGrantName(Permission permission);

    /**
     * Retrieves the name to display for the deny action.
     * @param permission The {@link Permission} instance
     * @return The name to display in the UI
     */
    String getPermissionDenyName(Permission permission);

    /**
     * Attach to the given {@link Permission} instance a set of permissions which depends on it so that if the
     * permission is denied then all its dependencies must be denied as well.
     * <p>
     * <p>For instance, the update and delete permission over a resource depends on the read permission.</p>
     * @param permission The {@link Permission} instance
     * @param dependencies The set of dependencies
     */
    void addDependencies(Permission permission,
                         Permission... dependencies);

    /**
     * Get the dependencies (if any) attached to a given permission instance.
     * @param permission The permission to check
     * @return A list of permissions
     */
    List<Permission> getDependencies(Permission permission);

    /**
     * Get the expand status
     * @return true if expanded, false if collapsed
     */
    boolean isExpanded();

    /**
     * Get a property attached to this node
     * @param key The property key
     * @return The value object
     */
    Object getProperty(String key);

    /**
     * Attach a property to this node
     * @param key The property key
     * @param value The value object
     */
    void setProperty(String key,
                     Object value);

    /**
     * Check if a property exists and matchs the given value
     * @param key The property key
     * @param value The value object to check
     */
    boolean propertyEquals(String key,
                           Object value);

    /**
     * Retrieve the permissions of the specified node that are implied by this node's permissions.
     * @return A sub-list of {@link Permission} instances
     */
    List<Permission> impliesName(PermissionNode node);

    /**
     * Expand the node.
     * <p>
     * <p>The children nodes are loaded asynchronously and the consumer instance passed
     * as a parameter is invoked after the loading process is done.</p>
     * <p>
     * <p>It does nothing in case the node is already expanded.</p>
     * @param consumer The callback instance that consumes the children nodes.
     */
    void expand(LoadCallback consumer);

    /**
     * Collapse the node
     */
    void collapse();
}
