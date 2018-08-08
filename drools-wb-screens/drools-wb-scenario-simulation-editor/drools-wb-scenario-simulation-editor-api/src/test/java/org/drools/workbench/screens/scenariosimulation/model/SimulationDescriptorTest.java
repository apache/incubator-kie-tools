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
package org.drools.workbench.screens.scenariosimulation.model;

import org.junit.Before;
import org.junit.Test;

public class SimulationDescriptorTest {

    SimulationDescriptor simulationDescriptor;
    FactIdentifier factIdentifier;
    ExpressionIdentifier expressionIdentifier;

    @Before
    public void init() {
        simulationDescriptor = new SimulationDescriptor();
        factIdentifier = simulationDescriptor.newFactIdentifier("test fact", String.class.getCanonicalName());
        expressionIdentifier = ExpressionIdentifier.identifier("test expression", FactMappingType.EXPECTED);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addFactMappingTest() {
        simulationDescriptor.addFactMapping(expressionIdentifier, factIdentifier);

        // Should fail
        simulationDescriptor.addFactMapping(expressionIdentifier, factIdentifier);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addFactMappingIndexTest() {
        // Should fail
        simulationDescriptor.addFactMapping(1, expressionIdentifier, factIdentifier);
    }
}
