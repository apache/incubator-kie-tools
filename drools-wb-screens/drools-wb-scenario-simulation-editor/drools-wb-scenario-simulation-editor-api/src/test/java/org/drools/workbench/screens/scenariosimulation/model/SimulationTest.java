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
import static org.junit.Assert.fail;

public class SimulationTest {

    Simulation simulation;
    Scenario originalScenario;

    @Before
    public void setup() {
        simulation = new Simulation();
        FactIdentifier factIdentifier = FactIdentifier.create("Test", String.class.getCanonicalName());
        ExpressionIdentifier expressionIdentifier = ExpressionIdentifier.create("Test", FactMappingType.GIVEN);
        simulation.getSimulationDescriptor().addFactMapping(factIdentifier, expressionIdentifier);

        originalScenario = simulation.addScenario();
        originalScenario.setDescription("Test Description");
        originalScenario.addMappingValue(factIdentifier, expressionIdentifier, "TEST");
    }

    @Test
    public void addScenarioTest() {
        simulation.addScenario(1);

        muteException(() -> {
                          simulation.addScenario(-1);
                          fail();
                      },
                      IllegalArgumentException.class);
        muteException(() -> {
                          simulation.addScenario(3);
                          fail();
                      },
                      IllegalArgumentException.class);
    }

    @Test
    public void cloneScenarioTest() {
        Scenario clonedScenario = simulation.cloneScenario(0, 1);

        assertEquals(originalScenario.getDescription(), clonedScenario.getDescription());
        assertEquals(originalScenario.getFactMappingValues().size(), clonedScenario.getFactMappingValues().size());
        assertEquals(originalScenario, simulation.getScenarioByIndex(0));
        assertEquals(clonedScenario, simulation.getScenarioByIndex(1));

        assertNotEquals(originalScenario, clonedScenario);
        assertNotEquals(originalScenario.getFactMappingValues().get(0), clonedScenario.getFactMappingValues().get(0));
    }

    @Test
    public void cloneScenarioFail() {

        muteException(() -> {
                          simulation.cloneScenario(-1, 1);
                          fail();
                      },
                      IllegalArgumentException.class);

        muteException(() -> {
                          simulation.cloneScenario(2, 1);
                          fail();
                      },
                      IllegalArgumentException.class);

        muteException(() -> {
                          simulation.cloneScenario(0, -1);
                          fail();
                      },
                      IllegalArgumentException.class);

        muteException(() -> {
                          simulation.cloneScenario(0, 2);
                          fail();
                      },
                      IllegalArgumentException.class);
    }

    private <T extends Throwable> void muteException(Runnable toBeExecuted, Class<T> expected) {
        try {
            toBeExecuted.run();
        } catch (Throwable t) {
            if (!t.getClass().isAssignableFrom(expected)) {
                throw t;
            }
        }
    }
}