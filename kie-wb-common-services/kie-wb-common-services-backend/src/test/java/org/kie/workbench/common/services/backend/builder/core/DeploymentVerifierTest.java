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
import org.guvnor.common.services.project.model.ModuleRepositories;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.project.service.ModuleRepositoriesService;
import org.guvnor.common.services.project.service.ModuleRepositoryResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DeploymentVerifierTest {

    @Mock
    private ModuleRepositoryResolver repositoryResolver;

    @Mock
    private ModuleRepositoriesService moduleRepositoriesService;

    private DeploymentVerifier deploymentVerifier;

    @Mock
    private KieModule module;

    @Mock
    private POM pom;

    @Mock
    private GAV gav;

    @Mock
    private Path path;

    @Mock
    private ModuleRepositories moduleRepositories;

    private Set<MavenRepositoryMetadata> repositories;

    @Mock
    private MavenRepositoryMetadata repositoryMetadata1;

    @Mock
    private MavenRepositoryMetadata repositoryMetadata2;

    private Exception exception;

    @Before
    public void setUp() {
        deploymentVerifier = new DeploymentVerifier(repositoryResolver,
                                                    moduleRepositoriesService);
        when(module.getPom()).thenReturn(pom);
        when(pom.getGav()).thenReturn(gav);
    }

    /**
     * Test the case when a VALIDATED deployment is about to be performed, the module is already deployed, and the
     * module version is a snapshot.
     */
    @Test
    public void testVerifyAlreadyDeployedValidatedSNAPSHOT() {
        prepareProjectIsDeployed(true);
        when(gav.isSnapshot()).thenReturn(true);
        executeNonErrorCase(DeploymentMode.VALIDATED);
    }

    /**
     * Test the case when a VALIDATED deployment is about to be performed, the module is not deployed, and the
     * module version is a snapshot.
     */
    @Test
    public void testVerifyNonDeployedValidatedSNAPSHOT() {
        prepareProjectIsDeployed(false);
        when(gav.isSnapshot()).thenReturn(true);
        executeNonErrorCase(DeploymentMode.VALIDATED);
    }

    /**
     * Test the case when a VALIDATED deployment is about to be performed, the module is deployed, and the module
     * version is a non snapshot.
     */
    @Test
    public void testVerifyAlreadyDeployedValidatedNonSNAPSHOT() {
        prepareProjectIsDeployed(true);
        when(gav.isSnapshot()).thenReturn(false);
        try {
            deploymentVerifier.verifyWithException(module,
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
     * Test the case when a VALIDATED deployment is about to be performed, the module is not deployed, and the module
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
            deploymentVerifier.verifyWithException(module,
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
        when(module.getRepositoriesPath()).thenReturn(path);
        when(moduleRepositoriesService.load(path)).thenReturn(moduleRepositories);
        when(repositoryResolver.getRepositoriesResolvingArtifact(eq(gav),
                                                                 eq(module),
                                                                 any(MavenRepositoryMetadata[].class))).thenReturn(repositories);
    }
}