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

package org.kie.workbench.common.screens.server.management.backend.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.kie.workbench.common.screens.server.management.utils.ControllerUtils.*;

public class ControllerUtilsTest {

    @Before
    @After
    public void clear() {
        System.clearProperty(KIE_SERVER_CONTROLLER);
        System.clearProperty(CFG_KIE_CONTROLLER_USER);
        System.clearProperty(CFG_KIE_CONTROLLER_PASSWORD);
        System.clearProperty(CFG_KIE_CONTROLLER_TOKEN);
    }

    @Test
    public void testEmbeddedSetup() {
        assertTrue(useEmbeddedController());
        assertNull(getControllerURL());
        assertEquals("kieserver", getControllerUser());
        assertEquals("kieserver1!", getControllerPassword());
        assertNull(getControllerToken());
    }

    @Test
    public void testStandaloneSetup() {
        final String controllerUrl = "http://localhost:8080/controller";
        final String user = "user";
        final String password = "password";
        final String token = "token";

        System.setProperty(KIE_SERVER_CONTROLLER,
                           controllerUrl);
        System.setProperty(CFG_KIE_CONTROLLER_USER,
                           user);
        System.setProperty(CFG_KIE_CONTROLLER_PASSWORD,
                           password);
        System.setProperty(CFG_KIE_CONTROLLER_TOKEN,
                           token);

        assertFalse(useEmbeddedController());
        assertEquals(controllerUrl, getControllerURL());
        assertEquals(user, getControllerUser());
        assertEquals(password, getControllerPassword());
        assertEquals(token, getControllerToken());
    }
}
