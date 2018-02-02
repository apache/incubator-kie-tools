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
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.ala.config.Config;
import org.guvnor.ala.pipeline.BiFunctionConfigExecutor;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.kie.workbench.common.services.backend.builder.ala.impl.LocalBuildBinaryImpl;
import org.kie.workbench.common.services.backend.builder.core.BuildHelper;

/**
 * Executor for a LocalBuildExecConfig configuration.
 */
@ApplicationScoped
public class LocalBuildExecConfigExecutor
        implements BiFunctionConfigExecutor<LocalBuildConfigInternal, LocalBuildExecConfig, LocalBinaryConfig> {

    private BuildHelper buildHelper;

    public LocalBuildExecConfigExecutor() {
        //Empty constructor for Weld proxying
    }

    @Inject
    public LocalBuildExecConfigExecutor(BuildHelper buildHelper) {
        this.buildHelper = buildHelper;
    }

    /**
     * This executor executes the formal build of the project by using the internal build configuration.
     * Internally the local build system relies on the BuildHelper.
     * @param localBuildConfigInternal an internal build configuration for building a project in the local build system.
     * @param localBuildExecConfig the local build execution configuration.
     * @return the information about the produced Binary and the build results in the local build system.
     */
    @Override
    public Optional<LocalBinaryConfig> apply(LocalBuildConfigInternal localBuildConfigInternal, LocalBuildExecConfig localBuildExecConfig) {
        Optional<LocalBinaryConfig> result = Optional.empty();
        BuildHelper.BuildResult buildResult;

        switch (localBuildConfigInternal.getBuildType()) {
            case FULL_BUILD:
                buildResult = buildHelper.build(localBuildConfigInternal.getModule());
                result = Optional.of(new LocalBuildBinaryImpl(buildResult.getBuilder(), buildResult.getBuildResults()));
                break;
            case INCREMENTAL_ADD_RESOURCE:
                result = Optional.of(new LocalBuildBinaryImpl(
                        buildHelper.addPackageResource(localBuildConfigInternal.getResource())));
                break;
            case INCREMENTAL_UPDATE_RESOURCE:
                result = Optional.of(new LocalBuildBinaryImpl(
                        buildHelper.updatePackageResource(localBuildConfigInternal.getResource())));
                break;
            case INCREMENTAL_DELETE_RESOURCE:
                result = Optional.of(new LocalBuildBinaryImpl(
                        buildHelper.deletePackageResource(localBuildConfigInternal.getResource())));
                break;
            case INCREMENTAL_BATCH_CHANGES:
                result = Optional.of(
                        new LocalBuildBinaryImpl(buildHelper.applyBatchResourceChanges(
                                localBuildConfigInternal.getModule(),
                                localBuildConfigInternal.getResourceChanges())));
                break;
            case FULL_BUILD_AND_DEPLOY:
                result = Optional.of(
                        new LocalBuildBinaryImpl(
                                buildHelper.buildAndDeploy(localBuildConfigInternal.getModule(),
                                                           localBuildConfigInternal.isSuppressHandlers(),
                                                           toDeploymentMode(localBuildConfigInternal.getDeploymentType()))));
                break;
        }
        return result;
    }

    @Override
    public Class<? extends Config> executeFor() {
        return LocalBuildExecConfig.class;
    }

    @Override
    public String outputId() {
        return "local-binary";
    }

    private DeploymentMode toDeploymentMode(LocalBuildConfig.DeploymentType deploymentType) {
        return deploymentType == LocalBuildConfig.DeploymentType.VALIDATED ? DeploymentMode.VALIDATED : DeploymentMode.FORCED;
    }
}