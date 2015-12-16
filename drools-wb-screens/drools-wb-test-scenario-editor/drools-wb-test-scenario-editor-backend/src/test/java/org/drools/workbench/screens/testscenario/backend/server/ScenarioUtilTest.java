/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.testscenario.backend.server;

import java.util.ArrayList;

import org.junit.Test;

import static org.drools.workbench.screens.testscenario.backend.server.ScenarioUtil.*;
import static org.junit.Assert.*;

public class ScenarioUtilTest {

    @Test
    public void testGetKSessionName() throws Exception {

        ArrayList<String> ksessions = new ArrayList<String>();
        ksessions.add("ksession1");
        ksessions.add("ksession2");
        assertEquals("ksession1", getKSessionName(ksessions));

        assertNull(getKSessionName(new ArrayList<String>()));

        assertNull(getKSessionName(null));

    }
}