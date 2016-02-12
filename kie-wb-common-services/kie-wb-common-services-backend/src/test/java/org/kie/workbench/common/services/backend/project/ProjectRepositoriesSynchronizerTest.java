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
import org.guvnor.common.services.project.model.ProjectRepositories;
import org.guvnor.common.services.project.service.ProjectRepositoriesService;
import org.guvnor.common.services.project.service.ProjectRepositoryResolver;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.project.KieProject;
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
public class ProjectRepositoriesSynchronizerTest {

    @Mock
    private IOService ioService;

    @Mock
    private ProjectRepositoryResolver repositoryResolver;

    @Mock
    private ProjectRepositoriesService projectRepositoriesService;

    @Mock
    private KieProjectFactory projectFactory;

    @Mock
    private Path pomPath;

    @Mock
    private org.uberfire.java.nio.file.Path pomNioPath;

    @Mock
    private Path projectRepositoriesPath;

    @Mock
    private KieProject project;

    @Mock
    private SessionInfo sessionInfo;

    private ProjectRepositoriesSynchronizer synchronizer;

    private ObservablePOMFile observablePOMFile = new ObservablePOMFile();

    @BeforeClass
    public static void setupSystemProperties() {
        //These are not needed for the tests
        System.setProperty( "org.uberfire.nio.git.daemon.enabled",
                            "false" );
        System.setProperty( "org.uberfire.nio.git.ssh.enabled",
                            "false" );
        System.setProperty( "org.uberfire.sys.repo.monitor.disabled",
                            "true" );
    }

    @Before
    public void setup() {
        synchronizer = new ProjectRepositoriesSynchronizer( ioService,
                                                            repositoryResolver,
                                                            projectRepositoriesService,
                                                            observablePOMFile,
                                                            projectFactory );
        when( pomPath.getFileName() ).thenReturn( "pom.xml" );
        when( pomPath.toURI() ).thenReturn( "default://p0/pom.xml" );

        when( ioService.get( any( URI.class ) ) ).thenReturn( pomNioPath );
        when( projectFactory.simpleProjectInstance( any( org.uberfire.java.nio.file.Path.class ) ) ).thenReturn( project );
        when( project.getRepositoriesPath() ).thenReturn( projectRepositoriesPath );
    }

    @Test
    public void testAddProjectRepository() {
        final ResourceUpdatedEvent event = new ResourceUpdatedEvent( pomPath,
                                                                     "",
                                                                     sessionInfo );
        final ProjectRepositories projectRepositories = new ProjectRepositories();

        when( projectRepositoriesService.load( projectRepositoriesPath ) ).thenReturn( projectRepositories );
        when( repositoryResolver.getRemoteRepositoriesMetaData( project ) ).thenReturn( new HashSet<MavenRepositoryMetadata>() {{
            add( new MavenRepositoryMetadata( "local-id",
                                              "local-url",
                                              MavenRepositorySource.LOCAL ) );
        }} );

        synchronizer.onResourceUpdated( event );

        final ArgumentCaptor<ProjectRepositories> projectRepositoriesArgumentCaptor = ArgumentCaptor.forClass( ProjectRepositories.class );
        verify( projectRepositoriesService,
                times( 1 ) ).save( eq( projectRepositoriesPath ),
                                   projectRepositoriesArgumentCaptor.capture(),
                                   any( String.class ) );

        final ProjectRepositories saved = projectRepositoriesArgumentCaptor.getValue();
        assertNotNull( saved );
        assertEquals( 1,
                      saved.getRepositories().size() );

        final ProjectRepositories.ProjectRepository repository = saved.getRepositories().iterator().next();
        assertTrue( repository.isIncluded() );
        assertEquals( "local-id",
                      repository.getMetadata().getId() );
        assertEquals( "local-url",
                      repository.getMetadata().getUrl() );
        assertEquals( MavenRepositorySource.LOCAL,
                      repository.getMetadata().getSource() );
    }

    @Test
    public void testRemoveProjectRepository() {
        final ResourceUpdatedEvent event = new ResourceUpdatedEvent( pomPath,
                                                                     "",
                                                                     sessionInfo );
        final Set<ProjectRepositories.ProjectRepository> repositories = new HashSet<ProjectRepositories.ProjectRepository>() {{
            add( new ProjectRepositories.ProjectRepository( true,
                                                            new MavenRepositoryMetadata( "local-id",
                                                                                         "local-url",
                                                                                         MavenRepositorySource.LOCAL ) ) );
        }};
        final ProjectRepositories projectRepositories = new ProjectRepositories( repositories );

        when( projectRepositoriesService.load( projectRepositoriesPath ) ).thenReturn( projectRepositories );

        synchronizer.onResourceUpdated( event );

        final ArgumentCaptor<ProjectRepositories> projectRepositoriesArgumentCaptor = ArgumentCaptor.forClass( ProjectRepositories.class );
        verify( projectRepositoriesService,
                times( 1 ) ).save( eq( projectRepositoriesPath ),
                                   projectRepositoriesArgumentCaptor.capture(),
                                   any( String.class ) );

        final ProjectRepositories saved = projectRepositoriesArgumentCaptor.getValue();
        assertNotNull( saved );
        assertEquals( 0,
                      saved.getRepositories().size() );
    }

    @Test
    public void testAddAndRemoveProjectRepository() {
        final ResourceUpdatedEvent event = new ResourceUpdatedEvent( pomPath,
                                                                     "",
                                                                     sessionInfo );
        final Set<ProjectRepositories.ProjectRepository> repositories = new HashSet<ProjectRepositories.ProjectRepository>() {{
            add( new ProjectRepositories.ProjectRepository( true,
                                                            new MavenRepositoryMetadata( "local-id",
                                                                                         "local-url",
                                                                                         MavenRepositorySource.LOCAL ) ) );
        }};
        final ProjectRepositories projectRepositories = new ProjectRepositories( repositories );

        when( projectRepositoriesService.load( projectRepositoriesPath ) ).thenReturn( projectRepositories );
        when( repositoryResolver.getRemoteRepositoriesMetaData( project ) ).thenReturn( new HashSet<MavenRepositoryMetadata>() {{
            add( new MavenRepositoryMetadata( "remote-id",
                                              "remote-url",
                                              MavenRepositorySource.PROJECT ) );
        }} );

        synchronizer.onResourceUpdated( event );

        final ArgumentCaptor<ProjectRepositories> projectRepositoriesArgumentCaptor = ArgumentCaptor.forClass( ProjectRepositories.class );
        verify( projectRepositoriesService,
                times( 1 ) ).save( eq( projectRepositoriesPath ),
                                   projectRepositoriesArgumentCaptor.capture(),
                                   any( String.class ) );

        final ProjectRepositories saved = projectRepositoriesArgumentCaptor.getValue();
        assertNotNull( saved );
        assertEquals( 1,
                      saved.getRepositories().size() );

        final ProjectRepositories.ProjectRepository repository = saved.getRepositories().iterator().next();
        assertTrue( repository.isIncluded() );
        assertEquals( "remote-id",
                      repository.getMetadata().getId() );
        assertEquals( "remote-url",
                      repository.getMetadata().getUrl() );
        assertEquals( MavenRepositorySource.PROJECT,
                      repository.getMetadata().getSource() );
    }

}
