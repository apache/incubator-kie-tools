/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.archetype.mgmt.backend.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.enterprise.event.Event;

import org.apache.maven.execution.MavenExecutionResult;
import org.appformer.maven.integration.embedder.MavenEmbedderException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.guvnor.common.services.project.backend.server.utils.PathUtil;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.structure.organizationalunit.NewOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.archetype.mgmt.backend.config.ArchetypeConfigStorage;
import org.kie.workbench.common.screens.archetype.mgmt.backend.config.ArchetypeConfigStorageImpl;
import org.kie.workbench.common.screens.archetype.mgmt.backend.maven.AbstractMavenCommand;
import org.kie.workbench.common.screens.archetype.mgmt.backend.maven.ArchetypeGenerateCommand;
import org.kie.workbench.common.screens.archetype.mgmt.backend.maven.ExecuteGoalsCommand;
import org.kie.workbench.common.screens.archetype.mgmt.backend.preference.ArchetypePreferencesManager;
import org.kie.workbench.common.screens.archetype.mgmt.shared.events.ArchetypeListUpdatedEvent;
import org.kie.workbench.common.screens.archetype.mgmt.shared.exceptions.ArchetypeAlreadyExistsException;
import org.kie.workbench.common.screens.archetype.mgmt.shared.exceptions.InvalidArchetypeException;
import org.kie.workbench.common.screens.archetype.mgmt.shared.exceptions.MavenExecutionException;
import org.kie.workbench.common.screens.archetype.mgmt.shared.model.Archetype;
import org.kie.workbench.common.screens.archetype.mgmt.shared.model.ArchetypeListOperation;
import org.kie.workbench.common.screens.archetype.mgmt.shared.model.ArchetypeStatus;
import org.kie.workbench.common.screens.archetype.mgmt.shared.model.PaginatedArchetypeList;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.StandardDeleteOption;
import org.uberfire.java.nio.fs.jgit.FileSystemLock;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.spaces.Space;

import static junit.framework.TestCase.assertSame;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ArchetypeServiceImplTest {

    private final String COMMON_ARCHETYPE_ALIAS = "myArchetype";
    private ArchetypeServiceImpl service;
    @Mock
    private IOService ioService;
    @Mock
    private RepositoryService repositoryService;
    @Mock
    private OrganizationalUnitService ouService;
    @Mock
    private Event<ArchetypeListUpdatedEvent> archetypeListUpdatedEvent;
    @Mock
    private ArchetypeConfigStorage archetypeConfigStorage;
    @Mock
    private PathUtil pathUtil;
    @Mock
    private ArchetypePreferencesManager archetypePreferencesManager;
    @Mock
    private KieModuleService moduleService;

    @Before
    public void setup() throws GitAPIException {
        service = spy(new ArchetypeServiceImpl(ioService,
                                               repositoryService,
                                               ouService,
                                               archetypeListUpdatedEvent,
                                               archetypeConfigStorage,
                                               pathUtil,
                                               archetypePreferencesManager,
                                               moduleService));

        doNothing().when(service).createTemporaryGitRepository(any(File.class));

        doReturn(mock(FileSystemLock.class)).when(service).createLock(any(File.class));
        doReturn(mock(Path.class)).when(service).createTempDirectory(anyString());
    }

    @Test
    public void startupValidationWhenArchetypesOrgUnitIsNotAvailableTest() {
        doReturn(Collections.emptyList()).when(ouService).getAllOrganizationalUnits(eq(false),
                                                                                    any());

        service.postConstruct();

        verify(archetypePreferencesManager, never()).initializeCustomPreferences();
        verify(service, never()).validateAll();
    }

    @Test
    public void startupValidationWhenArchetypesOrgUnitIsAvailableTest() {
        doReturn(Collections.nCopies(10, mock(OrganizationalUnit.class)))
                .when(ouService).getAllOrganizationalUnits(eq(false),
                                                           any());

        service.postConstruct();

        verify(archetypePreferencesManager).initializeCustomPreferences();
        verify(service).validateAll();
    }

    @Test
    public void newOrgUnitEventWhenArchetypesOrgUnitNotAvailableTest() {
        doReturn(Collections.emptyList()).when(ouService).getAllOrganizationalUnits(eq(false),
                                                                                    any());

        service.onNewOrganizationalUnitEvent(mock(NewOrganizationalUnitEvent.class));

        verify(archetypePreferencesManager, never()).initializeCustomPreference(anyString());
    }

    @Test
    public void newOrgUnitEventWhenIsArchetypesOrgUnitTest() {
        doReturn(Collections.nCopies(10, mock(OrganizationalUnit.class)))
                .when(ouService).getAllOrganizationalUnits(eq(false),
                                                           any());

        final NewOrganizationalUnitEvent event = mock(NewOrganizationalUnitEvent.class);
        final OrganizationalUnit orgUnit = mock(OrganizationalUnit.class);
        doReturn(ArchetypeConfigStorageImpl.ARCHETYPES_SPACE_NAME).when(orgUnit).getName();
        doReturn(orgUnit).when(event).getOrganizationalUnit();

        service.onNewOrganizationalUnitEvent(event);

        verify(archetypePreferencesManager, never()).initializeCustomPreference(anyString());
    }

    @Test
    public void newOrgUnitEventWhenShouldInitializePreferencesTest() {
        doReturn(Collections.nCopies(10, mock(OrganizationalUnit.class)))
                .when(ouService).getAllOrganizationalUnits(eq(false),
                                                           any());

        final NewOrganizationalUnitEvent event = mock(NewOrganizationalUnitEvent.class);
        final OrganizationalUnit orgUnit = mock(OrganizationalUnit.class);
        doReturn("new-org-unit").when(orgUnit).getName();
        doReturn(orgUnit).when(event).getOrganizationalUnit();

        service.onNewOrganizationalUnitEvent(event);

        verify(archetypePreferencesManager).initializeCustomPreference(anyString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addWhenInvalidArchetypeGavTest() {
        service.add(null);
    }

    @Test
    public void addWhenShouldDuplicateGavForTemplateTest() {
        final GAV gav = createGav();

        doNothing().when(service).add(any(GAV.class),
                                      any(GAV.class));

        service.add(gav);

        verify(service).add(gav,
                            gav);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addWhenInvalidTemplateGavTest() {
        service.add(createGav(),
                    null);
    }

    @Test(expected = ArchetypeAlreadyExistsException.class)
    public void addWhenArchetypeAlreadyExistsTest() {
        final GAV gav = createGav();

        mockArchetypesOrgUnit();

        doReturn(Collections.nCopies(10, mock(Repository.class)))
                .when(repositoryService).getAllRepositories(any(Space.class));

        final Archetype archetype = createArchetype();
        doReturn(archetype).when(archetypeConfigStorage).loadArchetype(anyString());

        service.add(gav);
    }

    @Test(expected = MavenExecutionException.class)
    public void addWhenArchetypeGenerateThrowsExceptionTest() throws MavenEmbedderException {
        doThrow(MavenExecutionException.class).when(service).executeMaven(any(ArchetypeGenerateCommand.class));

        service.add(createGav());
    }

    @Test
    public void addWhenExecuteMavenThrowsExceptionShouldLogOnlyTest() throws MavenEmbedderException {
        doThrow(MavenEmbedderException.class).when(service).executeMaven(any(ArchetypeGenerateCommand.class));

        try {
            service.add(createGav());
        } catch (Exception e) {
            fail("Should not have thrown any exception");
        }
    }

    @Test(expected = InvalidArchetypeException.class)
    public void addWhenModuleIsInvalidTest() throws MavenEmbedderException {
        doReturn(null).when(moduleService).resolveModule(any());
        doNothing().when(service).executeMaven(any(ArchetypeGenerateCommand.class));

        service.add(createGav());
    }

    @Test(expected = MavenExecutionException.class)
    public void addWhenExecuteGoalsThrowsExceptionTest() throws MavenEmbedderException {
        doReturn(mock(KieModule.class)).when(moduleService).resolveModule(any());
        doNothing().when(service).executeMaven(any(ArchetypeGenerateCommand.class));
        doThrow(MavenExecutionException.class).when(service).executeMaven(any(ExecuteGoalsCommand.class));

        service.add(createGav());
    }

    @Test
    public void addWhenShouldCreateArchetypesOrgUnitTest() throws MavenEmbedderException {
        final GAV gav = createGav();
        final GAV gavTemplate = createTemplateGav();

        doReturn(mock(KieModule.class)).when(moduleService).resolveModule(any());
        doNothing().when(service).executeMaven(any(AbstractMavenCommand.class));

        doReturn(Collections.emptyList()).when(ouService).getAllOrganizationalUnits(eq(false),
                                                                                    any());

        final Repository repository = mock(Repository.class);
        doReturn("myRepository").when(repository).getAlias();
        doReturn(repository).when(service).createArchetypeRepository(eq(gavTemplate),
                                                                     anyString());

        service.add(gav);

        verify(ouService).createOrganizationalUnit(eq(ArchetypeConfigStorageImpl.ARCHETYPES_SPACE_NAME), anyString());
        verify(archetypeConfigStorage).setup();
        verify(archetypePreferencesManager).initializeCustomPreferences();
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteWhenInvalidAliasTest() {
        service.delete(null);
    }

    @Test(expected = IllegalStateException.class)
    public void deleteWhenArchetypesOrgUnitIsNotAvailableTest() {
        doReturn(null)
                .when(ouService).getOrganizationalUnit(ArchetypeConfigStorageImpl.ARCHETYPES_SPACE_NAME);

        service.delete(COMMON_ARCHETYPE_ALIAS);
    }

    @Test
    public void deleteSuccessTest() {
        mockArchetypesOrgUnit();

        service.delete(COMMON_ARCHETYPE_ALIAS);

        verify(repositoryService).removeRepository(any(Space.class), eq(COMMON_ARCHETYPE_ALIAS));
        verify(archetypeConfigStorage).deleteArchetype(COMMON_ARCHETYPE_ALIAS);
        verify(archetypePreferencesManager).removeArchetype(COMMON_ARCHETYPE_ALIAS);
        verify(archetypeListUpdatedEvent).fire(new ArchetypeListUpdatedEvent(ArchetypeListOperation.DELETE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTemplateRepositoryWhenInvalidAliasTest() {
        service.getTemplateRepository(null);
    }

    @Test(expected = IllegalStateException.class)
    public void getTemplateRepositoryWhenOrgUnitIsNotAvailableTest() {
        mockArchetypesOrgUnitNotAvailable();

        service.getTemplateRepository(COMMON_ARCHETYPE_ALIAS);
    }

    @Test(expected = IllegalStateException.class)
    public void getTemplateRepositoryWhenRepositoryIsNotAvailableTest() {
        mockArchetypesOrgUnit();

        doReturn(null).when(repositoryService).getRepositoryFromSpace(any(Space.class),
                                                                      anyString());

        service.getTemplateRepository(COMMON_ARCHETYPE_ALIAS);
    }

    @Test
    public void getTemplateRepositorySuccessTest() {
        mockArchetypesOrgUnit();

        final Repository expectedRepository = mock(Repository.class);
        doReturn(expectedRepository).when(repositoryService).getRepositoryFromSpace(any(Space.class),
                                                                                    eq(COMMON_ARCHETYPE_ALIAS));

        final Repository repository = service.getTemplateRepository(COMMON_ARCHETYPE_ALIAS);

        assertSame(expectedRepository,
                   repository);
    }

    @Test(expected = IllegalStateException.class)
    public void createArchetypeRepositoryWhenOrgUnitIsNotAvailableTest() {
        mockArchetypesOrgUnitNotAvailable();

        service.createArchetypeRepository(eq(createTemplateGav()),
                                          anyString());
    }

    @Test
    public void createArchetypeRepositorySuccessTest() {
        final GAV templateGav = createTemplateGav();

        final OrganizationalUnit orgUnit = mockArchetypesOrgUnit();

        final Repository expectedRepository = mock(Repository.class);
        final Branch branch = mock(Branch.class);
        doReturn(Optional.of(branch)).when(expectedRepository).getDefaultBranch();
        doReturn(expectedRepository)
                .when(repositoryService).createRepository(eq(orgUnit),
                                                          eq(GitRepository.SCHEME.toString()),
                                                          eq(templateGav.toString()),
                                                          any(RepositoryEnvironmentConfigurations.class));

        final Git git = mock(Git.class);
        doReturn(git).when(service).getGitFromBranch(branch);
        doNothing().when(git).removeRemote(anyString(),
                                           anyString());

        final Repository repository = service.createArchetypeRepository(templateGav,
                                                                        "repository-uri");

        assertSame(expectedRepository,
                   repository);

        verify(git).removeRemote(anyString(),
                                 anyString());
    }

    @Test(expected = MavenExecutionException.class)
    public void throwMavenExecutionExceptionEmptyTest() {
        service.throwMavenExecutionException(Collections.emptyList());
    }

    @Test(expected = MavenExecutionException.class)
    public void throwMavenExecutionExceptionWithContentTest() {
        service.throwMavenExecutionException(Collections.nCopies(10, mock(Throwable.class)));
    }

    @Test(expected = MavenExecutionException.class)
    public void executeMavenWhenHasExceptionsTest() throws MavenEmbedderException {
        final AbstractMavenCommand command = mock(AbstractMavenCommand.class);
        final MavenExecutionResult result = mock(MavenExecutionResult.class);
        doReturn(true).when(result).hasExceptions();
        doReturn(result).when(command).execute();

        service.executeMaven(command);
    }

    @Test
    public void executeMavenSuccessTest() throws MavenEmbedderException {
        final AbstractMavenCommand command = mock(AbstractMavenCommand.class);
        doReturn(mock(MavenExecutionResult.class)).when(command).execute();

        try {
            service.executeMaven(command);
        } catch (Exception e) {
            fail("Should not have thrown any exception");
        }
    }

    @Test
    public void validateAllWhenExecuteMavenThrowsExceptionTest() throws MavenEmbedderException {
        mockArchetypesOrgUnit();
        doReturn(Collections.singletonList(mock(Repository.class)))
                .when(repositoryService).getAllRepositories(any(Space.class));
        doReturn(mock(Path.class)).when(service).unpackArchetype(anyString());

        doThrow(MavenEmbedderException.class).when(service).executeMaven(any(ExecuteGoalsCommand.class));

        final Archetype archetype = createArchetypeWithStatus(ArchetypeStatus.INVALID);
        doReturn(archetype).when(archetypeConfigStorage).loadArchetype(anyString());
        doNothing().when(archetypeConfigStorage).saveArchetype(archetype);

        service.validateAll();

        verify(archetypePreferencesManager).enableArchetype(anyString(),
                                                            eq(false),
                                                            eq(true));
        verify(archetypePreferencesManager).addArchetype(anyString());
        verify(archetypeConfigStorage).loadArchetype(anyString());
        verify(archetypeConfigStorage).saveArchetype(any(Archetype.class));
    }

    @Test
    public void validateAllWhenOnlyOneAvailableTest() throws MavenEmbedderException {
        mockArchetypesOrgUnit();
        doReturn(Collections.singletonList(mock(Repository.class)))
                .when(repositoryService).getAllRepositories(any(Space.class));
        doReturn(mock(Path.class)).when(service).unpackArchetype(anyString());
        doNothing().when(service).executeMaven(any(ExecuteGoalsCommand.class));

        final Archetype archetype = createArchetypeWithStatus(ArchetypeStatus.VALID);
        doReturn(archetype).when(archetypeConfigStorage).loadArchetype(anyString());
        doNothing().when(archetypeConfigStorage).saveArchetype(archetype);

        service.validateAll();

        verify(archetypePreferencesManager).enableArchetype(anyString(),
                                                            eq(true),
                                                            eq(true));

        verify(archetypePreferencesManager).setDefaultArchetype(anyString());

        verify(archetypePreferencesManager).addArchetype(anyString());
        verify(archetypeConfigStorage).loadArchetype(anyString());
        verify(archetypeConfigStorage).saveArchetype(any(Archetype.class));
    }

    @Test
    public void validateAllWhenManyAvailableTest() throws MavenEmbedderException {
        mockArchetypesOrgUnit();
        doReturn(Collections.nCopies(10, mock(Repository.class)))
                .when(repositoryService).getAllRepositories(any(Space.class));
        doReturn(mock(Path.class)).when(service).unpackArchetype(anyString());
        doNothing().when(service).executeMaven(any(ExecuteGoalsCommand.class));

        final Archetype archetype = createArchetypeWithStatus(ArchetypeStatus.VALID);
        doReturn(archetype).when(archetypeConfigStorage).loadArchetype(anyString());
        doNothing().when(archetypeConfigStorage).saveArchetype(archetype);

        service.validateAll();

        verify(archetypePreferencesManager, times(10)).enableArchetype(anyString(),
                                                                       eq(true),
                                                                       eq(false));

        verify(archetypePreferencesManager, never()).setDefaultArchetype(anyString());

        verify(archetypePreferencesManager, times(10)).addArchetype(anyString());
        verify(archetypeConfigStorage, times(10)).loadArchetype(anyString());
        verify(archetypeConfigStorage, times(10)).saveArchetype(any(Archetype.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateWhenInvalidAliasTest() {
        service.validate(null);
    }

    @Test(expected = IllegalStateException.class)
    public void validateWhenOrgUnitIsNotAvailableTest() {
        mockArchetypesOrgUnitNotAvailable();

        service.validate(COMMON_ARCHETYPE_ALIAS);
    }

    @Test(expected = IllegalStateException.class)
    public void validateWhenRepositoryIsNotAvailableTest() {
        mockArchetypesOrgUnit();

        doReturn(null).when(repositoryService).getRepositoryFromSpace(any(Space.class),
                                                                      anyString());

        service.validate(COMMON_ARCHETYPE_ALIAS);
    }

    @Test
    public void validateWhenExecuteMavenThrowsExceptionTest() throws MavenEmbedderException {
        mockArchetypesOrgUnit();
        doReturn(mock(Repository.class)).when(repositoryService).getRepositoryFromSpace(any(Space.class),
                                                                                        eq(COMMON_ARCHETYPE_ALIAS));
        doReturn(mock(Path.class)).when(service).unpackArchetype(anyString());

        doThrow(MavenEmbedderException.class).when(service).executeMaven(any(ExecuteGoalsCommand.class));

        final Archetype archetype = createArchetypeWithStatus(ArchetypeStatus.INVALID);
        doReturn(archetype).when(archetypeConfigStorage).loadArchetype(anyString());
        doNothing().when(archetypeConfigStorage).saveArchetype(archetype);

        service.validate(COMMON_ARCHETYPE_ALIAS);

        verify(archetypePreferencesManager).enableArchetype(anyString(),
                                                            eq(false),
                                                            eq(true));
        verify(archetypePreferencesManager).addArchetype(anyString());
        verify(archetypeConfigStorage).loadArchetype(anyString());
        verify(archetypeConfigStorage).saveArchetype(any(Archetype.class));
        verify(archetypeListUpdatedEvent).fire(new ArchetypeListUpdatedEvent(ArchetypeListOperation.VALIDATE));
    }

    @Test
    public void validateSuccessTest() throws MavenEmbedderException {
        mockArchetypesOrgUnit();
        doReturn(mock(Repository.class)).when(repositoryService).getRepositoryFromSpace(any(Space.class),
                                                                                        eq(COMMON_ARCHETYPE_ALIAS));
        doReturn(mock(Path.class)).when(service).unpackArchetype(anyString());
        doNothing().when(service).executeMaven(any(ExecuteGoalsCommand.class));

        final Archetype archetype = createArchetypeWithStatus(ArchetypeStatus.VALID);
        doReturn(archetype).when(archetypeConfigStorage).loadArchetype(anyString());
        doNothing().when(archetypeConfigStorage).saveArchetype(archetype);

        service.validate(COMMON_ARCHETYPE_ALIAS);

        verify(archetypePreferencesManager).enableArchetype(anyString(),
                                                            eq(true),
                                                            eq(false));

        verify(archetypePreferencesManager).addArchetype(anyString());
        verify(archetypeConfigStorage).loadArchetype(anyString());
        verify(archetypeConfigStorage).saveArchetype(any(Archetype.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void listWhenInvalidPageTest() {
        service.list(null, 1, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void listWhenInvalidPageSizeTest() {
        service.list(0, null, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void listWhenInvalidStatusTest() {
        service.list(0, 1, "", null);
    }

    @Test
    public void listAllTest() {
        mockArchetypesOrgUnit();
        doReturn(Collections.nCopies(10, mock(Repository.class)))
                .when(repositoryService).getAllRepositories(any(Space.class));
        doReturn(createArchetype()).when(archetypeConfigStorage).loadArchetype(anyString());

        final PaginatedArchetypeList result = service.list(0, 0, "");

        assertThat(result.getTotal()).isEqualTo(10);
        assertThat(result.getArchetypes()).hasSize(10);
        assertThat(result.getPageNumber()).isZero();
        assertThat(result.getPageSize()).isZero();
    }

    @Test
    public void listAllWithFilterTest() {
        mockArchetypesOrgUnit();

        final Archetype archetype1 = createArchetypeWithAlias("findme");
        final Archetype archetype2 = createArchetypeWithAlias("hidden");

        final List<Archetype> archetypes = new ArrayList<>();
        archetypes.addAll(Collections.nCopies(26, archetype1));
        archetypes.addAll(Collections.nCopies(30, archetype2));

        final List<Repository> repositories = new ArrayList<>();
        archetypes.forEach(archetype -> {
            doReturn(archetype).when(archetypeConfigStorage).loadArchetype(archetype.getAlias());
            final Repository repository = mock(Repository.class);
            doReturn(archetype.getAlias()).when(repository).getAlias();
            repositories.add(repository);
        });

        doReturn(repositories).when(repositoryService).getAllRepositories(any(Space.class));

        final PaginatedArchetypeList result = service.list(0, 0, "findme");

        assertThat(result.getTotal()).isEqualTo(26);
        assertThat(result.getArchetypes()).hasSize(26);
        assertThat(result.getPageNumber()).isZero();
        assertThat(result.getPageSize()).isZero();
    }

    @Test
    public void listAllPaginatedTest() {
        mockArchetypesOrgUnit();
        doReturn(Collections.nCopies(13, mock(Repository.class)))
                .when(repositoryService).getAllRepositories(any(Space.class));
        doReturn(createArchetype()).when(archetypeConfigStorage).loadArchetype(anyString());

        PaginatedArchetypeList result = service.list(0, 5, "");

        assertThat(result.getTotal()).isEqualTo(13);
        assertThat(result.getArchetypes()).hasSize(5);
        assertThat(result.getPageNumber()).isEqualTo(0);
        assertThat(result.getPageSize()).isEqualTo(5);

        result = service.list(1, 5, "");

        assertThat(result.getTotal()).isEqualTo(13);
        assertThat(result.getArchetypes()).hasSize(5);
        assertThat(result.getPageNumber()).isEqualTo(1);
        assertThat(result.getPageSize()).isEqualTo(5);

        result = service.list(2, 5, "");

        assertThat(result.getTotal()).isEqualTo(13);
        assertThat(result.getArchetypes()).hasSize(3);
        assertThat(result.getPageNumber()).isEqualTo(2);
        assertThat(result.getPageSize()).isEqualTo(5);
    }

    @Test
    public void listAllWithFilterPaginatedTest() {
        mockArchetypesOrgUnit();

        final Archetype archetype1 = createArchetypeWithAlias("findme");
        final Archetype archetype2 = createArchetypeWithAlias("hidden");

        final List<Archetype> archetypes = new ArrayList<>();
        archetypes.addAll(Collections.nCopies(13, archetype1));
        archetypes.addAll(Collections.nCopies(30, archetype2));

        final List<Repository> repositories = new ArrayList<>();
        archetypes.forEach(archetype -> {
            doReturn(archetype).when(archetypeConfigStorage).loadArchetype(archetype.getAlias());
            final Repository repository = mock(Repository.class);
            doReturn(archetype.getAlias()).when(repository).getAlias();
            repositories.add(repository);
        });

        final String filterText = "findme";

        doReturn(repositories).when(repositoryService).getAllRepositories(any(Space.class));

        PaginatedArchetypeList result = service.list(0, 5, filterText);

        assertThat(result.getTotal()).isEqualTo(13);
        assertThat(result.getArchetypes()).hasSize(5);
        assertThat(result.getPageNumber()).isEqualTo(0);
        assertThat(result.getPageSize()).isEqualTo(5);

        result = service.list(1, 5, filterText);

        assertThat(result.getTotal()).isEqualTo(13);
        assertThat(result.getArchetypes()).hasSize(5);
        assertThat(result.getPageNumber()).isEqualTo(1);
        assertThat(result.getPageSize()).isEqualTo(5);

        result = service.list(2, 5, filterText);

        assertThat(result.getTotal()).isEqualTo(13);
        assertThat(result.getArchetypes()).hasSize(3);
        assertThat(result.getPageNumber()).isEqualTo(2);
        assertThat(result.getPageSize()).isEqualTo(5);
    }

    @Test
    public void listAllWithStatusTest() {
        mockArchetypesOrgUnit();

        final Archetype archetype1 = createArchetypeWithFields("findme", ArchetypeStatus.VALID);
        final Archetype archetype2 = createArchetypeWithFields("hidden", ArchetypeStatus.INVALID);

        final List<Archetype> archetypes = new ArrayList<>();
        archetypes.addAll(Collections.nCopies(12, archetype1));
        archetypes.addAll(Collections.nCopies(13, archetype2));

        final List<Repository> repositories = new ArrayList<>();
        archetypes.forEach(archetype -> {
            doReturn(archetype).when(archetypeConfigStorage).loadArchetype(archetype.getAlias());
            final Repository repository = mock(Repository.class);
            doReturn(archetype.getAlias()).when(repository).getAlias();
            repositories.add(repository);
        });

        doReturn(repositories).when(repositoryService).getAllRepositories(any(Space.class));

        final PaginatedArchetypeList result = service.list(0, 0, "", ArchetypeStatus.VALID);

        assertThat(result.getTotal()).isEqualTo(12);
        assertThat(result.getArchetypes()).hasSize(12);
        assertThat(result.getPageNumber()).isZero();
        assertThat(result.getPageSize()).isZero();
    }

    @Test
    public void listAllWithFilterAndStatusTest() {
        mockArchetypesOrgUnit();

        final Archetype archetype1 = createArchetypeWithFields("findme", ArchetypeStatus.VALID);
        final Archetype archetype2 = createArchetypeWithFields("tryfindme", ArchetypeStatus.INVALID);
        final Archetype archetype3 = createArchetypeWithFields("hidden", ArchetypeStatus.VALID);
        final Archetype archetype4 = createArchetypeWithFields("hidden", ArchetypeStatus.INVALID);

        final List<Archetype> archetypes = new ArrayList<>();
        archetypes.addAll(Collections.nCopies(12, archetype1));
        archetypes.addAll(Collections.nCopies(13, archetype2));
        archetypes.addAll(Collections.nCopies(14, archetype3));
        archetypes.addAll(Collections.nCopies(15, archetype4));

        final List<Repository> repositories = new ArrayList<>();
        archetypes.forEach(archetype -> {
            doReturn(archetype).when(archetypeConfigStorage).loadArchetype(archetype.getAlias());
            final Repository repository = mock(Repository.class);
            doReturn(archetype.getAlias()).when(repository).getAlias();
            repositories.add(repository);
        });

        doReturn(repositories).when(repositoryService).getAllRepositories(any(Space.class));

        final PaginatedArchetypeList result = service.list(0, 0, "findme", ArchetypeStatus.VALID);

        assertThat(result.getTotal()).isEqualTo(12);
        assertThat(result.getArchetypes()).hasSize(12);
        assertThat(result.getPageNumber()).isZero();
        assertThat(result.getPageSize()).isZero();
    }

    @Test
    public void listAllWithStatusPaginatedTest() {
        mockArchetypesOrgUnit();

        final Archetype archetype1 = createArchetypeWithFields("findme", ArchetypeStatus.VALID);
        final Archetype archetype2 = createArchetypeWithFields("tryfindme", ArchetypeStatus.INVALID);

        final List<Archetype> archetypes = new ArrayList<>();
        archetypes.addAll(Collections.nCopies(13, archetype1));
        archetypes.addAll(Collections.nCopies(5, archetype2));

        final List<Repository> repositories = new ArrayList<>();
        archetypes.forEach(archetype -> {
            doReturn(archetype).when(archetypeConfigStorage).loadArchetype(archetype.getAlias());
            final Repository repository = mock(Repository.class);
            doReturn(archetype.getAlias()).when(repository).getAlias();
            repositories.add(repository);
        });

        doReturn(repositories).when(repositoryService).getAllRepositories(any(Space.class));

        PaginatedArchetypeList result = service.list(0, 5, "", ArchetypeStatus.VALID);

        assertThat(result.getTotal()).isEqualTo(13);
        assertThat(result.getArchetypes()).hasSize(5);
        assertThat(result.getPageNumber()).isEqualTo(0);
        assertThat(result.getPageSize()).isEqualTo(5);

        result = service.list(1, 5, "", ArchetypeStatus.VALID);

        assertThat(result.getTotal()).isEqualTo(13);
        assertThat(result.getArchetypes()).hasSize(5);
        assertThat(result.getPageNumber()).isEqualTo(1);
        assertThat(result.getPageSize()).isEqualTo(5);

        result = service.list(2, 5, "", ArchetypeStatus.VALID);

        assertThat(result.getTotal()).isEqualTo(13);
        assertThat(result.getArchetypes()).hasSize(3);
        assertThat(result.getPageNumber()).isEqualTo(2);
        assertThat(result.getPageSize()).isEqualTo(5);
    }

    @Test
    public void listAllWithFilterAndStatusPaginatedTest() {
        mockArchetypesOrgUnit();

        final Archetype archetype1 = createArchetypeWithFields("findme", ArchetypeStatus.VALID);
        final Archetype archetype2 = createArchetypeWithFields("tryfindme", ArchetypeStatus.INVALID);
        final Archetype archetype3 = createArchetypeWithFields("hidden", ArchetypeStatus.VALID);
        final Archetype archetype4 = createArchetypeWithFields("hidden", ArchetypeStatus.INVALID);

        final List<Archetype> archetypes = new ArrayList<>();
        archetypes.addAll(Collections.nCopies(13, archetype1));
        archetypes.addAll(Collections.nCopies(5, archetype2));
        archetypes.addAll(Collections.nCopies(7, archetype3));
        archetypes.addAll(Collections.nCopies(9, archetype4));

        final List<Repository> repositories = new ArrayList<>();
        archetypes.forEach(archetype -> {
            doReturn(archetype).when(archetypeConfigStorage).loadArchetype(archetype.getAlias());
            final Repository repository = mock(Repository.class);
            doReturn(archetype.getAlias()).when(repository).getAlias();
            repositories.add(repository);
        });

        doReturn(repositories).when(repositoryService).getAllRepositories(any(Space.class));

        PaginatedArchetypeList result = service.list(0, 5, "findme", ArchetypeStatus.VALID);

        assertThat(result.getTotal()).isEqualTo(13);
        assertThat(result.getArchetypes()).hasSize(5);
        assertThat(result.getPageNumber()).isEqualTo(0);
        assertThat(result.getPageSize()).isEqualTo(5);

        result = service.list(1, 5, "findme", ArchetypeStatus.VALID);

        assertThat(result.getTotal()).isEqualTo(13);
        assertThat(result.getArchetypes()).hasSize(5);
        assertThat(result.getPageNumber()).isEqualTo(1);
        assertThat(result.getPageSize()).isEqualTo(5);

        result = service.list(2, 5, "findme", ArchetypeStatus.VALID);

        assertThat(result.getTotal()).isEqualTo(13);
        assertThat(result.getArchetypes()).hasSize(3);
        assertThat(result.getPageNumber()).isEqualTo(2);
        assertThat(result.getPageSize()).isEqualTo(5);
    }

    @Test(expected = IllegalStateException.class)
    public void unpackArchetypeWhenOrgUnitIsNotAvailableTest() {
        mockArchetypesOrgUnitNotAvailable();

        service.unpackArchetype(COMMON_ARCHETYPE_ALIAS);
    }

    @Test(expected = IllegalStateException.class)
    public void unpackArchetypeWhenRepositoryIsNotAvailableTest() {
        mockArchetypesOrgUnit();

        doReturn(null).when(repositoryService).getRepositoryFromSpace(any(Space.class),
                                                                      anyString());

        service.unpackArchetype(COMMON_ARCHETYPE_ALIAS);
    }

    @Test
    public void unpackArchetypeTest() throws GitAPIException {
        mockArchetypesOrgUnit();

        doReturn(mock(Repository.class)).when(repositoryService).getRepositoryFromSpace(any(Space.class),
                                                                                        anyString());

        final Git git = mock(Git.class);
        final org.eclipse.jgit.lib.Repository gitRepository = mock(org.eclipse.jgit.lib.Repository.class);
        doReturn(mock(File.class)).when(gitRepository).getDirectory();
        doReturn(gitRepository).when(git).getRepository();
        doReturn(git).when(service).getGitFromRepository(any(Repository.class));

        doNothing().when(service).cloneRepository(any(File.class),
                                                  any(File.class));

        service.unpackArchetype(COMMON_ARCHETYPE_ALIAS);

        verify(ioService).deleteIfExists(any(Path.class),
                                         eq(StandardDeleteOption.NON_EMPTY_DIRECTORIES));
    }

    @Test(expected = IllegalStateException.class)
    public void unpackArchetypeWhenExceptionIsThrownTest() throws GitAPIException {
        mockArchetypesOrgUnit();

        doReturn(mock(Repository.class)).when(repositoryService).getRepositoryFromSpace(any(Space.class),
                                                                                        anyString());

        final Git git = mock(Git.class);
        final org.eclipse.jgit.lib.Repository gitRepository = mock(org.eclipse.jgit.lib.Repository.class);
        doReturn(mock(File.class)).when(gitRepository).getDirectory();
        doReturn(gitRepository).when(git).getRepository();
        doReturn(git).when(service).getGitFromRepository(any(Repository.class));

        doThrow(WrongRepositoryStateException.class).when(service).cloneRepository(any(File.class),
                                                                                   any(File.class));

        service.unpackArchetype(COMMON_ARCHETYPE_ALIAS);
    }

    private GAV createGav() {
        return createGav("group",
                         "artifact",
                         "version");
    }

    private GAV createTemplateGav() {
        return createGav("group",
                         "artifact",
                         "version-TEMPLATE");
    }

    private GAV createGav(final String group,
                          final String artifact,
                          final String version) {
        final String gavString = String.format("%s:%s:%s",
                                               group,
                                               artifact,
                                               version);
        return new GAV(gavString);
    }

    private Archetype createArchetype() {
        return createArchetypeWithFields(COMMON_ARCHETYPE_ALIAS,
                                         ArchetypeStatus.VALID);
    }

    private Archetype createArchetypeWithStatus(final ArchetypeStatus status) {
        return createArchetypeWithFields(COMMON_ARCHETYPE_ALIAS,
                                         status);
    }

    private Archetype createArchetypeWithAlias(final String alias) {
        return createArchetypeWithFields(alias,
                                         ArchetypeStatus.VALID);
    }

    private Archetype createArchetypeWithFields(final String alias,
                                                final ArchetypeStatus status) {
        return new Archetype(alias,
                             createTemplateGav(),
                             new Date(),
                             status);
    }

    private OrganizationalUnit mockArchetypesOrgUnit() {
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        doReturn(organizationalUnit)
                .when(ouService).getOrganizationalUnit(ArchetypeConfigStorageImpl.ARCHETYPES_SPACE_NAME);

        return organizationalUnit;
    }

    private void mockArchetypesOrgUnitNotAvailable() {
        doReturn(null).when(ouService).getOrganizationalUnit(ArchetypeConfigStorageImpl.ARCHETYPES_SPACE_NAME);
    }
}
