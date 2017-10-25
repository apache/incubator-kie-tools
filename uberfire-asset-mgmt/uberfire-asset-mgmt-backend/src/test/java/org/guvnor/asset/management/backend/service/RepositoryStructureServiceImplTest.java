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

package org.guvnor.asset.management.backend.service;

import java.util.Collections;
import java.util.HashSet;

import org.guvnor.asset.management.service.RepositoryStructureService;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.MavenRepositorySource;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectRepositoryResolver;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.guvnor.structure.repositories.Repository;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryStructureServiceImplTest {

    @Mock
    private POMService pomService;

    @Mock
    private ProjectService<Project> projectService;

    @Mock
    private GuvnorM2Repository m2service;

    @Mock
    private ProjectRepositoryResolver repositoryResolver;

    private RepositoryStructureService service;

    @BeforeClass
    public static void setupSystemProperties() {
        //These are not needed for the tests
        System.setProperty("org.uberfire.nio.git.daemon.enabled",
                           "false");
        System.setProperty("org.uberfire.nio.git.ssh.enabled",
                           "false");
        System.setProperty("org.uberfire.sys.repo.monitor.disabled",
                           "true");
    }

    @Before
    public void setup() {
        service = new RepositoryStructureServiceImpl(mock(IOService.class),
                                                     pomService,
                                                     projectService,
                                                     m2service,
                                                     mock(CommentedOptionFactory.class),
                                                     repositoryResolver,
                                                     mock(RepositoryStructureModelLoader.class),
                                                     mock(ManagedStatusUpdater.class));
    }

    @Test
    public void testInitRepositoryStructure1() {
        final GAV gav = new GAV("groupId",
                                "artifactId",
                                "version");
        final Repository repository = mock(Repository.class);
        final Path repositoryRootPath = mock(Path.class);
        when(repository.getAlias()).thenReturn("alias");
        when(repository.getRoot()).thenReturn(repositoryRootPath);

        when(repositoryResolver.getRepositoriesResolvingArtifact(eq(gav))).thenReturn(Collections.<MavenRepositoryMetadata>emptySet());

        service.initRepositoryStructure(gav,
                                        repository,
                                        DeploymentMode.VALIDATED);

        verify(repositoryResolver,
               times(1)).getRepositoriesResolvingArtifact(eq(gav));

        final ArgumentCaptor<POM> pomArgumentCaptor = ArgumentCaptor.forClass(POM.class);
        verify(pomService,
               times(1)).create(eq(repositoryRootPath),
                                eq(""),
                                pomArgumentCaptor.capture());

        assertNotNull(pomArgumentCaptor.getValue());
        assertEquals("groupId",
                     pomArgumentCaptor.getValue().getGav().getGroupId());
        assertEquals("artifactId",
                     pomArgumentCaptor.getValue().getGav().getArtifactId());
        assertEquals("version",
                     pomArgumentCaptor.getValue().getGav().getVersion());

        verify(m2service,
               times(1)).deployParentPom(eq(gav));
    }

    @Test
    public void testInitRepositoryStructure1ClashingGAV() {
        final GAV gav = new GAV("groupId",
                                "artifactId",
                                "version");
        final Repository repository = mock(Repository.class);
        final Path repositoryRootPath = mock(Path.class);
        when(repository.getAlias()).thenReturn("alias");
        when(repository.getRoot()).thenReturn(repositoryRootPath);

        when(repositoryResolver.getRepositoriesResolvingArtifact(eq(gav))).thenReturn(new HashSet<MavenRepositoryMetadata>() {
            {
                add(new MavenRepositoryMetadata("local-id",
                                                "local-url",
                                                MavenRepositorySource.LOCAL));
            }
        });

        try {
            service.initRepositoryStructure(gav,
                                            repository,
                                            DeploymentMode.VALIDATED);
        } catch (GAVAlreadyExistsException expected) {
            //This is expected, but we want to verify the other services are not called
        }

        verify(repositoryResolver,
               times(1)).getRepositoriesResolvingArtifact(eq(gav));

        verify(pomService,
               never()).create(eq(repositoryRootPath),
                               eq(""),
                               any(POM.class));
        verify(m2service,
               never()).deployParentPom(eq(gav));
    }

    @Test
    public void testInitRepositoryStructure1ClashingGAVForced() {
        final GAV gav = new GAV("groupId",
                                "artifactId",
                                "version");
        final Repository repository = mock(Repository.class);
        final Path repositoryRootPath = mock(Path.class);
        when(repository.getAlias()).thenReturn("alias");
        when(repository.getRoot()).thenReturn(repositoryRootPath);

        when(repositoryResolver.getRepositoriesResolvingArtifact(eq(gav))).thenReturn(new HashSet<MavenRepositoryMetadata>() {
            {
                add(new MavenRepositoryMetadata("local-id",
                                                "local-url",
                                                MavenRepositorySource.LOCAL));
            }
        });

        try {
            service.initRepositoryStructure(gav,
                                            repository,
                                            DeploymentMode.FORCED);
        } catch (GAVAlreadyExistsException expected) {
            //This is expected, but we want to verify the other services are not called
        }

        verify(repositoryResolver,
               never()).getRepositoriesResolvingArtifact(eq(gav));

        final ArgumentCaptor<POM> pomArgumentCaptor = ArgumentCaptor.forClass(POM.class);
        verify(pomService,
               times(1)).create(eq(repositoryRootPath),
                                eq(""),
                                pomArgumentCaptor.capture());

        assertNotNull(pomArgumentCaptor.getValue());
        assertEquals("groupId",
                     pomArgumentCaptor.getValue().getGav().getGroupId());
        assertEquals("artifactId",
                     pomArgumentCaptor.getValue().getGav().getArtifactId());
        assertEquals("version",
                     pomArgumentCaptor.getValue().getGav().getVersion());

        verify(m2service,
               times(1)).deployParentPom(eq(gav));
    }

    @Test
    public void testInitRepositoryStructure2SingleModule() {
        final GAV gav = new GAV("groupId",
                                "artifactId",
                                "version");
        final POM pom = new POM(gav);
        final Repository repository = mock(Repository.class);
        final Path repositoryRootPath = mock(Path.class);
        when(repository.getAlias()).thenReturn("alias");
        when(repository.getDefaultBranch()).thenReturn("master");
        when(repository.getBranchRoot("master")).thenReturn(repositoryRootPath);

        final Project project = mock(Project.class);
        final Path pomPath = mock(Path.class);
        when(project.getPomXMLPath()).thenReturn(pomPath);

        when(repositoryResolver.getRepositoriesResolvingArtifact(eq(gav))).thenReturn(Collections.<MavenRepositoryMetadata>emptySet());
        when(projectService.newProject(eq(repositoryRootPath),
                                       eq(pom),
                                       eq("baseUrl"),
                                       eq(DeploymentMode.VALIDATED))).thenReturn(project);

        service.initRepositoryStructure(pom,
                                        "baseUrl",
                                        repository,
                                        false,
                                        DeploymentMode.VALIDATED);

        verify(repositoryResolver,
               times(1)).getRepositoriesResolvingArtifact(eq(gav));

        verify(projectService,
               times(1)).newProject(eq(repositoryRootPath),
                                    eq(pom),
                                    eq("baseUrl"),
                                    eq(DeploymentMode.VALIDATED));
    }

    @Test
    public void testInitRepositoryStructure2SingleModuleClashingGAV() {
        final GAV gav = new GAV("groupId",
                                "artifactId",
                                "version");
        final POM pom = new POM(gav);
        final Repository repository = mock(Repository.class);
        final Path repositoryRootPath = mock(Path.class);
        when(repository.getAlias()).thenReturn("alias");
        when(repository.getRoot()).thenReturn(repositoryRootPath);

        final Project project = mock(Project.class);
        final Path pomPath = mock(Path.class);
        when(project.getPomXMLPath()).thenReturn(pomPath);

        when(repositoryResolver.getRepositoriesResolvingArtifact(eq(gav))).thenReturn(new HashSet<MavenRepositoryMetadata>() {
            {
                add(new MavenRepositoryMetadata("local-id",
                                                "local-url",
                                                MavenRepositorySource.LOCAL));
            }
        });

        final GAVAlreadyExistsException gae = new GAVAlreadyExistsException(pom.getGav(),
                                                                            new HashSet<MavenRepositoryMetadata>() {{
                                                                                add(new MavenRepositoryMetadata("local-id",
                                                                                                                "local-url",
                                                                                                                MavenRepositorySource.LOCAL));
                                                                            }});
        doThrow(gae).when(projectService).newProject(eq(repositoryRootPath),
                                                     eq(pom),
                                                     eq("baseUrl"),
                                                     eq(DeploymentMode.VALIDATED));

        try {
            service.initRepositoryStructure(pom,
                                            "baseUrl",
                                            repository,
                                            false,
                                            DeploymentMode.VALIDATED);
        } catch (GAVAlreadyExistsException expected) {
            //This is expected, but we want to verify the other services are not called
        }

        verify(repositoryResolver,
               times(1)).getRepositoriesResolvingArtifact(eq(gav));

        verify(projectService,
               never()).newProject(eq(repositoryRootPath),
                                   eq(pom),
                                   eq("baseUrl"),
                                   any(DeploymentMode.class));
    }

    @Test
    public void testInitRepositoryStructure2SingleModuleClashingGAVForced() {
        final GAV gav = new GAV("groupId",
                                "artifactId",
                                "version");
        final POM pom = new POM(gav);
        final Repository repository = mock(Repository.class);
        final Path repositoryRootPath = mock(Path.class);
        when(repository.getAlias()).thenReturn("alias");
        when(repository.getDefaultBranch()).thenReturn("master");
        when(repository.getBranchRoot("master")).thenReturn(repositoryRootPath);

        final Project project = mock(Project.class);
        final Path pomPath = mock(Path.class);
        when(project.getPomXMLPath()).thenReturn(pomPath);

        when(repositoryResolver.getRepositoriesResolvingArtifact(eq(gav))).thenReturn(new HashSet<MavenRepositoryMetadata>() {
            {
                add(new MavenRepositoryMetadata("local-id",
                                                "local-url",
                                                MavenRepositorySource.LOCAL));
            }
        });

        final GAVAlreadyExistsException gae = new GAVAlreadyExistsException(pom.getGav(),
                                                                            new HashSet<MavenRepositoryMetadata>() {{
                                                                                add(new MavenRepositoryMetadata("local-id",
                                                                                                                "local-url",
                                                                                                                MavenRepositorySource.LOCAL));
                                                                            }});
        doThrow(gae).when(projectService).newProject(eq(repositoryRootPath),
                                                     eq(pom),
                                                     eq("baseUrl"),
                                                     eq(DeploymentMode.VALIDATED));
        when(projectService.newProject(eq(repositoryRootPath),
                                       eq(pom),
                                       eq("baseUrl"),
                                       eq(DeploymentMode.FORCED))).thenReturn(project);

        try {
            service.initRepositoryStructure(pom,
                                            "baseUrl",
                                            repository,
                                            false,
                                            DeploymentMode.FORCED);
        } catch (GAVAlreadyExistsException expected) {
            //This is expected, but we want to verify the other services are not called
        }

        verify(repositoryResolver,
               never()).getRepositoriesResolvingArtifact(eq(gav));

        verify(projectService,
               times(1)).newProject(eq(repositoryRootPath),
                                    eq(pom),
                                    eq("baseUrl"),
                                    eq(DeploymentMode.FORCED));
    }

    @Test
    public void testInitRepositoryStructure2MultiModule() {
        final GAV gav = new GAV("groupId",
                                "artifactId",
                                "version");
        final POM pom = new POM(gav);
        final Repository repository = mock(Repository.class);
        final Path repositoryRootPath = mock(Path.class);
        when(repository.getAlias()).thenReturn("alias");
        when(repository.getRoot()).thenReturn(repositoryRootPath);

        when(repositoryResolver.getRepositoriesResolvingArtifact(eq(gav))).thenReturn(Collections.<MavenRepositoryMetadata>emptySet());

        service.initRepositoryStructure(pom,
                                        "baseUrl",
                                        repository,
                                        true,
                                        DeploymentMode.VALIDATED);

        verify(repositoryResolver,
               times(1)).getRepositoriesResolvingArtifact(eq(gav));

        final ArgumentCaptor<POM> pomArgumentCaptor = ArgumentCaptor.forClass(POM.class);
        verify(pomService,
               times(1)).create(eq(repositoryRootPath),
                                eq("baseUrl"),
                                pomArgumentCaptor.capture());

        assertNotNull(pomArgumentCaptor.getValue());
        assertEquals("groupId",
                     pomArgumentCaptor.getValue().getGav().getGroupId());
        assertEquals("artifactId",
                     pomArgumentCaptor.getValue().getGav().getArtifactId());
        assertEquals("version",
                     pomArgumentCaptor.getValue().getGav().getVersion());

        verify(m2service,
               times(1)).deployParentPom(eq(gav));
    }

    @Test
    public void testInitRepositoryStructure2MultiModuleClashingGAV() {
        final GAV gav = new GAV("groupId",
                                "artifactId",
                                "version");
        final POM pom = new POM(gav);
        final Repository repository = mock(Repository.class);
        final Path repositoryRootPath = mock(Path.class);
        when(repository.getAlias()).thenReturn("alias");
        when(repository.getRoot()).thenReturn(repositoryRootPath);

        when(repositoryResolver.getRepositoriesResolvingArtifact(eq(gav))).thenReturn(new HashSet<MavenRepositoryMetadata>() {
            {
                add(new MavenRepositoryMetadata("local-id",
                                                "local-url",
                                                MavenRepositorySource.LOCAL));
            }
        });

        try {
            service.initRepositoryStructure(pom,
                                            "baseUrl",
                                            repository,
                                            true,
                                            DeploymentMode.VALIDATED);
        } catch (GAVAlreadyExistsException expected) {
            //This is expected, but we want to verify the other services are not called
        }

        verify(repositoryResolver,
               times(1)).getRepositoriesResolvingArtifact(eq(gav));

        verify(pomService,
               never()).create(eq(repositoryRootPath),
                               eq(""),
                               any(POM.class));
        verify(m2service,
               never()).deployParentPom(eq(gav));
    }

    @Test
    public void testInitRepositoryStructure2MultiModuleClashingGAVForced() {
        final GAV gav = new GAV("groupId",
                                "artifactId",
                                "version");
        final POM pom = new POM(gav);
        final Repository repository = mock(Repository.class);
        final Path repositoryRootPath = mock(Path.class);
        when(repository.getAlias()).thenReturn("alias");
        when(repository.getRoot()).thenReturn(repositoryRootPath);

        when(repositoryResolver.getRepositoriesResolvingArtifact(eq(gav))).thenReturn(new HashSet<MavenRepositoryMetadata>() {
            {
                add(new MavenRepositoryMetadata("local-id",
                                                "local-url",
                                                MavenRepositorySource.LOCAL));
            }
        });

        try {
            service.initRepositoryStructure(pom,
                                            "baseUrl",
                                            repository,
                                            true,
                                            DeploymentMode.FORCED);
        } catch (GAVAlreadyExistsException expected) {
            //This is expected, but we want to verify the other services are not called
        }

        verify(repositoryResolver,
               never()).getRepositoriesResolvingArtifact(eq(gav));

        final ArgumentCaptor<POM> pomArgumentCaptor = ArgumentCaptor.forClass(POM.class);
        verify(pomService,
               times(1)).create(eq(repositoryRootPath),
                                eq("baseUrl"),
                                pomArgumentCaptor.capture());

        assertNotNull(pomArgumentCaptor.getValue());
        assertEquals("groupId",
                     pomArgumentCaptor.getValue().getGav().getGroupId());
        assertEquals("artifactId",
                     pomArgumentCaptor.getValue().getGav().getArtifactId());
        assertEquals("version",
                     pomArgumentCaptor.getValue().getGav().getVersion());

        verify(m2service,
               times(1)).deployParentPom(eq(gav));
    }
}
