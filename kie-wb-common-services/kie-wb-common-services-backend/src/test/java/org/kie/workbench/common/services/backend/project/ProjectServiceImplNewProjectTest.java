/*
 * Copyright 2013 JBoss Inc
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
import java.util.Map;
import javax.enterprise.event.Event;

import org.guvnor.common.services.project.backend.server.AbstractProjectService;
import org.guvnor.common.services.project.backend.server.DeleteProjectObserverBridge;
import org.guvnor.common.services.project.backend.server.ProjectConfigurationContentHandler;
import org.guvnor.common.services.project.builder.events.InvalidateDMOProjectCacheEvent;
import org.guvnor.common.services.project.events.DeleteProjectEvent;
import org.guvnor.common.services.project.events.NewPackageEvent;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.events.RenameProjectEvent;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.shared.kmodule.KModuleService;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystemNotFoundException;
import org.uberfire.java.nio.file.FileSystems;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceDeletedEvent;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

public class ProjectServiceImplNewProjectTest {

    private IOService ioService;
    private POMService pomService;
    private AbstractProjectService projectService;

    @Before
    public void setup() {
        ioService = mock( IOService.class );
        pomService = mock( POMService.class );

        final KModuleService kModuleService = mock( KModuleService.class );
        final ProjectConfigurationContentHandler projectConfigurationContentHandler = new ProjectConfigurationContentHandler();
        final ConfigurationService configurationService = mock( ConfigurationService.class );
        final ConfigurationFactory configurationFactory = mock( ConfigurationFactory.class );
        final Event<NewProjectEvent> newProjectEvent = mock( Event.class );
        final Event<NewPackageEvent> newPackageEvent = mock( Event.class );
        final Event<RenameProjectEvent> renameProjectEvent = mock( Event.class );
        final Event<DeleteProjectEvent> deleteProjectEvent = mock( Event.class );
        final Event<InvalidateDMOProjectCacheEvent> invalidateDMOCache = mock( Event.class );
        final User identity = mock( User.class );
        final SessionInfo sessionInfo = mock( SessionInfo.class );

        final Project project = mock( Project.class );
        final Path projectRootPath = mock( Path.class );
        when( project.getRootPath() ).thenReturn( projectRootPath );
        when( projectRootPath.toURI() ).thenReturn( "git://test/p0" );

        when( ioService.createDirectory( any( org.uberfire.java.nio.file.Path.class ) ) ).thenAnswer( new Answer<Object>() {
            @Override
            public Object answer( final InvocationOnMock invocation ) throws Throwable {
                return invocation.getArguments()[ 0 ];
            }
        } );

        projectService = new ProjectServiceImpl( ioService,
                                                 pomService,
                                                 kModuleService,
                                                 projectConfigurationContentHandler,
                                                 configurationService,
                                                 configurationFactory,
                                                 newProjectEvent,
                                                 newPackageEvent,
                                                 renameProjectEvent,
                                                 deleteProjectEvent,
                                                 invalidateDMOCache,
                                                 identity,
                                                 sessionInfo ) {

            @Override
            //Override as we don't have the Project Structure set-up in this test
            protected boolean hasPom( final org.uberfire.java.nio.file.Path path ) {
                return true;
            }

            @Override
            //Override as we don't have the Project Structure set-up in this test
            protected boolean hasKModule( final org.uberfire.java.nio.file.Path path ) {
                return true;
            }

            @Override
            //Override Package resolution as we don't have the Project Structure set-up in this test
            public Package resolvePackage( final Path resource ) {
                return makePackage( project,
                                    resource );
            }
        };

        assertNotNull( projectService );
    }

    @Test
    public void testNewProjectCreation() throws URISyntaxException {
        final URI fs = new URI( "git://test" );
        try {
            FileSystems.getFileSystem( fs );
        } catch ( FileSystemNotFoundException e ) {
            FileSystems.newFileSystem( fs,
                                       new HashMap<String, Object>() );
        }

        final Repository repository = mock( Repository.class );
        final POM pom = new POM();
        final String baseURL = "/";

        final Path repositoryRootPath = mock( Path.class );
        when( repository.getRoot() ).thenReturn( repositoryRootPath );
        when( repositoryRootPath.toURI() ).thenReturn( "git://test" );

        pom.setName( "p0" );
        pom.getGav().setGroupId( "org.kie.workbench.services" );
        pom.getGav().setArtifactId( "kie-wb-common-services-test" );
        pom.getGav().setVersion( "1.0.0-SNAPSHOT" );

        when( pomService.load( any( Path.class ) ) ).thenReturn( pom );

        final AbstractProjectService projectServiceSpy = spy( projectService );

        final Project project = projectServiceSpy.newProject( repository,
                                                              pom,
                                                              baseURL );

        verify( projectServiceSpy,
                times( 1 ) ).simpleProjectInstance( any( org.uberfire.java.nio.file.Path.class ) );

        assertEquals( pom,
                      project.getPom() );
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

        final DeleteProjectObserverBridge bridge = new DeleteProjectObserverBridge( ioService,
                                                                                    projectServiceSpy,
                                                                                    deleteProjectEvent );

        bridge.onBatchResourceChanges( new ResourceDeletedEvent( path,
                                                                 "message",
                                                                 sessionInfo ) );

        verify( deleteProjectEvent,
                times( 1 ) ).fire( any( DeleteProjectEvent.class ) );

        verify( projectServiceSpy,
                times( 0 ) ).newProject( any( Repository.class ),
                                         any( POM.class ),
                                         any( String.class ) );
        verify( projectServiceSpy,
                times( 1 ) ).simpleProjectInstance( any( org.uberfire.java.nio.file.Path.class ) );
    }

    @Test
    public void testPackageNameWhiteList() throws URISyntaxException {
        final URI fs = new URI( "git://test" );
        try {
            FileSystems.getFileSystem( fs );
        } catch ( FileSystemNotFoundException e ) {
            FileSystems.newFileSystem( fs,
                                       new HashMap<String, Object>() );
        }

        final Map<String, String> writes = new HashMap<String, String>();

        final Repository repository = mock( Repository.class );
        final POM pom = new POM();
        final String baseURL = "/";

        final Path repositoryRootPath = mock( Path.class );
        when( repository.getRoot() ).thenReturn( repositoryRootPath );
        when( repositoryRootPath.toURI() ).thenReturn( "git://test" );

        when( ioService.write( any( org.uberfire.java.nio.file.Path.class ),
                               anyString() ) ).thenAnswer( new Answer<Object>() {
            @Override
            public Object answer( final InvocationOnMock invocation ) throws Throwable {
                if ( invocation.getArguments().length == 2 ) {
                    final String path = ( (org.uberfire.java.nio.file.Path) invocation.getArguments()[ 0 ] ).toUri().getPath();
                    final String content = ( (String) invocation.getArguments()[ 1 ] );
                    writes.put( path,
                                content );
                }
                return invocation.getArguments()[ 0 ];
            }
        } );

        pom.setName( "p0" );
        pom.getGav().setGroupId( "org.kie.workbench.services" );
        pom.getGav().setArtifactId( "kie-wb-common-services-test" );
        pom.getGav().setVersion( "1.0.0-SNAPSHOT" );

        projectService.newProject( repository,
                                   pom,
                                   baseURL );

        assertTrue( writes.containsKey( "/p0/package-names-white-list" ) );
        assertEquals( "org.kie.workbench.services.kie_wb_common_services_test.**",
                      writes.get( "/p0/package-names-white-list" ) );
    }

}
