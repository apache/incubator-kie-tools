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
import java.util.List;

import org.guvnor.common.services.project.builder.service.BuildValidationHelper;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.workbench.common.services.backend.validation.DefaultGenericKieValidator;
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
                                             new PackageNameWhiteList( ioService ),
                                             dependenciesClassLoaderCache,
                                             pomModelCache );

        assertNotNull( builder.getKieContainer() );
    }

    @Test
    public void testBuilderFixForBrokenKProject() throws Exception {

        IOService ioService = getReference( IOService.class );
        KieProjectService projectService = getReference( KieProjectService.class );
        ProjectImportsService importsService = getReference( ProjectImportsService.class );
        LRUProjectDependenciesClassLoaderCache dependenciesClassLoaderCache = getReference( LRUProjectDependenciesClassLoaderCache.class );
        LRUPomModelCache pomModelCache = getReference( LRUPomModelCache.class );

        SimpleFileSystemProvider provider = new SimpleFileSystemProvider();
        org.uberfire.java.nio.file.Path path = provider.getPath( this.getClass().getResource( "/BuilderExampleBrokenSyntax" ).toURI() );

        final Project project = projectService.resolveProject( Paths.convert( path ) );

        final Builder builder = new Builder( project,
                                             ioService,
                                             projectService,
                                             importsService,
                                             new ArrayList<BuildValidationHelper>(),
                                             new PackageNameWhiteList( ioService ),
                                             dependenciesClassLoaderCache,
                                             pomModelCache );

        assertNull( builder.getKieContainer() );

        builder.deleteResource( provider.getPath( this.getClass().getResource( File.separatorChar + "BuilderExampleBrokenSyntax" +
                                                                                       File.separatorChar + "src" +
                                                                                       File.separatorChar + "main" +
                                                                                       File.separatorChar + "resources" +
                                                                                       File.separatorChar + "rule1.drl"
                                                                             ).toURI() ) );

        assertNotNull( builder.getKieContainer() );
    }

    @Test
    public void testBuilderKieContainerInstantiation() throws Exception {
        final IOService ioService = getReference( IOService.class );
        final KieProjectService projectService = getReference( KieProjectService.class );
        final ProjectImportsService importsService = getReference( ProjectImportsService.class );
        final LRUProjectDependenciesClassLoaderCache dependenciesClassLoaderCache = getReference( LRUProjectDependenciesClassLoaderCache.class );
        final LRUPomModelCache pomModelCache = getReference( LRUPomModelCache.class );

        final URL url = this.getClass().getResource( "/GuvnorM2RepoDependencyExample1" );
        final SimpleFileSystemProvider p = new SimpleFileSystemProvider();
        final org.uberfire.java.nio.file.Path path = p.getPath( url.toURI() );

        final Project project = projectService.resolveProject( Paths.convert( path ) );

        //Build Project, including Rules and Global definition
        final Builder builder = new Builder( project,
                                             ioService,
                                             projectService,
                                             importsService,
                                             new ArrayList<BuildValidationHelper>(),
                                             new PackageNameWhiteList( ioService ),
                                             dependenciesClassLoaderCache,
                                             pomModelCache );

        assertNotNull( builder.getKieContainer() );

        //Validate Rule excluding Global definition
        final DefaultGenericKieValidator validator = new DefaultGenericKieValidator( ioService,
                                                                                     projectService );
        final URL urlToValidate = this.getClass().getResource( "/GuvnorM2RepoDependencyExample1/src/main/resources/rule2.drl" );
        final org.uberfire.java.nio.file.Path pathToValidate = p.getPath( urlToValidate.toURI() );
        final List<ValidationMessage> validationMessages = validator.validate( Paths.convert( pathToValidate ),
                                                                               this.getClass().getResourceAsStream( "/GuvnorM2RepoDependencyExample1/src/main/resources/rule2.drl" ) );
        assertNotNull( validationMessages );
        assertEquals( 0,
                      validationMessages.size() );

        // Retrieve a KieSession for the Project and set the Global. This should not fail as the
        // KieContainer is retrieved direct from the KieBuilder and not KieRepository (as was the
        // case before BZ1202551 was fixed.
        final KieContainer kieContainer1 = builder.getKieContainer();
        final KieSession kieSession1 = kieContainer1.newKieSession();
        kieSession1.setGlobal( "list",
                               new ArrayList<String>() );
    }

}