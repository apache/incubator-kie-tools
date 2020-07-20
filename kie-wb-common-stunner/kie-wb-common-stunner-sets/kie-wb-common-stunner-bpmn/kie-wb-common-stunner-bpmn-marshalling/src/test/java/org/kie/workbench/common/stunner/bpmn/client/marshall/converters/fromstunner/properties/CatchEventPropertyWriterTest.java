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

package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties;

import java.util.HashSet;

import bpsim.ElementParameters;
import org.eclipse.bpmn2.StartEvent;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationAttributeSet;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;

public class CatchEventPropertyWriterTest {

    @Test
    public void simulationSetMustHaveElementRef() {
        String elementId = "MY_ID";
        StartEvent startEvent = bpmn2.createStartEvent();
        startEvent.setId(elementId);

        CatchEventPropertyWriter p =
                new CatchEventPropertyWriter(
                        startEvent,
                        new FlatVariableScope(),
                        new HashSet<>());

        SimulationAttributeSet defaults = new SimulationAttributeSet();
        p.setSimulationSet(defaults);

        ElementParameters simulationParameters = p.getSimulationParameters();
        assertEquals(elementId, simulationParameters.getElementRef());
    }
}