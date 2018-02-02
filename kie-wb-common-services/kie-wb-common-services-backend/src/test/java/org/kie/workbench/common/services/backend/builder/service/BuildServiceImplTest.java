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

package org.kie.workbench.common.services.backend.builder.service;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.backend.builder.ala.LocalBinaryConfig;
import org.kie.workbench.common.services.backend.builder.ala.LocalBuildConfig;
import org.kie.workbench.common.services.backend.builder.core.Builder;
import org.kie.workbench.common.services.backend.builder.core.LRUBuilderCache;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.ResourceChange;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BuildServiceImplTest {

    @Mock
    private LRUBuilderCache cache;

    @Mock
    private KieModuleService moduleService;

    @Mock
    private BuildServiceHelper buildServiceHelper;

    private BuildServiceImpl buildService;

    @Mock
    private KieModule module;

    @Mock
    private Path path;

    @Mock
    private Builder builder;

    @Mock
    private BuildResults buildResults;

    @Mock
    private IncrementalBuildResults incrementalBuildResults;

    @Mock
    private Map<Path, Collection<ResourceChange>> resourceChanges;

    @Mock
    private LocalBinaryConfig localBinaryConfig;

    @Before
    public void setUp() {
        buildService = new BuildServiceImpl(moduleService,
                                            buildServiceHelper,
                                            cache);
    }

    @Test
    public void testBuild() {
        when(buildServiceHelper.localBuild(module)).thenReturn(buildResults);
        BuildResults result = buildService.build(module);
        assertEquals(buildResults,
                     result);
    }

    @Test
    public void testBuildWithConsumer() {
        // emulate the buildServiceHelper response
        when(localBinaryConfig.getBuilder()).thenReturn(builder);
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Consumer consumer = (Consumer) invocation.getArguments()[1];
                consumer.accept(localBinaryConfig);
                return null;
            }
        }).when(buildServiceHelper).localBuild(eq(module),
                                               any(Consumer.class));

        buildService.build(module,
                           new Consumer<Builder>() {
                               @Override
                               public void accept(Builder result) {
                                   // the resulting builder must the returned by the buildServiceHelper
                                   assertEquals(builder,
                                                result);
                               }
                           });
        verify(buildServiceHelper,
               times(1)).localBuild(eq(module),
                                    any(Consumer.class));
    }

    @Test
    public void testIsBuiltTrue() {
        when(cache.assertBuilder(module)).thenReturn(builder);
        when(builder.isBuilt()).thenReturn(true);
        assertTrue(buildService.isBuilt(module));
    }

    @Test
    public void testIsBuiltFalse() {
        when(cache.assertBuilder(module)).thenReturn(builder);
        when(builder.isBuilt()).thenReturn(false);
        assertFalse(buildService.isBuilt(module));
    }

    @Test
    public void testBuildAndDeploy() {
        prepareBuildAndDeploy(module,
                              DeploymentMode.VALIDATED,
                              false);
        BuildResults result = buildService.buildAndDeploy(module);
        assertEquals(buildResults,
                     result);
        verifyBuildAndDeploy(module,
                             DeploymentMode.VALIDATED,
                             false);
    }

    @Test
    public void testBuildAndDeployWithDeploymentMode() {
        prepareBuildAndDeploy(module,
                              DeploymentMode.VALIDATED,
                              false);
        BuildResults result = buildService.buildAndDeploy(module,
                                                          DeploymentMode.VALIDATED);
        assertEquals(buildResults,
                     result);
        verifyBuildAndDeploy(module,
                             DeploymentMode.VALIDATED,
                             false);
    }

    @Test
    public void testBuildAndDeployWithSuppressHandlers() {
        prepareBuildAndDeploy(module,
                              DeploymentMode.VALIDATED,
                              false);
        BuildResults result = buildService.buildAndDeploy(module,
                                                          false);
        assertEquals(buildResults,
                     result);
        verifyBuildAndDeploy(module,
                             DeploymentMode.VALIDATED,
                             false);
    }

    @Test
    public void testBuildAndDeployWithDeploymentModeAndSuppressHandlers() {
        prepareBuildAndDeploy(module,
                              DeploymentMode.VALIDATED,
                              false);
        BuildResults result = buildService.buildAndDeploy(module,
                                                          false,
                                                          DeploymentMode.VALIDATED);
        assertEquals(buildResults,
                     result);
        verifyBuildAndDeploy(module,
                             DeploymentMode.VALIDATED,
                             false);
    }

    private void prepareBuildAndDeploy(KieModule module,
                                       DeploymentMode deploymentMode,
                                       boolean suppressHandlers) {
        when(buildServiceHelper.localBuildAndDeploy(module,
                                                    deploymentMode,
                                                    suppressHandlers)).thenReturn(buildResults);
    }

    private void verifyBuildAndDeploy(KieModule module,
                                      DeploymentMode deploymentMode,
                                      boolean suppressHandlers) {
        verify(buildServiceHelper,
               times(1)).localBuildAndDeploy(module,
                                             deploymentMode,
                                             suppressHandlers);
    }

    @Test
    public void testAddPackageResource() {
        prepareIncrementalBuild(path,
                                LocalBuildConfig.BuildType.INCREMENTAL_ADD_RESOURCE);
        IncrementalBuildResults result = buildService.addPackageResource(path);
        assertEquals(incrementalBuildResults,
                     result);
        verifyIncrementalBuild(path,
                               LocalBuildConfig.BuildType.INCREMENTAL_ADD_RESOURCE);
    }

    @Test
    public void testDeletePackageResource() {
        prepareIncrementalBuild(path,
                                LocalBuildConfig.BuildType.INCREMENTAL_DELETE_RESOURCE);
        IncrementalBuildResults result = buildService.deletePackageResource(path);
        assertEquals(incrementalBuildResults,
                     result);
        verifyIncrementalBuild(path,
                               LocalBuildConfig.BuildType.INCREMENTAL_DELETE_RESOURCE);
    }

    @Test
    public void testUpdatePackageResource() {
        prepareIncrementalBuild(path,
                                LocalBuildConfig.BuildType.INCREMENTAL_UPDATE_RESOURCE);
        IncrementalBuildResults result = buildService.updatePackageResource(path);
        assertEquals(incrementalBuildResults,
                     result);
        verifyIncrementalBuild(path,
                               LocalBuildConfig.BuildType.INCREMENTAL_UPDATE_RESOURCE);
    }

    private void prepareIncrementalBuild(Path path,
                                         LocalBuildConfig.BuildType buildType) {
        when(moduleService.resolveModule(path)).thenReturn(module);
        when(buildServiceHelper.localBuild(module,
                                           buildType,
                                           path)).thenReturn(incrementalBuildResults);
    }

    private void verifyIncrementalBuild(Path path,
                                        LocalBuildConfig.BuildType buildType) {
        verify(buildServiceHelper,
               times(1)).localBuild(module,
                                    buildType,
                                    path);
    }

    @Test
    public void testApplyBatchResourceChanges() {
        when(buildServiceHelper.localBuild(module,
                                           resourceChanges)).thenReturn(incrementalBuildResults);
        IncrementalBuildResults result = buildService.applyBatchResourceChanges(module,
                                                                                resourceChanges);
        assertEquals(incrementalBuildResults,
                     result);
        verify(buildServiceHelper,
               times(1)).localBuild(module,
                                    resourceChanges);
    }
}
