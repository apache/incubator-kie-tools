/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.project;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import javax.enterprise.event.Event;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.backend.server.AbstractModuleService;
import org.guvnor.common.services.project.backend.server.ModuleFinder;
import org.guvnor.common.services.project.builder.events.InvalidateDMOModuleCacheEvent;
import org.guvnor.common.services.project.events.DeleteModuleEvent;
import org.guvnor.common.services.project.events.NewModuleEvent;
import org.guvnor.common.services.project.events.NewPackageEvent;
import org.guvnor.common.services.project.events.RenameModuleEvent;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.MavenRepositorySource;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.project.service.ModuleRepositoryResolver;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystemNotFoundException;
import org.uberfire.java.nio.file.FileSystems;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceDeletedEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ModuleServiceImplNewModuleTest {

    @Mock
    private IOService ioService;

    @Mock
    private ModuleSaver saver;

    @Mock
    private ModuleRepositoryResolver moduleRepositoryResolver;

    @Mock
    private KieModuleFactory moduleFactory;

    private KieModuleServiceImpl moduleService;

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
        final Event<NewModuleEvent> newModuleEvent = mock(Event.class);
        final Event<NewPackageEvent> newPackageEvent = mock(Event.class);
        final Event<RenameModuleEvent> renameModuleEvent = mock(Event.class);
        final Event<InvalidateDMOModuleCacheEvent> invalidateDMOCache = mock(Event.class);

        moduleService = new KieModuleServiceImpl(ioService,
                                                 saver,
                                                 mock(POMService.class),
                                                 mock(RepositoryService.class),
                                                 newModuleEvent,
                                                 newPackageEvent,
                                                 renameModuleEvent,
                                                 invalidateDMOCache,
                                                 mock(SessionInfo.class),
                                                 mock(CommentedOptionFactory.class),
                                                 mock(ModuleFinder.class),
                                                 mock(KieResourceResolver.class),
                                                 moduleRepositoryResolver) {
        };

        assertNotNull(moduleService);
    }

    @Test
    public void testNewModuleCreationNonClashingGAV() throws URISyntaxException {
        final Repository repository = mock(Repository.class);
        final Path masterBranchRoot = mock(Path.class);
        doReturn(Optional.of(new Branch("master", masterBranchRoot))).when(repository).getDefaultBranch();
        final POM pom = new POM();

        final KieModule expected = new KieModule();

        when(saver.save(masterBranchRoot,
                        pom)).thenReturn(expected);

        final Module project = moduleService.newModule(masterBranchRoot,
                                                       pom);

        assertEquals(expected,
                     project);
    }

    @Test(expected = GAVAlreadyExistsException.class)
    public void testNewModuleCreationClashingGAV() throws URISyntaxException {
        final Repository repository = mock(Repository.class);
        final Path masterBranchRoot = mock(Path.class);
        doReturn(Optional.of(new Branch("master", masterBranchRoot))).when(repository).getDefaultBranch();
        final POM pom = new POM();

        final KieModule expected = new KieModule();

        when(moduleRepositoryResolver.getRepositoriesResolvingArtifact(eq(pom.getGav()))).thenReturn(new HashSet<MavenRepositoryMetadata>() {{
            add(new MavenRepositoryMetadata("id",
                                            "url",
                                            MavenRepositorySource.SETTINGS));
        }});
        when(saver.save(masterBranchRoot,
                        pom)).thenReturn(expected);

        moduleService.newModule(masterBranchRoot,
                                pom);
    }

    @Test()
    public void testNewModuleCreationClashingGAVForced() throws URISyntaxException {
        final Repository repository = mock(Repository.class);
        final Path masterBranchRoot = mock(Path.class);
        doReturn(Optional.of(new Branch("master", masterBranchRoot))).when(repository).getDefaultBranch();
        final POM pom = new POM();

        final KieModule expected = new KieModule();

        when(moduleRepositoryResolver.getRepositoriesResolvingArtifact(eq(pom.getGav()))).thenReturn(new HashSet<MavenRepositoryMetadata>() {{
            add(new MavenRepositoryMetadata("id",
                                            "url",
                                            MavenRepositorySource.SETTINGS));
        }});
        when(saver.save(masterBranchRoot,
                        pom)).thenReturn(expected);

        try {
            moduleService.newModule(masterBranchRoot,
                                    pom,
                                    DeploymentMode.FORCED);
        } catch (GAVAlreadyExistsException e) {
            fail("Unexpected exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testDeleteModuleObserverBridge() throws URISyntaxException {
        final URI fs = new URI("git://test");
        try {
            FileSystems.getFileSystem(fs);
        } catch (FileSystemNotFoundException e) {
            FileSystems.newFileSystem(fs,
                                      new HashMap<String, Object>());
        }

        final Path path = mock(Path.class);
        final org.uberfire.java.nio.file.Path nioPath = mock(org.uberfire.java.nio.file.Path.class);
        when(path.getFileName()).thenReturn("pom.xml");
        when(path.toURI()).thenReturn("git://test/p0/pom.xml");
        when(nioPath.getParent()).thenReturn(nioPath);
        when(nioPath.resolve(any(String.class))).thenReturn(nioPath);
        when(nioPath.toUri()).thenReturn(URI.create("git://test/p0/pom.xml"));
        when(nioPath.getFileSystem()).thenReturn(FileSystems.getFileSystem(fs));
        when(ioService.get(any(URI.class))).thenReturn(nioPath);

        final SessionInfo sessionInfo = mock(SessionInfo.class);
        final Event<DeleteModuleEvent> deleteModuleEvent = mock(Event.class);
        final AbstractModuleService moduleServiceSpy = spy(moduleService);

        final DeleteKieModuleObserverBridge bridge = new DeleteKieModuleObserverBridge(ioService,
                                                                                       deleteModuleEvent,
                                                                                       moduleFactory);

        bridge.onBatchResourceChanges(new ResourceDeletedEvent(path,
                                                               "message",
                                                               sessionInfo));

        verify(deleteModuleEvent,
               times(1)).fire(any(DeleteModuleEvent.class));

        verify(moduleServiceSpy,
               times(0)).newModule(any(Path.class),
                                   any(POM.class));
        verify(moduleFactory,
               times(1)).simpleModuleInstance(any(org.uberfire.java.nio.file.Path.class));
    }
}
