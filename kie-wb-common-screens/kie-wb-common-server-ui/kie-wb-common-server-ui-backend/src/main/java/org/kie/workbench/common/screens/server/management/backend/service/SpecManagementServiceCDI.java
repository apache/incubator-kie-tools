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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.server.controller.api.KieServerControllerIllegalArgumentException;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerConfig;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.model.spec.ServerTemplateKeyList;
import org.kie.server.controller.api.model.spec.ServerTemplateList;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;

@Service
@ApplicationScoped
public class SpecManagementServiceCDI implements SpecManagementService {

    @Inject
    @Any
    private org.kie.server.controller.api.service.SpecManagementService service;

    @Override
    public void deleteServerInstance(final ServerInstanceKey serverInstanceKey) {
        service.deleteServerInstance(serverInstanceKey);
    }

    @Override
    public boolean isContainerIdValid(final String serverTemplateId,
                                      final String containerId) {
        if (!isValidIdentifier(containerId)) {
            return false;
        }
        final ServerTemplate template = getServerTemplate(serverTemplateId);
        if (template == null) {
            throw new RuntimeException("Server template doesn't exists");
        }

        return template.getContainerSpec(containerId) == null;
    }

    @Override
    public String validContainerId(final String serverTemplateId,
                                   final String containerId) {

        if (isContainerIdValid(serverTemplateId,
                               containerId)) {
            return containerId;
        }

        return validContainerIdWithSuffix(serverTemplateId,
                                          containerId);
    }

    @Override
    public boolean isNewServerTemplateIdValid(final String serverTemplateId) {
        try {
            ServerTemplate serverTemplate = getServerTemplate(serverTemplateId);
            return serverTemplate == null;
        } catch (KieServerControllerIllegalArgumentException notFoundException) {
            return true;
        }
    }

    private String validContainerIdWithSuffix(final String serverTemplateId,
                                              final String containerId) {
        int attemptNumber = 2;

        while (!isContainerIdValid(serverTemplateId,
                                   containerId + "-" + attemptNumber)) {
            attemptNumber++;
        }

        return containerId + "-" + attemptNumber;
    }

    private boolean isValidIdentifier(final String identifier) {
        return identifier != null && identifier.matches("[A-Za-z0-9_\\-.:]+");
    }

    @Override
    public void saveContainerSpec(final String serverTemplateId,
                                  final ContainerSpec containerSpec) {
        service.saveContainerSpec(serverTemplateId,
                                  containerSpec);
    }

    @Override
    public void saveServerTemplate(final ServerTemplate serverTemplate) {
        service.saveServerTemplate(serverTemplate);
    }

    @Override
    public ServerTemplate getServerTemplate(final String serverTemplateId) {
        return service.getServerTemplate(serverTemplateId);
    }

    @Override
    public ServerTemplateKeyList listServerTemplateKeys() {
        return service.listServerTemplateKeys();
    }

    @Override
    public ServerTemplateList listServerTemplates() {
        return service.listServerTemplates();
    }

    @Override
    public void deleteContainerSpec(final String serverTemplateId,
                                    final String containerSpecId) {
        service.deleteContainerSpec(serverTemplateId,
                                    containerSpecId);
    }

    @Override
    public void deleteServerTemplate(String serverTemplateId) {
        service.deleteServerTemplate(serverTemplateId);
    }

    @Override
    public void copyServerTemplate(final String serverTemplateId,
                                   final String newServerTemplateId,
                                   final String newServerTemplateName) {
        service.copyServerTemplate(serverTemplateId,
                                   newServerTemplateId,
                                   newServerTemplateName);
    }

    @Override
    public void updateContainerConfig(final String serverTemplateId,
                                      final String containerSpecId,
                                      final Capability capability,
                                      final ContainerConfig containerConfig) {
        service.updateContainerConfig(serverTemplateId,
                                      containerSpecId,
                                      capability,
                                      containerConfig);
    }

    @Override
    public void startContainer(final ContainerSpecKey containerSpecKey) {
        service.startContainer(containerSpecKey);
    }

    @Override
    public void stopContainer(final ContainerSpecKey containerSpecKey) {
        service.stopContainer(containerSpecKey);
    }
    
    @Override
    public void activateContainer(final ContainerSpecKey containerSpecKey) {
        service.activateContainer(containerSpecKey);
    }

    @Override
    public void deactivateContainer(final ContainerSpecKey containerSpecKey) {
        service.deactivateContainer(containerSpecKey);
    }
}
