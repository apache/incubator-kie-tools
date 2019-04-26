/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.client.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.resources.ProjectResources;
import org.guvnor.common.services.project.client.resources.i18n.ProjectConstants;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositorySearchService;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.security.Resource;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.client.authz.tree.LoadCallback;
import org.uberfire.security.client.authz.tree.LoadOptions;
import org.uberfire.security.client.authz.tree.PermissionNode;
import org.uberfire.security.client.authz.tree.PermissionTreeProvider;
import org.uberfire.security.client.authz.tree.impl.PermissionLeafNode;
import org.uberfire.security.client.authz.tree.impl.PermissionResourceNode;

import static org.guvnor.structure.security.RepositoryAction.BUILD;
import static org.guvnor.structure.security.RepositoryAction.CREATE;
import static org.guvnor.structure.security.RepositoryAction.DELETE;
import static org.guvnor.structure.security.RepositoryAction.READ;
import static org.guvnor.structure.security.RepositoryAction.UPDATE;

/**
 * The {@link PermissionTreeProvider} plugin that brings {@link Repository} permissions into the ACL editor
 */
@ApplicationScoped
public class RepositoryTreeProvider implements PermissionTreeProvider {

    private PermissionManager permissionManager;
    private Caller<RepositorySearchService> searchService;
    private int rootNodePosition = 0;
    private ProjectConstants i18n = ProjectResources.CONSTANTS;

    public RepositoryTreeProvider() {
    }

    @Inject
    public RepositoryTreeProvider(PermissionManager permissionManager,
                                  Caller<RepositorySearchService> searchService) {
        this.permissionManager = permissionManager;
        this.searchService = searchService;
    }

    public int getRootNodePosition() {
        return rootNodePosition;
    }

    public void setRootNodePosition(int rootNodePosition) {
        this.rootNodePosition = rootNodePosition;
    }

    @Override
    public PermissionNode buildRootNode() {
        PermissionResourceNode rootNode = new PermissionResourceNode(i18n.ProjectResource(),
                                                                     this);
        rootNode.setNodeName(i18n.ProjectsNode());
        rootNode.setNodeFullName(i18n.ProjectsHelp());
        rootNode.setPositionInTree(rootNodePosition);

        Permission readPermission = newPermission(READ);
        Permission updatePermission = newPermission(UPDATE);
        Permission deletePermission = newPermission(DELETE);
        Permission createPermission = newPermission(CREATE);
        Permission buildPermission = newPermission(BUILD);

        rootNode.addPermission(readPermission,
                               i18n.ProjectActionRead());
        rootNode.addPermission(updatePermission,
                               i18n.ProjectActionUpdate());
        rootNode.addPermission(deletePermission,
                               i18n.ProjectActionDelete());
        rootNode.addPermission(createPermission,
                               i18n.ProjectActionCreate());
        rootNode.addPermission(buildPermission,
                               i18n.ProjectActionBuild());

        rootNode.addDependencies(readPermission,
                                 updatePermission,
                                 deletePermission,
                                 buildPermission);
        return rootNode;
    }

    private Permission newPermission(ResourceAction action) {
        return permissionManager.createPermission(Repository.RESOURCE_TYPE,
                                                  action,
                                                  true);
    }

    private Permission newPermission(Resource resource,
                                     ResourceAction action) {
        return permissionManager.createPermission(resource,
                                                  action,
                                                  true);
    }

    @Override
    public void loadChildren(PermissionNode parent,
                             LoadOptions options,
                             LoadCallback callback) {
        Collection<String> resourceIds = options.getResourceIds();
        int maxNodes = options.getMaxNodes();

        if (searchService != null) {
            if (resourceIds != null) {
                searchService.call((Collection<Repository> repositories) -> {
                    List<PermissionNode> children = buildPermissionNodes(repositories);
                    callback.afterLoad(children);
                }).searchById(resourceIds);
            } else {
                String pattern = options.getNodeNamePattern();
                searchService.call((Collection<Repository> repositories) -> {
                    List<PermissionNode> children = buildPermissionNodes(repositories);
                    callback.afterLoad(children);
                }).searchByAlias(pattern,
                                 maxNodes,
                                 false);
            }
        } else {
            callback.afterLoad(Collections.emptyList());
        }
    }

    private List<PermissionNode> buildPermissionNodes(Collection<Repository> repositories) {
        List<PermissionNode> nodes = new ArrayList<>();
        for (Repository p : repositories) {
            nodes.add(toPermissionNode(p));
        }
        return nodes;
    }

    private PermissionNode toPermissionNode(Repository r) {
        PermissionLeafNode node = new PermissionLeafNode();
        node.setNodeName(r.getAlias());

        Permission readPermission = newPermission(r,
                                                  READ);
        Permission updatePermission = newPermission(r,
                                                    UPDATE);
        Permission deletePermission = newPermission(r,
                                                    DELETE);
        Permission buildPermission = newPermission(r,
                                                   BUILD);

        node.addPermission(readPermission,
                           i18n.ProjectActionRead());
        node.addPermission(updatePermission,
                           i18n.ProjectActionUpdate());
        node.addPermission(deletePermission,
                           i18n.ProjectActionDelete());
        node.addPermission(buildPermission,
                           i18n.ProjectActionBuild());

        node.addDependencies(readPermission,
                             updatePermission,
                             deletePermission,
                             buildPermission);
        return node;
    }
}