/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

import java.net.URL;
import java.util.Set;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.jboss.weld.environment.se.StartMain;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.junit.Assert.*;

/**
 * Tests for ProjectServiceImpl resolveTestPackage
 */
public class ProjectServiceImplResolvePackagesTest {

    private final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();
    private BeanManager beanManager;
    private Paths paths;

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
    public void setUp() throws Exception {
        //Bootstrap WELD container
        StartMain startMain = new StartMain( new String[ 0 ] );
        beanManager = startMain.go().getBeanManager();

        //Instantiate Paths used in tests for Path conversion
        final Bean pathsBean = (Bean) beanManager.getBeans( Paths.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( pathsBean );
        paths = (Paths) beanManager.getReference( pathsBean,
                                                  Paths.class,
                                                  cc );

        //Ensure URLs use the default:// scheme
        fs.forceAsDefault();
    }

    @Test
    public void testResolvePackages() throws Exception {

        final Bean projectServiceBean = (Bean) beanManager.getBeans( KieProjectService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( projectServiceBean );
        final KieProjectService projectService = (KieProjectService) beanManager.getReference( projectServiceBean,
                                                                                               KieProjectService.class,
                                                                                               cc );

        final URL root = this.getClass().getResource( "/ProjectBackendTestProject1" );
        final URL pom = this.getClass().getResource( "/ProjectBackendTestProject1/pom.xml" );
        final URL kmodule = this.getClass().getResource( "/ProjectBackendTestProject1/src/main/resources/META-INF/kmodule.xml" );
        final URL imports = this.getClass().getResource( "/ProjectBackendTestProject1/project.imports" );
        final URL repositories = this.getClass().getResource( "/ProjectBackendTestProject1/project.repositories" );
        final URL packageNameWhiteList = this.getClass().getResource( "/ProjectBackendTestProject1/package-names-white-list" );
        final Project project = new KieProject( paths.convert( fs.getPath( root.toURI() ) ),
                                                paths.convert( fs.getPath( pom.toURI() ) ),
                                                paths.convert( fs.getPath( kmodule.toURI() ) ),
                                                paths.convert( fs.getPath( imports.toURI() ) ),
                                                paths.convert( fs.getPath( repositories.toURI() ) ),
                                                paths.convert( fs.getPath( packageNameWhiteList.toURI() ) ),
                                                "ProjectBackendTestProject1" );

        {
            Set<Package> packages = projectService.resolvePackages( (Package) null );
            assertEquals( 0, packages.size() );
        }

        Package defaultPkg = null;
        {
            Set<Package> packages = projectService.resolvePackages( project );
            assertEquals( 6, packages.size() );
            for ( final Package pkg : packages ) {
                if ( pkg.getCaption().equals( "<default>" ) ) {
                    defaultPkg = pkg;
                    break;
                }
            }
            assertEquals( defaultPkg, projectService.resolveDefaultPackage( project ) );
        }

        assertNotNull( defaultPkg );
        assertEquals( "<default>", defaultPkg.getCaption() );
        assertEquals( "<default>", defaultPkg.getRelativeCaption() );

        Package rootPkg = null;
        {
            Set<Package> packages = projectService.resolvePackages( defaultPkg );
            assertEquals( 1, packages.size() );
            rootPkg = packages.iterator().next();
        }

        assertNotNull( rootPkg );
        assertEquals( "org", rootPkg.getCaption() );
        assertEquals( "org", rootPkg.getRelativeCaption() );

        Package kiePkg = null;
        {
            Set<Package> packages = projectService.resolvePackages( rootPkg );
            assertEquals( 1, packages.size() );
            kiePkg = packages.iterator().next();
        }
        assertNotNull( kiePkg );
        assertEquals( "org.kie", kiePkg.getCaption() );
        assertEquals( "kie", kiePkg.getRelativeCaption() );

        assertEquals( rootPkg, projectService.resolveParentPackage( kiePkg ) );

        assertEquals( defaultPkg, projectService.resolveParentPackage( rootPkg ) );

        assertNull( projectService.resolveParentPackage( defaultPkg ) );

        {
            Set<Package> packages = projectService.resolvePackages( kiePkg );
            assertEquals( 1, packages.size() );
        }
    }

}
