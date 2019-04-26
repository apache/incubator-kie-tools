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

import org.guvnor.structure.client.resources.i18n.CommonConstants;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitSearchService;
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

import static org.guvnor.structure.security.OrganizationalUnitAction.CREATE;
import static org.guvnor.structure.security.OrganizationalUnitAction.DELETE;
import static org.guvnor.structure.security.OrganizationalUnitAction.READ;
import static org.guvnor.structure.security.OrganizationalUnitAction.UPDATE;

/**
 * The {@link PermissionTreeProvider} plugin that brings {@link OrganizationalUnit} permissions into the ACL editor
 */
@ApplicationScoped
public class OrganizationalUnitTreeProvider implements PermissionTreeProvider {

    private CommonConstants i18n = CommonConstants.INSTANCE;
    private PermissionManager permissionManager;
    private int rootNodePosition = 0;
    private Caller<OrganizationalUnitSearchService> searchService;

    public OrganizationalUnitTreeProvider() {
    }

    @Inject
    public OrganizationalUnitTreeProvider(PermissionManager permissionManager,
                                          Caller<OrganizationalUnitSearchService> searchService) {
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
        PermissionResourceNode rootNode = new PermissionResourceNode(i18n.SpaceResource(),
                                                                     this);
        rootNode.setNodeName(i18n.SpacesNode());
        rootNode.setNodeFullName(i18n.SpacesHelp());
        rootNode.setPositionInTree(rootNodePosition);

        Permission readPermission = newPermission(READ);
        Permission updatePermission = newPermission(UPDATE);
        Permission deletePermission = newPermission(DELETE);
        Permission createPermission = newPermission(CREATE);

        rootNode.addPermission(readPermission,
                               i18n.SpaceActionRead());
        rootNode.addPermission(updatePermission,
                               i18n.SpaceActionUpdate());
        rootNode.addPermission(deletePermission,
                               i18n.SpaceActionDelete());
        rootNode.addPermission(createPermission,
                               i18n.SpaceActionCreate());

        rootNode.addDependencies(readPermission,
                                 updatePermission,
                                 deletePermission);
        return rootNode;
    }

    private Permission newPermission(ResourceAction action) {
        return permissionManager.createPermission(OrganizationalUnit.RESOURCE_TYPE,
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
                searchService.call((Collection<OrganizationalUnit> orgUnits) -> {
                    List<PermissionNode> children = buildPermissionNodes(orgUnits);
                    callback.afterLoad(children);
                }).searchById(resourceIds);
            } else {
                String namePattern = options.getNodeNamePattern();
                searchService.call((Collection<OrganizationalUnit> orgUnits) -> {
                    List<PermissionNode> children = buildPermissionNodes(orgUnits);
                    callback.afterLoad(children);
                }).searchByName(namePattern,
                                maxNodes,
                                false);
            }
        } else {
            callback.afterLoad(Collections.emptyList());
        }
    }

    private List<PermissionNode> buildPermissionNodes(Collection<OrganizationalUnit> orgUnits) {
        List<PermissionNode> nodes = new ArrayList<>();
        for (OrganizationalUnit ou : orgUnits) {
            nodes.add(toPermissionNode(ou));
        }
        return nodes;
    }

    private PermissionNode toPermissionNode(OrganizationalUnit ou) {
        PermissionLeafNode node = new PermissionLeafNode();
        node.setNodeName(ou.getName());

        Permission readPermission = newPermission(ou,
                                                  READ);
        Permission updatePermission = newPermission(ou,
                                                    UPDATE);
        Permission deletePermission = newPermission(ou,
                                                    DELETE);

        node.addPermission(readPermission,
                           i18n.SpaceActionRead());
        node.addPermission(updatePermission,
                           i18n.SpaceActionUpdate());
        node.addPermission(deletePermission,
                           i18n.SpaceActionDelete());

        node.addDependencies(readPermission,
                             updatePermission,
                             deletePermission);
        return node;
    }
}