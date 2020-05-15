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
package org.guvnor.common.services.backend.migration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.security.shared.api.GroupImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.authz.AuthorizationPolicyStorage;
import org.uberfire.backend.events.AuthorizationPolicyDeployedEvent;
import org.uberfire.backend.vfs.Path;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCollection;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.impl.authz.DefaultPermissionManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ACLMigrationToolTest {

    @Mock
    AuthorizationPolicyStorage policyStorage;

    @Mock
    OrganizationalUnitService organizationalUnitService;

    @Mock
    RepositoryService repositoryService;

    @Mock
    OrganizationalUnit orgUnit1;

    @Mock
    Repository repo1;

    @Mock
    Module module1;

    @Mock
    WorkspaceProject workspaceProject1;

    @Spy
    @InjectMocks
    ACLMigrationTool migrationTool;

    PermissionManager permissionManager;
    AuthorizationPolicy authorizationPolicy;

    @Before
    public void setUp() {
        permissionManager = new DefaultPermissionManager();
        authorizationPolicy = permissionManager.newAuthorizationPolicy().build();
        migrationTool = spy(new ACLMigrationTool(organizationalUnitService,
                                                 repositoryService,
                                                 permissionManager,
                                                 policyStorage));

        final Path repo1root = mock(Path.class);

        when(organizationalUnitService.getAllOrganizationalUnits()).thenReturn(Collections.singleton(orgUnit1));
        when(repositoryService.getAllRepositoriesFromAllUserSpaces()).thenReturn(Collections.singleton(repo1));

        when(orgUnit1.getIdentifier()).thenReturn("orgUnit1");
        when(orgUnit1.getResourceType()).thenReturn(OrganizationalUnit.RESOURCE_TYPE);
        when(orgUnit1.getGroups()).thenReturn(Collections.singleton("group1"));

        when(repo1.getIdentifier()).thenReturn("repo1");
        final Branch master = new Branch("master",
                                         repo1root);
        when(repo1.getBranch("master")).thenReturn(Optional.of(master));
        when(repo1.getDefaultBranch()).thenReturn(Optional.of(master));

        when(repo1.getResourceType()).thenReturn(Repository.RESOURCE_TYPE);
        final ArrayList<String> groupList = new ArrayList<>();
        groupList.add("group1");
        groupList.add("group2");
        when(repo1.getGroups()).thenReturn(groupList);

        when(migrationTool.isACLMigrationToolEnabled()).thenReturn(true);
    }

    @Test
    public void migrationTest() {
        migrationTool.onDeploy(new AuthorizationPolicyDeployedEvent(authorizationPolicy));

        verify(migrationTool).migrateOrgUnits(authorizationPolicy);
        verify(migrationTool).migrateRepositories(authorizationPolicy);
        verify(policyStorage).savePolicy(authorizationPolicy);

        assertEquals(authorizationPolicy.getRoles().size(),
                     0);
        assertEquals(authorizationPolicy.getGroups().size(),
                     2);

        PermissionCollection pc1 = authorizationPolicy.getPermissions(new GroupImpl("group1"));
        assertNotNull(pc1);
        assertEquals(pc1.collection().size(),
                     2);

        Permission p1 = pc1.get("orgunit.read.orgUnit1");
        assertNotNull(p1);
        assertEquals(p1.getResult(),
                     AuthorizationResult.ACCESS_GRANTED);

        Permission p2 = pc1.get("repository.read.repo1");
        assertNotNull(p2);
        assertEquals(p2.getResult(),
                     AuthorizationResult.ACCESS_GRANTED);

        PermissionCollection pc2 = authorizationPolicy.getPermissions(new GroupImpl("group2"));
        assertNotNull(pc2);
        assertEquals(pc2.collection().size(),
                     1);
    }

    @Test
    public void testMonitoringEnabled() {
        when(migrationTool.isACLMigrationToolEnabled()).thenReturn(false);
        migrationTool.onDeploy(new AuthorizationPolicyDeployedEvent(authorizationPolicy));
        verify(migrationTool, never()).migrateOrgUnits(any());
    }
}
