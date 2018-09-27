/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.services.verifier.plugin.backend;

import java.io.FileNotFoundException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DecisionTableVerifierWebWorkerProviderTest {

    @Test
    public void id() throws Exception {
        assertEquals("dtableVerifier", new DecisionTableVerifierWebWorkerProvider().getId());
    }

    @Test
    public void testLoad() throws Exception {
        assertEquals("hello there", new DecisionTableVerifierWebWorkerProvider().loadResource("test.txt"));
    }

    @Test(expected = FileNotFoundException.class)
    public void fileDoesNotExist() throws Exception {
        new DecisionTableVerifierWebWorkerProvider().loadResource("doesNotExists.txt");
    }
}