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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.kie.server.controller.client.KieServerControllerClient;
import org.kie.server.controller.client.KieServerControllerClientFactory;
import org.kie.server.controller.client.event.EventHandler;
import org.kie.workbench.common.screens.server.management.backend.utils.StandaloneController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.workbench.common.screens.server.management.utils.ControllerUtils.*;

@ApplicationScoped
@StandaloneController
public class KieServerStandaloneControllerProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KieServerStandaloneControllerProducer.class);

    @Produces
    @ApplicationScoped
    @StandaloneController
    public KieServerControllerClient produceKieServerControllerClient(final @StandaloneController EventHandler handler) {
        LOGGER.debug("Creating KieServerControllerClient...");
        final String controllerURL = getControllerURL();
        validateProtocol(controllerURL);

        LOGGER.info("Using standalone controller url: {}", controllerURL);
        final String token = getControllerToken();
        if (token == null) {
            return KieServerControllerClientFactory.newWebSocketClient(controllerURL,
                                                                       getControllerUser(),
                                                                       getControllerPassword(),
                                                                       handler);
        } else {
            return KieServerControllerClientFactory.newWebSocketClient(controllerURL,
                                                                       token,
                                                                       handler);
        }
    }

    protected static void validateProtocol(final String controllerURL){
        if(controllerURL.startsWith("ws:") == false){
            throw new RuntimeException("Invalid protocol for connecting with remote standalone controller, only Web Socket connections are supported");
        }
    }

}