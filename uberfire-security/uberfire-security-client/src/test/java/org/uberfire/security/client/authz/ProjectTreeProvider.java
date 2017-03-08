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
package org.uberfire.security.client.authz;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.uberfire.security.Resource;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceType;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.client.authz.tree.LoadCallback;
import org.uberfire.security.client.authz.tree.LoadOptions;
import org.uberfire.security.client.authz.tree.PermissionNode;
import org.uberfire.security.client.authz.tree.PermissionTreeProvider;
import org.uberfire.security.client.authz.tree.impl.PermissionLeafNode;
import org.uberfire.security.client.authz.tree.impl.PermissionResourceNode;

/**
 * Resource based tree provider
 */
public class ProjectTreeProvider implements PermissionTreeProvider {

    public static final ResourceType PROJECT_TYPE = () -> "project";
    public static final ResourceAction PROJECT_CREATE = () -> "create";
    public static final ResourceAction PROJECT_READ = () -> "read";
    public static final ResourceAction PROJECT_EDIT = () -> "edit";
    public static final ResourceAction PROJECT_DELETE = () -> "delete";

    private PermissionManager permissionManager;
    private List<Resource> projectList;

    public ProjectTreeProvider(PermissionManager permissionManager,
                               List<Resource> projectList) {
        this.permissionManager = permissionManager;
        this.projectList = projectList;
    }

    @Override
    public PermissionNode buildRootNode() {
        PermissionResourceNode rootNode = new PermissionResourceNode("Project",
                                                                     this);
        rootNode.setNodeName("Projects");
        rootNode.addPermission(newPermission(PROJECT_CREATE),
                               "Create");
        rootNode.addPermission(newPermission(PROJECT_READ),
                               "Read");
        rootNode.addPermission(newPermission(PROJECT_EDIT),
                               "Edit");
        rootNode.addPermission(newPermission(PROJECT_DELETE),
                               "Delete");
        return rootNode;
    }

    @Override
    public void loadChildren(PermissionNode parent,
                             LoadOptions options,
                             LoadCallback callback) {
        if (parent.getNodeName().equals("Projects")) {
            List<PermissionNode> nodes = getAllProjects().stream()
                    .filter(p -> match(p,
                                       options))
                    .map(this::toProjectNode)
                    .collect(Collectors.toList());

            callback.afterLoad(nodes);
        }
    }

    private Permission newPermission(ResourceAction action) {
        return permissionManager.createPermission(PROJECT_TYPE,
                                                  action,
                                                  true);
    }

    private Permission newPermission(Resource resource,
                                     ResourceAction action) {
        return permissionManager.createPermission(resource,
                                                  action,
                                                  true);
    }

    private boolean match(Resource project,
                          LoadOptions options) {
        Collection<String> includedIds = options.getResourceIds();

        if (includedIds == null || includedIds.isEmpty()) {
            return true;
        }
        for (String resourceId : includedIds) {
            if (project.getIdentifier().contains(resourceId)) {
                return true;
            }
        }
        return false;
    }

    private PermissionNode toProjectNode(Resource project) {
        PermissionLeafNode node = new PermissionLeafNode();
        node.setNodeName(project.getIdentifier());
        node.addPermission(newPermission(project,
                                         PROJECT_READ),
                           "Read");
        node.addPermission(newPermission(project,
                                         PROJECT_EDIT),
                           "Edit");
        node.addPermission(newPermission(project,
                                         PROJECT_DELETE),
                           "Delete");
        return node;
    }

    private List<Resource> getAllProjects() {
        return projectList;
    }
}
