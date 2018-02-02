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
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.backend.builder.ala.LocalBuildConfig;
import org.kie.workbench.common.services.backend.builder.core.Builder;
import org.kie.workbench.common.services.backend.builder.core.LRUBuilderCache;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.ResourceChange;

@Service
@ApplicationScoped
public class BuildServiceImpl
        implements BuildService {

    private BuildServiceHelper buildServiceHelper;

    private KieModuleService moduleService;

    private LRUBuilderCache cache;

    public BuildServiceImpl() {
        //Empty constructor for Weld
    }

    @Inject
    public BuildServiceImpl(final KieModuleService moduleService,
                            final BuildServiceHelper buildServiceHelper,
                            final LRUBuilderCache cache) {
        this.moduleService = moduleService;
        this.buildServiceHelper = buildServiceHelper;
        this.cache = cache;
    }

    @Override
    public BuildResults build(final Module module) {
        return buildServiceHelper.localBuild(module);
    }

    public void build(final Module module,
                      final Consumer<Builder> consumer) {
        buildServiceHelper.localBuild(module,
                                      localBinaryConfig ->
                                              consumer.accept(localBinaryConfig.getBuilder()));
    }

    @Override
    public BuildResults buildAndDeploy(final Module module) {
        return buildAndDeploy(module,
                              DeploymentMode.VALIDATED);
    }

    @Override
    public BuildResults buildAndDeploy(final Module module,
                                       final DeploymentMode mode) {

        return buildAndDeploy(module,
                              false,
                              mode);
    }

    @Override
    public BuildResults buildAndDeploy(final Module module,
                                       final boolean suppressHandlers) {
        return buildAndDeploy(module,
                              suppressHandlers,
                              DeploymentMode.VALIDATED);
    }

    @Override
    public BuildResults buildAndDeploy(final Module module,
                                       final boolean suppressHandlers,
                                       final DeploymentMode mode) {
        return buildServiceHelper.localBuildAndDeploy(module,
                                                      mode,
                                                      suppressHandlers);
    }

    @Override
    public boolean isBuilt(final Module module) {
        final Builder builder = cache.assertBuilder(module);
        return builder.isBuilt();
    }

    @Override
    public IncrementalBuildResults addPackageResource(final Path resource) {
        return buildIncrementally(resource,
                                  LocalBuildConfig.BuildType.INCREMENTAL_ADD_RESOURCE);
    }

    @Override
    public IncrementalBuildResults deletePackageResource(final Path resource) {
        return buildIncrementally(resource,
                                  LocalBuildConfig.BuildType.INCREMENTAL_DELETE_RESOURCE);
    }

    @Override
    public IncrementalBuildResults updatePackageResource(final Path resource) {
        return buildIncrementally(resource,
                                  LocalBuildConfig.BuildType.INCREMENTAL_UPDATE_RESOURCE);
    }

    private IncrementalBuildResults buildIncrementally(Path resource,
                                                       LocalBuildConfig.BuildType buildType) {
        Module module = moduleService.resolveModule(resource);
        if (module == null) {
            return new IncrementalBuildResults();
        }
        return buildServiceHelper.localBuild(module,
                                             buildType,
                                             resource);
    }

    @Override
    public IncrementalBuildResults applyBatchResourceChanges(final Module module,
                                                             final Map<Path, Collection<ResourceChange>> changes) {
        if (module == null) {
            return new IncrementalBuildResults();
        }
        return buildServiceHelper.localBuild(module,
                                             changes);
    }
}