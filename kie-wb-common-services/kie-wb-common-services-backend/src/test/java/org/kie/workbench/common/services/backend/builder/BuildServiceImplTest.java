/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

import java.io.ByteArrayInputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import javax.enterprise.inject.Instance;

import org.guvnor.common.services.project.builder.service.PostBuildHandler;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.ProjectRepositories;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectRepositoriesService;
import org.guvnor.common.services.project.service.ProjectRepositoryResolver;
import org.guvnor.m2repo.backend.server.ExtendedM2RepoService;
import org.guvnor.test.TestFileSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.builder.KieFileSystem;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BuildServiceImplTest {

    @Mock
    private POMService pomService;

    @Mock
    private ExtendedM2RepoService m2RepoService;

    @Mock
    private ProjectRepositoryResolver repositoryResolver;

    @Mock
    private ProjectRepositoriesService projectRepositoriesService;

    @Mock
    private Instance<PostBuildHandler> handlers;

    private LRUBuilderCache cache;

    private TestFileSystem testFileSystem;

    private KieProjectService projectService;

    private BuildServiceImpl service;

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
        testFileSystem = new TestFileSystem();
        projectService = testFileSystem.getReference( KieProjectService.class );
        cache = testFileSystem.getReference( LRUBuilderCache.class );
        service = spy( new BuildServiceImpl( pomService,
                                             m2RepoService,
                                             projectService,
                                             repositoryResolver,
                                             projectRepositoriesService,
                                             cache,
                                             handlers ) );

        final ProjectRepositories projectRepositories = new ProjectRepositories();
        when( projectRepositoriesService.load( any( Path.class ) ) ).thenReturn( projectRepositories );
    }

    @After
    public void tearDown() throws Exception {
        testFileSystem.tearDown();
    }

    @Test
    public void testBuildAndDeployNonSnapshot() {
        final KieProject project = projectMock();
        final POM pom = mock( POM.class );
        final GAV gav = new GAV( "groupID",
                                 "artifactID",
                                 "1.0.0" );
        when( project.getPom() ).thenReturn( pom );
        when( pom.getGav() ).thenReturn( gav );
        when( repositoryResolver.getRepositoriesResolvingArtifact( eq( gav ) ) ).thenReturn( Collections.<MavenRepositoryMetadata>emptySet() );

        service.buildAndDeploy( project );

        verify( service,
                times( 1 ) ).buildAndDeploy( eq( project ),
                                             eq( DeploymentMode.VALIDATED ) );
        verify( projectRepositoriesService,
                times( 1 ) ).load( any( Path.class ) );
        verify( repositoryResolver,
                times( 1 ) ).getRepositoriesResolvingArtifact( eq( gav ) );
    }

    @Test
    public void testBuildAndDeploySnapshot() {
        final KieProject project = projectMock();
        final POM pom = mock( POM.class );
        final GAV gav = new GAV( "groupID",
                                 "artifactID",
                                 "1.0.0-SNAPSHOT" );
        when( project.getPom() ).thenReturn( pom );
        when( pom.getGav() ).thenReturn( gav );
        when( repositoryResolver.getRepositoriesResolvingArtifact( eq( gav ) ) ).thenReturn( Collections.<MavenRepositoryMetadata>emptySet() );

        service.buildAndDeploy( project );

        verify( service,
                times( 1 ) ).buildAndDeploy( eq( project ),
                                             eq( DeploymentMode.VALIDATED ) );
        verify( projectRepositoriesService,
                never() ).load( any( Path.class ) );
        verify( repositoryResolver,
                never() ).getRepositoriesResolvingArtifact( eq( gav ) );
    }

    @Test
    public void testBuildAndDeploySuppressHandlersNonSnapshot() {
        final KieProject project = projectMock();
        final POM pom = mock( POM.class );
        final GAV gav = new GAV( "groupID",
                                 "artifactID",
                                 "1.0.0" );
        when( project.getPom() ).thenReturn( pom );
        when( pom.getGav() ).thenReturn( gav );
        when( repositoryResolver.getRepositoriesResolvingArtifact( eq( gav ) ) ).thenReturn( Collections.<MavenRepositoryMetadata>emptySet() );

        service.buildAndDeploy( project,
                                true );

        verify( service,
                times( 1 ) ).buildAndDeploy( eq( project ),
                                             eq( true ),
                                             eq( DeploymentMode.VALIDATED ) );
        verify( projectRepositoriesService,
                times( 1 ) ).load( any( Path.class ) );
        verify( repositoryResolver,
                times( 1 ) ).getRepositoriesResolvingArtifact( eq( gav ) );
    }

    @Test
    public void testBuildAndDeploySuppressHandlersSnapshot() {
        final KieProject project = projectMock();
        final POM pom = mock( POM.class );
        final GAV gav = new GAV( "groupID",
                                 "artifactID",
                                 "1.0.0-SNAPSHOT" );
        when( project.getPom() ).thenReturn( pom );
        when( pom.getGav() ).thenReturn( gav );
        when( repositoryResolver.getRepositoriesResolvingArtifact( eq( gav ) ) ).thenReturn( Collections.<MavenRepositoryMetadata>emptySet() );

        service.buildAndDeploy( project,
                                true );

        verify( service,
                times( 1 ) ).buildAndDeploy( eq( project ),
                                             eq( true ),
                                             eq( DeploymentMode.VALIDATED ) );
        verify( projectRepositoriesService,
                never() ).load( any( Path.class ) );
        verify( repositoryResolver,
                never() ).getRepositoriesResolvingArtifact( eq( gav ) );
    }

    @Test
    public void testBuildThatDoesNotUpdateTheCache() throws Exception {
        final Path path = path();

        service.build( projectService.resolveProject( path ), path, inputStream() );

        assertTrue( cachedFileSystemDoesNotChange() );
    }

    @Test
    public void testUpdatePackageResourceThatDoesNotUpdateTheCache() throws Exception {
        final Path path = path();

        service.build( projectService.resolveProject( path ) );
        service.updatePackageResource( path, inputStream() );

        assertTrue( cachedFileSystemDoesNotChange() );
    }

    private KieProject projectMock() {
        return mock( KieProject.class );
    }

    private Path path() throws URISyntaxException {
        final URL urlToValidate = this.getClass().getResource( "/GuvnorM2RepoDependencyExample1/src/main/resources/rule2.drl" );
        return Paths.convert( testFileSystem.fileSystemProvider.getPath( urlToValidate.toURI() ) );
    }

    private ByteArrayInputStream inputStream() {
        final String content = "package org.kie.workbench.common.services.builder.tests.test1\n" +
                "\n" +
                "rule R2\n" +
                "when\n" +
                "Ban()\n" +
                "then\n" +
                "end";
        return new ByteArrayInputStream( content.getBytes() );
    }

    private boolean cachedFileSystemDoesNotChange() throws URISyntaxException {
        final Builder builder = service.getCache().assertBuilder( projectService.resolveProject( path() ) );
        final KieFileSystem fileSystem = builder.getKieFileSystem();
        final String fileContent = new String( fileSystem.read( "src/main/resources/rule2.drl" ), StandardCharsets.UTF_8 );

        return fileContent.contains( "Bean" );
    }
}
