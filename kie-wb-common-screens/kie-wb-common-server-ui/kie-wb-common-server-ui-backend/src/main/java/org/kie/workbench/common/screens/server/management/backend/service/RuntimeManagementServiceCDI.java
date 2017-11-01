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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.storage.KieServerTemplateStorage;
import org.kie.server.controller.impl.KieServerInstanceManager;
import org.kie.server.controller.impl.service.RuntimeManagementServiceImpl;
import org.kie.workbench.common.screens.server.management.model.ContainerSpecData;
import org.kie.workbench.common.screens.server.management.service.RuntimeManagementService;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;

@Service
@ApplicationScoped
public class RuntimeManagementServiceCDI extends RuntimeManagementServiceImpl
        implements RuntimeManagementService {

    @Inject
    private SpecManagementService specManagementService;

    @Inject
    @Override
    public void setKieServerInstanceManager(KieServerInstanceManager kieServerInstanceManager) {
        super.setKieServerInstanceManager(kieServerInstanceManager);
    }

    @Inject
    @Override
    public void setTemplateStorage(KieServerTemplateStorage templateStorage) {
        super.setTemplateStorage(templateStorage);
    }

    @Override
    public Collection<Container> getContainersByServerInstance(final String serverTemplateId, final String serverInstanceId) {
        final ServerTemplate serverTemplate = loadServerTemplate(serverTemplateId);
        return serverTemplate.getServerInstanceKeys().stream()
                .filter(serverInstanceKey -> serverInstanceKey.getServerInstanceId().equalsIgnoreCase(serverInstanceId))
                .findFirst()
                .map(this::getContainers)
                .orElse(Collections.emptyList());
    }

    @Override
    public ContainerSpecData getContainersByContainerSpec(final String serverTemplateId, final String containerSpecId) {
        final ServerTemplate serverTemplate = loadServerTemplate(serverTemplateId);

        final ContainerSpec containerSpec = serverTemplate.getContainerSpec(containerSpecId);

        final List<Container> containers = getKieServerInstanceManager().getContainers(serverTemplate, containerSpec);

        return new ContainerSpecData(containerSpec, containers);
    }

    private ServerTemplate loadServerTemplate(String serverTemplateId) {
        final ServerTemplate template = getTemplateStorage().load(serverTemplateId);
        if (template == null) {
            throw new RuntimeException("No server template found for id " + serverTemplateId);
        }
        return template;
    }
}