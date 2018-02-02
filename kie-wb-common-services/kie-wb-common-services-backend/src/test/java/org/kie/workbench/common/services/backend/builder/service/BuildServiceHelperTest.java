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
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.backend.builder.ala.BuildPipelineInvoker;
import org.kie.workbench.common.services.backend.builder.ala.LocalBinaryConfig;
import org.kie.workbench.common.services.backend.builder.ala.LocalBuildConfig;
import org.kie.workbench.common.services.backend.builder.core.DeploymentVerifier;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.ResourceChange;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BuildServiceHelperTest {

    @Mock
    private BuildPipelineInvoker pipelineInvoker;

    @Mock
    private DeploymentVerifier deploymentVerifier;

    private BuildServiceHelper serviceHelper;

    @Mock
    private Module module;

    @Mock
    private LocalBinaryConfig localBinaryConfig;

    @Mock
    private BuildResults buildResults;

    @Mock
    private IncrementalBuildResults incrementalBuildResults;

    @Mock
    private Map<Path, Collection<ResourceChange>> resourceChanges;

    @Mock
    private Path resource;

    private BuildPipelineInvoker.LocalBuildRequest expectedRequest;

    @Before
    public void setUp() {
        serviceHelper = new BuildServiceHelper(pipelineInvoker,
                                               deploymentVerifier);
    }

    @Test
    public void testLocalBuild() {
        prepareLocalFullBuild();
        when(localBinaryConfig.getBuildResults()).thenReturn(buildResults);
        BuildResults result = serviceHelper.localBuild(module);
        assertEquals(buildResults,
                     result);
        verify(pipelineInvoker,
               times(1)).invokeLocalBuildPipeLine(eq(expectedRequest),
                                                  any(Consumer.class));
    }

    @Test
    public void testLocalBuildWithConsumer() {
        prepareLocalFullBuild();
        serviceHelper.localBuild(module,
                                 new Consumer<LocalBinaryConfig>() {
                                     @Override
                                     public void accept(LocalBinaryConfig result) {
                                         // the returned result should the same as the localBinaryConfig produced by the PipelineInvoker.
                                         assertEquals(localBinaryConfig,
                                                      result);
                                     }
                                 });
        verify(pipelineInvoker,
               times(1)).invokeLocalBuildPipeLine(eq(expectedRequest),
                                                  any(Consumer.class));
    }

    private void prepareLocalFullBuild() {
        expectedRequest = BuildPipelineInvoker.LocalBuildRequest.newFullBuildRequest(module);
        preparePipelineInvocation(expectedRequest);
    }

    @Test
    public void testLocalBuildAndDeployForced() {
        prepareBuildAndDeploy(module,
                              LocalBuildConfig.DeploymentType.FORCED,
                              false);
        BuildResults result = serviceHelper.localBuildAndDeploy(module,
                                                                DeploymentMode.FORCED,
                                                                false);
        verifyBuildAndDeploy(result);
    }

    @Test
    public void testLocalBuildAndDeployValidated() {
        prepareBuildAndDeploy(module,
                              LocalBuildConfig.DeploymentType.VALIDATED,
                              false);
        BuildResults result = serviceHelper.localBuildAndDeploy(module,
                                                                DeploymentMode.VALIDATED,
                                                                false);
        verifyBuildAndDeploy(result);
    }

    private void prepareBuildAndDeploy(Module module,
                                       LocalBuildConfig.DeploymentType deploymentType,
                                       boolean suppressHandlers) {
        expectedRequest = BuildPipelineInvoker.LocalBuildRequest.newFullBuildAndDeployRequest(module,
                                                                                              deploymentType,
                                                                                              suppressHandlers);
        preparePipelineInvocation(expectedRequest);
        when(localBinaryConfig.getBuildResults()).thenReturn(buildResults);
    }

    private void verifyBuildAndDeploy(BuildResults result) {
        assertEquals(buildResults,
                     result);
        verify(pipelineInvoker,
               times(1)).invokeLocalBuildPipeLine(eq(expectedRequest),
                                                  any(Consumer.class));
    }

    @Test
    public void testLocalBuildWithAddResource() {
        testLocalBuildWithResource(module,
                                   LocalBuildConfig.BuildType.INCREMENTAL_ADD_RESOURCE,
                                   resource);
    }

    @Test
    public void testLocalBuildWithDeleteResource() {
        testLocalBuildWithResource(module,
                                   LocalBuildConfig.BuildType.INCREMENTAL_DELETE_RESOURCE,
                                   resource);
    }

    @Test
    public void testLocalBuildWithUpdateResource() {
        testLocalBuildWithResource(module,
                                   LocalBuildConfig.BuildType.INCREMENTAL_UPDATE_RESOURCE,
                                   resource);
    }

    private void testLocalBuildWithResource(Module module,
                                            LocalBuildConfig.BuildType buildType,
                                            Path resource) {
        BuildPipelineInvoker.LocalBuildRequest buildRequest =
                BuildPipelineInvoker.LocalBuildRequest.newIncrementalBuildRequest(module,
                                                                                  buildType,
                                                                                  resource);
        preparePipelineInvocation(buildRequest);
        when(localBinaryConfig.getIncrementalBuildResults()).thenReturn(incrementalBuildResults);
        IncrementalBuildResults result = serviceHelper.localBuild(module,
                                                                  buildType,
                                                                  resource);
        assertEquals(incrementalBuildResults,
                     result);
        verify(pipelineInvoker,
               times(1)).invokeLocalBuildPipeLine(eq(buildRequest),
                                                  any(Consumer.class));
    }

    @Test
    public void testLocalBuildWithResourceChanges() {
        BuildPipelineInvoker.LocalBuildRequest buildRequest =
                BuildPipelineInvoker.LocalBuildRequest.newIncrementalBuildRequest(module,
                                                                                  resourceChanges);
        preparePipelineInvocation(buildRequest);
        when(localBinaryConfig.getIncrementalBuildResults()).thenReturn(incrementalBuildResults);
        IncrementalBuildResults result = serviceHelper.localBuild(module,
                                                                  resourceChanges);
        assertEquals(incrementalBuildResults,
                     result);
        verify(pipelineInvoker,
               times(1)).invokeLocalBuildPipeLine(eq(buildRequest),
                                                  any(Consumer.class));
    }

    private void preparePipelineInvocation(BuildPipelineInvoker.LocalBuildRequest buildRequest) {
        //emulate the pipeline invocation with the desired params.
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Consumer consumer = (Consumer) invocation.getArguments()[1];
                consumer.accept(localBinaryConfig);
                return null;
            }
        }).when(pipelineInvoker).invokeLocalBuildPipeLine(eq(buildRequest),
                                                          any(Consumer.class));
    }
}