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

import java.net.URI;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.client.jaxrs.BasicAuthentication;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.controller.api.model.events.ContainerSpecUpdated;
import org.kie.server.controller.api.model.events.ServerInstanceConnected;
import org.kie.server.controller.api.model.events.ServerInstanceDeleted;
import org.kie.server.controller.api.model.events.ServerInstanceDisconnected;
import org.kie.server.controller.api.model.events.ServerInstanceUpdated;
import org.kie.server.controller.api.model.events.ServerTemplateDeleted;
import org.kie.server.controller.api.model.events.ServerTemplateUpdated;
import org.kie.server.controller.api.model.runtime.ServerInstanceKeyList;
import org.kie.server.controller.client.KieServerControllerClientFactory;
import org.kie.server.controller.client.event.EventHandler;
import org.kie.server.controller.client.websocket.WebSocketKieServerControllerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class StandaloneControllerMultinodeIT extends AbstractControllerIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(StandaloneControllerMultinodeIT.class);

    public static final String PRIMARY_NODE = "wildfly-node1";
    public static final String SECONDARY_NODE = "wildfly-node2";
    public static final String KIE_SERVER_ID = "wildfly-multinode-kie-server";

    @Deployment(name = "kie-server", order = 1, testable = false, managed = false)
    @TargetsContainer(SECONDARY_NODE)
    public static WebArchive createKieServerWarDeployment() {
        return createKieServerWar();
    }

    @Deployment(name = "workbench", order = 2, managed = false)
    @TargetsContainer(PRIMARY_NODE)
    public static WebArchive createWorkbenchWarDeployment() {
        return createWorkbenchWar();
    }

    @ArquillianResource
    private ContainerController controller;

    @ArquillianResource
    private Deployer deployer;

    @Before
    public void before() {

        if (controller.isStarted(SECONDARY_NODE)) {
            controller.stop(SECONDARY_NODE);
        }
        controller.start(SECONDARY_NODE);
    }

    @After
    public void after() {
        if (controller.isStarted(SECONDARY_NODE)) {
            controller.stop(SECONDARY_NODE);
        }
    }

    /**
     * This creates a multiinstance node where
     * instance 1 deploys the workbench
     * instance 2 deploys kie-server
     * after checking that the instance 2 manual is instantiated and working
     * it kills the server (only working in linux for now) -> on windows it will stop the app server gracefully
     * instance 2 is killed and the health check detects the problem
     * notifies back to the test that the kie server is disconnected (CountDownLatch in the EventHandler)
     */
    @Test
    @RunAsClient
    @OperateOnDeployment("workbench")
    public void testAvailableRestEndpoint() throws Exception {
        String url = new URL("http://localhost:8080/workbench/websocket/controller").toExternalForm();

        URL serverUrl = new URL("http://localhost:8230/kie-server/services/rest/server");

        CountDownLatch serverDown = new CountDownLatch(1);
        CountDownLatch kieServerTemplateUp = new CountDownLatch(1);
        CountDownLatch kieServerInstanceUp = new CountDownLatch(1);
        EventHandler customWSEventHandler = new EventHandler() {

            @Override
            public void onServerInstanceConnected(ServerInstanceConnected serverInstanceConnected) {
                LOGGER.info("onServerInstanceConnected :" + serverInstanceConnected);
            }

            @Override
            public void onServerInstanceDeleted(ServerInstanceDeleted serverInstanceDeleted) {
                LOGGER.info("onServerInstanceDeleted :" + serverInstanceDeleted);
            }

            @Override
            public void onServerInstanceDisconnected(ServerInstanceDisconnected serverInstanceDisconnected) {
                LOGGER.info("onServerInstanceDisconnected :" + serverInstanceDisconnected);
                serverDown.countDown();
            }

            @Override
            public void onServerTemplateDeleted(ServerTemplateDeleted serverTemplateDeleted) {
                LOGGER.info("onServerTemplateDeleted :" + serverTemplateDeleted);
            }

            @Override
            public void onServerTemplateUpdated(ServerTemplateUpdated serverTemplateUpdated) {
                LOGGER.info("onServerTemplateUpdated :" + serverTemplateUpdated);
                kieServerTemplateUp.countDown();
            }

            @Override
            public void onServerInstanceUpdated(ServerInstanceUpdated serverInstanceUpdated) {
                LOGGER.info("onServerInstanceUpdated :" + serverInstanceUpdated);
                kieServerInstanceUp.countDown();
            }

            @Override
            public void onContainerSpecUpdated(ContainerSpecUpdated containerSpecUpdated) {
                LOGGER.info("onContainerSpecUpdated :" + containerSpecUpdated);
            }
        };

        deployer.deploy("kie-server");
        assertTrue(ping(serverUrl));

        // the use of manually deployment is the only way to guarantee that the web context is completely deployed
        // and not causing a race condition when the controller ping the server and severing the connection
        deployer.deploy("workbench");

        try (WebSocketKieServerControllerClient client = (WebSocketKieServerControllerClient) KieServerControllerClientFactory.newWebSocketClient(url,
                                                                                                                                                  USER,
                                                                                                                                                  PASSWORD,
                                                                                                                                                  customWSEventHandler)) {
            kieServerTemplateUp.await(100, TimeUnit.SECONDS);
            kieServerInstanceUp.await(100, TimeUnit.SECONDS);

            ServerInstanceKeyList list = client.getServerInstances(KIE_SERVER_ID);

            assertEquals(1, list.getServerInstanceKeys().length);

            // kill the secondary node
            controller.kill(SECONDARY_NODE);

            serverDown.await(100, TimeUnit.SECONDS);

            assertFalse(ping(serverUrl));

            list = client.getServerInstances(KIE_SERVER_ID);

            assertEquals(0, list.getServerInstanceKeys().length);
        }
    }

    private boolean ping(URL url) {
        try {
            Client client = ClientBuilder.newClient().register(new BasicAuthentication(USER,
                                                                                       PASSWORD));
            WebTarget target = client.target(URI.create(url.toExternalForm()));

            Response response = target.request().accept(MediaType.APPLICATION_XML).get();

            return response.getStatus() == 200;
        } catch (Exception e) {
            return false;
        }
    }
}