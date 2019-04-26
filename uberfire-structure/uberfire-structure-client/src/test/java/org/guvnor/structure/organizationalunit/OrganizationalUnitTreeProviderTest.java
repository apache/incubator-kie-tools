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
package org.guvnor.structure.organizationalunit;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.structure.client.security.OrganizationalUnitTreeProvider;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class OrganizationalUnitTreeProviderTest {

    @Mock
    OrganizationalUnitSearchService searchService;

    @Mock
    PermissionTree permissionTree;

    @Mock
    OrganizationalUnit orgUnit1;

    @Mock
    OrganizationalUnit orgUnit2;

    PermissionManager permissionManager;
    OrganizationalUnitTreeProvider treeProvider;
    PermissionNode rootNode;

    @Before
    public void setup() {
        permissionManager = new DefaultPermissionManager();
        treeProvider = new OrganizationalUnitTreeProvider(permissionManager,
                                                          new CallerMock<>(searchService));
        rootNode = treeProvider.buildRootNode();
        rootNode.setPermissionTree(permissionTree);

        when(orgUnit1.getIdentifier()).thenReturn("ou1");
        when(orgUnit2.getIdentifier()).thenReturn("ou2");
        when(orgUnit1.getName()).thenReturn("ou1");
        when(orgUnit2.getName()).thenReturn("ou2");
        when(orgUnit1.getResourceType()).thenReturn(OrganizationalUnit.RESOURCE_TYPE);
        when(orgUnit2.getResourceType()).thenReturn(OrganizationalUnit.RESOURCE_TYPE);
        when(permissionTree.getChildrenResourceIds(any())).thenReturn(null);
        when(searchService.searchByName(anyString(),
                                        anyInt(),
                                        anyBoolean())).thenReturn(Arrays.asList(orgUnit1,
                                                                                orgUnit2));
    }

    @Test
    public void testRootNode() {
        assertEquals(rootNode.getPermissionList().size(),
                     4);
        checkDependencies(rootNode, 2);
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
                             3);
                checkDependencies(child, 2);

                List<String> permissionNames = permissionList.stream()
                        .map(Permission::getName)
                        .collect(Collectors.toList());

                assertTrue(permissionNames.contains("orgunit.read." + child.getNodeName()));
                assertTrue(permissionNames.contains("orgunit.update." + child.getNodeName()));
                assertTrue(permissionNames.contains("orgunit.delete." + child.getNodeName()));
            }
        });
    }

    protected void checkDependencies(PermissionNode permissionNode, int numberOfDependencies) {
        for (Permission permission : permissionNode.getPermissionList()) {
            Collection<Permission> dependencies = permissionNode.getDependencies(permission);

            if (permission.getName().startsWith("orgunit.read")) {
                assertEquals(dependencies.size(),
                             numberOfDependencies);
            } else {
                assertNull(dependencies);
            }
        }
    }
}
