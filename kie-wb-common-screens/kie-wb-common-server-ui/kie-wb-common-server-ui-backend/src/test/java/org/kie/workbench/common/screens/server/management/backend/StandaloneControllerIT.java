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
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.client.jaxrs.BasicAuthentication;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.controller.client.KieServerControllerClientFactory;
import org.kie.server.controller.client.exception.KieServerControllerHTTPClientException;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class StandaloneControllerIT extends AbstractControllerIT {

    @Deployment(name = "kie-server-controller", order = 1, testable = false)
    public static WebArchive createKieServerControllerWarDeployment() {
        return createKieServerControllerWar();
    }

    @Deployment(name = "workbench", order = 2)
    public static WebArchive createWorkbenchWarDeployment() {
        return createWorkbenchWar();
    }

    @Deployment(name = "kie-server", order = 3, testable = false)
    public static WebArchive createKieServerWarDeployment() {
        return createKieServerWar();
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("workbench")
    public void testEmbeddedRestEndpoint(final @ArquillianResource URL baseURL) throws Exception {
        final String controllerURL = baseURL + "rest/controller";
        try {
            client = KieServerControllerClientFactory.newRestClient(controllerURL,
                                                                    USER,
                                                                    PASSWORD);
            client.listServerTemplates();
            fail("Connection to embedded controller endpoint should fail");
        } catch (KieServerControllerHTTPClientException ex) {
            assertEquals(404,
                         ex.getResponseCode());
        }
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("workbench")
    public void testEmbeddedWebSocketEndpoint(final @ArquillianResource URL baseURL) throws Exception {
        try {
            client = KieServerControllerClientFactory.newWebSocketClient(getWebSocketUrl(baseURL),
                                                                         USER,
                                                                         PASSWORD);
            fail("Connection to embedded controller endpoint should fail");
        } catch (Exception ex) {
            assertTrue(ex.getMessage().contains("Invalid response code 404"));
        }
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("workbench")
    public void testAvailableRestEndpoint(final @ArquillianResource URL baseURL) throws Exception {
        Client client = null;
        try {
            client = ClientBuilder.newClient().register(new BasicAuthentication(USER,
                                                                                PASSWORD));
            WebTarget target = client.target(URI.create(new URL(baseURL,
                                                                "rest/spaces").toExternalForm()));

            Response response = target.request().accept(MediaType.APPLICATION_JSON).get();

            assertEquals(200,
                         response.getStatus());
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}