/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.services.shared.builder;

import java.util.Set;

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.services.shared.context.Project;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.ResourceChange;

@Remote
public interface BuildService {

    /**
     * Full build without deployment
     * @param project
     */
    void build( final Project project );

    /**
     * Full build with deployment
     * @param project
     */
    void buildAndDeploy( final Project project );

    /**
     * Add a Package resource to the build.
     * @param resource
     */
    void addPackageResource( final Path resource );

    /**
     * Remove a Package resource from the build.
     * @param resource
     */
    void deletePackageResource( final Path resource );

    /**
     * Update an existing Package resource in the build.
     * @param resource
     */
    void updatePackageResource( final Path resource );

    /**
     * Update an existing Project resource in the build.
     * @param resource
     */
    void updateProjectResource( final Path resource );

    /**
     * Process a batch of changes to a Project's resources.
     * @param project
     * @param changes
     */
    void applyBatchResourceChanges( final Project project,
                                    final Set<ResourceChange> changes );

}
