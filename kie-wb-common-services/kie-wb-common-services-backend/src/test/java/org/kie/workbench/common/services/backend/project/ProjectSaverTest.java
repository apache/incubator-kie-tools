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

import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.events.NewPackageEvent;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectRepositoriesService;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.junit.After;
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
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;
import org.uberfire.rpc.SessionInfo;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.mockito.ArgumentCaptor;

@RunWith(MockitoJUnitRunner.class)
public class ProjectSaverTest extends WeldProjectTestBase {

    public static final String GROUP_ID = "org.kie.workbench.services";
    public static final String ARTIFACT_ID = "kie-wb-common-services-test";
    public static final String VERSION = "1.0.0-SNAPSHOT";

    public static final String PROJECT_NAME = "p0";

    @Mock
    private IOService ioService;

    @Mock
    private POMService pomService;

    @Mock
    private KieResourceResolver resourceResolver;
    @Mock
    private Event<NewPackageEvent> newPackageEvent;

    private ProjectSaver saver;
    private SimpleFileSystemProvider fs;
    private Paths paths;

    @Before
    public void setUp() throws Exception {
        fs = new SimpleFileSystemProvider();

        super.startWeld();

        //Instantiate Paths used in tests for Path conversion
        final Bean pathsBean = (Bean) beanManager.getBeans( Paths.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( pathsBean );

        paths = (Paths) beanManager.getReference( pathsBean,
                Paths.class,
                cc );

        //Ensure URLs use the default:// scheme
        fs.forceAsDefault();

        final Event<NewProjectEvent> newProjectEvent = mock( Event.class );

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

    @After
    public void cleanUp() {
        super.stopWeld();
    }

    @Test
    public void testNewProjecCreationByGAV() throws IOException {

        final POM pom = new POM( new GAV( GROUP_ID, ARTIFACT_ID, VERSION ) );

        runProjecCreationTest( pom );
    }

    @Test
    public void testNewProjectRegularCreation() throws URISyntaxException, IOException {

        final POM pom = new POM();

        pom.setName( PROJECT_NAME );
        pom.getGav().setGroupId( GROUP_ID );
        pom.getGav().setArtifactId( ARTIFACT_ID );
        pom.getGav().setVersion( VERSION );

        runProjecCreationTest( pom );
    }

    /**
     * This test checks that when we have a managed repository and the user tries to create a project "project1" in the
     * given repository, and a project "project1" already exists. Then the parent pom.xml for the managed repository
     * must remain untouched. i.e. If the project "project1" already exists then the parent pom.xml shouldn't be modified.
     * In this way the parent pom.xml remains consistent with the already existing structure.
     */
    @Test
    public void testDuplicatedChildInManagedRepositoryPrevention() throws URISyntaxException, IOException {

        final POM parentPom = mock( POM.class );
        final POM newPOM = mock( POM.class );

        final Repository repository = mock( Repository.class );
        final String baseURL = "/";

        final File test = File.createTempFile( "test", Long.toString( System.nanoTime() ) );
        final Path repositoryRootPath = paths.convert( fs.getPath( test.toURI() ) );

        FileAlreadyExistsException fileExistsException = null;

        when( repository.getRoot() ).thenReturn( repositoryRootPath );

        //name for the project we are trying to re-create over an existing one.
        when ( newPOM.getName() ).thenReturn( "existingProject" );

        //path that will be calculated for looking for the parent pom.xml for the managed repository. (the parent pom.xml
        //lies basically in the root of the repository by definition.)
        final org.uberfire.java.nio.file.Path parentPomNioPath = paths.convert( repositoryRootPath ).resolve( "pom.xml" );
        final Path parentPomVFSPath = paths.convert( parentPomNioPath );

        //path that will be calculated for saving the project pom.xml for the project that are about to be created.
        final org.uberfire.java.nio.file.Path projectNioPath = paths.convert( repositoryRootPath ).resolve( "existingProject" ).resolve( "pom.xml" );

        //emulates that we have a parent pom.xml in current repository root.
        //This is the case for the ManagedRepository.
        when( pomService.load( parentPomVFSPath ) ).thenReturn( parentPom );

        //emulate the project already exists
        when ( ioService.exists( projectNioPath ) ).thenReturn( true );

        try {
            saver.save( repositoryRootPath, newPOM, baseURL );
        } catch ( FileAlreadyExistsException e ) {
            fileExistsException = e;
        }

        //The file already exists must have been thrown, since the project already exists.
        assertNotNull( fileExistsException );

        //And also the parent pom must have never been updated/modified.
        verify( pomService, never() ).save( eq( parentPomVFSPath ) , any( POM.class ), any( Metadata.class ), any( String.class ) );
    }

    @Test
    public void testGavBasedPackagesSanitized() throws IOException {
        String unsanitizedId = "hyphs-and.int.123";
        String sanitizedPkgStructure = "hyphs_and/_int/_23/hyphs_and/_int/_23";

        POM pom = new POM();
        pom.setName( "gavBassedPackagesTest" );
        pom.getGav().setGroupId( unsanitizedId );
        pom.getGav().setArtifactId( unsanitizedId );
        pom.getGav().setVersion( VERSION );

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass( Event.class );

        doNothing().when( newPackageEvent ).fire( ( NewPackageEvent ) eventCaptor.capture() );
        when( resourceResolver.newPackage( any( org.guvnor.common.services.project.model.Package.class ), anyString(), anyBoolean() ) )
            .thenAnswer( new Answer<Object>() {
                @Override
                public Object answer( InvocationOnMock invocation ) throws Throwable {
                    org.guvnor.common.services.project.model.Package pkg = mock( org.guvnor.common.services.project.model.Package.class );
                    when( pkg.getRelativeCaption() ).thenReturn( ( String ) invocation.getArguments()[1] );
                    return pkg;
                }
            } );

        runProjecCreationTest( pom );

        assertEquals( sanitizedPkgStructure, ( ( NewPackageEvent ) eventCaptor.getValue() ).getPackage().getRelativeCaption() );
    }

    protected void runProjecCreationTest( final POM pom ) throws IOException {
        final Repository repository = mock( Repository.class );
        final String baseURL = "/";

        final File test = File.createTempFile( "test", Long.toString( System.nanoTime() ) );
        final Path repositoryRootPath = paths.convert( fs.getPath( test.toURI() ) );

        when( repository.getRoot() ).thenReturn( repositoryRootPath );


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

        Project project = saver.save( repository.getRoot(), pom, baseURL );

        assertEquals( 4, directories.size() );
        assertTrue( directories.add( "src/main/java" ) );
        assertTrue( directories.add( "src/main/resources" ) );
        assertTrue( directories.add( "src/test/resources" ) );
        assertTrue( directories.add( "src/main/java" ) );

        assertEquals( pom, project.getPom() );
    }
}
