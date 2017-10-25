/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.common.services.project.builder.service;

import java.util.Collection;
import java.util.Map;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.ResourceChange;

@Remote
public interface BuildService {

    /**
     * Full build without deployment
     * @param project
     */
    BuildResults build(final Project project);

    /**
     * Full build with deployment
     * @param project
     */
    BuildResults buildAndDeploy(final Project project);

    /**
     * Full build with forced deployment to Maven. Pre-existing artifacts with the same GAV will be overwritten.
     * @param project Project to be built
     * @param mode The deployment mode
     */
    BuildResults buildAndDeploy(final Project project,
                                final DeploymentMode mode);

    /**
     * Full build with deployment with ability to suppress any post operations handlers to ensure
     * that only build and deploy was invoked
     * @param project
     */
    BuildResults buildAndDeploy(final Project project,
                                final boolean suppressHandlers);

    /**
     * Full build with forced deployment to Maven. Pre-existing artifacts with the same GAV will be overwritten. This
     * method has the ability to suppress any post operations handlers to ensure * that only build and deploy is invoked
     * @param project Project to be built
     * @param suppressHandlers true to ignore post-processing
     * @param mode The deployment mode
     */
    BuildResults buildAndDeploy(final Project project,
                                final boolean suppressHandlers,
                                final DeploymentMode mode);

    /**
     * Check whether a Project has been built
     * @param project
     * @return
     */
    boolean isBuilt(final Project project);

    /**
     * Add a Package resource to the build.
     * @param resource
     */
    IncrementalBuildResults addPackageResource(final Path resource);

    /**
     * Remove a Package resource from the build.
     * @param resource
     */
    IncrementalBuildResults deletePackageResource(final Path resource);

    /**
     * Update an existing Package resource in the build.
     * @param resource
     */
    IncrementalBuildResults updatePackageResource(final Path resource);

    /**
     * Process a batch of changes to a Project's resources.
     * @param project
     * @param changes
     */
    IncrementalBuildResults applyBatchResourceChanges(final Project project,
                                                      final Map<Path, Collection<ResourceChange>> changes);
}
