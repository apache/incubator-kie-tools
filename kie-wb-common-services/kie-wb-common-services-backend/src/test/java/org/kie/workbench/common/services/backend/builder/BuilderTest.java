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
import java.util.HashSet;
import java.util.List;

import org.guvnor.common.services.project.builder.service.BuildValidationHelper;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.workbench.common.services.backend.whitelist.PackageNameSearchProvider;
import org.kie.workbench.common.services.backend.whitelist.PackageNameWhiteListServiceImpl;
import org.kie.workbench.common.services.backend.validation.DefaultGenericKieValidator;
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
public class BuilderTest
        extends BuilderTestBase {

    @Mock
    private PackageNameSearchProvider packageNameSearchProvider;

    private IOService ioService;
    private KieProjectService projectService;
    private ProjectImportsService importsService;
    private LRUProjectDependenciesClassLoaderCache dependenciesClassLoaderCache;
    private LRUPomModelCache pomModelCache;

    @Before
    public void setUp() throws Exception {
        PackageNameSearchProvider.PackageNameSearch nameSearch = mock( PackageNameSearchProvider.PackageNameSearch.class );
        when( nameSearch.search() ).thenReturn( new HashSet<String>() );
        when( packageNameSearchProvider.newTopLevelPackageNamesSearch( any( POM.class ) ) ).thenReturn( nameSearch );
        startMain();
        setUpGuvnorM2Repo();

        ioService = getReference( IOService.class );
        projectService = getReference( KieProjectService.class );
        importsService = getReference( ProjectImportsService.class );
        dependenciesClassLoaderCache = getReference( LRUProjectDependenciesClassLoaderCache.class );
        pomModelCache = getReference( LRUPomModelCache.class );
    }

    @Test
    public void testBuilderSimpleKProject() throws Exception {
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

        assertNotNull( builder.getKieContainer() );
    }

    @Test
    public void testBuilderFixForBrokenKProject() throws Exception {

        LRUPomModelCache pomModelCache = getReference( LRUPomModelCache.class );

        SimpleFileSystemProvider provider = new SimpleFileSystemProvider();
        org.uberfire.java.nio.file.Path path = provider.getPath( this.getClass().getResource( "/BuilderExampleBrokenSyntax" ).toURI() );

        final Project project = projectService.resolveProject( Paths.convert( path ) );

        final Builder builder = new Builder( project,
                                             ioService,
                                             projectService,
                                             importsService,
                                             new ArrayList<BuildValidationHelper>(),
                                             dependenciesClassLoaderCache,
                                             pomModelCache,
                                             new PackageNameWhiteListServiceImpl( ioService ,
                                                                              packageNameSearchProvider ) );

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
                                             dependenciesClassLoaderCache,
                                             pomModelCache,
                                             new PackageNameWhiteListServiceImpl( ioService,
                                                                              packageNameSearchProvider ) );

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