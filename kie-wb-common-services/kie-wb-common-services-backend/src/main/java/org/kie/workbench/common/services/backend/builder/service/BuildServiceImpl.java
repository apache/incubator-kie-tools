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
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.backend.builder.ala.LocalBuildConfig;
import org.kie.workbench.common.services.backend.builder.core.Builder;
import org.kie.workbench.common.services.backend.builder.core.LRUBuilderCache;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.ResourceChange;

@Service
@ApplicationScoped
public class BuildServiceImpl implements BuildService {

    private BuildServiceHelper buildServiceHelper;

    private KieProjectService projectService;

    private LRUBuilderCache cache;

    public BuildServiceImpl( ) {
        //Empty constructor for Weld
    }

    @Inject
    public BuildServiceImpl( final KieProjectService projectService,
                             final BuildServiceHelper buildServiceHelper,
                             final LRUBuilderCache cache ) {
        this.projectService = projectService;
        this.buildServiceHelper = buildServiceHelper;
        this.cache = cache;
    }

    @Override
    public BuildResults build( final Project project ) {
        return buildServiceHelper.localBuild( project );
    }

    public void build( final Project project, final Consumer< Builder > consumer ) {
        buildServiceHelper.localBuild( project, localBinaryConfig ->
                consumer.accept( localBinaryConfig.getBuilder( ) ) );
    }

    @Override
    public BuildResults buildAndDeploy( final Project project ) {
        return buildAndDeploy( project, DeploymentMode.VALIDATED );
    }

    @Override
    public BuildResults buildAndDeploy( final Project project,
                                        final DeploymentMode mode ) {

        return buildAndDeploy( project, false, mode );
    }

    @Override
    public BuildResults buildAndDeploy( final Project project,
                                        final boolean suppressHandlers ) {
        return buildAndDeploy( project, suppressHandlers, DeploymentMode.VALIDATED );
    }

    @Override
    public BuildResults buildAndDeploy( final Project project,
                                        final boolean suppressHandlers,
                                        final DeploymentMode mode ) {
        return buildServiceHelper.localBuildAndDeploy( project, mode, suppressHandlers );
    }

    @Override
    public boolean isBuilt( final Project project ) {
        final Builder builder = cache.assertBuilder( project );
        return builder.isBuilt( );
    }

    @Override
    public IncrementalBuildResults addPackageResource( final Path resource ) {
        return buildIncrementally( resource, LocalBuildConfig.BuildType.INCREMENTAL_ADD_RESOURCE );
    }

    @Override
    public IncrementalBuildResults deletePackageResource( final Path resource ) {
        return buildIncrementally( resource, LocalBuildConfig.BuildType.INCREMENTAL_DELETE_RESOURCE );
    }

    @Override
    public IncrementalBuildResults updatePackageResource( final Path resource ) {
        return buildIncrementally( resource, LocalBuildConfig.BuildType.INCREMENTAL_UPDATE_RESOURCE );
    }

    private IncrementalBuildResults buildIncrementally( Path resource, LocalBuildConfig.BuildType buildType ) {
        Project project = projectService.resolveProject( resource );
        if ( project == null ) {
            return new IncrementalBuildResults( );
        }
        return buildServiceHelper.localBuild( project, buildType, resource );
    }

    @Override
    public IncrementalBuildResults applyBatchResourceChanges( final Project project,
                                                              final Map< Path, Collection< ResourceChange > > changes ) {
        if ( project == null ) {
            return new IncrementalBuildResults( );
        }
        return buildServiceHelper.localBuild( project, changes );
    }

}