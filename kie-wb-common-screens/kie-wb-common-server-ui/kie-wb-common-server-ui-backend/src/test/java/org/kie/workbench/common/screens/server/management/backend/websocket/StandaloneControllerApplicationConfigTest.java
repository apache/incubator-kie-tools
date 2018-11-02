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

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.server.controller.websocket.WebSocketKieServerControllerImpl;
import org.kie.server.controller.websocket.management.WebSocketKieServerMgmtControllerImpl;
import org.kie.server.controller.websocket.notification.WebSocketKieServerControllerNotification;
import org.kie.workbench.common.screens.server.management.utils.ControllerUtils;
import org.mockito.internal.util.collections.Sets;

import static org.junit.Assert.*;

public class StandaloneControllerApplicationConfigTest {

    private StandaloneControllerApplicationConfig config = new StandaloneControllerApplicationConfig();

    @Before
    @After
    public void clear() {
        System.clearProperty(ControllerUtils.KIE_SERVER_CONTROLLER);
    }

    @Test
    public void testEmbeddedSetup() {
        Set<Class<?>> scanned = Sets.newSet(WebSocketKieServerControllerImpl.class,
                                            WebSocketKieServerControllerNotification.class,
                                            WebSocketKieServerMgmtControllerImpl.class);

        Set<Class<?>> result = config.getAnnotatedEndpointClasses(scanned);

        assertEquals(3,
                     result.size());
    }

    @Test
    public void testStandaloneSetup() {
        System.setProperty(ControllerUtils.KIE_SERVER_CONTROLLER,
                           "http://localhost:8080/controller");

        Set<Class<?>> scanned = Sets.newSet(WebSocketKieServerControllerImpl.class,
                                            WebSocketKieServerControllerNotification.class,
                                            WebSocketKieServerMgmtControllerImpl.class);

        Set<Class<?>> result = config.getAnnotatedEndpointClasses(scanned);

        assertTrue(result.isEmpty());
    }
}
