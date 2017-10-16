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
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.server.controller.api.model.events.ServerInstanceDeleted;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.impl.KieServerInstanceManager;
import org.kie.server.controller.rest.RestSpecManagementServiceImpl;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Service
@ApplicationScoped
public class SpecManagementServiceCDI extends RestSpecManagementServiceImpl implements SpecManagementService {

    @Inject
    @Override
    public void setKieServerInstanceManager(KieServerInstanceManager kieServerInstanceManager) {
        super.setKieServerInstanceManager(kieServerInstanceManager);
    }

    @Inject
    @Override
    public void setNotificationService(org.kie.server.controller.api.service.NotificationService notificationService) {
        super.setNotificationService(notificationService);
    }

    @Inject
    @Override
    public void setTemplateStorage(org.kie.server.controller.api.storage.KieServerTemplateStorage templateStorage) {
        super.setTemplateStorage(templateStorage);
    }

    @Override
    public void deleteServerInstance(final ServerInstanceKey serverInstanceKey) {
        checkNotNull("serverInstanceKey",
                     serverInstanceKey);
        if (getKieServerInstanceManager().isAlive(serverInstanceKey)) {
            throw new RuntimeException("Can't delete live instance.");
        } else {
            final String serverInstanceId = serverInstanceKey.getServerInstanceId();
            final ServerTemplate serverTemplate = getServerTemplate(serverInstanceKey.getServerTemplateId());
            if (serverTemplate != null) {
                serverTemplate.deleteServerInstance(serverInstanceId);
                getTemplateStorage().update(serverTemplate);
                getNotificationService().notify(new ServerInstanceDeleted(serverInstanceId));
            }
        }
    }

    @Override
    public boolean isContainerIdValid(String serverTemplateId,
                                      String containerId) {
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
    public boolean isNewServerTemplateIdValid(String serverTemplateId) {
        return !getTemplateStorage().exists(serverTemplateId);
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
}
