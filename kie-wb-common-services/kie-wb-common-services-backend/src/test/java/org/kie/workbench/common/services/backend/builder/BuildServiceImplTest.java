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

package org.kie.workbench.common.services.backend.builder;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.drools.core.rule.TypeMetaInfo;
import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildValidationHelper;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.scanner.KieModuleMetaData;
import org.kie.workbench.common.services.backend.whitelist.PackageNameSearchProvider;
import org.kie.workbench.common.services.backend.whitelist.PackageNameWhiteListServiceImpl;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BuildServiceImplTest
        extends BuilderTestBase {

    @Mock
    private PackageNameSearchProvider packageNameSearchProvider;

    @Before
    public void setUp() throws Exception {
        PackageNameSearchProvider.PackageNameSearch nameSearch = mock( PackageNameSearchProvider.PackageNameSearch.class );
        when( nameSearch.search() ).thenReturn( new HashSet<String>() );
        when( packageNameSearchProvider.newTopLevelPackageNamesSearch( any( POM.class ) ) ).thenReturn( nameSearch );
        startMain();
        setUpGuvnorM2Repo();
    }

    @Test
    public void testBuilderSimpleKProject() throws Exception {
        IOService ioService = getReference( IOService.class );
        KieProjectService projectService = getReference( KieProjectService.class );
        ProjectImportsService importsService = getReference( ProjectImportsService.class );
        LRUProjectDependenciesClassLoaderCache dependenciesClassLoaderCache = getReference( LRUProjectDependenciesClassLoaderCache.class );
        LRUPomModelCache pomModelCache = getReference( LRUPomModelCache.class );

        URL url = this.getClass().getResource( "/GuvnorM2RepoDependencyExample1" );
        SimpleFileSystemProvider p = new SimpleFileSystemProvider();
        org.uberfire.java.nio.file.Path path = p.getPath( url.toURI() );

        final Project project = projectService.resolveProject( Paths.convert( path ) );

        final Builder builder = new Builder( project,
                                             ioService,
                                             projectService,
                                             importsService,
                                             new ArrayList<BuildValidationHelper>(),
                                             dependenciesClassLoaderCache,
                                             pomModelCache,
                                             new PackageNameWhiteListServiceImpl( ioService,
                                                                              packageNameSearchProvider ) );

        final BuildResults results = builder.build();

        assertTrue( results.getMessages().isEmpty() );
    }

    @Test
    public void testBuilderKProjectHasDependency() throws Exception {
        IOService ioService = getReference( IOService.class );
        KieProjectService projectService = getReference( KieProjectService.class );
        ProjectImportsService importsService = getReference( ProjectImportsService.class );
        LRUProjectDependenciesClassLoaderCache dependenciesClassLoaderCache = getReference( LRUProjectDependenciesClassLoaderCache.class );
        LRUPomModelCache pomModelCache = getReference( LRUPomModelCache.class );

        URL url = this.getClass().getResource( "/GuvnorM2RepoDependencyExample2" );
        SimpleFileSystemProvider p = new SimpleFileSystemProvider();
        org.uberfire.java.nio.file.Path path = p.getPath( url.toURI() );

        final Project project = projectService.resolveProject( Paths.convert( path ) );

        final Builder builder = new Builder( project,
                                             ioService,
                                             projectService,
                                             importsService,
                                             new ArrayList<BuildValidationHelper>(),
                                             dependenciesClassLoaderCache,
                                             pomModelCache,
                                             new PackageNameWhiteListServiceImpl( ioService,
                                                                              packageNameSearchProvider ) );

        final BuildResults results = builder.build();

        //Debug output
        if ( !results.getMessages().isEmpty() ) {
            for ( BuildMessage m : results.getMessages() ) {
                System.out.println( m.getText() );
            }
        }

        assertTrue( results.getMessages().isEmpty() );
    }

    @Test
    public void testBuilderKProjectHasSnapshotDependency() throws Exception {
        IOService ioService = getReference( IOService.class );
        KieProjectService projectService = getReference( KieProjectService.class );
        ProjectImportsService importsService = getReference( ProjectImportsService.class );
        LRUProjectDependenciesClassLoaderCache dependenciesClassLoaderCache = getReference( LRUProjectDependenciesClassLoaderCache.class );
        LRUPomModelCache pomModelCache = getReference( LRUPomModelCache.class );

        URL url = this.getClass().getResource( "/GuvnorM2RepoDependencyExample2Snapshot" );
        SimpleFileSystemProvider p = new SimpleFileSystemProvider();
        org.uberfire.java.nio.file.Path path = p.getPath( url.toURI() );

        final Project project = projectService.resolveProject( Paths.convert( path ) );

        final Builder builder = new Builder( project,
                                             ioService,
                                             projectService,
                                             importsService,
                                             new ArrayList<BuildValidationHelper>(),
                                             dependenciesClassLoaderCache,
                                             pomModelCache,
                                             new PackageNameWhiteListServiceImpl( ioService,
                                                                              packageNameSearchProvider ) );

        final BuildResults results = builder.build();

        //Debug output
        if ( !results.getMessages().isEmpty() ) {
            for ( BuildMessage m : results.getMessages() ) {
                System.out.println( m.getText() );
            }
        }

        assertTrue( results.getMessages().isEmpty() );
    }

    @Test
    public void testBuilderKProjectHasDependencyMetaData() throws Exception {
        IOService ioService = getReference( IOService.class );
        KieProjectService projectService = getReference( KieProjectService.class );
        ProjectImportsService importsService = getReference( ProjectImportsService.class );
        LRUProjectDependenciesClassLoaderCache dependenciesClassLoaderCache = getReference( LRUProjectDependenciesClassLoaderCache.class );
        LRUPomModelCache pomModelCache = getReference( LRUPomModelCache.class );

        URL url = this.getClass().getResource( "/GuvnorM2RepoDependencyExample2" );
        SimpleFileSystemProvider p = new SimpleFileSystemProvider();
        org.uberfire.java.nio.file.Path path = p.getPath( url.toURI() );

        final Project project = projectService.resolveProject( Paths.convert( path ) );

        final Builder builder = new Builder( project,
                                             ioService,
                                             projectService,
                                             importsService,
                                             new ArrayList<BuildValidationHelper>(),
                                             dependenciesClassLoaderCache,
                                             pomModelCache,
                                             new PackageNameWhiteListServiceImpl( ioService,
                                                                              packageNameSearchProvider ) );

        final BuildResults results = builder.build();

        //Debug output
        if ( !results.getMessages().isEmpty() ) {
            for ( BuildMessage m : results.getMessages() ) {
                System.out.println( m.getText() );
            }
        }

        assertTrue( results.getMessages().isEmpty() );

        final KieModuleMetaData metaData = KieModuleMetaData.Factory.newKieModuleMetaData( builder.getKieModule() );

        //Check packages
        final Set<String> packageNames = new HashSet<String>();
        final Iterator<String> packageNameIterator = metaData.getPackages().iterator();
        while ( packageNameIterator.hasNext() ) {
            packageNames.add( packageNameIterator.next() );
        }
        assertEquals( 2,
                      packageNames.size() );
        assertTrue( packageNames.contains( "defaultpkg" ) );
        assertTrue( packageNames.contains( "org.kie.workbench.common.services.builder.tests.test1" ) );

        //Check classes
        final String packageName = "org.kie.workbench.common.services.builder.tests.test1";
        assertEquals( 1,
                      metaData.getClasses( packageName ).size() );
        final String className = metaData.getClasses( packageName ).iterator().next();
        assertEquals( "Bean",
                      className );

        //Check metadata
        final Class clazz = metaData.getClass( packageName,
                                               className );
        final TypeMetaInfo typeMetaInfo = metaData.getTypeMetaInfo( clazz );
        assertNotNull( typeMetaInfo );
        assertFalse( typeMetaInfo.isEvent() );
    }

    @Test
    public void testKProjectContainsXLS() throws Exception {
        IOService ioService = getReference( IOService.class );
        KieProjectService projectService = getReference( KieProjectService.class );
        ProjectImportsService importsService = getReference( ProjectImportsService.class );
        LRUProjectDependenciesClassLoaderCache dependenciesClassLoaderCache = getReference( LRUProjectDependenciesClassLoaderCache.class );
        LRUPomModelCache pomModelCache = getReference( LRUPomModelCache.class );

        URL url = this.getClass().getResource( "/ExampleWithExcel" );
        SimpleFileSystemProvider p = new SimpleFileSystemProvider();
        org.uberfire.java.nio.file.Path path = p.getPath( url.toURI() );

        final Project project = projectService.resolveProject( Paths.convert( path ) );

        final Builder builder = new Builder( project,
                                             ioService,
                                             projectService,
                                             importsService,
                                             new ArrayList<BuildValidationHelper>(),
                                             dependenciesClassLoaderCache,
                                             pomModelCache,
                                             new PackageNameWhiteListServiceImpl( ioService,
                                                                              packageNameSearchProvider ) );

        final BuildResults results = builder.build();

        //Debug output
        if ( !results.getMessages().isEmpty() ) {
            for ( BuildMessage m : results.getMessages() ) {
                System.out.println( m.getText() );
            }
        }

        assertTrue( results.getMessages().isEmpty() );
    }

}
