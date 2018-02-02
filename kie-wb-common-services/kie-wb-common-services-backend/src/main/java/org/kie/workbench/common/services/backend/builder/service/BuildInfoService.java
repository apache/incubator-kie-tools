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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.model.Module;
import org.kie.workbench.common.services.backend.builder.core.Builder;
import org.kie.workbench.common.services.backend.builder.core.LRUBuilderCache;

/**
 * Service for providing access to the build information for a given project.
 * In general workbench modules and services should avoid direct access to the LRUBuilderCache, instead they should
 * use the BuildInfoService.
 */
@ApplicationScoped
public class BuildInfoService {

    private BuildService buildService;

    private LRUBuilderCache builderCache;

    public BuildInfoService() {
        //Empty constructor for Weld proxying
    }

    @Inject
    public BuildInfoService(BuildService buildService,
                            LRUBuilderCache builderCache) {
        this.buildService = buildService;
        this.builderCache = builderCache;
    }

    /**
     * Gets the BuildInfo for a given module. The BuildInfoService decides internally whenever the module should be
     * built prior to construct the BuildInfo.
     * @param module The module for getting the BuildInfo.
     * @return the BuildInfo for the given module.
     */
    public BuildInfo getBuildInfo(final Module module) {
        final Builder[] result = {builderCache.getBuilder(module)};
        if (result[0] == null || !result[0].isBuilt()) {
            ((BuildServiceImpl) buildService).build(module,
                                                    builder -> result[0] = builder);
        }
        return new BuildInfoImpl(result[0]);
    }
}