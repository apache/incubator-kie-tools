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
package org.guvnor.common.services.project.client.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectSearchService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.client.authz.tree.PermissionNode;
import org.uberfire.security.client.authz.tree.PermissionTree;
import org.uberfire.security.impl.authz.DefaultPermissionManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProjectTreeProviderTest {

    @Mock
    ProjectSearchService searchService;

    @Mock
    PermissionTree permissionTree;

    @Mock
    Project project1;

    @Mock
    Project project2;

    PermissionManager permissionManager;
    ProjectTreeProvider treeProvider;
    PermissionNode rootNode;

    @Before
    public void setup() {
        permissionManager = new DefaultPermissionManager();
        treeProvider = new ProjectTreeProvider(permissionManager,
                                               new CallerMock<>(searchService));
        rootNode = treeProvider.buildRootNode();
        rootNode.setPermissionTree(permissionTree);

        when(project1.getIdentifier()).thenReturn("p1");
        when(project2.getIdentifier()).thenReturn("p2");
        when(project1.getProjectName()).thenReturn("p1");
        when(project2.getProjectName()).thenReturn("p2");
        when(project1.getResourceType()).thenReturn(Project.RESOURCE_TYPE);
        when(project2.getResourceType()).thenReturn(Project.RESOURCE_TYPE);
        when(permissionTree.getChildrenResourceIds(any())).thenReturn(null);
        when(searchService.searchByName(anyString(),
                                        anyInt(),
                                        anyBoolean())).thenReturn(Arrays.asList(project1,
                                                                                project2));
    }

    @Test
    public void testRootNode() {
        assertEquals(rootNode.getPermissionList().size(),
                     5);
        checkDependencies(rootNode);
    }

    @Test
    public void testChildrenNodes() {
        rootNode.expand(children -> {
            verify(searchService).searchByName(anyString(),
                                               anyInt(),
                                               anyBoolean());
            for (PermissionNode child : children) {
                List<Permission> permissionList = child.getPermissionList();
                assertEquals(permissionList.size(),
                             4);
                checkDependencies(child);

                List<String> permissionNames = permissionList.stream()
                        .map(Permission::getName)
                        .collect(Collectors.toList());

                assertTrue(permissionNames.contains("project.read." + child.getNodeName()));
                assertTrue(permissionNames.contains("project.update." + child.getNodeName()));
                assertTrue(permissionNames.contains("project.delete." + child.getNodeName()));
                assertTrue(permissionNames.contains("project.build." + child.getNodeName()));
            }
        });
    }

    protected void checkDependencies(PermissionNode permissionNode) {
        for (Permission permission : permissionNode.getPermissionList()) {
            Collection<Permission> dependencies = permissionNode.getDependencies(permission);

            if (permission.getName().startsWith("project.read")) {
                assertEquals(dependencies.size(),
                             3);
            } else {
                assertNull(dependencies);
            }
        }
    }
}
