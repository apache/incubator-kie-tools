/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.server.management.backend.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.runtime.ContainerList;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.workbench.common.screens.server.management.model.ContainerSpecData;
import org.kie.workbench.common.screens.server.management.service.RuntimeManagementService;

@Service
@ApplicationScoped
public class RuntimeManagementServiceCDI implements RuntimeManagementService {

    @Inject
    @Any
    private org.kie.server.controller.api.service.SpecManagementService specManagementService;

    @Inject
    @Any
    private org.kie.server.controller.api.service.RuntimeManagementService service;

    @Override
    public Collection<Container> getContainersByServerInstance(final String serverTemplateId,
                                                               final String serverInstanceId) {
        final ServerTemplate serverTemplate = specManagementService.getServerTemplate(serverTemplateId);
        if (serverTemplate == null) {
            throw new RuntimeException("Server template with id " + serverTemplateId + " not found");
        }
        return serverTemplate.getServerInstanceKeys().stream()
                .filter(serverInstanceKey -> serverInstanceKey.getServerInstanceId().equalsIgnoreCase(serverInstanceId))
                .findFirst()
                .map(serverInstanceKey -> Arrays.asList(service.getContainers(serverInstanceKey).getContainers()))
                .orElse(Collections.emptyList());
    }

    @Override
    public ContainerSpecData getContainersByContainerSpec(final String serverTemplateId,
                                                          final String containerSpecId) {
        final ServerTemplate serverTemplate = specManagementService.getServerTemplate(serverTemplateId);
        if (serverTemplate == null) {
            throw new RuntimeException("Server template with id " + serverTemplateId + " not found");
        }
        final ContainerSpec containerSpec = serverTemplate.getContainerSpec(containerSpecId);
        if (containerSpec == null) {
            throw new RuntimeException("Container with id " + containerSpecId + " not found");
        }
        final ContainerList containerList = service.getContainers(serverTemplate,
                                                                  containerSpec);
        final List<Container> containers = Arrays.asList(containerList.getContainers());
        return new ContainerSpecData(containerSpec,
                                     containers);
    }
}