/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.backend.builder.core;

import java.util.HashSet;
import java.util.Set;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.ProjectRepositories;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.project.service.ProjectRepositoriesService;
import org.guvnor.common.services.project.service.ProjectRepositoryResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DeploymentVerifierTest {

    @Mock
    private ProjectRepositoryResolver repositoryResolver;

    @Mock
    private ProjectRepositoriesService projectRepositoriesService;

    private DeploymentVerifier deploymentVerifier;

    @Mock
    private KieProject project;

    @Mock
    private POM pom;

    @Mock
    private GAV gav;

    @Mock
    private Path path;

    @Mock
    private ProjectRepositories projectRepositories;

    private Set<MavenRepositoryMetadata> repositories;

    @Mock
    private MavenRepositoryMetadata repositoryMetadata1;

    @Mock
    private MavenRepositoryMetadata repositoryMetadata2;

    private Exception exception;

    @Before
    public void setUp() {
        deploymentVerifier = new DeploymentVerifier(repositoryResolver,
                                                    projectRepositoriesService);
        when(project.getPom()).thenReturn(pom);
        when(pom.getGav()).thenReturn(gav);
    }

    /**
     * Test the case when a VALIDATED deployment is about to be performed, the project is already deployed, and the
     * project version is a snapshot.
     */
    @Test
    public void testVerifyAlreadyDeployedValidatedSNAPSHOT() {
        prepareProjectIsDeployed(true);
        when(gav.isSnapshot()).thenReturn(true);
        executeNonErrorCase(DeploymentMode.VALIDATED);
    }

    /**
     * Test the case when a VALIDATED deployment is about to be performed, the project is not deployed, and the
     * project version is a snapshot.
     */
    @Test
    public void testVerifyNonDeployedValidatedSNAPSHOT() {
        prepareProjectIsDeployed(false);
        when(gav.isSnapshot()).thenReturn(true);
        executeNonErrorCase(DeploymentMode.VALIDATED);
    }

    /**
     * Test the case when a VALIDATED deployment is about to be performed, the project is deployed, and the project
     * version is a non snapshot.
     */
    @Test
    public void testVerifyAlreadyDeployedValidatedNonSNAPSHOT() {
        prepareProjectIsDeployed(true);
        when(gav.isSnapshot()).thenReturn(false);
        try {
            deploymentVerifier.verifyWithException(project,
                                                   DeploymentMode.VALIDATED);
        } catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
        assertTrue(exception instanceof GAVAlreadyExistsException);
        assertEquals(gav,
                     ((GAVAlreadyExistsException) exception).getGAV());
        assertEquals(repositories,
                     ((GAVAlreadyExistsException) exception).getRepositories());
    }

    /**
     * Test the case when a VALIDATED deployment is about to be performed, the project is not deployed, and the project
     * version is a non snapshot.
     */
    @Test
    public void testVerifyNonDeployedValidatedNonSNAPSHOT() {
        prepareProjectIsDeployed(false);
        when(gav.isSnapshot()).thenReturn(true);
        executeNonErrorCase(DeploymentMode.VALIDATED);
    }

    private void executeNonErrorCase(DeploymentMode deploymentMode) {
        try {
            deploymentVerifier.verifyWithException(project,
                                                   deploymentMode);
        } catch (Exception e) {
            exception = e;
        }
        assertNull(exception);
    }

    private void prepareProjectIsDeployed(boolean isDeployed) {
        repositories = new HashSet<>();
        if (isDeployed) {
            repositories.add(repositoryMetadata1);
            repositories.add(repositoryMetadata2);
        }
        when(project.getRepositoriesPath()).thenReturn(path);
        when(projectRepositoriesService.load(path)).thenReturn(projectRepositories);
        when(repositoryResolver.getRepositoriesResolvingArtifact(eq(gav),
                                                                 eq(project),
                                                                 any(MavenRepositoryMetadata[].class))).thenReturn(repositories);
    }
}