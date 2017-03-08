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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCollection;
import org.uberfire.security.client.authz.tree.LoadCallback;
import org.uberfire.security.client.authz.tree.PermissionNode;
import org.uberfire.security.client.authz.tree.PermissionTree;
import org.uberfire.security.client.authz.tree.PermissionTreeProvider;

public class AbstractPermissionNode implements PermissionNode {

    private PermissionTree permissionTree = null;
    private PermissionTreeProvider permissionTreeProvider = null;
    private PermissionNode parentNode = null;
    private List<Permission> permissionList = new ArrayList<>();
    private Map<Permission, List<Permission>> dependencyMap = new HashMap<>();
    private Map<String, Object> propertyMap = new HashMap<>();
    private Map<String, String> grantNameMap = new HashMap<>();
    private Map<String, String> denyNameMap = new HashMap<>();
    private String nodeName = null;
    private String nodeFullName = null;
    private boolean expanded = false;
    private int positionInTree = 0;

    @Override
    public PermissionTree getPermissionTree() {
        return permissionTree;
    }

    public void setPermissionTree(PermissionTree permissionTree) {
        this.permissionTree = permissionTree;
    }

    @Override
    public PermissionTreeProvider getPermissionTreeProvider() {
        PermissionNode node = this;
        PermissionTreeProvider provider = permissionTreeProvider;
        while (provider == null && node != null) {
            node = node.getParentNode();
            provider = node.getPermissionTreeProvider();
        }
        return provider;
    }

    @Override
    public void setPermissionTreeProvider(PermissionTreeProvider permissionTreeProvider) {
        this.permissionTreeProvider = permissionTreeProvider;
    }

    @Override
    public PermissionNode getParentNode() {
        return parentNode;
    }

    @Override
    public void setParentNode(PermissionNode parentNode) {
        this.parentNode = parentNode;
    }

    @Override
    public List<Permission> getPermissionList() {
        return permissionList;
    }

    @Override
    public void addPermission(Permission permission,
                              String name) {
        addPermission(permission,
                      name,
                      name);
    }

    @Override
    public void addPermission(Permission permission,
                              String grantName,
                              String denyName) {
        permissionList.add(permission);
        setPermissionGrantName(permission,
                               grantName);
        setPermissionDenyName(permission,
                              denyName);
    }

    @Override
    public boolean isExpanded() {
        return expanded;
    }

    @Override
    public Object getProperty(String key) {
        return propertyMap.get(key);
    }

    @Override
    public void setProperty(String key,
                            Object value) {
        propertyMap.put(key,
                        value);
    }

    @Override
    public boolean propertyEquals(String key,
                                  Object value) {
        return propertyMap.containsKey(key) && propertyMap.get(key).equals(value);
    }

    @Override
    public int getPositionInTree() {
        return positionInTree;
    }

    public void setPositionInTree(int positionInTree) {
        this.positionInTree = positionInTree;
    }

    @Override
    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    @Override
    public String getNodeFullName() {
        return nodeFullName == null ? getNodeName() : nodeFullName;
    }

    public void setNodeFullName(String nodeFullName) {
        this.nodeFullName = nodeFullName;
    }

    @Override
    public String getPermissionGrantName(Permission permission) {
        return grantNameMap.get(permission.getName());
    }

    public void setPermissionGrantName(Permission permission,
                                       String name) {
        grantNameMap.put(permission.getName(),
                         name);
    }

    @Override
    public String getPermissionDenyName(Permission permission) {
        String name = denyNameMap.get(permission.getName());
        return name != null ? name : grantNameMap.get(permission.getName());
    }

    public void setPermissionDenyName(Permission permission,
                                      String name) {
        denyNameMap.put(permission.getName(),
                        name);
    }

    @Override
    public List<Permission> impliesName(PermissionNode node) {
        List<Permission> result = new ArrayList<>();
        for (Permission other : node.getPermissionList()) {
            if (impliesName(other)) {
                result.add(other);
            }
        }
        return result;
    }

    public boolean impliesName(Permission other) {
        for (Permission p : permissionList) {
            if (p.impliesName(other)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void updatePermissionList(PermissionCollection permissions) {
        if (permissions != null) {
            for (Permission p : permissionList) {
                Permission existing = permissions.get(p.getName());
                if (existing != null) {
                    p.setResult(existing.getResult());
                } else {
                    PermissionNode parent = getFirstParentWithPermissions();
                    if (parent != null) {
                        for (Permission parentPermission : parent.getPermissionList()) {
                            if (parentPermission.impliesName(p)) {
                                p.setResult(parentPermission.getResult());
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private PermissionNode getFirstParentWithPermissions() {
        PermissionNode parent = getParentNode();
        while (parent != null && parent.getPermissionList().isEmpty()) {
            parent = parent.getParentNode();
        }
        return parent;
    }

    @Override
    public void addDependencies(Permission permission,
                                Permission... dependencies) {
        if (dependencies != null) {
            List<Permission> dependencyList = dependencyMap.get(permission);
            if (dependencyList == null) {
                dependencyList = new ArrayList<>();
                dependencyMap.put(permission,
                                  dependencyList);
            }
            for (Permission dependency : dependencies) {
                dependencyList.add(dependency);
            }
        }
    }

    @Override
    public List<Permission> getDependencies(Permission permission) {
        return dependencyMap.get(permission);
    }

    @Override
    public int getLevel() {
        int level = 0;
        PermissionNode parent = getParentNode();
        while (parent != null) {
            parent = parent.getParentNode();
            level++;
        }
        return level;
    }

    @Override
    public void expand(LoadCallback callback) {
        if (!(this instanceof PermissionLeafNode)) {
            Collection<String> resourceIds = permissionTree.getChildrenResourceIds(this);
            DefaultLoadOptions options = new DefaultLoadOptions();
            options.setResourceIds(resourceIds);

            getPermissionTreeProvider().loadChildren(this,
                                                     options,
                                                     children -> {
                                                         expanded = true;
                                                         for (PermissionNode child : children) {
                                                             child.setPermissionTree(permissionTree);
                                                             child.setParentNode(AbstractPermissionNode.this);
                                                             child.updatePermissionList(permissionTree.getPermissions());
                                                         }
                                                         callback.afterLoad(children);
                                                     });
        } else {
            expanded = true;
            callback.afterLoad(Collections.emptyList());
        }
    }

    @Override
    public void collapse() {
        expanded = false;
    }
}
