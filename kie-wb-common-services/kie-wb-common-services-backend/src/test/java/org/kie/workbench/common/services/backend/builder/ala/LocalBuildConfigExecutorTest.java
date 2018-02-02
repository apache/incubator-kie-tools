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

package org.kie.workbench.common.services.backend.builder.ala;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.guvnor.ala.config.BuildConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.workbench.events.ResourceChange;
import org.uberfire.workbench.events.ResourceChangeType;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LocalBuildConfigExecutorTest
        implements BuildPipelineTestConstants {

    @Mock
    private KieModule project;

    @Mock
    private LocalModule localModule;

    @Mock
    private LocalBuildConfig buildConfig;

    private LocalBuildConfigExecutor executor;

    private Path resource1VFSPath = PathFactory.newPath(RESOURCE_NAME_1,
                                                        RESOURCE_URI_1);

    private Path resource2VFSPath = PathFactory.newPath(RESOURCE_NAME_2,
                                                        RESOURCE_URI_2);

    private Path resource3VFSPath = PathFactory.newPath(RESOURCE_NAME_3,
                                                        RESOURCE_URI_3);

    @Before
    public void setup() {
        executor = new LocalBuildConfigExecutor();
    }

    @Test
    public void testApplyForModuleFullBuild() {
        when(localModule.getModule()).thenReturn(project);
        when(buildConfig.getBuildType()).thenReturn(LocalBuildConfig.BuildType.FULL_BUILD.name());

        Optional<BuildConfig> result = executor.apply(localModule,
                                                      buildConfig);

        assertTrue(result.isPresent());
        assertEquals(LocalBuildConfig.BuildType.FULL_BUILD,
                     ((LocalBuildConfigInternal) result.get()).getBuildType());
        assertEquals(project,
                     ((LocalBuildConfigInternal) result.get()).getModule());
    }

    @Test
    public void testApplyForIncrementalResourceAddBuild() {
        testApplyForIncrementalResourceBuild(LocalBuildConfig.BuildType.INCREMENTAL_ADD_RESOURCE,
                                             RESOURCE_URI_1);
    }

    @Test
    public void testApplyForIncrementalResourceUpdateBuild() {
        testApplyForIncrementalResourceBuild(LocalBuildConfig.BuildType.INCREMENTAL_UPDATE_RESOURCE,
                                             RESOURCE_URI_1);
    }

    @Test
    public void testApplyForIncrementalResourceDeleteBuild() {
        testApplyForIncrementalResourceBuild(LocalBuildConfig.BuildType.INCREMENTAL_DELETE_RESOURCE,
                                             RESOURCE_URI_1);
    }

    private void testApplyForIncrementalResourceBuild(LocalBuildConfig.BuildType buildType,
                                                      String resourceURI) {
        when(localModule.getModule()).thenReturn(project);
        when(buildConfig.getBuildType()).thenReturn(buildType.name());
        when(buildConfig.getResource()).thenReturn(resourceURI);

        Optional<BuildConfig> result = executor.apply(localModule,
                                                      buildConfig);

        assertTrue(result.isPresent());
        assertEquals(buildType,
                     ((LocalBuildConfigInternal) result.get()).getBuildType());
        assertEquals(project,
                     ((LocalBuildConfigInternal) result.get()).getModule());
        assertEquals(RESOURCE_URI_1,
                     ((LocalBuildConfigInternal) result.get()).getResource().toURI());
    }

    @Test
    public void testApplyForIncrementalBatchChangesBuild() {
        when(localModule.getModule()).thenReturn(project);
        when(buildConfig.getBuildType()).thenReturn(LocalBuildConfig.BuildType.INCREMENTAL_BATCH_CHANGES.name());

        Map<String, String> resourceChanges = new HashMap<>();
        resourceChanges.put(LocalBuildConfig.RESOURCE_CHANGE + RESOURCE_URI_1,
                            "ADD");
        resourceChanges.put(LocalBuildConfig.RESOURCE_CHANGE + RESOURCE_URI_2,
                            "ADD,UPDATE");
        resourceChanges.put(LocalBuildConfig.RESOURCE_CHANGE + RESOURCE_URI_3,
                            "ADD,UPDATE,DELETE");
        when(buildConfig.getResourceChanges()).thenReturn(resourceChanges);

        Optional<BuildConfig> result = executor.apply(localModule,
                                                      buildConfig);

        assertTrue(result.isPresent());
        assertEquals(LocalBuildConfig.BuildType.INCREMENTAL_BATCH_CHANGES,
                     ((LocalBuildConfigInternal) result.get()).getBuildType());
        assertHasAllChanges(((LocalBuildConfigInternal) result.get()).getResourceChanges().get(resource1VFSPath),
                            ResourceChangeType.ADD);
        assertHasAllChanges(((LocalBuildConfigInternal) result.get()).getResourceChanges().get(resource2VFSPath),
                            ResourceChangeType.ADD,
                            ResourceChangeType.UPDATE);
        assertHasAllChanges(((LocalBuildConfigInternal) result.get()).getResourceChanges().get(resource3VFSPath),
                            ResourceChangeType.ADD,
                            ResourceChangeType.UPDATE,
                            ResourceChangeType.DELETE);
    }

    private boolean assertHasAllChanges(Collection<ResourceChange> changes,
                                        ResourceChangeType... changeTypes) {
        for (ResourceChangeType changeType : changeTypes) {
            if (!changes.stream()
                    .filter(resourceChange -> resourceChange.getType().equals(changeType))
                    .findFirst().isPresent()) {
                return false;
            }
        }
        return true;
    }

    @Test
    public void testApplyForModuleFullBuildAndDeployForcedNotSuppressHandlers() {
        testApplyForModuleFullBuildAndDeploy(LocalBuildConfig.DeploymentType.FORCED,
                                             false);
    }

    @Test
    public void testApplyForModuleFullBuildAndDeployForcedSuppressHandlers() {
        testApplyForModuleFullBuildAndDeploy(LocalBuildConfig.DeploymentType.FORCED,
                                             true);
    }

    @Test
    public void testApplyForModuleFullBuildAndDeployValidatedNotSuppressHandlers() {
        testApplyForModuleFullBuildAndDeploy(LocalBuildConfig.DeploymentType.VALIDATED,
                                             false);
    }

    @Test
    public void testApplyForModuleFullBuildAndDeployValidatedSuppressHandlers() {
        testApplyForModuleFullBuildAndDeploy(LocalBuildConfig.DeploymentType.VALIDATED,
                                             true);
    }

    private void testApplyForModuleFullBuildAndDeploy(LocalBuildConfig.DeploymentType deploymentType,
                                                      boolean suppressHandlers) {
        when(localModule.getModule()).thenReturn(project);
        when(buildConfig.getBuildType()).thenReturn(LocalBuildConfig.BuildType.FULL_BUILD_AND_DEPLOY.name());
        when(buildConfig.getDeploymentType()).thenReturn(deploymentType.name());
        when(buildConfig.getSuppressHandlers()).thenReturn(Boolean.toString(suppressHandlers));

        Optional<BuildConfig> result = executor.apply(localModule,
                                                      buildConfig);

        assertTrue(result.isPresent());
        assertEquals(project,
                     ((LocalBuildConfigInternal) result.get()).getModule());
        assertEquals(LocalBuildConfig.BuildType.FULL_BUILD_AND_DEPLOY,
                     ((LocalBuildConfigInternal) result.get()).getBuildType());
        assertEquals(deploymentType,
                     ((LocalBuildConfigInternal) result.get()).getDeploymentType());
        assertEquals(suppressHandlers,
                     ((LocalBuildConfigInternal) result.get()).isSuppressHandlers());
    }
}