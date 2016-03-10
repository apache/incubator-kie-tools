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
import javax.enterprise.event.Event;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.backend.server.AbstractProjectService;
import org.guvnor.common.services.project.builder.events.InvalidateDMOProjectCacheEvent;
import org.guvnor.common.services.project.events.DeleteProjectEvent;
import org.guvnor.common.services.project.events.NewPackageEvent;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.events.RenameProjectEvent;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.MavenRepositorySource;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectRepositoryResolver;
import org.guvnor.structure.backend.backcompat.BackwardCompatibleUtil;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystemNotFoundException;
import org.uberfire.java.nio.file.FileSystems;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.events.ResourceDeletedEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectServiceImplNewProjectTest {

    @Mock
    private IOService ioService;

    @Mock
    private ProjectSaver saver;

    @Mock
    private ProjectRepositoryResolver projectRepositoryResolver;

    @Mock
    private KieProjectFactory projectFactory;

    private KieProjectServiceImpl projectService;

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
        final Event<NewProjectEvent> newProjectEvent = mock( Event.class );
        final Event<NewPackageEvent> newPackageEvent = mock( Event.class );
        final Event<RenameProjectEvent> renameProjectEvent = mock( Event.class );
        final Event<InvalidateDMOProjectCacheEvent> invalidateDMOCache = mock( Event.class );

        projectService = new KieProjectServiceImpl( ioService,
                                                    saver,
                                                    mock( POMService.class ),
                                                    mock( ConfigurationService.class ),
                                                    mock( ConfigurationFactory.class ),
                                                    newProjectEvent,
                                                    newPackageEvent,
                                                    renameProjectEvent,
                                                    invalidateDMOCache,
                                                    mock( SessionInfo.class ),
                                                    mock( AuthorizationManager.class ),
                                                    mock( BackwardCompatibleUtil.class ),
                                                    mock( CommentedOptionFactory.class ),
                                                    mock( KieResourceResolver.class ),
                                                    projectRepositoryResolver ) {
        };

        assertNotNull( projectService );
    }

    @Test
    public void testNewProjectCreationNonClashingGAV() throws URISyntaxException {
        final Repository repository = mock( Repository.class );
        final POM pom = new POM();
        final String baseURL = "/";

        final KieProject expected = new KieProject();

        when( saver.save( repository.getRoot(),
                          pom,
                          baseURL ) ).thenReturn( expected );

        final Project project = projectService.newProject( repository.getRoot(),
                                                           pom,
                                                           baseURL );

        assertEquals( expected,
                      project );
    }

    @Test(expected = GAVAlreadyExistsException.class)
    public void testNewProjectCreationClashingGAV() throws URISyntaxException {
        final Repository repository = mock( Repository.class );
        final POM pom = new POM();
        final String baseURL = "/";

        final KieProject expected = new KieProject();

        when( projectRepositoryResolver.getRepositoriesResolvingArtifact( eq( pom.getGav() ) ) ).thenReturn( new HashSet<MavenRepositoryMetadata>() {{
            add( new MavenRepositoryMetadata( "id",
                                              "url",
                                              MavenRepositorySource.SETTINGS ) );
        }} );
        when( saver.save( repository.getRoot(),
                          pom,
                          baseURL ) ).thenReturn( expected );

        projectService.newProject( repository.getRoot(),
                                   pom,
                                   baseURL );
    }

    @Test()
    public void testNewProjectCreationClashingGAVForced() throws URISyntaxException {
        final Repository repository = mock( Repository.class );
        final POM pom = new POM();
        final String baseURL = "/";

        final KieProject expected = new KieProject();

        when( projectRepositoryResolver.getRepositoriesResolvingArtifact( eq( pom.getGav() ) ) ).thenReturn( new HashSet<MavenRepositoryMetadata>() {{
            add( new MavenRepositoryMetadata( "id",
                                              "url",
                                              MavenRepositorySource.SETTINGS ) );
        }} );
        when( saver.save( repository.getRoot(),
                          pom,
                          baseURL ) ).thenReturn( expected );

        try {
            projectService.newProject( repository.getRoot(),
                                       pom,
                                       baseURL,
                                       DeploymentMode.FORCED );

        } catch ( GAVAlreadyExistsException e ) {
            fail( "Unexpected exception thrown: " + e.getMessage() );
        }
    }

    @Test
    public void testDeleteProjectObserverBridge() throws URISyntaxException {
        final URI fs = new URI( "git://test" );
        try {
            FileSystems.getFileSystem( fs );
        } catch ( FileSystemNotFoundException e ) {
            FileSystems.newFileSystem( fs,
                                       new HashMap<String, Object>() );
        }

        final Path path = mock( Path.class );
        final org.uberfire.java.nio.file.Path nioPath = mock( org.uberfire.java.nio.file.Path.class );
        when( path.getFileName() ).thenReturn( "pom.xml" );
        when( path.toURI() ).thenReturn( "git://test/p0/pom.xml" );
        when( nioPath.getParent() ).thenReturn( nioPath );
        when( nioPath.resolve( any( String.class ) ) ).thenReturn( nioPath );
        when( nioPath.toUri() ).thenReturn( URI.create( "git://test/p0/pom.xml" ) );
        when( nioPath.getFileSystem() ).thenReturn( FileSystems.getFileSystem( fs ) );
        when( ioService.get( any( URI.class ) ) ).thenReturn( nioPath );

        final SessionInfo sessionInfo = mock( SessionInfo.class );
        final Event<DeleteProjectEvent> deleteProjectEvent = mock( Event.class );
        final AbstractProjectService projectServiceSpy = spy( projectService );

        final DeleteKieProjectObserverBridge bridge = new DeleteKieProjectObserverBridge( ioService,
                                                                                          deleteProjectEvent,
                                                                                          projectFactory );

        bridge.onBatchResourceChanges( new ResourceDeletedEvent( path,
                                                                 "message",
                                                                 sessionInfo ) );

        verify( deleteProjectEvent,
                times( 1 ) ).fire( any( DeleteProjectEvent.class ) );

        verify( projectServiceSpy,
                times( 0 ) ).newProject( any( Path.class ),
                                         any( POM.class ),
                                         any( String.class ) );
        verify( projectFactory,
                times( 1 ) ).simpleProjectInstance( any( org.uberfire.java.nio.file.Path.class ) );
    }

}
