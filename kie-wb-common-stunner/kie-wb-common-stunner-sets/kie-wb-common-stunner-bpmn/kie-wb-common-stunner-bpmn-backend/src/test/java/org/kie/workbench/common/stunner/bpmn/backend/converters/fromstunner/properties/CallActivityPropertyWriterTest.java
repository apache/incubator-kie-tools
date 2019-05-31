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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public class CallActivityPropertyWriterTest {

    private CallActivityPropertyWriter tested =
            new CallActivityPropertyWriter(bpmn2.createCallActivity(),
                                           new FlatVariableScope());

    @Test
    public void testSetCase_true() throws Exception {
        tested.setCase(Boolean.TRUE);

        assertTrue(CustomElement.isCase.of(tested.getFlowElement()).get());
    }

    @Test
    public void testSetCase_false() throws Exception {
        tested.setCase(Boolean.FALSE);

        assertFalse(CustomElement.isCase.of(tested.getFlowElement()).get());
    }

    @Test
    public void testSetAdHocAutostart_true() throws Exception {
        tested.setAdHocAutostart(Boolean.TRUE);

        assertTrue(CustomElement.autoStart.of(tested.getFlowElement()).get());
    }

    @Test
    public void testSetAdHocAutostart_false() throws Exception {
        tested.setAdHocAutostart(Boolean.FALSE);

        assertFalse(CustomElement.autoStart.of(tested.getFlowElement()).get());
    }
}