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

import java.util.Collections;
import javax.enterprise.inject.Instance;

import org.guvnor.common.services.project.builder.service.BuildService;
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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BuildServiceImplTest {

    @Mock
    private POMService pomService;

    @Mock
    private ExtendedM2RepoService m2RepoService;

    @Mock
    private KieProjectService projectService;

    @Mock
    private ProjectRepositoryResolver repositoryResolver;

    @Mock
    private ProjectRepositoriesService projectRepositoriesService;

    @Mock
    private LRUBuilderCache cache;

    @Mock
    private Instance<PostBuildHandler> handlers;

    private BuildService service;

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

    @Test
    public void testBuildAndDeployNonSnapshot() {
        final KieProject project = mock( KieProject.class );
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
        final KieProject project = mock( KieProject.class );
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
        final KieProject project = mock( KieProject.class );
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
        final KieProject project = mock( KieProject.class );
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

}
