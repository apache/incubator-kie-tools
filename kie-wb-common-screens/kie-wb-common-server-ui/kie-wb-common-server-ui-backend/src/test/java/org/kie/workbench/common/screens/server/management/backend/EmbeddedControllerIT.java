/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.server.management.backend;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.controller.api.model.runtime.ContainerList;
import org.kie.server.controller.api.model.runtime.ServerInstanceKeyList;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.model.spec.ServerTemplateList;
import org.kie.server.controller.client.KieServerControllerClient;
import org.kie.server.controller.client.KieServerControllerClientFactory;
import org.kie.server.controller.client.event.EventHandler;
import org.kie.server.integrationtests.shared.KieServerDeployer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(Arquillian.class)
public class EmbeddedControllerIT extends AbstractControllerIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedControllerIT.class);

    protected static final ReleaseId RELEASE_ID = new ReleaseId("org.kie.server.testing",
                                                                "stateless-session-kjar",
                                                                "1.0.0");
    protected static final String CONTAINER_ID = "kie-concurrent";
    protected static final String CONTAINER_NAME = "containerName";

    @Deployment(name = "workbench", order = 1)
    public static WebArchive createEmbeddedControllerWarDeployment() {
        return createWorkbenchWar();
    }

    @Deployment(name = "kie-server", order = 2, testable = false)
    public static WebArchive createKieServerWarDeployment() {
        return createKieServerWar();
    }

    @BeforeClass
    public static void setup() {
        KieServerDeployer.createAndDeployKJar(RELEASE_ID);
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("workbench")
    public void testEmbeddedEndpointsUsingRest(final @ArquillianResource URL baseURL) {
        client = KieServerControllerClientFactory.newRestClient(getRestURL(baseURL),
                                                                USER,
                                                                PASSWORD);
        testEmbeddedEndpoints(client);
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("workbench")
    public void testEmbeddedEndpointsUsingWebSocket(final @ArquillianResource URL baseURL) {
        client = KieServerControllerClientFactory.newWebSocketClient(getWebSocketUrl(baseURL),
                                                                     USER,
                                                                     PASSWORD);
        testEmbeddedEndpoints(client);
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("workbench")
    public void testEmbeddedNotificationsUsingWebSocket(final @ArquillianResource URL baseURL) {
        EventHandler eventHandler = mock(EventHandler.class);
        client = KieServerControllerClientFactory.newWebSocketClient(getWebSocketUrl(baseURL),
                                                                     USER,
                                                                     PASSWORD,
                                                                     eventHandler);
        runAsync(() -> {
            // Check that there are no kie servers deployed in controller.
            ServerTemplateList instanceList = client.listServerTemplates();
            assertServerTemplateList(instanceList);

            // Create new server template
            ServerTemplate template = new ServerTemplate("notification-int-test",
                                                         "Notification Test Server");
            client.saveServerTemplate(template);

            // Check that kie server is registered in controller.
            instanceList = client.listServerTemplates();
            assertNotNull(instanceList);
            assertEquals(2,
                         instanceList.getServerTemplates().length);

            // Delete server template
            client.deleteServerTemplate(template.getId());
            instanceList = client.listServerTemplates();
            assertServerTemplateList(instanceList);
        });

        verify(eventHandler,
               timeout(2000L)).onServerTemplateUpdated(any());
        verify(eventHandler,
               timeout(2000L)).onServerTemplateDeleted(any());

        verifyNoMoreInteractions(eventHandler);
    }

    protected void runAsync(final Runnable runnable) {
        final ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(runnable);
        service.shutdown();
    }

    protected void testEmbeddedEndpoints(final KieServerControllerClient client) {
        try {
            final ServerTemplateList serverTemplateList = client.listServerTemplates();
            assertServerTemplateList(serverTemplateList);

            final ServerInstanceKeyList instances = client.getServerInstances(serverTemplateList.getServerTemplates()[0].getId());
            assertNotNull(instances);
            assertEquals(1, instances.getServerInstanceKeys().length);
            final ServerTemplate serverTemplate = serverTemplateList.getServerTemplates()[0];

            // Deploy container for kie server instance.
            ContainerSpec containerToDeploy = new ContainerSpec(CONTAINER_ID,
                                                                CONTAINER_NAME,
                                                                serverTemplate,
                                                                RELEASE_ID,
                                                                KieContainerStatus.STARTED,
                                                                new HashMap());
            client.saveContainerSpec(serverTemplate.getId(),
                                                    containerToDeploy);

            ContainerList containers = client.getContainers(instances.getServerInstanceKeys()[0]);
            assertNotNull(containers);
            assertEquals(1,
                         containers.getContainers().length);

            client.deleteContainerSpec(serverTemplate.getId(), containerToDeploy.getId());
            containers = client.getContainers(instances.getServerInstanceKeys()[0]);
            assertNotNull(containers);
            assertEquals(0,
                         containers.getContainers().length);
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (IOException ex) {
                    LOGGER.warn("Error trying to close client connection: {}",
                                ex.getMessage(),
                                ex);
                }
            }
        }
    }
}