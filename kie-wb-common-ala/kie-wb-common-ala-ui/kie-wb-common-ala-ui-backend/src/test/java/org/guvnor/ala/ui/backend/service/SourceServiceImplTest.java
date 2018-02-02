/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.backend.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.service.ModuleService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.spaces.Space;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SourceServiceImplTest {

    private static final int OU_SIZE = 7;

    private static final int REPOSITORIES_SIZE = 10;

    private static final String OU_NAME = "OU_NAME";

    private static final String REPO_NAME = "REPO_NAME";

    private static final String BRANCH_NAME = "BRANCH_NAME";

    private static final Space SPACE = new Space(OU_NAME);

    @Mock
    private OrganizationalUnitService organizationalUnitService;

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private ModuleService<? extends Module> moduleService;

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private User identity;

    private SourceServiceImpl service;

    private List<OrganizationalUnit> organizationalUnits;

    @Before
    public void setUp() {
        organizationalUnits = mockOrganizationalUnits(OU_NAME,
                                                      OU_SIZE);

        service = new SourceServiceImpl(organizationalUnitService,
                                        repositoryService,
                                        moduleService,
                                        authorizationManager,
                                        identity);
    }

    @Test
    public void testGetOrganizationalUnits() {
        when(organizationalUnitService.getOrganizationalUnits()).thenReturn(organizationalUnits);
        List<String> expectedResult = organizationalUnits.stream()
                .map(OrganizationalUnit::getName)
                .collect(Collectors.toList());

        Collection<String> result = service.getOrganizationUnits();
        assertEquals(expectedResult,
                     result);
    }

    @Test
    public void testGetRepositories() {
        List<Repository> repositories = mockRepositories("RepoName.",
                                                         REPOSITORIES_SIZE);
        OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        when(organizationalUnit.getName()).thenReturn(OU_NAME);

        //the organizational unit not exists.
        when(organizationalUnitService.getOrganizationalUnit(OU_NAME)).thenReturn(null);
        Collection<String> result = service.getRepositories(OU_NAME);
        //nothing is returned.
        assertTrue(result.isEmpty());

        //there organizational unit exists, but no repository is authorized.
        when(organizationalUnitService.getOrganizationalUnit(OU_NAME)).thenReturn(organizationalUnit);
        when(organizationalUnit.getRepositories()).thenReturn(repositories);

        result = service.getRepositories(OU_NAME);
        //nothing is returned since there are no authorized repository.
        assertTrue(result.isEmpty());

        //finally we authorize some repositories. Take some arbitrary indexes.
        List<Integer> authorizedIndexes = new ArrayList<>();
        List<String> authorizedNames = new ArrayList<>();
        authorizedIndexes.add(1);
        authorizedIndexes.add(4);
        authorizedIndexes.add(6);

        authorizedIndexes.forEach(index -> {
            when(authorizationManager.authorize(repositories.get(index),
                                                identity)).thenReturn(true);
            authorizedNames.add(repositories.get(index).getAlias());
        });

        result = service.getRepositories(OU_NAME);
        assertEquals(authorizedNames,
                     result);
    }

    @Test
    public void testGetBranches() {
        final List<Branch> branches = new ArrayList<>();
        branches.add(new Branch("branch1",
                                mock(Path.class)));
        branches.add(new Branch("branch2",
                                mock(Path.class)));
        branches.add(new Branch("branch3",
                                mock(Path.class)));
        Repository repository = mock(Repository.class);
        when(repository.getBranches()).thenReturn(branches);

        when(repositoryService.getRepositoryFromSpace(SPACE, REPO_NAME)).thenReturn(repository);

        final Collection<String> result = service.getBranches(SPACE, REPO_NAME);

        final List<String> originalBranchNames = new ArrayList<>();
        for (final Branch branch : branches) {
            originalBranchNames.add(branch.getName());
        }

        assertEquals(originalBranchNames,
                     result);
    }

    @Test
    public void testProjects() {
        final Repository repository = mock(Repository.class);
        final Branch branch = new Branch(BRANCH_NAME,
                                         mock(Path.class));
        doReturn(Optional.of(branch)).when(repository).getBranch(eq(BRANCH_NAME));
        @SuppressWarnings("unchecked")
        final Set<Module> modules = mock(Set.class);

        when(repositoryService.getRepositoryFromSpace(SPACE, REPO_NAME)).thenReturn(repository);
        when(moduleService.getAllModules(branch)).thenReturn(modules);

        final Collection<Module> result = service.getModules(SPACE,
                                                             REPO_NAME,
                                                             BRANCH_NAME);
        assertEquals(modules,
                     result);
    }

    private List<OrganizationalUnit> mockOrganizationalUnits(String suffix,
                                                             int count) {
        List<OrganizationalUnit> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            OrganizationalUnit ou = mock(OrganizationalUnit.class);
            when(ou.getName()).thenReturn(suffix + i);
            result.add(ou);
        }
        return result;
    }

    private List<Repository> mockRepositories(String suffix,
                                              int count) {
        List<Repository> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Repository repo = mock(Repository.class);
            when(repo.getAlias()).thenReturn(suffix + i);
            result.add(repo);
        }
        return result;
    }
}
