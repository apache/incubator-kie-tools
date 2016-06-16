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

package org.uberfire.ext.security.management.client.acl;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.client.authz.tree.LoadCallback;
import org.uberfire.security.client.authz.tree.LoadOptions;
import org.uberfire.security.client.authz.tree.PermissionNode;
import org.uberfire.security.client.authz.tree.PermissionTreeProvider;
import org.uberfire.security.client.authz.tree.impl.PermissionGroupNode;
import org.uberfire.security.client.authz.tree.impl.PermissionLeafNode;

/**
 * This is a dummy example (only for demo purposes) of how to create custom permission tree nodes
 */
@ApplicationScoped
public class GeneralTreeProvider implements PermissionTreeProvider {

    private PermissionManager permissionManager;
    private boolean active = true;
    private String rootNodeName = null;
    private int rootNodePosition = 0;

    public GeneralTreeProvider() {
    }

    @Inject
    public GeneralTreeProvider(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getRootNodeName() {
        return rootNodeName;
    }

    public void setRootNodeName(String rootNodeName) {
        this.rootNodeName = rootNodeName;
    }

    public int getRootNodePosition() {
        return rootNodePosition;
    }

    public void setRootNodePosition(int rootNodePosition) {
        this.rootNodePosition = rootNodePosition;
    }

    private String _getRooNodeName() {
        return rootNodeName != null ? rootNodeName : "General";
    }

    @Override
    public PermissionNode buildRootNode() {
        PermissionGroupNode rootNode = new PermissionGroupNode(this);
        rootNode.setPositionInTree(rootNodePosition);
        rootNode.setNodeName(_getRooNodeName());
        return rootNode;
    }

    @Override
    public void loadChildren(PermissionNode parent, LoadOptions options, LoadCallback callback) {

        if (parent.getNodeName().equals(_getRooNodeName())) {
            List<PermissionNode> result = new ArrayList<>();
            result.addAll(buildSettingNodes(null, 1, 2));

            PermissionGroupNode categoryA = new PermissionGroupNode();
            categoryA.setNodeName("Category A");
            result.add(categoryA);

            PermissionGroupNode categoryB = new PermissionGroupNode();
            categoryB.setNodeName("Category B");
            Permission p = permissionManager.createPermission("general.categoryB", true);
            categoryB.addPermission(p, "Allow", "Deny");
            result.add(categoryB);

            callback.afterLoad(result);
        }
        else if (parent.getNodeName().equals("Category A")) {
            callback.afterLoad(buildSettingNodes("A", 3, 5));
        }
        else if (parent.getNodeName().equals("Category B")) {
            callback.afterLoad(buildSettingNodes("B", 8, 3));
        }
    }

    private List<PermissionNode> buildSettingNodes(String id, int from, int n) {
        List<PermissionNode> result = new ArrayList<>();

        for (int i=from; i<from+n; i++) {
            PermissionLeafNode setting = new PermissionLeafNode();
            setting.setNodeName("Setting " + i);
            String name = "general" + (id != null ? ".category" + id : "") + ".setting" + i;
            Permission p = permissionManager.createPermission(name, true);
            setting.addPermission(p, "Allow", "Deny");
            result.add(setting);
        }
        return result;
    }
}