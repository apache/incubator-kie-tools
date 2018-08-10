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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public class SimulationDescriptorTest {

    SimulationDescriptor simulationDescriptor;
    FactIdentifier factIdentifier;
    ExpressionIdentifier expressionIdentifier;

    @Before
    public void init() {
        simulationDescriptor = new SimulationDescriptor();
        factIdentifier = FactIdentifier.create("test fact", String.class.getCanonicalName());
        expressionIdentifier = ExpressionIdentifier.create("test expression", FactMappingType.EXPECTED);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addFactMappingTest() {
        simulationDescriptor.addFactMapping(factIdentifier, expressionIdentifier);

        // Should fail
        simulationDescriptor.addFactMapping(factIdentifier, expressionIdentifier);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addFactMappingIndexTest() {
        // Should fail
        simulationDescriptor.addFactMapping(1, factIdentifier, expressionIdentifier);
    }

    @Test
    public void sortByLogicalPositionTest() {
        List<FactMapping> originalFactMappings = IntStream.range(0, 2).boxed()
                .map(i -> simulationDescriptor
                        .addFactMapping(FactIdentifier.create("test " + i, String.class.getCanonicalName()),
                                        ExpressionIdentifier.create("test " + i, FactMappingType.EXPECTED))
                ).collect(Collectors.toList());
        FactMapping factMappingEdited = originalFactMappings.get(0);
        factMappingEdited.setLogicalPosition(100);
        simulationDescriptor.sortByLogicalPosition();
        List<FactMapping> updatedFactMappings = simulationDescriptor.getFactMappings();
        assertNotSame(updatedFactMappings.get(0), factMappingEdited);
        assertSame(updatedFactMappings.get(updatedFactMappings.size() - 1), factMappingEdited);
        assertEquals(originalFactMappings.size(), updatedFactMappings.size());
    }

    @Test
    public void getIndexByIdentifierTest() {
        simulationDescriptor.addFactMapping(factIdentifier, expressionIdentifier);
        int index = simulationDescriptor.getIndexByIdentifier(this.factIdentifier, this.expressionIdentifier);
        assertEquals(0, index);
    }
}
