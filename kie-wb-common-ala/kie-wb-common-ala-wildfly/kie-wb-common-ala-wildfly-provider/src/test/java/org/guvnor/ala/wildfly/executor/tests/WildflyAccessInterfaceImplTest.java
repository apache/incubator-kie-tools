/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.wildfly.executor.tests;

import org.guvnor.ala.wildfly.access.WildflyClient;
import org.guvnor.ala.wildfly.access.impl.WildflyAccessInterfaceImpl;
import org.guvnor.ala.wildfly.config.impl.WildflyProviderConfigImpl;
import org.guvnor.ala.wildfly.model.WildflyProviderImpl;
import org.junit.Test;

import static org.junit.Assert.*;

public class WildflyAccessInterfaceImplTest {

    private WildflyAccessInterfaceImpl accessInterface = new WildflyAccessInterfaceImpl();

    private static final String PROVIDER = "wildfly";

    @Test
    public void testWildflyClientNull() {
        final WildflyClient client = accessInterface.getWildflyClient(
                new WildflyProviderImpl(
                        new WildflyProviderConfigImpl(PROVIDER,
                                                      null,
                                                      null,
                                                      null,
                                                      null,
                                                      null)
                )
        );

        assertNotNull(client);
        assertEquals(client.getPort(),
                     8080);
        assertEquals(client.getManagementPort(),
                     9990);
    }

    @Test
    public void testWildflyClient() {
        final String hostId = "localhost";
        final String port = "8081";
        final String managementPort = "9991";
        final String user = "admin";
        final String password = "pass";

        final WildflyClient client = accessInterface.getWildflyClient(
                new WildflyProviderImpl(new WildflyProviderConfigImpl(PROVIDER,
                                                                      hostId,
                                                                      port,
                                                                      managementPort,
                                                                      user,
                                                                      password)
                )
        );

        assertNotNull(client);
        assertEquals(client.getPort(),
                     8081);
        assertEquals(client.getManagementPort(),
                     9991);
        assertEquals(client.getHost(),
                     hostId);
        assertEquals(client.getProviderName(),
                     PROVIDER);
        assertEquals(client.getUser(),
                     user);
        assertEquals(client.getPassword(),
                     password);
    }
}
