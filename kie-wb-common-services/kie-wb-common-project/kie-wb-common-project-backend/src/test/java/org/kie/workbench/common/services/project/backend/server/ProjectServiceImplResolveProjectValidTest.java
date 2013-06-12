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

package org.kie.workbench.common.services.project.backend.server;

import java.net.URL;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.environment.se.StartMain;
import org.junit.Before;
import org.junit.Test;
import org.kie.commons.java.nio.fs.file.SimpleFileSystemProvider;
import org.kie.workbench.common.services.project.service.ProjectService;
import org.kie.workbench.common.services.shared.context.Project;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;

public class ProjectServiceImplResolveProjectValidTest {

    private final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();
    private BeanManager beanManager;
    private Paths paths;

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
    public void testProjectServiceInstantiation() throws Exception {

        final Bean projectServiceBean = (Bean) beanManager.getBeans( ProjectService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( projectServiceBean );
        final ProjectService projectService = (ProjectService) beanManager.getReference( projectServiceBean,
                                                                                         ProjectService.class,
                                                                                         cc );
        assertNotNull( projectService );
    }

    @Test
    public void testResolveProjectWithNonProjectPath() throws Exception {

        final Bean projectServiceBean = (Bean) beanManager.getBeans( ProjectService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( projectServiceBean );
        final ProjectService projectService = (ProjectService) beanManager.getReference( projectServiceBean,
                                                                                         ProjectService.class,
                                                                                         cc );

        final URL testUrl = this.getClass().getResource( "/" );
        final org.kie.commons.java.nio.file.Path testNioPath = fs.getPath( testUrl.toURI() );
        final Path testPath = paths.convert( testNioPath );

        //Test a non-Project Path resolves to null
        final Project result = projectService.resolveProject( testPath );
        assertNull( result );
    }

    @Test
    public void testResolveProjectWithRootPath() throws Exception {

        final Bean projectServiceBean = (Bean) beanManager.getBeans( ProjectService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( projectServiceBean );
        final ProjectService projectService = (ProjectService) beanManager.getReference( projectServiceBean,
                                                                                         ProjectService.class,
                                                                                         cc );

        final URL rootUrl = this.getClass().getResource( "/ProjectBackendTestProjectStructureValid" );
        final org.kie.commons.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        //Test a root resolves to the Project's root
        final Project result = projectService.resolveProject( rootPath );
        assertEquals( rootPath.toURI(),
                      result.getRootPath().toURI() );
    }

    @Test
    public void testResolveProjectWithChildPath() throws Exception {

        final Bean projectServiceBean = (Bean) beanManager.getBeans( ProjectService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( projectServiceBean );
        final ProjectService projectService = (ProjectService) beanManager.getReference( projectServiceBean,
                                                                                         ProjectService.class,
                                                                                         cc );

        final URL rootUrl = this.getClass().getResource( "/ProjectBackendTestProjectStructureValid" );
        final org.kie.commons.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        final URL testUrl = this.getClass().getResource( "/ProjectBackendTestProjectStructureValid/src" );
        final org.kie.commons.java.nio.file.Path nioTestPath = fs.getPath( testUrl.toURI() );
        final Path testPath = paths.convert( nioTestPath );

        //Test a child folder resolves to the Project's root
        final Project result = projectService.resolveProject( testPath );
        assertEquals( rootPath.toURI(),
                      result.getRootPath().toURI() );
    }

    @Test
    public void testResolveProjectWithJavaFile() throws Exception {

        final Bean projectServiceBean = (Bean) beanManager.getBeans( ProjectService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( projectServiceBean );
        final ProjectService projectService = (ProjectService) beanManager.getReference( projectServiceBean,
                                                                                         ProjectService.class,
                                                                                         cc );

        final URL rootUrl = this.getClass().getResource( "/ProjectBackendTestProjectStructureValid" );
        final org.kie.commons.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        final URL testUrl = this.getClass().getResource( "/ProjectBackendTestProjectStructureValid/src/main/java/org/kie/test/Bean.java" );
        final org.kie.commons.java.nio.file.Path nioTestPath = fs.getPath( testUrl.toURI() );
        final Path testPath = paths.convert( nioTestPath );

        //Test a child folder resolves to the Project's root
        final Project result = projectService.resolveProject( testPath );
        assertEquals( rootPath.toURI(),
                      result.getRootPath().toURI() );
    }

    @Test
    public void testResolveProjectWithResourcesFile() throws Exception {

        final Bean projectServiceBean = (Bean) beanManager.getBeans( ProjectService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( projectServiceBean );
        final ProjectService projectService = (ProjectService) beanManager.getReference( projectServiceBean,
                                                                                         ProjectService.class,
                                                                                         cc );

        final URL rootUrl = this.getClass().getResource( "/ProjectBackendTestProjectStructureValid" );
        final org.kie.commons.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        final URL testUrl = this.getClass().getResource( "/ProjectBackendTestProjectStructureValid/src/main/resources/rule1.drl" );
        final org.kie.commons.java.nio.file.Path nioTestPath = fs.getPath( testUrl.toURI() );
        final Path testPath = paths.convert( nioTestPath );

        //Test a child folder resolves to the Project's root
        final Project result = projectService.resolveProject( testPath );
        assertEquals( rootPath.toURI(),
                      result.getRootPath().toURI() );
    }

}
