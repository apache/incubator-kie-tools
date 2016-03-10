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

package org.kie.workbench.common.services.backend.project;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.events.NewPackageEvent;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectRepositoriesService;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.jboss.weld.environment.se.StartMain;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.kmodule.KModuleService;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.kie.workbench.common.services.shared.whitelist.PackageNameWhiteListService;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;
import org.uberfire.rpc.SessionInfo;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectSaverTest {

    @Mock
    private IOService ioService;

    @Mock
    private POMService pomService;

    @Mock
    private KieResourceResolver resourceResolver;

    private ProjectSaver saver;
    private SimpleFileSystemProvider fs;
    private Paths paths;

    @Before
    public void setUp() throws Exception {
        fs = new SimpleFileSystemProvider();

        //Bootstrap WELD container
        final StartMain startMain = new StartMain( new String[ 0 ] );
        final BeanManager beanManager = startMain.go().getBeanManager();

        //Instantiate Paths used in tests for Path conversion
        final Bean pathsBean = (Bean) beanManager.getBeans( Paths.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( pathsBean );

        paths = (Paths) beanManager.getReference( pathsBean,
                                                  Paths.class,
                                                  cc );

        //Ensure URLs use the default:// scheme
        fs.forceAsDefault();

        final Event<NewProjectEvent> newProjectEvent = mock( Event.class );
        final Event<NewPackageEvent> newPackageEvent = mock( Event.class );

        when( ioService.createDirectory( any( org.uberfire.java.nio.file.Path.class ) ) ).thenAnswer( new Answer<Object>() {
            @Override
            public Object answer( final InvocationOnMock invocation ) throws Throwable {
                return invocation.getArguments()[ 0 ];
            }
        } );

        saver = new ProjectSaver( ioService,
                                  pomService,
                                  mock( KModuleService.class ),
                                  newProjectEvent,
                                  newPackageEvent,
                                  resourceResolver,
                                  mock( ProjectImportsService.class ),
                                  mock( ProjectRepositoriesService.class ),
                                  mock( PackageNameWhiteListService.class ),
                                  mock( CommentedOptionFactory.class ),
                                  new SessionInfo() {
                                      @Override
                                      public String getId() {
                                          return "session";
                                      }

                                      @Override
                                      public User getIdentity() {
                                          return new UserImpl( "testuser" );
                                      }
                                  } );
    }

    @Test
    public void testNewProjectCreation() throws URISyntaxException, IOException {
        final Repository repository = mock( Repository.class );
        final POM pom = new POM();
        final String baseURL = "/";

        final File test = File.createTempFile( "test", Long.toString( System.nanoTime() ) );
        final Path repositoryRootPath = paths.convert( fs.getPath( test.toURI() ) );
        when( repository.getRoot() ).thenReturn( repositoryRootPath );

        pom.setName( "p0" );
        pom.getGav().setGroupId( "org.kie.workbench.services" );
        pom.getGav().setArtifactId( "kie-wb-common-services-test" );
        pom.getGav().setVersion( "1.0.0-SNAPSHOT" );

        when( pomService.load( any( Path.class ) ) ).thenReturn( pom );

        final ArrayList<String> directories = new ArrayList<String>();

        when( resourceResolver.simpleProjectInstance( any( org.uberfire.java.nio.file.Path.class ) ) ).thenReturn( mock( KieProject.class ) );

        final KieProject kieProject = new KieProject();
        kieProject.setPom( pom );
        when( resourceResolver.resolveProject( any( Path.class ) ) ).thenReturn( kieProject );

        stub( ioService.createDirectory( any( org.uberfire.java.nio.file.Path.class ) ) ).toAnswer( new Answer<org.uberfire.java.nio.file.Path>() {
            @Override
            public org.uberfire.java.nio.file.Path answer( final InvocationOnMock invocationOnMock ) throws Throwable {
                org.uberfire.java.nio.file.Path path = (org.uberfire.java.nio.file.Path) invocationOnMock.getArguments()[ 0 ];
                directories.add( path.toString() );
                return null;
            }
        } );

        Project project = saver.save( repository.getRoot() ,
                                      pom,
                                      baseURL );

        assertEquals( 4, directories.size() );
        assertTrue( directories.add( "src/main/java" ) );
        assertTrue( directories.add( "src/main/resources" ) );
        assertTrue( directories.add( "src/test/resources" ) );
        assertTrue( directories.add( "src/main/java" ) );

        assertEquals( pom,
                      project.getPom() );
    }
}