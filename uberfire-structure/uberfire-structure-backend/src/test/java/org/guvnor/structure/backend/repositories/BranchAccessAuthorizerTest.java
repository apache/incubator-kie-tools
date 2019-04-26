/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.backend.repositories;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.config.BranchPermissions;
import org.guvnor.structure.organizationalunit.config.RolePermissions;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorage;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.repositories.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.spaces.Space;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BranchAccessAuthorizerTest {

    @Mock
    private SpaceConfigStorageRegistry spaceConfigStorageRegistry;

    @Mock
    private ConfiguredRepositories configuredRepositories;

    @Mock
    private OrganizationalUnitService organizationalUnitService;

    private BranchAccessAuthorizer branchAccessAuthorizer;

    @Before
    public void setup() {
        branchAccessAuthorizer = new BranchAccessAuthorizer(spaceConfigStorageRegistry,
                                                            configuredRepositories,
                                                            organizationalUnitService);

        final Repository repository = mock(Repository.class);
        final List<Contributor> contributors = Arrays.asList(new Contributor("owner",
                                                                             ContributorType.OWNER),
                                                             new Contributor("admin",
                                                                             ContributorType.ADMIN),
                                                             new Contributor("contributor",
                                                                             ContributorType.CONTRIBUTOR));
        doReturn(contributors).when(repository).getContributors();
        doReturn(repository).when(configuredRepositories).getRepositoryByRepositoryAlias(new Space("space"),
                                                                                         "repositoryAlias");

        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        doReturn(new Space("space")).when(organizationalUnit).getSpace();
        doReturn(organizationalUnit).when(organizationalUnitService).getOrganizationalUnit("space");

        final Map<String, RolePermissions> permissionsByRole = new HashMap<>();
        permissionsByRole.put("OWNER",
                              new RolePermissions("OWNER",
                                                  true,
                                                  true,
                                                  true,
                                                  true));
        permissionsByRole.put("ADMIN",
                              new RolePermissions("ADMIN",
                                                  true,
                                                  true,
                                                  false,
                                                  true));
        permissionsByRole.put("CONTRIBUTOR",
                              new RolePermissions("CONTRIBUTOR",
                                                  true,
                                                  true,
                                                  false,
                                                  false));
        final BranchPermissions branchPermissions = new BranchPermissions("branch",
                                                                          permissionsByRole);

        final SpaceConfigStorage spaceConfigStorage = mock(SpaceConfigStorage.class);
        doReturn(branchPermissions).when(spaceConfigStorage).loadBranchPermissions("branch",
                                                                                   "repositoryIdentifier");
        doReturn(spaceConfigStorage).when(spaceConfigStorageRegistry).get("space");
    }

    @Test
    public void authorizeOwnerTest() {
        assertTrue(branchAccessAuthorizer.authorize("owner",
                                                    "space",
                                                    "repositoryIdentifier",
                                                    "repositoryAlias",
                                                    "branch",
                                                    BranchAccessAuthorizer.AccessType.READ));
        assertTrue(branchAccessAuthorizer.authorize("owner",
                                                    "space",
                                                    "repositoryIdentifier",
                                                    "repositoryAlias",
                                                    "branch",
                                                    BranchAccessAuthorizer.AccessType.WRITE));
        assertTrue(branchAccessAuthorizer.authorize("owner",
                                                    "space",
                                                    "repositoryIdentifier",
                                                    "repositoryAlias",
                                                    "branch",
                                                    BranchAccessAuthorizer.AccessType.DELETE));
    }

    @Test
    public void authorizeAdminTest() {
        assertTrue(branchAccessAuthorizer.authorize("admin",
                                                    "space",
                                                    "repositoryIdentifier",
                                                    "repositoryAlias",
                                                    "branch",
                                                    BranchAccessAuthorizer.AccessType.READ));
        assertTrue(branchAccessAuthorizer.authorize("admin",
                                                    "space",
                                                    "repositoryIdentifier",
                                                    "repositoryAlias",
                                                    "branch",
                                                    BranchAccessAuthorizer.AccessType.WRITE));
        assertFalse(branchAccessAuthorizer.authorize("admin",
                                                     "space",
                                                     "repositoryIdentifier",
                                                     "repositoryAlias",
                                                     "branch",
                                                     BranchAccessAuthorizer.AccessType.DELETE));
    }

    @Test
    public void authorizeContributorTest() {
        assertTrue(branchAccessAuthorizer.authorize("contributor",
                                                    "space",
                                                    "repositoryIdentifier",
                                                    "repositoryAlias",
                                                    "branch",
                                                    BranchAccessAuthorizer.AccessType.READ));
        assertTrue(branchAccessAuthorizer.authorize("contributor",
                                                    "space",
                                                    "repositoryIdentifier",
                                                    "repositoryAlias",
                                                    "branch",
                                                    BranchAccessAuthorizer.AccessType.WRITE));
        assertFalse(branchAccessAuthorizer.authorize("contributor",
                                                     "space",
                                                     "repositoryIdentifier",
                                                     "repositoryAlias",
                                                     "branch",
                                                     BranchAccessAuthorizer.AccessType.DELETE));
    }
}
