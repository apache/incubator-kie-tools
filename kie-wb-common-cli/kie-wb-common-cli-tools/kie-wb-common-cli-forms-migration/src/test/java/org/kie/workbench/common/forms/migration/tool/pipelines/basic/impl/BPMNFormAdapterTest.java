/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.migration.tool.pipelines.basic.impl;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BPMNFormAdapterTest {

    @Test
    public void testIsBPMNFile() throws Exception {
        assertTrue(BPMNFormAdapter.isBPMNFile("abc.bpmn"));
        assertTrue(BPMNFormAdapter.isBPMNFile("abc.bpmn2"));
        assertTrue(BPMNFormAdapter.isBPMNFile("abc.bpmn-cm"));
        assertFalse(BPMNFormAdapter.isBPMNFile("abc.bpmn2-cm"));
    }
}