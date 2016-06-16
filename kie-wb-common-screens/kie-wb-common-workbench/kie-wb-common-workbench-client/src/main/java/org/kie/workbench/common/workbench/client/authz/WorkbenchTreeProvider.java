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

package org.kie.workbench.common.workbench.client.authz;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.workbench.client.resources.i18n.DefaultWorkbenchConstants;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.client.authz.tree.LoadCallback;
import org.uberfire.security.client.authz.tree.LoadOptions;
import org.uberfire.security.client.authz.tree.PermissionNode;
import org.uberfire.security.client.authz.tree.PermissionTreeProvider;
import org.uberfire.security.client.authz.tree.impl.PermissionGroupNode;
import org.uberfire.security.client.authz.tree.impl.PermissionLeafNode;

import static org.kie.workbench.common.workbench.client.authz.WorkbenchFeatures.*;

/**
 * A tree permission provider which add general workbench permissions non tied to any specific resource.
 */
@ApplicationScoped
public class WorkbenchTreeProvider implements PermissionTreeProvider {

    private PermissionManager permissionManager;
    private int rootNodePosition = 0;
    private DefaultWorkbenchConstants i18n = DefaultWorkbenchConstants.INSTANCE;

    public static final String NODE_TYPE = "type";
    public static final String NODE_ROOT = "root";

    public WorkbenchTreeProvider() {
    }

    @Inject
    public WorkbenchTreeProvider(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public int getRootNodePosition() {
        return rootNodePosition;
    }

    public void setRootNodePosition(int rootNodePosition) {
        this.rootNodePosition = rootNodePosition;
    }

    @Override
    public PermissionNode buildRootNode() {
        PermissionGroupNode rootNode = new PermissionGroupNode(this);
        rootNode.setPositionInTree(rootNodePosition);
        rootNode.setNodeName(i18n.WorkbenchRootNodeName());
        rootNode.setProperty(NODE_TYPE, NODE_ROOT);
        return rootNode;
    }

    @Override
    public void loadChildren(PermissionNode parent, LoadOptions options, LoadCallback callback) {

        if (parent.propertyEquals(NODE_TYPE, NODE_ROOT)) {
            List<PermissionNode> result = new ArrayList<>();

            Permission p1 = permissionManager.createPermission(CONFIGURE_REPOSITORY, true);
            Permission p2 = permissionManager.createPermission(PROMOTE_ASSETS, true);
            Permission p3 = permissionManager.createPermission(RELEASE_PROJECT, true);
            Permission p4 = permissionManager.createPermission(MANAGE_DASHBOARDS, true);
            Permission p5 = permissionManager.createPermission(PLANNER_AVAILABLE, true);

            PermissionLeafNode node1 = new PermissionLeafNode();
            node1.setNodeName(i18n.ConfigureRepositories());
            node1.addPermission(p1, i18n.PermissionAllow(), i18n.PermissionDeny());

            PermissionLeafNode node2 = new PermissionLeafNode();
            node2.setNodeName(i18n.PromoteAssets());
            node2.addPermission(p3, i18n.PermissionAllow(), i18n.PermissionDeny());

            PermissionLeafNode node3 = new PermissionLeafNode();
            node3.setNodeName(i18n.ReleaseProjects());
            node3.addPermission(p3, i18n.PermissionAllow(), i18n.PermissionDeny());

            PermissionLeafNode node4 = new PermissionLeafNode();
            node4.setNodeName(i18n.ManageDashboards());
            node4.addPermission(p4, i18n.PermissionAllow(), i18n.PermissionDeny());

            PermissionLeafNode node5 = new PermissionLeafNode();
            node5.setNodeName(i18n.ResourcePlanner());
            node5.addPermission(p5, i18n.PermissionAllow(), i18n.PermissionDeny());

            result.add(node1);
            result.add(node2);
            result.add(node3);
            result.add(node4);
            result.add(node5);

            callback.afterLoad(result);
        }
    }
}