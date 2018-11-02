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

package org.kie.workbench.common.screens.server.management.backend.websocket;

import java.util.Collections;
import java.util.Set;
import javax.websocket.Endpoint;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpointConfig;

import org.kie.server.controller.websocket.WebSocketKieServerControllerImpl;
import org.kie.server.controller.websocket.management.WebSocketKieServerMgmtControllerImpl;
import org.kie.server.controller.websocket.notification.WebSocketKieServerControllerNotification;
import org.kie.workbench.common.screens.server.management.utils.ControllerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandaloneControllerApplicationConfig implements ServerApplicationConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(StandaloneControllerApplicationConfig.class);

    @Override
    public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> endpointClasses) {
        return Collections.emptySet();
    }

    @Override
    public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
        if(ControllerUtils.useEmbeddedController() == false) {
            LOGGER.info("Standalone controller in use, removing Web Socket endpoints");
            scanned.remove(WebSocketKieServerControllerImpl.class);
            scanned.remove(WebSocketKieServerControllerNotification.class);
            scanned.remove(WebSocketKieServerMgmtControllerImpl.class);
        }
        return scanned;
    }
}
