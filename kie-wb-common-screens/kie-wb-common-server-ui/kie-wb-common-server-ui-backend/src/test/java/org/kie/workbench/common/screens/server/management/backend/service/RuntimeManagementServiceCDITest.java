/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.server.management.backend.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;
import org.kie.server.controller.api.storage.KieServerTemplateStorage;
import org.kie.server.controller.impl.KieServerInstanceManager;
import org.kie.server.controller.impl.service.RuntimeManagementServiceImpl;
import org.kie.server.controller.impl.service.SpecManagementServiceImpl;
import org.kie.workbench.common.screens.server.management.model.ContainerSpecData;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RuntimeManagementServiceCDITest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Spy
    SpecManagementServiceImpl specManagementService = new SpecManagementServiceImpl();

    @Spy
    RuntimeManagementServiceImpl runtimeManagementService = new RuntimeManagementServiceImpl();

    @InjectMocks
    RuntimeManagementServiceCDI runtimeManagementServiceCDI;

    @Test
    public void getContainersByServerInstance_throwsRuntimeException_whenServerTemplateNotFound() {
        final String templateId = "this_template_does_NOT_exist";

        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(containsString("No server template found for id " + templateId));

        //Tested method
        runtimeManagementServiceCDI.getContainersByServerInstance(templateId, "dummy_container_spec_id");
    }

    @Test
    public void getContainersByServerInstance_returnsEmptyList_whenInstanceWithIdDoesntExistInTemplate() {
        final String templateId = "templateId";
        final String serverInstanceId = "serverInstanceId";

        KieServerTemplateStorage templateStorageMock = mock(KieServerTemplateStorage.class);
        when(templateStorageMock.load(eq(templateId)))
                .thenReturn(new ServerTemplate(null, null, Collections.emptyList(), Collections.emptyMap(), Collections.emptyList(), Collections.emptyList()));

        // Setup tested service
        runtimeManagementService.setTemplateStorage(templateStorageMock);
        specManagementService.setTemplateStorage(templateStorageMock);

        // Tested method
        Collection<Container> containers = runtimeManagementServiceCDI.getContainersByServerInstance(templateId, serverInstanceId);
        assertThat(containers)
                .as("List of containers should be empty, when Server template doesn't contain server instance id")
                .isEmpty();
    }

    @Test
    public void getContainersByServerInstance_returnsListOfContainers_whenInstanceWithIdExists() {
        final String templateId = "templateId";
        final String templateName = "templateName";
        final String serverInstanceId = "serverInstanceId";
        ServerInstanceKey serverInstanceKey = new ServerInstanceKey(templateId, templateName, serverInstanceId, "dummyUrl");
        ServerTemplate serverTemplate = new ServerTemplate(
                templateId,
                templateName,
                Collections.emptyList(),
                Collections.emptyMap(),
                Collections.emptyList(),
                Collections.singletonList(serverInstanceKey)
        );
        final List<Container> containersInServerInstance = Arrays.asList(new Container(), new Container());

        KieServerTemplateStorage templateStorageMock = createMockStorageWithOneTemplate(serverTemplate);

        // Instance with 2 dummy containers
        KieServerInstanceManager instanceMangerMock = mock(KieServerInstanceManager.class);
        when(instanceMangerMock.getContainers(eq(serverInstanceKey)))
                .thenReturn(containersInServerInstance);

        runtimeManagementService.setTemplateStorage(templateStorageMock);
        runtimeManagementService.setKieServerInstanceManager(instanceMangerMock);
        specManagementService.setTemplateStorage(templateStorageMock);
        specManagementService.setKieServerInstanceManager(instanceMangerMock);

        Collection<Container> containers = runtimeManagementServiceCDI.getContainersByServerInstance(templateId, serverInstanceId);

        assertThat(containers)
                .as("Should return list of containers from server instance id")
                .hasSameSizeAs(containersInServerInstance);
    }

    @Test
    public void getContainersByContainerSpec_throwsRuntimeException_whenServerTemplateNotFound() {
        final String templateId = "this_template_does_NOT_exist";

        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(containsString("No server template found for id " + templateId));

        runtimeManagementServiceCDI.getContainersByContainerSpec(templateId, "dummy_container_spec_id");
    }

    @Test
    public void getContainersByContainerSpec_returnsContainerSpecData() {
        final String
                templateId = "templateId",
                templateName = "templateName",
                serverInstanceId = "serverInstanceId",
                containerName = "containerName1",
                group = "g1",
                artifact = "a1",
                version = "1",
                containerSpecId = String.join(":", group, artifact, version);

        final ReleaseId releaseId = new ReleaseId(group, artifact, version);

        ServerInstanceKey serverInstanceKey = new ServerInstanceKey(templateId, null, serverInstanceId, null);

        Container container = new Container(containerSpecId, containerName, serverInstanceKey, Collections.emptyList(), releaseId, null);

        ContainerSpec containerSpec = new ContainerSpec(
                containerSpecId,
                containerName,
                new ServerTemplateKey(templateId, templateName),
                releaseId,
                KieContainerStatus.STARTED,
                Collections.emptyMap()
        );

        ServerTemplate serverTemplate = new ServerTemplate(
                templateId,
                templateName,
                Collections.emptyList(),
                Collections.emptyMap(),
                Collections.singletonList(containerSpec),
                Collections.singletonList(serverInstanceKey)
        );

        final List<Container> containersInServerInstance = Collections.singletonList(container);

        // Setup mocks
        KieServerTemplateStorage templateStorageMock = createMockStorageWithOneTemplate(serverTemplate);

        KieServerInstanceManager instanceMangerMock = mock(KieServerInstanceManager.class);
        when(instanceMangerMock.getContainers(serverTemplate, containerSpec))
                .thenReturn(containersInServerInstance);

        // Setup tested object
        runtimeManagementService.setTemplateStorage(templateStorageMock);
        runtimeManagementService.setKieServerInstanceManager(instanceMangerMock);
        specManagementService.setTemplateStorage(templateStorageMock);
        specManagementService.setKieServerInstanceManager(instanceMangerMock);

        //Tested method
        ContainerSpecData containerSpecData = runtimeManagementServiceCDI.getContainersByContainerSpec(templateId, containerSpecId);

        assertThat(containerSpecData.getContainers()).contains(container);
        assertThat(containerSpecData.getContainerSpec()).isEqualTo(containerSpec);
    }

    private KieServerTemplateStorage createMockStorageWithOneTemplate(ServerTemplate serverTemplate) {
        KieServerTemplateStorage templateStorageMock = mock(KieServerTemplateStorage.class);
        when(templateStorageMock.load(eq(serverTemplate.getId())))
                .thenReturn(serverTemplate);
        return templateStorageMock;
    }
}
