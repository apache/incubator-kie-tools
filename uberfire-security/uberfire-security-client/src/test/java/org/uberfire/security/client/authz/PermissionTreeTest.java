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

import java.util.Arrays;
import java.util.List;
import javax.enterprise.inject.Instance;

import org.jboss.errai.security.shared.api.RoleImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.client.authz.tree.PermissionNode;
import org.uberfire.security.client.authz.tree.PermissionTree;
import org.uberfire.security.client.authz.tree.PermissionTreeFactory;
import org.uberfire.security.client.authz.tree.PermissionTreeProvider;
import org.uberfire.security.client.authz.tree.PermissionTreeVisitor;
import org.uberfire.security.client.authz.tree.impl.DefaultPermissionTreeFactory;
import org.uberfire.security.client.authz.tree.impl.PermissionLeafNode;
import org.uberfire.security.client.authz.tree.impl.PermissionResourceNode;
import org.uberfire.security.impl.authz.DefaultPermissionManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PermissionTreeTest {

    @Mock
    Resource projectA;

    @Mock
    Resource projectB;

    @Mock
    Instance<PermissionTreeProvider> permissionProviders;

    ProjectTreeProvider projectProvider;
    GeneralTreeProvider generalTreeProvider;
    PermissionTreeFactory permissionTreeFactory;
    PermissionManager permissionManager;

    @Before
    public void setUp() {
        when(projectA.getIdentifier()).thenReturn("p1");
        when(projectB.getIdentifier()).thenReturn("p2");
        when(projectA.getResourceType()).thenReturn(ProjectTreeProvider.PROJECT_TYPE);
        when(projectB.getResourceType()).thenReturn(ProjectTreeProvider.PROJECT_TYPE);

        permissionManager = new DefaultPermissionManager();
        projectProvider = new ProjectTreeProvider(permissionManager,
                                                  Arrays.asList(projectA,
                                                                projectB));
        generalTreeProvider = new GeneralTreeProvider(permissionManager);
        permissionTreeFactory = new DefaultPermissionTreeFactory(permissionManager,
                                                                 Arrays.asList(generalTreeProvider,
                                                                               projectProvider));

        permissionManager.setAuthorizationPolicy(
                permissionManager.newAuthorizationPolicy()
                        .role("admin")
                        .permission("project.create",
                                    true)
                        .permission("project.read",
                                    false)
                        .permission("project.read.p1",
                                    true)
                        .permission("project.edit",
                                    true)
                        .permission("project.edit.p1",
                                    false)
                        .permission("project.delete",
                                    true)
                        .permission("project.delete.p1",
                                    false)
                        .permission("general.categoryB",
                                    false)
                        .permission("general.categoryB.setting8",
                                    false)
                        .permission("general.categoryB.setting9",
                                    true)
                        .permission("general.categoryB.setting10",
                                    true)
                        .role("manager")
                        .permission("project.create",
                                    false)
                        .permission("project.read",
                                    true)
                        .build());
    }

    @Test
    public void testProjectNodeInitialization() {
        PermissionTree tree = permissionTreeFactory.createPermissionTree();
        List<PermissionNode> rootNodes = tree.getRootNodes();
        assertEquals(rootNodes.size(),
                     2);

        PermissionNode rootNode = rootNodes.get(1);
        assertEquals(rootNode.getNodeName(),
                     "Projects");
        assertEquals(rootNode.getPermissionList().size(),
                     4);
        assertTrue(rootNode instanceof PermissionResourceNode);
        assertEquals(rootNode.getLevel(),
                     0);

        rootNode.expand(children -> {
            assertEquals(children.size(),
                         2);
            PermissionNode projectNode = children.get(0);
            assertEquals(projectNode.getNodeName(),
                         "p1");
            assertTrue(projectNode instanceof PermissionLeafNode);
            assertEquals(projectNode.getPermissionList().size(),
                         3);
            assertEquals(projectNode.getLevel(),
                         1);
            assertEquals(rootNode.impliesName(projectNode).size(),
                         3);

            projectNode = children.get(1);
            assertEquals(projectNode.getNodeName(),
                         "p2");
            assertTrue(projectNode instanceof PermissionLeafNode);
            assertEquals(projectNode.getPermissionList().size(),
                         3);
            assertEquals(rootNode.impliesName(projectNode).size(),
                         3);
        });
    }

    @Test
    public void testProjectsLoading() {
        PermissionTree tree = permissionTreeFactory.createPermissionTree(new RoleImpl("admin"));
        PermissionNode rootNode = tree.getRootNodes().get(1);
        assertEquals(rootNode.getPermissionList().get(0).getResult(),
                     AuthorizationResult.ACCESS_GRANTED);
        assertEquals(rootNode.getPermissionList().get(1).getResult(),
                     AuthorizationResult.ACCESS_DENIED);
        assertEquals(rootNode.getPermissionList().get(2).getResult(),
                     AuthorizationResult.ACCESS_GRANTED);
        assertEquals(rootNode.getPermissionList().get(3).getResult(),
                     AuthorizationResult.ACCESS_GRANTED);

        rootNode.expand(children -> {
            assertEquals(children.size(),
                         1);
            PermissionNode projectNode = children.get(0);
            assertEquals(projectNode.getNodeName(),
                         "p1");
            assertEquals(projectNode.getPermissionList().size(),
                         3);
            assertEquals(projectNode.getPermissionList().get(0).getName(),
                         "project.read.p1");
            assertEquals(projectNode.getPermissionList().get(1).getName(),
                         "project.edit.p1");
            assertEquals(projectNode.getPermissionList().get(2).getName(),
                         "project.delete.p1");
            assertEquals(projectNode.getPermissionList().get(0).getResult(),
                         AuthorizationResult.ACCESS_GRANTED);
            assertEquals(projectNode.getPermissionList().get(1).getResult(),
                         AuthorizationResult.ACCESS_DENIED);
            assertEquals(projectNode.getPermissionList().get(2).getResult(),
                         AuthorizationResult.ACCESS_DENIED);
        });
    }

    @Test
    public void testVisitor() {
        PermissionTree tree = permissionTreeFactory.createPermissionTree(new RoleImpl("admin"));
        PermissionTreeVisitor visitor = mock(PermissionTreeVisitor.class);
        tree.accept(visitor);

        ArgumentCaptor<PermissionNode> argumentCaptor = ArgumentCaptor.forClass(PermissionNode.class);
        verify(visitor,
               times(15)).visit(argumentCaptor.capture());

        boolean projectsVisited = false;
        boolean p1Visited = false;
        boolean p2Visited = false;
        for (PermissionNode permissionNode : argumentCaptor.getAllValues()) {
            String name = permissionNode.getNodeName();
            if ("Projects".equals(name)) {
                projectsVisited = true;
            } else if ("p1".equals(name)) {
                p1Visited = true;
                assertEquals(permissionNode.getPermissionList().size(),
                             3);
                assertEquals(permissionNode.getPermissionList().get(0).getName(),
                             "project.read.p1");
                assertEquals(permissionNode.getPermissionList().get(1).getName(),
                             "project.edit.p1");
                assertEquals(permissionNode.getPermissionList().get(2).getName(),
                             "project.delete.p1");
                assertEquals(permissionNode.getPermissionList().get(0).getResult(),
                             AuthorizationResult.ACCESS_GRANTED);
                assertEquals(permissionNode.getPermissionList().get(1).getResult(),
                             AuthorizationResult.ACCESS_DENIED);
                assertEquals(permissionNode.getPermissionList().get(2).getResult(),
                             AuthorizationResult.ACCESS_DENIED);
            } else if ("p2".equals(name)) {
                p2Visited = true;
            }
        }
        assertTrue(projectsVisited);
        assertTrue(p1Visited);
        assertFalse(p2Visited);
    }

    @Test
    public void testInheritPermissionValue() {
        PermissionTree tree = permissionTreeFactory.createPermissionTree(new RoleImpl("admin"));
        PermissionTreeVisitor visitor = mock(PermissionTreeVisitor.class);
        tree.accept(visitor);

        ArgumentCaptor<PermissionNode> argumentCaptor = ArgumentCaptor.forClass(PermissionNode.class);
        verify(visitor,
               times(15)).visit(argumentCaptor.capture());

        for (PermissionNode node : argumentCaptor.getAllValues()) {

            // setting8 must be initialized to false according to its parent
            if (node.getNodeName().equals("Setting 8")) {
                assertEquals(node.getPermissionList().size(),
                             1);
                Permission p = node.getPermissionList().get(0);
                assertEquals(p.getResult(),
                             AuthorizationResult.ACCESS_DENIED);
            }
        }
    }
}
