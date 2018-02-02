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

import org.guvnor.ala.config.BuildConfig;
import org.guvnor.common.services.project.model.Module;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.ResourceChange;

/**
 * This interface represents the internal configuration for a build to be performed by the local build system.
 */
public class LocalBuildConfigInternal implements BuildConfig {

    private LocalBuildConfig.BuildType buildType;

    private LocalBuildConfig.DeploymentType deploymentType;

    private Module module;

    private Path resource;

    private Map<Path, Collection<ResourceChange>> resourceChanges = new HashMap<>();

    private boolean suppressHandlers;

    public LocalBuildConfigInternal() {
    }

    public LocalBuildConfigInternal(Module module) {
        this.module = module;
        this.buildType = LocalBuildConfig.BuildType.FULL_BUILD;
    }

    public LocalBuildConfigInternal(Module module, LocalBuildConfig.DeploymentType deploymentType, boolean suppressHandlers) {
        this.module = module;
        this.deploymentType = deploymentType;
        this.suppressHandlers = suppressHandlers;
        this.buildType = LocalBuildConfig.BuildType.FULL_BUILD_AND_DEPLOY;
    }

    public LocalBuildConfigInternal(Module module, LocalBuildConfig.BuildType buildType, Path resource) {
        this.module = module;
        this.buildType = buildType;
        this.resource = resource;
    }

    public LocalBuildConfigInternal(Module module, Map<Path, Collection<ResourceChange>> resourceChanges) {
        this.module = module;
        this.resourceChanges = resourceChanges;
        this.buildType = LocalBuildConfig.BuildType.INCREMENTAL_BATCH_CHANGES;
    }

    /**
     * @return the BuildType to be performed.
     */
    public LocalBuildConfig.BuildType getBuildType() {
        return buildType;
    }

    /**
     * @return the module that will be built.
     */
    public Module getModule() {
        return module;
    }

    /**
     * @return in the case of an incremental build returns the resource that will be used for the incremental build.
     */
    public Path getResource() {
        return resource;
    }

    /**
     * @return in the case of an incremental build where multiple resources where changed returns the set of changes.
     */
    public Map<Path, Collection<ResourceChange>> getResourceChanges() {
        return resourceChanges;
    }

    /**
     * @return in the case where a full build and deploy was selected returns the DeploymentType to perform.
     */
    public LocalBuildConfig.DeploymentType getDeploymentType() {
        return deploymentType;
    }

    /**
     * @return in the case where a full build and deploy was selected returns a boolean indicating if the PostBuildHandlers
     * invocation should be suppressed.
     */
    public boolean isSuppressHandlers() {
        return suppressHandlers;
    }
}