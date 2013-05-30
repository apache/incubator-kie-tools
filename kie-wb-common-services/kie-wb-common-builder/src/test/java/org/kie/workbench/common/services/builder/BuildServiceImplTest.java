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

package org.kie.workbench.common.services.builder;

import java.io.InputStream;
import java.net.URL;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.drools.core.rule.TypeMetaInfo;
import org.guvnor.m2repo.backend.server.ExtendedM2RepoService;
import org.jboss.weld.environment.se.StartMain;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.fs.file.SimpleFileSystemProvider;
import org.kie.scanner.KieModuleMetaData;
import org.kie.workbench.common.services.project.service.ProjectService;
import org.kie.workbench.common.services.project.service.model.GAV;
import org.kie.workbench.common.services.shared.builder.model.BuildMessage;
import org.kie.workbench.common.services.shared.builder.model.BuildResults;
import org.uberfire.backend.server.util.Paths;

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
        Paths paths = getReference( Paths.class );
        IOService ioService = getReference( IOService.class );
        ProjectService projectService = getReference( ProjectService.class );

        URL url = this.getClass().getResource( "/GuvnorM2RepoDependencyExample1" );
        SimpleFileSystemProvider p = new SimpleFileSystemProvider();
        org.kie.commons.java.nio.file.Path path = p.getPath( url.toURI() );

        final Builder builder = new Builder( path,
                                             "guvnor-m2repo-dependency-example1",
                                             paths,
                                             ioService,
                                             projectService );

        final BuildResults results = builder.build();

        assertTrue( results.getMessages().isEmpty() );
    }

    @Test
    public void testBuilderKProjectHasDependency() throws Exception {
        Paths paths = getReference( Paths.class );
        IOService ioService = getReference( IOService.class );
        ProjectService projectService = getReference( ProjectService.class );

        URL url = this.getClass().getResource( "/GuvnorM2RepoDependencyExample2" );
        SimpleFileSystemProvider p = new SimpleFileSystemProvider();
        org.kie.commons.java.nio.file.Path path = p.getPath( url.toURI() );

        final Builder builder = new Builder( path,
                                             "guvnor-m2repo-dependency-example2",
                                             paths,
                                             ioService,
                                             projectService );

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
    @Ignore("This fails unless the parent project contains a KBase definition in the kmodule.xml")
    public void testBuilderKProjectHasDependencyMissingKBaseDefinition() throws Exception {
        Paths paths = getReference( Paths.class );
        IOService ioService = getReference( IOService.class );
        ProjectService projectService = getReference( ProjectService.class );

        URL url = this.getClass().getResource( "/GuvnorM2RepoDependencyExample2MissingKBase" );
        SimpleFileSystemProvider p = new SimpleFileSystemProvider();
        org.kie.commons.java.nio.file.Path path = p.getPath( url.toURI() );

        final Builder builder = new Builder( path,
                                             "guvnor-m2repo-dependency-example2-missing-kbase",
                                             paths,
                                             ioService,
                                             projectService );

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
        Paths paths = getReference( Paths.class );
        IOService ioService = getReference( IOService.class );
        ProjectService projectService = getReference( ProjectService.class );

        URL url = this.getClass().getResource( "/GuvnorM2RepoDependencyExample2Snapshot" );
        SimpleFileSystemProvider p = new SimpleFileSystemProvider();
        org.kie.commons.java.nio.file.Path path = p.getPath( url.toURI() );

        final Builder builder2 = new Builder( path,
                                              "guvnor-m2repo-dependency-example2-snapshot",
                                              paths,
                                              ioService,
                                              projectService );

        final BuildResults results2 = builder2.build();
        assertTrue( results2.getMessages().isEmpty() );
    }

    @Test
    public void testBuilderKProjectHasDependencyMetaData() throws Exception {
        Paths paths = getReference( Paths.class );
        IOService ioService = getReference( IOService.class );
        ProjectService projectService = getReference( ProjectService.class );

        URL url = this.getClass().getResource( "/GuvnorM2RepoDependencyExample2" );
        SimpleFileSystemProvider p = new SimpleFileSystemProvider();
        org.kie.commons.java.nio.file.Path path = p.getPath( url.toURI() );

        final Builder builder = new Builder( path,
                                             "guvnor-m2repo-dependency-example2",
                                             paths,
                                             ioService,
                                             projectService );

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
        assertEquals( 1,
                      metaData.getPackages().size() );
        final String packageName = metaData.getPackages().iterator().next();
        assertEquals( "org.kie.test.repodependencyexample1",
                      packageName );

        //Check classes
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
        Paths paths = getReference( Paths.class );
        IOService ioService = getReference( IOService.class );
        ProjectService projectService = getReference( ProjectService.class );

        URL url = this.getClass().getResource( "/ExampleWithExcel" );
        SimpleFileSystemProvider p = new SimpleFileSystemProvider();
        org.kie.commons.java.nio.file.Path path = p.getPath( url.toURI() );

        final Builder builder = new Builder( path,
                                             "example-with-excel",
                                             paths,
                                             ioService,
                                             projectService );

        final BuildResults results = builder.build();

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
        m2RepoService.deployJar( is,
                                 gav );

        //Deploy a SNAPSHOT version of guvnor-m2repo-dependency-example1-snapshot kjar
        GAV gav2 = new GAV( "org.kie.workbench.common.services.builder.tests",
                            "dependency-test1-snapshot",
                            "1.0-SNAPSHOT" );

        InputStream is2 = this.getClass().getResourceAsStream( "/dependency-test1-1.0.jar" );
        m2RepoService.deployJar( is2,
                                 gav2 );
    }
}
