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
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.guvnor.common.services.project.builder.service.PostBuildHandler;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.kie.workbench.common.services.backend.builder.ala.BuildPipelineInvoker;
import org.kie.workbench.common.services.backend.builder.ala.LocalBinaryConfig;
import org.kie.workbench.common.services.backend.builder.ala.LocalBuildConfig;
import org.kie.workbench.common.services.backend.builder.core.DeploymentVerifier;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.ResourceChange;

/**
 * Helper class for performing module build tasks. This class is mainly used by the BuildServiceImpl and hides the
 * interactions with the underlying build system.
 */
@ApplicationScoped
public class BuildServiceHelper {

    private BuildPipelineInvoker buildPipelineInvoker;

    private DeploymentVerifier deploymentVerifier;

    public BuildServiceHelper() {
        //Empty constructor for Weld proxying
    }

    @Inject
    public BuildServiceHelper(BuildPipelineInvoker buildPipelineInvoker,
                              DeploymentVerifier deploymentVerifier) {
        this.buildPipelineInvoker = buildPipelineInvoker;
        this.deploymentVerifier = deploymentVerifier;
    }

    /**
     * Performs the full build of a module.
     * @param module the module to build.
     * @return the BuildResults for the module full build operation. Interested parties may check the results for
     * knowing if the build produced errors.
     */
    public BuildResults localBuild(Module module) {
        final BuildResults[] result = new BuildResults[1];
        invokeLocalBuildPipeLine(module,
                                 localBinaryConfig -> {
                                     result[0] = localBinaryConfig.getBuildResults();
                                 });
        return result[0];
    }

    /**
     * Performs the full build of a module.
     * @param module the module to build.
     * @param consumer a consumer for consuming the BuildResults for the module full build operation. Interested parties
     * may check the results for knowing if the build produced errors.
     */
    public void localBuild(Module module,
                           Consumer<LocalBinaryConfig> consumer) {
        invokeLocalBuildPipeLine(module,
                                 consumer);
    }

    /**
     * Performs the incremental build of a module.
     * @param module the module to build incrementally.
     * @param buildType the incremental build type to perform.
     * @param resource the Path to the resource for which the incremental build will be produced.
     * @return the IncrementalBuildResults for the incremental build operation. Interested parties may check the results
     * for knowing if the incremental build produced errors.
     */
    public IncrementalBuildResults localBuild(Module module,
                                              LocalBuildConfig.BuildType buildType,
                                              Path resource) {
        final IncrementalBuildResults[] result = new IncrementalBuildResults[1];
        invokeLocalBuildPipeLine(module,
                                 buildType,
                                 resource,
                                 localBinaryConfig -> {
                                     result[0] = localBinaryConfig.getIncrementalBuildResults();
                                 });
        return result[0];
    }

    /**
     * Performs the incremental build of a module when a set of resources has been changed. This method is typically used
     * when batch operations were performed and a set of resources has changed as part of the same operation.
     * @param module the module to build incrementally.
     * @param resourceChanges a Map which holds the collection of changes produced by resource.
     * @return the IncrementalBuildResults for the incremental build operation. Interested parties may check the results
     * for knowing if the incremental build produced errors.
     */
    public IncrementalBuildResults localBuild(Module module,
                                              Map<Path, Collection<ResourceChange>> resourceChanges) {
        final IncrementalBuildResults[] result = new IncrementalBuildResults[1];
        invokeLocalBuildPipeLine(module,
                                 resourceChanges,
                                 localBinaryConfig -> {
                                     result[0] = localBinaryConfig.getIncrementalBuildResults();
                                 });
        return result[0];
    }

    /**
     * Performs the full build of a module and deploys the generated maven artifact in current server m2Repository.
     * @param module the module to build incrementally.
     * @param mode the DeploymentMode do use.
     * @param suppressHandlers true of PostBuildHandlers invocation should be suppressed, false in any other case.
     * @return the BuildResults for the module build and deploy operation. Interested parties may check the results for
     * knowing if errors has occurred.
     * @see DeploymentMode
     * @see PostBuildHandler
     */
    public BuildResults localBuildAndDeploy(final Module module,
                                            final DeploymentMode mode,
                                            final boolean suppressHandlers) {
        final BuildResults[] result = new BuildResults[1];
        invokeLocalBuildPipeLine(module,
                                 suppressHandlers,
                                 mode,
                                 localBinaryConfig -> {
                                     result[0] = localBinaryConfig.getBuildResults();
                                 });
        return result[0];
    }

    private void invokeLocalBuildPipeLine(Module module,
                                          Consumer<LocalBinaryConfig> consumer) {
        BuildPipelineInvoker.LocalBuildRequest buildRequest = BuildPipelineInvoker.LocalBuildRequest.newFullBuildRequest(module);
        buildPipelineInvoker.invokeLocalBuildPipeLine(buildRequest,
                                                      consumer);
    }

    private void invokeLocalBuildPipeLine(Module module,
                                          LocalBuildConfig.BuildType buildType,
                                          Path resource,
                                          Consumer<LocalBinaryConfig> consumer) {
        BuildPipelineInvoker.LocalBuildRequest buildRequest = BuildPipelineInvoker.LocalBuildRequest.newIncrementalBuildRequest(module,
                                                                                                                                buildType,
                                                                                                                                resource);
        buildPipelineInvoker.invokeLocalBuildPipeLine(buildRequest,
                                                      consumer);
    }

    private void invokeLocalBuildPipeLine(Module module,
                                          Map<Path, Collection<ResourceChange>> resourceChanges,
                                          Consumer<LocalBinaryConfig> consumer) {
        BuildPipelineInvoker.LocalBuildRequest buildRequest = BuildPipelineInvoker.LocalBuildRequest.newIncrementalBuildRequest(module,
                                                                                                                                resourceChanges);
        buildPipelineInvoker.invokeLocalBuildPipeLine(buildRequest,
                                                      consumer);
    }

    private void invokeLocalBuildPipeLine(Module module,
                                          boolean suppressHandlers,
                                          DeploymentMode mode,
                                          Consumer<LocalBinaryConfig> consumer) {
        deploymentVerifier.verifyWithException(module,
                                               mode);
        BuildPipelineInvoker.LocalBuildRequest buildRequest = BuildPipelineInvoker.LocalBuildRequest.newFullBuildAndDeployRequest(module,
                                                                                                                                  toDeploymentType(mode),
                                                                                                                                  suppressHandlers);
        buildPipelineInvoker.invokeLocalBuildPipeLine(buildRequest,
                                                      consumer);
    }

    private LocalBuildConfig.DeploymentType toDeploymentType(DeploymentMode deploymentMode) {
        return deploymentMode == DeploymentMode.VALIDATED ? LocalBuildConfig.DeploymentType.VALIDATED : LocalBuildConfig.DeploymentType.FORCED;
    }
}