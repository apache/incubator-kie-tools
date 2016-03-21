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

package org.kie.workbench.common.screens.server.management.backend.runtime;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.server.api.model.KieContainerResource;
import org.kie.server.controller.api.KieServerControllerException;
import org.kie.server.controller.api.model.KieServerInstance;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.storage.KieServerTemplateStorage;
import org.kie.server.controller.impl.KieServerInstanceManager;
import org.kie.server.controller.rest.RestKieServerControllerAdminImpl;

@ApplicationScoped
public class KieServerAdminControllerCDI extends RestKieServerControllerAdminImpl {

    private KieServerInstanceManager kieServerInstanceManager = KieServerInstanceManager.getInstance();

    @Override
    public void notifyKieServersOnCreateContainer(KieServerInstance kieServerInstance, KieContainerResource container) {
        ServerTemplate serverTemplate = getTemplateStorage().load(kieServerInstance.getIdentifier());
        if (serverTemplate == null) {
            throw new KieServerControllerException("KieServerInstance not found with id: " + kieServerInstance.getIdentifier());
        }

        ContainerSpec containerSpec = serverTemplate.getContainerSpec(container.getContainerId());
        if (containerSpec == null) {
            throw new KieServerControllerException("Container not found with id: " + container.getContainerId() + " within kie server with id " + kieServerInstance.getIdentifier());
        }

        kieServerInstanceManager.startContainer(serverTemplate, containerSpec);
    }

    @Override
    public void notifyKieServersOnDeleteContainer(KieServerInstance kieServerInstance, String containerId) {
        ServerTemplate serverTemplate = getTemplateStorage().load(kieServerInstance.getIdentifier());
        if (serverTemplate == null) {
            throw new KieServerControllerException("KieServerInstance not found with id: " + kieServerInstance.getIdentifier());
        }
        ContainerSpec containerSpec = serverTemplate.getContainerSpec(containerId);
        if (containerSpec == null) {
            // since the container was removed create it with id only
            containerSpec = new ContainerSpec(containerId, containerId, null, null, null, null);
        }
        kieServerInstanceManager.stopContainer(serverTemplate, containerSpec);
    }

    @Inject
    @Override
    public void setTemplateStorage(KieServerTemplateStorage templateStorage) {
        super.setTemplateStorage(templateStorage);
    }

    public void setKieServerInstanceManager(KieServerInstanceManager kieServerInstanceManager) {
        this.kieServerInstanceManager = kieServerInstanceManager;
    }
}
