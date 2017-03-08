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

/**
 * A {@link PermissionTree} is composed by a list of root nodes. The classes implementing this interface are used by
 * the {@link PermissionTreeFactory} to build those root nodes.
 * <p>
 * <p>From application perspective, custom tree providers can be implemented in order to make application related
 * permissions part of the permission tree.</p>
 */
public interface PermissionTreeProvider {

    /**
     * Returns a brand new node containing a permission node hierarchy.
     * @return A completely initialized {@link PermissionNode} instance.
     */
    PermissionNode buildRootNode();

    /**
     * Asynchronous interface for loading the children nodes of a given parent permission node.
     * @param parent The parent node
     * @param options The load options
     * @param consumer The callback instance to notify once the loading process is done.
     */
    void loadChildren(PermissionNode parent,
                      LoadOptions options,
                      LoadCallback consumer);

    /**
     * Flag indicating if this provider is in active state.
     * <p>
     * <p>Only if active, its root node is added to the permission tree.</p>
     */
    default boolean isActive() {
        return true;
    }
}
