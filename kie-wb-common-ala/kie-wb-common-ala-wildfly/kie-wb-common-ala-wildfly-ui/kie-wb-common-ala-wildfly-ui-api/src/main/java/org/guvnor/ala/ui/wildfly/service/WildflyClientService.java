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

package org.guvnor.ala.ui.wildfly.service;

import org.jboss.errai.bus.server.annotations.Remote;

/**
 * This service has specific methods that are required by the Wildlfy provider ui.
 */
@Remote
public interface WildflyClientService {

    /**
     * Test the connectivity with a Wildfly/EAP server.
     * @param host the host server name where the Wildfly/EAP instance is running.
     * @param port the http port for the Wildfly/EAP instance.
     * @param managementPort the management port for the Wildfly/EAP instance.
     * @param user the user for establishing the connection.
     * @param password the user password.
     * @return returns a TestConnectionResult object that indicates if the connection could be established or not.
     */
    TestConnectionResult testConnection(final String host,
                                        final int port,
                                        final int managementPort,
                                        final String user,
                                        final String password);
}
