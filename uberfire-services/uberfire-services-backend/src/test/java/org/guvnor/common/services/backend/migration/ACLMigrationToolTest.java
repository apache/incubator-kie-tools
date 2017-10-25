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

import java.util.Collections;
import javax.enterprise.inject.Instance;

import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
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
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCollection;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.impl.authz.DefaultPermissionManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ACLMigrationToolTest {

    @Mock
    AuthorizationPolicyStorage policyStorage;

    @Mock
    OrganizationalUnitService organizationalUnitService;

    @Mock
    RepositoryService repositoryService;

    @Mock
    ProjectService projectService;

    @Mock
    OrganizationalUnit orgUnit1;

    @Mock
    Repository repo1;

    @Mock
    Project project1;

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
                                                 mock(Instance.class),
                                                 permissionManager,
                                                 policyStorage));

        when(migrationTool.getProjectService()).thenReturn(projectService);
        when(organizationalUnitService.getAllOrganizationalUnits()).thenReturn(Collections.singleton(orgUnit1));
        when(repositoryService.getAllRepositories()).thenReturn(Collections.singleton(repo1));
        when(projectService.getAllProjects(repo1,
                                           "master")).thenReturn(Collections.singleton(project1));

        when(orgUnit1.getIdentifier()).thenReturn("orgUnit1");
        when(orgUnit1.getResourceType()).thenReturn(OrganizationalUnit.RESOURCE_TYPE);
        when(orgUnit1.getGroups()).thenReturn(Collections.singleton("group1"));

        when(repo1.getIdentifier()).thenReturn("repo1");
        when(repo1.getResourceType()).thenReturn(Repository.RESOURCE_TYPE);
        when(repo1.getGroups()).thenReturn(Collections.singleton("group1"));

        when(project1.getIdentifier()).thenReturn("project1");
        when(project1.getResourceType()).thenReturn(Project.RESOURCE_TYPE);
        when(project1.getGroups()).thenReturn(Collections.singleton("group2"));
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

        Permission p3 = pc2.get("project.read.project1");
        assertNotNull(p3);
        assertEquals(p3.getResult(),
                     AuthorizationResult.ACCESS_GRANTED);
    }
}
