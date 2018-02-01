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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.controller.api.model.spec.ServerTemplateList;
import org.kie.server.controller.client.KieServerControllerClient;
import org.kie.server.controller.client.KieServerControllerClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Arquillian.class)
public class EmbeddedControllerIT extends AbstractControllerIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedControllerIT.class);

    @Deployment(name = "workbench", order = 1)
    public static WebArchive createEmbeddedControllerWarDeployment() {
        return createWorkbenchWar();
    }

    @Deployment(name = "kie-server", order = 2, testable = false)
    public static WebArchive createKieServerWarDeployment() {
        return createKieServerWar();
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("workbench")
    public void testEmbeddedEndpointsUsingRest(final @ArquillianResource URL baseURL) {
        testEmbeddedEndpoints(KieServerControllerClientFactory.newRestClient(getRestURL(baseURL),
                                                                             USER,
                                                                             PASSWORD));
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("workbench")
    public void testEmbeddedEndpointsUsingWebSocket(final @ArquillianResource URL baseURL) {
        testEmbeddedEndpoints(KieServerControllerClientFactory.newWebSocketClient(getWebSocketUrl(baseURL),
                                                                                  USER,
                                                                                  PASSWORD));
    }

    protected void testEmbeddedEndpoints(final KieServerControllerClient client) {
        try {
            final ServerTemplateList serverTemplateList = client.listServerTemplates();

            assertServerTemplateList(serverTemplateList);
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