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
import java.util.HashSet;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.PermissionCollection;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.authz.VotingStrategy;
import org.uberfire.security.client.authz.tree.PermissionNode;
import org.uberfire.security.client.authz.tree.PermissionTree;
import org.uberfire.security.client.authz.tree.PermissionTreeFactory;
import org.uberfire.security.client.authz.tree.PermissionTreeProvider;

@ApplicationScoped
public class DefaultPermissionTreeFactory implements PermissionTreeFactory {

    private PermissionManager permissionManager;
    private SyncBeanManager beanManager;
    private Collection<PermissionTreeProvider> permissionTreeProviderSet = new HashSet<>();

    public DefaultPermissionTreeFactory() {
    }

    @Inject
    public DefaultPermissionTreeFactory(PermissionManager permissionManager,
                                        SyncBeanManager beanManager) {
        this.permissionManager = permissionManager;
        this.beanManager = beanManager;
    }

    public DefaultPermissionTreeFactory(PermissionManager permissionManager,
                                        Collection<PermissionTreeProvider> permissionTreeProviderSet) {
        this.permissionManager = permissionManager;
        this.permissionTreeProviderSet = permissionTreeProviderSet;
    }

    @PostConstruct
    private void init() {
        for (SyncBeanDef<PermissionTreeProvider> beanDef : beanManager.lookupBeans(PermissionTreeProvider.class)) {
            PermissionTreeProvider provider = beanDef.getInstance();
            permissionTreeProviderSet.add(provider);
        }
    }

    @Override
    public PermissionTree createPermissionTree() {
        return createPermissionTree((PermissionCollection) null);
    }

    @Override
    public PermissionTree createPermissionTree(Role role) {
        AuthorizationPolicy policy = permissionManager.getAuthorizationPolicy();
        PermissionCollection pc = policy.getPermissions(role);
        return createPermissionTree(pc);
    }

    @Override
    public PermissionTree createPermissionTree(Group group) {
        AuthorizationPolicy policy = permissionManager.getAuthorizationPolicy();
        PermissionCollection pc = policy.getPermissions(group);
        return createPermissionTree(pc);
    }

    @Override
    public PermissionTree createPermissionTree(User user,
                                               VotingStrategy votingStrategy) {
        PermissionCollection pc = permissionManager.resolvePermissions(user,
                                                                       votingStrategy);
        return createPermissionTree(pc);
    }

    private PermissionTree createPermissionTree(PermissionCollection permissions) {
        List<PermissionNode> rootNodes = new ArrayList<>();
        for (PermissionTreeProvider provider : permissionTreeProviderSet) {
            if (provider.isActive()) {
                PermissionNode rootNode = provider.buildRootNode();
                if (rootNode != null) {
                    rootNodes.add(rootNode);
                }
            }
        }
        Collections.sort(rootNodes,
                         this::compareRootNode);
        return new DefaultPermissionTree(permissionManager,
                                         rootNodes,
                                         permissions);
    }

    private int compareRootNode(PermissionNode n1,
                                PermissionNode n2) {
        if (n1.getPositionInTree() > n2.getPositionInTree()) {
            return 1;
        }
        if (n1.getPositionInTree() < n2.getPositionInTree()) {
            return -1;
        }
        String name1 = n1.getNodeName();
        String name2 = n2.getNodeName();
        return name1.compareTo(name2);
    }
}
