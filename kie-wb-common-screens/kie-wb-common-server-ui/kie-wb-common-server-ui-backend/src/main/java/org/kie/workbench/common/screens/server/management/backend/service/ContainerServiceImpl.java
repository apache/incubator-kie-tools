/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.server.management.backend.service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.QueryServicesClient;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.impl.KieServerInstanceManager;
import org.kie.server.controller.api.service.SpecManagementService;
import org.kie.workbench.common.screens.server.management.service.ContainerService;

@Service
@ApplicationScoped
public class ContainerServiceImpl implements ContainerService {

    private KieServerInstanceManager kieServerInstanceManager = KieServerInstanceManager.getInstance();

    @Inject
    @Any
    private SpecManagementService specManagementService;

    @Override
    public boolean isRunningContainer(ContainerSpec containerSpec) {
        ServerTemplate serverTemplate = specManagementService.getServerTemplate(containerSpec.getServerTemplateKey().getId());
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        for (ServerInstanceKey serverInstanceKey : serverTemplate.getServerInstanceKeys()) {
            KieServicesClient client = kieServerInstanceManager.getClient(serverInstanceKey.getUrl());

            QueryServicesClient queryServicesClient = client.getServicesClient(QueryServicesClient.class);
            List<ProcessInstance> processInstances = queryServicesClient.findProcessInstancesByContainerId(containerSpec.getId(), Arrays.asList(0, 1, 4), 0, 100);

            if (!processInstances.isEmpty()) {
                atomicBoolean.set(true);
                break;
            }
        }

        return atomicBoolean.get();
    }

    protected void setKieServerInstanceManager(KieServerInstanceManager kieServerInstanceManager) {
        this.kieServerInstanceManager = kieServerInstanceManager;
    }

    protected void setSpecManagementService(SpecManagementService specManagementService) {
        this.specManagementService = specManagementService;
    }
}
