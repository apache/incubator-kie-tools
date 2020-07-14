/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.scenariosimulation.backend.server;

import javax.inject.Inject;

import org.kie.api.runtime.KieContainer;
import org.kie.workbench.common.services.backend.builder.core.Builder;
import org.kie.workbench.common.services.backend.builder.service.BuildInfo;
import org.kie.workbench.common.services.backend.builder.service.BuildInfoImpl;
import org.kie.workbench.common.services.backend.builder.service.BuildInfoService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.vfs.Path;

public abstract class AbstractKieContainerService {

    @Inject
    private KieModuleService moduleService;
    @Inject
    private BuildInfoService buildInfoService;

    protected KieContainer getKieContainer(Path path) {
        final KieModule kieModule = moduleService.resolveModule(path);

        BuildInfo buildInfo = buildInfoService.getBuildInfo(kieModule);
        if (buildInfo instanceof BuildInfoImpl) {
            // The kie builder needs to be cloned so that the original does not get altered.
            final Builder clone = ((BuildInfoImpl) buildInfo).getBuilder().clone();
            clone.build();

            final KieContainer kieContainer = clone.getKieContainer();
            if (kieContainer == null) {
                throw new IllegalArgumentException("Retrieving KieContainer has failed. Fix all compilation errors within the " +
                                                           "project and build the project again.");
            }
            return kieContainer;
        }

        throw new IllegalStateException("Failed to clone Builder.");

    }
}
