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

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.drools.core.rule.TypeMetaInfo;
import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildValidationHelper;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.m2repo.backend.server.ExtendedM2RepoService;
import org.jboss.weld.environment.se.StartMain;
import org.junit.Before;
import org.junit.Test;
import org.kie.scanner.KieModuleMetaData;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.junit.Assert.*;

public class BuildServiceImplTest {

    private BeanManager beanManager;

    @Before
    public void setUp() throws Exception {
        StartMain startMain = new StartMain( new String[ 0 ] );
        beanManager = startMain.go().getBeanManager();

        setUpGuvnorM2Repo();
    }

    @Test
    public void testBuilderSimpleKProject() throws Exception {
        IOService ioService = getReference( IOService.class );
        ProjectService projectService = getReference( ProjectService.class );
        ProjectImportsService importsService = getReference( ProjectImportsService.class );

        URL url = this.getClass().getResource( "/GuvnorM2RepoDependencyExample1" );
        SimpleFileSystemProvider p = new SimpleFileSystemProvider();
        org.uberfire.java.nio.file.Path path = p.getPath( url.toURI() );

        final Builder builder = new Builder( path,
                                             new GAV(),
                                             ioService,
                                             projectService,
                                             importsService,
                                             new ArrayList<BuildValidationHelper>() );

        final BuildResults results = builder.build();

        assertTrue( results.getMessages().isEmpty() );
    }

    @Test
    public void testBuilderKProjectHasDependency() throws Exception {
        IOService ioService = getReference( IOService.class );
        ProjectService projectService = getReference( ProjectService.class );
        ProjectImportsService importsService = getReference( ProjectImportsService.class );

        URL url = this.getClass().getResource( "/GuvnorM2RepoDependencyExample2" );
        SimpleFileSystemProvider p = new SimpleFileSystemProvider();
        org.uberfire.java.nio.file.Path path = p.getPath( url.toURI() );

        final Builder builder = new Builder( path,
                                             new GAV(),
                                             ioService,
                                             projectService,
                                             importsService,
                                             new ArrayList<BuildValidationHelper>() );

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
        ProjectService projectService = getReference( ProjectService.class );
        ProjectImportsService importsService = getReference( ProjectImportsService.class );

        URL url = this.getClass().getResource( "/GuvnorM2RepoDependencyExample2Snapshot" );
        SimpleFileSystemProvider p = new SimpleFileSystemProvider();
        org.uberfire.java.nio.file.Path path = p.getPath( url.toURI() );

        final Builder builder = new Builder( path,
                                             new GAV(),
                                             ioService,
                                             projectService,
                                             importsService,
                                             new ArrayList<BuildValidationHelper>() );

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
        ProjectService projectService = getReference( ProjectService.class );
        ProjectImportsService importsService = getReference( ProjectImportsService.class );

        URL url = this.getClass().getResource( "/GuvnorM2RepoDependencyExample2" );
        SimpleFileSystemProvider p = new SimpleFileSystemProvider();
        org.uberfire.java.nio.file.Path path = p.getPath( url.toURI() );

        final Builder builder = new Builder( path,
                                             new GAV(),
                                             ioService,
                                             projectService,
                                             importsService,
                                             new ArrayList<BuildValidationHelper>() );

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
        ProjectService projectService = getReference( ProjectService.class );
        ProjectImportsService importsService = getReference( ProjectImportsService.class );

        URL url = this.getClass().getResource( "/ExampleWithExcel" );
        SimpleFileSystemProvider p = new SimpleFileSystemProvider();
        org.uberfire.java.nio.file.Path path = p.getPath( url.toURI() );

        final Builder builder = new Builder( path,
                                             new GAV(),
                                             ioService,
                                             projectService,
                                             importsService,
                                             new ArrayList<BuildValidationHelper>() );

        final BuildResults results = builder.build();

        //Debug output
        if ( !results.getMessages().isEmpty() ) {
            for ( BuildMessage m : results.getMessages() ) {
                System.out.println( m.getText() );
            }
        }

        assertTrue( results.getMessages().isEmpty() );
    }

    private <T> T getReference( Class<T> clazz ) {
        Bean bean = (Bean) beanManager.getBeans( clazz ).iterator().next();
        CreationalContext cc = beanManager.createCreationalContext( bean );
        return (T) beanManager.getReference( bean,
                                             clazz,
                                             cc );
    }

    private void setUpGuvnorM2Repo() {
        Bean m2RepoServiceBean = (Bean) beanManager.getBeans( ExtendedM2RepoService.class ).iterator().next();
        CreationalContext cc = beanManager.createCreationalContext( m2RepoServiceBean );
        ExtendedM2RepoService m2RepoService = (ExtendedM2RepoService) beanManager.getReference( m2RepoServiceBean,
                                                                                                ExtendedM2RepoService.class,
                                                                                                cc );

        String m2RepoURL = m2RepoService.getRepositoryURL( null );

        //Deploy a 1.0 version of guvnor-m2repo-dependency-example1-snapshot kjar
        GAV gav = new GAV( "org.kie.workbench.common.services.builder.tests",
                           "dependency-test1",
                           "1.0" );

        InputStream is = this.getClass().getResourceAsStream( "/dependency-test1-1.0.jar" );
        m2RepoService.deployJarInternal( is,
                                         gav );

        //Deploy a SNAPSHOT version of guvnor-m2repo-dependency-example1-snapshot kjar
        GAV gav2 = new GAV( "org.kie.workbench.common.services.builder.tests",
                            "dependency-test1-snapshot",
                            "1.0-SNAPSHOT" );

        InputStream is2 = this.getClass().getResourceAsStream( "/dependency-test1-snapshot-1.0-SNAPSHOT.jar" );
        m2RepoService.deployJarInternal( is2,
                                         gav2 );
    }
}
