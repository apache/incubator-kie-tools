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
package org.uberfire.security.client.authz.tree.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCollection;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.client.authz.tree.PermissionNode;
import org.uberfire.security.client.authz.tree.PermissionTree;
import org.uberfire.security.client.authz.tree.PermissionTreeVisitor;

public class DefaultPermissionTree implements PermissionTree {

    private PermissionManager permissionManager;
    private List<PermissionNode> rootNodes;
    private PermissionCollection permissions;

    public DefaultPermissionTree(PermissionManager permissionManager,
                                 List<PermissionNode> rootNodes,
                                 PermissionCollection permissions) {
        this.permissionManager = permissionManager;
        this.rootNodes = rootNodes;
        this.permissions = permissions;
        this.init();
    }

    private void init() {
        for (PermissionNode rootNode : rootNodes) {
            rootNode.setPermissionTree(this);
            rootNode.updatePermissionList(permissions);
        }
    }

    public List<PermissionNode> getRootNodes() {
        return Collections.unmodifiableList(rootNodes);
    }

    @Override
    public PermissionCollection getPermissions() {
        return permissions;
    }

    public Collection<String> getChildrenResourceIds(PermissionNode node) {
        Set<String> result = new HashSet<>();
        for (Permission permission : getChildrenPermissions(node)) {
            String id = permissionManager.resolveResourceId(permission);
            if (id != null) {
                result.add(id);
            }
        }
        return result;
    }

    public Set<Permission> getChildrenPermissions(PermissionNode parent) {
        List<Permission> permissionInTree = parent.getPermissionList();
        if (permissionInTree == null || permissions == null || permissions.collection().isEmpty()) {
            return Collections.emptySet();
        }

        Set<Permission> result = new HashSet<>();
        for (Permission parentPermission : permissionInTree) {
            for (Permission p : permissions.collection()) {

                /// Get only the children that overwrite its parent
                if (parentPermission.impliesName(p) && !parentPermission.impliesResult(p)) {
                    result.add(p);
                }
            }
        }
        return result;
    }

    public void accept(PermissionTreeVisitor visitor) {
        for (PermissionNode rootNode : rootNodes) {
            this.accept(visitor,
                        rootNode);
        }
    }

    public void accept(PermissionTreeVisitor visitor,
                       PermissionNode node) {
        visitor.visit(node);
        node.expand(children -> accept(visitor,
                                       children));
    }

    public void accept(PermissionTreeVisitor visitor,
                       List<PermissionNode> children) {
        for (PermissionNode child : children) {
            accept(visitor,
                   child);
        }
    }
}
