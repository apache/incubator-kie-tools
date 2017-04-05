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

import org.guvnor.structure.security.RepositoryFeatures;
import org.kie.workbench.common.workbench.client.resources.i18n.DefaultWorkbenchConstants;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.client.authz.tree.LoadCallback;
import org.uberfire.security.client.authz.tree.LoadOptions;
import org.uberfire.security.client.authz.tree.PermissionNode;
import org.uberfire.security.client.authz.tree.PermissionTreeProvider;
import org.uberfire.security.client.authz.tree.impl.PermissionGroupNode;
import org.uberfire.security.client.authz.tree.impl.PermissionLeafNode;

import static org.guvnor.m2repo.security.MavenRepositoryPagedJarTableFeatures.JAR_DOWNLOAD;
import static org.kie.workbench.common.workbench.client.authz.WorkbenchFeatures.EDIT_SOURCES;
import static org.kie.workbench.common.workbench.client.authz.WorkbenchFeatures.PLANNER_AVAILABLE;

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
        rootNode.setNodeFullName(i18n.WorkbenchRootNodeHelp());
        rootNode.setProperty(NODE_TYPE,
                             NODE_ROOT);
        return rootNode;
    }

    @Override
    public void loadChildren(PermissionNode parent,
                             LoadOptions options,
                             LoadCallback callback) {

        if (parent.propertyEquals(NODE_TYPE,
                                  NODE_ROOT)) {
            List<PermissionNode> result = new ArrayList<>();

            PermissionLeafNode node1 = createPermissionLeafNode(RepositoryFeatures.CONFIGURE_REPOSITORY,
                                                                i18n.ConfigureRepositories(),
                                                                i18n.ConfigureRepositoriesHelp());
            PermissionLeafNode node2 = createPermissionLeafNode(EDIT_SOURCES,
                                                                i18n.DataModelerEditSources(),
                                                                i18n.DataModelerEditSourcesHelp());
            PermissionLeafNode node3 = createPermissionLeafNode(PLANNER_AVAILABLE,
                                                                i18n.ResourcePlanner(),
                                                                i18n.ResourcePlannerHelp());
            PermissionLeafNode node4 = createPermissionLeafNode(JAR_DOWNLOAD,
                                                                i18n.MavenRepositoryPagedJarTableDownloadJar(),
                                                                i18n.MavenRepositoryPagedJarTableDownloadJarHelp());

            result.add(node1);
            result.add(node2);
            result.add(node3);
            result.add(node4);

            callback.afterLoad(result);
        }
    }

    private PermissionLeafNode createPermissionLeafNode(String permissionName,
                                                        String nodeName,
                                                        String nodeHelp) {
        Permission permission = permissionManager.createPermission(permissionName,
                                                                   true);
        PermissionLeafNode node = new PermissionLeafNode();
        node.setNodeName(nodeName);
        node.setNodeFullName(nodeHelp);
        node.addPermission(permission,
                           i18n.PermissionAllow(),
                           i18n.PermissionDeny());
        return node;
    }
}
