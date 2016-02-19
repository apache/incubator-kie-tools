/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.server.management.backend.storage;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.KieScannerStatus;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.controller.api.ModelFactory;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerConfig;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ProcessConfig;
import org.kie.server.controller.api.model.spec.RuleConfig;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;
import org.uberfire.mocks.FileSystemTestingUtils;

import static org.junit.Assert.*;

public class ServerTemplateVFSStorageTest {

    private FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();
    private ServerTemplateVFSStorage templateStorage;

    private ServerTemplate serverTemplate;
    private Container container;
    private ContainerSpec containerSpec;
    private RuleConfig ruleConfig;
    private ProcessConfig processConfig;

    @Before
    public void setup() throws IOException {
        fileSystemTestingUtils.setup();
        templateStorage = new ServerTemplateVFSStorage(fileSystemTestingUtils.getIoService(), fileSystemTestingUtils.getFileSystem());

        createServerTemplateWithContainer();
    }

    @After
    public void cleanup() {
        fileSystemTestingUtils.cleanup();
    }

    @Test
    public void testStoreServerTemplate() {

        templateStorage.store(serverTemplate);

        boolean exists = templateStorage.exists(serverTemplate.getId());
        assertTrue(exists);

        ServerTemplate fromStorage = templateStorage.load(serverTemplate.getId());
        assertNotNull(fromStorage);

        assertEquals(serverTemplate.getId(), fromStorage.getId());
        assertEquals(serverTemplate.getName(), fromStorage.getName());

        Collection<ServerInstanceKey> instances = fromStorage.getServerInstanceKeys();
        assertNotNull(instances);
        assertEquals(0, instances.size());

        Collection<ContainerSpec> containerSpecs = fromStorage.getContainersSpec();
        assertNotNull(containerSpecs);
        assertEquals(1, containerSpecs.size());

        ContainerSpec spec = containerSpecs.iterator().next();
        assertNotNull(spec);

        assertEquals(containerSpec.getId(), spec.getId());
        assertEquals(containerSpec.getReleasedId(), spec.getReleasedId());
        assertEquals(containerSpec.getServerTemplateKey().getId(), spec.getServerTemplateKey().getId());
        assertEquals(containerSpec.getServerTemplateKey().getName(), spec.getServerTemplateKey().getName());
        assertEquals(containerSpec.getConfigs().size(), spec.getConfigs().size());

        assertTrue(spec.getConfigs().containsKey(Capability.RULE));
        assertTrue(spec.getConfigs().containsKey(Capability.PROCESS));

        RuleConfig ruleConfig = (RuleConfig) spec.getConfigs().get(Capability.RULE);
        assertNotNull(ruleConfig);
        assertEquals(this.ruleConfig.getPollInterval(), ruleConfig.getPollInterval());
        assertEquals(this.ruleConfig.getScannerStatus(), ruleConfig.getScannerStatus());

        ProcessConfig processConfig = (ProcessConfig) spec.getConfigs().get(Capability.PROCESS);
        assertNotNull(processConfig);
        assertEquals(this.processConfig.getKBase(), processConfig.getKBase());
        assertEquals(this.processConfig.getKSession(), processConfig.getKSession());
        assertEquals(this.processConfig.getMergeMode(), processConfig.getMergeMode());
        assertEquals(this.processConfig.getRuntimeStrategy(), processConfig.getRuntimeStrategy());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStoreServerTemplateDuplicate() {

        templateStorage.store(serverTemplate);

        boolean exists = templateStorage.exists(serverTemplate.getId());
        assertTrue(exists);

        templateStorage.store(serverTemplate);
        fail("Duplicated server templates are not allowed, something is wrong...");
    }

    @Test
    public void testStoreAndLoadServerTemplates() {

        templateStorage.store(serverTemplate);

        Collection<ServerTemplateKey> templateKeys = templateStorage.loadKeys();
        assertNotNull(templateKeys);
        assertEquals(1, templateKeys.size());

        ServerTemplateKey templateKey = templateKeys.iterator().next();
        assertNotNull(templateKey);

        assertEquals(serverTemplate.getId(), templateKey.getId());
        assertEquals(serverTemplate.getName(), templateKey.getName());

        Collection<ServerTemplate> templates = templateStorage.load();

        assertNotNull(templates);
        assertEquals(1, templates.size());

        ServerTemplate template = templates.iterator().next();
        assertNotNull(template);

        assertEquals(serverTemplate.getId(), template.getId());
        assertEquals(serverTemplate.getName(), template.getName());

        Collection<ServerInstanceKey> instances = template.getServerInstanceKeys();
        assertNotNull(instances);
        assertEquals(0, instances.size());

        Collection<ContainerSpec> containerSpecs = template.getContainersSpec();
        assertNotNull(containerSpecs);
        assertEquals(1, containerSpecs.size());

        ContainerSpec spec = containerSpecs.iterator().next();
        assertNotNull(spec);

        assertEquals(containerSpec.getId(), spec.getId());
        assertEquals(containerSpec.getReleasedId(), spec.getReleasedId());
        assertEquals(containerSpec.getServerTemplateKey().getId(), spec.getServerTemplateKey().getId());
        assertEquals(containerSpec.getServerTemplateKey().getName(), spec.getServerTemplateKey().getName());
        assertEquals(containerSpec.getConfigs().size(), spec.getConfigs().size());

        assertTrue(spec.getConfigs().containsKey(Capability.RULE));
        assertTrue(spec.getConfigs().containsKey(Capability.PROCESS));

        RuleConfig ruleConfig = (RuleConfig) spec.getConfigs().get(Capability.RULE);
        assertNotNull(ruleConfig);
        assertEquals(this.ruleConfig.getPollInterval(), ruleConfig.getPollInterval());
        assertEquals(this.ruleConfig.getScannerStatus(), ruleConfig.getScannerStatus());

        ProcessConfig processConfig = (ProcessConfig) spec.getConfigs().get(Capability.PROCESS);
        assertNotNull(processConfig);
        assertEquals(this.processConfig.getKBase(), processConfig.getKBase());
        assertEquals(this.processConfig.getKSession(), processConfig.getKSession());
        assertEquals(this.processConfig.getMergeMode(), processConfig.getMergeMode());
        assertEquals(this.processConfig.getRuntimeStrategy(), processConfig.getRuntimeStrategy());

    }

    @Test
    public void testEmptyLoadServerTemplates() {
        Collection<ServerTemplateKey> templateKeys = templateStorage.loadKeys();
        assertNotNull(templateKeys);
        assertEquals(0, templateKeys.size());

        Collection<ServerTemplate> templates = templateStorage.load();

        assertNotNull(templates);
        assertEquals(0, templates.size());
    }

    @Test
    public void testEmptyLoadServerTemplate() {
        ServerTemplate template = templateStorage.load("not existing");
        assertNull(template);
    }

    @Test
    public void testStoreLoadAndDeleteServerTemplate() {

        templateStorage.store(serverTemplate);

        Collection<ServerTemplateKey> templateKeys = templateStorage.loadKeys();
        assertNotNull(templateKeys);
        assertEquals(1, templateKeys.size());

        templateStorage.delete(serverTemplate.getId());

        boolean exists = templateStorage.exists(serverTemplate.getId());
        assertFalse(exists);

        ServerTemplate template = templateStorage.load("not existing");
        assertNull(template);

        templateKeys = templateStorage.loadKeys();
        assertNotNull(templateKeys);
        assertEquals(0, templateKeys.size());
    }

    @Test
    public void testStoreLoadAndUpdateServerTemplate() {

        templateStorage.store(serverTemplate);
        ServerTemplate fromStorage = templateStorage.load(serverTemplate.getId());
        assertNotNull(fromStorage);

        assertEquals(serverTemplate.getId(), fromStorage.getId());
        assertEquals(serverTemplate.getName(), fromStorage.getName());
        // let's add new container
        ContainerSpec newContainerSpec = new ContainerSpec();
        newContainerSpec.setId("test container 2");
        newContainerSpec.setServerTemplateKey(new ServerTemplateKey(serverTemplate.getId(), serverTemplate.getName()));
        newContainerSpec.setReleasedId(new ReleaseId("org.kie", "kie-server-kjar", "3.0"));
        newContainerSpec.setStatus(KieContainerStatus.STARTED);

        fromStorage.addContainerSpec(newContainerSpec);

        // now let's add server instance
        fromStorage.addServerInstance(ModelFactory.newServerInstanceKey(serverTemplate.getId(), "http://localhost:8080/server"));

        templateStorage.update(fromStorage);

        ServerTemplate template = templateStorage.load(serverTemplate.getId());
        assertNotNull(template);

        assertEquals(serverTemplate.getId(), template.getId());
        assertEquals(serverTemplate.getName(), template.getName());

        Collection<ServerInstanceKey> instances = template.getServerInstanceKeys();
        assertNotNull(instances);
        assertEquals(1, instances.size());

        ServerInstanceKey serverInstanceKey = instances.iterator().next();
        assertNotNull(serverInstanceKey);
        assertEquals(serverTemplate.getId(), serverInstanceKey.getServerTemplateId());
        assertEquals(serverTemplate.getId()+"@localhost:8080", serverInstanceKey.getServerName());
        assertEquals(serverTemplate.getId() + "@localhost:8080", serverInstanceKey.getServerInstanceId());
        assertEquals("http://localhost:8080/server", serverInstanceKey.getUrl());

        Collection<ContainerSpec> containerSpecs = template.getContainersSpec();
        assertNotNull(containerSpecs);
        assertEquals(2, containerSpecs.size());

        Iterator<ContainerSpec> iterator = containerSpecs.iterator();

        // first container spec...
        ContainerSpec spec = iterator.next();
        assertNotNull(spec);

        assertEquals(containerSpec.getId(), spec.getId());
        assertEquals(containerSpec.getReleasedId(), spec.getReleasedId());
        assertEquals(containerSpec.getServerTemplateKey().getId(), spec.getServerTemplateKey().getId());
        assertEquals(containerSpec.getServerTemplateKey().getName(), spec.getServerTemplateKey().getName());
        assertEquals(containerSpec.getConfigs().size(), spec.getConfigs().size());

        assertTrue(spec.getConfigs().containsKey(Capability.RULE));
        assertTrue(spec.getConfigs().containsKey(Capability.PROCESS));

        RuleConfig ruleConfig = (RuleConfig) spec.getConfigs().get(Capability.RULE);
        assertNotNull(ruleConfig);
        assertEquals(this.ruleConfig.getPollInterval(), ruleConfig.getPollInterval());
        assertEquals(this.ruleConfig.getScannerStatus(), ruleConfig.getScannerStatus());

        ProcessConfig processConfig = (ProcessConfig) spec.getConfigs().get(Capability.PROCESS);
        assertNotNull(processConfig);
        assertEquals(this.processConfig.getKBase(), processConfig.getKBase());
        assertEquals(this.processConfig.getKSession(), processConfig.getKSession());
        assertEquals(this.processConfig.getMergeMode(), processConfig.getMergeMode());
        assertEquals(this.processConfig.getRuntimeStrategy(), processConfig.getRuntimeStrategy());

        // second container spec
        spec = iterator.next();
        assertNotNull(spec);

        assertEquals(newContainerSpec.getId(), spec.getId());
        assertEquals(newContainerSpec.getReleasedId(), spec.getReleasedId());
        assertEquals(newContainerSpec.getServerTemplateKey().getId(), spec.getServerTemplateKey().getId());
        assertEquals(newContainerSpec.getServerTemplateKey().getName(), spec.getServerTemplateKey().getName());
        assertEquals(newContainerSpec.getConfigs().size(), spec.getConfigs().size());
    }

    /*
     * helper method to setup template container and container spec
     */
    protected void createServerTemplateWithContainer() {
        serverTemplate = new ServerTemplate();

        serverTemplate.setName("test server");
        serverTemplate.setId(UUID.randomUUID().toString());


        Map<Capability, ContainerConfig> configs = new HashMap<Capability, ContainerConfig>();
        ruleConfig = new RuleConfig();
        ruleConfig.setPollInterval(1000l);
        ruleConfig.setScannerStatus(KieScannerStatus.STARTED);

        configs.put(Capability.RULE, ruleConfig);

        processConfig = new ProcessConfig();
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

    }
}
