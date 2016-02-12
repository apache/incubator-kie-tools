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

package org.kie.workbench.common.screens.projecteditor.backend.server;

import java.util.HashSet;
import java.util.List;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.MavenRepositorySource;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.ProjectImports;
import org.guvnor.common.services.project.model.ProjectRepositories;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectRepositoriesService;
import org.guvnor.common.services.project.service.ProjectRepositoryResolver;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.services.shared.kmodule.KModuleModel;
import org.kie.workbench.common.services.shared.kmodule.KModuleService;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.kie.workbench.common.services.shared.whitelist.PackageNameWhiteListService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.rpc.SessionInfo;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectScreenServiceImplTest {

    @Mock
    private POMService pomService;

    @Mock
    private KModuleService kModuleService;

    @Mock
    private KieProjectService projectService;

    @Mock
    private MetadataService metadataService;

    @Mock
    private ValidationService validationService;

    @Mock
    private ProjectImportsService importsService;

    @Mock
    private ProjectRepositoriesService repositoriesService;

    @Mock
    private PackageNameWhiteListService whiteListService;

    @Mock
    private ProjectRepositoryResolver repositoryResolver;

    @Mock
    private User identity;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private IOService ioService;

    @Mock
    private Path pathToPom;

    @Mock
    private Path pathToKieModule;

    @Mock
    private Path pathToProjectImports;

    @Mock
    private Path pathToProjectRepositories;

    @Mock
    private KieProject project;

    @Mock
    private KModuleModel kmodule;

    @Mock
    private Metadata pomMetaData;

    @Mock
    private Metadata kmoduleMetaData;

    @Mock
    private Metadata projectImportsMetaData;

    @Mock
    private CommentedOptionFactory commentedOptionFactory;

    private ProjectScreenService service;
    private ProjectScreenModelLoader loader;
    private ProjectScreenModelSaver saver;
    private ProjectImports projectImports;
    private ProjectRepositories projectRepositories;

    private GAV gav = new GAV( "org.test",
                               "project-screen-test",
                               "1.0.0" );
    private POM pom = new POM( "test",
                               "test",
                               gav );

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
    public void setup() {
        projectImports = new ProjectImports();
        projectRepositories = new ProjectRepositories();
        loader = new ProjectScreenModelLoader( projectService,
                                               pomService,
                                               metadataService,
                                               kModuleService,
                                               importsService,
                                               repositoriesService,
                                               whiteListService ) {
            @Override
            protected boolean fileExists( final Path path ) {
                return true;
            }
        };
        saver = new ProjectScreenModelSaver( pomService,
                                             kModuleService,
                                             importsService,
                                             repositoriesService,
                                             whiteListService,
                                             ioService,
                                             projectService,
                                             repositoryResolver,
                                             commentedOptionFactory );
        service = new ProjectScreenServiceImpl( projectService,
                                                loader,
                                                saver );

        when( project.getKModuleXMLPath() ).thenReturn( pathToKieModule );
        when( project.getImportsPath() ).thenReturn( pathToProjectImports );
        when( project.getRepositoriesPath() ).thenReturn( pathToProjectRepositories );
        when( project.getPom() ).thenReturn( pom );

        when( pathToPom.toURI() ).thenReturn( "default://project/pom.xml" );

        when( pomService.load( eq( pathToPom ) ) ).thenReturn( pom );
        when( kModuleService.load( eq( pathToKieModule ) ) ).thenReturn( kmodule );
        when( importsService.load( eq( pathToProjectImports ) ) ).thenReturn( projectImports );
        when( repositoriesService.load( eq( pathToProjectRepositories ) ) ).thenReturn( projectRepositories );

        when( projectService.resolveProject( eq( pathToPom ) ) ).thenReturn( project );

        when( metadataService.getMetadata( eq( pathToPom ) ) ).thenReturn( pomMetaData );
        when( metadataService.getMetadata( eq( pathToKieModule ) ) ).thenReturn( kmoduleMetaData );
        when( metadataService.getMetadata( eq( pathToProjectImports ) ) ).thenReturn( projectImportsMetaData );
    }

    @Test
    public void testLoad() throws Exception {
        final ProjectScreenModel model = service.load( pathToPom );

        assertEquals( pom,
                      model.getPOM() );
        assertEquals( pomMetaData,
                      model.getPOMMetaData() );
        assertEquals( pathToPom,
                      model.getPathToPOM() );

        assertEquals( kmodule,
                      model.getKModule() );
        assertEquals( kmoduleMetaData,
                      model.getKModuleMetaData() );
        assertEquals( pathToKieModule,
                      model.getPathToKModule() );

        assertEquals( projectImports,
                      model.getProjectImports() );
        assertEquals( projectImportsMetaData,
                      model.getProjectImportsMetaData() );
        assertEquals( pathToProjectImports,
                      model.getPathToImports() );

        assertEquals( projectRepositories,
                      model.getRepositories() );
        assertEquals( pathToProjectRepositories,
                      model.getPathToRepositories() );

        verify( pomService,
                times( 1 ) ).load( eq( pathToPom ) );
        verify( metadataService,
                times( 1 ) ).getMetadata( eq( pathToPom ) );
        verify( projectService,
                times( 1 ) ).resolveProject( eq( pathToPom ) );

        verify( kModuleService,
                times( 1 ) ).load( eq( pathToKieModule ) );
        verify( metadataService,
                times( 1 ) ).getMetadata( eq( pathToKieModule ) );

        verify( importsService,
                times( 1 ) ).load( eq( pathToProjectImports ) );
        verify( metadataService,
                times( 1 ) ).getMetadata( eq( pathToProjectImports ) );

        verify( repositoriesService,
                times( 1 ) ).load( eq( pathToProjectRepositories ) );
    }

    @Test
    public void testSaveNonClashingGAVChangeToGAV() {
        when( pathToPom.toURI() ).thenReturn( "default://p0/pom.xml" );

        final ProjectScreenModel model = new ProjectScreenModel();
        model.setPOM( new POM( new GAV( "groupId",
                                        "artifactId",
                                        "2.0.0" ) ) );
        model.setPOMMetaData( pomMetaData );
        model.setPathToPOM( pathToPom );

        model.setKModule( kmodule );
        model.setKModuleMetaData( kmoduleMetaData );
        model.setPathToKModule( pathToKieModule );

        model.setProjectImports( projectImports );
        model.setProjectImportsMetaData( projectImportsMetaData );
        model.setPathToImports( pathToProjectImports );

        model.setRepositories( projectRepositories );
        model.setPathToRepositories( pathToProjectRepositories );

        final String comment = "comment";

        service.save( pathToPom,
                      model,
                      comment );

        verify( repositoryResolver,
                times( 1 ) ).getRepositoriesResolvingArtifact( eq( model.getPOM().getGav() ),
                                                               eq( project ) );

        verify( ioService,
                times( 1 ) ).startBatch( any( FileSystem.class ),
                                         any( CommentedOption.class ) );
        verify( pomService,
                times( 1 ) ).save( eq( pathToPom ),
                                   eq( model.getPOM() ),
                                   eq( pomMetaData ),
                                   eq( comment ) );
        verify( kModuleService,
                times( 1 ) ).save( eq( pathToKieModule ),
                                   eq( kmodule ),
                                   eq( kmoduleMetaData ),
                                   eq( comment ) );
        verify( importsService,
                times( 1 ) ).save( eq( pathToProjectImports ),
                                   eq( projectImports ),
                                   eq( projectImportsMetaData ),
                                   eq( comment ) );
        verify( repositoriesService,
                times( 1 ) ).save( eq( pathToProjectRepositories ),
                                   eq( projectRepositories ),
                                   eq( comment ) );
        verify( ioService,
                times( 1 ) ).endBatch();
    }

    @Test
    public void testSaveNonClashingGAVNoChangeToGAV() {
        when( pathToPom.toURI() ).thenReturn( "default://p0/pom.xml" );

        final ProjectScreenModel model = new ProjectScreenModel();
        model.setPOM( pom );
        model.setPOMMetaData( pomMetaData );
        model.setPathToPOM( pathToPom );

        model.setKModule( kmodule );
        model.setKModuleMetaData( kmoduleMetaData );
        model.setPathToKModule( pathToKieModule );

        model.setProjectImports( projectImports );
        model.setProjectImportsMetaData( projectImportsMetaData );
        model.setPathToImports( pathToProjectImports );

        model.setRepositories( projectRepositories );
        model.setPathToRepositories( pathToProjectRepositories );

        final String comment = "comment";

        service.save( pathToPom,
                      model,
                      comment );

        verify( repositoryResolver,
                never() ).getRepositoriesResolvingArtifact( eq( model.getPOM().getGav() ),
                                                            eq( project ) );

        verify( ioService,
                times( 1 ) ).startBatch( any( FileSystem.class ),
                                         any( CommentedOption.class ) );
        verify( pomService,
                times( 1 ) ).save( eq( pathToPom ),
                                   eq( model.getPOM() ),
                                   eq( pomMetaData ),
                                   eq( comment ) );
        verify( kModuleService,
                times( 1 ) ).save( eq( pathToKieModule ),
                                   eq( kmodule ),
                                   eq( kmoduleMetaData ),
                                   eq( comment ) );
        verify( importsService,
                times( 1 ) ).save( eq( pathToProjectImports ),
                                   eq( projectImports ),
                                   eq( projectImportsMetaData ),
                                   eq( comment ) );
        verify( repositoriesService,
                times( 1 ) ).save( eq( pathToProjectRepositories ),
                                   eq( projectRepositories ),
                                   eq( comment ) );
        verify( ioService,
                times( 1 ) ).endBatch();
    }

    @Test()
    public void testSaveClashingGAVChangeToGAV() {
        when( pathToPom.toURI() ).thenReturn( "default://p0/pom.xml" );

        final ProjectScreenModel model = new ProjectScreenModel();
        model.setPOM( new POM( new GAV( "groupId",
                                        "artifactId",
                                        "2.0.0" ) ) );
        model.setPOMMetaData( pomMetaData );
        model.setPathToPOM( pathToPom );

        model.setKModule( kmodule );
        model.setKModuleMetaData( kmoduleMetaData );
        model.setPathToKModule( pathToKieModule );

        model.setProjectImports( projectImports );
        model.setProjectImportsMetaData( projectImportsMetaData );
        model.setPathToImports( pathToProjectImports );

        model.setRepositories( projectRepositories );
        model.setPathToRepositories( pathToProjectRepositories );

        final MavenRepositoryMetadata repositoryMetadata = new MavenRepositoryMetadata( "id",
                                                                                        "url",
                                                                                        MavenRepositorySource.LOCAL );

        projectRepositories.getRepositories().add( new ProjectRepositories.ProjectRepository( true,
                                                                                              repositoryMetadata ) );

        when( repositoryResolver.getRepositoriesResolvingArtifact( eq( gav ),
                                                                   eq( project ),
                                                                   eq( repositoryMetadata ) ) ).thenReturn( new HashSet<MavenRepositoryMetadata>() {{
            add( repositoryMetadata );
        }} );

        final String comment = "comment";

        try {
            service.save( pathToPom,
                          model,
                          comment );

        } catch ( GAVAlreadyExistsException e ) {
            // This is expected! We catch here rather than let JUnit handle it with
            // @Test(expected = GAVAlreadyExistsException.class) so we can verify
            // that only the expected methods have been invoked.

        } catch ( Exception e ) {
            fail( e.getMessage() );
        }

        verify( repositoryResolver,
                times( 1 ) ).getRepositoriesResolvingArtifact( eq( model.getPOM().getGav() ),
                                                               eq( project ),
                                                               any( MavenRepositoryMetadata.class ) );

        verify( pomService,
                times( 1 ) ).save( eq( pathToPom ),
                                   eq( model.getPOM() ),
                                   eq( pomMetaData ),
                                   eq( comment ) );
        verify( kModuleService,
                times( 1 ) ).save( eq( pathToKieModule ),
                                   eq( kmodule ),
                                   eq( kmoduleMetaData ),
                                   eq( comment ) );
        verify( importsService,
                times( 1 ) ).save( eq( pathToProjectImports ),
                                   eq( projectImports ),
                                   eq( projectImportsMetaData ),
                                   eq( comment ) );
        verify( repositoriesService,
                times( 1 ) ).save( eq( pathToProjectRepositories ),
                                   eq( projectRepositories ),
                                   eq( comment ) );
    }

    @Test()
    public void testSaveClashingGAVNoChangeToGAV() {
        when( pathToPom.toURI() ).thenReturn( "default://p0/pom.xml" );

        final ProjectScreenModel model = new ProjectScreenModel();
        model.setPOM( pom );
        model.setPOMMetaData( pomMetaData );
        model.setPathToPOM( pathToPom );

        model.setKModule( kmodule );
        model.setKModuleMetaData( kmoduleMetaData );
        model.setPathToKModule( pathToKieModule );

        model.setProjectImports( projectImports );
        model.setProjectImportsMetaData( projectImportsMetaData );
        model.setPathToImports( pathToProjectImports );

        model.setRepositories( projectRepositories );
        model.setPathToRepositories( pathToProjectRepositories );

        final MavenRepositoryMetadata repositoryMetadata = new MavenRepositoryMetadata( "id",
                                                                                        "url",
                                                                                        MavenRepositorySource.LOCAL );

        projectRepositories.getRepositories().add( new ProjectRepositories.ProjectRepository( true,
                                                                                              repositoryMetadata ) );

        when( repositoryResolver.getRepositoriesResolvingArtifact( eq( gav ),
                                                                   eq( project ),
                                                                   eq( repositoryMetadata ) ) ).thenReturn( new HashSet<MavenRepositoryMetadata>() {{
            add( repositoryMetadata );
        }} );

        final String comment = "comment";

        try {
            service.save( pathToPom,
                          model,
                          comment );

        } catch ( GAVAlreadyExistsException e ) {
            fail( e.getMessage() );
        }

        verify( repositoryResolver,
                never() ).getRepositoriesResolvingArtifact( eq( model.getPOM().getGav() ),
                                                            eq( project ) );

        verify( pomService,
                times( 1 ) ).save( eq( pathToPom ),
                                   eq( model.getPOM() ),
                                   eq( pomMetaData ),
                                   eq( comment ) );
        verify( kModuleService,
                times( 1 ) ).save( eq( pathToKieModule ),
                                   eq( kmodule ),
                                   eq( kmoduleMetaData ),
                                   eq( comment ) );
        verify( importsService,
                times( 1 ) ).save( eq( pathToProjectImports ),
                                   eq( projectImports ),
                                   eq( projectImportsMetaData ),
                                   eq( comment ) );
        verify( repositoriesService,
                times( 1 ) ).save( eq( pathToProjectRepositories ),
                                   eq( projectRepositories ),
                                   eq( comment ) );
    }

    @Test()
    public void testSaveClashingGAVFilteredRepositoryChangeToGAV() {
        when( pathToPom.toURI() ).thenReturn( "default://p0/pom.xml" );

        final ProjectScreenModel model = new ProjectScreenModel();
        model.setPOM( new POM( new GAV( "groupId",
                                        "artifactId",
                                        "2.0.0" ) ) );
        model.setPOMMetaData( pomMetaData );
        model.setPathToPOM( pathToPom );

        model.setKModule( kmodule );
        model.setKModuleMetaData( kmoduleMetaData );
        model.setPathToKModule( pathToKieModule );

        model.setProjectImports( projectImports );
        model.setProjectImportsMetaData( projectImportsMetaData );
        model.setPathToImports( pathToProjectImports );

        model.setRepositories( projectRepositories );
        model.setPathToRepositories( pathToProjectRepositories );

        final MavenRepositoryMetadata repositoryMetadata = new MavenRepositoryMetadata( "id",
                                                                                        "url",
                                                                                        MavenRepositorySource.LOCAL );

        projectRepositories.getRepositories().add( new ProjectRepositories.ProjectRepository( false,
                                                                                              repositoryMetadata ) );

        final ArgumentCaptor<MavenRepositoryMetadata> filterCaptor = ArgumentCaptor.forClass( MavenRepositoryMetadata.class );
        when( repositoryResolver.getRepositoriesResolvingArtifact( eq( gav ),
                                                                   eq( project ),
                                                                   filterCaptor.capture() ) ).thenReturn( new HashSet<MavenRepositoryMetadata>() );

        final String comment = "comment";

        try {
            service.save( pathToPom,
                          model,
                          comment );

        } catch ( GAVAlreadyExistsException e ) {
            //This should not be thrown if we're filtering out the Repository from the check
            fail( e.getMessage() );
        }

        final List<MavenRepositoryMetadata> filter = filterCaptor.getAllValues();
        assertEquals( 0,
                      filter.size() );

        verify( repositoryResolver,
                times( 1 ) ).getRepositoriesResolvingArtifact( eq( model.getPOM().getGav() ),
                                                               eq( project ) );

        verify( ioService,
                times( 1 ) ).startBatch( any( FileSystem.class ),
                                         any( CommentedOption.class ) );
        verify( pomService,
                times( 1 ) ).save( eq( pathToPom ),
                                   eq( model.getPOM() ),
                                   eq( pomMetaData ),
                                   eq( comment ) );
        verify( kModuleService,
                times( 1 ) ).save( eq( pathToKieModule ),
                                   eq( kmodule ),
                                   eq( kmoduleMetaData ),
                                   eq( comment ) );
        verify( importsService,
                times( 1 ) ).save( eq( pathToProjectImports ),
                                   eq( projectImports ),
                                   eq( projectImportsMetaData ),
                                   eq( comment ) );
        verify( repositoriesService,
                times( 1 ) ).save( eq( pathToProjectRepositories ),
                                   eq( projectRepositories ),
                                   eq( comment ) );
        verify( ioService,
                times( 1 ) ).endBatch();
    }

    @Test()
    public void testSaveClashingGAVFilteredRepositoryNoChangeToGAV() {
        when( pathToPom.toURI() ).thenReturn( "default://p0/pom.xml" );

        final ProjectScreenModel model = new ProjectScreenModel();
        model.setPOM( pom );
        model.setPOMMetaData( pomMetaData );
        model.setPathToPOM( pathToPom );

        model.setKModule( kmodule );
        model.setKModuleMetaData( kmoduleMetaData );
        model.setPathToKModule( pathToKieModule );

        model.setProjectImports( projectImports );
        model.setProjectImportsMetaData( projectImportsMetaData );
        model.setPathToImports( pathToProjectImports );

        model.setRepositories( projectRepositories );
        model.setPathToRepositories( pathToProjectRepositories );

        final MavenRepositoryMetadata repositoryMetadata = new MavenRepositoryMetadata( "id",
                                                                                        "url",
                                                                                        MavenRepositorySource.LOCAL );

        projectRepositories.getRepositories().add( new ProjectRepositories.ProjectRepository( false,
                                                                                              repositoryMetadata ) );

        final ArgumentCaptor<MavenRepositoryMetadata> filterCaptor = ArgumentCaptor.forClass( MavenRepositoryMetadata.class );
        when( repositoryResolver.getRepositoriesResolvingArtifact( eq( gav ),
                                                                   eq( project ),
                                                                   filterCaptor.capture() ) ).thenReturn( new HashSet<MavenRepositoryMetadata>() );

        final String comment = "comment";

        try {
            service.save( pathToPom,
                          model,
                          comment );

        } catch ( GAVAlreadyExistsException e ) {
            //This should not be thrown if we're filtering out the Repository from the check
            fail( e.getMessage() );
        }

        final List<MavenRepositoryMetadata> filter = filterCaptor.getAllValues();
        assertEquals( 0,
                      filter.size() );

        verify( repositoryResolver,
                never() ).getRepositoriesResolvingArtifact( eq( model.getPOM().getGav() ),
                                                            eq( project ) );

        verify( ioService,
                times( 1 ) ).startBatch( any( FileSystem.class ),
                                         any( CommentedOption.class ) );
        verify( pomService,
                times( 1 ) ).save( eq( pathToPom ),
                                   eq( model.getPOM() ),
                                   eq( pomMetaData ),
                                   eq( comment ) );
        verify( kModuleService,
                times( 1 ) ).save( eq( pathToKieModule ),
                                   eq( kmodule ),
                                   eq( kmoduleMetaData ),
                                   eq( comment ) );
        verify( importsService,
                times( 1 ) ).save( eq( pathToProjectImports ),
                                   eq( projectImports ),
                                   eq( projectImportsMetaData ),
                                   eq( comment ) );
        verify( repositoriesService,
                times( 1 ) ).save( eq( pathToProjectRepositories ),
                                   eq( projectRepositories ),
                                   eq( comment ) );
        verify( ioService,
                times( 1 ) ).endBatch();
    }

    @Test()
    public void testSaveClashingGAVForced() {
        when( pathToPom.toURI() ).thenReturn( "default://p0/pom.xml" );

        final ProjectScreenModel model = new ProjectScreenModel();
        model.setPOM( pom );
        model.setPOMMetaData( pomMetaData );
        model.setPathToPOM( pathToPom );

        model.setKModule( kmodule );
        model.setKModuleMetaData( kmoduleMetaData );
        model.setPathToKModule( pathToKieModule );

        model.setProjectImports( projectImports );
        model.setProjectImportsMetaData( projectImportsMetaData );
        model.setPathToImports( pathToProjectImports );

        model.setRepositories( projectRepositories );
        model.setPathToRepositories( pathToProjectRepositories );

        final MavenRepositoryMetadata repositoryMetadata = new MavenRepositoryMetadata( "id",
                                                                                        "url",
                                                                                        MavenRepositorySource.LOCAL );

        projectRepositories.getRepositories().add( new ProjectRepositories.ProjectRepository( true,
                                                                                              repositoryMetadata ) );

        when( repositoryResolver.getRepositoriesResolvingArtifact( eq( gav ),
                                                                   eq( project ),
                                                                   eq( repositoryMetadata ) ) ).thenReturn( new HashSet<MavenRepositoryMetadata>() {{
            add( repositoryMetadata );
        }} );

        final String comment = "comment";

        try {
            service.save( pathToPom,
                          model,
                          comment,
                          DeploymentMode.FORCED );

        } catch ( GAVAlreadyExistsException e ) {
            fail( "Unexpected exception thrown: " + e.getMessage() );
        }

        verify( pomService,
                times( 1 ) ).save( eq( pathToPom ),
                                   eq( model.getPOM() ),
                                   eq( pomMetaData ),
                                   eq( comment ) );
        verify( kModuleService,
                times( 1 ) ).save( eq( pathToKieModule ),
                                   eq( kmodule ),
                                   eq( kmoduleMetaData ),
                                   eq( comment ) );
        verify( importsService,
                times( 1 ) ).save( eq( pathToProjectImports ),
                                   eq( projectImports ),
                                   eq( projectImportsMetaData ),
                                   eq( comment ) );
        verify( repositoriesService,
                times( 1 ) ).save( eq( pathToProjectRepositories ),
                                   eq( projectRepositories ),
                                   eq( comment ) );
    }

}