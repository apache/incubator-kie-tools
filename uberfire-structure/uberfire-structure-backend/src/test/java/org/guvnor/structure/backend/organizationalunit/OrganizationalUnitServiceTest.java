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

package org.guvnor.structure.backend.organizationalunit;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.enterprise.event.Event;

import org.guvnor.structure.backend.backcompat.BackwardCompatibleUtil;
import org.guvnor.structure.backend.config.ConfigurationFactoryImpl;
import org.guvnor.structure.backend.repositories.ConfiguredRepositories;
import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.guvnor.structure.organizationalunit.NewOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.RemoveOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.RepoAddedToOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.RepoRemovedFromOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.UpdatedOrganizationalUnitEvent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.security.OrganizationalUnitAction;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationService;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.java.nio.fs.jgit.JGitPathImpl;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.spaces.Space;
import org.uberfire.spaces.SpacesAPI;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrganizationalUnitServiceTest {

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private SpacesAPI spacesAPI;

    @Mock
    private ConfigurationService configurationService;

    @Spy
    @InjectMocks
    private ConfigurationFactoryImpl configurationFactory;

    private OrganizationalUnitFactoryImpl organizationalUnitFactory;

    private BackwardCompatibleUtil backward;

    @Mock
    private Event<NewOrganizationalUnitEvent> newOrganizationalUnitEvent;

    @Mock
    private Event<RemoveOrganizationalUnitEvent> removeOrganizationalUnitEvent;

    @Mock
    private Event<RepoAddedToOrganizationalUnitEvent> repoAddedToOrgUnitEvent;

    @Mock
    private Event<RepoRemovedFromOrganizationalUnitEvent> repoRemovedFromOrgUnitEvent;

    @Mock
    private Event<UpdatedOrganizationalUnitEvent> updatedOrganizationalUnitEvent;

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private RepositoryService repoService;

    @Mock
    private OrganizationalUnit orgUnit;

    @Mock
    private IOService ioService;

    @Mock
    private ConfiguredRepositories configuredRepositories;

    private OrganizationalUnitServiceImpl organizationalUnitService;

    @Before
    public void setUp() throws Exception {
        backward = new BackwardCompatibleUtil(configurationFactory);
        organizationalUnitFactory = spy(new OrganizationalUnitFactoryImpl(repositoryService,
                                                                          backward,
                                                                          spacesAPI,
                                                                          configurationService,
                                                                          configurationFactory));
        organizationalUnitService = new OrganizationalUnitServiceImpl(configurationService,
                                                                      configurationFactory,
                                                                      organizationalUnitFactory,
                                                                      repoService,
                                                                      backward,
                                                                      newOrganizationalUnitEvent,
                                                                      removeOrganizationalUnitEvent,
                                                                      repoAddedToOrgUnitEvent,
                                                                      repoRemovedFromOrgUnitEvent,
                                                                      updatedOrganizationalUnitEvent,
                                                                      authorizationManager,
                                                                      spacesAPI,
                                                                      sessionInfo,
                                                                      ioService,
                                                                      configuredRepositories);

        organizationalUnitService.registeredOrganizationalUnits.put("A",
                                                                    orgUnit);
        when(authorizationManager.authorize(any(Resource.class),
                                            any(User.class))).thenReturn(false);
    }

    @Test
    public void testAllOrgUnits() throws Exception {
        Collection<OrganizationalUnit> orgUnits = organizationalUnitService.getAllOrganizationalUnits();
        assertEquals(orgUnits.size(),
                     1);
    }

    @Test
    public void testSecuredOrgUnits() throws Exception {
        Collection<OrganizationalUnit> orgUnits = organizationalUnitService.getOrganizationalUnits();
        assertEquals(orgUnits.size(),
                     0);
    }

    @Test
    public void createOrganizationalUnitWithDuplicatedNameTest() {
        setOUCreationPermission(true);

        final OrganizationalUnit ou = organizationalUnitService.createOrganizationalUnit("a",
                                                                                         "default.group.id");

        assertNull(ou);

        verify(organizationalUnitFactory,
               never()).newOrganizationalUnit(any());
    }

    @Test
    public void createValidOrganizationalUnitTest() {
        List<Contributor> contributors = new ArrayList<>();
        contributors.add(new Contributor("admin",
                                         ContributorType.ADMIN));

        setOUCreationPermission(true);

        final OrganizationalUnit ou = organizationalUnitService.createOrganizationalUnit("name",
                                                                                         "default.group.id",
                                                                                         new ArrayList<>(),
                                                                                         contributors);

        assertNotNull(ou);
        verify(organizationalUnitFactory).newOrganizationalUnit(any());
        assertEquals("name",
                     ou.getName());
        assertEquals("default.group.id",
                     ou.getDefaultGroupId());
        assertEquals(contributors,
                     ou.getContributors());

        final URI configFSUri = URI.create(SpacesAPI.resolveConfigFileSystemPath(SpacesAPI.Scheme.DEFAULT,
                                                                                 "name"));
        final Map<String, Object> env = new HashMap<String, Object>() {{
            put("init",
                Boolean.TRUE);
            put("internal",
                Boolean.TRUE);
        }};
        verify(ioService).newFileSystem(configFSUri,
                                        env);
    }

    @Test
    public void removeOrganizationalUnitRemovesRepositories() throws Exception {
        Repository repoA = mock(Repository.class);
        Repository repoB = mock(Repository.class);
        List<Repository> repos = Arrays.asList(repoA,
                                               repoB);
        when(repoA.getAlias()).thenReturn("A");
        when(repoB.getAlias()).thenReturn("B");

        Space space = new Space("A");
        when(orgUnit.getRepositories()).thenReturn(repos);
        when(orgUnit.getSpace()).thenReturn(space);

        ConfigGroup configGroup = new ConfigGroup();
        configGroup.setName("A");
        when(configurationService.getConfiguration(ConfigType.SPACE)).thenReturn(Collections.singletonList(configGroup));

        final JGitPathImpl configPath = mock(JGitPathImpl.class);
        final JGitFileSystem fileSystem = mock(JGitFileSystem.class);
        final Git git = mock(Git.class);
        final org.eclipse.jgit.lib.Repository repository = mock(org.eclipse.jgit.lib.Repository.class);
        final File directory = mock(File.class);
        final Path fsPath = mock(Path.class);
        doReturn(directory).when(directory).getParentFile();
        doReturn(directory).when(repository).getDirectory();
        doReturn(repository).when(git).getRepository();
        doReturn(git).when(fileSystem).getGit();
        doReturn(fsPath).when(fileSystem).getPath("");
        doReturn(fileSystem).when(configPath).getFileSystem();
        doReturn(configPath).when(ioService).get(any(URI.class));

        organizationalUnitService.removeOrganizationalUnit("A");

        verify(repoService).removeRepositories(eq(space),
                                               eq(new HashSet<>(Arrays.asList("A",
                                                                              "B"))));
        ArgumentCaptor<RemoveOrganizationalUnitEvent> eventCaptor = ArgumentCaptor.forClass(RemoveOrganizationalUnitEvent.class);
        verify(removeOrganizationalUnitEvent).fire(eventCaptor.capture());
        RemoveOrganizationalUnitEvent event = eventCaptor.getValue();
        assertEquals(repos,
                     event.getOrganizationalUnit().getRepositories());
        verify(ioService).delete(fsPath);
        verify(directory).delete();
        assertEquals(repos,
                     event.getOrganizationalUnit().getRepositories());
    }

    private void setOUCreationPermission(final boolean hasPermission) {
        when(authorizationManager.authorize(eq(OrganizationalUnit.RESOURCE_TYPE),
                                            eq(OrganizationalUnitAction.CREATE),
                                            any(User.class))).thenReturn(hasPermission);
    }
}