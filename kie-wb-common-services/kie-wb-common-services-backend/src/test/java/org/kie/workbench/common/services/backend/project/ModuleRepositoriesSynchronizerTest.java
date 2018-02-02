/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.project;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.guvnor.common.services.builder.ObservablePOMFile;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.MavenRepositorySource;
import org.guvnor.common.services.project.model.ModuleRepositories;
import org.guvnor.common.services.project.service.ModuleRepositoriesService;
import org.guvnor.common.services.project.service.ModuleRepositoryResolver;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ModuleRepositoriesSynchronizerTest {

    @Mock
    private IOService ioService;

    @Mock
    private ModuleRepositoryResolver repositoryResolver;

    @Mock
    private ModuleRepositoriesService moduleRepositoriesService;

    @Mock
    private KieModuleFactory moduleFactory;

    @Mock
    private Path pomPath;

    @Mock
    private org.uberfire.java.nio.file.Path pomNioPath;

    @Mock
    private Path moduleRepositoriesPath;

    @Mock
    private KieModule module;

    @Mock
    private SessionInfo sessionInfo;

    private ModuleRepositoriesSynchronizer synchronizer;

    private ObservablePOMFile observablePOMFile = new ObservablePOMFile();

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
        synchronizer = new ModuleRepositoriesSynchronizer(ioService,
                                                          repositoryResolver,
                                                          moduleRepositoriesService,
                                                          observablePOMFile,
                                                          moduleFactory);
        when(pomPath.getFileName()).thenReturn("pom.xml");
        when(pomPath.toURI()).thenReturn("default://p0/pom.xml");

        when(ioService.get(any(URI.class))).thenReturn(pomNioPath);
        when(moduleFactory.simpleModuleInstance(any(org.uberfire.java.nio.file.Path.class))).thenReturn(module);
        when(module.getRepositoriesPath()).thenReturn(moduleRepositoriesPath);
    }

    @Test
    public void testAddModuleRepository() {
        final ResourceUpdatedEvent event = new ResourceUpdatedEvent(pomPath,
                                                                    "",
                                                                    sessionInfo);
        final ModuleRepositories moduleRepositories = new ModuleRepositories();

        when(moduleRepositoriesService.load(moduleRepositoriesPath)).thenReturn(moduleRepositories);
        when(repositoryResolver.getRemoteRepositoriesMetaData(module)).thenReturn(new HashSet<MavenRepositoryMetadata>() {{
            add(new MavenRepositoryMetadata("local-id",
                                            "local-url",
                                            MavenRepositorySource.LOCAL));
        }});

        synchronizer.onResourceUpdated(event);

        final ArgumentCaptor<ModuleRepositories> moduleRepositoriesArgumentCaptor = ArgumentCaptor.forClass(ModuleRepositories.class);
        verify(moduleRepositoriesService,
               times(1)).save(eq(moduleRepositoriesPath),
                              moduleRepositoriesArgumentCaptor.capture(),
                              any(String.class));

        final ModuleRepositories saved = moduleRepositoriesArgumentCaptor.getValue();
        assertNotNull(saved);
        assertEquals(1,
                     saved.getRepositories().size());

        final ModuleRepositories.ModuleRepository repository = saved.getRepositories().iterator().next();
        assertTrue(repository.isIncluded());
        assertEquals("local-id",
                     repository.getMetadata().getId());
        assertEquals("local-url",
                     repository.getMetadata().getUrl());
        assertEquals(MavenRepositorySource.LOCAL,
                     repository.getMetadata().getSource());
    }

    @Test
    public void testRemoveModuleRepository() {
        final ResourceUpdatedEvent event = new ResourceUpdatedEvent(pomPath,
                                                                    "",
                                                                    sessionInfo);
        final Set<ModuleRepositories.ModuleRepository> repositories = new HashSet<ModuleRepositories.ModuleRepository>() {{
            add(new ModuleRepositories.ModuleRepository(true,
                                                        new MavenRepositoryMetadata("local-id",
                                                                                    "local-url",
                                                                                    MavenRepositorySource.LOCAL)));
        }};
        final ModuleRepositories moduleRepositories = new ModuleRepositories(repositories);

        when(moduleRepositoriesService.load(moduleRepositoriesPath)).thenReturn(moduleRepositories);

        synchronizer.onResourceUpdated(event);

        final ArgumentCaptor<ModuleRepositories> moduleRepositoriesArgumentCaptor = ArgumentCaptor.forClass(ModuleRepositories.class);
        verify(moduleRepositoriesService,
               times(1)).save(eq(moduleRepositoriesPath),
                              moduleRepositoriesArgumentCaptor.capture(),
                              any(String.class));

        final ModuleRepositories saved = moduleRepositoriesArgumentCaptor.getValue();
        assertNotNull(saved);
        assertEquals(0,
                     saved.getRepositories().size());
    }

    @Test
    public void testAddAndRemoveModuleRepository() {
        final ResourceUpdatedEvent event = new ResourceUpdatedEvent(pomPath,
                                                                    "",
                                                                    sessionInfo);
        final Set<ModuleRepositories.ModuleRepository> repositories = new HashSet<ModuleRepositories.ModuleRepository>() {{
            add(new ModuleRepositories.ModuleRepository(true,
                                                        new MavenRepositoryMetadata("local-id",
                                                                                    "local-url",
                                                                                    MavenRepositorySource.LOCAL)));
        }};
        final ModuleRepositories moduleRepositories = new ModuleRepositories(repositories);

        when(moduleRepositoriesService.load(moduleRepositoriesPath)).thenReturn(moduleRepositories);
        when(repositoryResolver.getRemoteRepositoriesMetaData(module)).thenReturn(new HashSet<MavenRepositoryMetadata>() {{
            add(new MavenRepositoryMetadata("remote-id",
                                            "remote-url",
                                            MavenRepositorySource.PROJECT));
        }});

        synchronizer.onResourceUpdated(event);

        final ArgumentCaptor<ModuleRepositories> moduleRepositoriesArgumentCaptor = ArgumentCaptor.forClass(ModuleRepositories.class);
        verify(moduleRepositoriesService,
               times(1)).save(eq(moduleRepositoriesPath),
                              moduleRepositoriesArgumentCaptor.capture(),
                              any(String.class));

        final ModuleRepositories saved = moduleRepositoriesArgumentCaptor.getValue();
        assertNotNull(saved);
        assertEquals(1,
                     saved.getRepositories().size());

        final ModuleRepositories.ModuleRepository repository = saved.getRepositories().iterator().next();
        assertTrue(repository.isIncluded());
        assertEquals("remote-id",
                     repository.getMetadata().getId());
        assertEquals("remote-url",
                     repository.getMetadata().getUrl());
        assertEquals(MavenRepositorySource.PROJECT,
                     repository.getMetadata().getSource());
    }
}
