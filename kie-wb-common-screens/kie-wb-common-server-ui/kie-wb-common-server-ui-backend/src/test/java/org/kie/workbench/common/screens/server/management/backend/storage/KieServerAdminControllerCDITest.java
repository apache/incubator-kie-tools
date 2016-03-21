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

package org.kie.workbench.common.screens.server.management.backend.storage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.KieScannerResource;
import org.kie.server.api.model.KieScannerStatus;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.controller.api.KieServerControllerException;
import org.kie.server.controller.api.model.KieServerInstance;
import org.kie.server.controller.api.model.KieServerInstanceInfo;
import org.kie.server.controller.api.model.KieServerSetup;
import org.kie.server.controller.api.model.KieServerStatus;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerConfig;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ProcessConfig;
import org.kie.server.controller.api.model.spec.RuleConfig;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;
import org.kie.server.controller.api.storage.KieServerTemplateStorage;
import org.kie.server.controller.impl.KieServerInstanceManager;
import org.kie.server.controller.impl.storage.InMemoryKieServerTemplateStorage;
import org.kie.workbench.common.screens.server.management.backend.runtime.KieServerAdminControllerCDI;
import org.mockito.Mockito;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class KieServerAdminControllerCDITest {


    private KieServerInstanceManager kieServerInstanceManager;

    private KieServerTemplateStorage templateStorage = InMemoryKieServerTemplateStorage.getInstance();

    private ServerTemplate serverTemplate;
    private Container container;
    private ContainerSpec containerSpec;

    @Before
    public void prepareStorage() {
        // clear the storage to always start fresh
        ((InMemoryKieServerTemplateStorage)templateStorage).clear();

        this.kieServerInstanceManager = Mockito.mock(KieServerInstanceManager.class);

        serverTemplate = new ServerTemplate();

        serverTemplate.setName("test server");
        serverTemplate.setId(UUID.randomUUID().toString());


        Map<Capability, ContainerConfig> configs = new HashMap<Capability, ContainerConfig>();
        RuleConfig ruleConfig = new RuleConfig();
        ruleConfig.setPollInterval(1000l);
        ruleConfig.setScannerStatus(KieScannerStatus.STARTED);

        configs.put(Capability.RULE, ruleConfig);

        ProcessConfig processConfig = new ProcessConfig();
        processConfig.setKBase("defaultKieBase");
        processConfig.setKSession("defaultKieSession");
        processConfig.setMergeMode("MERGE_COLLECTION");
        processConfig.setRuntimeStrategy("PER_PROCESS_INSTANCE");

        configs.put(Capability.PROCESS, processConfig);

        containerSpec = new ContainerSpec();
        containerSpec.setId("test container");
        containerSpec.setServerTemplateKey(new ServerTemplateKey(serverTemplate.getId(), serverTemplate.getName()));
        containerSpec.setReleasedId(new ReleaseId("org.kie", "kie-server-kjar", "1.0"));
        containerSpec.setStatus(KieContainerStatus.STOPPED);
        containerSpec.setConfigs(configs);

        serverTemplate.addContainerSpec(containerSpec);

        container = new Container();
        container.setServerInstanceId(serverTemplate.getId());
        container.setServerTemplateId(serverTemplate.getId());
        container.setResolvedReleasedId(containerSpec.getReleasedId());
        container.setContainerName(containerSpec.getContainerName());
        container.setContainerSpecId(containerSpec.getId());
        container.setUrl("http://fake.server.net/kie-server");

        templateStorage.store(serverTemplate);
    }

    @Test
    public void testStartContainerOverAdminAPI() {
        KieServerAdminControllerCDI adminController = new KieServerAdminControllerCDI();
        adminController.setKieServerInstanceManager(kieServerInstanceManager);
        adminController.setTemplateStorage(templateStorage);

        adminController.startContainer(serverTemplate.getId(), containerSpec.getId());

        verify( kieServerInstanceManager, times(1)).startContainer(any(ServerTemplate.class), any(ContainerSpec.class));
    }

    @Test
    public void testStopContainerOverAdminAPI() {
        KieServerAdminControllerCDI adminController = new KieServerAdminControllerCDI();
        adminController.setKieServerInstanceManager(kieServerInstanceManager);
        adminController.setTemplateStorage(templateStorage);

        adminController.stopContainer(serverTemplate.getId(), containerSpec.getId());

        verify( kieServerInstanceManager, times(1)).stopContainer(any(ServerTemplate.class), any(ContainerSpec.class));
    }

    @Test
    public void testCreateContainerOverAdminAPI() {
        KieServerAdminControllerCDI adminController = new KieServerAdminControllerCDI();
        adminController.setKieServerInstanceManager(kieServerInstanceManager);
        adminController.setTemplateStorage(templateStorage);

        KieContainerResource containerResource = new KieContainerResource();
        containerResource.setContainerId("another container");
        containerResource.setReleaseId(containerSpec.getReleasedId());
        containerResource.setStatus(containerSpec.getStatus());

        adminController.createContainer(serverTemplate.getId(), containerResource.getContainerId(), containerResource);

        verify( kieServerInstanceManager, never()).startContainer(any(ServerTemplate.class), any(ContainerSpec.class));
    }

    @Test
    public void testDeleteContainerOverAdminAPI() {
        KieServerAdminControllerCDI adminController = new KieServerAdminControllerCDI();
        adminController.setKieServerInstanceManager(kieServerInstanceManager);
        adminController.setTemplateStorage(templateStorage);

        adminController.deleteContainer(serverTemplate.getId(), containerSpec.getId());

        verify( kieServerInstanceManager, times(1)).stopContainer(any(ServerTemplate.class), any(ContainerSpec.class));
    }

    @Test
    public void testStartNonExistingContainerOverAdminAPI() {
        KieServerAdminControllerCDI adminController = new KieServerAdminControllerCDI();
        adminController.setKieServerInstanceManager(kieServerInstanceManager);
        adminController.setTemplateStorage(templateStorage);

        String containerId = "not existing";
        try {
            adminController.startContainer(serverTemplate.getId(), containerId);
            fail("There is no such container " + containerId);
        } catch (KieServerControllerException e) {

        }
        verify( kieServerInstanceManager, never()).startContainer(any(ServerTemplate.class), any(ContainerSpec.class));
    }

    @Test
    public void testStopNonExistingContainerOverAdminAPI() {
        KieServerAdminControllerCDI adminController = new KieServerAdminControllerCDI();
        adminController.setKieServerInstanceManager(kieServerInstanceManager);
        adminController.setTemplateStorage(templateStorage);

        String containerId = "not existing";
        try {
            adminController.startContainer(serverTemplate.getId(), containerId);
            fail("There is no such container " + containerId);
        } catch (KieServerControllerException e) {

        }
        verify( kieServerInstanceManager, never()).startContainer(any(ServerTemplate.class), any(ContainerSpec.class));
    }

    protected KieServerInstance forServerTemplate(ServerTemplate serverTemplate) {
        KieServerInstance kieServerInstance = new KieServerInstance();
        kieServerInstance.setIdentifier(serverTemplate.getId());
        kieServerInstance.setVersion("");
        kieServerInstance.setName(serverTemplate.getName());
        kieServerInstance.setKieServerSetup(new KieServerSetup());
        kieServerInstance.setStatus(KieServerStatus.DOWN);
        kieServerInstance.setManagedInstances(new HashSet<KieServerInstanceInfo>());

        if (serverTemplate.getServerInstanceKeys() != null) {
            for (ServerInstanceKey instanceKey : serverTemplate.getServerInstanceKeys()) {
                KieServerInstanceInfo instanceInfo = new KieServerInstanceInfo(instanceKey.getUrl(), KieServerStatus.UP, serverTemplate.getCapabilities());

                kieServerInstance.getManagedInstances().add(instanceInfo);
            }
        }

        return kieServerInstance;
    }

    protected KieContainerResource forContainerSpec(ContainerSpec containerSpec) {
        KieContainerResource containerResource = new KieContainerResource();
        containerResource.setContainerId(containerSpec.getId());
        containerResource.setReleaseId(containerSpec.getReleasedId());
        containerResource.setStatus(containerSpec.getStatus());

        // cover scanner and rules config
        ContainerConfig containerConfig = containerSpec.getConfigs().get(Capability.RULE);
        if (containerConfig != null) {
            RuleConfig ruleConfig = (RuleConfig) containerConfig;

            KieScannerResource scannerResource = new KieScannerResource();
            scannerResource.setPollInterval(ruleConfig.getPollInterval());
            scannerResource.setStatus(ruleConfig.getScannerStatus());

            containerResource.setScanner(scannerResource);
        }


        return containerResource;
    }
}
