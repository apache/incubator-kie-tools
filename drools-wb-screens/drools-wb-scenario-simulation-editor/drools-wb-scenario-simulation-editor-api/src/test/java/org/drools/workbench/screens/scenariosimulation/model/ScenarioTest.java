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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ScenarioTest {

    SimulationDescriptor simulationDescriptor;
    Scenario scenario;
    FactIdentifier factIdentifier;
    ExpressionIdentifier expressionIdentifier;
    Simulation simulation;

    @Before
    public void init() {
        simulation = new Simulation();
        simulationDescriptor = simulation.getSimulationDescriptor();
        scenario = simulation.addScenario();
        factIdentifier = FactIdentifier.create("test fact", String.class.getCanonicalName());
        expressionIdentifier = ExpressionIdentifier.create("test expression", FactMappingType.EXPECTED);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addMappingValueTest() {
        scenario.addMappingValue(factIdentifier, expressionIdentifier, "test value");
        // Should fail
        scenario.addMappingValue(factIdentifier, expressionIdentifier, "test value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getDescriptionTestFail() {
        scenario.getDescription();
    }

    @Test
    public void getDescriptionTest() {
        scenario.addMappingValue(FactIdentifier.DESCRIPTION, ExpressionIdentifier.DESCRIPTION, "Test Description");
        scenario.getDescription();
    }

    @Test
    public void addOrUpdateMappingValue() {
        Object value1 = "Test 1";
        Object value2 = "Test 2";
        FactMappingValue factMappingValue = scenario.addMappingValue(factIdentifier, expressionIdentifier, value1);
        assertEquals(factMappingValue.getRawValue(), value1);
        FactMappingValue factMappingValue1 = scenario.addOrUpdateMappingValue(factIdentifier, expressionIdentifier, value2);
        assertEquals(factMappingValue, factMappingValue1);
        assertEquals(factMappingValue1.getRawValue(), value2);
    }

    @Test
    public void sortTest() {
        ExpressionIdentifier expressionIdentifier2 = ExpressionIdentifier.create("Test expression 2", FactMappingType.GIVEN);
        simulationDescriptor.addFactMapping(factIdentifier, expressionIdentifier);
        simulationDescriptor.addFactMapping(factIdentifier, expressionIdentifier2);
        scenario.addMappingValue(factIdentifier, expressionIdentifier2, "Test 2");
        FactMappingValue factMappingValue1 = scenario.addMappingValue(factIdentifier, this.expressionIdentifier, "Test");

        assertEquals(scenario.getFactMappingValues().get(1), factMappingValue1);

        scenario.sort();
        assertNotEquals(scenario.getFactMappingValues().get(1), factMappingValue1);
        assertEquals(scenario.getFactMappingValues().get(0), factMappingValue1);
    }
}