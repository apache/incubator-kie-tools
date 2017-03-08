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

import java.util.Collection;
import java.util.List;

import org.uberfire.security.authz.PermissionCollection;

/**
 * A hierarchy of {@link PermissionNode} instances
 */
public interface PermissionTree {

    /**
     * Get the root nodes of the tree.
     * <p>
     * A list of nodes order by its {@link PermissionNode#getPositionInTree()}. The nodes with the same position
     * are ordered by node name in incremental order.
     */
    List<PermissionNode> getRootNodes();

    /**
     * Get the resource ids (if any) referenced from the permission instances, see {@link #getPermissions()}.
     * @param parentNode The parent node to start looking for
     * @return A collection of resource identifiers
     */
    Collection<String> getChildrenResourceIds(PermissionNode parentNode);

    /**
     * Get the collection of permissions this tree has been initiliazed with
     */
    PermissionCollection getPermissions();

    /**
     * Entry point for visitor interfaces
     */
    void accept(PermissionTreeVisitor visitor);
}
