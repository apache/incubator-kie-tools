/*
 * Copyright 2015 JBoss Inc
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

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import org.guvnor.common.services.project.builder.service.BuildValidationHelper;
import org.guvnor.common.services.project.model.Project;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.junit.Assert.*;

public class BuilderTest
        extends BuilderTestBase {

    @Before
    public void setUp() throws Exception {
        startMain();
        setUpGuvnorM2Repo();
    }

    @Test
    public void testBuilderSimpleKProject() throws Exception {
        IOService ioService = getReference( IOService.class );
        KieProjectService projectService = getReference( KieProjectService.class );
        ProjectImportsService importsService = getReference( ProjectImportsService.class );
        LRUProjectDependenciesClassLoaderCache dependenciesClassLoaderCache = getReference( LRUProjectDependenciesClassLoaderCache.class );

        URL url = this.getClass().getResource( "/GuvnorM2RepoDependencyExample1" );
        SimpleFileSystemProvider p = new SimpleFileSystemProvider();
        org.uberfire.java.nio.file.Path path = p.getPath( url.toURI() );

        final Project project = projectService.resolveProject( Paths.convert( path ) );

        final Builder builder = new Builder( project,
                                             ioService,
                                             projectService,
                                             importsService,
                                             new ArrayList<BuildValidationHelper>(),
                                             new PackageNameWhiteList( ioService ),
                                             dependenciesClassLoaderCache );

        assertNotNull( builder.getKieContainer() );
    }

    @Test
    public void testBuilderFixForBrokenKProject() throws Exception {

        IOService ioService = getReference( IOService.class );
        KieProjectService projectService = getReference( KieProjectService.class );
        ProjectImportsService importsService = getReference( ProjectImportsService.class );
        LRUProjectDependenciesClassLoaderCache dependenciesClassLoaderCache = getReference( LRUProjectDependenciesClassLoaderCache.class );

        SimpleFileSystemProvider provider = new SimpleFileSystemProvider();
        org.uberfire.java.nio.file.Path path = provider.getPath( this.getClass().getResource( "/BuilderExampleBrokenSyntax" ).toURI() );

        final Project project = projectService.resolveProject( Paths.convert( path ) );

        final Builder builder = new Builder( project,
                                             ioService,
                                             projectService,
                                             importsService,
                                             new ArrayList<BuildValidationHelper>(),
                                             new PackageNameWhiteList( ioService ),
                                             dependenciesClassLoaderCache );

        assertNull( builder.getKieContainer() );

        builder.deleteResource( provider.getPath( this.getClass().getResource( File.separatorChar + "BuilderExampleBrokenSyntax" +
                                                                                       File.separatorChar + "src" +
                                                                                       File.separatorChar + "main" +
                                                                                       File.separatorChar + "resources" +
                                                                                       File.separatorChar + "rule1.drl"
                                                                             ).toURI() ) );

        assertNotNull( builder.getKieContainer() );
    }

}