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

public class ScenarioTest {

    SimulationDescriptor simulationDescriptor;
    Scenario scenario;
    FactIdentifier factIdentifier;
    ExpressionIdentifier expressionIdentifier;

    @Before
    public void init() {
        simulationDescriptor = new SimulationDescriptor();
        scenario = new Scenario("Test scenario", simulationDescriptor);
        factIdentifier = simulationDescriptor.newFactIdentifier("test fact", String.class.getCanonicalName());
        expressionIdentifier = ExpressionIdentifier.identifier("test expression", FactMappingType.EXPECTED);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addMappingValueTest() {
        scenario.addMappingValue(factIdentifier, expressionIdentifier, "test value");
        // Should fail
        scenario.addMappingValue(factIdentifier, expressionIdentifier, "test value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getFactMappingValueByIndexTest() {
        simulationDescriptor.addFactMapping(expressionIdentifier, factIdentifier);

        scenario.getFactMappingValueByIndex(0);
        // Should fail
        scenario.getFactMappingValueByIndex(1);
    }
}