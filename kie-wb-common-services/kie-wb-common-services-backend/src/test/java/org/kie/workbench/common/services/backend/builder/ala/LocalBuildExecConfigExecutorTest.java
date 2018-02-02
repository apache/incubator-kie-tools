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

import java.util.Optional;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.backend.builder.core.BuildHelper;
import org.kie.workbench.common.services.backend.builder.core.Builder;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LocalBuildExecConfigExecutorTest {

    @Mock
    private BuildHelper buildHelper;

    @Mock
    private LocalBuildConfigInternal internalBuildConfig;

    @Mock
    private LocalBuildExecConfig buildExecConfig;

    @Mock
    private KieModule module;

    @Mock
    private BuildHelper.BuildResult buildResult;

    @Mock
    private Builder builder;

    @Mock
    private BuildResults buildResults;

    @Mock
    private IncrementalBuildResults incrementalBuildResults;

    @Mock
    private Path path;

    private LocalBuildExecConfigExecutor executor;

    @Before
    public void setUp() {
        executor = new LocalBuildExecConfigExecutor(buildHelper);
        when(buildResult.getBuilder()).thenReturn(builder);
        when(buildResult.getBuildResults()).thenReturn(buildResults);
        when(buildResult.getIncrementalBuildResults()).thenReturn(incrementalBuildResults);
    }

    @Test
    public void testApplyForModuleFullBuild() {
        when(internalBuildConfig.getBuildType()).thenReturn(LocalBuildConfig.BuildType.FULL_BUILD);
        when(internalBuildConfig.getModule()).thenReturn(module);
        when(buildHelper.build(module)).thenReturn(buildResult);

        Optional<LocalBinaryConfig> result = executor.apply(internalBuildConfig,
                                                            buildExecConfig);

        assertTrue(result.isPresent());
        assertEquals(builder,
                     result.get().getBuilder());
        assertEquals(buildResults,
                     result.get().getBuildResults());
        verify(buildHelper,
               times(1)).build(module);
    }

    @Test
    public void testApplyForIncrementalResourceAddBuild() {
        when(internalBuildConfig.getBuildType()).thenReturn(LocalBuildConfig.BuildType.INCREMENTAL_ADD_RESOURCE);
        when(internalBuildConfig.getResource()).thenReturn(path);
        when(buildHelper.addPackageResource(path)).thenReturn(incrementalBuildResults);

        Optional<LocalBinaryConfig> result = executor.apply(internalBuildConfig,
                                                            buildExecConfig);

        assertTrue(result.isPresent());
        assertEquals(incrementalBuildResults,
                     result.get().getIncrementalBuildResults());
        verify(buildHelper,
               times(1)).addPackageResource(path);
    }

    @Test
    public void testApplyForIncrementalResourceUpdateBuild() {
        when(internalBuildConfig.getBuildType()).thenReturn(LocalBuildConfig.BuildType.INCREMENTAL_UPDATE_RESOURCE);
        when(internalBuildConfig.getResource()).thenReturn(path);
        when(buildHelper.updatePackageResource(path)).thenReturn(incrementalBuildResults);

        Optional<LocalBinaryConfig> result = executor.apply(internalBuildConfig,
                                                            buildExecConfig);

        assertTrue(result.isPresent());
        assertEquals(incrementalBuildResults,
                     result.get().getIncrementalBuildResults());
        verify(buildHelper,
               times(1)).updatePackageResource(path);
    }

    @Test
    public void testApplyForIncrementalResourceDeleteBuild() {
        when(internalBuildConfig.getBuildType()).thenReturn(LocalBuildConfig.BuildType.INCREMENTAL_DELETE_RESOURCE);
        when(internalBuildConfig.getResource()).thenReturn(path);
        when(buildHelper.deletePackageResource(path)).thenReturn(incrementalBuildResults);

        Optional<LocalBinaryConfig> result = executor.apply(internalBuildConfig,
                                                            buildExecConfig);

        assertTrue(result.isPresent());
        assertEquals(incrementalBuildResults,
                     result.get().getIncrementalBuildResults());
        verify(buildHelper,
               times(1)).deletePackageResource(path);
    }

    @Test
    public void testApplyForIncrementalBatchChangesBuild() {
        when(internalBuildConfig.getBuildType()).thenReturn(LocalBuildConfig.BuildType.INCREMENTAL_BATCH_CHANGES);
        when(internalBuildConfig.getModule()).thenReturn(module);
        when(buildHelper.applyBatchResourceChanges(eq(module),
                                                   anyMap())).thenReturn(incrementalBuildResults);

        Optional<LocalBinaryConfig> result = executor.apply(internalBuildConfig,
                                                            buildExecConfig);

        assertTrue(result.isPresent());
        assertEquals(incrementalBuildResults,
                     result.get().getIncrementalBuildResults());
        verify(buildHelper,
               times(1)).applyBatchResourceChanges(eq(module),
                                                   anyMap());
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
        when(internalBuildConfig.getBuildType()).thenReturn(LocalBuildConfig.BuildType.FULL_BUILD_AND_DEPLOY);
        when(internalBuildConfig.getModule()).thenReturn(module);
        when(internalBuildConfig.getDeploymentType()).thenReturn(deploymentType);
        when(internalBuildConfig.isSuppressHandlers()).thenReturn(suppressHandlers);
        when(buildHelper.buildAndDeploy(module,
                                        suppressHandlers,
                                        DeploymentMode.valueOf(deploymentType.name()))).thenReturn(buildResults);

        Optional<LocalBinaryConfig> result = executor.apply(internalBuildConfig,
                                                            buildExecConfig);

        assertTrue(result.isPresent());
        assertEquals(buildResults,
                     result.get().getBuildResults());
        verify(buildHelper,
               times(1)).buildAndDeploy(module,
                                        suppressHandlers,
                                        DeploymentMode.valueOf(deploymentType.name()));
    }
}
