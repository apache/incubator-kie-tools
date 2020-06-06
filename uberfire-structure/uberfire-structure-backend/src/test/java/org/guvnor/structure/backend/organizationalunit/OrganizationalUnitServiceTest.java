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
import java.util.HashSet;
import java.util.List;

import javax.enterprise.event.Event;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.guvnor.structure.backend.organizationalunit.config.SpaceConfigStorageRegistryImpl;
import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.guvnor.structure.contributors.SpaceContributorsUpdatedEvent;
import org.guvnor.structure.organizationalunit.NewOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.RemoveOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.RepoAddedToOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.RepoRemovedFromOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.UpdatedOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorage;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.organizationalunit.config.SpaceInfo;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
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
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.java.nio.fs.jgit.JGitPathImpl;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.mocks.SessionInfoMock;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.spaces.Space;
import org.uberfire.spaces.SpacesAPI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrganizationalUnitServiceTest {

    private static final String SPACE_NAME = "space";
    private static final String SPACE_DESCRIPTION = "This is test space";
    private static final String DEFAULT_GROUP_ID = "default.group.id";

    private static final String REPO_A = "repoA";
    private static final String REPO_B = "repoB";

    private static final String ROLES = "security:roles";
    private static final String ADMIN = "admin";

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private SpacesAPI spacesAPI;

    @Mock
    private ConfigurationService configurationService;

    private OrganizationalUnitFactoryImpl organizationalUnitFactory;

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

    private SessionInfo sessionInfo;

    @Mock
    private RepositoryService repoService;

    @Mock
    private OrganizationalUnit orgUnit;

    @Mock
    private IOService ioService;

    @Mock
    private SpaceConfigStorageRegistry spaceConfigStorageRegistry;

    @Mock
    private SpaceConfigStorage spaceConfigStorage;

    private SpaceInfo spaceInfo;

    @Mock
    private FileSystem systemFS;

    @Mock
    private Event<SpaceContributorsUpdatedEvent> spaceContributorsUpdatedEvent;

    @Captor
    private ArgumentCaptor<List<Contributor>> contributorsCapture;

    private OrganizationalUnitServiceImpl organizationalUnitService;

    @Before
    public void setUp() throws Exception {

        spaceInfo = new SpaceInfo(SPACE_NAME,
                                  SPACE_DESCRIPTION,
                                  DEFAULT_GROUP_ID,
                                  new ArrayList<>(),
                                  new ArrayList<>(),
                                  new ArrayList<>());

        when(spaceConfigStorage.loadSpaceInfo()).thenReturn(spaceInfo);
        when(spaceConfigStorage.isInitialized()).thenReturn(true);

        when(spaceConfigStorageRegistry.get(anyString())).thenReturn(spaceConfigStorage);
        when(spaceConfigStorageRegistry.getBatch(anyString())).thenReturn(new SpaceConfigStorageRegistryImpl.SpaceStorageBatchImpl(spaceConfigStorage));
        when(spaceConfigStorageRegistry.exist(anyString())).thenReturn(true);

        sessionInfo = new SessionInfoMock();

        organizationalUnitFactory = spy(new OrganizationalUnitFactoryImpl(repositoryService,
                                                                          spacesAPI));
        organizationalUnitService = spy(new OrganizationalUnitServiceImpl(organizationalUnitFactory,
                                                                          repoService,
                                                                          newOrganizationalUnitEvent,
                                                                          removeOrganizationalUnitEvent,
                                                                          repoAddedToOrgUnitEvent,
                                                                          repoRemovedFromOrgUnitEvent,
                                                                          updatedOrganizationalUnitEvent,
                                                                          authorizationManager,
                                                                          spacesAPI,
                                                                          sessionInfo,
                                                                          ioService,
                                                                          spaceConfigStorageRegistry,
                                                                          systemFS,
                                                                          spaceContributorsUpdatedEvent,
                                                                          configurationService));

        when(authorizationManager.authorize(any(Resource.class),
                                            any(User.class))).thenReturn(false);

        doAnswer(invocation -> false).when(organizationalUnitService).isDeleted(any());

        doReturn(Paths.get("src/test/resources/niogit").toFile().toPath()).when(organizationalUnitService).getNiogitPath();
    }

    @Test
    public void testAllOrgUnits() {
        Collection<OrganizationalUnit> orgUnits = organizationalUnitService.getAllOrganizationalUnits();
        assertEquals(2,
                     orgUnits.size());
    }

    @Test
    public void testSecuredOrgUnits() {
        Collection<OrganizationalUnit> orgUnits = organizationalUnitService.getOrganizationalUnits();
        assertEquals(0,
                     orgUnits.size());
    }

    @Test
    public void testSecuredOrgUnitsWithPermission() {
        when(authorizationManager.authorize(any(Resource.class),
                                            any(User.class))).thenReturn(true);
        Collection<OrganizationalUnit> orgUnits = organizationalUnitService.getOrganizationalUnits();
        assertEquals(2,
                     orgUnits.size());
    }

    @Test
    public void testSecuredOrgUnitsToCollaborators() {
        when(orgUnit.getContributors()).thenReturn(Collections.singletonList(new Contributor(ADMIN, ContributorType.OWNER)));
        doReturn(Collections.singletonList(orgUnit)).when(organizationalUnitService).getAllOrganizationalUnits(anyBoolean());

        Collection<OrganizationalUnit> orgUnits = organizationalUnitService.getOrganizationalUnits();
        assertEquals(1,
                     orgUnits.size());
    }

    @Test
    public void createOrganizationalUnitWithDuplicatedNameTest() {
        setOUCreationPermission(true);

        doReturn(true)
                .when(organizationalUnitService)
                .spaceDirectoryExists(anyString());

        final OrganizationalUnit ou = organizationalUnitService.createOrganizationalUnit(SPACE_NAME,
                                                                                         DEFAULT_GROUP_ID);

        assertNull(ou);

        verify(organizationalUnitFactory,
               never()).newOrganizationalUnit(any());
    }

    @Test
    public void createValidOrganizationalUnitTest() {
        List<Contributor> contributors = new ArrayList<>();
        contributors.add(new Contributor(ADMIN,
                                         ContributorType.ADMIN));

        setOUCreationPermission(true);

        final OrganizationalUnit ou = organizationalUnitService.createOrganizationalUnit(SPACE_NAME,
                                                                                         DEFAULT_GROUP_ID,
                                                                                         new ArrayList<>(),
                                                                                         contributors,
                                                                                         SPACE_DESCRIPTION);

        assertNotNull(ou);
        verify(organizationalUnitFactory).newOrganizationalUnit(any());
        assertEquals(SPACE_NAME, ou.getName());
        assertEquals(SPACE_DESCRIPTION, ou.getDescription());
        assertEquals(DEFAULT_GROUP_ID, ou.getDefaultGroupId());
        assertEquals(contributors, ou.getContributors());
    }

    @Test
    public void removeOrganizationalUnitRemovesRepositories() throws Exception {
        Repository repoA = mock(Repository.class);
        Repository repoB = mock(Repository.class);
        List<Repository> repos = Arrays.asList(repoA, repoB);
        when(repoA.getAlias()).thenReturn(REPO_A);
        when(repoB.getAlias()).thenReturn(REPO_B);

        Space space = new Space(SPACE_NAME);
        when(orgUnit.getRepositories()).thenReturn(repos);
        when(orgUnit.getSpace()).thenReturn(space);

        ConfigGroup configGroup = new ConfigGroup();
        configGroup.setName(SPACE_NAME);
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

        doReturn(orgUnit).when(organizationalUnitService).getOrganizationalUnit(SPACE_NAME);

        organizationalUnitService.removeOrganizationalUnit(SPACE_NAME);

        verify(repoService).removeRepositories(eq(space), eq(new HashSet<>(Arrays.asList(REPO_A, REPO_B))));

        ArgumentCaptor<RemoveOrganizationalUnitEvent> eventCaptor = ArgumentCaptor.forClass(RemoveOrganizationalUnitEvent.class);
        verify(removeOrganizationalUnitEvent).fire(eventCaptor.capture());

        RemoveOrganizationalUnitEvent event = eventCaptor.getValue();
        assertEquals(repos, event.getOrganizationalUnit().getRepositories());
    }

    @Test
    public void testOnRemoveOrgUnit() {

        OrganizationalUnit organizationalUnit = new OrganizationalUnitImpl(SPACE_NAME, DEFAULT_GROUP_ID);
        RemoveOrganizationalUnitEvent event = new RemoveOrganizationalUnitEvent(organizationalUnit, ADMIN);
        this.organizationalUnitService.onRemoveOrganizationalUnit(event);
        verify(this.spaceConfigStorageRegistry).remove(SPACE_NAME);
    }

    @Test
    public void testUpdateOrganizationalUnit() {
        final String newGroupId = "newGroupId";

        OrganizationalUnit organizationalUnit = organizationalUnitService.updateOrganizationalUnit(SPACE_NAME, newGroupId, Collections.emptyList());

        Assertions.assertThat(organizationalUnit)
                .hasFieldOrPropertyWithValue("name", SPACE_NAME)
                .hasFieldOrPropertyWithValue("defaultGroupId", newGroupId);

        Assertions.assertThat(spaceInfo)
                .hasFieldOrPropertyWithValue("name", SPACE_NAME)
                .hasFieldOrPropertyWithValue("defaultGroupId", newGroupId);

        verify(spaceConfigStorage).startBatch();
        verify(spaceConfigStorage).saveSpaceInfo(eq(spaceInfo));
        verify(spaceConfigStorage).endBatch();
    }

    @Test
    public void testCheckChildrenRepositoryContributors() {
        OrganizationalUnit organizationalUnit = new OrganizationalUnitImpl(SPACE_NAME, DEFAULT_GROUP_ID);
        organizationalUnit.getContributors().add(new Contributor("contributor1", ContributorType.OWNER));
        organizationalUnit.getContributors().add(new Contributor("contributor2", ContributorType.ADMIN));
        organizationalUnit.getContributors().add(new Contributor("contributor3", ContributorType.CONTRIBUTOR));

        Repository repository = mock(Repository.class);
        final List<Contributor> repositoryContributors = new ArrayList<>();
        repositoryContributors.add(new Contributor("contributor1", ContributorType.OWNER));
        repositoryContributors.add(new Contributor("contributor2", ContributorType.CONTRIBUTOR));
        repositoryContributors.add(new Contributor("contributor4", ContributorType.ADMIN));
        doReturn(repositoryContributors).when(repository).getContributors();

        doReturn(Collections.singletonList(repository)).when(repoService).getAllRepositories(any());

        organizationalUnitService.checkChildrenRepositoryContributors(organizationalUnit);

        verify(repoService).updateContributors(same(repository), contributorsCapture.capture());
        final List<Contributor> updateRepositoryContributors = contributorsCapture.getValue();
        assertEquals(2, updateRepositoryContributors.size());
        assertEquals("contributor1", updateRepositoryContributors.get(0).getUsername());
        assertEquals(ContributorType.OWNER, updateRepositoryContributors.get(0).getType());
        assertEquals("contributor2", updateRepositoryContributors.get(1).getUsername());
        assertEquals(ContributorType.CONTRIBUTOR, updateRepositoryContributors.get(1).getType());
    }

    @Test
    public void testAddGroup() {
        OrganizationalUnit organizationalUnit = new OrganizationalUnitImpl(SPACE_NAME, DEFAULT_GROUP_ID);

        organizationalUnitService.addGroup(organizationalUnit, ROLES);

        ArgumentCaptor<UpdatedOrganizationalUnitEvent> captor = ArgumentCaptor.forClass(UpdatedOrganizationalUnitEvent.class);
        verify(updatedOrganizationalUnitEvent).fire(captor.capture());
        assertSame(ADMIN, captor.getValue().getUserName());

        Assertions.assertThat(captor.getValue().getOrganizationalUnit())
                .hasFieldOrPropertyWithValue("name", SPACE_NAME)
                .hasFieldOrPropertyWithValue("defaultGroupId", DEFAULT_GROUP_ID);

        Assertions.assertThat(captor.getValue().getOrganizationalUnit().getGroups())
                .hasSize(1)
                .containsExactly(ROLES);

        verify(spaceConfigStorage).startBatch();
        verify(spaceConfigStorage).saveSpaceInfo(eq(spaceInfo));
        verify(spaceConfigStorage).endBatch();

        Assertions.assertThat(spaceInfo.getSecurityGroups())
                .hasSize(1)
                .contains(ROLES);
    }

    @Test
    public void testRemoveGroup() {
        testAddGroup();

        OrganizationalUnit organizationalUnit = new OrganizationalUnitImpl(SPACE_NAME, DEFAULT_GROUP_ID);

        organizationalUnitService.removeGroup(organizationalUnit, ROLES);

        ArgumentCaptor<UpdatedOrganizationalUnitEvent> captor = ArgumentCaptor.forClass(UpdatedOrganizationalUnitEvent.class);
        verify(updatedOrganizationalUnitEvent, times(2)).fire(captor.capture());
        assertSame(ADMIN, captor.getValue().getUserName());

        Assertions.assertThat(captor.getValue().getOrganizationalUnit())
                .hasFieldOrPropertyWithValue("name", SPACE_NAME)
                .hasFieldOrPropertyWithValue("defaultGroupId", DEFAULT_GROUP_ID);

        Assertions.assertThat(captor.getValue().getOrganizationalUnit().getGroups())
                .isEmpty();

        verify(spaceConfigStorage, times(2)).startBatch();
        verify(spaceConfigStorage, times(2)).saveSpaceInfo(eq(spaceInfo));
        verify(spaceConfigStorage, times(2)).endBatch();
    }

    @Test
    public void testAddRepository() {
        OrganizationalUnit organizationalUnit = new OrganizationalUnitImpl(SPACE_NAME, DEFAULT_GROUP_ID);

        Repository repoA = mock(Repository.class);
        when(repoA.getAlias()).thenReturn(REPO_A);

        organizationalUnitService.addRepository(organizationalUnit, repoA);

        checkRepos(1, REPO_A, false);

        ArgumentCaptor<RepoAddedToOrganizationalUnitEvent> captor = ArgumentCaptor.forClass(RepoAddedToOrganizationalUnitEvent.class);
        verify(repoAddedToOrgUnitEvent).fire(captor.capture());
        assertSame(repoA, captor.getValue().getRepository());

        verify(spaceConfigStorage).startBatch();
        verify(spaceConfigStorage).saveSpaceInfo(eq(spaceInfo));
        verify(spaceConfigStorage).endBatch();

        Repository repoB = mock(Repository.class);
        when(repoB.getAlias()).thenReturn(REPO_B);

        organizationalUnitService.addRepository(organizationalUnit, repoB);

        checkRepos(2, REPO_B, false);

        captor = ArgumentCaptor.forClass(RepoAddedToOrganizationalUnitEvent.class);
        verify(repoAddedToOrgUnitEvent, times(2)).fire(captor.capture());
        assertSame(repoB, captor.getValue().getRepository());

        verify(spaceConfigStorage, times(2)).startBatch();
        verify(spaceConfigStorage, times(2)).saveSpaceInfo(eq(spaceInfo));
        verify(spaceConfigStorage, times(2)).endBatch();
    }

    @Test
    public void testRemoveRepository() {
        testAddRepository(); // Adding repos

        OrganizationalUnit organizationalUnit = new OrganizationalUnitImpl(SPACE_NAME, DEFAULT_GROUP_ID);

        Repository repoA = mock(Repository.class);
        when(repoA.getAlias()).thenReturn(REPO_A);

        organizationalUnitService.removeRepository(organizationalUnit, repoA);

        checkRepos(2, REPO_A, true);

        ArgumentCaptor<RepoRemovedFromOrganizationalUnitEvent> captor = ArgumentCaptor.forClass(RepoRemovedFromOrganizationalUnitEvent.class);
        verify(repoRemovedFromOrgUnitEvent).fire(captor.capture());
        assertSame(repoA, captor.getValue().getRepository());

        verify(spaceConfigStorage, times(3)).startBatch();
        verify(spaceConfigStorage, times(3)).saveSpaceInfo(eq(spaceInfo));
        verify(spaceConfigStorage, times(3)).endBatch();

        Repository repoB = mock(Repository.class);
        when(repoB.getAlias()).thenReturn(REPO_B);

        organizationalUnitService.removeRepository(organizationalUnit, repoB);

        checkRepos(2, REPO_B, true);

        captor = ArgumentCaptor.forClass(RepoRemovedFromOrganizationalUnitEvent.class);
        verify(repoRemovedFromOrgUnitEvent, times(2)).fire(captor.capture());
        assertSame(repoB, captor.getValue().getRepository());

        verify(spaceConfigStorage, times(4)).startBatch();
        verify(spaceConfigStorage, times(4)).saveSpaceInfo(eq(spaceInfo));
        verify(spaceConfigStorage, times(4)).endBatch();
    }

    private void checkRepos(final int expectedRepos, final String repoName, final boolean deleted) {
        Assertions.assertThat(spaceInfo.getRepositories())
                .hasSize(expectedRepos)
                .areAtLeastOne(new Condition<>(repositoryInfo -> repositoryInfo.getName().equals(repoName) && repositoryInfo.isDeleted() == deleted, "RepositoryInfo {name: '" + repoName + "', deleted: " + deleted + "}"));

    }

    private void setOUCreationPermission(final boolean hasPermission) {
        when(authorizationManager.authorize(eq(OrganizationalUnit.RESOURCE_TYPE),
                                            eq(OrganizationalUnitAction.CREATE),
                                            any(User.class))).thenReturn(hasPermission);
    }
}