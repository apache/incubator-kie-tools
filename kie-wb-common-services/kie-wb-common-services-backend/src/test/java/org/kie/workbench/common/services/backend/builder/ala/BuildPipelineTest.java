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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.guvnor.ala.pipeline.ConfigExecutor;
import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.Stage;
import org.guvnor.ala.pipeline.events.AfterPipelineExecutionEvent;
import org.guvnor.ala.pipeline.events.AfterStageExecutionEvent;
import org.guvnor.ala.pipeline.events.BeforePipelineExecutionEvent;
import org.guvnor.ala.pipeline.events.BeforeStageExecutionEvent;
import org.guvnor.ala.pipeline.events.PipelineEventListener;
import org.guvnor.ala.registry.PipelineRegistry;
import org.guvnor.ala.registry.inmemory.InMemoryPipelineRegistry;
import org.guvnor.ala.source.Source;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.backend.builder.core.BuildHelper;
import org.kie.workbench.common.services.backend.builder.core.Builder;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.ResourceChange;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BuildPipelineTest
        extends BuildPipelineTestBase {

    @Mock
    private KieModuleService moduleService;

    @Mock
    private BuildHelper buildHelper;

    private PipelineRegistry pipelineRegistry = new InMemoryPipelineRegistry();

    private BuildPipelineInitializer pipelineInitializer;

    private Pipeline pipe;

    private LocalSourceConfigExecutor localSourceConfigExecutor;

    private LocalModuleConfigExecutor localModuleConfigExecutor;

    private LocalBuildConfigExecutor localBuildConfigExecutor;

    private LocalBuildExecConfigExecutor localBuildExecConfigExecutor;

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
    private PipelineEventListener pipelineEventListener;

    private Input input;

    private ArgumentCaptor<LocalSourceConfig> localSourceConfigCaptor;

    private ArgumentCaptor<Source> sourceConfigCaptor;

    private ArgumentCaptor<LocalProjectConfig> localProjectConfigCaptor;

    private ArgumentCaptor<LocalModule> localModuleCaptor;

    private ArgumentCaptor<LocalBuildConfig> localBuildConfigCaptor;

    private ArgumentCaptor<LocalBuildConfigInternal> localBuildConfigInternalCaptor;

    private ArgumentCaptor<LocalBuildExecConfig> localBuildExecConfigCaptor;

    @Before
    public void setUp() {
        localSourceConfigExecutor = spy(new LocalSourceConfigExecutor());
        localModuleConfigExecutor = spy(new LocalModuleConfigExecutor(moduleService));
        localBuildConfigExecutor = spy(new LocalBuildConfigExecutor());
        localBuildExecConfigExecutor = spy(new LocalBuildExecConfigExecutor(buildHelper));

        localSourceConfigCaptor = ArgumentCaptor.forClass(LocalSourceConfig.class);
        sourceConfigCaptor = ArgumentCaptor.forClass(Source.class);
        localProjectConfigCaptor = ArgumentCaptor.forClass(LocalProjectConfig.class);
        localModuleCaptor = ArgumentCaptor.forClass(LocalModule.class);
        localBuildConfigCaptor = ArgumentCaptor.forClass(LocalBuildConfig.class);
        localBuildConfigInternalCaptor = ArgumentCaptor.forClass(LocalBuildConfigInternal.class);
        localBuildExecConfigCaptor = ArgumentCaptor.forClass(LocalBuildExecConfig.class);

        Collection<ConfigExecutor> configs = new ArrayList<>();
        configs.add(localSourceConfigExecutor);
        configs.add(localModuleConfigExecutor);
        configs.add(localBuildConfigExecutor);
        configs.add(localBuildExecConfigExecutor);

        pipelineInitializer = new BuildPipelineInitializer(pipelineRegistry,
                                                           configs);
        pipe = pipelineRegistry.getPipelineByName(BuildPipelineInitializer.LOCAL_BUILD_PIPELINE);

        // verify the pipeline is properly initialized.
        assertNotNull(pipe);
        List<Stage> stages = pipe.getStages();
        assertEquals(4,
                     stages.size());
        assertEquals("Local Source Config",
                     stages.get(0).getName());
        assertEquals("Local Project Config",
                     stages.get(1).getName());
        assertEquals("Local Build Config",
                     stages.get(2).getName());
        assertEquals("Local Build Exec",
                     stages.get(3).getName());

        when(moduleService.resolveModule(Paths.convert(POM_PATH))).thenReturn(module);

        when(buildResult.getBuilder()).thenReturn(builder);
        when(buildResult.getBuildResults()).thenReturn(buildResults);
        when(buildResult.getIncrementalBuildResults()).thenReturn(incrementalBuildResults);
    }

    @Test
    public void testFullBuildExecution() {
        when(buildHelper.build(module)).thenReturn(buildResult);

        // prepare the pipeline input.
        input = createFullBuildInput(ROOT_PATH_URI);

        // execute the pipeline and verify the result.
        pipelineInitializer.getExecutor().execute(input,
                                                  pipe,
                                                  (Consumer<LocalBinaryConfig>) localBinaryConfig -> {
                                                      assertEquals(buildResults,
                                                                   localBinaryConfig.getBuildResults());
                                                      assertEquals(builder,
                                                                   localBinaryConfig.getBuilder());
                                                  },
                                                  pipelineEventListener);

        // verify that all stages were properly invoked.
        verifyLocalSourceConfigWasInvoked();
        verifyLocalProjectConfigWasInvoked();
        verifyLocalBuildConfigExecutorWasInvoked(module,
                                                 LocalBuildConfig.BuildType.FULL_BUILD.name());
        verifyLocalBuildExecConfigExecutorWasInvoked(module,
                                                     LocalBuildConfig.BuildType.FULL_BUILD);

        verifyPipelineEvents();
    }

    @Test
    public void testIncrementalBuildResourceAddExecution() {
        Path resourcePath = Paths.convert(getNioPath(RESOURCE_URI_1));
        when(moduleService.resolveModule(resourcePath)).thenReturn(module);
        when(buildHelper.addPackageResource(resourcePath)).thenReturn(incrementalBuildResults);
        doTestIncrementalBuildResourceExecution(module,
                                                RESOURCE_URI_1,
                                                LocalBuildConfig.BuildType.INCREMENTAL_ADD_RESOURCE);
    }

    @Test
    public void testIncrementalBuildResourceUpdateExecution() {
        Path resourcePath = Paths.convert(getNioPath(RESOURCE_URI_1));
        when(moduleService.resolveModule(resourcePath)).thenReturn(module);
        when(buildHelper.updatePackageResource(resourcePath)).thenReturn(incrementalBuildResults);
        doTestIncrementalBuildResourceExecution(module,
                                                RESOURCE_URI_1,
                                                LocalBuildConfig.BuildType.INCREMENTAL_UPDATE_RESOURCE);
    }

    @Test
    public void testIncrementalBuildResourceDeleteExecution() {
        Path resourcePath = Paths.convert(getNioPath(RESOURCE_URI_1));
        when(moduleService.resolveModule(resourcePath)).thenReturn(module);
        when(buildHelper.deletePackageResource(resourcePath)).thenReturn(incrementalBuildResults);
        doTestIncrementalBuildResourceExecution(module,
                                                RESOURCE_URI_1,
                                                LocalBuildConfig.BuildType.INCREMENTAL_DELETE_RESOURCE);
    }

    @Test
    public void testIncrementalBuildResourceChangesExecution() {
        when(buildHelper.applyBatchResourceChanges(eq(module),
                                                   any(Map.class))).thenReturn(incrementalBuildResults);

        // prepare the pipeline input.
        Input input = createBatchChangesInput(ROOT_PATH_URI,
                                              LocalBuildConfig.BuildType.INCREMENTAL_BATCH_CHANGES.name(),
                                              changes);

        // execute the pipeline and verify the result.
        pipelineInitializer.getExecutor().execute(input,
                                                  pipe,
                                                  (Consumer<LocalBinaryConfig>) localBinaryConfig -> {
                                                      assertEquals(incrementalBuildResults,
                                                                   localBinaryConfig.getIncrementalBuildResults());
                                                  },
                                                  pipelineEventListener);

        // verify that all stages were properly invoked.
        verifyLocalSourceConfigWasInvoked();
        verifyLocalProjectConfigWasInvoked();
        verifyLocalBuildConfigExecutorWasInvoked(module,
                                                 changes);
        verifyLocalBuildExecConfigExecutorWasInvoked(module,
                                                     changes);

        // verify the pipeline events where properly raised.
        verifyPipelineEvents();
    }

    @Test
    public void testFullBuildAndDeployForcedNotSuppressHandlersExecution() {
        testFullBuildAndDeploy(LocalBuildConfig.DeploymentType.FORCED,
                               false);
    }

    @Test
    public void testFullBuildAndDeployForcedSuppressHandlers() {
        testFullBuildAndDeploy(LocalBuildConfig.DeploymentType.FORCED,
                               true);
    }

    @Test
    public void testFullBuildAndDeployValidatedNotSuppressHandlers() {
        testFullBuildAndDeploy(LocalBuildConfig.DeploymentType.VALIDATED,
                               false);
    }

    @Test
    public void testFullBuildAndDeployValidatedSuppressHandlers() {
        testFullBuildAndDeploy(LocalBuildConfig.DeploymentType.VALIDATED,
                               false);
    }

    private void testFullBuildAndDeploy(LocalBuildConfig.DeploymentType deploymentType,
                                        boolean suppressHandlers) {
        when(buildHelper.buildAndDeploy(module,
                                        suppressHandlers,
                                        DeploymentMode.valueOf(deploymentType.name()))).thenReturn(buildResults);

        // prepare the pipeline input.
        input = createFullBuildAndDeployInput(ROOT_PATH_URI,
                                              deploymentType.name(),
                                              suppressHandlers);

        // execute the pipeline and verify the result.
        pipelineInitializer.getExecutor().execute(input,
                                                  pipe,
                                                  (Consumer<LocalBinaryConfig>) localBinaryConfig -> {
                                                      assertEquals(buildResults,
                                                                   localBinaryConfig.getBuildResults());
                                                  },
                                                  pipelineEventListener);

        // verify that all stages were properly invoked.
        verifyLocalSourceConfigWasInvoked();
        verifyLocalProjectConfigWasInvoked();
        verifyLocalBuildConfigExecutorWasInvoked(module,
                                                 LocalBuildConfig.BuildType.FULL_BUILD_AND_DEPLOY.name(),
                                                 deploymentType.name(),
                                                 Boolean.toString(suppressHandlers));
        verifyLocalBuildExecConfigExecutorWasInvoked(module,
                                                     LocalBuildConfig.BuildType.FULL_BUILD_AND_DEPLOY,
                                                     deploymentType,
                                                     suppressHandlers);

        // verify the pipeline events where properly raised.
        verifyPipelineEvents();
    }

    private void doTestIncrementalBuildResourceExecution(KieModule module,
                                                         String resourceUri,
                                                         LocalBuildConfig.BuildType buildType) {
        // prepare the pipeline input.
        Input input = createIncrementalBuildInput(ROOT_PATH_URI,
                                                  resourceUri,
                                                  buildType.name());

        // execute the pipeline and verify the result.
        pipelineInitializer.getExecutor().execute(input,
                                                  pipe,
                                                  (Consumer<LocalBinaryConfig>) localBinaryConfig -> {
                                                      assertEquals(incrementalBuildResults,
                                                                   localBinaryConfig.getIncrementalBuildResults());
                                                  },
                                                  pipelineEventListener);

        // verify that all stages were properly invoked.
        verifyLocalSourceConfigWasInvoked();
        verifyLocalProjectConfigWasInvoked();
        verifyLocalBuildConfigExecutorWasInvoked(module,
                                                 resourceUri,
                                                 buildType.name());
        verifyLocalBuildExecConfigExecutorWasInvoked(module,
                                                     Paths.convert(getNioPath(resourceUri)),
                                                     buildType);

        // verify the pipeline events where properly raised.
        verifyPipelineEvents();
    }

    private void verifyLocalSourceConfigWasInvoked() {
        verify(localSourceConfigExecutor,
               times(1)).apply(localSourceConfigCaptor.capture());
        assertEquals(ROOT_PATH_URI,
                     localSourceConfigCaptor.getValue().getRootPath());
    }

    private void verifyLocalProjectConfigWasInvoked() {
        verify(localModuleConfigExecutor,
               times(1)).apply(sourceConfigCaptor.capture(),
                               localProjectConfigCaptor.capture());
        assertEquals(ROOT_PATH,
                     sourceConfigCaptor.getValue().getPath());
    }

    private void verifyLocalBuildConfigExecutorWasInvoked(KieModule module,
                                                          String buildType) {
        verify(localBuildConfigExecutor,
               times(1)).apply(localModuleCaptor.capture(),
                               localBuildConfigCaptor.capture());
        assertEquals(module,
                     localModuleCaptor.getValue().getModule());
        assertEquals(buildType,
                     localBuildConfigCaptor.getValue().getBuildType());
    }

    private void verifyLocalBuildConfigExecutorWasInvoked(KieModule module,
                                                          String resourceUri,
                                                          String buildType) {
        verify(localBuildConfigExecutor,
               times(1)).apply(localModuleCaptor.capture(),
                               localBuildConfigCaptor.capture());
        assertEquals(module,
                     localModuleCaptor.getValue().getModule());
        assertEquals(resourceUri,
                     localBuildConfigCaptor.getValue().getResource());
        assertEquals(buildType,
                     localBuildConfigCaptor.getValue().getBuildType());
    }

    private void verifyLocalBuildConfigExecutorWasInvoked(KieModule module,
                                                          String buildType,
                                                          String deploymentType,
                                                          String suppressHandlers) {
        verify(localBuildConfigExecutor,
               times(1)).apply(localModuleCaptor.capture(),
                               localBuildConfigCaptor.capture());
        assertEquals(module,
                     localModuleCaptor.getValue().getModule());
        assertEquals(buildType,
                     localBuildConfigCaptor.getValue().getBuildType());
        assertEquals(deploymentType,
                     localBuildConfigCaptor.getValue().getDeploymentType());
        assertEquals(suppressHandlers,
                     localBuildConfigCaptor.getValue().getSuppressHandlers());
    }

    private void verifyLocalBuildConfigExecutorWasInvoked(KieModule module,
                                                          ResourceChangeRequest... changes) {
        verify(localBuildConfigExecutor,
               times(1)).apply(localModuleCaptor.capture(),
                               localBuildConfigCaptor.capture());
        assertEquals(module,
                     localModuleCaptor.getValue().getModule());
        assertEquals(LocalBuildConfig.BuildType.INCREMENTAL_BATCH_CHANGES.name(),
                     localBuildConfigCaptor.getValue().getBuildType());
        for (ResourceChangeRequest change : changes) {
            assertNotNull(localBuildConfigCaptor.getValue().getResourceChanges().get(LocalBuildConfig.RESOURCE_CHANGE + change.getUri()));
            assertEquals(change.getChanges(),
                         localBuildConfigCaptor.getValue().getResourceChanges().get(LocalBuildConfig.RESOURCE_CHANGE + change.getUri()));
        }
    }

    private void verifyLocalBuildExecConfigExecutorWasInvoked(KieModule module,
                                                              LocalBuildConfig.BuildType buildType) {
        verify(localBuildExecConfigExecutor,
               times(1)).apply(localBuildConfigInternalCaptor.capture(),
                               localBuildExecConfigCaptor.capture());
        assertEquals(module,
                     localBuildConfigInternalCaptor.getValue().getModule());
        assertEquals(buildType,
                     localBuildConfigInternalCaptor.getValue().getBuildType());
    }

    private void verifyLocalBuildExecConfigExecutorWasInvoked(KieModule module,
                                                              Path resourcePath,
                                                              LocalBuildConfig.BuildType buildType) {
        verify(localBuildExecConfigExecutor,
               times(1)).apply(localBuildConfigInternalCaptor.capture(),
                               localBuildExecConfigCaptor.capture());
        assertEquals(module,
                     localBuildConfigInternalCaptor.getValue().getModule());
        assertEquals(resourcePath,
                     localBuildConfigInternalCaptor.getValue().getResource());
        assertEquals(buildType,
                     localBuildConfigInternalCaptor.getValue().getBuildType());
    }

    private void verifyLocalBuildExecConfigExecutorWasInvoked(KieModule module,
                                                              ResourceChangeRequest... changes) {
        verify(localBuildExecConfigExecutor,
               times(1)).apply(localBuildConfigInternalCaptor.capture(),
                               localBuildExecConfigCaptor.capture());
        assertEquals(module,
                     localBuildConfigInternalCaptor.getValue().getModule());
        assertEquals(LocalBuildConfig.BuildType.INCREMENTAL_BATCH_CHANGES,
                     localBuildConfigInternalCaptor.getValue().getBuildType());
        assertEqualsChanges(createResourceChanges(changes),
                            localBuildConfigInternalCaptor.getValue().getResourceChanges());
    }

    private void verifyLocalBuildExecConfigExecutorWasInvoked(KieModule module,
                                                              LocalBuildConfig.BuildType buildType,
                                                              LocalBuildConfig.DeploymentType deploymentType,
                                                              boolean suppressHandlers) {
        verify(localBuildExecConfigExecutor,
               times(1)).apply(localBuildConfigInternalCaptor.capture(),
                               localBuildExecConfigCaptor.capture());
        assertEquals(module,
                     localBuildConfigInternalCaptor.getValue().getModule());
        assertEquals(buildType,
                     localBuildConfigInternalCaptor.getValue().getBuildType());
        assertEquals(deploymentType,
                     localBuildConfigInternalCaptor.getValue().getDeploymentType());
        assertEquals(suppressHandlers,
                     localBuildConfigInternalCaptor.getValue().isSuppressHandlers());
    }

    private void verifyPipelineEvents() {
        ArgumentCaptor<BeforePipelineExecutionEvent> beforePipelineExecutionCaptor = ArgumentCaptor.forClass(BeforePipelineExecutionEvent.class);
        ArgumentCaptor<BeforeStageExecutionEvent> beforeStageExecutionCaptor = ArgumentCaptor.forClass(BeforeStageExecutionEvent.class);
        ArgumentCaptor<AfterStageExecutionEvent> afterStageExecutionCaptor = ArgumentCaptor.forClass(AfterStageExecutionEvent.class);
        ArgumentCaptor<AfterPipelineExecutionEvent> afterPipelineExecutionCaptor = ArgumentCaptor.forClass(AfterPipelineExecutionEvent.class);

        // verify the pipeline initialization event was raised.
        verify(pipelineEventListener,
               times(1)).beforePipelineExecution(beforePipelineExecutionCaptor.capture());
        assertEquals(pipe,
                     beforePipelineExecutionCaptor.getValue().getPipeline());

        // verify the initialization and finalization events were properly raised for current pipe stages.
        verify(pipelineEventListener,
               times(4)).beforeStageExecution(beforeStageExecutionCaptor.capture());
        verify(pipelineEventListener,
               times(4)).afterStageExecution(afterStageExecutionCaptor.capture());

        for (int i = 0; i < pipe.getStages().size(); i++) {
            assertEquals(pipe.getStages().get(i),
                         beforeStageExecutionCaptor.getAllValues().get(i).getStage());
            assertEquals(pipe,
                         beforeStageExecutionCaptor.getAllValues().get(i).getPipeline());
            assertEquals(pipe.getStages().get(i),
                         afterStageExecutionCaptor.getAllValues().get(i).getStage());
            assertEquals(pipe,
                         afterStageExecutionCaptor.getAllValues().get(i).getPipeline());
        }

        // verify the pipeline finalization event was raised.
        verify(pipelineEventListener,
               times(1)).afterPipelineExecution(afterPipelineExecutionCaptor.capture());
        assertEquals(pipe,
                     afterPipelineExecutionCaptor.getValue().getPipeline());
    }

    private void assertEqualsChanges(Map<Path, Collection<ResourceChange>> expectedResourceChanges,
                                     Map<Path, Collection<ResourceChange>> resourceChanges) {
        assertEquals(expectedResourceChanges.size(),
                     resourceChanges.size());
        for (Map.Entry<Path, Collection<ResourceChange>> entry : expectedResourceChanges.entrySet()) {
            assertNotNull(resourceChanges.get(entry.getKey()));
            assertEquals(entry.getValue().size(),
                         resourceChanges.get(entry.getKey()).size());
            for (ResourceChange resourceChange : entry.getValue()) {
                assertEquals(1,
                             resourceChanges.get(entry.getKey()).stream().filter(c -> resourceChange.getType().equals(c.getType())).count());
            }
        }
    }
}