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

import org.junit.Test;

import static org.junit.Assert.*;
import static org.kie.workbench.common.screens.server.management.backend.KieServerStandaloneControllerProducer.validateProtocol;

public class KieServerStandaloneControllerProducerTest {

    @Test
    public void testWebSocketProtocol() {
        validateProtocol("ws://localhost:8080/controller");
    }

    @Test
    public void testUnsupportedProtocol() {
        try {
            validateProtocol("http://localhost:8080/controller");
            fail("Unsupported protocol should throw exception");
        } catch (Exception ex) {
            assertEquals("Invalid protocol for connecting with remote standalone controller, only Web Socket connections are supported",
                         ex.getMessage());
        }
    }
}
