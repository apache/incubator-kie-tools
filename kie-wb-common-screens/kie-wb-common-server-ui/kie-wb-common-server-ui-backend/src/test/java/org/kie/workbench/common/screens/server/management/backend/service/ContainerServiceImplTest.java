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
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.QueryServicesClient;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;
import org.kie.server.controller.api.service.SpecManagementService;
import org.kie.server.controller.impl.KieServerInstanceManager;
import org.kie.workbench.common.screens.server.management.service.ContainerService;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ContainerServiceImplTest {

    @Mock
    SpecManagementService specManagementService ;

    @Mock
    KieServerInstanceManager kieServerInstanceManager;

    private ContainerService containerService;

    @Before
    public void init() {
        containerService = spy(new ContainerServiceImpl());
        ((ContainerServiceImpl) containerService).setSpecManagementService(specManagementService);
        ((ContainerServiceImpl) containerService).setKieServerInstanceManager(kieServerInstanceManager);
    }

    @Test
    public void testIsRunningContainer() {
        ServerTemplate serverTemplate = mock(ServerTemplate.class);
        doReturn(serverTemplate).when(specManagementService).getServerTemplate(any());
        when(serverTemplate.getServerInstanceKeys()).thenReturn(Arrays.asList(new ServerInstanceKey("test", "test", "test", "test")));

        QueryServicesClient queryServicesClient = mock(QueryServicesClient.class);
        KieServicesClient client = mock(KieServicesClient.class);
        when(kieServerInstanceManager.getClient(any())).thenReturn(client);
        when(client.getServicesClient(QueryServicesClient.class)).thenReturn(queryServicesClient);
        when(queryServicesClient.findProcessInstancesByContainerId("test", Arrays.asList(0, 1, 4), 0, 100)).thenReturn(Arrays.asList(new ProcessInstance()));
        boolean runningResult = containerService.isRunningContainer(new ContainerSpec("test", "", new ServerTemplateKey("1", "test"), null, null, null));

        assertEquals(true, runningResult);

        when(queryServicesClient.findProcessInstancesByContainerId("test", Arrays.asList(0, 1, 4), 0, 100)).thenReturn(Collections.emptyList());
        boolean result = containerService.isRunningContainer(new ContainerSpec("test", "", new ServerTemplateKey("1", "test"), null, null, null));
        assertEquals(false, result);

        when(queryServicesClient.findProcessesByContainerId("test", 0, 10)).thenReturn(Collections.emptyList());
        assertEquals(false, result);
    }
}
