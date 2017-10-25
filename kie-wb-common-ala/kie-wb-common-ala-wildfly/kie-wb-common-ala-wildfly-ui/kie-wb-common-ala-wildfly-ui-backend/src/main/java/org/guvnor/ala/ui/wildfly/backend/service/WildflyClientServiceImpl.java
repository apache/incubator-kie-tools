/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.wildfly.backend.service;

import javax.enterprise.context.ApplicationScoped;

import org.guvnor.ala.ui.wildfly.service.TestConnectionResult;
import org.guvnor.ala.ui.wildfly.service.WildflyClientService;
import org.guvnor.ala.wildfly.access.WildflyClient;
import org.jboss.errai.bus.server.annotations.Service;

@Service
@ApplicationScoped
public class WildflyClientServiceImpl
        implements WildflyClientService {

    @Override
    public TestConnectionResult testConnection(final String host,
                                               final int port,
                                               final int managementPort,
                                               final String user,
                                               final String password) {
        final TestConnectionResult result = new TestConnectionResult();
        try {

            final String testMessage = createWFClient(host,
                                                      port,
                                                      managementPort,
                                                      user,
                                                      password).testConnection();
            result.setManagementConnectionError(false);
            result.setManagementConnectionMessage(testMessage);
        } catch (Exception e) {
            result.setManagementConnectionError(true);
            result.setManagementConnectionMessage(e.getMessage());
        }
        return result;
    }

    protected WildflyClient createWFClient(final String host,
                                           final int port,
                                           final int managementPort,
                                           final String user,
                                           final String password) {
        return new WildflyClient("",
                                 user,
                                 password,
                                 host,
                                 port,
                                 managementPort);
    }
}
